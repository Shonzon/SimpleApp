package com.example.simpleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.simpleapp.Helper.NetworkInformation;
import com.example.simpleapp.Helper.SharedPrefHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button loginButton,registerButton;
    private EditText userEmail,userPassword;
    LinearLayout activitylayout;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDoalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        initialize();
    }
    private void initialize(){
        mAuth = FirebaseAuth.getInstance();

        loginButton=(Button) findViewById(R.id.buttonlogin);
        registerButton=(Button) findViewById(R.id.login_page_register);
        userEmail=(EditText)findViewById(R.id.username_login);
        userPassword=(EditText)findViewById(R.id.password_login);
        activitylayout=(LinearLayout)findViewById(R.id.activitylayout);

        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Loading.... Please Wait");
        progressDoalog.setCanceledOnTouchOutside(false);


        if (SharedPrefHelper.getInstance(getApplicationContext()).isLoggedIN()){
            finish();
            Intent i = new Intent(MainActivity.this,ProfileActiviy.class);
            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }


        buttonClickMethod();
    }
    private void buttonClickMethod(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=userEmail.getText().toString().trim();
                String password=userPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    userEmail.setError( "User Email is required!" );
                } else if (TextUtils.isEmpty(password)){
                    userPassword.setError( "User Email is required!" );
                } else if (!SharedPrefHelper.isEmailValid(email)){
                    userEmail.setError( "Use a valid email address" );
                } else {
                    if (NetworkInformation.isConnected(getApplicationContext())){
                        progressDoalog.show();
                        userLoginByfirebase(email,password);
                    }else {
                        dialogAlert("No Network Connection");
                    }
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,RegisterActivity.class);
                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }

    private void userLoginByfirebase(String email, final String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressDoalog.dismiss();
                            SharedPrefHelper.getInstance(getApplicationContext())
                                    .userLogin(user.getEmail(),user.getUid());
                            Intent i = new Intent(MainActivity.this,ProfileActiviy.class);
                            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        } else {
                            progressDoalog.dismiss();
                            dialogAlert("Username or password not match");
                            Snackbar.make(activitylayout,"Username or password not match",Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void dialogAlert(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(" --ALERT-- ");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }



}
