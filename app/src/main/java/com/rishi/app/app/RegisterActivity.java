package com.rishi.app.app;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by RishiS on 1/27/2016.
 */

public class RegisterActivity extends Activity {

    SessionManager sessionManager;

    EditText nameET;
    EditText emailET;
    EditText passwordET;
    private String TAG = RegisterActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);


        sessionManager = new SessionManager(getApplicationContext());


        nameET = (EditText)findViewById(R.id.registername);

        emailET = (EditText)findViewById(R.id.registerEmail);
        passwordET = (EditText)findViewById(R.id.registerPassword);

    }

    /**
     * Method gets triggered when Register button is clicked
     *
     * @param view
     */
    public void registerUser(View view) throws JSONException, UnsupportedEncodingException {


        String name = nameET.getText().toString();
        String emailId = emailET.getText().toString();
        String password = passwordET.getText().toString();

        JSONObject jsonObject = new JSONObject();

        if(Utility.isNotNull(emailId) && Utility.isNotNull(password)) {
            jsonObject.put("name", name);
            jsonObject.put("emailId", emailId);
            jsonObject.put("password", password);
            jsonObject.put("mobileNo","6263203932");
            jsonObject.put("displayPicture","displayPicture");
            invokeWS(jsonObject);
        }
        else{
            Toast.makeText(getApplicationContext(), "Proszę wypełnić wszystkie pola!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method that performs RESTful webservice invocations
     *
     //* @param params
     */
    public void invokeWS(JSONObject object) throws UnsupportedEncodingException{

        StringEntity jsonString = new StringEntity(object.toString());
        AsyncHttpClient client = new AsyncHttpClient();
      //  client.post(getApplicationContext(),"http://52.89.2.186/project/webservice/public/Register", jsonString,"application/json", new AsyncHttpResponseHandler() {
            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Register", jsonString, "application/json", new AsyncHttpResponseHandler() {
                // When the response returned by REST has Http response code '200'
                @Override
                public void onSuccess(String response) {

                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getBoolean("error")) {
                            Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                        } else {

                            JSONObject userObj = obj.getJSONObject("user");

                            sessionManager.createLoginSession(userObj);
                            if (checkPlayServices()) {
                               registerGCM();
                                navigatetoUploadDisplayPicture();

                            }

                        }
                    }catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();

                    }

                }


                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {

                    Toast.makeText(getApplicationContext(), "Error Occured [", Toast.LENGTH_LONG).show();

                }

            });
    }

    /**
     * Method which navigates from Register Activity to Login Activity
     */
    public void navigatetoLoginActivity(View view){
        Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    public void navigatetoUploadDisplayPicture(){
        Intent homeIntent = new Intent(getApplicationContext(),UploadDP.class);
        startActivity(homeIntent);
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





}