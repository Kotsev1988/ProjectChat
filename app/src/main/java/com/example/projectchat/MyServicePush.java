package com.example.projectchat;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.room.Room;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.projectchat.RoomDB.DataBase;
import com.example.projectchat.RoomDB.message_chattest17;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.json.JSONException;
import org.json.JSONObject;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSetting;
import org.pjsip.pjsua2.StringVector;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class MyServicePush extends FirebaseMessagingService implements LifecycleOwner {


    Map<String, String> map;
    Ringtone ring;
    Uri notif;
    NotificationManager notificationManager1;
    String newmess, login1, nick;
    message_chattest17 message_chattest_17;
    Long idstamp;
    String phone;
    WorkManager workManager;

@Override
public void onCreate(){

    Intent intent1 = new Intent(MyServicePush.this, SipPhone.class);
    //startService(intent1);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //startForegroundService(intent1);
      //  this.startForegroundService(intent1);

    }else{
        startService(intent1);
    }
    Intent intent = new Intent(MyServicePush.this, ServiceChat.class);
    //startService(intent);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

       // this.startForegroundService(intent);
        //startForeground(0, null);
    }else{
        startService(intent);
    }
}


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Sq=new sqLite(this);
        //Sq.open();
        //Log.d("SqlitePush", ""+Sq.timestamp());
        //JobExersize.scheduleJob(getApplicationContext());

        notif = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ring = RingtoneManager.getRingtone(this, notif);

        notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chanel = new NotificationChannel("78", "My Channel", NotificationManager.IMPORTANCE_HIGH);

            chanel.setDescription("My channel description");
            chanel.enableLights(true);
            chanel.setLightColor(Color.RED);
            chanel.enableVibration(false);
            chanel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            notificationManager1.createNotificationChannel(chanel);

        }

       /* ActivityManager manager1 = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager1.getRunningServices(Integer.MAX_VALUE)) {
            Log.d("runningServicesPush", "" + service.service.getClassName());

            if (service.service.getClassName().matches("com.example.projectchat.MyJobService")) {
                //Sq.close();
                onDestroy();
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        Log.d("token ", ""+task.getResult());
                    }
                });
            }

        }*/

        // Log.d("remote mess", "" + remoteMessage.getData().size());
        //  Log.d("remote mess", "" + remoteMessage.getFrom().toString());
        // Log.d("remote mess", "" + remoteMessage.toString());
        // map = new HashMap<String, String>();
        // map = remoteMessage.getData();
      /*  Iterator<String> iterator = remoteMessage.getData().values().iterator();

        while (iterator.hasNext()) {
            Log.d("iterator", "" + iterator.next());

        }
        for (String key : remoteMessage.getData().keySet()) {
            Log.d("keys", "" + key);
        }*/
        try {

            if (remoteMessage.getData().get("messageType").toString().matches("message")) {
                newmess = remoteMessage.getData().get("message");
                login1 = remoteMessage.getData().get("fromm");
                showNotification(login1, newmess);
            } else if (remoteMessage.getData().get("messageType").matches("phone")) {
                newmess = remoteMessage.getData().get("phone");
                login1 = remoteMessage.getData().get("account");
                CallNotification(login1, newmess);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNotification(String title, String msg) {


        Intent i = new Intent(MyServicePush.this, MainActivity.class);

        i.putExtra("userType", "chat");
        i.putExtra("newuser", "no");
        i.putExtra("userjid", title);
        i.putExtra("userjidPush", title);
        i.putExtra("userNick", "");
        i.putExtra("msg", msg);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder=TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        //stackBuilder.addNextIntentWithParentStack(i);
        stackBuilder.addNextIntent(i);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyServicePush.this, "78")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setChannelId("78")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setContentIntent(pendingIntent);
        Notification notification1 = builder.build();

        notificationManager1.notify(78, notification1);
        //}
    }



    private void CallNotification(String title, String msg) {

        Intent intent1 = new Intent(MyServicePush.this, SipPhone.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent1);


        Intent i = new Intent(MyServicePush.this, CallActivity.class);
        i.putExtra("msg", msg);
        TaskStackBuilder stackBuilder=TaskStackBuilder.create(this);
        stackBuilder.addParentStack(newChatPage.class);
        stackBuilder.addNextIntent(i);
       // i.putExtra("msg", msg);
       // PendingIntent pendingIntent = PendingIntent.getActivity(MyServicePush.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        //notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         PendingIntent pendingIntent=stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyServicePush.this, "78")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("title")
                    .setContentText("Call From")
                    .setAutoCancel(true)
                    .setChannelId("78")
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent);
            Notification notification1 = builder.build();

            //  notificationManager1.notify(78, notification1);

            startForeground(1337, notification1);

        //else{

        }else{
            Intent intent = new Intent(MyServicePush.this, SipPhone.class);
            startService(intent);
            Intent i = new Intent(MyServicePush.this, CallActivity.class);
            i.putExtra("msg", msg);
            TaskStackBuilder stackBuilder=TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(i);
            // i.putExtra("msg", msg);
            // PendingIntent pendingIntent = PendingIntent.getActivity(MyServicePush.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            //notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent=stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyServicePush.this, "78")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("title")
                    .setContentText("Call From")
                    .setAutoCancel(true)
                    .setChannelId("78")
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent);
            Notification notification1 = builder.build();


            notificationManager1.notify(78, notification1);
        }
    }

   /* public void updateChatDB(String _body, String _from, String _timestamp, String _idstanza, String _to, String _picture, int _mark) {
        message_chattest_17 = new message_chattest17();
        message_chattest_17.body1 = _body;
        message_chattest_17.from1 = _from;
        message_chattest_17.to1 = _to;
        message_chattest_17.timestampid = _timestamp;
        message_chattest_17.idstanza1 = _idstanza;
        message_chattest_17.picture1 = _picture;
        message_chattest_17.mark = _mark;
        App.getComponent().database().mesagechatest_Dao().insert(message_chattest_17);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Sq.close();
        Log.d("FirebaseonDestroy", "onDestroy");
    }


    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }
}
