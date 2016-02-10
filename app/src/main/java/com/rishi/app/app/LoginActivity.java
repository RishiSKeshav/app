package com.rishi.app.app;


import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.entity.StringEntity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.preference.PreferenceActivity.Header;
import java.io.UnsupportedEncodingException;




public class LoginActivity extends ActionBarActivity {
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    SessionManager sessionManager;

    EditText emailET;


    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager=CallbackManager.Factory.create();

        setContentView(R.layout.login);

        sessionManager = new SessionManager(getApplicationContext());

        emailET = (EditText) findViewById(R.id.loginEmail);
        passwordET = (EditText) findViewById(R.id.loginPassword);

        loginButton= (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email","user_friends");




        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(
//                                    JSONObject object,
//                                    GraphResponse response) {
//
//                                Log.e("response: ", response + "");
//                                try {
//
//                                    Log.i("sss",object.toString());
////                                    user = new User();
////                                    user.facebookID = object.getString("id").toString();
////                                    user.email = object.getString("email").toString();
////                                    user.name = object.getString("name").toString();
////                                    user.gender = object.getString("gender").toString();
////                                    PrefUtils.setCurrentUser(user, LoginActivity.this);
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
////                                Toast.makeText(LoginActivity.this, "welcome " + user.name, Toast.LENGTH_LONG).show();
////                                Intent intent = new Intent(LoginActivity.this, LogoutActivity.class);
////                                startActivity(intent);
////                                finish();
//
//                            }
//
//                        });
//
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,email,gender, birthday");
//                request.setParameters(parameters);
//                request.executeAsync();


                new GraphRequest(
                        loginResult.getAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.e("response: ", response + "");
                            }
                        }
                ).executeAsync();



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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    public void  loginUser(View view) throws JSONException, UnsupportedEncodingException {

        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        JSONObject jsonObject = new JSONObject();

        if(Utility.isNotNull(email) && Utility.isNotNull(password)) {
            jsonObject.put("emailId", email);
            jsonObject.put("password", password);
            invokeWS(jsonObject);
        }
        else{
            Toast.makeText(getApplicationContext(), "Proszę wypełnić wszystkie pola!", Toast.LENGTH_LONG).show();
        }
    }

    private void invokeWS(JSONObject jsonObject) throws UnsupportedEncodingException {

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
                    JSONObject obj = new JSONObject(response);

                    if(obj.getBoolean("error")){
                        Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                    }else{

                        JSONObject userObj = obj.getJSONObject("user");
                       // Toast.makeText(getApplicationContext(),"Welcome " + userObj.get("lastName") + "!", Toast.LENGTH_LONG).show();

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
        Log.i("SER","invoked navigatetoRegisterActivity() method");
        Intent loginIntent = new Intent(getApplicationContext(),RegisterActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    public void navigatetoHomeActivity(){
        Intent homeIntent = new Intent(getApplicationContext(),HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}