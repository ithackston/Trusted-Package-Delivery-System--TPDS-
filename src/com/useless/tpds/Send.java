package com.useless.tpds;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Send extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the send package tab.");
        setContentView(textview);
    }
}