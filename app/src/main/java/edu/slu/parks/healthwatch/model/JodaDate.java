package edu.slu.parks.healthwatch.model;

import android.content.Context;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by okori on 13-Nov-16.
 */
public class JodaDate implements IDate {

    public JodaDate(Context context) {
        JodaTimeAndroid.init(context);
    }

    @Override
    public String toString(String format, DateTime date) {

        DateTimeFormatter fr = DateTimeFormat.forPattern(format);

        return fr.print(date);
    }
}
