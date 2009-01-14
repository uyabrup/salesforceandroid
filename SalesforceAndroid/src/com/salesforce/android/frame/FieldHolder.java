/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.salesforce.android.frame;

import java.util.Comparator;

/**
 * This class is responsible for managing fields information by Hitting describeLayout.
 * 
 * @author Dai Odahara
 * 
 */
public class FieldHolder implements Comparator {
	public boolean editable;
	public String label;
	public int tabOrder;
	public String type;
	public String value;
	public boolean required;
	public boolean placeholder;
	public int desplayLines;
	
	@Override
	public int compare(Object o1, Object o2) {
	    return ((FieldHolder)o2).tabOrder - ((FieldHolder)o1).tabOrder;
	}
}
