package com.useless.tpds;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
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

public class Packages extends MapActivity {
	private Packages self = this;
	private Dialog dialogSend, dialogTrack;
	private Button buttonSend, buttonTrack;
	private Bundle activeUser;
	private GeoPoint currLoc;
	private MapView map;
	private MapController mapctl;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        activeUser = intent.getExtras();
        
        setContentView(R.layout.pkg);
        
        buttonSend = (Button) findViewById(R.id.sendPackage);
        buttonTrack = (Button) findViewById(R.id.trackPackage);
        buttonSend.setOnClickListener(clickListener);
        buttonTrack.setOnClickListener(clickListener);

        map = (MapView) findViewById(R.id.mapview);
        map.setBuiltInZoomControls(true);
        
        mapctl = map.getController();
        setLocation();
        mapctl.setZoom(16);
    }
	
	private OnClickListener clickListener = new OnClickListener() {
    	@Override
        public void onClick(View v) {
    		if(v == buttonSend) {
    			dialogSend = new PackagesSend(self,activeUser);
    			dialogSend.setOnDismissListener(dismissListener);
    			dialogSend.show();
    		} else if(v == buttonTrack) {
    			dialogTrack = new PackagesTrack(self,activeUser);
    			dialogTrack.setOnDismissListener(dismissListener);
    			dialogTrack.show();
    		}
        }
    };
    
    private OnDismissListener dismissListener = new OnDismissListener() {
    	@Override
        public void onDismiss(DialogInterface d) {
        	if(d == dialogSend) {
        		
        	} else if(d == dialogTrack) {
        		
        	}
        }
    };
	
	private void getLocation() {
		if(activeUser != null && activeUser.containsKey("latitude") && activeUser.containsKey("longitude")) {
        	try{
        		float lat = Float.valueOf(activeUser.getString("latitude"));
        		float lng = Float.valueOf(activeUser.getString("longitude"));
        		currLoc = new GeoPoint((int)(lat * 1E6),(int)(lng * 1E6));
        	} catch(Exception e) {
        		Log.e("TPDS",e.getMessage());
        	}
        }
	}
	
	private void setLocation() {
		getLocation();
		mapctl.setCenter(currLoc);
	}
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
	class DeliveryBoy extends Overlay {
		public static final int STANDARD = R.drawable.map_marker_red;
		public static final int ACTIVEUSER = R.drawable.map_marker_blue;
		public static final int HANDLER = R.drawable.map_marker_yellow;
		private GeoPoint point;
		private int type;
		private int num;
		private String name;
		
		public DeliveryBoy(GeoPoint point, int type, int num, String name) {
			super();
			this.point = point;
			this.type = type;
			this.num = num;
			this.name = name;
		}
	
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			Paint paint = new Paint();
			
			// converts latitude and longitude to pixel coordinates
			Point screenPoint = new Point();
			mapView.getProjection().toPixels(point, screenPoint);
			
			paint.setStrokeWidth(1);
			paint.setARGB(0, 0, 0, 0);
			paint.setStyle(Paint.Style.STROKE);
			
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), type);
			canvas.drawBitmap(bmp, screenPoint.x, screenPoint.y, paint);
			canvas.drawText(String.valueOf(num), screenPoint.x, screenPoint.y, paint);
		} 
		
		public String getName() {
			return name;
		}
	}
}