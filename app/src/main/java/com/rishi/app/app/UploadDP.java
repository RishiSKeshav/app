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
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadDP extends AppCompatActivity {

    Button take_photo,choose_gallery;
    final int PICK_FROM_CAPTURE = 0;
    final int PICK_FROM_FILE = 1;
    final int PIC_CROP = 2;
    private Uri mImageCaptureUri;
    private Bitmap bitmap;
    private String imagepath=null;
    String encodedString;
    RequestParams params = new RequestParams();
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploaddp);
        sessionManager = new SessionManager(getApplicationContext());

        take_photo = (Button) findViewById(R.id.take_photo);
        choose_gallery = (Button) findViewById(R.id.choose_gallery);

        CircleImageView imageView = (CircleImageView) findViewById(R.id.diplayPicture);

        Picasso.with(getApplicationContext()).load(sessionManager.getDisplayPicture())
                .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
                .into(imageView);

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(captureIntent, PICK_FROM_CAPTURE);
            }
        });

        choose_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
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
                    startActivityForResult(Intent.createChooser(intent,"Complete action using"), PICK_FROM_FILE);
                }

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_FROM_FILE){
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
        if(requestCode == PICK_FROM_CAPTURE){
            mImageCaptureUri = data.getData();
            performCrop();
        }
        if(requestCode == PIC_CROP){
            Bundle extras = data.getExtras();
            if(extras != null) {
                bitmap = extras.getParcelable("data");
                encodeImagetoString();
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
                            Log.i("response", response);
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                            } else {
                                sessionManager.changedisplayPicture(obj.getString("displayPicture"));
                                Intent i = new Intent(UploadDP.this, UploadDP.class);
                                UploadDP.this.startActivity(i);
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
        inflater.inflate(R.menu.menu_upload_display_picture, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.skip) {

            Intent i = new Intent(this,HomeActivity.class);
            this.startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }





}
