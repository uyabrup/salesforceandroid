/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.frame;

import com.android.R;
import com.android.salesforce.sobject.AccountInfo;
import com.android.salesforce.sobject.AccountList;
import com.android.salesforce.sobject.OpportunityList;
import com.android.salesforce.viewer.ChartViewer;
import com.android.salesforce.viewer.DocumentViewer;
import com.android.salesforce.util.StaticInformation;

import android.app.TabActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.content.Intent;

/**
 * This class is a constrctor of tab frame.
 */
public class TabMenuMaker extends TabActivity {
	private static final String TAG = "TabMenuMaker";
	private static int childCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final TabHost tabHost = getTabHost();		
		
		tabHost.addTab(tabHost.newTabSpec("tab" + childCount++).setIndicator("Home",
				getResources().getDrawable(R.drawable.icon3)).setContent(
				new Intent(this, CalendarComponent.class)));		
		
		/**
		 * This tab sets the intent flag so that it is recreated each time the
		 * tab is clicked.
		 */
		Class cl;

		if(StaticInformation.isList) cl = AccountList.class;
		else cl = AccountInfo.class;

		tabHost.addTab(tabHost.newTabSpec("tab" + childCount++).setIndicator("Account",
				getResources().getDrawable(R.drawable.star_big_on)).setContent(
				new Intent(this, cl)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		
		StaticInformation.isList = true;
		tabHost.addTab(tabHost.newTabSpec("tab" + childCount++).setIndicator("Oppty",
				getResources().getDrawable(R.drawable.icon)).setContent(
				new Intent(this, OpportunityList.class)));

		/**
		 * This tab sets the intent flag so that it is recreated each time the
		 * tab is clicked.
		 */
		tabHost.addTab(tabHost.newTabSpec("tab" + childCount++).setIndicator("Chart",
				getResources().getDrawable(R.drawable.icon2)).setContent(
				new Intent(this, ChartViewer.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		/**
		Log.v(TAG, ";" + t.getChildCount());
		int id = t.getChildAt(1).getId();

		View vv = t.getChildAt(0);
		vv.setNextFocusDownId(id);
		
		for (int i = 1; i < childCount - 1; i++){
			vv = t.getChildAt(i);
			
			id = t.getChildAt(i+1).getId();
			vv.setNextFocusDownId(id);
			
			id = t.getChildAt(i-1).getId();
			vv.setNextFocusUpId(id);
		}
		
		vv = t.getChildAt(childCount - 1);
		id = t.getChildAt(childCount - 2).getId();
		vv.setNextFocusUpId(id);

		final View tw = tabHost.getCurrentTabView();
		final TabWidget ww = tabHost.getTabWidget();
		ww.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Log.v(TAG, "TW id is " + ww.getId());
			}
		});
		
		tw.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//Log.v(TAG, "Tab id are " + t.getId());
				//tw.setNextFocusDownId(nextFocusDownId);
				
				Log.v(TAG, "Tab next adown " + tw.getNextFocusDownId());
				Log.v(TAG, "Tab next up " + tw.getNextFocusUpId());
				Log.v(TAG, "Tab next left " + tw.getNextFocusLeftId());
				Log.v(TAG, "Tab next right " + tw.getNextFocusRightId());
			}
		});
	*/
	//	Log.v(TAG, "Tab # is " + tabHost.getCurrentTab());
	}
}
