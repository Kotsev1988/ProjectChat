package com.example.projectchat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSetting;
import org.pjsip.pjsua2.StringVector;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;

public class SipPhone extends Service implements  MyAppObserver, Handler.Callback {

    public static MyApp app = null;
    public static MyCall currentCall = null;
    public static MyAccount account = null;
    public static AccountConfig accCfg = null;
    private final Handler handler = new Handler(this);

    Roster roster;
    public class MSG_TYPE
    {
        public final static int INCOMING_CALL = 1;
        public final static int CALL_STATE = 2;
        public final static int REG_STATE = 3;
        public final static int BUDDY_STATE = 4;
        public final static int CALL_MEDIA_STATE = 5;
        public final static int CHANGE_NETWORK = 6;
    }

    public SipPhone() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ServiceonStartComand", "start");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
      return   new Binder();
    }
    @Override
    public void onCreate() {
        app = new MyApp();
        app.init(this);

        accCfg = new AccountConfig();
        accCfg.setIdUri("sip:6002@192.168.0.27");
        accCfg.getNatConfig().setIceEnabled(true);
        accCfg.getVideoConfig().setAutoTransmitOutgoing(true);
        accCfg.getVideoConfig().setAutoShowIncoming(true);
        account = app.addAcc(accCfg);
        accCfg.getRegConfig().setRegistrarUri("sip:192.168.0.27");
        AuthCredInfoVector creds = accCfg.getSipConfig().
                getAuthCreds();

        creds.clear();
        creds.add(new AuthCredInfo("Digest", "*", "6002", 0,
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
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message m) {

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

            } else if (m.what == MSG_TYPE.CALL_MEDIA_STATE) {

                {
                    CallInfo ci = (CallInfo) m.obj;

                    if (currentCall == null || ci == null || ci.getId() != currentCall.getId()) {
                        System.out.println("Call state event received, but call info is invalid");
                        return true;
                    }

                    if (CallActivity.handler_ != null) {
                        android.os.Message m2 = android.os.Message.obtain(CallActivity.handler_, MSG_TYPE.CALL_STATE, ci);
                        m2.sendToTarget();
                    }


                }
            } else if (m.what == MSG_TYPE.BUDDY_STATE) {

                MyBuddy buddy = (MyBuddy) m.obj;
                int idx = account.buddyList.indexOf(buddy);
            } else if (m.what == MSG_TYPE.REG_STATE) {

                String msg_str = (String) m.obj;
                //lastRegStatus = msg_str;

            } else if (m.what == MSG_TYPE.INCOMING_CALL) {

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


                    call.answer(prm);
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

    @Override
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

        android.os.Message m = android.os.Message.obtain(handler, SipPhone.MSG_TYPE.REG_STATE, msg_str);
        m.sendToTarget();
    }

    @Override
    public void notifyIncomingCall(MyCall call) {
        Log.d("NotifyIncoming", "Call");
        android.os.Message m = android.os.Message.obtain(handler, SipPhone.MSG_TYPE.INCOMING_CALL, call);
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

        android.os.Message m = android.os.Message.obtain(handler, SipPhone.MSG_TYPE.CALL_STATE, ci);
        m.sendToTarget();
    }

    @Override
    public void notifyCallMediaState(MyCall call) {
        android.os.Message m = android.os.Message.obtain(handler, SipPhone.MSG_TYPE.CALL_MEDIA_STATE, null);
        m.sendToTarget();
    }

    @Override
    public void notifyBuddyState(MyBuddy buddy) {

    }

    @Override
    public void notifyChangeNetwork() {

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("onTaskRemovedServ", "onTaskRemovedServ");

        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            MessageReceiver myres = new MessageReceiver();
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("startSchedule", "start");
            myres.onReceive(ServiceChat.this, broadcastIntent);

        } else {
            MessageReceiver myres = new MessageReceiver();
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("startSchedule", "start");
            myres.onReceive(ServiceChat.this, broadcastIntent);

        }*/
        app.deinit();
        if (currentCall!=null) {
            currentCall.delete();
            currentCall = null;
        }
    }
}