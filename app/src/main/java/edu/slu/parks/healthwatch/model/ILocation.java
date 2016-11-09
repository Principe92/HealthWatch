package edu.slu.parks.healthwatch.model;

import android.location.Location;

/**
 * Created by okori on 11/3/2016.
 */

public interface ILocation {
    void save();

    void update(Location location);

    boolean isValid();

    Location getLocation();

    boolean geoCoderIsPresent();
}
