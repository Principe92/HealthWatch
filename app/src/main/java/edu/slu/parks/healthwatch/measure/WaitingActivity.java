package edu.slu.parks.healthwatch.measure;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import edu.slu.parks.healthwatch.BaseActivity;
import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.bluetooth.ConnectThread;
import edu.slu.parks.healthwatch.bluetooth.GattBluetooth;
import edu.slu.parks.healthwatch.bluetooth.IBluetooth;
import edu.slu.parks.healthwatch.bluetooth.OtherBluetooth;
import edu.slu.parks.healthwatch.bluetooth.Phase;
import edu.slu.parks.healthwatch.fragments.AlertDialogFragment;
import edu.slu.parks.healthwatch.model.ILocation;
import edu.slu.parks.healthwatch.model.MyLocation;
import edu.slu.parks.healthwatch.utils.Constants;

public class WaitingActivity extends BaseActivity implements AlertDialogFragment.Listener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OtherBluetooth.IBluetoothListener {

    private GoogleApiClient mGoogleApiClient;
    private boolean includeLocation;
    private ILocation gps;
    private TextView pressureView;
    private TextView statusView;
    private IBluetooth bluetooth;
    private ConnectThread connectThread;
    private boolean acquiringSystolic;
    private boolean acquiringDiastolic;
    private int diastolic;
    private int systolic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpToolbar(R.string.measure);

        pressureView = (TextView) findViewById(R.id.txt_pressure);
        statusView = (TextView) findViewById(R.id.status);
        bluetooth = new GattBluetooth(this, this);

        gps = new MyLocation(this);
        includeLocation = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.gps_switch), false);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_waiting;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetooth.close();
    }

    private void next() {
        Intent intent = new Intent(WaitingActivity.this, RecordActivity.class);
        intent.putExtra(Constants.SYSTOLIC, systolic);
        intent.putExtra(Constants.DIASTOLIC, diastolic);

        if (gps.isValid()) {
            intent.putExtra(Constants.LATITUDE, gps.getLocation().getLatitude());
            intent.putExtra(Constants.LONGITUDE, gps.getLocation().getLongitude());
        }

        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
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
        Snackbar.make(findViewById(R.id.status), "Unable to acquire gps location", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(findViewById(R.id.status), "Unable to acquire gps location", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (includeLocation) {
            gps.update(location);
            gps.save();
        }

        disconnectGps();
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

        bluetooth.onPause();

        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        bluetooth.onResume();
        updateLocation();
    }

    private void startConnection(BluetoothDevice device) {
        notify(String.format("Connecting to %s", device.getName()));

        Handler bluetoothHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                Phase phase = Phase.toEnum(inputMessage.what);

                // Log.d(getClass().getName(), "phase: " + phase.name());
                if (phase != Phase.UNKNOWN) {
                    switch (phase) {
                        case START:
                            WaitingActivity.this.notify("Starting measurement");
                            break;
                        case INFLATING:
                            updateStatus(R.string.inflating);
                            break;
                        case DEFLATING:
                            updateStatus(R.string.deflating);
                            break;
                        case SYSTOLIC:
                            acquiringSystolic = true;
                            updateStatus(R.string.getting_systolic);
                            break;
                        case DIASTOLIC:
                            acquiringDiastolic = true;
                            updateStatus(R.string.getting_diatolic);
                            break;

                        case DONE:
                            next();
                            break;
                    }
                } else {
                    if (acquiringDiastolic) {
                        diastolic = inputMessage.what;
                        acquiringDiastolic = false;
                    } else if (acquiringSystolic) {
                        systolic = inputMessage.what;
                        acquiringSystolic = false;
                    } else pressureView.setText(String.valueOf(inputMessage.what));
                }
            }
        };

        if (connectThread != null) connectThread.cancel();
        connectThread = new ConnectThread(device, bluetoothHandler);
        connectThread.start();
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
        if (connectThread != null) connectThread.cancel();
    }

    @Override
    public void updateStatus(int message) {
        statusView.setText(message);
        notify(getString(message));
    }

    @Override
    public void updateStatus(String message) {
        statusView.setText(message);
        notify(message);
    }

    @Override
    public void displayData(String data) {
        statusView.setText(data);
    }

    @Override
    public void onDataAvailable() {
        next();
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        String name = device.getName() != null ? device.getName() : device.getAddress();
        notify(String.format("%s found", name));

        // startConnection(device);
    }

    private void notify(String text) {
        Snackbar.make(statusView,
                text,
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBluetoothNotAvailable() {
        notify(getString(R.string.no_bluetooth));
        statusView.setText(R.string.no_bluetooth);
        onBackPressed();
    }

    @Override
    public void onBluetoothOff() {
        Snackbar.make(statusView, "Bluetooth is off", Snackbar.LENGTH_INDEFINITE)
                .setAction("Turn on", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bluetooth.enable();
                    }
                }).show();
    }
}
