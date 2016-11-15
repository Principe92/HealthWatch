package edu.slu.parks.healthwatch.model;

import org.joda.time.DateTime;

/**
 * Created by okori on 13-Nov-16.
 */

public interface IDate {
    String toString(String format, DateTime date);
}
