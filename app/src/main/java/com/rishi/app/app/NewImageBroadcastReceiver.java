package com.rishi.app.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by RishiS on 2/12/2016.
 */
public class NewImageBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Cursor cursor = context.getContentResolver().query(intent.getData(),      null,null, null, null);
        cursor.moveToFirst();
        String image_path = cursor.getString(cursor.getColumnIndex("_data"));

        //Log.d("BroadcastReceiver","onReceive");
        Toast.makeText(context,image_path, Toast.LENGTH_SHORT).show();

    }
}
