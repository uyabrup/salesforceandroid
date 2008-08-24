/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.test;

import android.content.Context;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.*;

//Referenced classes of package android.widget:
//         BaseAdapter, TextView, Filterable, Filter

public class ExtendedArrayAdapter extends BaseAdapter
 implements Filterable
{
 private class ArrayFilter extends Filter
 {

     protected Filter.FilterResults performFiltering(CharSequence prefix)
     {
         Filter.FilterResults results = new Filter.FilterResults();
         if(mOriginalValues == null)
             synchronized(lock)
             {
                 mOriginalValues = new ArrayList(mObjects);
             }
         if(prefix == null || prefix.length() == 0)
         {
             synchronized(lock)
             {
                 ArrayList list = new ArrayList(mOriginalValues);
                 results.values = list;
                 results.count = list.size();
             }
         } else
         {
             String prefixString = prefix.toString().toLowerCase();
             ArrayList values = mOriginalValues;
             int count = values.size();
             ArrayList newValues = new ArrayList(count);
             for(int i = 0; i < count; i++)
             {
                 Object value = values.get(i);
                 if(value != null && value.toString().toLowerCase().startsWith(prefixString))
                     newValues.add(value);
             }

             results.values = newValues;
             results.count = newValues.size();
         }
         return results;
     }

     protected void publishResults(CharSequence constraint, Filter.FilterResults results)
     {
         mObjects = (List)results.values;
         if(results.count > 0)
             notifyDataSetChanged();
         else
             notifyDataSetInvalidated();
     }

     final ExtendedArrayAdapter this$0;

     private ArrayFilter()
     {
         super();
    	 this$0 = ExtendedArrayAdapter.this;

     }

 }


 public ExtendedArrayAdapter(Context context, int resource)
 {
     lock = new Object();
     mFieldId = -2147483648;
     init(context, resource, -2147483648, new ArrayList());
 }

 public ExtendedArrayAdapter(Context context, int resource, int fieldId)
 {
     lock = new Object();
     mFieldId = -2147483648;
     init(context, resource, fieldId, new ArrayList());
 }

 public ExtendedArrayAdapter(Context context, int resource, Object objects[])
 {
     lock = new Object();
     mFieldId = -2147483648;
     init(context, resource, -2147483648, Arrays.asList(objects));
 }

 public ExtendedArrayAdapter(Context context, int resource, int fieldId, Object objects[])
 {
     lock = new Object();
     mFieldId = -2147483648;
     init(context, resource, fieldId, Arrays.asList(objects));
 }

 public ExtendedArrayAdapter(Context context, int resource, List objects)
 {
     lock = new Object();
     mFieldId = -2147483648;
     init(context, resource, -2147483648, objects);
 }

 public ExtendedArrayAdapter(Context context, int resource, int fieldId, List objects)
 {
     lock = new Object();
     mFieldId = -2147483648;
     init(context, resource, fieldId, objects);
 }

 public void addObject(Object object)
 {
     if(mOriginalValues != null)
     {
         synchronized(lock)
         {
             mOriginalValues.add(object);
             notifyDataSetChanged();
         }
     } else
     {
         mObjects.add(object);
         notifyDataSetChanged();
     }
 }

 public void insertObject(Object object, int index)
 {
     if(mOriginalValues != null)
     {
         synchronized(lock)
         {
             mOriginalValues.add(index, object);
             notifyDataSetChanged();
         }
     } else
     {
         mObjects.add(index, object);
         notifyDataSetChanged();
     }
 }

 public void removeObject(Object object)
 {
     if(mOriginalValues != null)
         synchronized(lock)
         {
             mOriginalValues.remove(object);
         }
     else
         mObjects.remove(object);
     notifyDataSetChanged();
 }

 private void init(Context context, int resource, int fieldId, List objects)
 {
     mContext = context;
     mInflate = (LayoutInflater)context.getSystemService("inflate");
     mResource = mDropDownResource = resource;
     mObjects = objects;
     mFieldId = fieldId;
 }

 public Context getContext()
 {
     return mContext;
 }

 public int getCount()
 {
     return mObjects.size();
 }

 public Object getItem(int position)
 {
     return mObjects.get(position);
 }

 public int getPosition(Object item)
 {
     return mObjects.indexOf(item);
 }

 public long getItemId(int position)
 {
     return (long)position;
 }

 public View getMeasurementView(ViewGroup parent)
 {
     int count = mObjects.size();
     int position = 0;
     if(count < 20)
     {
         int maxLength = 0;
         for(int i = 0; i < count; i++)
         {
             Object item = mObjects.get(i);
             if(item == null)
                 continue;
             String content = item.toString();
             int currentLength = content.length();
             if(currentLength > maxLength)
             {
                 maxLength = currentLength;
                 position = i;
             }
         }

     }
     return getView(position, null, parent);
 }

 public View getView(int position, View convertView, ViewGroup parent)
 {
     return createViewFromResource(position, convertView, parent, mResource);
 }

 private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource)
 {
     View view;
     if(convertView == null)
         view = mInflate.inflate(resource, parent, false);
     else
         view = convertView;
     if(!(view instanceof TextView))return view;
     TextView text;
     if(mFieldId == -2147483648)
         text = (TextView)view;
     else
         text = (TextView)view.findViewById(mFieldId);
     text.setText(getItem(position).toString());
     return view;
 }

 public void setDropDownViewResource(int resource)
 {
     mDropDownResource = resource;
 }

 public View getDropDownView(int position, View convertView, ViewGroup parent)
 {
     return createViewFromResource(position, convertView, parent, mDropDownResource);
 }

 public int getNewSelectionForKey(int currentSelection, int key, KeyEvent event)
 {
     return -2147483648;
 }

 public static ExtendedArrayAdapter createFromResource(Context context, int textArrayResId, int textViewResId)
 {
     CharSequence strings[] = context.getResources().getTextArray(textArrayResId);
     return new ExtendedArrayAdapter(context, textViewResId, strings);
 }

 public Filter getFilter()
 {
     if(mFilter == null)
         mFilter = new ArrayFilter();
     return mFilter;
 }

 private List mObjects;
 private Context mContext;
 private LayoutInflater mInflate;
 private int mResource;
 private ArrayFilter mFilter;
 private ArrayList mOriginalValues;
 private int mDropDownResource;
 private final Object lock;
 protected int mFieldId;





}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\Application\Android\android-sdk_m5-rc15_windows\android-sdk_m5-rc15_windows\android.jar
	Total time: 15 ms
	Jad reported messages/errors:
Overlapped try statements detected. Not all exception handlers will be resolved in the method performFiltering
Overlapped try statements detected. Not all exception handlers will be resolved in the method addObject
Overlapped try statements detected. Not all exception handlers will be resolved in the method insertObject
Overlapped try statements detected. Not all exception handlers will be resolved in the method removeObject
	Exit status: 0
	Caught exceptions:
*/