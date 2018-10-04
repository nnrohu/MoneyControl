package com.example.nnroh.moneycontrol.Data.local;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.nnroh.moneycontrol.Data.Person;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PersonsEntry;
import com.example.nnroh.moneycontrol.Data.Debt;
import com.example.nnroh.moneycontrol.Data.Payment;
import com.example.nnroh.moneycontrol.Data.PersonDebt;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.DebtsEntry;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PaymentsEntry;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private final DebtsDbHelper mDbHelper;

    public DataManager(Context context) {
        mDbHelper = new DebtsDbHelper(context);
    }


    public List<PersonDebt> getAllPersonDebtsByType(int debtType) {

        List<PersonDebt> personDebts = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String where = DebtsEntry.TABLE_NAME + "." + DebtsEntry.COLUMN_TYPE + DebtsDbHelper.WHERE_EQUAL_TO;
        String sql = buildJoinsQueryFromDebtsPersonsTable(where);

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(debtType)});

        if (cursor != null && cursor.getCount() > 0) {

            PersonDebt personDebt;

            while (cursor.moveToNext()) {

                long dateDue = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_DUE));
                String personPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_PERSON_PHONE_NUMBER));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_STATUS));
                int debtType1 = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_TYPE));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_NOTE));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_AMOUNT));
                String entryId = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.ALIAS_DEBT_ID));
                long dateEntered = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_ENTERED));

                Debt debt = new Debt.Builder(entryId, personPhoneNumber, amount, dateEntered, debtType1, status)
                        .dueDate(dateDue)
                        .note(note)
                        .build();

                personDebt = getPersonDebt(debt.getId(), debtType);

                personDebt.getDebt().setAmount(debt.getAmount());

                personDebts.add(personDebt);
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        if (personDebts.isEmpty()) {
            return new ArrayList<>();
        } else {
            return personDebts;
        }
    }

    public PersonDebt getPersonDebt(String debtId,  int debtType) {

        PersonDebt personDebt;

        if (debtHavePayments(debtId)) {
            personDebt = getPersonDebtFromPersonsDebtsPaymentsTable(debtId, debtType);
        }else {
            personDebt = getPersonDebtFromDebtsAndPersonsTable(debtId, debtType);
        }
        return personDebt;
    }


    private PersonDebt getPersonDebtFromPersonsDebtsPaymentsTable( String debtId,  int debtType) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String where = DebtsEntry.TABLE_NAME + "." + DebtsEntry.COLUMN_ENTRY_ID + " =? AND " +
                DebtsEntry.TABLE_NAME + "." + DebtsEntry.COLUMN_TYPE + DebtsDbHelper.WHERE_EQUAL_TO;

        String sql = buildJoinsQueryFromDebtsPersonsPaymentsTable(where);

        Cursor cursor = db.rawQuery(sql, new String[]{debtId, String.valueOf(debtType)});
        PersonDebt personDebt = null;

        if (cursor != null && cursor.getCount() > 0) {

            cursor.moveToFirst();

            String paymentId = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_ENTRY_ID));
            String paymentDebtId = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_DEBT_ID));
            String personPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.ALIAS_PERSON_PHONE_NUMBER));
            double paymentAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(PaymentsEntry.ALIAS_AMOUNT));
            long paymentDateEntered = cursor.getLong(cursor.getColumnIndexOrThrow(PaymentsEntry.ALIAS_DATE_ENTERED));
            String paymentNote = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.ALIAS_NOTE));

            Payment payment = new Payment.Builder()
                    .id(paymentId)
                    .amount(paymentAmount)
                    .debtId(paymentDebtId)
                    .dateEntered(paymentDateEntered)
                    .note(paymentNote)
                    .personPhoneNumber(personPhoneNumber)
                    .build();

            long dateDue = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_DUE));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_STATUS));
            int debtType1 = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_TYPE));
            String debtId2 = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.ALIAS_DEBT_ID));
            long dateEntered = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.ALIAS_DATE_ENTERED));
            String debtNote = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.ALIAS_NOTE));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DebtsEntry.ALIAS_AMOUNT));

            Debt debt = new Debt.Builder(debtId2, personPhoneNumber, amount, dateEntered, debtType1, status)
                    .dueDate(dateDue)
                    .note(debtNote)
                    .build();

            debt.addPayment(payment);

            DatabaseUtils.dumpCursor(cursor);

            Person person = getPersonFromCursor(cursor);

            while (cursor.moveToNext()) {

                String paymentId2 = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_ENTRY_ID));
                String paymentDebtId2 = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_DEBT_ID));
                String personPhoneNumber2 = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.ALIAS_PERSON_PHONE_NUMBER));
                double paymentAmount2 = cursor.getDouble(cursor.getColumnIndexOrThrow(PaymentsEntry.ALIAS_AMOUNT));
                long paymentDateEntered2 = cursor.getLong(cursor.getColumnIndexOrThrow(PaymentsEntry.ALIAS_DATE_ENTERED));
                String paymentNote2 = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.ALIAS_NOTE));

                Payment payment2 = new Payment.Builder()
                        .id(paymentId2)
                        .amount(paymentAmount2)
                        .debtId(paymentDebtId2)
                        .dateEntered(paymentDateEntered2)
                        .note(paymentNote2)
                        .personPhoneNumber(personPhoneNumber2)
                        .build();

                debt.addPayment(payment2);
            }

            personDebt = new PersonDebt(person, debt);
        }

        if (cursor != null) {
            cursor.close();
        }

        return personDebt;
    }


    private Person getPersonFromCursor(Cursor cursor) {
        String personName = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_NAME));
        String personPhoneNo = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_PHONE_NO));
        String personImageUri = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_IMAGE_URI));
        return new Person(personName, personPhoneNo, personImageUri);
    }


    private PersonDebt getPersonDebtFromDebtsAndPersonsTable( String debtId,  int debtType) {

        String where = DebtsEntry.TABLE_NAME + "." + DebtsEntry.COLUMN_ENTRY_ID + " =? AND " +
                DebtsEntry.TABLE_NAME + "." + DebtsEntry.COLUMN_TYPE + DebtsDbHelper.WHERE_EQUAL_TO;

        String sql = buildJoinsQueryFromDebtsPersonsTable(where);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{debtId, String.valueOf(debtType)});
        PersonDebt personDebt = null;

        if (cursor != null && cursor.getCount() > 0) {

            cursor.moveToFirst();

            long dateDue = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_DUE));
            String personPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_PERSON_PHONE_NUMBER));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_STATUS));
            int debtType1 = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_TYPE));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_NOTE));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_AMOUNT));
            String entryId = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.ALIAS_DEBT_ID));
            long dateEntered = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_ENTERED));

            Debt debt = new Debt.Builder(entryId, personPhoneNumber, amount, dateEntered, debtType1, status)
                    .dueDate(dateDue)
                    .note(note)
                    .build();

            Person person = getPersonFromCursor(cursor);

            personDebt = new PersonDebt(person, debt);
            cursor.close();
        }

        if (cursor != null) {
            cursor.close();
        }

        return personDebt;
    }


    private boolean debtHavePayments( String debtId) {
        boolean hasPayments = false;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(PaymentsEntry.TABLE_NAME, PaymentsEntry.getAllColumns(), PaymentsEntry.COLUMN_DEBT_ID + "=?",
                new String[]{debtId}, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            hasPayments = true;
        }

        if (cursor != null) {
            cursor.close();
        }
        return hasPayments;
    }


    private String buildJoinsQueryFromDebtsPersonsTable(String whereValue) {

        String comma = ", ";
        String alias = " AS ";
        String dot = ".";
        StringBuilder sqlStringBuilder = new StringBuilder(40);
        sqlStringBuilder.append("SELECT ").append(DebtsEntry.COLUMN_PERSON_PHONE_NUMBER).append(comma)
                .append(DebtsEntry.TABLE_NAME).append(dot).append(DebtsEntry.COLUMN_ENTRY_ID).append(alias)
                .append(DebtsEntry.ALIAS_DEBT_ID).append(comma).append(DebtsEntry.COLUMN_AMOUNT).append(comma)
                .append(DebtsEntry.TABLE_NAME).append(dot).append(DebtsEntry.COLUMN_DATE_ENTERED).append(comma)
                .append(DebtsEntry.TABLE_NAME).append(dot).append(DebtsEntry.COLUMN_DATE_DUE).append(comma)
                .append(DebtsEntry.TABLE_NAME).append(dot).append(DebtsEntry.COLUMN_NOTE).append(comma)
                .append(DebtsEntry.TABLE_NAME).append(dot).append(DebtsEntry.COLUMN_STATUS).append(comma)
                .append(DebtsEntry.TABLE_NAME).append(dot).append(DebtsEntry.COLUMN_TYPE).append(comma)
                .append(PersonsEntry.TABLE_NAME).append(dot).append(PersonsEntry.COLUMN_NAME).append(comma)
                .append(PersonsEntry.TABLE_NAME).append(dot).append(PersonsEntry.COLUMN_IMAGE_URI).append(comma)
                .append(PersonsEntry.TABLE_NAME).append(dot).append(PersonsEntry.COLUMN_PHONE_NO)
                .append(" FROM ").append(DebtsEntry.TABLE_NAME).append(" INNER JOIN ").append(PersonsEntry.TABLE_NAME)
                .append(" ON ").append(DebtsEntry.TABLE_NAME).append(dot).append(DebtsEntry.COLUMN_PERSON_PHONE_NUMBER)
                .append(" = ").append(PersonsEntry.TABLE_NAME).append(dot).append(PersonsEntry.COLUMN_PHONE_NO);

        if (!"no".equals(whereValue)) {
            sqlStringBuilder.append(DebtsDbHelper.WHERE).append(whereValue);
        }

        return sqlStringBuilder.toString();
    }


    private String buildJoinsQueryFromDebtsPersonsPaymentsTable(String whereValue) {

        String comma = ", ";
        String alias = " AS ";
        String dot = ".";
        StringBuilder sqlStringBuilder = new StringBuilder(51);
        sqlStringBuilder.append("SELECT ").append(DebtsEntry.TABLE_NAME).append(dot).append(DebtsEntry.COLUMN_PERSON_PHONE_NUMBER)
                .append(alias).append(DebtsEntry.ALIAS_PERSON_PHONE_NUMBER).append(comma)
                .append(DebtsEntry.TABLE_NAME).append(dot).append(DebtsEntry.COLUMN_ENTRY_ID).append(alias)
                .append(DebtsEntry.ALIAS_DEBT_ID).append(comma).append(DebtsEntry.TABLE_NAME).append(dot)
                .append(DebtsEntry.COLUMN_AMOUNT).append(alias).append(DebtsEntry.ALIAS_AMOUNT).append(comma)
                .append(DebtsEntry.TABLE_NAME).append(dot).append(DebtsEntry.COLUMN_DATE_ENTERED)
                .append(alias).append(DebtsEntry.ALIAS_DATE_ENTERED).append(comma).append(DebtsEntry.COLUMN_DATE_DUE).append(comma)
                .append(DebtsEntry.TABLE_NAME).append(dot).append(DebtsEntry.COLUMN_NOTE).append(alias)
                .append(DebtsEntry.ALIAS_NOTE).append(comma).append(DebtsEntry.COLUMN_STATUS)
                .append(comma).append(DebtsEntry.COLUMN_TYPE).append(comma).append(PersonsEntry.COLUMN_NAME).append(comma)
                .append(PersonsEntry.COLUMN_IMAGE_URI).append(comma)
                .append(PersonsEntry.COLUMN_PHONE_NO).append(comma)
                .append(PaymentsEntry.TABLE_NAME).append(dot).append(PaymentsEntry.COLUMN_AMOUNT)
                .append(alias).append(PaymentsEntry.ALIAS_AMOUNT).append(comma)
                .append(PaymentsEntry.TABLE_NAME).append(dot).append(PaymentsEntry.COLUMN_DATE_ENTERED).append(alias)
                .append(PaymentsEntry.ALIAS_DATE_ENTERED).append(comma)
                .append(PaymentsEntry.TABLE_NAME).append(dot).append(PaymentsEntry.COLUMN_DEBT_ID).append(comma)
                .append(PaymentsEntry.TABLE_NAME).append(dot).append(PaymentsEntry.COLUMN_ENTRY_ID).append(comma)
                .append(PaymentsEntry.TABLE_NAME).append(dot).append(PaymentsEntry.COLUMN_PERSON_PHONE_NUMBER).append(alias)
                .append(PaymentsEntry.ALIAS_PERSON_PHONE_NUMBER).append(comma)
                .append(PaymentsEntry.TABLE_NAME).append(dot).append(PaymentsEntry.COLUMN_NOTE).append(alias).append(PaymentsEntry.ALIAS_NOTE)
                .append(" FROM ").append(DebtsEntry.TABLE_NAME)
                .append(" INNER JOIN ").append(PersonsEntry.TABLE_NAME)
                .append(" ON ").append(DebtsEntry.TABLE_NAME).append(dot).append(DebtsEntry.COLUMN_PERSON_PHONE_NUMBER)
                .append(" = ").append(PersonsEntry.TABLE_NAME).append(dot).append(PersonsEntry.COLUMN_PHONE_NO)
                .append(" INNER JOIN ").append(PaymentsEntry.TABLE_NAME)
                .append(" ON ").append(DebtsEntry.TABLE_NAME).append(dot).append(DebtsEntry.COLUMN_ENTRY_ID)
                .append(" = ").append(PaymentsEntry.TABLE_NAME).append(dot).append(PaymentsEntry.COLUMN_DEBT_ID);

        if (!"no".equals(whereValue)) {
            sqlStringBuilder.append(DebtsDbHelper.WHERE).append(whereValue);
        }

        return sqlStringBuilder.toString();
    }


    public void savePersonDebt( Debt debt, Person person) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (!personAlreadyExist(person.getPhoneNumber())) {
            ContentValues personValues = new ContentValues();
            personValues.put(PersonsEntry.COLUMN_NAME, person.getFullname());
            personValues.put(PersonsEntry.COLUMN_PHONE_NO, person.getPhoneNumber());
            personValues.put(PersonsEntry.COLUMN_IMAGE_URI, person.getImageUri());
            db.insert(PersonsEntry.TABLE_NAME, null, personValues);
        }

        ContentValues debtValues = new ContentValues();
        debtValues.put(DebtsEntry.COLUMN_ENTRY_ID, debt.getId());
        debtValues.put(DebtsEntry.COLUMN_AMOUNT, debt.getAmount());
        debtValues.put(DebtsEntry.COLUMN_DATE_DUE, debt.getDueDate());
        debtValues.put(DebtsEntry.COLUMN_DATE_ENTERED, debt.getCreatedDate());
        debtValues.put(DebtsEntry.COLUMN_NOTE, debt.getNote());
        debtValues.put(DebtsEntry.COLUMN_PERSON_PHONE_NUMBER, person.getPhoneNumber());
        debtValues.put(DebtsEntry.COLUMN_STATUS, debt.getStatus());
        debtValues.put(DebtsEntry.COLUMN_TYPE, debt.getDebtType());

        db.insert(DebtsEntry.TABLE_NAME, null, debtValues);
    }


    public void savePayment( Payment payment) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues paymentValues = getContentValuesFromPayment(payment);
        db.insert(PaymentsEntry.TABLE_NAME, null, paymentValues);
        Debt debt = getDebt(payment.getDebtId());
        double newAmount = debt.getAmount() - payment.getAmount();
        updateDebtAmount(payment.getDebtId(), newAmount);
