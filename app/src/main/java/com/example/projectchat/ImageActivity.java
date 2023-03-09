package com.example.projectchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;

public class ImageActivity extends AppCompatActivity {
    ImageView activityImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        activityImage = (ImageView) findViewById(R.id.activityImage);
        Intent intent = getIntent();
      //  Log.d("getExtra", intent.getStringExtra("imageurl"));
        //Bitmap bitmap = BitmapFactory.decodeFile(intent.getStringExtra("imageurl"));
       // activityImage.setImageBitmap(bitmap);
        Glide.with(this).load(Uri.fromFile(new File(intent.getStringExtra("imageurl")))).into(activityImage);

    }
}
