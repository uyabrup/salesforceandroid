/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.viewer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.android.R;
import com.android.google.operation.ChartAPICaller;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

/**
 * This class is responsible for dashboard. At present. it calls google chart api with hard-coded parameters.
 * In future, it will read parameters dynamically from UI or salesforce.
 * 
 * @author Dai Odahara
 * 
 */
public class ChartViewer extends Activity {
	private static final String TAG = "DashBoardList";

	/**
	 * Called when the activity is first created. TODO access to google chart
	 * api server dynamically
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.dashboard_array);

		try {
			ChartAPICaller ca = new ChartAPICaller();
			URL lineUrl = new URL(ca.getLineChartURL());
				//"http://chart.apis.google.com/chart?cht=lc&chf=bg,s,304040|c,lg,0,363433,1.0,2E2B2A,0.0&chg=20,20,3,2&chdl=Lead|Opp&chd=e:AAczWZv.5m,zMmZWZMzGa&chxl=0:||25|50|75|100|1:|USD|2:|June|July|Aug|Sep|3:|2008|2008|2008|2008|4:|Month&chco=CA3D05,87CEEB&chxp=1,50.0|4,50.0&chxs=0,FFFFFF,18,0|1,FFFFFF,18,0|2,FFFFFF,18,0|3,FFFFFF,18,0|4,FFFFFF,22,0&chls=3,1,0|3,1,0&chxt=y,y,x,x,x&chs=500x450&chxr=1,0,100|4,0,100&chts=FFFFFF,22&chtt=Opporutynity+vs+Lead|(in+billions+of+deal)");
			URL gomUrl = new URL(ca.getOmeterChartURL());
					//"http://chart.apis.google.com/chart?cht=gom&chf=bg,s,304040&chs=500x250&chd=e:5m&chco=FF0000,FF6633,FFFF00,99FF00,009900&chl=Goal");
			URL pieUrl = new URL(ca.getPieChartURL());
					//"http://chart.apis.google.com/chart?cht=p3&chf=bg,s,304040&chs=600x300&chd=e:czczGa&chco=CACACA,DF7417,01A1DB&chts=FFFFFF,16&chl=Safari|Firefox|IE&chtt=A+Better+Web");

			getAndShowImage(lineUrl, R.id.dashboard1);
			getAndShowImage(gomUrl, R.id.dashboard2);
			getAndShowImage(pieUrl, R.id.dashboard3);

		} catch (MalformedURLException ex) {
			Log.v(TAG, ex.toString());
		}
	}

	/**
	 * Connect to the URL given and get file and set it on resource
	 * 
	 * @param url
	 * @param resource
	 */
	private void getAndShowImage(URL url, int resource) {
		try {
			ImageView imgViewer = (ImageView) findViewById(resource);
			imgViewer.setImageBitmap(BitmapFactory.decodeStream(url
					.openStream()));
		} catch (IOException ex) {
			Log.v(TAG, ex.toString());
		}
	}

}
