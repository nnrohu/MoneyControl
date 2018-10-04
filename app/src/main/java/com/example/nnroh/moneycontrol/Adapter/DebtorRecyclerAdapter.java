package com.example.nnroh.moneycontrol.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nnroh.moneycontrol.App.PaymentActivity;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.DebtsEntry;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PersonsEntry;
import com.example.nnroh.moneycontrol.Dialog.PaymentDetailsFragment;
import com.example.nnroh.moneycontrol.R;
import com.example.nnroh.moneycontrol.Data.PersonDebt;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class DebtorRecyclerAdapter extends RecyclerView.Adapter<DebtorRecyclerAdapter.ViewHolder>{

    private Context mContext;
    private final LayoutInflater mLayoutInflater;
    private Cursor mCursor;
    ColorGenerator mGenerator = ColorGenerator.MATERIAL;
    private int mDebtAmountPos, mDebtIdPos, mDebtDateCreatePos, mDebtDateDuePos, mDebtNotePos;
    private int mPersonNamePos, mPersonPhonePos, mPersonImagePos;

    public DebtorRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCursor = cursor;
    }

    private void populateColumnPosition() {
        if (mCursor == null)
            return;
        //get column position from cursor
        mDebtAmountPos = mCursor.getColumnIndex(DebtsEntry.COLUMN_AMOUNT);
        mDebtDateCreatePos = mCursor.getColumnIndex(DebtsEntry.COLUMN_DATE_ENTERED);
        mDebtDateDuePos = mCursor.getColumnIndex(DebtsEntry.COLUMN_DATE_DUE);
        mDebtNotePos = mCursor.getColumnIndex(DebtsEntry.COLUMN_NOTE);
        mDebtIdPos = mCursor.getColumnIndex(DebtsEntry.COLUMN_ENTRY_ID);

        mPersonNamePos = mCursor.getColumnIndex(PersonsEntry.COLUMN_NAME);
        mPersonPhonePos = mCursor.getColumnIndex(PersonsEntry.COLUMN_PHONE_NO);
        mPersonImagePos = mCursor.getColumnIndex(PersonsEntry.COLUMN_IMAGE_URI);

    }

    public void changeCursour(Cursor cursor){
//        if (mCursor != null)
//            mCursor.close();
        mCursor = cursor;
        populateColumnPosition();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_debtor, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String personName = mCursor.getString(mPersonNamePos);
        String personPhone = mCursor.getString(mPersonPhonePos);
        String personImage = mCursor.getString(mPersonImagePos);

        String debtAmount = mCursor.getString(mDebtAmountPos);
        long debtCreateDate = mCursor.getLong(mDebtDateCreatePos);
        long debtDueDate = mCursor.getLong(mDebtDateDuePos);
        String debtNote = mCursor.getString(mDebtNotePos);
        String debtId = mCursor.getString(mDebtIdPos);

        if (personImage != null){
            Glide.with(mContext).applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                    .load(personImage).into(holder.mDebtorImage);
        }
        else {
            String letter = String.valueOf(personName.charAt(0));
            TextDrawable drawable = TextDrawable.builder().buildRound(letter,mGenerator.getRandomColor());
            holder.mDebtorImage.setImageDrawable(drawable);
        }
        holder.mDebtorName.setText(personName);
        holder.mDebtorAmount.setText(debtAmount);
        holder.mDebtorNote.setText(debtNote);
        if (Calendar.getInstance().getTimeInMillis() > debtDueDate)
            holder.mDebtorDueDate.setTextColor(Color.RED);

        holder.mDebtorDueDate.setText(mContext.getString(R.string.due_date_label) + getDate(debtDueDate));

        holder.mDebtorCreateDate.setText(mContext.getString(R.string.created_date_label) + getDate(debtCreateDate));
        if (Double.parseDouble(debtAmount) == 0){
            holder.mDebtorPayment.setText(mContext.getString(R.string.paid_text));
            holder.mDebtorPayment.setEnabled(false);
        }
        holder.debtId = debtId;
        holder.amountToTrans = Double.parseDouble(debtAmount);
        holder.phoneNo = personPhone;
    }

    private String getDate(long date) {
        SimpleDateFormat df = new SimpleDateFormat("EEEE,dd MMM,yyyy");
        String formattedDate = df.format(date);
        return formattedDate;
    }



    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mDebtorImage;
        public final TextView mDebtorName, mDebtorAmount, mDebtorNote, mDebtorDueDate;
        private final TextView mDebtorPayment;
        private final TextView mDebtorCreateDate;
        String debtId, phoneNo;
        double amountToTrans;

        public ViewHolder(View itemView) {
            super(itemView);

            mDebtorImage =  itemView.findViewById(R.id.iv_person_image);
            mDebtorName =  itemView.findViewById(R.id.tv_person_name);
            mDebtorAmount =  itemView.findViewById(R.id.tv_debtor_amount);
            mDebtorNote =  itemView.findViewById(R.id.tv_debtor_note);
            mDebtorDueDate =  itemView.findViewById(R.id.tv_debtor_due_date);
            mDebtorCreateDate =  itemView.findViewById(R.id.tv_debtor_created_date);
            mDebtorPayment =  itemView.findViewById(R.id.tv_debtor_payment);


            mDebtorPayment.setOnClickListener(new View.OnClickListener() {
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