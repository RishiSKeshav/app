package com.rishi.app.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.TextAttribute;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by amitrajula on 2/13/16.
 */
public class EditProfile extends AppCompatActivity {
    SessionManager sessionManager;
    TextView nameTV,mobilenoTV;
    final int PICK_FROM_CAPTURE = 0;
    final int PICK_FROM_FILE = 1;
    final int PIC_CROP = 2;
    private Uri mImageCaptureUri;
    private Bitmap bitmap;
    RequestParams params = new RequestParams();
    String encodedString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        Toolbar toolbar= (Toolbar) findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());

        CircleImageView imageView = (CircleImageView) findViewById(R.id.edit_profile_image);

        Picasso.with(getApplicationContext()).load(sessionManager.getDisplayPicture())
                .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
                .into(imageView);



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(EditProfile.this, v);
                popup.getMenuInflater().inflate(R.menu.menu_display_picture, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().equals("Take Photo")) {
                            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            //we will handle the returned data in onActivityResult
                            startActivityForResult(captureIntent, PICK_FROM_CAPTURE);
                        } else {
                            Intent intent = new Intent();
//                            intent.setType("image/*");
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);

                            if (Build.VERSION.SDK_INT < 19) {
                                intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("*/*");
                                startActivityForResult(Intent.createChooser(intent,
                                        "Complete action using"), PICK_FROM_FILE);
                            } else {
                                intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("*/*");
                                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                                        mImageCaptureUri);
                                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                            }
                        }

                        return true;
                    }
                });

                popup.show();
            }
        });

        nameTV = (TextView)findViewById(R.id.edit_name);
        nameTV.setText(sessionManager.getName());

        mobilenoTV = (TextView)findViewById(R.id.edit_mobileno);
        mobilenoTV.setText(sessionManager.getMobileNo());

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {
            if (requestCode == PICK_FROM_FILE) {
                if (Build.VERSION.SDK_INT < 19) {
                    mImageCaptureUri = data.getData();

                } else {
                    mImageCaptureUri = data.getData();
                    ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        parcelFileDescriptor = getContentResolver()
                                .openFileDescriptor(mImageCaptureUri, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor
                                .getFileDescriptor();
                        bitmap = BitmapFactory
                                .decodeFileDescriptor(fileDescriptor);
                        parcelFileDescriptor.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                performCrop();

            }
        }
        if(requestCode == PICK_FROM_CAPTURE){
            mImageCaptureUri = data.getData();
          //  picturePath = getRealPathFromURI(mImageCaptureUri);
            performCrop();
        }
        if(requestCode == PIC_CROP){
            Bundle extras = data.getExtras();
            if(extras != null) {
                bitmap = extras.getParcelable("data");
                encodeImagetoString();

                //  Log.i("fff",picturePath.toString());
//                File f  = new File(mImageCaptureUri.);
//                f.delete();
            }
        }

    }

    private void performCrop(){

        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(mImageCaptureUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);

        }
        catch(ActivityNotFoundException anfe){
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {
                //BitmapFactory.Options options = null;
                //options = new BitmapFactory.Options();
                //options.inSampleSize = 3;
                //  bitmap = BitmapFactory.decodeFile(imgPath,
                //        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                // prgDialog.setMessage("Calling Upload");
                // Put converted Image string into Async Http Post param
                params.put("image", encodedString);
                params.put("userId", sessionManager.getId());
                params.put("filename",System.currentTimeMillis() + ".jpg");
                triggerImageUpload();
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }


    public void makeHTTPCall() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://52.89.2.186/project/webservice/displayPicture.php",
                params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            //Log.i("response", response);
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                            } else {
                                sessionManager.changedisplayPicture(obj.getString("displayPicture"));
                                Intent i = new Intent(EditProfile.this, EditProfile.class);
                                EditProfile.this.startActivity(i);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }

                    public void onFailure(int statusCode, Throwable error,
                                          String content) {

                        //prgDialog.hide();
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error Occured n Most Common Error: n1. Device not connected to Internetn2. Web App is not deployed in App servern3. App server is not runningn HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_profile, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.edit_profile_save) {

            try {
                JSONObject obj = new JSONObject();
                obj.put("action","edit_profile");
                obj.put("name", nameTV.getText().toString());
                obj.put("mobileNo", mobilenoTV.getText().toString());
                obj.put("userId",sessionManager.getId());
                StringEntity jsonString = new StringEntity(obj.toString());


                AsyncHttpClient client = new AsyncHttpClient();

                client.post(getApplicationContext(), "http://52.89.2.186/project/webservice/public/Editprofile", jsonString, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                    }

                    // @Override
                    public void onSuccess(String response) {
                        // called when response HTTP status is "200 OK"
                        try {
                            //Log.i("sss", response);
                            JSONObject obj = new JSONObject(response);

                            if (obj.getBoolean("error")) {
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(EditProfile.this)
                                                .text("something went wrong")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                                );
                            } else {

                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(getApplicationContext())
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

                                                        sessionManager.changeNameMobileNo(nameTV.getText().toString(),mobilenoTV.getText().toString());
                                                        Intent i = new Intent(EditProfile.this, HomeSettings.class);
                                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        EditProfile.this.startActivity(i);



                                                    }

                                                    @Override
                                                    public void onDismissByReplace(com.nispok.snackbar.Snackbar snackbar) {

                                                    }

                                                    @Override
                                                    public void onDismissed(com.nispok.snackbar.Snackbar snackbar) {

                                                    }
                                                })
                                        , EditProfile.this);

                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
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



        return super.onOptionsItemSelected(item);
    }


}
