package com.useless.tpds;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
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
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

public class Packages extends MapActivity implements OnClickListener,OnDismissListener {
	public static final int SEND_REQUEST = 0;
	public static final int TRACK_REQUEST = 1;
	public static final int SAVED_REQUEST = 2;
	public static final int TAP_RADIUS = 28; //meters
	public static final double DEF_LATITUDE = 38.54168123571238;
	public static final double DEF_LONGITUDE = -121.74940466880798;
	public static final GeoPoint DEF_POINT = new GeoPoint((int)(DEF_LATITUDE * 1E6),(int)(DEF_LONGITUDE * 1E6));
	
	private Packages self = Packages.this;
	private ImageButton buttonSend, buttonTrack, buttonSaved, buttonReload;
	private TextView textViewPid, textViewStatus;
	private TableLayout pkgInfo;
	private Bundle activeUser;
	private MapView map;
	private MapController mapCtl;
	private List<Overlay> mapOverlay;
	private ArrayList<Bundle> path;
	private Bundle pkg;
	private boolean activeUserInPath;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProgressDialog progress = ProgressDialog.show(this, "Please Wait", "Loading map...", true);
        
        Intent intent = getIntent();
        activeUser = intent.getExtras();
        
        setContentView(R.layout.pkg);
        
        buttonSend = (ImageButton) findViewById(R.id.buttonSend);
        buttonTrack = (ImageButton) findViewById(R.id.buttonTrack);
        buttonSaved = (ImageButton) findViewById(R.id.buttonSaved);
        buttonReload = (ImageButton) findViewById(R.id.buttonReload);
        buttonSend.setOnClickListener(this);
        buttonTrack.setOnClickListener(this);
        buttonSaved.setOnClickListener(this);
        buttonReload.setOnClickListener(this);
        
