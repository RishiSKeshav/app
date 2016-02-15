package com.rishi.app.app;

/**
 * Created by RishiS on 2/10/2016.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FetchImages extends Activity {

    private File root;
    private ArrayList<File> fileList = new ArrayList<File>();
    private LinearLayout view;
    private ArrayList<Image> imageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fetch_images);

        view = (LinearLayout) findViewById(R.id.view);

        imageList = new ArrayList<Image>();

        ContentResolver cr = this.getContentResolver();

        String[] columns = new String[] {
                ImageColumns._ID,
                ImageColumns.TITLE,
                ImageColumns.DATA,
                ImageColumns.MIME_TYPE,
                ImageColumns.SIZE,
                ImageColumns.DATE_TAKEN,
                ImageColumns.DISPLAY_NAME};
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null, null);


        if (cursor.moveToFirst()) {
            do {
                Image image = new Image();

                image.setPath(cursor.getString(2));
                image.setName(cursor.getString(6));
                image.setData_taken(cursor.getString(5));

                imageList.add(image);
            } while (cursor.moveToNext());
        }


        Iterator<Image> imageListIterator = imageList.iterator();

        while(imageListIterator.hasNext()){

            Image img = imageListIterator.next();

            Log.d("image details: ",img.getName() + " "+ img.data_taken +" " + img.getPath());
        }

        //getting SDcard root path
        /*root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath());
        getfile(root);

        for (int i = 0; i < fileList.size(); i++) {
            TextView textView = new TextView(this);
            textView.setText(fileList.get(i).getName()+ " " +fileList.get(i).lastModified());
            textView.setPadding(5, 5, 5, 5);

            System.out.println(fileList.get(i).getName());

            if (fileList.get(i).isDirectory()) {
                textView.setTextColor(Color.parseColor("#FF0000"));
            }
            view.addView(textView);
        }

    }

    public ArrayList<File> getfile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    fileList.add(listFile[i]);
                    getfile(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".png")
                            || listFile[i].getName().endsWith(".jpg")
                            || listFile[i].getName().endsWith(".jpeg")
                            || listFile[i].getName().endsWith(".gif"))

                    {
                        fileList.add(listFile[i]);
                    }
                }

            }
        }
        return fileList;*/
    }

}
