package com.rishi.app.app;

import android.content.Intent;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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

/**
 * Created by amitrajula on 2/9/16.
 */
public class FragmentFacebook extends Fragment{

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private RecyclerView recyclerView;
    private FragmentFacebookAdapter ffAdapter;


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


        loginButton= (LoginButton)vq.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        getLoginDetails(loginButton);
        return vq;
    }


    protected void getLoginDetails(LoginButton login_button){


        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

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

                                    final ArrayList<FacebookFriends> friends = new ArrayList<FacebookFriends>();

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

                                                        FacebookFriends ff = new FacebookFriends(userDetails.optString("id"),userDetails.optString("firstName"),
                                                                userDetails.optString("lastName"),userDetails.optString("emailId"),
                                                                userDetails.optString("mobileNo"),userDetails.optString("displayPicture"),
                                                                userDetails.optString("facebookId"));

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

                                    recyclerView = (RecyclerView) getView().findViewById(R.id.facebook_friends);
                                    ffAdapter = new FragmentFacebookAdapter(friends);
                                    recyclerView.setHasFixedSize(true);
                                  //  recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setAdapter(ffAdapter);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }catch(UnsupportedEncodingException ee){
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
        });
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}