package com.example.nnroh.moneycontrol.Data.local;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class DebtsContract {
    // prevent instantiation
    private DebtsContract() {}


    // Authority of database
    public static final String CONTENT_AUTHORITY = "com.example.nnroh.debt.provider";

    // Debts Table
    public static final class DebtsEntry implements BaseColumns {


        //Base contents Uri
        private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        //This constants stores the path for each of the tables which will be appended to the base content URI.
        public static final String PATH_DEBT= "debts";
        public static final String PATH_DEBT_JOIN= "debts_join";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of data.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DEBT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single data.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DEBT;


        /**
         * The content URI to access the people data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DEBT);
        public static final Uri CONTENT_URI_JOIN = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DEBT_JOIN);

        public static final String TABLE_NAME = "debts";

        public static final String ALIAS_DEBT_ID = "debt_id";
        public static final String ALIAS_DATE_ENTERED = "debt_date_entered";
        public static final String ALIAS_AMOUNT = "debt_amount";
        public static final String ALIAS_NOTE = "debt_note";
        public static final String ALIAS_PERSON_PHONE_NUMBER = "debt_person_phone_number";
        public static final String COLUMN_ENTRY_ID = "entry_id";
        public static final String COLUMN_PERSON_PHONE_NUMBER = "person_phone_number";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_DATE_DUE = "date_due";
        public static final String COLUMN_DATE_ENTERED = "date_entered";
        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_TYPE = "debt_type";

        // returns all columns as array
        public static String[] getAllColumns() {
            return new String[]{
                    COLUMN_ENTRY_ID,
                    COLUMN_PERSON_PHONE_NUMBER,
                    COLUMN_STATUS,
                    COLUMN_AMOUNT,
                    COLUMN_DATE_DUE,
                    COLUMN_DATE_ENTERED,
                    COLUMN_NOTE,
                    COLUMN_TYPE
            };
        }

        public static final String getQName(String columnName){
            return TABLE_NAME + "." + columnName;
        }

    }

    // Persons Table
    public static abstract class PersonsEntry implements BaseColumns {

        //Base contents Uri
        private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        //This constants stores the path for each of the tables which will be appended to the base content URI.
        public static final String PATH_PERSON= "persons";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of data.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSON;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single data.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSON;


        /**
         * The content URI to access the people data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PERSON);

        public static final String TABLE_NAME = "persons";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE_NO = "phone_no";
        public static final String COLUMN_IMAGE_URI = "image_uri";

        // returns all columns as array
        public static String[] getAllColumns() {
            return new String[]{
                    COLUMN_NAME,
                    COLUMN_PHONE_NO,
                    COLUMN_IMAGE_URI
            };
        }

        public static final String getQName(String columnName){
            return TABLE_NAME + "." + columnName;
        }

    }

    // Payments Table
    public static abstract class PaymentsEntry implements BaseColumns {

        //Base contents Uri
        private  static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        //This constants stores the path for each of the tables which will be appended to the base content URI.
        public static final String PATH_PAYMENT= "payments";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of data.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PAYMENT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single data.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PAYMENT;


        /**
         * The content URI to access the people data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PAYMENT);


        public static final String TABLE_NAME = "payments";

        public static final String COLUMN_AMOUNT = "amount";
        public static final String ALIAS_AMOUNT = "payment_amount";
        public static final String ALIAS_DATE_ENTERED = "payment_date_entered";
        public static final String ALIAS_PERSON_PHONE_NUMBER = "payment_person_number";
        public static final String ALIAS_NOTE = "payment_note";
        public static final String COLUMN_ENTRY_ID = "entry_id";
        public static final String COLUMN_DEBT_ID = "debt_id";
        public static final String COLUMN_DATE_ENTERED = "date_entered";
        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_PERSON_PHONE_NUMBER = "person_phone_number";

        public static String[] getAllColumns() {
            return new String[] {
                    COLUMN_AMOUNT,
                    COLUMN_DEBT_ID,
                    COLUMN_DATE_ENTERED,
                    COLUMN_ENTRY_ID,
                    COLUMN_NOTE,
                    COLUMN_PERSON_PHONE_NUMBER
            };
        }

        public static final String getQName(String columnName){
            return TABLE_NAME + "." + columnName;
        }
    }

}
