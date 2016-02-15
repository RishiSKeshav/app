package com.rishi.app.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by RishiS on 2/14/2016.
 */
public class ImageUploadService extends Service {


    ArrayList<Image> unSyncedImageList;
    SessionManager sessionManager;

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
                Log.d("image path: ",img.getPath());
                Bitmap bitmap = BitmapFactory.decodeFile(img.getPath());
                encodeImagetoString(img,bitmap);
                i++;
            }
        }
    }

    @Override
    public void onDestroy() {

        Log.d("service"," service ended");

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public void encodeImagetoString(Image img,Bitmap bitmap) {

        RequestParams params = new RequestParams();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        String encodedString = Base64.encodeToString(byte_arr, 0);

        params.put("image", encodedString);
        params.put("userId", "1");
        params.put("filename", img.getName());

        triggerImageUpload(params);
    }

    public void triggerImageUpload(RequestParams params) {
        makeHTTPCall(params);
    }


    public void makeHTTPCall(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://52.89.2.186/project/webservice/uploadMedia.php",
                params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            Log.i("response", response);
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                            } else {



                        }
                        } catch (JSONException e) {



                           // TODO Auto-generated catch block
                            Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }

                    public void onFailure(int statusCode, Throwable error,
                                          String content) {

                        //prgDialog.hide();
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error Occured n Most Common Error: n1. Device not connected to Internetn2. Web App is not deployed in App servern3. App server is not runningn HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

}
