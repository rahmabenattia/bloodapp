package com.example.bloodapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        getSupportActionBar().setTitle("bloodapp");
        Button buttonadmin = findViewById(R.id.button_admin);
        buttonadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity1.this, AdminloginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button buttonUser = findViewById(R.id.button_user);
        buttonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity1.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
}