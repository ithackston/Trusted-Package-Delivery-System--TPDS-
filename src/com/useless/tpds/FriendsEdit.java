package com.useless.tpds;

import org.json.JSONObject;

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
    private Bundle targetUser,activeUser;
    private Button save, saveDisabled;
    private CheckBox checkTrusted, checkDeliverable;
    private ImageView imgTrusted, imgDeliverable;
    private TableLayout alert;
    private RadioButton removeOnly, removeReport;
    private TextView alertMsg,username,realname;
    private boolean trusted, deliverable;
    
	public FriendsEdit(Context context, Bundle a, Bundle t) {
		super(context);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.friends_edit);
		
		targetUser = t;
		activeUser = a;
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
		removeOnly.setOnClickListener(radioListener);
		removeReport = (RadioButton) findViewById(R.id.remove_report);
		removeReport.setOnClickListener(radioListener);
		
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
			saveChanges();
			dismiss();
		} else if(v == checkTrusted) {
			toggleTrust();
		} else if(v == checkDeliverable) {
			toggleDeliverable();
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
	
	private View.OnClickListener radioListener = new View.OnClickListener() {
	    public void onClick(View v) {
	        disableSave(false);
	    }
	};
	
	private void toggleTrust() {
		if(checkTrusted.isChecked()) {
			trusted = true;
			targetUser.putString("trusted","1");
			imgTrusted.setImageResource(R.drawable.trusted);
		} else {
			trusted = false;
			targetUser.putString("trusted","0");
			imgTrusted.setImageResource(R.drawable.blank);
		}
	}
	
	private void toggleDeliverable() {
		if(checkDeliverable.isChecked()) {
			deliverable = true;
			targetUser.putString("deliverable","1");
			imgDeliverable.setImageResource(R.drawable.deliverable);
		} else {
			deliverable = false;
			targetUser.putString("deliverable","0");
			imgDeliverable.setImageResource(R.drawable.blank);
		}
	}
	
	private void saveChanges() {
		String requestUrl = "";
		
		if(removeReport.isChecked()) {
			//remove from all friends lists
			if(!trusted && !deliverable) {
				requestUrl = "http://snarti.nu/?data=friends&action=rem";
				requestUrl += "&token=" + activeUser.getString("token");
				requestUrl += "&handler=" + targetUser.getString("username");
				Database.get(requestUrl);
			}
		} else {
			/* NOTE the server application will automatically remove friend without
			 * any permissions set (i.e. trusted = false, deliverable = false)
			 */
			
			//update trust
			if(trusted) {
				requestUrl = "http://snarti.nu/?data=trust&action=add";
			} else {
				requestUrl = "http://snarti.nu/?data=trust&action=rem";
			}
			requestUrl += "&token=" + activeUser.getString("token");
			requestUrl += "&handler=" + targetUser.getString("username");
			Database.get(requestUrl);
			
			//update deliverable
			if(deliverable) {
				requestUrl = "http://snarti.nu/?data=deliverable&action=add";
			} else {
				requestUrl = "http://snarti.nu/?data=deliverable&action=rem";
			}
			requestUrl += "&token=" + activeUser.getString("token");
			requestUrl += "&handler=" + targetUser.getString("username");
			Database.get(requestUrl);
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