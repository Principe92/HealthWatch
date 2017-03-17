package edu.slu.parks.healthwatch.security;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;

/**
 * Created by okori on 17-Jan-17.
 */

public interface IFingerPrint {

    void startListening();

    @TargetApi(Build.VERSION_CODES.M)
    boolean initCipher();

    void stopListening();

    void setUseFingerPrint(boolean use);

    boolean canUseFingerPrint();

    boolean isFingerprintAuthAvailable();

    boolean IsFingerPrintSetUp();

    void requestFingerPrintPermission(Activity activity);

    void addFingerPrints(View view, final Activity activity);
}
