package com.useless.tpds;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class TPDS extends TabActivity {
	public static final int LOGIN_REQUEST = 0;
	public static final int REGISTER_REQUEST = 1;

	public Bundle activeUser;
	public SharedPreferences prefs;
	
	private void loadTabs() {
		setContentView(R.layout.main);
		
		Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, Status.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("status").setIndicator("Status",
	                      res.getDrawable(R.drawable.ic_tab_status))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, Send.class);
	    spec = tabHost.newTabSpec("send").setIndicator("Send",
	                      res.getDrawable(R.drawable.ic_tab_send))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, Friends.class);
	    spec = tabHost.newTabSpec("friends").setIndicator("Friends",
	                      res.getDrawable(R.drawable.ic_tab_friends))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, Settings.class);
	    spec = tabHost.newTabSpec("settings").setIndicator("Settings",
	                      res.getDrawable(R.drawable.ic_tab_settings))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);

	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        prefs = getSharedPreferences(getString(R.string.prefs_filename),0);
        
        //ask for login
        Intent i = new Intent(this,Login.class);
        startActivityForResult(i, LOGIN_REQUEST);
    }
    
	//called when activity ends
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == LOGIN_REQUEST) {
    		if(resultCode == UserAuth.LOGIN_SUCCESSFUL) {
    			activeUser = data.getExtras();
    			loadTabs();
    		} else if(resultCode == UserAuth.REGISTER_USER) {
    			Intent i = new Intent(this,Register.class);
    	        startActivityForResult(i, REGISTER_REQUEST);
            } else if(resultCode == UserAuth.LOGIN_UNSUCCESSFUL) {
            	Intent i = new Intent(this,Login.class);
                startActivityForResult(i, LOGIN_REQUEST);
            }
    	} else if(requestCode == REGISTER_REQUEST) {
    		if(resultCode == UserAuth.LOGIN_SUCCESSFUL) {
    			activeUser = data.getExtras();
    			loadTabs();
    		} else {
    			Intent i = new Intent(this,Login.class);
    	        startActivityForResult(i, LOGIN_REQUEST);
            }
    	}
    }
}