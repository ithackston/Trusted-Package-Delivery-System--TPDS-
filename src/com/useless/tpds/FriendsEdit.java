package com.useless.tpds;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

public class FriendsEdit extends Activity implements OnClickListener {
    private Bundle targetUser;
    private Button back, report;
    private CheckBox checkTrusted, checkDeliverable;
    private ImageView imgTrusted, imgDeliverable;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.friends_edit);
		Intent intent = getIntent();
		targetUser = intent.getExtras();
		
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		report = (Button) findViewById(R.id.report);
		report.setOnClickListener(this);
		
		checkTrusted = (CheckBox) findViewById(R.id.check_trusted);
		checkTrusted.setOnClickListener(this);
		checkDeliverable = (CheckBox) findViewById(R.id.check_deliverable);
		checkDeliverable.setOnClickListener(this);
		
		imgTrusted = (ImageView) findViewById(R.id.img_trusted);
		imgDeliverable = (ImageView) findViewById(R.id.img_deliverable);
	}
	
	@Override
	public void onClick(View v) {
		if(v == back) {
			//switch back to FriendsList
	        Intent intent = new Intent(getApplicationContext(), FriendsList.class);
	        intent.putExtras(targetUser);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	       
	        View view = Friends.group
	        	.getLocalActivityManager()
	        	.startActivity(getString(R.string.friends_list_app), intent)
	        	.getDecorView();
	       
	        Friends.group.replaceView(view);
		} else if(v == report) {
			//delete user
		} else if(v == checkTrusted) {
			
			//TODO Update database on change
			
			if(checkTrusted.isChecked()) {
				targetUser.putString("trusted","1");
				imgTrusted.setImageResource(R.drawable.trusted);
			} else {
				targetUser.putString("trusted","0");
				imgTrusted.setImageResource(R.drawable.blank);
			}
		} else if(v == checkDeliverable) {
			
			//TODO Update database on change
			
			if(checkDeliverable.isChecked()) {
				targetUser.putString("deliverable","1");
				imgDeliverable.setImageResource(R.drawable.deliverable);
			} else {
				targetUser.putString("deliverable","0");
				imgDeliverable.setImageResource(R.drawable.blank);
			}
		}
	}
}