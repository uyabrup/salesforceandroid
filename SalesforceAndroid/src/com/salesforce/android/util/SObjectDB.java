/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.salesforce.android.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.salesforce.android.frame.LayoutHolder;
import com.salesforce.android.frame.SectionHolder;

/**
 * This class is temporary DB. This will be replaced...
 * 
 * @author Dai Odahara
 * 
 */
public class SObjectDB {
	/** */
	public static HashMap<String,LayoutHolder> SOBJECTS = new HashMap<String,LayoutHolder>();
	public static HashMap<String, HashMap<String, HashMap>> SOBJECT_DB = new HashMap<String, HashMap<String, HashMap>>();
	public static HashMap<String, HashMap<String, String>> SOBJECT_USER_DB = new HashMap<String, HashMap<String, String>>();
	public static HashMap<String, StringBuffer> WHERE_HOLDER = new HashMap<String, StringBuffer>();
	public static HashMap<String, String> KEYPREFIX_SOBJECT = new HashMap<String, String>();
	public static HashMap<String, String> SOBJECT_NAME_LABEL = new HashMap<String, String>();
}
