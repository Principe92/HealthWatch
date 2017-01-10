package edu.slu.parks.healthwatch.settings;

/**
 * Created by okori on 11/1/2016.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import edu.slu.parks.healthwatch.R;

/**
 * This fragment shows data and sync preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PrivacyPreferenceFragment extends BaseSettingsFragment {
    private SettingsListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SettingsListener) {
            mListener = (SettingsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SignInFragment.SignInListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_data_sync);
        setHasOptionsMenu(true);

        SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initPreference(findPreference(getString(R.string.gps_switch)), manager.getBoolean(getString(R.string.gps_switch), false));
    }

    @Override
    protected boolean respondToPreferenceChange(Preference preference, Object value) {
        String key = preference.getKey();

        if (key.equals(getString(R.string.gps_switch))) {
            Boolean checked = (Boolean) value;

            if (checked && mListener != null) {
                mListener.requestPermission((SwitchPreference) preference);
            }
        }

        return true;
    }


    public interface SettingsListener {

        void requestPermission(SwitchPreference preference);
    }
}
