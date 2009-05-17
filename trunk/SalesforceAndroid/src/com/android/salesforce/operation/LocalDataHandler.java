package com.android.salesforce.operation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.salesforce.database.SObjectDataFactory;
import com.android.salesforce.main.SalesforceAndroid;
import com.android.salesforce.util.SObjectDB;
import com.android.salesforce.util.StaticInformation;

public class LocalDataHandler {
	private static final String TAG = "LocalDataHandler";
	private SalesforceAndroid sa;
	private static SObjectDataFactory ss = new SObjectDataFactory();
	private static boolean login = false;
	private static StringBuffer table = new StringBuffer();;
	private static TextView mSwitcher;
	private static Button loginButton;
	private static CheckBox demoId;
	private static String SObject;
	private final Handler handler = new Handler();
	private static ApexApiCaller bind = new ApexApiCaller();
	
	public LocalDataHandler(SalesforceAndroid asa, TextView m, Button l, CheckBox d) {
		sa = asa;
		
 		mSwitcher = m;
 		loginButton = l;
 		demoId = d;
	}
	
	public boolean login() {
		//ApexApiCaller bind = new ApexApiCaller();		
		return bind.login(StaticInformation.USER_ID, StaticInformation.USER_PW);
	}
	
	// login caller
	public boolean loginWithApi(){
		boolean b = bind.login(StaticInformation.USER_ID, StaticInformation.USER_PW);
		if(b) b = bind.checkIsActive();
		return b;
	}
	
	/** refresh local data */
	public void dataRefresh() {
		login = false;
		
		SObjectDB.SOBJECT_DB.clear();
		SObjectDB.SOBJECTS.clear();
		SObjectDB.SOBJECT_USER_DB.clear();
		SObjectDB.WHERE_HOLDER.clear();
	}
	
	public void getSObjectData() {
		ss.open(sa);
		
		// Create Keyprefix x Sobject createKeyprefixSObject
		anaylizeKeyPreFix();
		
		getLayout();
		getData();
						
		//getReport();				
		//saveIdAndToken();
		
		ss.close();
		
		// proccess after calling api
		handler.post(new Runnable() {
			public void run() {
				mSwitcher.setText("Login Success");
				loginButton.setText(" Go ");
				loginButton.setClickable(true);
				loginButton.setFocusable(true);
				demoId.setText("Click Go !");
			}
		});

	}
	
	// get layout date
	private void getLayout() {
		// English org record type
		String rds;	
		rds = StaticInformation.MASTER_RECORD_TYPE_ID;

		// DescribeSObject
		describeSObject("Event");
		
		//rds = "012400000005OcI"; //event
		bind.describeLayout("Event", rds);
		
		describeSObject("Task");
		//rds = "012400000005OcN"; //task
		bind.describeLayout("Task", rds);
		
		// Opportunity
		describeSObject("Opportunity");
		//rds = "012400000005NZa"; 
		bind.describeLayout("Opportunity", rds);
		
		// Case
		describeSObject("Case");
		//rds = "012400000005OnC";
		bind.describeLayout("Case", rds);
		
		// Contact
		describeSObject("Contact");
		//rds = "012400000005OmT";
		bind.describeLayout("Contact", rds);

		// Lead
		describeSObject("Lead");
		//rds = "012400000005On7";
		bind.describeLayout("Lead", rds);
		
		// Account
		describeSObject("Account");
		//rds = "012400000005NXt";
		bind.describeLayout("Account", rds);
		
		// User
		describeSObject("User");
	}
	
	// get data
	private void getData() {
		// query				
		query("Event");
		query("Task");
		query("Lead");
		query("Opportunity");
		query("Case");
		query("Contact");				
		query("Account");
		
		// user query
		bind.queryUser();

	}

	// get report data that is to be implmented
	private void getReport() {
		String mresult = retrieveMetaData();
		String id = checkStatus(mresult);
		String zip = checkRetrieveStatus(id);
		//writeData(zip);
		unZip("data/data/com.android/files/data5.zip");
		Log.v(TAG, "Unziping zipfile");
		//readFile("data/data/com.android/files/reports_SFA_OpportunityByPhase.report");
		//String json = readXmlFileAsJson("data/data/com.android/files/dashboards_Folder_OpportunityDashboard.dashboard");
		
	}
	
