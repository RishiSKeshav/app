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

public class ToPersonalAlbumAdapter extends SelectableAdapter<ToPersonalAlbumAdapter.MyViewHolder> {
    @SuppressWarnings("unused")
    private List<Album> personalalbumList;
    private MyViewHolder.ClickListener clickListener;


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, date,count;
        public ImageView thumbnail,check;
        View selectedOverlay;
        private ClickListener listener;

        public MyViewHolder(View view, ClickListener listener) {
            super(view);
            view.setOnClickListener(this);
         //   view.setOnLongClickListener(this);
            this.listener = listener;
            thumbnail = (ImageView) view.findViewById(R.id.image_to_personal_album);
            name = (TextView) view.findViewById(R.id.name_to_personal_album);
            count = (TextView) view.findViewById(R.id.count_to_personal_album);
            selectedOverlay = view.findViewById(R.id.to_personal_album_selected_overlay);
            check = (ImageView) view.findViewById(R.id.to_personal_album_media_image_check);
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
         //   public boolean onItemLongClicked(int position);
        }
    }


    public ToPersonalAlbumAdapter(List<Album> personalalbumList, MyViewHolder.ClickListener clickListener) {
        super();
        this.personalalbumList = personalalbumList;
        this.clickListener = clickListener;
        // this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.to_personal_album_list, parent, false);

        return new MyViewHolder(itemView,clickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Album a = personalalbumList.get(position);
        holder.name.setText(a.getName());
        holder.count.setText(a.getCount() + " Items");
        Context c = holder.thumbnail.getContext();

        Picasso.with(c)
                .load(a.getThumbnail())
                .transform(new BitmapTransform(500, 500))
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .skipMemoryCache()
                .fit()
                .centerCrop()
                .into(holder.thumbnail);


        // Highlight the item if it's selected
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
        holder.check.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
        //holder.selectedOverlay.setBackgroundResource(R.drawable.ic_person_black_48dp);


    }

    @Override
    public int getItemCount() {
        return personalalbumList   .size();
    }
}