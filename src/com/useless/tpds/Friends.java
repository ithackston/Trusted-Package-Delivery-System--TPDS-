package com.useless.tpds;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

class Friends extends ActivityGroup{
	public static Friends group;
	private ArrayList<View> history;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      this.history = new ArrayList<View>();
	      group = this;
	      
	      Intent intent = getIntent();
	      Bundle activeUser = intent.getExtras();
	      intent = new Intent(this,FriendsList.class);
	      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	      intent.putExtras(activeUser);
	      
	      View view = getLocalActivityManager().startActivity(getString(R.string.friends_list_app),intent).getDecorView();
	      replaceView(view);
	}
	
	public void replaceView(View v) {
		history.add(v);
		setContentView(v);
	}
	
	public void back() {
		if(history.size() > 0) {
			history.remove(history.size()-1);
			setContentView(history.get(history.size()-1));
		}else {
			finish();
		}
	}

   @Override
    public void onBackPressed() {
    	Friends.group.back();
        return;
    }
} 