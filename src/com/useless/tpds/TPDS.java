package com.useless.tpds;

import org.json.JSONObject;

import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class TPDS extends TabActivity {
	public static final int LOGIN_REQUEST = 0;
	public static final int REGISTER_REQUEST = 1;
	public static final int MENU_REQUEST = 2;
	
	public Bundle activeUser;
	public SharedPreferences prefs;
	
	@SuppressWarnings("unused")
	private LocationLogger gpsLogger;
	private boolean gpsIsBound;
	private ServiceConnection gpsService = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			gpsLogger = ((LocationLogger.LocationLoggerBinder) service).getService();
			Log.i(LocationLogger.name,"LocationLogger connected.");
		}

		public void onServiceDisconnected(ComponentName name) {
			gpsLogger = null;
			Log.i(LocationLogger.name,"LocationLogger disconnected.");
		}
	};
	
	private void loadTabs() {
		Intent i = new Intent(this,Menu.class);
		i.putExtras(activeUser);
        startActivityForResult(i, MENU_REQUEST);
	}
	
	private void loadLogin() {
		Intent i = new Intent(this,Login.class);
        startActivityForResult(i, LOGIN_REQUEST);
	}
	
	private void loadRegister() {
		Intent i = new Intent(this,Register.class);
        startActivityForResult(i, REGISTER_REQUEST);
	}
	
	private boolean loginWithToken(String token) {
		//build query and get JSON
		String requestUrl = "http://snarti.nu/?data=user&action=login";
		requestUrl += "&token=" + token;
		JSONObject result = Database.get(requestUrl);
		
		if(result != null && result.has("token")) {
			//user object obtained, login successful
			activeUser = UserAuth.buildBundle(result);
			return true;
		}
		
		return false;
	}
	
	private boolean restoreSession() {
		if(prefs != null) {
			String token = prefs.getString("savedToken", "");
			if(token.length() > 1 && loginWithToken(token)) {
				return true;
			}
		}
		return false;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //get stored preferences
        prefs = getSharedPreferences(getString(R.string.prefs_filename),0);
        
        //TODO Require internet connection
        
        //attempt to restore saved session
        if(restoreSession()) {
        	loadTabs();
        } else {
        	//ask for login
            loadLogin();
        } 
    }
    
    private void startLocationLogger() {
    	bindService(new Intent(TPDS.this, LocationLogger.class).putExtras(activeUser), gpsService, Context.BIND_AUTO_CREATE);
        gpsIsBound = true;
    }
    
    private void stopLocationLogger() {
    	if (gpsIsBound) {
            // Detach our existing connection.
            unbindService(gpsService);
            gpsIsBound = false;
        }
    }
    
	//called when activity ends
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == LOGIN_REQUEST) {
    		if(resultCode == UserAuth.LOGIN_SUCCESSFUL) {
    			activeUser = data.getExtras();
    			startLocationLogger();
    			loadTabs();
    		} else if(resultCode == UserAuth.REGISTER_USER) {
    			loadRegister();
            } else if(resultCode == UserAuth.LOGIN_UNSUCCESSFUL) {
            	loadLogin();
            }
    	} else if(requestCode == REGISTER_REQUEST) {
    		if(resultCode == UserAuth.LOGIN_SUCCESSFUL) {
    			activeUser = data.getExtras();
    			startLocationLogger();
    			loadTabs();
    		} else {
    			loadLogin();
            }
    	} else if(requestCode == MENU_REQUEST) {
    		if(resultCode == Menu.LOGOUT) {
    			stopLocationLogger();
    			loadLogin();
    		} else {
    			stopLocationLogger();
    			finish();
            }
    	}
    }
}