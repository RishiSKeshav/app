package com.rishi.app.app;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SharedAlbumAdapter extends RecyclerView.Adapter<SharedAlbumAdapter.MyViewHolderShared> {

    private List<SharedAlbum> sharealbumList;
    private Bitmap bitmap;
    //Context context;

    public class MyViewHolderShared extends RecyclerView.ViewHolder implements  View.OnClickListener{
        public TextView name, date,count,members;
        public ImageView thumbnail;

        public MyViewHolderShared(View view) {
            super(view);
            view.setOnClickListener(this);
            name = (TextView) view.findViewById(R.id.name);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            count = (TextView) view.findViewById(R.id.image_data);
            date = (TextView) view.findViewById(R.id.date_data);
            members = (TextView)view.findViewById(R.id.members_data);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            SharedAlbum sm = sharealbumList.get(getPosition());
            Intent intent = new Intent(context,SharedAlbumMediaDisplay.class);
            intent.putExtra("Id",sm.getId());
            intent.putExtra("Name",sm.getName());

            context.startActivity(intent);

        }
    }



    public SharedAlbumAdapter(List<SharedAlbum> sharealbumList) {
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
        SharedAlbum sh = sharealbumList.get(position);
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