package edu.slu.parks.healthwatch.settings;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.noelchew.sparkpostutil.library.EmailListener;

import java.util.List;

import edu.slu.parks.healthwatch.AppCompatPreferenceActivity;
import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.security.FingerPrint;
import edu.slu.parks.healthwatch.security.IFingerPrint;
import edu.slu.parks.healthwatch.utils.Constants;


public class SettingsActivity extends AppCompatPreferenceActivity implements PrivacyPreferenceFragment.SettingsListener, EmailListener {

    private SwitchPreference gpsSetting;
    private SwitchPreference fingerPrintSetting;
    private IFingerPrint fingerPrint;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        fingerPrint = new FingerPrint(getApplicationContext(), new FingerPrint.FingerPrintListener() {
            @Override
            public void requestFingerPrintPermission() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(SettingsActivity.this,
                            new String[]{Manifest.permission.USE_FINGERPRINT},
                            Constants.REQUEST_FINGERPRINT);
                }
            }

            @Override
            public void addFingerPrints() {
                Snackbar.make(getSupportActionBar().getCustomView(), "Fingerprint is not setup", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Go to settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS), 0);
                            }
                        })
                        .show();
            }

            @Override
            public void onAuthenticated() {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || PrivacyPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length < 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    gpsSetting.setChecked(false);
                }
            }
            break;

            case Constants.REQUEST_FINGERPRINT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length < 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    fingerPrintSetting.setChecked(false);
                }
            }
            break;
        }
    }

    @Override
    public void requestPermission(SwitchPreference preference) {
        gpsSetting = preference;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.REQUEST_ACCESS_FINE_LOCATION);

        }
    }

    @Override
    public void requestFingerPrintPermission(SwitchPreference preference) {
        fingerPrintSetting = preference;
        fingerPrint.IsFingerPrintSetUp();
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        showMessage(getString(R.string.reset_mail_sent));
    }

    @Override
    public void onError(String errorMessage) {
        showMessage(errorMessage);
    }
}
