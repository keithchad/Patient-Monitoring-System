package com.robo101.patientmonitoringsystem.calls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.api.messageapi.APIClient;
import com.robo101.patientmonitoringsystem.api.messageapi.APIService;
import com.robo101.patientmonitoringsystem.constants.Constants;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitationActivity extends AppCompatActivity {

    ImageView imageMeetingType;
    ImageView imageStopInvitation;
    TextView textFirstChar;
    TextView textUsername;
    TextView textEmail;

    private String inviterToken = null;
    private String meetingRoom = null;
    private String meetingType = null;
    private String userName = null;
    private String userId = null;
    private String userToken = null;

    private int rejectionCount = 0;
    private  int totalReceivers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);

        setBarColors();
        initialize();
    }

    private void initialize() {
        imageMeetingType = findViewById(R.id.imageMeetingType);
        imageStopInvitation = findViewById(R.id.imageStopInvitation);
        textFirstChar = findViewById(R.id.textFirstChar);
        textUsername = findViewById(R.id.textUsername);
        textEmail = findViewById(R.id.textEmail);

        meetingType = getIntent().getStringExtra("type");

        userName = getIntent().getStringExtra(Constants.NAME);
        userToken = getIntent().getStringExtra(Constants.FCM_TOKEN);
        userId = getIntent().getStringExtra(Constants.USER_ID);

        textFirstChar.setText(userName.substring(0, 1));
        textUsername.setText(userName);

        if(meetingType != null) {
            if(meetingType.equals("video")) {
                imageMeetingType.setImageResource(R.drawable.ic_videocam);
            }else {
                imageMeetingType.setImageResource(R.drawable.ic_call);
            }
        }

        imageStopInvitation.setOnClickListener(v -> {
            cancelInvitation(userToken);
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null) {
                inviterToken = task.getResult().getToken();

                if (meetingType != null) {
                   totalReceivers = 1;
                   initiateMeeting(meetingType, userToken);
                }
            }
        });

    }

    private void initiateMeeting(String meetingType, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();

            if(receiverToken != null) {
                tokens.put(receiverToken);
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(Constants.NAME, userName);
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);

            meetingRoom = userId + "_" +
                    UUID.randomUUID().toString().substring(0, 5);

            data.put(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);

        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void sendRemoteMessage(String remoteMessageBody, String type) {
        APIClient.getClient().create(APIService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (type.equals(Constants.REMOTE_MSG_INVITATION)){
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitation Sent Successfully", Toast.LENGTH_SHORT).show();
                    }else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitation Cancelled", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else {
                    Toast.makeText(OutgoingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(OutgoingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void cancelInvitation(String receiverToken) {
        try {

            JSONArray tokens = new JSONArray();

            if (receiverToken != null) {
                tokens.put(receiverToken);
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_RESPONSE);

        }catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private final BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null) {
                if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {

                    try {
                        URL severURL = new URL("https://meet.jit.si");

                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(severURL);
                        builder.setWelcomePageEnabled(false);
                        builder.setRoom(meetingRoom);

                        if(meetingType.equals("audio")) {
                            builder.setVideoMuted(true);
                        }

                        JitsiMeetActivity.launch(OutgoingInvitationActivity.this, builder.build());
                        finish();

                    } catch (MalformedURLException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    Toast.makeText(context, "Invitation Accepted", Toast.LENGTH_SHORT).show();
                }else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)) {
                    rejectionCount += 1;
                    if (rejectionCount == totalReceivers) {
                        Toast.makeText(context, "Invitation Rejected", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }

    private void setBarColors() {
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorMeetingInvitationEnd));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorMeetingInvitationStart));
    }
}