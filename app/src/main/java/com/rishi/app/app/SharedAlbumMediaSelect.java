package com.rishi.app.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.List;

/**
 * Created by amitrajula on 2/7/16.
 */
public class SharedAlbumMediaSelect extends AppCompatActivity implements SharedAlbumMediaSelectAdapter.MyViewHolder.ClickListener  {

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private ArrayList<SharedAlbumMedia> sharedalbumMediaList = new ArrayList<>();
    ArrayList<Media> mediaList = new ArrayList<>();
    private ArrayList<Integer> pos = new ArrayList<Integer>();
    private SharedAlbumMediaSelectAdapter samsAdapter;
    private RecyclerView recyclerView;
    TextView mediacount;
    EditText edit_album_title;
    int count = 0;
    String ID, NAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Displays Home Screen
        setContentView(R.layout.shared_album_media_select);

        mediacount = (TextView) findViewById(R.id.media_count);
        edit_album_title = (EditText) findViewById(R.id.edit_album_title);

        recyclerView = (RecyclerView) findViewById(R.id.shared_album_media_select_recycler_view);
        samsAdapter = new SharedAlbumMediaSelectAdapter(mediaList,this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(samsAdapter);

        Intent intent = getIntent();
        ID = intent.getStringExtra("Id");
        NAME = intent.getStringExtra("Name");

        Toolbar toolbar= (Toolbar) findViewById(R.id.shared_album_media_select_toolbar);
        TextView tv = (TextView) findViewById(R.id.tv_ld_header);
        tv.setText(NAME);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preapareSharedAlbumMedia();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
//            Intent i = new Intent(SharedAlbumMediaSelect.this,SharedAlbumMediaDisplay.class);
//            i.putExtra("Id",ID);
//            i.putExtra("Name",NAME);
//            SharedAlbumMediaSelect.this.startActivity(i);
            onBackPressed();
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
//
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
//
//

    private void toggleSelection(int position) {
        samsAdapter.toggleSelection(position);
        int count = samsAdapter.getSelectedItemCount();
        List<Integer> cnt = samsAdapter.getSelectedItems();

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
            mode.getMenuInflater().inflate (R.menu.menu_shared_album_media_select, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_shared_album_media_select:

                    showdelete();

                    mode.finish();
                    return true;
                case R.id.to_personal_album:
                    Intent i = new Intent(SharedAlbumMediaSelect.this,ToPersonalAlbum.class);
                    i.putIntegerArrayListExtra("mediaId", pos);
                    i.putExtra("Id", ID);
                    i.putExtra("Name", NAME);
                    i.putExtra("shared", "yes");
                    SharedAlbumMediaSelect.this.startActivity(i);

                    return true;

                case R.id.to_shared_album:
                    Intent in = new Intent(SharedAlbumMediaSelect.this,ToSharedAlbum.class);
                    in.putIntegerArrayListExtra("mediaId", pos);
                    in.putExtra("Id", ID);
                    in.putExtra("Name", NAME);
                    in.putExtra("shared", "yes");
                    SharedAlbumMediaSelect.this.startActivity(in);
                    return true;

                case R.id.to_others:
                    Intent i2 = new Intent(SharedAlbumMediaSelect.this,Userbase.class);
                    i2.putExtra("action","to_others");
                    i2.putIntegerArrayListExtra("mediaId", pos);
                    i2.putExtra("Id", ID);
                    i2.putExtra("Name", NAME);
                    i2.putExtra("shared","yes");
                    SharedAlbumMediaSelect.this.startActivity(i2);
                    return true;

                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            samsAdapter.clearSelection();
            actionMode = null;
        }
    }





    public void showdelete(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Discard Media?");
        alertDialogBuilder.setMessage("You will no longer be able to access this media from this album. Also you cannot delete" +
                "media shared by others");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                try {
                    JSONArray a = new JSONArray(pos);
                    JSONObject obj = new JSONObject();
                    obj.put("userId", "1");
                    obj.put("mediaId", a);
                    obj.put("albumId", ID);
                    StringEntity jsonString = new StringEntity(obj.toString());


                    AsyncHttpClient client = new AsyncHttpClient();

                    client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Deletealbummedia", jsonString, "application/json", new AsyncHttpResponseHandler() {

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

                                    Intent i = new Intent(SharedAlbumMediaSelect.this, SharedAlbumMediaDisplay.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("Id", ID);
                                    i.putExtra("Name", NAME);
                                    SharedAlbumMediaSelect.this.startActivity(i);

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

                               // ArrayList<Media> m = new ArrayList<Media>();
                                if(a.length() > 0) {
                                    for (int j = 0; j < a.length(); j++) {
                                        JSONObject jo = a.optJSONObject(j);
                                        Media md = new Media(jo.optString("id"), jo.optString("name"),
                                                jo.optString("path"), jo.optString("createdOn"));


                                        mediaList.add(md);
                                        samsAdapter.notifyDataSetChanged();
                                    }
                                }
                                SharedAlbumMedia smmedia = new SharedAlbumMedia(usermediadetails.optString("id"),usermediadetails.optString("name")
                                        ,usermediadetails.optString("displayPicture"),
                                        mediaList);
                                sharedalbumMediaList.add(smmedia);
                               // samAdapter.notifyDataSetChanged();


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