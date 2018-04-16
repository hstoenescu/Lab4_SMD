package com.example.student.mycamera;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileFilter;

public class DisplayPictureActivity extends AppCompatActivity {

    String mCurrentPhotoPath;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mImageView = (ImageView) findViewById(R.id.imageView);

        // extract from intent the path to photo - mCurrentPhotoPath used
        Intent intent = getIntent();
        if (intent.hasExtra("photoPath")) {
            mCurrentPhotoPath =intent.getStringExtra("photoPath");
        } else {
            // display last photo when using this activity with the app 2
            mCurrentPhotoPath = lastFilePath();
        }


        // !!! not calling setPic() from here
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mCurrentPhotoPath != null){
            setPic();
        }
    }

    private void setPic() {
        // return from intent - show the picture
        // dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // dimensions of bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    public String lastFilePath() {
        File storageDir = Environment.getExternalStoragePublicDirectory("/sdcard/Pictures");
        File[] files = storageDir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        });
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        if (files != null) {
            for (File file : files) {
                if (file.lastModified() > lastMod) {
                    choice = file;
                    lastMod = file.lastModified();
                }
            }
        }

        if (choice != null) {
            return choice.getAbsolutePath();
        } else {
            Log.d("not-found-tag","NOT FOUND PATH");
            return "";
        }
    }

}
