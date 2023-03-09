package com.example.projectchat;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BoxAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<Contact_users> objects;

    BoxAdapter(Context context, ArrayList<Contact_users> contact_users) {
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
            view = inflater.inflate(R.layout.layout, parent, false);
        }
        Contact_users p = getContact(position);

        if (p.mark != null && p.mark.matches("2")) {
            view.setBackgroundColor(Color.RED);
        }

        ((TextView) view.findViewById(R.id.tvValue)).setText(p.nick);
        ((ImageView) view.findViewById(R.id.IvImg)).setImageResource(p.image);

        return view;
    }

    Contact_users getContact(int position) {
        //Log.d("positionofList", ""+position);
        return ((Contact_users) getItem(position));
    }


}
