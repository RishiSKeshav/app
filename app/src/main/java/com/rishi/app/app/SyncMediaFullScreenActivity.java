package com.rishi.app.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SyncMediaFullScreenActivity extends AppCompatActivity {

    private ViewPager viewPager;
    SyncFullScreenAdapter adapter;
    String action;
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
        String position = i.getStringExtra("Position");
        action = i.getStringExtra("action");

         data = i.getStringArrayListExtra("data");

        adapter = new SyncFullScreenAdapter(SyncMediaFullScreenActivity.this,
                data);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(Integer.parseInt(position));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sync_media_full_screen, menu);

        if(action.equals("unsync")){
            menu.getItem(0).setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
//            Intent intent = new Intent(SyncMediaFullScreenActivity.this,SyncMediaDisplayActivity.class);
//            intent.putExtra("action",action);
//            startActivity(intent);
            Log.i("ooo","reachedback");
            onBackPressed();
    }
        if(id == R.id.delete_sync_media_display){

            deleteMedia();
        }



        return super.onOptionsItemSelected(item);
    }


    private void deleteMedia(){

        int pos = viewPager.getCurrentItem();
        String path = data.get(pos);


        db = new ImageDatabaseHandler(getApplicationContext(), Environment.getExternalStorageDirectory().toString() + "/ClikApp");
        final int mediaId = db.getMediaId(path);
        db.close();
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

                                                            db.deleteMedia(mediaId);



                                                            Log.i("countafte", String.valueOf(db.getCount()));

                                                            db.close();

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



}
