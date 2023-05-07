package com.example.bloodapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationsActivity extends AppCompatActivity {

    private TextView titleText;
    private TextView bodyText;
    private DatabaseReference userReference;
    private FirebaseAuth mAuth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference("notification"  );
        titleText = findViewById(R.id.titleText);
        bodyText = findViewById(R.id.bodyText);

        // Vérifie si l'activité a été lancée à partir d'une notification FCM
        if (getIntent().getExtras() != null) {
            String title = getIntent().getStringExtra("title");
            String text = getIntent().getStringExtra("text");

            // Affiche le titre et le corps de la notification dans les TextViews
            titleText.setText(title);
            bodyText.setText(text);
        }
    }
}