package com.example.projectchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.EmptyResultIQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class login extends Activity {
    EditText firstName, secondName, userName, password, confirmPassword, email, phone;
    TextView useralreadyhas;
    Button loginbtn;
    sqLite SqLite;

    AbstractXMPPConnection s;
    XMPPTCPConnectionConfiguration f;
    DomainBareJid servicename;
    InetAddress addr;
    Map<String, String> nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        firstName = (EditText) findViewById(R.id.FirstName);
        secondName = (EditText) findViewById(R.id.secondName);
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.Password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        loginbtn = (Button) findViewById(R.id.login);
        useralreadyhas = (TextView) findViewById(R.id.textView2);


        try {
            addr = InetAddress.getByName("192.168.0.26");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            servicename = JidCreate.domainBareFrom("192.168.0.26");
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        f = XMPPTCPConnectionConfiguration.builder()
                .setHostAddress(addr)
                .setXmppDomain(servicename)
                .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                .setPort(5222)
                .setSendPresence(true)
                .setCompressionEnabled(false)
                .build();
        s = new XMPPTCPConnection(f);

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


    }


    public void btnlogin(View view) {

        try {
            addr = InetAddress.getByName("192.168.0.26");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            servicename = JidCreate.domainBareFrom("192.168.0.26");
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        f = XMPPTCPConnectionConfiguration.builder()
                .setHostAddress(addr)
                .setXmppDomain(servicename)
                .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                .setPort(5222)
                .setSendPresence(true)
                .setCompressionEnabled(false)

                .build();
        s = new XMPPTCPConnection(f);
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

        if (s.isConnected() && !(s.isAuthenticated())) {
            s.addSyncStanzaListener(new StanzaListener() {
                @Override
                public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {

                }
            }, new StanzaFilter() {
                @Override
                public boolean accept(Stanza stanza) {

                    Log.d("stanza", "" + stanza.toXML(""));
                    Log.d("stanza", "" + stanza.getClass().getName());

                    switch (stanza.getClass().getName()) {
                        case "org.jivesoftware.smack.packet.EmptyResultIQ":
                            EmptyResultIQ emptyResultIQ = (EmptyResultIQ) stanza;
                            Log.d("emptyRequest", "" + emptyResultIQ.toString());
                            break;
                        case "org.jivesoftware.smackx.iqregister.packet.Registration":
                            Registration registration = (Registration) stanza;


                            if (registration.getError() != null) {
                                Log.d("registrationGetError", "" + registration.getError().getDescriptiveText("en"));
                                useralreadyhas.setText(registration.getError().getDescriptiveText("en").toString());
                                useralreadyhas.setVisibility(View.VISIBLE);

                            }
                            break;
                    }


                    return true;
                }
            });

            if (!s.isConnected()) {
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
            }

            try {
                Log.d("getAttributes", "" + userName.getText().toString());



                org.jivesoftware.smackx.iqregister.AccountManager accountManager = AccountManager.getInstance(s);



                accountManager.sensitiveOperationOverInsecureConnection(true);
                accountManager.createAccount(Localpart.from(userName.getText().toString()), password.getText().toString());
                s.login(userName.getText().toString(), password.getText().toString());
                VCardManager vCardManager = VCardManager.getInstanceFor(s);
                VCard vCard = vCardManager.loadVCard();
                vCard.setEmailHome(email.getText().toString());
                vCard.setPhoneHome("mobile", phone.getText().toString());
                vCard.setFirstName(firstName.getText().toString());
                vCard.setLastName(secondName.getText().toString());
                vCard.setNickName(firstName.getText().toString()+" "+secondName.getText().toString());

                vCardManager.saveVCard(vCard);
                if (s.isAuthenticated()) {
Completable.fromAction(new Action() {
    @Override
    public void run() throws Exception {
        SqLite = new sqLite(getApplicationContext());
        SqLite.open();
        nickname = new HashMap<String, String>();
        nickname.put("my_user_name", userName.getText().toString() + "@192.168.0.26");
        nickname.put("my_nick", userName.getText().toString());
        nickname.put("password", password.getText().toString());
        SqLite.updateMyLogin(nickname);
    }
}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action() {


    @Override
    public void run() throws Exception {
        Log.d("isExist", "" + SqLite.isExist());

        if (SqLite.isExist()) {
            Intent intent = new Intent(login.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}).subscribe();

                }

                Log.d("getAttributes", "" + accountManager.getAccountAttributes().toString());
                //s.login(Localpart.from("a_shaovat"), "1234");

            } catch (Exception e) {
                Log.d("getMessage", "" + e.getMessage().toString());
            }
        }
    }

}

