package com.robo101.patientmonitoringsystem.calls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.api.messageapi.APIClient;
import com.robo101.patientmonitoringsystem.api.messageapi.APIService;
import com.robo101.patientmonitoringsystem.constants.Constants;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingInvitationActivity extends AppCompatActivity {

    String meetingType = null;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_invitation);
        initialize();
    }

    private void initialize() {
        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        ImageView imageAcceptInvitation = findViewById(R.id.imageAcceptInvitation);
        ImageView imageRejectInvitation = findViewById(R.id.imageRejectInvitation);

        setupRingtone();

        ImageView imageProfile = findViewById(R.id.imageProfileIncoming);
        TextView textUsername = findViewById(R.id.textUsername );

        meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);

        String username = getIntent().getStringExtra(Constants.NAME);
        String imageURL = getIntent().getStringExtra(Constants.IMAGE_URL);

        textUsername.setText(username);
        Glide.with(getApplicationContext()).load(imageURL).into(imageProfile);

        if (meetingType != null) {
            if (meetingType.equals("video")) {
                imageMeetingType.setImageResource(R.drawable.ic_videocam);
            }else {
                imageMeetingType.setImageResource(R.drawable.ic_call);
            }
        }

        imageAcceptInvitation.setOnClickListener(v -> {

            sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED, getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
            mediaPlayer.stop();
            mediaPlayer.release();

        });

        imageRejectInvitation.setOnClickListener(v -> {

            sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED, getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
            mediaPlayer.stop();
            mediaPlayer.release();

        });
    }

    private void setupRingtone() {
        mediaPlayer = MediaPlayer.create(this, R.raw.iphone_ringtone);
        mediaPlayer.start();
    }

    private void sendInvitationResponse(String type, String receiverToken) {
        try {

            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), type);

        }catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void sendRemoteMessage(String remoteMessageBody, String type) {
        APIClient.getClient().create(APIService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                  if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {

                      try {

                          URL severURL = new URL("https://meet.jit.si");

                          JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                          builder.setServerURL(severURL);
                          builder.setWelcomePageEnabled(false);
                          builder.setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM));

                          if(meetingType.equals("audio")) {
                              builder.setVideoMuted(true);
                          }
                          JitsiMeetActivity.launch(IncomingInvitationActivity.this, builder.build());
                          finish();
                      }catch (Exception e) {
                          Toast.makeText(IncomingInvitationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                          finish();
                      }

                  }else {
                      Toast.makeText(IncomingInvitationActivity.this, "Invitation Rejected", Toast.LENGTH_SHORT).show();
                      finish();
                  }
                }else {
                    Toast.makeText(IncomingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(IncomingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null) {
                if(type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {
                    Toast.makeText(context, "Invitation Cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                broadcastReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                broadcastReceiver
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }
}