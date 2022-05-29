package com.robo101.patientmonitoringsystem.activity.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.adapter.ChatAdapter;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.model.Chat;
import com.robo101.patientmonitoringsystem.model.User_Patient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ImageView imageProfile;
    private TextView textUsername;
    private RecyclerView recyclerView;
    private EditText messageEdittext;
    private FloatingActionButton floatingActionButton;

    private ChatAdapter chatAdapter;
    private List<Chat> list;

    private String userId;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initialize();
    }

    private void initialize() {

        imageProfile = findViewById(R.id.imageProfileChat);
        textUsername = findViewById(R.id.textUsernameChat);
        recyclerView = findViewById(R.id.recyclerViewChat);
        messageEdittext = findViewById(R.id.messageEdittext);
        floatingActionButton = findViewById(R.id.sendButton);

        userId = getIntent().getStringExtra(Constants.USER_ID);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        floatingActionButton.setOnClickListener(v -> {

            String message = messageEdittext.getText().toString();
            if (message.equals("")) {
                Toast.makeText(ChatActivity.this, "You cannot send an empty message!", Toast.LENGTH_SHORT).show();
            } else {
                sendMessage(firebaseUser.getUid(), userId, message);
            }
            messageEdittext.setText("");

        });

        recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        getUserInfo();

    }

    private void getUserInfo() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Patients").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User_Patient userPatient = snapshot.getValue(User_Patient.class);
                Glide.with(ChatActivity.this).load(userPatient.getImageUrl()).into(imageProfile);
                textUsername.setText(userPatient.getName());

                readMessage(firebaseUser.getUid(), userId, userPatient.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
   
            }
        });

    }

    private void sendMessage(String sender, String receiver, String message) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.SENDER, sender);
        hashMap.put(Constants.RECEIVER, receiver);
        hashMap.put(Constants.MESSAGE, message);

        databaseReference.push().setValue(hashMap);

    }

    private void readMessage(String id, String userId, String imageUrl) {

        list = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat != null && (chat.getReceiver().equals(id) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(id))) {
                        list.add(chat);
                    }

                    chatAdapter = new ChatAdapter(getApplicationContext(), list, imageUrl);
                    recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}