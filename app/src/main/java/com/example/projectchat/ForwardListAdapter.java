package com.example.projectchat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectchat.RoomDB.message_withtest17;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForwardListAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private List<message_withtest17> mMessageListForward;
ForwardMessage forwardMessage;
    public interface CheckedLstener{
     void SomeEvent(Bundle s);
}

Map<Integer, String> list=new HashMap<>();
    public ForwardListAdapter(Context context, List<message_withtest17> _mListContact) {
        this.mContext = context;
        this.mMessageListForward = _mListContact;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactlist_forward, parent, false);
        forwardMessage = new ForwardMessage(view);
        return forwardMessage;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        message_withtest17 message = (message_withtest17) mMessageListForward.get(position);

        ((ForwardMessage) holder).bind(message.nick1, message.mwith1);
((ForwardMessage) holder).messageText.setOnClickListener(new View.OnClickListener() {
               @Override
            public void onClick(View view) {
                if (((ForwardMessage) holder).check.isChecked()==true){
                    ((ForwardMessage) holder).check.setChecked(false);
                    list.remove(((ForwardMessage)holder).getAdapterPosition());
                }else {
                    ((ForwardMessage) holder).check.setVisibility(View.VISIBLE);
                    ((ForwardMessage) holder).check.setChecked(true);
                    list.put(((ForwardMessage)holder).getAdapterPosition(), message.mwith1);//.add( message.nick1);
                }
            }
});

    }

    @Override
    public int getItemCount() {
     return    mMessageListForward.size();
    }


    public List<message_withtest17> getData() {
        return mMessageListForward;
    }

    public void setData(List<message_withtest17> set) {
        this.mMessageListForward = set;
    }
    public Map<Integer, String> onItemSelected(){
        return list;
    }

    class ForwardMessage extends RecyclerView.ViewHolder {
        final TextView messageText;
        ImageView image;
       CheckBox check;
        ArrayList<String> list ;
        ForwardMessage(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.contactList_forward);
            image=(ImageView) itemView.findViewById(R.id.image_avatar_forward);
            check=(CheckBox) itemView.findViewById(R.id.checkforwarduser);
        }

        void bind(String message, String mwith) {
            messageText.setText(message);
            String jid=mwith;
          /*  messageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (check.isChecked()==true){
                        check.setChecked(false);
                        list.remove(getAdapterPosition());
                    }else {
                        check.setVisibility(View.VISIBLE);
                        check.setChecked(true);
                        list.add(getAdapterPosition(), message);

                    }
                }
            });*/
        }

        public ArrayList<String> onItemSelected(){
            return list;
        }

    }
}
