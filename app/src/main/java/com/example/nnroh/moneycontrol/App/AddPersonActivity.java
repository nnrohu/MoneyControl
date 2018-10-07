package com.example.nnroh.moneycontrol.App;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
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
import com.example.nnroh.moneycontrol.Utils.CountryToPhonePrefix;
import com.example.nnroh.moneycontrol.Data.Debt;
import com.example.nnroh.moneycontrol.Data.local.DataManager;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.DebtsEntry;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PersonsEntry;
import com.example.nnroh.moneycontrol.R;
import com.karan.churi.PermissionManager.PermissionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddPersonActivity extends AppCompatActivity {


    public static final int REQUEST_CONTACT = 1;
    public static final int REQUEST_CAMERA = 2;
    public static final int REQUEST_GALLERY = 3;
    ImageButton mibContact;
    public static final String PERSON_NAME = "com.example.nnroh.moneycontrol.PERSON_NAME";
    public static final String PERSON_NUMBER = "com.example.nnroh.moneycontrol.PERSON_NUMBER";
    public static final String PERSON_PHOTO = "com.example.nnroh.moneycontrol.PERSON_PHOTO";

    private Calendar myCalendar = Calendar.getInstance();
    private ImageView mPhotoView, mCameraView;
    private EditText mFullNameView;
    private EditText mNumberView;
    private EditText mAmountView;
    private EditText mCommentView;
    private Button mDateCreatedView;
    private Button mDateDueView;
    private String mPersonPhotoUri;
    ColorGenerator mGenerator = ColorGenerator.MATERIAL;

    RadioGroup mRadioGroup;

    private TextInputLayout mNameLayout, mAmountLayout, mNumberLayout;

    DataManager dm;
    private int mDebtType;
    private long mDateDueLong;
    private long mDateCreatedLong;
    private String mContactName;
    private PermissionManager mPermissionManager;
    private boolean mPermissions;
    private String mNameIntent;
    private String mNumberIntent;
    private String mImageIntent;
    private String userChoosenTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        dm = new DataManager(this);

        mPermissionManager = new PermissionManager() {
        };
        mPermissions = mPermissionManager.checkAndRequestPermissions(AddPersonActivity.this);


        Intent intent = getIntent();
        if (intent.getExtras() != null) {

            mNameIntent = intent.getStringExtra(PERSON_NAME);
            mNumberIntent = intent.getStringExtra(PERSON_NUMBER);
            mImageIntent = intent.getStringExtra(PERSON_PHOTO);
        }


        mibContact = (ImageButton) findViewById(R.id.ib_contacts);

        if (mPermissions) {
            mibContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CONTACT);
                }
            });
        } else {
            mPermissionManager.checkAndRequestPermissions(AddPersonActivity.this);
        }

        mCameraView = findViewById(R.id.iv_camera_pick);
        mCameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPermissions)
                    showDialogOptionForImage();
                else
                    mPermissionManager.checkAndRequestPermissions(AddPersonActivity.this);
            }
        });

        mNameLayout = (TextInputLayout) findViewById(R.id.til_name);
        mNumberLayout = (TextInputLayout) findViewById(R.id.til_amount);
        mAmountLayout = (TextInputLayout) findViewById(R.id.til_phone_number);

        mPhotoView = (ImageView) findViewById(R.id.iv_set_profile_image);
        if (intent.getExtras() != null) {
            if (mImageIntent != null) {
                Glide.with(this)
                        .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                        .load(mImageIntent).into(mPhotoView);
            } else {
                String letter = String.valueOf(mNameIntent.charAt(0));
                //        Create a new TextDrawable for our image's background
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(letter.toUpperCase(), mGenerator.getRandomColor());
                mPhotoView.setImageDrawable(drawable);
            }
        }

        mFullNameView = (EditText) findViewById(R.id.et_full_name);
        mFullNameView.setText(mNameIntent);

        mNumberView = (EditText) findViewById(R.id.et_phone_number);
        mNumberView.setText(mNumberIntent);

        mAmountView = (EditText) findViewById(R.id.et_amount);
        mCommentView = (EditText) findViewById(R.id.et_comment);
        mDateCreatedView = (Button) findViewById(R.id.btn_date_created);
        mDateDueView = (Button) findViewById(R.id.btn_date_due);

        mRadioGroup = (RadioGroup) findViewById(R.id.rg_debt_type);
        mDebtType = Debt.DEBT_TYPE_OWED;
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_owed_by_me) {
                    mDebtType = Debt.DEBT_TYPE_IOWE;
                } else if (checkedId == R.id.rb_owed_to_me) {
                    mDebtType = Debt.DEBT_TYPE_OWED;
                }
            }
        });

        // setting label of current date
        mDateCreatedView.setText("Created on: " + getCurrentDate());
        mDateDueView.setText("Date Due: " + getCurrentDate());


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


        mDateCreatedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddPersonActivity.this, dateSetListener1,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mDateDueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddPersonActivity.this, dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }


    private void updateLabelDateCreated() {
        String dateFormat = "EEEE,dd MMM,yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        mDateCreatedView.setText("Created on: " + simpleDateFormat.format(myCalendar.getTime()));
    }

    private void updateLabelDateDue() {
        String dateFormat = "EEEE,dd MMM,yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        mDateDueView.setText("Date Due: " + simpleDateFormat.format(myCalendar.getTime()));
    }

    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        mDateCreatedLong = Calendar.getInstance().getTimeInMillis();
        mDateDueLong = Calendar.getInstance().getTimeInMillis();

        SimpleDateFormat df = new SimpleDateFormat("EEEE,dd MMM,yyyy");
        return df.format(c);
    }

    private void showDialogOptionForImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddPersonActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CONTACT:
                if (resultCode == RESULT_OK) {
                    Uri mContactUri = data.getData();
                    retrieveContactName(mContactUri);
                    retrieveContactNumber(mContactUri);
                    retrieveContactPhoto(mContactUri);
                }
                break;

            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    onCaptureImageResult(data);
                }
                break;

            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    onSelectFromGalleryResult(data);
                }
                break;
        }

    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

        Uri imageUri = getImageUri(this, thumbnail);
        Glide.with(this).applyDefaultRequestOptions(RequestOptions.centerCropTransform())
                .load(imageUri).into(mPhotoView);
        mPersonPhotoUri = String.valueOf(imageUri);
        Glide.with(this)
                .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                .load(imageUri).into(mPhotoView);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri tempUri = getImageUri(AddPersonActivity.this, bm);
        mPersonPhotoUri = String.valueOf(tempUri);
        Glide.with(this)
                .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                .load(tempUri).into(mPhotoView);
    }

    private String getCountryISO() {
        String iso = null;
        TelephonyManager telephonyManager =
                (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null){
            if (!telephonyManager.getNetworkCountryIso().toString().equals("")){
                iso = telephonyManager.getNetworkCountryIso().toString();
            }
        }
        return CountryToPhonePrefix.getPhone(iso);
    }


    private void retrieveContactNumber(Uri mContactUri) {

        String ISOPrefix = getCountryISO();
        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(mContactUri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                null, null, null);

        if (cursorPhone != null && cursorPhone.moveToFirst()) {
            String contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactNumber.replace(" ", "");
            contactNumber.replace("-", "");
            contactNumber.replace(")", "");
            contactNumber.replace("(", "");

            if (!String.valueOf(contactNumber.charAt(0)).equals("+")){
                contactNumber = ISOPrefix + contactNumber;
            }
            mNumberView.setText(contactNumber);

            cursorPhone.close();
        }
    }

    private void retrieveContactName(Uri mContactUri) {

        // querying contact data store
        Cursor cursor = getContentResolver().query(mContactUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            mContactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            mFullNameView.setText(mContactName);

            cursor.close();
        }
    }

    public void retrieveContactPhoto(Uri mContactUri) {
        Cursor cursor = getContentResolver().query(mContactUri, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {

            if (cursor.moveToFirst()) {
                mPersonPhotoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (mPersonPhotoUri != null) {
                    Glide.with(this)
                            .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                            .load(mPersonPhotoUri).into(mPhotoView);
                } else {
                    String letter = String.valueOf(mContactName.charAt(0));
                    //        Create a new TextDrawable for our image's background
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(letter.toUpperCase(), mGenerator.getRandomColor());
                    mPhotoView.setImageDrawable(drawable);
                }
            }

            cursor.close();
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

        if (id == R.id.action_save_debt) {
            if (validateName() && validateMoney() && validateNumber()) {
                insertDebt();
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertDebt() {

        Uri debtUri = DebtsEntry.CONTENT_URI;
        Uri personUri = PersonsEntry.CONTENT_URI;
        //Person value
        String photoString = mPersonPhotoUri;
        String nameString = mFullNameView.getText().toString().trim();
        String numberString = mNumberView.getText().toString().trim();

        //Debt value
        String amountString = mAmountView.getText().toString().trim();
        String noteString = mCommentView.getText().toString().trim();

        String entryId = String.valueOf(UUID.randomUUID());

        if (!dm.personAlreadyExist(numberString)) {
            ContentValues personValues = new ContentValues();
            personValues.put(PersonsEntry.COLUMN_NAME, nameString);
            personValues.put(PersonsEntry.COLUMN_PHONE_NO, numberString);
            personValues.put(PersonsEntry.COLUMN_IMAGE_URI, photoString);
            getContentResolver().insert(personUri, personValues);
        }
        ContentValues debtValues = new ContentValues();
        debtValues.put(DebtsEntry.COLUMN_ENTRY_ID, entryId);
        debtValues.put(DebtsEntry.COLUMN_AMOUNT, amountString);
        debtValues.put(DebtsEntry.COLUMN_DATE_DUE, mDateDueLong);
        debtValues.put(DebtsEntry.COLUMN_DATE_ENTERED, mDateCreatedLong);
        debtValues.put(DebtsEntry.COLUMN_NOTE, noteString);
        debtValues.put(DebtsEntry.COLUMN_PERSON_PHONE_NUMBER, numberString);
        debtValues.put(DebtsEntry.COLUMN_STATUS, Debt.DEBT_STATUS_ACTIVE);
        debtValues.put(DebtsEntry.COLUMN_TYPE, mDebtType);
        getContentResolver().insert(debtUri, debtValues);
    }

    private boolean validateName() {
        if (mFullNameView.getText().toString().isEmpty()) {
            mNameLayout.setError("This Field is required");
            return false;
        } else {
            mNameLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateMoney() {
        if (mAmountView.getText().toString().isEmpty()) {
            mAmountLayout.setError("This Field is required");
            return false;
        } else {
            mAmountLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateNumber() {
        if (mNumberView.getText().toString().isEmpty()) {
            mNumberLayout.setError(getString(R.string.error_msg_required_field));
            return false;
        } else {
            mNumberLayout.setErrorEnabled(false);
            return true;
        }
    }
}
