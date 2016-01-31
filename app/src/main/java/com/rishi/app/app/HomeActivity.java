package com.rishi.app.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by RishiS on 1/27/2016.
 */
/**
 *
 * Home Screen Activity
 */
public class HomeActivity extends Activity {

    SessionManager sessionManager;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Displays Home Screen
        setContentView(R.layout.home);

        sessionManager = new SessionManager(getApplicationContext());

        textView = new TextView(this);

//        Log.i("SER",sessionManager.getFirstName());
        Toast.makeText(getApplicationContext(),"Welcome " + sessionManager.getFirstName(),Toast.LENGTH_LONG).show();
        textView.setText(sessionManager.getFirstName());

        FrameLayout fl = (FrameLayout)findViewById(R.id.home);

        fl.addView(textView);
    }

}
