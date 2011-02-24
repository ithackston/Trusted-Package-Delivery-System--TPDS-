package com.useless.tpds;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class UserTab extends Activity {
	protected Bundle activeUser;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        activeUser = intent.getExtras();
    }
}