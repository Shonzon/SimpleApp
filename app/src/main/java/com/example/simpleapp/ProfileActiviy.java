package com.example.simpleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.simpleapp.Helper.NetworkInformation;
import com.example.simpleapp.Helper.SharedPrefHelper;
import com.example.simpleapp.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActiviy extends AppCompatActivity {

    private Button viewProfile,logout;
    private TextView userPref,userEmail,userName,userPhone;
    private ImageView imageView;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDoalog;
    LinearLayout prfshw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_activiy);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        initialize();



        if (SharedPrefHelper.getInstance(getApplicationContext()).isLoggedIN()){
            userPref.setText(SharedPrefHelper.getInstance(getApplicationContext()).getUserName());
        }
    }
    private void initialize(){

        progressDoalog = new ProgressDialog(ProfileActiviy.this);
        progressDoalog.setMessage("Loading.... Please Wait");
        progressDoalog.setCanceledOnTouchOutside(false);

        viewProfile = (Button)findViewById(R.id.showprofile_btn);
        logout= (Button)findViewById(R.id.logout_btn);

        prfshw=(LinearLayout)findViewById(R.id.prfsh);

        userPref=(TextView) findViewById(R.id.sherdpref);
        userEmail=(TextView) findViewById(R.id.useremail);
        userName=(TextView) findViewById(R.id.userName);
        userPhone=(TextView) findViewById(R.id.user_phone);
        imageView=(ImageView) findViewById(R.id.prfimageview);
        buttonClickMethod();

    }

    private void buttonClickMethod() {
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkInformation.isConnected(getApplicationContext())){
                    getDataImagefirebase();
                }else {
                    AlertDialog alertDialog = new AlertDialog.Builder(ProfileActiviy.this).create();
                    alertDialog.setTitle(" --ALERT-- ");
                    alertDialog.setMessage("No Network Connection");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(ProfileActiviy.this,MainActivity.class);
                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                SharedPrefHelper.getInstance(getApplicationContext()).logout();
            }
        });
    }
    private void getDataImagefirebase() {
        progressDoalog.show();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(SharedPrefHelper.getInstance(getApplicationContext()).getUserID());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel uploadModel=dataSnapshot.getValue(UserModel.class);
                        userEmail.setText(uploadModel.getUserEmail());
                        userName.setText(uploadModel.getUserName());
                        userPhone.setText(uploadModel.getUserPhoneNumber());
                        Glide.with(getApplicationContext()).load(uploadModel.getImageUri())
                                .into(imageView);
                        prfshw.setVisibility(View.VISIBLE);
                        progressDoalog.dismiss();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDoalog.dismiss();
                Toast.makeText(getApplicationContext(),databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog alertDialog = new AlertDialog.Builder(ProfileActiviy.this).create();
        alertDialog.setTitle(" --ALERT-- ");
        alertDialog.setMessage("You are going to log out ");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                        Intent i = new Intent(ProfileActiviy.this,MainActivity.class);
                        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                });
        alertDialog.show();
    }
}
