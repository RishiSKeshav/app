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
import com.rishi.app.app.Models.NotificationsModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends SelectableAdapter<NotificationAdapter.MyViewHolder> {

    private List<NotificationsModel> notifications;



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView dataThumbnail;
        CircleImageView from;
        public TextView message;


        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            from = (CircleImageView) view.findViewById(R.id.from);
            message = (TextView)view.findViewById(R.id.message);
            dataThumbnail = (ImageView) view.findViewById(R.id.dataThumbnail);
        }

        @Override
        public void onClick(View v) {

            Context context = v.getContext();
            NotificationsModel nm = notifications.get(getPosition());
            if(nm.getAction().equals("shared_album")) {
                Intent intent = new Intent(context, SharedAlbumMediaDisplay.class);
                intent.putExtra("Id", nm.getDataId());
                intent.putExtra("Name", nm.getDataName());
                context.startActivity(intent);
            }else{
                Intent intent = new Intent(context, SharedMediaDisplay.class);
                intent.putExtra("Id", nm.getDataId());
                intent.putExtra("image", nm.getDataName());
                context.startActivity(intent);
            }
        }


    }


    public NotificationAdapter(List<NotificationsModel> notifications) {
       // super();
        this.notifications = notifications;

        // this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotificationsModel n = notifications.get(position);

        Context context = holder.from.getContext();
        Picasso.with(context).load(n.getFrom()).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                .into(holder.from);

        holder.message.setText(n.getMessage());

        Context context1 = holder.dataThumbnail.getContext();
        Picasso.with(context1).load(n.getDatathumbnail()).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                .into(holder.dataThumbnail);



    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}