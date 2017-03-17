package edu.slu.parks.healthwatch.bluetooth;

import android.os.Binder;

/**
 * Created by okori on 09-Mar-17.
 */
public class LocalBinder extends Binder {
    private BluetoothLeService bluetoothLeService;

    public LocalBinder(BluetoothLeService bluetoothLeService) {
        this.bluetoothLeService = bluetoothLeService;
    }

    BluetoothLeService getService() {
        return bluetoothLeService;
    }
}
