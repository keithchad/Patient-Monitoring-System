package com.robo101.patientmonitoringsystem.fragment.doctor;

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
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.activity.patient.MainActivityPatient;
import com.robo101.patientmonitoringsystem.adapter.DoctorAdapter;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.listeners.OnPatientClicked;
import com.robo101.patientmonitoringsystem.model.User_Patient;
import com.robo101.patientmonitoringsystem.model.Vitals;
import com.robo101.patientmonitoringsystem.utils.PreferenceManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

//        DatabaseReference referenceNotification = FirebaseDatabase.getInstance().getReference().child("Vitals").child(firebaseUser.getUid());
//        referenceNotification.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Vitals vitals = snapshot.getValue(Vitals.class);
//
//                if (vitals != null) {
//
//                    if (vitals.getHeartBeat() <= 60 || vitals.getHeartBeat() <= 70) {
//                        createNotification("Heartbeat", "The Patient's HeartBeat is " + vitals.getHeartBeat());
//                    } else if (vitals.getBloodOxygen() <= 20) {
//                        createNotification("BloodOxygen", "The Patient's BloodOxygen is " + vitals.getBloodOxygen());
//                    } else if (vitals.getBloodPressure() <= 120.0) {
//                        createNotification("BloodPressure", "The Patient's BloodPressure is " + vitals.getBloodPressure());
//                    } else if (vitals.getBodyTemperature() <= 30.0) {
//                        createNotification("Body Temperature", "The Patient's Body Temperature is " + vitals.getBodyTemperature());
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void createNotification(String contentTitle, String contentText) {

        int notificationId = new Random().nextInt(100);
        String channelId = "notification_channel_1";

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getContext(), MainActivityPatient.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getContext(), channelId
        );

        builder.setSmallIcon(R.drawable.ic_dashboard);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);

        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {

                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId, "notification_channel_1",
                        NotificationManager.IMPORTANCE_HIGH
                );

                notificationChannel.setDescription("This is to notify the user");
                notificationChannel.enableVibration(true);
                notificationChannel.enableLights(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        Notification notification = builder.build();
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
        }

    }

    @Override
    public void onPatientClicked(User_Patient userPatient) {
        Intent intent = new Intent(getContext(), MainActivityPatient.class);
        preferenceManager.putString(Constants.USER_ID, userPatient.getUserId());
        Objects.requireNonNull(getActivity()).startActivity(intent);
    }
}