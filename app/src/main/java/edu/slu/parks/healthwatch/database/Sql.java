package edu.slu.parks.healthwatch.database;

/**
 * Created by okori on 11-Nov-16.
 */

public class Sql {
    public static final String CREATE_TABLE =
            "CREATE TABLE " + Table.NAME
                    + " ("
                    + Table.ID + " INTEGER PRIMARY KEY, "
                    + Table.SYSTOLIC + " REAL, "
                    + Table.DIASTOLIC + " REAL, "
                    + Table.COMMENT + " TEXT, "
                    + Table.LATITUDE + " INTEGER, "
                    + Table.LONGITUDE + " INTEGER, "
                    + Table.DATE + " TEXT"
                    + ");";

    public static final String UPGRADE_TABLE = "DROP TABLE IF EXISTS " + Table.NAME;
}
