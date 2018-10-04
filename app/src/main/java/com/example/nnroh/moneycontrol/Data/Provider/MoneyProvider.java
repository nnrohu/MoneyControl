package com.example.nnroh.moneycontrol.Data.Provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.nnroh.moneycontrol.Data.local.DebtsContract.DebtsEntry;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PaymentsEntry;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PersonsEntry;
import com.example.nnroh.moneycontrol.Data.local.DebtsDbHelper;

import static com.example.nnroh.moneycontrol.Data.local.DebtsContract.CONTENT_AUTHORITY;
import static com.example.nnroh.moneycontrol.Data.local.DebtsContract.DebtsEntry.PATH_DEBT;
import static com.example.nnroh.moneycontrol.Data.local.DebtsContract.DebtsEntry.PATH_DEBT_JOIN;
import static com.example.nnroh.moneycontrol.Data.local.DebtsContract.PaymentsEntry.PATH_PAYMENT;
import static com.example.nnroh.moneycontrol.Data.local.DebtsContract.PersonsEntry.PATH_PERSON;

public class MoneyProvider extends ContentProvider {

    private DebtsDbHelper mDbHelper;

    public MoneyProvider() {
    }

    /** Tag for the log messages */
    public static final String LOG_TAG = DebtProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the debt table */
    private static final int DEBT = 200;

    /** URI matcher code for the content URI for a single debt in the debt table */
    private static final int DEBT_ID = 201;

    /** URI matcher code for the content URI for the person table */
    private static final int PERSON = 100;

    /** URI matcher code for the content URI for a single person in the person table */
    private static final int PERSON_ID = 101;

    /** URI matcher code for the content URI for the payment table */
    private static final int PAYMENT = 300;

    /** URI matcher code for the content URI for a single payment in the payment table */
    private static final int PAYMENT_ID = 301;

    /** URI matcher code for the content URI for join debt table with person table */
    public static final int DEBT_JOIN = 202;

    /** URI matcher code for the content URI for getting single from join table */
    public static final int DEBT_JOIN_ID = 203;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);



    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_DEBT, DEBT);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_DEBT + "/#", DEBT_ID);

        //join table path
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_DEBT_JOIN, DEBT_JOIN);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_DEBT_JOIN + "/#", DEBT_JOIN_ID);

        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PERSON, PERSON);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PERSON + "/#", PERSON_ID);

        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PAYMENT, PAYMENT);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PAYMENT + "/#", PAYMENT_ID);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DEBT:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(DebtsEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;

            case DEBT_ID:
                // Delete a single row given by the ID in the URI
                selection = DebtsEntry.COLUMN_ENTRY_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(DebtsEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;

            case PAYMENT:

                rowsDeleted = database.delete(PaymentsEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;

            case PAYMENT_ID:

                // Delete a single row given by the ID in the URI
                selection = PaymentsEntry.COLUMN_ENTRY_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(PaymentsEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case DEBT:
                return insertDebt(uri, values);

            case PERSON:
                return insertPerson(uri, values);

            case PAYMENT:
                return insertPayment(uri, values);
            default:
                throw new IllegalArgumentException("insertion is not supported for " + uri);
        }
    }

    private Uri insertPerson(Uri uri, ContentValues values) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new debt with the given values
        long id = database.insert(PersonsEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //notify all listener that data has been changed
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertPayment(Uri uri, ContentValues values) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new debt with the given values
        long id = database.insert(PaymentsEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //notify all listener that data has been changed
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertDebt(Uri uri, ContentValues values){
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new debt with the given values
        long id = database.insert(DebtsEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //notify all listener that data has been changed
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DebtsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PERSON:

                cursor = db.query(PersonsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PERSON_ID:

                selection = PersonsEntry._ID + " =? ";
                selectionArgs = new String[]{String.valueOf(PersonsEntry._ID)};

                // This will perform a query on the person table where the _id equals to return a
                // Cursor containing that row of the table.
                cursor = db.query(PersonsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case DEBT:

                cursor = db.query(DebtsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case DEBT_ID:

                selection = DebtsEntry.COLUMN_ENTRY_ID + " =? ";
                selectionArgs = new String[]{String.valueOf(DebtsEntry.COLUMN_ENTRY_ID)};

                cursor = db.query(DebtsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case DEBT_JOIN:

                cursor = debtJoinQuery(db, projection, selection, selectionArgs, sortOrder);

                break;

            case DEBT_JOIN_ID:
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //set Notification uri on cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor debtJoinQuery(SQLiteDatabase db, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        String tableWithJoin = DebtsEntry.TABLE_NAME + " JOIN " +
                PersonsEntry.TABLE_NAME + " ON " +
                DebtsEntry.getQName(DebtsEntry.COLUMN_PERSON_PHONE_NUMBER) + " = " +
                PersonsEntry.getQName(PersonsEntry.COLUMN_PHONE_NO);
        return db.query(tableWithJoin, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PERSON:
                return updatePerson(uri, values, selection, selectionArgs);
            case PERSON_ID:
                selection = DebtsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePerson(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updatePerson(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(PersonsEntry.COLUMN_PHONE_NO)){
            String number = values.getAsString(PersonsEntry.COLUMN_PHONE_NO);
            if (number == null) {
                throw new IllegalArgumentException("Requires a number");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(PersonsEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
