/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.viewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import com.android.R;
import com.android.google.operation.ChartAPICaller;
import com.android.salesforce.operation.ApexApiCaller;
import com.android.salesforce.util.StaticInformation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.widget.Gallery.LayoutParams;

/**
 * This class is responsible for dashboard. At present. it calls google chart
 * api with hard-coded parameters. In future, it will read parameters
 * dynamically from UI or salesforce.
 * 
 * TODO changed to dynamic chart viewer
 * 
 * @author Dai Odahara
 * 
 */
public class ChartViewer extends Activity implements
		AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {
	private static final String TAG = "DashBoardList";
	private ImageSwitcher mSwitcher;
	private final Handler handler = new Handler();
	private static TextView lv;

	private String json;
	 
	/**
	 * Called when the activity is first created. TODO access to google chart
	 * api server dynamically
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.chart_viewer);

		// for temp json num
        Bundle bundle = getIntent().getExtras();		
        json = bundle.getString("json");
        
        
		Button button = (Button) findViewById(R.id.loading_button);
		lv = (TextView) findViewById(R.id.loading_title);
		lv.setText("Loading...");
		lv.setVisibility(View.INVISIBLE);
		
		try {

			ChartAPICaller ca = new ChartAPICaller();
			URL lineUrl = new URL(ca.getLineChartURL());
			// "http://chart.apis.google.com/chart?cht=lc&chf=bg,s,304040|c,lg,0,363433,1.0,2E2B2A,0.0&chg=20,20,3,2&chdl=Lead|Opp&chd=e:AAczWZv.5m,zMmZWZMzGa&chxl=0:||25|50|75|100|1:|USD|2:|June|July|Aug|Sep|3:|2008|2008|2008|2008|4:|Month&chco=CA3D05,87CEEB&chxp=1,50.0|4,50.0&chxs=0,FFFFFF,18,0|1,FFFFFF,18,0|2,FFFFFF,18,0|3,FFFFFF,18,0|4,FFFFFF,22,0&chls=3,1,0|3,1,0&chxt=y,y,x,x,x&chs=500x450&chxr=1,0,100|4,0,100&chts=FFFFFF,22&chtt=Opporutynity+vs+Lead|(in+billions+of+deal)");
			//URL gomUrl = new URL(ca.getOmeterChartURL());
			// "http://chart.apis.google.com/chart?cht=gom&chf=bg,s,304040&chs=500x250&chd=e:5m&chco=FF0000,FF6633,FFFF00,99FF00,009900&chl=Goal");
			//URL pieUrl = new URL(ca.getPieChartURL());
			// "http://chart.apis.google.com/chart?cht=p3&chf=bg,s,304040&chs=600x300&chd=e:czczGa&chco=CACACA,DF7417,01A1DB&chts=FFFFFF,16&chl=Safari|Firefox|IE&chtt=A+Better+Web");

			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Toast.makeText(ChartViewer.this, "Loading Google Chart API...", Toast.LENGTH_LONG).show();

					processLoad();
				}
			});
		} catch (MalformedURLException ex) {
			Log.v(TAG, ex.toString());
		}
	}

	// make query from json
	public HashMap<String, HashMap> cookQuery(String json) {
		String queryString = "";
		HashMap<String, HashMap> rnd = new HashMap<String, HashMap>();
		String l = "components=anyType";
		int sa = json.indexOf(l);
		String temp = json.substring(sa, json.length());
		//String temp = json.substring(sa + l.length() + 1, json.length());
		//String[] items = temp.split(l + "\\{");
		
		//Log.v(TAG, temp);
		String[] items = temp.split(l + "\\{");

		int s1 = 0;
		int e1 = 0;	
		Vector<HashMap<String, String>> ds = new Vector<HashMap<String, String>>();
		ApexApiCaller aac = new ApexApiCaller();
		String name = "SFA_OpportunityByPhase";
		String sobject = "Opportunity";
		for(int i = 1; i < items.length; i++) {
			Log.v(TAG, items[i]);
			s1 = items[i].indexOf("};");
			String[] lv = items[i].substring(0, s1).split("; ");
			HashMap<String, String> dc = new HashMap<String, String>();
			for(String b : lv) {
				String[] lx = b.split("=");
				//Log.v(TAG, lx[0] + "==" + lx[1]);
				dc.put(lx[0], lx[1]);
			}
			
			String report = dc.get("report").replaceAll("/", "_");
			Log.v(TAG, "report:" + report);
			if(!report.equals(name))continue;
			
			report = aac.readFileAsStream("data/data/com.android/files/reports_" + report + ".report");
			queryString = aac.makeQuery(report);
			rnd.put(name, aac.queryWith(queryString, sobject, name));
			
		}
		return rnd;
	}
	
	// load gchart api
	public void processLoad() {

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Looper.prepare();

					handler.post(new Runnable() {
						public void run() {
							lv.setVisibility(View.VISIBLE);
						}
					});
					
					ChartAPICaller ca = new ChartAPICaller();
					
			        Log.v(TAG, "onChart\n" + json);
			        HashMap<String, HashMap> rnd = cookQuery(json);
					String name = "SFA_OpportunityByPhase";
					HashMap vars = rnd.get(name);
					
					final URL barUrl = new URL(ca.getBarChartURL("フェーズ別今月の商談状況", "Amount", "Sum of Amount"));
					final ImageView ivb = (ImageView) findViewById(R.id.dashboard1);
					final Bitmap b1 = BitmapFactory.decodeStream(barUrl.openStream());

					handler.post(new Runnable() {
						public void run() {
							try {
								ivb.setImageBitmap(b1);
								TextView tv = (TextView) findViewById(R.id.dashboard1_title);
								tv.setText("フェーズ別今月の商談状況");

							} catch (Exception ex) {
								Log.v(TAG, ex.toString());
							}
						}
					});
					
					/**
					final URL lineUrl = new URL(ca.getLineChartURL());
					final ImageView iv1 = (ImageView) findViewById(R.id.dashboard1);
					final Bitmap b1 = BitmapFactory.decodeStream(lineUrl.openStream());

					handler.post(new Runnable() {
						public void run() {
							try {
								iv1.setImageBitmap(b1);
								TextView tv = (TextView) findViewById(R.id.dashboard1_title);
								tv.setText("Sales Dashboard");

							} catch (Exception ex) {
								Log.v(TAG, ex.toString());
							}
						}
					});
					*/
					
					final URL gomUrl = new URL(ca.getOmeterChartURL());
					final ImageView iv2 = (ImageView) findViewById(R.id.dashboard2);
					final Bitmap b2 = BitmapFactory.decodeStream(gomUrl.openStream());
					handler.post(new Runnable() {
						public void run() {
							try {

								iv2.setImageBitmap(b2);
								TextView tv = (TextView) findViewById(R.id.dashboard2_title);
								tv.setText("Archiveness");
								
							} catch (Exception ex) {
								Log.v(TAG, ex.toString());
							}
							
						}
					});

					final URL pieUrl = new URL(ca.getPieChartURL());
					final ImageView iv3 = (ImageView) findViewById(R.id.dashboard3);
					final Bitmap b3 = BitmapFactory.decodeStream(pieUrl.openStream());
					handler.post(new Runnable() {
						public void run() {
							try {

								iv3.setImageBitmap(b3);
								TextView tv = (TextView) findViewById(R.id.dashboard3_title);
								tv.setText("Market Dashboard");
								lv.setText("");
							} catch (Exception ex) {
								Log.v(TAG, ex.toString());
							}
						}
					});

					Looper.loop();
				} catch (IOException ex) {
					Log.v(TAG, ex.toString());
				}
			};
		});
		t.start();
	}

	private void setImageSwither() {
		mSwitcher = (ImageSwitcher) findViewById(R.id.dashboard1);
		mSwitcher.setFactory(this);
		mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));
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

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public View makeView() {
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return i;
	}

}
