package com.rishi.app.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FullScreenMediaDisplay extends AppCompatActivity {

    private FullScreenMediaAdapter adapter;
    private ViewPager viewPager;
    private ArrayList<String> albummedia = new ArrayList<String>();
    String SHARED,ID,NAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_media_display);

        Toolbar toolbar= (Toolbar) findViewById(R.id.full_screen_display_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent i = getIntent();
        String position = i.getStringExtra("Position");
        SHARED = i.getStringExtra("shared");
        ID = i.getStringExtra("Id");
        NAME = i.getStringExtra("Name");

        ArrayList<String> data = i.getStringArrayListExtra("data");

         adapter = new FullScreenMediaAdapter(FullScreenMediaDisplay.this,
               data);

        viewPager.setAdapter(adapter);

        // displaying selected image first
       viewPager.setCurrentItem(Integer.parseInt(position));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if(SHARED.equals("no")) {
                Intent i = new Intent(FullScreenMediaDisplay.this, AlbumMediaDisplay.class);
                i.putExtra("Id", ID);
                i.putExtra("Name", NAME);
                FullScreenMediaDisplay.this.startActivity(i);
            }else{
                Intent i = new Intent(FullScreenMediaDisplay.this, SharedAlbumMediaDisplay.class);
                i.putExtra("Id", ID);
                i.putExtra("Name", NAME);
                FullScreenMediaDisplay.this.startActivity(i);
                Log.i("dd",ID);
            }

           // onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
