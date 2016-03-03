package com.rishi.app.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by amitrajula on 2/23/16.
 */
public class PasswordReset extends AppCompatActivity {

    String NEWPASSWORD,EMAILID,CONFIRMPASSWORD;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reset);

        Toolbar toolbar = (Toolbar) findViewById(R.id.password_reset_toolbar);
        Intent i = getIntent();
        EMAILID = i.getStringExtra("emailId");

       final EditText et = (EditText) findViewById(R.id.new_password_edit);
        final EditText et2 = (EditText) findViewById(R.id.confirm_password_edit);


        Button reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                NEWPASSWORD = et.getText().toString();
                CONFIRMPASSWORD = et2.getText().toString();

                if (Utility.validatePassword(NEWPASSWORD)) {

                    if (Utility.checkMatchPassword(NEWPASSWORD, CONFIRMPASSWORD)) {

                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("emailId", EMAILID);
                            obj.put("password", NEWPASSWORD);

                            StringEntity jsonString = new StringEntity(obj.toString());


                            AsyncHttpClient client = new AsyncHttpClient();

                            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Passwordreset", jsonString, "application/json", new AsyncHttpResponseHandler() {

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
                                                    com.nispok.snackbar.Snackbar.with(PasswordReset.this)
                                                            .text("Something went wrong")
                                                            .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                            );
                                        } else {
                                            SnackbarManager.show(
                                                    com.nispok.snackbar.Snackbar.with(getApplicationContext())
                                                            .text("Password Reset Successful")
                                                            .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                                            .eventListener(new EventListener() {
                                                                @Override
                                                                public void onShow(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onShowByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onShown(com.nispok.snackbar.Snackbar snackbar) {

                                                                }


                                                                @Override
                                                                public void onDismiss(com.nispok.snackbar.Snackbar snackbar) {


                                                                    Intent homeIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                                                    //homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    startActivity(homeIntent);
                                                                    //finish();


                                                                }

                                                                @Override
                                                                public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                                                }
                                                            })
                                                    , PasswordReset.this);
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException ee) {
                            ee.printStackTrace();
                        }
                    }else{
                        SnackbarManager.show(
                                com.nispok.snackbar.Snackbar.with(PasswordReset.this)
                                        .text("New Password and Confirmation does not match")
                                        .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                        );
                    }
                }else{
                    SnackbarManager.show(
                            com.nispok.snackbar.Snackbar.with(PasswordReset.this)
                                    .text("Password should be 6 to 20 characters")
                                    .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                    );
                }
            }
        });

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PasswordReset.this,LoginActivity.class);
                startActivity(i);
            }
        });

    }



}
