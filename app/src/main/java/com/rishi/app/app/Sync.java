package com.rishi.app.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Iterator;

public class Sync extends Activity {

    private ArrayList<com.rishi.app.app.Image> imageList;
    private ArrayList<com.rishi.app.app.Image> syncedImageList;
    private ArrayList<com.rishi.app.app.Image> unSyncedImageList;
    private ArrayList<String> syncedPathList;
    int count=0;

    ProgressBar pBar;
    RelativeLayout layout;
    RelativeLayout mainLayout;

    Intent serviceIntent;

    Switch switchButton;
    TextView uploadLeftTxt;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        uploadLeftTxt =(TextView) findViewById(R.id.uploadLeftTxt);
        pBar = (ProgressBar) findViewById(R.id.progressBar);
        imgView =(ImageView)findViewById(R.id.imgView);

        layout =(RelativeLayout) findViewById(R.id.sync_sub_layout);
        mainLayout =(RelativeLayout) findViewById(R.id.sync_main_layout);

        IntentFilter filter = new IntentFilter("PROGRESS_ACTION");
        registerReceiver(myReceiver,filter);

        IntentFilter finalFilter = new IntentFilter("IMAGE_ACTION");
        registerReceiver(finalCountReceiver,finalFilter);

        initializeImageLists();

        serviceIntent = new Intent(Sync.this,ImageUploadService.class);
        serviceIntent.putParcelableArrayListExtra("unSyncedImageList", unSyncedImageList);
        startService(serviceIntent);

        switchButton = (Switch) findViewById(R.id.switchButton);

        switchButton.setChecked(true);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {

                    initializeImageLists();

                    serviceIntent = new Intent(Sync.this, ImageUploadService.class);
                    serviceIntent.putParcelableArrayListExtra("unSyncedImageList", unSyncedImageList);
                    startService(serviceIntent);

                    Log.d("checked", " checked reached");
                } else {

                    Log.d("unchecked", " unchecked reached");

                    serviceIntent = new Intent(Sync.this, ImageUploadService.class);

                    stopService(serviceIntent);

                }
            }
        });
    }

        private void initializeImageLists() {

        generateImageList();
        generateDBImageList();
        generateUnsyncedImageList();
    }

    private void generateUnsyncedImageList() {

        unSyncedImageList = new ArrayList<Image>();

        for(com.rishi.app.app.Image img : imageList){

            if(!syncedPathList.contains(img.path))
                unSyncedImageList.add(img);
        }

        count=unSyncedImageList.size();

        Log.d("unsynced count", String.valueOf(unSyncedImageList.size()));
    }

    private void generateDBImageList() {

        ImageDatabaseHandler db = new ImageDatabaseHandler(this, Environment.getExternalStorageDirectory().toString()+ "/app");

        if(db!=null){

            syncedPathList= db.getAllImagePath();

            Iterator<String> abc = syncedPathList.iterator();

            while(abc.hasNext())
            {

                Log.d("DB ",abc.next());
            }

        }
        else
            Log.i("databasae_name","DB not created");

        Log.d("DB list count ",String.valueOf(syncedPathList.size()));
    }

    private void generateImageList() {

        imageList = new ArrayList<com.rishi.app.app.Image>();

        ContentResolver cr = this.getContentResolver();

        String[] columns = new String[] {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                com.rishi.app.app.Image image = new com.rishi.app.app.Image();

                image.setPath(cursor.getString(0));
                image.setName(cursor.getString(1));

                imageList.add(image);
            } while (cursor.moveToNext());
        }

        Log.d("image list count", String.valueOf(imageList.size()));
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            int ola = intent.getIntExtra("NUMBER", 0);
            if(pBar.getParent()!=null)
                ((ViewGroup)pBar.getParent()).removeView(pBar);



            if(ola==100)
            {
                if(imgView.getParent()!=null)
                    ((ViewGroup)imgView.getParent()).removeView(imgView);

                if(uploadLeftTxt.getParent()!=null)
                    ((ViewGroup)uploadLeftTxt.getParent()).removeView(uploadLeftTxt);

                pBar.setVisibility(View.VISIBLE);
                pBar.setProgress(ola);
                pBar.setVisibility(View.INVISIBLE);
                layout.addView(pBar);

                imgView.setVisibility(View.INVISIBLE);
                layout.addView(imgView);

                uploadLeftTxt.setVisibility(View.INVISIBLE);
                layout.addView(uploadLeftTxt);
            }

            else {
                pBar.setVisibility(View.VISIBLE);
                pBar.setProgress(ola);
                layout.addView(pBar);
            }
            //Log.d("dddReceiver", String.valueOf(ola));   //can't see
        }
    };

    private BroadcastReceiver finalCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int countLeft = intent.getIntExtra("leftCount", 0);
            String imgPath = intent.getStringExtra("imgPath");

            Bitmap bmp = BitmapFactory.decodeFile(imgPath);

            if(imgView.getParent()!=null)
                ((ViewGroup)imgView.getParent()).removeView(imgView);

            imgView.setImageBitmap(bmp);
            layout.addView(imgView);


            if(uploadLeftTxt.getParent()!=null)
                ((ViewGroup)uploadLeftTxt.getParent()).removeView(uploadLeftTxt);

            uploadLeftTxt.setText("Backing up:" + countLeft + " left");
            layout.addView(uploadLeftTxt);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(myReceiver);
        unregisterReceiver(finalCountReceiver);
    }
}