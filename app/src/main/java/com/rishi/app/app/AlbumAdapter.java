package com.rishi.app.app;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rishi.app.app.Album;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {

    private List<Album> albumList;

    private Bitmap bitmap;
    //Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, date,count;
        public ImageView thumbnail;

            public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            name = (TextView) view.findViewById(R.id.name);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            count = (TextView) view.findViewById(R.id.count);
            date = (TextView) view.findViewById(R.id.date);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Album al = albumList.get(getPosition());
            Intent intent = new Intent(context,AlbumMediaDisplay.class);
            intent.putExtra("Id",al.getId());
            intent.putExtra("Name",al.getName());

            context.startActivity(intent);

        }
    }




    public AlbumAdapter(List<Album> albumList) {
        this.albumList = albumList;

       // this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.name.setText(album.getName());
        holder.count.setText(album.getCount());
        holder.date.setText(album.getDate());
        Context context = holder.thumbnail.getContext();

        Picasso.with(context)
                .load(album.getThumbnail())
                .transform(new BitmapTransform(500, 500))
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .skipMemoryCache()
                .fit()
                .centerCrop()
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}