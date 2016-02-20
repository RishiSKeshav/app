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
    long totalSize=0;
    AsyncTask<ArrayList<Image>,Integer,Integer>  uploadTask;
    int count=0;
    String imgPath="";

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.i("ee",intent.toString());
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("service", "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        unSyncedImageList = intent.getParcelableArrayListExtra("unSyncedImageList");
        startUpload(unSyncedImageList);
        return START_STICKY;
    }

//    @Override
//    public void onStart(Intent intent, int startId) {
//
//        Log.d("service", " service started");
//
//    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        unSyncedImageList.clear();
        uploadTask.cancel(true);
        stopSelf();
        Log.d("service", " service ended");

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public void startUpload(ArrayList<Image> unSyncedImageList)
    {
        Log.d("Service: count ", String.valueOf(unSyncedImageList.size()));

        count = unSyncedImageList.size();
        uploadTask = new uploadAsyncTask().execute(unSyncedImageList);

        /*int i=0;
        for(Image img: unSyncedImageList){

            if(i<4) {
                uploadFile(img.getPath(), img.getName());

                i++;
                Log.e("i",String.valueOf(i));
            }
        }*/
        db = new ImageDatabaseHandler(getApplicationContext(),Environment.getExternalStorageDirectory().toString()+ "/app");
        Log.d("Db count", String.valueOf(db.getCount()));
    }

   class  uploadAsyncTask extends AsyncTask<ArrayList<Image>,Integer,Integer> {

       @Override
       protected void onPostExecute(Integer leftCount) {
           super.onPostExecute(leftCount);

       }

       @Override
       protected Integer doInBackground(ArrayList<Image>... params) {

          for(int i=0;i<5;i++){

              imgPath=params[0].get(i).getPath();

              Intent intent = new Intent("IMAGE_ACTION");
              intent.putExtra("leftCount", count);
              intent.putExtra("imgPath", imgPath);
              sendBroadcast(intent);

              if(isCancelled())
                  break;

               String filePath=params[0].get(i).getPath();
               String filename = params[0].get(i).getName();

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

                                   publishProgress((int) y);
                               }
                           });

                   File sourceFile = new File(filePath);

                   // Adding file data to http body
                   entity.addPart("image", new StringBody(encodedString));

                   // Extra parameters if you want to pass to server
                   entity.addPart("userId", new StringBody("1"));
                   entity.addPart("filename", new StringBody(filename));

                   totalSize = entity.getContentLength();
                   httppost.setEntity(entity);

                   // Making server call
                   HttpResponse response = httpclient.execute(httppost);
                   HttpEntity r_entity = response.getEntity();

                   int statusCode = response.getStatusLine().getStatusCode();
                   if (statusCode == 200) {
                       // Server response
                       responseString = EntityUtils.toString(r_entity);

                       try {
                           JSONObject obj = new JSONObject(responseString);

                           if (obj.getBoolean("error")) {
                               Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                           } else {

                               int mediaId = Integer.parseInt(obj.getString("mediaId"));

                               File folder = new File(Environment.getExternalStorageDirectory().toString() + "/app");
                               boolean success = true;
                               if (!folder.exists()) {
                                   success = folder.mkdir();
                               }

                               db = new ImageDatabaseHandler(getApplicationContext(), folder.toString());

                               Image image = new Image();

                               image.setId(mediaId);
                               image.setPath(filePath);
                               image.setName(filename);

                               db.addImage(image);

                               count--;
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
           return count;
       }

       @Override
       protected void onPreExecute() {

           super.onPreExecute();
       }

       protected void onProgressUpdate(Integer... progress) {

           Intent i = new Intent("PROGRESS_ACTION");
           i.putExtra("NUMBER", progress[0]);
           sendBroadcast(i);
       }
   }
}