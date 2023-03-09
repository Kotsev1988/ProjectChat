package com.example.projectchat;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectchat.ui.main.PlaceholderFragment;
import com.example.projectchat.ui.main.viewModelContact;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.search.ReportedData;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class MessageListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<UserMessage> mMessageList;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_RECEIVEDGroup = 9;
    private static final int VIEW_TYPE_Image_RECEIVED = 3;
    private static final int VIEW_TYPE_Image_Sent = 4;
    private static final int VIEW_TYPE_Video_RECEIVED = 5;
    private static final int VIEW_TYPE_Video_Sent = 6;
    private static final int VIEW_TYPE_Image_RECEIVED_GROUP = 7;
    private static final int VIEW_TYPE_Video_RECEIVED_GROUP = 8;
    private static final int VIEW_TYPE_LOADING = 10;
    private static final int VIEW_TYPE_NORMAL = 11;

    private boolean isLoadervisible = false;
    videlenie videlenie=new videlenie(false);
    HashMap<Integer, String> map=new HashMap<>();
View view1;
    ImageButton imageForward, imageDelete, imageAnswer;
    public MessageListAdapter(Context context, List<UserMessage> messageList) {
        mContext = context;
        mMessageList = messageList;
    }
    public interface onSomeEventListener {
        public void SomeEventVideo(Bundle s);
    }

    onSomeEventListener someEventListener;

    @Override
    public int getItemViewType(int position) {
        UserMessage message = (UserMessage) mMessageList.get(position);
        Log.d("viewType", ""+message.messageFrom());
        if (message.messageFrom() == VIEW_TYPE_MESSAGE_SENT) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else if (message.messageFrom() == VIEW_TYPE_MESSAGE_RECEIVED) {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }else if (message.messageFrom() == VIEW_TYPE_MESSAGE_RECEIVEDGroup) {
            return VIEW_TYPE_MESSAGE_RECEIVEDGroup;
        }
        else if (message.messageFrom() == VIEW_TYPE_Image_RECEIVED) {
            return VIEW_TYPE_Image_RECEIVED;
        } else if (message.messageFrom() == VIEW_TYPE_Image_Sent) {
            return VIEW_TYPE_Image_Sent;
        }else if (message.messageFrom()==VIEW_TYPE_Video_RECEIVED){
            return VIEW_TYPE_Video_RECEIVED;
        } else if (message.messageFrom()==VIEW_TYPE_Video_Sent){
            return VIEW_TYPE_Video_Sent;
        }else if (message.messageFrom() == VIEW_TYPE_Image_RECEIVED_GROUP) {
            return VIEW_TYPE_Image_RECEIVED_GROUP;
        }else if (message.messageFrom()==VIEW_TYPE_Video_RECEIVED_GROUP){
            return VIEW_TYPE_Video_RECEIVED_GROUP;
        }
        Log.d("isLoaderVisible", "" + isLoadervisible);
        if (isLoadervisible) {
            if (position == mMessageList.size() - 1)
                return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_NORMAL;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        someEventListener = (onSomeEventListener) mContext;
        Log.d("viewType", ""+viewType);
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVEDGroup) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolderGroup(view);
        }
        else if (viewType == VIEW_TYPE_Image_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemimagefile, parent, false);
            return new ReceivedImageHolder(view);
        }
        else if (viewType == VIEW_TYPE_Image_Sent)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemsentimage, parent, false);
            return new SendImageHolder(view);
        }
       else if (viewType==VIEW_TYPE_Video_RECEIVED){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incoming_video, parent, false);
            return new ReceivedVideoHolder(view, someEventListener);
        }
      else  if (viewType==VIEW_TYPE_Video_Sent){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sent_video, parent, false);
            return new SentVideoHolder(view, someEventListener);
        }
        if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new ProgressHolder(view);
        }
        if (viewType == VIEW_TYPE_NORMAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new ProgressHolder(view);
        }else if (viewType == VIEW_TYPE_Image_RECEIVED_GROUP) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemimagefile, parent, false);
            return new ReceivedImageHolderGroup(view);
        }else if (viewType==VIEW_TYPE_Video_RECEIVED_GROUP){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incoming_video, parent, false);
            return new ReceivedVideoHolderGroup(view, someEventListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        UserMessage message = (UserMessage) mMessageList.get(position);
        Context context=holder.itemView.getRootView().getContext();
        imageForward = ((AppCompatActivity)context).findViewById(R.id.forward);
        imageDelete = ((AppCompatActivity)context).findViewById(R.id.delete_message);
        imageAnswer = ((AppCompatActivity)context).findViewById(R.id.answer_message);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                videlenie.setVidelenie(true);
                holder.itemView.setBackgroundColor(Color.GREEN);

                newChatPage.imageCall.setVisibility(View.INVISIBLE);
                imageForward.setVisibility(View.VISIBLE);
                imageForward.setImageResource(R.drawable.ic_action_forward);
                imageDelete.setVisibility(View.VISIBLE);
                imageAnswer.setVisibility(View.VISIBLE);
                map.put(holder.getAdapterPosition(), message.getName());
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videlenie.getVidelenie()==true ) {
                    if (map.containsKey(holder.getAdapterPosition()))
                    {
                        holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                        map.remove(holder.getAdapterPosition());
                        if (map.isEmpty()){
                            videlenie.setVidelenie(false);
                        }
                    }
                       else
                    {
                        holder.itemView.setBackgroundColor(Color.GREEN);
                        map.put(holder.getAdapterPosition(), message.getName());
                    }
                }
            }
        });

        imageForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ForwardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("message", map);
                imageForward.setVisibility(View.INVISIBLE);
                //imageForward.setImageResource(R.drawable.call_user);
                imageDelete.setVisibility(View.INVISIBLE);
                imageAnswer.setVisibility(View.INVISIBLE);

                context.startActivity(intent);
                videlenie.setVidelenie(false);
                map.clear();
                notifyDataSetChanged();
            }
        });


        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVEDGroup:
                ((ReceivedMessageHolderGroup) holder).bind(message);
                break;
            case VIEW_TYPE_Image_RECEIVED:
                ((ReceivedImageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_Image_RECEIVED_GROUP:
                ((ReceivedImageHolderGroup) holder).bind(message);
                break;
            case VIEW_TYPE_Image_Sent:
                ((SendImageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_Video_RECEIVED:
                ((ReceivedVideoHolder)holder).bind(message);
                break;
            case VIEW_TYPE_Video_RECEIVED_GROUP:
                ((ReceivedVideoHolderGroup)holder).bind(message);
                break;
            case VIEW_TYPE_Video_Sent:
                ((SentVideoHolder)holder).bind(message);

                break;
            case VIEW_TYPE_LOADING:
                ((ProgressHolder) holder).bind();
                break;
            case VIEW_TYPE_NORMAL:
                ((ProgressHolder) holder).bind1();
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return mMessageList.size();
    }

    public void addItems(List<UserMessage> _mMessageList) {
        mMessageList.addAll(_mMessageList);
        notifyDataSetChanged();
    }

    public void addItem(List<UserMessage> _mMessageList) {
        mMessageList.addAll(0, _mMessageList);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoadervisible = true;
        notifyItemInserted(mMessageList.size() - 1);
    }

    public void removeLoading() {
        isLoadervisible = false;
        int position = mMessageList.size() - 1;
        UserMessage userMessage = getItem(position);
        if (userMessage != null) {
            mMessageList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setBoolean(boolean videlenie){
        videlenie=true;

    }


    UserMessage getItem(int position) {
        return mMessageList.get(position);
    }
}

class SentMessageHolder extends RecyclerView.ViewHolder {
    final TextView messageText, date_time_sent;

    SentMessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.textView);
        date_time_sent = (TextView) itemView.findViewById(R.id.datetime_sent);
    }

    void bind(UserMessage message) {
        date_time_sent.setText(message.getDate());
        messageText.setText(message.getName());
        messageText.setTextSize(18);
    }
}

class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    final TextView messageText1,  date_time_receive;
    ImageButton imageForward, imageDelete, imageAnswer;
    ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText1 = (TextView) itemView.findViewById(R.id.text_message_body);
        date_time_receive = (TextView) itemView.findViewById(R.id.datetime_receive);

    }

    void bind(UserMessage message) {

        messageText1.setText(message.getName());
        date_time_receive.setText(message.getDate());
        messageText1.setTextSize(18);

    }
}

