/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.salesforce.frame.SectionHolder;

/**
 * This class is temporary DB. This will be replaced...
 * 
 * @author Dai Odahara
 * 
 */
public class SObjectDB {
	/*
	public static StringBuffer AccountIdAndName = new StringBuffer();
	public static HashMap<String, String> AccountIdAndNameMap = new HashMap<String, String>();

	public static ArrayList<String> AccountLayoutName = new ArrayList<String>();
	public static HashMap<String, String> AccountLayoutNameToLabel = new HashMap<String, String>();
	public static HashMap<String, HashMap<String, String>> IdAndNAV = new HashMap<String, HashMap<String, String>>();
	public static StringBuffer ContactName = new StringBuffer();
	public static StringBuffer CaseName = new StringBuffer();
	public static StringBuffer OpportunityName = new StringBuffer();
	*/
	/** */
	public static HashMap<String,SObjectFactory> SOBJECTS = new HashMap<String,SObjectFactory>();
	public static HashMap<String, HashMap<String, HashMap>> SOBJECT_DB = new HashMap<String, HashMap<String, HashMap>>();
	public static HashMap<String, HashMap<String, String>> SYSTEM_DB = new HashMap<String, HashMap<String, String>>();
	public static HashMap<String, ArrayList<String>> WHERE_HOLDER = new HashMap<String, ArrayList<String>>();
	
}
