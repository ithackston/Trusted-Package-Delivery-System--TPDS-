package com.useless.tpds;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class UserTab extends Activity {
	protected Bundle activeUser;
	protected SharedPreferences prefs;
	
	protected void logout() {
		//run logout request
		String requestUrl = "http://snarti.nu/?data=user&action=logout";
		requestUrl += "&token=" + activeUser.getString("token");
		Database.get(requestUrl);
		
		//erase stored token
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove("savedToken");
		editor.commit();
		
		//go to login activity
		setResult(Menu.LOGOUT);
		finish();
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //get user object
        Intent intent = getIntent();
        activeUser = intent.getExtras();
        
        //get saved preferences
        prefs = getSharedPreferences(getString(R.string.prefs_filename),0);
    }
}