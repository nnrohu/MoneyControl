package com.example.nnroh.moneycontrol.Adapter;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nnroh.moneycontrol.App.PaymentActivity;
import com.example.nnroh.moneycontrol.Data.Debt;
import com.example.nnroh.moneycontrol.Data.local.DataManager;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.DebtsEntry;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PaymentsEntry;
import com.example.nnroh.moneycontrol.Data.local.DebtsDbHelper;
import com.example.nnroh.moneycontrol.Dialog.PaymentDetailsFragment;
import com.example.nnroh.moneycontrol.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class PersonDebtDetailsAdapter extends
        RecyclerView.Adapter<PersonDebtDetailsAdapter.ViewHolder>{

    private Context mContext;
    private final LayoutInflater mLayoutInflater;
    //  private final List<Debt> mDebtList;
    private Cursor mCursor;
    private int mDebtTypePos;
    private int mDebtNotePos;
    private int mDebtAmountPos;
    private int mDebtDueDatePos;
    private int mDebtPhonePos;
    private int mDebtIdPos;
    private DebtsDbHelper mDbHelper;


    public PersonDebtDetailsAdapter(Context context, Cursor cursor) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCursor = cursor;
    }

    public void changeCursor(Cursor cursor) {
        if (mCursor != null)
            mCursor.close();
        mCursor = cursor;
        populateColumnPosition();
        notifyDataSetChanged();
    }

    private void populateColumnPosition() {
        if (mCursor == null)
            return;

        mDebtTypePos = mCursor.getColumnIndex(DebtsEntry.COLUMN_TYPE);
        mDebtNotePos = mCursor.getColumnIndex(DebtsEntry.COLUMN_NOTE);
        mDebtAmountPos = mCursor.getColumnIndex(DebtsEntry.COLUMN_AMOUNT);
        mDebtDueDatePos = mCursor.getColumnIndex(DebtsEntry.COLUMN_DATE_DUE);
        mDebtPhonePos = mCursor.getColumnIndex(DebtsEntry.COLUMN_PERSON_PHONE_NUMBER);
        mDebtIdPos = mCursor.getColumnIndex(DebtsEntry.COLUMN_ENTRY_ID);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_person_debt, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        mCursor.moveToPosition(position);

        final int debtType = Integer.parseInt(mCursor.getString(mDebtTypePos));
        if (debtType == Debt.DEBT_TYPE_IOWE) {
            holder.mDebtType.setText(R.string.caps_iowe);
        } else {
            holder.mDebtType.setText(R.string.caps_owed);
        }

        holder.mDebtNote.setText(mCursor.getString(mDebtNotePos));
        holder.mDebtAmount.setText(mCursor.getString(mDebtAmountPos));
        long dueDate = Long.parseLong(mCursor.getString(mDebtDueDatePos));
        if (Calendar.getInstance().getTimeInMillis() > dueDate) {
            holder.mDebtDueDate.setTextColor(Color.RED);
        }
        holder.mDebtDueDate.setText(mContext.getString(R.string.due_date_label) + getDate(dueDate));
        if (Double.parseDouble(mCursor.getString(mDebtAmountPos)) == 0){
            holder.mDebtPay.setText(R.string.paid_text);
            holder.mDebtPay.setEnabled(false);
        }

        holder.amountToTrans = Double.parseDouble(mCursor.getString(mDebtAmountPos));
        holder.debtId = mCursor.getString(mDebtIdPos);
        holder.phoneNo = mCursor.getString(mDebtPhonePos);

    }

    private String getDate(long date) {
        SimpleDateFormat df = new SimpleDateFormat("EEEE,dd MMM,yyyy");
        return  df.format(date);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void removeItem(final String debtId) {
        Uri debtUri = DebtsEntry.CONTENT_URI;
        Uri paymentUri = PaymentsEntry.CONTENT_URI;
        String selection = DebtsEntry.COLUMN_ENTRY_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(debtId)};
        mContext.getContentResolver().delete(debtUri,selection, selectionArgs);
        DataManager dm = new DataManager(mContext);
        if(dm.debtHasPayments(debtId)){
            String selectionPayment = PaymentsEntry.COLUMN_DEBT_ID + " = ? ";
            mContext.getContentResolver().delete(paymentUri, selectionPayment, selectionArgs);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mDebtAmount, mDebtNote, mDebtDueDate, mDebtType, mDebtPay;
        private long mDateLong;
        String debtId, phoneNo;
        double amountToTrans;
        private final ImageView mDeleteDebt;


        public ViewHolder(View itemView) {
            super(itemView);

            mDebtAmount = (TextView) itemView.findViewById(R.id.tv_debt_amount);
            mDebtDueDate = (TextView) itemView.findViewById(R.id.tv_debt_due_date);
            mDebtNote = (TextView) itemView.findViewById(R.id.tv_debt_note);
            mDebtType = (TextView) itemView.findViewById(R.id.tv_debt_type);
            mDebtPay = (TextView) itemView.findViewById(R.id.tv_payment);

            mDeleteDebt = itemView.findViewById(R.id.iv_delete_person_debt);
            mDeleteDebt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(debtId);
                    Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                }
            });
            mDebtPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PaymentActivity.class);
                    intent.putExtra(PaymentActivity.DEBT_ID, debtId);
                    intent.putExtra(PaymentActivity.DEBT_AMOUNT, amountToTrans);
                    intent.putExtra(PaymentActivity.PERSON_NUMBER, phoneNo);
                    mContext.startActivity(intent);
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("debtId", debtId);
                    PaymentDetailsFragment dialog = new PaymentDetailsFragment();
                    dialog.setArguments(bundle);
                    dialog.show(((AppCompatActivity)mContext).getSupportFragmentManager(), dialog.getTag());
                }
            });
        }
    }
}