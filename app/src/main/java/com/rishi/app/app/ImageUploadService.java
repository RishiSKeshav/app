package com.rishi.app.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by RishiS on 2/14/2016.
 */
public class ImageUploadService extends Service {

    ArrayList<Image> unSyncedImageList = new ArrayList<>();
    SessionManager sessionManager;
    ImageDatabaseHandler db;
    long totalSize = 0;
    AsyncTask<ArrayList<Image>, Integer, Integer> uploadTask;
    int count = 0;
    String imgPath = "";

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("ee", intent.toString());
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("service", "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sessionManager = new SessionManager(getApplicationContext());
        unSyncedImageList = intent.getParcelableArrayListExtra("unSyncedImageList");
        startUpload(unSyncedImageList);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        unSyncedImageList.clear();
        stopSelf();

        Log.d("service", " service ended");
    }

    public void startUpload(final ArrayList<Image> unSyncedImageList) {
        Log.d("Service: count ", String.valueOf(unSyncedImageList.size()));

        count = unSyncedImageList.size();

        Runnable r = new Runnable() {
            public void run() {

                for (int i = 0; i < unSyncedImageList.size(); i++) {
                    synchronized (this) {
                        try {
                            upload(unSyncedImageList.get(i));

                        } catch (Exception e) {
                        }
                    }
                }
            }
        };

        Thread t = new Thread(r);
        t.start();

        db = new ImageDatabaseHandler(getApplicationContext(), Environment.getExternalStorageDirectory().toString() + "/ClikApp");
        Log.d("Db count", String.valueOf(db.getCount()));
    }

    public void upload(Image img) {

        if(sessionManager.getSyncStatus()) {

            File imgFile = new File(img.getPath());

            if (imgFile.exists()) {

                Long fileSize = imgFile.length();
                //Log.d("Image upload size",String.valueOf(imgFile.length()));

                Intent intent = new Intent("IMAGE_ACTION");
                intent.putExtra("imgPath", img.path);
                intent.putExtra("leftCount", count);
                sendBroadcast(intent);

                Log.d("userID", sessionManager.getId());
                String filePath = img.getPath();
                String filename = img.getName();

                Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                String encodedString = Base64.encodeToString(byte_arr, 0);

                String responseString = null;

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://52.89.2.186/project/webservice/uploadMedia.php");

                try {
                    AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                            new AndroidMultiPartEntity.ProgressListener() {

                                @Override
                                public void transferred(long num) {

                                    double y = (long) (((float) num / totalSize) * 100);

                                    Intent i = new Intent("PROGRESS_ACTION");
                                    i.putExtra("NUMBER", (int) y);
                                    sendBroadcast(i);
                                }
                            });

                    // Adding file data to http body
                    entity.addPart("image", new StringBody(encodedString));

                    // Extra parameters if you want to pass to server
                    entity.addPart("userId", new StringBody(sessionManager.getId()));
                    entity.addPart("filename", new StringBody(filename));
                    entity.addPart("filesize", new StringBody(fileSize.toString()));

                    totalSize = entity.getContentLength();
                    httppost.setEntity(entity);

                    Log.d("entity",entity.toString());

                    // Making server call
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();

                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        // Server response
                        responseString = EntityUtils.toString(r_entity);

                        Log.d("response", responseString);
                        try {
                            JSONObject obj = new JSONObject(responseString);

                            if (obj.getBoolean("error")) {

                                Log.d("file error", obj.getString("msg"));
                                //Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                            } else {

                                int mediaId = Integer.parseInt(obj.getString("mediaId"));
                                String link = obj.getString("link");

                                Log.d("response ok", String.valueOf(mediaId) + " " + link);

                                Log.d("link", link);

                                File folder = new File(Environment.getExternalStorageDirectory().toString() + "/ClikApp");
                                boolean success = true;
                                if (!folder.exists()) {
                                    success = folder.mkdir();
                                }

                                db = new ImageDatabaseHandler(getApplicationContext(), folder.toString());

                                Image image = new Image();

                                image.setId(mediaId);
                                image.setPath(filePath);
                                image.setName(filename);
                                image.setLink(link);

                                db.addImage(image,sessionManager.getId());

                                count--;
                                Log.d("count", String.valueOf(count));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        responseString = "Error occurred! Http Status Code: "
                                + statusCode;
                    }

                } catch (ClientProtocolException e) {
                    responseString = e.toString();
                } catch (IOException e) {
                    responseString = e.toString();
                }
            }
        }
    }
}