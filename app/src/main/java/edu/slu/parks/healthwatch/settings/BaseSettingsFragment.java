package edu.slu.parks.healthwatch.settings;

import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by okori on 11/1/2016.
 */

public abstract class BaseSettingsFragment extends PreferenceFragment {


    protected abstract boolean respondToPreferenceChange(Preference preference, Object value);
}
