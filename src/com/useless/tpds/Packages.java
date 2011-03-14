package com.useless.tpds;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Packages extends MapActivity implements OnClickListener {
	public static final int SEND_REQUEST = 0;
	public static final int TRACK_REQUEST = 1;
	
	private Button buttonSend, buttonTrack;
	private Bundle activeUser;
	private MapView map;
	private MapController mapCtl;
	private List<Overlay> mapOverlay;
	private ArrayList<Bundle> path;
	private Bundle pkg;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        activeUser = intent.getExtras();
        
        setContentView(R.layout.pkg);
        
        buttonSend = (Button) findViewById(R.id.sendPackage);
        buttonTrack = (Button) findViewById(R.id.trackPackage);
        buttonSend.setOnClickListener(this);
        buttonTrack.setOnClickListener(this);

        map = (MapView) findViewById(R.id.mapview);
        map.setBuiltInZoomControls(true);
        
        mapCtl = map.getController();
        mapOverlay = map.getOverlays();
        mapCtl.setZoom(16);
        
        refreshMap();
    }
	
	@Override
    public void onClick(View v) {
		if(v == buttonSend) {
			Intent i = new Intent(this,PackagesSend.class);
			i.putExtras(activeUser);
	        startActivityForResult(i, SEND_REQUEST);
		} else if(v == buttonTrack) {
			Intent i = new Intent(this,PackagesTrack.class);
			i.putExtras(activeUser);
	        startActivityForResult(i, TRACK_REQUEST);
		}
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == SEND_REQUEST) {
    		//data.getExtras();
    	} else if(requestCode == TRACK_REQUEST) {
    		//data.getExtras();
    	}
    }
    
    private void refreshMap() {
    	if(pkg == null) {
    		activeUser = refreshUser(activeUser);
    		setMarker(activeUser,0);
    		return;
    	}
    	String requestUrl = "http://snarti.nu/?data=package&action=get";
    	requestUrl += "&token=" + activeUser.getString("token");
    	requestUrl += "&pid=" + pkg.getString("id");
    	JSONObject result = Database.get(requestUrl);
    	
    	if(result != null && result.has("id")){
    		try {
    			path = PackagesDialog.makePath(result.getJSONArray("path"));
    			pkg = UserAuth.buildBundle(result.getJSONObject("package"));
    		} catch(Exception e) {
    			Log.e("TPDS",e.getMessage());
    		}
    	}
    }
    
    private Bundle refreshUser(Bundle user) {
    	String requestUrl = "http://snarti.nu/?data=user&action=get";
    	requestUrl += "&token=" + activeUser.getString("token");
    	if(user != activeUser) {
    		requestUrl += "&target=" + user.getString("username");
    	}
    	JSONObject result = Database.get(requestUrl);
    	
    	if(result != null && result.has("id")) {
    		user = UserAuth.buildBundle(result);
    	}
    	
    	return user;
    }
    
    private boolean removeMarker(String id) {
    	for(int i = 0; i < mapOverlay.size(); i++) {
    		DeliveryBoy b = (DeliveryBoy) mapOverlay.get(i);
    		if(b.isID(id)) {
    			mapOverlay.remove(i);
    			return true;
    		}
    	}
    	return false;
    }
    
    private void setMarker(Bundle user,int pos) {
    	int type = DeliveryBoy.STANDARD;
    	if(path != null) {
    		if(user == path.get(0)) {
    			type = DeliveryBoy.SOURCE;
    		} else if(user == path.get(path.size() - 1)) {
    			type = DeliveryBoy.DESTINATION;
    		}
    	}
    	if(user == activeUser) {
    		type = DeliveryBoy.ACTIVEUSER;
    	}
    	removeMarker(user.getString("id"));
    	mapOverlay.add(new DeliveryBoy(getLocation(user),type,pos,user));
    }
	
	private GeoPoint getLocation(Bundle user) {
		GeoPoint p = null;
		if(user != null && user.containsKey("latitude") && user.containsKey("longitude")) {
        	try{
        		float lat = Float.valueOf(user.getString("latitude"));
        		float lng = Float.valueOf(user.getString("longitude"));
        		p = new GeoPoint((int)(lat * 1E6),(int)(lng * 1E6));
        	} catch(Exception e) {
        		Log.e("TPDS",e.getMessage());
        	}
        }
		return p;
	}
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
	class DeliveryBoy extends Overlay {
		public static final int STANDARD = R.drawable.map_marker_blue;
		public static final int ACTIVEUSER = R.drawable.map_marker_yellow;
		public static final int SOURCE = R.drawable.map_marker_red;
		public static final int DESTINATION = R.drawable.map_marker_green;
		private GeoPoint point;
		private int type;
		private int num;
		private Bundle user;
		
		public DeliveryBoy(GeoPoint point, int type, int num, Bundle user) {
			super();
			this.point = point;
			this.type = type;
			this.num = num;
			this.user = user;
		}
	
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			
			// converts latitude and longitude to pixel coordinates
			Point screenPoint = new Point();
			mapView.getProjection().toPixels(point, screenPoint);
			
			Paint white = new Paint();
			white.setStrokeWidth(1);
			white.setARGB(255,255,255,255);
			white.setStyle(Paint.Style.STROKE);
			
			Paint black = new Paint();
			black.setAntiAlias(true);
			black.setTextAlign(Paint.Align.CENTER);
			black.setTextSize(20);
			black.setColor(android.graphics.Color.BLACK);
			black.setStyle(Paint.Style.STROKE);
			
			String label = String.valueOf(num);
			if(num == 0) {
				if(type == ACTIVEUSER) {
					label = "me";
					black.setTextSize(16);
				} else {
					label = "S";
				}
			} else if(num == path.size() - 1) {
				label = "F";
			}
			
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), type);
			int width = bmp.getWidth();
			int height = bmp.getHeight();
			canvas.drawBitmap(bmp, (screenPoint.x - (width / 2)), (screenPoint.y - height), white);
			canvas.drawText(label, (screenPoint.x), (screenPoint.y - (height / 2)), black);
		} 
		
		public String getName() {
			return user.getString("realname");
		}
		
		public boolean isID(String id) {
			if(user.getString("id").equals(id)) {
				return true;
			}
			return false;
		}
	}
}