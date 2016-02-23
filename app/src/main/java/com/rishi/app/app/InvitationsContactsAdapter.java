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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rishi.app.app.Album;
import com.rishi.app.app.Models.InvitationsContactsModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InvitationsContactsAdapter extends RecyclerView.Adapter<InvitationsContactsAdapter.MyViewHolder> {

    private List<InvitationsContactsModel> contactsList;
    private ArrayList<String> arrayalbumMediaList = new ArrayList<String>();
    private Bitmap bitmap;
    String path = "",ID,NAME;
    //Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name,number;
        public Button send;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            name = (TextView) view.findViewById(R.id.name_contacts);
            number = (TextView) view.findViewById(R.id.number_contacts);
            send = (Button) view.findViewById(R.id.send_contacts);
            //date = (TextView) view.findViewById(R.id.date);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();


        }
    }

    public InvitationsContactsAdapter(List<InvitationsContactsModel> contactsList) {
        this.contactsList = contactsList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invitations_contacts_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
       final InvitationsContactsModel icm = contactsList.get(position);

        holder.name.setText(icm.getName());
        holder.number.setText(icm.getNumber());
        final Context c = holder.send.getContext();
        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", icm.getNumber());
                smsIntent.putExtra("sms_body", "Test");
                c.startActivity(Intent.createChooser(smsIntent, "SMS:"));

            }
        });
//        AlbumMedia am = albumMediaList.get(position);
//        // holder.name.setText(am.getName());
//        // holder.date.setText(am.getDate());
//        Context context = holder.path.getContext();
//        Picasso.with(context).load(am.getPath()).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
//                .into(holder.path);
//        path = albumMediaList.get(position).getPath();
//        arrayalbumMediaList.add(path);
    }



    @Override
    public int getItemCount() {
        return contactsList.size();
    }
}