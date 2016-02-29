package com.rishi.app.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by amitrajula on 2/15/16.
 */
public class MyGcmPushReceiver extends GcmListenerService {

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;

    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        String action = bundle.getString("action");

        if(action.equals("shared_album")) {
            String title = bundle.getString("title");
            String message = bundle.getString("message");
            String image = bundle.getString("image");
            String timestamp = "2016-02-16 04:16:21";
            String sharedAlbumId = bundle.getString("sharedAlbumId");
            String albumName = bundle.getString("albumName");



            Intent resultIntent = new Intent(getApplicationContext(), SharedAlbumMediaDisplay.class);
            resultIntent.putExtra("message", message);
            resultIntent.putExtra("Id",sharedAlbumId);
            resultIntent.putExtra("Name",albumName);

            if (TextUtils.isEmpty(image)) {
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
            } else {
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, image);
            }



        }else{
            String title = bundle.getString("title");
            String message = bundle.getString("message");
            String image = bundle.getString("image");
            String timestamp = bundle.getString("timestamp");
            String dataId = bundle.getString("dataId");
            String dataPath = bundle.getString("dataPath");



            Intent resultIntent = new Intent(getApplicationContext(), SharedMediaDisplay.class);
            resultIntent.putExtra("message", message);
            resultIntent.putExtra("Id",dataId);
            resultIntent.putExtra("image",dataPath);

            if (TextUtils.isEmpty(image)) {
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
            } else {
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, image);
            }



        }
//        Log.e(TAG, "From: " + from);
//        Log.e(TAG, "Title: " + title);
//        Log.e(TAG, "message: " + message);
//        Log.e(TAG, "image: " + sharedAlbumId);
//        Log.e(TAG, "timestamp: " + timestamp);

//        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//
//            // app is in foreground, broadcast the push message
//            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//            pushNotification.putExtra("message", message);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//            // play notification sound
//            NotificationUtils notificationUtils = new NotificationUtils();
//            notificationUtils.playNotificationSound();
//        } else {


        //}
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
