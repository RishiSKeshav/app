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
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;

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
    EditText mobilenoET;
    private String TAG = RegisterActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        sessionManager = new SessionManager(getApplicationContext());

       // nameET = (EditText)findViewById(R.id.registername);
        emailET = (EditText)findViewById(R.id.registerEmail);
        //passwordET = (EditText)findViewById(R.id.registerPassword);
       // mobilenoET = (EditText)findViewById(R.id.registermobileNo);

    }

    /**
     * Method gets triggered when Register button is clicked
     *
     * @param view
     */
    public void registerUser(View view) throws JSONException, UnsupportedEncodingException {


        //String name = nameET.getText().toString();
        // String mobileNo = mobilenoET.getText().toString();
        String emailId = emailET.getText().toString();
        // String password = passwordET.getText().toString();

        JSONObject jsonObject = new JSONObject();

        //  if(Utility.isNotNull(emailId) && Utility.isNotNull(password) && Utility.isNotNull(name) && Utility.isNotNull(mobileNo) ) {
//
        if (Utility.isNotNull(emailId) || Utility.validateEmail(emailId)) {

//                if(Utility.validatePassword(password)){

            //                  jsonObject.put("name", name);
            jsonObject.put("emailId", emailId);
            //                jsonObject.put("password", password);
            //              jsonObject.put("mobileNo",mobileNo);
            //            jsonObject.put("displayPicture","displayPicture");
            invokeWS(jsonObject);
            //      }
            //    else
            //  {
            //    SnackbarManager.show(
            //          com.nispok.snackbar.Snackbar.with(RegisterActivity.this)
            //                .text("Password should contain 6-8 characters")
            //              .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
            //);

            //Toast.makeText(getApplicationContext(), "Password should contain 6-8 characters", Toast.LENGTH_LONG).show();
        }

        //}
        else {
            SnackbarManager.show(
                    com.nispok.snackbar.Snackbar.with(RegisterActivity.this)
                            .text("Email is not valid!!")
                            .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
            );
            //  Toast.makeText(getApplicationContext(), "Email is not valid!!", Toast.LENGTH_LONG).show();
        }
    }
      //  }
      //  else{
        //    SnackbarManager.show(
          //          com.nispok.snackbar.Snackbar.with(RegisterActivity.this)
            //                .text("Please enter all values")
              //              .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
            //);

            //Toast.makeText(getApplicationContext(), "Email or Password is Empty!", Toast.LENGTH_LONG).show();
      //  }
    //}

    /**
     * Method that performs RESTful webservice invocations
     *
     //* @param params
     */
//    public void invokeWS(JSONObject object) throws UnsupportedEncodingException{
//
//        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Creating Account...");
//        progressDialog.show();
//
//
//        StringEntity jsonString = new StringEntity(object.toString());
//        AsyncHttpClient client = new AsyncHttpClient();
//      //  client.post(getApplicationContext(),"http://52.89.2.186/project/webservice/public/Register", jsonString,"application/json", new AsyncHttpResponseHandler() {
//            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Register", jsonString, "application/json", new AsyncHttpResponseHandler() {
//                // When the response returned by REST has Http response code '200'
//                @Override
//                public void onSuccess(String response) {
//
//                    try {
//                        JSONObject obj = new JSONObject(response);
//
//                        if (obj.getBoolean("error")) {
//
//                            SnackbarManager.show(
//                                    com.nispok.snackbar.Snackbar.with(RegisterActivity.this)
//                                            .text("Oops. Something went wrong !!")
//                                            .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
//                            );
//
//                            //Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
//                        } else {
//
//                            JSONObject userObj = obj.getJSONObject("user");
//
//                            sessionManager.createLoginSession(userObj);
//                            if (checkPlayServices()) {
//                               registerGCM();
//                                navigatetoUploadDisplayPicture();
//
//                            }
//
//                        }
//                    }catch (JSONException e) {
//                        // TODO Auto-generated catch block
//                        Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                    }
//
//                }
//
//
//                @Override
//                public void onFailure(int statusCode, Throwable error,
//                                      String content) {
//
//                    SnackbarManager.show(
//                            com.nispok.snackbar.Snackbar.with(RegisterActivity.this)
//                                    .text("Oops. Something went wrong")
//                                    .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
//                    );
//
//                    //Toast.makeText(getApplicationContext(), "Error Occured [", Toast.LENGTH_LONG).show();
//                }
//
//            });
//    }


    public void invokeWS(final JSONObject object){


        try {
            JSONObject obj = new JSONObject();
            obj.put("emailId", object.optString("emailId"));

            StringEntity jsonString = new StringEntity(obj.toString());


            AsyncHttpClient client = new AsyncHttpClient();

            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Register", jsonString, "application/json", new AsyncHttpResponseHandler() {

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
                                    com.nispok.snackbar.Snackbar.with(RegisterActivity.this)
                                            .text("Something went wrong")
                                            .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                            );
                        } else {

                            if(obj.getBoolean("user")){
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(RegisterActivity.this)
                                                .text("User with emailId already exist")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                );

                            }else{

                                Intent i = new Intent(RegisterActivity.this,EmailAuthentication.class);
                                i.putExtra("code",obj.getString("code"));
                                i.putExtra("emailId",object.optString("emailId"));
                                startActivity(i);

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



//
//        Intent i = new Intent(RegisterActivity.this,EmailAuthentication.class);
//        i.putExtra("name",object.optString("name"));
//        i.putExtra("emailId",object.optString("emailId"));
//        i.putExtra("password",object.optString("password"));
//        i.putExtra("mobileNo",object.optString("mobileNo"));
//        i.putExtra("displayPicture",object.optString("displayPicture"));
//        startActivity(i);
    }
    /**
     * Method which navigates from Register Activity to Login Activity
     */
    public void navigatetoLoginActivity(View view){
        Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }








}