package com.example.projectchat;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

public class FullScreenVideo extends MediaController {
    private String isFullScreen;
    public FullScreenVideo(Context context) {
        super(context);
    }

    @Override
    public void setAnchorView(View view){
        super.setAnchorView(view);
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity= Gravity.LEFT;
        params.rightMargin=80;
        addView(isFullScreen, params);


    }

    private void addView(String isFullScreen, LayoutParams params) {

    }
}
