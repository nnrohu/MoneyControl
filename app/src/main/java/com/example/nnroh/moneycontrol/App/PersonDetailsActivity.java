package com.example.nnroh.moneycontrol.App;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nnroh.moneycontrol.Adapter.PersonDebtDetailsAdapter;
import com.example.nnroh.moneycontrol.Data.Debt;
import com.example.nnroh.moneycontrol.Data.Person;
import com.example.nnroh.moneycontrol.Data.local.DataManager;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.DebtsEntry;
import com.example.nnroh.moneycontrol.Data.local.DebtsDbHelper;
import com.example.nnroh.moneycontrol.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.nnroh.moneycontrol.App.AddPersonActivity.PERSON_NAME;
import static com.example.nnroh.moneycontrol.App.AddPersonActivity.PERSON_NUMBER;
import static com.example.nnroh.moneycontrol.App.AddPersonActivity.PERSON_PHOTO;

public class PersonDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_GALLERY = 2;
    private static final int LOADER_AMOUNT_DETAILS_ME = 4;
    private static final int LOADER_AMOUNT_DETAILS_TO = 5;
    private ImageView mPersonImage;
    private DataManager mDataManager;
    private Person mPerson;
    ColorGenerator mGenerator = ColorGenerator.MATERIAL;
    private PersonDebtDetailsAdapter mPersonDebtDetailAdapter;
    private static final int LOADER_DEBT_DETAILS = 3;
    private DebtsDbHelper mDbHelper;
    private String mNumber;
    private TextView mTotalAmountView;
    private String mPersonName;
    private String mPersonPhoto;
    private ProgressDialog mProgressDialog;
    private boolean mLoadQueryAmountTo, mLoadQueryAmountMe;
    private String userChoosenTask;
    private double mTotalMe;
    private double mTotalTo;


    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_DEBT_DETAILS, null, this);
        getLoaderManager().restartLoader(LOADER_AMOUNT_DETAILS_ME, null, this);
        getLoaderManager().restartLoader(LOADER_AMOUNT_DETAILS_TO, null, this);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDbHelper = new DebtsDbHelper(this);
        mDataManager = new DataManager(this);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {

            mNumber = intent.getStringExtra(MainActivity.PERSON_NUMBER);
            mPersonName = intent.getStringExtra(MainActivity.PERSON_NAME);
            mPersonPhoto = intent.getStringExtra(PERSON_PHOTO);
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(true);
        mPersonImage =  findViewById(R.id.iv_person);
        mTotalAmountView =  findViewById(R.id.tv_total_amount);

        if (intent.getExtras() != null) {
            if (mPersonPhoto != null) {
                Glide.with(this).applyDefaultRequestOptions(RequestOptions.centerCropTransform())
                        .load(mPersonPhoto).into(mPersonImage);
            } else {
                String letter = String.valueOf(mPersonName.charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(letter.toUpperCase(), mGenerator.getRandomColor());
                mPersonImage.setImageDrawable(drawable);
            }
        }
        CollapsingToolbarLayout layout =  findViewById(R.id.toolbar_layout);
        layout.setTitle(mPersonName);

        RecyclerView personDebtRecycler =  findViewById(R.id.item_person_debt_list);
        mPersonDebtDetailAdapter = new PersonDebtDetailsAdapter(this, null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        personDebtRecycler.setLayoutManager(layoutManager);
        personDebtRecycler.setAdapter(mPersonDebtDetailAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_call) {
            callDebtor();
        } else if (id == R.id.action_sms) {
            smsDebtor();
        } else if (id == R.id.action_add_debt) {
            addDebt();
        }else if (id == R.id.action_update_image){
            showDialogOptionForImage();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                onCaptureImageResult(data);
            }
        } else if (requestCode == REQUEST_GALLERY) {
            if (resultCode == RESULT_OK) {
                onSelectFromGalleryResult(data);
            }
        }
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

        Uri imageUri = getImageUri(this, thumbnail);
        Glide.with(this).applyDefaultRequestOptions(RequestOptions.centerCropTransform())
                .load(imageUri).into(mPersonImage);

        //Update person table
        Uri uri = DebtsContract.PersonsEntry.CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(DebtsContract.PersonsEntry.COLUMN_IMAGE_URI, String.valueOf(imageUri));

        String where = DebtsContract.PersonsEntry.COLUMN_PHONE_NO + " =? ";

        String[] selectionArgs = {mNumber};

        getContentResolver().update(uri, values, where, selectionArgs );

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri imageUri = getImageUri(this, bm);
        Glide.with(this).applyDefaultRequestOptions(RequestOptions.centerCropTransform())
                .load(imageUri).into(mPersonImage);

        //Update person table
        Uri uri = DebtsContract.PersonsEntry.CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(DebtsContract.PersonsEntry.COLUMN_IMAGE_URI, String.valueOf(imageUri));

        String where = DebtsContract.PersonsEntry.COLUMN_PHONE_NO + " =? ";

        String[] selectionArgs = {mNumber};

        getContentResolver().update(uri, values, where, selectionArgs );
    }


    private void addDebt() {
        Intent i = new Intent(PersonDetailsActivity.this, AddPersonActivity.class);
        i.putExtra(PERSON_NAME, mPersonName);
        i.putExtra(PERSON_NUMBER, mNumber);
        i.putExtra(PERSON_PHOTO, mPersonPhoto);
        startActivity(i);
    }

    private void callDebtor() {
        String dial = "tel:" + mNumber;
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
    }

    private void smsDebtor() {
        String sms = "smsto:" + mNumber;
        startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(sms)));
    }

    private void showDialogOptionForImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(PersonDetailsActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),REQUEST_GALLERY);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mProgressDialog.show();
        CursorLoader loader = null;
        if (id == LOADER_DEBT_DETAILS) {
            loader = createLoaderDebtDetails();
        }else if (id == LOADER_AMOUNT_DETAILS_TO){
            loader = createLoaderAmountDetails(Debt.DEBT_TYPE_OWED);
        }else if (id == LOADER_AMOUNT_DETAILS_ME){
            loader = createLoaderAmountDetails(Debt.DEBT_TYPE_IOWE);
        }
        return loader;
    }

    private CursorLoader createLoaderAmountDetails(int debtType) {
        mLoadQueryAmountTo =false;
        mLoadQueryAmountMe = false;
        Uri uri = DebtsEntry.CONTENT_URI;
        String[] projection = {DebtsEntry.COLUMN_AMOUNT};
        String selection = DebtsEntry.COLUMN_PERSON_PHONE_NUMBER + " = ? " +
                " AND " + DebtsEntry.COLUMN_TYPE + " = ? ";
        String[] selectionArgs = {mNumber, String.valueOf(debtType)};

        return new CursorLoader(this, uri, projection,
                selection, selectionArgs, null);
    }


    private CursorLoader createLoaderDebtDetails() {
        Uri uri = DebtsEntry.CONTENT_URI;
        String[] debtDetails = {
                DebtsEntry.COLUMN_TYPE,
                DebtsEntry.COLUMN_NOTE,
                DebtsEntry.COLUMN_DATE_DUE,
                DebtsEntry.COLUMN_AMOUNT,
                DebtsEntry.COLUMN_ENTRY_ID,
                DebtsEntry.COLUMN_PERSON_PHONE_NUMBER
        };

        String selection = DebtsEntry.COLUMN_PERSON_PHONE_NUMBER + " = ? ";
        String[] selectionArgs = {mNumber};

        return new CursorLoader(this, uri, debtDetails, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProgressDialog.dismiss();
        if (loader.getId() == LOADER_DEBT_DETAILS) {
            mPersonDebtDetailAdapter.changeCursor(data);
        }
        else if (loader.getId() == LOADER_AMOUNT_DETAILS_ME){
            mTotalMe = loadTotalAmount(data);
            mLoadQueryAmountMe = true;
        }else if (loader.getId() == LOADER_AMOUNT_DETAILS_TO){
            mTotalTo = loadTotalAmount(data);
            mLoadQueryAmountTo = true;
        }

//        double mTotalMe = loadTotalAmount(Debt.DEBT_TYPE_IOWE);
//        double mTotalTo = loadTotalAmount(Debt.DEBT_TYPE_OWED);
        if (mLoadQueryAmountMe && mLoadQueryAmountTo) {
            double mEffectiveAmount = mTotalMe - mTotalTo;
            mTotalAmountView.setText(String.valueOf(mEffectiveAmount));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_DEBT_DETAILS) {
            mPersonDebtDetailAdapter.changeCursor(null);
        }
        else if (loader.getId() == LOADER_AMOUNT_DETAILS_ME){
            loadTotalAmount(null);
        }else if (loader.getId() == LOADER_AMOUNT_DETAILS_TO){
            loadTotalAmount(null);
        }
    }


    private double loadTotalAmount(Cursor cursor) {

        double totalAmt = 0;

        if (cursor != null && cursor.getCount() > 0) {
            int amountPos = cursor.getColumnIndex(DebtsEntry.COLUMN_AMOUNT);
            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                totalAmt += Double.parseDouble(cursor.getString(amountPos));
            }
        }
        return totalAmt;
    }
}
