package edu.slu.parks.healthwatch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.slu.parks.healthwatch.security.IPinManager;
import edu.slu.parks.healthwatch.security.IPreference;
import edu.slu.parks.healthwatch.security.PinManager;
import edu.slu.parks.healthwatch.security.Preference;
import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.utils.SleepTimer;

/**
 * Created by okori on 02-Jan-17.
 */

public abstract class BaseActivity extends AppCompatActivity implements PinManager.PinManagerListener {
    protected IPreference preference;
    protected IPinManager pinManager;
    private SleepTimer sleepTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        preference = new Preference(this);
        pinManager = new PinManager(this, this);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        //Reset the timer on user interaction...

        if (sleepTimer != null) {
            sleepTimer.cancel();
            sleepTimer.start();
        }
    }

    private int getTimeOut() {
        int timeout;

        try {
            timeout = Integer.parseInt(preference.getString(getString(R.string.key_timeout)));

        } catch (NumberFormatException e) {
            timeout = 1;
        }

        return timeout;
    }

    public abstract int getLayoutId();

    @Override
    protected void onStop() {
        super.onStop();

        sleepTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();

        long startTime = getTimeOut() * 60 * 1000;
        sleepTimer = new SleepTimer(this, startTime, Constants.INTERVAL);
        sleepTimer.start();
    }

    @Override
    public void showMessage(String message) {
    }
}
