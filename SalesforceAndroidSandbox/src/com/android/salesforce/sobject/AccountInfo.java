/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.sobject;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.R;
import com.android.salesforce.util.SObjectDB;
import com.android.salesforce.util.StaticInformation;

public class AccountInfo extends SObject implements SObjectIF {
		private static final String TAG = "AccountInfo";
		public static String WebSite;
		public static String Description;
		public static String Phone;
		public static String Site;
				
		//private static final String pn = "com.android.salesforce.sobject.";
		
		@Override
	    protected void onCreate(Bundle icicle) {
			super.onCreate(icicle);
			Toast.makeText(AccountInfo.this, "Tabnaized Frame is to be implemented", Toast.LENGTH_LONG).show();

	        setContentView(R.layout.detailinfo_array);
	        Bundle bundle = getIntent().getExtras();
			
	        TextView top = (TextView) findViewById(R.id.list_top);
	        top.setText("Account Infomation");
	        top.setTextColor(0xe0ffff00);
			
	        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
	        layout.setSelected(true);
	        
	        String id = bundle.getString("id");
	        Map<String, String> nav = SObjectDB.IdAndNAV.get(id);
	        Set<Entry<String, String>> se = nav.entrySet();

	        for (Map.Entry<String, String> e : se){ 

	        	TextView textView1 = new TextView(this);
	        	//TextView textView1 = (TextView) findViewById(R.id.item_label);
	        	String label = SObjectDB.AccountLayoutNameToLabel.get(e.getKey());
	        	if(label.equals("Deleted"))continue;
	        	if(label.equals("System Modstamp"))continue;
	        	if(label.equals("Last Modified Date"))continue;
	        	if(label.equals("Created By ID"))continue;
	        	if(label.equals("Owner ID"))continue;
	        	if(label.equals("Last Modified By ID"))continue;
	        	if(label.equals("Created Date"))continue;
	        	
	        	textView1.setText(label);
	        	textView1.setTextSize(18);
		        //top.setTextColor(0xE6E6FA00);
	        	//textView1.setTextColor(0x00FF0000);
	        	textView1.setTextColor(0xdcdcdc00);
	            textView1.setFocusable(true);
	            //textView1.setAutoLinkMask(Linkify.ALL);
	            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
	                    LinearLayout.LayoutParams.FILL_PARENT,
	                    LinearLayout.LayoutParams.WRAP_CONTENT
	            );
	            
	            layout.addView(textView1, p);

	            TextView textView2 = new TextView(this);
	            //TextView textView2 = (TextView) findViewById(R.id.item_value);
	            textView2.setText(e.getValue());
	            Linkify.addLinks(textView2, Linkify.ALL);
	            textView2.setAutoLinkMask(Linkify.WEB_URLS);
	            textView2.setTextSize(18);
	            textView2.setFocusable(true);
	            layout.addView(textView2, p);
	        }
			Log.v(TAG, "Detail Infomation Loading End");
	    }
		
		public Field[] getFields() {
			try {
				Class cl = Class.forName(StaticInformation.SOBJECT_PACKAGE_NAME + "AccountInfo");
				return cl.getFields();
		
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
			return null;
		}
}
