package com.example.projectchat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectchat.ui.main.PageViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class PlanOfDay extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private PageViewModel pageViewModel;
    private RecyclerView mNewsPersonRecycler;
    private newsPersonListAdapter newsPersonListAdapter;
    List<NewsPerson> newsPersonList = new ArrayList<>();
    String login1;
    LinearLayoutManager layoutManager;

    public static PlanOfDay newInstance(int index) {
        PlanOfDay fragment = new PlanOfDay();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
         //pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.planofday, container, false);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        mNewsPersonRecycler = (RecyclerView) root.findViewById(R.id.recycle_message_list_person);
        newsPersonListAdapter = new newsPersonListAdapter(getContext(), newsPersonList);

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        mNewsPersonRecycler.setLayoutManager(layoutManager);
        mNewsPersonRecycler.setAdapter(newsPersonListAdapter);

        pageViewModel.getBundleMutableLiveDataUser().observe(getViewLifecycleOwner(), new Observer<Bundle>() {
            @Override
            public void onChanged(Bundle bundle) {

                newsPersonList=new ArrayList<>();
                newsPersonList.add(new NewsPerson("news", "", "", 1));
                mNewsPersonRecycler.setLayoutManager(layoutManager);
                newsPersonListAdapter.addItem(newsPersonList);

                newsPersonList=new ArrayList<>();
                newsPersonList.add(new NewsPerson("news1", "", "", 1));
                mNewsPersonRecycler.setLayoutManager(layoutManager);
                newsPersonListAdapter.addItem(newsPersonList);
                Log.d("newuseryesNews", "" +bundle.getString("userNick"));
            }
        });
        return root;
    }


    }


