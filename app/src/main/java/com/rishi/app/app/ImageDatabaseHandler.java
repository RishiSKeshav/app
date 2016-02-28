package com.rishi.app.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RishiS on 2/13/2016.
 */
public class ImageDatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="ImageData_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_SyncedMedia= "table_syncimage";

    /*public static final String SaveIt = "CREATE TABLE IF NOT EXISTS "
            + TABLE + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_COMMENT
            + " text not null," + COLUMN_LAT+ ","  + COLUMN_LONG + "," + COLUMN_RADI +  ");";*/

    public ImageDatabaseHandler(Context context,String path)
    {
        super(context, path+"/"+DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("Database handler","Database created");

        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SyncedMedia + "( media_id INTEGER PRIMARY KEY,"
                + " name TEXT,"
                + " path TEXT,"
                + " link TEXT,"
                + " source TEXT,"
                + " userId TEXT"
                 + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void addImage(Image img,String userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("media_id", img.getId());
        values.put("name", img.getName());
        values.put("path", img.getPath());
        values.put("link", img.getLink());
        values.put("source", "sync");
        values.put("userId", userId);

        // Inserting Row
        db.insert(TABLE_SyncedMedia, null, values);
        db.close(); // Closing database connection
    }

    void addCameraImage(Image img,String userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("media_id", img.getId());
        values.put("name", img.getName());
        values.put("path", img.getPath());
        values.put("link", img.getLink());
        values.put("source","camera");
        values.put("userId", userId);

        // Inserting Row
        db.insert(TABLE_SyncedMedia, null, values);
        db.close(); // Closing database connection
    }

    public int getCount() {

        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_SyncedMedia;

        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public ArrayList<SyncImages> getAllImages(String userId) {
        ArrayList<SyncImages> syncedImageList = new ArrayList<SyncImages>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SyncedMedia +" where userId='"+userId+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SyncImages img = new SyncImages();

                img.setId(Integer.parseInt(cursor.getString(0)));
                img.setName(cursor.getString(1));
                img.setPath(cursor.getString(2));
                img.setLink(cursor.getString(3));
                img.setSyncStatus(true);

                // Adding contact to list
                syncedImageList.add(img);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return syncedImageList;
    }

    public ArrayList<String> getAllImagePath(String userId) {
        ArrayList<String> syncedImagePathList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SyncedMedia +" where userId='"+userId+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                syncedImagePathList.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return syncedImagePathList;
    }

    public ArrayList<String> getAllCameraImagePath(String userId) {
        ArrayList<String> cameraImagePathList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SyncedMedia +" where source='camera' and userId='"+userId+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cameraImagePathList.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return cameraImagePathList;
    }

    public ArrayList<String> getAllSyncImagePath(String userId) {
        ArrayList<String> syncImagePathList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SyncedMedia +" where source='sync' and userId='"+userId+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                syncImagePathList.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return syncImagePathList;
    }

    public int getMediaId(String path,String userId){

        int mediaId=0;

        String selectQuery = "SELECT media_id  FROM " + TABLE_SyncedMedia +" where link='"+path+"' and userId='"+userId+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                mediaId = Integer.parseInt(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return mediaId;
    }

    public void deleteMedia(int mediaId){

        //String selectQuery = "Delete  FROM " + TABLE_SyncedMedia +" where media_id='"+mediaId+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor cursor = db.rawQuery(selectQuery, null);
        db.delete(TABLE_SyncedMedia,"media_id="+mediaId,null);
        //cursor.close();
    }
}
