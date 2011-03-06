package com.useless.tpds;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;

public class FriendsEdit extends Dialog implements OnClickListener {
    private Bundle targetUser;
    private Button save, saveDisabled;
    private CheckBox checkTrusted, checkDeliverable;
    private ImageView imgTrusted, imgDeliverable;
    private TableLayout alert;
    private RadioButton removeOnly, removeReport;
    private TextView alertMsg,username,realname;
    private boolean trusted, deliverable;
    
	public FriendsEdit(Context context, Bundle t) {
		super(context);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
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
		
		alert = (TableLayout) findViewById(R.id.alert);
		alertMsg = (TextView) findViewById(R.id.textViewAlert);
		alertMsg.setText(context.getString(R.string.remove_prompt,realname.getText()));
		
		removeOnly = (RadioButton) findViewById(R.id.remove_only);
		removeOnly.setOnClickListener(this);
		removeReport = (RadioButton) findViewById(R.id.remove_report);
		removeReport.setOnClickListener(this);
		
		save = (Button) findViewById(R.id.save);
		saveDisabled = (Button) findViewById(R.id.save_disabled);
		save.setOnClickListener(this);
		
		if(trusted) {
			checkTrusted.setChecked(true);
		} else {
			checkTrusted.setChecked(false);
			imgTrusted.setImageResource(R.drawable.blank);
		}
		if(deliverable) {
			checkDeliverable.setChecked(true);
		} else {
			checkDeliverable.setChecked(false);
			imgDeliverable.setImageResource(R.drawable.blank);
		}
	}
	
	@Override
	public void onClick(View v) {
		if(v == save) {
			if(removeOnly.isChecked()) {
				//TODO Remove friend from this list only
			} else if(removeReport.isChecked()) {
				//TODO Remove all relationships with this friend
			} else {
				//TODO Update friend
			}
			dismiss();
		} else if(v == checkTrusted) {
			if(checkTrusted.isChecked()) {
				trusted = true;
				targetUser.putString("trusted","1");
				imgTrusted.setImageResource(R.drawable.trusted);
			} else {
				trusted = false;
				targetUser.putString("trusted","0");
				imgTrusted.setImageResource(R.drawable.blank);
			}
		} else if(v == checkDeliverable) {
			if(checkDeliverable.isChecked()) {
				deliverable = true;
				targetUser.putString("deliverable","1");
				imgDeliverable.setImageResource(R.drawable.deliverable);
			} else {
				deliverable = false;
				targetUser.putString("deliverable","0");
				imgDeliverable.setImageResource(R.drawable.blank);
			}
		} else if(v == removeOnly) {
			disableSave(false);
		} else if(v == removeReport) {
			disableSave(false);
		}
		
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
	
	private void disableSave(boolean disabled) {
		if(disabled) {
			save.setVisibility(android.view.View.GONE);
			saveDisabled.setVisibility(android.view.View.VISIBLE);
		} else {
			save.setVisibility(android.view.View.VISIBLE);
			saveDisabled.setVisibility(android.view.View.GONE);
		}
	}
}