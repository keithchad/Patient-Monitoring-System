package com.robo101.patientmonitoringsystem.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.robo101.patientmonitoringsystem.authentication.PhoneOTPActivity;
import com.robo101.patientmonitoringsystem.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initialize();
    }

    private void initialize() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (firebaseUser != null) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, PhoneOTPActivity.class);
            }
            startActivity(intent);
        }, 2000);
    }
}