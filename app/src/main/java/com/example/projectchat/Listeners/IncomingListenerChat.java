package com.example.projectchat.Listeners;

import android.util.Log;

import com.example.projectchat.RoomDB.DataBase;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;

public class IncomingListenerChat {
    int isActive;
    DataBase dataBase;

    public IncomingListenerChat(AbstractXMPPConnection abstractXMPPConnection) {
        ChatManager.getInstanceFor(abstractXMPPConnection).addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                Log.d("from",    "" + from.toString());
                Log.d("message", "" + message.toXML(""));
            }
        });
    }
}
