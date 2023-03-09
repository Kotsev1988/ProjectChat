package com.example.projectchat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.projectchat.RoomDB.DataBase;
import com.example.projectchat.RoomDB.message_chattest17;
import com.example.projectchat.RoomDB.message_withtest17;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.roster.AbstractRosterListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.sm.StreamManagementException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.element.MamElements;
import org.jivesoftware.smackx.muc.MucEnterConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.ItemsExtension;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubException;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSetting;
import org.pjsip.pjsua2.StringVector;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

public class ServiceChat extends Service {//implements  MyAppObserver, Handler.Callback{

    MyLoginTask task;
    int IdServ;

    EntityFullJid jid1;
    UserSearchManager userSearchManager;
     DomainBareJid domainBareJid;
    Form form;
    Form answerForm;
    ReportedData reportedData;
    String usernickfrom, myjid;
    String nameofgroup1;
    IQ iq;

    URL url = null;
    HttpURLConnection urlConnection1 = null;
    Ringtone r;
    String currentDateTimeInc;
    MamManager mamManager, mamManager1;
    List<message_withtest17> mucName = new ArrayList<>();
    DataBase dataBase;
    message_chattest17 message_chattest_17;
    message_withtest17 message_withtest_17;

    IncomingChatMessageListener incomingChatMessageListener;
    SharedPreferences sharedPreferences;
    StanzaListener stanzaListener;
    StanzaFilter stanzaFilter;
    StandardExtensionElement eventElement1, eventElement2;
    Roster roster;

    /*public static MyApp app = null;
    public static MyCall currentCall = null;
    public static MyAccount account = null;
    public static AccountConfig accCfg = null;
    private final Handler handler = new Handler(this);


    public class MSG_TYPE
    {
        public final static int INCOMING_CALL = 1;
        public final static int CALL_STATE = 2;
        public final static int REG_STATE = 3;
        public final static int BUDDY_STATE = 4;
        public final static int CALL_MEDIA_STATE = 5;
        public final static int CHANGE_NETWORK = 6;
    }*/
    @Override
    public void onCreate() {
        Log.d("OnCreateService", "OnCreateService");


        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        sharedPreferences = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        Log.d("sharedPref", "" + sharedPreferences.getInt("activityState", 0));
        //((XMPPTCPConnection)App.getComponent().userauth().getS()).setReplyTimeout(15000);
        /*app = new MyApp();
        app.init(ServiceChat.this);
        accCfg = new AccountConfig();
        accCfg.setIdUri("sip:6004@192.168.0.27");
        accCfg.getNatConfig().setIceEnabled(true);
        accCfg.getVideoConfig().setAutoTransmitOutgoing(true);
        accCfg.getVideoConfig().setAutoShowIncoming(true);
        account = app.addAcc(accCfg);
        accCfg.getRegConfig().setRegistrarUri("sip:192.168.0.27");
        AuthCredInfoVector creds = accCfg.getSipConfig().
                getAuthCreds();

        creds.clear();
        creds.add(new AuthCredInfo("Digest", "*", "6004", 0,
                "1234"));

        StringVector proxies = accCfg.getSipConfig().getProxies();
        proxies.clear();

        accCfg.getNatConfig().setIceEnabled(true);

        try {
            account.modify(accCfg);
        } catch (Exception e) {
            app.deinit();
            if (currentCall!=null) {
                currentCall.delete();
                currentCall = null;
            }
        }*/


        task = new MyLoginTask();
        task.execute();

    }

