package edu.slu.parks.healthwatch.utils;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import edu.slu.parks.healthwatch.authentication.LoginActivity;

/**
 * Created by okori on 09-Jan-17.
 */

public class SleepTimer extends CountDownTimer {
    private Context context;

    public SleepTimer(Context context, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.context = context;
    }

    @Override
    public void onTick(long l) {

    }

    @Override
    public void onFinish() {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
