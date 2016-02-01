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
    private static final String PREF_NAME = "AndroidHivePref";

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
                editor.putString("firstName",userObj.getString("firstName"));
                editor.putString("lastName",userObj.getString("lastName"));
                editor.putString("emailId",userObj.getString("emailId"));
                editor.putString("mobileNo",userObj.getString("mobileNo"));
                editor.putString("displayPicture",userObj.getString("displayPicture"));

                editor.commit();

                Log.i("SER", "After Creating session, lastName: " + userObj.getString("lastName"));
        } catch (JSONException e) {
            Toast.makeText(_context, "Error ", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public String getId()
    {
       return pref.getString("id",null);
    }

    public String getFirstName(){
        return pref.getString("firstName",null);
    }

    public String getLastName(){
        return pref.getString("lastName",null);
    }

    public String getEmailId(){
        return pref.getString("emailId",null);
    }

    public String getMobileNo(){
        return pref.getString("mobileNo",null);
    }

    public String getDisplayPicture(){
        return pref.getString("displayPicture",null);
    }
}