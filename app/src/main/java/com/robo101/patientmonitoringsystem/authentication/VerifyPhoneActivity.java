package com.robo101.patientmonitoringsystem.authentication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.robo101.patientmonitoringsystem.activity.MainActivity;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.utils.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {

    private TextInputEditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private ProgressBar progressBar;
    private MaterialButton buttonVerify;
    private ScrollView scrollView;
    private String verificationId;
    private PreferenceManager preferenceManager;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        initialize();
    }

    private void initialize() {

        TextView textMobile = findViewById(R.id.textMobile);
        textMobile.setText(String.format(
                "+254-%s", getIntent().getStringExtra(Constants.MOBILE)
        ));

        inputCode1 = findViewById(R.id.edittextInputCode1);
        inputCode2 = findViewById(R.id.edittextInputCode2);
        inputCode3 = findViewById(R.id.edittextInputCode3);
        inputCode4 = findViewById(R.id.edittextInputCode4);
        inputCode5 = findViewById(R.id.edittextInputCode5);
        inputCode6 = findViewById(R.id.edittextInputCode6);
        progressBar = findViewById(R.id.progressBarVerify);
        buttonVerify = findViewById(R.id.buttonVerify);
        scrollView = findViewById(R.id.scrollViewVerify);

        firebaseAuth = FirebaseAuth.getInstance();
        verificationId = getIntent().getStringExtra(Constants.VERIFICATION_ID);
        preferenceManager = new PreferenceManager(this);

        buttonVerify.setOnClickListener(v -> verify());

        setUpOTPInputs();
    }

    private void verify() {

        if (Objects.requireNonNull(inputCode1.getText()).toString().trim().isEmpty()
        || Objects.requireNonNull(inputCode2.getText()).toString().trim().isEmpty()
        || Objects.requireNonNull(inputCode3.getText()).toString().trim().isEmpty()
        || Objects.requireNonNull(inputCode4.getText()).toString().trim().isEmpty()
        || Objects.requireNonNull(inputCode5.getText()).toString().trim().isEmpty()
        || Objects.requireNonNull(inputCode6.getText()).toString().trim().isEmpty()) {
            Snackbar.make(scrollView, "Code can't be empty", Snackbar.LENGTH_SHORT).show();
            return;
        }
        String code = inputCode1.getText().toString() +
                inputCode2.getText().toString() +
                inputCode3.getText().toString() +
                inputCode4.getText().toString() +
                inputCode5.getText().toString() +
                inputCode6.getText().toString();

        if (verificationId != null) {
            progressBar.setVisibility(View.VISIBLE);
            buttonVerify.setVisibility(View.GONE);

            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                    verificationId,
                    code
            );

            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        buttonVerify.setVisibility(View.VISIBLE);

                        if (task.isSuccessful()) {
                            openDialog();
                        } else {
                            Snackbar.make(scrollView, "The code entered was invalid!", Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        buttonVerify.setVisibility(View.VISIBLE);
                    });
        }

        resendCode();
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void openDialog() {

        Dialog dialog = new Dialog(this, R.style.FullScreenDialogTheme);
        dialog.setContentView(R.layout.name_dialog_layout);
        dialog.show();
        dialog.setCancelable(false);

        TextInputEditText edittextName = dialog.findViewById(R.id.edittextName);
        TextInputEditText edittextAge = dialog.findViewById(R.id.edittextAge);
        TextInputEditText edittextGender = dialog.findViewById(R.id.edittextGender);
        MaterialButton createTextInputLayoutBd = dialog.findViewById(R.id.createTextInputLayoutBd);
        MaterialButton buttonContinue = dialog.findViewById(R.id.buttonContinue);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBarNameDialog);

        createTextInputLayoutBd.setOnClickListener(v -> {

            final  Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog pickerDialog = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                pickerDialog = new DatePickerDialog(VerifyPhoneActivity.this, (view, year1, month1, dayOfMonth) -> {

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, month1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String format = new SimpleDateFormat("dd/MMM/yyyy").format(calendar.getTime());
                    createTextInputLayoutBd.setText(format);
                    edittextAge.setText(calculateAge(calendar.getTimeInMillis()) + " years");

                }, year, month, day);

            }
            if (pickerDialog != null) {
                pickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                pickerDialog.show();
            }

        });

        buttonContinue.setOnClickListener(v -> {

            if (Objects.requireNonNull(edittextName.getText()).toString().trim().isEmpty()) {
                Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                return;
            } else if(Objects.requireNonNull(edittextAge.getText()).toString().trim().isEmpty()) {
                Toast.makeText(this, "Age can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }else if(Objects.requireNonNull(edittextGender.getText()).toString().trim().isEmpty()) {
                Toast.makeText(this, "Gender can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            buttonContinue.setVisibility(View.INVISIBLE);

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            String name = edittextName.getText().toString();
            String birthday = createTextInputLayoutBd.getText().toString();
            String age = edittextAge.getText().toString();
            String gender = edittextGender.getText().toString();

            String userId = null;
            if (firebaseUser != null) {
                userId = firebaseUser.getUid();
            }

            DatabaseReference reference = null;
            if (userId != null) {
                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
            }

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(Constants.USER_ID, userId);
            hashMap.put(Constants.NAME, name);
            hashMap.put(Constants.BIRTHDAY, birthday);
            hashMap.put(Constants.GENDER, gender);
            hashMap.put(Constants.AGE, age);
            hashMap.put(Constants.PHONE_NUMBER, "+254" + getIntent().getStringExtra(Constants.MOBILE));
            hashMap.put(Constants.IMAGE_URL, Constants.DUMMY_IMAGE);

            if (reference != null) {
                reference.setValue(hashMap)
                        .addOnCompleteListener(referenceTask -> {
                            if (referenceTask.isSuccessful()) {
                                progressBar.setVisibility(View.VISIBLE);
                                buttonContinue.setVisibility(View.INVISIBLE);
                                dialog.dismiss();

                                preferenceManager.putString(Constants.USER_NAME, name);
                                preferenceManager.putBoolean(Constants.IS_LOGGED_IN, true);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(e -> Snackbar.make(scrollView, "Failed to add name", Snackbar.LENGTH_SHORT).show());
            }
        });
    }

    private int calculateAge(long date) {
        Calendar dob  = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }
        return age;
    }

    private void resendCode() {

        findViewById(R.id.buttonResendOTP).setOnClickListener(v -> {

            PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
            callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                }

                @Override
                public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    verificationId = newVerificationId;
                    Snackbar.make(scrollView, "OTP Resent", Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Snackbar.make(scrollView, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                }
            };

            PhoneAuthOptions authOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber("+254" + getIntent().getStringExtra(Constants.MOBILE))
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(VerifyPhoneActivity.this)
                    .setCallbacks(callbacks)
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(authOptions);
        });
    }

    private void setUpOTPInputs() {

        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}