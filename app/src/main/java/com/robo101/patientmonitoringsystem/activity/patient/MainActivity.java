package com.robo101.patientmonitoringsystem.activity.patient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.robo101.patientmonitoringsystem.fragment.patient.HomeFragment;
import com.robo101.patientmonitoringsystem.fragment.patient.MapsFragment;
import com.robo101.patientmonitoringsystem.fragment.patient.ProfileFragment;
import com.robo101.patientmonitoringsystem.R;

public class MainActivity extends AppCompatActivity {

    private Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        initialize();

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorWhite));
    }

    private void initialize() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();

        ChipNavigationBar bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemSelected(R.id.home_menu, true);

        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
    }

    @SuppressLint("NonConstantResourceId")
    private final ChipNavigationBar.OnItemSelectedListener onItemSelectedListener = i -> {
        switch (i) {
            case R.id.home_menu:
                selectedFragment = new HomeFragment();
                break;
            case R.id.maps_menu:
                selectedFragment = new MapsFragment();
                break;
            case R.id.settings_menu:
                selectedFragment = new ProfileFragment();
                break;
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment).commit();
        }
    };
}
