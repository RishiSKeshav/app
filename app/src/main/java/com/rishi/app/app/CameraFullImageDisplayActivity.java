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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class CameraFullImageDisplayActivity extends AppCompatActivity {

    private CameraImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_full_image_display);

        viewPager = (ViewPager) findViewById(R.id.pager);

        Toolbar toolbar= (Toolbar) findViewById(R.id.camera_full_image_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_camera_full_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.camera_upload:
                Log.i("eee","reached");
                return true;

        }

        return(super.onOptionsItemSelected(item));
    }

}
