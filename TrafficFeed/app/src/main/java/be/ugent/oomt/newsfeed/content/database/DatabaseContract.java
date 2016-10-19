package be.ugent.oomt.newsfeed.content.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.Locale;

/**
 * Created by elias on 04/11/13.
 */
public abstract class DatabaseContract {

    // If you change the database schema, you must increment the database version.
    static final int DATABASE_VERSION = 8;
    static final String DATABASE_NAME = "database.db";

    static void onCreate(SQLiteDatabase db) {
        Item.onCreate(db);
    }

    static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseContract.class.getName(), String.format("Upgrading database from version %d to %d, which will destroy all old data", oldVersion, newVersion));
        Item.onUpgrade(db, oldVersion, newVersion);
    }

    public static abstract class Item implements BaseColumns {

        private static final String TAG = Item.class.getCanonicalName();

        // Database table
        public static final String TABLE_NAME = "traffic_alerts";
        public static final String COLUMN_NAME_ID = _ID;
        public static final String COLUMN_NAME_CONCAT_NAME = "concatName";
        public static final String COLUMN_NAME_SOURCE = "source";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_TRANSPORT = "transport";
        public static final String COLUMN_NAME_ALARM_NAME = "alarmName";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_TIME_STAMP = "timestamp";
        public static final String DEFAULT_SORT_ORDER = COLUMN_NAME_TIME_STAMP + " DESC";

        // Database creation SQL statement
        private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY, " +
                COLUMN_NAME_CONCAT_NAME + " TEXT, " +
                COLUMN_NAME_SOURCE + " TEXT, " +
                COLUMN_NAME_TYPE + " TEXT, " +
                COLUMN_NAME_TRANSPORT + " TEXT, " +
                COLUMN_NAME_ALARM_NAME + " TEXT, " +
                COLUMN_NAME_MESSAGE + " TEXT, " +
                COLUMN_NAME_LONGITUDE + " REAL, " +
                COLUMN_NAME_LATITUDE + " REAL, " +
                COLUMN_NAME_TIME_STAMP + " INTEGER " +
                " )";

        static void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE);
        }

        static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            Log.i(TAG, String.format(Locale.getDefault(), "Updating database from version %d to %d.", oldVersion, newVersion));
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(database);
        }
    }
}
