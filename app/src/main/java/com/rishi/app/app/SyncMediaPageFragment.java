package com.rishi.app.app;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by amitrajula on 1/31/16.
 */
public class SyncMediaPageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage,countalbum,countsharedalbum,countsharedmedia;
    Context context;
    private ArrayList<com.rishi.app.app.Image> imageList;
    private ArrayList<String> syncedPathList = new ArrayList<>();
    private ArrayList<com.rishi.app.app.Image> unSyncImageList;
    private ArrayList<String> cameraImageList = new ArrayList<>();
    private ArrayList<String> syncImageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SyncMediaUnsyncAdapter smuAdapter;
    private SyncMediaSyncAdapter smsAdapter;
    private SyncMediaCameraAdapter smcAdapter;
    private SwipeRefreshLayout aswipeRefreshLayout;
    SessionManager sessionManager;


    public static SyncMediaPageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        SyncMediaPageFragment fragment = new SyncMediaPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

        sessionManager= new SessionManager(getContext());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sync_media_fragment_page, container, false);

        aswipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.sync_media_refresh_layout);
        initializeImageLists();

        if(mPage == 1) {

            recyclerView = (RecyclerView) view.findViewById(R.id.sync_media_recycler_view);
            smuAdapter = new SyncMediaUnsyncAdapter(unSyncImageList);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(),3);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(smuAdapter);

//            prepareUnsyncData();
//
            aswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    initializeImageLists();
                }
            });
        }

        if (mPage == 2){
            recyclerView = (RecyclerView) view.findViewById(R.id.sync_media_recycler_view);
            smsAdapter = new SyncMediaSyncAdapter(syncImageList);

            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager sLayoutManager = new GridLayoutManager(getContext(),3);
            recyclerView.setLayoutManager(sLayoutManager);
            recyclerView.setAdapter(smsAdapter);
//            prepareSyncData();
//
            aswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    initializeImageLists();
                }
            });

        }

        if(mPage == 3){
            recyclerView = (RecyclerView) view.findViewById(R.id.sync_media_recycler_view);
            smcAdapter = new SyncMediaCameraAdapter(cameraImageList);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager smLayoutManager = new GridLayoutManager(getContext(),3);
            recyclerView.setLayoutManager(smLayoutManager);
            recyclerView.setAdapter(smcAdapter);
//            prepareCameraData();
//
            aswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    initializeImageLists();
                }
            });
        }

        return view;
    }


    private void initializeImageLists() {

        generateImageList();
        generateDBImageList();
        generateUnsyncedImageList();
        generateCameraImageList();
        generateSyncImageList();


        aswipeRefreshLayout.setRefreshing(false);
    }

//    private void prepareUnsyncData() {
//
//        generateImageList();
//        generateDBImageList();
//        generateUnsyncedImageList();
//
//    }
//
//    private void prepareSyncData(){
//
//        generateSyncImageList();
//
//    }
//
//
//    private void prepareCameraData(){
//
//        generateCameraImageList();
//
//    }





    private void generateUnsyncedImageList() {

        unSyncImageList = new ArrayList<Image>();

        for(com.rishi.app.app.Image img : imageList){

            if(!syncedPathList.contains(img.path))

                unSyncImageList.add(img);
//                smuAdapter.notifyDataSetChanged();
        }

        // Log.i("eeee", unSyncImageList.toString());
    }

    private void generateDBImageList() {

        ImageDatabaseHandler db = new ImageDatabaseHandler(getContext(), Environment.getExternalStorageDirectory().toString()+ "/ClikApp");

        if(db!=null){

            syncedPathList= db.getAllImagePath(sessionManager.getId());

        }
        else
            Log.i("databasae_name","DB not created");

        db.close();

        Log.d("DB list count ",String.valueOf(syncedPathList.size()));
    }



    private void generateImageList() {

        imageList = new ArrayList<com.rishi.app.app.Image>();

        ContentResolver cr = getContext().getContentResolver();

        String[] columns = new String[] {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                com.rishi.app.app.Image image = new com.rishi.app.app.Image();

                image.setPath(cursor.getString(0));
                image.setName(cursor.getString(1));

                imageList.add(image);
            } while (cursor.moveToNext());
        }
        cursor.close();

        Log.d("image list count", String.valueOf(imageList.size()));
    }

    private void generateCameraImageList() {

        ImageDatabaseHandler db = new ImageDatabaseHandler(getContext(), Environment.getExternalStorageDirectory().toString()+ "/ClikApp");

        if(db!=null){

            cameraImageList= db.getAllCameraImagePath(sessionManager.getId());

        }
        else
            Log.i("databasae_name","DB not created");

        db.close();

        Log.d("DB list count ",String.valueOf(cameraImageList.size()));
    }

    private void generateSyncImageList() {

        ImageDatabaseHandler db = new ImageDatabaseHandler(getContext(), Environment.getExternalStorageDirectory().toString()+ "/ClikApp");

        if(db!=null){

            syncImageList= db.getAllSyncImagePath(sessionManager.getId());



        }
        else
            Log.i("databasae_name","DB not created");

        db.close();

        Log.d("DB list count ",String.valueOf(syncImageList.size()));
    }
}