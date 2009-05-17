package com.android.salesforce.operation;

import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.salesforce.main.SalesforceAndroid;
import com.android.salesforce.util.SObjectDB;
import com.android.salesforce.util.StaticInformation;

public class WebDataHandler {
	private static final String TAG = "WebDataHandler";
	private final Handler handler = new Handler();
	private SalesforceAndroid sa;
	private WebView main_view;
	private WebView sub_view;
	private static TextView mSwitcher;
	private String protocol_domain = "";
	private String selectedName = "";
	private ArrayList<DashboardComponent> dl = null;
   
	public WebDataHandler(SalesforceAndroid asa, TextView m, WebView w, WebView v) {
		sa = asa;
		mSwitcher = m;
		main_view = w;
		sub_view = v;
		initWebView();
	}	
	
	public void getDashboard() {
		handler.post(new Runnable() {
			public void run() {
					mSwitcher.setText("Loading Dashboard...");
			}
		});
		
		String dUrl = createDashBoardUrl();
		Log.v(TAG, "Loading Dashboard..." + dUrl);
		main_view.loadUrl(dUrl);
	}
	
	// init webview
	private void initWebView() {
		// JavaScript must be enabled if you want it to work, obviously
		main_view.getSettings().setJavaScriptEnabled(true);
		sub_view.getSettings().setJavaScriptEnabled(true);
		
		// Register a new JavaScript interface called HTMLOUT
		main_view.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		
		// WebViewClient must be set BEFORE calling loadUrl!
		main_view.setWebViewClient(new WebViewClient() {
			public void onPageFinished(WebView view, String url) {
				try {
					URL u = new URL(main_view.getUrl());					
					protocol_domain = u.getProtocol() + "://" + u.getHost();
					main_view.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
					Log.v(TAG, "url:" + main_view.getUrl());				
				} catch (Exception ex) {
					Log.v(TAG, ex.toString());
				}
			}
		});
		
		Log.v(TAG, "id/pw=" + StaticInformation.USER_ID + "-" + StaticInformation.USER_PW);
		
		//main_view.loadUrl(DASHBOARD_URL);
		main_view.setVisibility(View.INVISIBLE);
		sub_view.setVisibility(View.VISIBLE);
	}
	
	private String createDashBoardUrl() {		
		String dURL = "https://" + StaticInformation.DOMAIN + ".salesforce.com/secur/frontdoor.jsp";		
		dURL += "?un=" + StaticInformation.USER_ID + "&sid=" + StaticInformation.SESSION_ID;
		//DASHBOARD_URL += "&retURL=https://" + StaticInformation.DOMAIN + ".salesforce.com/" + did;
		dURL += "&retURL=" + StaticInformation.dUrl;
		return dURL;
	}
	
	private void showSelectedDashboard() {
		//String temp = "", data = "<html><body>View Dashboard " + selectOption + "<br><br><center>";
		String temp = "", data = "<html><body><center><br>";
		
		for(DashboardComponent d : dl) {
			//Log.v(TAG, "url:" + d.url.length() + "\ttable:" + d.table.length());
			if(d.url.length() != 0) temp += d.title + "<br><img src=\"" + protocol_domain + d.url + "\"/><br><br>";
			else if(d.table.length() != 0) temp += d.title + "<br>" + d.table + "<br><br>";

			//temp += d.title + "<br><img src=\"" + protocol_domain + d.url + "\"/><br><br>";
		}
		data = data + temp + "</center></body></html>";
		sub_view.loadData(data, "text/html", "utf-8");
		SObjectDB.dData = data;
	}
	
