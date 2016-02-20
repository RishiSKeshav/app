package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.TextAttribute;
import java.io.UnsupportedEncodingException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by amitrajula on 2/13/16.
 */
public class EditProfile extends AppCompatActivity {
    SessionManager sessionManager;
    TextView nameTV,mobilenoTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        Toolbar toolbar= (Toolbar) findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());

        CircleImageView imageView = (CircleImageView) findViewById(R.id.edit_profile_image);

        Picasso.with(getApplicationContext()).load(sessionManager.getDisplayPicture())
                .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
                .into(imageView);

        nameTV = (TextView)findViewById(R.id.edit_name);
        nameTV.setText(sessionManager.getName());

        mobilenoTV = (TextView)findViewById(R.id.edit_mobileno);
        mobilenoTV.setText(sessionManager.getMobileNo());

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
                obj.put("action","edit_profile");
                obj.put("name", nameTV.getText().toString());
                obj.put("mobileNo", mobilenoTV.getText().toString());
                obj.put("userId",sessionManager.getId());
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
                            Log.i("sss", response);
                            JSONObject obj = new JSONObject(response);

                            if (obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();

                                sessionManager.changeNameMobileNo(nameTV.getText().toString(),mobilenoTV.getText().toString());
                                Intent i = new Intent(EditProfile.this, HomeSettings.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                EditProfile.this.startActivity(i);

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
