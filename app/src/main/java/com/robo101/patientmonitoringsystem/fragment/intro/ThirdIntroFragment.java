package com.robo101.patientmonitoringsystem.fragment.intro;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.authentication.patient.PhoneOTPActivity;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.utils.PreferenceManager;

public class ThirdIntroFragment extends Fragment {

    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third_intro, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {

        TextView textNext = view.findViewById(R.id.textNext);
        preferenceManager = new PreferenceManager(getContext());

        textNext.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PhoneOTPActivity.class);
            preferenceManager.putBoolean(Constants.IS_NEW_USER, false);
            startActivity(intent);
        });

    }
}