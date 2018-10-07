package com.example.nnroh.moneycontrol.App;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chaos.view.PinView;
import com.example.nnroh.moneycontrol.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;
import com.rilixtech.CountryCodePicker;
import com.shuhart.stepview.StepView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private Button mSendCodeButton;
    private Button mVerifyCodeButton;
    private Button mSaveProfileButton;

    private EditText mProfileName;
    CountryCodePicker ccp;

    private FirebaseAuth mAuth;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private static final String TAG = "LoginActivity";
    private String phoneNumber;
    private String photoUri;
    String verificationCode;
    private EditText mPhoneNumberField;
    private PinView verifyCodeET;

    private int currentStep = 0;
    LinearLayout layout1, layout2, layout3;
    StepView stepView;
    AlertDialog dialog_verifying, profile_dialog;
    private TextView mPhonNumberView;
    public static final String USER_NAME = "com.example.nnroh.moneycontrol.USER_NAME";
    public static final String USER_PHONE_NO = "com.example.nnroh.moneycontrol.USER_PHONE_NO";

    private String userChoosenTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mProfileName = findViewById(R.id.et_set_name);

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);

        ccp =  findViewById(R.id.ccp);
        //Button
        mSendCodeButton = findViewById(R.id.btn_send_code);
        mVerifyCodeButton = findViewById(R.id.btn_verify_code);
        mSaveProfileButton = findViewById(R.id.btn_save_profile);

        //edittext
        mPhoneNumberField = findViewById(R.id.et_phone_number);
        verifyCodeET = (PinView) findViewById(R.id.pinView);
        mPhonNumberView = findViewById(R.id.tv_phone_number);

        stepView = findViewById(R.id.step_view);
        stepView.setStepsNumber(3);
        stepView.go(0, true);
        layout1.setVisibility(View.VISIBLE);


        mSendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ccp.registerPhoneNumberTextView(mPhoneNumberField);

                phoneNumber = ccp.getFullNumberWithPlus();
               // String phoneNumberWithCode = "+91" + phoneNumber;
                mPhonNumberView.setText(phoneNumber);
                if (TextUtils.isEmpty(phoneNumber)) {
                    mPhoneNumberField.setError("Enter a Phone Number");
                    mPhoneNumberField.requestFocus();
                } else if (phoneNumber.length() < 10) {
                    mPhoneNumberField.setError("Please enter a valid phone number");
                    mPhoneNumberField.requestFocus();
                } else {

                    if (currentStep < stepView.getStepCount() - 1) {
                        currentStep++;
                        stepView.go(currentStep, true);
                    } else {
                        stepView.done(true);
                    }
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.VISIBLE);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            LoginActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });

        mVerifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificationCode = verifyCodeET.getText().toString();
                if (verificationCode.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter verification code", Toast.LENGTH_SHORT).show();
                }else {
                    LayoutInflater inflater = getLayoutInflater();
                    View alertLayout = inflater.inflate(R.layout.processing_dialog, null);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);

                    dialog.setView(alertLayout);
                    dialog.setCancelable(false);
                    dialog_verifying = dialog.create();
                    dialog_verifying.show();

                    PhoneAuthCredential userCredential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(userCredential);
                }
            }
        });

        mSaveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String profileName = mProfileName.getText().toString();
                if (TextUtils.isEmpty(profileName)) {
                    mProfileName.setError("This field is required.");
                } else {
                    if (currentStep < stepView.getStepCount() - 1) {
                        currentStep++;
                        stepView.go(currentStep, true);
                    } else {
                        stepView.done(true);
                    }
                    LayoutInflater inflater = getLayoutInflater();
                    View alertLayout = inflater.inflate(R.layout.profile_create_dialog, null);
                    AlertDialog.Builder show = new AlertDialog.Builder(LoginActivity.this);
                    show.setView(alertLayout);
                    show.setCancelable(false);
                    profile_dialog = show.create();
                    profile_dialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            profile_dialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra(USER_PHONE_NO, phoneNumber);
                            intent.putExtra(USER_NAME, profileName);
                            startActivity(intent);
                            finish();
                        }
                    }, 2000);
                }
            }
        });


        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.processing_dialog, null);
                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);

                dialog.setView(alertLayout);
                dialog.setCancelable(false);
                dialog_verifying = dialog.create();
                dialog_verifying.show();

                Toast.makeText(LoginActivity.this, "Verified", Toast.LENGTH_SHORT);

                signInWithPhoneAuthCredential(credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    mPhoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(LoginActivity.this, "Code Sent", Toast.LENGTH_SHORT);

            }
        };
        // [END phone_auth_callbacks]


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog_verifying.dismiss();
                            if (currentStep < stepView.getStepCount() - 1) {
                                currentStep++;
                                stepView.go(currentStep, true);
                            } else {
                                stepView.done(true);
                            }
                            layout1.setVisibility(View.GONE);
                            layout2.setVisibility(View.GONE);
                            layout3.setVisibility(View.VISIBLE);
                            // ...

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null){
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final DatabaseReference mUserDB = database.getReference("user").child(user.getUid());
                                mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()){
                                            Map<String, Object>  userMap = new HashMap<>();
                                            userMap.put("Phone", user.getPhoneNumber());
                                            mUserDB.updateChildren(userMap);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                        } else {
                            dialog_verifying.dismiss();
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}



