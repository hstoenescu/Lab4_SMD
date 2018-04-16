package com.example.student.mycamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUriExposedException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // in this activity - the photo is captured, then displayed to output

    // permissions for app
    private static final String TAG = "PERM";
    int PERMISSIONS_REQUEST_CODE = 123;
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // step 1)
    // create the image file on sdcard Pictures
    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // use as storage location sdcard Pictures
        // added also in android manifest for android resource
        File path = Environment.getExternalStoragePublicDirectory("/sdcard/Pictures");
        path.mkdirs();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                path      /* directory */
        );

        // Make sure the Pictures directory exists.
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("IMAGE FILE LOCATION", mCurrentPhotoPath.toString());
        return image;
    }

    final int REQUEST_TAKE_PHOTO = 1;
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void dispatchTakePicturesIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // create the file where the picture is saved - we need a name for it
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.d("CREATE_FILE", "Failure when creating the image file location");
                e.printStackTrace();
            }

            // if the picture file is successfully created
            if (photoFile != null) {
                Uri photoURI = null;
                try {
                    photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                } catch (FileUriExposedException fileUriExposedException) {
                    Log.d("FILE ERROR", fileUriExposedException.toString());
                }
                if (photoURI != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // request permissions at the runtime
        if (ContextCompat.checkSelfPermission(this, String.valueOf(PERMISSIONS)) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }

        // step 2)
        // button - if pressed goto camera app
        final Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                // call dispatchTakePicturesIntent method
                dispatchTakePicturesIntent();
            }
        });
    }

    // used to request the permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // your code here
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length >= PERMISSIONS.length) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: Permission " + permissions[i] +
                                " was not granted");
                    }
                }
            } else {
                Log.d(TAG, "onRequestPermissionsResult: not all permissions were granted");
            }
        }
    }

    // setPic() method - moved to DisplayPictureActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // call intent for the second activity
        displayPicture();
    }

    private void displayPicture(){
        Intent intent = new Intent();
        intent.setAction("com.example.student.mycamera.startDisplayPictureActivity");
        intent.putExtra("photoPath", mCurrentPhotoPath);
        startActivity(intent);
    }
}
