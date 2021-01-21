package com.robo101.patientmonitoringsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.model.Notification;
import com.robo101.patientmonitoringsystem.model.User_Patient;
import com.robo101.patientmonitoringsystem.model.Vitals;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final List<Notification> list;

    public NotificationAdapter(Context context, List<Notification> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = list.get(position);
        getUserInfo(holder.imageProfile, holder.userName, notification.getUserId());
        getIssueType(holder.textIssue, notification);
        holder.textIssueName.setText(notification.getTextIssueName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textIssueName;
        private final TextView userName;
        private final TextView textIssue;

        private final CircleImageView imageProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textIssueName = itemView.findViewById(R.id.textIssueName);
            userName = itemView.findViewById(R.id.textUserNameNotification);
            textIssue = itemView.findViewById(R.id.textIssue);
            imageProfile = itemView.findViewById(R.id.imageProfileNotification);
        }
    }

    private void getUserInfo(CircleImageView imageView, TextView userName, String userId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User_Patient userPatient = snapshot.getValue(User_Patient.class);

                if (userPatient != null) {
                    userName.setText(userPatient.getName());
                    Glide.with(context).load(userPatient.getImageUrl()).into(imageView);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIssueType(TextView textIssue, Notification notification) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Vitals");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Vitals vitals = snapshot.getValue(Vitals.class);
                if (vitals != null) {
                    if (notification.isHeartBeat()) {
                        textIssue.setText(vitals.getHeartBeat());
                    } else if (notification.isBloodOxygen()) {
                        textIssue.setText(vitals.getBloodOxygen());
                    } else if (notification.isBloodPressure()) {
                        textIssue.setText(vitals.getBloodPressure());
                    } else if(notification.isTemperature()) {
                        textIssue.setText(vitals.getBodyTemperature());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
