package com.useless.tpds;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;

public class PackagesSend extends PackagesDialog {
	private TableRow useTrusted, useTrustedDisabled;
	private CheckBox useTrustedCheck;
	
	public PackagesSend(Context c, Bundle a) {
		super(c, a);
		
		src = activeUser;
		
		setContentView(R.layout.send);
		
		textNotFound = (TextView) findViewById(R.id.textNotFound);
		
		destRealname = (TextView) findViewById(R.id.destRealname);
		destUsername = (TextView) findViewById(R.id.destUsername);
		
		useTrusted = (TableRow) findViewById(R.id.useTrusted);
		useTrustedDisabled = (TableRow) findViewById(R.id.useTrustedDisabled);
		useTrustedCheck = (CheckBox) findViewById(R.id.useTrustedCheck);
		
		go = (Button) findViewById(R.id.sendButton);
		go.setOnClickListener(this);
		goEnabled = (TableRow) findViewById(R.id.sendEnabled);
		goDisabled = (TableRow) findViewById(R.id.sendDisabled);
		
		refreshInfo();
		
		datasetUrl = "http://snarti.nu/?data=user&action=list";
		datasetUrl += "&token=" + activeUser.getString("token");
		getDataset();
		
		find = (AutoCompleteTextView)findViewById(R.id.find_user);
        find.setAdapter(new ArrayAdapter<String>(context, R.layout.simple_item, dataset));
        find.addTextChangedListener(watcher);
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshInfo();
    }

	@Override
	protected JSONObject queryDatabase(Editable s) {
		String requestUrl = "http://snarti.nu/?data=user&action=get";
		requestUrl += "&token=" + activeUser.getString("token");
		requestUrl += "&target=" + s;
		return Database.get(requestUrl);
	}

	@Override
	protected void useData(JSONObject dbResult) {
		dest = UserAuth.buildBundle(dbResult);
	}

	@Override
	protected void refreshInfo() {
		if(dest != null) {
			notFound(false);
			setDest();
			enableView(useTrusted,useTrustedDisabled,true);
			enableView(goEnabled,goDisabled,true);
		} else {
			notFound(true);
			clrDest();
			enableView(useTrusted,useTrustedDisabled,false);
			enableView(goEnabled,goDisabled,false);
		}
	}

	@Override
	public void onClick(View v) {
		if(v == go) {
			if(dest != null) {
				String requestUrl = "http://snarti.nu/?data=package&action=send";
				requestUrl += "&token=" + activeUser.getString("token");
				requestUrl += "&recipient=" + dest.getString("username");
				
				if(useTrustedCheck.isChecked()) {
					requestUrl += "&usetrust";
				}
				
				JSONObject result = Database.get(requestUrl);
				if(result != null){
					if(result.has("id")) {
						try {
							makePath(result.getJSONArray("path"));
							pkg = UserAuth.buildBundle(result.getJSONObject("package"));
						} catch(Exception e) {
							Log.e("TPDS", e.getMessage());
						}
					} else {
						Log.e("TPDS","Unable to plan path.");
					}
				}
			}
			dismiss();
		}
	}
}