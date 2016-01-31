package com.rishi.app.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

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

      //  textView = (TextView) findViewById(R.id.display);
    }

}
