package com.robo101.patientmonitoringsystem.adapter;

import android.annotation.SuppressLint;
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
import com.robo101.patientmonitoringsystem.activity.feed.CommentsActivity;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.model.Post;
import com.robo101.patientmonitoringsystem.model.User_Doctor;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context context;
    private final List<Post> list;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Post post = list.get(position);
        Glide.with(context).load(post.getPostImage()).into(holder.postImage);

        if (post.getCaption().equals("")) {
            holder.textCaption.setVisibility(View.GONE);
        } else {
            holder.textCaption.setVisibility(View.VISIBLE);
            holder.textCaption.setText(post.getCaption());
        }

        publisherInfo(holder.profileImage, holder.textUsername, post.getPublisherId());
        isLiked(post.getPostId(), holder.imageLike);
        numberOfLikes(holder.textLikes, post.getPostId());
        getComments(post.getPostId(), holder.textComment);


        holder.imageLike.setOnClickListener(v -> {
            if (holder.imageLike.getTag().equals("Like")) {
                FirebaseDatabase.getInstance().getReference().child("Likes")
                        .child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Likes")
                        .child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
            }
        });

        holder.imageComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra(Constants.POST_ID, post.getPostId());
            intent.putExtra(Constants.PUBLISHER_ID, post.getPublisherId());
            context.startActivity(intent);
        });

        holder.textComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra(Constants.POST_ID, post.getPostId());
            intent.putExtra(Constants.PUBLISHER_ID, post.getPublisherId());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView profileImage;
        private final ImageView postImage;
        private final ImageView imageLike;
        private final ImageView imageComment;
        private final TextView textUsername;
        private final TextView textCaption;
        private final TextView textComment;
        private final TextView textLikes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            postImage = itemView.findViewById(R.id.imagePost);
            imageLike = itemView.findViewById(R.id.imageLike);
            imageComment = itemView.findViewById(R.id.imageComment);
            textUsername = itemView.findViewById(R.id.textUsernamePost);
            textCaption = itemView.findViewById(R.id.textCaption);
            textComment = itemView.findViewById(R.id.textComments);
            textLikes = itemView.findViewById(R.id.textLike);
        }
    }

    private void getComments(String postId, TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText(snapshot.getChildrenCount() + " Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isLiked(String postId, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firebaseUser != null && snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("Liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void numberOfLikes(TextView likes, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void publisherInfo(ImageView imageProfile, TextView userName, String userId ) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Doctor").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User_Doctor user = snapshot.getValue(User_Doctor.class);
                if (user != null) {
                    Glide.with(context).load(user.getImageUrl()).into(imageProfile);
                    userName.setText(user.getName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
}
