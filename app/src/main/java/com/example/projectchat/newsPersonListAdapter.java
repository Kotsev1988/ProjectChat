package com.example.projectchat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class newsPersonListAdapter extends RecyclerView.Adapter {
    private Context context;
    List<NewsPerson> newsPersonList ;
    private static final int VIEW_TYPE_Image_CONTENT = 1;
    private static final int VIEW_TYPE_Text_Content = 2;

    public newsPersonListAdapter(Context mContext, List<NewsPerson> newsPersonList ) {
        this.context = mContext;
        this.newsPersonList = newsPersonList;
    }

    @Override
    public int getItemViewType(int position) {
        NewsPerson content = (NewsPerson) newsPersonList.get(position);
        if (content.getTypecontent() == VIEW_TYPE_Image_CONTENT) {
            return VIEW_TYPE_Image_CONTENT;
        } else if (content.getTypecontent() == VIEW_TYPE_Text_Content) {
            return VIEW_TYPE_Text_Content;
        }
        return 0;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_Image_CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contentnews, parent, false);
            return new newcontent(view);
        } else if (viewType == VIEW_TYPE_Text_Content) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mycontenttext, parent, false);
            return new newcontentText(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        NewsPerson newsPerson=newsPersonList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_Image_CONTENT:
                ((newcontent) holder).bind(newsPerson);
                break;
            case VIEW_TYPE_Text_Content:
                ((newcontentText) holder).bind(newsPerson);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return newsPersonList.size();
    }

    public void addItem(List<NewsPerson> _mMessageList) {
        newsPersonList.addAll(0, _mMessageList);
        notifyDataSetChanged();
    }

    class newcontent extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView  contentFrom;

        public newcontent(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imagenewscontent);
            contentFrom=(TextView) itemView.findViewById(R.id.contentfrom);
        }

        void bind(NewsPerson news1) {
            contentFrom.setText(news1.getmName());
            Picasso.get().load("http://192.168.0.27/content/"+news1.getmName()+"/"+news1.getmImageUrl()).into(imageView);


        }
    }

    class newcontentText extends RecyclerView.ViewHolder {

        TextView textView;

        public newcontentText(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textviewnewcontent);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textView.setMaxLines(Integer.MAX_VALUE);
                }
            });
        }

        void bind(NewsPerson news1) {
            textView.setText(news1.getmText());
            Log.d("Files ", ""+news1.getmImageUrl());

        }
    }
}
