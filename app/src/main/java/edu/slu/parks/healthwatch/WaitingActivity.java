package edu.slu.parks.healthwatch;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import edu.slu.parks.healthwatch.model.ILocation;
import edu.slu.parks.healthwatch.model.MyLocation;
import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.views.AlertDialogFragment;

public class WaitingActivity extends AppCompatActivity implements AlertDialogFragment.Listener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private boolean includeLocation;
    private ILocation gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.measure);
        setSupportActionBar(toolbar);

        gps = new MyLocation(this);
        includeLocation = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.gps_switch), false);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (!includeLocation) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    next();
                }
            }, 5000);
        }
    }

    private void next() {
        Intent intent = new Intent(WaitingActivity.this, RecordActivity.class);
        intent.putExtra(Constants.SYSTOLIC, new Random().nextInt(220));
        intent.putExtra(Constants.DIASTOLIC, new Random().nextInt(80));

        if (gps.isValid()) {
            intent.putExtra(Constants.LATITUDE, gps.getLocation().getLatitude());
            intent.putExtra(Constants.LONGITUDE, gps.getLocation().getLongitude());
        }

        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(this.getLocalClassName(), "mGoogleApi Connected");


        updateLocation();
    }

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || !includeLocation || !mGoogleApiClient.isConnected()) {

            disconnectGps();
            return;
        }

        gps.update(LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient));

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, createLocationRequest(), this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Unable to acquire gps location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Unable to acquire gps location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(this.getLocalClassName(), "onLocation changed");

        if (includeLocation) {
            gps.update(location);
            gps.save();
        }

        disconnectGps();

        next();
    }

    private void disconnectGps() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onBackPressed() {
        Bundle arg = new Bundle();
        arg.putInt(Constants.TITLE, R.string.cancel_measure);
        arg.putInt(Constants.MESSAGE, R.string.cancel_measure_msg);
        arg.putInt(Constants.CANCEL, R.string.no);
        arg.putInt(Constants.OK, R.string.cancel);

        AlertDialogFragment alert = new AlertDialogFragment();
        alert.setArguments(arg);
        alert.show(getSupportFragmentManager(), Constants.MEASURE_DIALOG);
    }

    @Override
    public void onOkayButtonClick() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateLocation();
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
}
