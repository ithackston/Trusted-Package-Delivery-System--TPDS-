package com.useless.tpds;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Friends extends ListActivity {
	private ProgressDialog progressDialog = null;
    private ArrayList<Bundle> friendsList = null;
    private FriendsAdapter adapter;
    private Runnable viewFriends;
    private Bundle activeUser;
    private int numFriends;
	
	private void getFriends(){
		friendsList = new ArrayList<Bundle>();
		
		String requestUrl = "http://snarti.nu/?data=friends&action=get";
		requestUrl += "&token=" + activeUser.getString("token");
		JSONArray result = Database.getArray(requestUrl);
		
		numFriends = 0;
		
		if(result != null) {
			for (int i = 0; i < result.length(); i++) {
				JSONObject f = null;
				try {
					f = result.getJSONObject(i);
				} catch(Exception e) {
					//no object error
				}
			    if(f != null && f.has("id")) {
			    	numFriends++;
			    	friendsList.add(UserAuth.buildBundle(f));
			    }
			}
		}
		runOnUiThread(returnRes);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent i = getIntent();
        activeUser = i.getExtras();
        
        setContentView(R.layout.friends);
        
        friendsList = new ArrayList<Bundle>();
        adapter = new FriendsAdapter(this, R.layout.friends_item, friendsList);
        setListAdapter(adapter);
       
        viewFriends = new Runnable(){
            @Override
            public void run() {
                getFriends();
            }
        };
        
        Thread thread =  new Thread(null, viewFriends, "TPDSFriend");
        thread.start();
        progressDialog = ProgressDialog.show(this, "Please wait...", "Retrieving data ...", true);
    }
    
    private Runnable returnRes = new Runnable() {
        @Override
        public void run() {
	    	if(friendsList != null && friendsList.size() > 0){
	    		adapter.notifyDataSetChanged();
	            for(int i = 0; i < numFriends; i++)
	            	adapter.add(friendsList.get(i));
	        }
	    	progressDialog.dismiss();
	    	adapter.notifyDataSetChanged();
        }
    };
    
    private class FriendsAdapter extends ArrayAdapter<Bundle> {
		private ArrayList<Bundle> items;
	
		public FriendsAdapter(Context context, int textViewResourceId, ArrayList<Bundle> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		
		@Override
		public View getView(int position, View v, ViewGroup parent) {
			Bundle friend = null;
			
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.friends_item, null);
			}
			
			if(position < numFriends) {
				friend = items.get(position);
			}
			
			if (friend != null) {
				boolean trusted = friend.getString("trusted").equals("1");
				boolean deliverable = friend.getString("deliverable").equals("1");
				ImageView imgTrusted = (ImageView) v.findViewById(R.id.trusted);
				ImageView imgDeliverable = (ImageView) v.findViewById(R.id.deliverable);
				
				TextView realname = (TextView) v.findViewById(R.id.realname);
				TextView username = (TextView) v.findViewById(R.id.username);
				realname.setText(friend.getString("realname"));
				username.setText(friend.getString("username"));
				
				if(!trusted) {
					imgTrusted.setImageResource(R.drawable.blank);
				}
				if(!deliverable) {
					imgDeliverable.setImageResource(R.drawable.blank);
				}
			}
			return v;
		}
	}
}