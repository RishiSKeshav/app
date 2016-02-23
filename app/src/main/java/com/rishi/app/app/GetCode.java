package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by amitrajula on 2/23/16.
 */
public class GetCode extends AppCompatActivity {

    String EMAILID,NAME,DISPLAYPICTURE;
    TextView name,name2;
    CircleImageView displayPicture;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_code);

        Toolbar toolbar = (Toolbar) findViewById(R.id.get_code_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        EMAILID = i.getStringExtra("emailId");
        NAME = i.getStringExtra("name");
        DISPLAYPICTURE = i.getStringExtra("displayPicture");

        TextView name = (TextView)findViewById(R.id.get_code_name);
        name.setText(NAME);

        TextView name2 = (TextView)findViewById(R.id.get_code_name2);
        name2.setText("Send a password reset code to " + EMAILID);

        CircleImageView displayPicture= (CircleImageView) findViewById(R.id.get_code_circleView);
        Picasso.with(getApplicationContext()).load(DISPLAYPICTURE).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                .into(displayPicture);


        Button get_code_send = (Button) findViewById(R.id.get_code_send);
        get_code_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                            com.nispok.snackbar.Snackbar.with(GetCode.this)
                                                    .text("Something went wrong")
                                                    .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                    );
                                } else {
                                        Intent i = new Intent(GetCode.this,EmailAuthentication.class);
                                        i.putExtra("code",obj.getString("code"));
                                        i.putExtra("emailId",EMAILID);
                                        i.putExtra("action","password_rest");
                                        startActivity(i);
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
        });

        Button get_code_not_my_account = (Button) findViewById(R.id.get_code_not_my_account);
        get_code_not_my_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GetCode.this, SearchUser.class);
                startActivity(i);
            }
        });


    }


 }
