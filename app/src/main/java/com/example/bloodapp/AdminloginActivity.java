package com.example.bloodapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AdminloginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Create the "admins" node in the Firebase database
        DatabaseReference adminsRef = firebaseDatabase.getReference().child("admins");

        // Set the key for the new admin
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Set the email and password as the value for the child nodes
            Map<String, Object> adminValues = new HashMap<>();
            adminValues.put("email", "rahmabenattia12345@gmail.com");
            adminValues.put("password", "123456");

            // Set the child nodes for the new admin using the UID as the key
            adminsRef.child(uid).setValue(adminValues)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Admin created successfully in Realtime Database
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Admin creation failed in Realtime Database
                        }
                    });

            // Create the admin account in Authentication Database
            firebaseAuth.createUserWithEmailAndPassword("rahmabenattia12345@gmail.com", "123456")
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Admin account created successfully in Authentication Database
                            } else {
                                // Admin account creation failed in Authentication Database
                            }
                        }
                    });
        } else {
            // User is not logged in
            Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference adminsRef = firebaseDatabase.getReference().child("admins");


                                    adminsRef.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override

                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                // User is an admin
                                                Intent intent = new Intent(AdminloginActivity.this, AdminActivity.class);
                                                startActivity(intent);
                                            } else {
                                                // User is not an admin
                                                Toast.makeText(AdminloginActivity.this, "You do not have permission to access this area.", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle errors
                                        }
                                    });
                                } else {
                                    Toast.makeText(AdminloginActivity.this, "Login failed. Please check your email and password.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
