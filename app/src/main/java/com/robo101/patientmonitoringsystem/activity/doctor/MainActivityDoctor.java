package com.robo101.patientmonitoringsystem.activity.doctor;

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
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.activity.patient.MainActivityPatient;
import com.robo101.patientmonitoringsystem.fragment.feed.DoctorFeedFragment;
import com.robo101.patientmonitoringsystem.fragment.doctor.HomeFragmentDoctor;
import com.robo101.patientmonitoringsystem.fragment.doctor.NotificationFragmentDoctor;

public class MainActivityDoctor extends AppCompatActivity {

    private Fragment selectedFragment = null;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        initialize();
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorWhite));
    }

    private void initializeAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                Toast.makeText(MainActivityDoctor.this, "Initialized", Toast.LENGTH_SHORT).show();
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Toast.makeText(MainActivityDoctor.this, "Loaded Ad", Toast.LENGTH_SHORT).show();
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
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerDoctor, new HomeFragmentDoctor()).commit();

        ChipNavigationBar bottomNavigationView = findViewById(R.id.bottomNavigationViewDoctor);
        bottomNavigationView.setItemSelected(R.id.home_menu_doctor, true);

        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
    }

    @SuppressLint("NonConstantResourceId")
    private final ChipNavigationBar.OnItemSelectedListener onItemSelectedListener = i -> {
        switch (i) {
            case R.id.home_menu_doctor:
                selectedFragment = new HomeFragmentDoctor();
                break;
            case R.id.notifications_menu:
                selectedFragment = new NotificationFragmentDoctor();
                break;
            case R.id.settings_menu_doctor:
                selectedFragment = new DoctorFeedFragment();
                break;
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerDoctor, selectedFragment).commit();
        }
    };

    private void checkForBatteryOptimization() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

            if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityDoctor.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int REQUEST_CODE_BATTERY_OPTIMIZATIONS = 1;
        if(requestCode == REQUEST_CODE_BATTERY_OPTIMIZATIONS) {
            checkForBatteryOptimization();
        }
    }
}