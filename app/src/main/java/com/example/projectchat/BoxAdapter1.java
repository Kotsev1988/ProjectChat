package com.example.projectchat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BoxAdapter1 extends BaseAdapter {

    Context ctx;
    LayoutInflater inflater;
    ArrayList<Contact_users> objects;

    BoxAdapter1(Context context, ArrayList<Contact_users> contact_users) {
        ctx = context;
        objects = contact_users;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.layout2, parent, false);
        }

        Contact_users p = getContact(position);

        ((TextView) view.findViewById(R.id.tvValue1)).setText(p.nick);
        ((ImageView) view.findViewById(R.id.IvImg1)).setImageResource(p.image);
        CheckBox checkBoxUser = (CheckBox) view.findViewById(R.id.checkboxuser);
        checkBoxUser.setOnCheckedChangeListener(myChange);
        checkBoxUser.setTag(position);
        checkBoxUser.setChecked(p.tag);

        return view;
    }

    Contact_users getContact(int position) {
        Log.d("positionofListgroup", "" + position);
        return ((Contact_users) getItem(position));
    }

    ArrayList<Contact_users> getCheckedUsers() {
        ArrayList<Contact_users> checkedUser = new ArrayList<Contact_users>();
        for (Contact_users p : objects) {
            if (p.tag) {
                checkedUser.add(p);
            }
        }
        return checkedUser;
    }

    CompoundButton.OnCheckedChangeListener myChange = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            getContact((Integer) buttonView.getTag()).tag = isChecked;
        }
    };

}

