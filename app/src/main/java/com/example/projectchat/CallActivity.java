package com.example.projectchat;


import static org.pjsip.pjsua2.pjmedia_event_type.PJMEDIA_EVENT_VID_DEV_ERROR;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.widget.ImageButton;

import org.pjsip.pjsua2.*;

class VideoPreviewHandler implements SurfaceHolder.Callback
{
    public boolean videoPreviewActive = false;

    public void updateVideoPreview(SurfaceHolder holder)
    {
        if (SipPhone.currentCall != null &&
                SipPhone.currentCall.vidWin != null &&
                SipPhone.currentCall.vidPrev != null)
        {
            if (videoPreviewActive) {
                VideoWindowHandle vidWH = new VideoWindowHandle();
                vidWH.getHandle().setWindow(holder.getSurface());
                VideoPreviewOpParam vidPrevParam = new VideoPreviewOpParam();
                vidPrevParam.setWindow(vidWH);
                try {
                    SipPhone.currentCall.vidPrev.start(vidPrevParam);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                try {
                    SipPhone.currentCall.vidPrev.stop();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        updateVideoPreview(holder);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        try {
            SipPhone.currentCall.vidPrev.stop();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

public class CallActivity extends Activity
        implements Handler.Callback, SurfaceHolder.Callback
{
    long videoCount;
    public static Handler handler_;
    ImageButton accept;
    int getExtra;
    private static VideoPreviewHandler previewHandler =
            new VideoPreviewHandler();

    private final Handler handler = new Handler(this);
    private static CallInfo lastCallInfo;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        Intent intent1 = new Intent(CallActivity.this, SipPhone.class);
        startService(intent1);
        try {
            Log.d("callonCreate", ""+SipPhone.currentCall.getInfo().getState());
            CallInfo callInfo=SipPhone.currentCall.getInfo();
            audioManager=(AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            accept=(ImageButton)findViewById(R.id.buttonAccept);
            videoCount = (callInfo.getRemOfferer()) ? callInfo.getRemVideoCount() : callInfo.getSetting().getVideoCount();

            Log.d("VideoCount ", ""+videoCount);
             getExtra=getIntent().getIntExtra("calling", 0);
            Log.d("VideoCountgetExtra ", ""+getExtra);

        } catch (Exception e) {
            e.printStackTrace();
        }
        handler_ = handler;
        if (SipPhone.currentCall != null) {
            try {
                lastCallInfo =SipPhone.currentCall.getInfo();
                updateCallState(lastCallInfo);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            updateCallState(lastCallInfo);
        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        WindowManager wm;
        Display display;
        int rotation;
        int orient;

        wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        rotation = display.getRotation();
        System.out.println("Device orientation changed: " + rotation);

        switch (rotation) {
            case Surface.ROTATION_0:   // Portrait
                orient = pjmedia_orient.PJMEDIA_ORIENT_ROTATE_270DEG;
                break;
            case Surface.ROTATION_90:  // Landscape, home button on the right
                orient = pjmedia_orient.PJMEDIA_ORIENT_NATURAL;
                break;
            case Surface.ROTATION_180:
                orient = pjmedia_orient.PJMEDIA_ORIENT_ROTATE_90DEG;
                break;
            case Surface.ROTATION_270: // Landscape, home button on the left
                orient = pjmedia_orient.PJMEDIA_ORIENT_ROTATE_180DEG;
                break;
            default:
                orient = pjmedia_orient.PJMEDIA_ORIENT_UNKNOWN;
        }

        if (MyApp.ep != null && SipPhone.account != null) {
            String codecId = "H264";

            VidCodecParam param = new VidCodecParam();
// Sending 1280 x 720.
            param.getEncFmt().setWidth(1280);
            param.getEncFmt().setHeight(720);

// Sending @30fps.
            param.getEncFmt().setFpsNum(30);
            param.getEncFmt().setFpsDenum(1);

// Bitrate range preferred: 512-2048kbps.
            param.getEncFmt().setAvgBps(512000);
            param.getEncFmt().setMaxBps(2048000);

// Buffer size for decode frame
            param.getDecFmt().setWidth(2048);
            param.getDecFmt().setHeight(1080);

            try {
                MyApp.ep.setVideoCodecParam(codecId, param);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                AccountConfig cfg = SipPhone.account.cfg;
                int cap_dev = cfg.getVideoConfig().getDefaultCaptureDevice();
                MyApp.ep.vidDevManager().setCaptureOrient(cap_dev, orient,
                        true);

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        handler_ = null;
    }

    private void updateVideoWindow(boolean show)
    {

        if (SipPhone.currentCall != null &&
                SipPhone.currentCall.vidWin != null &&
                SipPhone.currentCall.vidPrev != null)
        {
            SurfaceView surfaceInVideo = (SurfaceView)
                    findViewById(R.id.surfaceIncomingVideo);

            VideoWindowHandle vidWH = new VideoWindowHandle();


            if (show) {
                vidWH.getHandle().setWindow(
                        surfaceInVideo.getHolder().getSurface());
            } else {
                vidWH.getHandle().setWindow(null);
            }
            try {

                SipPhone.currentCall.vidWin.setWindow(vidWH);
            } catch (Exception e) {
                System.out.println(e);
            }
        }

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        Log.d("SurfaceChanged ", ""+format);
        Log.d("SurfaceChanged ", ""+holder.isCreating());
       // updateVideoWindow(true);
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d("SurfaceCreated ", ""+holder.getSurface().toString());
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        updateVideoWindow(false);
    }

    public void acceptCall(View view)
    {
        CallOpParam prm = new CallOpParam();
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
        try {
            SipPhone.currentCall.answer(prm);
        } catch (Exception e) {
            System.out.println(e);
        }
if (videoCount == 1) {
    updateVideoWindow(true);
    view.setVisibility(View.GONE);
}else {
    updateVideoWindow(false);
    view.setVisibility(View.GONE);
}
    }

    public void hangupCall(View view)
    {
        handler_ = null;
        finish();

        if (SipPhone.currentCall != null) {
            CallOpParam prm = new CallOpParam();
            prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
            try {
                SipPhone.currentCall.hangup(prm);
                SipPhone.currentCall=null;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void setupVideoSurface()
    {
        SurfaceView surfaceInVideo = (SurfaceView)
                findViewById(R.id.surfaceIncomingVideo);
        SurfaceView surfacePreview = (SurfaceView)
                findViewById(R.id.surfacePreviewCapture);

        surfaceInVideo.setVisibility(View.VISIBLE);
        surfacePreview.setVisibility(View.GONE);
    }

    @Override
    public boolean handleMessage(Message m)
    {

        if (m.what == SipPhone.MSG_TYPE.CALL_STATE) {

            lastCallInfo = (CallInfo) m.obj;
            updateCallState(lastCallInfo);

        } else if (m.what == SipPhone.MSG_TYPE.CALL_MEDIA_STATE) {

            if (SipPhone.currentCall.vidWin != null) {
                /* Set capture orientation according to current
                 * device orientation.
                 */
                onConfigurationChanged(getResources().getConfiguration());
                /* If there's incoming video, display it. */
                setupVideoSurface();
            }

        } else {

            /* Message not handled */
            return false;

        }

        return true;
    }

    private void updateCallState(CallInfo ci)  {

        String call_state = "";
Log.d("callstate", ""+ci.getState()+"/"+ci.getStateText());
Log.d("callstate", ""+pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED);



        if (ci == null) {

            return;
        }

        if (ci.getState() >=
                pjsip_inv_state.PJSIP_INV_STATE_CALLING)
        {

            Log.d("calling", ""+pjsip_inv_state.PJSIP_INV_STATE_CALLING);
            Log.d("calling", ""+getExtra);

            if ( videoCount==0) {
                SurfaceView surfacePreview = (SurfaceView)
                        findViewById(R.id.surfacePreviewCapture);
                surfacePreview.setVisibility(View.INVISIBLE);
                SurfaceView surfaceInVideo = (SurfaceView)
                        findViewById(R.id.surfaceIncomingVideo);
                surfaceInVideo.setVisibility(View.INVISIBLE);
            } else {
                SurfaceView surfacePreview = (SurfaceView)
                        findViewById(R.id.surfacePreviewCapture);
                surfacePreview.setVisibility(View.VISIBLE);
                SurfaceView surfaceInVideo = (SurfaceView)
                        findViewById(R.id.surfaceIncomingVideo);
                surfaceInVideo.setVisibility(View.VISIBLE);
            }
        }


        if (ci.getRole() == pjsip_role_e.PJSIP_ROLE_UAC) {
            accept.setVisibility(View.GONE);
        }


        if (ci.getState() <
                pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED)
        {
            if (ci.getRole() == pjsip_role_e.PJSIP_ROLE_UAS) {
                call_state = "Incoming call..";
                /* Default button texts are already 'Accept' & 'Reject' */
            } else {

                call_state = ci.getStateText();
            }
        }
        else if (ci.getState() >=
                pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED)
        {

            call_state = ci.getStateText();
            if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                SurfaceView surfacePreview = (SurfaceView)
                        findViewById(R.id.surfacePreviewCapture);
                previewHandler.videoPreviewActive = !previewHandler.videoPreviewActive;
                previewHandler.updateVideoPreview(surfacePreview.getHolder());

                if (videoCount==1) {

                    updateVideoWindow(true);

                    audioManager.setSpeakerphoneOn(true);
                }else{
                    updateVideoWindow(false);
                    audioManager.setSpeakerphoneOn(false);
                }
            } else if (ci.getState() ==
                    pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)
            {
                handler_ = null;
                finish();

                if (SipPhone.currentCall != null) {

                    audioManager.setSpeakerphoneOn(false);
                    CallOpParam prm = new CallOpParam();
                    prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
                    try {
                        SipPhone.currentCall.hangup(prm);
                        SipPhone.currentCall=null;
                        updateVideoWindow(false);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                call_state = "Call disconnected: " + ci.getLastReason();
            }
        }


    }
}

