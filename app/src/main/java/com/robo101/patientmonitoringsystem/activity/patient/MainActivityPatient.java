package com.robo101.patientmonitoringsystem.activity.patient;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.fragment.patient.HomeFragment;
import com.robo101.patientmonitoringsystem.fragment.patient.MapsFragment;
import com.robo101.patientmonitoringsystem.fragment.patient.ProfileFragment;
import com.robo101.patientmonitoringsystem.R;

public class MainActivityPatient extends AppCompatActivity {

    private Fragment selectedFragment = null;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        initialize();

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        checkForBatteryOptimization();

    }

    private void initialize() {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();

        ChipNavigationBar bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemSelected(R.id.home_menu, true);

        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
        userId = getIntent().getStringExtra(Constants.USER_ID);

    }

    @SuppressLint("NonConstantResourceId")
    private final ChipNavigationBar.OnItemSelectedListener onItemSelectedListener = i -> {
        switch (i) {
            case R.id.home_menu:
                selectedFragment = new HomeFragment();

                Bundle bundle = new Bundle();
                bundle.putString(Constants.USER_ID, "userId");
                //Log.e("eeeerrrooooor",userId);
                selectedFragment.setArguments(bundle);
                break;
            case R.id.maps_menu:
                selectedFragment = new MapsFragment();
                break;
            case R.id.settings_menu:
                selectedFragment = new ProfileFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString(Constants.USER_ID, userId);
                selectedFragment.setArguments(bundle2);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int REQUEST_CODE_BATTERY_OPTIMIZATIONS = 1;
        if(requestCode == REQUEST_CODE_BATTERY_OPTIMIZATIONS) {
            checkForBatteryOptimization();
        }
    }
}
