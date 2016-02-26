package com.rishi.app.app;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

public class SyncMediaFullScreenActivity extends AppCompatActivity {

    private ViewPager viewPager;
    SyncFullScreenAdapter adapter;
    String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_media_full_screen);

        Toolbar toolbar= (Toolbar) findViewById(R.id.full_screen_display_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        viewPager = (ViewPager) findViewById(R.id.syncViewPager);

        Intent i = getIntent();
        String position = i.getStringExtra("Position");
        action = i.getStringExtra("action");

        ArrayList<String> data = i.getStringArrayListExtra("data");

        adapter = new SyncFullScreenAdapter(SyncMediaFullScreenActivity.this,
                data);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(Integer.parseInt(position));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sync_media_full_screen, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(SyncMediaFullScreenActivity.this,SyncMediaDisplayActivity.class);
            intent.putExtra("action",action);
            startActivity(intent);
    }

        return super.onOptionsItemSelected(item);
    }
}
