package edu.slu.parks.healthwatch.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import edu.slu.parks.healthwatch.utils.Constants;

/**
 * Created by okori on 22-Nov-16.
 */

public class ConnectThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final Handler handler;
    private ConnectedThread connectedThread;

    public ConnectThread(BluetoothDevice device, Handler handler) {
        this.handler = handler;
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        Log.d(getClass().getName(), "Called");


        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(Constants.UUID));
        } catch (IOException e) {
        }
        mmSocket = tmp;
    }

    @Override
    public void run() {

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            Log.d(this.getClass().getName(), "run");
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) {
            }
            return;
        }

        // Do work to manage the connection (in a separate thread)

        connectedThread = new ConnectedThread(mmSocket, handler);
        connectedThread.start();
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */

    public void cancel() {
        try {
            mmSocket.close();
            if (connectedThread != null) connectedThread.cancel();
        } catch (IOException e) {
        }
    }
}
