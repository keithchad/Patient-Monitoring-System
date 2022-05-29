package com.robo101.patientmonitoringsystem.activity.patient;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.robo101.patientmonitoringsystem.alarm.ui.home.HomeFragment;
import com.robo101.patientmonitoringsystem.alarm.ui.home.TimeItem;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.fragment.feed.PatientFeedFragment;
import com.robo101.patientmonitoringsystem.fragment.patient.HomeFragmentPatient;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivityPatient extends AppCompatActivity {

    private Fragment selectedFragment = null;
    private String userId;
    private FirebaseUser firebaseUser;
    private AdView mAdView;

    public static ArrayList<TimeItem> timeItems= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        initialize();
        initializeAds();

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        checkForBatteryOptimization();

    }

    private void initializeAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                Toast.makeText(MainActivityPatient.this, "Initialized", Toast.LENGTH_SHORT).show();
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Toast.makeText(MainActivityPatient.this, "Loaded Ad", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }

    private void initialize() {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragmentPatient()).commit();

        ChipNavigationBar bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemSelected(R.id.home_menu, true);
        PreferenceManager preferenceManager = new PreferenceManager(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
        userId = preferenceManager.getString(Constants.USER_ID);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful() && task.getResult() != null) {

                        if (userId == null) {

                            sendFCMTokenToDatabase(task.getResult());

                        }
                    }
                });

    }

    @SuppressLint("NonConstantResourceId")
    private final ChipNavigationBar.OnItemSelectedListener onItemSelectedListener = i -> {
        switch (i) {
            case R.id.home_menu:
                selectedFragment = new HomeFragmentPatient();
                break;
            case R.id.maps_menu:
                selectedFragment = new HomeFragment();
                break;
            case R.id.settings_menu:
                selectedFragment = new PatientFeedFragment();
                break;
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment).commit();
        }
    };

    private void checkForBatteryOptimization() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

            if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityPatient.this);
                builder.setTitle("Warning");
                builder.setMessage("Battery Optimization is Enabled,It can Interrupt running in background!");
                builder.setPositiveButton("Disable", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    startActivity(intent);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        }
    }

    private void sendFCMTokenToDatabase(String token) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Patients").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.FCM_TOKEN, token);

        reference.updateChildren(hashMap).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Unable to send Token: "+ e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int REQUEST_CODE_BATTERY_OPTIMIZATIONS = 1;
        if(requestCode == REQUEST_CODE_BATTERY_OPTIMIZATIONS) {
            checkForBatteryOptimization();
        }
    }
}
