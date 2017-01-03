package edu.slu.parks.healthwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.joda.time.DateTime;

import edu.slu.parks.healthwatch.security.IPreference;
import edu.slu.parks.healthwatch.security.Preference;
import edu.slu.parks.healthwatch.utils.Constants;

/**
 * Created by okori on 02-Jan-17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected IPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        preference = new Preference(this);

    }

    public abstract int getLayoutId();

    @Override
    protected void onStop() {
        super.onStop();

        preference.saveString(DateTime.now().toString(), Constants.PAUSED);
    }

    @Override
    public void onResume() {

        String paused = preference.getString(Constants.PAUSED);
        int timeout = preference.getInteger(getString(R.string.key_timeout), 1);

        if (!paused.isEmpty()) {
            DateTime pausedTime = DateTime.parse(paused);
            DateTime now = DateTime.now();
            if (now.getDayOfYear() > pausedTime.getDayOfYear() || (now.getMinuteOfDay() - pausedTime.getMinuteOfDay()) > timeout) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        }

        super.onResume();
    }
}
