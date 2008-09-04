package com.android.google.operation;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.R;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.GeoPoint;

public class TutorialOnMaps extends MapActivity {
	private static MapView mMapView;
	//protected OverlayController mOverlayController;
	protected List<Overlay> mOverlayList;
	private Button mZin;
	private Button mZout;
	private Button mPanN;
	private Button mPanE;
	private Button mPanW;
	private Button mPanS;
	private Button mGps;
	private Button mSat;
	private Button mTraffic;
	private String mDefCaption = "";
	private GeoPoint mDefGeoPoint;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.google_map);

		// Get the map view from resource file
		mMapView = (MapView) findViewById(R.id.mv);

		// Initialize the map center, zoom scale, and display mode
		// mDefGeoPoint = new GeoPoint(37423398,-122086507); // Google HQ
		mDefGeoPoint = new GeoPoint(39041907, -94591640); // The Country Club
															// Plaza
		mMapView.getController().setZoom(16);
		mMapView.getController().setCenter(mDefGeoPoint);
		mDefCaption = "KC Country Club Plaza";

		// Set up the overlay controller
		mOverlayList = mMapView.getOverlays();// createOverlayController();
		MyOverlay mo = new MyOverlay();
		mOverlayList.add(mo);

		// Set up the button for "Zoom In"
		mZin = (Button) findViewById(R.id.zin);
		mZin.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View arg0) {
				zoomIn();
			}
		});

		// Set up the button for "Zoom Out"
		mZout = (Button) findViewById(R.id.zout);
		mZout.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View arg0) {
				zoomOut();
			}
		});

		// Set up the button for "Pan North"
		mPanN = (Button) findViewById(R.id.pann);
		mPanN.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View arg0) {
				panNorth();
			}
		});

		// Set up the button for "Pan East"
		mPanE = (Button) findViewById(R.id.pane);
		mPanE.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View arg0) {
				panEast();
			}
		});

		// Set up the button for "Pan West"
		mPanW = (Button) findViewById(R.id.panw);
		mPanW.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View arg0) {
				panWest();
			}
		});

		// Set up the button for "Pan South"
		mPanS = (Button) findViewById(R.id.pans);
		mPanS.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View arg0) {
				panSouth();
			}
		});

		// Set up the button for "GPS"
		mGps = (Button) findViewById(R.id.gps);
		mGps.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View arg0) {
				centerOnGPSPosition();
			}
		});

		// Set up the button for "Satellite toggle"
		mSat = (Button) findViewById(R.id.sat);
		mSat.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View arg0) {
				toggleSatellite();
			}
		});

		// Set up the button for "Traffic toggle"
		mTraffic = (Button) findViewById(R.id.traffic);
		mTraffic.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View arg0) {
				toggleTraffic();
			}
		});

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			panWest();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			panEast();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			panNorth();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			panSouth();
			return true;
		}

		return false;
	}

	// This is used draw an overlay on the map
	protected class MyOverlay extends Overlay {
		@Override
		public void draw(Canvas canvas, MapView mv, boolean shadow) {
			super.draw(canvas, mv, shadow);

			if (mDefCaption.length() == 0) {
				return;
			}

			Paint p = new Paint();
			int[] scoords = new int[2];
			int sz = 5;

			//mv.getProjection()
			// Convert to screen coords
			//pc.getGeoPointXY(mDefGeoPoint, scoords);

			// Draw point caption and its bounding rectangle
			p.setTextSize(14);
			p.setAntiAlias(true);
			int sw = (int) (p.measureText(mDefCaption) + 0.5f);
			int sh = 25;
			int sx = scoords[0] - sw / 2 - 5;
			int sy = scoords[1] - sh - sz - 2;
			RectF rec = new RectF(sx, sy, sx + sw + 10, sy + sh);

			p.setStyle(Style.FILL);
			p.setARGB(128, 255, 0, 0);
			canvas.drawRoundRect(rec, 5, 5, p);
			p.setStyle(Style.STROKE);
			p.setARGB(255, 255, 255, 255);
			canvas.drawRoundRect(rec, 5, 5, p);

			canvas.drawText(mDefCaption, sx + 5, sy + sh - 8, p);

			// Draw point body and outer ring
			p.setStyle(Style.FILL);
			p.setARGB(88, 255, 0, 0);
			p.setStrokeWidth(1);
			RectF spot = new RectF(scoords[0] - sz, scoords[1] + sz, scoords[0]
					+ sz, scoords[1] - sz);
			canvas.drawOval(spot, p);

			p.setARGB(255, 255, 0, 0);
			p.setStyle(Style.STROKE);
			canvas.drawCircle(scoords[0], scoords[1], sz, p);
		}
	}

	public void panWest() {
		GeoPoint pt = new GeoPoint(mMapView.getMapCenter().getLatitudeE6(),
				mMapView.getMapCenter().getLongitudeE6()
						- mMapView.getLongitudeSpan() / 4);
		mMapView.getController().setCenter(pt);
	}

	public void panEast() {
		GeoPoint pt = new GeoPoint(mMapView.getMapCenter().getLatitudeE6(),
				mMapView.getMapCenter().getLongitudeE6()
						+ mMapView.getLongitudeSpan() / 4);
		mMapView.getController().setCenter(pt);
	}

	public void panNorth() {
		GeoPoint pt = new GeoPoint(mMapView.getMapCenter().getLatitudeE6()
				+ mMapView.getLatitudeSpan() / 4, mMapView.getMapCenter()
				.getLongitudeE6());
		mMapView.getController().setCenter(pt);
	}

	public void panSouth() {
		GeoPoint pt = new GeoPoint(mMapView.getMapCenter().getLatitudeE6()
				- mMapView.getLatitudeSpan() / 4, mMapView.getMapCenter()
				.getLongitudeE6());
		mMapView.getController().setCenter(pt);
	}

	public void zoomIn() {
		mMapView.getController().zoomIn();//(mMapView.getZoomLevel() + 1);
	}

	public void zoomOut() {
		mMapView.getController().zoomOut();//(mMapView.getZoomLevel() - 1);
	}

	public void toggleSatellite() {
		mMapView.setSatellite(true);
	}

	public void toggleTraffic() {
		mMapView.setTraffic(true);
	}

	public int getDistance(double lat1, double lon1, double lat2, double lon2) {
		Location loc1 = new Location("tokyo");
		loc1.setLatitude(lat1);
		loc1.setLongitude(lon1);
		Location loc2 = new Location("los angles");
		loc2.setLatitude(lat2);
		loc2.setLongitude(lon2);
		return (int) (loc2.distanceTo(loc1));
	}

	private void centerOnGPSPosition() {
		String provider = "gps";
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// NOTE: When LocationManager is called the first time, lat / lon is
		// initialized to 0.
		// Subsequent calls are fine and fast.
		//lm.getLastKnownLocation(provider);
		Location loc = lm.getLastKnownLocation(provider);

		mDefGeoPoint = new GeoPoint((int) (loc.getLatitude() * 1000000),
				(int) (loc.getLongitude() * 1000000));
		mDefCaption = "GPS location";

		mMapView.getController().animateTo(mDefGeoPoint);
		mMapView.getController().setCenter(mDefGeoPoint);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}