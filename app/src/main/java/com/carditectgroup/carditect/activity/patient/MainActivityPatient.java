package com.carditectgroup.carditect.activity.patient;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.carditectgroup.carditect.constants.Constants;
import com.carditectgroup.carditect.fragment.patient.DietFragment;
import com.carditectgroup.carditect.fragment.patient.HomeFragment;
import com.carditectgroup.carditect.fragment.patient.ProfileFragment;
import com.carditectgroup.carditect.R;
import com.carditectgroup.carditect.utils.PreferenceManager;

public class MainActivityPatient extends AppCompatActivity {

    private Fragment selectedFragment = null;
    private String userId;
    private PreferenceManager preferenceManager;
    private FirebaseUser firebaseUser;

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
        preferenceManager = new PreferenceManager(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
        userId = preferenceManager.getString(Constants.USER_ID);

    }

    @SuppressLint("NonConstantResourceId")
    private final ChipNavigationBar.OnItemSelectedListener onItemSelectedListener = i -> {
        switch (i) {
            case R.id.home_menu:
                selectedFragment = new HomeFragment();
                break;
            case R.id.maps_menu:
                selectedFragment = new DietFragment();
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
