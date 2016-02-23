package com.rishi.app.app;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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
import java.util.List;

/**
 * Created by amitrajula on 1/31/16.
 */
public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage,countalbum,countsharedalbum,countsharedmedia;
    Context context;
    private List<Album> albumList = new ArrayList<>();
    private List<SharedAlbum> sharealbumlist = new ArrayList<>();
    private List<SharedMedia> sharedMediaList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlbumAdapter mAdapter;
    private SharedAlbumAdapter sAdapter;
    private SharedMediaAdapter smAdapter;
    private SwipeRefreshLayout aswipeRefreshLayout;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_page, container, false);

        if(mPage == 1) {
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            aswipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.pager_refresh_layout);
            mAdapter = new AlbumAdapter(albumList);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(),2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(mAdapter);

            prepareAlbumData();

            aswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    prepareAlbumData();
                }
            });





        }

        if (mPage == 2){
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            sAdapter = new SharedAlbumAdapter(sharealbumlist);
            aswipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.pager_refresh_layout);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager sLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(sLayoutManager);
            recyclerView.setAdapter(sAdapter);
            prepareShareAlbumData();

            aswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    prepareShareAlbumData();
                }
            });

        }

        if(mPage == 3){
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            smAdapter = new SharedMediaAdapter(sharedMediaList);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager smLayoutManager = new GridLayoutManager(getContext(),3);
            recyclerView.setLayoutManager(smLayoutManager);
            recyclerView.setAdapter(smAdapter);
            prepareSharedMediaData();
        }

        return view;
    }

    private void prepareAlbumData() {
        albumList.clear();
        try {
            JSONObject obj = new JSONObject();
            obj.put("userId", "1");
            obj.put("shared", "no");
           StringEntity jsonString = new StringEntity(obj.toString());


        AsyncHttpClient client = new AsyncHttpClient();

        client.post(this.context, "http://52.89.2.186/project/webservice/public/Getalbum", jsonString, "application/json", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            // @Override
            public void onSuccess(String response) {
                // called when response HTTP status is "200 OK"
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("error")) {
                        Toast.makeText(context, obj.getString("msg"), Toast.LENGTH_LONG).show();
                    } else {

                        JSONObject albumObj = obj.getJSONObject("outputObj");
                        JSONArray albumarray = albumObj.optJSONArray("album");
                        countalbum = albumarray.length();
                        if(countalbum > 0) {
                            for (int i = 0; i < albumarray.length(); i++) {
                                JSONObject albumdetails = albumarray.optJSONObject(i);

                                Album al = new Album(albumdetails.optString("id"), albumdetails.optString("name"), albumdetails.optString("thumbnail"),
                                        albumdetails.optString("count"), albumdetails.optString("date"));
                                albumList.add(al);
                                mAdapter.notifyDataSetChanged();
                            }
                        }else{

                        }
                    }
                    aswipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            //@Override
            public void onFailure(int statusCode, PreferenceActivity.Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            //@Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }


                });

        }catch (JSONException e) {
            e.printStackTrace();
        }catch(UnsupportedEncodingException ee){
            ee.printStackTrace();
        }
        }

        private void prepareShareAlbumData(){
            sharealbumlist.clear();
            try {
                JSONObject obj = new JSONObject();
                obj.put("userId", "1");
                obj.put("shared", "yes");
                StringEntity jsonString = new StringEntity(obj.toString());


                AsyncHttpClient client = new AsyncHttpClient();

                client.post(this.context, "http://52.89.2.186/project/webservice/public/Getsharedalbum", jsonString, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                    }

                    // @Override
                    public void onSuccess(String response) {
                        // called when response HTTP status is "200 OK"
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (obj.getBoolean("error")) {
                                Toast.makeText(context, obj.getString("msg"), Toast.LENGTH_LONG).show();
                            } else {

                                JSONObject albumObj = obj.getJSONObject("outputObj");
                                JSONArray albumarray = albumObj.optJSONArray("album");
                                countsharedalbum = albumarray.length();
                                if(countsharedalbum > 0) {
                                    for (int i = 0; i < albumarray.length(); i++) {
                                        JSONObject albumdetails = albumarray.optJSONObject(i);


                                        SharedAlbum sa = new SharedAlbum(albumdetails.optString("id"),albumdetails.optString("name"), albumdetails.optString("thumbnail"),
                                                albumdetails.optString("count"), albumdetails.optString("date"), albumdetails.optString("members"));
                                        sharealbumlist.add(sa);
                                        sAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            aswipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }

                    //@Override
                    public void onFailure(int statusCode, PreferenceActivity.Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }

                    //@Override
                    public void onRetry(int retryNo) {
                        // called when request is retried
                    }


                });

            }catch (JSONException e) {
                e.printStackTrace();
            }catch(UnsupportedEncodingException ee){
                ee.printStackTrace();
            }
        }


        private void prepareSharedMediaData(){

            sharedMediaList.clear();
            try {
                JSONObject obj = new JSONObject();
                obj.put("userId", "1");
                StringEntity jsonString = new StringEntity(obj.toString());


                AsyncHttpClient client = new AsyncHttpClient();

                client.post(this.context, "http://52.89.2.186/project/webservice/public/Getsharedmediauser", jsonString, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                    }

                    // @Override
                    public void onSuccess(String response) {
                        // called when response HTTP status is "200 OK"
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (obj.getBoolean("error")) {
                                Toast.makeText(context, obj.getString("msg"), Toast.LENGTH_LONG).show();
                            } else {

                                JSONObject mediaObj = obj.getJSONObject("outputObj");
                                JSONArray mediaaaray = mediaObj.optJSONArray("media");
                                countsharedmedia = mediaaaray.length();
                                if(countsharedmedia > 0) {

                                    for (int i = 0; i < mediaaaray.length(); i++) {
                                        JSONObject mediadetails = mediaaaray.optJSONObject(i);


                                        SharedMedia sm = new SharedMedia(mediadetails.optString("id"),
                                                mediadetails.optString("name"), mediadetails.optString("path"),
                                                mediadetails.optString("date"));
                                        sharedMediaList.add(sm);
                                        smAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }

                    //@Override
                    public void onFailure(int statusCode, PreferenceActivity.Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }

                    //@Override
                    public void onRetry(int retryNo) {
                        // called when request is retried
                    }


                });

            }catch (JSONException e) {
                e.printStackTrace();
            }catch(UnsupportedEncodingException ee){
                ee.printStackTrace();
            }
        }
    }