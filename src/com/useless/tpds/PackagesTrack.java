package com.useless.tpds;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

public class PackagesTrack extends PackagesDialog {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.track);		
		
		textNotFound = (TextView) findViewById(R.id.textNotFound);
		
		destRealname = (TextView) findViewById(R.id.destRealname);
		destUsername = (TextView) findViewById(R.id.destUsername);
		srcRealname = (TextView) findViewById(R.id.srcRealname);
		srcUsername = (TextView) findViewById(R.id.srcUsername);
        
		go = (Button) findViewById(R.id.getButton);
		go.setOnClickListener(this);
		goEnabled = (TableRow) findViewById(R.id.getEnabled);
		goDisabled = (TableRow) findViewById(R.id.getDisabled);
		
		refreshInfo();
		
		datasetUrl = "http://snarti.nu/?data=package&action=get";
		datasetUrl += "&token=" + activeUser.getString("token");
		getDataset();
		
		find = (AutoCompleteTextView)findViewById(R.id.find_package);
        find.setAdapter(new ArrayAdapter<String>(getBaseContext(), R.layout.simple_item, dataset));
        find.addTextChangedListener(watcher);
	}
	
	public static ArrayList<Bundle> getPath(String pathid, String token) {
		String requestUrl = "http://snarti.nu/?data=path&action=get";
		requestUrl += "&token=" + token;
		requestUrl += "&pathid=" + pathid;
		JSONObject result = Database.get(requestUrl);
		
		if(result != null && result.has("path")) {
			try {
				return makePath(result.getJSONArray("path"));
			} catch(Exception e) {
				Log.e("TPDS",e.getMessage());
			}
		}
		return null;
	}

	@Override
	protected JSONObject queryDatabase(Editable s) {
		String requestUrl = "http://snarti.nu/?data=package&action=get";
		requestUrl += "&token=" + activeUser.getString("token");
		requestUrl += "&pid=" + s;
		return Database.get(requestUrl);
	}

	@Override
	protected void useData(JSONObject dbResult) {
		try {
			path = makePath(dbResult.getJSONArray("path"));
			src = path.get(0);
			dest = path.get(path.size() - 1);
			pkg = UserAuth.buildBundle(dbResult.getJSONObject("package"));
		} catch(Exception e) {
			Log.e("TPDS",e.getMessage());
		}
	}

	@Override
	protected void refreshInfo() {
		if(dest != null && src != null) {
			notFound(false);
			setDest();
			setSrc();
			enableView(goEnabled,goDisabled,true);
		} else {
			notFound(true);
			clrDest();
			clrSrc();
			enableView(goEnabled,goDisabled,false);
		}
	}

	@Override
	public void onClick(View v) {
		if(v == go) {
			if(pkg != null) {
				setResult(PKG_FOUND,new Intent().putExtras(pkg));
			} else {
				setResult(PKG_NOT_FOUND);
			}
			finish();
		}
	}
}