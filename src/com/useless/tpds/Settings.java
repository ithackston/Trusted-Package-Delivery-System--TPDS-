package com.useless.tpds;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class Settings extends Activity implements OnClickListener {
	private TextView textRealname, textUsername, textLocation, textToken;
    private CheckBox rememberMe;
	private Bundle activeUser;
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefsEdit;
	private Button buttonLogout;
	
	private void logout() {
		//run logout request
		String requestUrl = "http://snarti.nu/?data=user&action=logout";
		requestUrl += "&token=" + activeUser.getString("token");
		Database.get(requestUrl);
		
		//clear all preferences
		clearAll();
		
		//go to login activity
		setResult(Menu.LOGOUT);
		finish();
	}
	
	private void clearAll() {
		prefsEdit.remove("last_latitude");
		prefsEdit.remove("last_longitude");
		clearToken();
	}
	
	private void clearToken() {
		prefsEdit.remove("savedToken");
		prefsEdit.commit();
	}
	
	private void refreshUser() {
        textRealname.setText(activeUser.getString("realname"));
        textUsername.setText(activeUser.getString("username"));
        textToken.setText(activeUser.getString("token"));
        
        float lat = prefs.getFloat("last_latitude",1000);
        float lon = prefs.getFloat("last_longitude",1000);
        String loc = "Unknown.";
        
        if(lat != 1000 && lon != 1000) {
        	loc = "(" + String.valueOf(lat);
        	loc += "," + String.valueOf(lon) + ")";
        }
        
        textLocation.setText(loc);
        
        if(prefs.getString("savedToken","").equals("")) {
        	rememberMe.setChecked(false);
        } else {
        	rememberMe.setChecked(true);
        }
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.settings);
        
        textRealname = (TextView) findViewById(R.id.fieldRealname);
        textUsername = (TextView) findViewById(R.id.fieldUsername);
        textLocation = (TextView) findViewById(R.id.fieldLocation);
        textToken = (TextView) findViewById(R.id.fieldToken);
        rememberMe = (CheckBox) findViewById(R.id.savePassword);
        
        //get user object
        Intent intent = getIntent();
        activeUser = intent.getExtras();
        
        //get saved preferences
        prefs = getSharedPreferences(getString(R.string.prefs_filename),0);
        prefsEdit = prefs.edit();
        
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
    	} else if(v == rememberMe) {
    		if(rememberMe.isChecked()) {
    			if(activeUser.containsKey("token")) {
    				prefsEdit.putString("savedToken", activeUser.getString("token"));
    			}
    		} else {
    			clearToken();
    		}
    		refreshUser();
    	}
    }
}