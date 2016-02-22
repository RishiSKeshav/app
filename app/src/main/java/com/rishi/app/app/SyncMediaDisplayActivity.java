package com.rishi.app.app;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

public class SyncMediaDisplayActivity extends AppCompatActivity {

    private ArrayList<SyncImages> imageList;
    private ArrayList<SyncImages> unSyncedImageList;
    private ArrayList<SyncImages> syncedImageList;
    private ArrayList<String> syncedPathList;
    private ArrayList<SyncImages> syncMediaDisplayList = new ArrayList<SyncImages>();

    SyncImages sImage;
    SyncMediaAdapter syncAdapter;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_media_display);

        Toolbar toolbar= (Toolbar) findViewById(R.id.sync_images_toolbar);
        setSupportActionBar(toolbar);

        initializeImageLists();

        recyclerView = (RecyclerView)findViewById(R.id.sync_media_recycler_view);
        syncAdapter = new SyncMediaAdapter(syncMediaDisplayList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,4);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(syncAdapter);

        generateMainList();


        for(SyncImages img:syncMediaDisplayList){

            if(img.getLink().equals("localPath"))
                Log.d("unsynced",img.getPath() + " " + String.valueOf(img.getSyncStatus()));
            else
                Log.d("synced",img.getLink() + " " + String.valueOf(img.getSyncStatus()));

        }

    }

    private void initializeImageLists() {

        generateImageList();
        generateDBImageList();
        generateUnsyncedImageList();

    }

    private void generateMainList()
    {
        for(SyncImages img:unSyncedImageList){
            syncMediaDisplayList.add(img);

            syncAdapter.notifyDataSetChanged();
        }

        for(SyncImages img:syncedImageList){
            syncMediaDisplayList.add(img);

            syncAdapter.notifyDataSetChanged();
        }
    }

    private void generateUnsyncedImageList() {

        unSyncedImageList = new ArrayList<SyncImages>();

        for(SyncImages img : imageList){
            Boolean flag = false;
            for(SyncImages sImg:syncedImageList) {
                if(img.getPath().equals(sImg.getPath()))
                    flag=true;
            }

            if(flag==false)
                unSyncedImageList.add(img);
        }
    }

    private void generateDBImageList() {

        ImageDatabaseHandler db = new ImageDatabaseHandler(this, Environment.getExternalStorageDirectory().toString()+ "/app");

        if(db!=null){

            syncedImageList=db.getAllImages();
        }
        else
            Log.i("databasae_name","DB not created");

//        Log.d("DB list count ",String.valueOf(syncedPathList.size()));
    }

    private void generateImageList() {

        imageList = new ArrayList<SyncImages>();

        ContentResolver cr = this.getContentResolver();

        String[] columns = new String[] {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                SyncImages image = new SyncImages();

                image.setPath(cursor.getString(0));
                image.setName(cursor.getString(1));
                image.setLink("localPath");
                image.setSyncStatus(false);

                imageList.add(image);
            } while (cursor.moveToNext());
        }

        Log.d("image list count", String.valueOf(imageList.size()));
    }
}
