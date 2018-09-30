package com.example.nnroh.moneycontrol;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nnroh.moneycontrol.Data.Payment;
import com.example.nnroh.moneycontrol.Data.PersonDebt;
import com.example.nnroh.moneycontrol.Data.local.DataManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ViewDialog {

    private TextInputLayout mAmountLayout;
    private EditText mAmountEditText;
    private EditText mNoteEditText;
    private Button mDateButton;
    private ImageView mCloseDialog;
    DateHelper mDateHelper;
    Calendar myCalender;
    private long mDateLong;
    Context mContext;
    private ColorGenerator mGenerator = ColorGenerator.MATERIAL;

    public ViewDialog(Context context) {
        mContext = context;
    }

    public void showDialogForPayment(final double amount, final String phoneNo, final String debtId){
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.content_payment);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        mDateHelper = new DateHelper();
        myCalender = Calendar.getInstance();

        mAmountLayout = (TextInputLayout) dialog.findViewById(R.id.til_amount_payment);
        mAmountEditText = (EditText) dialog.findViewById(R.id.et_amount_payment);
        mAmountEditText.setText(String.valueOf(amount));
        mNoteEditText = (EditText) dialog.findViewById(R.id.et_comment_payment);
        mDateButton = (Button) dialog.findViewById(R.id.btn_date_created_payment);
        mDateButton.setText("CREATED ON : " + getCurrentDate());
        mCloseDialog = (ImageView) dialog.findViewById(R.id.iv_close_dialog_payment);
        mCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView proceed = (TextView) dialog.findViewById(R.id.tv_proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amountDouble = Double.parseDouble(mAmountEditText.getText().toString().trim());
                String noteString = mNoteEditText.getText().toString().trim();
                if (validateAmount(amountDouble, amount)) {
                    Payment payment = new Payment.Builder().amount(amountDouble)
                            .dateEntered(mDateLong).note(noteString).id(UUID.randomUUID().toString())
                            .personPhoneNumber(phoneNo).debtId(debtId).build();
                    DataManager dm = new DataManager(mContext);
                    dm.savePayment(payment);
                    Snackbar.make(v, "Payment Successful", Snackbar.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalender.set(Calendar.DATE, dayOfMonth);
                myCalender.set(Calendar.MONTH, month);
                myCalender.set(Calendar.YEAR, year);
                mDateLong = myCalender.getTimeInMillis();
                updateLabelDateLong();
            }
        };


        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(mContext, dateSetListener, myCalender
                        .get(Calendar.YEAR), myCalender.get(Calendar.MONTH),
                        myCalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    public void showDialogForDebtor(final PersonDebt mCurrentPerson){
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.debt_details_by_type);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageView profileImage = (ImageView) dialog.findViewById(R.id.profile_image);
        ImageView closeDialog = (ImageView) dialog.findViewById(R.id.iv_close_dialog);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView name = (TextView) dialog.findViewById(R.id.tv_debt_name);
        TextView note = (TextView) dialog.findViewById(R.id.tv_debt_note);
        TextView dueDate = (TextView) dialog.findViewById(R.id.tv_debt_due_date);
        TextView createDate = (TextView) dialog.findViewById(R.id.tv_debt_created_date);
        TextView debtPayment = (TextView) dialog.findViewById(R.id.tv_debt_payment);

        debtPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForPayment(mCurrentPerson.getDebt().getAmount(),
                        mCurrentPerson.getPerson().getPhoneNumber(), mCurrentPerson.getDebt().getId());
                dialog.dismiss();
            }
        });

        if (mCurrentPerson.getPerson().getImageUri() != null){
            Glide.with(mContext).applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                    .load(mCurrentPerson.getPerson().getImageUri()).into(profileImage);
        }
        else {
            String letter = String.valueOf(mCurrentPerson.getPerson().getFullname().charAt(0));
            TextDrawable drawable = TextDrawable.builder().buildRound(letter.toUpperCase(),mGenerator.getRandomColor());
            profileImage.setImageDrawable(drawable);
        }

        name.setText(mCurrentPerson.getPerson().getFullname());
        note.setText(mCurrentPerson.getDebt().getNote());
        long dueDateLong = mCurrentPerson.getDebt().getDueDate();
        if (Calendar.getInstance().getTimeInMillis() > dueDateLong) {
            dueDate.setTextColor(Color.RED);
        }
        dueDate.setText("Due Date : " + getDate(dueDateLong));

        long createDateLong = mCurrentPerson.getDebt().getCreatedDate();
        createDate.setText("Create Date: " + getDate(createDateLong));

    }

    private boolean validateAmount(double amountDouble, double amount) {
        if (mAmountEditText.getText().toString().isEmpty()) {
            mAmountLayout.setError(mContext.getString(R.string.empty_error_msg));
            return false;
        }else if (amountDouble > amount){
            mAmountLayout.setError(mContext.getString(R.string.greater_amount_alert));
            return false;
        }else {
            mAmountLayout.setErrorEnabled(false);
            return true;
        }
    }
    private void updateLabelDateLong() {
        String dateFormat = "EEEE,dd MMM,yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        mDateButton.setText("Date Due: " + simpleDateFormat.format(myCalender.getTime()));
    }

    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        mDateLong = Calendar.getInstance().getTimeInMillis();

        SimpleDateFormat df = new SimpleDateFormat("EEEE,dd MMM,yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    private String getDate(long date) {
        SimpleDateFormat df = new SimpleDateFormat("EEEE,dd MMM,yyyy");
        String formattedDate = df.format(date);
        return formattedDate;
    }


}

