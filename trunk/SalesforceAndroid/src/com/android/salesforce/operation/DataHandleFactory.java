package com.android.salesforce.operation;

import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.salesforce.main.SalesforceAndroid;
import com.android.salesforce.util.StaticInformation;

public class DataHandleFactory {
	private static final String TAG = "DataHandleFactory";
	private LocalDataHandler ldh;
	private WebDataHandler wdh;
	private SalesforceAndroid sa;
	
	public DataHandleFactory(SalesforceAndroid asa, TextView m, Button l, TextView h, CheckBox d,
							WebView w, WebView v) {
		sa = asa;
 		ldh = new LocalDataHandler(asa, m, l, d);
 		wdh = new WebDataHandler(asa, m, w, v); 		
	}
	
	/** initial login process */
	public boolean initialProcess() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				if(!ldh.loginWithApi()) return;

				if(StaticInformation.dUrl.startsWith("https://")) wdh.getDashboard();
				
				ldh.getSObjectData();
				
				Looper.loop();
			};
		}); 
		t.start();
		
		return true;
	}
	/** refresh local data */
	public void dataRefresh() {
		ldh.dataRefresh();
	}
	
	/** read salesforce token data */
	public String readIdAndToken(){
		Log.v(TAG, "reading id and token...");
		return "";
		//return ss.readIdAndToken(sa);
	}
}