        pkgInfo = (TableLayout) findViewById(R.id.pkgInfo);
        textViewPid = (TextView) findViewById(R.id.textViewPid);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);

        map = (MapView) findViewById(R.id.mapview);
        map.setBuiltInZoomControls(true);
        
        mapCtl = map.getController();
        mapOverlay = map.getOverlays();
        mapCtl.setZoom(16);
        progress.dismiss();
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
		} else if(v == buttonReload) {
			refreshMap();
		} else if(v == buttonSaved) {
			Intent i = new Intent(this,PackagesSaved.class);
			i.putExtras(activeUser);
	        startActivityForResult(i, SAVED_REQUEST);
		}
    }
	
	@Override
	public void onDismiss(DialogInterface d) {
		refreshMap();
	}
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode == PackagesDialog.PKG_FOUND) {
    		pkg = data.getExtras();
    		refreshMap();
    	} else {
    		//TODO no path found error
    	}
    }
    
    private void refreshMap() {
    	activeUser = refreshUser(activeUser);
    	
    	// tell the map to expect a reload, clear previous markers
    	map.invalidate();
    	mapOverlay.clear();
    	
    	// no package yet, just put a marker for the current user
    	if(pkg == null) {
    		setMarker(activeUser,0,false);
    		GeoPoint p = getLocation(activeUser);
    		if(p != null) {
    			mapCtl.setCenter(p);
    		}
    		return;
    	}
    	
    	// build request string
    	String requestUrl = "http://snarti.nu/?data=package&action=get";
    	requestUrl += "&token=" + activeUser.getString("token");
    	requestUrl += "&pid=" + pkg.getString("id");
    	JSONObject result = Database.get(requestUrl);
    	
    	// unsuccessful request
    	if(result == null || !result.has("path") || !result.has("package")) {
    		Log.e("TPDS","Request unsuccessful: " + requestUrl);
    		setMarker(activeUser,0,false);
    		GeoPoint p = getLocation(activeUser);
    		if(p != null) {
    			mapCtl.setCenter(p);
    		}
    		return;
    	}
    	
    	activeUserInPath = false;
    	
		try {
			path = PackagesDialog.makePath(result.getJSONArray("path"));
			pkg = UserAuth.buildBundle(result.getJSONObject("package"));
			
			String activeId = activeUser.getString("id");
			String handlerId = pkg.getString("handler");
			boolean handoff = false;
			Bundle handler = null;
			
			for(int i = 0; i < path.size(); i++) {
				Bundle current = path.get(i);
				setMarker(current,i,handoff);
				
				// reset hand off bit
				if(handoff == true) {
					handoff = false;
				}
				
				if(current.getString("id").equals(handlerId)) {
					// center map over handler
					handler = current;
					GeoPoint p = getLocation(handler);
					mapCtl.animateTo(p);
					
					if(handlerId.equals(activeId)) {
						// add hand off option to next node
						handoff = true;
						// don't generate another activeUser marker later on
						activeUserInPath = true;
					}
				}
			}
			
			// refresh package information
			refreshPackage(handler);
			
		} catch(Exception e) {
			Log.e("TPDS",e.toString());
		}
    	
    	if(!activeUserInPath) {
			setMarker(activeUser,0,false);
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
    
	private void refreshPackage(Bundle handler) {
    	if(pkg != null && pkg.containsKey("id")) {
    		pkgInfo.setVisibility(android.view.View.VISIBLE);
    		textViewPid.setText(pkg.getString("id"));
    		
    		String hid = handler.getString("id");
    		String recipient = pkg.getString("recipient");

			if(hid.equals(recipient)) {
				textViewStatus.setText("Delivered");
				
				// remove package from database
				String requestUrl = "http://snarti.nu/?data=package&action=rem";
				requestUrl += "&token=" + activeUser.getString("token");
				requestUrl += "&pid=" + pkg.getString("id");
				Database.get(requestUrl);
			} else if(handler != null) {
				textViewStatus.setText(handler.getString("realname"));
			}
    	} else {
    		pkgInfo.setVisibility(android.view.View.GONE);
    	}
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
    
    private void setMarker(Bundle user,int pos,boolean nextstop) {
    	if(user == activeUser && (!user.containsKey("latitude") || !user.containsKey("longitude"))) {
    		mapCtl.setCenter(DEF_POINT);
    		return;
    	}
    	
    	int type = DeliveryBoy.STANDARD;
    	
    	if(path != null) {
    		String id = user.getString("id");
    		
			if(id.equals(path.get(0).getString("id"))) {
				type = DeliveryBoy.SOURCE;
			} else if(id.equals(path.get(path.size() - 1).getString("id"))) {
				type = DeliveryBoy.DESTINATION;
			} else if(id.equals(pkg.getString("handler"))) {
				type = DeliveryBoy.HANDLER;
			}
    	}
    	
    	if(user == activeUser) {
    		type = DeliveryBoy.ACTIVEUSER;
    	}
    	
    	removeMarker(user.getString("id"));
    	mapOverlay.add(new DeliveryBoy(getLocation(user),type,pos,user,nextstop));
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
		public static final int HANDLER = R.drawable.map_marker_pkg;
		private GeoPoint point;
		private int type;
		private Bundle user;
		private boolean nextstop;
		private Paint black;
		private String label;
		
		public DeliveryBoy(GeoPoint point, int type, int num, Bundle user,boolean nextstop) {
			super();
			this.point = point;
			this.type = type;
			this.user = user;
			this.nextstop = nextstop;
			
			black = new Paint();
			black.setAntiAlias(true);
			black.setTextAlign(Paint.Align.CENTER);
			black.setTextSize(20);
			black.setColor(android.graphics.Color.BLACK);
			black.setStyle(Paint.Style.STROKE);
			
			if(num == 0) {
				if(type == ACTIVEUSER) {
					label = "me";
					black.setTextSize(16);
				} else {
					label = "S";
				}
			} else if(num == path.size() - 1) {
				label = "F";
			} else {
				label = String.valueOf(num);
			}
		}
	
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			
			// converts latitude and longitude to pixel coordinates
			Point screenPoint = new Point();
			mapView.getProjection().toPixels(point, screenPoint);
			
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), type);
			int markerWidth = bmp.getWidth();
			int markerHeight = bmp.getHeight();
			
			canvas.drawBitmap(bmp, (screenPoint.x - (markerWidth / 2)), (screenPoint.y - markerHeight), black);
			
			if(type != HANDLER) {
				canvas.drawText(label, (screenPoint.x), (screenPoint.y - (markerHeight / 2)), black);
			}
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
		
		public boolean onTap(GeoPoint p, MapView mv) {
			GeoPointMod pMod = new GeoPointMod(p.getLatitudeE6(),p.getLongitudeE6());
			double dist = pMod.distanceTo(point);
			if(dist <= TAP_RADIUS) {
				PathInfoDialog pathInfo = new PathInfoDialog(self,activeUser,this.user,pkg,this.nextstop);
				pathInfo.setOnDismissListener(self);
				pathInfo.show();
	    		
				return true;
			}
			
			return false;
		}
	}
}