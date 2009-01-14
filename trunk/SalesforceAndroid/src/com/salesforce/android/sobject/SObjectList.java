/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.salesforce.android.sobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.saleseforce.android.operation.ApexApiCaller;
import com.salesforce.R;
import com.salesforce.android.frame.FieldHolder;
import com.salesforce.android.frame.SectionHolder;
import com.salesforce.android.util.SObjectDB;
import com.salesforce.android.util.StaticInformation;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SObjectList extends ListActivity {
	private static String TAG;
	private static String sobject;
	private EditText et;
	private String parentId;
	private String parentName;
	private int onlineCount = 0;
	private String SOBJECT_TYPE;
	private final static Handler handler = new Handler();
	private final static ApexApiCaller bind = new ApexApiCaller();
	private String main_name = "Name";
	private String[] records_list;
	private String[] records_search;
	private TextView listv;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.list_with_incremental_search);
		
		listv = (TextView)findViewById(R.id.online_load_message);
		listv.setText("Loading...");
		listv.setVisibility(4);
		
		et = (EditText) findViewById(R.id.auto_complete_view);
        Bundle bundle = getIntent().getExtras();
		sobject = bundle.getString("SObject");
        Log.v(TAG,"SObject : " + sobject + " : " + SObjectDB.SOBJECT_NAME_LABEL.get(sobject));
 
        //if(SObject.equals("Event") || SObject.equals("Task")) title += " - This Week & Next Week";
        
        TAG = SObjectDB.SOBJECT_NAME_LABEL.get(sobject) + " List Information";
        SOBJECT_TYPE = sobject;
        if(SOBJECT_TYPE.equals("Event") 
        		|| SOBJECT_TYPE.equals("Task")
        		|| SOBJECT_TYPE.equals("Case"))main_name = "Subject";
        else if(SOBJECT_TYPE.equals("Lead"))main_name = "Name";
		ListView lv = (ListView) findViewById(android.R.id.list);
		ColorDrawable dw = new ColorDrawable(0x00FFFFFF);
		
		//lv.setBackgroundColor(0xFFECECFF);
		//lv.setFocusableInTouchMode(false);

		//lv.setDivider(dw);
		lv.setDividerHeight(2);
		parentId = bundle.getString("ParentId");
		parentName = bundle.getString("ParentName");
		Log.v(TAG, "pid:" + parentId +  "=parentName:" + parentName);
		init();
	}

	//
	private void init() {
		/** getting from records on list from temp DB or online search*/
		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        Log.v(TAG, "parentName:" + parentName);
		records_list = getRecords(data);
		String title = "";//SObjectDB.SOBJECT_NAME_LABEL.get(sobject) + " List Information - " + records_list.length + " records";

		if(null == data || null == records_list) {
			title = SObjectDB.SOBJECT_NAME_LABEL.get(sobject) + " List Information - 0 records";
		    setTitle(title);
			return;
		} else title = SObjectDB.SOBJECT_NAME_LABEL.get(sobject) + " List Information - " + records_list.length + " records";
	        setTitle(title);
	       
		Log.v(TAG,"data:" + data + "-recores_list:" + records_list[0]);
		if(records_list.length == 0) return;
		Log.v(TAG,"data:" + data + "-recores_list:" + records_list[0]);
		
        if(parentName != null) title += " of " + parentName;
 
        
		ExtendedAdapter one_list_adapter = new ExtendedAdapter(this, data,
				R.layout.one_line_list, new String[] { "value" },
				new int[] { R.id.one_of_oneline });

		setListAdapter(one_list_adapter);
		
		Log.v(TAG, "re_list=" + records_list.length);
		StringBuffer temp = new StringBuffer();
		for (String s : records_list) {
			Log.v(TAG,"aRecord:" + s);
			String val = new String(s);
			int on = 0;
			if(s.endsWith("ONLINE")) on = StaticInformation.ONLINE_SIZE;
			
//				val = s.substring(StaticInformation.ONLINE_SIZE, s.length());
	//		Log.v(TAG, val);
			temp.append( val.substring(StaticInformation.RECORD_ID_LENGTH + 1, val.length()-on) ).append("/");
			//Log.v(TAG, "name1:" + s.substring(StaticInformation.RECORD_ID_LENGTH + 1, s.length()));
		}

		records_search = temp.toString().split("/");
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		//ExtendedArrayAdapter adapter = new ExtendedArrayAdapter(this,
					android.R.layout.simple_dropdown_item_1line, records_search);

		AutoCompleteTextView autoCompTextView = (AutoCompleteTextView) findViewById(R.id.auto_complete_view);
		
		autoCompTextView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				listv.setText("");
			}
		});
		
		autoCompTextView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent();
						TextView tv = (TextView)view;
						Log.v(TAG, "in : " + tv.getText());
						String name = tv.getText().toString();
						
						Set<Entry<String, HashMap>> se = SObjectDB.SOBJECT_DB.get(SOBJECT_TYPE).entrySet();
						
						for (Map.Entry<String, HashMap> e : se) {
							HashMap hm = e.getValue();
							if(!hm.get("SObjectType").equals(SOBJECT_TYPE))continue;
							String value = (String)hm.get(main_name);
							Log.v(TAG, "val : " + value);
							if(name.equals(value)) {
								intent.putExtra("Id", e.getKey());
								break;
							}
						}
						intent.putExtra("SObject", SOBJECT_TYPE);
						intent.setClass(SObjectList.this,
								com.salesforce.android.sobject.SObjectDetail.class);
						
						//Toast.makeText(AccountList.this, "Dynamic Page Jump is to be implemented", Toast.LENGTH_LONG).show();
						startActivity(intent);
					}
				});

		autoCompTextView.setAdapter(adapter);

		ImageView lb = (ImageView) findViewById(R.id.online_search_button);
		lb.setFocusable(true); lb.setFocusableInTouchMode(true);
		lb.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					if(et.getText().toString().length() < 2){
						listv.setText("Type more than 2 words");
						listv.setVisibility(0);
						return;
					}
					doOnlineSearch();
				}
			}
		);
		
		Log.v(TAG, "Finish Loading");
	}

	// do online search as thread
	private void doOnlineSearch() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				
				handler.post(new Runnable() {
					public void run() {
						ImageView lb = (ImageView) findViewById(R.id.online_search_button);
						lb.setFocusable(false);
						listv.setVisibility(0);
					}
				});
				Log.v(TAG, "Logging...");
				if(!bind.login()){
					Log.v(TAG, "Loging Fault");
					return;
				}
				Log.v(TAG, sobject + "--" +  et.getText().toString());
				onlineCount = bind.queryWithName(sobject, et.getText().toString());
				
				handler.post(new Runnable() {
					public void run() {
						if(0 == onlineCount) listv.setText("Online search results are 0");
						else {
							listv.setText("Online search " + onlineCount + " results described blue");
							init();
						}
						//listv.setVisibility(4);
						ImageView lb = (ImageView) findViewById(R.id.online_search_button);
						lb.setFocusable(true);
					}
				});
				Looper.loop();

			};
		});
		t.start();
	}
	
	public class ExtendedAdapter extends SimpleAdapter {
		private Context mContext;

		public ExtendedAdapter(Context context, List data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			mContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv;
			if (convertView == null) {
				tv = new TextView(mContext);
			} else {
				tv = (TextView) convertView;
			}

			if ('|' != records_list[position].charAt(StaticInformation.RECORD_ID_LENGTH)) {
				Log.e(TAG, "Error occurs. CharAt(18) is NOT '|'");
				return null;
			}
			// String temp[] = accs[position].split("|");
			// Log.v(TAG, "name : " + accs[position].substring(19,
			// accs[position].length()));
			//Log.v(TAG, allAccs[position].substring(StaticInformation.RECORD_ID_LENGTH + 1, allAccs[position]
			//		.length()));
			String name = records_list[position].substring(StaticInformation.RECORD_ID_LENGTH + 1, records_list[position].length());
			if(name.endsWith("ONLINE")){
				tv.setText(name.substring(0, name.length()-StaticInformation.ONLINE_SIZE));
				tv.setBackgroundColor(0xDDe0f8ff);
				tv.setTextColor(0xFF0000AA);				
			} else {
				tv.setText(name);
				tv.setBackgroundColor(0xDDf0f8ff);
				tv.setTextColor(0xFF000044);				
			}
			tv.setTextSize(17);
			return tv;
		}

	}
	

	private String[] getRecords(List<HashMap<String, String>> ret) {
		//List<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();
		StringBuffer rec = new StringBuffer();
		
		// No Data
		if(null==SObjectDB.SOBJECT_DB.get(SOBJECT_TYPE))return null;
		Set<Entry<String, HashMap>> se = SObjectDB.SOBJECT_DB.get(SOBJECT_TYPE).entrySet();
		
		boolean hp = true;
		for (Map.Entry<String, HashMap> e : se) {
			if(!e.getValue().get("SObjectType").equals(SOBJECT_TYPE))continue;
			
			HashMap<String, String> temp = new HashMap<String, String>();
			
			String id = e.getKey();
			String value = (String)e.getValue().get(main_name);
			String value2 = value;
			if(value.endsWith("ONLINE"))
				value = value.substring(0, value.length()-StaticInformation.ONLINE_SIZE).toString();
			
			Log.v(TAG, "id:" + id + "/value:" + value);
			temp.put("value", id + "|" + value);
			temp.put(value, id + "|" + value);
			
			if(parentId != null)
				hp = hasParentSObject(id, parentId, SOBJECT_TYPE);
			
			if(hp) {
				ret.add(temp);
				rec.append(id + "|" + value2).append("/");
				Log.v(TAG, "hasParent:id:" + id + "/value:" + value);
			}
		}
		Log.v(TAG, "rec:" + rec.toString());

		if(rec == null || rec.length() == 0)return null;
		records_list = rec.toString().split("/");
		/*
		for(String s : allAccs) {
			Log.v(TAG, "a one:" + s);
		}
		*/

		return records_list; 
	}
	
	private boolean hasParentSObject(String id, String parentId, String sobject) {
		  boolean ret = false;
	      HashMap tempDB = SObjectDB.SOBJECT_DB.get(sobject).get(id);
	      Set<String> set = tempDB.keySet();

	      Iterator<String> iterator = set.iterator();
	      Object object;
	 
	      while(iterator.hasNext()){
               object = iterator.next();
               //Log.v(TAG, object + " = " + tempDB.get(object));
               if(tempDB.get(object).equals(parentId)){
            	   ret = true;
            	   Log.v(TAG, "BINGO!:" + parentId);
               }
	      }

		return ret;
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		try {
			startActivity(l, v, position, id, records_list);
		} catch (Exception ex) {
			Log.v(TAG, ex.toString());
		}
	}

	private void startActivity(ListView l, View v, int position, long id,
			String[] strings) {
		try {
			Intent intent = new Intent();
			TextView tv = (TextView)v;
			String name = tv.getText().toString();
			Log.v(TAG, "click:" + name+":");
			Set<Entry<String, HashMap>> se = SObjectDB.SOBJECT_DB.get(SOBJECT_TYPE).entrySet();
			
			for (Map.Entry<String, HashMap> e : se) {
				HashMap<String, String> hm = e.getValue();
				if(!hm.get("SObjectType").equals(SOBJECT_TYPE))continue;
				String value = hm.get(main_name);
				
				Log.v(TAG, "value:" + value+":");
				int tl = 0;
				if(value.endsWith("ONLINE"))tl = StaticInformation.ONLINE_SIZE;
				
				if(name.equals(value.substring(0, value.length() - tl))){
					intent.putExtra("Id", e.getKey());
					break;
				}
			}
			
			//intent.putExtra("Id", tv.getText().toString().substring(0, StaticInformation.RECORD_ID_LENGTH));
			//Log.v(TAG, "--" + tv.getText().toString().substring(0, StaticInformation.RECORD_ID_LENGTH));
			intent.putExtra("SObject", SOBJECT_TYPE);
			intent.setClass(SObjectList.this,
					com.salesforce.android.sobject.SObjectDetail.class);
			//StaticInformation.isList = false;
			startActivity(intent);
		} catch (Exception ex) {
			Log.v(TAG, ex.toString() + ex.getMessage());
		}
	}

}
