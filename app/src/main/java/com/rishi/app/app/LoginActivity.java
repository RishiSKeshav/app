package com.rishi.app.app;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.entity.StringEntity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nispok.snackbar.SnackbarManager;

import android.preference.PreferenceActivity.Header;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.UUID;


public class LoginActivity extends ActionBarActivity {
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    SessionManager sessionManager;
    ImageDatabaseHandler db;

    EditText emailET;

    private String TAG = LoginActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager=CallbackManager.Factory.create();

        setContentView(R.layout.login);

        databaseSetup();

        String fontPath = "fonts/Orbitron-Bold.ttf";

        TextView logo = (TextView) findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        logo.setTypeface(tf);


        sessionManager = new SessionManager(getApplicationContext());

        emailET = (EditText) findViewById(R.id.loginEmail);
        passwordET = (EditText) findViewById(R.id.loginPassword);

        loginButton= (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email","user_friends");




        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {

                                Log.e("responsefrom login: ", response + "");
                                try {

                                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                                            R.style.AppTheme_Dark_Dialog);
                                    progressDialog.setIndeterminate(true);
                                    progressDialog.setMessage("Authenticating...");
                                    progressDialog.show();
//                                    user.email = object.getString("email").toString();

                                    JSONObject obj = new JSONObject();
                                    obj.put("emailId", object.getString("email").toString());
                                    obj.put("password","");
                                    obj.put("name", object.getString("name").toString());
                                    obj.put("mobileNo", "");
                                    obj.put("displayPicture","https://graph.facebook.com/" + object.getString("id").toString() +"/picture?type=large");
                                    obj.put("facebookId",object.getString("id").toString());
                                    StringEntity jsonString = new StringEntity(obj.toString());


                                    AsyncHttpClient client = new AsyncHttpClient();

                                    client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Facebooklogin", jsonString, "application/json", new AsyncHttpResponseHandler() {

                                        @Override
                                        public void onStart() {
                                            // called before request is started
                                        }

                                        // @Override
                                        public void onSuccess(String response) {
                                            // called when response HTTP status is "200 OK"
                                                Log.i("res",response);
                                            try {
                                                JSONObject obj = new JSONObject(response);


                                                if (obj.getBoolean("error")) {
                                                    Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                                                } else {

                                                    JSONObject outputObj = obj.getJSONObject("outputObj");
                                                    JSONObject user = outputObj.getJSONObject("user");
                                                   sessionManager.createLoginSession(user);

                                                    if(outputObj.optString("gcm").equals("")) {
                                                        if (checkPlayServices()) {
                                                            registerGCM();
                                                        }
                                                        progressDialog.hide();
                                                        navigatetoHomeActivity();
                                                    }else{
                                                        progressDialog.hide();
                                                        navigatetoHomeActivity();
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





                            } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();





            }

            @Override
            public void onCancel() {
                //info.setText("Login attempt cancelled.");
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
                //info.setText("Login attempt failed.");
            }
        });
    }


    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void databaseSetup() {

        File folder = new File(Environment.getExternalStorageDirectory().toString()+ "/ClikApp");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }

        db = new ImageDatabaseHandler(this,folder.toString());

        Log.d("databasae_name", "Database SEtup started");

        Log.i("databasae_name",db.getDatabaseName());

        /*String uuid = UUID.randomUUID().toString();

        com.rishi.app.app.Image img = new com.rishi.app.app.Image();

        img.setName("IMG_20160207_124315.jpg");
        img.setPath("/storage/emulated/0/IMG_20160207_124315.jpg");
        img.setUuid(uuid);

        db.addImage(img);*/

        /*if(db!=null){
            Toast.makeText(LoginActivity.this, db.getDatabaseName(), Toast.LENGTH_SHORT).show();
            Log.i("databasae_name",db.getDatabaseName());
        }
        else
            Log.i("databasae_name","DB not created");


        db.addContact("Amit","6268419153");

        Toast.makeText(LoginActivity.this,"count: "+ db.getContactsCount(), Toast.LENGTH_SHORT).show();*/

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    public void  loginUser(View view) throws JSONException, UnsupportedEncodingException {

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        Boolean vEmail=false;
        Boolean vPassword = false;

        JSONObject jsonObject = new JSONObject();

        if(Utility.isNotNull(email) && Utility.isNotNull(password)) {
            jsonObject.put("emailId", email);
            jsonObject.put("password", password);
            invokeWS(jsonObject);
        }
        else{

            SnackbarManager.show(
                    com.nispok.snackbar.Snackbar.with(this)
                            .text("Invalid Email or Password")
                            .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
            );

            //Toast.makeText(getApplicationContext(), "Email or Password is empty", Toast.LENGTH_LONG).show();
        }
    }

    private void invokeWS(JSONObject jsonObject) throws UnsupportedEncodingException {

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        StringEntity jsonString = new StringEntity(jsonObject.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getApplicationContext(),"http://52.89.2.186/project/webservice/public/Login",jsonString,"application/json", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

           // @Override
            public void onSuccess(String response) {
                // called when response HTTP status is "200 OK"
                try{
                   // Log.i("fff",response);
                    JSONObject obj = new JSONObject(response);

                    if(obj.getBoolean("error")){

                        progressDialog.hide();

                        SnackbarManager.show(
                                com.nispok.snackbar.Snackbar.with(LoginActivity.this)
                                        .text("Wrong Credentials")
                                        .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                        );

                        //Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                    }else{

                        JSONObject userObj = obj.getJSONObject("user");
                        sessionManager.createLoginSession(userObj);
                        navigatetoHomeActivity();
                    }

                }catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            //@Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            //@Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });


    }


    public void goToRegisterActivity(View view)
    {
        //Log.i("SER", "invoked navigatetoRegisterActivity() method");
        Intent loginIntent = new Intent(getApplicationContext(),RegisterActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }

    public void goToSearchUser(View view){
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        Intent homeIntent = new Intent(getApplicationContext(),SearchUser.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
       // finish();

        progressDialog.hide();
    }

    public void navigatetoHomeActivity(){


        Intent homeIntent = new Intent(getApplicationContext(),HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();


    }
}