/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is temporary DB. This will be replaced...
 * 
 * @author Dai Odahara
 * 
 */
public class SObjectDB {
	public static StringBuffer AccountIdAndName = new StringBuffer();
	public static HashMap<String, String> AccountIdAndNameMap = new HashMap<String, String>();

	public static ArrayList<String> AccountLayoutName = new ArrayList<String>();
	public static HashMap<String, String> AccountLayoutNameToLabel = new HashMap<String, String>();
	public static HashMap<String, HashMap<String, String>> IdAndNAV = new HashMap<String, HashMap<String, String>>();
	public static StringBuffer ContactName = new StringBuffer();
	public static StringBuffer CaseName = new StringBuffer();
	public static StringBuffer OpportunityName = new StringBuffer();

}
