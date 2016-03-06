package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
 * Created by amitrajula on 2/6/16.
 */
public class ToPersonalAlbum extends AppCompatActivity implements ToPersonalAlbumAdapter.MyViewHolder.ClickListener{

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private RecyclerView recyclerView;
    private ToPersonalAlbumAdapter tpaAdapter;
    private List<Album> personalalbumList = new ArrayList<>();
    private ArrayList<AlbumMedia> albummediaList = new ArrayList<>();
    private ArrayList<Integer> pos = new ArrayList<Integer>();
    private ArrayList<Integer> albumid = new ArrayList<>();
    String ID,NAME,SHARED;
    String imagedisplay="",action ="";

    SessionManager sessionManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Displays Home Screen
        setContentView(R.layout.to_personal_album);

        sessionManager = new SessionManager(getApplicationContext());

        Intent i = getIntent();
        if(i.hasExtra("imagedisplay")){
            imagedisplay = i.getStringExtra("imagedisplay");
            ID = i.getStringExtra("Id");
        }else {

            if(i.hasExtra("action")){
                action = i.getStringExtra("action");
                pos = i.getIntegerArrayListExtra("mediaId");
            }else {

                pos = i.getIntegerArrayListExtra("mediaId");
                ID = i.getStringExtra("Id");
                NAME = i.getStringExtra("Name");
                SHARED = i.getStringExtra("shared");

                if (SHARED.equals("no")) {
                    albummediaList = i.getParcelableArrayListExtra("al");
                }
            }
        }

        Toolbar toolbar= (Toolbar) findViewById(R.id.to_personal_album_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_to_personal_album);
        tpaAdapter = new ToPersonalAlbumAdapter(personalalbumList,this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(tpaAdapter);

        preparePersonalAlbumData();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if(imagedisplay.equals("")){

                if(action.equals("sync") || action.equals("camera")){
                    onBackPressed();
                }else{

                if (SHARED.equals("no")) {
                    Intent i = new Intent(ToPersonalAlbum.this, AlbumMediaSelect.class);
                    i.putParcelableArrayListExtra("al", albummediaList);
                    i.putExtra("id", ID);
                    i.putExtra("name", NAME);
                    ToPersonalAlbum.this.startActivity(i);
                } else {
                    Intent i = new Intent(ToPersonalAlbum.this, SharedAlbumMediaSelect.class);
                    i.putExtra("Id", ID);
                    i.putExtra("Name", NAME);
                    ToPersonalAlbum.this.startActivity(i);
                }
                }
            }else {
                Intent i = new Intent(ToPersonalAlbum.this, SharedMediaDisplay.class);
                i.putExtra("image", imagedisplay);
                i.putExtra("Id",ID);
                ToPersonalAlbum.this.startActivity(i);
            }
         //   onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }




    private void toggleSelection(int position) {
        tpaAdapter.toggleSelection(position);
        int count = tpaAdapter.getSelectedItemCount();
        List<Integer> cnt = tpaAdapter.getSelectedItems();

        albumid.clear();
        for(int i=0;i<cnt.size();i++){
            Album a = personalalbumList.get(cnt.get(i));
            albumid.add(Integer.parseInt(a.getId()));
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
            mode.getMenuInflater().inflate (R.menu.menu_to_personal_album, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.done_to_personal_album:

                    if(imagedisplay.equals("") || action.equals("sync") || action.equals("camera")){

                       // if(SHARED.equals("no")) {
                            try {
                                    JSONArray mediapos = new JSONArray(pos);
                                    JSONArray albumids = new JSONArray(albumid);
                                    JSONObject obj = new JSONObject();
                                    obj.put("userId", sessionManager.getId());
                                    obj.put("mediaId", mediapos);
                                    obj.put("albumId", albumids);
                                    obj.put("shared", "no");

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
                                                SnackbarManager.show(
                                                        com.nispok.snackbar.Snackbar.with(getApplicationContext())
                                                                .text("Something went wrong")
                                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                                );
                                            } else {
                                                SnackbarManager.show(
                                                        com.nispok.snackbar.Snackbar.with(getApplicationContext())
                                                                .text(obj.getString("msg"))
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

                                                                        if(action.equals("sync") || action.equals("camera")){
                                                                            onBackPressed();

                                                                        }else {

                                                                            if (SHARED.equals("no")) {
                                                                                Intent i = new Intent(ToPersonalAlbum.this, AlbumMediaDisplay.class);
                                                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                i.putExtra("Id", ID);
                                                                                i.putExtra("Name", NAME);
                                                                                i.putExtra("shared", SHARED);
                                                                                ToPersonalAlbum.this.startActivity(i);
                                                                                finish();
                                                                            } else {
                                                                                Intent i = new Intent(ToPersonalAlbum.this, SharedAlbumMediaDisplay.class);
                                                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                i.putExtra("Id", ID);
                                                                                i.putExtra("Name", NAME);
                                                                                i.putExtra("shared", SHARED);
                                                                                ToPersonalAlbum.this.startActivity(i);
                                                                                finish();
                                                                            }
                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                                    }

                                                                    @Override
                                                                    public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                                                    }
                                                                })
                                                        , ToPersonalAlbum.this);
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
//                        }else{
//
//                        }
                    }else{
                        try {
                            JSONArray albumids = new JSONArray(albumid);
                            JSONObject obj = new JSONObject();
                            obj.put("userId", sessionManager.getId());
                            obj.put("albumId", albumids);
                            obj.put("mediaId", ID);
                            StringEntity jsonString = new StringEntity(obj.toString());


                            AsyncHttpClient client = new AsyncHttpClient();

                            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Movesharedmedia", jsonString, "application/json", new AsyncHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    // called before request is started
                                }

                                // @Override
                                public void onSuccess(String response) {
                                    // called when response HTTP status is "200 OK"
                                    try {
                                        //Log.i("response",response);
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
                                                            .text(obj.getString("msg"))
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

                                                                        Intent i = new Intent(ToPersonalAlbum.this, HomeActivity.class);
                                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        i.putExtra("shared_media", "2");
                                                                        ToPersonalAlbum.this.startActivity(i);
                                                                    finish();
                                                                }

                                                                @Override
                                                                public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                                                }
                                                            })
                                                    , ToPersonalAlbum.this);
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
            tpaAdapter.clearSelection();
            actionMode = null;
        }
    }

    private void preparePersonalAlbumData(){
        personalalbumList.clear();
        try {
            JSONObject obj = new JSONObject();
            obj.put("userId", sessionManager.getId());
            obj.put("shared", "no");
            StringEntity jsonString = new StringEntity(obj.toString());


            AsyncHttpClient client = new AsyncHttpClient();

            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Getalbum", jsonString, "application/json", new AsyncHttpResponseHandler() {

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


                                Album al = new Album(albumdetails.optString("id"),albumdetails.optString("name"), albumdetails.optString("thumbnail"),
                                        albumdetails.optString("count"), albumdetails.optString("date"));
                                personalalbumList.add(al);
                                tpaAdapter.notifyDataSetChanged();
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
