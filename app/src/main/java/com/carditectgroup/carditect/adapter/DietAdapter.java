package com.carditectgroup.carditect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.carditectgroup.carditect.R;
import com.carditectgroup.carditect.listeners.OnPatientClicked;
import com.carditectgroup.carditect.model.Diet;
import com.carditectgroup.carditect.model.User_Patient;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DietAdapter extends RecyclerView.Adapter<DietAdapter.ViewHolder> {

    private final Context context;
    private final List<Diet> list;

    public DietAdapter(Context context, List<Diet> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.diet_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Diet diet = list.get(position);
        Glide.with(context).load(diet.dietImage).into(holder.imageDiet);
        holder.textDietName.setText(diet.dietName);
        holder.textDietDescription.setText(diet.dietDescription);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textDietName;
        private final RoundedImageView imageDiet;
        private final TextView textDietDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDietDescription = itemView.findViewById(R.id.textDietDescription);
            imageDiet = itemView.findViewById(R.id.dietImage);
            textDietName = itemView.findViewById(R.id.textDietName);
        }
    }
}
