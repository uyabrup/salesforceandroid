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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AccountList extends ListActivity {
	private static final String TAG = "AccountList";
	private static final String SOBJECT_TYPE = "Account";
	//private HashMap<String, String> idAndNameMap;
	private String[] allAccs;
	private String[] autoAccs;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.autocomplete2);

		ListView lv = (ListView) findViewById(android.R.id.list);
		ColorDrawable dw = new ColorDrawable(0xFFFFFFFE);
		
		lv.setBackgroundColor(0xFFECECFF);
		lv.setFocusableInTouchMode(false);

		lv.setDivider(dw);
		lv.setDividerHeight(5);
		
		/** getting from acc list from temp DB */
		List<HashMap<String, String>> myData = getData2();

		ExtendedAdapter one_list_adapter = new ExtendedAdapter(this, myData,
				R.layout.one_line_list, new String[] { "value" },
				new int[] { R.id.one_of_oneline });

		setListAdapter(one_list_adapter);

		StringBuffer temp = new StringBuffer();
		for (String s : allAccs) {
			//autoAccs[i] = allAccs[i].substring(StaticInformation.RECORD_ID_LENGTH + 1, allAccs[i].length());
			temp.append( s.substring(StaticInformation.RECORD_ID_LENGTH + 1, s.length()) ).append("/");
			//Log.v(TAG, "name1:" + s.substring(StaticInformation.RECORD_ID_LENGTH + 1, s.length()));
		}

		autoAccs = temp.toString().split("/");
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		//ExtendedArrayAdapter adapter = new ExtendedArrayAdapter(this,
					android.R.layout.simple_dropdown_item_1line, autoAccs);

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
							String value = (String)hm.get("Name");
							if(name.equals(value)) {
								intent.putExtra("Id", e.getKey());
								break;
							}
						}

						intent.setClass(AccountList.this,
								com.android.salesforce.sobject.AccountInfo.class);
						//StaticInformation.isList = false;
						
						//Toast.makeText(AccountList.this, "Dynamic Page Jump is to be implemented", Toast.LENGTH_LONG).show();
						startActivity(intent);
						
					}
				});

		autoCompTextView.setAdapter(adapter);

		Log.v(TAG, "Finish Loading");
	}

	
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

			/**
			if ('|' != autoAccs[position].charAt(18)) {
				Log.e(TAG, "Error occurs. CharAt(18) is NOT '|'");
				return null;
			}
			// String temp[] = accs[position].split("|");
			Log.v(TAG, "name2 : "
					+ autoAccs[position].substring(StaticInformation.RECORD_ID_LENGTH + 1, autoAccs[position]
							.length()));
			tv.setText(autoAccs[position].substring(StaticInformation.RECORD_ID_LENGTH + 1, autoAccs[position]
					.length()));
					*/
			tv.setTextColor(0xFFFFFFFF);
			tv.setText(autoAccs[position]);
			tv.setBackgroundColor(0xFFB0C4DE);
			tv.setFocusable(false);
			tv.setTextSize(16);
			return tv;
		}

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

			if ('|' != allAccs[position].charAt(StaticInformation.RECORD_ID_LENGTH)) {
				Log.e(TAG, "Error occurs. CharAt(18) is NOT '|'");
				return null;
			}
			// String temp[] = accs[position].split("|");
			// Log.v(TAG, "name : " + accs[position].substring(19,
			// accs[position].length()));
			//Log.v(TAG, allAccs[position].substring(StaticInformation.RECORD_ID_LENGTH + 1, allAccs[position]
			//		.length()));
			tv.setText(allAccs[position].substring(StaticInformation.RECORD_ID_LENGTH + 1, allAccs[position]
					.length()));
			tv.setTextSize(16);
			tv.setBackgroundColor(0xFFEAEAFF);
			tv.setTextColor(0xFF000000);
			return tv;
		}

	}

	private List<HashMap<String, String>> getData2() {
		List<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();
		StringBuffer acc = new StringBuffer();
		Set<Entry<String, HashMap>> se = SObjectDB.SOBJECT_DB.get(SOBJECT_TYPE).entrySet();
		
		for (Map.Entry<String, HashMap> e : se) {
			if(!e.getValue().get("SObjectType").equals(SOBJECT_TYPE))continue;
			
			HashMap<String, String> temp = new HashMap<String, String>();
			//String temp = (String)(e.getValue().get("Name"));
			String id = e.getKey();
			String value = (String)e.getValue().get("Name");
			
			temp.put("value", id + "|" + value);
			temp.put(value, id + "|" + value);
			
			ret.add(temp);
			acc.append(id + "|" + value).append("/");
			
		}
		allAccs = acc.toString().split("/");
		/*
		for(String s : allAccs) {
			Log.v(TAG, "a one:" + s);
		}
		*/

		return ret; 
	
	}
	
	/**
	private List<HashMap<String, String>> getData() {
		List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();
		Set<Entry<String, String>> se = SObjectDB.AccountIdAndNameMap
				.entrySet();
		StringBuffer acc = new StringBuffer();

		for (Map.Entry<String, String> e : se) {
			// Log.v(TAG, e.getKey() + "|" + e.getValue());
			addItem(myData, e.getKey(), e.getValue());
			acc.append(e.getKey() + "|" + e.getValue()).append("/");
		}

		allAccs = acc.toString().split("/");
		return myData;
	}
	

	protected void addItem(List<HashMap<String, String>> data, String id,
			String value) {
		idAndNameMap.put("value", id + "|" + value);
		idAndNameMap.put(value, id + "|" + value);
		Log.v(TAG, "value : " + value);
		data.add(idAndNameMap);
	}
	*/

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		try {
			startActivity(l, v, position, id, allAccs);
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
				String value = (String)hm.get("Name");
				if(name.equals(value)) {
					intent.putExtra("Id", e.getKey());
					break;
				}
			}
			
			//intent.putExtra("Id", tv.getText().toString().substring(0, StaticInformation.RECORD_ID_LENGTH));
			//Log.v(TAG, "--" + tv.getText().toString().substring(0, StaticInformation.RECORD_ID_LENGTH));
			intent.setClass(AccountList.this,
					com.android.salesforce.sobject.AccountInfo.class);
			//StaticInformation.isList = false;
			startActivity(intent);
		} catch (Exception ex) {
			Log.v(TAG, ex.toString());
		}
	}

	/**
	private void setListArray() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
		layout.setSelected(true);

		Bundle bundle = getIntent().getExtras();
		String id = bundle.getString("Id");
		Map<String, String> nav = SObjectDB.IdAndNAV.get(id);
		Set<Entry<String, String>> se = nav.entrySet();
		// for (int i = 0; i < size; i++) {
		for (Map.Entry<String, String> e : se) {

			TextView textView1 = new TextView(this);

			String label = SObjectDB.AccountLayoutNameToLabel.get(e.getKey());
			if (label.equals("Deleted"))
				continue;
			if (label.equals("System Modstamp"))
				continue;
			if (label.equals("Last Modified Date"))
				continue;
			if (label.equals("Created By ID"))
				continue;
			if (label.equals("Owner ID"))
				continue;
			if (label.equals("Last Modified By ID"))
				continue;
			if (label.equals("Created Date"))
				continue;

			Log.v(TAG, "Loading Label : " + label);

			textView1.setText(label);
			textView1.setTextSize(18);
			// top.setTextColor(0xE6E6FA00);
			// textView1.setTextColor(0x00FF0000);
			textView1.setTextColor(0xdcdcdc00);
			textView1.setFocusable(true);
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			layout.addView(textView1, p);

			TextView textView2 = new TextView(this);
			// TextView textView2 = (TextView) findViewById(R.id.item_value);
			textView2.setText(e.getValue());
			textView2.setTextSize(18);
			textView2.setFocusable(true);
			layout.addView(textView2, p);
		}
	}
	*/

}
