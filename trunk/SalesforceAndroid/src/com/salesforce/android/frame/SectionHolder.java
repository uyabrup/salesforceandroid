package com.salesforce.android.frame;

import java.util.ArrayList;
/**
 * This class is responsible for managing section information of layout by Hitting describeLayout.
 * 
 * @author Dai Odahara
 * 
 */
public class SectionHolder {
	public int sectionOrder;
	public String name;
	public ArrayList<FieldHolder> fields;// = new ArrayList<FieldHolder>();
	public ArrayList<FieldHolder> related;// = new ArrayList<FieldHolder>();

}
