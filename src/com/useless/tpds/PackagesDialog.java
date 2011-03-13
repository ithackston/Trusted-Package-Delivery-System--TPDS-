package com.useless.tpds;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

abstract class PackagesDialog extends Dialog implements OnClickListener {
	protected Context context;
	protected AutoCompleteTextView find;
    protected Bundle activeUser,src,dest,pkg;
    protected ArrayList<Bundle> path;
    protected Button go;
    protected String[] dataset;
    protected String datasetUrl;
    protected TextView destUsername,destRealname,srcUsername,srcRealname,textNotFound;
    protected TableRow goDisabled,goEnabled;
    
	public PackagesDialog(Context c, Bundle a) {
		super(c);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		context = c;
		activeUser = a;
	}

	@Override
	abstract public void onClick(View v);
	
	public ArrayList<Bundle> getPath() {
		return path;
	}
	
	protected void makePath(JSONArray jsonpath) {
		path = new ArrayList<Bundle>();
		
		if(jsonpath != null) {
			for (int i = 0; i < jsonpath.length(); i++) {
				JSONObject f = null;
				try {
					f = jsonpath.getJSONObject(i);
				} catch(Exception e) {
					//no object error
				}
			    if(f != null && f.has("id")) {
			    	path.add(UserAuth.buildBundle(f));
			    }
			}
		}
	}
	
	protected void setDest() {
		if(dest != null && destRealname != null && destUsername != null) {
			destRealname.setText(dest.getString("realname"));
			destUsername.setText(dest.getString("username"));
		}
	}
	
	protected void setSrc() {
		if(src != null && srcRealname != null && srcUsername != null) {
			srcRealname.setText(src.getString("realname"));
			srcUsername.setText(src.getString("username"));
		}
	}
	
	protected void clrDest() {
		if(destRealname != null && destUsername != null) {
			destRealname.setText("");
			destUsername.setText("");
		}
	}
	
	protected void clrSrc() {
		if(srcRealname != null && srcUsername != null) {
			srcRealname.setText("");
			srcUsername.setText("");
		}
	}
	
	protected void notFound(boolean show) {
		if(show) {
			textNotFound.setVisibility(android.view.View.VISIBLE);
		} else {
			textNotFound.setVisibility(android.view.View.GONE);
		}
	}
	
	abstract protected void refreshInfo();
	abstract protected JSONObject queryDatabase(Editable s);
	abstract protected void useData(JSONObject dbResult);
	
	protected void getDataset() {
		if(datasetUrl == null) {
			return;
		}
		
		JSONArray result = Database.getArray(datasetUrl);
		
		if(result != null) {
			dataset = new String[result.length()];
			for (int i = 0; i < result.length(); i++) {
				String f = "";
				try {
					f = result.getString(i);
				} catch(Exception e) {
					//no object error
				}
			    if(!f.equals("")) {
			    	dataset[i] = f;
			    }
			}
		}
	}
	
	protected TextWatcher watcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			if(Arrays.binarySearch(dataset,String.valueOf(s)) >= 0) {
				JSONObject result = queryDatabase(s);
				
				if(result != null && result.has("id")) {
					useData(result);
				}
			}
			refreshInfo();
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
	
	protected void enableView(View enabled,View disabled,boolean show) {
    	if(show) {
    		enabled.setVisibility(android.view.View.VISIBLE);
    		disabled.setVisibility(android.view.View.GONE);
    	} else {
    		enabled.setVisibility(android.view.View.GONE);
    		disabled.setVisibility(android.view.View.VISIBLE);
    	}
    }
}