package com.example.projectchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectchat.RoomDB.message_withtest17;
import com.example.projectchat.loadAvatar.ImageAvatarActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jivesoftware.smackx.pubsub.PubSubManager;

import java.util.ArrayList;
import java.util.List;

public class contactlistAdapter extends RecyclerView.Adapter<contactlistAdapter.MyViewHolder> {
    private List<message_withtest17> mListContact;
    Context mContext;
    Target target;

    public contactlistAdapter(Context context, List<message_withtest17> _mListContact) {
        this.mContext = context;
        this.mListContact = _mListContact;
    }

    public void filterList(List<message_withtest17> filterlist){
        mListContact=filterlist;
        notifyDataSetChanged();
    }

    public void setStatus(){

        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactlist, parent, false);
        MyViewHolder mvh = new MyViewHolder(view);
        return mvh;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position, List<Object> payloads) {
        message_withtest17 message = (message_withtest17) mListContact.get(position);

if (message.typing==null){
    message.typing=0;
}
            ((MyViewHolder) holder).bind(message.nick1, mListContact.get(position).mark, message.mwith1, message.status, message.type, message.avatar, message.typing);

        holder.textView.setOnClickListener(view -> {
            if (mListContact.get(position).mtime1=="0"){
                Intent inte = new Intent(mContext, newChatPage.class);
                inte.putExtra("userNick", mListContact.get(position).nick1);
                inte.putExtra("userjid", mListContact.get(position).mwith1);
                inte.putExtra("userType", mListContact.get(position).type);
                inte.putExtra("newuser", "yes");
                mContext.startActivity(inte);
            }
            else {
                Intent inte = new Intent(mContext, newChatPage.class);
                inte.putExtra("userNick", mListContact.get(position).nick1);
                inte.putExtra("userjid", mListContact.get(position).mwith1);
                inte.putExtra("userType", mListContact.get(position).type);
                inte.putExtra("newuser", "no");
                mContext.startActivity(inte);
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ImageContactClick", "ImageContactClick");

                Intent intent = new Intent(mContext, ImageAvatarActivity.class);
                intent.putExtra("avatar", message.avatar);
                mContext.startActivity(intent);
            }
        });

        if (!payloads.isEmpty()) {
            Bundle b = (Bundle) payloads.get(0);
            Log.d("payloads", "" + b.getInt("mtime1"));
            ((MyViewHolder) holder).bind(message.nick1, b.getInt("mtime1"), message.mwith1, message.status, message.type, message.avatar, message.typing);
        }
    }

    public List<message_withtest17> getData() {
        return mListContact;
    }

    public void setData(List<message_withtest17> set) {
        this.mListContact = set;
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return mListContact.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CardView cardView;
        ImageView imageView;
        ImageView statusview;
        TextView notReaded;




        MyViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.contactList);
            notReaded=(TextView)v.findViewById(R.id.notreadedmess);
            cardView = (CardView) v.findViewById(R.id.card_view);
            imageView = (ImageView) v.findViewById(R.id.image_avatar);
            statusview = (ImageView) v.findViewById(R.id.status);
            statusview.setVisibility(View.INVISIBLE);
        }

        void bind(String s, int mark, String s1, String status, String type, String avatar, int typing) {
            Log.d("bind", "bind");
            Log.d("Type of chat ", ""+s);
            Log.d("Type of chat ", ""+s1);
            Log.d("Type of chat ", ""+type);
            Log.d("Status of chat ", ""+status);
            Log.d("Avatar of chat ", ""+avatar);
            imageView.setImageResource(R.drawable.ic_launcher_background);

            Picasso.get().load(avatar)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    .resize(40, 40)
                    .into(imageView);
if (type.matches("chat")) {
if (status!=null) {
    if (status.matches("online")) {
        statusview.setVisibility(View.VISIBLE);
    } else if (status.matches("offline")) {
        statusview.setVisibility(View.INVISIBLE);
    }
} else {

        statusview.setVisibility(View.INVISIBLE);

}
}else{
    statusview.setVisibility(View.INVISIBLE);
}
            textView.setText(s);
if (mark!=0){
    notReaded.setText(String.valueOf(mark));

}else if (mark==0){
    notReaded.setText("");
}


    switch (typing) {
        case 1:
            textView.setText("печатает...");
            break;
        case 0:
            textView.setText(s);
            break;
    }

           /* switch (mark) {
                case 1:
                    Log.d("bindText", "" + s);
                    textView.setText(s);
                    textView.setTextColor(Color.BLACK);
                    break;
                case 2:
                    textView.setText(s);
                    textView.setTextColor(Color.parseColor("#A83B16"));
                    break;
            }*/
        }
    }
}
