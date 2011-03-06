package com.useless.tpds;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;

public class FriendsEdit extends FriendsDialog {
    private TableLayout alert;
    private RadioButton removeOnly, removeReport;
    private TextView alertMsg;
    
	public FriendsEdit(Context context, Bundle a, Bundle t) {
		super(context,a);
		
		setContentView(R.layout.friends_edit);
		
		targetUser = t;
		trusted = targetUser.getString("trusted").equals("1");
		deliverable = targetUser.getString("deliverable").equals("1");
		
		realname = (TextView) findViewById(R.id.realname);
		realname.setText(targetUser.getString("realname"));
		username = (TextView) findViewById(R.id.username);
		username.setText(targetUser.getString("username"));
		
		imgTrusted = (ImageView) findViewById(R.id.img_trusted);
		imgDeliverable = (ImageView) findViewById(R.id.img_deliverable);
		
		checkTrusted = (CheckBox) findViewById(R.id.check_trusted);
		checkTrusted.setOnClickListener(this);
		checkDeliverable = (CheckBox) findViewById(R.id.check_deliverable);
		checkDeliverable.setOnClickListener(this);
		
		save = (Button) findViewById(R.id.save);
		saveDisabled = (Button) findViewById(R.id.save_disabled);
		save.setOnClickListener(this);
		
		alert = (TableLayout) findViewById(R.id.alert);
		alertMsg = (TextView) findViewById(R.id.textViewAlert);
		alertMsg.setText(context.getString(R.string.remove_prompt,realname.getText()));
		
		removeOnly = (RadioButton) findViewById(R.id.remove_only);
		removeOnly.setOnClickListener(radioListener);
		removeReport = (RadioButton) findViewById(R.id.remove_report);
		removeReport.setOnClickListener(radioListener);
		
		setChecks();
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		//check status
		if(!checkTrusted.isChecked() && !checkDeliverable.isChecked()) {
			//show alert
			alert.setVisibility(android.view.View.VISIBLE);
			disableSave(true);
		} else {
			//hide alert
			alert.setVisibility(android.view.View.GONE);
			disableSave(false);
		}
	}
	
	protected View.OnClickListener radioListener = new View.OnClickListener() {
	    public void onClick(View v) {
	    	// Enables save button when user selects a radio button
	    	disableSave(false);
	    }
	};
	
	protected void saveChanges() {
		if(removeReport.isChecked()) {
			//remove from all friends lists
			if(!trusted && !deliverable) {
				String requestUrl = "http://snarti.nu/?data=friends&action=rem";
				requestUrl += "&token=" + activeUser.getString("token");
				requestUrl += "&handler=" + targetUser.getString("username");
				Database.get(requestUrl);
			}
		} else {
			super.saveChanges();
		}
	}
}