//        // perform debt amount update based on action
//        if (payment.getAction() == Payment.PAYMENT_ACTION_DEBT_DECREASE) {
//            double newAmount = getDebt(payment.getDebtId()).getAmount() - payment.getAmount();
//            updateDebtAmount(payment.getDebtId(), newAmount);
//        } else if (payment.getAction() == Payment.PAYMENT_ACTION_DEBT_INCREASE) {
//
//        }
    }


    private void updateDebtAmount(String debtId, double newDebtAmount) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues debtContentValue = new ContentValues();
        debtContentValue.put(DebtsEntry.COLUMN_AMOUNT, newDebtAmount);
        db.update(DebtsEntry.TABLE_NAME, debtContentValue, DebtsEntry.COLUMN_ENTRY_ID + DebtsDbHelper.WHERE_EQUAL_TO,
                new String[]{debtId});
    }


    public void editPayment(Payment payment, Debt debt) {

        Payment initialPayment = getPayment(payment.getId(), debt);
        double initialPaymentAmount = initialPayment.getAmount();
        double initialDebtAmount = getDebt(payment.getDebtId()).getAmount();

            double debtAmount = initialPaymentAmount + initialDebtAmount;
            double newDebtAmount = debtAmount - payment.getAmount();
            updateDebtAmount(payment.getDebtId(), newDebtAmount);


        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues paymentValues = getContentValuesFromPayment(payment);
        db.update(PaymentsEntry.TABLE_NAME, paymentValues, PaymentsEntry.COLUMN_ENTRY_ID + DebtsDbHelper.WHERE_EQUAL_TO,
                new String[]{payment.getId()});
    }


    public Debt getDebt( String debtId) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(DebtsEntry.TABLE_NAME, DebtsEntry.getAllColumns(), DebtsEntry.COLUMN_ENTRY_ID + DebtsDbHelper.WHERE_EQUAL_TO,
                new String[]{debtId}, null, null, null);
        Debt debt = null;

        if (cursor != null && cursor.getCount() > 0) {

            cursor.moveToFirst();

            long dateDue = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_DUE));
            String personPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_PERSON_PHONE_NUMBER));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_STATUS));
            int debtType1 = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_TYPE));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_NOTE));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_AMOUNT));
            String entryId = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_ENTRY_ID));
            long dateEntered = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_ENTERED));

            debt = new Debt.Builder(entryId, personPhoneNumber, amount, dateEntered, debtType1, status)
                    .dueDate(dateDue)
                    .note(note)
                    .build();

            cursor.close();
        }

        return debt;
    }


    public void deletePayment(Payment payment) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(PaymentsEntry.TABLE_NAME, PaymentsEntry.COLUMN_ENTRY_ID + DebtsDbHelper.WHERE_EQUAL_TO,
                new String[]{payment.getId()});

        // perform debt amount update based on action
        updateDebtAmountBasedOnPaymentAction(payment);
    }

    private void updateDebtAmountBasedOnPaymentAction(Payment payment) {

            double newDebtAmount = getDebt(payment.getDebtId()).getAmount() + payment.getAmount();
            updateDebtAmount(payment.getDebtId(), newDebtAmount);

    }


    public Payment getPayment(String paymentId,  Debt debt) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Payment payment = null;
        Cursor cursor = db.query(PaymentsEntry.TABLE_NAME, PaymentsEntry.getAllColumns(),
                PaymentsEntry.COLUMN_ENTRY_ID + DebtsDbHelper.WHERE_EQUAL_TO,
                new String[]{paymentId}, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            String paymentDebtId = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_DEBT_ID));
            String personPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PERSON_PHONE_NUMBER));
            double paymentAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_AMOUNT));
            long paymentDateEntered = cursor.getLong(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_DATE_ENTERED));
            String paymentNote = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_NOTE));

            payment = new Payment.Builder()
                    .id(paymentId)
                    .amount(paymentAmount)
                    .debtId(paymentDebtId)
                    .dateEntered(paymentDateEntered)
                    .note(paymentNote)
                    .personPhoneNumber(personPhoneNumber)
                    .build();
        }

        if (cursor != null) {
            cursor.close();
        }
        return payment;
    }


    private ContentValues getContentValuesFromPayment( Payment payment) {
        ContentValues paymentValues = new ContentValues();
        paymentValues.put(PaymentsEntry.COLUMN_ENTRY_ID, payment.getId());
        paymentValues.put(PaymentsEntry.COLUMN_AMOUNT, payment.getAmount());
        paymentValues.put(PaymentsEntry.COLUMN_DATE_ENTERED, payment.getDateEntered());
        paymentValues.put(PaymentsEntry.COLUMN_DEBT_ID, payment.getDebtId());
        paymentValues.put(PaymentsEntry.COLUMN_NOTE, payment.getNote());
        paymentValues.put(PaymentsEntry.COLUMN_PERSON_PHONE_NUMBER, payment.getPersonPhoneNo());
        return paymentValues;
    }



    public List<Payment> getDebtPayments( String debtId) {

        List<Payment> payments = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query(PaymentsEntry.TABLE_NAME, PaymentsEntry.getAllColumns(),
                PaymentsEntry.COLUMN_DEBT_ID + DebtsDbHelper.WHERE_EQUAL_TO, new String[]{debtId}, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                String id = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_ENTRY_ID));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_AMOUNT));
                long dateEntered = cursor.getLong(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_DATE_ENTERED));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_NOTE));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PERSON_PHONE_NUMBER));

                Payment payment = new Payment.Builder()
                        .dateEntered(dateEntered)
                        .amount(amount)
                        .debtId(debtId)
                        .note(note)
                        .personPhoneNumber(phoneNumber)
                        .id(id).build();

                payments.add(payment);
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        if (payments.isEmpty()) {
            // return empty list
            return new ArrayList<>();
        } else {
            return payments;
        }
    }


    public void deleteAllDebtPayments( String debtId) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(PaymentsEntry.TABLE_NAME, PaymentsEntry.COLUMN_DEBT_ID + DebtsDbHelper.WHERE_EQUAL_TO, new String[]{debtId});
    }


    public void deleteAllPayments() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(PaymentsEntry.TABLE_NAME, null, null);
    }

    public void refreshDebts() {
        // refresh the debts
    }


    public void deleteAllPersonDebts() {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(DebtsEntry.TABLE_NAME, null, null);
        db.delete(PersonsEntry.TABLE_NAME, null, null);
    }


    public void deletePersonDebt( PersonDebt personDebt) {

        String debtId = personDebt.getDebt().getId();

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(DebtsEntry.TABLE_NAME, DebtsEntry.COLUMN_ENTRY_ID + DebtsDbHelper.WHERE_EQUAL_TO, new String[]{debtId});

        // delete person if he has only one debt
        String personPhoneNumber = personDebt.getPerson().getPhoneNumber();
        if (personHasNoDebts(personPhoneNumber)) {
            deletePerson(personPhoneNumber);
        }

        if (debtHasPayments(debtId)) {
            deleteAllDebtPayments(debtId);
        }
    }


    public void batchDelete( List<PersonDebt> personDebts,  int debtType) {
        for (PersonDebt personDebt : personDebts) {
            deletePersonDebt(personDebt);
        }
    }


    public List<Person> getAllPersonWithDebts() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        List<Person> persons = new ArrayList<>();
        Cursor cursor = db.query(PersonsEntry.TABLE_NAME, PersonsEntry.getAllColumns(), null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String personName = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_NAME));
                String personPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_PHONE_NO));
                String personImageUri = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_IMAGE_URI));

                Person person = new Person(personName, personPhoneNumber, personImageUri);

                person.setDebts(getPersonDebts(personPhoneNumber));
                persons.add(person);

            }
        }

        if (cursor != null) {
            cursor.close();
        }

        if (persons.isEmpty()) {
            return new ArrayList<>();
        } else {
            return persons;
        }
    }


    public List<Debt> getPersonDebts(String personPhoneNumber) {


        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        List<Debt> debts = new ArrayList<>();
        Cursor cursor = db.query(DebtsEntry.TABLE_NAME, DebtsEntry.getAllColumns(),
                DebtsEntry.COLUMN_PERSON_PHONE_NUMBER
                        + DebtsDbHelper.WHERE_EQUAL_TO, new String[]{personPhoneNumber}, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_AMOUNT));
                long dateDue = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_DUE));
                long dateEntered = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_ENTERED));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_NOTE));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_STATUS));
                int debtType1 = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_TYPE));
                String entryId = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_ENTRY_ID));

                Debt debt = new Debt.Builder(entryId, personPhoneNumber, amount, dateEntered, debtType1, status)
                        .dueDate(dateDue)
                        .note(note)
                        .build();

                debts.add(debt);

            }
        }
        if (cursor != null) {
            cursor.close();
        }

        if (debts.isEmpty()) {
            return new ArrayList<>();
        } else {
            return debts;
        }
    }


    private void deletePerson( String personPhoneNumber) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(PersonsEntry.TABLE_NAME, PersonsEntry.COLUMN_PHONE_NO + DebtsDbHelper.WHERE_EQUAL_TO,
                new String[]{personPhoneNumber});
    }


    private boolean personHasNoDebts(String personPhoneNumber) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query(DebtsEntry.TABLE_NAME, DebtsEntry.getAllColumns(),
                DebtsEntry.COLUMN_PERSON_PHONE_NUMBER + DebtsDbHelper.WHERE_EQUAL_TO, new String[]{personPhoneNumber}, null, null, null);

        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


    public boolean debtHasPayments(String debtId) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor cursor = db.query(PaymentsEntry.TABLE_NAME, PaymentsEntry.getAllColumns(),
                PaymentsEntry.COLUMN_DEBT_ID + DebtsDbHelper.WHERE_EQUAL_TO, new String[]{debtId}, null, null, null);

        cursor.moveToFirst();

        if (cursor.getCount() >= 1) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


    public void deleteAllPersonDebtsByType(int debtType) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(DebtsEntry.TABLE_NAME, DebtsEntry.COLUMN_TYPE + DebtsDbHelper.WHERE_EQUAL_TO, new String[]{String.valueOf(debtType)});
    }


    public void updatePersonDebt(PersonDebt personDebt) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Debt debt = personDebt.getDebt();
        Person person = personDebt.getPerson();

        if (personAlreadyExist(person.getPhoneNumber())) {
            ContentValues personContentValues = new ContentValues();
            personContentValues.put(PersonsEntry.COLUMN_PHONE_NO, person.getPhoneNumber());
            personContentValues.put(PersonsEntry.COLUMN_NAME, person.getFullname());
            db.update(PersonsEntry.TABLE_NAME, personContentValues, PersonsEntry.COLUMN_PHONE_NO +
                    DebtsDbHelper.WHERE_EQUAL_TO, new String[]{person.getPhoneNumber()});
        } else {
            saveNewPerson(person);
        }

        ContentValues debtContentValues = new ContentValues();
        debtContentValues.put(DebtsEntry.COLUMN_DATE_ENTERED, debt.getCreatedDate());
        debtContentValues.put(DebtsEntry.COLUMN_PERSON_PHONE_NUMBER, person.getPhoneNumber());
        debtContentValues.put(DebtsEntry.COLUMN_DATE_DUE, debt.getDueDate());
        debtContentValues.put(DebtsEntry.COLUMN_NOTE, debt.getNote());
        debtContentValues.put(DebtsEntry.COLUMN_AMOUNT, debt.getAmount());
        debtContentValues.put(DebtsEntry.COLUMN_TYPE, debt.getDebtType());
        debtContentValues.put(DebtsEntry.COLUMN_STATUS, debt.getStatus());

        db.update(DebtsEntry.TABLE_NAME, debtContentValues, DebtsEntry.COLUMN_ENTRY_ID +
                DebtsDbHelper.WHERE_EQUAL_TO, new String[]{debt.getId()});
    }


    private void saveNewPerson( Person person) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues personValues = new ContentValues();
        personValues.put(PersonsEntry.COLUMN_NAME, person.getFullname());
        personValues.put(PersonsEntry.COLUMN_PHONE_NO, person.getPhoneNumber());
        personValues.put(PersonsEntry.COLUMN_IMAGE_URI, person.getImageUri());
        db.insert(PersonsEntry.TABLE_NAME, null, personValues);
    }


    public Person getPerson(String personPhoneNumber) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PersonsEntry.TABLE_NAME + DebtsDbHelper.WHERE +
                PersonsEntry.COLUMN_PHONE_NO + DebtsDbHelper.WHERE_EQUAL_TO, new String[]{personPhoneNumber});
        Person person = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_NAME));
            String phoneNo = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_PHONE_NO));
            String personImageUri = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_IMAGE_URI));
            person = new Person(fullName, phoneNo, personImageUri);
        }

        if (cursor != null) {
            cursor.close();
        }
        return person;
    }


    public boolean personAlreadyExist(String phoneNumber) {

        if (phoneNumber != null) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT " + PersonsEntry.COLUMN_PHONE_NO + " FROM " + PersonsEntry.TABLE_NAME +
                    DebtsDbHelper.WHERE + PersonsEntry.COLUMN_PHONE_NO + DebtsDbHelper.WHERE_EQUAL_TO,
                    new String[]{String.valueOf(phoneNumber)});
            if (cursor.moveToFirst()) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
}
