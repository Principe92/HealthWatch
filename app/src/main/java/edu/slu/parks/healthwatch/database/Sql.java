package edu.slu.parks.healthwatch.database;

/**
 * Created by okori on 11-Nov-16.
 */

public class Sql {
    public static final String CREATE_TABLE =
            "CREATE TABLE " + Table.NAME
                    + " ("
                    + Table.ID + " INTEGER PRIMARY KEY, "
                    + Table.SYSTOLIC + " INTEGER, "
                    + Table.DIASTOLIC + " INTEGER, "
                    + Table.COMMENT + " TEXT, "
                    + Table.LATITUDE + " REAL, "
                    + Table.LONGITUDE + " REAL, "
                    + Table.DATE + " TEXT"
                    + ");";

    public static final String UPGRADE_TABLE = "DROP TABLE IF EXISTS " + Table.NAME;
    public static final String getLatestReading =
            "SELECT * FROM " + Table.NAME
                    + " ORDER BY datetime(date) DESC"
                    + " LIMIT 1";
}
