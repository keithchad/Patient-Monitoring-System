package com.robo101.patientmonitoringsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.model.User_Patient;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {

    private final Context context;
    private final List<User_Patient> list;

    public DoctorAdapter(Context context, List<User_Patient> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.doctor_home_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User_Patient userPatient = list.get(position);
        holder.textFullname.setText(userPatient.getName());
        Glide.with(context).load(userPatient.getImageUrl()).into(holder.imagePatientProfile);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textFullname;
        private final CircleImageView imagePatientProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textFullname = itemView.findViewById(R.id.textFullname);
            imagePatientProfile = itemView.findViewById(R.id.patientImage);
        }
    }
}