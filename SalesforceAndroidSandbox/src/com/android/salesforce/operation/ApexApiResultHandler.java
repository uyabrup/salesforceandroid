/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.operation;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.android.salesforce.util.SObjectDB;
import com.android.salesforce.util.StaticInformation;

public class ApexApiResultHandler {
	private static final String TAG = "ApexApiResultHandler";

	public ApexApiResultHandler() {
	}

	/** extracts session id of salesforce force.com api */
	public void extractSessionId(Object result) {
		Log.v(TAG, "Start Extracting Session Id");
		String res = result.toString();
		String si = "sessionId=";
		int ss = res.indexOf(si);
		int es = res.indexOf(' ', ss);
		StaticInformation.SESSION_ID = res.substring(ss + si.length(), es - 1);
		Log.v(TAG, "Session Id : " + StaticInformation.SESSION_ID);
	}

	/** extracts API server URL of salesforce force.com api */
	public void extractAPIServerURL(Object result) {
		String res = result.toString();
		String si = "serverUrl=";
		int ss = res.indexOf(si);
		int es = res.indexOf(' ', ss);
		Log.v(TAG, "\tAPI Server URL : "
				+ res.substring(ss + si.length(), es - 1));
		StaticInformation.API_SERVER_URL = res.substring(ss + si.length(),
				es - 1);
	}

	/** extracts record item name and value from soap */
	public HashMap<String, String> extractNameAndValue(String records) {
		HashMap<String, String> ret = new HashMap<String, String>();
		String[] elements = records.split("; ");
		int size = elements.length;
		for (int i = 0; i < size; i++) {
			String[] vals = elements[i].split("=");
			ret.put(vals[0], vals[1]);
		}
		return ret;
	}

	/** extracts query result size */
	public int getQueryResultSize(String res) {
		Log.v(TAG, "Result : " + res);
		String si = "size=";
		int ss = res.indexOf(si);
		int es = res.indexOf(';', ss + 1);
		Log.v(TAG, "Result size : "
				+ Integer.valueOf(res.substring(ss + si.length(), es)));
		return Integer.valueOf(res.substring(ss + si.length(), es));
	}

	/** extracts result records */	
	public String[] getQueryResults(Object result, String sobject) {

		String res = result.toString();
		int sa = getQueryResultSize(res);
		String[] ret = new String[sa];

		String si = "records=" + sobject + "{";
		int ss = 0, es = 0;
		for (int i = 0; i < sa; i++) {
			ss = res.indexOf(si, es);
			es = res.indexOf('}', ss + 1);
			ret[i] = res.substring(ss + si.length(), es);
			Log.v(TAG, "record : " + ret[i]);
		}
		return ret;
	}
	
	/** analyze apex describe api call result */	
	public String[] analyzeDescribeResults(Object result, String sobject) {

		String res = result.toString();
		// System.out.println( res.length() );

		String fn = "fields=anyType";
		int start = res.indexOf(fn);

		String kpn = "keyPrefix";
		int end = res.indexOf(kpn);

		String f = res.substring(start, end);
		// System.out.println( f );
		// System.out.println( res.substring(matcher.start(), matcher.start() +
		// start.length()) );

		String[] fs = f.split("fields=anyType\\{");
		int fSize = fs.length, s1 = 0, e1 = 0;

		String[] ps;
		int pSize = 0, s2 = 0, e2 = 0;

		for (int i = 1; i < fSize; i++) {
			s1 = fs[i].indexOf("autoNumber=");
			e1 = fs[i].indexOf(";", s1);

			// System.out.print(i + ":autoNumber=" + fs[i].substring(s1 +
			// "autoNumber=".length(), e1));

			s1 = fs[i].indexOf("calculated=");
			e1 = fs[i].indexOf(";", s1);

			// System.out.print(":calculated=" + fs[i].substring(s1 +
			// "calculated=".length(), e1));

			s1 = fs[i].indexOf("idLookup=");
			e1 = fs[i].indexOf(";", s1);

			// System.out.print(":idLookup=" + fs[i].substring(s1 +
			// "idLookup=".length(), e1));

			s1 = fs[i].indexOf("label=");
			e1 = fs[i].indexOf(";", s1);
			String label = fs[i].substring(s1 + "label=".length(), e1);
			// SObjectDB.AccountLayoutLabel.add(fs[i].substring(s1 +
			// "label=".length(), e1));
			Log.v(TAG, "label=" + fs[i].substring(s1 + "label=".length(), e1));
			// System.out.print(":label=" + fs[i].substring(s1 +
			// "label=".length(), e1));

			s1 = fs[i].indexOf("length=", e1);
			e1 = fs[i].indexOf(";", s1);

			// System.out.print(":length=" + fs[i].substring(s1 +
			// "length=".length(), e1));

			s1 = fs[i].indexOf("name=", e1);
			e1 = fs[i].indexOf(";", s1);

			// Log.v(TAG, "name=" + fs[i].substring(s1 + "name=".length(), e1));
			// SObjectDB.AccountLayout.append(fs[i].substring(s1 +
			// "name=".length(), e1)).append(",");
			SObjectDB.AccountLayoutName.add(fs[i].substring(s1
					+ "name=".length(), e1));
			SObjectDB.AccountLayoutNameToLabel.put(fs[i].substring(s1
					+ "name=".length(), e1), label);

			ps = fs[i].split("picklistValues=anyType\\{");
			pSize = ps.length;
			s2 = 0;
			e2 = 0;
			for (int j = 1; j < pSize; j++) {
				s2 = ps[j].indexOf("label=");
				e2 = ps[j].indexOf(";", s2);
				// System.out.print("\tlabel=" + ps[j].substring(s2 +
				// "label=".length(), e2));

				s2 = ps[j].indexOf("value=", e2);
				e2 = ps[j].indexOf(";", s2);

				// System.out.println(":value=" + ps[j].substring(s2 +
				// "value=".length(), e2));
			}
		}
		fSize++;

		int sa = getDescribeResultFieldSize(result);
		String[] ret = new String[sa];

		return ret;
	}

	private int getDescribeResultFieldSize(Object result) {
		Pattern pattern = Pattern.compile("fields=anyType");
		Matcher matcher = pattern.matcher(result.toString());
		int size = 0;
		while (matcher.find()) {
			size++;
		}
		Log.v(TAG, "Fields size : " + size);
		return size;
	}
}
