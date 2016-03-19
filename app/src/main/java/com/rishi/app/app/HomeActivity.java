package com.rishi.app.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.Image;

import android.net.LinkAddress;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Base64;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by RishiS on 1/27/2016.
 */

/**
 * Home Screen Activity
 */
public class HomeActivity extends AppCompatActivity {

    SessionManager sessionManager;
    TextView nameTV;
    String picturePath;
    TextView name;
    private FloatingActionButton fab, fab1;
    final int PICK_FROM_CAPTURE = 0;
    final int PICK_FROM_FILE = 1;
    final int PIC_CROP = 2;
    private Uri mImageCaptureUri;
    private Bitmap bitmap;
    String encodedString;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;


    private FloatingActionButton album;
    private FloatingActionButton share_album;
    private static final String TAG = HomeActivity.class.getName();
    RequestParams params = new RequestParams();

    TextView t1, t2, t3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        sessionManager = new SessionManager(getApplicationContext());

        String fontPath = "fonts/Orbitron-Bold.ttf";

        Toolbar home_toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        TextView header = (TextView) findViewById(R.id.tv_ld_header);
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        header.setTypeface(tf);
        setSupportActionBar(home_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        View hView = nvDrawer.inflateHeaderView(R.layout.nav_header);
        TextView tv = (TextView) hView.findViewById(R.id.nav_name);
        tv.setText(sessionManager.getName());

        CircleImageView cv = (CircleImageView) hView.findViewById(R.id.nav_circleView);

        Picasso.with(getApplicationContext())
                .load(sessionManager.getDisplayPicture())
                .transform(new BitmapTransform(500, 500))
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .skipMemoryCache()
                .fit()
                .centerCrop()
                .into(cv);

        t1 = (TextView) hView.findViewById(R.id.photos_id);
        t2 = (TextView) hView.findViewById(R.id.personal_album_id);
        t3 = (TextView) hView.findViewById(R.id.shared_album_id);


        album = (FloatingActionButton) findViewById(R.id.fab_album);
        share_album = (FloatingActionButton) findViewById(R.id.fab_shared_album);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NewAlbum.class);
                intent.putExtra("shared", "no");
                startActivity(intent);

            }
        });
        share_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NewAlbum.class);
                intent.putExtra("shared", "yes");
                startActivity(intent);

            }
        });


        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(HomeActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.menu_display_picture, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().equals("Take Photo")) {
                            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            //we will handle the returned data in onActivityResult
                            startActivityForResult(captureIntent, PICK_FROM_CAPTURE);
                        } else {
                            Intent intent = new Intent();
//                            intent.setType("image/*");
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);

                            if (Build.VERSION.SDK_INT < 19) {
                                intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("*/*");
                                startActivityForResult(Intent.createChooser(intent,
                                        "Complete action using"), PICK_FROM_FILE);
                            } else {
                                intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("*/*");
                                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                                        mImageCaptureUri);
                                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                            }
                        }

                        return true;
                    }
                });

                popup.show();
            }
        });

        Intent i = getIntent();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                HomeActivity.this));

        if (i.hasExtra("shared_media")) {
            viewPager.setCurrentItem(2);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


        navHeaderData();

    }


    private void navHeaderData() {


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

                            // Log.i("ddd",obj.getJSONObject("user").toString());

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

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException ee) {
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
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                Intent i = new Intent(HomeActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                break;

            case R.id.nav_second_fragment:
                Intent i2 = new Intent(HomeActivity.this, CameraActivity.class);
                //i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i2);
                break;

            case R.id.nav_third_fragment:
                Intent i3 = new Intent(HomeActivity.this, Notification.class);
                //i3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i3);
                break;

            case R.id.nav_fourth_fragment:
                Intent i4 = new Intent(HomeActivity.this, Sync.class);
                //i4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i4);
                break;

            case R.id.nav_fifth_fragment:
                final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                Intent i5 = new Intent(HomeActivity.this, SyncMediaDisplayActivity.class);
                // i5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i5);
                progressDialog.hide();
                break;

            default:
        }


        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == PICK_FROM_FILE) {
                if (Build.VERSION.SDK_INT < 19) {
                    mImageCaptureUri = data.getData();

                } else {
                    mImageCaptureUri = data.getData();
                    ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        parcelFileDescriptor = getContentResolver()
                                .openFileDescriptor(mImageCaptureUri, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor
                                .getFileDescriptor();
                        bitmap = BitmapFactory
                                .decodeFileDescriptor(fileDescriptor);
                        parcelFileDescriptor.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                performCrop();

            }
        }
        if (requestCode == PICK_FROM_CAPTURE) {
            mImageCaptureUri = data.getData();
            picturePath = getRealPathFromURI(mImageCaptureUri);
            performCrop();
        }
        if (requestCode == PIC_CROP) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    bitmap = extras.getParcelable("data");
                    encodeImagetoString();

                    //  Log.i("fff",picturePath.toString());
//                File f  = new File(mImageCaptureUri.);
//                f.delete();
                }
            }
        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }


    private void performCrop() {

        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(mImageCaptureUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);

        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            }

            ;

            @Override
            protected String doInBackground(Void... params) {
                //BitmapFactory.Options options = null;
                //options = new BitmapFactory.Options();
                //options.inSampleSize = 3;
                //  bitmap = BitmapFactory.decodeFile(imgPath,
                //        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                // prgDialog.setMessage("Calling Upload");
                // Put converted Image string into Async Http Post param
                params.put("image", encodedString);
                params.put("userId", sessionManager.getId());
                params.put("filename", System.currentTimeMillis() + ".jpg");
                triggerImageUpload();
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }


    public void makeHTTPCall() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://52.89.2.186/project/webservice/displayPicture.php",
                params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            //Log.i("response", response);
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                            } else {
                                sessionManager.changedisplayPicture(obj.getString("displayPicture"));
                                Intent i = new Intent(HomeActivity.this, HomeActivity.class);
                                HomeActivity.this.startActivity(i);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }

                    public void onFailure(int statusCode, Throwable error,
                                          String content) {

                        //prgDialog.hide();
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error Occured n Most Common Error: n1. Device not connected to Internetn2. Web App is not deployed in App servern3. App server is not runningn HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
        }

        if (id == R.id.home_settings) {

            Intent i = new Intent(this, HomeSettings.class);
            this.startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    protected void animateFab(final int position) {
        switch (position) {
            case 0:
                album.setVisibility(View.VISIBLE);
                share_album.setVisibility(View.INVISIBLE);
                break;
            case 1:
                album.setVisibility(View.INVISIBLE);
                share_album.setVisibility(View.VISIBLE);
                break;

            default:
                album.setVisibility(View.INVISIBLE);
                share_album.setVisibility(View.INVISIBLE);
                break;
        }
    }


}
