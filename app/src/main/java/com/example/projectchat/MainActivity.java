package com.example.projectchat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.example.projectchat.News.NewsActivity;
import com.example.projectchat.RoomDB.DataBase;
import com.example.projectchat.RoomDB.message_withtest17;
import com.example.projectchat.loadAvatar.Avatar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MucEnterConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity  {


    boolean bound = false;
    int flag1 = 0;
    int isActive = 0;
    Form answerForm;
    MyLoginTask task;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static String[] PERMISSIONS_USESIP = {
            Manifest.permission.USE_SIP
    };
    AutoCompleteTextView autoCompleteTextView;
    SearchView searchView;
    DataBase dataBase;
    DomainBareJid domainBareJid;
    ReportedData reportedData;
    ArrayAdapter<String> adapter;
    List<message_withtest17> contact_users;
    EntityFullJid jid1;
    RecyclerView recyclerView;
    contactlistAdapter contactlistAdapter;
    message_withtest17 message_withtest_17;
    IQ iq;
    String mynick;
    InvitationListener invitationListener;


    SharedPreferences sPref;
    SharedPreferences.Editor editor;
    Flowable<List<message_withtest17>> flowable;
    Disposable disposable;
    String login1;
    int sizeDB = 0;
    List<ReportedData.Row> list;
    Form form;
    UserSearchManager userSearchManager;

    public static MyApp app = null;
    public static MyBroadcastReceiver receiver = null;
    public static IntentFilter intentFilter = null;
    Set<RosterEntry> entries;
    List<message_withtest17> list1;
    Roster roster;
    Toolbar toolbar;
   static String myToken;
   Socket clientSocket;


    public class MSG_TYPE
    {
        public final static int INCOMING_CALL = 1;
        public final static int CALL_STATE = 2;
        public final static int REG_STATE = 3;
        public final static int BUDDY_STATE = 4;
        public final static int CALL_MEDIA_STATE = 5;
        public final static int CHANGE_NETWORK = 6;
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        private String conn_name = "";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("IncomingCall")){
                Log.d("IncomingCall", "incomingCall");
                showCallActivity();
            }

            if (isNetworkChange(context)){
                app.handleNetworkChange();
            }
        }

        private boolean isNetworkChange(Context context) {
            boolean network_changed = false;
            ConnectivityManager connectivity_mgr =
                    ((ConnectivityManager)context.getSystemService(
                            Context.CONNECTIVITY_SERVICE));

            NetworkInfo net_info = connectivity_mgr.getActiveNetworkInfo();
            if(net_info != null && net_info.isConnectedOrConnecting() &&
                    !conn_name.equalsIgnoreCase(""))
            {
                String new_con = net_info.getExtraInfo();
                if (new_con != null && !new_con.equalsIgnoreCase(conn_name))
                    network_changed = true;

                conn_name = (new_con == null)?"":new_con;
            } else {

            }
            return network_changed;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> listPermissions=new ArrayList<>();
       if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_SIP) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission  ", "granted");
        } else {
           listPermissions.add(Manifest.permission.USE_SIP);
           // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_SIP}, 0);
            Log.d("Permission  not", "granted");
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission  ", "granted");
        } else {
            listPermissions.add(Manifest.permission.RECORD_AUDIO);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);

            Log.d("Permission  not", "granted");
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission  ", "granted");
        } else {
            listPermissions.add(Manifest.permission.CAMERA);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
            Log.d("Permission  not", "granted");
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission  ", "granted");
        } else {
            listPermissions.add(Manifest.permission.READ_PHONE_STATE);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
            Log.d("Permission  not", "granted");
        }

        for (int i=0; i<listPermissions.size(); i++){
            if (ContextCompat.checkSelfPermission(this, listPermissions.get(i)) == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission  ", "granted");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{listPermissions.get(i)}, 0);
                Log.d("Permission  not", "granted");
            }
        }

        verifyStoragePermissions(MainActivity.this);
        Log.d("onActivity", "onCreate");
        dataBase = App.getComponent().database();
        recyclerView = (RecyclerView) findViewById(R.id.recyclecontactlist);

        //startService(new Intent(this, MyServicePush.class));
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                sizeDB = dataBase.myLoginsDao().getAll().size();
                Log.d("databaseSize", "" + sizeDB);
                Log.d("databaseSize", "" + App.getComponent().database().myLoginsDao().my_user_name());
                if (sizeDB == 0 && App.getComponent().database().myLoginsDao().getAll().isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {

                    login1 = App.getComponent().database().myLoginsDao().my_user_name();
                    mynick = dataBase.myLoginsDao().my_nick();
                    contact_users = dataBase.message_withtestDAO().getAll();
                    Intent intent = new Intent(MainActivity.this, ServiceChat.class);
                    startService(intent);
                    Intent intent1 = new Intent(MainActivity.this, SipPhone.class);
                    startService(intent1);


                    roster=App.getComponent().userauth().getRoster();
                    if (roster!=null){
                        roster.addRosterLoadedListener(new RosterLoadedListener() {
                            @Override
                            public void onRosterLoaded(Roster roster) {
                                entries = roster.getEntries();

                                for (RosterEntry entry : entries) {
                                    Presence entryPresence = null;
                                    try {
                                        entryPresence = roster.getPresence(JidCreate.bareFrom(entry.getUser()));
                                    } catch (XmppStringprepException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onRosterLoadingFailed(Exception exception) {

                            }
                        });

                        roster.addRosterListener(new RosterListener() {
                            @Override
                            public void entriesAdded(Collection<Jid> addresses) {

                            }

                            @Override
                            public void entriesUpdated(Collection<Jid> addresses) {

                            }

                            @Override
                            public void entriesDeleted(Collection<Jid> addresses) {

                            }

                            @Override
                            public void presenceChanged(Presence presence) {
                                Log.d("Presence ", "" + presence.getType().toString());
                                if (presence.getType().toString().matches("available")) {
                                    String checkUser = dataBase.message_withtestDAO().checkUser(presence.getFrom().asEntityBareJidIfPossible().toString());

                                    if (checkUser != null) {
                                        Log.d("Presence ", "" + checkUser);
                                        dataBase.message_withtestDAO().updateStatus("online", checkUser);
                                    }
                                } else {
                                    String checkUser = dataBase.message_withtestDAO().checkUser(presence.getFrom().asEntityBareJidIfPossible().toString());

                                    if (checkUser != null) {
                                        Log.d("Presence ", "" + checkUser);
                                        dataBase.message_withtestDAO().updateStatus("offline", checkUser);
                                    }
                                }
                            }
                        });
                    }
                   /* FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            Log.d("MyToken", "" + instanceIdResult.getToken());
                            isTokenSet(instanceIdResult.getToken());
                        }
                    });*/





                    invitationListener=new InvitationListener() {
                    @Override
                    public void invitationReceived(XMPPConnection conn, MultiUserChat room, EntityJid inviter, String reason, String password, Message message, MUCUser.Invite invitation) {

                        StandardExtensionElement eventElement1;
                        eventElement1=message.getExtension("archived", "urn:xmpp:mam:tmp");
                        Long idstamp=(Long.parseLong(eventElement1.getAttributeValue("id"))/1000);
                        try {
                            MucEnterConfiguration.Builder build = room.getEnterConfigurationBuilder(Resourcepart.from(App.getComponent().userauth().getS().getUser().asEntityBareJidString()));
                            build.requestMaxStanzasHistory(0);
                            build.requestMaxCharsHistory(0);
                            MucEnterConfiguration muconfig=build.build();
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
                        message_withtest_17 = new message_withtest17();
                        message_withtest_17.mwith1 = room.getRoom().asEntityBareJidString();
                        message_withtest_17.nick1 = reason.substring(reason.lastIndexOf("&") + 1);
                        message_withtest_17.type = "group";
                        message_withtest_17.mark = 1;
                        dataBase.message_withtestDAO().insert(message_withtest_17);
                    }
                };
                MultiUserChatManager.getInstanceFor(App.getComponent().userauth().getS()).addInvitationListener(invitationListener);
                }
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                if (contact_users != null) {

                    for(int i=0; i<contact_users.size();i++){
                        if (contact_users.get(i).type.matches("group")) {
                            try {
                                try {
                                    MultiUserChat muc=MultiUserChatManager.getInstanceFor(App.getComponent().userauth().getS()).getMultiUserChat(JidCreate.entityBareFrom(contact_users.get(i).mwith1));
                                    MucEnterConfiguration.Builder build = muc.getEnterConfigurationBuilder(Resourcepart.from(login1));
                                    build.requestMaxStanzasHistory(0);
                                    build.requestMaxCharsHistory(0);
                                    MucEnterConfiguration muconfig=build.build();
                                    Log.d("MucJoin ", ""+muc.getRoom());
                                    muc.join(muconfig);
                                } catch (MultiUserChatException.NotAMucServiceException e1) {
                                    e1.printStackTrace();
                                } catch (SmackException.NoResponseException e1) {
                                    e1.printStackTrace();
                                } catch (XMPPException.XMPPErrorException e1) {
                                    e1.printStackTrace();
                                } catch (SmackException.NotConnectedException e1) {
                                    e1.printStackTrace();
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                };
                            } catch (XmppStringprepException e1) {
                                e1.printStackTrace();
                            }
                        }


                    }
                }
                if (getIntent().getStringExtra("userjidPush")!=null){
                    Intent inte = new Intent(MainActivity.this, newChatPage.class);
                    inte.putExtra("userjid", getIntent().getStringExtra("userjidPush"));
                    inte.putExtra("userType", "chat");
                    inte.putExtra("newuser", "no");
                    startActivity(inte);
                }
            }


        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                if (contact_users!=null) {
                    flowable = dataBase.message_withtestDAO().getAll1().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
                    disposable = flowable.subscribe(new Consumer<List<message_withtest17>>() {
                        @Override
                        public void accept(List<message_withtest17> message_withtest17s) throws Exception {
                            {
                                diffutil diffutil2 = new diffutil(contactlistAdapter.getData(), message_withtest17s);
                                DiffUtil.DiffResult diffResult1 = DiffUtil.calculateDiff(diffutil2);
                                contactlistAdapter.setData(message_withtest17s);
                                diffResult1.dispatchUpdatesTo(contactlistAdapter);
                            }
                        }
                    });
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                    contactlistAdapter = new contactlistAdapter(MainActivity.this, contact_users);
                    recyclerView.setAdapter(contactlistAdapter);
                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount());
                }


            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                searchView = (SearchView) findViewById(R.id.searchview);
                searchView.setOnSearchClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            roster.reloadAndWait();
                        } catch (SmackException.NotLoggedInException e5) {
                            e5.printStackTrace();
                        } catch (SmackException.NotConnectedException e5) {
                            e5.printStackTrace();
                        } catch (InterruptedException e5) {
                            e5.printStackTrace();
                        }
                    }
                });
                list1 = new ArrayList<>();
                RxSearchObservable.fromView(searchView)
                        .debounce(1000, TimeUnit.MILLISECONDS)
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String text) throws Exception {

                                if (text.isEmpty()) {
                                    Log.d("text", "" + text);
                                    contact_users = dataBase.message_withtestDAO().getAll();
                                    return true;
                                } else {
                                    if (list1!=null && list1.size()!=0 ){
                                        list1.clear();
                                    }
                                    for(int i=0; i<contactlistAdapter.getData().size();i++){
                                        if (contactlistAdapter.getData().get(i).nick1.toLowerCase().contains(text)){
                                            message_withtest17 mes = new message_withtest17();
                                            mes._id1 = 0;
                                            mes.mark = 0;
                                            mes.mwith1 = contactlistAdapter.getData().get(i).mwith1;
                                            mes.nick1 = contactlistAdapter.getData().get(i).nick1;
                                            mes.type = "chat";
                                            mes.status=contactlistAdapter.getData().get(i).status;
                                            mes.mtime1 = "0";
                                            list1.add(mes);
                                        }
                                    }

                                        for (RosterEntry entry : entries) {
                                            Presence entryPresence = null;
                                            try {
                                                entryPresence = App.getComponent().userauth().getRoster().getPresence(JidCreate.bareFrom(entry.getUser()));
                                            } catch (XmppStringprepException e) {
                                                e.printStackTrace();
                                            }
                                            if (entry.getName() != null) {
                                                Log.d("XMPPChatDemoActivity", "Name: " + entry.getName());
                                                Log.d("XMPPChatDemoActivity", "Id: " + entry.getUser());
                                                if (entry.getName().toLowerCase().contains(text)) {
                                                    Log.d("XMPPChatDemoActivity", "Name: " + entry.getName());
                                                    Log.d("XMPPChatDemoActivity", "Id: " + entry.getUser());
                                                    message_withtest17 mes = new message_withtest17();
                                                    mes._id1 = 0;
                                                    mes.mark = 0;
                                                    mes.mwith1 = entry.getUser();
                                                    mes.nick1 = entry.getName();
                                                    mes.type = "chat";
                                                    mes.status = entryPresence.getType().toString();
                                                    mes.mtime1 = "0";
                                                    list1.add(mes);
                                                }
                                            }
                                        }

                                    return true;
                                }

                            }
                        }).distinctUntilChanged()
                        .switchMap((Function<String, ObservableSource<String>>) s -> {
                            return Observable.just(s);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                Log.d("TextFinal", s);
                                Log.d("TextFinal", "" + s.length());

                                contactlistAdapter.setData(list1);

                                if (s.length() == 0) {
                                    contactlistAdapter.setData(contact_users);
                                }
                            }
                        });


                invitationListener = new InvitationListener() {
                    @Override
                    public void invitationReceived(XMPPConnection conn, MultiUserChat room, EntityJid inviter, String reason, String password, Message message, MUCUser.Invite invitation) {

                        subscribeRoom(mynick, room.getRoom().getLocalpart().toString());

                        message_withtest_17 = new message_withtest17();
                        message_withtest_17.mwith1 = room.getRoom().asEntityBareJidString();
                        message_withtest_17.nick1 = reason.substring(reason.lastIndexOf("&") + 1);
                        message_withtest_17.type = "group";
                        message_withtest_17.status="offline";
                        message_withtest_17.mark = 1;
                        dataBase.message_withtestDAO().insert(message_withtest_17);
                        try {
                            room.join(Resourcepart.from(mynick));
                        } catch (SmackException.NoResponseException e) {
                            e.printStackTrace();
                        } catch (XMPPException.XMPPErrorException e) {
                            e.printStackTrace();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (MultiUserChatException.NotAMucServiceException e) {
                            e.printStackTrace();
                        } catch (XmppStringprepException e) {
                            e.printStackTrace();
                        }
                    }
                };

                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        myToken=task.getResult();
                        Log.d("token ", ""+myToken);
                        isTokenSet(myToken);

                    }
                });


            }
        }).subscribe();

        sPref = getSharedPreferences("pref", MODE_PRIVATE);

        if (receiver == null) {
            receiver = new MyBroadcastReceiver();
            intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            intentFilter.addAction("IncomingCall");
            registerReceiver(receiver, intentFilter);
        }


}

        @Override
        public boolean onCreateOptionsMenu(Menu menu){
        //super.onCreateOptionsMenu(menu);
            MenuInflater inflater=getMenuInflater();
            inflater.inflate(R.menu.menu, menu);

        return true;
        }

        @Override
        public  boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.action_create_group:
                Intent intentCreateGroup = new Intent(MainActivity.this, CreateGroup.class);
                startActivity(intentCreateGroup);
                return  true;
            case R.id.action_avatar:
                Intent intentAvatar = new Intent(MainActivity.this, Avatar.class);
                startActivity(intentAvatar);
                return  true;

            case R.id.addContent:
                Intent intent = new Intent(MainActivity.this, AddMyContent.class);
                startActivity(intent);
                break;
        }
        return  true;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        private void onAppBackground(){

            editor = sPref.edit();
            editor.putInt("activityState", 1677);
            editor.commit();
        }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForeground(){
        Log.d("MyApp", "app in Foreground");
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

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }



    public void clews(View view) {
        Log.d("Click News", "Click News");
        Intent intent = new Intent(MainActivity.this, NewsActivity.class);
        startActivity(intent);
    }

    public void addMyContent(View view) {
        Log.d("Click News", "Click News");
        Intent intent = new Intent(MainActivity.this, MyContent.class);
        startActivity(intent);
    }


    public void isTokenSet(final String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Socket clientSocket;
                BufferedReader in;
                BufferedWriter out;
                JSONObject json;
                try {
                    Log.d("serverWord", "" + token);
                   // clientSocket = new Socket("10.30.10.102", 4005);
                    Socket  clientSocket= SocketFactory.getDefault().createSocket();
                    InetSocketAddress inetSocketAddress=new InetSocketAddress("10.30.10.102", 4005);
                    clientSocket.connect(inetSocketAddress, 50000);
                    Log.d("serverWord", ""+clientSocket.isConnected() );
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    json = new JSONObject();
                    json.put("message", "auth");
                    json.put("user", login1);
                    json.put("deviceToken", token);

                    out.write(json + "\n");
                    out.flush();

                    String serverWord = in.readLine();
                    Log.d("serverWord", "" + serverWord);
                    in.close();
                    out.close();
                    clientSocket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }





    private void showCallActivity()
    {
        Intent intent = new Intent(this, CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = 2;
        Log.d("onActivity", "onStart");
        Log.d("onActivity", "onStartflag" + flag1);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onActivityDestroy", "");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onActivityResume", "");
        editor = sPref.edit();
        editor.putInt("activityState", 1675);
        editor.commit();
        flag1 = 1;

    }

    protected void onPause() {
        super.onPause();
        Log.d("onActivityPause", "");
        flag1 = 0;
        if (searchView != null) {
            searchView.clearFocus();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //editor = sPref.edit();
        Log.d("onActivityStop", "");
      //  editor.putInt("activityState", 1677);
       // editor.commit();
        flag1 = 0;
        if (sizeDB != 0)
        {
            MultiUserChatManager.getInstanceFor(App.getComponent().userauth().getS()).removeInvitationListener(invitationListener);
        }
    }

    class MyLoginTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... voids) {
            ((XMPPTCPConnection)App.getComponent().userauth().getS()).setReplyTimeout(50000);
            try {
                userSearchManager = new UserSearchManager(App.getComponent().userauth().getS());

                domainBareJid = JidCreate.domainBareFrom("vjud.192.168.0.26");

                form = userSearchManager.getSearchForm(domainBareJid);
                answerForm = form.createAnswerForm();
                reportedData = userSearchManager.getSearchResults(answerForm, domainBareJid);
                //  userSearchManager.getSearchServices();
                Log.d("ReportData ", ""+reportedData.getRows().size());

            } catch (XmppStringprepException e) {
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
            return null;
        }
    }

}






       /* dataBase.mesagechatest_Dao().getItemList().subscribeOn(Schedulers.io()).observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread()).subscribe(new Consumer<List<message_chattest17>>() {
            @Override
            public void accept(List<message_chattest17> message_chattest17s) throws Exception {
                Log.d("message_chattest17with", "" + message_chattest17s.size());
            }
        });

        dataBase.tmp_messageoutDAO().getAll1()
                .subscribeOn(Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<tmp_outmessage17>>() {
                       @Override
                       public void accept(List<tmp_outmessage17> tmp_outmessage17s) throws Exception {
                              Log.d("tmp.outmessage", ""+tmp_outmessage17s.size());
                              for(int i=0; i<tmp_outmessage17s.size(); i++){
                              Log.d("tmp.outmessage", ""+tmp_outmessage17s.get(i).tmpbody);
                              Log.d("tmp.outmessage", ""+tmp_outmessage17s.get(i).getIdtmpbody());
                }
            }
        });*/


   /* Thread th=new Thread(() -> {
        if (dataBase.myLoginsDao().getAll().size()==0) {
            Intent intent = new Intent(MainActivity.this, login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else {
            //dataBase.message_withtestDAO().delete();
            //dataBase.mesagechatest_Dao().delete();
            //dataBase.tmp_messageoutDAO().delete();
            contact_users=dataBase.message_withtestDAO().getAll();
            for(message_withtest17 um: contact_users)
            {
                Log.d("uchetka est", "uchetka est"+um.nick1);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setAdapter(contactlistAdapter);
                contactlists_.add(new contactlist(um.nick1));
            }

            contactlistAdapter=new contactlistAdapter(MainActivity.this, contact_users);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            recyclerView.setAdapter(contactlistAdapter);
            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
            Log.d("contactlistAdapter", ""+contactlistAdapter.getData().size());
        }
    });
//th.start();*/


