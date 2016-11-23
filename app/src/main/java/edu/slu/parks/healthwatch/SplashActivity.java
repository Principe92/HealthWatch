package edu.slu.parks.healthwatch;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
