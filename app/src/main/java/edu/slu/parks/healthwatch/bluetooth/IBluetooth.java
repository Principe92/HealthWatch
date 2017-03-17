package edu.slu.parks.healthwatch.bluetooth;

/**
 * Created by okori on 14-Feb-17.
 */

public interface IBluetooth {
    void enable();

    void close();

    void onResume();

    void onPause();
}
