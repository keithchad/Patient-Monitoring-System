package com.carditectgroup.carditect.fragment.patient;

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
import com.carditectgroup.carditect.R;
import com.carditectgroup.carditect.activity.SplashActivity;
import com.carditectgroup.carditect.activity.patient.EditProfileActivity;
import com.carditectgroup.carditect.constants.Constants;
import com.carditectgroup.carditect.model.User_Patient;
import com.carditectgroup.carditect.utils.PreferenceManager;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private TextView textUsername;
    private TextView textUserNameTop;
    private TextView textBirthday;
    private TextView textGender;
    private TextView textAge;
    private TextView textNumber;
    private ImageView imageProfile;

    private ImageView imageVideo;
    private ImageView imageCall;

    private String userId;

    private PreferenceManager preferenceManager;
    private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_patient, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {
        textUsername = view.findViewById(R.id.textUsernameProfile);
        textUserNameTop = view.findViewById(R.id.textUsernameTop);
        textBirthday = view.findViewById(R.id.textBirthday);
        textGender = view.findViewById(R.id.textGender);
        textAge = view.findViewById(R.id.textAge);
        textNumber = view.findViewById(R.id.textUserNumber);
        imageProfile = view.findViewById(R.id.imageProfileProfile);
        imageVideo = view.findViewById(R.id.imageVideoCall);
        imageCall = view.findViewById(R.id.imageCall);

        preferenceManager = new PreferenceManager(Objects.requireNonNull(getContext()));
        MaterialButton buttonSignOut = view.findViewById(R.id.buttonSignOut);
        MaterialButton buttonChangeProfileDetails = view.findViewById(R.id.buttonChangeProfileDetails);

        if (preferenceManager.getString(Constants.USER_ID) != null) {
            userId = preferenceManager.getString(Constants.USER_ID);
            buttonSignOut.setVisibility(View.GONE);
            buttonChangeProfileDetails.setVisibility(View.GONE);
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        buttonChangeProfileDetails.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        });

        buttonSignOut.setOnClickListener(v -> signOut());

        getUserInfo();
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child(Constants.FCM_TOKEN);
        reference.removeValue().addOnSuccessListener(aVoid -> {

            Intent intent = new Intent(getContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            preferenceManager.clearPreferences();

        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Unable to Sign Out", Toast.LENGTH_SHORT).show());
    }

    private void getUserInfo() {

        if (userId == null) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User_Patient userPatient = snapshot.getValue(User_Patient.class);
                    if (userPatient != null) {
                        textUsername.setText(userPatient.getName());
                        textUserNameTop.setText(userPatient.getName());
                        textBirthday.setText(userPatient.getBirthday());
                        textGender.setText(userPatient.getGender());
                        textAge.setText(userPatient.getAge());
                        textNumber.setText(userPatient.getPhoneNumber());
                        Glide.with(getContext()).load(userPatient.getImageUrl()).into(imageProfile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User_Patient userPatient = snapshot.getValue(User_Patient.class);
                    if (userPatient != null) {
                        textUsername.setText(userPatient.getName());
                        textUserNameTop.setText(userPatient.getName());
                        textBirthday.setText(userPatient.getBirthday());
                        textGender.setText(userPatient.getGender());
                        textAge.setText(userPatient.getAge());
                        textNumber.setText(userPatient.getPhoneNumber());
                        Glide.with(Objects.requireNonNull(getContext())).load(userPatient.getImageUrl()).into(imageProfile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

}