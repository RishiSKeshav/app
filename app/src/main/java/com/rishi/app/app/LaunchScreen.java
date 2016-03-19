package com.rishi.app.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
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

/**
 * Created by amitrajula on 2/20/16.
 */

public class LaunchScreen extends AppCompatActivity {
    SessionManager sessionManager;
    String version = "";
    Boolean forceUpdate = false;
    private static int SPLASH_TIME_OUT = 2000;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Displays Home Screen
        setContentView(R.layout.launch_screen);

        String fontPath = "fonts/Orbitron-Bold.ttf";
        TextView lauch_screen_logo = (TextView) findViewById(R.id.lauch_screen_logo);
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        lauch_screen_logo.setTypeface(tf);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sessionManager = new SessionManager(getApplicationContext());
                FacebookSdk.sdkInitialize(getApplicationContext());
                getAppVersion();
            }
        }, SPLASH_TIME_OUT);






    }

    public boolean isLoggedIn() {
        Boolean mIsLoggedIn = sessionManager.LoginValue();
        return mIsLoggedIn;
    }

    private void getAppVersion() {

        try {
            JSONObject obj = new JSONObject();
            //obj.put("userId", sessionManager.getId());
            StringEntity jsonString = new StringEntity(obj.toString());


            AsyncHttpClient client = new AsyncHttpClient();

            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Appversion", jsonString, "application/json", new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                // @Override
                public void onSuccess(String response) {
                    // called when response HTTP status is "200 OK"
                    Log.i("response", response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        try {
                            PackageManager manager = getApplicationContext().getPackageManager();
                            PackageInfo info = manager.getPackageInfo(
                                    getApplicationContext().getPackageName(), 0);
                            String version = info.versionName;

                            if (version.equals(obj.getString("version"))) {

                                if (isLoggedIn()) {
                                    //user is already logged in!
                                    Intent i = new Intent(LaunchScreen.this, HomeActivity.class);
                                    startActivity(i);
                                    finish();

                                } else {
                                    //user is logged out!
                                    Intent i = new Intent(LaunchScreen.this, LoginActivity.class);
                                    startActivity(i);
                                    finish();
                                }


                            } else {
                                if (obj.getBoolean("forceUpdate")) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LaunchScreen.this);
                                    alertDialogBuilder.setMessage("A new version of ClikApp is avaliable in PlayStore");

                                    alertDialogBuilder.setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                finish();
                                            } catch (android.content.ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                                finish();
                                            }
                                        }
                                    });

                                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();

                                        }
                                    });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                } else {
                                    if (isLoggedIn()) {
                                        //user is already logged in!
                                        Intent i = new Intent(LaunchScreen.this, HomeActivity.class);
                                        startActivity(i);
                                        finish();

                                    } else {
                                        //user is logged out!
                                        Intent i = new Intent(LaunchScreen.this, LoginActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            }

                        } catch (PackageManager.NameNotFoundException e) {
                            //ErrorReporter.getInstance().handleSilentException(e);
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

        }//catch (JSONException e) {
        // e.printStackTrace();
        //     }
        catch (UnsupportedEncodingException ee) {
            ee.printStackTrace();
        }
    }
}
