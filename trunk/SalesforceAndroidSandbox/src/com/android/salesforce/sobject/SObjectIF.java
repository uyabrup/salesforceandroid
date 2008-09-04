/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.sobject;

import java.lang.reflect.Field;

/**
 * This class is interface for SObjectImpl
 * @author Dai Odahara
 *
 */
public interface SObjectIF {
	public Field[] getFields();
}
