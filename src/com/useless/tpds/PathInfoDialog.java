package com.useless.tpds;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PathInfoDialog extends Dialog implements OnClickListener {
	private Bundle user, activeUser;
	private String pkgId;
	private Button close, handoff;
	private TextView username,realname,latitude,longitude,last;
    
	public PathInfoDialog(Context context, Bundle activeUser, Bundle user, Bundle pkg, boolean nextstop) {
		super(context);
		this.activeUser = activeUser;
		this.user = user;
		
		if(pkg != null && pkg.containsKey("id")) {
			this.pkgId = pkg.getString("id");
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pathinfo);
		
		realname = (TextView) findViewById(R.id.textViewRealname);
		realname.setText(user.getString("realname"));
		username = (TextView) findViewById(R.id.textViewUsername);
		username.setText(user.getString("username"));
		latitude = (TextView) findViewById(R.id.textViewLatitude);
		latitude.setText(user.getString("latitude"));
		longitude = (TextView) findViewById(R.id.textViewLongitude);
		longitude.setText(user.getString("longitude"));
		last = (TextView) findViewById(R.id.textViewLastTime);
		last.setText(user.getString("time"));
		
		close = (Button) findViewById(R.id.buttonClose);
		close.setOnClickListener(this);
		handoff = (Button) findViewById(R.id.buttonHandoff);
		handoff.setOnClickListener(this);
		
		if(nextstop) {
			handoff.setVisibility(android.view.View.VISIBLE);
		} else {
			handoff.setVisibility(android.view.View.GONE);
		}
	}
	
	@Override
	public void onClick(View v) {
		if(v == close) {
			dismiss();
		} else if(v == handoff && pkgId != null) {
			// Update the database with new handler
			String requestUrl = "http://snarti.nu/?data=package&action=handoff";
			requestUrl += "&token=" + activeUser.getString("token");
			requestUrl += "&pid=" + pkgId;
			requestUrl += "&handler=" + user.getString("username");
			JSONObject result = Database.get(requestUrl);
			
			if((result != null && result.has("error")) || result == null) {
				//TODO error handing off package
			}
			
			// let refreshMap() change the markers
			dismiss();
		}
	}
}