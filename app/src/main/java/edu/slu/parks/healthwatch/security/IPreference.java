package edu.slu.parks.healthwatch.security;

/**
 * Created by okori on 30-Dec-16.
 */

public interface IPreference {
    boolean getBoolean(String key);

    String getString(String key);

    void saveString(String data, String key);

    void saveBoolean(boolean data, String key);

    int getInteger(String key, int defaults);
}
