package com.robo101.patientmonitoringsystem.fragment.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.robo101.patientmonitoringsystem.activity.SplashActivity;
import com.robo101.patientmonitoringsystem.activity.doctor.EditProfileActivityDoctor;
import com.robo101.patientmonitoringsystem.activity.patient.EditProfileActivity;
import com.robo101.patientmonitoringsystem.activity.patient.MainActivityPatient;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.model.User_Doctor;
import com.robo101.patientmonitoringsystem.model.User_Patient;
import com.robo101.patientmonitoringsystem.utils.PreferenceManager;

import java.util.Objects;

public class ProfileFragmentDoctor extends Fragment {

    private PreferenceManager preferenceManager;
    private FirebaseUser firebaseUser;

    private TextView textUsername;
    private TextView textUserNameTop;
    private TextView textEmail;
    private TextView textGender;
    private TextView textHospital;
    private TextView textSpecialization;
    private ImageView imageProfile;

    private String userId;

    private ImageView imageVideo;
    private ImageView imageCall;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_doctor, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {

        textUsername = view.findViewById(R.id.textUsernameProfileDoctor);
        textUserNameTop = view.findViewById(R.id.textUsernameTopDoctor);
        textHospital = view.findViewById(R.id.textHospital);
        textGender = view.findViewById(R.id.textGenderDoctor);
        textSpecialization = view.findViewById(R.id.textSpecializationDoctor);
        textEmail = view.findViewById(R.id.textEmailDoctor);
        imageProfile = view.findViewById(R.id.imageProfileProfileDoctor);
        imageVideo = view.findViewById(R.id.imageVideoCallDoctor);
        imageCall = view.findViewById(R.id.imageCallDoctor);

        MaterialButton buttonSignOut = view.findViewById(R.id.buttonSignOutDoctor);
        MaterialButton buttonChangeProfileDetails = view.findViewById(R.id.buttonChangeProfileDetailsDoctor);
        preferenceManager = new PreferenceManager(Objects.requireNonNull(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (preferenceManager.getString(Constants.USER_ID) != null) {
            userId = preferenceManager.getString(Constants.USER_ID);
            imageVideo.setVisibility(View.VISIBLE);
            imageCall.setVisibility(View.VISIBLE);
        }

        buttonChangeProfileDetails.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivityDoctor.class);
            startActivity(intent);
        });

        preferenceManager = new PreferenceManager(Objects.requireNonNull(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        imageVideo.setOnClickListener(v -> {
            initiateVideoMeeting();
        });

        imageCall.setOnClickListener(v -> {
            initiateCallMeeting();
        });

        buttonSignOut.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(getContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            preferenceManager.clearPreferences();
        });

        getUserInfo();
    }

    private void initiateCallMeeting() {

        Intent intent = new Intent(getContext(), MainActivityPatient.class);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User_Patient userPatient = snapshot.getValue(User_Patient.class);
                if (userPatient != null) {
                    if(userPatient.getToken() == null || userPatient.getToken().trim().isEmpty()) {
                        Toast.makeText(getContext(), userPatient.getName() + "is not Available", Toast.LENGTH_SHORT).show();
                    } else {
                        intent.putExtra(Constants.NAME, userPatient.getName());
                        intent.putExtra(Constants.FCM_TOKEN, userPatient.getToken());
                        intent.putExtra(Constants.USER_ID, userPatient.getUserId());
                        intent.putExtra("type", "audio");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        startActivity(intent);

    }

    private void initiateVideoMeeting() {

        Intent intent = new Intent(getContext(), MainActivityPatient.class);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User_Patient userPatient = snapshot.getValue(User_Patient.class);
                if (userPatient != null) {
                    if(userPatient.getToken() == null || userPatient.getToken().trim().isEmpty()) {
                        Toast.makeText(getContext(), userPatient.getName() + "is not Available", Toast.LENGTH_SHORT).show();
                    } else {
                        intent.putExtra(Constants.NAME, userPatient.getName());
                        intent.putExtra(Constants.FCM_TOKEN, userPatient.getToken());
                        intent.putExtra(Constants.USER_ID, userPatient.getUserId());
                        intent.putExtra("type", "video");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        startActivity(intent);

    }

    private void getUserInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Doctor").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User_Doctor userDoctor = snapshot.getValue(User_Doctor.class);

                if (userDoctor != null) {

                    textUsername.setText(userDoctor.getName());
                    textUserNameTop.setText(userDoctor.getName());
                    textEmail.setText(userDoctor.getEmail());
                    textGender.setText(userDoctor.getGender());
                    textHospital.setText(userDoctor.getHospital());
                    textSpecialization.setText(userDoctor.getSpecialization());
                    Glide.with(Objects.requireNonNull(getContext())).load(userDoctor.getImageUrl()).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}