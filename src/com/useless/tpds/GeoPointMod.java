package com.useless.tpds;

import com.google.android.maps.GeoPoint;

class GeoPointMod extends GeoPoint {
	static final double Radius = 6371009; // radius of earth in meters

	public GeoPointMod(int latitudeE6, int longitudeE6) {
		super(latitudeE6, longitudeE6);
	}
	
	public double distanceTo(GeoPoint p) {
		double lat1 = this.getLatitudeE6()/1E6;
		double lat2 = p.getLatitudeE6()/1E6;
		double lon1 = this.getLongitudeE6()/1E6;
		double lon2 = p.getLongitudeE6()/1E6;
		double dLat = Math.toRadians(lat2-lat1);
		double dLon = Math.toRadians(lon2-lon1);
		double a = Math.sin(dLat/2) 
				* Math.sin(dLat/2) 
				+ Math.cos(Math.toRadians(lat1)) 
				* Math.cos(Math.toRadians(lat2)) 
				* Math.sin(dLon/2) 
				* Math.sin(dLon/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return Radius * c;
	}
}