package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
public class EmailAuthentication extends AppCompatActivity {
    String EMAILID,CODE,ACTION="";
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_authentication);

        Toolbar toolbar= (Toolbar) findViewById(R.id.email_authentication_toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        CODE = i.getStringExtra("code");
        EMAILID = i.getStringExtra("emailId");

        Log.i("CODE",CODE);

        if(i.hasExtra("action")){
            ACTION = "reset_password";
        }


        TextView tv = (TextView)findViewById(R.id.text_code);
        if(ACTION.equals("")) {
            tv.setText("You should have received a email with activation code. It was sent to " + EMAILID);
        }else{
            tv.setText("You should have received a email with pass reset code. It was sent to " + EMAILID);
        }
        et = (EditText) findViewById(R.id.code);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(CODE.equals(et.getText().toString())){

                    if(ACTION.equals("")) {

                        Intent i = new Intent(EmailAuthentication.this, RegistrationDetails.class);
                        i.putExtra("emailId", EMAILID);
                        startActivity(i);
                        finish();
                    }else{
                        Intent i = new Intent(EmailAuthentication.this, PasswordReset.class);
                        i.putExtra("emailId", EMAILID);
                        startActivity(i);
                        finish();
                    }


                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TextView noCode = (TextView)findViewById(R.id.noCode);
        noCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ACTION.equals("")){

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("emailId", EMAILID);

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
                                                com.nispok.snackbar.Snackbar.with(EmailAuthentication.this)
                                                        .text("Something went wrong")
                                                        .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                        );
                                    } else {

                                        if(obj.getBoolean("user")){
                                            SnackbarManager.show(
                                                    com.nispok.snackbar.Snackbar.with(EmailAuthentication.this)
                                                            .text("User with emailId already exist")
                                                            .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                            );

                                        }else{

//                                            Intent i = new Intent(EmailAuthentication.this,EmailAuthentication.class);
//                                            i.putExtra("code",obj.getString("code"));
//                                            i.putExtra("emailId",object.optString("emailId"));
//                                            startActivity(i);
//                                            finish();

                                            SnackbarManager.show(
                                                    com.nispok.snackbar.Snackbar.with(EmailAuthentication.this)
                                                            .text("Code sent to " + EMAILID)
                                                            .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                            );

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

                }else{


                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("emailId", EMAILID);

                        StringEntity jsonString = new StringEntity(obj.toString());


                        AsyncHttpClient client = new AsyncHttpClient();

                        client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Getcode", jsonString, "application/json", new AsyncHttpResponseHandler() {

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
                                                com.nispok.snackbar.Snackbar.with(EmailAuthentication.this)
                                                        .text("Something went wrong")
                                                        .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                        );
                                    } else {
//                                        Intent i = new Intent(GetCode.this,EmailAuthentication.class);
//                                        i.putExtra("code",obj.getString("code"));
//                                        i.putExtra("emailId",EMAILID);
//                                        i.putExtra("action","password_rest");
//                                        startActivity(i);
//                                        finish();

                                        SnackbarManager.show(
                                                com.nispok.snackbar.Snackbar.with(EmailAuthentication.this)
                                                        .text("Code has been sent to " + EMAILID)
                                                        .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                        );


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
            }
        });

    }
}
