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
import android.os.Bundle;
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




public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }



    public void  loginUser(View view) throws JSONException, UnsupportedEncodingException {
        String login = "amit.rajula@gmail.com";
        String password = "test1234s";

        JSONObject jsonObject = new JSONObject();
        if(Utility.isNotNull(login) && Utility.isNotNull(password)) {
            jsonObject.put("emailId", "amit.rajula@gmail.com");
            jsonObject.put("password", "test1234s");
            invokeWS(jsonObject);
        }
        else{
            Toast.makeText(getApplicationContext(), "Proszę wypełnić wszystkie pola!", Toast.LENGTH_LONG).show();
        }
    }

    private void invokeWS(JSONObject jsonObject) throws UnsupportedEncodingException {

        StringEntity se = new StringEntity(jsonObject.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getApplicationContext(),"http://52.89.2.186/project/webservice/public/Login",se,"application/json", new AsyncHttpResponseHandler() {

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
                        Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
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