package com.example.projectchat;

import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.DiffUtil;

import com.example.projectchat.RoomDB.message_withtest17;

import java.util.List;

public class diffutil extends DiffUtil.Callback {
    List<message_withtest17> OLDcontact_users;
    List<message_withtest17> NEWcontact_users;

    public diffutil(List<message_withtest17> OLDcontact_users, List<message_withtest17> NEWcontact_users) {
        this.NEWcontact_users = NEWcontact_users;
        this.OLDcontact_users = OLDcontact_users;
    }

    @Override
    public int getOldListSize() {
        return OLDcontact_users.size();
    }

    @Override
    public int getNewListSize() {
        return NEWcontact_users.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        message_withtest17 oldwithtest = OLDcontact_users.get(oldItemPosition);
        message_withtest17 newwithtest = NEWcontact_users.get(newItemPosition);
        if (oldwithtest.mtime1 != newwithtest.mtime1 || oldwithtest.mark != newwithtest.mark) {
            Log.d("mtime1new", "" + newwithtest.mtime1);
            Log.d("marknew", "" + newwithtest.mark);
        }
        return oldItemPosition == newItemPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        message_withtest17 oldwithtest = OLDcontact_users.get(oldItemPosition);
        message_withtest17 newwithtest = NEWcontact_users.get(newItemPosition);

        return oldwithtest.equals(newwithtest);

    }

    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        message_withtest17 message_withtest1 = OLDcontact_users.get(oldItemPosition);
        message_withtest17 message_withtest2 = NEWcontact_users.get(newItemPosition);
        Bundle difBundle = new Bundle();
        if (message_withtest1.mark != message_withtest2.mark) {
            difBundle.putInt("mtime1", message_withtest2.mark);
        }
        return difBundle;
    }
}
