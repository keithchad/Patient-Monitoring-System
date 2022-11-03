
package com.carditectgroup.carditect.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.carditectgroup.carditect.activity.doctor.MainActivityDoctor;
import com.carditectgroup.carditect.activity.patient.MainActivityPatient;
import com.carditectgroup.carditect.authentication.patient.PhoneOTPActivity;
import com.carditectgroup.carditect.R;
import com.carditectgroup.carditect.constants.Constants;
import com.carditectgroup.carditect.utils.PreferenceManager;

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
                        finish();
                    } else {
                        intent = new Intent(this, MainActivityDoctor.class);
                        finish();
                    }
                } else {
                    if(preferenceManager.getBoolean(Constants.IS_NEW_USER)) {
                        intent = new Intent(this, PhoneOTPActivity.class);
                        finish();
                    } else {
                        intent = new Intent(getApplicationContext(), PhoneOTPActivity.class);
                        finish();
                    }
                }
                startActivity(intent);

        }, 2000);
    }
}