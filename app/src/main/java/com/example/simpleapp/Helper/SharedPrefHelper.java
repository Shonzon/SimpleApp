package com.example.simpleapp.Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SharedPrefHelper {
    private static SharedPrefHelper mInstance;
    private static Context mCtx;

    private static final String SHARED_PREFER_NAME="myshareduserdata";
    private static final String KEY_USER_EMAIL="useremail";
    private static final String KEY_USER_ID="userpass";

    private SharedPrefHelper(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefHelper(context);
        }
        return mInstance;
    }

    public boolean userLogin(String username,String id){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREFER_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(KEY_USER_EMAIL,username);
        editor.putString(KEY_USER_ID,id);

        editor.apply();
        return true;
    }

    public boolean isLoggedIN(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREFER_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_USER_EMAIL,null)!=null){
            return true;
        }
        return false;
    }

    public boolean logout(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREFER_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public String getUserName(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREFER_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL,null);
    }
    public String getUserID(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREFER_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_ID,null);
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}
