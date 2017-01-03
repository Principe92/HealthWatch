package edu.slu.parks.healthwatch.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by okori on 30-Dec-16.
 */
public class Preference implements IPreference {
    private final SharedPreferences preferences;
    private final Context context;

    public Preference(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public boolean getBoolean(String key) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(key, false);
    }

    @Override
    public String getString(String key) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, "");
    }

    @Override
    public void saveString(String data, String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, data);
        editor.apply();
    }

    @Override
    public void saveBoolean(boolean data, String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, data);
        editor.apply();
    }

    @Override
    public int getInteger(String key, int defaults) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(key, defaults);
    }
}
