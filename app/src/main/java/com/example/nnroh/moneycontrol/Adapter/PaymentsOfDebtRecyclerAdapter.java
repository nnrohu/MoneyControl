package com.example.nnroh.moneycontrol.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nnroh.moneycontrol.Data.Payment;
import com.example.nnroh.moneycontrol.R;

import java.text.SimpleDateFormat;
import java.util.List;


public class PaymentsOfDebtRecyclerAdapter extends RecyclerView.Adapter<PaymentsOfDebtRecyclerAdapter.ViewHolder>{

    private Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<Payment> mPayments;

    public PaymentsOfDebtRecyclerAdapter(Context context, List<Payment> payments) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mPayments = payments;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_payment_details_of_debt, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        long paymentCreateDate = mPayments.get(position).getDateEntered();
        String paymentNote = mPayments.get(position).getNote();
        double paymentAmount =  mPayments.get(position).getAmount();

        holder.mPaymentAmount.setText(String.valueOf(paymentAmount));
        holder.mPaymentCreateDate.setText(mContext.getString(R.string.dated_label) + getDate(paymentCreateDate));
        holder.mPaymentNote.setText(paymentNote);


    }

    @Override
    public int getItemCount() {
       return mPayments.size();
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