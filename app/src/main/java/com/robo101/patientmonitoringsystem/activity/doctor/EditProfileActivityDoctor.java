package com.robo101.patientmonitoringsystem.activity.doctor;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.storage.StorageTask;
import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.activity.patient.MainActivityPatient;
import com.robo101.patientmonitoringsystem.constants.Constants;
import com.robo101.patientmonitoringsystem.model.User_Doctor;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

public class EditProfileActivityDoctor extends AppCompatActivity {

    private ImageView imageProfile;

    private TextView textSave;

    private ProgressBar progressBar;

    private TextInputEditText edittextSpecializationEditProfile;
    private TextInputEditText edittextFullname;
    private TextInputEditText edittextHospitalEditProfile;

    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_doctor);
        initialize();
    }

    private void initialize() {

        ImageView imageClose = findViewById(R.id.imageClose);
        imageProfile = findViewById(R.id.imageProfileEditProfileDoctor);
        textSave = findViewById(R.id.textSave);
        TextView textChangePhoto = findViewById(R.id.textChangeDoctor);
        edittextFullname = findViewById(R.id.edittextFullNameEditProfileDoctor);
        edittextHospitalEditProfile = findViewById(R.id.edittextHospitalEditProfile);
        edittextSpecializationEditProfile = findViewById(R.id.edittextSpecializationEditProfile);
        progressBar = findViewById(R.id.editProfileProgressBar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Doctor's Uploads");

        imageClose.setOnClickListener(v -> finish());

        textChangePhoto.setOnClickListener(v -> CropImage.activity()
                .setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(EditProfileActivityDoctor.this));

        imageProfile.setOnClickListener(v -> CropImage.activity()
                .setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(EditProfileActivityDoctor.this));

        textSave.setOnClickListener(v -> {
            if(Objects.requireNonNull(edittextFullname.getText()).toString().trim().isEmpty()) {
                Toast.makeText(EditProfileActivityDoctor.this, "Update Name", Toast.LENGTH_SHORT).show();
                return;
            }else if(Objects.requireNonNull(edittextHospitalEditProfile.getText()).toString().trim().isEmpty()) {
                Toast.makeText(EditProfileActivityDoctor.this, "Update Hospital", Toast.LENGTH_SHORT).show();
                return;
            }else if(Objects.requireNonNull(edittextSpecializationEditProfile.getText()).toString().trim().isEmpty()) {
                Toast.makeText(EditProfileActivityDoctor.this, "Update Specialization", Toast.LENGTH_SHORT).show();
                return;
            }
            updateProfile(edittextFullname.getText().toString(),
                    edittextHospitalEditProfile.getText().toString(),
                    edittextSpecializationEditProfile.getText().toString());
        });

        getUserInfo();
    }

    private void getUserInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Doctors").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User_Doctor userDoctor = snapshot.getValue(User_Doctor.class);
                if (userDoctor != null) {
                    edittextFullname.setText(userDoctor.getName());
                    edittextSpecializationEditProfile.setText(userDoctor.getSpecialization());
                    edittextHospitalEditProfile.setText(userDoctor.getHospital());
                    Glide.with(EditProfileActivityDoctor.this).load(userDoctor.getImageUrl()).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateProfile(String fullname, String hospital, String specialization) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Doctors").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.NAME, fullname);
        hashMap.put(Constants.HOSPITAL, hospital);
        hashMap.put(Constants.SPECIALIZATION, specialization);

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
            StorageTask uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask((Continuation) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String url = downloadUri.toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance()
                            .getReference("Doctors").child(firebaseUser.getUid());

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(Constants.IMAGE_URL, ""+url);

                    reference.updateChildren(hashMap);

                    progressBar.setVisibility(View.GONE);
                    textSave.setVisibility(View.VISIBLE);

                    startActivity(new Intent(EditProfileActivityDoctor.this, MainActivityPatient.class));
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