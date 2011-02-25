package com.useless.tpds;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Settings extends Activity implements OnClickListener {
	private Bundle activeUser;
	private SharedPreferences prefs;
	private Button buttonLogout;
	
	private void logout() {
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
	
	private void refreshUser() {
		TextView textRealname = (TextView) findViewById(R.id.fieldRealname);
        TextView textUsername = (TextView) findViewById(R.id.fieldUsername);
        
        textRealname.setText(activeUser.getString("realname"));
        textUsername.setText(activeUser.getString("username"));
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.settings);
        
        //get user object
        Intent intent = getIntent();
        activeUser = intent.getExtras();
        
        //get saved preferences
        prefs = getSharedPreferences(getString(R.string.prefs_filename),0);
        
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);
    }
    
    public void onResume() {
    	super.onResume();
    	refreshUser();
    }
    
    public void onClick(View v) {
    	if(v == buttonLogout) {
    		logout();
    	}
    }
}