package com.rishi.app.app;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

/**
 * Created by RishiS on 2/21/2016.
 */

public class SyncFullScreenAdapter extends PagerAdapter {

    private Activity _activity;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;

    // constructor
    public SyncFullScreenAdapter(Activity activity,
                                  ArrayList<String> imagePaths) {
        this._activity = activity;
        this._imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;
        Button btnClose;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_sync_fullscreen, container,
                false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.syncImageDisplay);

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
//        imgDisplay.setImageBitmap(bitmap);

        final int MAX_WIDTH = 1024;
        final int MAX_HEIGHT = 768;

        int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));

        if(_imagePaths.get(position).startsWith("http")) {

            Context context = imgDisplay.getContext();

            Picasso.with(context)
                    .load(_imagePaths.get(position))
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .skipMemoryCache()
                    .resize(size, size)
                    .centerInside()
                    .into(imgDisplay);


            /*Picasso.with(context).load(_imagePaths.get(position)).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                    .into(imgDisplay);*/
        }
        else
        {
            Context context = imgDisplay.getContext();
            File f = new File(_imagePaths.get(position));
            Picasso.with(context).load(f).into(imgDisplay);
        }

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }



}
