package com.example.bloodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserprofileActivity extends AppCompatActivity {

    private TextView textviewwelcome, textviewFullName, textviewemail, textviewage;
    private ProgressBar progressBar;
    private String fullName, email, age;
    private ImageView imageView;
    private FirebaseAuth authProfile;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        getSupportActionBar().setTitle("Home");
        textviewwelcome = findViewById(R.id.textview_show_welcome);
        textviewFullName = findViewById(R.id.textview_show_full_name);
        textviewemail = findViewById(R.id.textview_show_email);
        textviewage = findViewById(R.id.textview_show_age);
        progressBar = findViewById(R.id.progressBar);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(UserprofileActivity.this, "Something went wrong! User's details are not availabale at the moment", Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }


    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String UserId = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child((UserId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    age = readUserDetails.age;

                    textviewwelcome.setText("Welcome, " + fullName + "!");
                    textviewFullName.setText(fullName);
                    textviewemail.setText(email);
                    textviewage.setText(age);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserprofileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
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
             Intent intent = new Intent(UserprofileActivity.this, UpdateprofileActivity.class);
             startActivity(intent);
         } else if (id == R.id.menu_update_email) {
             Intent intent = new Intent(UserprofileActivity.this, UpdateemailActivity.class);
             startActivity(intent);
         } else if (id == R.id.menu_settings) {
             Toast.makeText(this, "menu_Settings", Toast.LENGTH_SHORT).show();
         } else if (id == R.id.meni_change_password) {
             Intent intent = new Intent(UserprofileActivity.this, ChangepasswordActivity.class);
             startActivity(intent);
         } else if (id == R.id.menu_delete_profile) {
             Intent intent = new Intent(UserprofileActivity.this, DeleteprofileActivity.class);
             startActivity(intent);
         } else if (id == R.id.menu_logout) {
             authProfile.signOut();
             Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();
             Intent intent = new Intent(UserprofileActivity.this, MainActivity.class);

             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
             startActivity(intent);
             finish();
         }else {
             Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
         }
        return super.onOptionsItemSelected(item);
    }
}

