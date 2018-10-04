package com.example.nnroh.moneycontrol.App;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.nnroh.moneycontrol.Data.Payment;
import com.example.nnroh.moneycontrol.Data.local.DataManager;
import com.example.nnroh.moneycontrol.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class PaymentActivity extends AppCompatActivity {

    public static final String DEBT_ID = "com.example.nnroh.moneycontrol.DEBT_ID";
    public static final String DEBT_AMOUNT= "com.example.nnroh.moneycontrol.DEBT_AMOUNT";
    public static final String PERSON_NUMBER= "com.example.nnroh.moneycontrol.DEBT_NUMBER";
    private TextInputLayout mAmountLayout;
    private EditText mAmountEditText;
    private EditText mNoteEditText;
    private Button mDateButton;
    Calendar myCalender;
    private long mDateLong;
    private ColorGenerator mGenerator = ColorGenerator.MATERIAL;
    private DataManager dm;
    private TextView mProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        myCalender = Calendar.getInstance();

        dm = new DataManager(this);

        Intent intent = getIntent();
        final String debtId = intent.getStringExtra(DEBT_ID);
        final double debtAmount = intent.getDoubleExtra(DEBT_AMOUNT, 0);
        final String personNumber = intent.getStringExtra(PERSON_NUMBER);

        mAmountLayout =  findViewById(R.id.til_amount_payment);
        mAmountEditText =  findViewById(R.id.et_amount_payment);
        mAmountEditText.setText(String.valueOf(debtAmount));
        mNoteEditText =  findViewById(R.id.et_comment_payment);
        mDateButton =  findViewById(R.id.btn_date_created_payment);
        mDateButton.setText("CREATED ON : " + getCurrentDate());

        mProceed =  findViewById(R.id.tv_proceed);
        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amountDouble = Double.parseDouble(mAmountEditText.getText().toString().trim());
                String noteString = mNoteEditText.getText().toString().trim();
                if (validateAmount(amountDouble,debtAmount)) {
                    Payment payment = new Payment.Builder().amount(amountDouble)
                            .dateEntered(mDateLong).note(noteString).id(UUID.randomUUID().toString())
                            .personPhoneNumber(personNumber).debtId(debtId).build();
                    dm.savePayment(payment);
                    Snackbar.make(v, "Payment Successful", Snackbar.LENGTH_SHORT).show();
                }
                finish();
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
                new DatePickerDialog(PaymentActivity.this, dateSetListener, myCalender
                        .get(Calendar.YEAR), myCalender.get(Calendar.MONTH),
                        myCalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private boolean validateAmount(double amountDouble, double amount) {
        if (mAmountEditText.getText().toString().isEmpty()) {
            mAmountLayout.setError(getString(R.string.empty_error_msg));
            return false;
        }else if (amountDouble > amount){
            mAmountLayout.setError(getString(R.string.greater_amount_alert));
            return false;
        }else {
            mAmountLayout.setErrorEnabled(false);
            return true;
        }
    }
    private void updateLabelDateLong() {
        String dateFormat = "EEEE,dd MMM,yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        mDateButton.setText(getString(R.string.due_date_label) + simpleDateFormat.format(myCalender.getTime()));
    }

    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        mDateLong = Calendar.getInstance().getTimeInMillis();

        SimpleDateFormat df = new SimpleDateFormat("EEEE,dd MMM,yyyy");
        return df.format(c);
    }

    private String getDate(long date) {
        SimpleDateFormat df = new SimpleDateFormat("EEEE,dd MMM,yyyy");
        return df.format(date);
    }


}
