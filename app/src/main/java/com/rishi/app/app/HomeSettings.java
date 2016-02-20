package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by amitrajula on 2/13/16.
 */
public class HomeSettings extends AppCompatActivity {

    TextView editprofile;
    LinearLayout editprofilelayout,change_email,change_password;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_settings);

        Toolbar toolbar= (Toolbar) findViewById(R.id.home_settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());

        editprofilelayout = (LinearLayout) findViewById(R.id.edit_profile_layout);
        editprofilelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeSettings.this, EditProfile.class);
                HomeSettings.this.startActivity(i);
            }
        });

        change_email = (LinearLayout) findViewById(R.id.change_email);
        change_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeSettings.this,EditEmail.class );
                HomeSettings.this.startActivity(i);
            }
        });

        change_password = (LinearLayout) findViewById(R.id.change_password);
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeSettings.this,EditPassword.class );
                HomeSettings.this.startActivity(i);
            }
        });

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

        return super.onOptionsItemSelected(item);
    }
    }
