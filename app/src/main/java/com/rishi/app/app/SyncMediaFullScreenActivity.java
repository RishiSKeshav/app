package com.rishi.app.app;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class SyncMediaFullScreenActivity extends AppCompatActivity {

    private ViewPager viewPager;
    SyncFullScreenAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_media_full_screen);

        viewPager = (ViewPager) findViewById(R.id.syncViewPager);

        Intent i = getIntent();
        String position = i.getStringExtra("Position");

        ArrayList<String> data = i.getStringArrayListExtra("data");

        adapter = new SyncFullScreenAdapter(SyncMediaFullScreenActivity.this,
                data);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(Integer.parseInt(position));
    }
}
