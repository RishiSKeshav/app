package com.rishi.app.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.google.android.gms.internal.zzip.runOnUiThread;

/**
 * Created by RishiS on 2/14/2016.
 */
public class ImageUploadService extends Service {

    private TextView txtPercentage;
    private ProgressBar progressBar;
    ArrayList<Image> unSyncedImageList;
    SessionManager sessionManager;
    ImageDatabaseHandler db;
    private LayoutInflater inflater;
    private Activity _activity;
    long totalSize=0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("service", " service created");
    }

    @Override
    public void onStart(Intent intent, int startId) {

        Log.d("service", " service started");
        unSyncedImageList =(ArrayList<Image>) intent.getSerializableExtra("unSyncedImageList");

        Log.d("Service: count ", String.valueOf(unSyncedImageList.size()));

        int i=0;
        for(Image img: unSyncedImageList){

            if(i<10) {
                uploadFile(img.getPath(), img.getName());
                i++;
            }
        }


        db = new ImageDatabaseHandler(getApplicationContext(),Environment.getExternalStorageDirectory().toString()+ "/app");
        Log.d("Db count",String.valueOf(db.getCount()));

    }

    @Override
    public void onDestroy() {

        Log.d("service", " service ended");

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("deprecation")
    private void uploadFile(final String filePath,final String filename) {

        new AsyncTask<Void,Integer,Void>(){

            @Override
            protected void onPreExecute() {
                // setting progress bar to zero
                //progressBar.setProgress(0);
                super.onPreExecute();
            }

            protected void onProgressUpdate(Integer... progress) {

                Intent i = new Intent("PROGRESS_ACTION");
                i.putExtra("totalsize",totalSize);
                i.putExtra("NUMBER", progress[0]);
                sendBroadcast(i);
/*

                // Making progress bar visible
                progressBar.setVisibility(View.VISIBLE);

                // updating progress bar value
                progressBar.setProgress(progress[0]);

                // updating percentage value
                txtPercentage.setText(String.valueOf(progress[0]) + "%");
*/
            }


            @Override
            protected Void doInBackground(Void... params) {

                Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                String encodedString = Base64.encodeToString(byte_arr, 0);

                String responseString=null;

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://52.89.2.186/project/webservice/uploadMedia.php");

                try {
                    AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                            new AndroidMultiPartEntity.ProgressListener() {

                                @Override
                                public void transferred(long num) {
                                    //float x = (num/totalSize)*100;
                                    Log.e("n",String.valueOf(num));
                                    Log.e("t",String.valueOf(totalSize));
                                    /*Log.d("n/t",String.valueOf(num/totalSize));
*/
                                    double y = (long)(((float)num/totalSize)*100);

                                    //Long y = (100/totalSize)*num;
                                    Log.d("y",String.valueOf(y));
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

                                int mediaId =Integer.parseInt(obj.getString("mediaId"));

                                File folder = new File(Environment.getExternalStorageDirectory().toString()+ "/app");
                                boolean success = true;
                                if (!folder.exists()) {
                                    success = folder.mkdir();
                                }

                                db = new ImageDatabaseHandler(getApplicationContext(),folder.toString());

                                Image image = new Image();

                                image.setId(mediaId);
                                image.setPath(filePath);
                                image.setName(filename);

                                db.addImage(image);
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

                return null;
            }
        }.execute(null, null, null);
    }
}
