package com.example.projectchat;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.ImageFormat;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.projectchat.RoomDB.DataBase;
import com.example.projectchat.RoomDB.message_chattest17;
import com.example.projectchat.RoomDB.message_withtest17;
import com.example.projectchat.sentImage.ImageSentExtensions;
import com.example.projectchat.ui.main.PageViewModel;
import com.example.projectchat.ui.main.PlaceholderFragment;
import com.example.projectchat.ui.main.SectionsPagerAdapter;
import com.example.projectchat.ui.main.viewModelContact;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.sm.StreamManagementException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AccountVideoConfig;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSetting;
import org.pjsip.pjsua2.MediaFormatVideo;
import org.pjsip.pjsua2.VidCodecParam;
import org.pjsip.pjsua2.pjsip_inv_state;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import javax.net.SocketFactory;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class newChatPage extends AppCompatActivity implements PlaceholderFragment.onSomeEventListener, ForwardActivity.onSomeEventListener, MessageListAdapter.onSomeEventListener {

    Intent getuserjid;
    ViewModelProvider.Factory viewmodel;
    ViewModelProvider viewModelProvider;
    ImageButton btn;
    TextView textTitle;
   static ImageButton imageCall, videoCall;
    ReportedData reportedData;
    viewModelContact pageViewModel;
    OutgoingChatMessageListener outgoingChatMessageListener;
    message_withtest17 message_withtest_17;
    String check, stanzaId, userjid, userType, myjid;
    MultiUserChatManager multiUserChatManager;
    MultiUserChat multiUserChat;
    EntityBareJid mucJid;
    WorkManager workManager;
    Flowable<message_chattest17> flowable;
    Disposable disposable;
    int countflag = 0;
    DataBase dataBase;
    ViewPager viewPager;
    TabLayout tabs;
    ProgressBar progressBar;
    String myNick;

    public static MyApp app = null;
    Socket clientSocket;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat_page);
        ActivityManager manager1 = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager1.getRunningServices(Integer.MAX_VALUE)) {
            Log.d("runningServicesPush", "" + service.service.getClassName());

            if (!service.service.getClassName().matches("com.example.projectchat.ServiceChat")) {

                Intent intent1 = new Intent(newChatPage.this, ServiceChat.class);
                startService(intent1);
            }

        }
