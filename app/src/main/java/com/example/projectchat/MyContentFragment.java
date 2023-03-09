package com.example.projectchat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projectchat.ui.main.PageViewModel;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubException;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MyContentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    String login1;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private PageViewModel pageViewModel;
    RecyclerView recyclerView;
    private newsPersonListAdapter newsPersonListAdapter;
    List<NewsPerson> newsPersonList = new ArrayList<>();
    LinearLayoutManager layoutManager;
    LeafNode node1 = null;


    public static MyContentFragment newInstance(String param1) {
        MyContentFragment fragment = new MyContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
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
                login1 = App.getComponent().database().myLoginsDao().my_nick();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_content, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycle_my_content);
        newsPersonListAdapter = new newsPersonListAdapter(getContext(), newsPersonList);

        layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(newsPersonListAdapter);
        //recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount());

        PubSubManager manager=App.getComponent().userauth().getPubSubManager();

        try {
            node1 = (LeafNode) manager.getNode(App.getComponent().userauth().getS().getUser().asEntityBareJidString());
            for (int i = 0; i < node1.getItems().size(); i++) {
                Log.d("PubsubItem1 ", "" + node1.getItems().get(i).toString());
                PayloadItem item= (PayloadItem) node1.getItems().get(i);
                ExtensionElement payload=item.getPayload();
                if (payload.getNamespace().matches("https://mycontent")) {

                    try {
                        XmlPullParser xmlPullParser = PacketParserUtils.getParserFor(payload.toXML("https://mycontent").toString());

                        Log.d("PubsubXml ", "" + xmlPullParser.nextTag().name());
                        String text=xmlPullParser.nextText();
                        Log.d("PubsubXml ", "" + xmlPullParser.nextTag().name());
                        String from=xmlPullParser.nextText();
                        Log.d("PubsubXml ", "" + from);
                        Log.d("PubsubXml ", "" + xmlPullParser.nextTag().name());
                        String time=xmlPullParser.nextText();
                        Log.d("PubsubXml ", "" + time);
                        Log.d("PubsubXml ", "" + xmlPullParser.nextTag().name());

                        String file=xmlPullParser.nextText();
                        Log.d("PubsubXml ", "" + file);
                        newsPersonList=new ArrayList<>();
                        newsPersonList.add(new NewsPerson(from, null, file, 1));
                        recyclerView.setLayoutManager(layoutManager);
                        newsPersonListAdapter.addItem(newsPersonList);

                        newsPersonList=new ArrayList<>();
                        newsPersonList.add(new NewsPerson(null, text, null, 2));
                        recyclerView.setLayoutManager(layoutManager);
                        newsPersonListAdapter.addItem(newsPersonList);

                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }

            }
            node1.addItemEventListener(new ItemEventListener() {
                @Override
                public void handlePublishedItems(ItemPublishEvent items) {
                    Log.d("Pubsub ", "" + items.getItems().size());
                    for (int i = 0; i < items.getItems().size(); i++) {
                        Log.d("PubsubItem ", "" + items.getItems().get(i).toString());
                        PayloadItem item= (PayloadItem) items.getItems().get(i);
                        try {
                            XmlPullParser xmlPullParser= PacketParserUtils.getParserFor(item.getPayload().toXML("https://mycontent").toString());
                            Log.d("PubsubXml ", "" + xmlPullParser.toString());

                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            });
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (PubSubException.NotAPubSubNodeException e) {
            e.printStackTrace();
        }
        pageViewModel.getBundleMutableLiveDataUser().observe(getViewLifecycleOwner(), new Observer<Bundle>() {
            @Override
            public void onChanged(Bundle bundle) {


            }
        });





        return root;
    }
}