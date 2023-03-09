package com.example.projectchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectchat.RoomDB.DataBase;
import com.example.projectchat.RoomDB.message_chattest17;
import com.example.projectchat.RoomDB.message_withtest17;
import com.example.projectchat.RoomDB.tmp_outmessage17;
import com.example.projectchat.databinding.ActivityForwardBinding;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rx.Observable;

public class ForwardActivity extends AppCompatActivity {

    private ActivityForwardBinding binding;

    Observable<CharSequence> addSymbol;
    AutoCompleteTextView autoCompleteTextView;
    DataBase dataBase;
    List<message_withtest17> contact_users;
    RecyclerView recyclerView;
    ForwardListAdapter contactlistAdapter;
    String mynick;


    SharedPreferences sPref;
    SharedPreferences.Editor editor;
    Flowable<List<message_withtest17>> flowable;
    Disposable disposable;
    String login1, jidForward;
    int position;
    Button forward;
    String type;
    String nick=null;

    message_chattest17 message_chattest_17;
    tmp_outmessage17 tmpOutmessage17;

    public interface onSomeEventListener {
        public void SomeEventForward(Bundle s);
    }

   onSomeEventListener someEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityForwardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        dataBase = App.getComponent().database();
        recyclerView = (RecyclerView) findViewById(R.id.recyclecontactlist_forward);
        forward=(Button)findViewById(R.id.button);

     Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                    login1 = App.getComponent().database().myLoginsDao().my_user_name();
                    mynick = dataBase.myLoginsDao().my_nick();
                    contact_users = dataBase.message_withtestDAO().getAll();
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                if (contact_users != null) {
                    contactlistAdapter = new ForwardListAdapter(ForwardActivity.this, contact_users);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread()).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                if (contact_users != null) {
                    flowable = dataBase.message_withtestDAO().getAll1().subscribeOn(Schedulers.io()).observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread());

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

                    recyclerView.setLayoutManager(new LinearLayoutManager(ForwardActivity.this));
                    recyclerView.setAdapter(contactlistAdapter);
                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount());

                }
            }
        }).subscribe();


     forward.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

             Set<Map.Entry<Integer, String>> me=contactlistAdapter.onItemSelected().entrySet();
             HashMap<Integer, String> map=(HashMap<Integer, String>) getIntent().getSerializableExtra("message");
             Set<Map.Entry<Integer, String>> setme=map.entrySet();


                 Completable.fromAction(new Action() {
                     @Override
                     public void run() throws Exception {

                         for (Map.Entry<Integer, String> set: me) {
                             jidForward = set.getValue();
                             position = set.getKey();

                             type=App.getComponent().database().message_withtestDAO().getTypeChat(jidForward);
                             for (Map.Entry<Integer, String> me1 : setme) {

                                 Message message = new Message();
                                 message.setBody(me1.getValue());

                                 {
                                     switch (type) {
                                         case "chat":

                                             SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                                             Date date = new Date();
                                             Timestamp ts = new Timestamp(date.getTime());
                                             message_chattest_17 = new message_chattest17();
                                             message_chattest_17.from1 = login1;
                                             message_chattest_17.to1 = jidForward;
                                             message_chattest_17.body1 = message.getBody();
                                             message_chattest_17.timestampid = String.valueOf(ts.getTime());
                                             message_chattest_17.idstanza1 = message.getStanzaId();


                                                     App.getComponent().database().mesagechatest_Dao().insert(message_chattest_17);
                                                     App.getComponent().database().message_withtestDAO().updateDBUser(String.valueOf(ts.getTime()), jidForward);

                                             tmpOutmessage17 = new tmp_outmessage17();
                                             tmpOutmessage17.tmpto = jidForward;
                                             tmpOutmessage17.tmpbody = message.getBody();
                                             tmpOutmessage17.idstanzatmp = String.valueOf(ts.getTime());
                                             tmpOutmessage17.idtmpbody = message.getStanzaId();
                                             tmpOutmessage17.tmptype = "chat";

                                             App.getComponent().database().tmp_messageoutDAO().insert(tmpOutmessage17);

                                             break;

                                         case "group":

                                             SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                                             Date date1 = new Date();
                                             Timestamp ts1 = new Timestamp(date1.getTime());

                                             message_chattest_17 = new message_chattest17();
                                             message_chattest_17.from1 = login1;
                                             message_chattest_17.to1 = jidForward;
                                             message_chattest_17.body1 = message.getBody();
                                             message_chattest_17.timestampid = String.valueOf(ts1.getTime());
                                             message_chattest_17.idstanza1 = message.getStanzaId();


                                                     App.getComponent().database().mesagechatest_Dao().insert(message_chattest_17);
                                                     App.getComponent().database().message_withtestDAO().updateDBUser(String.valueOf(ts1.getTime()), jidForward);


                                             tmpOutmessage17 = new tmp_outmessage17();
                                             tmpOutmessage17.tmpto = jidForward;
                                             tmpOutmessage17.tmpbody = message.getBody();
                                             tmpOutmessage17.idstanzatmp = String.valueOf(ts1.getTime());
                                             tmpOutmessage17.idtmpbody = message.getStanzaId();
                                             tmpOutmessage17.tmptype = "group";
                                             Completable.fromAction(() -> App.getComponent().database().tmp_messageoutDAO().insert(tmpOutmessage17));

                                             break;
                                     }
                                 }

                                 try {
                                     Log.d("Forward message ", message.getBody());
                                     ChatManager.getInstanceFor(App.getComponent().userauth().getS()).chatWith(JidCreate.entityBareFrom(set.getValue())).send(message);
                                 } catch (SmackException.NotConnectedException e) {
                                     e.printStackTrace();
                                 } catch (InterruptedException e) {
                                     e.printStackTrace();
                                 } catch (XmppStringprepException e) {
                                     e.printStackTrace();
                                 }
                             }
                         }

                     }
                 }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action() {
                     @Override
                     public void run() throws Exception {

                     }
                 }).subscribe();




if (me.size()==1) {
    for (Map.Entry<Integer, String> set: me) {
        jidForward = set.getValue();
    }
    Completable.fromAction(new Action() {
        @Override
        public void run() throws Exception {
            Log.d("jidForward", jidForward);
            nick=dataBase.message_withtestDAO().getNick(jidForward);
        }
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action() {
        @Override
        public void run() throws Exception {
            Intent inte = new Intent(ForwardActivity.this, newChatPage.class);
            inte.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            inte.putExtra("userNick", nick);
            inte.putExtra("newuser", "no");
            startActivity(inte);
        }
    }).subscribe();

}else{
    Intent inte = new Intent(ForwardActivity.this, MainActivity.class);
    inte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
    startActivity(inte);
    finish();
}
}
             });
    }


}