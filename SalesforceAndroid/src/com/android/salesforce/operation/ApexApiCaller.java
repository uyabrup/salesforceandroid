/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.mypack.transport.AndroidHttpTransport;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;


import com.android.salesforce.database.SObjectSQLite;
import com.android.salesforce.frame.SectionHolder;
import com.android.salesforce.sobject.AccountInfo;
import com.android.salesforce.sobject.SObjectImpl;
import com.android.salesforce.util.SObjectDB;
import com.android.salesforce.util.StaticInformation;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public class ApexApiCaller {
	private static final String TAG = "ApexApiCaller";

	private SoapSerializationEnvelope envelope;
	private AndroidHttpTransport androidHttpTransport;

	private SoapObject binding;
	private static ApexApiResultHandler handler = new ApexApiResultHandler();

	/** Called when the activity is first created. */
	public ApexApiCaller() {}

	/** login method */
	public boolean login() {

		try {
			Log.v(TAG, "Logging...");
			Log.v(TAG, "id : " + StaticInformation.USER_ID);
			Log.v(TAG, "pw : " + StaticInformation.USER_PW);

			binding = new SoapObject(StaticInformation.NAMESPACE, "login");
			binding.addProperty("username", StaticInformation.USER_ID);
			binding.addProperty("password", StaticInformation.USER_PW);

			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.LOGIN_SERVER_URL);
			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);

			Object result = envelope.getResponse();

			handler.extractSessionId(result);
			handler.extractAPIServerURL(result);
			
			Log.v(TAG, "Ending Login with Success.");

			return true;
		} catch (IOException E) {
			E.printStackTrace();
		} catch (Exception E) {
			E.printStackTrace();
		}
		return false;
	}

	/** query method */
	public ArrayList<ContentValues> query(String sobject) {
		ArrayList<ContentValues> ret = new ArrayList<ContentValues>();
		try {
			Log.v(TAG, "Start Querying...");
			binding = new SoapObject(StaticInformation.NAMESPACE, "query");

			/** to be modified */
			// ArrayList<String> qf = new ArrayList<String>();
			// qf.add("Id"); qf.add("Name"); qf.add("Description");
			// qf.add("Phone");qf.add("Site");
			// ArrayList<String> ql =SObjectDB.AccountLayoutLabel;
			/** end of being modified */
			
			ArrayList<String> qf = new ArrayList<String>();
			//String q = qf.toString();

			qf.add("Id");
			ArrayList<SectionHolder> osh = SObjectDB.SOBJECTS.get(sobject).sections;
			int va = osh.size();
			//Log.v(TAG, "Section# :" + va);
			SectionHolder sh = new SectionHolder();
			for(int i = 0; i < va; i++) {
				sh = osh.get(i);
				int fa = sh.fields.size();
				//Log.v(TAG, "Field# :" + fa);
				for(int j = 0; j < fa; j++) {					
					qf.add(sh.fields.get(j).value);
				}
			}
			
			StringBuffer where = new StringBuffer();
			if(sobject.equals("Account")){				
				ArrayList<String> eles = SObjectDB.WHERE_HOLDER.get("Opportunity");
				where.append(" WHERE");
				for(String s : eles) {
					where.append(" Id=").append("'" + s + "'").append(" OR");
				}
				where.delete(where.toString().length()-3, where.toString().length());
				//where.append(")");
			}
			
			Log.v(TAG, "WHERE :" + where.toString());
			
			String limit = " LIMIT 20";
			Log.v(TAG, "LIMIT :" + limit);
			
			String q = qf.toString();
			binding.addProperty("queryString", "SELECT "
					+ q.substring(1, q.length() - 1) + " FROM " + sobject
					+ where
					+ limit);
			
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_SERVER_URL);
			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);

			Object result = envelope.getResponse();

			Log.v(TAG, "Finishe Querying...");

			String[] records = handler.analyzeQueryResults(result,
					sobject);

			ret = handler.saveData(records, sobject);

			return ret;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}

	/** temp method for quering user */
	/** query method */
	public HashMap<String, HashMap> queryUser() {
		String sobject = "User";
		HashMap<String, HashMap> ret = new HashMap<String, HashMap>();
		try {
			Log.v(TAG, "Start Querying User...");
			binding = new SoapObject(StaticInformation.NAMESPACE, "query");

			/** to be modified */
			// ArrayList<String> qf = new ArrayList<String>();
			// qf.add("Id"); qf.add("Name"); qf.add("Description");
			// qf.add("Phone");qf.add("Site");
			// ArrayList<String> ql =SObjectDB.AccountLayoutLabel;
			/** end of being modified */
			
			ArrayList<String> qf = new ArrayList<String>();
			//String q = qf.toString();
			qf.add("Id");qf.add("Name");qf.add("Title"); qf.add("Email");
			
			String where = "";
			String limit = "";
			Log.v(TAG, "LIMIT :" + limit);
			
			String q = qf.toString();
			binding.addProperty("queryString", "SELECT "
					+ q.substring(1, q.length() - 1) + " FROM " + sobject
					+ where
					+ limit);
			
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_SERVER_URL);
			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);

			Object result = envelope.getResponse();

			Log.v(TAG, "Finishe Querying...");

			String[] records = handler.analyzeQueryResults(result,
					sobject);

			handler.saveUserData(records, sobject);

			return ret;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}

	
	/** describe method */
	public StringBuffer describeSOject(String sobject) {
		StringBuffer table = new StringBuffer();
		try {
			Log.v(TAG, "Start Describing...");
			binding = new SoapObject(StaticInformation.NAMESPACE,
					"describeSObject");

			/** to be modified */
			// ArrayList<String> qf = new ArrayList<String>();
			// qf.add("Account");
			// String q = qf.toString();
			/** end of being modified */

			binding.addProperty("sObjectType", sobject);
			
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_SERVER_URL);

			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);

			Object result = envelope.getResponse();

			/** to be modified */
			// ArrayList<String> qf = new ArrayList<String>();
			// qf.add("Id"); qf.add("Subject"); qf.add("Description");

			table = handler.analyzeDescribeSObjectResults(result, sobject);
			
			return table;
			//SObjectSQLite ss = new SObjectSQLite(context, table);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return table;
	}
	
	/** describeLayout method */

	public void describeLayout(String sobject, String recordTypeIds) {
		try {
			Log.v(TAG, "DescribingLayout...");
			binding = new SoapObject(
					StaticInformation.NAMESPACE, "describeLayout");
			binding.addProperty("sObjectType", sobject);
			binding.addProperty("recordTypeIds", recordTypeIds);
			
			/** end of being modified */
			
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);
			
			androidHttpTransport = new AndroidHttpTransport( StaticInformation.API_SERVER_URL );
			
			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);

			Object result = envelope.getResponse();
			
			//System.out.println("Query Result : \n" + result.toString());
			//showWithIndent(result.toString());
			handler.analyzeDescribeLayoutResults(result, sobject);
			
		} catch (Exception E) {
			 E.printStackTrace();
		}
	}

}