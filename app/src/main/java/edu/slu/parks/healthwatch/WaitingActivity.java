package edu.slu.parks.healthwatch;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Set;

import edu.slu.parks.healthwatch.bluetooth.ConnectThread;
import edu.slu.parks.healthwatch.bluetooth.Phase;
import edu.slu.parks.healthwatch.model.ILocation;
import edu.slu.parks.healthwatch.model.MyLocation;
import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.views.AlertDialogFragment;

public class WaitingActivity extends BaseActivity implements AlertDialogFragment.Listener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private boolean includeLocation;
    private ILocation gps;
    private BluetoothAdapter mBluetoothAdapter;
    private TextView pressureView;
    private TextView statusView;
    private BluetoothDevice healthWatch;
    private android.os.Handler bluetoothHandler;
    private ConnectThread connectThread;
    private boolean aquiringSystolic;
    private boolean aquiringDiastolic;
    private int diastolic;
    private int systolic;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Snackbar.make(findViewById(R.id.status),
                        String.format("%s found", device.getAddress()),
                        Snackbar.LENGTH_SHORT).show();

                String name = device.getName() != null ? device.getName() : "";
                if (name.toLowerCase().contains(Constants.DEVICE_NAME.toLowerCase())) {
                    Snackbar.make(findViewById(R.id.status),
                            String.format("%s found as valid", name),
                            Snackbar.LENGTH_SHORT).show();

                    healthWatch = device;
                    mBluetoothAdapter.cancelDiscovery();
                    startConnection(device);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            healthWatch = savedInstanceState.getParcelable(Constants.BT_ADDRESS);
            startConnection(healthWatch);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.measure);
        setSupportActionBar(toolbar);

        pressureView = (TextView) findViewById(R.id.txt_pressure);
        statusView = (TextView) findViewById(R.id.status);

        gps = new MyLocation(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        includeLocation = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.gps_switch), false);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        Log.d(getLocalClassName(), "includeLocation: " + includeLocation);

        handleBluetooth();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_waiting;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (healthWatch != null) {
            savedInstanceState.putParcelable(Constants.BT_ADDRESS, healthWatch);
            //savedInstanceState.putString(Constants.BT_NAME, healthWatch.getName());
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void requestBluetoothActivation() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                connectToDevice();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBluetoothAdapter.cancelDiscovery();
        unregisterReceiver(mReceiver);
    }

    public void handleBluetooth() {
        updateStatus(R.string.verify_bluetooth);

        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Snackbar.make(findViewById(R.id.status), "Bluetooth is off", Snackbar.LENGTH_INDEFINITE)
                    .setAction("TURN ON", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mBluetoothAdapter.enable();
                            connectToDevice();
                        }
                    }).show();

        } else if (mBluetoothAdapter == null) {
            Snackbar.make(findViewById(R.id.status),
                    "Device does not support bluetooth",
                    Snackbar.LENGTH_SHORT).show();
        } else {
            connectToDevice();
        }
    }

    private void updateStatus(int message) {
        statusView.setText(message);
    }

    private void connectToDevice() {
        BluetoothDevice device = getPairedDevice();

        if (device != null) {
            updateStatus(R.string.connecting);
            healthWatch = device;
            startConnection(device);

        } else {
            updateStatus(R.string.search);
            performDeviceDiscovery();
        }
    }

    private void startConnection(BluetoothDevice device) {
        updateStatus(String.format("Connecting to %s", device.getName()));

        bluetoothHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                Phase phase = Phase.toEnum(inputMessage.what);

                // Log.d(getClass().getName(), "phase: " + phase.name());
                if (phase != Phase.UNKNOWN) {
                    switch (phase) {
                        case START:
                            updateStatus("Starting measurement");
                            break;
                        case INFLATING:
                            updateStatus(R.string.inflating);
                            break;
                        case DEFLATING:
                            updateStatus(R.string.deflating);
                            break;
                        case SYSTOLIC:
                            aquiringSystolic = true;
                            updateStatus(R.string.getting_systolic);
                            break;
                        case DIASTOLIC:
                            aquiringDiastolic = true;
                            updateStatus(R.string.getting_diatolic);
                            break;

                        case DONE:
                            next();
                            break;
                    }
                } else {
                    if (aquiringDiastolic) {
                        diastolic = inputMessage.what;
                        aquiringDiastolic = false;
                    } else if (aquiringSystolic) {
                        systolic = inputMessage.what;
                        aquiringSystolic = false;
                    } else pressureView.setText(String.valueOf(inputMessage.what));
                }
            }
        };

        if (connectThread != null) connectThread.cancel();
        connectThread = new ConnectThread(device, bluetoothHandler);
        connectThread.start();
    }

    private void updateStatus(String message) {
        if (statusView != null)
            statusView.setText(message);
    }

    private void performDeviceDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
    }

    private BluetoothDevice getPairedDevice() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {

                if (device.getName().toLowerCase().contains(Constants.DEVICE_NAME.toLowerCase()))
                    return device;
            }
        }

        return null;
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

        connectToDevice();
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

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

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
        if (connectThread != null) connectThread.cancel();
    }
}
