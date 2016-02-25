package com.rishi.app.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RishiS on 2/19/2016.
 */

public class SyncMediaSyncAdapter extends RecyclerView.Adapter<SyncMediaSyncAdapter.MyViewHolder> {

    private ArrayList<String> syncMediaList = new ArrayList<String>();
    private Bitmap bitmap;
    //String path = "";
    //Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, date;
        public ImageView path,check,check1;
        View selectedOverlay;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            // name = (TextView) view.findViewById(R.id.name);
            path = (ImageView) view.findViewById(R.id.syncMediasyncImgview);

        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            String pos = Integer.toString(position);
            Context context = view.getContext();
            Intent intent = new Intent(context,SyncMediaFullScreenActivity.class);
            intent.putExtra("Position", pos);
            intent.putStringArrayListExtra("data",syncMediaList);
            context.startActivity(intent);
        }
    }

    public SyncMediaSyncAdapter(ArrayList<String> List) {
        this.syncMediaList = List;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_sync_media_sync_display, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Context c = holder.path.getContext();

        Picasso.with(c).load(syncMediaList.get(position)).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                    .into(holder.path);

    }


    @Override
    public int getItemCount() {
        return syncMediaList.size();
    }
}