package com.example.projectchat.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.example.projectchat.App;
import com.example.projectchat.MessageListAdapter;
import com.example.projectchat.PaginationListener;
import com.example.projectchat.R;
import com.example.projectchat.RoomDB.message_chattest17;
import com.example.projectchat.RoomDB.tmp_outmessage17;
import com.example.projectchat.UserMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static com.example.projectchat.PaginationListener.PAGE_START;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.time.EntityTimeManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

public class PlaceholderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    String login1;
    String login2_group;
    EditText edttxt;
    List<UserMessage> messageList = new ArrayList<>();
    List<message_chattest17> messageList1 = new ArrayList<>();
    List<message_chattest17> messageListGroup1 = new ArrayList<>();
    static final int GALLERY_REQUEST = 1;
    Uri selectImage;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter messageListAdapter;
    boolean bound;
    String userNick, userjid, nickgroup;
    String myNick, userType;
    String result = "";
    message_chattest17 message_chattest_17;
    tmp_outmessage17 tmpOutmessage17;

    SwipeRefreshLayout swipeRefreshLayout;

    private boolean isLastPage = false;
    private boolean isLoading = false;

    int itemCount = 0;
    int itemMinus = 0;
    int totalPage = 1;
    int k = 0;
    int m = 0;
    int j = 0;

    private int currentPage = PAGE_START;
    LinearLayoutManager layoutManager;

    String type;
    Chat chat;
    public interface onSomeEventListener {
        public void SomeEvent(Bundle s);
    }

    onSomeEventListener someEventListener;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            someEventListener = (onSomeEventListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreatePlaceHolder", "onCreatePlaceHolder");
        pageViewModel = ViewModelProviders.of(getActivity()).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        Executors.newSingleThreadExecutor().execute(new Runnable(){

            @Override
            public void run() {
                login1 = App.getComponent().database().myLoginsDao().my_user_name();
            }
        });

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d("onCreateView", "onCreateView");
        View root = inflater.inflate(R.layout.fragment_new_chat_page, container, false);
        ImageButton btn = (ImageButton) root.findViewById(R.id.sendMess);
        ImageButton getPic = (ImageButton)  root.findViewById(R.id.getPic);
        edttxt = (EditText) root.findViewById(R.id.edittextchatbox);
        mMessageRecycler = (RecyclerView) root.findViewById(R.id.recycle_message_list);
        messageListAdapter = new MessageListAdapter(getContext(), messageList);

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        mMessageRecycler.setLayoutManager(layoutManager);
        mMessageRecycler.setAdapter(messageListAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefresh);
        // swipeRefreshLayout.setOnRefreshListener(this);


        pageViewModel.getBundleMutableLiveDataUser().observe(getViewLifecycleOwner(), new Observer<Bundle>() {
            @Override
            public void onChanged(Bundle bundle) {
                switch (bundle.getString("newuser")) {
                    case "yes":
                        Log.d("newuseryes", "" + bundle.getString("userjid"));
                        userjid = bundle.getString("userjid");
                        userNick = bundle.getString("userNick");
                        break;
                    case "no":
                        Log.d("newuserno", "" + bundle.getString("userNick"));
                        userNick = bundle.getString("userNick");
                        break;
                }
            }
        });

        pageViewModel.getBundleMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Bundle>() {
            @Override

            public void onChanged(Bundle bundle) {
                Log.d("bundleObserve", bundle.getString("what"));
                switch (bundle.getString("what")) {
                    case "msg_hello":
                        if (userjid.equals(bundle.getString("fromMessage")) && userType.matches("chat")) {
                            messageList = new ArrayList<>();
                            messageList.add(new UserMessage(bundle.getString("fromServ"), userNick, bundle.getString("datetime"), 2, null, null));
                            mMessageRecycler.setLayoutManager(layoutManager);
                            messageListAdapter.addItem(messageList);
                        }
                        break;
                    case "getImage":
                        //-----
                        String pathofFile=bundle.getString("PathOfImage");
                        String datetime=bundle.getString("datetime");
                        String type=pathofFile.substring(pathofFile.lastIndexOf('.'), pathofFile.length());
                        Log.d("typeofFile ", type);
                        Log.d("typeofFile1111 ", ""+pathofFile);
                        File file = new File(pathofFile);


                        if (file.exists()) {

                            if (type.matches(".jpeg") || type.matches(".png") || type.matches(".jpg") || type.matches(".JPG")) {
                                messageList = new ArrayList<>();
                                mMessageRecycler.setLayoutManager(layoutManager);
                                messageList.add(new UserMessage(null, null, datetime, 3, pathofFile, null));
                            } else if (type.matches(".mp4")) {
                                messageList = new ArrayList<>();
                                mMessageRecycler.setLayoutManager(layoutManager);
                                messageList.add(new UserMessage(null, null, datetime, 5, pathofFile, null));
                            }
                            messageListAdapter.addItem(messageList);
                        }

                       /* if (file.exists()) {
                            messageList = new ArrayList<>();
                            mMessageRecycler.setLayoutManager(layoutManager);
                            messageList.add(new UserMessage(null, userNick, bundle.getString("datetime"), 3, bundle.getString("PathOfImage"), null));

                        }*/
                        break;
                    case "message_to_group":
                        login2_group = bundle.getString("getFrom");
                        if (!(login1.matches(login2_group + "@192.168.0.26"))) {// && userjid.equals(bundle.getString("togroup")) && userType.matches("group")) {
                            messageList = new ArrayList<>();
                            mMessageRecycler.setLayoutManager(layoutManager);
                            messageList.add(new UserMessage(bundle.getString("newmess"), bundle.getString("getFrom"), bundle.getString("datetime"), 9, null, null));
                            messageListAdapter.addItem(messageList);
                        }
                        break;
                    case "getImageGroup":

                        login2_group = bundle.getString("getFrom");
                        Log.d("path", "" + bundle.getString("PathOfImage"));
                        String pathofFilegroup=bundle.getString("PathOfImage");
                        String datetimegroup=bundle.getString("datetime");
                        String typegroup=pathofFilegroup.substring(pathofFilegroup.lastIndexOf('.'), pathofFilegroup.length());
                        Log.d("typeofFile ", typegroup);
                        Log.d("typeofFile1111 ", ""+pathofFilegroup);
                        Log.d("typeofFile1111 ", ""+login2_group);
                        File file1 = new File(pathofFilegroup);


                        if (file1.exists()) {

                            if (typegroup.matches(".jpeg") || typegroup.matches(".png") || typegroup.matches(".jpg") || typegroup.matches(".JPG")) {
                                messageList = new ArrayList<>();
                                mMessageRecycler.setLayoutManager(layoutManager);
                                messageList.add(new UserMessage(null, login2_group, datetimegroup, 7, pathofFilegroup, null));

                            } else if (typegroup.matches(".mp4")) {
                                messageList = new ArrayList<>();
                                mMessageRecycler.setLayoutManager(layoutManager);
                                messageList.add(new UserMessage(null, login2_group, datetimegroup, 8, pathofFilegroup, null));
                            }
                            messageListAdapter.addItem(messageList);
                        }

                        Log.d("received Image", "" + file1.getName());
                        break;
                    case "getJid":
                        break;
                }
            }
        });


        switch (pageViewModel.getBundleMutableLiveDataUser().getValue().getString("newuser")) {
            case "yes":
                Log.d("newYes", "yes");

                userType = "chat";
                userjid = pageViewModel.getBundleMutableLiveDataUser().getValue().getString("userjid");
                myNick = pageViewModel.getBundleMutableLiveDataUser().getValue().getString("mynick");
                Completable.fromAction(() -> {
                    login1 = App.getComponent().database().myLoginsDao().my_user_name();
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
                break;

            case "no":
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy:MM:dd HH:mm");
                Log.d("no", "no");

                userNick = pageViewModel.getBundleMutableLiveDataUser().getValue().getString("userNick");

                Completable.fromAction(() -> {
                    //userjid = App.getComponent().database().message_withtestDAO().getJid(userNick);
                    userjid = pageViewModel.getBundleMutableLiveDataUser().getValue().getString("userjid");
                    Log.d("userJidP", "yes "+userjid);
                    chat=ChatManager.getInstanceFor(App.getComponent().userauth().getS()).chatWith(JidCreate.entityBareFrom(userjid));
                    userType = App.getComponent().database().message_withtestDAO().getTypeChat(userjid);
                    login1 = App.getComponent().database().myLoginsDao().my_user_name();
                    Log.d("Login1 ", ""+login1);
                    userType="chat";
                  //  myNick = App.getComponent().database().myLoginsDao().my_nick();
                    if (userType.matches("chat")) {
                        messageList1 = new ArrayList<>();
                        messageList1 = App.getComponent().database().mesagechatest_Dao().getData(login1, userjid);
                    } else if (userType.matches("group")) {
                        messageListGroup1 = new ArrayList<>();
                        messageListGroup1 = App.getComponent().database().mesagechatest_Dao().getDataGroup(userjid);
                    }


                                switch (userType) {
                                    case "chat":

                                        if (messageList1.size() % 10 == 0) {
                                            totalPage = messageList1.size() / 10;
                                        } else {
                                            totalPage = (messageList1.size() / 10) + 1;
                                        }

                                        messageList = new ArrayList<>();
                                        if (messageList1.size() >= 10) {
                                            itemMinus = 10;
                                        } else {
                                            itemMinus = messageList1.size();
                                        }
                                        for (int i = 0; i < itemMinus; i++) {
                                            itemCount++;
                                            if (messageList1.get(i).getNameFrom().matches(login1)) {
                                                if (messageList1.get(i).picture1 != null) {
                                                    String pathofFile=messageList1.get(i).picture1;
                                                    String type=pathofFile.substring(pathofFile.lastIndexOf('.'), pathofFile.length());

                                                    if (type.matches(".jpeg") || type.matches(".png") || type.matches(".jpg") || type.matches(".JPG")) {
                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageList1.get(i).timestampid)));
                                                        messageList.add(new UserMessage(null, myNick, currentDateTime, 4, null, messageList1.get(i).picture1));
                                                    } else if (type.matches(".mp4")){
                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageList1.get(i).timestampid)));
                                                        messageList.add(new UserMessage(null, myNick, currentDateTime, 6, null, messageList1.get(i).picture1));
                                                    }
                                                } else if (messageList1.get(i).picture1 == null && messageList1.get(i).body1 != null) {
                                                    mMessageRecycler.setLayoutManager(layoutManager);
                                                    String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageList1.get(i).timestampid)));
                                                    messageList.add(new UserMessage(messageList1.get(i).body1, myNick, currentDateTime, 1, null, null));
                                                }
                                            } else if (messageList1.get(i).getNameFrom().matches(userjid)) {
                                                if (messageList1.get(i).picture1 != null) {

                                                    String pathofFile=messageList1.get(i).picture1;
                                                    String type=pathofFile.substring(pathofFile.lastIndexOf('.'), pathofFile.length());
                                                    Log.d("typeofFile ", type);
                                                    String extension= MimeTypeMap.getFileExtensionFromUrl(pathofFile);

                                                    if (type.matches(".jpeg") || type.matches(".png") || type.matches(".jpg") || type.matches(".JPG")) {
                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageList1.get(i).timestampid)));
                                                        messageList.add(new UserMessage(null, userNick, currentDateTime, 3, messageList1.get(i).picture1, null));
                                                    }else if (type.matches(".mp4")){
                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageList1.get(i).timestampid)));
                                                        messageList.add(new UserMessage(null, myNick, currentDateTime, 5, messageList1.get(i).picture1, null));
                                                    }
                                                } else if (messageList1.get(i).picture1 == null && messageList1.get(i).body1 != null) {

                                                    mMessageRecycler.setLayoutManager(layoutManager);
                                                    String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageList1.get(i).timestampid)));
                                                    messageList.add(new UserMessage(messageList1.get(i).body1, userNick, currentDateTime, 2, null, null));
                                                }
                                            }
                                            k = i;

                                        }
                                        if (currentPage != PAGE_START) messageListAdapter.removeLoading();
                                        messageListAdapter.addItems(messageList);
                                        if (currentPage < totalPage) {
                                        } else {
                                            isLastPage = true;
                                        }
                                        isLoading = false;

                                        mMessageRecycler.addOnScrollListener(new PaginationListener(layoutManager) {
                                            @Override
                                            protected void loadMoreItems() {
                                                isLoading = true;
                                                itemMinus = messageList1.size() - itemCount;
                                                if (itemMinus >= 10) {
                                                    itemMinus = 10;
                                                    m = itemCount + 10;
                                                } else {
                                                    itemMinus = messageList1.size();
                                                    m = messageList1.size();
                                                }

                                                currentPage++;
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        messageList = new ArrayList<>();
                                                        int l = k;

                                                        for (int i = l; i < m; i++) {
                                                            itemCount++;
                                                            if (messageList1.get(i).getNameFrom().matches(login1)) {
                                                                if (messageList1.get(i).picture1 != null) {
                                                                    String pathofFile=messageList1.get(i).picture1;
                                                                    String type=pathofFile.substring(pathofFile.lastIndexOf('.'), pathofFile.length());
                                                                    Log.d("typeofFile ", type);
                                                                    String extension= MimeTypeMap.getFileExtensionFromUrl(pathofFile);
                                                                    if (extension!=null){
                                                                        Log.d("MimeType", ""+MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
                                                                    }
                                                                    if (type.matches(".jpeg") || type.matches(".png") || type.matches(".jpg") || type.matches(".JPG")) {
                                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageList1.get(i).timestampid)));
                                                                        messageList.add(new UserMessage(null, userNick, currentDateTime, 3, messageList1.get(i).picture1, null));
                                                                    }else if (type.matches(".mp4")){
                                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageList1.get(i).timestampid)));
                                                                        messageList.add(new UserMessage(null, myNick, currentDateTime, 5, null, messageList1.get(i).picture1));
                                                                    }
                                                                }
                                                            } else if (messageList1.get(i).getNameFrom().matches(userjid)) {
                                                                if (messageList1.get(i).picture1 != null) {
                                                                    String pathofFile=messageList1.get(i).picture1;
                                                                    String type=pathofFile.substring(pathofFile.lastIndexOf('.'), pathofFile.length());
                                                                    Log.d("typeofFile ", type);
                                                                    String extension= MimeTypeMap.getFileExtensionFromUrl(pathofFile);
                                                                    if (extension!=null){
                                                                        Log.d("MimeType", ""+MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
                                                                    }
                                                                    if (type.matches(".jpeg") || type.matches(".png") || type.matches(".jpg") || type.matches(".JPG")) {
                                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageList1.get(i).timestampid)));
                                                                        messageList.add(new UserMessage(null, userNick, currentDateTime, 3, messageList1.get(i).picture1, null));
                                                                    }else if (type.matches(".mp4")){
                                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageList1.get(i).timestampid)));
                                                                        messageList.add(new UserMessage(null, myNick, currentDateTime, 5, null, messageList1.get(i).picture1));
                                                                    }
                                                                }
                                                            }

                                                            k = i;
                                                        }



                                                        if (currentPage != PAGE_START)
                                                            messageListAdapter.removeLoading();
                                                        messageListAdapter.addItems(messageList);

                                                        swipeRefreshLayout.setRefreshing(false);
                                                        if (currentPage < totalPage) {
                                                            //messageListAdapter.addLoading();
                                                        } else {
                                                            isLastPage = true;
                                                            swipeRefreshLayout.setRefreshing(false);
                                                        }
                                                        isLoading = false;
                                                    }
                                                }, 1500);
                                            }

                                            @Override
                                            public boolean isLastPage() {
                                                Log.d("isLastPage", "" + isLastPage);
                                                return isLastPage;
                                            }

                                            @Override
                                            public boolean isLoading() {
                                                Log.d("isLoading", "" + isLoading);
                                                return isLoading;
                                            }
                                        });

                                        break;
                                    case "group":

                                        Log.d("messageListGroup1", "" + messageListGroup1.size());
                                        //Log.d("messageSize", "" + currentPage);
                                        Log.d("Delenie", "" + messageListGroup1.size() / 10);
                                        if (messageListGroup1.size() % 10 == 0) {
                                            Log.d("Delenie", "" + messageListGroup1.size() / 10);
                                            totalPage = messageListGroup1.size() / 10;
                                        } else {
                                            Log.d("Delenie", "" + (double) messageListGroup1.size() / 10);
                                            totalPage = (messageListGroup1.size() / 10) + 1;
                                        }

                                        messageList = new ArrayList<>();
                                        if (messageListGroup1.size() >= 10) {
                                            itemMinus = 10;
                                        } else {
                                            itemMinus = messageListGroup1.size();
                                        }

                                        Log.d("itemMinus", "" + itemMinus);
                                        for (int i = 0; i < itemMinus; i++) {
                                            j = i;
                                            Log.d("itemMinusJ", "" + j);
                                            itemCount++;
                                            if (messageListGroup1.get(i).getNameFrom().matches(login1)) {

                                                if (messageListGroup1.get(i).picture1 != null) {
                                                    String pathofFile=messageListGroup1.get(i).picture1;
                                                    String type=pathofFile.substring(pathofFile.lastIndexOf('.'), pathofFile.length());

                                                    if (type.matches(".jpeg") || type.matches(".png") || type.matches(".jpg") || type.matches(".JPG")) {
                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageListGroup1.get(i).timestampid)));
                                                        messageList.add(new UserMessage(null, myNick, currentDateTime, 4, null, messageListGroup1.get(i).picture1));
                                                    } else if (type.matches(".mp4")){
                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageListGroup1.get(i).timestampid)));
                                                        messageList.add(new UserMessage(null, myNick, currentDateTime, 6, null, messageListGroup1.get(i).picture1));
                                                    }

                                                } else if (messageListGroup1.get(i).picture1 == null && messageListGroup1.get(i).body1 != null) {
                                                    mMessageRecycler.setLayoutManager(layoutManager);
                                                    String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageListGroup1.get(i).timestampid)));
                                                    messageList.add(new UserMessage(messageListGroup1.get(i).body1, null, currentDateTime, 1, null, null));
                                                }
                                            } else if (!(messageListGroup1.get(i).getNameFrom().matches(login1))) {

                                                //---------------------------------------------------------------------------------------------------------------------------
                                                Completable.fromAction(new Action() {
                                                    @Override
                                                    public void run() throws Exception {
                                                        nickgroup = App.getComponent().database().message_withtestDAO().getNick(messageListGroup1.get(j).getNameFrom());
                                                        Log.d("picturenickDB", "" + nickgroup);
                                                    }
                                                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());//.subscribe();

                                                //---------------------------------------------------------------------------------------------------------------------------------

                                                nickgroup = App.getComponent().database().message_withtestDAO().getNick(messageListGroup1.get(j).getNameFrom());

                                                if (messageListGroup1.get(i).picture1 != null) {

                                                    String pathofFile=messageListGroup1.get(i).picture1;
                                                    Log.d("picturenick", "" + pathofFile);
                                                    String type=pathofFile.substring(pathofFile.lastIndexOf('.'), pathofFile.length());
                                                    Log.d("typeofFile ", type);
                                                    String extension= MimeTypeMap.getFileExtensionFromUrl(pathofFile);
                                                    if (extension!=null){
                                                        Log.d("MimeType", ""+MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
                                                    }

                                                    if (type.matches(".jpeg") || type.matches(".png") || type.matches(".jpg") || type.matches(".JPG")) {
                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageListGroup1.get(i).timestampid)));
                                                        messageList.add(new UserMessage(null, messageListGroup1.get(i).getNameFrom(), currentDateTime, 7, messageListGroup1.get(i).picture1, null));
                                                    }else if (type.matches(".mp4")){
                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageListGroup1.get(i).timestampid)));
                                                        messageList.add(new UserMessage(null, messageListGroup1.get(i).getNameFrom(), currentDateTime, 8, messageListGroup1.get(i).picture1, null));
                                                    }

                                                } else if (messageListGroup1.get(i).picture1 == null && messageListGroup1.get(i).body1 != null) {
                                                    Log.d("getNameFrom", "" + messageListGroup1.get(i).getNameFrom());
                                                    mMessageRecycler.setLayoutManager(layoutManager);
                                                    String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageListGroup1.get(i).getDate())));
                                                    messageList.add(new UserMessage(messageListGroup1.get(i).body1, messageListGroup1.get(i).getNameFrom(), currentDateTime, 9, null, null));
                                                }
                                            }
                                            k = j;

                                        }
                                        if (currentPage != PAGE_START) messageListAdapter.removeLoading();
                                        messageListAdapter.addItems(messageList);
                                        if (currentPage < totalPage) {
                                        } else {
                                            isLastPage = true;
                                        }
                                        isLoading = false;

                                        mMessageRecycler.addOnScrollListener(new PaginationListener(layoutManager) {
                                            @Override
                                            protected void loadMoreItems() {
                                                isLoading = true;
                                                itemMinus = messageListGroup1.size() - itemCount;
                                                if (itemMinus >= 10) {
                                                    itemMinus = 10;
                                                    m = itemCount + 10;
                                                } else {
                                                    itemMinus = messageListGroup1.size();
                                                    m = messageListGroup1.size();
                                                }
                                                currentPage++;
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        messageList = new ArrayList<>();
                                                        int l = k;

                                                        for (int i = l; i < m; i++) {
                                                            j = i;
                                                            itemCount++;
                                                            if (messageListGroup1.get(j).getNameFrom().matches(login1)) {

                                                                if (messageListGroup1.get(i).picture1 != null) {
                                                                    Log.d("picturenotempty", "" + messageListGroup1.get(i).picture1);
                                                                    mMessageRecycler.setLayoutManager(layoutManager);
                                                                    String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageListGroup1.get(i).timestampid)));
                                                                    messageList.add(new UserMessage(null, myNick, currentDateTime, 4, null, messageListGroup1.get(i).picture1));
                                                                } else if (messageListGroup1.get(i).picture1 == null && messageListGroup1.get(i).body1 != null) {
                                                                    mMessageRecycler.setLayoutManager(layoutManager);
                                                                    String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageListGroup1.get(i).timestampid)));
                                                                    messageList.add(new UserMessage(messageListGroup1.get(i).body1, myNick, currentDateTime, 1, null, null));
                                                                }
                                                            } else if (!(messageListGroup1.get(i).getNameFrom().matches(login1))) {
                                                                Completable.fromAction(new Action() {
                                                                    @Override
                                                                    public void run() throws Exception {
                                                                        nickgroup = App.getComponent().database().message_withtestDAO().getNick(messageListGroup1.get(j).getNameFrom());
                                                                        Log.d("picturenickDB", "" + nickgroup);
                                                                    }
                                                                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());//.subscribe();

                                                                //---------------------------------------------------------------------------------------------------------------------------------

                                                                nickgroup = App.getComponent().database().message_withtestDAO().getNick(messageListGroup1.get(j).getNameFrom());

                                                                if (messageListGroup1.get(i).picture1 != null) {

                                                                    String pathofFile=messageListGroup1.get(i).picture1;
                                                                    Log.d("picturenick", "" + pathofFile);
                                                                    String type=pathofFile.substring(pathofFile.lastIndexOf('.'), pathofFile.length());
                                                                    Log.d("typeofFile ", type);
                                                                    String extension= MimeTypeMap.getFileExtensionFromUrl(pathofFile);
                                                                    if (extension!=null){
                                                                        Log.d("MimeType", ""+MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
                                                                    }

                                                                    if (type.matches(".jpeg") || type.matches(".png") || type.matches(".jpg") || type.matches(".JPG")) {
                                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageListGroup1.get(i).timestampid)));
                                                                        messageList.add(new UserMessage(null, messageListGroup1.get(i).getNameFrom(), currentDateTime, 7, messageListGroup1.get(i).picture1, null));
                                                                    }else if (type.matches(".mp4")){
                                                                        mMessageRecycler.setLayoutManager(layoutManager);
                                                                        String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageListGroup1.get(i).timestampid)));
                                                                        messageList.add(new UserMessage(null, messageListGroup1.get(i).getNameFrom(), currentDateTime, 8, messageListGroup1.get(i).picture1, null));
                                                                    }

                                                                } else if (messageListGroup1.get(i).picture1 == null && messageListGroup1.get(i).body1 != null) {
                                                                    Log.d("getNameFrom", "" + messageListGroup1.get(i).getNameFrom());
                                                                    mMessageRecycler.setLayoutManager(layoutManager);
                                                                    String currentDateTime = simpleDateFormat1.format(new Date(Long.parseLong(messageListGroup1.get(i).getDate())));
                                                                    messageList.add(new UserMessage(messageListGroup1.get(i).body1, messageListGroup1.get(i).getNameFrom(), currentDateTime, 9, null, null));
                                                                }
                                                            }
                                                            k = j;
                                                        }

                                                        if (currentPage != PAGE_START)
                                                            messageListAdapter.removeLoading();
                                                        messageListAdapter.addItems(messageList);

                                                        swipeRefreshLayout.setRefreshing(false);
                                                        if (currentPage < totalPage) {
                                                            //messageListAdapter.addLoading();
                                                        } else {
                                                            isLastPage = true;
                                                            swipeRefreshLayout.setRefreshing(false);
                                                        }
                                                        isLoading = false;
                                                    }
                                                }, 1500);
                                            }

                                            @Override
                                            public boolean isLastPage() {
                                                Log.d("isLastPage", "" + isLastPage);
                                                return isLastPage;
                                            }

                                            @Override
                                            public boolean isLoading() {
                                                Log.d("isLoading", "" + isLoading);
                                                return isLoading;
                                            }
                                        });

                                        break;
                                }


                            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
                break;
        }
