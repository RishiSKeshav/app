package com.rishi.app.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by amitrajula on 2/19/16.
 */
public class SharedMediaDisplay extends AppCompatActivity{

    String imagedisplay,ID;
    ArrayList<Integer> mediasIDS = new ArrayList<>();
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_media_display);

        sessionManager = new SessionManager(getApplicationContext());

        Toolbar shared_media_dsiplay_toolbar= (Toolbar) findViewById(R.id.shared_media_display_toolbar);
        setSupportActionBar(shared_media_dsiplay_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        imagedisplay = i.getStringExtra("image");
        ID = i.getStringExtra("Id");


        ImageView image = (ImageView)findViewById(R.id.shared_media_display_image);

        Picasso.with(getApplicationContext()).load(imagedisplay)
                .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
                .into(image);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shared_media_display, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

//        if(id == android.R.id.home){
////            Intent i = new Intent(SharedMediaDisplay.this,HomeActivity.class);
////            i.putExtra("shared_media","2");
////            startActivity(i);
//
//            onBackPressed();
//            return true;
//        }

        if(id == R.id.delete_shared_media_display){
            showdelete();
        }

        if (id == R.id.to_personal_album) {
            Intent i = new Intent(SharedMediaDisplay.this,ToPersonalAlbum.class);
            i.putExtra("imagedisplay",imagedisplay);
            i.putExtra("Id",ID);
            startActivity(i);
        }

        if (id == R.id.to_shared_album) {
            Intent i = new Intent(SharedMediaDisplay.this,ToSharedAlbum.class);
            i.putExtra("imagedisplay",imagedisplay);
            i.putExtra("Id",ID);
            startActivity(i);
        }

        if (id == R.id.to_others) {
            mediasIDS.add(Integer.parseInt(ID));
            Intent i = new Intent(SharedMediaDisplay.this,Userbase.class);
            i.putExtra("action","shared_media");
            i.putExtra("imagedisplay", imagedisplay);
            i.putExtra("Id",ID);
            i.putIntegerArrayListExtra("mediaId", mediasIDS);

            startActivity(i);
        }



        return super.onOptionsItemSelected(item);
    }



    public void showdelete(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Discard Media?");
        alertDialogBuilder.setMessage("You can still have this media from Media library");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("userId", sessionManager.getId());
                    obj.put("mediaId", ID);
                    StringEntity jsonString = new StringEntity(obj.toString());


                    AsyncHttpClient client = new AsyncHttpClient();

                    client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Deletesharedmedia", jsonString, "application/json", new AsyncHttpResponseHandler() {

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


                                                    Intent i = new Intent(SharedMediaDisplay.this, HomeActivity.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    SharedMediaDisplay.this.startActivity(i);


                                                }

                                                @Override
                                                public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                }

                                                @Override
                                                public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                                }
                                            })
                                            , SharedMediaDisplay.this);

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

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException ee) {
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
