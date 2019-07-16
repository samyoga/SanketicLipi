package com.example.sanketiclipi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelUuid;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class MainActivity extends AppCompatActivity {

    TextToSpeech textToSpeech;
    Button mbutton;
    TextView WelcomeMessage;
    private static int GALLERY=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mbutton = (Button) findViewById(R.id.button);
        WelcomeMessage = (TextView) findViewById(R.id.WelcomeText);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.US);

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

        textToSpeech.speak(WelcomeMessage.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialogue();
            }
        });
    }

    private void showPictureDialogue(){
        AlertDialog.Builder pictureDialogue = new AlertDialog.Builder(this);
        pictureDialogue.setTitle("Select Action");
        String[] items = {
                "Select Image From Gallery",
                "Select Image From Camera"
        };

        pictureDialogue.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        chooseImageFromGallery();
                        break;
                    case 1:
                        takeImageFromCamera();
                        break;
                }
            }
        });

        pictureDialogue.show();
    }

    public void chooseImageFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takeImageFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        Intent intent = new Intent(MainActivity.this, ImageRecognitionActivity.class);
//        intent.putExtra("requestCode", requestCode);
//        intent.putExtra("resultCode", resultCode);
//        intent.setData(Uri);
//        startActivity(intent);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                Log.d("IMageUri", String.valueOf(contentURI));
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, ImageRecognitionActivity.class);
                    intent.setData(contentURI);
                    startActivity(intent);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            Intent intent = new Intent(MainActivity.this, ImageRecognitionActivity.class);
            intent.putExtra("BitmapImage", thumbnail);
            startActivity(intent);
            saveImage(thumbnail);
            Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "IMAGE_DIRECTORY");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

}
