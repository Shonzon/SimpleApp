package com.example.simpleapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.simpleapp.Helper.SharedPrefHelper;
import com.example.simpleapp.Model.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ProgressDialog progressDoalog;
    Button chooseButton, uploadButton,registerButton;
    EditText userEmail,userName,userPhone,userPassword,userConfirmPassword;
    ImageView selectImage;
    Uri filePathUri;
    private static int RESULT_LOAD_IMAGE = 7;
    LinearLayout registeractivity;
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;
    private StorageReference sReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        initialize();
    }
    private void initialize(){
        progressDoalog = new ProgressDialog(RegisterActivity.this);
        progressDoalog.setMessage("Loading.... Please Wait");
        progressDoalog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        sReference= FirebaseStorage.getInstance().getReference("users");

        registeractivity=(LinearLayout) findViewById(R.id.registeractivity);

        chooseButton = (Button)findViewById(R.id.chooseImage);
        uploadButton = (Button)findViewById(R.id.uploadImage);
        registerButton = (Button)findViewById(R.id.registration);


        selectImage=(ImageView)findViewById(R.id.imageview);
        userEmail=(EditText)findViewById(R.id.useremail_register);
        userName=(EditText)findViewById(R.id.username_register);
        userPhone=(EditText)findViewById(R.id.usernumber_register);
        userPassword=(EditText)findViewById(R.id.password_register);
        userConfirmPassword=(EditText)findViewById(R.id.confirmpassword_register);

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage.setImageResource(0);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (validation()!=null){
                        userRegistration(validation());
                    }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathUri);
                selectImage.setImageBitmap(bitmap);
                chooseButton.setText("Image Selected");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private UserModel validation(){
        UserModel userModel=new UserModel();
        if (TextUtils.isEmpty(userEmail.getText().toString().trim())){
            userEmail.setError( "User Email is required!" );
        }else if (TextUtils.isEmpty(userName.getText().toString().trim())){
            userName.setError( "User Name  is required!" );
        }else if (TextUtils.isEmpty(userPhone.getText().toString().trim())){
            userPhone.setError( "Phone number  is required!" );
        }else if (isInteger(userPhone.getText().toString().trim())){
            userPhone.setError( "Enter valid Phone number" );
        } else if (TextUtils.isEmpty(userPassword.getText().toString().trim())){
            userPassword.setError( "Password  is required!" );
        } else if (TextUtils.isEmpty(userConfirmPassword.getText().toString().trim())){
            userName.setError( "Confirm Password  is required!" );
        }else if (!SharedPrefHelper.isEmailValid(userEmail.getText().toString().trim())){
            userEmail.setError( "Use a valid email address" );
        }else if (!userPassword.getText().toString().trim().equals(userConfirmPassword.getText().toString().trim())){
            Snackbar.make(registeractivity,"Password not matches",Snackbar.LENGTH_LONG).show();
        }else if (selectImage.getDrawable()==null){
            Snackbar.make(registeractivity,"No image attached",Snackbar.LENGTH_LONG).show();
        } else {
            userModel.setUserEmail(userEmail.getText().toString().trim());
            userModel.setUserName(userName.getText().toString().trim());
            userModel.setUserPhoneNumber(userPhone.getText().toString().trim());
            userModel.setUserPassword(userPassword.getText().toString().trim());
            userModel.setImageUri(filePathUri.toString());
            return userModel;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(RegisterActivity.this,MainActivity.class);
        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public boolean isInteger(String s) {
        Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    private void userRegistration(final UserModel userModel){
        progressDoalog.show();
        mAuth.createUserWithEmailAndPassword(userModel.getUserEmail(), userModel.getUserPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            uploadFileInDatabase(userModel,user);
                        } else {
                            System.out.println(task.getException());
                            progressDoalog.dismiss();
                            Snackbar.make(registeractivity,"User Already Exists With Email or fail:failure",Snackbar.LENGTH_LONG).show();
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDoalog.dismiss();
                Snackbar.make(registeractivity,"Promlem occourd:failure",Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void uploadFileInDatabase(final UserModel userModel, final FirebaseUser firebaseUser){
        if (filePathUri != null)
        {
            final StorageReference fileref=sReference.child(System.currentTimeMillis()+"."+getFileExtention(filePathUri));
            fileref.putFile(filePathUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        progressDoalog.dismiss();
                        throw task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        progressDoalog.dismiss();
                        userModel.setImageUri(task.getResult().toString());
                        mDatabase.child("users").child(firebaseUser.getUid()).setValue(userModel);
                        Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    } else
                    {
                        progressDoalog.dismiss();
                        Snackbar.make(registeractivity,"Database :failure",Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    private String getFileExtention(Uri uri){
        ContentResolver cs=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cs.getType(uri));
    }
}
