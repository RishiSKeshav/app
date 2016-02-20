package com.rishi.app.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amitrajula on 2/4/16.
 */
public class AddMoreMedia extends AppCompatActivity implements AddMoreMediaAdapter.MyViewHolder.ClickListener{

    private ArrayList<Media> mediaList = new ArrayList<Media>();
    private AddMoreMediaAdapter mmAdapter;
    private RecyclerView recyclerView;
    private List<Integer> pos = new ArrayList<Integer>();
    ArrayList<String> ids= new ArrayList<String>();
    String ID,NAME,SHARED;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Displays Home Screen
        setContentView(R.layout.add_more_media);

        Toolbar add_more_toolbar= (Toolbar) findViewById(R.id.add_more_toolbar);
        setSupportActionBar(add_more_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        ids = intent.getStringArrayListExtra("albummediaids");
        ID = intent.getStringExtra("id");
        NAME = intent.getStringExtra("name");
        SHARED = intent.getStringExtra("shared");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_add_more_media);

        mmAdapter = new AddMoreMediaAdapter(mediaList,this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),4);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mmAdapter);
        prepareMediaData();



    }




    @Override
    public void onItemClicked(int position) {
        toggleSelection(position);
    }

    @Override
    public boolean onItemLongClicked(int position) {
        toggleSelection(position);
        return true;
    }


    private void toggleSelection(int position) {
        mmAdapter.toggleSelection(position);
        int count = mmAdapter.getSelectedItemCount();
        List<Integer> cnt = mmAdapter.getSelectedItems();

        pos.clear();
        for(int i=0;i<cnt.size();i++){
            Media m = mediaList.get(cnt.get(i));
            pos.add(Integer.parseInt(m.getId()));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_more, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){

            if(SHARED.equals("no")){
                Intent i = new Intent(AddMoreMedia.this,AlbumMediaDisplay.class);
                i.putExtra("Id",ID);
                i.putExtra("Name",NAME);
                AddMoreMedia.this.startActivity(i);
            }else{
                Intent i = new Intent(AddMoreMedia.this,SharedAlbumMediaDisplay.class);
                i.putExtra("Id",ID);
                i.putExtra("Name",NAME);
                AddMoreMedia.this.startActivity(i);
            }

        //    onBackPressed();
            return true;
        }

        if (id == R.id.done_add_more) {


            try {
                JSONArray a = new JSONArray(pos);
                JSONArray previous = new JSONArray(ids);
                JSONObject obj = new JSONObject();
                obj.put("userId", "1");
                obj.put("albumId", ID);
                obj.put("mediaId",a);
                obj.put("previousmedia",previous);
                StringEntity jsonString = new StringEntity(obj.toString());


                AsyncHttpClient client = new AsyncHttpClient();

                client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Mediaalbum", jsonString, "application/json", new AsyncHttpResponseHandler() {

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


//

                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(getApplicationContext())
                                                .text(obj.optString("msg"))
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
                                                        if(SHARED.equals("no")) {
                                                        Intent i = new Intent(AddMoreMedia.this, AlbumMediaDisplay.class);
                                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        i.putExtra("Id", ID);
                                                        i.putExtra("Name", NAME);
                                                        AddMoreMedia.this.startActivity(i);
                                                        }else{
                                                        Intent i = new Intent(AddMoreMedia.this, SharedAlbumMediaDisplay.class);
                                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        i.putExtra("Id", ID);
                                                        i.putExtra("Name", NAME);
                                                        AddMoreMedia.this.startActivity(i);
                                                    }

                                                    }

                                                    @Override
                                                    public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                    }

                                                    @Override
                                                    public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                                    }
                                                })
                                        ,AddMoreMedia.this);



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



            return true;
        }


        return super.onOptionsItemSelected(item);
    }





    private void prepareMediaData() {
        mediaList.clear();
        try {
            JSONObject obj = new JSONObject();
            obj.put("userId", "1");
            StringEntity jsonString = new StringEntity(obj.toString());


            AsyncHttpClient client = new AsyncHttpClient();

            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Getmedia", jsonString, "application/json", new AsyncHttpResponseHandler() {

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
                            JSONArray mediaarray = mediaObj.optJSONArray("media");

                            for (int i = 0; i < mediaarray.length(); i++) {
                                JSONObject mediadetails = mediaarray.optJSONObject(i);

                                Media m = new Media(mediadetails.optString("id"),mediadetails.optString("name"),
                                        mediadetails.optString("path"),mediadetails.optString("date"));
                                mediaList.add(m);
                                mmAdapter.notifyDataSetChanged();

                                if(ids.contains(mediadetails.optString("id"))){
                                    pos.add(Integer.parseInt(mediadetails.optString("id")));
                                    mmAdapter.toggleSelection(i);
                                }
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
