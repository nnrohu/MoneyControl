package com.example.nnroh.moneycontrol.App;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nnroh.moneycontrol.Adapter.DebtorRecyclerAdapter;
import com.example.nnroh.moneycontrol.Adapter.PersonRecyclerAdapter;
import com.example.nnroh.moneycontrol.BuildConfig;
import com.example.nnroh.moneycontrol.Data.Debt;
import com.example.nnroh.moneycontrol.Data.PersonDebt;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.DebtsEntry;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PersonsEntry;
import com.example.nnroh.moneycontrol.Notification.DebtReminder;
import com.example.nnroh.moneycontrol.Notification.DebtReminderIntentService;
import com.example.nnroh.moneycontrol.Notification.ReminderTasks;
import com.example.nnroh.moneycontrol.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String PERSON_NUMBER = "com.example.nnroh.moneycontrol.PERSON_NUMBER";
    public static final String PERSON_NAME = "com.example.nnroh.moneycontrol.PERSON_NAME";
    public static final int LOADER_PERSON = 0;
    public static final int LOADER_DEBT_TO_ME = 1;
    public static final int LOADER_DEBT_BY_ME = 2;
    private static final int RC_SIGN_IN = 2;
    private static final String TAG = "MainActivity.class";

    ColorGenerator mGenerator = ColorGenerator.MATERIAL;


    private PersonRecyclerAdapter mPersonRecyclerAdapter;
    private DebtorRecyclerAdapter mDebtorRecyclerAdapterByMe;
    private PermissionManager permission;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManagerForPerson;
    private LinearLayoutManager mLayoutManagerForDebt;
    private DebtorRecyclerAdapter mDebtorRecyclerAdapterToMe;
    private TextView mTotalAmountToMe;
    private TextView mTotalAmountByMe;
    private ProgressDialog mProgressDialog;
    private boolean mQueryLoadForPersonFinished;

    private FirebaseAuth mAuth;
    private ImageView mProfileImage;

    public static final int REQUEST_CAMERA = 7;
    public static final int REQUEST_GALLERY = 8;

    private TextView mUserName;
    private TextView mUserPhone;
    private SharedPreferences mPref;

    GoogleApiClient mGoogleApiClient;
    private SignInButton mSignInButton;

    @Override
    protected void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(LOADER_DEBT_BY_ME, null, this);
        getLoaderManager().restartLoader(LOADER_DEBT_TO_ME, null, this);

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mSignInButton.setVisibility(View.GONE);
            updateUI(currentUser);
        }
    }

    private void updateUI( FirebaseUser currentUser) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Uri imageUri = account.getPhotoUrl();
        if (imageUri == null) {
            DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());

            mUserDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imageVal = (String) dataSnapshot.child("image").getValue();
                    if (imageVal != null) {
                        Glide.with(getApplicationContext())
                                .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                                .load(imageVal).into(mProfileImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        String name = account.getDisplayName();
        String email = account.getEmail();
        if (imageUri != null) {
            Glide.with(this).applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                    .load(imageUri).into(mProfileImage);
        }
        mUserName.setText(name);
        mUserPhone.setText(email);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        enableStrictMode();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(true);

        permission = new PermissionManager() {
        };
        permission.checkAndRequestPermissions(this);

        FloatingActionButton fab = findViewById(R.id.fab_add_person);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPersonActivity.class);
                startActivity(intent);
            }
        });

        //show notification dily
        DebtReminder.showNotificationReminder(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        mTotalAmountToMe = (TextView) navigationView.getMenu().findItem(R.id.nav_owed_to_me).getActionView();
        mTotalAmountByMe = (TextView) navigationView.getMenu().findItem(R.id.nav_owed_by_me).getActionView();
        View headerView = navigationView.getHeaderView(0);
        mProfileImage = headerView.findViewById(R.id.iv_user_image);
        mUserName = headerView.findViewById(R.id.tv_user_name);
        mUserPhone = headerView.findViewById(R.id.tv_user_email);
        ImageView imageEdit = headerView.findViewById(R.id.iv_profile_edit);
        mSignInButton = headerView.findViewById(R.id.google_sign_in);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNetworkConnection()) {
                    signIn();
                }
                else {
                    Snackbar.make(v, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show();
                }
            }
        });

        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogOptionForImage();
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        initializeDisplayContent();
        getLoaderManager().initLoader(LOADER_PERSON, null, this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            mSignInButton.setVisibility(View.GONE);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll().penaltyLog().build();
            StrictMode.setThreadPolicy(policy);
        }
    }


    private void initializeDisplayContent() {
        mRecyclerView = findViewById(R.id.item_person_list);
        //person adapter
        mGridLayoutManagerForPerson = new GridLayoutManager(this,
                getResources().getInteger(R.integer.person_grid_span));


        mPersonRecyclerAdapter = new PersonRecyclerAdapter(this, null);

        //debtor adapter
        mLayoutManagerForDebt = new LinearLayoutManager(this);
        // debtor type by me
        mDebtorRecyclerAdapterByMe = new DebtorRecyclerAdapter(this, null);
        //debtor type to me
        mDebtorRecyclerAdapterToMe = new DebtorRecyclerAdapter(this, null);

        displayPerson();

    }


    private void displayPerson() {
        mRecyclerView.setLayoutManager(mGridLayoutManagerForPerson);
        mRecyclerView.setAdapter(mPersonRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_person);

    }

    private void displayDebtToMe() {
        mRecyclerView.setLayoutManager(mLayoutManagerForDebt);
        mRecyclerView.setAdapter(mDebtorRecyclerAdapterToMe);
        selectNavigationMenuItem(R.id.nav_owed_to_me);
    }

    private void displayDebtByMe() {
        mRecyclerView.setLayoutManager(mLayoutManagerForDebt);
        mRecyclerView.setAdapter(mDebtorRecyclerAdapterByMe);
        selectNavigationMenuItem(R.id.nav_owed_by_me);
    }


    private void selectNavigationMenuItem(int id) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(id).setChecked(true);
    }

    private void showDialogOptionForImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_from_library),
                getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_photo_label));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getString(R.string.take_photo))) {
                    cameraIntent();

                } else if (items[item].equals(getString(R.string.choose_from_library))) {
                    galleryIntent();

                } else if (items[item].equals(getString(R.string.cancel))) {
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
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                onCaptureImageResult(data);
            }
        } else if (requestCode == REQUEST_GALLERY) {
            if (resultCode == RESULT_OK) {
                onSelectFromGalleryResult(data);
            }
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

        Uri imageUri = getImageUri(this, thumbnail);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference("user").child(user.getUid());
        mUserDb.child("image").setValue(String.valueOf(imageUri));

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
        Uri imageUri = getImageUri(this, bm);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference("user").child(user.getUid());
        mUserDb.child("image").setValue(String.valueOf(imageUri));

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_person) {
            displayPerson();
        } else if (id == R.id.nav_owed_to_me) {
            if (mQueryLoadForPersonFinished)
                displayDebtToMe();
        } else if (id == R.id.nav_owed_by_me) {
            if (mQueryLoadForPersonFinished)
                displayDebtByMe();
        } else if (id == R.id.nav_share) {
            shareApp();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                R.string.share_message);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        mProgressDialog.show();
        CursorLoader loader = null;
        if (id == LOADER_PERSON) {
            loader = createLoaderPerson();
        } else if (id == LOADER_DEBT_TO_ME) {
            loader = createLoaderDebtToMe();
        } else if (id == LOADER_DEBT_BY_ME) {
            loader = createLoaderDebtByMe();
        }
        return loader;
    }

    private CursorLoader createLoaderDebtByMe() {
        Uri uri = DebtsEntry.CONTENT_URI_JOIN;

        String selection = DebtsEntry.COLUMN_TYPE + " = ? ";

        String[] selectionArgs = {String.valueOf(Debt.DEBT_TYPE_IOWE)};

        String[] projection = {
                PersonsEntry.getQName(PersonsEntry.COLUMN_IMAGE_URI),
                PersonsEntry.getQName(PersonsEntry.COLUMN_NAME),
                PersonsEntry.getQName(PersonsEntry.COLUMN_PHONE_NO),
                DebtsEntry.getQName(DebtsEntry.COLUMN_DATE_DUE),
                DebtsEntry.getQName(DebtsEntry.COLUMN_AMOUNT),
                DebtsEntry.getQName(DebtsEntry.COLUMN_DATE_ENTERED),
                DebtsEntry.getQName(DebtsEntry.COLUMN_ENTRY_ID),
                DebtsEntry.getQName(DebtsEntry.COLUMN_STATUS),
                DebtsEntry.getQName(DebtsEntry.COLUMN_NOTE)
        };


        return new CursorLoader(this, uri, projection, selection, selectionArgs, null);
    }

    @NonNull
    private CursorLoader createLoaderDebtToMe() {
        Uri uri = DebtsEntry.CONTENT_URI_JOIN;

        String selection = DebtsEntry.COLUMN_TYPE + " = ? ";

        String[] selectionArgs = {String.valueOf(Debt.DEBT_TYPE_OWED)};

        String[] projection = {
                PersonsEntry.getQName(PersonsEntry.COLUMN_IMAGE_URI),
                PersonsEntry.getQName(PersonsEntry.COLUMN_NAME),
                PersonsEntry.getQName(PersonsEntry.COLUMN_PHONE_NO),
                DebtsEntry.getQName(DebtsEntry.COLUMN_DATE_DUE),
                DebtsEntry.getQName(DebtsEntry.COLUMN_AMOUNT),
                DebtsEntry.getQName(DebtsEntry.COLUMN_DATE_ENTERED),
                DebtsEntry.getQName(DebtsEntry.COLUMN_ENTRY_ID),
                DebtsEntry.getQName(DebtsEntry.COLUMN_STATUS),
                DebtsEntry.getQName(DebtsEntry.COLUMN_NOTE)
        };


        return new CursorLoader(this, uri, projection, selection, selectionArgs, null);
    }


    @NonNull
    private CursorLoader createLoaderPerson() {
        mQueryLoadForPersonFinished = false;
        Uri uri = PersonsEntry.CONTENT_URI;
        String[] personColumns = {
                PersonsEntry.COLUMN_IMAGE_URI,
                PersonsEntry.COLUMN_NAME,
                PersonsEntry.COLUMN_PHONE_NO};
        String sortOrder = PersonsEntry.COLUMN_NAME + " ASC ";
        return new CursorLoader(this, uri, personColumns, null, null, sortOrder);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mProgressDialog.dismiss();
        mQueryLoadForPersonFinished = true;
        if (loader.getId() == LOADER_PERSON) {
            mPersonRecyclerAdapter.changeCursour(data);
        } else if (loader.getId() == LOADER_DEBT_TO_ME) {
            mDebtorRecyclerAdapterToMe.changeCursour(data);
            loadTotalAmountToMe(data);
        } else if (loader.getId() == LOADER_DEBT_BY_ME) {
            mDebtorRecyclerAdapterByMe.changeCursour(data);
            loadTotalAmountByMe(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_PERSON) {
            mPersonRecyclerAdapter.changeCursour(null);
        } else if (loader.getId() == LOADER_DEBT_TO_ME) {
            mDebtorRecyclerAdapterToMe.changeCursour(null);
        } else if (loader.getId() == LOADER_DEBT_BY_ME) {
            mDebtorRecyclerAdapterByMe.changeCursour(null);
        }

    }

    private void loadTotalAmountToMe(Cursor data) {
        double totalAmt = 0;
        int amountPos = data.getColumnIndex(DebtsEntry.COLUMN_AMOUNT);
        int count = data.getCount();
        for (int i = 0; i < count; i++) {
            data.moveToPosition(i);
            totalAmt += Double.parseDouble(data.getString(amountPos));
        }
        mTotalAmountToMe.setText(String.valueOf(totalAmt));
    }

    private void loadTotalAmountByMe(Cursor data) {
        double totalAmt = 0;
        int amountPos = data.getColumnIndex(DebtsEntry.COLUMN_AMOUNT);
        int count = data.getCount();
        for (int i = 0; i < count; i++) {
            data.moveToPosition(i);
            totalAmt += Double.parseDouble(data.getString(amountPos));
        }
        mTotalAmountByMe.setText(String.valueOf(totalAmt));
    }

}
