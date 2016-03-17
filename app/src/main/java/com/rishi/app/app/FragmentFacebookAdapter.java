package com.rishi.app.app;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

public class FragmentFacebookAdapter extends SelectableAdapter<FragmentFacebookAdapter.MyViewHolder> {

    //private List<AlbumMedia> albumMediaList;
    private ArrayList<FacebookFriends> friends = new ArrayList<FacebookFriends>();
    private Bitmap bitmap;
    private MyViewHolder.ClickListener clickListener;
    //String path = "";
    //Context context;


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public ImageView check,path;
        private ClickListener listener;
        View selectedOverlay;

        public MyViewHolder(View view,ClickListener listener) {
            super(view);
            view.setOnClickListener(this);
           // view.setOnLongClickListener(this);
            this.listener = listener;
            // name = (TextView) view.findViewById(R.id.name);
            path =  (ImageView) view.findViewById(R.id.facebook_displayPicture);
            name = (TextView) view.findViewById(R.id.facebook_name);
            check = (ImageView) view.findViewById(R.id.facebook_image_check);
            selectedOverlay = view.findViewById(R.id.facebook_selected_overlay);
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
       //     public boolean onItemLongClicked(int position);
        }
    }

    public FragmentFacebookAdapter(ArrayList<FacebookFriends> friends,MyViewHolder.ClickListener clickListener) {
        super();
        this.friends = friends;
        this.clickListener = clickListener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.facebook_fragment_list, parent, false);

        return new MyViewHolder(itemView,clickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        FacebookFriends ff = friends.get(position);

        holder.name.setText(ff.getName());

        Context context = holder.path.getContext();
        Picasso.with(context)
                .load(ff.getDisplayPicture())
                .transform(new BitmapTransform(500, 500))
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .skipMemoryCache()
                .fit()
                .centerCrop()
                .into(holder.path);

        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
        holder.check.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


}

