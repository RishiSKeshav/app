package com.rishi.app.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Iterator;

public class Sync extends Activity {

    private ArrayList<com.rishi.app.app.Image> imageList;
    private ArrayList<com.rishi.app.app.Image> syncedImageList;
    private ArrayList<com.rishi.app.app.Image> unSyncedImageList;
    private ArrayList<String> syncedPathList;

    Intent serviceIntent;

    Switch switchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        initializeImageLists();

        serviceIntent = new Intent(Sync.this,ImageUploadService.class);
        serviceIntent.putExtra("unSyncedImageList", unSyncedImageList);
        startService(serviceIntent);

        switchButton = (Switch) findViewById(R.id.switchButton);

        switchButton.setChecked(true);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {

                    initializeImageLists();

                    serviceIntent = new Intent(Sync.this, ImageUploadService.class);
                    serviceIntent.putExtra("unSyncedImageList", unSyncedImageList);
                    startService(serviceIntent);

                    Log.d("checked", " checked reached");


                } else {

                    Log.d("unchecked", " unchecked reached");

                    Intent serviceIntent = new Intent(Sync.this, ImageUploadService.class);

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

        Log.d("unsynced count", String.valueOf(unSyncedImageList.size()));

       /* Iterator<com.rishi.app.app.Image> iteratorList = unSyncedImageList.iterator();

        Log.d("Unsynced ImageList b","-------------------------------------");
        while(iteratorList.hasNext()){

            com.rishi.app.app.Image img = iteratorList.next();

            Log.d("image details: ",img.getName()  +" " + img.getPath());
        }
        Log.d("Unsynced ImageList e","-------------------------------------");*/
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
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.TITLE,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.MIME_TYPE,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null, null);


        if (cursor.moveToFirst()) {
            do {
                com.rishi.app.app.Image image = new com.rishi.app.app.Image();

                image.setPath(cursor.getString(2));
                image.setName(cursor.getString(6));
                image.setData_taken(cursor.getString(5));

                imageList.add(image);
            } while (cursor.moveToNext());
        }

        Log.d("image list count", String.valueOf(imageList.size()));

        /*Log.d("ImageList","Displaying image list");
        Iterator<com.rishi.app.app.Image> iteratorList = imageList.iterator();

        while(iteratorList.hasNext()){

            com.rishi.app.app.Image img = iteratorList.next();

            Log.d("image details: ",img.getName()  +" " + img.getPath());
        }
        Log.d("ImageList","-------------------------------------");*/

    }



}