//userAuth=App.getComponent().userauth();

//App.getComponent().userauth();
    /*  Thread th=new Thread(new Runnable() {
          @Override
          public void run() {
              if (dataBase.myLoginsDao().getAll().size()==0){
                  Intent intent = new Intent(MainActivity.this, login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
              }else{
                  //Intent intent = new Intent(MainActivity.this, ServiceChat.class);
                  NotificationManager notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                  notificationManager1.cancelAll();
                 // startService(intent);

                 // startService(intent);

                  Log.d("onActivity", "onCreate");
                  verifyStoragePermissions(MainActivity.this);

                  groupchat = (Button) findViewById(R.id.buttoncreategroup);
                  Lview = (ListView) findViewById(R.id.lvSimple);
                  boxAdapter = new BoxAdapter(MainActivity.this, cUsers);
                  /*sConn = new ServiceConnection() {
                      @Override
                      public void onServiceConnected(ComponentName name, IBinder service) {
                          Log.d("onStartActivity", "onServiceConnected");
                          mService1 = new Messenger(service);
                          Message msg = Message.obtain(null, ServiceChat.register_client2, 1, 0);
                          msg.replyTo = mMessenger1;
                          try {
                              mService1.send(msg);
                          } catch (RemoteException e) {
                              e.printStackTrace();
                          }
                          isActive = 0;
                          try {
                              Intent inte1 = new Intent(ServiceChat.active);
                              inte1.putExtra(PARAM, isActive);
                              sendBroadcast(inte1);
                              Log.d("onStart1", "onStop");
                          } catch (Exception e) {
                              e.printStackTrace();
                          }
                          bound = true;
                      }

                      @Override
                      public void onServiceDisconnected(ComponentName name) {
                          Log.d("onStartActivity", "onServiceDisconnect");
                          mService1 = null;
                          bound = false;
                      }
                  };*/
