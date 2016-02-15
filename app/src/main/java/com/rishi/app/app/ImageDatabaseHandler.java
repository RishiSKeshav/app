package com.rishi.app.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

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

        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SyncedMedia + "( image_id INTEGER PRIMARY KEY autoincrement,"
                + " name TEXT,"
                + " uuid TEXT,"
                + " path TEXT"
                 + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void addImage(Image img) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", img.getName());
        values.put("uuid", img.getUuid());
        values.put("path", img.getPath());

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

    public ArrayList<Image> getAllImages() {
        ArrayList<Image> syncedImageList = new ArrayList<Image>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SyncedMedia;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Image img = new Image();

                img.setId(Integer.parseInt(cursor.getString(0)));
                img.setName(cursor.getString(1));
                img.setUuid(cursor.getString(2));
                img.setPath(cursor.getString(3));

                // Adding contact to list
                syncedImageList.add(img);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return syncedImageList;
    }

    public ArrayList<String> getAllImagePath() {
        ArrayList<String> syncedImagePathList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SyncedMedia;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                syncedImagePathList.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return syncedImagePathList;
    }
}
