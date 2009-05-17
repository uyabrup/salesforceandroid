/***
 * Excerpted from "Hello, Android!",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband for more book information.
***/

package com.android.salesforce.web;

import java.io.IOException;
import java.net.URL;

//import com.android.google.operation.ChartAPICaller;
import com.android.salesforce.operation.ApexApiCaller;
import com.android.salesforce.util.StaticInformation;
import com.android.salesforce.viewer.ChartViewer;
import com.android.R;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class has web browser function to display web page and visualforce.
 * @author Dai Odahara
 */
public class VisualforceViewer extends Activity {
   private static final String TAG = "BrowserView";
   private static final String APEX = "/apex/";
   private static final String AFROUS = "/servlet/servlet.Integration?lid=01r8000000070qk&ic=1";
   
	private final Handler handler = new Handler();
   private EditText urlText;
   private Button goButton;
   private static String VisualforceUrl;
   
   private WebView webView;
   
   private static int progressStatus = 0;
   private static TextView wmg;   
   private static TextView header;
   
   private static final String accUrl = "AccountInfo?id=0014000000IJtzv";
   private static final String chartUrl = "ChartViewer";
   
   //private static String urlPrefix = "https://na2.salesforce.com/secur/frontdoor.jsp";
   //private static final String afrousPrefix = "https://na6.salesforce.com/secur/frontdoor.jsp";
      
   @Override
   public void onCreate(Bundle icicle) {
      super.onCreate(icicle);
      setContentView(R.layout.web_browser);
      setTitle("Visualforce Viewer");
      
      if(!login())return;
		
      webView = (WebView) findViewById(R.id.web_view);
      webView.getSettings().setJavaScriptEnabled(true);

      //String url = "https://android.na3.visual.force.com/apex/VisualYouTube";
      //url = "http://www.yahoo.co.jp/";
     // String startURL = afrousPrefix + "?un=" + StaticInformation.USER_ID + "&sid=" + StaticInformation.SESSION_ID + "&retURL=" + AFROUS;
		VisualforceUrl = "https://" + StaticInformation.DOMAIN + ".salesforce.com/secur/frontdoor.jsp";		
      //VisualforceUrl = "https://www.salesforce.com/secur/frontdoor.jsp";		
		VisualforceUrl += "?un=" + StaticInformation.USER_ID + "&sid=" + StaticInformation.SESSION_ID;
		//VisualforceUrl += "&retURL=https://" + StaticInformation.DOMAIN + ".salesforce.com/apex/VisualYouTube";
		VisualforceUrl += "&retURL=" + StaticInformation.vUrl;
			
      	Log.v(TAG, "StartURL:" + VisualforceUrl);
      //	VisualforceUrl = "http://www.google.com/";
 	   webView.setEnabled(true);
 	   webView.setKeepScreenOn(true);
	   webView.requestFocus();
	   webView.loadUrl(VisualforceUrl);
	   processBar();
 	   
   }
   
	private boolean login() {
		ApexApiCaller bind = new ApexApiCaller();
		return bind.login(StaticInformation.USER_ID, StaticInformation.USER_PW);
	}
	
   private void processBar() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Looper.prepare();
				    progressStatus = 0;
				      
					//final ProgressBar progress = (ProgressBar)findViewById(R.id.progress_bar);
					while(progressStatus < 100) {
						Thread.sleep(100);
						progressStatus = webView.getProgress();
					}
					
					handler.post(new Runnable() {
						public void run() {
							//wmg.setText(R.string.web_click);
							header = (TextView) findViewById(R.id.dashboard1_header_title);
							header.setText("");
							//progress.setProgress(progressStatus);
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

   /*
	public void processLoad() {

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Looper.prepare();

					handler.post(new Runnable() {
						public void run() {
							//lv.setVisibility(View.VISIBLE);
						}
					});
					//openBrowser(loadUrl);
					VisualforceUrl = urlPrefix + "?un=" + StaticInformation.USER_ID + "&sid=" + StaticInformation.SESSION_ID + "&retURL=" + loadUrl;
					Log.v(TAG, "URL Access:" + VisualforceUrl);
				
				    handler.post(new Runnable() {
						public void run() {
							webView.loadUrl(VisualforceUrl);
							webView.requestFocus();
							//lv.setText("");						      
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
   */
}
