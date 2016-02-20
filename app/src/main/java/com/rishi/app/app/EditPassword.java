package com.rishi.app.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by amitrajula on 2/13/16.
 */
public class EditPassword extends AppCompatActivity {
    SessionManager sessionManager;
    LinearLayout layout_old_password;
    TextView edit_old_password,edit_new_password,edit_retype_password;
    private static final String TAG = EditPassword.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_password);

        Toolbar toolbar= (Toolbar) findViewById(R.id.edit_password_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionManager = new SessionManager(getApplicationContext());

        if(sessionManager.getPassword().isEmpty()){


        }else{
            layout_old_password= (LinearLayout)findViewById(R.id.layout_old_password);
            layout_old_password.setVisibility(View.VISIBLE);
        }

        edit_old_password = (TextView) findViewById(R.id.edit_old_password);
        edit_new_password = (TextView) findViewById(R.id.edit_new_password);
        edit_retype_password = (TextView) findViewById(R.id.edit_retype_password);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_profile, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
//            Intent i = new Intent(AlbumMediaSelect.this,AlbumMediaDisplay.class);
//            i.putExtra("Id",ID);
//            i.putExtra("Name",NAME);
//            AlbumMediaSelect.this.startActivity(i);

            onBackPressed();
            return true;
        }

        if (id == R.id.edit_profile_save) {


            try {
                JSONObject obj = new JSONObject();
                obj.put("action","check_password");
                obj.put("old_password", edit_old_password.getText().toString());
                obj.put("new_password",edit_new_password.getText().toString());
                obj.put("userId","1");
                StringEntity jsonString = new StringEntity(obj.toString());


                AsyncHttpClient client = new AsyncHttpClient();

                client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Editprofile", jsonString, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                    }

                    // @Override
                    public void onSuccess(String response) {
                        // called when response HTTP status is "200 OK"
                        try {
                            Log.i("ddd", response);
                            JSONObject obj = new JSONObject(response);

                            if (obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();



                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
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



        return super.onOptionsItemSelected(item);
    }





}
