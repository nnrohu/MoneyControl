package com.example.nnroh.moneycontrol.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
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
import com.example.nnroh.moneycontrol.R;
import com.example.nnroh.moneycontrol.Data.PersonDebt;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class DebtorRecyclerAdapter extends RecyclerView.Adapter<DebtorRecyclerAdapter.ViewHolder>{

    private Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final List<PersonDebt> mDebtList;
    ColorGenerator mGenerator = ColorGenerator.MATERIAL;

    public DebtorRecyclerAdapter(Context context, List<PersonDebt> debts) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mDebtList = debts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_debtor, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonDebt debt = mDebtList.get(position);


        if (debt.getPerson().getImageUri() != null){
            Glide.with(mContext).applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                    .load(debt.getPerson().getImageUri()).into(holder.mDebtorImage);
        }
        else {
            String letter = String.valueOf(debt.getPerson().getFullname().charAt(0));
            TextDrawable drawable = TextDrawable.builder().buildRound(letter,mGenerator.getRandomColor());
            holder.mDebtorImage.setImageDrawable(drawable);
        }
        holder.mDebtorName.setText(debt.getPerson().getFullname());
        String debtAmount = String.valueOf(debt.getDebt().getAmount());
        holder.mDebtorAmount.setText(debtAmount);
        holder.mDebtorNote.setText(debt.getDebt().getNote());
        long dueDate = debt.getDebt().getDueDate();
        if (Calendar.getInstance().getTimeInMillis() > dueDate) {
            holder.mDebtorDueDate.setTextColor(Color.RED);
        }
            holder.mDebtorDueDate.setText("Due Date : " + getDueDate(dueDate));

    }

    private String getDueDate(long dueDate) {
        SimpleDateFormat df = new SimpleDateFormat("EEEE,dd MMM,yyyy");
        String formattedDate = df.format(dueDate);
        return formattedDate;
    }


    @Override
    public int getItemCount() {
        return mDebtList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final ImageView mDebtorImage;
        public final TextView mDebtorName, mDebtorAmount, mDebtorNote, mDebtorDueDate;

        public ViewHolder(View itemView) {
            super(itemView);

            mDebtorImage = (ImageView) itemView.findViewById(R.id.iv_person_image);
            mDebtorName = (TextView) itemView.findViewById(R.id.tv_person_name);
            mDebtorAmount = (TextView) itemView.findViewById(R.id.tv_debtor_amount);
            mDebtorNote = (TextView) itemView.findViewById(R.id.tv_debtor_note);
            mDebtorDueDate = (TextView) itemView.findViewById(R.id.tv_debtor_due_date);
        }
    }
}