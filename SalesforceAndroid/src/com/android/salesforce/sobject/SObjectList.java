/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.sobject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.R;

public class SObjectList extends ListActivity {
	private static final String TAG = "SObjectList";
	private List<Map<Integer, Object>> myData;
	
	/** static hard code now. to be changed to set dynamically by hitting DescribeTabs */
	private String[] sObjects = { "EventList", "TaskList", "AccountList",
			"ContactList", "OpportunityList", "CaseList", "DocumentViewerList",
			"DashBoardList", "SFDCTestList", "TabList" };

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.list_array);

		TextView top = (TextView) findViewById(R.id.list_top);
		top.setText(R.string.message_on_main_menu);
		top.setTextColor(R.drawable.enjoy_message);

		myData = new ArrayList<Map<Integer, Object>>();

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
			intent.putExtra("Id", "0014000000IJtzvAAD");
			int p = (Integer) l.getItemAtPosition(position);
			intent.setClass(SObjectList.this, Class
					.forName("com.android.salesforce.sobject." + sObjects[p]));
			startActivity(intent);
		} catch (ClassNotFoundException ex) {
			Log.v(TAG, ex.toString());
		}
	}

	public void onAnimationRepeat() {
		// TODO Auto-generated method stub

	}

	public void onAnimationStart() {
		// TODO Auto-generated method stub

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
			return !sObjects[position].startsWith("-");
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
			int ss = sObjects[position].indexOf("List");

			tv.setText(sObjects[position].substring(0, ss));
			tv.setTextSize(24);

			addItem(myData, sObjects[position], new Intent());

			return tv;
		}
	}


}
