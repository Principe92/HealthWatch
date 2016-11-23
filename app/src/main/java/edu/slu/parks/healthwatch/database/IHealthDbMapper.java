package edu.slu.parks.healthwatch.database;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by okori on 11-Nov-16.
 */

public interface IHealthDbMapper {
    Record toRecord(Cursor cursor);

    ContentValues toDbRow(Record record);
}
