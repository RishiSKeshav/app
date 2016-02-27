package com.rishi.app.app;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import com.squareup.picasso.Picasso;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class SyncMediaDisplayActivity extends AppCompatActivity {

    private ArrayList<com.rishi.app.app.Image> imageList;
    private ArrayList<com.rishi.app.app.Image> syncedImageList;
    private ArrayList<com.rishi.app.app.Image> unSyncedImageList;
    private ArrayList<String> syncedPathList;
    private ArrayList<String> cameraImageList;
    private ArrayList<String> syncImageList;

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    TextView t1,t2,t3;
    SessionManager sessionManager;
    String action;


    SyncImages sImage;
    SyncMediaUnsyncAdapter syncMediaUnsyncAdapter;
    SyncMediaCameraAdapter syncMediaCameraAdapter;
    SyncMediaSyncAdapter syncMediaSyncAdapter;

    RecyclerView recyclerView1;
    RecyclerView recyclerView2;
    RecyclerView recyclerView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_media_display);
        sessionManager = new SessionManager(getApplicationContext());

        Toolbar toolbar= (Toolbar) findViewById(R.id.sync_images_toolbar);
        setSupportActionBar(toolbar);
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


        final ViewPager viewPager = (ViewPager) findViewById(R.id.sync_images_viewpager);
        viewPager.setAdapter(new SyncMediaPagerAdapter(getSupportFragmentManager(),
                SyncMediaDisplayActivity.this));

        Intent i = getIntent();

        if(i.hasExtra("action")){
            action = i.getStringExtra("action");
            if(action.equals("unsync")){
                viewPager.setCurrentItem(0);
            }
            if(action.equals("sync")){
                viewPager.setCurrentItem(1);
            }
            if(action.equals("camera")){
                viewPager.setCurrentItem(2);
            }
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sync_images_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


//        initializeImageLists();
//
//        recyclerView1 = (RecyclerView)findViewById(R.id.unsync_media_recycler_view);
//        syncMediaUnsyncAdapter = new SyncMediaUnsyncAdapter(unSyncedImageList);
//        recyclerView1.setHasFixedSize(true);
//        RecyclerView.LayoutManager mLayoutManager1 = new GridLayoutManager(this,4);
//        recyclerView1.setLayoutManager(mLayoutManager1);
//        recyclerView1.setAdapter(syncMediaUnsyncAdapter);
//
//        recyclerView2 = (RecyclerView)findViewById(R.id.sync_media_recycler_view);
//        syncMediaSyncAdapter = new SyncMediaSyncAdapter(syncImageList);
//        recyclerView2.setHasFixedSize(true);
//        RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(this,4);
//        recyclerView2.setLayoutManager(mLayoutManager2);
//        recyclerView2.setAdapter(syncMediaSyncAdapter);
//
//        recyclerView3 = (RecyclerView)findViewById(R.id.camera_media_recycler_view);
//        syncMediaCameraAdapter = new SyncMediaCameraAdapter(cameraImageList);
//        recyclerView3.setHasFixedSize(true);
//        RecyclerView.LayoutManager mLayoutManager3 = new GridLayoutManager(this,4);
//        recyclerView3.setLayoutManager(mLayoutManager3);
//        recyclerView3.setAdapter(syncMediaCameraAdapter);

        //generateMainList();


        /*for(SyncImages img:syncMediaDisplayList){

            if(img.getLink().equals("localPath"))
                Log.d("unsynced",img.getPath() + " " + String.valueOf(img.getSyncStatus()));
            else
                Log.d("synced",img.getLink() + " " + String.valueOf(img.getSyncStatus()));

        }*/

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

                            Log.i("dddSync",obj.getJSONObject("user").toString());

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
                startActivity(i);
                break;

            case R.id.nav_second_fragment:
                Intent i2 = new Intent(this,CameraActivity.class);
                startActivity(i2);
                break;

            case R.id.nav_third_fragment:
                Intent i3 = new Intent(this,Notification.class);
                startActivity(i3);
                break;

            case R.id.nav_fourth_fragment:
                Intent i4 = new Intent(this,Sync.class);
                startActivity(i4);
                break;

            case R.id.nav_fifth_fragment:
                Intent i5 = new Intent(this,SyncMediaDisplayActivity.class);
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






//    private void initializeImageLists() {
//
//        generateImageList();
//        generateDBImageList();
//        generateUnsyncedImageList();
//        generateCameraImageList();
//        generateSyncImageList();
//    }
//
//
//
//    private void generateUnsyncedImageList() {
//
//        unSyncedImageList = new ArrayList<Image>();
//
//        for(com.rishi.app.app.Image img : imageList){
//
//            if(!syncedPathList.contains(img.path))
//                unSyncedImageList.add(img);
//        }
//    }
//
//    private void generateDBImageList() {
//
//        ImageDatabaseHandler db = new ImageDatabaseHandler(this, Environment.getExternalStorageDirectory().toString()+ "/app");
//
//        if(db!=null){
//
//            syncedPathList= db.getAllImagePath();
//
//            Iterator<String> abc = syncedPathList.iterator();
//
//            while(abc.hasNext())
//            {
//
//                Log.d("DB ",abc.next());
//            }
//
//        }
//        else
//            Log.i("databasae_name","DB not created");
//
//        Log.d("DB list count ",String.valueOf(syncedPathList.size()));
//    }
//
//
//
//    private void generateImageList() {
//
//        imageList = new ArrayList<com.rishi.app.app.Image>();
//
//        ContentResolver cr = this.getContentResolver();
//
//        String[] columns = new String[] {
//                MediaStore.Images.ImageColumns.DATA,
//                MediaStore.Images.ImageColumns.DISPLAY_NAME};
//        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                columns, null, null, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                com.rishi.app.app.Image image = new com.rishi.app.app.Image();
//
//                image.setPath(cursor.getString(0));
//                image.setName(cursor.getString(1));
//
//                imageList.add(image);
//            } while (cursor.moveToNext());
//        }
//
//        Log.d("image list count", String.valueOf(imageList.size()));
//    }
//
//    private void generateCameraImageList() {
//
//        ImageDatabaseHandler db = new ImageDatabaseHandler(this, Environment.getExternalStorageDirectory().toString()+ "/app");
//
//        if(db!=null){
//
//            cameraImageList= db.getAllCameraImagePath();
//
//            Iterator<String> abc = cameraImageList.iterator();
//
//            while(abc.hasNext())
//            {
//
//                Log.d("DB ",abc.next());
//            }
//
//        }
//        else
//            Log.i("databasae_name","DB not created");
//
//        Log.d("DB list count ",String.valueOf(cameraImageList.size()));
//    }
//
//    private void generateSyncImageList() {
//
//        ImageDatabaseHandler db = new ImageDatabaseHandler(this, Environment.getExternalStorageDirectory().toString()+ "/app");
//
//        if(db!=null){
//
//            syncImageList= db.getAllSyncImagePath();
//
//            Iterator<String> abc = syncImageList.iterator();
//
//            while(abc.hasNext())
//            {
//
//                Log.d("DB ",abc.next());
//            }
//
//        }
//        else
//            Log.i("databasae_name","DB not created");
//
//        Log.d("DB list count ",String.valueOf(syncImageList.size()));
//    }
}