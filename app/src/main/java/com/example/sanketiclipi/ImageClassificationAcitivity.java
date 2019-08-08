package com.example.sanketiclipi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

public class ImageClassificationAcitivity extends AppCompatActivity {

    // IBM WATSON VISUAL RECOGNITION RELATED
    private final String API_KEY = "Typ13FYgc6jYt4XAiqspPVo68_ZRwp9-OZ9XkyTIUhID";
    ImageView loadImageView, navigateBack;
    TextView textRecognized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recognition);

        loadImageView = (ImageView) findViewById(R.id.iv);
        navigateBack = (ImageView) findViewById(R.id.navigateBack);
        textRecognized = (TextView) findViewById(R.id.textRecognized);

//        final Handler handler = new Handler();
//        final Runnable r = new Runnable() {
//            @Override
//            public void run() {
//
//
//                handler.postDelayed(this, 1000);
//
//            }
//        };
//
//        handler.postDelayed(r, 1000);


//        Intent intent = new Intent(ImageClassificationAcitivity.this, ImageRecognitionActivity.class);
//        intent.putExtra("result", (Parcelable) result);
//        startActivity(intent);

        new GetImage().execute(API_KEY);

    }

    private class GetImage extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(ImageClassificationAcitivity.this, "", "Loading", true,false);

        }

        @Override
        protected String doInBackground(String... strings) {
            Uri selectedImgUri = getIntent().getData();

            //convert uri to imagefile
            File auxFile = new File(selectedImgUri.getPath());

            IamOptions options = new IamOptions.Builder()
                    .apiKey(API_KEY)
                    .build();

            VisualRecognition service = new VisualRecognition("2018-03-19", options);

            InputStream imagesStream = null;
            try {
                imagesStream = new FileInputStream(auxFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                    .imagesFile(imagesStream)
                    .threshold((float) 0.6)
                    .classifierIds(Arrays.asList("SanketicLipixFinalx_2024922322"))
                    .build();

            ClassifiedImages result = service.classify(classifyOptions).execute();
//            Log.d("valueofResult", String.valueOf(result));
            return String.valueOf(result);
        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            textRecognized.setText(result);
        }
    }
}
