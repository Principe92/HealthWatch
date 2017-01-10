package edu.slu.parks.healthwatch.settings;

/**
 * Created by okori on 11/1/2016.
 */

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Locale;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.utils.Util;

/**
 * This fragment shows general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GeneralPreferenceFragment extends BaseSettingsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);

        SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(getActivity());

        initPreference(findPreference(getString(R.string.key_email)), manager.getString(getString(R.string.key_email), ""));
        initPreference(findPreference(getString(R.string.key_timeout)), manager.getString(getString(R.string.key_timeout), "1"));
    }

    @Override
    protected boolean respondToPreferenceChange(Preference preference, Object value) {
        String key = preference.getKey();

        if (key.equals(getString(R.string.key_email))) {
            String mail = (String) value;

            if (Util.emailIsValid(mail))
                preference.setSummary(mail);
            else {
                Toast.makeText(getActivity(), R.string.invalid_email_address, Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (key.equals(getString(R.string.key_timeout))) {
            String timeout = (String) value;
            preference.setSummary(String.format(Locale.getDefault(), "Request pin after %s minute of inactivity", timeout));
        }

        return true;
    }
}
