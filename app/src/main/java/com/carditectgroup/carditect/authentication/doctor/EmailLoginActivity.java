package com.carditectgroup.carditect.authentication.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.carditectgroup.carditect.R;
import com.carditectgroup.carditect.activity.doctor.MainActivityDoctor;

public class EmailLoginActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar signInProgressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);
        initialize();
        setBarColors();
    }

    private void initialize() {
        TextView textSignUp = findViewById(R.id.textSignUp);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignIn = findViewById(R.id.buttonLogIn);
        signInProgressBar = findViewById(R.id.signInProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonSignIn.setOnClickListener(v -> {
            if(inputEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(EmailLoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                Toast.makeText(EmailLoginActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
            }else if(inputPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(EmailLoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            }else {
                signIn();
            }
        });

        textSignUp.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), EmailRegisterActivity.class)));

    }

    private void signIn() {

        buttonSignIn.setVisibility(View.INVISIBLE);
        signInProgressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(inputEmail.getText().toString(), inputPassword.getText().toString())
                .addOnCompleteListener(task -> {
                    buttonSignIn.setVisibility(View.VISIBLE);
                    signInProgressBar.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(EmailLoginActivity.this, MainActivityDoctor.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }).addOnFailureListener(e -> {
                    buttonSignIn.setVisibility(View.VISIBLE);
                    signInProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setBarColors() {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorWhite));
    }

}