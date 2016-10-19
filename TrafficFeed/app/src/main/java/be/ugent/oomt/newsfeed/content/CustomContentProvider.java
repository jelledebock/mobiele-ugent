package be.ugent.oomt.newsfeed.content;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import be.ugent.oomt.newsfeed.content.database.DatabaseContract;
import be.ugent.oomt.newsfeed.content.database.DbHelper;


/**
 * Created by elias on 04/11/13.
 */
public class CustomContentProvider extends ContentProvider {

	// database
    private SQLiteDatabase sqlDB;

    // used for the UriMatcher
    private static final int ITEMS = 10;
    private static final int ITEM_ID = 20;
    private static final String ITEMS_PATH = "items";

    // unique namespace for contentprovider (provider name)
    private static final String AUTHORITY = "be.ugent.oomt.newsfeed.content.CustomContentProvider";
    public static final Uri ITEMS_CONTENT_URL = Uri.parse("content://" + AUTHORITY + "/" + ITEMS_PATH);

    public static final String CONTENT_ITEMS_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/items";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/item";

    private static final UriMatcher sURIMatcher;
    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(AUTHORITY, ITEMS_PATH, ITEMS);
        sURIMatcher.addURI(AUTHORITY, ITEMS_PATH + "/#", ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        DbHelper dbHelper = new DbHelper(getContext());
        sqlDB = dbHelper.getWritableDatabase();
        return sqlDB != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {

    	//String groupBy = DatabaseContract.Message._ID;
        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case ITEMS:
                queryBuilder.setTables(DatabaseContract.Item.TABLE_NAME);
                break;
            case ITEM_ID:
                queryBuilder.setTables(DatabaseContract.Item.TABLE_NAME);
                queryBuilder.appendWhere(DatabaseContract.Item._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        long id;
        Uri returnUri;
        switch (uriType) {
            case ITEMS:
                id = sqlDB.insertWithOnConflict(DatabaseContract.Item.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                returnUri = ContentUris.withAppendedId(ITEMS_CONTENT_URL, id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int uriType = sURIMatcher.match(uri);
        int rowsInserted = 0;
        switch (uriType) {
            case ITEMS:
                sqlDB.beginTransaction();
                try {
                    for (ContentValues c : values) {
                        if (sqlDB.insertWithOnConflict(DatabaseContract.Item.TABLE_NAME, null, c, SQLiteDatabase.CONFLICT_REPLACE) != -1)
                            rowsInserted++;
                    }
                    sqlDB.setTransactionSuccessful();
                } finally {
                    sqlDB.endTransaction();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsInserted;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted;
        String id;
        switch (uriType) {
            case ITEMS:
                rowsDeleted = sqlDB.delete(DatabaseContract.Item.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DatabaseContract.Item.TABLE_NAME, DatabaseContract.Item._ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(DatabaseContract.Item.TABLE_NAME, DatabaseContract.Item._ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        int rowsUpdated;
        String id;
        switch (uriType) {
            case ITEMS:
                rowsUpdated = sqlDB.update(DatabaseContract.Item.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DatabaseContract.Item.TABLE_NAME, values, DatabaseContract.Item._ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(DatabaseContract.Item.TABLE_NAME, values, DatabaseContract.Item._ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        String type;
        switch (uriType) {
            case ITEMS:
                type = CONTENT_ITEMS_TYPE;
                break;
            case ITEM_ID:
                type = CONTENT_ITEM_TYPE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return type;
    }
}
