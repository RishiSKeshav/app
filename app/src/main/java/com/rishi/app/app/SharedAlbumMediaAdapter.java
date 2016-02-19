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

public class SharedAlbumMediaAdapter extends RecyclerView.Adapter<SharedAlbumMediaAdapter.MyViewHolder> {

    private List<SharedAlbumMedia> sharedalbumMediaList;
    private List<Media> mediaList = new ArrayList<>();
    private ArrayList<String> arrayalbumMediaList = new ArrayList<String>();
    public SharedAlbumMediaListImageAdapter samliAdapter;
    private Context mcontext;
    String pathfullscreen="",ID,NAME;



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, date;
        public ImageView path;
        //public RecyclerView recyclerView;

      //  public SharedAlbumMediaListImageAdapter samliAdapter;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            path = (ImageView) view.findViewById(R.id.shared_album_media_image);
//            recyclerView = (RecyclerView) view.findViewById(R.id.shared_album_media_recycler_view2);
//            samliAdapter = new SharedAlbumMediaListImageAdapter(mediaList);
       //     name = (TextView) view.findViewById(R.id.shared_album_user);

        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            String pos = Integer.toString(position);
            Context context = view.getContext();
            Intent intent = new Intent(context,FullScreenMediaDisplay.class);
            intent.putExtra("Position", pos);
            intent.putStringArrayListExtra("data",arrayalbumMediaList);
            intent.putExtra("Id",ID);
            intent.putExtra("Name",NAME);
            intent.putExtra("shared","yes");
            context.startActivity(intent);
        }

    }

    public SharedAlbumMediaAdapter(Context mcontext){
        this.mcontext = mcontext;
    }

    public SharedAlbumMediaAdapter(List<Media> mediaList,String ID,String NAME) {
        this.mediaList = mediaList;
        this.ID = ID;
        this.NAME = NAME;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shared_album_media_list, parent, false);

     //   MyViewHolder h = new MyViewHolder(itemView);
//        h.recyclerView.setHasFixedSize(true);
//
//       // Context c = h.recyclerView.getContext();
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mcontext,4);
//        h.recyclerView.setHorizontalScrollBarEnabled(true);
//        h.recyclerView.setLayoutManager(mLayoutManager);
//        h.recyclerView.setAdapter(samliAdapter);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Media m = mediaList.get(position);

        Context context = holder.path.getContext();
        Picasso.with(context).load(m.getPath()).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                .into(holder.path);

        pathfullscreen = mediaList.get(position).getPath();
        arrayalbumMediaList.add(pathfullscreen);


    }


    @Override
    public int getItemCount() {
        return mediaList.size();
    }
}