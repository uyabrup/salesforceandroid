/***
 * Excerpted from "Hello, Android!",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband for more book information.
***/

package com.android.web;

import java.io.IOException;
import java.net.URL;

import com.android.R;
import com.android.google.operation.ChartAPICaller;
import com.android.salesforce.util.StaticInformation;
import com.android.salesforce.viewer.ChartViewer;

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
public class BrowserViewer extends Activity {
   private static final String TAG = "BrowserView";
   private static final String APEX = "/apex/";
   private static final String AFROUS = "/servlet/servlet.Integration?lid=01r8000000070qk&ic=1";
   
	private final Handler handler = new Handler();
   private EditText urlText;
   private Button goButton;
   private static String startURL;
   
   private WebView webView;
   private WebChromeClient chromeView;
   private static String loadUrl;
 //  private TextView lv;
   
   private static int progressStatus = 0;
   private static TextView adv;
   private static TextView rcv;
   
   private static final String accUrl = "AccountInfo?id=0014000000IJtzv";
   private static final String chartUrl = "ChartViewer";
   
   private static final String urlPrefix = "https://na2.salesforce.com/secur/frontdoor.jsp";
   private static final String afrousPrefix = "https://na6.salesforce.com/secur/frontdoor.jsp";
   
   
   @Override
   public void onCreate(Bundle icicle) {
      super.onCreate(icicle);
      setContentView(R.layout.web_browser);
      
      adv = (TextView) findViewById(R.id.account_detail_visualforce);
      Linkify.addLinks(adv, Linkify.WEB_URLS);
      
      rcv = (TextView) findViewById(R.id.report_chart_visualforce);
      Linkify.addLinks(rcv, Linkify.WEB_URLS);
      
      
      adv.setOnClickListener(new View.OnClickListener() {
    	 public void onClick(View v) {
    		 Log.v(TAG, "open Visualforce Account Detail");

    		 loadUrl = APEX + accUrl;
    		 Toast.makeText(BrowserViewer.this, "Loading Account Info Visualforce...", Toast.LENGTH_LONG).show();
    		 processBar();
    		 processLoad();
    		 
    	 }
      });
      
      
      rcv.setOnClickListener(new View.OnClickListener() {
     	 public void onClick(View v) {
    		 loadUrl = APEX + chartUrl;

    		 Toast.makeText(BrowserViewer.this, "Loading Google Visualization API...", 6).show();
    		 processBar();
    		 processLoad();
    		 
     	 }
       });
       
      
      webView = (WebView) findViewById(R.id.web_view);
      webView.getSettings().setJavaScriptEnabled(true);

      chromeView = new WebChromeClient();
      //webView.setWebChromeClient(chromeView);
      
      
      /**
      goButton.setOnClickListener(new OnClickListener() {
         public void onClick(View view) {
            openBrowser();
         }
      });
      urlText.setOnKeyListener(new OnKeyListener() {
         public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
               openBrowser();
               return true;
            }
            return false;
         }
      });
      */
     // String startURL = afrousPrefix + "?un=" + StaticInformation.USER_ID + "&sid=" + StaticInformation.SESSION_ID + "&retURL=" + AFROUS;
		Log.v(TAG, "StartURL:" + startURL);
		initOpenBrowser(startURL);
      
   }
   
   /** initial Open Browser */
   private void initOpenBrowser(String url) {
	   //String startURL = "https://na3.salesforce.com/secur/frontdoor.jsp?un=dai.odahara%40google.com&sid=472200D500000007P5i!AQQAQNmx9GKk1OwRzgIGLOcZeztqyc_dpEGrTE_AKJIUoLeKcHhhwF34twk0n2QY_zStWeACNC76VCAznWbPr53ZkdaZj6qn&retURL=/apex/AccountInfo?id=0015000000HJLgLAAX";

	   String startURL = urlPrefix + "?un=" + StaticInformation.USER_ID + "&sid=" + StaticInformation.SESSION_ID + "&retURL=" + APEX + chartUrl;
	   startURL = "http://www.google.com/";
	   webView.loadUrl(startURL);
	      
	   webView.requestFocus();
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
						progressStatus += 1;
					}
					
					handler.post(new Runnable() {
						public void run() {
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
					startURL = urlPrefix + "?un=" + StaticInformation.USER_ID + "&sid=" + StaticInformation.SESSION_ID + "&retURL=" + loadUrl;
					Log.v(TAG, "URL Access:" + startURL);
					   //startURL = "/home/home.jsp";

				    handler.post(new Runnable() {
						public void run() {
							webView.loadUrl(startURL);
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
   
}