class ReceivedMessageHolderGroup extends RecyclerView.ViewHolder {
    final TextView messagefromgroup, messageText1,  date_time_receive;

    ReceivedMessageHolderGroup(View itemView) {
        super(itemView);
        messagefromgroup=(TextView)itemView.findViewById(R.id.receivedmessagegroup);
        messageText1 = (TextView) itemView.findViewById(R.id.text_message_body);
        date_time_receive = (TextView) itemView.findViewById(R.id.datetime_receive);

    }

    void bind(UserMessage message) {

        messagefromgroup.setText(message.getNameFrom());
        messageText1.setText(message.getName());
        date_time_receive.setText(message.getDate());
        messageText1.setTextSize(18);

    }
}

class ReceivedImageHolder extends RecyclerView.ViewHolder {
    final TextView  date_time_receive;
    ImageView receiveImage;
    String mess;

    ReceivedImageHolder(View itemView) {
        super(itemView);
        receiveImage = (ImageView) itemView.findViewById(R.id.receiveImage);
        date_time_receive = (TextView) itemView.findViewById(R.id.datetime_image_receive);
    }

    void bind(UserMessage message) {
        receiveImage.setVisibility(View.VISIBLE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(message.setBitmap(), options);
        receiveImage.setImageBitmap(bitmap);
        mess = message.setBitmap();
        date_time_receive.setText(message.getDate());
        receiveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceivedImageHolder.super.itemView.getContext(), ImageActivity.class);
                intent.putExtra("imageurl", mess);
                ReceivedImageHolder.super.itemView.getContext().startActivity(intent);
            }
        });
    }
}

