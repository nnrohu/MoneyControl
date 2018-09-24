package com.example.nnroh.moneycontrol.App;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nnroh.moneycontrol.Contact.ContactActivity;
import com.example.nnroh.moneycontrol.Data.Debt;
import com.example.nnroh.moneycontrol.Data.Person;
import com.example.nnroh.moneycontrol.Data.local.DataManager;
import com.example.nnroh.moneycontrol.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddPersonActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1;
    ImageButton mibContact;
    public static final String PERSON_NAME = "com.example.nnroh.moneycontrol.PERSON_NAME";
    public static final String PERSON_NUMBER = "com.example.nnroh.moneycontrol.PERSON_NUMBER";
    public static final String PERSON_PHOTO = "com.example.nnroh.moneycontrol.PERSON_PHOTO";

    private Calendar myCalendar = Calendar.getInstance();
    private ImageView mPhoto;
    private EditText mFullName;
    private EditText mNumber;
    private EditText mAmount;
    private EditText mComment;
    private Button mDateCreated;
    private Button mDateDue;
    private String personPhoto;
    ColorGenerator mGenerator = ColorGenerator.MATERIAL;
    private TextDrawable mDrawable;

    RadioGroup mRadioGroup;

    private TextInputLayout mNameLayout, mAmountLayout, mNumberLayout;

    DataManager dm;
    private int mDebtType;
    private long mDateDueLong;
    private long mDateCreatedLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        dm = new DataManager(this);

        mibContact = (ImageButton) findViewById(R.id.ib_contacts);

        mibContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPersonActivity.this, ContactActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        mNameLayout = (TextInputLayout) findViewById(R.id.til_name);
        mNumberLayout = (TextInputLayout) findViewById(R.id.til_amount);
        mAmountLayout = (TextInputLayout) findViewById(R.id.til_phone_number);

        mPhoto = (ImageView) findViewById(R.id.iv_debtor);
        mFullName = (EditText) findViewById(R.id.et_full_name);
        mNumber = (EditText) findViewById(R.id.et_phone_number);
        mAmount = (EditText) findViewById(R.id.et_amount);
        mComment = (EditText) findViewById(R.id.et_comment);
        mDateCreated = (Button) findViewById(R.id.btn_date_created);
        mDateDue = (Button) findViewById(R.id.btn_date_due);

        mRadioGroup = (RadioGroup) findViewById(R.id.rg_debt_type);
        mDebtType = Debt.DEBT_TYPE_OWED;
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_owed_by_me) {
                    mDebtType = Debt.DEBT_TYPE_IOWE;
                }else if(checkedId == R.id.rb_owed_to_me){
                    mDebtType = Debt.DEBT_TYPE_OWED;
                }
            }
        });

        // setting label of current date
        mDateCreated.setText("Created on: " + getCurrentDate());
        mDateDue.setText("Date Due: " + getCurrentDate());


        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.DATE, dayOfMonth);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.YEAR, year);
                mDateDueLong = myCalendar.getTimeInMillis();
                updateLabelDateDue();
            }
        };

        final DatePickerDialog.OnDateSetListener dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.DATE, dayOfMonth);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.YEAR, year);
                mDateCreatedLong = myCalendar.getTimeInMillis();
                updateLabelDateCreated();

            }
        };



        mDateCreated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddPersonActivity.this, dateSetListener1,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mDateDue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddPersonActivity.this, dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabelDateCreated() {
        String dateFormat = "EEEE,dd MMM,yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        mDateCreated.setText("Created on: " + simpleDateFormat.format(myCalendar.getTime()));
    }

    private void updateLabelDateDue() {
        String dateFormat = "EEEE,dd MMM,yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        mDateDue.setText("Date Due: " + simpleDateFormat.format(myCalendar.getTime()));
    }

    private String getCurrentDate(){
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        mDateCreatedLong = Calendar.getInstance().getTimeInMillis();
        mDateDueLong = Calendar.getInstance().getTimeInMillis();

        SimpleDateFormat df = new SimpleDateFormat("EEEE,dd MMM,yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                String personName = data.getStringExtra(PERSON_NAME);
                String personNumber = data.getStringExtra(PERSON_NUMBER);
                personPhoto = data.getStringExtra(PERSON_PHOTO);

                mFullName.setText(personName);
                mNumber.setText(personNumber);

                if (personPhoto != null) {
                    Glide.with(this)
                            .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                            .load(personPhoto).into(mPhoto);
                }else {
                    String letter = String.valueOf(personName.charAt(0));
                    //        Create a new TextDrawable for our image's background
                    mDrawable = TextDrawable.builder()
                            .buildRound(letter, mGenerator.getRandomColor());
                    mPhoto.setImageDrawable(mDrawable);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save_debt){
            if (validateName() && validateMoney() && validateNumber()) {
                insertDebt();
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertDebt() {
        //Person value
        String photoString = personPhoto;
        String nameString = mFullName.getText().toString().trim();
        String numberString = mNumber.getText().toString().trim();

        //Debt value
        String amountString = mAmount.getText().toString().trim();
        String noteString = mComment.getText().toString().trim();

        String entryId = String.valueOf(UUID.randomUUID());

        Person person = new Person(nameString, numberString, photoString);
        Debt debt = new Debt.Builder(entryId, numberString, Double.parseDouble(amountString),
                mDateCreatedLong, mDebtType, Debt.DEBT_STATUS_ACTIVE)
                .dueDate(mDateDueLong).note(noteString).build();
        dm.savePersonDebt(debt, person);
    }

    private boolean validateName() {
        if (mFullName.getText().toString().isEmpty()) {
            mNameLayout.setError("This Field is required");
            return false;
        } else {
            mNameLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateMoney() {
        if (mAmount.getText().toString().isEmpty()) {
            mAmountLayout.setError("This Field is required");
            return false;
        } else {
            mAmountLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateNumber() {
        if (mNumber.getText().toString().isEmpty()) {
            mNumberLayout.setError(getString(R.string.error_msg_required_field));
            return false;
        } else {
            mNumberLayout.setErrorEnabled(false);
            return true;
        }
    }
}
