package edu.slu.parks.healthwatch.database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.Random;

import edu.slu.parks.healthwatch.R;

/**
 * Created by okori on 07-Nov-16.
 */
public class WatchDb implements IWatchDb {

    private Activity context;

    public WatchDb(Activity context) {
        this.context = context;
    }

    @Override
    public Record getLatestReading() {

        Record r = new Record();
        r.date = new Date();
        r.diastolic = Math.abs(new Random().nextInt() % 220);
        r.systolic = Math.abs(new Random().nextInt() % 220);

        // save location
        SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
        r.latitude = Double.longBitsToDouble(sharedPref.getLong(context.getString(R.string.gps_latitude), 0));
        r.longitude = Double.longBitsToDouble(sharedPref.getLong(context.getString(R.string.gps_longitude), 0));

        return r;
    }
}
