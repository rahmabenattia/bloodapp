package com.example.bloodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.widget.Toast;

public class RendezvousActivity<Rendezvous> extends AppCompatActivity {
    TextView tv_date;
    EditText et_date, et_time;
    int tHour, tMinute;
    private Button buttonsave;
    private DatabaseReference userReference;
    private FirebaseDatabase userDB;
    private FirebaseAuth mAuth;
    private List<Rendezvoushis> rendezvoushisList;
    DatePickerDialog.OnDateSetListener setListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rendezvous);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference("users/" + userId + "/appointement");

        getSupportActionBar().setTitle("Appointment");

        rendezvoushisList = new ArrayList<>();
        buttonsave = findViewById(R.id.buttonsave);
        et_date = findViewById(R.id.et_date);
        et_time = findViewById(R.id.et_time);
        buttonsave = findViewById(R.id.buttonsave);
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RendezvousActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        buttonsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRendezvous();
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_rendezvous);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_Location:
                    startActivity(new Intent(getApplicationContext(), LocationActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_support:
                    startActivity(new Intent(getApplicationContext(), SupportActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                    return true;
                case R.id.bottom_rendezvous:
                    return true;
                case R.id.bottom_profile:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
            }
            return false;
        });
        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                et_date.setText(date);
            }
        };
        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(RendezvousActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tHour = hourOfDay;
                        tMinute = minute;
                        String time = tHour + ":" + tMinute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);
                            SimpleDateFormat f12Hours = new SimpleDateFormat("HH:mm aa");
                            et_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }, 12, 0, false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(tHour, tMinute);
                timePickerDialog.show();

            }
        });

    }

    private void saveRendezvous() {
        String date = et_date.getText().toString().trim();
        String time = et_time.getText().toString().trim();
        if (TextUtils.isEmpty(date)) {
            et_date.setError("date is required.");
            return;
        }
        if (TextUtils.isEmpty(time)) {
            et_time.setError("date is required.");
            return;
        }
        Rendezvous rendezvous = (Rendezvous) new com.example.bloodapp.Rendezvous(date, time);
        HashMap<String, String> request = new HashMap<>();
        request.put("date", date);
        request.put("time", time);
        request.put("status", "waiting");
        String userEmail = mAuth.getCurrentUser().getEmail();
        request.put("email", userEmail);

        String userId = mAuth.getCurrentUser().getUid();

        userDB = FirebaseDatabase.getInstance();
        userReference = userDB.getReference("Appointment");
        userReference.child(userId).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(RendezvousActivity.this, "Rendezvous information saved.", Toast.LENGTH_LONG).show();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RendezvousActivity.this, "Error saving rendezvous information.", Toast.LENGTH_LONG).show();
            }
        });
    }

}