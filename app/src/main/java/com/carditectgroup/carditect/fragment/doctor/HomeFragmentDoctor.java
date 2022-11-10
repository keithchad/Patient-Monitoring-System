package com.carditectgroup.carditect.fragment.doctor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.carditectgroup.carditect.R;
import com.carditectgroup.carditect.activity.patient.MainActivityPatient;
import com.carditectgroup.carditect.adapter.DoctorAdapter;
import com.carditectgroup.carditect.constants.Constants;
import com.carditectgroup.carditect.listeners.OnPatientClicked;
import com.carditectgroup.carditect.model.User_Patient;
import com.carditectgroup.carditect.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class HomeFragmentDoctor extends Fragment implements OnPatientClicked {

    private List<User_Patient> list;
    private DoctorAdapter doctorAdapter;
    private PreferenceManager preferenceManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_doctor, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {
        preferenceManager = new PreferenceManager(getContext());
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshHome);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this::getPatientData);

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
                swipeRefreshLayout.setRefreshing(false);
                list.clear();
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
        preferenceManager.putString(Constants.USER_ID, userPatient.getUserId());
        Objects.requireNonNull(getActivity()).startActivity(intent);
    }
}