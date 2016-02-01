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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;

/**
 * Created by RishiS on 1/27/2016.
 */

public class RegisterActivity extends Activity {

    SessionManager sessionManager;

    EditText firstNameET;
    EditText lastNameET;
    EditText emailET;
    EditText passwordET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);


        sessionManager = new SessionManager(getApplicationContext());


        firstNameET = (EditText)findViewById(R.id.registerFirstName);
        lastNameET = (EditText)findViewById(R.id.registerLastName);
        emailET = (EditText)findViewById(R.id.registerEmail);
        passwordET = (EditText)findViewById(R.id.registerPassword);

    }

    /**
     * Method gets triggered when Register button is clicked
     *
     * @param view
     */
    public void registerUser(View view) throws JSONException, UnsupportedEncodingException {


        String firstName = firstNameET.getText().toString();
        String lastName = lastNameET.getText().toString();
        String emailId = emailET.getText().toString();
        String password = passwordET.getText().toString();

        JSONObject jsonObject = new JSONObject();

        if(Utility.isNotNull(emailId) && Utility.isNotNull(password)) {
            jsonObject.put("firstName", firstName);
            jsonObject.put("lastName", lastName);
            jsonObject.put("emailId", emailId);
            jsonObject.put("password", password);
            jsonObject.put("mobileNo","6263203932");
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
        Log.i("SER","reached");
        AsyncHttpClient client = new AsyncHttpClient();
      //  client.post(getApplicationContext(),"http://52.89.2.186/project/webservice/public/Register", jsonString,"application/json", new AsyncHttpResponseHandler() {
            client.post(getApplicationContext(),"http://52.89.2.186/project/webservice/public/Register",jsonString,"application/json", new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try{
                    JSONObject obj = new JSONObject(response);

                    if(obj.getBoolean("error")){
                        Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                    }else{

                        JSONObject userObj = obj.getJSONObject("user");
                      //  Toast.makeText(getApplicationContext(),userObj.getString("lastName"), Toast.LENGTH_LONG).show();

                        sessionManager.createLoginSession(userObj);


                        navigatetoHomeActivity();

                    }

                }catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }

                }


            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content){

    Toast.makeText(getApplicationContext(), "Error Occured [", Toast.LENGTH_LONG).show();

            }

        });
    }

    /**
     * Method which navigates from Register Activity to Login Activity
     */
    public void navigatetoLoginActivity(View view){
        Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
        // Clears History of Activity
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    public void navigatetoHomeActivity(){
        Intent homeIntent = new Intent(getApplicationContext(),HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }


}