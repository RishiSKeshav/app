package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by amitrajula on 2/19/16.
 */
public class SharedMediaDisplay extends AppCompatActivity{

    String imagedisplay,ID;
    ArrayList<Integer> mediasIDS = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_media_display);

        Toolbar shared_media_dsiplay_toolbar= (Toolbar) findViewById(R.id.shared_media_display_toolbar);
        setSupportActionBar(shared_media_dsiplay_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        imagedisplay = i.getStringExtra("image");
        ID = i.getStringExtra("Id");


        ImageView image = (ImageView)findViewById(R.id.shared_media_display_image);

        Picasso.with(getApplicationContext()).load(imagedisplay)
                .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
                .into(image);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shared_media_display, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){
            Intent i = new Intent(SharedMediaDisplay.this,HomeActivity.class);
            i.putExtra("shared_media","2");
            startActivity(i);
        }

        if (id == R.id.to_personal_album) {
            Intent i = new Intent(SharedMediaDisplay.this,ToPersonalAlbum.class);
            i.putExtra("imagedisplay",imagedisplay);
            i.putExtra("Id",ID);
            startActivity(i);
        }

        if (id == R.id.to_shared_album) {
            Intent i = new Intent(SharedMediaDisplay.this,ToSharedAlbum.class);
            i.putExtra("imagedisplay",imagedisplay);
            i.putExtra("Id",ID);
            startActivity(i);
        }

        if (id == R.id.to_others) {
            mediasIDS.add(Integer.parseInt(ID));
            Intent i = new Intent(SharedMediaDisplay.this,Userbase.class);
            i.putExtra("action","shared_media");
            i.putExtra("imagedisplay", imagedisplay);
            i.putExtra("Id",ID);
            i.putIntegerArrayListExtra("mediaId", mediasIDS);

            startActivity(i);
        }



        return super.onOptionsItemSelected(item);
    }

}
