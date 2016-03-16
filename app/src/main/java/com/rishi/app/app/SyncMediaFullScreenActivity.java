package com.rishi.app.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SyncMediaFullScreenActivity extends AppCompatActivity {

    private ViewPager viewPager;
    SyncFullScreenAdapter adapter;
    String action,position;
    SessionManager sessionManager;
    ArrayList<String> data = new ArrayList<>();
    ImageDatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_media_full_screen);
        sessionManager = new SessionManager(getApplicationContext());

        Toolbar toolbar= (Toolbar) findViewById(R.id.full_screen_display_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.syncViewPager);

        Intent i = getIntent();
        position = i.getStringExtra("Position");

        //Log.i("position",position);
        action = i.getStringExtra("action");
        data = i.getStringArrayListExtra("data");
        adapter = new SyncFullScreenAdapter(SyncMediaFullScreenActivity.this,
                data);

        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        viewPager.setCurrentItem(Integer.parseInt(position));

       /* viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(Integer.parseInt(position));
            }
        },100);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sync_media_full_screen, menu);

        if(action.equals("unsync")){
            menu.getItem(0).setVisible(false);
            menu.getItem(2).setVisible(false);
        }
        if(action.equals("sync")){
            menu.getItem(1).setVisible(false);
        }
        if(action.equals("camera"))
            menu.getItem(1).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
    }
        if(id == R.id.delete_sync_media_display){

            deleteMedia();
        }

        if(id == R.id.upload_sync_media_display){

            final ProgressDialog ringProgressDialog = ProgressDialog.show(SyncMediaFullScreenActivity.this, "Please wait ...", "Uploading Image ...", true);
            ringProgressDialog.setCancelable(true);

            int pos = viewPager.getCurrentItem();
            final String path = data.get(pos);

            Runnable r = new Runnable() {
                public void run() {
                   upload(path);
                    ringProgressDialog.dismiss();

                }
            };

            Thread t = new Thread(r);
            t.start();


        }

        if (id == R.id.to_personal_album) {
            final int mediaId = getMediaId();
            //Log.i("idssss",String.valueOf(mediaId));
            ArrayList<Integer> mediaIDS = new ArrayList<>();
            mediaIDS.add(mediaId);
            Intent i = new Intent(SyncMediaFullScreenActivity.this,ToPersonalAlbum.class);
            i.putExtra("action",action);
            i.putExtra("mediaId", mediaIDS);
            startActivity(i);
        }

        if (id == R.id.to_shared_album) {
            final int mediaId = getMediaId();
            ArrayList<Integer> mediaIDS = new ArrayList<>();
            mediaIDS.add(mediaId);
            Intent i = new Intent(SyncMediaFullScreenActivity.this,ToSharedAlbum.class);
            i.putExtra("action",action);
            i.putExtra("mediaId", mediaIDS);
            startActivity(i);
        }

        if (id == R.id.to_others) {
            final int mediaId = getMediaId();
            ArrayList<Integer> mediaIDS = new ArrayList<>();
            mediaIDS.add(mediaId);
            Intent i = new Intent(SyncMediaFullScreenActivity.this,Userbase.class);
            i.putExtra("action",action);
            i.putExtra("data",data);
            i.putExtra("position",position);
            i.putIntegerArrayListExtra("mediaId", mediaIDS);
//
            startActivity(i);
        }



        return super.onOptionsItemSelected(item);
    }



    public void upload(String path) {



        File imgFile = new File(path);

        if (imgFile.exists()) {

            Long fileSize = imgFile.length();
            //Log.d("Image upload size",String.valueOf(imgFile.length()));

            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.toString());

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
                            }
                        });

                // Adding file data to http body
                entity.addPart("image", new StringBody(encodedString));

                // Extra parameters if you want to pass to server
                entity.addPart("userId", new StringBody(sessionManager.getId()));
                entity.addPart("filename", new StringBody(imgFile.getName()));
                entity.addPart("filesize", new StringBody(fileSize.toString()));

                httppost.setEntity(entity);

                //Log.d("entity",entity.toString());

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);

                    //Log.d("response", responseString);
                    try {
                        JSONObject obj = new JSONObject(responseString);

                        if (obj.getBoolean("error")) {

                            //Log.d("file error", obj.getString("msg"));
                            //Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                        } else {
                            int mediaId = Integer.parseInt(obj.getString("mediaId"));
                            String link = obj.getString("link");

                            //Log.d("response ok", String.valueOf(mediaId) + " " + link);

                            //Log.d("link", link);

                            File folder = new File(Environment.getExternalStorageDirectory().toString() + "/ClikApp");
                            boolean success = true;
                            if (!folder.exists()) {
                                success = folder.mkdir();
                            }

                            db = new ImageDatabaseHandler(getApplicationContext(), folder.toString());

                            Image image = new Image();

                            image.setId(mediaId);
                            image.setPath(path);
                            image.setName(imgFile.getName());
                            image.setLink(link);

                            db.addImage(image, sessionManager.getId());

                            db.close();

                            SnackbarManager.show(
                                    com.nispok.snackbar.Snackbar.with(getApplicationContext())
                                            .text("Media Uploaded Successfully")
                                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
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

                                                    Intent i = new Intent(getApplicationContext(),SyncMediaDisplayActivity.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    i.putExtra("action",action);
                                                    getApplicationContext().startActivity(i);
                                                }

                                                @Override
                                                public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                }

                                                @Override
                                                public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                                }
                                            })
                                    , SyncMediaFullScreenActivity.this);


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


    private void deleteMedia(){

        final int mediaId = getMediaId();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to delete? This media will be deleted from all personal and shared album.");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("userId", sessionManager.getId());
                    obj.put("mediaId",mediaId);
                    StringEntity jsonString = new StringEntity(obj.toString());


                    AsyncHttpClient client = new AsyncHttpClient();

                    client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Deletemedia", jsonString, "application/json", new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart() {
                            // called before request is started
                        }

                        // @Override
                        public void onSuccess(String response) {
                            // called when response HTTP status is "200 OK"

                            Log.i("responseDelete",response.toString());
                            try {
                                JSONObject obj = new JSONObject(response);

                                if (obj.getBoolean("error")) {
                                    SnackbarManager.show(
                                            com.nispok.snackbar.Snackbar.with(getApplicationContext())
                                                    .text("Something went wrong")
                                                    .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                    );
                                } else {

                                    SnackbarManager.show(
                                            com.nispok.snackbar.Snackbar.with(getApplicationContext())
                                                    .text("Media Deleted")
                                                    .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
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

                                                            db = new ImageDatabaseHandler(getApplicationContext(), Environment.getExternalStorageDirectory().toString() + "/ClikApp");

                                                            //Log.i("count initial", String.valueOf(db.getCount()));
                                                            db.deleteMedia(mediaId);

                                                            //Log.i("countafte", String.valueOf(db.getCount()));

                                                            db.close();

                                                            Intent i = new Intent(getApplicationContext(),SyncMediaDisplayActivity.class);
                                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            i.putExtra("action",action);
                                                            getApplicationContext().startActivity(i);
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                        }

                                                        @Override
                                                        public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                                        }
                                                    })
                                            , SyncMediaFullScreenActivity.this);

                                }

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();

                            }
                        }

                        //@Override
                        public void onFailure(int statusCode, PreferenceActivity.Header[] headers, byte[] errorResponse, Throwable e) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        }

                        //@Override
                        public void onRetry(int retryNo) {
                            // called when request is retried
                        }


                    });

                }catch (JSONException e) {
                    e.printStackTrace();
                }catch(UnsupportedEncodingException ee){
                    ee.printStackTrace();
                }

            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private int getMediaId(){

        int pos = viewPager.getCurrentItem();
        String path = data.get(pos);

        db = new ImageDatabaseHandler(getApplicationContext(), Environment.getExternalStorageDirectory().toString() + "/ClikApp");
        final int mediaId = db.getMediaId(path, sessionManager.getId());
        db.close();

        return mediaId;
    }
}