	// describeSobject caller
	private void describeSObject(String sobject) {
		SObject = sobject;

		table = bind.describeSOject(sobject);
		Log.v(TAG, sobject+ " table:" + table.toString());
		handler.post(new Runnable() {
			public void run() {
				mSwitcher.setText("Loading " + SObject + " Layout...");
			}
		});
		// to be implemented
		//ss.create(sa, table.toString(), sobject);
	}
	
	// save id and token	
	private void saveIdAndToken() {
		/**
		Log.v(TAG, "saving id and token...");
		String id = UserId.getText().toString();
		String token = "";//UserToken.getText().toString();
		String t = token == null ? "" : token;
		ss.saveIdAndToken(id, t, sa);
		*/
	}
	
	// read id and token
	public String readIdAndToken(){
		Log.v(TAG, "reading id and token...");
		return ss.readIdAndToken(sa);
	}
	
	// analize key prefix
	public void anaylizeKeyPreFix() {
		Log.v(TAG, "Creating Keyprefix x Sobject Table...");
		ss.createKeyprefixSObject(sa);
	}
	
	// describeLayout caller
	private void describeLayout(String sobject) {		
		//bind.describeLayout(sobject, rds);
	}
	
	// query caller
	private void query(String sobject){
		SObject = sobject;
		ArrayList<ContentValues> cv = bind.query(sobject);
		handler.post(new Runnable() {
			public void run() {
					mSwitcher.setText("Querying " + SObject + "...");
			}
		});
		
		/** this insert seems to cause an illegalstatement exception */
		/**
		for(ContentValues c : cv) {
			ss.insert(c, sobject);
		}
		*/
	}
	/** below methods for metadata api for near future */
	// metadata retrieve caller
	private String retrieveMetaData() {
		Log.v(TAG, "retrieve Metadata...");
		String ret = bind.retrieveMetaData();
		return ret;
	}
	
	// metadata retrieve caller
	private String checkStatus(String result) {
		Log.v(TAG, "checkStatus of Metadata...");
		String id = bind.checkStatus(result);
		return id;
	}
	
	// metadata retrieve caller
	private String  checkRetrieveStatus(String id) {
		Log.v(TAG, "checkRetrieveStatus of Metadata...");
		String zip = bind.checkRetrieveStatus(id);
		return zip;
	}
	
	// write data
	private void writeData(String fname, String data) {
		Log.v(TAG, "Writing data into local file...");
		ss.write(sa, fname, data);
	}
	
	/**
	 * unzip file of zipfile and copy the fils in other directory
	 * @param fileName
	 */
	private void unZip(String fileName) {
		Log.v(TAG, "unzipping file...");
		ss.unZip(fileName);
	}

	/** read file of give file
	 * 
	 * @param fileName
	 */
	private String readXmlFileAsJson(String fileName) {
		Log.v(TAG, "reading xml file as json...");
		String ret = bind.readFileAsStream(fileName);
		return ret;
	}
	/** read file of give file
	 * 
	 * @param fileName
	 */
	private void readFile(String fileName) {
	      BufferedReader br = null;
		try {
			  //String tfn = fileName.replaceAll("/", "_");
		      br = new BufferedReader(new FileReader(fileName));
		      String msg = "";
		      StringBuffer sb = new StringBuffer();
		      while(null != (msg = br.readLine())) {
		    	  Log.v(TAG, msg);
		    	  sb.append(msg.trim());
		      }
		      
		      Pattern pattern = Pattern.compile(".*.report");
		      Matcher matcher = pattern.matcher(fileName);
		      if(matcher.matches())ss.parseReportXML(sb.toString());

		      pattern = Pattern.compile(".*.dashboard");
		      matcher = pattern.matcher(fileName);
		      if(matcher.matches())ss.parseDashboardXML(sb.toString());

		      ss.parseReportXML(sb.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				br.close();
			} catch(IOException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			} 
		}
	}
}
