package com.rishi.app.app;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

public class AlbumMediaAdapter extends RecyclerView.Adapter<AlbumMediaAdapter.MyViewHolder> {

    private List<AlbumMedia> albumMediaList;
    private ArrayList<String> arrayalbumMediaList = new ArrayList<String>();
    private Bitmap bitmap;
    String path = "",ID,NAME;
    //Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, date;
        public ImageView path;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
           // name = (TextView) view.findViewById(R.id.name);
            path = (ImageView) view.findViewById(R.id.album_media);
            //date = (TextView) view.findViewById(R.id.date);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

           String pos = Integer.toString(position);
            Context context = view.getContext();
            Intent intent = new Intent(context,FullScreenMediaDisplay.class);
            intent.putExtra("Position", pos);
            intent.putStringArrayListExtra("data",arrayalbumMediaList);
            intent.putExtra("shared","no");
            intent.putExtra("Id",ID);
            intent.putExtra("Name",NAME);
            context.startActivity(intent);

        }
    }

    public AlbumMediaAdapter(List<AlbumMedia> albumMediaList,String ID,String NAME) {
        this.albumMediaList = albumMediaList;
        this.ID = ID;
        this.NAME = NAME;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_media_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
//        layoutParams.setFullSpan(true);
        AlbumMedia am = albumMediaList.get(position);
       // holder.name.setText(am.getName());
       // holder.date.setText(am.getDate());
        Context c = holder.path.getContext();
        Picasso.with(c)
                .load(am.getPath())
                .transform(new BitmapTransform(500, 500))
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .skipMemoryCache()
                .fit()
                .centerCrop()
                .into(holder.path);
            path = albumMediaList.get(position).getPath();
            arrayalbumMediaList.add(path);
    }



    @Override
    public int getItemCount() {
        return albumMediaList.size();
    }
}