//        Log.d("AppGetData ", ""+App.getComponent().userauth().getS().getUser().asEntityBareJidIfPossible().toString());

        btn = (ImageButton) findViewById(R.id.sendMess);
        textTitle=(TextView) findViewById(R.id.title);
        imageCall=(ImageButton)findViewById(R.id.call);
        imageCall.setImageResource(R.drawable.call_user);

        videoCall=(ImageButton)findViewById(R.id.videocall);
        videoCall.setImageResource(R.drawable.videocall);
        getuserjid = getIntent();
        textTitle.setText(getuserjid.getStringExtra("userNick"));
        viewmodel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        viewModelProvider = new ViewModelProvider(this, viewmodel);
        dataBase = App.getComponent().database();

        pageViewModel = ViewModelProviders.of(this).get(viewModelContact.class);
        pageViewModel.getNick().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("StringNick", "" + s);
            }
        });
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
         viewPager = findViewById(R.id.view_pager);

        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                myjid = dataBase.myLoginsDao().my_user_name();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();


        switch (getuserjid.getStringExtra("newuser")) {
            case "yes":
                Bundle bundle = new Bundle();
                bundle.putString("newuser", "yes");
                bundle.putString("userjid", getuserjid.getStringExtra("userjid"));
                bundle.putString("userNick", getuserjid.getStringExtra("userNick"));
                bundle.putString("mynick", getuserjid.getStringExtra("mynick"));
                userjid = getuserjid.getStringExtra("userjid");
                viewModelProvider.get(PageViewModel.class).setBudleUser(bundle);
                break;
            case "no":
                Bundle bundle1 = new Bundle();
                bundle1.putString("newuser", "no");
                myNick=getuserjid.getStringExtra("userNick");
                bundle1.putString("userNick", myNick);
                bundle1.putString("userjid", getuserjid.getStringExtra("userjid"));
                viewModelProvider.get(PageViewModel.class).setBudleUser(bundle1);
                Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Exception {
                        userjid = getuserjid.getStringExtra("userjid");//dataBase.message_withtestDAO().getJid(getuserjid.getStringExtra("userNick"));
                        userType = getuserjid.getStringExtra("userType");//dataBase.message_withtestDAO().getTypeChat(userjid);
                        Log.d("userjidCompletable", "" + userjid);
                        dataBase.message_withtestDAO().updateMark(0, userjid);
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (userType != null) {
                            switch (userType) {
                                case "chat":
                                    ChatManager.getInstanceFor(App.getComponent().userauth().getS()).addOutgoingListener(outgoingChatMessageListener);
                                    break;
                                case "group":
                                    multiUserChatManager = MultiUserChatManager.getInstanceFor(App.getComponent().userauth().getS());
                                    break;
                            }
                        }
                    }
                }).subscribe();
                break;
        }


        flowable = dataBase.mesagechatest_Dao().getDatauser()
                .flatMap(new Function<List<message_chattest17>, Flowable<message_chattest17>>() {
                    @Override
                    public Flowable<message_chattest17> apply(List<message_chattest17> message_chattest17s) throws Exception {
                        return Flowable.fromIterable(message_chattest17s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        switch (getuserjid.getStringExtra("newuser")) {
            case "yes":

                disposable = flowable
                        .subscribe(new Consumer<message_chattest17>() {
                            @Override
                            public void accept(message_chattest17 message_chattest17) throws Exception {
                                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy:MM:dd HH:mm");
                                String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(message_chattest17.getDate())));

                                if (message_chattest17.getNameFrom().matches(userjid) && message_chattest17.getTo().matches(myjid)) {

                                    if (message_chattest17.setBitmap() == null) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("what", "msg_hello");
                                        bundle.putString("fromServ", message_chattest17.getBody());
                                        bundle.putString("fromMessage", message_chattest17.getNameFrom());
                                        bundle.putString("datetime", currentDateTime);
                                        viewModelProvider.get(PageViewModel.class).setpostBudle(bundle);

                                    } else {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("what", "getImage");
                                        bundle.putString("PathOfImage", message_chattest17.setBitmap());
                                        bundle.putString("fromMessage", message_chattest17.getNameFrom());
                                        bundle.putString("datetime", currentDateTime);
                                        viewModelProvider.get(PageViewModel.class).setpostBudle(bundle);
                                    }
                                }
                            }
                        });
                break;
            case "no":
                disposable = flowable.skip(1)
                        .subscribe(new Consumer<message_chattest17>() {
                            @Override
                            public void accept(message_chattest17 message_chattest17) throws Exception {
                                Log.d("countFlag", "" + countflag);
                                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy:MM:dd HH:mm");
                                String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(message_chattest17.getDate())));

                                switch (userType) {
                                    case "chat":
                                        Log.d("chat", "chat incoming");
                                        if (message_chattest17.getNameFrom().matches(userjid) && message_chattest17.getTo().matches(myjid)) {

                                            if (message_chattest17.setBitmap() == null) {

                                                Bundle bundle = new Bundle();
                                                bundle.putString("what", "msg_hello");
                                                bundle.putString("fromServ", message_chattest17.getBody());
                                                bundle.putString("fromMessage", message_chattest17.getNameFrom());
                                                bundle.putString("datetime", currentDateTime);
                                                viewModelProvider.get(PageViewModel.class).setpostBudle(bundle);

                                            } else {
                                                Log.d("dateTimePic", "" + currentDateTime);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("what", "getImage");
                                                bundle.putString("PathOfImage", message_chattest17.setBitmap());
                                                bundle.putString("fromMessage", message_chattest17.getNameFrom());
                                                bundle.putString("datetime", currentDateTime);
                                                viewModelProvider.get(PageViewModel.class).setpostBudle(bundle);
                                            }
                                        }
                                        break;
                                    case "group":

                                        if (message_chattest17.getTo().matches(userjid) && !message_chattest17.getNameFrom().matches(myjid)) {

                                            Bundle bundlegroup1 = new Bundle();

                                            Completable.fromAction(new Action() {
                                                @Override
                                                public void run() throws Exception {
                                                    if (message_chattest17.setBitmap() == null) {
                                                        bundlegroup1.putString("what", "message_to_group");
                                                        bundlegroup1.putString("newmess", message_chattest17.getBody());
                                                        if (dataBase.message_withtestDAO().getNick(message_chattest17.getNameFrom())!=null) {
                                                            bundlegroup1.putString("getFrom", dataBase.message_withtestDAO().getNick(message_chattest17.getNameFrom()));
                                                        }else {
                                                            bundlegroup1.putString("getFrom", message_chattest17.getNameFrom());
                                                        }
                                                        bundlegroup1.putString("datetime", currentDateTime);

                                                    } else {
                                                        bundlegroup1.putString("what", "getImageGroup");
                                                        Log.d("TypeComeFrom", ""+message_chattest17.getNameFrom());
                                                        bundlegroup1.putString("PathOfImage", message_chattest17.setBitmap());
                                                        Log.d("getNick", ""+dataBase.message_withtestDAO().getNick(message_chattest17.getNameFrom()));
                                                        if (dataBase.message_withtestDAO().getNick(message_chattest17.getNameFrom())!=null) {
                                                            bundlegroup1.putString("getFrom", dataBase.message_withtestDAO().getNick(message_chattest17.getNameFrom()));
                                                        }else {
                                                            bundlegroup1.putString("getFrom", message_chattest17.getNameFrom());
                                                        }
                                                        bundlegroup1.putString("datetime", currentDateTime);
                                                    }
                                                }
                                            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action() {
                                                @Override
                                                public void run() throws Exception {
                                                    viewModelProvider.get(PageViewModel.class).setpostBudle(bundlegroup1);
                                                }
                                            }).subscribe();

                                        }
                                        break;
                                }
                            }
                        });
        }

        outgoingChatMessageListener = (to, message, chat) ->
        {
            Log.d("Outgoing ", message.toXML("").toString());
          if (message.getStanzaId()==null) {
              message.setStanzaId(stanzaId);
          }

            try {
                isConPing(message.getStanzaId());
            } catch (StreamManagementException.StreamManagementNotEnabledException e) {
                e.printStackTrace();
            }

        };


    }

    public void makeVideoCall(View view)
    {
        if (SipPhone.currentCall != null) {
            SipPhone.currentCall.delete();
            return;
        }

        MyCall call = new MyCall(SipPhone.account, -1);
        CallOpParam prm = new CallOpParam(true);
       /*CallSetting callSetting=new CallSetting();
        callSetting.setAudioCount(1);
        callSetting.setVideoCount(0);
        prm.setOpt(callSetting);*/
        AccountVideoConfig avc=new AccountVideoConfig();
        MediaFormatVideo mf=new MediaFormatVideo();

        mf.setFpsNum(30);
        mf.setFpsDenum(1);
        mf.setAvgBps(512000);
        mf.setMaxBps(2048000);
        mf.setHeight(720);
        mf.setWidth(1280);


        Log.e("javan-video",String.valueOf(avc.getAutoShowIncoming()));
        Log.e("javan-videofps",String.valueOf(mf.getFpsNum()));

        try {
            call.makeCall("sip:6004@192.168.0.27", prm);
            if (!App.getComponent().userauth().getRoster().getPresence(JidCreate.bareFrom(userjid)).getType().toString().matches("available")) {


                Data.Builder data1 = new Data.Builder();
                data1.putString("message", "phone");
                data1.putString("userjid", userjid);
                data1.putString("text", "");
                data1.putString("phone", "6004");


                OneTimeWorkRequest pushWorker=new OneTimeWorkRequest.Builder(MyMainWorker.class).setInputData(data1.build()).build();
                workManager = WorkManager.getInstance(getApplicationContext());
                workManager.beginWith(pushWorker).enqueue();
                workManager.getWorkInfoByIdLiveData(pushWorker.getId()).observe(this, workInfo -> {
                    if (workInfo != null) {

                        WorkInfo.State state = workInfo.getState();
                        if (state.name().matches("RUNNING")) {

                        }
                        if (state.name().matches("SUCCEEDED")) {
                            Log.d("Sended", "Sended");
                        }
                    }
                });

            }
        } catch (Exception e) {
            call.delete();
            return;
        }
        AudioManager audioManager=(AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
        SipPhone.currentCall = call;
        showCallActivity(1);
    }
    public void makeCall1(View view)
    {

        if (SipPhone.currentCall != null) {
            SipPhone.currentCall.delete();
            return;
        }

        MyCall call = new MyCall(SipPhone.account, -1);
        CallOpParam prm = new CallOpParam(true);
        CallSetting callSetting=new CallSetting();
        callSetting.setAudioCount(1);
        callSetting.setVideoCount(0);
        prm.setOpt(callSetting);
        AccountVideoConfig avc=new AccountVideoConfig();
        MediaFormatVideo mf=new MediaFormatVideo();

        mf.setFpsNum(30);
        mf.setFpsDenum(1);
        mf.setAvgBps(512000);
        mf.setMaxBps(2048000);
        mf.setHeight(720);
        mf.setWidth(1280);
        Log.e("javan-video",String.valueOf(avc.getAutoShowIncoming()));
        Log.e("javan-videofps",String.valueOf(mf.getFpsNum()));

        try {
            call.makeCall("sip:6004@192.168.0.27", prm);
            if (!App.getComponent().userauth().getRoster().getPresence(JidCreate.bareFrom(userjid)).getType().toString().matches("available")) {


                Data.Builder data1 = new Data.Builder();
                data1.putString("message", "phone");
                data1.putString("userjid", userjid);
                data1.putString("text", "");
                data1.putString("phone", "6004");


                OneTimeWorkRequest pushWorker=new OneTimeWorkRequest.Builder(MyMainWorker.class).setInputData(data1.build()).build();
                workManager = WorkManager.getInstance(getApplicationContext());
                workManager.beginWith(pushWorker).enqueue();
                workManager.getWorkInfoByIdLiveData(pushWorker.getId()).observe(this, workInfo -> {
                    if (workInfo != null) {

                        WorkInfo.State state = workInfo.getState();
                        if (state.name().matches("RUNNING")) {

                        }
                        if (state.name().matches("SUCCEEDED")) {
                            Log.d("Sended", "Sended");
                        }
                    }
                });

            }
        } catch (Exception e) {
            call.delete();
            return;
        }

        SipPhone.currentCall = call;
        showCallActivity(0);
    }

    @Override
    public void SomeEvent(Bundle k) {
        switch (k.getString("type")) {
            case "chat":
                stanzaId = k.getString("stanzaId");
                Log.d("stanzaId", "" + stanzaId);
                Log.d("bundlegetString", "" + k.getString("to"));
                Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Exception {
                        check = dataBase.message_withtestDAO().checkUser(k.getString("to"));
                        Log.d("check", "" + check);
                        if (check == null) {
                            Date date = new Date();
                            Timestamp ts = new Timestamp(date.getTime());

                            try {
                                App.getComponent().userauth().getRoster().createEntry(JidCreate.bareFrom(getuserjid.getStringExtra("userJid")), getuserjid.getStringExtra("userNick"), new String[] {"friend of"+App.getComponent().userauth().getS().getUser().getLocalpartOrThrow()});

                                Presence presence2 = new Presence(Presence.Type.subscribe);
                                presence2.setStatus("Online, Programmatically!");
                                presence2.setPriority(24);
                                presence2.setMode(Presence.Mode.available);
                                App.getComponent().userauth().getS().sendStanza(presence2);

                                if (App.getComponent().userauth().getRoster().getPresence(JidCreate.bareFrom(getuserjid.getStringExtra("userJid"))).getType().toString().matches("available")){
                                    message_withtest_17 = new message_withtest17();
                                    message_withtest_17.mwith1 = getuserjid.getStringExtra("userJid");
                                    message_withtest_17.nick1 = getuserjid.getStringExtra("userNick");
                                    message_withtest_17.type = "chat";
                                    message_withtest_17.status="online";
                                    message_withtest_17.mark = 1;
                                    message_withtest_17.mtime1 = String.valueOf(ts.getTime());
                                    dataBase.message_withtestDAO().insert(message_withtest_17);
                                } else {
                                    message_withtest_17 = new message_withtest17();
                                    message_withtest_17.mwith1 = getuserjid.getStringExtra("userJid");
                                    message_withtest_17.nick1 = getuserjid.getStringExtra("userNick");
                                    message_withtest_17.type = "chat";
                                    message_withtest_17.status="offline";
                                    message_withtest_17.mark = 1;
                                    message_withtest_17.mtime1 = String.valueOf(ts.getTime());
                                    dataBase.message_withtestDAO().insert(message_withtest_17);
                                }
                            } catch (XmppStringprepException e) {
                                e.printStackTrace();
                            }catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }catch (SmackException.NotLoggedInException e) {
                                e.printStackTrace();
                            } catch (SmackException.NoResponseException e) {
                                e.printStackTrace();
                            } catch (XMPPException.XMPPErrorException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
                try {
                    ChatManager.getInstanceFor(App.getComponent().userauth().getS()).chatWith(JidCreate.entityBareFrom(k.getString("to"))).send(k.getString("message"));

                    Log.d("getRoster ", ""+App.getComponent().userauth().getRoster().getPresence(JidCreate.bareFrom(k.getString("to"))).getType().toString());

                    Log.d("getRoster ", ""+App.getComponent().userauth().getRoster().getPresence(JidCreate.bareFrom(k.getString("to"))).getType().toString().matches("available"));
                    //if (!App.getComponent().userauth().getRoster().getPresence(JidCreate.bareFrom(k.getString("to"))).getType().toString().matches("available")) {


                        Data.Builder data1 = new Data.Builder();
                        data1.putString("message", "message");
                        data1.putString("userjid", userjid);
                        data1.putString("from", myjid);
                        data1.putString("text", k.getString("message"));
                        data1.putString("phone", "0");


                        OneTimeWorkRequest pushWorker=new OneTimeWorkRequest.Builder(MyMainWorker.class).setInputData(data1.build()).build();
                        workManager = WorkManager.getInstance(getApplicationContext());
                        workManager.beginWith(pushWorker).enqueue();
                        workManager.getWorkInfoByIdLiveData(pushWorker.getId()).observe(this, workInfo -> {
                            if (workInfo != null) {

                                WorkInfo.State state = workInfo.getState();
                                if (state.name().matches("RUNNING")) {

                                }
                                if (state.name().matches("SUCCEEDED")) {
                                   Log.d("Sended", "Sended");
                                }
                            }
                        });


                   // }
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
                break;
            case "group":
                stanzaId = k.getString("stanzaId");

                sendToGroup(k.getString("to"), k.getString("message"));
                break;
            case "sendPicChat":
                stanzaId = k.getString("stanzaId");
                Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Exception {
                        check = dataBase.message_withtestDAO().checkUser(userjid);
                        Log.d("check", "" + check);
                        if (check == null) {
                            Date date = new Date();
                            Timestamp ts = new Timestamp(date.getTime());
                            for (int i = 0; i < reportedData.getRows().size(); i++) {
                                if (userjid.equals(reportedData.getRows().get(i).getValues("jid").get(0).toString())) {
                                    Log.d("estUser", "" + reportedData.getRows().get(i).getValues("jid").get(0).toString());
                                    message_withtest_17 = new message_withtest17();
                                    message_withtest_17.mwith1 = reportedData.getRows().get(i).getValues("jid").get(0).toString();
                                    message_withtest_17.nick1 = reportedData.getRows().get(i).getValues("nick").get(0).toString();
                                    message_withtest_17.type = "chat";
                                    message_withtest_17.mark = 1;
                                    message_withtest_17.mtime1 = String.valueOf(ts.getTime());
                                    dataBase.message_withtestDAO().insert(message_withtest_17);
                                }
                            }
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

                sendPicture(k.getString("fromClientPic"), stanzaId, k.getString("servertime"));
                Log.d("pathFile ", ""+k.getString("fromClientPic"));
                break;
            case "sendPicGroup":
                stanzaId = k.getString("stanzaId");
                sendToGroup1(k.getString("to"),k.getString("fromClientPic"), stanzaId, k.getString("servertime"));
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("onStart", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onStart", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onStart", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("onStart", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatManager.getInstanceFor(App.getComponent().userauth().getS()).removeOutgoingListener(outgoingChatMessageListener);


        disposable.dispose();
        Log.d("onStart", "onDestroy");
    }

    public void isConPing(String id) throws StreamManagementException.StreamManagementNotEnabledException {
        ((XMPPTCPConnection) App.getComponent().userauth().getS()).addStanzaIdAcknowledgedListener(id, new StanzaListener() {
            @Override
            public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                Log.d("ackStanza", "" + packet.toXML(""));
                if (App.getComponent().userauth().getS().isConnected()) {
                    org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message) packet;
                    Log.d("ackStanzaMessage", "" + message.getStanzaId());
                    dataBase.tmp_messageoutDAO().deleteFromTmp(message.getStanzaId());
                }
            }
        });
    }

    public void sendToGroup(String groupname, String messageToGroup) {

        try {
            mucJid = JidCreate.entityBareFrom(groupname);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        multiUserChat = multiUserChatManager.getMultiUserChat(mucJid);
        try {
            Log.d("isJoined", "" + multiUserChat.isJoined());
            final org.jivesoftware.smack.packet.Message messagetogroup = new org.jivesoftware.smack.packet.Message();
            messagetogroup.setStanzaId(stanzaId);
            messagetogroup.setBody(messageToGroup);
            multiUserChat.addMessageListener(message -> Log.d("messagemuclistener", "" + message.toXML("")));
            multiUserChat.sendMessage(messagetogroup);
            try {
                isConPing(messagetogroup.getStanzaId());
            } catch (StreamManagementException.StreamManagementNotEnabledException e) {
                e.printStackTrace();
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void sendToGroup1(String groupname, String result, String _stanzaId, String _servertime) {
        try {
            mucJid = JidCreate.entityBareFrom(groupname);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        multiUserChat= multiUserChatManager.getMultiUserChat(mucJid);
        File file1 = new File(result);

        String type=file1.getName().substring(file1.getName().lastIndexOf('.'), file1.getName().length());
        String extension= MimeTypeMap.getFileExtensionFromUrl(file1.getName());
        String mimetype=MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (extension!=null){
            Log.d("MimeType", ""+MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
        }


        Log.d("multiUserChat", ""+multiUserChat.toString());
        ImageSentExtensions imageSentExtensions1=new ImageSentExtensions();
        imageSentExtensions1.setDescription("description");
        imageSentExtensions1.setUrl("ChatMFC");

        Data.Builder data = new Data.Builder();
        data.putString("result", result);
        data.putString("userjid", groupname);
        data.putString("name", _servertime);
        data.putString("mime", mimetype);
        data.putString("type", type);

        Data.Builder data1 = new Data.Builder();
        data1.putString("result", result);
        data1.putString("userjid", groupname);
        data1.putString("name", _servertime);
        data1.putString("type", type);

        OneTimeWorkRequest oneTimeWorkRequestgroup = new OneTimeWorkRequest.Builder(MyWorker.class).setInputData(data.build()).build();
        OneTimeWorkRequest oneTimeWorkRequestgroup1 = new OneTimeWorkRequest.Builder(MyWorkerThumb.class).setInputData(data1.build()).build();

        workManager = WorkManager.getInstance(getApplicationContext());
        workManager.beginWith(oneTimeWorkRequestgroup1).then(oneTimeWorkRequestgroup).enqueue();
        workManager.getWorkInfoByIdLiveData(oneTimeWorkRequestgroup.getId()).observe(this, workInfo -> {
            if (workInfo != null) {

                WorkInfo.State state = workInfo.getState();
                if (state.name().matches("RUNNING")) {
                    if (mimetype.equals("video/mp4")) {
                        progressBar = (ProgressBar) findViewById(R.id.progressvideo);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
                if (state.name().matches("SUCCEEDED")) {
                    Log.d("stategroup", "state = " + state.name());

                        if (mimetype.equals("video/mp4")) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        ImageSentExtensions imageSentExtensions = new ImageSentExtensions();
                        imageSentExtensions.setDescription("description");
                        imageSentExtensions.setUrl("ChatMFC"+_servertime+file1.getName().substring(file1.getName().lastIndexOf('.'), file1.getName().length()));

                        Message message1 = new Message();
                        message1.addExtension(imageSentExtensions);
                        message1.addBody("tag", "hey");
                        message1.setStanzaId(_stanzaId);
                        Log.d("messagegroup", ""+message1.toXML());
                        try {
                            Log.d("isJoined", "" + multiUserChat.isJoined());
                            multiUserChat.sendMessage(message1);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

        });

        try {
            Log.d("resultfromPlaceholder", "" + WorkManager.getInstance().enqueue(oneTimeWorkRequestgroup).getResult().get().toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static int caluclateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height=options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height>reqHeight || width>reqWidth){
            final int halfHeight=height/2;
            final int halfWidth = width/2;
            while ((halfHeight/inSampleSize)>=reqHeight && (halfWidth/inSampleSize)>=reqWidth) {
            inSampleSize*=2;
            }
        }
        return inSampleSize;
    }


    public void sendPicture(String fileName, String _stanzaId, String _servertime) {
         File file = new File(fileName);

        String type=file.getName().substring(file.getName().lastIndexOf('.'), file.getName().length());
        String extension= MimeTypeMap.getFileExtensionFromUrl(file.getName());
        String mimetype=MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (extension!=null){
            Log.d("MimeType", ""+MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
        }

        Data.Builder data = new Data.Builder();
        data.putString("result", fileName);
        data.putString("userjid", App.getComponent().userauth().getS().getUser().asEntityBareJidString());
        data.putString("name", _servertime);
        data.putString("mime", mimetype);
        data.putString("type", type);

        Data.Builder data1 = new Data.Builder();
        data1.putString("result", fileName);
        data1.putString("userjid", App.getComponent().userauth().getS().getUser().asEntityBareJidString());
        data1.putString("name", _servertime);
        data1.putString("type", type);

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MyWorker.class).setInputData(data.build()).build();
        OneTimeWorkRequest oneTimeWorkRequest1 = new OneTimeWorkRequest.Builder(MyWorkerThumb.class).setInputData(data1.build()).build();

        workManager = WorkManager.getInstance(getApplicationContext());
        workManager.beginWith(oneTimeWorkRequest1).then(oneTimeWorkRequest).enqueue();
        workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId()).observe(this, workInfo -> {
            if (workInfo != null) {

                WorkInfo.State state = workInfo.getState();
                Log.d("state", "state = " + state.name());
                Log.d("state1", "state = " + state.name());
                if (state.name().matches("RUNNING")) {
                    if (mimetype.equals("video/mp4")) {
                        progressBar = (ProgressBar) findViewById(R.id.progressvideo);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
                if (state.name().matches("SUCCEEDED")) {
                    if (mimetype.equals("video/mp4")) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    ImageSentExtensions imageSentExtensions1 = new ImageSentExtensions();
                    imageSentExtensions1.setDescription("description");
                    imageSentExtensions1.setUrl("ChatMFC"+_servertime+file.getName().substring(file.getName().lastIndexOf('.'), file.getName().length()));

                    Message message1 = new Message();
                    message1.addExtension(imageSentExtensions1);
                    message1.addBody("tag", "hey");
                    message1.setStanzaId(_stanzaId);
                    try {
                        ChatManager.getInstanceFor(App.getComponent().userauth().getS()).chatWith(JidCreate.entityBareFrom(userjid)).send(message1);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (XmppStringprepException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void SomeEventForward(Bundle k) {
        {
             stanzaId = k.getString("stanzaId");
        }
    }

    @Override
    public void SomeEventVideo(Bundle s) {
        String pathVideo=s.getString("pathVideo");

        Intent intent=new Intent(this, ScreenSlidePagerActivity.class);
        intent.putExtra("pathVideo", pathVideo);
        startActivity(intent);
    }


    private void showCallActivity(int typeofcall)
    {
        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra("calling", typeofcall);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



}
