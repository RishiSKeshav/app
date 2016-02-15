package com.rishi.app.app;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.params.Face;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
 * Created by amitrajula on 2/9/16.
 */
public class FragmentFacebook extends Fragment implements FragmentFacebookAdapter.MyViewHolder.ClickListener{

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private RecyclerView recyclerView;
    private FragmentFacebookAdapter ffAdapter;
    private SwipeRefreshLayout aswipeRefreshLayout;
    ArrayList<FacebookFriends> friends = new ArrayList<FacebookFriends>();
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private Userbase u;
    private ArrayList<Integer> userIDS = new ArrayList<Integer>();
    private ArrayList<Integer> mediaIDS = new ArrayList<>();
    String ID,NAME,ACTION,ALBUM_NAME,SHARED;



    public FragmentFacebook() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager=CallbackManager.Factory.create();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vq = inflater.inflate(R.layout.facebook_fragment, container, false);

        ACTION = getArguments().getString("action");

        if(ACTION.equals("create_shared_album"))
        {
            mediaIDS = getArguments().getIntegerArrayList("mediaId");
            ALBUM_NAME = getArguments().getString("album_name");
        }
        if(ACTION.equals("to_others"))
        {
            mediaIDS = getArguments().getIntegerArrayList("mediaId");
            ID = getArguments().getString("Id");
            NAME = getArguments().getString("Name");
            SHARED = getArguments().getString("shared");
        }
        if(ACTION.equals("add_user")){
            ID = getArguments().getString("Id");
            NAME = getArguments().getString("Name");
            SHARED = getArguments().getString("shared");
        }

