package com.rishi.app.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
public class AlbumMediaSelect extends AppCompatActivity implements AlbumMediaSelectAdapter.MyViewHolder.ClickListener{

    private AlbumMediaSelectAdapter amsAdapter;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private RecyclerView recyclerView;
    private ArrayList<Integer> pos = new ArrayList<Integer>();
    private List<AlbumMedia> albummediaList = new ArrayList<>();
    String ID,NAME;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Displays Home Screen
        setContentView(R.layout.album_media_select);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Media");

        Intent i = getIntent();
        albummediaList = i.getParcelableArrayListExtra("al");
        ID = i.getStringExtra("id");
        NAME = i.getStringExtra("name");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_album_media_select);
        amsAdapter = new AlbumMediaSelectAdapter(albummediaList,this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(amsAdapter);

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
        amsAdapter.toggleSelection(position);
        int count = amsAdapter.getSelectedItemCount();
        List<Integer> cnt = amsAdapter.getSelectedItems();

        pos.clear();
        for(int i=0;i<cnt.size();i++){
            AlbumMedia am = albummediaList.get(cnt.get(i));
            pos.add(Integer.parseInt(am.getId()));
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
            mode.getMenuInflater().inflate (R.menu.menu_album_media_select, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_album_media_select:

                    confirmdeletealbummedia();
                    mode.finish();
                    return true;
                case R.id.to_personal_album:

                    Intent i = new Intent(AlbumMediaSelect.this,ToPersonalAlbum.class);
                    i.putIntegerArrayListExtra("mediaId", pos);
                    i.putExtra("Id", ID);
                    i.putExtra("Name", NAME);
                    i.putExtra("shared","no");
                    AlbumMediaSelect.this.startActivity(i);
                    return true;

                case R.id.to_shared_album:
                    Intent intent = new Intent(AlbumMediaSelect.this,ToSharedAlbum.class);
                    intent.putIntegerArrayListExtra("mediaId", pos);
                    intent.putExtra("Id", ID);
                    intent.putExtra("Name", NAME);
                    intent.putExtra("shared","no");
                    AlbumMediaSelect.this.startActivity(intent);
                    return true;

                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            amsAdapter.clearSelection();
            actionMode = null;
        }
    }

    private void confirmdeletealbummedia(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Discard Media?");
        alertDialogBuilder.setMessage("You can still have this media from Media library");

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

                                    Intent i = new Intent(AlbumMediaSelect.this, AlbumMediaDisplay.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("Id", ID);
                                    i.putExtra("Name", NAME);
                                    AlbumMediaSelect.this.startActivity(i);

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
