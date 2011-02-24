package com.useless.tpds;

import android.os.Bundle;
import android.widget.TextView;

public class Friends extends UserTab {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the trust and deliverable list tab.");
        setContentView(textview);
    }
}