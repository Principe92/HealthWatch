package edu.slu.parks.healthwatch.utils;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

import edu.slu.parks.healthwatch.model.IAddressReceiver;

/**
 * Created by okori on 11/3/2016.
 */
public class AddressResultReceiver extends ResultReceiver {
    private IAddressReceiver receiver;

    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null)
            receiver.onAddressReceived(resultCode, resultData);
    }

    public void setReceiver(IAddressReceiver receiver) {
        this.receiver = receiver;
    }
}