//bindService(intent, sConn, 0);

                  /*
                  Lview.setAdapter(boxAdapter);
              }
          }
      });*/
// th.start();


 /* public class ClientHandle extends Handler {
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case ServiceChat.contact_list:
                    bundle1=new Bundle();
                     bundle1=(Bundle)msg.obj;
                    cUsers.add(new Contact_users(bundle1.get("nick").toString(), bundle1.getString("mark"), R.drawable.ic_launcher_background, flag, false));
                   Lview.setAdapter(boxAdapter);
                    break;
                case ServiceChat.addItem:
                    Bundle bundle2=(Bundle)msg.obj;
                    Log.d("AddItem", ""+bundle2.get("nick").toString());

                   if (boxAdapter.getCount()!=0) {
                       for (int i = 0; i < boxAdapter.getCount(); i++) {
                           if (i <= boxAdapter.getCount()) {
                               tmp = cUsers.get(i);
                           }
                           if (i == 0) {
                               flag = 1;
                               cUsers.add(0, new Contact_users(bundle2.get("nick").toString(), "1", R.drawable.ic_launcher_background, flag, false));
                           } else {
                               cUsers.set(i, tmp);
                           }
                       }
                   } else if (boxAdapter.getCount()==0){
                       cUsers.add(0, new Contact_users(bundle2.get("nick").toString(), "1", R.drawable.ic_launcher_background, flag, false));
                   }
                    boxAdapter.notifyDataSetChanged();
                    break;

                    case ServiceChat.messagecome:
                        Log.d("redkrasni", "redkrasni");
                    Bundle newmessfrom=(Bundle)msg.obj;
                    for(int i=0;i<boxAdapter.getCount();i++)
                    {
                       if (boxAdapter.getContact(i).nick.equals(newmessfrom.get("newmess"))) {
                           Log.d("onActivPaus", ""+flag1);
                           boxAdapter.getContact(i).flag=flag1;
                           tmp=cUsers.get(i);
                           for (int k=0; k<=i; k++){
                               tmp1=cUsers.get(k);
                               cUsers.set(k, tmp);
                               tmp=tmp1;
                           }
                       }
                    }
                        for(int i=0;i<boxAdapter.getCount();i++)
                        {
                            if (boxAdapter.getContact(i).flag==1 && flag1==1)
                            {
                                Log.d("onActivity.flag", ""+flag);
                                Lview.getChildAt(i).setBackgroundColor(Color.RED);
                            }
                        }
                    boxAdapter.notifyDataSetChanged();
                    break;

                case ServiceChat.messagesend:
                    Bundle bundlemessagesend=(Bundle)msg.obj;
                    for(int i=0;i<boxAdapter.getCount();i++){
                        if (boxAdapter.getContact(i).nick.equals(bundlemessagesend.getString("messsend"))) {
                            boxAdapter.getContact(i).flag=1;
                            tmp=cUsers.get(i);
                            for (int k=0; k<=i; k++){
                                tmp1=cUsers.get(k);
                                cUsers.set(k, tmp);
                                tmp=tmp1;
                            }
                        }
                    }
                    boxAdapter.notifyDataSetChanged();
                    break;
                case ServiceChat.searchUsers:
                    Bundle serchedUser=(Bundle)msg.obj;
                    Log.d("message sending", serchedUser.getString("user"));

                    ArrayAdapter<String > adapter=new ArrayAdapter<String>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, new String[]{serchedUser.getString("user")});
                    autoCompleteTextView.setAdapter(adapter);

                    autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {

                        Bundle bundle=new Bundle();
                        bundle.putString("userNick", adapterView.getItemAtPosition(i).toString());
                        try {
                            mService1.send(Message.obtain(null, ServiceChat.getJid, bundle));
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        Log.d("AdapterView", ""+adapterView.getItemAtPosition(i).toString());
                        Log.d("i", ""+i);
                        Log.d("AdapterView=l", ""+l);
                    });

                    break;
                case ServiceChat.getJid:
                    Bundle Jid=(Bundle)msg.obj;
                     Intent inte = new Intent(MainActivity.this, newChatPage.class);
                     inte.putExtra("userJid", Jid.getString("Jid"));
                     inte.putExtra("userNick", Jid.getString("userNick"));
                     inte.putExtra("newuser", "yes");
                     startActivity(inte);

                    break;
            }
            super.handleMessage(msg);
        }
    }*/

// final Messenger mMessenger1=new Messenger(new ClientHandle());
