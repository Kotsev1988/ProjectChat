package com.example.projectchat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.projectchat.RoomDB.DataBase;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.element.MamElements;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jivesoftware.smackx.pubsub.ItemsExtension;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class MyJobService extends JobService {
    boolean isRun = true;
    MyLoginTask task;
    static AbstractXMPPConnection s;
    ChatManager chatManager;
    Ringtone r;
    Uri notif;
    JobParameters jobParameters1;
    String currentDateTimeInc;
    DataBase dataBase;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.jobParameters1 = jobParameters;
        Log.d("rnServJobSrv", "" + "startService");
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo service : manager.getRunningAppProcesses()) {
            Log.d("rnServJobSrv", "" + service.processName.toString());
        }


        NotificationManager notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager1.cancel("My Channel", 78);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyJobService.this, "channel_id")
                .setSmallIcon(R.mipmap.ic_launcher)

                .setContentText("hi")
                .setAutoCancel(true);
        //.setContentIntent(pendingIntent);
        Notification notification1 = builder.build();
        notificationManager1.notify(1, notification1);

        ActivityManager manager5 = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager5.getRunningServices(Integer.MAX_VALUE)) {
            Log.d("rnServJobSrv", "" + service.service.getClassName());
        }

        isRun = isServiceChatRun("com.example.projectchat:externalProcess");
        Log.d("scheduleJob", "scheduleJob");


        ActivityManager manager1 = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        notif = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = RingtoneManager.getRingtone(getApplicationContext(), notif);
        NotificationManager notificationManager10 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager10.cancel(78);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chanel = new NotificationChannel("77", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            chanel.setDescription("My channel description");
            chanel.enableLights(true);
            chanel.setLightColor(Color.RED);
            chanel.enableVibration(false);
            notificationManager10.createNotificationChannel(chanel);
        }

        task = new MyLoginTask();
        task.execute();


        return true;
    }

    class MyLoginTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.d("logServiceSchedule", "" + s.isConnected());
                dataBase = App.getComponent().database();
                s=App.getComponent().userauth().getS();
                if (!(s.isConnected())) {
                    s.connect();
                    Log.d("logServiceSchedule", "connected");
                }


                SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
                SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                SASLAuthentication.unBlacklistSASLMechanism("PLAIN");

                XMPPTCPConnection.setUseStreamManagementDefault(true);
                XMPPTCPConnection.setUseStreamManagementResumptionDefault(true);

                Log.d("logServiceSchedule", "" + s.isAuthenticated());

                if (!(s.isAuthenticated())) {
                    Log.d("logServiceSchedule", "auth done");
                }



                s.addConnectionListener(new ConnectionListener() {
                    @Override
                    public void connected(XMPPConnection connection) {
                        //JobExersize.scheduleJob(getApplicationContext());

                        Log.d("connectjobserv", "" + connection.isConnected());
                        Log.d("connectjobserv", "" + connection.isAuthenticated());
                    }

                    @Override
                    public void authenticated(XMPPConnection connection, boolean resumed) {
                        Log.d("authenticated", "" + resumed);
                        //checknewMessage();
                    }

                    @Override
                    public void connectionClosed() {
                        Log.d("connection close", "connclose");

                    }

                    @Override
                    public void connectionClosedOnError(Exception e) {
                        s.disconnect();
                        Log.d("conn closeon Error", "" + e.getMessage());
                    }
                });


                chatManager.addIncomingListener(new IncomingChatMessageListener() {

                    @Override
                    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {

                        Long idstamp = Long.parseLong("0");

                        StandardExtensionElement eventElement1, delayElement;
                        Log.d("message incoming", "" + message.toXML(""));

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        eventElement1 = message.getExtension("archived", "urn:xmpp:mam:tmp");

                        if (message.getExtension("urn:xmpp:mam:tmp") != null) {
                            idstamp = (Long.parseLong(eventElement1.getAttributeValue("id")) / 1000);
                            Log.d("idstamparchive", "" + idstamp);
                            currentDateTimeInc = simpleDateFormat.format(new Date(Long.parseLong(eventElement1.getAttributeValue("id")) / 1000));

                            Log.d("message incomingDate", "" + Long.parseLong(eventElement1.getAttributeValue("id")) / 1000);
                            Log.d("message incoming", "" + eventElement1.getAttributeValue("id"));
                        }

                        if (message.getExtension("urn:xmpp:delay") != null) {
                            DelayInformation delayInformation = DelayInformation.from(message);

                            if (delayInformation != null) {
                                Log.i("RECVEIVED", "delay 1 : " + delayInformation.getStamp());
                                Log.i("RECVEIVED", "delay 2 : " + delayInformation.getStamp().getTime());
                                idstamp = delayInformation.getStamp().getTime();
                                Log.d("idstampstream", "" + idstamp);
                                currentDateTimeInc = simpleDateFormat.format(new Date(idstamp));
                            }
                        }
                        Log.d("newMessFromSched", "" + message.getBody());



                        Intent resultIntent = new Intent(MyJobService.this, StopJobService.class);
                        resultIntent.putExtra("startSchedule", "stop");

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyJobService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationManager notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager1.cancel("My Channel", 78);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyJobService.this, "channel_id")
                                .setSmallIcon(R.mipmap.ic_launcher)

                                .setContentText(message.getBody())
                                .setAutoCancel(true);
                                //.setContentIntent(pendingIntent);
                        Notification notification1 = builder.build();
                        notificationManager1.notify(1, notification1);
                        //r.play();
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
            //jobFinished(jobParameters1, false);
            // task.cancel(true);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

       /* public void listenerpacket() {


                        ItemsExtension itemsExtension = (ItemsExtension) eventElement.getEvent();

                        for (int i = 0; i < itemsExtension.getItems().size(); i++) {
                            NamedElement namedElement = itemsExtension.getExtensions().get(i);
                            Log.d("extensions", "" + itemsExtension.getExtensions().get(i).getNamespace().toString());
                            PayloadItem payloadItem = (PayloadItem) namedElement;
                            SimplePayload simplePayload;
                            simplePayload = (SimplePayload) payloadItem.getPayload();
                            String parserString = simplePayload.toXML((XmlEnvironment) null);
                            try {
                                XmlPullParser xmlPullParser = null;
                                try {
                                    xmlPullParser = (XmlPullParser) PacketParserUtils.getParserFor(parserString);
                                } catch (org.jivesoftware.smack.xml.XmlPullParserException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    org.jivesoftware.smack.packet.Message message = PacketParserUtils.parseMessage((org.jivesoftware.smack.xml.XmlPullParser) xmlPullParser);

                                    if (message.getSubject() == null && message.getExtension("pic") == null) {

                                        Log.d("to", "" + message.getFrom().getLocalpartOrNull());
                                        Log.d("from", "" + message.getFrom().getResourceOrNull().toString());


                                        Long idstamp = Long.parseLong("0");

                                        StandardExtensionElement eventElement1, delayElement;
                                        Log.d("message incoming", "" + message.toXML(""));

                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        eventElement1 = message.getExtension("archived", "urn:xmpp:mam:tmp");
                                        ExtensionElement delay = message.getExtension("urn:xmpp:delay");

                                        if (message.getExtension("urn:xmpp:mam:tmp") != null) {
                                            idstamp = (Long.parseLong(eventElement1.getAttributeValue("id")) / 1000);
                                            Log.d("idstamparchive", "" + idstamp);

                                            currentDateTimeInc = simpleDateFormat.format(new Date(Long.parseLong(eventElement1.getAttributeValue("id")) / 1000));
                                            Log.d("message incomingDate", "" + Long.parseLong(eventElement1.getAttributeValue("id")) / 1000);
                                            Log.d("message incomingDate", "" + currentDateTimeInc.toString());
                                            Log.d("message incoming", "" + eventElement1.getAttributeValue("id"));
                                        }


                                        if (message.getExtension("urn:xmpp:delay") != null) {
                                            DelayInformation delayInformation = DelayInformation.from(message);

                                            if (delayInformation != null) {
                                                Log.i("RECVEIVED", "delay 1 : " + delayInformation.getStamp());
                                                Log.i("RECVEIVED", "delay 2 : " + delayInformation.getStamp().getTime());
                                                idstamp = delayInformation.getStamp().getTime();
                                                Log.d("idstampstream", "" + idstamp);
                                                currentDateTimeInc = simpleDateFormat.format(new Date(idstamp));
                                            }
                                        }

                                        Log.d("message incoming", "" + message.getStanzaId());
                                        Log.d("idstampgen", "" + idstamp);


                                        Log.d("messagegroup", "" + message.getFrom().asEntityBareJidIfPossible());

                                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                            Intent resultIntent = new Intent(MyJobService.this, StopJobService.class);
                                            resultIntent.putExtra("startSchedule", "stop");
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MyJobService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                            NotificationManager notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyJobService.this, "77")
                                                    .setSmallIcon(R.mipmap.ic_launcher)

                                                    .setContentText(message.getBody())
                                                    .setAutoCancel(true);
                                                   // .setContentIntent(pendingIntent);

                                            Notification notification1 = builder.build();
                                            notificationManager1.notify(77, notification1);
                                            //r.play();

                                        } else if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

                                            Intent resultIntent = new Intent(MyJobService.this, StopJobService.class);
                                            resultIntent.putExtra("startSchedule", "stop");
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MyJobService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                            NotificationManager notificationManager2 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyJobService.this)
                                                    .setSmallIcon(R.mipmap.ic_launcher)

                                                    .setContentText(message.getBody())
                                                    .setAutoCancel(true);

                                            Notification notification2 = builder.build();
                                            notificationManager2.notify(77, notification2);
                                            //r.play();
                                        }
                                    } else if (message.getExtension("pic").getNamespace().matches("pic")) {

                                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                            Intent resultIntent = new Intent(MyJobService.this, StopJobService.class);
                                            resultIntent.putExtra("startSchedule", "stop");
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MyJobService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                            NotificationManager notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyJobService.this, "77")
                                                    .setSmallIcon(R.mipmap.ic_launcher)
                                                    .setContentTitle("Picture")
                                                    //.setContentText(message.getBody())
                                                    .setAutoCancel(true);
                                                    //.setContentIntent(pendingIntent);

                                            Notification notification1 = builder.build();
                                            notificationManager1.notify(77, notification1);
                                            //r.play();

                                        } else if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                            Intent resultIntent = new Intent(MyJobService.this, StopJobService.class);
                                            resultIntent.putExtra("startSchedule", "stop");
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MyJobService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                            NotificationManager notificationManager2 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyJobService.this)
                                                    .setSmallIcon(R.mipmap.ic_launcher)
                                                    .setContentTitle("Picture")
                                                    //.setContentText(message.getBody())
                                                    .setAutoCancel(true);
                                                   // .setContentIntent(pendingIntent);
                                            Notification notification2 = builder.build();
                                            notificationManager2.notify(77, notification2);
                                            //r.play();
                                        }
                                    }

                                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String currentDateTime1 = simpleDateFormat1.format(new Date());

                                    Log.d("messageStanza", "" + message.getBody());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }, new StanzaFilter() {
                @Override
                public boolean accept(Stanza stanza) {
                    return true;
                }
            });
        }*/

        public void checknewMessage() {

        }

    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        //task.cancel(false);
        // s.disconnect();
        Log.d("scheduleJobstop", "scheduleJob");
        return true;
    }

    public static void discon() {
        if (s.isConnected()) {
            s.disconnect();
        }
    }

    private boolean isServiceChatRun(String s) {
        Log.d("ServiceName", "" + s);

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo service : manager.getRunningAppProcesses()) {
            if (service.processName.equals(s)) {
                Log.d("IsrunningServices", "" + service.processName);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("OnDestroyJobServSched", "OnDestroyJobServ");
        // s.disconnect();
        // task.cancel(false);
        // stopSelf();
        // stopjob();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("onTaskRemovedSched", "onTaskRemovedSched");
        s.disconnect();
        task.cancel(false);
        stopSelf();
        stopjob();
    }

    public void stopjob() {
        jobFinished(jobParameters1, false);
    }
}
