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
import android.widget.TextView;

public class FriendsDialog extends Dialog implements OnClickListener {
	protected Context context;
    protected Bundle targetUser,activeUser;
    protected Button save, saveDisabled;
    protected CheckBox checkTrusted, checkDeliverable;
    protected ImageView imgTrusted, imgDeliverable;
    protected TextView username,realname;
    protected boolean trusted, deliverable;
    
	public FriendsDialog(Context c, Bundle a) {
		super(c);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		context = c;
		activeUser = a;
		targetUser = new Bundle();
	}
	
	protected void setChecks() {
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
	}
	
	protected void toggleTrust() {
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
	
	protected void toggleDeliverable() {
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
	
	protected void saveChanges() {
		String requestUrl = "";
		
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
	
	protected void disableSave(boolean disabled) {
		if(disabled) {
			save.setVisibility(android.view.View.GONE);
			saveDisabled.setVisibility(android.view.View.VISIBLE);
		} else {
			save.setVisibility(android.view.View.VISIBLE);
			saveDisabled.setVisibility(android.view.View.GONE);
		}
	}
}