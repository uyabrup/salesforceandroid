package com.android.salesforce.frame;

import java.util.List;

import com.android.salesforce.database.SObjectSQLite;
import com.android.salesforce.util.Schedule;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SQLiteComponent extends Activity {
	//@Override
	/**
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		TextView tv = new TextView(this);
		SObjectSQLite sl = new SObjectSQLite(this, "", "Account");
		sl.create("aaaa", "salesforce", 1);
		sl.create("bbbb", "Google", 2);
		sl.create("cccc", "Microsoft", 3);
		
		List<Schedule> list = sl.selectAll();
		sl.close();
		StringBuffer buf= new StringBuffer();
		buf.append(" Id | Name ");
		buf.append(System.getProperty("line.separator"));
		for(Schedule s : list) {
			buf.append(s.Id);
			buf.append("|");
			buf.append(s.Name);
			buf.append(System.getProperty("line.separator"));
		}
		tv.setText(buf.toString());
		setContentView(tv);
	}
	*/
}
