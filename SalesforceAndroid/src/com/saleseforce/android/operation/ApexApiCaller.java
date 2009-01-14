/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.saleseforce.android.operation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

//import org.apache.commons.codec.binary.Base64;
import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.mypack.transport.AndroidHttpTransport;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;


import com.salesforce.android.database.SObjectDataFactory;
import com.salesforce.android.frame.FieldHolder;
import com.salesforce.android.frame.SectionHolder;
import com.salesforce.android.util.SObjectDB;
import com.salesforce.android.util.StaticInformation;

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
	private SoapSerializationEnvelope menvelope;

	private AndroidHttpTransport androidHttpTransport;

	private SoapObject binding;
	private static ApexApiResultHandler handler = new ApexApiResultHandler();

	/** Called when the activity is first created. */
	public ApexApiCaller() {}

	/** login method */
	public boolean login() {
		boolean ret = false;
		try {
			Log.v(TAG, "Logging...");
			//Log.v(TAG, "id : " + StaticInformation.USER_ID);
			//Log.v(TAG, "pw : " + StaticInformation.USER_PW);

			binding = new SoapObject(StaticInformation.NAMESPACE, "login");
			binding.addProperty("username", StaticInformation.USER_ID);
			binding.addProperty("password", StaticInformation.USER_PW + StaticInformation.USER_TOKEN);

			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.LOGIN_SERVER_URL,
					StaticInformation.NAMESPACE);
			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);

			Object result = envelope.getResponse();

			handler.extractSessionId(result);
			handler.extractUserId(result);
			handler.extractAPIServerURL(result);
			
			Log.v(TAG, "Ending Login with Success.");
			//ErrorLogMailSender.getInstance().sendMail("LoginSuccess", StaticInformation.USER_ID);
			
			ret = true;
		} catch (IOException ex) {
			ex.printStackTrace();
			//ErrorLogMailSender.getInstance().sendMail(TAG, ex.getStackTrace());
		} catch (Exception ex) {
			ex.printStackTrace();
			//ErrorLogMailSender.getInstance().sendMail(TAG, ex.getStackTrace());
		}
		return ret;
	}

	/** Salesforce Force.com MetaData API */
	/**
	 * Issue a retrieve() call to start the asynchronous retrieval. An
	 * AsyncResult object is returned. If the call is completed, the done field
	 * contains true. Most often, the call is not completed quickly enough to be
	 * noted in the result. If it is completed, note the value in the id field
	 * returned and skip the next step.
	 */
	public String retrieveMetaData() {
		String result = "";
		try {
			SoapObject parent = new SoapObject(
					StaticInformation.METADATA_NAMESPACE, "retrieve");

			SoapObject mbinding = new SoapObject(
					StaticInformation.METADATA_NAMESPACE, "Dashboard");

			//SoapObject pac = new SoapObject(
			//		StaticInformation.METADATA_NAMESPACE, "Package");

			//String pack = "SalesPackage";
			//String dn = "CompanyPerformanceDashboard";
			// client.addProperty("folder", dn);//AdoptionDashboard
			// CompanyDashboards
			// dn = "layouts/Contact-Contact Layout.layout";

			SoapPrimitive sp = new SoapPrimitive(
					StaticInformation.XSD_NAMESPACE, "double", "14.0");
			mbinding.addProperty("apiVersion", sp);

			sp = new SoapPrimitive(StaticInformation.XSD_NAMESPACE, "string",
			"SSS");
			mbinding.addProperty("packageNames", sp);
		
			sp = new SoapPrimitive(StaticInformation.XSD_NAMESPACE, "boolean",
					"true");
			mbinding.addProperty("singlePackage", sp);
			//String nn = "CompanyDashboards/";
			// nn = "dash";
			//String fn = "dashboards/dashboards/CompanyDashboards/CompanyPerformanceDashboard.dashboard";
			//fn = "dashboards/CompanyDashboards/CompanyPerformanceDashboard";
			/*
			sp = new SoapPrimitive(StaticInformation.XSD_NAMESPACE, "string",
					"SSS");
			mbinding.addProperty("packageNames", sp);

			sp = new SoapPrimitive(StaticInformation.XSD_NAMESPACE, "boolean",
					"true");
			mbinding.addProperty("singlePackage", sp);
			*/
			// success : sp = new SoapPrimitive(StaticInformation.XSD_NAMESPACE,
			// "string", fn);
			// sp = new SoapPrimitive(StaticInformation.XSD_NAMESPACE, "string",
			// fn);
			// mbinding.addProperty("specificFiles", sp);

			/*
			 * sp = new SoapPrimitive(StaticInformation.XSD_NAMESPACE, "string",
			 * fn); pac.addProperty("fullName", sp);
			 * 
			 * sp = new SoapPrimitive(StaticInformation.XSD_NAMESPACE, "string",
			 * "1.0.0"); pac.addProperty("version", sp);
			 */
			// mbinding.addProperty("unpackaged", pac);
			parent.addProperty("metadata", mbinding);

			menvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			menvelope.setOutputSoapObject(parent);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_META_DATA_SERVER_URL,
					StaticInformation.METADATA_NAMESPACE);

			androidHttpTransport.call(StaticInformation.SOAP_ACTION, menvelope);

			result = menvelope.getResponse().toString();
			Log.v(TAG, "retrieve Result : \n" + result);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * If the call is not complete, issue a checkStatus() call in a loop using
	 * the value in the id field of the AsyncResult object returned by the
	 * retrieve() call in the previous step. Check the AsyncResult object
	 * returned, until the done field contains true. The time taken to complete
	 * a retrieve() call depends on the size of the zip file being deployed, so
	 * a longer wait time between iterations should be used as the size of the
	 * zip file increases.
	 * 
	 * @param result
	 * @return
	 */
	public String checkStatus(String result) {
		String id = "";
		try {
			int s = result.indexOf("id=");
			int e = result.indexOf("; ", s);
			id = result.subSequence(s + "id=".length(), e).toString();

			Log.v(TAG, "retrieve id:" + id);
		
			SoapObject parent = new SoapObject(
					StaticInformation.METADATA_NAMESPACE, "checkStatus");

			SoapPrimitive sp = new SoapPrimitive(
					StaticInformation.METADATA_NAMESPACE, "ID", id);
			parent.addProperty("asyncProcessId", sp);

			menvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			menvelope.setOutputSoapObject(parent);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_META_DATA_SERVER_URL,
					StaticInformation.METADATA_NAMESPACE);

			boolean done = false;

			while (!done) {
				androidHttpTransport.call(StaticInformation.SOAP_ACTION, menvelope);

				String response = menvelope.getResponse().toString();

				Log.v(TAG, "retrieveMetaData Result : \n" + response);

				s = response.toString().indexOf("done=");
				e = response.toString().indexOf("; ", s);
				
				//System.out.println("done?"
				//		+ response.subSequence(s + "done=".length(), e));

				done = Boolean.valueOf(response.toString().subSequence(
						s + "done=".length(), e).toString());

				Log.v(TAG, "Waiting...");
				Thread.sleep(500);
			}
			Log.v(TAG, "CheckedStatus is Done");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return id;

	}

	/**
	 * Issue a checkRetrieveStatus() call to obtain the results of the
	 * retrieve() call, using the id value returned in the first step.
	 * 
	 * @param id
	 */
	public String checkRetrieveStatus(String id) {
		String zip = "";
		try {
			SoapObject parent = new SoapObject(
					StaticInformation.METADATA_NAMESPACE, "checkRetrieveStatus");

			SoapPrimitive sp = new SoapPrimitive(
					StaticInformation.METADATA_NAMESPACE, "ID", id);
			parent.addProperty("asyncProcessId", sp);

			menvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			menvelope.setOutputSoapObject(parent);
			
			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_META_DATA_SERVER_URL,
					StaticInformation.METADATA_NAMESPACE);
			androidHttpTransport.call(StaticInformation.SOAP_ACTION, menvelope);

			String result = menvelope.getResponse().toString();

			Log.v(TAG, "checkRetrieveStatus Result : \n" + result);

			int s = result.indexOf("zipFile=");
			int e = result.indexOf("; ", s);
			zip = result.substring(s + "zipFile=".length(), e).toString();
			
			Log.v(TAG, "zipFile:\n" + zip + "\n");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return zip;
	}

	/** read file as stream of give file. This is not actually calling api call.
	 * 
	 * @param fileName
	 */
	public String readFileAsStream(String fileName) {
		String ret = "";
		try {
            FileInputStream is = new FileInputStream(fileName);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            AndroidHttpTransport aht = new AndroidHttpTransport(StaticInformation.API_META_DATA_SERVER_URL,
					StaticInformation.METADATA_NAMESPACE);
            aht.parseXmlAsJson(envelope, is, "Dashboard");
            ret = envelope.getResponseAsString();

			Log.v(TAG, "Xml2Json Result :" + ret);

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}
	

	/** read file as stream of give file
	 * 
	 * @param fileName
	 */
	public String makeQuery(String json) {
		StringBuffer ret = new StringBuffer();
		ret.append("SELECT Id,Amount,");
		int i = 0, j = 0;
		String temp = "";
		while(-1 != (i = json.indexOf("columns=anyType{field=", i))){
			j = json.indexOf(";", i+1);
			//Log.v(TAG, i + ":" + j);
			temp = json.substring(i + "columns=anyType{field=".length(), j);
			Log.v(TAG, temp);
			i = j;
			Log.v(TAG, "temp:" + temp);
			if(temp.equals("OPPORTUNITY_NAME"))ret.append("Name");
			else if(temp.equals("ACCOUNT_NAME")){ret.append("AccountId"); /** ret.append("Account.Name");*/}
			else if(temp.equals("CLOSE_DATE"))ret.append("CloseDate");
			else if(temp.equals("STAGE_NAME"))ret.append("StageName");
			//else if(temp.equals("AMOUNT"))ret.append("Amount");
			else if(temp.equals("NEXT_STEP"))ret.append("NextStep");
			else if(temp.equals("CREATED_DATE"))ret.append("CreatedDate");
			else continue;
			
			ret.append(",");
			/*
			String[] lv = temp.split("=");
			for(String b : lv) {
				String[] lx = b.split("=");
				Log.v(TAG, lx[0] + "-----" + lx[1]);
				//dc.put(lx[0], lx[1]);
			}
			*/
		}
		ret = ret.delete(ret.length()-1, ret.length());
		
		temp = json.substring(j, json.length()-1);
		Log.v(TAG, i + "--" + j);
		Log.v(TAG, "temp:" + temp);
		i = json.indexOf("reportType=", j);
		Log.v(TAG, i + "--" + j);
		
		j = json.indexOf(";", i+1);
		Log.v(TAG, i + "--" + j);
		Log.v(TAG, "rest:" + json.substring(i + "reportType=".length(), json.length()-1));
		Log.v(TAG, "rest:" + json.substring(i + "reportType=".length(), j));
		//temp = temp.substring(i + "reportType=".length(), json.length());
		ret.append(" FROM " + json.substring(i + "reportType=".length(), j));
		
		i = temp.indexOf("sortOrder=");
		j = temp.indexOf("; ", i+1);
		
		//Log.v(TAG, "rest:" + temp.substring(i + "sortOrder=".length(), j));
		//ret.append(" FROM " + json.substring(i + "sortOrder=".length(), j));
		
		i = temp.indexOf("timeFrameFilter=anyType{dateColumn=");
		j = temp.indexOf("; ", i+1);
		
		Log.v(TAG, "rest:" + temp.substring(i + "timeFrameFilter=anyType{dateColumn=".length(), j));
		String where = temp.substring(i + "timeFrameFilter=anyType{dateColumn=".length(), j);
		if(where.equals("CLOSE_DATE"))where = "CloseDate";
		
		i = temp.indexOf("interval=");
		j = temp.indexOf("; ", i+1);
		String inteval = temp.substring(i + "interval=".length(), j);
		Log.v(TAG, "rest:" + temp.substring(i + "interval=".length(), j));
		if(inteval.equals("INTERVAL_THISMONTH"))inteval = "=THIS_MONTH";
		
		ret.append(" WHERE " + where + inteval);
		Log.v(TAG, ret.toString());
		
		return ret.toString();
	}
	
	/** query method */
	public boolean checkIsActive() {

		try {
			Log.v(TAG, "Start User Activeness...");
			binding = new SoapObject(StaticInformation.NAMESPACE, "query");

			//String select = "SELECT Id, Name, Android__User__r.Id, Android__visualforceUrl__c ,Android__isActive__c FROM Android__AndroidUserManagement__c WHERE Android__User__r.Id='" + StaticInformation.USER_ID_18DIGITS + "'";
			String select = "SELECT Id, Name, Android__User__r.Id, Android__visualforceUrl__c ,Android__isActive__c FROM Android__AndroidUserManagement__c WHERE Android__User__r.Id='" + StaticInformation.USER_ID_18DIGITS + "'";

			binding.addProperty("queryString", select);
			
			Log.v(TAG, "SELECT :" + select);			

			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_SERVER_URL,
					StaticInformation.NAMESPACE);
			
			Log.v(TAG, "Start querying...");
			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);
			Object result = envelope.getResponse();

			//Log.v(TAG, "Finish Activeness Querying...");

			Log.v(TAG, "ActiveCheckResult:" + result.toString());
			
			return handler.extractUserIsActive(result.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
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

			//ArrayList<String> qf = new ArrayList<String>();
			StringBuffer select = new StringBuffer();
			select.append("SELECT Id,LastModifiedDate");

			if(sobject.equals("Contact") || sobject.equals("Lead"))select.append(",Name");
			
			ArrayList<SectionHolder> osh = SObjectDB.SOBJECTS.get(sobject).detail;
			int va = osh.size();
			
			//Log.v(TAG, "Section# :" + va);
			HashSet<String> refTypeField = new HashSet<String>();
			
			ArrayList<FieldHolder> fields = new ArrayList<FieldHolder>();
			for(int i = 0; i < va; i++) {
				fields = osh.get(i).fields;
				int fa = fields.size();
				//Log.v(TAG, "Field# :" + fa);
				for(int j = 0; j < fa; j++) {
					FieldHolder af = fields.get(j);
					if(af.type.equals(REF_TYPE))refTypeField.add(af.value);
					select.append("," + af.value);
				}
			}
			select.append(" FROM " + sobject);

			StringBuffer where = new StringBuffer(" WHERE");
			
			/**
			if(sobject.equals("Event")) {
				where.append(" (");
				where.append("OwnerId='" + StaticInformation.USER_ID_18DIGITS + "'");
				where.append(" AND ");
				where.append("StartDateTime>=THIS_WEEK AND EndDateTime<=NEXT_WEEK)");
			}
			
			if(sobject.equals("Task")) {
				where.append(" (");
				where.append("OwnerId='" + StaticInformation.USER_ID_18DIGITS + "'");
				where.append(" AND ");
				where.append("ActivityDate=THIS_WEEK AND ActivityDate=NEXT_WEEK)");
			}
			
			*/
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
			//where.append(")");
			if(!where.toString().equals(" WHERE"))where.append(" ORDER BY LastModifiedDate DESC NULLS LAST LIMIT 25");
			else where = new StringBuffer(" ORDER BY LastModifiedDate DESC NULLS LAST LIMIT 25");
			
			//String limit = " LIMIT 15";
			String limit = "";

			//String q = qf.toString();
			binding.addProperty("queryString", 
					select.toString()
					//+ q.substring(1, q.length() - 1) + " FROM " + sobject
					+ where.toString()
					+ limit);
			
			Log.v(TAG, "SELECT :" + select.toString());			
			Log.v(TAG, "WHERE :" + where.toString());
			Log.v(TAG, "LIMIT :" + limit);
			
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_SERVER_URL,
					StaticInformation.NAMESPACE);
			Log.v(TAG, "Start querying...");
			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);
			Object result = envelope.getResponse();

			Log.v(TAG, "Finish " + sobject + " Querying...");

			String[] records = handler.analyzeQueryResults(result,
					sobject);

			ret = handler.saveData(records, sobject, refTypeField, true, false);

			return ret;
		} catch (Exception ex) {
			ex.printStackTrace();
			//ex.getCause().toString()
			//ErrorLogMailSender.getInstance().sendMail("Error:" + TAG, ex.getCause().toString());
		}
		return ret;
	}

	/** query method */
	public ArrayList<ContentValues> queryIdAndName(String sobject, String pId, String pField) {
		ArrayList<ContentValues> ret = new ArrayList<ContentValues>();
		String REF_TYPE = "reference";
		try {
			Log.v(TAG, "Start " + sobject + " Querying Id and Name...");
			binding = new SoapObject(StaticInformation.NAMESPACE, "query");

			ArrayList<String> qf = new ArrayList<String>();
			qf.add("Id"); qf.add("Name");
			
			String select = "SELECT Id, Name, " + pField + " FROM " 
				+ sobject + " WHERE " + pField + "=" + pId;
			Log.v(TAG, "SELECT:" + select);

			String q = qf.toString();
			binding.addProperty("queryString", select);
			
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_SERVER_URL,
					StaticInformation.NAMESPACE);
			Log.v(TAG, "Start querying...");
			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);
			Object result = envelope.getResponse();

			Log.v(TAG, "Finish " + sobject + " Querying...");

			String[] records = handler.analyzeQueryResults(result,
					sobject);

			for(String r : records) {
				Log.v(TAG, "rec:" + r);
			}
			//ret = handler.saveData(records, sobject, refTypeField, true);

			return ret;
		} catch (Exception ex) {
			ex.printStackTrace();
			//ex.getCause().toString()
			//ErrorLogMailSender.getInstance().sendMail("Error:" + TAG, ex.getCause().toString());
		}
		return ret;
	}

	
	/** query method with specified query */
	public HashMap queryWith(String queryString, String sobject, String name) {
		HashMap ret = new HashMap();
		String REF_TYPE = "reference";
		try {
			Log.v(TAG, "Start queryWith Querying...");
			binding = new SoapObject(StaticInformation.NAMESPACE, "query");

			binding.addProperty("queryString", queryString);

			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_SERVER_URL,
					StaticInformation.NAMESPACE);
			Log.v(TAG, "Before query");
			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);
			Log.v(TAG, "After query");
			Object result = envelope.getResponse();

			Log.v(TAG, "Finish queryWith Querying...");

			String[] records = handler.analyzeQueryResults(result,
					sobject);

			ret = handler.retrieveData(records, sobject, false);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}
	
	/** query method with name as where clause */
	public int queryWithName(String sobject, String like) {
		int recs = 0;
		String REF_TYPE = "reference";
		try {
			Log.v(TAG, "Start queryWithName Querying...");
			binding = new SoapObject(StaticInformation.NAMESPACE, "query");

			StringBuffer select = new StringBuffer();
			select.append("SELECT Id,LastModifiedDate");
			if(sobject.equals("Contact") || sobject.equals("Lead"))select.append(",Name");
			
			ArrayList<SectionHolder> osh = SObjectDB.SOBJECTS.get(sobject).detail;
			int va = osh.size();
			HashSet<String> refTypeField = new HashSet<String>();		
			ArrayList<FieldHolder> fields = new ArrayList<FieldHolder>();
			for(int i = 0; i < va; i++) {
				fields = osh.get(i).fields;
				int fa = fields.size();
				//Log.v(TAG, "Field# :" + fa);
				for(int j = 0; j < fa; j++) {
					FieldHolder af = fields.get(j);
					if(af.type.equals(REF_TYPE))refTypeField.add(af.value);
					select.append("," + af.value);
				}
			}
			String name = sobject.equals("Event") || sobject.equals("Task") || sobject.equals("Case")? "Subject" : "Name";
			select.append(" FROM " + sobject + " WHERE " + name + " Like '%" + like + "%'");
			Log.v(TAG, "select:" + select.toString());
			
			binding.addProperty("queryString", select.toString());

			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_SERVER_URL,
					StaticInformation.NAMESPACE);
			Log.v(TAG, "Before query");
			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);
			Log.v(TAG, "After query");
			Object result = envelope.getResponse();

			Log.v(TAG, "Finish queryWithName Querying...");

			String[] records = handler.analyzeQueryResults(result,
					sobject);
			recs = records.length;
			//ret = handler.retrieveData(records, sobject, false);
			handler.saveData(records, sobject, refTypeField, true, true);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return recs;
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
					StaticInformation.API_SERVER_URL,
					StaticInformation.NAMESPACE);
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
					StaticInformation.API_SERVER_URL,
					StaticInformation.NAMESPACE);

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
			
			androidHttpTransport = new AndroidHttpTransport( StaticInformation.API_SERVER_URL, StaticInformation.NAMESPACE );
			
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