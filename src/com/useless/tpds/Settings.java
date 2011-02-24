package com.useless.tpds;

import android.os.Bundle;
import android.widget.TextView;

public class Settings extends UserTab {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textview = new TextView(this);
        String msg = "Hello " + activeUser.getString("realname") + "! ";
        msg += " Your token is: " + activeUser.getString("token");
        textview.setText(msg);
        setContentView(textview);
    }
}