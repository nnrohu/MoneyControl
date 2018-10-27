package com.example.nnroh.moneycontrol.Notification;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.nnroh.moneycontrol.Data.local.DebtsContract.DebtsEntry;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PersonsEntry;
import com.example.nnroh.moneycontrol.Data.local.DebtsDbHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class ReminderTasks {


    public static final String ACTION_DEBT_REVIEW_REMINDER = "debt-review-reminder";
    public static final String ACTION_DEBT_REMINDER = "debt-reminder";

    public static void executeTask(Context context, String action) {
         if (ACTION_DEBT_REVIEW_REMINDER.equals(action)) {
            issueReviewReminder(context);
        }else if (ACTION_DEBT_REMINDER.equals(action))
            issueDebtReminder(context);

    }

    private static void issueDebtReminder(Context context) {
        Uri joinUri = DebtsEntry.CONTENT_URI_JOIN;
        String selection = DebtsEntry.COLUMN_DATE_DUE + " < ? " + " AND " + DebtsEntry.COLUMN_AMOUNT + " > ?";


        String[] selectionArgs = {String.valueOf(Calendar.getInstance().getTimeInMillis()), "1"};

        String[] projection = {
                PersonsEntry.getQName(PersonsEntry.COLUMN_IMAGE_URI),
                PersonsEntry.getQName(PersonsEntry.COLUMN_NAME),
                DebtsEntry.getQName(DebtsEntry.COLUMN_AMOUNT)
        };
        final Cursor cursor = context.getContentResolver().query(joinUri, projection, selection, selectionArgs, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int namePos = cursor.getColumnIndex(PersonsEntry.COLUMN_NAME);
                int amountPos = cursor.getColumnIndex(DebtsEntry.COLUMN_AMOUNT);

                String name = cursor.getString(namePos);
                String amount = cursor.getString(amountPos);

                NotificationUtils.remindUserForDebt(context, name, amount);
            }
        cursor.close();
        }
    }

    private static void issueReviewReminder(Context context) {
        NotificationUtils.remindUserForReview(context);
    }


}