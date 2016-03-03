package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nispok.snackbar.SnackbarManager;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by amitrajula on 2/20/16.
 */
public class RegistrationDetails extends AppCompatActivity {
    String EMAILID;
    String NAME,MOBILENO,PASSWORD;
    EditText name,mobileNo,password;
    private String TAG = RegistrationDetails.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    SessionManager sessionManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_details);

        sessionManager = new SessionManager(getApplicationContext());

        Intent i = getIntent();
        EMAILID = i.getStringExtra("emailId");

         name = (EditText) findViewById(R.id.registername);
         mobileNo = (EditText) findViewById(R.id.registermobileNo);
         password = (EditText) findViewById(R.id.registerPassword);


    }

    public void registerUser(View view){
        NAME = name.getText().toString();
        MOBILENO = mobileNo.getText().toString();
        PASSWORD = password.getText().toString();

        if(Utility.isNotNull(PASSWORD) && Utility.isNotNull(NAME) && Utility.isNotNull(MOBILENO) ) {


                if(Utility.validatePassword(PASSWORD)){

                    invokeWS();

                }else{
                    SnackbarManager.show(
                                      com.nispok.snackbar.Snackbar.with(RegistrationDetails.this)
                                            .text("Password should contain 6-8 characters")
                                          .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                            );
                }
        }else{
            SnackbarManager.show(
                              com.nispok.snackbar.Snackbar.with(RegistrationDetails.this)
                                    .text("Enter all details")
                                  .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                    );
        }

    }


    public void invokeWS(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("emailId", EMAILID);
            obj.put("name",NAME);
            obj.put("password",PASSWORD);
            obj.put("mobileNo",MOBILENO);

            StringEntity jsonString = new StringEntity(obj.toString());


            AsyncHttpClient client = new AsyncHttpClient();

            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Registrationdetails", jsonString, "application/json", new AsyncHttpResponseHandler() {

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
                                    com.nispok.snackbar.Snackbar.with(RegistrationDetails.this)
                                            .text("Something went wrong")
                                            .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                            );
                        } else {

                            JSONObject userObj = obj.getJSONObject("user");

                            sessionManager.createLoginSession(userObj);
                            if (checkPlayServices()) {
                               registerGCM();
                                navigatetoUploadDisplayPicture();

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

    public void navigatetoUploadDisplayPicture(){
        Intent homeIntent = new Intent(getApplicationContext(),UploadDP.class);
        startActivity(homeIntent);
        finish();
    }

}
