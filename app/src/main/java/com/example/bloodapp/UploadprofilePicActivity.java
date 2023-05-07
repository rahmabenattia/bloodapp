package com.example.bloodapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadprofilePicActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView imageviewUploadpic;
    private FirebaseAuth authProfile;
    private StorageReference StorageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST =1;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadprofile_pic2);



        Button buttonUploadPicChoose = findViewById(R.id.upload_pic_choose_button);
        Button buttonUploadPic = findViewById(R.id.upload_pic_button);
        progressBar = findViewById(R.id.progressBar);
        imageviewUploadpic = findViewById(R.id.imageview_profile_dp);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        StorageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

        Uri uri = firebaseUser.getPhotoUrl();
        Picasso.get().load(uri).into(imageviewUploadpic);
       buttonUploadPicChoose.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               openFileChoser();
           }
       });
        buttonUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                UploadPic();
            }
        });

    }

    private void openFileChoser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null){
            uriImage = data.getData();
            imageviewUploadpic.setImageURI(uriImage);
        }
    }
    private void UploadPic(){
        if (uriImage != null){
            StorageReference fileReference = StorageReference.child(authProfile.getCurrentUser().getUid() + "."
                    + getFileExtension(uriImage));

            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = authProfile.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);
                        }
                    });

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadprofilePicActivity.this, "Upload Successful !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UploadprofilePicActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadprofilePicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(UploadprofilePicActivity.this, "No File Selected !", Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(UploadprofilePicActivity.this, UpdateprofileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UploadprofilePicActivity.this, UpdateemailActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_settings) {
            Toast.makeText(this, "menu_Settings", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.meni_change_password) {
            Intent intent = new Intent(UploadprofilePicActivity.this, ChangepasswordActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UploadprofilePicActivity.this, DeleteprofileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UploadprofilePicActivity.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}