class ReceivedImageHolderGroup extends RecyclerView.ViewHolder {
    final TextView image_comefrom, date_time_receive;
    ImageView receiveImage;
    String mess;

    ReceivedImageHolderGroup(View itemView) {
        super(itemView);
        image_comefrom = (TextView) itemView.findViewById(R.id.image_come_from);
        receiveImage = (ImageView) itemView.findViewById(R.id.receiveImage);
        date_time_receive = (TextView) itemView.findViewById(R.id.datetime_image_receive);
    }

    void bind(UserMessage message) {
        receiveImage.setVisibility(View.VISIBLE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(message.setBitmap(), options);
        receiveImage.setImageBitmap(bitmap);
        Log.d("imageComeGroup", ""+message.getNameFrom());
        image_comefrom.setText(message.getNameFrom());
        mess = message.setBitmap();
        date_time_receive.setText(message.getDate());
        receiveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceivedImageHolderGroup.super.itemView.getContext(), ImageActivity.class);
                intent.putExtra("imageurl", mess);
                ReceivedImageHolderGroup.super.itemView.getContext().startActivity(intent);

            }
        });
    }
}

class SendImageHolder extends RecyclerView.ViewHolder {
    final TextView   date_time_sent;
    ImageView sendImage;
    String mess;

    SendImageHolder(View itemView) {
        super(itemView);

        sendImage = (ImageView) itemView.findViewById(R.id.sendImage);
        date_time_sent = (TextView) itemView.findViewById(R.id.datetime_image_sent);
    }

    void bind(UserMessage message) {
        sendImage.setVisibility(View.VISIBLE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        mess = message.setouBitmap();
        Bitmap bitmap = BitmapFactory.decodeFile(message.setouBitmap(), options);

        sendImage.setImageBitmap(bitmap);
        date_time_sent.setText(message.getDate());

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SendImageHolder.super.itemView.getContext(), ImageActivity.class);
                intent.putExtra("imageurl", mess);
                SendImageHolder.super.itemView.getContext().startActivity(intent);
            }
        });
    }
}



class ReceivedVideoHolder extends RecyclerView.ViewHolder {
    final TextView  date_time_receive;
    ImageView receiveVideo;
    ImageButton playIncomVideo;
    Context context;
    MessageListAdapter.onSomeEventListener someEventListener;

