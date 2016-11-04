package edu.slu.parks.healthwatch;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import edu.slu.parks.healthwatch.utils.Constants;

/**
 * Created by okori on 11/3/2016.
 */
public class AddressResultReceiver extends ResultReceiver {
    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        // Display the address string
        // or an error message sent from the intent service.
        String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

        // Show a toast message if an address was found.
        if (resultCode == Constants.SUCCESS_RESULT) {
            Log.d(Constants.TAG, mAddressOutput);
        }

    }
}
