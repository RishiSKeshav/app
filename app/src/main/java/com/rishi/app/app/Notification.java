package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nispok.snackbar.SnackbarManager;
import com.rishi.app.app.Models.NotificationsModel;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by amitrajula on 2/17/16.
 */
public class Notification extends AppCompatActivity {
    RecyclerView recyclerView;
    private NotificationAdapter nAdapter;
    private List<NotificationsModel> notifications = new ArrayList<>();
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    SessionManager sessionManager;
    TextView t1,t2,t3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        sessionManager = new SessionManager(getApplicationContext());

        Toolbar tl = (Toolbar) findViewById(R.id.notification_toolbar);
        setSupportActionBar(tl);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        View hView =  nvDrawer.inflateHeaderView(R.layout.nav_header);
        TextView tv= (TextView) hView.findViewById(R.id.nav_name);
        tv.setText(sessionManager.getName());

        CircleImageView cv = (CircleImageView) hView.findViewById(R.id.nav_circleView);
        Picasso.with(getApplicationContext()).load(sessionManager.getDisplayPicture())
                .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
                .into(cv);
        t1 = (TextView) hView.findViewById(R.id.photos_id);
        t2 = (TextView) hView.findViewById(R.id.personal_album_id);
        t3 = (TextView) hView.findViewById(R.id.shared_album_id);

        navHeaderData();


        recyclerView = (RecyclerView)findViewById(R.id.notification_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        nAdapter = new NotificationAdapter(notifications);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(nAdapter);
        prepareNotificationData();

    }


    private  void navHeaderData(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("userId", sessionManager.getId());
            StringEntity jsonString = new StringEntity(obj.toString());


            AsyncHttpClient client = new AsyncHttpClient();

            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Getuserdatastatus", jsonString, "application/json", new AsyncHttpResponseHandler() {

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

                            //Log.i("ddd",obj.getJSONObject("user").toString());

                            JSONObject user = obj.getJSONObject("user");
                            t1.setText(user.optString("photos"));
                            t2.setText(user.optString("personal_albums"));
                            t3.setText(user.optString("shared_albums"));

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

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                Intent i = new Intent(this,HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                break;

            case R.id.nav_second_fragment:
                Intent i2 = new Intent(this,CameraActivity.class);
                i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i2);
                break;

            case R.id.nav_third_fragment:
                Intent i3 = new Intent(this,Notification.class);
                i3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i3);
                break;

            case R.id.nav_fourth_fragment:
                Intent i4 = new Intent(this,Sync.class);
                i4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i4);
                break;

            case R.id.nav_fifth_fragment:
                Intent i5 = new Intent(this,SyncMediaDisplayActivity.class);
                i5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i5);
                break;

            default:
        }


        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){
            mDrawer.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void prepareNotificationData(){
        notifications.clear();

        try {
            JSONObject obj = new JSONObject();
            obj.put("userId", sessionManager.getId());
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
                        //Log.i("ee", response);
                        JSONObject obj = new JSONObject(response);

                        if (obj.getBoolean("error")) {
                            Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                        } else {

                            JSONObject notiObj = obj.getJSONObject("outputObj");
                            JSONArray notiarray = notiObj.optJSONArray("notifications");

                            if(notiarray.length() == 0){

                            }else {

                                for (int i = 0; i < notiarray.length(); i++) {
                                    JSONObject notidetails = notiarray.optJSONObject(i);
                                    NotificationsModel n = new NotificationsModel(notidetails.optString("id"), notidetails.optString("from"),
                                            notidetails.optString("message"), notidetails.optString("dataThumbnail"),
                                            notidetails.optString("date"), notidetails.optString("dataId"),
                                            notidetails.optString("dataName"),notidetails.optString("destination"));

                                    notifications.add(n);
                                    nAdapter.notifyDataSetChanged();
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

