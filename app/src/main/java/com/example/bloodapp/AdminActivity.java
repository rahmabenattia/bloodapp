package com.example.bloodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Iterator;

public class AdminActivity extends AppCompatActivity {
    private TextView request1, request2, request3, request4, request5;
    private Button acceptButton, refuseButton;
    private FirebaseAuth userAuth;
    private FirebaseDatabase userDB;
    private DatabaseReference reference;
    private String[] donor;
    private int key;
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home , profile2 ,  logout;
    int nbreReq=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        request1 = findViewById(R.id.request1);
        request2 = findViewById(R.id.request2);
        request3 = findViewById(R.id.request3);
        request4 = findViewById(R.id.request4);
        request5 = findViewById(R.id.request5);

        request1.setOnClickListener(this::requestClick);
        request2.setOnClickListener(this::requestClick);
        request3.setOnClickListener(this::requestClick);
        request4.setOnClickListener(this::requestClick);
        request5.setOnClickListener(this::requestClick);

        TextView[] fields = {request1, request2, request3, request4, request5};
        acceptButton = findViewById(R.id.acceptButton);
        refuseButton = findViewById(R.id.refuseButton);

        acceptButton.setOnClickListener(this::requestHandler);
        refuseButton.setOnClickListener(this::requestHandler);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        profile2 = findViewById(R.id.profil2);

        logout = findViewById(R.id.logout);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        profile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(AdminActivity.this, profile2Activity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminActivity.this, "logout", Toast.LENGTH_SHORT).show();
                redirectActivity(AdminActivity.this,AdminloginActivity.class);
            }
        });

    }
    public static  void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public static void closeDrawer(DrawerLayout drawerLayout){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    public void refreshRequests(View view) {
        getRequests();
    }
    public void getRequests(){
        TextView[] fields = {request1, request2, request3, request4, request5};
        userAuth = FirebaseAuth.getInstance();
        userDB = FirebaseDatabase.getInstance();
        reference = userDB.getReference("Appointment");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()){
                    DataSnapshot data = task.getResult();
                    long iter = data.getChildrenCount();
                    int iteration = (int) iter;
                    //Log.d("iteration",String.valueOf(iteration));
                    donor = new String[iteration];
                    nbreReq=0;
                    final Iterator<DataSnapshot> iterator = data.getChildren().iterator();

                    for(int i = 0; i < iteration; i++){

                        Log.d("nbreReq1 ",""+nbreReq);
                        donor[i] = iterator.next().getKey();
                        reference.child(donor[i]).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.getResult().exists()){
                                    DataSnapshot dataSnapshot = task.getResult();
                                    Log.d("nbreReq2 ",""+nbreReq);
                                   /* Log.v("count ",""+dataSnapshot.getChildrenCount());
                                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                        Log.v("keys ",""+ childDataSnapshot.getKey()); //displays the key for the node
                                       // Log.v(TAG,""+ childDataSnapshot.child(--ENTER THE KEY NAME eg. firstname or email etc.--).getValue());   //gives the value for given keyname
                                        if(String.valueOf(dataSnapshot.child("status").getValue()).equals("waiting")) {
                                            Log.v("keys ","try");
                                            nbreReq++;+
                                        }
                                    }*/
                                    Log.d("email ",""+dataSnapshot.child("email").getValue());
                                    Log.d("status  ",""+dataSnapshot.child("status").getValue());
                                    if(String.valueOf(dataSnapshot.child("status").getValue()).equals("waiting")
                                    || String.valueOf(dataSnapshot.child("status").getValue()).equals("accepted")){
                                        String date = String.valueOf(dataSnapshot.child("date").getValue());
                                        String time = String.valueOf(dataSnapshot.child("time").getValue());
                                        String status = String.valueOf(dataSnapshot.child("status").getValue());
                                        String email = String.valueOf(dataSnapshot.child("email").getValue());

                                        Log.d("requests ","Request " + nbreReq + " : "+ email + " , " + date+" , " + time+" , " + status);
                                        fields[nbreReq].setText("Request " + nbreReq + " : "+ email + " , " + date+" , " + time+" , " + status);

                                        /* for(int j = 0; j < nbreReq; j++){
                                            //if(fields[j].getText().toString().isEmpty()){
                                                fields[j].setText("Request " + j + " : "+ date + " , " + time);
                                            /*}else{
                                                j++;
                                            }
                                        }*/
                                    }
                                    nbreReq++;
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    public void requestClick(View view){
        switch(view.getId()){
            case R.id.request1:
                acceptButton.setVisibility(View.VISIBLE);
                refuseButton.setVisibility(View.VISIBLE);
                key = 0;
                break;
            case R.id.request2:
                acceptButton.setVisibility(View.VISIBLE);
                refuseButton.setVisibility(View.VISIBLE);
                key = 1;
                break;
            case R.id.request3:
                acceptButton.setVisibility(View.VISIBLE);
                refuseButton.setVisibility(View.VISIBLE);
                key = 2;
                break;
            case R.id.request4:
                acceptButton.setVisibility(View.VISIBLE);
                refuseButton.setVisibility(View.VISIBLE);
                key = 3;
                break;
            case R.id.request5:
                acceptButton.setVisibility(View.VISIBLE);
                refuseButton.setVisibility(View.VISIBLE);
                key = 4;
                break;
        }
    }
    public void requestHandler(View view){
        TextView[] fields = {request1, request2, request3, request4, request5};
        switch(view.getId()){
            case R.id.acceptButton:
                reference = userDB.getReference("Appointment");
                reference.child(donor[key]).child("status").setValue("accepted");
                //fields[key].setText(reference.child(donor[key]).get().getResult().child("date").getValue().toString());
                getRequests();
                break;
            case R.id.refuseButton:
                reference = userDB.getReference("Appointment");
                reference.child(donor[key]).child("status").setValue("refused");
                fields[key].setText("");
                getRequests();
                break;
        }
    }

}