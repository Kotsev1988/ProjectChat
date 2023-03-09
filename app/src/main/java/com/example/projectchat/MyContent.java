package com.example.projectchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
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

public class MyContent extends AppCompatActivity {
RecyclerView recyclerView;
    private newsPersonListAdapter newsPersonListAdapter;
    List<NewsPerson> newsPersonList = new ArrayList<>();
    String login1;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    private ViewPager2 mPager;
    FragmentStateAdapter pagerAdapter;
    private static final int NUM_PAGES=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_content);

        mPager=(ViewPager2) findViewById(R.id.pager_mycontent);
        pagerAdapter=new MyContentAdapter(this);
        mPager.setAdapter(pagerAdapter);


    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("landscapeAct", "Landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d("PORTRAITAct", "PORTRAIT");
        }
    }


    private class MyContentAdapter extends FragmentStateAdapter {
        public MyContentAdapter(FragmentActivity fm){

            super(fm);
        }



        @NonNull
        @Override
        public Fragment createFragment(int position) {
            MyContentFragment screenSlidePageFragment=new MyContentFragment();
            Bundle bundle=new Bundle();
            //bundle.putString("pathVideo", pathVideo);
            screenSlidePageFragment.setArguments(bundle);
            return MyContentFragment.newInstance("");
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }


}

/*

 swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshPerson);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_my_content);
        recyclerView.setNestedScrollingEnabled(true);
        newsPersonListAdapter = new newsPersonListAdapter(this, newsPersonList);

        layoutManager = new LinearLayoutManager(this);

        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(newsPersonListAdapter);
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount());


        PubSubManager manager=App.getComponent().userauth().getPubSubManager();
        LeafNode node1 = null;

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
                        Log.d("PubsubXml ", "" + xmlPullParser.nextTag().name());
                        String time=xmlPullParser.nextText();
                        Log.d("PubsubXml ", "" + xmlPullParser.nextTag().name());
                        String file=xmlPullParser.nextText();
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
                            XmlPullParser xmlPullParser=PacketParserUtils.getParserFor(item.getPayload().toXML("https://mycontent").toString());
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
 */