package com.robo101.patientmonitoringsystem.fragment.feed;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.activity.feed.PostActivity;
import com.robo101.patientmonitoringsystem.adapter.PostAdapter;
import com.robo101.patientmonitoringsystem.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PatientFeedFragment extends Fragment {

    private List<Post> list;
    private PostAdapter postAdapter;
    private FirebaseUser firebaseUser;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_feed, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFeedPatient);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshFeedPatient);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this::readPosts);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        list = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), list);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        readPosts();

    }

    private void readPosts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Feeds");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                swipeRefreshLayout.setRefreshing(false);
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    list.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}