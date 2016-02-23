package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nispok.snackbar.SnackbarManager;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by amitrajula on 2/23/16.
 */
public class SearchUser extends AppCompatActivity {




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_user_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final EditText et = (EditText) findViewById(R.id.email_search_user);


        Button continue_search_user = (Button) findViewById(R.id.continue_search_user);
        continue_search_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String etEmail = et.getText().toString();

                fetchUser(etEmail);


//                Intent i = new Intent(SearchUser.this,GetCode.class);
//                i.putExtra("emailId",etEmail);
//                startActivity(i);


            }
        });

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchUser.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void fetchUser(String email) {

        try {
            JSONObject obj = new JSONObject();
            obj.put("emailId", email);

            StringEntity jsonString = new StringEntity(obj.toString());


            AsyncHttpClient client = new AsyncHttpClient();

            client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Getuserbyemail", jsonString, "application/json", new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                // @Override
                public void onSuccess(String response) {
                    // called when response HTTP status is "200 OK"
                    try {
                        Log.i("res", response.toString());
                        JSONObject obj = new JSONObject(response);

                        if (obj.getBoolean("error")) {
                            SnackbarManager.show(
                                    com.nispok.snackbar.Snackbar.with(getApplicationContext())
                                            .text("Something went wrong")
                                            .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                            );
                        } else {

                            JSONObject user = obj.getJSONObject("outputObj");
                            if (user.getJSONObject("user").length() == 0) {

                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(getApplicationContext())
                                                .text("No record matched for email address")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                );

                            } else {
                                    JSONObject userdetails = user.getJSONObject("user");
                                    Intent i = new Intent(SearchUser.this,GetCode.class);
                                    i.putExtra("name",userdetails.optString("name"));
                                    i.putExtra("emailId",userdetails.optString("emailId"));
                                    i.putExtra("displayPicture",userdetails.optString("displayPicture"));
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

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException ee) {
            ee.printStackTrace();
        }
    }

}
