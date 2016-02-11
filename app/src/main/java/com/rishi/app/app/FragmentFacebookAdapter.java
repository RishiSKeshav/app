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

public class FragmentFacebookAdapter extends RecyclerView.Adapter<FragmentFacebookAdapter.MyViewHolder> {

    //private List<AlbumMedia> albumMediaList;
    private ArrayList<FacebookFriends> friends = new ArrayList<FacebookFriends>();
    private Bitmap bitmap;
    //String path = "";
    //Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView path;
        public CheckBox check;

        public MyViewHolder(View view) {
            super(view);
            // name = (TextView) view.findViewById(R.id.name);
            path =  (ImageView) view.findViewById(R.id.facebook_displayPicture);
            name = (TextView) view.findViewById(R.id.facebook_name);
            check = (CheckBox) view.findViewById(R.id.facebook_chkSelected);
        }
    }

    public FragmentFacebookAdapter(ArrayList<FacebookFriends> friends) {
        this.friends = friends;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.facebook_fragment_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        FacebookFriends ff = friends.get(position);

        holder.name.setText(ff.getFirstName() + " " + ff.getLastName());

        holder.check.setChecked(ff.isSelected());
        holder.check.setTag(ff);

        holder.check.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                FacebookFriends ffs = (FacebookFriends) cb.getTag();

                ffs.setSelected(cb.isChecked());
                friends.get(position).setSelected(cb.isChecked());

                Toast.makeText(
                        v.getContext(),
                        "Clicked on Checkbox: " + cb.getText() + " is "
                                + cb.isChecked(), Toast.LENGTH_LONG).show();
            }
        });

        Context context = holder.path.getContext();
        Picasso.with(context).load(ff.getDisplayPicture()).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                .into(holder.path);
    }



    @Override
    public int getItemCount() {
        return friends.size();
    }



    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
}

