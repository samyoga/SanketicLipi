package com.example.sanketiclipi;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelUuid;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class MainActivity extends AppCompatActivity {

    TextToSpeech textToSpeech;
    Button mbutton;
    TextView WelcomeMessage, WcTxtNepali;
    private static int GALLERY=1;
    RequestManager requestManager;
    Uri mSelectedImagesUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mbutton = (Button) findViewById(R.id.button);
        WelcomeMessage = (TextView) findViewById(R.id.WelcomeText);
        WcTxtNepali = (TextView) findViewById(R.id.WctxtNepali);

        requestManager = Glide.with(MainActivity.this);


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.forLanguageTag("hin"));

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak(WcTxtNepali.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 800);
        startBottomPicker();


    }

    @AfterPermissionGranted(1)
    public void startBottomPicker() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getApplicationContext(), perms)) {
            mbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TedBottomPicker.with(MainActivity.this)
                            .setSelectedUri(mSelectedImagesUri)
                            //.showVideoMedia()
                            .setPeekHeight(1200)
                            .setCompleteButtonText("DONE")
                            .setEmptySelectionText("No photos have been selected.")
                            .show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
                                @Override
                                public void onImageSelected(Uri uri) {
                                    mSelectedImagesUri = uri;

                                    Intent intent = new Intent(MainActivity.this, ImageClassificationAcitivity.class);
                                    intent.setData(mSelectedImagesUri);
                                    startActivity(intent);

                                }
                            });


                }
            });


        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.photo_permit),
                    1, perms);
        }
    }

//    @Override
//    public void onStart() {
//
//        super.onStart();
//        textToSpeech.speak(WcTxtNepali.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
//    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

}
