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

public class SyncMediaUnsyncAdapter extends RecyclerView.Adapter<SyncMediaUnsyncAdapter.MyViewHolder> {

    private ArrayList<Image> unsyncMediaList = new ArrayList<Image>();
    private ArrayList<String> unsyncMediaPathList = new ArrayList<String>();
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
            path = (ImageView) view.findViewById(R.id.syncMedaiImgview);
            selectedOverlay = view.findViewById(R.id.sync_media_select_selected_overlay);
            check = (ImageView) view.findViewById(R.id.sync_media_select_image_check);
            check1 = (ImageView) view.findViewById(R.id.sync_media_select_image_check1);
            //date = (TextView) view.findViewById(R.id.date);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            String pos = Integer.toString(position);
            Context context = view.getContext();
            Intent intent = new Intent(context,SyncMediaFullScreenActivity.class);
            intent.putExtra("Position", pos);
            intent.putExtra("action","unsync");
            intent.putStringArrayListExtra("data",unsyncMediaPathList);
            context.startActivity(intent);
        }
    }

    public SyncMediaUnsyncAdapter(ArrayList<Image> List) {
        this.unsyncMediaList = List;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_sync_media_display, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Image sm = unsyncMediaList.get(position);

        String imgPath;

//        if(sm.getSyncStatus()) {
//
//            Log.d("no overlay", String.valueOf(sm.getSyncStatus()));
//
//            Context context = holder.path.getContext();
//            Picasso.with(context).load(sm.getLink()).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
//                    .into(holder.path);
//
//            holder.check.setVisibility(View.INVISIBLE);
//
//            imgPath = syncMediaList.get(position).getLink();
//            syncMediaPathList.add(imgPath);
//        }
//        else
//        {
          ///  Log.d("overlay", String.valueOf(sm.()));


            Context c = holder.path.getContext();

            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inScaled=false;

            Bitmap bitmap = decodeSampledBitmapFromPath(sm.getPath(),300,300);
            holder.path.setImageBitmap(bitmap);

            holder.check.setVisibility(View.VISIBLE);

            imgPath = unsyncMediaList.get(position).getPath();
            unsyncMediaPathList.add(imgPath);
       // }
    }

    public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth,
                                                     int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        return bmp;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
    {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    @Override
    public int getItemCount() {
        return unsyncMediaList.size();
    }
}