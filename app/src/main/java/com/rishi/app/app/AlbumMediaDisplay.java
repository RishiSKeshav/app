package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
 * Created by amitrajula on 2/3/16.
 */
public class AlbumMediaDisplay extends AppCompatActivity{

    private List<AlbumMedia> albumMediaList = new ArrayList<>();
    private AlbumMediaAdapter amAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Displays Home Screen
        setContentView(R.layout.album_media_display);

        recyclerView = (RecyclerView)findViewById(R.id.album_media_recycler_view);
        amAdapter = new AlbumMediaAdapter(albumMediaList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(amAdapter);

        Intent intent = getIntent();
        String id = intent.getStringExtra("Id");

        prepareAlbumMediaData(id);


    }


    private void prepareAlbumMediaData(String id){
        albumMediaList.clear();
        try {
            JSONObject obj = new JSONObject();
            obj.put("userId", "1");
            obj.put("albumId",id);
            StringEntity jsonString = new StringEntity(obj.toString());


            AsyncHttpClient client = new AsyncHttpClient();

            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Getalbummedia", jsonString, "application/json", new AsyncHttpResponseHandler() {

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
                                if(mediaarray.length() > 0)
                                {
                                for (int i = 0; i < mediaarray.length(); i++) {
                                    JSONObject mediadetails = mediaarray.optJSONObject(i);

                                    AlbumMedia am = new AlbumMedia(mediadetails.optString("id"), mediadetails.optString("name"),
                                            mediadetails.optString("path"), mediadetails.optString("date"),mediadetails.optString("albumId"));
                                    albumMediaList.add(am);
                                    amAdapter.notifyDataSetChanged();

                                }
                            }else{
                                Toast.makeText(getApplicationContext(),"No Data",Toast.LENGTH_LONG).show();
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
