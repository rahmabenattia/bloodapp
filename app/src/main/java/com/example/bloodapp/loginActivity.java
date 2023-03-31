package com.example.bloodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {
    private EditText edittextLoginEmail,edittextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG="loginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("login");

        edittextLoginEmail = findViewById(R.id.edittext_login_email);
        edittextLoginPwd = findViewById(R.id.edittext_login_pwd);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();

        Button buttonForgotpassword = findViewById(R.id.button_forgot_password);
        buttonForgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(loginActivity.this, "You can reset your password now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(loginActivity.this, ForgotpasswordActivity.class));
            }
        });
        ImageView imageviewshowhidepwd = findViewById(R.id.image_view_hide_pwd);
        imageviewshowhidepwd.setImageResource(R.drawable.hide);
        imageviewshowhidepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    edittextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageviewshowhidepwd.setImageResource(R.drawable.hide);
                }else {
                    edittextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageviewshowhidepwd.setImageResource(R.drawable.show);
                }
            }
        });




        Button buttonlogin = findViewById(R.id.button_login);
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = edittextLoginEmail.getText().toString();
                String textPwd = edittextLoginPwd.getText().toString();

                if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(loginActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    edittextLoginEmail.setError("Email is required");
                    edittextLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(loginActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    edittextLoginEmail.setError(" Valid email is required");
                    edittextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(loginActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    edittextLoginPwd.setError("Password is required");
                    edittextLoginPwd.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                }
            }
        });
    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    if (firebaseUser.isEmailVerified()){
                        Toast.makeText(loginActivity.this, "You are logged in now", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(loginActivity.this, UserprofileActivity.class));
                        finish();
                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }
                }
                else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        edittextLoginEmail.setError("User does not exist or is no longer valid.Please register again");
                        edittextLoginEmail.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        edittextLoginEmail.setError("Invalid credentials.Kindly, check and re-enter");
                        edittextLoginEmail.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(loginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity.this);
        builder.setTitle("Email Not verified");
        builder.setMessage("Please verifiy your email now. You can not login without email verification");

        builder.setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN );
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null){
            Toast.makeText(this, "Already logged In!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(loginActivity.this, UserprofileActivity.class));
            finish();
        }else {
            Toast.makeText(this, "You can login Now!", Toast.LENGTH_LONG).show();
        }
    }
}