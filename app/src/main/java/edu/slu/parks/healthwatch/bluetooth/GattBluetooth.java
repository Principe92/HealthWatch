package edu.slu.parks.healthwatch.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.security.IPreference;
import edu.slu.parks.healthwatch.security.Preference;
import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.utils.Util;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by okori on 14-Feb-17.
 */
public class GattBluetooth extends BluetoothGattCallback implements IBluetooth {
    private final static String TAG = GattBluetooth.class.getName();

    private BluetoothManager manager;
    private BluetoothAdapter adapter;
    private Context context;
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private OtherBluetooth.IBluetoothListener listener;
    private IPreference preference;

    private BluetoothLeService mBluetoothLeService;
    private boolean dataIsAvailable;
    private String mDeviceAddress;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (Constants.Gatt.ACTION_GATT_CONNECTED.equals(action)) {
                listener.updateStatus(String.format("Connected to %s", mBluetoothLeService.getDeviceName()));
            } else if (Constants.Gatt.ACTION_GATT_DISCONNECTED.equals(action)) {
                listener.updateStatus(String.format("Disconnected from %s", mBluetoothLeService.getDeviceName()));
                onDeviceDisconnected();
            } else if (Constants.Gatt.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                // displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (Constants.Gatt.ACTION_DATA_AVAILABLE.equals(action)) {
                listener.displayData(intent.getStringExtra(Constants.Gatt.EXTRA_DATA));
            }
        }
    };
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((LocalBinder) service).getService();
            Log.i(TAG, "Service connected");

            if (!mBluetoothLeService.initialize()) {
                listener.onBluetoothNotAvailable();
                Log.e(TAG, "Unable to initialize Bluetooth");
            }

            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    public GattBluetooth(final Context context, final OtherBluetooth.IBluetoothListener listener) {
        this.context = context;
        this.preference = new Preference(context);
        this.listener = listener;
        manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        adapter = manager.getAdapter();

        leScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                // your implementation here

                String name = device.getName() != null ? device.getName() : "No name";

                Log.d(this.getClass().getName(), String.format("%s - %s found", name, device.getAddress()));

                if (name.toLowerCase().contains(Constants.DEVICE_NAME.toLowerCase())) {
                    listener.onDeviceFound(device);
                    mDeviceAddress = device.getAddress();
                    preference.saveString(mDeviceAddress, Constants.MAC);
                    adapter.stopLeScan(leScanCallback);

                    startService();
                }
            }
        };

        enable();
    }

    private void onDeviceDisconnected() {
        if (dataIsAvailable) {
            listener.onDataAvailable();
        } else {
            handleBluetooth();
        }
    }

    @Override
    public void enable() {
        handleBluetooth();
    }

    private void handleBluetooth() {
        listener.updateStatus(R.string.verify_bluetooth);

        if (isBluetoothAvailable() && !adapter.isEnabled()) {
            listener.onBluetoothOff();

        } else if (!isBluetoothAvailable()) {
            listener.onBluetoothNotAvailable();
        } else {
            connectToDevice();
        }
    }

    private void connectToDevice() {
        mDeviceAddress = preference.getString(Constants.MAC);

        BluetoothDevice device = !Util.isNullorEmpty(mDeviceAddress) ? adapter.getRemoteDevice(mDeviceAddress) : null;

        if (device == null) {
            adapter.startLeScan(leScanCallback);
        } else {
            listener.updateStatus(String.format("Connecting to %s", device.getName()));
            startService();
        }
    }

    private void startService() {
        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        context.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private boolean isBluetoothAvailable() {
        return adapter != null;
    }

    @Override
    public void close() {
        context.unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public void onResume() {
        context.registerReceiver(mGattUpdateReceiver, Constants.Gatt.makeGattUpdateIntentFilter());

        if (mBluetoothLeService != null) {
            mBluetoothLeService.connect(mDeviceAddress);
        }
    }

    @Override
    public void onPause() {
        if (isBluetoothAvailable())
            adapter.stopLeScan(leScanCallback);

        context.unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        // this will get called anytime you perform a read or write characteristic operation

        //read the characteristic data
        byte[] data = characteristic.getValue();

        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));

            listener.updateStatus(stringBuilder.toString());
            Log.d(getClass().getName(), stringBuilder.toString());
        }
    }
}
