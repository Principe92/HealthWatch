package edu.slu.parks.healthwatch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import edu.slu.parks.healthwatch.model.IDate;
import edu.slu.parks.healthwatch.model.JodaDate;

/**
 * Created by okori on 07-Nov-16.
 */
public class HealthDb extends SQLiteOpenHelper implements IHealthDb {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "HealthDb";
    private final IDate myDate;
    private IHealthDbMapper mapper;

    public HealthDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mapper = new HealthDbMapper();
        this.myDate = new JodaDate(context);
    }

    @Override
    public Record getLatestReading() {
        String query = "SELECT * FROM " + Table.NAME
                + " ORDER BY datetime(date) DESC"
                + " LIMIT 1";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            return mapper.toRecord(cursor);
        }

        return null;
    }

    @Override
    public long addRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();



        // Inserting Row
        ContentValues values = mapper.toDbRow(record);
        long id = db.insert(Table.NAME, null, values);
        db.close(); // Closing database connection

        return id;
    }

    @Override
    public List<Record> getRecordByDate(DateTime date) {
        List<Record> records = new ArrayList<>();

        String query = "SELECT * FROM " + Table.NAME
                //+ " WHERE date like '%" + myDate.toString("yyyy-MM-d", date) + "%'";
                + " WHERE strftime('%Y-%m-%d', date) = strftime('%Y-%m-%d', '" + date.toString() + "')";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Record record = mapper.toRecord(cursor);

                // Adding contact to list
                if (record != null) records.add(record);
            } while (cursor.moveToNext());
        }

        // return contact list
        return records;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Sql.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Sql.UPGRADE_TABLE);

        onCreate(db);
    }
}
