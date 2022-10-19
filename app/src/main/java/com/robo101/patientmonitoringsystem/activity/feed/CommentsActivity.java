package com.robo101.patientmonitoringsystem.activity.feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.adapter.CommentsAdapter;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.model.Comment;
import com.robo101.patientmonitoringsystem.model.User_Doctor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity {

    private TextInputEditText edittextComment;
    private ImageView imageProfile;

    private String postId;

    private CommentsAdapter commentsAdapter;
    private List<Comment> list;

    private String publisherId;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        initialize();
    }

    private void initialize() {

        ImageView imageClose = findViewById(R.id.imageClose);
        edittextComment = findViewById(R.id.edittextComment);
        imageProfile = findViewById(R.id.imageProfileComments);
        TextInputLayout textInputLayout = findViewById(R.id.createTextInputLayoutComment);
        RecyclerView recyclerView = findViewById(R.id.commentsRecyclerView);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        list = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(commentsAdapter);

        Intent intent = getIntent();
        postId = intent.getStringExtra(Constants.POST_ID);
        publisherId = intent.getStringExtra(Constants.PUBLISHER_ID);

        imageClose.setOnClickListener(v -> finish());
        textInputLayout.setEndIconCheckable(true);
        textInputLayout.setEndIconOnClickListener(v -> {

            if (Objects.requireNonNull(edittextComment.getText()).toString().equals("")) {
                Toast.makeText(CommentsActivity.this, "You can't post empty comment!", Toast.LENGTH_SHORT).show();
            } else {
                addComment();
            }

        });

        getPublisherImage();
        readComments();
    }

    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.COMMENT, Objects.requireNonNull(edittextComment.getText()).toString());
        hashMap.put(Constants.PUBLISHER, firebaseUser.getUid());

        reference.push().setValue(hashMap);
        edittextComment.setText("");
    }


    private void getPublisherImage() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Doctor").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User_Doctor user = snapshot.getValue(User_Doctor.class);
                if (user != null) {
                    Glide.with(CommentsActivity.this).load(user.getImageUrl()).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    list.add(comment);
                }
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}