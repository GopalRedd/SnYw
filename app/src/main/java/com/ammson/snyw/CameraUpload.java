package com.ammson.snyw;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class CameraUpload extends AppCompatActivity implements View.OnClickListener {

    ImageView imgprview;
    Button camera,upload;
    EditText nameimg;
    ProgressDialog progressDialog ;

    Intent intent ;

    public  static final int RequestPermissionCode  = 1 ;
    public static final int CAMERA_REQUEST=123;

    Bitmap bitmap;
    String nameofimage;


    boolean check = true;


    String ImageNameFieldOnServer = "image_name" ;

    String ImagePathFieldOnServer = "image_path" ;

    String ImageUploadPathOnSever ="http://183.82.98.52:85/SnYW/android_login_api/img_upload_to_server.php" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameraupload);

        imgprview = (ImageView) findViewById(R.id.priview);
        camera = (Button) findViewById(R.id.btncamera);
        nameimg =(EditText)findViewById(R.id.imagname);
        upload=(Button)findViewById(R.id.uploadbtn);

        EnableRuntimePermissionToAccessCamera();


        camera.setOnClickListener(this);
        upload.setOnClickListener(this);


;




    }
// permisson to use camera
    private void EnableRuntimePermissionToAccessCamera() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(CameraUpload.this,
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            Toast.makeText(CameraUpload.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(CameraUpload.this,new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK ) {

            Uri uri = data.getData();

            // Adding captured image in bitmap.
            bitmap = (Bitmap)data.getExtras().get("data");

            // adding captured image in imageview.
            imgprview.setImageBitmap(bitmap);

        }


    }

// buttons listeners

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btncamera:

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, CAMERA_REQUEST);

                break;

            case R.id.uploadbtn:

              nameofimage = nameimg.getText().toString();


            imageuploadtoserver();

                break;

        }







    }




// image upload to server so i am writing this function here and i am calling from uploadbtn case
    private void imageuploadtoserver() {

        nameimg.setText("");
        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        Bitmap bitmap = ((BitmapDrawable) imgprview.getDrawable()).getBitmap();
        byte[] byteArrayVar = byteArrayOutputStream.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class  AsyncTaskUploadClass extends AsyncTask<Void,Void,String>
        {

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(CameraUpload.this,"Image is Uploading","Please Wait",false,false);            }


            @Override
            protected void onPostExecute(String s) {
                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(CameraUpload.this,s,Toast.LENGTH_LONG).show();

                // Setting image as transparent after done uploading.
                imgprview.setImageResource(android.R.color.transparent);

            }

            @Override
            protected String doInBackground(Void... voids) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();


                HashMapParams.put(ImageNameFieldOnServer,nameofimage );

                HashMapParams.put(ImagePathFieldOnServer, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(ImageUploadPathOnSever, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();

        }


    private class ImageProcessClass {
        public String ImageHttpRequest(String imageUploadPathOnSever, HashMap<String, String> hashMapParams) {
            StringBuilder stringBuilder= new StringBuilder();
            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(imageUploadPathOnSever);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(hashMapParams));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {

        switch (requestCode)
        {
            case RequestPermissionCode:
            if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

            }
            else
                {
                Toast.makeText(getApplicationContext(),"permission denied",Toast.LENGTH_LONG).show();
                 }
            break;

        }


    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(CameraUpload.this,MainActivity.class));

    }
}

