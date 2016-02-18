package com.rishi.app.app;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class CameraFullImageDisplayActivity extends Activity {

    private CameraImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_full_image_display);

        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent intent = getIntent();
        ArrayList<String> photoList = intent.getStringArrayListExtra("photoFileList");

        Log.d("photolist size",photoList.toString());

        ArrayList<File> photoFileList = new ArrayList<File>();


        adapter = new CameraImageAdapter(CameraFullImageDisplayActivity.this,
                photoList);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(photoList.size());


    }

}
