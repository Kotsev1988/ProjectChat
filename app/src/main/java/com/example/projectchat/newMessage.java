package com.example.projectchat;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class newMessage {


    Activity activity;
    public String stroka;
    private Context context;

    public newMessage(String _stroka) {
        this.stroka = _stroka;
        Log.d("log", "stroka" + stroka);

    }

    public newMessage(Context _context) {

        this.context = _context;

        LinearLayout linearLayout = new LinearLayout(context);

        // LinearLayout parent = (LinearLayout)((Activity)context).findViewById(R.id.content_main); //activity.findViewById(R.id.content_main);

        LinearLayout.LayoutParams llayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(context);
        tv.setText(stroka);
        tv.setLayoutParams(llayout);
        //  parent.addView(tv);
    }
       /* ConstraintLayout constraintLayout=(ConstraintLayout) activity.findViewById(R.id.content_main);
        constraintLayout.setId(R.id.content_main);
        TextView txt1=new TextView(activity);

        txt1.setId(View.generateViewId());
        txt1.setText("baksancity");
        constraintLayout.addView(txt1, 0);
        ConstraintSet set=new ConstraintSet();

        ConstraintLayout.LayoutParams consL=new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        txt1.setLayoutParams(consL);

        set.clone(constraintLayout);
        set.connect(txt1.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 60);

        set.applyTo(constraintLayout);*/
}
