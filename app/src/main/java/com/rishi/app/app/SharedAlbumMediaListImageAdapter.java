package com.rishi.app.app;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
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

public class SharedAlbumMediaListImageAdapter extends RecyclerView.Adapter<SharedAlbumMediaListImageAdapter.MyViewHolder> {

    private List<Media> mediaList;
    private ArrayList<String> arraysharedalbumMediaList = new ArrayList<String>();
    String path1 = "";

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        public TextView name, date;
        public ImageView path;


        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            path = (ImageView) view.findViewById(R.id.shared_album_media_list_media);
        }

        @Override
        public void onClick(View view) {

            Log.i("eee",arraysharedalbumMediaList.toString());
            int position = getAdapterPosition();

            String pos = Integer.toString(position);
            Context context = view.getContext();
            Intent intent = new Intent(context,FullScreenMediaDisplay.class);
            intent.putExtra("Position", pos);
            intent.putStringArrayListExtra("data", arraysharedalbumMediaList);
            context.startActivity(intent);
        }


    }

    public SharedAlbumMediaListImageAdapter(List<Media> mediaList) {
        this.mediaList = mediaList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shared_album_media_list_image, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Media m = mediaList.get(position);

          Context c = holder.path.getContext();

           Picasso.with(c).load(m.getPath()).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                .into(holder.path);
            arraysharedalbumMediaList.add(m.getPath());

        Log.i("qqq",arraysharedalbumMediaList.toString());

    }



    @Override
    public int getItemCount() {
        return mediaList.size();
    }
}