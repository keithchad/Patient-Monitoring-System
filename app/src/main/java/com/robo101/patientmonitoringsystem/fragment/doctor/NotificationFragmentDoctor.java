package com.robo101.patientmonitoringsystem.fragment.doctor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robo101.patientmonitoringsystem.R;

public class NotificationFragmentDoctor extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_doctor, container, false);
        initialize();
        return view;
    }

    private void initialize() {
    }
}