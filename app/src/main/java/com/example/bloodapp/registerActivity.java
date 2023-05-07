package com.example.bloodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registerActivity extends AppCompatActivity {

    private EditText edittextRegisterFullName,edittextRegisterEmail,edittextRegisterAge,edittextRegisterpassword;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterChronic;
    private  RadioButton oui;
    private RadioButton radioButtonRegisterChronicselected;
    private static final String TAG="registerActivity";
    private Spinner spinnerGroupesanguin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");
        Toast.makeText(this, "You can register now", Toast.LENGTH_SHORT).show();

        edittextRegisterFullName = findViewById(R.id.edittext_register_full_name);
        progressBar = findViewById(R.id.progressBar);
        edittextRegisterEmail = findViewById(R.id.edittext_register_email);
        oui = findViewById(R.id.radio_oui);
        edittextRegisterAge = findViewById(R.id.edittext_register_age);
        spinnerGroupesanguin = findViewById(R.id.spinnerGroupesanguin);
        edittextRegisterpassword = findViewById(R.id.edittext_register_password);

        radioGroupRegisterChronic = findViewById(R.id.radio_group_register_chronic);
        radioGroupRegisterChronic.clearCheck();
        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedchronicId= radioGroupRegisterChronic.getCheckedRadioButtonId();
                radioButtonRegisterChronicselected = findViewById(selectedchronicId);

                String textFullName = edittextRegisterFullName.getText().toString();
                String textEmail = edittextRegisterEmail.getText().toString();
                String textgrp = spinnerGroupesanguin.getSelectedItem().toString();
                String textPwd = edittextRegisterpassword.getText().toString();
                Integer textAge = Integer.valueOf( edittextRegisterAge.getText().toString());
                String textChronic;

                if (TextUtils.isEmpty(textFullName)){
                    Toast.makeText(registerActivity.this, "PLease enter your full name", Toast.LENGTH_LONG).show();
                    edittextRegisterFullName.setError("Full Name is required");
                    edittextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(registerActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    edittextRegisterEmail.setError("Email is required");
                    edittextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(registerActivity.this, "Please enter your re-email", Toast.LENGTH_LONG).show();
                    edittextRegisterEmail.setError(" Valid Email is required");
                    edittextRegisterEmail.requestFocus();
                } String ageString = Integer.toString(textAge);
                if (ageString.isEmpty()){
                    edittextRegisterAge.setError("Age is required");
                    edittextRegisterAge.requestFocus();
                } else if ((textAge < 18 )|| (textAge > 60)) {
                    Toast.makeText(registerActivity.this, "Your age doesn't allow you to register", Toast.LENGTH_LONG).show();
                    edittextRegisterAge.setError("Your age doesn't allow you to register");
                    edittextRegisterAge.requestFocus();
                } else if (radioGroupRegisterChronic.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(registerActivity.this, "Please select a choice", Toast.LENGTH_LONG).show();
                    radioButtonRegisterChronicselected.setError("Chronic Diseases is required");
                    radioButtonRegisterChronicselected.requestFocus();
                } if (oui.isChecked()){
                    Toast.makeText(registerActivity.this, "You cannot register.Sorry", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(registerActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    edittextRegisterpassword.setError("Password is required");
                    edittextRegisterpassword.requestFocus();
                } else if (textPwd.length() < 6) {
                    Toast.makeText(registerActivity.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                    edittextRegisterpassword.setError("Password too weak");
                    edittextRegisterpassword.requestFocus();
                } else {
                    textChronic = radioButtonRegisterChronicselected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser (textFullName,textEmail,textAge,textPwd,textChronic,textgrp);
                }
            }
        });
    }

    private void registerUser(String textFullName, String textEmail, Integer textAge, String textPwd, String textChronic ,String textgrp) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(registerActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    UserProfileChangeRequest profileChangeRequest= new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textAge,textChronic,textgrp);
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(registerActivity.this, "User registerd successfully.Please verify your email", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(registerActivity.this, loginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(registerActivity.this, "User registerd failed.Please try again", Toast.LENGTH_LONG).show();

                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        edittextRegisterpassword.setError("Your password is too weak.Kindly use a mix of alphabets, numbers and special characters");
                        edittextRegisterpassword.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        edittextRegisterpassword.setError("Your email is invalid or already in use.Kindly re-enter");
                        edittextRegisterpassword.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e){
                        edittextRegisterpassword.setError("User is alredy registered with this email. Use another email");
                        edittextRegisterpassword.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(registerActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}