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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
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
import com.robo101.patientmonitoringsystem.viewmodel.TipsViewModel;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private ImageView imageProfile;
    private ImageView redDotHeart;
    private ImageView redDotOxygen;
    private ImageView redDotPressure;
    private ImageView redDotTemperature;

    private FirebaseUser firebaseUser;

    private TextView textUsername;
    private TextView textTips;
    private TextView textHeartRate;
    private TextView textBloodOxygen;
    private TextView textBloodPressure;
    private TextView textTemperature;

    private ProgressBar progressBar;

    private ConstraintLayout layoutTips;

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
        ScrollView scrollView = view.findViewById(R.id.scrollViewHome);
        ImageView imageRefresh = view.findViewById(R.id.imageRefresh);

        textHeartRate = view.findViewById(R.id.textHeartbeat);
        textBloodOxygen = view.findViewById(R.id.textBloodOxygen);
        textBloodPressure = view.findViewById(R.id.textBloodPressure);
        textTemperature = view.findViewById(R.id.textTemperature);

        redDotHeart = view.findViewById(R.id.redDotHeart);
        redDotOxygen = view.findViewById(R.id.redDotOxygen);
        redDotPressure = view.findViewById(R.id.redDotPressure);
        redDotTemperature = view.findViewById(R.id.redDotTemperature);

        tipsViewModel = new ViewModelProvider(this).get(TipsViewModel.class);
        patientId = getActivity().getIntent().getStringExtra(Constants.USER_ID);

        imageRefresh.setOnClickListener(v -> getTips());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

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
    }

    private void getVitals() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Vitals");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Vitals vitals = snapshot.getValue(Vitals.class);
                if (vitals != null) {
                    textHeartRate.setText(vitals.getHeartBeat());
                    textBloodOxygen.setText(vitals.getBloodOxygen());
                    textBloodPressure.setText(vitals.getBloodPressure());
                    textTemperature.setText(vitals.getBodyTemperature());
                }

                if (vitals.getHeartBeat() <= 60) {
                    redDotHeart.setVisibility(View.VISIBLE);
                } else if (vitals.getBloodOxygen() <= 20) {
                    redDotOxygen.setVisibility(View.VISIBLE);
                } else if (vitals.getBloodPressure() <= 120) {
                    redDotPressure.setVisibility(View.VISIBLE);
                } else if (vitals.getBodyTemperature() <= 30) {
                    redDotTemperature.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserInfo() {

        if (getActivity().getIntent().hasExtra(Constants.USER_ID)) {
            layoutTips.setVisibility(View.GONE);
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

    public void getTips() {
        tipsViewModel.getTips().observe(getViewLifecycleOwner(), tipsResponse -> {
            progressBar.setVisibility(View.GONE);
            if (tipsResponse != null) {
                if (tipsResponse.getTips() != null) {
                    textTips.setText(tipsResponse.getTips().getTips());
                }
            }
        });
    }

}