//-------------------------------------------------------------------------

        edttxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               /* if (i2>i1){
                    try {
                        Log.d("getUser ", ""+App.getComponent().userauth().getS().getUser());
                        ChatStateManager.getInstance(App.getComponent().userauth().getS()).setCurrentState(ChatState.composing, chat);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (i2<=i1){
                    try {
                        ChatStateManager.getInstance(App.getComponent().userauth().getS()).setCurrentState(ChatState.gone, chat);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("Editable ", ""+editable);

            }
        });

        btn.setOnClickListener(view -> {
            Log.d("btnuserjid", "" + userjid);
            Log.d("btnuserjid", "" + userType);
            switch (userType) {
                case "chat":
                    Log.d("btnuserjid", "" + userjid);
                    if (edttxt.getText().toString().trim().equals("")) {
                        return;
                    }
                    mMessageRecycler.setLayoutManager(layoutManager);
                    //mMessageRecycler.setAdapter(messageListAdapter);
                    messageList = new ArrayList<>();

                    messageList.add(new UserMessage(edttxt.getText().toString(), myNick, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()), 1, null, null));
                    messageListAdapter.addItem(messageList);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    Date date = new Date();
                    Timestamp ts = new Timestamp(date.getTime());
                    Log.d("outstamp", "" + ts.getTime());

                    org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
                    message.setBody(edttxt.getText().toString());

                    message_chattest_17 = new message_chattest17();
                    message_chattest_17.from1 = login1;
                    message_chattest_17.to1 = userjid;
                    message_chattest_17.body1 = edttxt.getText().toString();
                    message_chattest_17.timestampid = String.valueOf(ts.getTime());
                    message_chattest_17.idstanza1 = message.getStanzaId();

                    Completable.fromAction(new Action() {
                        @Override
                        public void run() throws Exception {
                            App.getComponent().database().mesagechatest_Dao().insert(message_chattest_17);
                            App.getComponent().database().message_withtestDAO().updateDBUser(String.valueOf(ts.getTime()), userjid);

                        }
                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

                    Bundle bundle = new Bundle();
                    bundle.putString("to", userjid);
                    bundle.putString("message", edttxt.getText().toString());
                    bundle.putString("from", login1);
                    bundle.putString("type", "chat");
                    bundle.putString("timestamp", String.valueOf(ts.getTime()));
                    bundle.putString("stanzaId", message.getStanzaId());

                    tmpOutmessage17 = new tmp_outmessage17();
                    tmpOutmessage17.tmpto = userjid;
                    tmpOutmessage17.tmpbody = edttxt.getText().toString();
                    tmpOutmessage17.idstanzatmp = String.valueOf(ts.getTime());
                    tmpOutmessage17.idtmpbody = message.getStanzaId();
                    tmpOutmessage17.tmptype = "chat";

                    Completable.fromAction(() -> App.getComponent().database().tmp_messageoutDAO().insert(tmpOutmessage17)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

                    someEventListener.SomeEvent(bundle);
                    break;

                case "group":


                    mMessageRecycler.setLayoutManager(layoutManager);
                    messageList = new ArrayList<>();

                    messageList.add(new UserMessage(edttxt.getText().toString(), myNick, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()), 1, null, null));
                    messageListAdapter.addItem(messageList);

                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    Date date1 = new Date();
                    Timestamp ts1 = new Timestamp(date1.getTime());
                    Log.d("outstamp", "" + ts1.getTime());
                    Log.d("outstamp", "" + edttxt.getText().toString());

                    org.jivesoftware.smack.packet.Message message2 = new org.jivesoftware.smack.packet.Message();
                    message2.setBody(edttxt.getText().toString());

                    message_chattest_17 = new message_chattest17();
                    message_chattest_17.from1 = login1;
                    message_chattest_17.to1 = userjid;
                    message_chattest_17.body1 = edttxt.getText().toString();
                    message_chattest_17.timestampid = String.valueOf(ts1.getTime());
                    message_chattest_17.idstanza1 = message2.getStanzaId();

                    Completable.fromAction(new Action() {
                        @Override
                        public void run() throws Exception {
                            App.getComponent().database().mesagechatest_Dao().insert(message_chattest_17);
                            App.getComponent().database().message_withtestDAO().updateDBUser(String.valueOf(ts1.getTime()), userjid);
                        }
                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

                    Bundle bundle2 = new Bundle();
                    bundle2.putString("to", userjid);
                    bundle2.putString("message", edttxt.getText().toString());
                    bundle2.putString("from", login1);
                    bundle2.putString("type", "group");
                    bundle2.putString("timestamp", String.valueOf(ts1.getTime()));
                    bundle2.putString("stanzaId", message2.getStanzaId());

                    tmpOutmessage17 = new tmp_outmessage17();
                    tmpOutmessage17.tmpto = userjid;
                    tmpOutmessage17.tmpbody = edttxt.getText().toString();
                    tmpOutmessage17.idstanzatmp = String.valueOf(ts1.getTime());
                    tmpOutmessage17.idtmpbody = message2.getStanzaId();
                    tmpOutmessage17.tmptype = "group";
                    Completable.fromAction(() -> App.getComponent().database().tmp_messageoutDAO().insert(tmpOutmessage17)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
                    someEventListener.SomeEvent(bundle2);

                    break;
            }
            edttxt.getText().clear();
        });


        getPic.setOnClickListener(view -> {
            Intent photoGetIntent = new Intent(Intent.ACTION_PICK);
            photoGetIntent.putExtra("userType", userType);
            photoGetIntent.setType("image/* video/*");
            startActivityForResult(photoGetIntent, GALLERY_REQUEST);
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imegeReturn) {
        super.onActivityResult(requestCode, resultCode, imegeReturn);
        Bitmap bitmap = null;

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST:
                    selectImage = imegeReturn.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectImage);
                        Log.d("picture", "=" + selectImage.toString());
                        Log.d("requestCode", "=" + requestCode);

                        getRealPath(selectImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void getRealPath(Uri contentURI) {

        Cursor cursor = getContext().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
        }

        Log.d("result", "" + result);
        Log.d("result", "" + bound);

        switch (userType) {
            case "chat":


                try {
                org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
                String servertime= null;

                EntityTimeManager tim=EntityTimeManager.getInstanceFor(App.getComponent().userauth().getS());
                Log.d("Timestamp ", ""+tim);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");


                    long timestamp=tim.getTime(App.getComponent().userauth().getS().getXMPPServiceDomain()).getTime().getTime();
                    servertime = simpleDateFormat.format(timestamp);
                    File filecopy;

                 type=result.substring(result.lastIndexOf('.'), result.length());
                    if (type.matches(".heic")){
                        Log.d("type ", ""+type);
                        File file = new File(result);
                        String fileout= convertI(servertime);

                        filecopy = new File(fileout);
                        //copyFile(file, filecopy);

                    }else {
                        File file = new File(result);
                         filecopy = new File(Environment.getExternalStorageDirectory() + File.separator + "mfchat" + File.separator + "ChatMFC" + servertime + type);
                        copyFile(file, filecopy);
                    }
                  //  Log.d("pathFile ", ""+filecopy.getPath());
                tmpOutmessage17 = new tmp_outmessage17();
                tmpOutmessage17.tmpto = userjid;
                tmpOutmessage17.tmppicture1 = filecopy.getPath();
                tmpOutmessage17.idstanzatmp = String.valueOf(timestamp);
                tmpOutmessage17.idtmpbody = message.getStanzaId();
                tmpOutmessage17.tmptype = "chat";

                message_chattest_17 = new message_chattest17();
                message_chattest_17.from1 = login1;
                message_chattest_17.to1 = userjid;
                message_chattest_17.picture1 = filecopy.getPath();
                message_chattest_17.timestampid = String.valueOf(timestamp);
                message_chattest_17.idstanza1 = message.getStanzaId();
                Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Exception {
                        App.getComponent().database().mesagechatest_Dao().insert(message_chattest_17);
                        App.getComponent().database().message_withtestDAO().updateDBUser(String.valueOf(timestamp), userjid);
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

                Completable.fromAction(() -> App.getComponent().database().tmp_messageoutDAO().insert(tmpOutmessage17)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

                Bundle bundle = new Bundle();
                bundle.putString("type", "sendPicChat");
                bundle.putString("fromClientPic", filecopy.getPath());
                bundle.putString("stanzaId", message.getStanzaId());
                bundle.putString("servertime", servertime);

                someEventListener.SomeEvent(bundle);

                messageList = new ArrayList<>();
                mMessageRecycler.setLayoutManager(layoutManager);

                if (type.matches(".jpeg") || type.matches(".png") || type.matches(".jpg") || type.matches(".JPG")) {
                    mMessageRecycler.setLayoutManager(layoutManager);
                    messageList.add(new UserMessage(null, myNick, servertime, 4, null, filecopy.getPath()));
                } else if (type.matches(".mp4")){
                    mMessageRecycler.setLayoutManager(layoutManager);
                    messageList.add(new UserMessage(null, myNick, servertime, 6, null, filecopy.getPath()));
                }else if (type.matches(".heic")){
                    mMessageRecycler.setLayoutManager(layoutManager);
                    messageList.add(new UserMessage(null, myNick, servertime, 4, null, filecopy.getPath()));
                }
                messageListAdapter.addItem(messageList);
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "group":
                try {
                org.jivesoftware.smack.packet.Message message2 = new org.jivesoftware.smack.packet.Message();

                String servertime= null;

                EntityTimeManager tim=EntityTimeManager.getInstanceFor(App.getComponent().userauth().getS());
                Log.d("Timestamp ", ""+tim);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");


                    long timestampgroup=tim.getTime(App.getComponent().userauth().getS().getXMPPServiceDomain()).getTime().getTime();

                servertime = simpleDateFormat.format(timestampgroup);


                String type=result.substring(result.lastIndexOf('.'), result.length());

                message2.setBody(edttxt.getText().toString());

                message_chattest_17 = new message_chattest17();
                message_chattest_17.from1 = login1;
                message_chattest_17.to1 = userjid;
                message_chattest_17.picture1 = result;
                message_chattest_17.timestampid = String.valueOf(timestampgroup);
                message_chattest_17.idstanza1 = message2.getStanzaId();

                Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Exception {
                        App.getComponent().database().mesagechatest_Dao().insert(message_chattest_17);
                        App.getComponent().database().message_withtestDAO().updateDBUser(String.valueOf(timestampgroup), userjid);

                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

                File file1 = new File(result);
                File filecopy = new File(Environment.getExternalStorageDirectory() +File.separator+"mfchat"+File.separator+"ChatMFC"+servertime+type);
                copyFile(file1, filecopy);
                Bundle bundle2 = new Bundle();
                bundle2.putString("to", userjid);
                bundle2.putString("type", "sendPicGroup");
                bundle2.putString("fromClientPic", filecopy.getPath());
                bundle2.putString("stanzaId", message2.getStanzaId());
                bundle2.putString("servertime", servertime);

                tmpOutmessage17 = new tmp_outmessage17();
                tmpOutmessage17.tmpto = userjid;
                tmpOutmessage17.tmpbody = edttxt.getText().toString();
                tmpOutmessage17.idstanzatmp = String.valueOf(timestampgroup);
                tmpOutmessage17.tmppicture1 = result;
                tmpOutmessage17.tmptype = "group";
                Completable.fromAction(() -> App.getComponent().database().tmp_messageoutDAO().insert(tmpOutmessage17)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
                someEventListener.SomeEvent(bundle2);

                messageList = new ArrayList<>();
                mMessageRecycler.setLayoutManager(layoutManager);

                if (type.matches(".jpeg") || type.matches(".png") || type.matches(".jpg") || type.matches(".JPG")) {
                    mMessageRecycler.setLayoutManager(layoutManager);
                    messageList.add(new UserMessage(null, myNick, servertime, 4, null, filecopy.getPath()));
                } else if (type.matches(".mp4")){
                    mMessageRecycler.setLayoutManager(layoutManager);
                    messageList.add(new UserMessage(null, myNick, servertime, 6, null, filecopy.getPath()));
                }
                messageListAdapter.addItem(messageList);
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("gruppa", "for group");
                break;
        }
    }

    public String compressImage(String imageUri, String neme){
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        Bitmap bmp=BitmapFactory.decodeFile(imageUri, options);
        int actualHeight=options.outHeight;
        int actualWidth=options.outWidth;

        float maxHeight=actualHeight;
        float maxWidth=actualWidth;

        float imageRatio=actualWidth/actualHeight;
        float maxRatio=maxWidth/maxHeight;

        if (actualHeight>maxHeight || actualWidth>maxWidth){
            if (imageRatio<maxRatio){
                imageRatio=maxHeight/actualHeight;
                actualWidth=(int)(imageRatio*actualWidth);
                actualHeight=(int)maxHeight;
            } else if (imageRatio>maxRatio){
                imageRatio=maxWidth/actualWidth;
                actualHeight=(int)(imageRatio*actualHeight);
                actualWidth=(int)maxWidth;
            }else {
                actualHeight=(int)maxHeight;
                actualWidth=(int)maxWidth;
            }
        }

        options.inSampleSize=caluclateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds=false;
        options.inPurgeable=true;
        options.inInputShareable=true;
        options.inTempStorage=new byte[16*1024];
        try{
            bmp=BitmapFactory.decodeFile(result, options);
        }catch(OutOfMemoryError exception){
            exception.printStackTrace();
        }
        try{
            scaledBitmap=Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        }catch(OutOfMemoryError exception){
            exception.printStackTrace();
        }

        float ratioX=actualWidth/(float)options.outWidth;
        float ratioY=actualHeight/(float)options.outHeight;
        float middleX=actualWidth/2.0f;
        float middleY=actualHeight/2.0f;

        Matrix scaleMatrix=new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas=new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth()/2, middleY-bmp.getHeight()/2, new Paint(Paint.FILTER_BITMAP_FLAG));
        ExifInterface exif;
        try{
            exif=new ExifInterface(result);
            int orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,0);
            Matrix matrix=new Matrix();
            if (orientation==6){
                matrix.postRotate(90);
            } else if (orientation==3){
                matrix.postRotate(180);
            } else if (orientation==8){
                matrix.postRotate(270);
            }
            scaledBitmap=Bitmap.createBitmap(scaledBitmap, 0,0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,true);

        }catch(IOException exception){
            exception.printStackTrace();
        }
        FileOutputStream out=null;
        String filename=getFileName(neme);
        try{
            out=new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
        }catch(FileNotFoundException exception){
            exception.printStackTrace();
        }
        return filename;
    }

    public String getFileName(String name){
        File file =new File(Environment.getExternalStorageDirectory()+File.separator, "mfchat");
        String uriString;
        if (!file.exists()){
            file.mkdir();
        }

        if ( type.matches(".heic")) {
            uriString = (file.getAbsolutePath() + "/" + "ChatMFC"+name + ".jpg");
        }else{
            uriString = (file.getAbsolutePath() + "/" + "ChatMFC"+name + type);
        }

        return uriString;
    }

    public static int caluclateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height=options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height>reqHeight || width>reqWidth){
            final int halfHeight=height/2;
            final int halfWidth = width/2;
            while ((halfHeight/inSampleSize)>=reqHeight && (halfWidth/inSampleSize)>=reqWidth) {
                inSampleSize*=2;
            }
        }
        return inSampleSize;
    }


    private  String convertI(String neme){
        FileInputStream inStream;
        String filename="";

            try {
                inStream = new FileInputStream(new File(result));


                try {
                    Metadata metadata = ImageMetadataReader.readMetadata(inStream);
                } catch (ImageProcessingException e) {
                    e.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                Bitmap picBitmap = BitmapFactory.decodeFile(result);
                FileOutputStream outStream;
                filename=getFileName(neme);
        outStream = new FileOutputStream(new File(filename));
        picBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outStream);

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            }
return  filename;
    }
    private void copyFile(File scr, File dst) {

        try {
            InputStream in = new FileInputStream(scr);
            OutputStream out = new FileOutputStream(dst);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }




    @Override
    public void onRefresh() {
        Log.d("onRefresh--", "oRefresh");
        if (currentPage >= totalPage) {
            swipeRefreshLayout.setRefreshing(false);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "onDestroy");
    }
}
