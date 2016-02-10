package com.rishi.app.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by amitrajula on 2/7/16.
 */
public class SharedAlbumMediaDisplay extends AppCompatActivity {

    private ArrayList<SharedAlbumMedia> sharedalbumMediaList = new ArrayList<>();
    private SharedAlbumMediaAdapter samAdapter;
    private RecyclerView recyclerView;
    TextView mediacount;
    EditText edit_album_title;
    int count = 0;
    String ID, NAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Displays Home Screen
        setContentView(R.layout.shared_album_media_display);

        mediacount = (TextView) findViewById(R.id.media_count);
        edit_album_title = (EditText) findViewById(R.id.edit_album_title);

        recyclerView = (RecyclerView) findViewById(R.id.shared_album_media_recycler_view);
        samAdapter = new SharedAlbumMediaAdapter(sharedalbumMediaList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(samAdapter);

        Intent intent = getIntent();
        ID = intent.getStringExtra("Id");
        NAME = intent.getStringExtra("Name");



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(NAME);

        preapareSharedAlbumMedia();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shared_album_media, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add_media) {
            ArrayList<String> ids = new ArrayList<String>();
            for(int i=0;i<sharedalbumMediaList.size();i++){
                if(sharedalbumMediaList.get(i).getId().equals("1")){
                    ArrayList<Media> m = sharedalbumMediaList.get(i).getMedia();
                    for(int j=0;j<m.size();j++){
                        ids.add(m.get(j).getId());
                    }
                }
            }

            Intent intent = new Intent(this,AddMoreMedia.class);
            intent.putExtra("id",ID);
            intent.putExtra("name", NAME);
            intent.putExtra("shared","yes");
            intent.putStringArrayListExtra("albummediaids", ids);
            this.startActivity(intent);
            return true;
        }

        if (id == R.id.select) {

            Intent i = new Intent(SharedAlbumMediaDisplay.this,SharedAlbumMediaSelect.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("Id", ID);
            i.putExtra("Name", NAME);
            this.startActivity(i);

            return true;
        }

        if (id == R.id.edit_title) {
            showeditalbumtitle();
            return true;
        }

        if(id == R.id.delete_album){
            showleave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public void showeditalbumtitle(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.shared_album_edit_album_title, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit_shared_album_title);
        edt.setText(NAME);


        dialogBuilder.setTitle("Edit Album Title");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final EditText edt1 = (EditText) dialogView.findViewById(R.id.edit_shared_album_title);

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("userId", "1");
                    obj.put("shared","yes");
                    obj.put("albumId",ID);
                    obj.put("name",edt1.getText().toString());
                    StringEntity jsonString = new StringEntity(obj.toString());


                    AsyncHttpClient client = new AsyncHttpClient();

                    client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Updatealbumtitle", jsonString, "application/json", new AsyncHttpResponseHandler() {

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
                                    Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();

                                    Intent i = new Intent(SharedAlbumMediaDisplay.this,SharedAlbumMediaDisplay.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("Id", ID);
                                    i.putExtra("Name",edt1.getText().toString());
                                    SharedAlbumMediaDisplay.this.startActivity(i);

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

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();

    }


    public void showleave(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Leave from Album?");
        alertDialogBuilder.setMessage("You will no longer be accessed to this, unless someone adds you back");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("userId", "1");
                    obj.put("shared","yes");
                    obj.put("albumId",ID);
                    StringEntity jsonString = new StringEntity(obj.toString());


                    AsyncHttpClient client = new AsyncHttpClient();

                    client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Deletealbum", jsonString, "application/json", new AsyncHttpResponseHandler() {

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
                                    Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();

                                    Intent i = new Intent(getApplicationContext(),HomeActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(i);

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



    private void preapareSharedAlbumMedia(){

        try {
            JSONObject obj = new JSONObject();
            obj.put("userId", "1");
            obj.put("albumId",ID);
            obj.put("shared","yes");
            StringEntity jsonString = new StringEntity(obj.toString());


            AsyncHttpClient client = new AsyncHttpClient();

            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Getsharedalbummedia", jsonString, "application/json", new AsyncHttpResponseHandler() {

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
                            Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                        } else {
                            JSONObject mediaObj = obj.getJSONObject("outputObj");
                            JSONArray usermediaarray = mediaObj.optJSONArray("users");

                                for (int i = 0; i < usermediaarray.length(); i++) {
                                    JSONObject usermediadetails = usermediaarray.optJSONObject(i);
                                    JSONArray a = usermediadetails.optJSONArray("media");

                                    ArrayList<Media> m = new ArrayList<Media>();
                                    if(a.length() > 0) {
                                        for (int j = 0; j < a.length(); j++) {
                                            JSONObject jo = a.optJSONObject(j);
                                            Media md = new Media(jo.optString("id"), jo.optString("name"),
                                                    jo.optString("path"), jo.optString("createdOn"));


                                            m.add(md);
                                        }
                                    }
                                    SharedAlbumMedia smmedia = new SharedAlbumMedia(usermediadetails.optString("id"),usermediadetails.optString("firstName"),
                                            usermediadetails.optString("lastName"),usermediadetails.optString("displayPicture"),
                                    m);
                                    sharedalbumMediaList.add(smmedia);
                                    samAdapter.notifyDataSetChanged();


                            }
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
}