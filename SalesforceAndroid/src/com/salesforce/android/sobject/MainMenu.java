/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.salesforce.android.sobject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.salesforce.R;
import com.salesforce.android.util.SObjectDB;
import com.salesforce.android.util.StaticInformation;
import com.salesforce.android.viewer.ChartViewer;
import com.salesforce.android.web.VisualforceViewer;

/**
 * This class is in charge of sobject name list of main menu. 
 * @author Dai Odahara
 */
public class MainMenu extends ListActivity  {
	private static final String TAG = "SObjectList";
	private List<Map<Integer, Object>> data;
	
	/** static hard code now. to be changed to set dynamically by hitting DescribeTabs */
	 private String[] sObjects;
	 private String json;
	 
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setTitle("Welcome " + StaticInformation.USER_NAME + " !");
		
		setContentView(R.layout.main_menu);
		
		// for temp json num
        //Bundle bundle = getIntent().getExtras();		
        //json = bundle.getString("json");
		
        
		ListView lv = (ListView) findViewById(android.R.id.list);
		ColorDrawable dw = new ColorDrawable(0xFFf0f8ff);

		//lv.setBackgroundColor(0xDAf0f8ff);
		lv.setDivider(dw);
		lv.setDividerHeight(2);

		TextView top = (TextView) findViewById(R.id.list_top);
		top.setText(R.string.message_on_main_menu);
		top.setTextColor(R.drawable.enjoy_message);

		sObjects = StaticInformation.DOWNLOAD_SOBJECTS;
		data = new ArrayList<Map<Integer, Object>>();

		setListAdapter(new SObjectListAdapter(this));
	}
	
	protected void addItem(List<Map<Integer, Object>> data, String name,
			Intent intent) {
		Map<Integer, Object> temp = new HashMap<Integer, Object>();
		temp.put(R.string.sobjectname, name);
		temp.put(R.string.intent, intent);
		data.add(temp);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		try {
			
			Intent intent = new Intent();
	//		TextView tv = (TextView)v;
            String name = ((TextView)v).getText().toString();
            Set<Map.Entry<String, String>> set = SObjectDB.SOBJECT_NAME_LABEL.entrySet();
            Iterator<Map.Entry<String, String>> it = set.iterator();

            while(it.hasNext()){
				Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
				if(name.equals(entry.getValue()))
				name = entry.getKey();            		
            }
			
			intent.putExtra("SObject", name);
			//intent.putExtra("SObject", sObjects[position]);
			Log.v(TAG, "Sobject :" + name);
			//int p = (Integer) l.getItemAtPosition(position);
			
			Class cl;
			if (name.equals("ChartViewer")) {
				cl = ChartViewer.class;
				intent.putExtra("json", json);
			}
			else if (StaticInformation.isDemo && name.equals("VisualforceViewer")) cl = VisualforceViewer.class;
			else cl = SObjectList.class;
			intent.setClass(MainMenu.this, cl);
			//intent.setClass(SObjectList.this, ChartViewer.class);
			startActivity(intent);
		} catch (Exception ex) {
			Log.v(TAG, ex.toString());
		}
	}

	public void onAnimationRepeat() {
		// TODO Auto-generated method stub

	}

	public void onAnimationStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.v(TAG, "keycode :" + keyCode);
		Log.v(TAG, "Keyevent :" + event);
		return true;
	}
	
	private class SObjectListAdapter extends BaseAdapter {
		private Context mContext;

		public SObjectListAdapter(Context context) {

			mContext = context;
		}

		@Override
		public int getCount() {
			return sObjects.length;
		}
		

		public boolean areAllItemsSelectable() {
			return false;
		}

		public boolean isSelectable(int position) {
			return true;//!sObjects[position].startsWith("-");
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv;
			if (convertView == null) {
				tv = new TextView(mContext);
			} else {
				tv = (TextView) convertView;
			}

			//tv.setText(sObjects[position]);
			if(sObjects[position].equals("VisualforceViewer")) tv.setText(sObjects[position]);
			else tv.setText(SObjectDB.SOBJECT_NAME_LABEL.get(sObjects[position]));
			
			tv.setTextSize(16);
			//tv.setTextColor(0xCC000000);
			//tv.setBackgroundColor(0xEDf0f8ff);
			
			tv.setBackgroundColor(0xDDf0f8ff);			
			tv.setTextColor(0xFF000044);
			addItem(data, sObjects[position], new Intent());

			return tv;
		}
	}
}
