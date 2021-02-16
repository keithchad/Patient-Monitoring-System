package com.robo101.patientmonitoringsystem.activity.doctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.fragment.doctor.HomeFragmentDoctor;
import com.robo101.patientmonitoringsystem.fragment.doctor.NotificationFragmentDoctor;
import com.robo101.patientmonitoringsystem.fragment.doctor.ProfileFragmentDoctor;

public class MainActivityDoctor extends AppCompatActivity {

    private Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        initialize();
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorWhite));
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
                selectedFragment = new ProfileFragmentDoctor();
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