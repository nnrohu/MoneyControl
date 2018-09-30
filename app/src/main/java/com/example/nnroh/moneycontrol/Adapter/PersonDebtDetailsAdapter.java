package com.example.nnroh.moneycontrol.Adapter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.nnroh.moneycontrol.Data.Debt;
import com.example.nnroh.moneycontrol.Data.Payment;
import com.example.nnroh.moneycontrol.Data.local.DataManager;
import com.example.nnroh.moneycontrol.R;
import com.example.nnroh.moneycontrol.ViewDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class PersonDebtDetailsAdapter extends RecyclerView.Adapter<PersonDebtDetailsAdapter.ViewHolder>{

    private Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final List<Debt> mDebtList;


    public PersonDebtDetailsAdapter(Context context,  List<Debt> debtList) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mDebtList = debtList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_person_debt, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int debtType = mDebtList.get(position).getDebtType();
        if (debtType == Debt.DEBT_TYPE_IOWE){
            holder.mDebtType.setText("OWE BY ME");
        }else {
            holder.mDebtType.setText("OWE TO ME");
        }

        holder.mDebtNote.setText(mDebtList.get(position).getNote());
        holder.mDebtAmount.setText(String.valueOf(mDebtList.get(position).getAmount()));
        long dueDate = mDebtList.get(position).getDueDate();
        if (Calendar.getInstance().getTimeInMillis() > dueDate) {
            holder.mDebtDueDate.setTextColor(Color.RED);
        }
        holder.mDebtDueDate.setText("Due Date : " + getDate(dueDate));

        holder.amountToTrans = mDebtList.get(position).getAmount();
        holder.debtId = mDebtList.get(position).getId();
        holder.phoneNo = mDebtList.get(position).getPersonPhoneNumber();

    }

    private String getDate(long date) {
        SimpleDateFormat df = new SimpleDateFormat("EEEE,dd MMM,yyyy");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    @Override
    public int getItemCount() {
        return mDebtList.size();
    }

     public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView mDebtAmount, mDebtNote, mDebtDueDate, mDebtType, mDebtPay;
         private long mDateLong;
         String  debtId, phoneNo;
         double amountToTrans;
         private Button mDate;
         private Calendar myCalendar;
         private TextView mAmount;
         private TextInputLayout mAmountLayout;

         public ViewHolder(View itemView) {
            super(itemView);

            mDebtAmount = (TextView) itemView.findViewById(R.id.tv_debt_amount);
            mDebtDueDate = (TextView) itemView.findViewById(R.id.tv_debt_due_date);
            mDebtNote = (TextView) itemView.findViewById(R.id.tv_debt_note);
            mDebtType = (TextView) itemView.findViewById(R.id.tv_debt_type);
            mDebtPay = (TextView) itemView.findViewById(R.id.tv_payment);
            mDebtPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewDialog dialog = new ViewDialog(mContext);
                    dialog.showDialogForPayment(amountToTrans, phoneNo, debtId);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
     }
}