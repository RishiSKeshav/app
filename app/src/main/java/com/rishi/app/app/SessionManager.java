package com.rishi.app.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "App";
    private static final String PREF_NAME1 = "login_status_key";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();



    }


    public  void createLoginSession(JSONObject userObj)
    {

        try {

                editor.putString("id",userObj.getString("id"));
                editor.putString("name",userObj.getString("name"));
                editor.putString("emailId",userObj.getString("emailId"));
                editor.putString("password",userObj.getString("password"));
                editor.putString("mobileNo",userObj.getString("mobileNo"));
                editor.putString("displayPicture", userObj.getString("displayPicture"));

                editor.putBoolean("login_status_value",true).commit();

                editor.commit();


        } catch (JSONException e) {
            Toast.makeText(_context, "Error ", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void changeNameMobileNo(String name,String mobileNo){
        editor.putString("name",name);
        editor.putString("mobileNo",mobileNo);
        editor.commit();
    }

    public void changeEmailId(String emailId){
        editor.putString("emailId",emailId);
        editor.commit();
    }

    public void changedisplayPicture(String displayPicture){
        editor.putString("displayPicture",displayPicture);
        editor.commit();
    }

    public void logoutUser(){
        editor.putBoolean("login_status_value",false);
        editor.commit();
    }

    public String getId()
    {
       return pref.getString("id",null);
    }

    public String getName(){
        return pref.getString("name",null);
    }

    public String getEmailId(){
        return pref.getString("emailId",null);
    }

    public String getPassword(){
        return pref.getString("password",null);
    }

    public String getMobileNo(){
        return pref.getString("mobileNo",null);
    }

    public String getDisplayPicture(){
        return pref.getString("displayPicture",null);
    }

    public Boolean LoginValue(){
        return pref.getBoolean("login_status_value", false);
    }

}