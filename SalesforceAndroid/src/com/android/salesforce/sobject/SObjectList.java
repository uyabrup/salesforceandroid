/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.sobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.android.R;
import com.android.salesforce.util.SObjectDB;
import com.android.salesforce.util.StaticInformation;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SObjectList extends ListActivity {
	private static String TAG;
	private static String SOBJECT_TYPE;
	//private HashMap<String, String> idAndNameMap;
	private String main_name = "Name";
	private String[] records_list;
	private String[] records_search;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.list_with_incremental_search);
        Bundle bundle = getIntent().getExtras();
		String SObject = bundle.getString("SObject");
       Log.v(TAG,"SObject : " + SObject);
        
        TAG = SObject + "List";
        SOBJECT_TYPE = SObject;
        if(SOBJECT_TYPE.equals("Event") 
        		|| SOBJECT_TYPE.equals("Task")
        		|| SOBJECT_TYPE.equals("Case"))main_name = "Subject";
        
		ListView lv = (ListView) findViewById(android.R.id.list);
		ColorDrawable dw = new ColorDrawable(0x00FFFFFF);
		
		//lv.setBackgroundColor(0xFFECECFF);
		//lv.setFocusableInTouchMode(false);

		//lv.setDivider(dw);
		lv.setDividerHeight(2);
		
		/** getting from records on list from temp DB */
		List<HashMap<String, String>> myData = getRecords();

		if(null == myData) return;
		
		ExtendedAdapter one_list_adapter = new ExtendedAdapter(this, myData,
				R.layout.one_line_list, new String[] { "value" },
				new int[] { R.id.one_of_oneline });

		setListAdapter(one_list_adapter);

		Log.v(TAG, "re_list=" + records_list.length);
		StringBuffer temp = new StringBuffer();
		for (String s : records_list) {
			//autoAccs[i] = allAccs[i].substring(StaticInformation.RECORD_ID_LENGTH + 1, allAccs[i].length());
			temp.append( s.substring(StaticInformation.RECORD_ID_LENGTH + 1, s.length()) ).append("/");
			//Log.v(TAG, "name1:" + s.substring(StaticInformation.RECORD_ID_LENGTH + 1, s.length()));
		}

		records_search = temp.toString().split("/");
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		//ExtendedArrayAdapter adapter = new ExtendedArrayAdapter(this,
					android.R.layout.simple_dropdown_item_1line, records_search);

		AutoCompleteTextView autoCompTextView = (AutoCompleteTextView) findViewById(R.id.auto_complete_view);
		
		
		autoCompTextView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent();
						TextView tv = (TextView)view;
						//Log.v(TAG, "in : " + tv.getText());
						String name = tv.getText().toString();
						
						Set<Entry<String, HashMap>> se = SObjectDB.SOBJECT_DB.get(SOBJECT_TYPE).entrySet();
						
						for (Map.Entry<String, HashMap> e : se) {
							HashMap hm = e.getValue();
							if(!hm.get("SObjectType").equals(SOBJECT_TYPE))continue;
							String value = (String)hm.get(main_name);
							if(name.equals(value)) {
								intent.putExtra("Id", e.getKey());
								break;
							}
						}
						intent.putExtra("SObject", SOBJECT_TYPE);
						intent.setClass(SObjectList.this,
								com.android.salesforce.sobject.SObjectDetailInfo.class);
						//StaticInformation.isList = false;
						
						//Toast.makeText(AccountList.this, "Dynamic Page Jump is to be implemented", Toast.LENGTH_LONG).show();
						startActivity(intent);
						
					}
				});

		autoCompTextView.setAdapter(adapter);

		Log.v(TAG, "Finish Loading");
	}

	/**
	public class ExtendedArrayAdapter<String> extends ArrayAdapter<String> {
		private Context mContext;

		public ExtendedArrayAdapter(Context context, int textViewResourceId,
				String[] strings) {
			super(context, textViewResourceId, strings);
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

			
			//if ('|' != autoAccs[position].charAt(18)) {
		//		Log.e(TAG, "Error occurs. CharAt(18) is NOT '|'");
			//	return null;
			//}
			// String temp[] = accs[position].split("|");
			//Log.v(TAG, "name2 : "
			//		+ autoAccs[position].substring(StaticInformation.RECORD_ID_LENGTH + 1, autoAccs[position]
			//				.length()));
			//tv.setText(autoAccs[position].substring(StaticInformation.RECORD_ID_LENGTH + 1, autoAccs[position]
			//		.length()));
					
			tv.setTextColor(0xFFFFFFFF);
			tv.setText(autoAccs[position]);
			//tv.setBackgroundColor(0xFFB0C4DE);
			tv.setFocusable(false);
			tv.setTextSize(14);
			return tv;
		}

	}
	*/
	
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
			String name = records_list[position].substring(StaticInformation.RECORD_ID_LENGTH + 1, records_list[position]
			                                                                                					.length());
			tv.setText(name);
			tv.setTextSize(17);
			tv.setBackgroundColor(0xDDf0f8ff);
			
			//tv.setPadding(3,3,3,3);
			tv.setTextColor(0xFF000044);
			return tv;
		}

	}
	

	private List<HashMap<String, String>> getRecords() {
		List<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();
		StringBuffer acc = new StringBuffer();
		
		// No Data
		if(null==SObjectDB.SOBJECT_DB.get(SOBJECT_TYPE))return null;
		Set<Entry<String, HashMap>> se = SObjectDB.SOBJECT_DB.get(SOBJECT_TYPE).entrySet();
				
		for (Map.Entry<String, HashMap> e : se) {
			if(!e.getValue().get("SObjectType").equals(SOBJECT_TYPE))continue;
			
			HashMap<String, String> temp = new HashMap<String, String>();
			
			String id = e.getKey();
			String value = (String)e.getValue().get(main_name);
			
			temp.put("value", id + "|" + value);
			temp.put(value, id + "|" + value);
			
			ret.add(temp);
			acc.append(id + "|" + value).append("/");
			
		}
		records_list = acc.toString().split("/");
		/*
		for(String s : allAccs) {
			Log.v(TAG, "a one:" + s);
		}
		*/

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
			
			Set<Entry<String, HashMap>> se = SObjectDB.SOBJECT_DB.get(SOBJECT_TYPE).entrySet();
			
			for (Map.Entry<String, HashMap> e : se) {
				HashMap hm = e.getValue();
				if(!hm.get("SObjectType").equals(SOBJECT_TYPE))continue;
				String value = (String)hm.get(main_name);
				if(name.equals(value)) {
					intent.putExtra("Id", e.getKey());
					break;
				}
			}
			
			//intent.putExtra("Id", tv.getText().toString().substring(0, StaticInformation.RECORD_ID_LENGTH));
			//Log.v(TAG, "--" + tv.getText().toString().substring(0, StaticInformation.RECORD_ID_LENGTH));
			intent.putExtra("SObject", SOBJECT_TYPE);
			intent.setClass(SObjectList.this,
					com.android.salesforce.sobject.SObjectDetailInfo.class);
			//StaticInformation.isList = false;
			startActivity(intent);
		} catch (Exception ex) {
			Log.v(TAG, ex.toString());
		}
	}

}
