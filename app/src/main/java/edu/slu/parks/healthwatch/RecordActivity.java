package edu.slu.parks.healthwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.model.ILocation;
import edu.slu.parks.healthwatch.model.MyLocation;
import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.views.AlertDialogFragment;

public class RecordActivity extends AppCompatActivity implements AlertDialogFragment.Listener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String RECORD_DIALOG = "measure_dialog";
    private EditText commentView;
    private CheckBox locationView;
    private GoogleApiClient mGoogleApiClient;
    private boolean includeLocation;
    private Record record;
    private ILocation gps;
    private double latitude;
    private double longitude;
    private boolean saveToDatabase;
    private boolean requestingGps;
    private int diastolic;
    private int systolic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        record = new Record();
        gps = new MyLocation(this);

        diastolic = getIntent().getIntExtra(Constants.DIASTOLIC, 0);
        systolic = getIntent().getIntExtra(Constants.SYSTOLIC, 0);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        includeLocation = sharedPref.getBoolean(getString(R.string.gps_switch), false);

        //sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        latitude = Double.longBitsToDouble(sharedPref.getLong(getString(R.string.gps_latitude), 0));
        longitude = Double.longBitsToDouble(sharedPref.getLong(getString(R.string.gps_longitude), 0));


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        commentView = (EditText) findViewById(R.id.txt_comment);
        TextView systolicView = (TextView) findViewById(R.id.layout_reading_systolic).findViewById(R.id.txt_record_pressure);
        TextView diastolicView = (TextView) findViewById(R.id.layout_reading_diastolic).findViewById(R.id.txt_record_pressure);
        ((TextView) findViewById(R.id.layout_reading_diastolic).findViewById(R.id.txt_reading)).setText("Diastolic (mmHg)");

        systolicView.setText(String.valueOf(systolic));
        diastolicView.setText(String.valueOf(diastolic));

        Button saveBtn = (Button) findViewById(R.id.btn_save);
        Button repeatBtn = (Button) findViewById(R.id.btn_repeat);
        locationView = (CheckBox) findViewById(R.id.box_location);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase();
            }
        });

        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeat();
            }
        });

        locationView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                includeLocation = isChecked;

                if (isChecked) {
                    tryRequestPermissionAndUpdate();
                }
            }
        });

        locationView.setChecked(includeLocation);
    }

    private void tryRequestPermissionAndUpdate() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.REQUEST_ACCESS_FINE_LOCATION);
        } else
            updateLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length < 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    locationView.setChecked(false);
                } else {
                    updateLocation();
                }
            }
        }
    }

    private void saveToDatabase() {
        record.systolic = systolic;
        record.diastolic = diastolic;
        record.comment = String.valueOf(commentView.getText());
        record.date = new Date();

        if (includeLocation) {
            Log.d(this.getLocalClassName(), "Latitude: " + String.valueOf(latitude));
            Log.d(this.getLocalClassName(), "Longitude: " + String.valueOf(longitude));

            if (latitude == 0 || longitude == 0) {
                saveToDatabase = true;

                if (requestingGps) {
                    Toast.makeText(this, "Please wait, obtaining your gps location", Toast.LENGTH_LONG).show();

                } else {
                    updateLocation();
                }
            } else {
                record.latitude = latitude;
                record.longitude = longitude;

                goHome();
            }
        } else
            goHome();
    }

    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void repeat() {
        Intent intent = new Intent(this, WaitingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Bundle arg = new Bundle();
        arg.putInt(Constants.TITLE, R.string.cancel_measure);
        arg.putInt(Constants.MESSAGE, R.string.cancel_record_msg);
        arg.putInt(Constants.CANCEL, R.string.no);
        arg.putInt(Constants.OK, R.string.cancel);

        AlertDialogFragment alert = new AlertDialogFragment();
        alert.setArguments(arg);
        alert.show(getSupportFragmentManager(), RECORD_DIALOG);
    }

    @Override
    public void onOkayButtonClick() {
        super.onBackPressed();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(this.getLocalClassName(), "mGoogleApi Connected");

        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(this.getLocalClassName(), "onLocation changed");
        requestingGps = false;

        gps.update(location);
        gps.save();

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

            mGoogleApiClient.disconnect();
        }

        if (saveToDatabase) saveToDatabase();
    }

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            return;
        }

        if (latitude == 0 || longitude == 0) {

            requestingGps = true;
            Toast.makeText(this, "Obtaining gps location", Toast.LENGTH_LONG).show();

            if (mGoogleApiClient.isConnected()) {

                gps.update(LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient));

                if (includeLocation) {
                    startLocationUpdates();
                }
            }
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            return;

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, createLocationRequest(), this);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
    }
}
