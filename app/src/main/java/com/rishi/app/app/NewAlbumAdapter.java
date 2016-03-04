package com.rishi.app.app;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
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

public class NewAlbumAdapter extends SelectableAdapter<NewAlbumAdapter.MyViewHolder> {
    @SuppressWarnings("unused")
    private List<Media> mediaList;
    private MyViewHolder.ClickListener clickListener;


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, date,count;
        public ImageView path,check;
        View selectedOverlay;
        private ClickListener listener;

        public MyViewHolder(View view, ClickListener listener) {
            super(view);
            view.setOnClickListener(this);
           // view.setOnLongClickListener(this);
            this.listener = listener;
            path = (ImageView) view.findViewById(R.id.new_album);
            selectedOverlay = view.findViewById(R.id.selected_overlay);
            check = (ImageView) view.findViewById(R.id.image_check);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getPosition());
            }
        }

//        @Override
//        public boolean onLongClick(View v) {
//            if (listener != null) {
//                return listener.onItemLongClicked(getPosition());
//            }
//
//            return false;
//        }

        public interface ClickListener {
            public void onItemClicked(int position);
           // public boolean onItemLongClicked(int position);
        }
    }


    public NewAlbumAdapter(List<Media> mediaList, MyViewHolder.ClickListener clickListener) {
        super();
        this.mediaList = mediaList;
        //Log.i("data",mediaList.toString());
        this.clickListener = clickListener;
        // this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_album_list, parent, false);

        return new MyViewHolder(itemView,clickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Media m = mediaList.get(position);

        Context context = holder.path.getContext();

        Picasso.with(context).load(m.getPath()).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                .into(holder.path);


        // Highlight the item if it's selected
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
        holder.check.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
        //holder.selectedOverlay.setBackgroundResource(R.drawable.ic_person_black_48dp);


    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }
}