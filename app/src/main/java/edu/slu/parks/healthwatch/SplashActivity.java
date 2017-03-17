package edu.slu.parks.healthwatch;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import net.danlew.android.joda.JodaTimeAndroid;

import edu.slu.parks.healthwatch.security.PinManager;
import edu.slu.parks.healthwatch.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

        PinManager manager = new PinManager(this, null);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, !manager.hasPin());
        JodaTimeAndroid.init(getApplicationContext());

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Constants.GOHOME, true);
        startActivity(intent);
    }
}
