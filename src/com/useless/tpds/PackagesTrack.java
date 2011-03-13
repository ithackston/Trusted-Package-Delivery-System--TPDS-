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
import android.widget.TableRow;
import android.widget.TextView;

public class PackagesTrack extends PackagesDialog {
	
	public PackagesTrack(Context c, Bundle a) {
		super(c, a);
		
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
        find.setAdapter(new ArrayAdapter<String>(context, R.layout.simple_item, dataset));
        find.addTextChangedListener(watcher);
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshInfo();
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
			makePath(dbResult.getJSONArray("path"));
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
			dismiss();
		}
	}
}