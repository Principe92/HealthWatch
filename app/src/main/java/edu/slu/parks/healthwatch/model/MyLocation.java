package edu.slu.parks.healthwatch.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

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
        editor.putLong(context.getString(R.string.gps_longitude), Double.doubleToRawLongBits(location.getLongitude()));
        editor.putLong(context.getString(R.string.gps_latitude), Double.doubleToRawLongBits(location.getLatitude()));
        editor.apply();

        Log.d(this.getClass().getName(), "longitude: " + location.getLongitude());
        Log.d(this.getClass().getName(), "latitude: " + location.getLatitude());
    }

    @Override
    public void update(Location location) {
        if (location != null)
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

    @Override
    public boolean geoCoderIsPresent() {
        return Geocoder.isPresent();
    }
}
