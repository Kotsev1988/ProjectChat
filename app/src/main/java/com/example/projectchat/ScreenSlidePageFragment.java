package com.example.projectchat;



import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class ScreenSlidePageFragment extends Fragment {
    VideoView videoView;
    MediaController mediaController;
    ImageButton playbutton;

    public static  ScreenSlidePageFragment newInstance(String path){
        ScreenSlidePageFragment screenSlidePageFragment=new ScreenSlidePageFragment();

        Log.d("pathVideobundle ", ""+path);
        Bundle bundle=new Bundle();
        bundle.putString("pathVideo", path);
        screenSlidePageFragment.setArguments(bundle);
        return screenSlidePageFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        videoView=(VideoView) rootView.findViewById(R.id.videofull1);
        playbutton=(ImageButton) rootView.findViewById(R.id.playButton);

        getSupportActionBar().hide();
        mediaController=new MediaController(getActivity()){
    @Override
    public void show(){
        super.show();
        if (getSupportActionBar()!=null) {
            getSupportActionBar().show();
        }
    }
    @Override
    public void hide(){
        super.hide();
        if (getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }
    }
};
        videoView.setMediaController(mediaController);
 videoView.setOnTouchListener(new View.OnTouchListener() {
     @Override
     public boolean onTouch(View view, MotionEvent motionEvent) {
         //getSupportActionBar().hide();
         return false;
     }
 });
        return rootView;
        }

    private ActionBar getSupportActionBar() {
        ActionBar actionBar = null;
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            actionBar = activity.getSupportActionBar();

        }
        return actionBar;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){


    }
@Override
public void onPrepareOptionsMenu(@NonNull Menu menu){
        super.onPrepareOptionsMenu(menu);

}

    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


           // mediaController.setAnchorView(videoView);

      Log.d("getArgs", getArguments().getString("pathVideo"));
      String path=getArguments().getString("pathVideo");
            Uri uri= Uri.parse(path);
            videoView.setVideoURI(uri);
           // videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                videoView.setVideoURI(uri);
                playbutton.setVisibility(View.VISIBLE);
            }
        });


            playbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    videoView.start();
                }
            });

            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                    if (i==MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                        playbutton.setVisibility(View.GONE);
                        return true;
                    } else if (i==MediaPlayer.MEDIA_INFO_VIDEO_NOT_PLAYING){
                        playbutton.setVisibility(View.VISIBLE);
                        return true;
                    }
                    return false;
                }
            });

    }


}
