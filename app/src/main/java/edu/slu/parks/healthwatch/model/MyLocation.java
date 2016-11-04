package edu.slu.parks.healthwatch.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import edu.slu.parks.healthwatch.R;

/**
 * Created by okori on 11/3/2016.
 */
public class MyLocation implements ILocation {
    private Activity context;
    private Location location;

    public MyLocation(Activity context) {
        this.context = context;
    }

    @Override
    public void save() {
        SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(context.getString(R.string.gps_latitude), (long) location.getLatitude());
        editor.putLong(context.getString(R.string.gps_latitude), (long) location.getLatitude());
        editor.apply();
    }

    @Override
    public void update(Location location) {
        this.location = location;
    }

    @Override
    public boolean isValid() {
        return location != null;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }
}
