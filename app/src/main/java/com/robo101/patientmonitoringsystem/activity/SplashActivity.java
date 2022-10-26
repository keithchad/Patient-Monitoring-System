
package com.robo101.patientmonitoringsystem.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.robo101.patientmonitoringsystem.activity.doctor.MainActivityDoctor;
import com.robo101.patientmonitoringsystem.activity.patient.MainActivityPatient;
import com.robo101.patientmonitoringsystem.authentication.patient.PhoneOTPActivity;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.utils.PreferenceManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initialize();
        
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorBlack));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));
        
    }

    private void initialize() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        PreferenceManager preferenceManager = new PreferenceManager(this);

        ImageView lottieAnimationView = findViewById(R.id.lottieAnimation);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
                if (firebaseUser != null) {
                    if (preferenceManager.getBoolean(Constants.IS_LOGGED_IN).equals(true)) {
                        intent = new Intent(this, MainActivityPatient.class);
                    } else {
                        intent = new Intent(this, MainActivityDoctor.class);
                    }
                } else {
                    if(preferenceManager.getBoolean(Constants.IS_NEW_USER)) {
                        intent = new Intent(this, PhoneOTPActivity.class);
                    } else {
                        intent = new Intent(getApplicationContext(), PhoneOTPActivity.class);
                    }
                }
                startActivity(intent);

        }, 2000);
    }
}