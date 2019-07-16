package com.example.sanketiclipi;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class ImageRecognitionActivity extends AppCompatActivity {

    ImageView imageView;
    private static int GALLERY=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recognition);

        imageView = (ImageView) findViewById(R.id.iv);

        Uri selectedImgUri = getIntent().getData();

        if (selectedImgUri != null) {
            Log.e("Gallery ImageURI", "" + selectedImgUri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImgUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /* Getting ImageBitmap from Camera from Main Activity */
        Intent intent_camera = getIntent();
        Bitmap camera_img_bitmap = (Bitmap) intent_camera
                .getParcelableExtra("BitmapImage");
        if (camera_img_bitmap != null) {
            imageView.setImageBitmap(camera_img_bitmap);
        }
    }

}



