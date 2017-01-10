package edu.slu.parks.healthwatch.settings;

import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

/**
 * Created by okori on 11/1/2016.
 */

public abstract class BaseSettingsFragment extends PreferenceFragment {

    protected Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            return respondToPreferenceChange(preference, value);
        }
    };

    protected abstract boolean respondToPreferenceChange(Preference preference, Object value);


    protected void initPreference(Preference preference, Object def) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, def);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
