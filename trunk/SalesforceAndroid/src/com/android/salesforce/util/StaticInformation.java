/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.util.Log;
import android.view.ViewGroup;

/**
 * This class is salesforce utility class. Analyze xml from salesforce query, describe, etc.
 * @author Dai Odahara
 *
 */
public class StaticInformation {
	private static final String TAG = "StaticInformation";
//	public static String USER_ID= "mn@sm.com";
//	public static String USER_PW = "sfdcj12345bqdqRDDd19FTdlOwyBfe94pLZ";
	//public static String USER_PW = "password";
	public static int RECORD_ID_LENGTH = 18;
	public static int SOBJECT_PREFIX_SIZE = 3;
	public static String USER_ID= "";
	public static String USER_NAME= "";
	//public static String USER_ID= "user01@capture.honishi.org";
	//public static String USER_PW = "abcd1234";
	public static String USER_PW = "";
	public static String USER_TOKEN = "";
	//public static String USER_ID= "jtasaki@sfl.08";
	//public static String USER_PW = "sfdcj";
	
	public static String SESSION_ID;
	
	public static String USER_ID_18DIGITS;
	public static String API_SERVER_URL;
	public static String API_META_DATA_SERVER_URL;
	
	public static boolean isLoaded = false;
	public static boolean isList = true;
//	public static boolean isDetail = false;
	
	public static final String SOAP_ACTION = "\"\"";
	public static final String LOGIN_SERVER_URL = "https://www.salesforce.com/services/Soap/c/14.0";
	public static final String NAMESPACE = "urn:enterprise.soap.sforce.com";	
	public static final String SOBJECT_PACKAGE_NAME = "com.android.salesforce.sobject.";
	public static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	
    public static ViewGroup MainContainer;
    
    public static String[] DOWNLOAD_SOBJECTS = { "Event", "Task", "Lead", "Account", "Contact", 
    		"Opportunity", "Case" /*, "ChartViewer", "BrowserViewer"*/};
    
    public static final String METADATA_NAMESPACE = "http://soap.sforce.com/2006/04/metadata";
	
    public static final String MASTER_RECORD_TYPE_ID = "012000000000000AAA";
    
    public static String vUrl = "";
    
}
