package com.robo101.patientmonitoringsystem.fragment.doctor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.adapter.NotificationAdapter;
import com.robo101.patientmonitoringsystem.model.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationFragmentDoctor extends Fragment {
    
    private RecyclerView recyclerView;
    private List<Notification> list;
    private NotificationAdapter notificationAdapter;
    private FirebaseUser firebaseUser;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_doctor, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshNotification);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this::getNotification);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        list = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), list);

        getNotification();
    }

    private void getNotification() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notification").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                swipeRefreshLayout.setRefreshing(true);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    if (notification != null) {
                        list.add(notification);
                    }
                }

                Collections.reverse(list);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}