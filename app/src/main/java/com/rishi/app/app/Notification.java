package com.rishi.app.app;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.rishi.app.app.Models.NotificationsModel;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;


/**
 * Created by amitrajula on 2/17/16.
 */
public class Notification extends AppCompatActivity {
    RecyclerView recyclerView;
    private NotificationAdapter nAdapter;
    private List<NotificationsModel> notifications = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        Toolbar tl = (Toolbar) findViewById(R.id.notification_toolbar);
        setSupportActionBar(tl);

        recyclerView = (RecyclerView)findViewById(R.id.notification_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        nAdapter = new NotificationAdapter(notifications);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(nAdapter);
        prepareNotificationData();

    }

    private void prepareNotificationData(){
        notifications.clear();

        try {
            JSONObject obj = new JSONObject();
            obj.put("userId", "124");
            StringEntity jsonString = new StringEntity(obj.toString());


            AsyncHttpClient client = new AsyncHttpClient();

            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Getnotifications", jsonString, "application/json", new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                // @Override
                public void onSuccess(String response) {
                    // called when response HTTP status is "200 OK"
                    try {
                        Log.i("ee", response);
                        JSONObject obj = new JSONObject(response);

                        if (obj.getBoolean("error")) {
                            Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                        } else {

                            JSONObject notiObj = obj.getJSONObject("outputObj");
                            JSONArray notiarray = notiObj.optJSONArray("notifications");

                            for (int i = 0; i < notiarray.length(); i++) {
                                JSONObject notidetails = notiarray.optJSONObject(i);
                                NotificationsModel n = new NotificationsModel(notidetails.optString("id"),notidetails.optString("from"),
                                        notidetails.optString("message"),notidetails.optString("dataThumbnail"),
                                        notidetails.optString("date"),notidetails.optString("dataId"),notidetails.optString("dataName"));

                                notifications.add(n);
                                nAdapter.notifyDataSetChanged();
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

