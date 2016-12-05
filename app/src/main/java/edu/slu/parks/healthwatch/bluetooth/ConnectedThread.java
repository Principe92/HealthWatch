package edu.slu.parks.healthwatch.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * Created by okori on 22-Nov-16.
 */

public class ConnectedThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private android.os.Handler mHandler;

    public ConnectedThread(BluetoothSocket socket, Handler mHandler) {
        mmSocket = socket;
        this.mHandler = mHandler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        Log.d(getClass().getName(), "Called");

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        Log.d(this.getClass().getName(), "run");

        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Tell device to start measurement
        byte[] sent = String.valueOf(Phase.START.value).getBytes();
        write(sent);

        // Display message on device
        mHandler.obtainMessage(Phase.START.value, 0, -1, null)
                .sendToTarget();

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {

                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                String res = new String(buffer, "US-ASCII").split("\n")[0];
                int value;// = ByteBuffer.wrap(buffer).getInt();

                try {
                    value = Integer.parseInt(res);

                } catch (Exception exception) {
                    value = Phase.UNKNOWN.value;
                }

                Log.d(getClass().getName(), "phase: " + res);

                // Send the obtained bytes to the UI activity
                mHandler.obtainMessage(value, bytes, -1, buffer)
                        .sendToTarget();

                if (Phase.toEnum(value) == Phase.DONE) break;

            } catch (IOException e) {
                Log.d(getClass().getName(), e.toString());
                break;
            }
        }
    }

    private byte[] intToByte(int value) {
        return BigInteger.valueOf(value).toByteArray();
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}

