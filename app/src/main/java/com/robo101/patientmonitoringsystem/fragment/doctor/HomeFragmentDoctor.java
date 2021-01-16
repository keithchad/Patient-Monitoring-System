package com.robo101.patientmonitoringsystem.fragment.doctor;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.activity.patient.MainActivityPatient;
import com.robo101.patientmonitoringsystem.adapter.DoctorAdapter;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.listeners.OnPatientClicked;
import com.robo101.patientmonitoringsystem.model.User_Patient;

import java.util.ArrayList;
import java.util.List;

public class HomeFragmentDoctor extends Fragment implements OnPatientClicked {

    private List<User_Patient> list;
    private DoctorAdapter doctorAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_doctor, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        list = new ArrayList<>();
        doctorAdapter = new DoctorAdapter(getContext(), list, this);

        recyclerView.setAdapter(doctorAdapter);

        getPatientData();
    }

    private void getPatientData() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User_Patient userPatient = dataSnapshot.getValue(User_Patient.class);
                    list.add(userPatient);
                }

                doctorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onPatientClicked(User_Patient userPatient) {
        Intent intent = new Intent(getContext(), MainActivityPatient.class);
        intent.putExtra(Constants.USER_ID, userPatient.getUserId());
        startActivity(intent);
    }
}