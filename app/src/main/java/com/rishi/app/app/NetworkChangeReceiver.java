package com.rishi.app.app;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by RishiS on 2/25/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    SessionManager sessionManager;
    Intent serviceIntent;

    private ArrayList<com.rishi.app.app.Image> imageList;
    private ArrayList<com.rishi.app.app.Image> syncedImageList;
    private ArrayList<com.rishi.app.app.Image> unSyncedImageList;
    private ArrayList<String> syncedPathList;

    Context c;

    public NetworkChangeReceiver(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        sessionManager = new SessionManager(context.getApplicationContext());
        c = context.getApplicationContext();
        //Log.d("network change", "network change");
       // Log.d("onReceive",sessionManager.getName());

        startOrStopSync();

    }

    public void startOrStopSync() {
    ConnectivityManager cm =
            (ConnectivityManager) c.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

    if (activeNetwork != null && activeNetwork.isConnected()) {

        boolean isMobileData = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        boolean isWIFI = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        if (sessionManager.getSyncStatus()) {
                        if (isWIFI) {
                startSync();
            } else if (isMobileData) {
                if (sessionManager.getPhotoSyncStatus())
                    startSync();
                else
                    stopSync();
            }

            initializeImageLists();

            serviceIntent = new Intent(c,ImageUploadService.class);
            serviceIntent.putParcelableArrayListExtra("unSyncedImageList", unSyncedImageList);
            c.startService(serviceIntent);
        } else {

            stopSync();
        }
    }
}

    private void startSync(){

        initializeImageLists();

        serviceIntent = new Intent(c,ImageUploadService.class);
        serviceIntent.putParcelableArrayListExtra("unSyncedImageList", unSyncedImageList);
        c.startService(serviceIntent);
    }

    private void stopSync(){
        serviceIntent = new Intent(c, ImageUploadService.class);
        c.stopService(serviceIntent);
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
    }

    private void generateDBImageList() {

        ImageDatabaseHandler db = new ImageDatabaseHandler(c, Environment.getExternalStorageDirectory().toString()+ "/ClikApp");

        if(db!=null){

            syncedPathList= db.getAllImagePath(sessionManager.getId());

            Iterator<String> abc = syncedPathList.iterator();

            while(abc.hasNext())
            {

                Log.d("DB ",abc.next());
            }

        }
        else
            Log.i("databasae_name","DB not created");

        Log.d("DB list count ", String.valueOf(syncedPathList.size()));
    }

    private void generateImageList() {

        imageList = new ArrayList<com.rishi.app.app.Image>();

        ContentResolver cr = c.getContentResolver();

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
}
