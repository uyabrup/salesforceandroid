/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.mypack.transport.AndroidHttpTransport;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;


import com.android.salesforce.database.SObjectSQLite;
import com.android.salesforce.frame.FieldHolder;
import com.android.salesforce.frame.SectionHolder;
import com.android.salesforce.util.SObjectDB;
import com.android.salesforce.util.StaticInformation;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

/**
 * This class works with Salesforce Apex API. 
 * @author Dai Odahara
 */
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
		String REF_TYPE = "reference";
		try {
			Log.v(TAG, "Start " + sobject + " Querying...");
			binding = new SoapObject(StaticInformation.NAMESPACE, "query");

			ArrayList<String> qf = new ArrayList<String>();
			qf.add("Id");

			if(sobject.equals("Contact") || sobject.equals("Lead"))qf.add("Name");
			
			ArrayList<SectionHolder> osh = SObjectDB.SOBJECTS.get(sobject).detail;
			int va = osh.size();
			
			//Log.v(TAG, "Section# :" + va);
			HashSet<String> refTypeField = new HashSet<String>();
			
			ArrayList<FieldHolder> fields = new ArrayList<FieldHolder>();
			for(int i = 0; i < va; i++) {
				//sh = osh.get(i);
				fields = osh.get(i).fields;
				int fa = fields.size();
				Log.v(TAG, "Field# :" + fa);
				for(int j = 0; j < fa; j++) {
					FieldHolder af = fields.get(j);
					if(af.type.equals(REF_TYPE))refTypeField.add(af.value);
					qf.add(af.value);
				}
			}
			StringBuffer where = new StringBuffer();
			if(SObjectDB.WHERE_HOLDER.containsKey(sobject)) {
				String[] refids = SObjectDB.WHERE_HOLDER.get(sobject).toString().split(",");
				int ids = refids.length;
				
				// where clause has ids to query
				if(ids >= 1) {
					String OR = " OR";
					for(int i = 0; i < ids; i++) {
						where.append(" Id=").append("'" + refids[i] + "'").append(OR);
					}
					where.delete(where.toString().length()-OR.length(), where.toString().length());
					
				}
			}
			/*
			if(sobject.equals("Account")){				
				ArrayList<String> eles = SObjectDB.WHERE_HOLDER.get("Opportunity");
				where.append(" WHERE");
				for(String s : eles) {
					where.append(" Id=").append("'" + s + "'").append(OR);
				}
				where.delete(where.toString().length()-OR.length(), where.toString().length());
				//where.append(")");
			}
			*/
		
			
			String limit = " LIMIT 20";

			String q = qf.toString();
			binding.addProperty("queryString", "SELECT "
					+ q.substring(1, q.length() - 1) + " FROM " + sobject
					+ where.toString()
					+ limit);
			
			Log.v(TAG, "SELECT :" + q);			
			Log.v(TAG, "WHERE :" + where.toString());
			Log.v(TAG, "LIMIT :" + limit);
			Log.v(TAG, "QUERY :" + "SELECT "
					+ q.substring(1, q.length() - 1) + " FROM " + sobject
					+ where
					+ limit);
			
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_SERVER_URL);
			Log.v(TAG, "Before query");
			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);
			Log.v(TAG, "After query");
			Object result = envelope.getResponse();

			Log.v(TAG, "Finish " + sobject + " Querying...");

			String[] records = handler.analyzeQueryResults(result,
					sobject);

			ret = handler.saveData(records, sobject, refTypeField);

			return ret;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}

	/** method for quering user */
	public HashMap<String, HashMap> queryUser() {
		String sobject = "User";
		HashMap<String, HashMap> ret = new HashMap<String, HashMap>();
		try {
			Log.v(TAG, "Start Querying User...");
			binding = new SoapObject(StaticInformation.NAMESPACE, "query");
			
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

			binding.addProperty("sObjectType", sobject);
			
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_SERVER_URL);

			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);

			Object result = envelope.getResponse();

			table = handler.analyzeDescribeSObjectResults(result, sobject);
			
			return table;
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