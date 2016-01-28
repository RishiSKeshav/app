package com.rishi.app.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by RishiS on 1/27/2016.
 */
/**
 *
 * Home Screen Activity
 */
public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Displays Home Screen
        setContentView(R.layout.home);
    }

}
