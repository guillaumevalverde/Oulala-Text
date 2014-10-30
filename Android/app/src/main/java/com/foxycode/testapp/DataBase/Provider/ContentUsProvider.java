package com.foxycode.testapp.DataBase.Provider;

/**
 * Created by gve on 19/09/2014.
 */


        import android.content.ContentResolver;
        import android.content.ContentValues;
        import android.content.UriMatcher;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteQueryBuilder;
        import android.net.Uri;
        import android.text.TextUtils;
        import android.util.Log;

        import com.foxycode.testapp.DataBase.Contract.ContentUs;

public class ContentUsProvider extends BaseContentProvider {
    public static final String TAG = "ContentUsProvider";
    public static final String TABLE_NAME = "contentStorage";

    private static final int CONTACTS = 10;
    private static final int CONTENT_ID = 20;
    private static final int CONTENTS_SELECT = 40;
    private static final String BASE_PATH = "contacts";
    private static final String BASE_PATH_SELECT = "contacts_select";

    public static final String AUTHORITY = "com.foxycode.testapp.DataBase.Provider.content.contentprovider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + AUTHORITY
            + ".contents";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + AUTHORITY + ".content";



    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, CONTACTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SELECT, CONTENTS_SELECT);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", CONTENT_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case CONTACTS:
                try{
                    rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                }catch(Exception e){
                    Log.e(TAG, e.getMessage());
                }
                break;
            case CONTENT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(TABLE_NAME, ContentUs.ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(TABLE_NAME, ContentUs.ID + "=" + id + " and "
                            + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        String type = null;
        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
            case CONTACTS:
                type = CONTENT_TYPE;
                break;
            case CONTENT_ID:
                type = CONTENT_ITEM_TYPE;
                break;
        }

        return type;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = -1;
        switch (uriType) {
            case CONTACTS:
                id = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Log.d(TAG,"row : "+id);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CONTACTS:
                break;
            case CONTENT_ID:
                // Adding the ID to the original query
                queryBuilder.appendWhere(ContentUs.ID + "=" + uri.getLastPathSegment());
                break;
            case CONTENTS_SELECT:
                // Adding the ID to the original query
                queryBuilder.appendWhere(ContentUs.ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null,
                sortOrder);
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case CONTACTS:
                rowsUpdated = sqlDB.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case CONTENT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(TABLE_NAME, values, ContentUs.ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(TABLE_NAME, values, ContentUs.ID + "=" + id + " and "
                            + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }


}
