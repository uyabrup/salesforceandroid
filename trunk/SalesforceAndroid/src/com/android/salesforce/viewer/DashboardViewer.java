package com.android.salesforce.viewer;

import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.utils.URLEncodedUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.R;
import com.android.salesforce.operation.ApexApiCaller;
import com.android.salesforce.util.SObjectDB;
import com.android.salesforce.util.StaticInformation;

public class DashboardViewer extends Activity {
	private static final String TAG = "DashboardViewer";
	final Context myApp = this;
	private final Handler handler = new Handler();
	static private TextView header = null;
	private String DASHBOARD_URL = "";
	private WebView main_view;
	private WebView sub_view;
	private Button button;
	private String protocol_domain = "";
	private String snipet = "";
	private String selectedName = "";
	private String selectedUrl = "";
	//private ArrayList<DashboardComponent> dl = null;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.dashboard);
		setTitle("Dashboard on Salesforce Android!");
		
		Bundle bundle = getIntent().getExtras();
		//selectedName = bundle.getString("selectedName");
		
		header = (TextView) findViewById(R.id.dashboard1_header);
		header.setText(SObjectDB.dName);
		
		/**
		button = (Button) findViewById(R.id.load_button);
		button.setFocusable(false);
		button.setClickable(false);
		button.setEnabled(false);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//new AlertDialog.Builder(myApp).setTitle("HTML").setMessage(snipet)
				//.setCancelable(false).create().show();				
				headerEnable();
				main_view.loadUrl(DASHBOARD_URL);				
			}
		});
		*/
		
		main_view = (WebView) findViewById(R.id.main_web_view);
		sub_view = (WebView) findViewById(R.id.sub_web_view);
		
		// * JavaScript must be enabled if you want it to work, obviously */
		main_view.getSettings().setJavaScriptEnabled(true);
		sub_view.getSettings().setJavaScriptEnabled(true);
		
		// * Register a new JavaScript interface called HTMLOUT */
		//main_view.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		
		sub_view.loadData(SObjectDB.dData, "text/html", "utf-8");
		sub_view.setVisibility(View.VISIBLE);
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "option menu clicked");
		//super.onCreateOptionsMenu(menu);
		menu.add(1, 2, 0, "2");
		menu.add(1, 3, 1, "33");
		return true;
	}
	
	private void processBar() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Looper.prepare();
				    int progressStatus = 0;
				      
					//final ProgressBar progress = (ProgressBar)findViewById(R.id.progress_bar);
					while(progressStatus < 100) {
						Thread.sleep(sub_view.getDrawingTime());
						progressStatus = sub_view.getProgress();
						Log.v(TAG, "time:" + sub_view.getDrawingTime());
						Log.v(TAG, "progress:" + progressStatus);
					}
					
					handler.post(new Runnable() {
						public void run() {
							header = (TextView) findViewById(R.id.dashboard1_header);
							header.setText("");
						}
					});
					
					Looper.loop();
				} catch (Exception ex) {
					Log.v(TAG, ex.toString());
				}
			};
		});
		t.start();
    }	
	
	public void buttonEnable() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				handler.post(new Runnable() {
					public void run() {
						button.setFocusable(true);
						button.setClickable(true);
						button.setEnabled(true);
					}
				});
				Looper.loop();
			};
		});
		t.start();
	}
	
	public void headerUnable() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				handler.post(new Runnable() {
					public void run() {
						header.setVisibility(View.INVISIBLE);
					}
				});
				Looper.loop();
			};
		});
		t.start();
	}
	
	public void headerEnable() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				handler.post(new Runnable() {
					public void run() {
						header.setVisibility(View.VISIBLE);
					}
				});
				Looper.loop();
			};
		});
		t.start();
	}
	
}
