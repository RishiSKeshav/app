package com.rishi.app.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RishiS on 2/6/2016.
 */

public class CameraImageAdapter extends PagerAdapter
{
    private Activity _activity;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;

    // constructor
    public CameraImageAdapter(Activity activity,
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
        View viewLayout = inflater.inflate(R.layout.layout_camera_imagefullscreen, container,
                false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);

        RelativeLayout layout = (RelativeLayout) viewLayout.findViewById(R.id.layout);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
        //imgDisplay.setImageBitmap(bitmap);

        if(imgDisplay.getParent()!=null)
            ((ViewGroup)imgDisplay.getParent()).removeView(imgDisplay); // <- fix
        imgDisplay.setImageBitmap(bitmap);
        layout.addView(imgDisplay);

       /* Picasso.with(context).load(_imagePaths.get(position)).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                .into(imgDisplay);
*/

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}