package com.rishi.app.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
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
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class Sync extends AppCompatActivity {

    SessionManager sessionManager;

    private ArrayList<com.rishi.app.app.Image> imageList;
    private ArrayList<com.rishi.app.app.Image> syncedImageList;
    private ArrayList<com.rishi.app.app.Image> unSyncedImageList;
    private ArrayList<String> syncedPathList;
    int count=0;

    ProgressBar pBar;
    RelativeLayout layout;
    RelativeLayout mainLayout;
    RelativeLayout sync_sub_layout;

    Intent serviceIntent;

    TextView txtview;
    Switch switchButton;
    CheckBox photoChk;
    TextView uploadLeftTxt;
    ImageView imgView;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    TextView t1,t2,t3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        sessionManager = new SessionManager(getApplicationContext());

        Toolbar toolbar= (Toolbar) findViewById(R.id.sync_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        View hView =  nvDrawer.inflateHeaderView(R.layout.nav_header);
        TextView tv= (TextView) hView.findViewById(R.id.nav_name);
        tv.setText(sessionManager.getName());

        navHeaderData();

        CircleImageView cv = (CircleImageView) hView.findViewById(R.id.nav_circleView);
        Picasso.with(getApplicationContext()).load(sessionManager.getDisplayPicture())
                .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
                .into(cv);
        t1 = (TextView) hView.findViewById(R.id.photos_id);
        t2 = (TextView) hView.findViewById(R.id.personal_album_id);
        t3 = (TextView) hView.findViewById(R.id.shared_album_id);

        sync_sub_layout =(RelativeLayout) findViewById(R.id.sync_sub_layout2);

        uploadLeftTxt =(TextView) findViewById(R.id.uploadLeftTxt);
        pBar = (ProgressBar) findViewById(R.id.progressBar);
        imgView =(ImageView)findViewById(R.id.imgView);

        layout =(RelativeLayout) findViewById(R.id.sync_sub_layout);
        mainLayout =(RelativeLayout) findViewById(R.id.sync_main_layout);

        IntentFilter filter = new IntentFilter("PROGRESS_ACTION");
        registerReceiver(myReceiver, filter);

        IntentFilter finalFilter = new IntentFilter("IMAGE_ACTION");
        registerReceiver(finalCountReceiver, finalFilter);

        switchButton = (Switch) findViewById(R.id.switchButton);
        photoChk =(CheckBox) findViewById(R.id.chkPhoto);
        txtview =(TextView) findViewById(R.id.txtView);

        if(sessionManager.getSyncStatus()) {
            switchButton.setChecked(true);

            setPhotoVideoStatusVisibile();

            initializeImageLists();

            serviceIntent = new Intent(Sync.this,ImageUploadService.class);
            serviceIntent.putParcelableArrayListExtra("unSyncedImageList", unSyncedImageList);
            startService(serviceIntent);
        }
        else{
            switchButton.setChecked(false);
            imgView.setVisibility(View.INVISIBLE);
            pBar.setVisibility(View.INVISIBLE);

            setPhotoVideoStatusInvisibile();
        }

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {

                    setPhotoVideoStatusVisibile();

                    sessionManager.changeSyncStatus(bChecked);

                    initializeImageLists();

                    serviceIntent = new Intent(Sync.this, ImageUploadService.class);
                    serviceIntent.putParcelableArrayListExtra("unSyncedImageList", unSyncedImageList);
                    startService(serviceIntent);

                    Log.d("checked", " checked reached");
                } else {

                    Log.d("unchecked", " unchecked reached");

                    sessionManager.changeSyncStatus(bChecked);

                    setPhotoVideoStatusInvisibile();
                    imgView.setVisibility(View.INVISIBLE);
                    pBar.setVisibility(View.INVISIBLE);

                    serviceIntent = new Intent(Sync.this, ImageUploadService.class);
                    stopService(serviceIntent);

                }
            }
        });

        photoChk.setOnCheckedChangeListener((new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    sessionManager.changePhotoSyncStatus(true);
                }
                else{
                    sessionManager.changePhotoSyncStatus(false);
                }
            }
        }));
    }


    private  void navHeaderData(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("userId", "1");
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

                            Log.i("ddd",obj.getJSONObject("user").toString());

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


    private void setPhotoVideoStatusInvisibile(){

        photoChk.setVisibility(View.INVISIBLE);
        txtview.setVisibility(View.INVISIBLE);

        photoChk.setChecked(sessionManager.getPhotoSyncStatus());

    }

    private void setPhotoVideoStatusVisibile(){

        photoChk.setVisibility(View.VISIBLE);
        txtview.setVisibility(View.VISIBLE);

        photoChk.setChecked(sessionManager.getPhotoSyncStatus());

    }

        private void initializeImageLists() {

        generateImageList();
        generateDBImageList();
        generateUnsyncedImageList();
    }

    private void generateUnsyncedImageList() {

        unSyncedImageList = new ArrayList<Image>();

        for(com.rishi.app.app.Image img : imageList){

            if(!syncedPathList.contains(img.path))
                unSyncedImageList.add(img);
        }

        count=unSyncedImageList.size();

        Log.d("unsynced count", String.valueOf(unSyncedImageList.size()));
    }

    private void generateDBImageList() {

        ImageDatabaseHandler db = new ImageDatabaseHandler(this, Environment.getExternalStorageDirectory().toString()+ "/app");

        if(db!=null){

            syncedPathList= db.getAllImagePath();

            Iterator<String> abc = syncedPathList.iterator();

            while(abc.hasNext())
            {

                Log.d("DB ",abc.next());
            }

        }
        else
            Log.i("databasae_name","DB not created");

        Log.d("DB list count ",String.valueOf(syncedPathList.size()));
    }

    private void generateImageList() {

        imageList = new ArrayList<com.rishi.app.app.Image>();

        ContentResolver cr = this.getContentResolver();

        String[] columns = new String[] {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                com.rishi.app.app.Image image = new com.rishi.app.app.Image();

                image.setPath(cursor.getString(0));
                image.setName(cursor.getString(1));

                imageList.add(image);
            } while (cursor.moveToNext());
        }

        Log.d("image list count", String.valueOf(imageList.size()));
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            int ola = intent.getIntExtra("NUMBER", 0);
            if(pBar.getParent()!=null)
                ((ViewGroup)pBar.getParent()).removeView(pBar);



            if(ola==100)
            {
                if(imgView.getParent()!=null)
                    ((ViewGroup)imgView.getParent()).removeView(imgView);

                if(uploadLeftTxt.getParent()!=null)
                    ((ViewGroup)uploadLeftTxt.getParent()).removeView(uploadLeftTxt);

                pBar.setVisibility(View.VISIBLE);
                pBar.setProgress(ola);
                pBar.setVisibility(View.INVISIBLE);
                layout.addView(pBar);

                imgView.setVisibility(View.INVISIBLE);
                layout.addView(imgView);

                uploadLeftTxt.setVisibility(View.INVISIBLE);
                layout.addView(uploadLeftTxt);
            }

            else {
                pBar.setVisibility(View.VISIBLE);
                pBar.setProgress(ola);
                layout.addView(pBar);
            }
            //Log.d("dddReceiver", String.valueOf(ola));   //can't see
        }
    };

    private BroadcastReceiver finalCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int countLeft = intent.getIntExtra("leftCount", 0);
            String imgPath = intent.getStringExtra("imgPath");

            Bitmap bmp = BitmapFactory.decodeFile(imgPath);

            if(imgView.getParent()!=null)
                ((ViewGroup)imgView.getParent()).removeView(imgView);

            imgView.setVisibility(View.VISIBLE);
            imgView.setImageBitmap(bmp);
            layout.addView(imgView);


            if(uploadLeftTxt.getParent()!=null)
                ((ViewGroup)uploadLeftTxt.getParent()).removeView(uploadLeftTxt);

            uploadLeftTxt.setVisibility(View.VISIBLE);
            uploadLeftTxt.setText("Backing up:" + countLeft + " left");
            layout.addView(uploadLeftTxt);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(myReceiver);
        unregisterReceiver(finalCountReceiver);
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            boolean isMobileData = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            boolean isWIFI = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

            if(sessionManager.getSyncStatus()==true){

                if(!isWIFI){
                    serviceIntent = new Intent(Sync.this, ImageUploadService.class);
                    stopService(serviceIntent);
                }
                if(isWIFI){
                    initializeImageLists();
                    serviceIntent = new Intent(Sync.this, ImageUploadService.class);
                    serviceIntent.putParcelableArrayListExtra("unSyncedImageList", unSyncedImageList);
                    startService(serviceIntent);
                }
                if(isMobileData && sessionManager.getPhotoSyncStatus()==false){
                    serviceIntent = new Intent(Sync.this, ImageUploadService.class);
                    stopService(serviceIntent);
                }

                if(isMobileData && sessionManager.getPhotoSyncStatus()==true){
                    serviceIntent = new Intent(Sync.this, ImageUploadService.class);
                    serviceIntent.putParcelableArrayListExtra("unSyncedImageList", unSyncedImageList);
                    startService(serviceIntent);
                }
            }
            else{
                serviceIntent = new Intent(Sync.this, ImageUploadService.class);
                stopService(serviceIntent);
            }
        }
    }
}