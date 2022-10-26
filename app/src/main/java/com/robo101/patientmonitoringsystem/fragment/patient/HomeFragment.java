package com.robo101.patientmonitoringsystem.fragment.patient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.model.User_Patient;
import com.robo101.patientmonitoringsystem.model.Vitals;
import com.robo101.patientmonitoringsystem.utils.PreferenceManager;
import com.robo101.patientmonitoringsystem.viewmodel.TipsViewModel;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private ImageView imageProfile;
    private ImageView redDotHeart;
    private ImageView redDotOxygen;
    private ImageView redDotPressure;
    private ImageView redDotTemperature;
    private Vitals vitals;

    private FirebaseUser firebaseUser;

    private TextView textUsername;
    private TextView textTips;
    private TextView textHeartRate;
    private TextView textBloodOxygen;
    private TextView textBloodPressure;
    private TextView textTemperature;

    private PreferenceManager preferenceManager;

    private ProgressBar progressBar;

    private ConstraintLayout layoutTips;

    private MaterialButton panicButton;

    private String patientId;

    private TipsViewModel tipsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_patient, container, false);
        initialize(view);
        progressBar.setVisibility(View.VISIBLE);
        return view;
    }

    @SuppressWarnings({"deprecation"})
    private void initialize(View view) {

        textUsername = view.findViewById(R.id.textUsername);
        imageProfile = view.findViewById(R.id.imageProfileHome);
        textTips = view.findViewById(R.id.textHealthTips);
        layoutTips = view.findViewById(R.id.layoutTips);
        progressBar = view.findViewById(R.id.progressBarTips);
        ImageView imageNoInternet = view.findViewById(R.id.imageNoInternet);
        TextView textNoInternet = view.findViewById(R.id.textNoInternet);
        panicButton = view.findViewById(R.id.panickButton);
        ScrollView scrollView = view.findViewById(R.id.scrollViewHome);
        ImageView imageRefresh = view.findViewById(R.id.imageRefresh);
        preferenceManager = new PreferenceManager(Objects.requireNonNull(getContext()));

        textHeartRate = view.findViewById(R.id.textHeartbeat);
        textBloodOxygen = view.findViewById(R.id.textBloodOxygen);
        textBloodPressure = view.findViewById(R.id.textBloodPressure);
        textTemperature = view.findViewById(R.id.textTemperature);

        redDotHeart = view.findViewById(R.id.redDotHeart);
        redDotOxygen = view.findViewById(R.id.redDotOxygen);
        redDotPressure = view.findViewById(R.id.redDotPressure);
        redDotTemperature = view.findViewById(R.id.redDotTemperature);

        tipsViewModel = new ViewModelProvider(this).get(TipsViewModel.class);
        patientId = preferenceManager.getString(Constants.USER_ID);
        imageRefresh.setOnClickListener(v -> getTips());

        preferenceManager.putString(Constants.USER_ID, patientId);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        ConnectivityManager connectivityManager =
                (ConnectivityManager) Objects.requireNonNull(getContext()).getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            getUserInfo();
            getVitals();
            getTips();
        } else {
            scrollView.setVisibility(View.GONE);
            textNoInternet.setVisibility(View.VISIBLE);
            imageNoInternet.setVisibility(View.VISIBLE);
        }

        panicButton.setOnClickListener(v -> sendNotification());

    }

    private void sendNotification() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notification").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.USER_ID, firebaseUser.getUid());
        hashMap.put(Constants.TEXT_ISSUE_NAME, "The User needs your help");

        reference.setValue(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Doctor has been notified!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getVitals() {

        if(patientId == null) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Vitals").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    vitals = snapshot.getValue(Vitals.class);

                    if (vitals != null) {

                        if (vitals.getHeartBeat() <= 60 || vitals.getHeartBeat() <= 70) {

                            double heartBeat = vitals.getHeartBeat();
                            BigDecimal bigDecimal = new BigDecimal(heartBeat).setScale(1, RoundingMode.HALF_UP);
                            textHeartRate.setText(String.valueOf(bigDecimal.doubleValue()));
                            redDotHeart.setVisibility(View.VISIBLE);
                            panicButton.setVisibility(View.VISIBLE);

                        }else {

                            double heartBeat = vitals.getHeartBeat();
                            BigDecimal bigDecimal = new BigDecimal(heartBeat).setScale(1, RoundingMode.HALF_UP);
                            textHeartRate.setText(String.valueOf(bigDecimal.doubleValue()));
                            redDotHeart.setVisibility(View.GONE);

                        }

                        if (vitals.getBloodOxygen() <= 20) {

                            textBloodOxygen.setText(String.valueOf(vitals.getBloodOxygen()));
                            redDotOxygen.setVisibility(View.VISIBLE);
                            panicButton.setVisibility(View.VISIBLE);

                        } else {

                            textBloodOxygen.setText(String.valueOf(vitals.getBloodOxygen()));
                            redDotOxygen.setVisibility(View.GONE);

                        }

                        if (vitals.getBloodPressure() <= 120.0) {

                            redDotPressure.setVisibility(View.VISIBLE);
                            double bloodPressure = vitals.getBloodPressure();
                            BigDecimal bigDecimal = new BigDecimal(bloodPressure).setScale(2, RoundingMode.HALF_UP);
                            textBloodPressure.setText(String.valueOf(bigDecimal.doubleValue()));
                            panicButton.setVisibility(View.VISIBLE);

                        } else {

                            redDotPressure.setVisibility(View.GONE);
                            double bloodPressure = vitals.getBloodPressure();
                            BigDecimal bigDecimal = new BigDecimal(bloodPressure).setScale(2, RoundingMode.HALF_UP);
                            textBloodPressure.setText(String.valueOf(bigDecimal.doubleValue()));
                        }

                        if (vitals.getBodyTemperature() <= 30.0) {

                            redDotTemperature.setVisibility(View.VISIBLE);
                            panicButton.setVisibility(View.VISIBLE);
                            double bodyTemperature = vitals.getBodyTemperature();
                            BigDecimal bigDecimalTemp = new BigDecimal(bodyTemperature).setScale(2, RoundingMode.HALF_UP);
                            textTemperature.setText(String.valueOf(bigDecimalTemp.doubleValue()));

                        } else {

                            redDotTemperature.setVisibility(View.GONE);
                            double bodyTemperature = vitals.getBodyTemperature();
                            BigDecimal bigDecimalTemp = new BigDecimal(bodyTemperature).setScale(2, RoundingMode.HALF_UP);
                            textTemperature.setText(String.valueOf(bigDecimalTemp.doubleValue()));

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Vitals").child(patientId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    vitals = snapshot.getValue(Vitals.class);

                    if (vitals != null) {

                        if (vitals.getHeartBeat() <= 60 || vitals.getHeartBeat() <= 70) {

                            double heartBeat = vitals.getHeartBeat();
                            BigDecimal bigDecimal = new BigDecimal(heartBeat).setScale(1, RoundingMode.HALF_UP);
                            textHeartRate.setText(String.valueOf(bigDecimal.doubleValue()));
                            redDotHeart.setVisibility(View.VISIBLE);

                        }else {

                            double heartBeat = vitals.getHeartBeat();
                            BigDecimal bigDecimal = new BigDecimal(heartBeat).setScale(1, RoundingMode.HALF_UP);
                            textHeartRate.setText(String.valueOf(bigDecimal.doubleValue()));
                            redDotHeart.setVisibility(View.GONE);

                        }

                        if (vitals.getBloodOxygen() <= 20) {

                            textBloodOxygen.setText(String.valueOf(vitals.getBloodOxygen()));
                            redDotOxygen.setVisibility(View.VISIBLE);
                            panicButton.setVisibility(View.VISIBLE);

                        } else {

                            textBloodOxygen.setText(String.valueOf(vitals.getBloodOxygen()));
                            redDotOxygen.setVisibility(View.GONE);

                        }

                        if (vitals.getBloodPressure() <= 120.0) {

                            redDotPressure.setVisibility(View.VISIBLE);
                            double bloodPressure = vitals.getBloodPressure();
                            BigDecimal bigDecimal = new BigDecimal(bloodPressure).setScale(2, RoundingMode.HALF_UP);
                            textBloodPressure.setText(String.valueOf(bigDecimal.doubleValue()));
                            panicButton.setVisibility(View.VISIBLE);

                        } else {

                            redDotPressure.setVisibility(View.GONE);
                            double bloodPressure = vitals.getBloodPressure();
                            BigDecimal bigDecimal = new BigDecimal(bloodPressure).setScale(2, RoundingMode.HALF_UP);
                            textBloodPressure.setText(String.valueOf(bigDecimal.doubleValue()));
                        }

                        if (vitals.getBodyTemperature() <= 30.0) {

                            redDotTemperature.setVisibility(View.VISIBLE);
                            panicButton.setVisibility(View.VISIBLE);
                            double bodyTemperature = vitals.getBodyTemperature();
                            BigDecimal bigDecimalTemp = new BigDecimal(bodyTemperature).setScale(2, RoundingMode.HALF_UP);
                            textTemperature.setText(String.valueOf(bigDecimalTemp.doubleValue()));

                        } else {

                            redDotTemperature.setVisibility(View.GONE);
                            double bodyTemperature = vitals.getBodyTemperature();
                            BigDecimal bigDecimalTemp = new BigDecimal(bodyTemperature).setScale(2, RoundingMode.HALF_UP);
                            textTemperature.setText(String.valueOf(bigDecimalTemp.doubleValue()));

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    private void getUserInfo() {
        if(patientId != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(patientId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User_Patient userPatient = snapshot.getValue(User_Patient.class);
                    if (userPatient != null) {
                        Glide.with(Objects.requireNonNull(getContext())).load(userPatient.getImageUrl()).into(imageProfile);
                        textUsername.setText(userPatient.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User_Patient userPatient = snapshot.getValue(User_Patient.class);
                    if (userPatient != null) {
                        Glide.with(Objects.requireNonNull(getContext())).load(userPatient.getImageUrl()).into(imageProfile);
                        textUsername.setText(userPatient.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void addNotificationHeartBeat(String userId, @NotNull Vitals vitals) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notification").child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.USER_ID, userId);
        hashMap.put(Constants.TEXT_ISSUE_NAME, "The patient's heartbeat is " + vitals.getHeartBeat());
        hashMap.put(Constants.TEXT_ISSUE, vitals.getHeartBeat());
        hashMap.put(Constants.IS_HEARTBEAT, true);
        hashMap.put(Constants.IS_BLOOD_PRESSURE, false);
        hashMap.put(Constants.IS_BODY_TEMPERATURE, true);
        hashMap.put(Constants.IS_BLOOD_OXYGEN, false);

        reference.push().setValue(hashMap);
    }

    private void addNotificationBloodOxygen(String userId, Vitals vitals) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notification").child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.USER_ID, userId);
        hashMap.put(Constants.TEXT_ISSUE_NAME, "The patient's Blood Oxygen is " + vitals.getBloodOxygen());
        hashMap.put(Constants.TEXT_ISSUE, vitals.getBloodOxygen());
        hashMap.put(Constants.IS_HEARTBEAT, false);
        hashMap.put(Constants.IS_BLOOD_PRESSURE, false);
        hashMap.put(Constants.IS_BODY_TEMPERATURE, false);
        hashMap.put(Constants.IS_BLOOD_OXYGEN, true);

        reference.push().setValue(hashMap);
    }

    private void addNotificationBodyTemperature(String userId, Vitals vitals) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notification").child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.USER_ID, userId);
        hashMap.put(Constants.TEXT_ISSUE_NAME, "The patient's Body Temperature is " + vitals.getBodyTemperature());
        hashMap.put(Constants.TEXT_ISSUE, vitals.getBodyTemperature());
        hashMap.put(Constants.IS_HEARTBEAT, false);
        hashMap.put(Constants.IS_BLOOD_PRESSURE, false);
        hashMap.put(Constants.IS_BODY_TEMPERATURE, true);
        hashMap.put(Constants.IS_BLOOD_OXYGEN, false);

        reference.push().setValue(hashMap);
    }

    private void addNotificationBloodPressure(String userId, Vitals vitals) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notification").child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.USER_ID, userId);
        hashMap.put(Constants.TEXT_ISSUE_NAME, "The patient's Blood Pressure is " + vitals.getBloodPressure());
        hashMap.put(Constants.TEXT_ISSUE, vitals.getBloodPressure());
        hashMap.put(Constants.IS_HEARTBEAT, false);
        hashMap.put(Constants.IS_BLOOD_PRESSURE, true);
        hashMap.put(Constants.IS_BODY_TEMPERATURE, false);
        hashMap.put(Constants.IS_BLOOD_OXYGEN, false);

        reference.push().setValue(hashMap);
    }

    public void getTips() {

        tipsViewModel.getTips().observe(getViewLifecycleOwner(), tips -> {
            progressBar.setVisibility(View.GONE);
            if (tips != null) {
                if (tips.getContent() != null) {
                    textTips.setText(tips.getContent());
                }
            }
        });


    }

}