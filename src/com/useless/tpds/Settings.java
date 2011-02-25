package com.useless.tpds;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Settings extends UserTab implements OnClickListener {
	private Button buttonLogout;
	
	public void refreshUser() {
		TextView textRealname = (TextView) findViewById(R.id.fieldRealname);
        TextView textUsername = (TextView) findViewById(R.id.fieldUsername);
        
        textRealname.setText(activeUser.getString("realname"));
        textUsername.setText(activeUser.getString("username"));
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.settings);
        
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