package com.rishi.app.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class CameraFullImageDisplayActivity extends AppCompatActivity {

    private CameraImageAdapter adapter;
    private ViewPager viewPager;
    ImageDatabaseHandler db;
    SessionManager sessionManager;

    ArrayList<Image> photoImageList;
    long totalSize=0;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_full_image_display);

        sessionManager = new SessionManager(getApplicationContext());

        viewPager = (ViewPager) findViewById(R.id.pager);

        sessionManager = new SessionManager(getApplicationContext());

        Toolbar toolbar= (Toolbar) findViewById(R.id.camera_full_image_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        photoImageList =intent.getParcelableArrayListExtra("photoImageList");
        ArrayList<String> photoList = intent.getStringArrayListExtra("photoFileList");

        Log.d("photolist size",photoList.toString());

        ArrayList<File> photoFileList = new ArrayList<File>();


        adapter = new CameraImageAdapter(CameraFullImageDisplayActivity.this,
                photoList);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(photoList.size());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_camera_full_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent1 = new Intent(this,HomeActivity.class);
                startActivity(intent1);
                return true;
            case R.id.camera_upload:

                final ProgressDialog progressDialog = new ProgressDialog(CameraFullImageDisplayActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Uploading...");
                progressDialog.show();

                startUpload(photoImageList);


                progressDialog.hide();


                SnackbarManager.show(
                        com.nispok.snackbar.Snackbar.with(this)
                                .text("Uploaded Successfully")
                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                .eventListener(new EventListener() {
                                    @Override
                                    public void onShow(com.nispok.snackbar.Snackbar snackbar) {

                                    }

                                    @Override
                                    public void onShowByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                    }

                                    @Override
                                    public void onShown(com.nispok.snackbar.Snackbar snackbar) {

                                    }

                                    @Override
                                    public void onDismiss(com.nispok.snackbar.Snackbar snackbar) {


                                        Intent intent2 = new Intent(CameraFullImageDisplayActivity.this, CameraActivity.class);
                                        startActivity(intent2);

                                    }

                                    @Override
                                    public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                    }

                                    @Override
                                    public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                    }
                                })
                        , CameraFullImageDisplayActivity.this);






                return true;
        }
        return(super.onOptionsItemSelected(item));
    }


    public void startUpload(final ArrayList<Image> unSyncedImageList)
    {



        Log.d("Service: count ", String.valueOf(unSyncedImageList.size()));

        count = unSyncedImageList.size();

        Runnable r = new Runnable() {
            public void run() {

                for (int i = 0; i < unSyncedImageList.size(); i++) {
                    synchronized (this) {
                        try {

                           /* progressDialog.setMessage("Uploading" + i+1 + "of" + unSyncedImageList.size());
                            progressDialog.show();*/
                            upload(unSyncedImageList.get(i), count);

                        } catch (Exception e) {
                        }
                    }
                }


            }
        };

        Thread t = new Thread(r);
        t.start();

        db = new ImageDatabaseHandler(getApplicationContext(), Environment.getExternalStorageDirectory().toString()+ "/ClikApp");
        Log.d("Db count", String.valueOf(db.getCount()));

    }
    public void upload(Image img,int count)
    {
        File f = new File(img.getPath());
        Intent intent = new Intent("IMAGE_ACTION");
        intent.putExtra("imgPath", img.path);
        intent.putExtra("leftCount", count);
        sendBroadcast(intent);

        String filePath=img.getPath();
        String filename = img.getName();

        /*Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        String encodedString = Base64.encodeToString(byte_arr, 0);*/

        String responseString = null;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://52.89.2.186/project/webservice/cameraMediaUpload.php");

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {

                            double y = (long) (((float) num / totalSize) * 100);

                           /* Intent i = new Intent("PROGRESS_ACTION");
                            i.putExtra("NUMBER",(int)y);
                            sendBroadcast(i);*/
                        }
                    });

            // Adding file data to http body
            //entity.addPart("image", new StringBody(encodedString));

            entity.addPart("image", new FileBody(new File(filePath)));
            // Extra parameters if you want to pass to server
            entity.addPart("userId", new StringBody(sessionManager.getId()));
            entity.addPart("filesize", new StringBody(String.valueOf(f.length())));
            //  entity.addPart("name", new StringBody(filename));

            totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
                Log.d("response 200",responseString);

                try {
                    JSONObject obj = new JSONObject(responseString);

                    Log.d("obh",String.valueOf(obj));
                    if (obj.getBoolean("error")) {
                        Log.d("error true", "true");

                        f.delete();
                        //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    } else {

                        Log.d("error false:", responseString);
                        //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        int mediaId = Integer.parseInt(obj.getString("mediaId"));
                        String link = obj.getString("link");
                        String fileName = obj.getString("fileName");

                        Log.d("link",link);

                        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/ClikApp");
                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdir();
                        }

                        db = new ImageDatabaseHandler(getApplicationContext(), folder.toString());

                        Image image = new Image();

                        image.setId(mediaId);
                        image.setPath(filePath);
                        image.setName(fileName);
                        image.setLink(link);

                        db.addCameraImage(image,sessionManager.getId());

                        count--;

                        f.delete();

                        Log.d("image inserted",String.valueOf(db.getCount()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;

                Log.d("response error",responseString);
            }

        } catch (ClientProtocolException e) {
            responseString = e.toString();
        } catch (IOException e) {
            responseString = e.toString();
        }
    }
}