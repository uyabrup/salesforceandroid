/**
 * Copyright (C) 2008 Dai Odahara.
 */
package com.android.salesforce.database;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.kobjects.base64.Base64;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.android.salesforce.util.StaticInformation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * This class is responsible for managing SQLite. At present, it does simply insert/update/delte.
 * 
 * TODO handle salesforce object relations
 * 
 * @author Dai Odahara
 * 
 */
public class SObjectDataFactory extends Activity {
	private static final String TAG = "SOjbectSQLite";
	private static final String DB_NAME = "SObject";
	private static final int DB_VERSION = 1;
	
	private static SQLiteDatabase db = null;
	private static final String NOTHING = "NOTHING";
	
	public SObjectDataFactory(){};
	
	/** create table */
	public void create (Context context, String table, String sobject) {
		try {

			//db = context.openOrCreateDatabase(DB_NAME, DB_VERSION, null);
			showErrorAsDialog(table);
			db.execSQL("drop table if exists " + sobject);
			db.execSQL(table);
		} catch (Exception ex) {
			//db = null;
			Log.v(TAG, ex.toString());
		}
	}
	
	/** create table */
	public void createKeyprefixSObject (Context context) {
		try {
			String tableName = "KeyPrefix_SObject";
			String table = "create table " + tableName + " (" 
				+ "keyPrefix text(" + StaticInformation.SOBJECT_PREFIX_SIZE + ") not null primary key, "
				+ "SObject text not null"
				+ ");";
			
			//db = context.openOrCreateDatabase(DB_NAME, DB_VERSION, null);
			db.execSQL("drop table if exists " + tableName);
			db.execSQL(table);
		} catch (Exception ex) {
			//db = null;
			Log.v(TAG, ex.toString());
		}
	}
	
	/** create table */
	public long insert(ContentValues insertData, String table) {
		Log.v(TAG, "Inserted:" + insertData.toString());
		return db.insert(table, null, insertData);
	}
	
	/** update table */
	public int update(ContentValues updateData, String table, String rowId){
		return db.update(table, updateData, "rowid = " + rowId, null);
	}
	
	/** read colmun - test */
	/**
	public List<Schedule> selectAll(String table) {
		List<Schedule> result = new ArrayList<Schedule>();
		Cursor cursor = db.query(table, null, null, null, null, null, "Name");
		while(cursor.moveToNext()) {
			Schedule s = new Schedule();
			s.Id = cursor.getString(0);
			s.Name = cursor.getString(1);
			s.Importance = cursor.getInt(2);
			result.add(s);
		}
		return result;
	}
	*/
	
	/** open db */
	public void open(Context context) {
		if(null == db || !db.isOpen())db = context.openOrCreateDatabase(DB_NAME, DB_VERSION, null);
	}
	
	/** close the db */
	public void close() {
		if(null != db)db.close();
	}
	
	/** show detail error message */
	private void showErrorAsDialog(String msg) {
		new AlertDialog.Builder(SObjectDataFactory.this)
        .setMessage(msg)
        .show();
	}
	
