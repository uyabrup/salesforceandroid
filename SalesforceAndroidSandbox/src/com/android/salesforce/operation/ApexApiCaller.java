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

import com.android.salesforce.sobject.AccountInfo;
import com.android.salesforce.sobject.SObjectImpl;
import com.android.salesforce.util.SObjectDB;
import com.android.salesforce.util.StaticInformation;

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
	public boolean query(String sobject) {
		try {
			Log.v(TAG, "Start Querying...");
			binding = new SoapObject(StaticInformation.NAMESPACE, "query");

			/** to be modified */
			// ArrayList<String> qf = new ArrayList<String>();
			// qf.add("Id"); qf.add("Name"); qf.add("Description");
			// qf.add("Phone");qf.add("Site");
			// ArrayList<String> ql =SObjectDB.AccountLayoutLabel;
			/** end of being modified */
			
			ArrayList<String> qf = SObjectDB.AccountLayoutName;
			String q = qf.toString();

			binding.addProperty("queryString", "SELECT "
					+ q.substring(1, q.length() - 1) + " FROM " + sobject
					+ " LIMIT 15");
			
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(binding);

			androidHttpTransport = new AndroidHttpTransport(
					StaticInformation.API_SERVER_URL);
			androidHttpTransport.call(StaticInformation.SOAP_ACTION, envelope);

			Object result = envelope.getResponse();

			Log.v(TAG, "Finishe Querying...");

			String[] records = handler.getQueryResults(result,
					sobject);

			int sa = records.length;

			for (int i = 0; i < sa; i++) {
				HashMap<String, String> nav = handler.extractNameAndValue(records[i]);
				SObjectImpl si = new SObjectImpl(sobject + "Info", qf, nav);
				SObjectDB.AccountIdAndName.append(((AccountInfo) (si.so)).Id)
						.append(":").append(((AccountInfo) (si.so)).Name)
						.append(";");
				SObjectDB.AccountIdAndNameMap.put(((AccountInfo) (si.so)).Id,
						((AccountInfo) (si.so)).Name);

				//Log.v(TAG, "nav : " + SObjectDB.AccountIdAndName.toString());

				SObjectDB.IdAndNAV.put(((AccountInfo) (si.so)).Id, nav);
				Log.v(TAG, "AccId : " + ((AccountInfo) (si.so)).Id);
				Log.v(TAG, "AccName : " + ((AccountInfo) (si.so)).Name);
				Log.v(TAG, "AccSite : " + ((AccountInfo) (si.so)).Site);
				Log.v(TAG, "AccPhone : " + ((AccountInfo) (si.so)).Phone);

			}

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/** describe method */
	public void describe(String sobject) {
		try {
			System.out.println("Start Describing...");
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

			handler.analyzeDescribeResults(result, sobject);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}