package edu.slu.parks.healthwatch.model;

import android.os.Bundle;

/**
 * Created by okori on 07-Nov-16.
 */

public interface IAddressReceiver {
    void onAddressReceived(int resultCode, Bundle resultData);
}
