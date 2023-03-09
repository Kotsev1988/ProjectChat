package com.example.projectchat.loadAvatar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectchat.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageAvatarActivity extends AppCompatActivity {
    ImageView activityImage;
    TextView textView;

    String avatar;
    Target target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageavatar);
        activityImage = (ImageView) findViewById(R.id.activityAvatarImage);
        textView = (TextView) findViewById(R.id.nopicture);
        Intent intent = getIntent();

        avatar=intent.getStringExtra("avatar");
        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d("BitmapAvatar", "Avatar" + bitmap.getByteCount());
                activityImage.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                if (!e.toString().isEmpty()) {
                    textView.setText("No Picture");
                    textView.setVisibility(View.VISIBLE);
                    Log.d("NoPhoto", "NoPhoto");

                }

                Log.d("Exception", "errorDrawable" + e.toString());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.get().load(avatar)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                //.into(activityImage);
                .into(target);
    }


}