        recyclerView = (RecyclerView) vq.findViewById(R.id.facebook_friends);
        ffAdapter = new FragmentFacebookAdapter(friends,this);
        recyclerView.setHasFixedSize(true);
        aswipeRefreshLayout = (SwipeRefreshLayout)vq.findViewById(R.id.facebook_refresh_layout);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(ffAdapter);
        loginButton= (LoginButton)vq.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
       // loginButton.registerCallback(getcall());
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               loginButton.registerCallback(callbackManager,mcallback);
            }
        });

        aswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        return vq;
    }


    @Override
    public void onItemClicked(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }

    @Override
    public boolean onItemLongClicked(int position) {


        toggleSelection(position);

        return true;
    }



    private void toggleSelection(int position) {
        ffAdapter.toggleSelection(position);
        int count = ffAdapter.getSelectedItemCount();
        List<Integer> cnt = ffAdapter.getSelectedItems();

        userIDS.clear();
        for(int i=0;i<cnt.size();i++){
            FacebookFriends ff = friends.get(cnt.get(i));
            userIDS.add(Integer.parseInt(ff.getId()));
        }
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.menu_userbase, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.done_userbase:

                    if(ACTION.equals("create_shared_album")){

                        try {
                            JSONArray a = new JSONArray(userIDS);
                            JSONArray b = new JSONArray(mediaIDS);
                            JSONObject obj = new JSONObject();
                            obj.put("userId", "1");
                            obj.put("mediaId", b);
                            obj.put("sharedUserId", a);
                            obj.put("name",ALBUM_NAME);
                            obj.put("shared","yes");
                            StringEntity jsonString = new StringEntity(obj.toString());


                            AsyncHttpClient client = new AsyncHttpClient();

                            client.post(getContext(), "http://52.89.2.186/project/webservice/public/Createsharedalbumusers", jsonString, "application/json", new AsyncHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    // called before request is started
                                }

                                // @Override
                                public void onSuccess(String response) {
                                    // called when response HTTP status is "200 OK"
                                    try {
                                        Log.i("zzz", response);
                                        JSONObject obj = new JSONObject(response);

                                        if (obj.getBoolean("error")) {
                                            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();

                                            Intent i = new Intent(getContext(), HomeActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            getContext().startActivity(i);

                                        }

                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException ee) {
                            ee.printStackTrace();
                        }


                    }
                        if(ACTION.equals("to_others"))    {

                        try {
                            JSONArray a = new JSONArray(userIDS);
                            JSONArray b = new JSONArray(mediaIDS);
                            JSONObject obj = new JSONObject();
                            obj.put("userId", "1");
                            obj.put("mediaId", b);
                            obj.put("sharedUserId", a);
                            StringEntity jsonString = new StringEntity(obj.toString());


                            AsyncHttpClient client = new AsyncHttpClient();

                            client.post(getContext(), "http://52.89.2.186/project/webservice/public/Sharedmedia", jsonString, "application/json", new AsyncHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    // called before request is started
                                }

                                // @Override
                                public void onSuccess(String response) {
                                    // called when response HTTP status is "200 OK"
                                    try {
                                        Log.i("zzz", response);
                                        JSONObject obj = new JSONObject(response);

                                        if (obj.getBoolean("error")) {
                                            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();

                                            if(SHARED.equals("no")) {
                                                Intent i = new Intent(getContext(), AlbumMediaDisplay.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                i.putExtra("Id", ID);
                                                i.putExtra("Name", NAME);
                                                getContext().startActivity(i);
                                            }else{
                                                Intent i = new Intent(getContext(), SharedAlbumMediaDisplay.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                i.putExtra("Id", ID);
                                                i.putExtra("Name", NAME);
                                                getContext().startActivity(i);
                                            }

                                        }

                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException ee) {
                            ee.printStackTrace();
                        }



                    }

                    if(ACTION.equals("add_user")){

                        try {
                            JSONArray a = new JSONArray(userIDS);
                            JSONObject obj = new JSONObject();
                            obj.put("userId", "1");
                            obj.put("sharedUserId", a);
                            obj.put("albumId",ID);
                            StringEntity jsonString = new StringEntity(obj.toString());


                            AsyncHttpClient client = new AsyncHttpClient();

                            client.post(getContext(), "http://52.89.2.186/project/webservice/public/Addusersharedalbum", jsonString, "application/json", new AsyncHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    // called before request is started
                                }

                                // @Override
                                public void onSuccess(String response) {
                                    // called when response HTTP status is "200 OK"
                                    try {
                                        Log.i("zzz", response);
                                        JSONObject obj = new JSONObject(response);

                                        if (obj.getBoolean("error")) {
                                            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();


                                                Intent i = new Intent(getContext(), SharedAlbumMediaDisplay.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                i.putExtra("Id", ID);
                                                i.putExtra("Name", NAME);
                                                getContext().startActivity(i);


                                        }

                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException ee) {
                            ee.printStackTrace();
                        }

                    }

                    return true;
                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            ffAdapter.clearSelection();
            actionMode = null;
        }
    }






    // private FacebookCallback<LoginResult> getcall() {
         private FacebookCallback<LoginResult> mcallback = new FacebookCallback<LoginResult>() {

                @Override
                public void onSuccess(LoginResult login_result) {
                    GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                            login_result.getAccessToken(),
                            //AccessToken.getCurrentAccessToken(),
                            "/me/friends",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    try {
                                        JSONArray rawName = response.getJSONObject().getJSONArray("data");

                                        Log.i("qqq", rawName.toString());

                                        for (int i = 0; i < rawName.length(); i++) {
                                            JSONObject obj = new JSONObject();
                                            obj.put("facebookId", rawName.getJSONObject(i).getString("id"));
                                            StringEntity jsonString = new StringEntity(obj.toString());

                                            AsyncHttpClient client = new AsyncHttpClient();

                                            client.post(getContext(), "http://52.89.2.186/project/webservice/public/Getfacebookuser", jsonString, "application/json", new AsyncHttpResponseHandler() {

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
                                                            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                                                        } else {
                                                            JSONObject userObj = obj.getJSONObject("outputObj");
                                                            JSONObject userDetails = userObj.getJSONObject("user");

                                                            FacebookFriends ff = new FacebookFriends(userDetails.optString("id"), userDetails.optString("name"), userDetails.optString("emailId"),
                                                                    userDetails.optString("mobileNo"), userDetails.optString("displayPicture"),
                                                                    userDetails.optString("facebookId"), false);

                                                            friends.add(ff);
                                                            ffAdapter.notifyDataSetChanged();

                                                        }

                                                    } catch (JSONException e) {
                                                        // TODO Auto-generated catch block
                                                        Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
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

                                        }

                                        aswipeRefreshLayout.setRefreshing(false);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (UnsupportedEncodingException ee) {
                                        ee.printStackTrace();
                                    }
                                }
                            }
                    ).executeAsync();

                }

                @Override
                public void onCancel() {
                    // code for cancellation
                }

                @Override
                public void onError(FacebookException exception) {
                    //  code to handle error
                }
            };

      //  }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}