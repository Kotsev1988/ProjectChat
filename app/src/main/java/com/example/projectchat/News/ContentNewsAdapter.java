package com.example.projectchat.News;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectchat.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContentNewsAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<News> news;
    List<Upload> mUpload;

    public ContentNewsAdapter(Context mContext, List<Upload> newsList) {
        this.context = mContext;
        this.mUpload = newsList;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contentnews, parent, false);
        return new newcontent(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Upload upload = mUpload.get(position);
        ((newcontent) holder).bind(upload);
    }

    @Override
    public int getItemCount() {
        return mUpload.size();
    }

    class newcontent extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public newcontent(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imagenewscontent);
            textView = (TextView) itemView.findViewById(R.id.textviewnewcontent);
        }

        void bind(Upload news1) {

            Log.d("news1", "" + news1.getmName());

            textView.setText(news1.getmName());
            Picasso.get().load(news1.getmImageUrl()).into(imageView);

        }
    }
}
