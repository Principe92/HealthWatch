package edu.slu.parks.healthwatch.database;

import android.content.ContentValues;
import android.database.Cursor;

import org.joda.time.DateTime;

/**
 * Created by okori on 11-Nov-16.
 */
public class HealthDbMapper implements IHealthDbMapper {
    @Override
    public Record toRecord(Cursor cursor) {
        if (cursor == null) return null;

        Record r = new Record();
        r.id = cursor.getInt(cursor.getColumnIndex(Table.ID));
        r.systolic = cursor.getInt(cursor.getColumnIndex(Table.SYSTOLIC));
        r.diastolic = cursor.getInt(cursor.getColumnIndex(Table.DIASTOLIC));
        r.date = new DateTime(cursor.getString(cursor.getColumnIndex(Table.DATE)));
        r.comment = cursor.getString(cursor.getColumnIndex(Table.COMMENT));
        r.longitude = cursor.getDouble(cursor.getColumnIndex(Table.LONGITUDE));
        r.latitude = cursor.getDouble(cursor.getColumnIndex(Table.LATITUDE));

        return r;
    }

    @Override
    public ContentValues toDbRow(Record record) {
        ContentValues values = new ContentValues();
        values.put(Table.SYSTOLIC, record.systolic);
        values.put(Table.DIASTOLIC, record.diastolic);
        values.put(Table.COMMENT, record.comment);
        values.put(Table.DATE, record.date.toString());
        values.put(Table.LATITUDE, record.latitude);
        values.put(Table.LONGITUDE, record.longitude);
        return values;
    }
}
