package edu.slu.parks.healthwatch.database;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by okori on 07-Nov-16.
 */

public interface IHealthDb {
    Record getLatestReading();

    long addRecord(Record record);

    List<Record> getRecordByDate(DateTime date);
}
