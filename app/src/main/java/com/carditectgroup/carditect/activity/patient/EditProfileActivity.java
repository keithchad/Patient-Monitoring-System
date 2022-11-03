package com.carditectgroup.carditect.activity.patient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.carditectgroup.carditect.R;
import com.carditectgroup.carditect.constants.Constants;
import com.carditectgroup.carditect.model.User_Patient;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imageClose;
    private ImageView imageProfile;

    private TextView textSave;
    private TextView textChangePhoto;

    private ProgressBar progressBar;

    private TextInputEditText edittextBirthdayEditProfile;
    private TextInputEditText edittextFullname;
    private TextInputEditText edittextAgeEditProfile;

    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private UploadTask uploadTask;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_patient);
        initialize();
    }

    private void initialize() {
        imageClose = findViewById(R.id.imageClose);
        imageProfile = findViewById(R.id.imageProfileEditProfile);
        textSave = findViewById(R.id.textSave);
        textChangePhoto = findViewById(R.id.textChange);
        edittextFullname = findViewById(R.id.edittextFullNameEditProfile);
        edittextAgeEditProfile = findViewById(R.id.edittextAgeEditProfile);
        edittextBirthdayEditProfile = findViewById(R.id.edittextBirthdayEditProfile);
        progressBar = findViewById(R.id.editProfileProgressBar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");

        imageClose.setOnClickListener(v -> finish());

        textChangePhoto.setOnClickListener(v -> CropImage.activity()
                .setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(EditProfileActivity.this));

        imageProfile.setOnClickListener(v -> CropImage.activity()
                .setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(EditProfileActivity.this));

        textSave.setOnClickListener(v ->
                updateProfile(edittextFullname.getText().toString(),
                        edittextBirthdayEditProfile.getText().toString(),
                        edittextAgeEditProfile.getText().toString()));
        getUserInfo();
    }

    private void getUserInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User_Patient userPatient = snapshot.getValue(User_Patient.class);
                if (userPatient != null) {

                    edittextFullname.setText(userPatient.getName());
                    edittextBirthdayEditProfile.setText(userPatient.getBirthday());
                    edittextAgeEditProfile.setText(userPatient.getAge());
                    Glide.with(getApplicationContext()).load(userPatient.getImageUrl()).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateProfile(String fullname, String birthday, String age) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.NAME, fullname);
        hashMap.put(Constants.BIRTHDAY, birthday);
        hashMap.put(Constants.AGE, age);

        reference.updateChildren(hashMap);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        progressBar.setVisibility(View.VISIBLE);
        textSave.setVisibility(View.GONE);

        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask((Continuation) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String url = downloadUri.toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance()
                            .getReference("Users").child(firebaseUser.getUid());

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(Constants.IMAGE_URL, ""+url);

                    reference.updateChildren(hashMap);

                    progressBar.setVisibility(View.GONE);
                    textSave.setVisibility(View.VISIBLE);

                    startActivity(new Intent(EditProfileActivity.this, MainActivityPatient.class));
                    finish();
                } else {
                    progressBar.setVisibility(View.GONE);
                    textSave.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                textSave.setVisibility(View.VISIBLE);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }else {
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                imageUri = result.getUri();
            }
            uploadImage();
        } else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }


}