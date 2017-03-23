package edu.slu.parks.healthwatch.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Set;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.utils.Constants;

/**
 * Created by okori on 16-Feb-17.
 */
public class OtherBluetooth extends BroadcastReceiver implements IBluetooth {
    private BluetoothAdapter mBluetoothAdapter;
    private Context context;
    private IBluetoothListener listener;

    public OtherBluetooth(Context context, IBluetoothListener listener) {
        this.context = context;
        this.listener = listener;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        handleBluetooth();
    }

    @Override
    public void enable() {
        handleBluetooth();
    }

    @Override
    public void close() {
        mBluetoothAdapter.cancelDiscovery();
        context.unregisterReceiver(this);
    }

    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(this, filter);

        enable();
    }

    @Override
    public void onPause() {
        close();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // When discovery finds a device
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {

            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            String name = device.getName() != null ? device.getName() : "";
            if (name.toLowerCase().contains(Constants.DEVICE_NAME.toLowerCase())) {
                mBluetoothAdapter.cancelDiscovery();
                listener.onDeviceFound(device);
            }
        }
    }

    private void handleBluetooth() {
        listener.updateStatus(R.string.verify_bluetooth);

        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            listener.onBluetoothOff();

        } else if (mBluetoothAdapter == null) {
            listener.onBluetoothNotAvailable();
        } else {
            connectToDevice();
        }
    }

    public void connectToDevice() {
        BluetoothDevice device = getPairedDevice();

        if (device != null) {
            listener.updateStatus(R.string.connecting);
            listener.onDeviceFound(device);

        } else {
            listener.updateStatus(R.string.search);
            performDeviceDiscovery();
        }
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

    public interface IBluetoothListener {

        void onDeviceFound(BluetoothDevice device);

        void onBluetoothNotAvailable();

        void onBluetoothOff();

        void updateStatus(int textId);

        void updateStatus(String message);

        void displayData(String data);

        void onDataAvailable();
    }
}
