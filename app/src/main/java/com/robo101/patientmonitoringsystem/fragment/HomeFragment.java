package com.robo101.patientmonitoringsystem.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.robo101.patientmonitoringsystem.model.User;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.viewmodel.TipsViewModel;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private ImageView imageProfile;
    private FirebaseUser firebaseUser;
    private TextView textUsername;
    private TextView textTips;

    private TipsViewModel tipsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {

        textUsername = view.findViewById(R.id.textUsername);
        imageProfile = view.findViewById(R.id.imageProfileHome);
        textTips = view.findViewById(R.id.textHealthTips);
        tipsViewModel = new ViewModelProvider(this).get(TipsViewModel.class);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getUserInfo();
        getTips();
    }

    private void getUserInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Glide.with(Objects.requireNonNull(getContext())).load(user.getImageUrl()).into(imageProfile);
                    textUsername.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getTips() {
        tipsViewModel.getTips().observe(getViewLifecycleOwner(), tipsResponse -> textTips.setText(tipsResponse.getTips().getTips()));
    }

}