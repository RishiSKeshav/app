package com.rishi.app.app;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by amitrajula on 2/9/16.
 */
public class ContactsFragment extends Fragment{

    public TextView outputText;
    Context context;
    ArrayList<ContactsFriends> contactsfriends = new ArrayList<>();

    private RecyclerView recyclerView;
    private ContactsFragmentAdapter cfAdapter;


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

        View vq = inflater.inflate(R.layout.contacts_fragment, container, false);

        recyclerView = (RecyclerView) vq.findViewById(R.id.contacts_friends);
        cfAdapter = new ContactsFragmentAdapter(contactsfriends);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(cfAdapter);

        fetchContacts();
      return vq;


    }




    public void fetchContacts() {

        String phoneNumber = null;
        String email = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
        final StringBuffer output = new StringBuffer();
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                if (hasPhoneNumber > 0) {

                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        Log.i("ggg",phoneNumber);
                        String no = phoneNumber.replaceAll("\\p{P}","");
                        String no1= no.replaceAll("\\s+","");

                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("mobileNo", no1);
                            StringEntity jsonString = new StringEntity(obj.toString());

                            AsyncHttpClient client = new AsyncHttpClient();

                            client.post(getContext(), "http://52.89.2.186/project/webservice/public/Getcontactsuser", jsonString, "application/json", new AsyncHttpResponseHandler() {

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
                                            if(userDetails.optString("id").equals("null")) {

                                            }else{

                                                ContactsFriends cf = new ContactsFriends(userDetails.optString("id"), userDetails.optString("name"),
                                                        userDetails.optString("emailId"),
                                                        userDetails.optString("mobileNo"), userDetails.optString("displayPicture"),
                                                        userDetails.optString("facebookId"));

                                                contactsfriends.add(cf);

                                                cfAdapter.notifyDataSetChanged();
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

                    phoneCursor.close();

                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);

                    while (emailCursor.moveToNext()) {

                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));

                        output.append("\nEmail:" + email);

                    }

                    emailCursor.close();
                }

            }
        }
    }


}