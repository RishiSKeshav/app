package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
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
public class NewAlbum extends AppCompatActivity implements NewAlbumAdapter.MyViewHolder.ClickListener{

    private ArrayList<Media> mediaList = new ArrayList<Media>();
    private NewAlbumAdapter nmAdapter;
    private RecyclerView recyclerView;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private ArrayList<Integer> pos = new ArrayList<Integer>();
    private EditText album_title;
    String SHARED;

    View main_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Displays Home Screen
        setContentView(R.layout.new_album);

        main_layout = (View) findViewById(R.id.main_layout);

        Toolbar new_album_toolbar= (Toolbar) findViewById(R.id.new_album_toolbar);
        setSupportActionBar(new_album_toolbar);
        TextView tv = (TextView) findViewById(R.id.tv_ld_header);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent i = getIntent();
        SHARED = i.getStringExtra("shared");

        album_title = (EditText)findViewById(R.id.album_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(SHARED.equals("yes")){
            tv.setText("New shared album");
        }else {
            tv.setText("New album");
        }
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_new_album);

        nmAdapter = new NewAlbumAdapter(mediaList,this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),4);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(nmAdapter);
        prepareMediaData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onItemClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

//    @Override
//    public boolean onItemLongClicked(int position) {
//        if (actionMode == null) {
//            actionMode = startSupportActionMode(actionModeCallback);
//        }
//
//        toggleSelection(position);
//
//        return true;
//    }


    private void toggleSelection(int position) {
        nmAdapter.toggleSelection(position);
        int count = nmAdapter.getSelectedItemCount();
        List<Integer> cnt = nmAdapter.getSelectedItems();

        pos.clear();
        for(int i=0;i<cnt.size();i++){
            Media m = mediaList.get(cnt.get(i));
            pos.add(Integer.parseInt(m.getId()));
        }

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.menu_new_album, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.selected_menu:
                    if (SHARED.equals("yes")) {
                        String at = album_title.getText().toString();
                        Intent intent = new Intent(NewAlbum.this, Userbase.class);
                        intent.putExtra("action","create_shared_album");
                        intent.putExtra("album_name",at);
                        intent.putIntegerArrayListExtra("mediaId", pos);
                        startActivity(intent);

                    } else {

                        String at = album_title.getText().toString();
                        try {
                            JSONArray a = new JSONArray(pos);
                            JSONObject obj = new JSONObject();
                            obj.put("userId", "1");
                            obj.put("shared", "No");
                            obj.put("name", at);
                            obj.put("mediaId", a);
                            StringEntity jsonString = new StringEntity(obj.toString());


                            AsyncHttpClient client = new AsyncHttpClient();

                            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Createalbum", jsonString, "application/json", new AsyncHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    // called before request is started
                                }

                                // @Override
                                public void onSuccess(String response) {
                                    // called when response HTTP status is "200 OK"
                                    try {
                                        final JSONObject obj = new JSONObject(response);


                                        if (obj.getBoolean("error")) {
                                            SnackbarManager.show(
                                                    com.nispok.snackbar.Snackbar.with(getApplicationContext())
                                                            .text("Something went wrong")
                                                            .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                            );
                                        } else {


                                            SnackbarManager.show(
                                                    com.nispok.snackbar.Snackbar.with(getApplicationContext())
                                                            .text("New album created")
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

                                                                    try {
                                                                        Intent homeIntent = new Intent(getApplicationContext(), AlbumMediaDisplay.class);
                                                                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        homeIntent.putExtra("Id", obj.getString("albumId"));
                                                                        homeIntent.putExtra("Name", obj.getString("name"));
                                                                        startActivity(homeIntent);

                                                                    }catch (JSONException e) {
                                                                        // TODO Auto-generated catch block
                                                                        Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                                                                        e.printStackTrace();

                                                                    }
                                                                }

                                                                @Override
                                                                public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                                                }
                                                            })
                                                    , NewAlbum.this);

//
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

                        mode.finish();
                        return true;
                    }

                        default:
                            return false;
                    }


        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            nmAdapter.clearSelection();
            actionMode = null;
        }
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

                            if(mediaarray.length()==0) {

                                for (int i = 0; i < mediaarray.length(); i++) {
                                    JSONObject mediadetails = mediaarray.optJSONObject(i);

                                    Media m = new Media(mediadetails.optString("id"), mediadetails.optString("name"),
                                            mediadetails.optString("path"), mediadetails.optString("date"));
                                    mediaList.add(m);
                                    nmAdapter.notifyDataSetChanged();
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
