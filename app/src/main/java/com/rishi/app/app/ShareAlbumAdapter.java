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

public class ShareAlbumAdapter extends RecyclerView.Adapter<ShareAlbumAdapter.MyViewHolderShared> {

    private List<ShareAlbum> sharealbumList;
    private Bitmap bitmap;
    //Context context;

    public class MyViewHolderShared extends RecyclerView.ViewHolder {
        public TextView name, date,count,members;
        public ImageView thumbnail;

        public MyViewHolderShared(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            count = (TextView) view.findViewById(R.id.image_data);
            date = (TextView) view.findViewById(R.id.date_data);
            members = (TextView)view.findViewById(R.id.members_data);
        }
    }


    public ShareAlbumAdapter(List<ShareAlbum> sharealbumList) {
        this.sharealbumList = sharealbumList;
        // this.context = context;
    }

    @Override
    public MyViewHolderShared onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shared_album_list, parent, false);

        return new MyViewHolderShared(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolderShared holder, int position) {
        ShareAlbum sh = sharealbumList.get(position);
        holder.name.setText(sh.getName());
        holder.count.setText(sh.getCount());
        holder.date.setText(sh.getDate());
        holder.members.setText(sh.getMembers());
        Context context = holder.thumbnail.getContext();
        Picasso.with(context).load(sh.getThumbnail()).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return sharealbumList.size();
    }
}