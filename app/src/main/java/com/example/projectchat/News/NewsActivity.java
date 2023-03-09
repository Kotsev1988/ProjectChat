package com.example.projectchat.News;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectchat.App;
import com.example.projectchat.R;
import com.example.projectchat.RoomDB.DataBase;
import com.example.projectchat.RoomDB.message_chattest17;
import com.example.projectchat.RoomDB.message_withtest17;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class NewsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ContentNewsAdapter contentNewsAdapter;
    List<Upload> newsList;
    private DatabaseReference mDataBase;
    Flowable<message_chattest17> flowable;
    Disposable disposable;
    DataBase dataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsactivity);

        Log.d("onCreatenewsAct", "newsActivity");

        recyclerView = (RecyclerView) findViewById(R.id.recyclenewcontent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsList = new ArrayList<>();
        dataBase = App.getComponent().database();



        dataBase.message_withtestDAO().getAll1()
                .subscribeOn(Schedulers.io()).observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<message_withtest17>>() {
                    @Override
                    public void accept(List<message_withtest17> message_withtest17s) throws Exception {
                        for (message_withtest17 mwith : message_withtest17s) {
                            mDataBase = FirebaseDatabase.getInstance().getReference(mwith.mwith1.substring(0, mwith.mwith1.lastIndexOf("@")));
                            Log.d("onCreatenewsAct", "newsActivity" + mwith.mwith1.substring(0, mwith.mwith1.lastIndexOf("@")));
                            mDataBase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                                        Upload upload = postSnapShot.getValue(Upload.class);
                                        newsList.add(upload);
                                    }
                                    contentNewsAdapter = new ContentNewsAdapter(NewsActivity.this, newsList);
                                    recyclerView.setAdapter(contentNewsAdapter);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    Log.d("onCancelledDBFire", "onCancelledDBFire");

                                }

                            });
                        }
                    }
                });
    }
}