    ReceivedVideoHolder(View itemView, MessageListAdapter.onSomeEventListener _someEventListener) {
        super(itemView);
        someEventListener=_someEventListener;
        date_time_receive=(TextView) itemView.findViewById(R.id.datetime_video_receive);
        receiveVideo=(ImageView) itemView.findViewById(R.id.receiveVideo);
        context=itemView.getContext();
        playIncomVideo=(ImageButton) itemView.findViewById(R.id.playIncomVideo);
    }

    void bind(UserMessage message) {
        Log.d("messageIncomVideo", ""+message.setBitmap());

        date_time_receive.setText(message.getDate());
        Glide.with(context).load(Uri.fromFile(new File(message.setBitmap()))).into(receiveVideo);
        receiveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OnVideoClick", "VideoClocik");
                Bundle bundle=new Bundle();
                bundle.putString("pathVideo", message.setBitmap());
                someEventListener.SomeEventVideo(bundle);

            }
        });

        playIncomVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("pathVideo", message.setBitmap());
                someEventListener.SomeEventVideo(bundle);

            }
        });
    }
}

class ReceivedVideoHolderGroup extends RecyclerView.ViewHolder {
    final TextView  receivedvidoegroupfrom, date_time_receive;
    ImageView receiveVideo;
    ImageButton playIncomVideo;
    Context context;
    MessageListAdapter.onSomeEventListener someEventListener;

    ReceivedVideoHolderGroup(View itemView, MessageListAdapter.onSomeEventListener _someEventListener) {
        super(itemView);
        someEventListener=_someEventListener;
        receivedvidoegroupfrom=(TextView)itemView.findViewById(R.id.receivedvideogroup);
        date_time_receive=(TextView) itemView.findViewById(R.id.datetime_video_receive);
        receiveVideo=(ImageView) itemView.findViewById(R.id.receiveVideo);
        context=itemView.getContext();
        playIncomVideo=(ImageButton) itemView.findViewById(R.id.playIncomVideo);
    }

    void bind(UserMessage message) {
        Log.d("messageIncomVideo", ""+message.setBitmap());

        receivedvidoegroupfrom.setText(message.getNameFrom());
        date_time_receive.setText(message.getDate());
        Glide.with(context).load(Uri.fromFile(new File(message.setBitmap()))).into(receiveVideo);
        receiveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OnVideoClick", "VideoClocik");
                Bundle bundle=new Bundle();
                bundle.putString("pathVideo", message.setBitmap());
                someEventListener.SomeEventVideo(bundle);

            }
        });

        playIncomVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("pathVideo", message.setBitmap());
                someEventListener.SomeEventVideo(bundle);

            }
        });
    }
}

class SentVideoHolder extends RecyclerView.ViewHolder {
    final TextView  date_time_sent;
    ImageView sentVideo;
    ImageButton buttonplay;
    Context context;
    MessageListAdapter.onSomeEventListener someEventListener;

    SentVideoHolder(View itemView, MessageListAdapter.onSomeEventListener _someEventListener) {
        super(itemView);
        someEventListener=_someEventListener;
        date_time_sent=(TextView) itemView.findViewById(R.id.datetime_video_sent);
        context=itemView.getContext();
        sentVideo=(ImageView) itemView.findViewById(R.id.sendVideo);
        buttonplay=(ImageButton) itemView.findViewById(R.id.buttonplay);
    }

    void bind(UserMessage message) {
         date_time_sent.setText(message.getDate());

        Glide.with(context).load(Uri.fromFile(new File(message.setouBitmap()))).into(sentVideo);
        sentVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OnVideoClick", "VideoClocik");
                Bundle bundle=new Bundle();
                bundle.putString("pathVideo", message.setouBitmap());
                someEventListener.SomeEventVideo(bundle);

            }
        });
        buttonplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("pathVideo", message.setouBitmap());
                someEventListener.SomeEventVideo(bundle);

            }
        });
    }
}

class ProgressHolder extends RecyclerView.ViewHolder {
    ProgressBar progressBar;

    ProgressHolder(View itemView) {
        super(itemView);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
    }

    void bind() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    void bind1() {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

}