	/** write data into local file */
	public void write(Context context, String data){
		FileOutputStream fos = null;
		byte[] outdata = Base64.decode(data);
		
		try {
			fos = context.openFileOutput("data5.zip", MODE_WORLD_WRITEABLE);
			fos.write(outdata);
			fos.close();
		} catch (Exception ex) {
			Log.v(TAG, ex.toString());
		} finally {
			try {
				fos.close();				
			} catch(IOException ex) {
				ex.printStackTrace();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	

	/**
	 * unzip file of zipfile and copy the fils in other directory
	 * @param fileName
	 */
	public void unZip(String fileName) {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ZipInputStream zis = null;
		ZipEntry zent = null;
		BufferedOutputStream out = null;
		
		try {
			fis = new FileInputStream(fileName);		
			bis = new BufferedInputStream(fis);
			zis = new ZipInputStream(bis);
			String sep = System.getProperty("file.separator");
			
            int data = 0;    
            
			// read file info in zip file
			while ((zent = zis.getNextEntry()) != null) {
				Log.v(TAG, zent.toString());
				
				if(zent.isDirectory())continue;
                
                String tfn = zent.getName().replaceAll(sep, "_");
                
                new File("data/data/com.android/files/" + tfn);                
                out = new BufferedOutputStream(new FileOutputStream("data/data/com.android/files/" + tfn));
				while( (data = zis.read()) != -1 )
                {
                      out.write(data);
                }
    			out.close();
    			zis.closeEntry();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				zis.close();
				bis.close();
				fis.close();
			} catch (IOException ex) {
				Log.v(TAG, ex.toString());
			} catch(Exception ex) {
				Log.v(TAG, ex.toString());
			}
		}
	}

	/**
	 * parse xml value of report file 
	 * @param xml
	 */
	public void parseReportXML(String xml) {
        StringBuffer fields = new StringBuffer();
        String ename = "";
        HashMap<String, HashSet<String>> hh = new HashMap<String, HashSet<String>>();
		try {
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	
	        xpp.setInput( new StringReader ( xml ) );

	        int eventType = xpp.getEventType();
	        int fb = 0;
	        while (eventType != XmlPullParser.END_DOCUMENT) {
		         if(eventType == XmlPullParser.START_DOCUMENT) {
		            // Log.v(TAG, "Start document");
		         } else if(eventType == XmlPullParser.END_DOCUMENT) {
		        	 //Log.v(TAG, "End document");
		         } else if(eventType == XmlPullParser.START_TAG) {
		        	 if (xpp.getName().equals("columns")) {fb = 2; ename = "columns";}
		        	 else if (xpp.getName().equals("field") && fb == 2) {fb = (fb | 1); ename = "columns";}		        	 
		        	 else if (xpp.getName().equals("sortOrder")) {fb = 1; ename = "sortOrder";}
		        	 else if (xpp.getName().equals("sortColumn")){fb = 1; ename = "sortColumn";}
		        	 else if (xpp.getName().equals("reportType")){fb = 1; ename = "reportType";}
		        	 else if (xpp.getName().equals("dateColumn")){fb = 1; ename = "dateColumn";}
		        	 else if (xpp.getName().equals("interval")){fb = 1; ename = "interval";}
		        	 else if (xpp.getName().equals("startDate")){fb = 1; ename = "startDate";}
		        	 else if (xpp.getName().equals("endDate")){fb = 1; ename = "endDate";}
		        	 
		        	 
		        	 else fb = 0;
		        	 
		        	// Log.v(TAG, "<"+xpp.getName());
		         } else if(eventType == XmlPullParser.END_TAG) {
		        	// Log.v(TAG, "</"+xpp.getName());
		         } else if(eventType == XmlPullParser.TEXT && 0 != fb
		        		 && xpp.getText() != null && xpp.getText().length() != 0) {
		        	// Log.v(TAG, "-" + xpp.getText() + "-");
		        	 if(hh.get(ename) == null) {
		        		 HashSet<String> hs = new HashSet<String>();
		        		 hs.add(xpp.getText());
		        		 hh.put(ename, hs);	        			 
		        	 } else {
		        		 HashSet<String> hs = hh.get(ename);
		        		 hs.add(xpp.getText());
		        		 hh.put(ename, hs);
		        	 }
		         }
		         eventType = xpp.next();
	        }
	        
	        for(HashMap.Entry<String, HashSet<String>> e : hh.entrySet()) {
	            Log.v(TAG, e.getKey());
	            Iterator<String> it = e.getValue().iterator();
	            while(it.hasNext()) {
		            Log.v(TAG, "\t" + it.next() );
	            }
	        }

	        
		}catch(XmlPullParserException ex) {
			Log.v(TAG, ex.toString());
		}catch(IOException ex) {
			Log.v(TAG, ex.toString());
		}

	}

	/**
	 * parse xml value of report file 
	 * @param xml
	 */
	public void parseDashboardXML(String xml) {
        StringBuffer fields = new StringBuffer();
        String ename = "";
        HashMap<String, HashSet<String>> hh = new HashMap<String, HashSet<String>>();
		try {
	        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	
	        xpp.setInput( new StringReader ( xml ) );

	        int eventType = xpp.getEventType();
	        int fb = 0;
	        while (eventType != XmlPullParser.END_DOCUMENT) {
		         if(eventType == XmlPullParser.START_DOCUMENT) {
		            // Log.v(TAG, "Start document");
		         } else if(eventType == XmlPullParser.END_DOCUMENT) {
		        	 //Log.v(TAG, "End document");
		         } else if(eventType == XmlPullParser.START_TAG) {
		        	 if (xpp.getName().equals("middleSection")) {fb = 1; ename = "middleSection";}
		        	 else if (xpp.getName().equals("components") && fb == 1) {fb = 2; ename = "components";}		        	 
		        	 else if (fb == 2) {ename = xpp.getName();}
		 
		        	 
		        	 
		        	 else if (xpp.getName().equals("sortColumn")){fb = 1; ename = "sortColumn";}
		        	 else if (xpp.getName().equals("reportType")){fb = 1; ename = "reportType";}
		        	 else if (xpp.getName().equals("dateColumn")){fb = 1; ename = "dateColumn";}
		        	 else if (xpp.getName().equals("interval")){fb = 1; ename = "interval";}
		        	 else if (xpp.getName().equals("startDate")){fb = 1; ename = "startDate";}
		        	 else if (xpp.getName().equals("endDate")){fb = 1; ename = "endDate";}
		        	 
		        	 
		        	 else fb = 0;
		        	 
		        	// Log.v(TAG, "<"+xpp.getName());
		         } else if(eventType == XmlPullParser.END_TAG) {
		        	// Log.v(TAG, "</"+xpp.getName());
		         } else if(eventType == XmlPullParser.TEXT && 0 != fb
		        		 && xpp.getText() != null && xpp.getText().length() != 0) {
		        	// Log.v(TAG, "-" + xpp.getText() + "-");
		        	 if(hh.get(ename) == null) {
		        		 HashSet<String> hs = new HashSet<String>();
		        		 hs.add(xpp.getText());
		        		 hh.put(ename, hs);	        			 
		        	 } else {
		        		 HashSet<String> hs = hh.get(ename);
		        		 hs.add(xpp.getText());
		        		 hh.put(ename, hs);
		        	 }
		         }
		         eventType = xpp.next();
	        }
	        
	        for(HashMap.Entry<String, HashSet<String>> e : hh.entrySet()) {
	            Log.v(TAG, e.getKey());
	            Iterator<String> it = e.getValue().iterator();
	            while(it.hasNext()) {
		            Log.v(TAG, "\t" + it.next() );
	            }
	        }

	        
		}catch(XmlPullParserException ex) {
			Log.v(TAG, ex.toString());
		}catch(IOException ex) {
			Log.v(TAG, ex.toString());
		}

	}

	// save id and token when finishing login with success
	public void saveIdAndToken(String id, String token, Context context) {
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		
		try {
			fos = context.openFileOutput("idandtoken.txt", 0);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write(id);
			bw.write(System.getProperty("line.separator"));
			bw.write(token);
			//bw.write(System.getProperty("line.separator"));
			bw.flush();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				bw.close();
				fos.close();
			} catch (IOException ex) {
				Log.v(TAG, ex.toString());
			} catch(Exception ex) {
				Log.v(TAG, ex.toString());
			}
		}
	}
	
	// save id and token when finishing login with success
	public String readIdAndToken(Context context) {
		FileInputStream fis = null;
		BufferedReader br = null;
		StringBuffer ret = new StringBuffer();
		try {
			fis = context.openFileInput("idandtoken.txt");
			br = new BufferedReader(new InputStreamReader(fis));
			String t = br.readLine();
			t = t == null ? "" : t;
			ret.append(t).append(":");
			t = br.readLine();
			t = t == null ? "" : t;			
			ret.append(t);
			
		} catch (FileNotFoundException ex) {
			return "";
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				br.close();
				fis.close();
			} catch (IOException ex) {
				Log.v(TAG, ex.toString());
			} catch(Exception ex) {
				Log.v(TAG, ex.toString());
			}
		}
		return ret.toString();
	}
	
}