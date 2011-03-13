package com.useless.tpds;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsAdd extends FriendsDialog {
    private AutoCompleteTextView usernameEntry;
    private String[] userList;
    private int numUsers;
    
	public FriendsAdd(Context context, Bundle a) {
		super(context,a);
		
		setContentView(R.layout.friends_add);
		
		getUsers();
		
		usernameEntry = (AutoCompleteTextView)findViewById(R.id.find_user);
		usernameEntry.setAdapter(new ArrayAdapter<String>(context, R.layout.simple_item, userList));
		usernameEntry.addTextChangedListener(watcher);
		
		realname = (TextView) findViewById(R.id.realname);
		username = (TextView) findViewById(R.id.username);
		
		imgTrusted = (ImageView) findViewById(R.id.img_trusted);
		imgDeliverable = (ImageView) findViewById(R.id.img_deliverable);
		
		checkTrusted = (CheckBox) findViewById(R.id.check_trusted);
		checkTrusted.setOnClickListener(this);
		checkTrusted.setChecked(false);
		trusted = false;
		checkDeliverable = (CheckBox) findViewById(R.id.check_deliverable);
		checkDeliverable.setOnClickListener(this);
		checkDeliverable.setChecked(false);
		deliverable = false;
		
		save = (Button) findViewById(R.id.save);
		saveDisabled = (Button) findViewById(R.id.save_disabled);
		save.setOnClickListener(this);
		
		setChecks();
		disableSave(true);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		//check status
		if(!checkTrusted.isChecked() && !checkDeliverable.isChecked()) {
			//disable save
			disableSave(true);
		} else {
			//enable save
			disableSave(false);
		}
	}
	
	private TextWatcher watcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			if(Arrays.binarySearch(userList,String.valueOf(s)) >= 0) {
				String requestUrl = "http://snarti.nu/?data=user&action=get";
				requestUrl += "&token=" + activeUser.getString("token");
				requestUrl += "&target=" + s;
				JSONObject result = Database.get(requestUrl);
				
				if(result != null && result.has("id")) {
					targetUser = UserAuth.buildBundle(result);
					realname.setText(targetUser.getString("realname"));
					username.setText(targetUser.getString("username"));
					return;
				}
			}
			realname.setText(context.getString(R.string.user_not_found));
			username.setText("");
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int after, int count) {
			//Do nothing
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			//Do nothing
		}
	};
	
	private void getUsers() {
    	String requestUrl = "http://snarti.nu/?data=user&action=list";
		requestUrl += "&token=" + activeUser.getString("token");
		JSONArray result = Database.getArray(requestUrl);
		
		numUsers = 0;
		
		if(result != null) {
			userList = new String[result.length()];
			for (int i = 0; i < result.length(); i++) {
				String f = "";
				try {
					f = result.getString(i);
				} catch(Exception e) {
					//no object error
				}
			    if(!f.equals("")) {
			    	numUsers++;
			    	userList[i] = f;
			    }
			}
		}
    }
}