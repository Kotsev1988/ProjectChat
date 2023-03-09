package com.example.projectchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.projectchat.RoomDB.message_withtest17;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.jid.util.JidUtil;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class CreateGroup extends Activity {
    Button users;
    ArrayList<Contact_users> cUsers = new ArrayList<Contact_users>();
    ArrayList<String> user = new ArrayList<String>();
    ListView Lview;
    EditText namegroup;
    BoxAdapter1 boxAdapter;
    List<message_withtest17> contact_users = new ArrayList<message_withtest17>();
    MultiUserChatManager multiUserChatManager;
    MultiUserChat multiUserChat;
    EntityBareJid mucJid;
    message_withtest17 message_withtest_17;
    IQ iq;
    String mynick;
    EntityBareJid jid1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        namegroup = (EditText) findViewById(R.id.GroupName);
        Lview = (ListView) findViewById(R.id.lvgetusers);
        Lview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        Completable.fromAction(() -> {
            contact_users = App.getComponent().database().message_withtestDAO().getAll();
            Log.d("contactUserRx", "" + contact_users.size());
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                for (message_withtest17 um : contact_users) {
                    if (um.type.matches("chat")) {
                        cUsers.add(new Contact_users(um.nick1, "1", R.drawable.ic_launcher_background, 0, false));
                    }
                }
                boxAdapter = new BoxAdapter1(CreateGroup.this, cUsers);
                Lview.setAdapter(boxAdapter);
            }
        }).subscribe();
    }

    public void btngr(View v) {
        String result = "";

        Log.d("usersSize", "" + user.size());
        for (Contact_users p : boxAdapter.getCheckedUsers()) {
            if (p.tag) {
                result += "\n" + p.nick;
                Completable.fromAction(() -> {

                    user.add(App.getComponent().database().message_withtestDAO().getJid(p.nick));

                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }
        }

        Log.d("users", "" + user.size());
        for (int i = 0; i < user.size(); i++) {
            Log.d("usersdoOnComp", "" + user.get(i));
        }
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        createMUC(namegroup.getText().toString(), user);
        //user.clear();

    }

    public void createMUC(String groupname, ArrayList<String> users) {
        try {
            mucJid = JidCreate.entityBareFrom(groupname + "@conference.192.168.0.26");
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        Completable.fromAction(() -> {
            multiUserChatManager = MultiUserChatManager.getInstanceFor(App.getComponent().userauth().getS());
            mynick = App.getComponent().database().myLoginsDao().my_nick();
            jid1 = JidCreate.entityBareFrom(App.getComponent().database().myLoginsDao().my_user_name());
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {

                multiUserChat = multiUserChatManager.getMultiUserChat(mucJid);

                try {
                    Form form = null;
                    form = multiUserChat.getConfigurationForm();
                    Form submitForm = form.createAnswerForm();
                    submitForm.setAnswer("muc#roomconfig_persistentroom", true);
                    submitForm.setAnswer("allow_subscription", true);
                    submitForm.setAnswer("muc#roomconfig_allowinvites", true);
                    submitForm.setAnswer("muc#roomconfig_membersonly", true);
                    submitForm.setAnswer("allow_query_users", true);
                    submitForm.setAnswer("muc#roomconfig_publicroom", true);
                    submitForm.setAnswer("muc#roomconfig_moderatedroom", true);
                    submitForm.setAnswer("mam", true);
                    multiUserChat.sendConfigurationForm(submitForm);

                    multiUserChat.create(Resourcepart.from(groupname)).makeInstant();

                    for (int i = 0; i < users.size(); i++) {
                        Log.d("usersinvite", "" + users.get(i).toString());

                        multiUserChat.invite(JidCreate.entityBareFrom(users.get(i)), "dobavit&" + groupname);
                    }

                    multiUserChat.join(Resourcepart.from(App.getComponent().userauth().getS().getUser().getResourceOrThrow().toString()));

                    if (multiUserChat.isJoined()) {
                        message_withtest_17 = new message_withtest17();
                        message_withtest_17.mwith1 = mucJid.asEntityBareJidString();
                        message_withtest_17.nick1 = groupname;
                        message_withtest_17.type = "group";
                        message_withtest_17.mark = 1;
                        Completable.fromAction(() -> {
                            App.getComponent().database().message_withtestDAO().insert(message_withtest_17);
                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

                        Log.d("joined", "" + multiUserChat.getOccupants().size());
                        Log.d("joined", "" + multiUserChat.isJoined());

                        Bundle contactList = new Bundle();
                        contactList.putString("nick", groupname);
                        contactList.putInt("position", 0);

                        for (int i = 0; i < multiUserChat.getOccupants().size(); i++) {
                            Log.d("occupants", "" + multiUserChat.getOccupants().get(i).toString());
                        }


                    }
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (MultiUserChatException.MucAlreadyJoinedException e) {
                    e.printStackTrace();
                } catch (MultiUserChatException.MissingMucCreationAcknowledgeException e) {
                    e.printStackTrace();
                } catch (MultiUserChatException.NotAMucServiceException e) {
                    e.printStackTrace();
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }

                try {
                    multiUserChat.leave();

                    Intent intent = new Intent(CreateGroup.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);


                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                user.clear();
                onDestroy();
            }
        }).subscribe();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onStart", "unbind");
    }
}
