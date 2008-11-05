/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.sobject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.R;
import com.android.salesforce.frame.FieldHolder;
import com.android.salesforce.frame.SectionHolder;
import com.android.salesforce.main.SalesforceAndroid;
import com.android.salesforce.util.SObjectDB;
import com.android.salesforce.util.StaticInformation;

public class SObjectDetailInfo extends Activity {//extends SObject implements SObjectIF {
		private static String TAG;
		private static String SOBJECT_TYPE;
		private static final String RELATED_LIST = "";
		public static String WebSite;
		public static String Description;
		public static String Phone;
		public static String Site;
		private static int MARGIN_SIZE = 6;
		private static int PADDING_SIZE = 2;
		
		private static int SECTION_WIDTH = 0;
		
		private static int HEADER_TEXT_SIZE = 16;
		private static int FIELD_TEXT_SIZE = 14;
		
		//private static HashMap refid = new HashMap();
		//private static String idPrefix;
		private HashMap<String, String> nai = new HashMap<String, String>();
				
		//private static final String pn = "com.android.salesforce.sobject.";
		
		@Override
	    protected void onCreate(Bundle icicle) {
			super.onCreate(icicle);
			//Toast.makeText(AccountInfo.this, "Tabnaized Frame is to be implemented", Toast.LENGTH_LONG).show();

	        setContentView(R.layout.detail_info);
	        Bundle bundle = getIntent().getExtras();
			
	        String id = bundle.getString("Id");
	        String SObject = bundle.getString("SObject");
	        
	        Log.v(TAG,"id : " + id);
	        Log.v(TAG,"SObject : " + SObject);
	        
	        TAG = SObject + "Info";
	        SOBJECT_TYPE = SObject;
	        
	       // TextView top = (TextView) findViewById(R.id.detail_info_on_top);
	       // top.setText(SObject + " Infomation");
	        
			
	        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
	        layout.setSelected(true);
	       // SECTION_WIDTH = ((LinearLayout) findViewById(R.id.detail_info_on_top)).getMeasuredWidth() - MARGIN_SIZE * 2;
	       // Log.v(TAG, "parent width:" + SECTION_WIDTH);
	        	        
	        ImageButton imgb = (ImageButton) findViewById(R.id.to_home_button);
	        imgb.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent intent = new Intent();
					//intent.setClass(SalesforceAndroid.this, TabMenuMaker.class);
					intent.setClass(SObjectDetailInfo.this, MainMenu.class);
					startActivity(intent);
				}
	        });
	        
	        
	        HashMap tempDB = SObjectDB.SOBJECT_DB.get(SOBJECT_TYPE).get(id);
	        HashMap<String, HashMap<String, String>> uDB = SObjectDB.SYSTEM_DB;

	        ArrayList<SectionHolder> shs = SObjectDB.SOBJECTS.get(SOBJECT_TYPE).detail;
	        for(SectionHolder sh : shs) {
	        	
	        	/** layout by each section */
	        	LinearLayout ll = new LinearLayout(this);
	            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
	                    LinearLayout.LayoutParams.FILL_PARENT,
	                    LinearLayout.LayoutParams.WRAP_CONTENT
	            );
	            llp.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE);	        
	        	ll.setLayoutParams(llp);
	        	ll.setPadding(PADDING_SIZE, PADDING_SIZE, PADDING_SIZE, PADDING_SIZE);
	        	ll.setOrientation(LinearLayout.VERTICAL);
	            ll.setFocusable(true);
	            
	        	/** setting section header */
	        	TextView sv = new TextView(this);
	        	sv.setText(sh.name);
	        	sv.setTextSize(HEADER_TEXT_SIZE);
	        	sv.setTextColor(0xFF708091);
	        	
	        	LinearLayout hll = new LinearLayout(this);
	            LinearLayout.LayoutParams hl = new LinearLayout.LayoutParams(
	                    LinearLayout.LayoutParams.FILL_PARENT,
	                    LinearLayout.LayoutParams.WRAP_CONTENT
	            );	            
	            hl.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE);
	            hll.setLayoutParams(hl);

	            hll.addView(sv);
	            ll.addView(hll);
	            
	            
	            ArrayList<FieldHolder> fhs = sh.fields;
	            for(FieldHolder fs : fhs) {
		        	LinearLayout lm = new LinearLayout(this);
		            LinearLayout.LayoutParams hm = new LinearLayout.LayoutParams(
		                    LinearLayout.LayoutParams.FILL_PARENT,
		                    LinearLayout.LayoutParams.WRAP_CONTENT
		            );
		            hm.setMargins(MARGIN_SIZE, 1, MARGIN_SIZE, 0);	
		            lm.setPadding(0, 0, 0, 0);
		            lm.setLayoutParams(hm);

		            if(fs.value.equals("Description"))lm.setOrientation(LinearLayout.VERTICAL);
		            else lm.setOrientation(LinearLayout.HORIZONTAL);
		        	lm.setBackgroundColor(0xFFFFFFFF);
	            	
	            	/** setting label */
		        	TextView label = new TextView(this);
		        	label.setText(fs.label);
		        	label.setTextSize(FIELD_TEXT_SIZE);
		        	label.setTextColor(0xFF688E23);
		        	label.setPadding(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE);
		        	//label.setWidth(SECTION_WIDTH / 2);
		        	
		            lm.addView(label);
		            		
		            /** setting value */
		            TextView data = new TextView(this);
		            String an = fs.value;
		            if(an.equals("ParentId"))continue;
		            
		            String v = (String)(tempDB.get(an));

		            data.setTextColor(0xFF000000);
		            data.setPadding(MARGIN_SIZE, 0, MARGIN_SIZE, 0);

		            Linkify.addLinks(data, Linkify.ALL);
		            
		           // to be modified by using field type "reference"
		            if(an.equals("AccountId") || an.equals("ContactId") || an.equals("WhoId") || an.equals("WhatId")){
		            	if(null == v)break;
		            	String sobject = chechSObjectFromId(v);
		            	data = setRefIdToName(v, data, sobject);
		            	
		            }else {
		            	  data.setText(v);
		            }
		            
		            //data.setWidth(SECTION_WIDTH / 2 );

		            
		            Log.v(TAG, an);
		            

		            /**
		            if(v == "" || v == null) data.setText("-");
		            else if (an.equals("Name")) ((TextView)findViewById(R.id.name_list_top)).setText(v);
		            */
		            // to be modified
		            if(v == "" || v == null) data.setText("-");
		            else if (an.equals("Name") || an.equals("Subject")) ((TextView)findViewById(R.id.name_list_top)).setText(v);
		            else if(an.equals("LastName")) ((TextView)findViewById(R.id.name_list_top)).setText((String)(tempDB.get("Name")));
		            
		            // Account
		            else if(an.equals("Website")) Linkify.addLinks(data, Linkify.WEB_URLS);
		            else if(an.equals("Phone")) Linkify.addLinks(data, Linkify.PHONE_NUMBERS);
		            else if(an.equals("AnnualRevenue")) data.setText(String.valueOf(Double.valueOf(v).intValue()));
		            
		            // Contact
		            else if(an.equals("LastName")) data.setText((String)(tempDB.get("Name")));
		            
		            // Opportunity
		            else if(an.endsWith("Amount")) data.setText(String.valueOf(Double.valueOf(v).intValue()));
		            else if(an.endsWith("TotalOpportunityQuantity")) data.setText(String.valueOf(Double.valueOf(v).intValue()));
		            else if(an.endsWith("ExpectedRevenue")) data.setText(String.valueOf(Double.valueOf(v).intValue()));

		            
		            if(an.equals("CreatedById"))data.setText(uDB.get(v).get("Name"));
		            else if(an.equals("OwnerId"))data.setText(uDB.get(v).get("Name"));
		            else if(an.equals("LastModifiedById"))data.setText(uDB.get(v).get("Name"));
		            
		            data.setTextSize(FIELD_TEXT_SIZE);

		            lm.addView(data);
		            ll.addView(lm);
	        
	            }
	            
	            layout.addView(ll);
	            
	        }
	        
        	/** setting section header */
        	/** layout by each section */
	        
        	LinearLayout rll = new LinearLayout(this);
            LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rlp.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, 0);	        
            rll.setLayoutParams(rlp);
            rll.setPadding(PADDING_SIZE, PADDING_SIZE, PADDING_SIZE, PADDING_SIZE);
            rll.setOrientation(LinearLayout.VERTICAL);
            rll.setFocusable(true);
        	TextView rl = new TextView(this);
        	rl.setText(RELATED_LIST);
        	rl.setTextSize(HEADER_TEXT_SIZE);
        	rl.setTextColor(0xFF708091);
        	
        	LinearLayout hll = new LinearLayout(this);
            LinearLayout.LayoutParams hl = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );	            
            hl.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, 0);
            hll.setLayoutParams(hl);

            hll.addView(rl);
            rll.addView(hll);
            layout.addView(rll);
	        
	        shs = SObjectDB.SOBJECTS.get(SOBJECT_TYPE).related;
	        for(SectionHolder sh : shs) {
	        	
	        	// layout by each section 
	        	LinearLayout ll = new LinearLayout(this);
	            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
	                    LinearLayout.LayoutParams.FILL_PARENT,
	                    LinearLayout.LayoutParams.WRAP_CONTENT
	            );
	            llp.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, PADDING_SIZE);	        
	        	ll.setLayoutParams(llp);
	        	ll.setPadding(PADDING_SIZE, 1, PADDING_SIZE, 0);
	        	ll.setOrientation(LinearLayout.VERTICAL);
	            ll.setFocusable(true);
	        	
	        	// setting section header 
	        	TextView sv = new TextView(this);
	        	sv.setText(sh.name + "   >>");
	        	sv.setTextSize(HEADER_TEXT_SIZE + 1);
	        	sv.setTextColor(0xFF7080CF);
	        	
	        	LinearLayout lm = new LinearLayout(this);
	            LinearLayout.LayoutParams hm = new LinearLayout.LayoutParams(
	                    LinearLayout.LayoutParams.FILL_PARENT,
	                    LinearLayout.LayoutParams.WRAP_CONTENT
	            );
	            hm.setMargins(MARGIN_SIZE, 1, MARGIN_SIZE, 0);	
	            lm.setPadding(0, 0, 0, 0);
	            lm.setLayoutParams(hm);

	        	lm.setBackgroundColor(0xFFFFFFFF);
            	
	        	sv.setPadding(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE);
	        	//label.setWidth(SECTION_WIDTH / 2);
	        	
	            lm.addView(sv);
	            ll.addView(lm);
	            
	            layout.addView(ll);
	        }
	        
	        
		}
	    
		/**
		 * check id and show its name of record from sobject pool 
		 * @param v
		 * @param data
		 * @param idp
		 */
		private TextView setRefIdToName(String id, TextView data, String sobject) {
			//idPrefix = sobject;
        	HashMap<String, String> refid = SObjectDB.SOBJECT_DB.get(sobject).get(id);
        	if(null == refid)return new TextView(this);
        	Log.v(TAG, "v=" + id);
        	Log.v(TAG, "id=" + refid);
        	String name = (String)refid.get("Name");
        	//if(sobject.equals("Event") || sobject.equals("Task") ||sobject.equals("Event") ) name = (String)refid.get("Subject");
        	//else name = (String)refid.get("Name");
        	nai.put(name, id);
        	data.setTextColor(0xEE4169E1);
        	data.setOnClickListener(new View.OnClickListener() {
        		public void onClick(View vv) {
		            TextView tv = (TextView)vv;
		            String id = nai.get(tv.getText());
		            Log.v(TAG, "\t Value:" + tv.getText());
		            Log.v(TAG, "\t Id:" + id);
		            
		            String sobject = chechSObjectFromId(id);
		            
					Intent intent = new Intent();
					intent.putExtra("Id", id);
					intent.putExtra("SObject", sobject);
					
					intent.setClass(SObjectDetailInfo.this,
							SObjectDetailInfo.class);
					//StaticInformation.isList = false;
					startActivity(intent);
        		}
        	});
            data.setFocusable(true);
            data.setText(name);
            
            return data;

		}
		
		/**
		 * 
		 * @param value
		 */
		private String chechSObjectFromId(String value) {
			//Log.v(TAG, "value:" + value);
			String prefix = value.substring(0, StaticInformation.SOBJECT_PREFIX_SIZE);
			String sobject = SObjectDB.KEYPREFIX_SOBJECT.get(prefix);
			Log.v(TAG, "SObject with prefix:" + sobject + "-" + prefix);

			return sobject;
		}

}
