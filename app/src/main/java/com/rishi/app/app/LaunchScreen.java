package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by amitrajula on 2/20/16.
 */

public class LaunchScreen extends AppCompatActivity {
    SessionManager sessionManager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Displays Home Screen
        setContentView(R.layout.launch_screen);
        sessionManager = new SessionManager(getApplicationContext());


        if (isLoggedIn()) {
            //user is already logged in!
           Intent i = new Intent(LaunchScreen.this,HomeActivity.class);
            startActivity(i);

        } else {
            //user is logged out!
            Intent i = new Intent(LaunchScreen.this,LoginActivity.class);
            startActivity(i);
        }



    }

    public boolean isLoggedIn() {
        Boolean mIsLoggedIn = sessionManager.LoginValue();
        return mIsLoggedIn;
    }
}
