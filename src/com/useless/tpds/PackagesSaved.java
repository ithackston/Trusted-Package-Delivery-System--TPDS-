package com.useless.tpds;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PackagesSaved extends ListActivity {
	private Bundle activeUser;
	private ArrayList<JSONObject> pkgList = null;
	private int numPkgs;
	private PkgsAdapter adapter;
	private Runnable viewPkgs;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent i = getIntent();
        activeUser = i.getExtras();
        
		setContentView(R.layout.pkg_saved);
		
		viewPkgs= new Runnable(){
            @Override
            public void run() {
                getPkgs();
            }
        };
        
        //dump the old list and adapter
		pkgList = new ArrayList<JSONObject>();
        adapter = new PkgsAdapter(this, R.layout.pkg_item, pkgList);
        setListAdapter(adapter);
        
        //update the list
		Thread thread =  new Thread(null, viewPkgs, "TPDSPkgs");
        thread.start();
	}
	
	@Override
    protected void onListItemClick(ListView lv, View v, int position, long id) {
		try {
			String requestUrl = "http://snarti.nu/?data=package&action=get";
			requestUrl += "&token=" + activeUser.getString("token");
			requestUrl += "&pid=" + pkgList.get(position).getString("id");
			JSONObject result = Database.get(requestUrl);
			
			if(result != null && result.has("package")) {
				Bundle pkg = UserAuth.buildBundle(result.getJSONObject("package"));
				
				if(pkg != null) {
					setResult(PackagesDialog.PKG_FOUND,new Intent().putExtras(pkg));
				} else {
					setResult(PackagesDialog.PKG_NOT_FOUND);
				}
			}
		} catch(Exception e) {
			Log.e("TPDS",e.getMessage());
		}
		
		finish();
    }
	
	private void getPkgs(){
		pkgList = new ArrayList<JSONObject>();
		
		String requestUrl = "http://snarti.nu/?data=package&action=mine";
		requestUrl += "&token=" + activeUser.getString("token");
		JSONArray result = Database.getArray(requestUrl);
		
		numPkgs = 0;
		
		if(result != null) {
			for (int i = 0; i < result.length(); i++) {
				JSONObject f = null;
				try {
					f = result.getJSONObject(i);
				} catch(Exception e) {
					//no object error
				}
			    if(f != null && f.has("id")) {
			    	numPkgs++;
			    	pkgList.add(f);
			    }
			}
		}
		
		runOnUiThread(returnRes);
	}
	
	private Runnable returnRes = new Runnable() {
        @Override
        public void run() {
	    	if(pkgList != null && pkgList.size() > 0){
	    		adapter.notifyDataSetChanged();
	            for(int i = 0; i < numPkgs; i++)
	            	adapter.add(pkgList.get(i));
	        }
	    	adapter.notifyDataSetChanged();
        }
    };
	
	private class PkgsAdapter extends ArrayAdapter<JSONObject> {
		private ArrayList<JSONObject> items;
	
		public PkgsAdapter(Context context, int textViewResourceId, ArrayList<JSONObject> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		
		@Override
		public View getView(int position, View v, ViewGroup parent) {
			JSONObject pkg = null;
			
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.pkg_item, null);
			}
			
			if(position < numPkgs) {
				pkg = items.get(position);
			}
			
			if (pkg != null) {
				TextView source = (TextView) v.findViewById(R.id.source);
				TextView destination = (TextView) v.findViewById(R.id.destination);
				try {
					source.setText(pkg.getString("source"));
					destination.setText(pkg.getString("destination"));
				} catch(Exception e) {
					Log.e("TPDS",e.getMessage());
					source.setText("Unknown");
					destination.setText("Unknown");
				}
			}
			return v;
		}
	}
}