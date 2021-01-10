package com.robo101.patientmonitoringsystem.fragment;

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
import com.robo101.patientmonitoringsystem.activity.EditProfileActivity;
import com.robo101.patientmonitoringsystem.activity.MainActivity;
import com.robo101.patientmonitoringsystem.model.User;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.utils.PreferenceManager;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private TextView textUsername;
    private TextView textUserNameTop;
    private TextView textBirthday;
    private TextView textGender;
    private TextView textAge;
    private TextView textNumber;
    private ImageView imageProfile;

    private PreferenceManager preferenceManager;
    private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_, container, false);
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

        MaterialButton buttonSignOut = view.findViewById(R.id.buttonSignOut);
        MaterialButton buttonChangeProfileDetails = view.findViewById(R.id.buttonChangeProfileDetails);
        preferenceManager = new PreferenceManager(Objects.requireNonNull(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        buttonChangeProfileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        buttonSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            preferenceManager.clearPreferences();
        });
        getUserInfo();
    }

    private void getUserInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    textUsername.setText(user.getName());
                    textUserNameTop.setText(user.getName());
                    textBirthday.setText(user.getBirthday());
                    textGender.setText(user.getGender());
                    textAge.setText(user.getAge());
                    textNumber.setText(user.getPhoneNumber());
                    Glide.with(Objects.requireNonNull(getContext())).load(user.getImageUrl()).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}