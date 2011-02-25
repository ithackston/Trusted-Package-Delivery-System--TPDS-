package com.useless.tpds;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class Menu extends TabActivity {
	public static final int LOGOUT = 0;
	public static final int TERMINATE = 1;
	
	private Bundle activeUser;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
		
		Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    
	    intent = getIntent();
	    activeUser = intent.getExtras();

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, Status.class);
	    
	    // Add user information bundle to intent
	    intent.putExtras(activeUser);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("status").setIndicator("Status",
	                      res.getDrawable(R.drawable.ic_tab_status))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, Send.class);
	    intent.putExtras(activeUser);
	    spec = tabHost.newTabSpec("send").setIndicator("Send",
	                      res.getDrawable(R.drawable.ic_tab_send))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, Friends.class);
	    intent.putExtras(activeUser);
	    spec = tabHost.newTabSpec("friends").setIndicator("Friends",
	                      res.getDrawable(R.drawable.ic_tab_friends))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, Settings.class);
	    intent.putExtras(activeUser);
	    spec = tabHost.newTabSpec("settings").setIndicator("Settings",
	                      res.getDrawable(R.drawable.ic_tab_settings))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);
	}
}