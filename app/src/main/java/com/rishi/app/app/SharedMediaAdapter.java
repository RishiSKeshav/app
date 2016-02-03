package com.rishi.app.app;


import android.content.Context;
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

import com.rishi.app.app.Album;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SharedMediaAdapter extends RecyclerView.Adapter<SharedMediaAdapter.MyViewHolderSharedMedia> {

    private List<SharedMedia> sharedMediaList;
    private Bitmap bitmap;
    //Context context;

    public class MyViewHolderSharedMedia extends RecyclerView.ViewHolder {
        public TextView name, date;
        public ImageView path;

        public MyViewHolderSharedMedia(View view) {
            super(view);
            path = (ImageView) view.findViewById(R.id.path);

        }
    }


    public SharedMediaAdapter(List<SharedMedia> sharedMediaList) {
        this.sharedMediaList = sharedMediaList;
        // this.context = context;
    }

    @Override
    public MyViewHolderSharedMedia onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shared_media_list, parent, false);

        return new MyViewHolderSharedMedia(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolderSharedMedia holder, int position) {
        SharedMedia sm = sharedMediaList.get(position);
        //holder.name.setText(sm.getName());
        //holder.date.setText(sm.getDate());
        Context context = holder.path.getContext();
        Picasso.with(context).load(sm.getPath()).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                .into(holder.path);
    }

    @Override
    public int getItemCount() {
        return sharedMediaList.size();
    }
}