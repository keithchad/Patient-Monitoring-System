package com.carditectgroup.carditect.authentication.patient;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.carditectgroup.carditect.authentication.doctor.EmailRegisterActivity;
import com.carditectgroup.carditect.constants.Constants;
import com.carditectgroup.carditect.R;


import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneOTPActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private MaterialButton buttonGetOTP;
    private ScrollView scrollView;
    private TextInputEditText editTextNumber;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_otp);
        initialize();
    }

    private void initialize() {
        TextView textDoctor = findViewById(R.id.textDoctor);
        scrollView = findViewById(R.id.scrollView);
        editTextNumber = findViewById(R.id.edittextInputNumber);
        buttonGetOTP = findViewById(R.id.buttonGetOTP);
        progressBar = findViewById(R.id.progressBar);

        textDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(PhoneOTPActivity.this, EmailRegisterActivity.class);
            startActivity(intent);
        });

        firebaseAuth = FirebaseAuth.getInstance();

        buttonGetOTP.setOnClickListener(v -> sendOTP());
    }

    private void sendOTP() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (Objects.requireNonNull(editTextNumber.getText()).toString().trim().isEmpty()) {
                Snackbar.make(scrollView, "Enter Mobile Number", Snackbar.LENGTH_SHORT).show();
                return;
            }
        }
        progressBar.setVisibility(View.VISIBLE);
        buttonGetOTP.setVisibility(View.GONE);

        PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;

        callBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                progressBar.setVisibility(View.GONE);
                buttonGetOTP.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                progressBar.setVisibility(View.GONE);
                buttonGetOTP.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), VerifyPhoneActivity.class);
                intent.putExtra(Constants.MOBILE, editTextNumber.getText().toString());
                intent.putExtra(Constants.VERIFICATION_ID, verificationId);
                startActivity(intent);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                buttonGetOTP.setVisibility(View.VISIBLE);
                Snackbar.make(scrollView, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
            }
        };

        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+254" + editTextNumber.getText())
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setActivity(this)
                .setCallbacks(callBacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);

    }
}