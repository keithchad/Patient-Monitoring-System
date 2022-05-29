package com.robo101.patientmonitoringsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.model.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {


    private static final int MESSAGE_TYPE_LEFT = 0;
    private static final int MESSAGE_TYPE_RIGHT = 1;

    private final Context context;
    private final List<Chat> list;
    private final String imageUrl;

    public ChatAdapter(Context context, List<Chat> list, String imageUrl) {
        this.context = context;
        this.list = list;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MESSAGE_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = list.get(position);

        holder.textMessage.setText(chat.getMessage());
        Glide.with(context).load(imageUrl).into(holder.imageProfile);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageProfile;
        private final TextView textMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfileChatItem);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }

    @Override
    public int getItemViewType(int position) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (list.get(position).getSender().equals(firebaseUser.getUid())) {
            return MESSAGE_TYPE_RIGHT;
        } else {
            return MESSAGE_TYPE_LEFT;
        }

    }
}
