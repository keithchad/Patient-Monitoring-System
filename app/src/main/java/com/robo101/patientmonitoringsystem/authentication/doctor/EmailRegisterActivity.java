package com.robo101.patientmonitoringsystem.authentication.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.activity.doctor.MainActivityDoctor;
import com.robo101.patientmonitoringsystem.constants.Constants;

import java.util.HashMap;

public class EmailRegisterActivity extends AppCompatActivity {

    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputHospital;
    private EditText inputGender;
    private EditText inputSpecialization;
    private EditText inputPassword;
    private EditText inputConfirmPassword;

    private MaterialButton buttonSignUp;
    private ProgressBar signUpProgressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_register);
        initialize();
        setBarColors();
    }

    private void initialize() {

        TextView textSignIn = findViewById(R.id.textSignIn);
        ImageView imageBack = findViewById(R.id.imageBack);
        inputFirstName = findViewById(R.id.inputFirstName);
        inputLastName = findViewById(R.id.inputLastName);
        inputGender = findViewById(R.id.inputGender);
        inputHospital = findViewById(R.id.inputHospital);
        inputSpecialization = findViewById(R.id.inputSpecialization);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        signUpProgressBar = findViewById(R.id.signUpProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonSignUp.setOnClickListener(v -> {
            if(inputFirstName.getText().toString().trim().isEmpty()) {
                Toast.makeText(EmailRegisterActivity.this, "Enter First Name", Toast.LENGTH_SHORT).show();
            }else if(inputLastName.getText().toString().trim().isEmpty()) {
                Toast.makeText(EmailRegisterActivity.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
            }else if(inputEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(EmailRegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                Toast.makeText(EmailRegisterActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
            }else if(inputPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(EmailRegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            }else if(inputHospital.getText().toString().trim().isEmpty()) {
                Toast.makeText(EmailRegisterActivity.this, "Enter Hospital", Toast.LENGTH_SHORT).show();
            }else if(inputGender.getText().toString().trim().isEmpty()) {
                Toast.makeText(EmailRegisterActivity.this, "Enter Gender", Toast.LENGTH_SHORT).show();
            }else if(inputConfirmPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(EmailRegisterActivity.this, "Confirm your Password", Toast.LENGTH_SHORT).show();
            }else if(!inputPassword.getText().toString().equals(inputConfirmPassword.getText().toString())) {
                Toast.makeText(EmailRegisterActivity.this, "Password & Confirm Password must be same", Toast.LENGTH_SHORT).show();
            }else {
                signUp();
            }
        });
        imageBack.setOnClickListener(v -> onBackPressed());
        textSignIn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), EmailLoginActivity.class)));
    }

    private void signUp() {
        buttonSignUp.setVisibility(View.INVISIBLE);
        signUpProgressBar.setVisibility(View.VISIBLE);

        String name = inputFirstName.getText().toString() + inputLastName.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(inputEmail.getText().toString(), inputPassword.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        String userId = null;
                        if (firebaseUser != null) {
                            userId = firebaseUser.getUid();
                        }

                        DatabaseReference reference = null;
                        if (userId != null) {
                            reference = FirebaseDatabase.getInstance().getReference().child("Doctors").child(userId);
                        }

                        HashMap<String, Object> user = new HashMap<>();
                        user.put(Constants.USER_ID, userId);
                        user.put(Constants.NAME, name);
                        user.put(Constants.EMAIL, inputEmail.getText().toString());
                        user.put(Constants.IMAGE_URL, Constants.DUMMY_IMAGE);
                        user.put(Constants.HOSPITAL, inputHospital.getText().toString());
                        user.put(Constants.GENDER, inputGender.getText().toString());
                        user.put(Constants.SPECIALIZATION, inputSpecialization.getText().toString());

                        if (reference != null) {
                            reference.setValue(user).addOnCompleteListener(referenceTask -> {
                                buttonSignUp.setVisibility(View.VISIBLE);
                                signUpProgressBar.setVisibility(View.GONE);
                                if (referenceTask.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivityDoctor.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(e -> {
                                buttonSignUp.setVisibility(View.VISIBLE);
                                signUpProgressBar.setVisibility(View.GONE);
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }).addOnFailureListener(e -> {
                    buttonSignUp.setVisibility(View.VISIBLE);
                    signUpProgressBar.setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setBarColors() {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorWhite));
    }
}