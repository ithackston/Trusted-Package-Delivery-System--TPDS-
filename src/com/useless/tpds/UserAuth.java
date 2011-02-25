package com.useless.tpds;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;
import org.json.JSONObject;
import java.security.*;
import java.util.Iterator;

public abstract class UserAuth extends Activity {
	public static final int LOGIN_SUCCESSFUL = 0;
	public static final int LOGIN_UNSUCCESSFUL = 1;
	public static final int REGISTER_USER = 2;
	
	protected static final int minPasswordLength = 6;
	protected CheckBox savePassword;
	protected TableRow rowAlert;
	protected TextView textAlert;
	protected Editable username;
	protected Editable password;
	protected Context context;
	protected String prefs;
	
	//user data
	public Bundle activeUser;
	
	protected void alert(String msg) {
		rowAlert.setVisibility(android.view.View.VISIBLE);
		textAlert.setText(msg);
	}
	
	protected void storeToken() {
		//save token
		SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_filename),0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("savedToken", activeUser.getString("token"));
		editor.commit();
	}
	
	public static Bundle buildBundle(JSONObject o) {
		Bundle b = new Bundle();
		if(o != null) {
			@SuppressWarnings("rawtypes")
			Iterator i = o.keys();
			while(i.hasNext()) {
				String key = (String) i.next();
				if (o.has(key)) {
					try {
						b.putString(key,o.getString(key));
					} catch(Exception e) {
						continue;
					}
				}
			}
		}
		return b;
	}
	
	//convert a field's value to an string representation of md5
	protected static String md5(Editable str) {
		byte[] hash = null;
		
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
		    digest.update(String.valueOf(str).getBytes());
		    hash = digest.digest();
		} catch(Exception e) {
		}
		
		return bytesToHex(hash);
	}
	
	//convert bytes to a string representation of hex
	private static String bytesToHex(byte[] b) {
		char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
		                   '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		StringBuffer buf = new StringBuffer();
		for (int j=0; j<b.length; j++) {
			buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
			buf.append(hexDigit[b[j] & 0x0f]);
		}
		return buf.toString();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getParent();
        setResult(LOGIN_UNSUCCESSFUL);
	}
}
