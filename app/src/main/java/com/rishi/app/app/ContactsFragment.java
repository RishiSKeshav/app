package com.rishi.app.app;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;

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
public class ContactsFragment extends Fragment implements ContactsFragmentAdapter.MyViewHolder.ClickListener{

    public TextView outputText;
    Context context;
    ArrayList<String> numbers  = new ArrayList<>();
    ArrayList<ContactsFriends> contactsfriends = new ArrayList<>();
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private RecyclerView recyclerView;
    private ContactsFragmentAdapter cfAdapter;
    String ID,NAME,ACTION,ALBUM_NAME,SHARED,imagedisplay,position;
    private ArrayList<Integer> mediaIDS = new ArrayList<>();
    private ArrayList<Integer> userIDS = new ArrayList<Integer>();
    private ArrayList<String> data = new ArrayList<String>();

    SessionManager sessionManager;
    String phoneNumber;


    public ContactsFragment() {

        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sessionManager = new SessionManager(getContext());

        View vq = inflater.inflate(R.layout.contacts_fragment, container, false);



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
        if(ACTION.equals("shared_media")){
            mediaIDS = getArguments().getIntegerArrayList("mediaId");
            ID = getArguments().getString("Id");
            imagedisplay = getArguments().getString("imagedisplay");
        }
        if(ACTION.equals("sync")||ACTION.equals("camera")){
            mediaIDS = getArguments().getIntegerArrayList("mediaId");
            data = getArguments().getStringArrayList("data");
            position = getArguments().getString("position");
        }

        recyclerView = (RecyclerView) vq.findViewById(R.id.contacts_friends);
        cfAdapter = new ContactsFragmentAdapter(contactsfriends,this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(cfAdapter);

        fetchContacts();
      return vq;


    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        cfAdapter.toggleSelection(position);
        int count = cfAdapter.getSelectedItemCount();
        List<Integer> cnt = cfAdapter.getSelectedItems();

        userIDS.clear();
        for(int i=0;i<cnt.size();i++){
            ContactsFriends cf = contactsfriends.get(cnt.get(i));
            userIDS.add(Integer.parseInt(cf.getId()));
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
                            obj.put("userId", sessionManager.getId());
                            obj.put("userName",sessionManager.getName());
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
                                    //    Log.i("zzz", response);
                                        JSONObject obj = new JSONObject(response);

                                        if (obj.getBoolean("error")) {
                                            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                                        } else {
                                            SnackbarManager.show(
                                                    com.nispok.snackbar.Snackbar.with(getActivity())
                                                            .text(obj.getString("msg"))
                                                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                                                            .eventListener(new EventListener() {
                                                                @Override
                                                                public void onShow(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onShowByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onShown(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onDismiss(com.nispok.snackbar.Snackbar snackbar) {

                                                                    Intent i = new Intent(getContext(), HomeActivity.class);
                                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    getContext().startActivity(i);


                                                                }

                                                                @Override
                                                                public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                                                }
                                                            })
                                                    , getActivity());

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
                            obj.put("userId", sessionManager.getId());
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
                                      //  Log.i("zzz", response);
                                        JSONObject obj = new JSONObject(response);

                                        if (obj.getBoolean("error")) {
                                            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                                        } else {
                                            SnackbarManager.show(
                                                    com.nispok.snackbar.Snackbar.with(getActivity())
                                                            .text(obj.getString("msg"))
                                                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                                                            .eventListener(new EventListener() {
                                                                @Override
                                                                public void onShow(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onShowByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onShown(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onDismiss(com.nispok.snackbar.Snackbar snackbar) {

                                                                    if (SHARED.equals("no")) {
                                                                        Intent i = new Intent(getContext(), AlbumMediaDisplay.class);
                                                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        i.putExtra("Id", ID);
                                                                        i.putExtra("Name", NAME);
                                                                        getContext().startActivity(i);
                                                                    } else {
                                                                        Intent i = new Intent(getContext(), SharedAlbumMediaDisplay.class);
                                                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        i.putExtra("Id", ID);
                                                                        i.putExtra("Name", NAME);
                                                                        getContext().startActivity(i);
                                                                    }


                                                                }

                                                                @Override
                                                                public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                                                }
                                                            })
                                                    , getActivity());
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
                            obj.put("userId", sessionManager.getId());
                            obj.put("name",NAME);
                            obj.put("sharedUserId", a);
                            obj.put("albumId",ID);
                            obj.put("userName",sessionManager.getName());
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
                                     //   Log.i("zzz", response);
                                        JSONObject obj = new JSONObject(response);

                                        if (obj.getBoolean("error")) {
                                            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                                        } else {
                                            SnackbarManager.show(
                                                    com.nispok.snackbar.Snackbar.with(getActivity())
                                                            .text(obj.getString("msg"))
                                                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                                                            .eventListener(new EventListener() {
                                                                @Override
                                                                public void onShow(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onShowByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onShown(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onDismiss(com.nispok.snackbar.Snackbar snackbar) {

                                                                    Intent i = new Intent(getContext(), SharedAlbumMediaDisplay.class);
                                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    i.putExtra("Id", ID);
                                                                    i.putExtra("Name", NAME);
                                                                    getContext().startActivity(i);


                                                                }

                                                                @Override
                                                                public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                                                }
                                                            })
                                                    , getActivity());


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

                    if(ACTION.equals("shared_media") || ACTION.equals("sync") || ACTION.equals("camera")){

                        try {
                            JSONArray a = new JSONArray(userIDS);
                            JSONArray b = new JSONArray(mediaIDS);
                            JSONObject obj = new JSONObject();
                            obj.put("userId", sessionManager.getId());
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
                                      //  Log.i("zzz", response);
                                        JSONObject obj = new JSONObject(response);

                                        if (obj.getBoolean("error")) {
                                            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                                        } else {
                                            SnackbarManager.show(
                                                    com.nispok.snackbar.Snackbar.with(getActivity())
                                                            .text(obj.getString("msg"))
                                                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                                                            .eventListener(new EventListener() {
                                                                @Override
                                                                public void onShow(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onShowByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onShown(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onDismiss(com.nispok.snackbar.Snackbar snackbar) {

                                                                    if(ACTION.equals("sync") || ACTION.equals("camera")){
                                                                        Intent i = new Intent(getContext(),Userbase.class);
                                                                        i.putExtra("action",ACTION);
                                                                        i.putStringArrayListExtra("data",data);
                                                                        i.putExtra("Position",position);
                                                                        getContext().startActivity(i);

                                                                    }else {

                                                                        Intent i = new Intent(getContext(), SharedMediaDisplay.class);
                                                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        i.putExtra("Id", ID);
                                                                        i.putExtra("image", imagedisplay);
                                                                        getContext().startActivity(i);
                                                                    }


                                                                }

                                                                @Override
                                                                public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                                }

                                                                @Override
                                                                public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                                                }
                                                            })
                                                    , getActivity());


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
            cfAdapter.clearSelection();
            actionMode = null;
        }
    }






    public void fetchContacts() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String no = phoneNumber.replaceAll("\\p{P}","");
            String no1= no.replaceAll("\\s+","");

            if(no1.length() > 10){
               String no2 = no1.substring(no1.length() - 10);
                if(!numbers.contains(no2)){
                    numbers.add(no2);
                }
            }else{
                if(!numbers.contains(no1)) {
                    numbers.add(no1);
                }
            }

        }

        prepareContactsData();

    }

    private void prepareContactsData(){

        try {
                            JSONArray a = new JSONArray(numbers);
                            JSONObject obj = new JSONObject();
                            obj.put("numbers", a);
                            StringEntity jsonString = new StringEntity(obj.toString());

                            AsyncHttpClient client = new AsyncHttpClient();

                            client.post(getContext(), "http://52.89.2.186/project/webservice/public/Getcontactsuser", jsonString, "application/json", new AsyncHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    // called before request is started
                                }

                                // @Override
                                public void onSuccess(String response) {

                                  //  Log.i("response",response.toString());
                                   // Log.i("bnnm",response);
                                    // called when response HTTP status is "200 OK"
                                    try {
                                        JSONObject obj = new JSONObject(response);



                                        if (obj.getBoolean("error")) {
                                            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                                        } else {
                                            JSONObject numberObj = obj.getJSONObject("outputObj");
                                            if(numberObj.getJSONArray("user").length() > 0){
                                                JSONArray userarray = numberObj.optJSONArray("user");

                                                for (int i = 0; i < userarray.length(); i++) {

                                                    JSONObject userdetails = userarray.optJSONObject(i);


                                                    ContactsFriends cf = new ContactsFriends(userdetails.optString("id"), userdetails.optString("name"),
                                                            userdetails.optString("emailId"), userdetails.optString("mobileNo"),
                                                            userdetails.optString("mobileNo"), userdetails.optString("displayPicture"),
                                                            userdetails.optString("facebookId"));

                                                    contactsfriends.add(cf);

                                                    cfAdapter.notifyDataSetChanged();


                                                }
                                            }
                                        }
                                    }catch (JSONException e) {
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