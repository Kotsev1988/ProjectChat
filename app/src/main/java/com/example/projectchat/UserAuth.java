package com.example.projectchat;


import android.util.Log;


import com.example.projectchat.RoomDB.DataBase;
import com.example.projectchat.RoomDB.logins;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.minidns.record.A;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.Completable;
import io.reactivex.functions.Action;


public class UserAuth {

    DomainBareJid servicename;
    InetAddress addr;
    String Smack = "Smack12";
    AbstractXMPPConnection s;
    XMPPTCPConnectionConfiguration f;
    String neme, password;
    XMPPTCPConnection xmpptcpConnection;
    MamManager mamManager;
    FileTransferManager fileTransferManager;
    PingManager pingManager;
    public DataBase database;
    logins log;
    Roster roster;
    PubSubManager pubSubManager;

    String urlconn = "192.168.0.26";
    String urlPic = "http://192.168.0.27/uploads/";

    public UserAuth(DataBase database) {

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (!database.myLoginsDao().getAll().isEmpty()) {
                    neme = database.myLoginsDao().my_nick();
                    password= database.myLoginsDao().my_pass();
                    Log.d("databaseSize", password);
                }
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                try {
                    addr = InetAddress.getByName(urlconn);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                try {
                    servicename = JidCreate.domainBareFrom("192.168.0.26");
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }

                try {
                    f = XMPPTCPConnectionConfiguration.builder()
                            .setHostAddress(addr)
                            .setXmppDomain(servicename)
                            .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                            .setPort(5222)
                            .setResource(Smack)
                            .setConnectTimeout(5000)
                            .setSendPresence(true)
                            .build();
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }

                s = new XMPPTCPConnection(f);
                try {
                    Log.d("neme", "" + neme);
                    s.connect();
                    s.login(neme, password);

                    Log.d("isConn", "" + s.isConnected());
                    Log.d("isConn", "" + s.isAuthenticated());

                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

               // xmpptcpConnection = (XMPPTCPConnection) s;
                mamManager = MamManager.getInstanceFor(s);
                pingManager = PingManager.getInstanceFor(s);
                roster= Roster.getInstanceFor(s);
                pubSubManager=PubSubManager.getInstanceFor(s);

            }
        }).observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread()).subscribe();

    }


    public AbstractXMPPConnection getS() {
        return s;
    }
    public Roster getRoster() {
        return roster;
    }
    public PubSubManager getPubSubManager() {
        return pubSubManager;
    }
}