    public void doForegroundThings(){
        NotificationManager notificationManager1;
        notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chanel = new NotificationChannel("78", "My Channel", NotificationManager.IMPORTANCE_HIGH);

            chanel.setDescription("My channel description");
            chanel.enableLights(true);
            chanel.setLightColor(Color.RED);
            chanel.enableVibration(false);
            chanel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            notificationManager1.createNotificationChannel(chanel);

        }
        //else{
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ServiceChat.this, "78")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("title")
                .setContentText("msg")
                .setAutoCancel(true)
                .setChannelId("78")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true);
        Notification notification1 = builder.build();

        notificationManager1.notify(78, notification1);
        //}
    }


    @Override
    public IBinder onBind(Intent intent) {
        //return mMessenger.getBinder();
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ServiceonStartComand", "start");
        IdServ = startId;
        return START_NOT_STICKY;
    }

  /*  @Override
    public void notifyRegState(int code, String reason, long expiration) {
        String msg_str = "";
        if (expiration == 0)
            msg_str += "Unregistration";
        else
            msg_str += "Registration";

        if (code/100 == 2)
            msg_str += " successful";
        else
            msg_str += " failed: " + reason;

        android.os.Message m = android.os.Message.obtain(handler, MSG_TYPE.REG_STATE, msg_str);
        m.sendToTarget();
    }

    @Override
    public void notifyIncomingCall(MyCall call) {

        Log.d("NotifyIncoming", "Call");
        android.os.Message m = android.os.Message.obtain(handler, ServiceChat.MSG_TYPE.INCOMING_CALL, call);
        m.sendToTarget();

    }

    @Override
    public void notifyCallState(MyCall call) {
        if (currentCall == null || call.getId() != currentCall.getId())
            return;

        CallInfo ci = null;
        try {
            ci = call.getInfo();
            Log.d("callstateService", ""+ci.getState()+"/"+ci.getStateText());
        } catch (Exception e) {}

        if (ci == null)
            return;

        android.os.Message m = android.os.Message.obtain(handler, MSG_TYPE.CALL_STATE, ci);
        m.sendToTarget();
    }

    @Override
    public void notifyCallMediaState(MyCall call) {
android.os.Message m = android.os.Message.obtain(handler, MSG_TYPE.CALL_MEDIA_STATE, null);
        m.sendToTarget();
    }

    @Override
    public void notifyBuddyState(MyBuddy buddy) {

    }

    @Override
    public void notifyChangeNetwork() {

    }

    @Override
    public boolean handleMessage(@NonNull android.os.Message m)
    {
        {
            if (m.what == 0) {

                app.deinit();
                //finish();
                Runtime.getRuntime().gc();
                android.os.Process.killProcess(android.os.Process.myPid());

            } else if (m.what == MainActivity.MSG_TYPE.CALL_STATE) {

                CallInfo ci = (CallInfo) m.obj;
                Log.d("cigetState", ci.getStateText());
                if (currentCall == null || ci == null || ci.getId() != currentCall.getId()) {
                    System.out.println("Call state event received, but call info is invalid");
                    return true;
                }
                if (CallActivity.handler_ != null) {
                    android.os.Message m2 = android.os.Message.obtain(CallActivity.handler_, MSG_TYPE.CALL_STATE, ci);
                    m2.sendToTarget();
                }

                if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)
                {
                    currentCall.delete();
                    currentCall = null;
                }

            } else if (m.what == ServiceChat.MSG_TYPE.CALL_MEDIA_STATE) {

                {
                    CallInfo ci = (CallInfo) m.obj;

                    if (currentCall == null || ci == null || ci.getId() != currentCall.getId()) {
                        System.out.println("Call state event received, but call info is invalid");
                        return true;
                    }

                    if (CallActivity.handler_ != null) {
                        android.os.Message m2 = android.os.Message.obtain(CallActivity.handler_, ServiceChat.MSG_TYPE.CALL_STATE, ci);
                        m2.sendToTarget();
                    }


                }
            } else if (m.what == ServiceChat.MSG_TYPE.BUDDY_STATE) {

                MyBuddy buddy = (MyBuddy) m.obj;
                int idx = account.buddyList.indexOf(buddy);
            } else if (m.what == ServiceChat.MSG_TYPE.REG_STATE) {

                String msg_str = (String) m.obj;
                //lastRegStatus = msg_str;

            } else if (m.what == ServiceChat.MSG_TYPE.INCOMING_CALL) {

                final MyCall call = (MyCall) m.obj;
                CallOpParam prm = new CallOpParam();

                if (currentCall != null) {
                    prm.setStatusCode(pjsip_status_code.PJSIP_SC_BUSY_HERE);
                    try {
                        call.hangup(prm);
                    } catch (Exception e) {}
                    call.delete();
                    return true;
                }

                prm.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
                try {
                    CallSetting callSetting=new CallSetting();
                    /*callSetting.setAudioCount(1);
                    callSetting.setVideoCount(0);
                    prm.setOpt(callSetting);*/


                  /*  call.answer(prm);
                } catch (Exception e) {}

                currentCall = call;
                Intent intent = new Intent();
                intent.setAction("IncomingCall");

                sendBroadcast(intent);

            } else if (m.what == MainActivity.MSG_TYPE.CHANGE_NETWORK) {
                app.handleNetworkChange();
            } else {
                return false;
            }
            return true;
        }
    }*/


    class MyLoginTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... voids) {

            dataBase = App.getComponent().database();
            sharedPreferences = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

            XMPPTCPConnection.setUseStreamManagementDefault(true);
            XMPPTCPConnection.setUseStreamManagementResumptionDefault(true);
            App.getComponent().reconnectListener();


            roster = App.getComponent().userauth().getRoster();
            roster.addRosterLoadedListener(new RosterLoadedListener() {
                @Override
                public void onRosterLoaded(Roster roster) {
                    Set<RosterEntry> entries = roster.getEntries();
                    for (RosterEntry entry : entries) {
                        Log.d("XMPPChatDemoActivity", "Name: "+entry.getName());
                        Log.d("XMPPChatDemoActivity", "Id: " + entry.getUser());

                        Presence entryPresence = null;
                        try {
                            entryPresence = roster.getPresence(JidCreate.bareFrom(entry.getUser()));
                        } catch (XmppStringprepException e) {
                            e.printStackTrace();
                        }
                        Log.d("XMPPChatDemoActivity", "Presence Status: " +        entryPresence.getStatus());
                        Log.d("XMPPChatDemoActivity", "Presence Type: " + entryPresence.getType());
                    }
                }

                @Override
                public void onRosterLoadingFailed(Exception exception) {

                }
            });
            try {
                roster.reloadAndWait();
            } catch (SmackException.NotLoggedInException e5) {
                e5.printStackTrace();
            } catch (SmackException.NotConnectedException e5) {
                e5.printStackTrace();
            } catch (InterruptedException e5) {
                e5.printStackTrace();
            }
            roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            Presence presence1 = new Presence(Presence.Type.available);
            presence1.setStatus("Hello body");
            presence1.setMode(Presence.Mode.available);
            try {
                App.getComponent().userauth().getS().sendStanza(presence1);
            } catch (SmackException.NotConnectedException e4) {
                e4.printStackTrace();
            } catch (InterruptedException e4) {
                e4.printStackTrace();
            }
            List<message_withtest17> contact_users = dataBase.message_withtestDAO().getAll();


            RosterGroup ry = roster.getGroup("friend of" + App.getComponent().userauth().getS().getUser().getLocalpartOrThrow());
            boolean isExictRoster = false;
            if (ry!=null){
            for (RosterEntry entry : ry.getEntries()) {
                String checkUser = dataBase.message_withtestDAO().checkUser(entry.getJid().asEntityBareJidIfPossible().toString());

                if (checkUser != null) {
                    isExictRoster = true;
                }

                if (isExictRoster == false) {
                    if (!entry.isSubscriptionPending()) {
                        try {
                            String nick = dataBase.message_withtestDAO().getNick(entry.getJid().toString());
                            if (nick != null) {
                                roster.createEntry(entry.getJid(), nick, new String[]{"friend of" + App.getComponent().userauth().getS().getUser().getLocalpartOrThrow()});
                                Presence presence = new Presence(Presence.Type.subscribe);
                                presence.setStatus("online, Programmatically!");
                                presence.setPriority(24);
                                presence.setMode(Presence.Mode.available);
                                App.getComponent().userauth().getS().sendStanza(presence);
                            }
                        } catch (SmackException.NotLoggedInException e) {
                            e.printStackTrace();
                        } catch (SmackException.NoResponseException e) {
                            e.printStackTrace();
                        } catch (XMPPException.XMPPErrorException e) {
                            e.printStackTrace();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.d("getTypeFriend", "" + roster.getPresence(entry.getJid()).getType().toString() + " " + entry.getJid().toString());
                    if (roster.getPresence(entry.getJid()).getType().toString().matches("available")) {
                        dataBase.message_withtestDAO().updateStatus("online", entry.getJid().toString());
                    } else {
                        dataBase.message_withtestDAO().updateStatus("offline", entry.getJid().toString());
                    }

                }
            }
        }
               File file =new File(Environment.getExternalStorageDirectory(), "mfchat");

            if (!file.exists()){
                file.mkdir();
            }

            incomingChatMessageListener = (from, message, chat) -> {
                Log.d("sharedPref", "" + sharedPreferences.getInt("activityState", 0));
                Long idstamp = Long.parseLong("0");
                ExtensionElement evExt;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                eventElement1 = message.getExtension("archived", "urn:xmpp:mam:tmp");

                if (message.getExtension("urn:xmpp:mam:tmp") != null) {
                    idstamp = (Long.parseLong(eventElement1.getAttributeValue("id")) / 1000);
                    currentDateTimeInc = simpleDateFormat.format(new Date(Long.parseLong(eventElement1.getAttributeValue("id")) / 1000));
                }

                if (message.getExtension("urn:xmpp:delay") != null) {
                    DelayInformation delayInformation = DelayInformation.from(message);

                    if (delayInformation != null) {
                        idstamp = delayInformation.getStamp().getTime();
                        currentDateTimeInc = simpleDateFormat.format(new Date(idstamp));
                    }
                }

                if (message.hasExtension("x", "jabber:x:oob") && message.getBody("tag").matches("hey")) {
                    eventElement2 = message.getExtension("x", "jabber:x:oob");
                    String fileName = eventElement2.getFirstElement("url", "jabber:x:oob").getText();

                    downloadFile("http://192.168.0.27/uploads/"+message.getFrom().asEntityBareJidIfPossible().toString(), fileName);

                    if (dataBase.message_withtestDAO().checkUser(from.toString()) == null) {
                        try {
                            VCard card= VCardManager.getInstanceFor(App.getComponent().userauth().getS()).loadVCard(from.asEntityBareJid());
                            if (card!=null){
                                updateDBWith(from.asEntityBareJidString(), card.getNickName(), "chat", "offline",2, String.valueOf(idstamp), 0);
                                updateChatDB(message.getBody("tag"), from.toString(), idstamp.toString(), message.getStanzaId(), App.getComponent().userauth().getS().getUser().asEntityBareJidString(), Environment.getExternalStorageDirectory() + File.separator + "mfchat" + File.separator + fileName, 110);
                                dataBase.message_withtestDAO().updateDBUser(idstamp.toString(), from.asEntityBareJid().toString());

                                try {
                                    roster.createEntry(JidCreate.bareFrom(from.toString()), card.getNickName(), new String[] {"friend of"+App.getComponent().userauth().getS().getUser().getLocalpartOrThrow()});

                                    Presence presence2 = new Presence(Presence.Type.subscribe);
                                    presence2.setStatus("Online, Programmatically!");
                                    presence2.setPriority(24);
                                    presence2.setMode(Presence.Mode.available);
                                    App.getComponent().userauth().getS().sendStanza(presence2);

                                    if (roster.getPresence(JidCreate.bareFrom(from.toString())).getType().toString().matches("available")){
                                        dataBase.message_withtestDAO().updateStatus("online", from.asEntityBareJid().toString());
                                    } else {
                                        dataBase.message_withtestDAO().updateStatus("offline", from.asEntityBareJid().toString());
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
                        } catch (SmackException.NoResponseException e) {
                            e.printStackTrace();
                        } catch (XMPPException.XMPPErrorException e) {
                            e.printStackTrace();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else {
                        updateChatDB(message.getBody("tag"), from.toString(), idstamp.toString(), message.getStanzaId(), App.getComponent().userauth().getS().getUser().asEntityBareJidString(), Environment.getExternalStorageDirectory() + File.separator + "mfchat" + File.separator + fileName, 110);
                        dataBase.message_withtestDAO().updateDBUser(idstamp.toString(), from.asEntityBareJid().toString());
                    }
                    if (sharedPreferences.getInt("activityState", 0) == 1677) {
                        String nick =dataBase.message_withtestDAO().getNick(from.toString());
                        notification(message.getBody(), nick);
                        dataBase.message_withtestDAO().updateMark(1, from.toString());
                    } else if (sharedPreferences.getInt("activityState", 0) == 1675) {
                        int getNotReaded=dataBase.message_withtestDAO().getMark(from.asEntityBareJidIfPossible().toString());
                        getNotReaded=getNotReaded+1;
                        dataBase.message_withtestDAO().updateMark(getNotReaded, from.asEntityBareJidIfPossible().toString());
                    } else if (sharedPreferences.getInt("activityState", 0) == 1777) {
                        String nick = dataBase.message_withtestDAO().getNick(from.toString());
                        notification("Picture", nick);
                        dataBase.message_withtestDAO().updateMark(2, from.toString());
                    }
                }

                usernickfrom = dataBase.message_withtestDAO().getNick(from.toString());

                if (!message.hasExtension("x", "jabber:x:oob")) {
                    if (dataBase.message_withtestDAO().checkUser(from.toString()) == null) {
                        try {
                            VCard card= VCardManager.getInstanceFor(App.getComponent().userauth().getS()).loadVCard(from.asEntityBareJid());
                            if (card!=null){
                                updateDBWith(from.asEntityBareJid().toString(), card.getNickName(), "chat", "offline", 2, String.valueOf(idstamp), 0);
                                Log.d("IncomeMessage ", ""+dataBase.message_withtestDAO().getAll().size());
                                updateChatDB(message.getBody(), from.toString(), idstamp.toString(), message.getStanzaId(), App.getComponent().userauth().getS().getUser().asEntityBareJidString(), null, 110);
                                dataBase.message_withtestDAO().updateDBUser(idstamp.toString(), from.asEntityBareJid().toString());

                                try {
                                    roster.createEntry(JidCreate.bareFrom(from.toString()), card.getNickName(), new String[] {"friend of"+App.getComponent().userauth().getS().getUser().getLocalpartOrThrow()});

                                    Presence presence2 = new Presence(Presence.Type.subscribe);
                                    presence2.setStatus("Online, Programmatically!");
                                    presence2.setPriority(24);
                                    presence2.setMode(Presence.Mode.available);
                                    App.getComponent().userauth().getS().sendStanza(presence2);

                                    if (roster.getPresence(JidCreate.bareFrom(from.toString())).getType().toString().matches("available")){
                                        dataBase.message_withtestDAO().updateStatus("online", from.asEntityBareJid().toString());
                                    } else {
                                        dataBase.message_withtestDAO().updateStatus("offline", from.asEntityBareJid().toString());
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
                        } catch (SmackException.NoResponseException e) {
                            e.printStackTrace();
                        } catch (XMPPException.XMPPErrorException e) {
                            e.printStackTrace();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else {
                        updateChatDB(message.getBody(), from.toString(), idstamp.toString(), message.getStanzaId(), App.getComponent().userauth().getS().getUser().asEntityBareJidString(), null, 110);
                        dataBase.message_withtestDAO().updateDBUser(idstamp.toString(), from.asEntityBareJid().toString());
                    }
                    if (sharedPreferences.getInt("activityState", 0) == 1677) {
                        String nick =dataBase.message_withtestDAO().getNick(from.toString());
                        notification(message.getBody(), nick);

                        dataBase.message_withtestDAO().updateMark(2, from.toString());
                    } else if (sharedPreferences.getInt("activityState", 0) == 1675) {
                        int getNotReaded=dataBase.message_withtestDAO().getMark(from.asEntityBareJidIfPossible().toString());
                        getNotReaded=getNotReaded+1;
                        dataBase.message_withtestDAO().updateMark(getNotReaded, from.asEntityBareJidIfPossible().toString());
                        }
                    else if (sharedPreferences.getInt("activityState", 0) == 1777) {
                        notification(message.getBody(), from.toString());
                        String nick = dataBase.message_withtestDAO().getNick(from.toString());
                        notification(message.getBody(), nick);
                        dataBase.message_withtestDAO().updateMark(2, nick);
                    }
                }
            };


            ChatStateManager.getInstance(App.getComponent().userauth().getS()).addChatStateListener(new ChatStateListener() {
                @Override
                public void stateChanged(Chat chat, ChatState state, Message message) {
                   Log.d("ChatState ", ""+chat.toString());
                   Log.d("ChatState ", ""+chat.getXmppAddressOfChatPartner());
                   Log.d("ChatStateState ", ""+state.name());
                   if (state.name().contains(ChatState.composing.name())) {
                       dataBase.message_withtestDAO().updateTyping(1, chat.getXmppAddressOfChatPartner().asEntityBareJidString());
                   }
                   else if (state.name().contains(ChatState.gone.toString())){
                       dataBase.message_withtestDAO().updateTyping(0, chat.getXmppAddressOfChatPartner().asEntityBareJidString());
                   }
                }
            });
            PubSubManager manager=App.getComponent().userauth().getPubSubManager();
            ConfigureForm form = new ConfigureForm(DataForm.Type.submit);

Node node2=null;
            try {


                try {
                    node2 =  manager.getNode(App.getComponent().userauth().getS().getUser().asEntityBareJidString());
                } catch (PubSubException.NotAPubSubNodeException e) {
                    e.printStackTrace();
                }
                if (node2==null) {
                    node2 = (LeafNode) manager.createNode(App.getComponent().userauth().getS().getUser().asEntityBareJidString(), form);
    }
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                try {
                    node2 = (LeafNode) manager.createNode(App.getComponent().userauth().getS().getUser().asEntityBareJidString(), form);
                } catch (SmackException.NoResponseException noResponseException) {
                    noResponseException.printStackTrace();
                } catch (XMPPException.XMPPErrorException xmppErrorException) {
                    xmppErrorException.printStackTrace();
                } catch (SmackException.NotConnectedException notConnectedException) {
                    notConnectedException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



               // LeafNode leaf = (LeafNode) manager.createNode("avatar", form);
                   for (int i=0; i<contact_users.size(); i++)
                   {

                       try {

                           LeafNode node1 = null;

                           node1 = (LeafNode) manager.getNode(contact_users.get(i).mwith1);

                           if (node1!=null) {
                               node1.addItemEventListener(new ItemEventListener() {
                                   @Override
                                   public void handlePublishedItems(ItemPublishEvent items) {
                                       Log.d("Pubsub ", "" + items.getItems().size());
                                       for (int i = 0; i < items.getItems().size(); i++) {
                                           Log.d("PubsubItem ", "" + items.getItems().get(i).toString());
                                           PayloadItem item= (PayloadItem) items.getItems().get(i);
                                           String id=item.getId();
                                           try {
                                            XmlPullParser xmlPullParser=PacketParserUtils.getParserFor(item.getPayload().toXML("https://myproject").toString());
                                             Log.d("PullParser ", ""+xmlPullParser.nextTag());
                                               String path="http://192.168.0.27/avatars"+ "/" +id+"/"+ xmlPullParser.nextText();
                                               dataBase.message_withtestDAO().updateAvatar(path, id);


                                           } catch (XmlPullParserException e) {
                                               e.printStackTrace();
                                           } catch (IOException exception) {
                                               exception.printStackTrace();
                                           }


                                       }
                                   }
                               });

                               node1.subscribe(App.getComponent().userauth().getS().getUser());
                           }
                       } catch (SmackException.NoResponseException e) {
                           e.printStackTrace();
                       } catch (XMPPException.XMPPErrorException e) {
                           e.printStackTrace();
                       } catch (SmackException.NotConnectedException e) {
                           e.printStackTrace();
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }catch (PubSubException.NotAPubSubNodeException e) {
                           e.printStackTrace();
                       }
                   }


            ChatManager.getInstanceFor(App.getComponent().userauth().getS()).addIncomingListener(incomingChatMessageListener);
            if (App.getComponent().userauth().getS().isConnected() && App.getComponent().userauth().getS().isAuthenticated()) {


                try {
                    ((XMPPTCPConnection)App.getComponent().userauth().getS()).addStanzaIdAcknowledgedListener("", new StanzaListener() {
                        @Override
                        public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                            Log.d("ackStanza", "" + packet.toXML(""));
                            if (App.getComponent().userauth().getS().isConnected()) {
                                org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message) packet;

                            }
                        }
                    });
                } catch (StreamManagementException.StreamManagementNotEnabledException e) {
                    e.printStackTrace();
                }
            }

            stanzaListener = new StanzaListener() {
                @Override
                public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                    Log.d("hasExtension", "" + packet.toXML());
                }
            };
            stanzaFilter = new StanzaFilter() {
                @Override
                public boolean accept(Stanza stanza) {
                    Log.d("hasExtension", "" + stanza.toXML());
                    Log.d("hasExtension", "" + stanza.hasExtension("vcard-temp:x:update"));

                    if (stanza.getExtension("urn:xmpp:mam:tmp") != null) {
                        Message messagek = (org.jivesoftware.smack.packet.Message) stanza;
                        if (messagek.getType().toString().matches("groupchat") && !messagek.hasExtension("x", "jabber:x:oob")) {
                            Long idstamp = Long.parseLong("0");
                            StandardExtensionElement eventElement1;
                            eventElement1 = messagek.getExtension("archived", "urn:xmpp:mam:tmp");
                            idstamp = (Long.parseLong(eventElement1.getAttributeValue("id")) / 1000);

                            if (!((messagek.getFrom().getResourceOrThrow().toString()).matches(App.getComponent().database().myLoginsDao().my_user_name()))) {
                                  updateChatDB(messagek.getBody(), messagek.getFrom().getResourceOrNull().toString(), idstamp.toString(), messagek.getStanzaId(), messagek.getFrom().asEntityBareJidIfPossible().toString(), null, 110);
                                  dataBase.message_withtestDAO().updateMark(1, messagek.getFrom().asEntityBareJidIfPossible().toString());
                                  dataBase.message_withtestDAO().updateDBUser(idstamp.toString(), messagek.getFrom().asEntityBareJidIfPossible().toString());
                                if (sharedPreferences.getInt("activityState", 0) == 1677) {
                                    notification(messagek.getBody(), messagek.getFrom().getLocalpartOrThrow().toString());
                                    dataBase.message_withtestDAO().updateMark(1, messagek.getFrom().asEntityBareJidIfPossible().toString());
                                } else if (sharedPreferences.getInt("activityState", 0) == 1675) {
                                    dataBase.message_withtestDAO().updateMark(2, messagek.getFrom().asEntityBareJidIfPossible().toString());
                                } else if (sharedPreferences.getInt("activityState", 0) == 1777) {
                                    notification(messagek.getBody(), messagek.getFrom().getLocalpartOrThrow().toString());
                                    dataBase.message_withtestDAO().updateMark(2, messagek.getFrom().asEntityBareJidIfPossible().toString());
                                }
                            }
                        }

                        else if (messagek.getType().toString().matches("groupchat") && !messagek.getFrom().getResourceOrEmpty().toString().contains(App.getComponent().database().myLoginsDao().my_user_name()) && messagek.hasExtension("x", "jabber:x:oob") && messagek.getBody("tag").matches("hey")) {

                            eventElement2 = messagek.getExtension("x", "jabber:x:oob");
                            Long idstamp = Long.parseLong("0");

                            StandardExtensionElement   eventElement1 = messagek.getExtension("archived", "urn:xmpp:mam:tmp");
                            idstamp = (Long.parseLong(eventElement1.getAttributeValue("id")) / 1000);
                            String fileName = eventElement2.getFirstElement("url", "jabber:x:oob").getText();

                            downloadFile("http://192.168.0.27/uploads/"+messagek.getFrom().asEntityBareJidIfPossible().toString(), fileName);
                            Log.d("GroupPic", ""+messagek.getFrom().asEntityBareJidIfPossible().toString());
                            if (!((messagek.getFrom().getResourceOrThrow().toString()).matches(App.getComponent().database().myLoginsDao().my_user_name()))) {

                                updateChatDB(messagek.getBody("tag"), messagek.getFrom().getResourceOrThrow().toString(), idstamp.toString(), messagek.getStanzaId(), messagek.getFrom().asEntityBareJidIfPossible().toString(), Environment.getExternalStorageDirectory() + File.separator + "mfchat" + File.separator + fileName, 110);
                                dataBase.message_withtestDAO().updateMark(1, messagek.getFrom().asEntityBareJidIfPossible().toString());
                                dataBase.message_withtestDAO().updateDBUser(idstamp.toString(), messagek.getFrom().asEntityBareJidIfPossible().toString());

                                if (sharedPreferences.getInt("activityState", 0) == 1677) {
                                    dataBase.message_withtestDAO().updateMark(1, messagek.getFrom().asEntityBareJidIfPossible().toString());
                                    notification("Picture", messagek.getFrom().getLocalpartOrThrow().toString());
                                } else if (sharedPreferences.getInt("activityState", 0) == 1675) {
                                    dataBase.message_withtestDAO().updateMark(2, messagek.getFrom().asEntityBareJidIfPossible().toString());
                                } else if (sharedPreferences.getInt("activityState", 0) == 1777) {
                                    notification("Picture", messagek.getFrom().getLocalpartOrThrow().toString());
                                    dataBase.message_withtestDAO().updateMark(2, messagek.getFrom().asEntityBareJidIfPossible().toString());
                                }
                            }
                        }
                    }

                    return true;
                }
            };

            App.getComponent().userauth().getS().addAsyncStanzaListener(stanzaListener, stanzaFilter);
            String timestamp = App.getComponent().database().mesagechatest_Dao().timestamp();
            checknewMessage(timestamp);
            checknewMessageMuc(timestamp);


            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }



    }

    public void downloadFile(String _url, String fileName) {
        try {
            int responceCode = -1;
            InputStream in;
            Bitmap bitmap = null;
            String str=new String(_url+"/"+fileName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                url = new URL(new String(str.getBytes(StandardCharsets.UTF_8)));
            }

            urlConnection1 = (HttpURLConnection) url.openConnection();
            urlConnection1.setDoOutput(true);
            urlConnection1.setDoInput(true);
            responceCode = urlConnection1.getResponseCode();
            Log.d("ResponceCode", ""+responceCode);
            Log.d("ResponceCode", ""+str);
            if (responceCode == HttpURLConnection.HTTP_OK) {


                File folder = new File(Environment.getExternalStorageDirectory() , "mfchat");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }

                String path = folder + File.separator + fileName;

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection1.getInputStream()));
                InputStream inputStream = urlConnection1.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(path);
                int bytesRead = -1;
                byte[] buffer = new byte[1024];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                fileOutputStream.close();
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checknewMessageMuc(String _timestamp){
Log.d("MuscTimestamp ", ""+_timestamp);

            myjid = App.getComponent().database().myLoginsDao().my_user_name();
            MultiUserChatManager multiUserChatManager1 = MultiUserChatManager.getInstanceFor(App.getComponent().userauth().getS());
            String timestamp = _timestamp;
            mucName.clear();
            mucName = App.getComponent().database().message_withtestDAO().getMucs();
            String jidOfSender=null;

            for(int t=0;t<mucName.size();t++) {
                try{
                    MultiUserChat mucchat = null;
                    try {
                        mucchat = multiUserChatManager1.getMultiUserChat(JidCreate.entityBareFrom(mucName.get(t).mwith1));
                    } catch (XmppStringprepException e1) {
                        e1.printStackTrace();
                    }

                    mamManager1=MamManager.getInstanceFor(mucchat);
                    MamManager.MamQuery mamQuery1;
                    mamQuery1 = mamManager1.queryArchive(MamManager.MamQueryArgs.builder().limitResultsSince(new Date(Long.parseLong(timestamp))).build());

                    for(int h=0;h<mamQuery1.getMessages().size(); h++) {
                        jidOfSender=mamQuery1.getMessages().get(h).getFrom().getResourceOrThrow().toString();//+"@192.168.0.26";
                        StandardExtensionElement eventElement1;
                        Long idstamp = null;
                        eventElement1 = mamQuery1.getMessages().get(h).getExtension("archived", "urn:xmpp:mam:tmp");

                        if (mamQuery1.getMessages().get(h).getExtension("urn:xmpp:mam:tmp") != null && !mamQuery1.getMessages().get(h).hasExtension("x", "jabber:x:oob")) {
                            idstamp = (Long.parseLong(eventElement1.getAttributeValue("id")) / 1000);
                        }
                        if (mamQuery1.getMessages().get(h).getBody()!=null && App.getComponent().database().mesagechatest_Dao().isReaded(mamQuery1.getMessages().get(h).getBody(),
                                mamQuery1.getMessages().get(h).getStanzaId())==null && !mamQuery1.getMessages().get(h).hasExtension("x", "jabber:x:oob"))
                        {
                            if (!App.getComponent().userauth().getS().getUser().getLocalpartOrNull().toString().matches(mamQuery1.getMessages().get(h).getFrom().getLocalpartOrNull().toString()))
                            {
                                StandardExtensionElement eventElement11;
                                eventElement11 = mamQuery1.getMessages().get(h).getExtension("archived", "urn:xmpp:mam:tmp");
                                if (mamQuery1.getMessages().get(h).getExtension("urn:xmpp:mam:tmp") != null)
                                {


                                        updateChatDB(mamQuery1.getMessages().get(h).getBody(), mamQuery1.getMessages().get(h).getFrom().getResourceOrNull().toString(), idstamp.toString(), mamQuery1.getMessages().get(h).getStanzaId(), mamQuery1.getMessages().get(h).getFrom().asEntityBareJidIfPossible().toString(), null, 110);
                                        dataBase.message_withtestDAO().updateMark(1, mamQuery1.getMessages().get(h).getFrom().asEntityBareJidIfPossible().toString());
                                        dataBase.message_withtestDAO().updateDBUser(idstamp.toString(), mamQuery1.getMessages().get(h).getFrom().asEntityBareJidIfPossible().toString());
                                        if (sharedPreferences.getInt("activityState", 0) == 1677) {
                                            notification(mamQuery1.getMessages().get(h).getBody(), mamQuery1.getMessages().get(h).getFrom().getLocalpartOrThrow().toString());
                                            dataBase.message_withtestDAO().updateMark(1, mamQuery1.getMessages().get(h).getFrom().asEntityBareJidIfPossible().toString());
                                        } else if (sharedPreferences.getInt("activityState", 0) == 1675) {
                                            dataBase.message_withtestDAO().updateMark(2, mamQuery1.getMessages().get(h).getFrom().asEntityBareJidIfPossible().toString());
                                        } else if (sharedPreferences.getInt("activityState", 0) == 1777) {
                                            notification(mamQuery1.getMessages().get(h).getBody(), mamQuery1.getMessages().get(h).getFrom().getLocalpartOrThrow().toString());
                                            dataBase.message_withtestDAO().updateMark(2, mamQuery1.getMessages().get(h).getFrom().asEntityBareJidIfPossible().toString());
                                        }

                                }
                            }
                        }

                        else if (mamQuery1.getMessages().get(h).hasExtension("x", "jabber:x:oob") && mamQuery1.getMessages().get(h).getBody("tag").matches("hey")
                                && App.getComponent().database().mesagechatest_Dao().isReaded(mamQuery1.getMessages().get(h).getBody("tag"), mamQuery1.getMessages().get(h).getStanzaId())==null)
                        {



                            if (!App.getComponent().userauth().getS().getUser().getLocalpartOrNull().toString().matches(jidOfSender)) {



                                //------------------------------------------
                                Long idstamp1 = Long.parseLong("0");
                                StandardExtensionElement eventElement11, eventElement2;
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                                eventElement11 = mamQuery1.getMessages().get(h).getExtension("archived", "urn:xmpp:mam:tmp");
                                eventElement2 = mamQuery1.getMessages().get(h).getExtension("x", "jabber:x:oob");

                                if (mamQuery1.getMessages().get(h).getExtension("urn:xmpp:mam:tmp") != null)
                                {
                                    idstamp1 = (Long.parseLong(eventElement11.getAttributeValue("id")) / 1000);
                                }
                                Log.d("ImageGroup", ""+mamQuery1.getMessages().get(h).toXML());
                                Log.d("ImageGroup", ""+idstamp1);
                                String fileName=eventElement2.getFirstElement("url", "jabber:x:oob").getText();
                                downloadFile("http://192.168.0.27/uploads/"+mamQuery1.getMessages().get(h).getFrom().asEntityBareJidIfPossible().toString(), fileName);

                                updateChatDB(mamQuery1.getMessages().get(h).getBody("tag"), mamQuery1.getMessages().get(h).getFrom().getResourceOrThrow().toString(), idstamp1.toString(), mamQuery1.getMessages().get(h).getStanzaId(), mamQuery1.getMessages().get(h).getFrom().asEntityBareJidIfPossible().toString(), Environment.getExternalStorageDirectory() + File.separator + "mfchat" + File.separator + fileName, 110);
                                dataBase.message_withtestDAO().updateMark(1, mamQuery1.getMessages().get(h).getFrom().asEntityBareJidIfPossible().toString());
                                dataBase.message_withtestDAO().updateDBUser(idstamp1.toString(), mamQuery1.getMessages().get(h).getFrom().asEntityBareJidIfPossible().toString());

                                if (sharedPreferences.getInt("activityState", 0) == 1677) {
                                    dataBase.message_withtestDAO().updateMark(1, mamQuery1.getMessages().get(h).getFrom().asEntityBareJidIfPossible().toString());
                                    notification("Picture", mamQuery1.getMessages().get(h).getFrom().getLocalpartOrThrow().toString());
                                } else if (sharedPreferences.getInt("activityState", 0) == 1675) {
                                    dataBase.message_withtestDAO().updateMark(2, mamQuery1.getMessages().get(h).getFrom().asEntityBareJidIfPossible().toString());
                                } else if (sharedPreferences.getInt("activityState", 0) == 1777) {
                                    notification("Picture", mamQuery1.getMessages().get(h).getFrom().getLocalpartOrThrow().toString());
                                    dataBase.message_withtestDAO().updateMark(2, mamQuery1.getMessages().get(h).getFrom().asEntityBareJidIfPossible().toString());
                                }

                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


    }
        public void checknewMessage(String _timestamp) {

            Log.d("MuscTimestamp ", "" + _timestamp);
            String lasttimestamp;
            myjid = App.getComponent().database().myLoginsDao().my_user_name();
            if (_timestamp == null) {
                lasttimestamp = "0";
            } else {
                lasttimestamp = _timestamp;
            }

            mamManager = MamManager.getInstanceFor(App.getComponent().userauth().getS());

            //if (App.getComponent().database().mesagechatest_Dao().timestamp() != null) {
            MamManager.MamQuery mamQuery = null;
            try {
                mamQuery = mamManager.queryArchive(MamManager.MamQueryArgs.builder().limitResultsSince(new Date(Long.parseLong(lasttimestamp))).build());
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
if(mamQuery!=null){
            if (mamQuery.getMessages().size() > 0) {
                Log.d("mamQuerySize ", "" + mamQuery.getMessages().size());


                for (int i = 0; i < mamQuery.getMessages().size(); i++) {
                    if (!App.getComponent().userauth().getS().getUser().asEntityBareJidIfPossible().toString().matches(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString())) {
                        MUCUser eventElem = mamQuery.getMessages().get(i).getExtension("x", "http://jabber.org/protocol/muc#user");

                        StandardExtensionElement eventElement1;
                        eventElement1 = mamQuery.getMessages().get(i).getExtension("archived", "urn:xmpp:mam:tmp");
                        Long idstamp = (Long.parseLong(eventElement1.getAttributeValue("id")) / 1000);
                        if (mamQuery.getMessages().get(i).getBody() == null && eventElem != null
                                && eventElem.getInvite().getElementName().matches("invite")) {
                            if (App.getComponent().database().message_withtestDAO().checkUser(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString()) == null) {

                                updateDBWith(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString(), eventElem.getInvite().getReason().substring(eventElem.getInvite().getReason().lastIndexOf("&") + 1), "group", "offline", 2, String.valueOf(lasttimestamp), 0);
                                dataBase.message_withtestDAO().updateDBUser(idstamp.toString(), mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());
                                try {
                                    MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(App.getComponent().userauth().getS());

                                    MultiUserChat room = multiUserChatManager.getMultiUserChat(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible());
                                    MucEnterConfiguration.Builder build = room.getEnterConfigurationBuilder(Resourcepart.from(App.getComponent().userauth().getS().getUser().asEntityBareJidString()));

                                    build.requestMaxStanzasHistory(0);
                                    build.requestMaxCharsHistory(0);

                                    MucEnterConfiguration muconfig = build.build();
                                    room.join(muconfig);
                                } catch (MultiUserChatException.NotAMucServiceException e) {
                                    e.printStackTrace();
                                } catch (SmackException.NoResponseException e) {
                                    e.printStackTrace();
                                } catch (XMPPException.XMPPErrorException e) {
                                    e.printStackTrace();
                                } catch (SmackException.NotConnectedException e) {
                                    e.printStackTrace();
                                } catch (XmppStringprepException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(App.getComponent().userauth().getS());

                                    MultiUserChat room = multiUserChatManager.getMultiUserChat(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible());
                                    MucEnterConfiguration.Builder build = room.getEnterConfigurationBuilder(Resourcepart.from(App.getComponent().userauth().getS().getUser().asEntityBareJidString()));

                                    build.requestMaxStanzasHistory(0);
                                    build.requestMaxCharsHistory(0);

                                    MucEnterConfiguration muconfig = build.build();
                                    room.join(muconfig);
                                } catch (MultiUserChatException.NotAMucServiceException e) {
                                    e.printStackTrace();
                                } catch (SmackException.NoResponseException e) {
                                    e.printStackTrace();
                                } catch (XMPPException.XMPPErrorException e) {
                                    e.printStackTrace();
                                } catch (SmackException.NotConnectedException e) {
                                    e.printStackTrace();
                                } catch (XmppStringprepException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (mamQuery.getMessages().get(i).getBody() != null && App.getComponent().database().mesagechatest_Dao().isReaded(mamQuery.getMessages().get(i).getBody(),
                                mamQuery.getMessages().get(i).getStanzaId()) == null && !mamQuery.getMessages().get(i).hasExtension("x", "jabber:x:oob")) {
                            StandardExtensionElement eventElement11;
                            eventElement11 = mamQuery.getMessages().get(i).getExtension("archived", "urn:xmpp:mam:tmp");

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                            if (mamQuery.getMessages().get(i).getExtension("urn:xmpp:mam:tmp") != null) {
                                Long idstamp1 = (Long.parseLong(eventElement11.getAttributeValue("id")) / 1000);
                                if (App.getComponent().database().message_withtestDAO().checkUser(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString()) == null) {
                                    VCard card = null;
                                    try {
                                        card = VCardManager.getInstanceFor(App.getComponent().userauth().getS()).loadVCard(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible());
                                    } catch (SmackException.NoResponseException e) {
                                        e.printStackTrace();
                                    } catch (XMPPException.XMPPErrorException e) {
                                        e.printStackTrace();
                                    } catch (SmackException.NotConnectedException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (card != null) {
                                        updateDBWith(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString(), card.getNickName(), "chat", "offline", 2, String.valueOf(idstamp1), 0);
                                        updateChatDB(mamQuery.getMessages().get(i).getBody(), mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString(), idstamp1.toString(), mamQuery.getMessages().get(i).getStanzaId(), App.getComponent().userauth().getS().getUser().asEntityBareJidString(), null, 110);
                                        dataBase.message_withtestDAO().updateDBUser(idstamp1.toString(), mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());
                                    }

                                } else {
                                    Log.d("mamQuery ", "" + mamQuery.getMessages().get(i).toXML());
                                    updateChatDB(mamQuery.getMessages().get(i).getBody(), mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString(), idstamp.toString(), mamQuery.getMessages().get(i).getStanzaId(), App.getComponent().userauth().getS().getUser().asEntityBareJidString(), null, 110);
                                    dataBase.message_withtestDAO().updateDBUser(idstamp.toString(), mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());
                                }
                                if (sharedPreferences.getInt("activityState", 0) == 1677) {
                                    String nick = dataBase.message_withtestDAO().getNick(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());
                                    notification(mamQuery.getMessages().get(i).getBody(), nick);

                                    dataBase.message_withtestDAO().updateMark(2, mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());
                                } else if (sharedPreferences.getInt("activityState", 0) == 1675) {
                                    dataBase.message_withtestDAO().updateMark(2, mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());
                                } else if (sharedPreferences.getInt("activityState", 0) == 1777) {
                                    notification(mamQuery.getMessages().get(i).getBody(), mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());
                                    String nick = dataBase.message_withtestDAO().getNick(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());
                                    notification(mamQuery.getMessages().get(i).getBody(), nick);
                                    dataBase.message_withtestDAO().updateMark(2, nick);
                                }

                            }
                        }
                        if (mamQuery.getMessages().get(i).hasExtension("x", "jabber:x:oob") && mamQuery.getMessages().get(i).getBody("tag").matches("hey")
                                && App.getComponent().database().mesagechatest_Dao().isReaded(mamQuery.getMessages().get(i).getBody("tag"), mamQuery.getMessages().get(i).setStanzaId()) == null) {

                            StandardExtensionElement eventElement2 = mamQuery.getMessages().get(i).getExtension("x", "jabber:x:oob");
                            if (mamQuery.getMessages().get(i).getExtension("urn:xmpp:mam:tmp") != null) {
                                ///-----------------------------------
                                eventElement2 = mamQuery.getMessages().get(i).getExtension("x", "jabber:x:oob");
                                String fileName = eventElement2.getFirstElement("url", "jabber:x:oob").getText();

                                downloadFile("http://192.168.0.27/uploads/" + mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString(), fileName);
                                Log.d("getFrom ", "" + mamQuery.getMessages().get(i).getFrom().toString());
                                Log.d("check ", "" + dataBase.message_withtestDAO().checkUser(mamQuery.getMessages().get(i).getFrom().toString()));
                                Log.d("getFrom ", "" + mamQuery.getMessages().get(i).toXML());

                                if (dataBase.message_withtestDAO().checkUser(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString()) == null) {
                                    try {
                                        VCard card = VCardManager.getInstanceFor(App.getComponent().userauth().getS()).loadVCard(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible());
                                        if (card != null) {
                                            updateDBWith(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString(), card.getNickName(), "chat", "offline", 2, String.valueOf(idstamp), 0);
                                            updateChatDB(mamQuery.getMessages().get(i).getBody("tag"), mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString(), idstamp.toString(), mamQuery.getMessages().get(i).getStanzaId(), App.getComponent().userauth().getS().getUser().asEntityBareJidString(), Environment.getExternalStorageDirectory() + File.separator + "mfchat" + File.separator + fileName, 110);
                                            dataBase.message_withtestDAO().updateDBUser(idstamp.toString(), mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());

                                            try {
                                                roster.createEntry(JidCreate.bareFrom(mamQuery.getMessages().get(i).getFrom().toString()), card.getNickName(), new String[]{"friend of" + App.getComponent().userauth().getS().getUser().getLocalpartOrThrow()});

                                                Presence presence2 = new Presence(Presence.Type.subscribe);
                                                presence2.setStatus("Online, Programmatically!");
                                                presence2.setPriority(24);
                                                presence2.setMode(Presence.Mode.available);
                                                App.getComponent().userauth().getS().sendStanza(presence2);

                                                if (roster.getPresence(JidCreate.bareFrom(mamQuery.getMessages().get(i).getFrom().toString())).getType().toString().matches("available")) {
                                                    dataBase.message_withtestDAO().updateStatus("online", mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());
                                                } else {
                                                    dataBase.message_withtestDAO().updateStatus("offline", mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());
                                                }
                                            } catch (XmppStringprepException e) {
                                                e.printStackTrace();
                                            } catch (SmackException.NotConnectedException e) {
                                                e.printStackTrace();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            } catch (SmackException.NotLoggedInException e) {
                                                e.printStackTrace();
                                            } catch (SmackException.NoResponseException e) {
                                                e.printStackTrace();
                                            } catch (XMPPException.XMPPErrorException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (SmackException.NoResponseException e) {
                                        e.printStackTrace();
                                    } catch (XMPPException.XMPPErrorException e) {
                                        e.printStackTrace();
                                    } catch (SmackException.NotConnectedException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    Log.d("mamQuery ", "" + mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());
                                    Log.d("mamQuery ", "" + fileName);
                                    Log.d("mamQuery ", "" + idstamp.toString());
                                    Log.d("mamQuery ", "" + mamQuery.getMessages().get(i).getStanzaId());
                                    String nick = App.getComponent().database().message_withtestDAO().getNick(mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());
                                    updateChatDB(mamQuery.getMessages().get(i).getBody("tag"), mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString(), idstamp.toString(), mamQuery.getMessages().get(i).getStanzaId(), App.getComponent().userauth().getS().getUser().asEntityBareJidString(), Environment.getExternalStorageDirectory() + File.separator + "mfchat" + File.separator + fileName, 110);
                                    dataBase.message_withtestDAO().updateDBUser(idstamp.toString(), mamQuery.getMessages().get(i).getFrom().asEntityBareJidIfPossible().toString());
                                }
                                if (sharedPreferences.getInt("activityState", 0) == 1677) {
                                    String nick = dataBase.message_withtestDAO().getNick(mamQuery.getMessages().get(i).getFrom().toString());
                                    notification(mamQuery.getMessages().get(i).getBody(), nick);
                                    dataBase.message_withtestDAO().updateMark(1, mamQuery.getMessages().get(i).getFrom().toString());
                                } else if (sharedPreferences.getInt("activityState", 0) == 1675) {
                                    dataBase.message_withtestDAO().updateMark(2, mamQuery.getMessages().get(i).getFrom().toString());
                                } else if (sharedPreferences.getInt("activityState", 0) == 1777) {
                                    String nick = dataBase.message_withtestDAO().getNick(mamQuery.getMessages().get(i).getFrom().toString());
                                    notification("Picture", nick);
                                    dataBase.message_withtestDAO().updateMark(2, mamQuery.getMessages().get(i).getFrom().toString());
                                }
                            }
                            //------------------------------------
                        }

                    }

                }
            }
        }

            //}
        }

        public void subscribeRoom(final String nick, final String nameofgroup) {

            iq = new IQ("", "") {
                @Override
                protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
                    xml.setEmptyElement();
                    xml.append("subscribe xmlns='urn:xmpp:mucsub:0'\n" +
                            "             nick='" + nick + "'\n" +
                            "             password=''>\n" +
                            "    <event node='urn:xmpp:mucsub:nodes:messages' />\n" +
                            "    <event node='urn:xmpp:mucsub:nodes:affiliations' />\n" +
                            "    <event node='urn:xmpp:mucsub:nodes:subject' />\n" +
                            "    <event node='urn:xmpp:mucsub:nodes:config' />\n" +
                            "  </subscribe>");
                    Log.d("nick", "" + nameofgroup);
                    return xml;
                }
            };
            iq.setType(IQ.Type.set);
            iq.setFrom(jid1);

            try {
                iq.setTo(JidCreate.entityBareFrom(nameofgroup + "@conference.192.168.0.26"));
                App.getComponent().userauth().getS().sendStanza(iq);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }




    public void updateChatDB(String _body, String _from, String _timestamp, String _idstanza, String _to, String _picture, int _mark) {
        message_chattest_17 = new message_chattest17();
        message_chattest_17.body1 = _body;
        message_chattest_17.from1 = _from;
        message_chattest_17.to1 = _to;
        message_chattest_17.timestampid = _timestamp;
        message_chattest_17.idstanza1 = _idstanza;
        message_chattest_17.picture1 = _picture;
        message_chattest_17.mark = _mark;

        dataBase.mesagechatest_Dao().insert(message_chattest_17);
    }

    public void updateDBWith(String _mwith, String _nick, String _type, String _status, Integer _mark, String _mtime, int _typing) {
        message_withtest_17 = new message_withtest17();
        message_withtest_17.mwith1 = _mwith;
        message_withtest_17.nick1 = _nick;
        message_withtest_17.type = _type;
        message_withtest_17.status=_status;
        message_withtest_17.mark = _mark;
        message_withtest_17.mtime1 = _mtime;
        message_withtest_17.typing = _typing;
        dataBase.message_withtestDAO().insert(message_withtest_17);
    }



    public void notification(String body, String from) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Intent resultIntent = new Intent(ServiceChat.this, MainActivity.class);
            resultIntent.putExtra("userjid", "User1");
            PendingIntent pendingIntent = PendingIntent.getActivity(ServiceChat.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            String name = "chanel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String CHANNEL_ID = from;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(CHANNEL_ID);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification.Builder builder = new Notification.Builder(ServiceChat.this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(from)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID)
                    .setContentIntent(pendingIntent);
            Notification notification1 = builder.build();
            notificationManager.notify(3, notification1);

        } else if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Intent resultIntent = new Intent(ServiceChat.this, MainActivity.class);
            resultIntent.putExtra("userjid", "User1");
            PendingIntent pendingIntent = PendingIntent.getActivity(ServiceChat.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(ServiceChat.this, from)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(from)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            Notification notification2 = builder.build();
            notificationManager.notify(3, notification2);
            r.play();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        stopSelf(IdServ);
        Log.d("onCreateDestroy", "onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("onTaskRemovedServ", "onTaskRemovedServ");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            MessageReceiver myres = new MessageReceiver();
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("startSchedule", "start");
            myres.onReceive(ServiceChat.this, broadcastIntent);
            //task.cancel(false);
            //s.disconnect();
        } else {
            MessageReceiver myres = new MessageReceiver();
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("startSchedule", "start");
            myres.onReceive(ServiceChat.this, broadcastIntent);

            //task.cancel(true);
            // s.disconnect();
        }

    }

}

 /*
                pingManager.registerPingFailedListener(() -> {
                    Log.d("PingFailed", "pingFailed");
                    s.disconnect();
                    try {
                        s.connect();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                chatManager = ChatManager.getInstanceFor(s);
                SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
                SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
                XMPPTCPConnection.setUseStreamManagementDefault(true);
                XMPPTCPConnection.setUseStreamManagementResumptionDefault(true);
*/

               /* ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(s);
                reconnectionManager.setFixedDelay(10);
                reconnectionManager.enableAutomaticReconnection();
                reconnectionManager.addReconnectionListener(new ReconnectionListener() {
                    @Override
                    public void reconnectingIn(int seconds) {
                        Log.d("reconnectingIn", "" + seconds);
                    }

                    @Override
                    public void reconnectionFailed(Exception e) {
                        Log.d("reconnectionFailed", "" + e.getMessage().toString());
                    }
                });*/


   /*  if (android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.O && isActive==2) {

                                            Intent resultIntent=new Intent(ServiceChat.this, MainActivity.class);
                                            resultIntent.putExtra("userjid", "User1");
                                            PendingIntent pendingIntent=PendingIntent.getActivity(ServiceChat.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                                            NotificationManager notificationManager1=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                            NotificationCompat.Builder builder=new NotificationCompat.Builder(ServiceChat.this, "channel_id")
                                                    .setSmallIcon(R.mipmap.ic_launcher)
                                                    .setContentTitle("Picture")
                                                    //.setContentText(message.getBody())
                                                    .setAutoCancel(true)
                                                    .setContentIntent(pendingIntent);

                                            Notification notification1=  builder.build();
                                            notificationManager1.notify(3, notification1);
                                            r.play();

                                        } else if (android.os.Build.VERSION.SDK_INT< Build.VERSION_CODES.O && isActive==2){
                                            Intent resultIntent=new Intent(ServiceChat.this, MainActivity.class);
                                            resultIntent.putExtra("userjid", "User1");
                                            PendingIntent pendingIntent=PendingIntent.getActivity(ServiceChat.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                            NotificationManager notificationManager2=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                            NotificationCompat.Builder builder=new NotificationCompat.Builder(ServiceChat.this)
                                                    .setSmallIcon(R.mipmap.ic_launcher)
                                                    .setContentTitle("Picture")
                                                    //.setContentText(message.getBody())
                                                    .setAutoCancel(true)
                                                    .setContentIntent(pendingIntent);

                                            Notification notification2=  builder.build();
                                            notificationManager2.notify(3, notification2);
                                            r.play();
                                        }*/
