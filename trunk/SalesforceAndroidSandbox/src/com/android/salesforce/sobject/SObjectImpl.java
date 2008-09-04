/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.sobject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import com.android.salesforce.util.StaticInformation;;


public class SObjectImpl {
	public Object so;
	public Map<String, String> layoutAndvalue;
	
	public SObjectImpl() {}
	
	public SObjectImpl(String sobjectName, ArrayList qf, Map<String, String> vMap) {
		
		try {
			Class cl = Class.forName(StaticInformation.SOBJECT_PACKAGE_NAME + sobjectName);
			so = cl.newInstance();

			SObjectIF sif = (SObjectIF)so;
			
			Field[] f = sif.getFields();
			for(int i = 0; i < f.length; i++) {
				String fn = f[i].getName();
				if(qf.contains(fn)) {
					f[i].setAccessible(true);
					f[i].set(fn, vMap.get(fn));
				}
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
	}
}
