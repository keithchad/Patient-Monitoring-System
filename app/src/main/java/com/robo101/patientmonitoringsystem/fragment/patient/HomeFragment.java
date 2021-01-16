package com.robo101.patientmonitoringsystem.fragment.patient;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.model.User_Patient;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.viewmodel.TipsViewModel;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private ImageView imageProfile;
    private FirebaseUser firebaseUser;
    private TextView textUsername;
    private TextView textTips;

    private ConstraintLayout layoutTips;

    private String patientId;

    private TipsViewModel tipsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_patient, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {

        textUsername = view.findViewById(R.id.textUsername);
        imageProfile = view.findViewById(R.id.imageProfileHome);
        textTips = view.findViewById(R.id.textHealthTips);
        layoutTips = view.findViewById(R.id.layoutTips);
        ImageView imageRefresh = view.findViewById(R.id.imageRefresh);

        tipsViewModel = new ViewModelProvider(this).get(TipsViewModel.class);
        patientId = getActivity().getIntent().getStringExtra(Constants.USER_ID);

        imageRefresh.setOnClickListener(v -> getTips());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getUserInfo();
        getTips();
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
        tipsViewModel.getTips().observe(getViewLifecycleOwner(), tipsResponse -> textTips.setText(tipsResponse.getTips().getTips()));
    }

}