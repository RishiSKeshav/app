package com.rishi.app.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FullScreenMediaDisplay extends Activity {

    private FullScreenMediaAdapter adapter;
    private ViewPager viewPager;
    private ArrayList<String> albummedia = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_media_display);

        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent i = getIntent();
        String position = i.getStringExtra("Position");

        ArrayList<String> data = i.getStringArrayListExtra("data");

         adapter = new FullScreenMediaAdapter(FullScreenMediaDisplay.this,
               data);

        viewPager.setAdapter(adapter);

        // displaying selected image first
       viewPager.setCurrentItem(Integer.parseInt(position));
    }


}