	/** extract dashboard select */
	private void extractSelectOption(String html) {
		/**
		new AlertDialog.Builder(myApp).setTitle("HTML").setMessage(html)
		.setPositiveButton(android.R.string.ok, null)
		.setCancelable(false).create().show();
		*/
		
		String sselect = "\"dashboardNote\">", eselect = "</p>", 
				selected = "\" selected=\"selected\"";
		int s =0, e=0;
				
		s = html.indexOf(sselect); e = html.indexOf(eselect, s+1);
		Log.v(TAG, "s=" + s + "--\te=" + e);
		
		if(-1 == s || -1 == e)return;
		SObjectDB.dName = html.substring(s + sselect.length(), e);//html.substring(s, e + eselect.length()).replaceAll("value=\"", "value=\"" + protocol_domain + "/");
		Log.v(TAG, "select:" + SObjectDB.dName);
		
	}
	
	
	private void showDashboradSelection() {		
		String data = "<html><body>View Dashboard " + selectedName + "<br></body></html>";
		//sub_view.setVisibility(View.VISIBLE);
		//sub_view.loadData(data, "text/html", "utf-8");
		//header_title.setText("");// Visibility(View.INVISIBLE);
	}
	

	private void setTableComponent(DashboardComponent dc, String comp, int start) {
		String etable = "</table>", table, title = "title=\"";
		int s = start, e = 0;
		
		e = comp.indexOf(etable, s+1);
		table = comp.substring(s, e + etable.length());

		Pattern p1 = Pattern.compile(" class=.*\"");
		
		String[] temp = table.split(">");
		StringBuffer sb = new StringBuffer();
		Matcher matcher;
		for(String str : temp) {
			if(str.startsWith("<a"))continue;
			if(str.endsWith("</a")) str = str.substring(0, str.length() - 3);
			matcher = p1.matcher(str);
			sb.append(matcher.replaceAll("")).append(">");
		}
	
		table = sb.toString().replaceAll("<table>", "<table border=\"1\">").replaceAll("<td>","<td align=\"right\">");
		
		Log.v(TAG, "table:" + table);
		//Log.v(TAG, "s=" + s + "--\te=" + e);
		dc.table = table;
			
	}
	
	private int num = 0;
	private void setImageComponent(DashboardComponent dc, String comp, int start) {
		String etag = ">", line, src = "src=\"", title = "title=\"", ssrc = "src=\"";;
		int s = start, e = 0;
		
		e = comp.indexOf(etag, etag.length() + s+1);
		line = comp.substring(s, e);
		Log.v(TAG, "s=" + s + "--\te=" + e);
		Log.v(TAG, "s=" + line);
		
		s = 0; e = 0;
		s = line.indexOf(src, s);
		e = line.indexOf("\"", src.length()+s+1);
		dc.url = line.substring(s + src.length(),e);
				
		s = line.indexOf(title, s+1);
		e = line.indexOf(" - ", title.length()+s+1);
		if(-1 == s || -1 == e) return;
		
		dc.title = line.substring(s + title.length(),e);
		
	}
	
	/* An instance of this class will be registered as a JavaScript interface */
	class MyJavaScriptInterface {
		public void showDashboard(String body) {
			//Log.v(TAG, "body:" + body);
			new AlertDialog.Builder(sa).setTitle("body").setMessage(body)
			.setCancelable(false).create().show();
		}
		
		//@SuppressWarnings("unused")
		public void showHTML(String html) {
			String img = "<img ", table = "<table";			
			int s =0, e=0;
			
			/** extract dashboard select */
			extractSelectOption(html);
				
			/** extract dashboard components */
			String[] dss = html.split("<div id=\"dashboard_");
			dl = new ArrayList<DashboardComponent>();

			for(int i = 1; i < dss.length; i++) {
				DashboardComponent dc = new DashboardComponent();
						
				s = dss[i].indexOf(img, s);
				if(-1 != s) {
					setImageComponent(dc, dss[i], s);
					dl.add(dc);
					continue;
				}
				
				s = dss[i].indexOf(table, s);
				if(-1 != s) {
					setTableComponent(dc, dss[i], s);
					dl.add(dc);
					continue;
				}
				
				s = 0;
				Log.v(TAG, "title:" + dc.title + "--url:" + dc.url);
				
			}

			//showDashboradSelection();

			showSelectedDashboard();

		}
	}
	
	class DashboardComponent{
		public String title = "";
		public String url = "";
		public String table = "";
	}
}
