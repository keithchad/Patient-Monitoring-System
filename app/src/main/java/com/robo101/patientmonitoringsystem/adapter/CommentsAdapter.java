package com.robo101.patientmonitoringsystem.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.model.Comment;
import com.robo101.patientmonitoringsystem.model.User_Doctor;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private final Context context;
    private final List<Comment> list;

    private FirebaseUser firebaseUser;

    public CommentsAdapter(Context context, List<Comment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = list.get(position);

        holder.textComment.setText(comment.getComment());
        getUserInfo(holder.imageProfile, holder.textUserName, comment.getPublisher());

//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, MainActivity.class);
//            intent.putExtra(Constants.PUBLISHER_ID, comment.getPublisher());
//            context.startActivity(intent);
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageProfile;
        private TextView textUserName;
        private TextView textComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textComment = itemView.findViewById(R.id.textCommentView);
            textUserName = itemView.findViewById(R.id.textUsernameComment);
            imageProfile = itemView.findViewById(R.id.imageProfileComment);

        }
    }

    private void getUserInfo(ImageView imageView, TextView textView, String publisherId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Doctor").child(publisherId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User_Doctor user = snapshot.getValue(User_Doctor.class);
                if (user != null) {
                    Glide.with(context).load(user.getImageUrl()).into(imageView);
                    textView.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
