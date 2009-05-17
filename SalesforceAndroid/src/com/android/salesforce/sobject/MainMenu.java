/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.sobject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.SubMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.android.salesforce.main.SalesforceAndroid;
import com.android.salesforce.util.SObjectDB;
import com.android.salesforce.util.StaticInformation;
import com.android.salesforce.viewer.ChartViewer;
import com.android.salesforce.viewer.DashboardViewer;
import com.android.salesforce.web.VisualforceViewer;
import com.android.R;

/**
 * This class is in charge of sobject name list of main menu. 
 * @author Dai Odahara
 */
public class MainMenu extends ListActivity  {
	private static final String TAG = "MainMenu";
	private List<Map<Integer, Object>> data;
    //private static final int START_MENU_ID = Menu.S;
	/** static hard code now. to be changed to set dynamically by hitting DescribeTabs */
	private String selectedName = "";
    private String[] sObjects;
	private String json;
    private static final int LOGIN = 0;
    private static final int REFRESH_DATA = 1;
    private static final int RESYNC = 2;
	 
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setTitle("Welcome " + StaticInformation.USER_NAME + " !");
		
		setContentView(R.layout.main_menu);
		
		// for temp json num
        Bundle bundle = getIntent().getExtras();		
		selectedName = bundle.getString("selectedName");
        //json = bundle.getString("json");
		        
		ListView lv = (ListView) findViewById(android.R.id.list);
		ColorDrawable dw = new ColorDrawable(0xFFf0f8ff);

		//lv.setBackgroundColor(0xDAf0f8ff);
		lv.setDivider(dw);
		lv.setDividerHeight(2);

		//TextView top = (TextView) findViewById(R.id.list_top);
		//top.setText(R.string.message_on_main_menu);
		//top.setTextColor(R.drawable.enjoy_message);

		if(StaticInformation.isDandV) sObjects = StaticInformation.DOWNLOAD_SOBJECTS_WITH_DEMO;
		else sObjects = StaticInformation.DOWNLOAD_SOBJECTS;
		
		data = new ArrayList<Map<Integer, Object>>();

		setListAdapter(new SObjectListAdapter(this));
	}
	
	/** Creating Menu */
	/**
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "option menu clicked");
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, R.string.label_salesforce_refresh);
		menu.add(0, 1, 0, R.string.label_salesforce_resync);
		return result;
	}
	*/
	/** Menu on Display */
	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		Log.v(TAG, "id:" + featureId + "-item:" + item.getItemId());
		
		switch(item.getItemId()) {
			case RESYNC:
				refresh();
				//processLogin();
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void refresh() {
		SObjectDB.SOBJECT_DB.clear();
		SObjectDB.SOBJECTS.clear();
		SObjectDB.SOBJECT_USER_DB.clear();
		SObjectDB.WHERE_HOLDER.clear();
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
			else if (StaticInformation.isDemo && name.equals("Dashboard") ) {
				if(0 == StaticInformation.dUrl.length()) return;
				intent.putExtra("selectedName", selectedName);
				cl = DashboardViewer.class;
			}
			else if (StaticInformation.isDemo && name.equals("Visualforce") ) {
				if(0 == StaticInformation.vUrl.length()) return;
				cl = VisualforceViewer.class;
			}
			else cl = SObjectList.class;
			intent.setClass(MainMenu.this, cl);
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

	/**
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.v(TAG, "keycode :" + keyCode);
		Log.v(TAG, "Keyevent :" + event);
		return true;
	}
	*/
	
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
			if(sObjects[position].equals("Dashboard")) {
				tv.setText(sObjects[position]);
				if(StaticInformation.dUrl.equals("") ) tv.setBackgroundColor(0xBB000033);	
			}
			else if(sObjects[position].equals("Visualforce") && StaticInformation.isDandV) {
				tv.setText(sObjects[position]);
				if(StaticInformation.vUrl.equals("")) tv.setBackgroundColor(0xBB000033);
			}
			else tv.setText(SObjectDB.SOBJECT_NAME_LABEL.get(sObjects[position]));
			
			tv.setTextSize(20);
			//tv.setTextColor(0xCC000000);
			//tv.setBackgroundColor(0xEDf0f8ff);
			
			tv.setBackgroundColor(0xDDf0f8ff);			
			tv.setTextColor(0xFF000044);
			addItem(data, sObjects[position], new Intent());

			return tv;
		}
	}
}
