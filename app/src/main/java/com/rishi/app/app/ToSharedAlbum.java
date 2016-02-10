package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amitrajula on 2/6/16.
 */
public class ToSharedAlbum extends AppCompatActivity implements ToSharedAlbumAdapter.MyViewHolder.ClickListener{

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private RecyclerView recyclerView;
    private ToSharedAlbumAdapter tsaAdapter;
    private List<SharedAlbum> sharedalbumList = new ArrayList<>();
    private ArrayList<Integer> pos = new ArrayList<Integer>();
    private ArrayList<Integer> albumid = new ArrayList<>();
    String ID,NAME,SHARED;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Displays Home Screen
        setContentView(R.layout.to_shared_album);

        getSupportActionBar().setTitle("Select Shared Album");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        pos= i.getIntegerArrayListExtra("mediaId");
        ID = i.getStringExtra("Id");
        NAME = i.getStringExtra("Name");
        SHARED = i.getStringExtra("shared");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_to_shared_album);
        tsaAdapter = new ToSharedAlbumAdapter(sharedalbumList,this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(tsaAdapter);

        prepareSharedAlbumData();

    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);

        return true;
    }



    private void toggleSelection(int position) {
        tsaAdapter.toggleSelection(position);
        int count = tsaAdapter.getSelectedItemCount();
        List<Integer> cnt = tsaAdapter.getSelectedItems();

        albumid.clear();
        for(int i=0;i<cnt.size();i++){
            SharedAlbum sa = sharedalbumList.get(cnt.get(i));
            albumid.add(Integer.parseInt(sa.getId()));
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
            mode.getMenuInflater().inflate (R.menu.menu_to_shared_album, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.done_to_shared_album:

                    try {
                        JSONArray mediapos = new JSONArray(pos);
                        JSONArray albumids = new JSONArray(albumid);
                        JSONObject obj = new JSONObject();
                        obj.put("userId", "1");
                        obj.put("mediaId", mediapos);
                        obj.put("albumId",albumids);
                        obj.put("shared","yes");
                        StringEntity jsonString = new StringEntity(obj.toString());


                        AsyncHttpClient client = new AsyncHttpClient();

                        client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Sharetoalbum", jsonString, "application/json", new AsyncHttpResponseHandler() {

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

                                        if(SHARED.equals("no")) {
                                            Intent i = new Intent(ToSharedAlbum.this, AlbumMediaDisplay.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            i.putExtra("Id",ID);
                                            i.putExtra("Name",NAME);
                                            i.putExtra("shared",SHARED);
                                            ToSharedAlbum.this.startActivity(i);
                                        }else{
                                            Intent i = new Intent(ToSharedAlbum.this, SharedAlbumMediaDisplay.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            i.putExtra("Id",ID);
                                            i.putExtra("Name",NAME);
                                            i.putExtra("shared",SHARED);
                                            ToSharedAlbum.this.startActivity(i);
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



                    mode.finish();
                    return true;

                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            tsaAdapter.clearSelection();
            actionMode = null;
        }
    }

    private void prepareSharedAlbumData(){
        sharedalbumList.clear();
        try {
            JSONObject obj = new JSONObject();
            obj.put("userId", "1");
            obj.put("shared", "yes");
            StringEntity jsonString = new StringEntity(obj.toString());


            AsyncHttpClient client = new AsyncHttpClient();

            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Getsharedalbum", jsonString, "application/json", new AsyncHttpResponseHandler() {

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

                            JSONObject albumObj = obj.getJSONObject("outputObj");
                            JSONArray albumarray = albumObj.optJSONArray("album");

                            for (int i = 0; i < albumarray.length(); i++) {
                                JSONObject albumdetails = albumarray.optJSONObject(i);


                                SharedAlbum sa = new SharedAlbum(albumdetails.optString("id"),albumdetails.optString("name"), albumdetails.optString("thumbnail"),
                                        albumdetails.optString("count"), albumdetails.optString("date"), albumdetails.optString("members"));
                                sharedalbumList.add(sa);
                                tsaAdapter.notifyDataSetChanged();
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
