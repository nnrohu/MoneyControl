package com.example.nnroh.moneycontrol.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nnroh.moneycontrol.Data.Payment;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PaymentsEntry;
import com.example.nnroh.moneycontrol.R;

import java.text.SimpleDateFormat;
import java.util.List;


public class PaymentsOfDebtRecyclerAdapter extends RecyclerView.Adapter<PaymentsOfDebtRecyclerAdapter.ViewHolder>{

    private Context mContext;
    private final LayoutInflater mLayoutInflater;
  //  private List<Payment> mPayments;
    private Cursor mCursor;
    private int mPaymentAmountPos;
    private int mPaymentNotePos;
    private int mPaymentCreateDatePos;

    public PaymentsOfDebtRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCursor = cursor;
    }

    public void changeCursour(Cursor cursor){
        if (mCursor != null)
            mCursor.close();
        mCursor = cursor;
        populateColumnPosition();
        notifyDataSetChanged();
    }

    private void populateColumnPosition() {
        mPaymentAmountPos = mCursor.getColumnIndex(PaymentsEntry.COLUMN_AMOUNT);
        mPaymentNotePos = mCursor.getColumnIndex(PaymentsEntry.COLUMN_NOTE);
        mPaymentCreateDatePos = mCursor.getColumnIndex(PaymentsEntry.COLUMN_DATE_ENTERED);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_payment_details_of_debt, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        long paymentCreateDate = Long.parseLong(mCursor.getString(mPaymentCreateDatePos));
        String paymentNote = mCursor.getString(mPaymentNotePos);
        String paymentAmount = mCursor.getString(mPaymentAmountPos);

        holder.mPaymentAmount.setText(paymentAmount);
        holder.mPaymentCreateDate.setText(mContext.getString(R.string.dated_label) + getDate(paymentCreateDate));
        holder.mPaymentNote.setText(paymentNote);


    }

    @Override
    public int getItemCount() {
       return mCursor == null ? 0 : mCursor.getCount();
    }

     public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mPaymentAmount, mPaymentCreateDate, mPaymentNote;

        public ViewHolder(View itemView) {
            super(itemView);

            mPaymentAmount =  itemView.findViewById(R.id.tv_payment_amount);
            mPaymentCreateDate =  itemView.findViewById(R.id.tv_payment_create_date);
            mPaymentNote =  itemView.findViewById(R.id.tv_payment_note);
        }
    }

    private String getDate(long date) {
        SimpleDateFormat df = new SimpleDateFormat("EEEE,dd MMM,yyyy");
        return df.format(date);
    }
}