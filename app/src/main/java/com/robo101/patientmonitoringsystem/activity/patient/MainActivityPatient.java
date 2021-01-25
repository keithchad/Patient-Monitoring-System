package com.robo101.patientmonitoringsystem.activity.patient;


import android.annotation.SuppressLint;
import android.os.Bundle;

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
                bundle.putString(Constants.USER_ID, userId);
                Fragment fragment = new Fragment();
                fragment.setArguments(bundle);
                break;
            case R.id.maps_menu:
                selectedFragment = new MapsFragment();
                break;
            case R.id.settings_menu:
                selectedFragment = new ProfileFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString(Constants.USER_ID, userId);
                Fragment fragment2 = new Fragment();
                fragment2.setArguments(bundle2);
                break;
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment).commit();
        }
    };
}
