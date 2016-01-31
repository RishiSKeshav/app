package com.rishi.app.app;

import org.apache.http.protocol.HTTP;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.preference.PreferenceActivity.Header;
import java.io.UnsupportedEncodingException;




public class LoginActivity extends ActionBarActivity {

    SessionManager sessionManager;

    EditText emailET;

    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sessionManager = new SessionManager(getApplicationContext());

        emailET = (EditText) findViewById(R.id.loginEmail);
        passwordET = (EditText) findViewById(R.id.loginPassword);
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

                        //Retrieve data
                        // If value is not present return second param value - In this case null
                        //pref.getString("key_name", null);

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


    public void navigatetoHomeActivity(){
        Intent homeIntent = new Intent(getApplicationContext(),HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }


    public void navigatetoRegisterActivity(View view){
        Intent loginIntent = new Intent(getApplicationContext(),RegisterActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

}