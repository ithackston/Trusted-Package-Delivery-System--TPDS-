package com.useless.tpds;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationLogger extends Service implements LocationListener {
	public static final long GPS_INTERVAL = 20000; // milliseconds
	public static final float GPS_MIN_DIST = 0; // meters
	public static final String name = "TPDS GPS";
	private LocationManager mgr;
    private String bestProvider;
    
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEdit;
    
    private boolean started;
    
    private final IBinder llBinder = new LocationLoggerBinder();
    
	public void onCreate() { 
		Criteria criteria = new Criteria();
		mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		bestProvider = mgr.getBestProvider(criteria, true);
        Log.i(name, "Using provider: " + bestProvider);
    	startGPS();
    	
    	prefs = getSharedPreferences(getString(R.string.prefs_filename),0);
    	prefsEdit = prefs.edit();
	}
	
	public class LocationLoggerBinder extends Binder
	{
		public LocationLogger getService()
		{
			Log.i(name,"LocationLogger.getService called.");
			return LocationLogger.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.i(name,"onStartCommand called.");
		//startGPS();
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		stopGPS();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(name,"IBinder called.");
		return llBinder;
	}
	
	public void startGPS() {
		Log.i(name,"Starting LocationManager.");
		mgr.requestLocationUpdates(bestProvider, GPS_INTERVAL, GPS_MIN_DIST, this);
		started = true;
	}
	
	public void stopGPS() {
		if(started) {
			Log.i(name,"Stopping LocationManager.");
			mgr.removeUpdates(this);
		}
	}
	
	private void logLocation(Location location) {
		// post location to database
		String requestUrl = "http://snarti.nu/?data=location&action=add";
		requestUrl += "&lat=" + location.getLatitude();
		requestUrl += "&long=" + location.getLongitude();
		Log.i(name,"Posting location: " + requestUrl);
		Database.get(requestUrl);
		
		// save last location to preferences
		prefsEdit.putFloat("last_latitude", (float) location.getLatitude());
		prefsEdit.putFloat("last_longitude", (float) location.getLongitude());
		prefsEdit.putLong("last_time", location.getTime());
		prefsEdit.commit();
	}

	@Override
	public void onLocationChanged(Location location) {
		logLocation(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// Do nothing, this is just here for the interface.
	}

	@Override
	public void onProviderEnabled(String provider) {
		// Do nothing, this is just here for the interface.
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Do nothing, this is just here for the interface.
	}
}