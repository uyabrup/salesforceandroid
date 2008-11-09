/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.app.AlertDialog;
import android.app.Dialog;

import org.ksoap2.serialization.KvmSerializable;

import com.android.R;
import com.android.animation.Rotate3dAnimation;

import com.android.salesforce.database.SObjectDataFactory;
import com.android.salesforce.operation.ApexApiCaller;
import com.android.salesforce.sobject.MainMenu;
import com.android.salesforce.util.SObjectDB;
import com.android.salesforce.util.StaticInformation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

/**
 * This class is a main Activity of Salesforce Android Application
 * 
 * @author Dai Odahara
 * 
 */
public class SalesforceAndroid extends Activity implements
		ViewSwitcher.ViewFactory {
	private static final String TAG = "SalesforceAndroid";
	public static final String KEY_TITLE = "title";
	public static final String KEY_BODY = "body";

	private final Handler handler = new Handler();
	private static SalesforceAndroid sa;
	private static StringBuffer table = new StringBuffer();;
	private static SObjectDataFactory ss = new SObjectDataFactory();
	private static ApexApiCaller bind = new ApexApiCaller();
	//private static ArrayList<ContentValues> cv = new ArrayList<ContentValues>();
	private static ViewFlipper VFlipper;
	private static TextView mSwitcher;
	private static EditText UserId;
	private static EditText UserPassword;
	private static EditText UserToken;
	
	private static boolean login = false;
	private static boolean initialProcess = false;
	private static Button loginButton;
	private static String SObject;
	private static String json;
    private ViewGroup mContainer;
    private static final int LOGIN = 0;
    private static final int REFRESH_DATA = 1;
    private static final int RESYNC = 2;
    private static final int HELP_MESSAGE = 0;
    
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		sa = this;
		setContentView(R.layout.login_menu);
 
		//StaticInformation.MainContainer = (ViewGroup) findViewById(R.id.container);

		// progress dialog
		// prog = new ProgressDialog(SalesforceAndroid.this);
		// setting spinner 
		// VFlipper = ((ViewFlipper) this.findViewById(R.id.flipper));
		// VFlipper.startFlipping();
        mContainer = (ViewGroup) findViewById(R.id.login_container);
        
		mSwitcher = (TextView) findViewById(R.id.switcher);
		//mSwitcher.setFactory(this);
		//mSwitcher.setBackgroundColor(0xFF000000);

		Animation in = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in);
		Animation out = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out);
		//mSwitcher.setInAnimation(in);
		//mSwitcher.setOutAnimation(out);
		mSwitcher.setVisibility(4);
		mSwitcher.setText("Authenticating...");
		
		/*
		 * Spinner s = (Spinner) findViewById(R.id.spinner); ArrayAdapter<String>
		 * adapter = new ArrayAdapter<String>(this,
		 * android.R.layout.simple_spinner_item, mStrings);
		 * adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 * s.setAdapter(adapter); s.setOnItemSelectedListener(this);
		 */

		/** setting help */
		TextView hv = (TextView)findViewById(R.id.salesforce_login_help);
		hv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
					showDialog(HELP_MESSAGE);
			}
		});
		
		/** setting user id/pw */
		UserId = (EditText) findViewById(R.id.salesforce_user_id);
		UserPassword = (EditText) findViewById(R.id.salesforce_user_password);
		UserToken = (EditText) findViewById(R.id.salesforce_user_token);
		//UserId.setText("android@vv.com");
		//UserPassword.setText("google12345");

		String[] it = readIdAndToken().split(":");
		String t = it[0] == null ? "" : it[0];
		UserId.setText(t);
		if(it.length >= 2){
			t = it[1] == null ? "" : it[1];
			UserToken.setText(t);
		}
		
		//UserToken.setText("vQhHjp0xgSWxpvpDdJR631FA");
		
		loginButton = (Button) findViewById(R.id.salesforce_login);
		loginButton.setAnimation(in);
		if(login)loginButton.setText(" Go ");
		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				 //login = true;
				
				if (login) {
					//applyRotation(0, 180);
					Log.v(TAG, "Already logged in...");
					// applyRotation(-1, 0, 90);

					Intent intent = new Intent();
					intent.putExtra("json", json);
					//intent.setClass(SalesforceAndroid.this, TabMenuMaker.class);
					intent.setClass(SalesforceAndroid.this, MainMenu.class);
					startActivity(intent);
				} else if(UserId.getText().toString().length() == 0 || UserPassword.getText().toString().length() == 0) {
					mSwitcher.setVisibility(0);
					mSwitcher.setText("Enter Id/Pw");
					return;
				} 
				else {

					StaticInformation.USER_ID = UserId.getText().toString();
					StaticInformation.USER_PW = UserPassword.getText().toString();
					String t = UserToken.getText().toString();
					StaticInformation.USER_TOKEN = t == null ? "" : t;					
					
					mSwitcher.setVisibility(0);
					Log.v(TAG, "Not logged in Yet...");
					loginButton.setClickable(false);
					
					processLogin();
					//loginButton.setFocusable(true);
				}

			}
		});

	}

	/** dialog override function */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        	case HELP_MESSAGE:
        		return makeHelpAlert();
        }
        return null;
    }
	
	/** help window maker */
	private AlertDialog makeHelpAlert() {
        return new AlertDialog.Builder(SalesforceAndroid.this)
        //.setIcon(R.drawable.alert_dialog_icon)
        .setTitle(R.string.alert_help_title)
        .setMessage(R.string.alert_help_message)
        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* User clicked OK so do some stuff */
            }
        })
        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* User clicked Cancel so do some stuff */
            }
        })
        .create();
	}
	
	
	/**
	 * Image View Factory.
	 * 
	 */
	private static class ImageView extends View {
		private Bitmap mBitmap;

		public ImageView(Context context) {
			super(context);
			setFocusable(true);

			java.io.InputStream is;
			is = context.getResources().openRawResource(R.drawable.salesforce);

			BitmapFactory.Options opts = new BitmapFactory.Options();
			Bitmap bm;

			opts.inJustDecodeBounds = true;
			bm = BitmapFactory.decodeStream(is, null, opts);

			// now opts.outWidth and opts.outHeight are the dimension of the
			// bitmap, even though bm is null

			opts.inJustDecodeBounds = false; // this will request the bm
			opts.inSampleSize = 16; // scaled down by 16
			bm = BitmapFactory.decodeStream(is, null, opts);

			mBitmap = bm;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(mBitmap, 10, 10, null);
		}
	}

	/**
	 * Setup a new 3D rotation on the container view.
	 * 
	 * @param position
	 *            the item that was clicked to show a picture, or -1 to show the
	 *            list
	 * @param start
	 *            the start angle at which the rotation must begin
	 * @param end
	 *            the end angle of the rotation
	 */
	private void applyRotation(float start, float end) {
		/** Find the center of the container */
		final float centerX = mContainer.getWidth() / 2.0f;
		final float centerY = mContainer.getHeight() / 2.0f;

		/**
		 * Create a new 3D rotation with the supplied parameter The animation
		 * listener is used to trigger the next animation
		 */
		final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
				centerX, centerY, 310.0f, true);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		//rotation.setAnimationListener(new MainMenu());

		mContainer.startAnimation(rotation);

	}

	/** Creating Menu */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
			
			//menu.add(0, 0, 0, R.string.label_salesforce_refresh);
			menu.add(0, 1, 0, R.string.label_salesforce_refresh);
			menu.add(0, 2, 0, R.string.label_salesforce_resync);
			//menu.add(0, 2, 0, "Logged");
			
		// menu.add(0, DELETE_ID, R.string.menu_delete);
		// menu.add(0, SFDC_LOGIN_ID, R.string.menu_sfdc_login);
		// menu.add(0, SFDC_ACCOUNT_ID, R.string.menu_sfdc_account);
		return true;
	}

	/** Menu on Display */
	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		
		Log.v(TAG, "id:" + featureId + "-item:" + item.getItemId());
		
		switch(item.getItemId()) {
			case LOGIN:
				if(UserId.getText().toString().length() != 0 && UserPassword.getText().toString().length() != 0) {
					mSwitcher.setVisibility(0);
					Log.v(TAG, "Not logged in Yet...");
					loginButton.setClickable(false);
					
					processLogin();
				}
			case REFRESH_DATA:
				
				refresh();
			case RESYNC:
				refresh();
				if(UserId.getText().toString().length() != 0 && UserPassword.getText().toString().length() != 0) {
					mSwitcher.setVisibility(0);
					Log.v(TAG, "Not logged in Yet...");
					loginButton.setClickable(false);
					
					processLogin();
				}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void refresh() {
		initialProcess = false;
		login = false;
		mSwitcher.setVisibility(4);
		mSwitcher.setText("Authenticating...");
		loginButton.setText("Login");
		
		SObjectDB.SOBJECT_DB.clear();
		SObjectDB.SOBJECTS.clear();
		SObjectDB.SOBJECT_USER_DB.clear();
		SObjectDB.WHERE_HOLDER.clear();
	}
	
	public View makeView() {
		TextView t = new TextView(this);
		// t.setLayoutParams(Alignment.ALIGN_CENTER);
		// t.setAlignment(Alignment.ALIGN_CENTER);
		t.setTextSize(18);
		return t;
	}

	public void stop() {

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(600);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent();
				//intent.setClass(SalesforceAndroid.this, TabMenuMaker.class);
				intent.setClass(SalesforceAndroid.this, MainMenu.class);
				startActivity(intent);

			}
		});
		t.start();
	}
	
	public void processLogin() {

		Thread t = new Thread(new Runnable() {
			public void run() {
				Looper.prepare();

				handler.post(new Runnable() {
					public void run() {

					}
				});
				
				ss.open(sa);
				boolean success = false;
				// Login Call
				if(!(login=login())){
					handler.post(new Runnable() {
						public void run() {
							mSwitcher.setText("Login Fault\nCheck ID, PW, Grant IP Addresses...");
							loginButton.setText("Retry");
							loginButton.setClickable(true);
						}
					});
					return;
				}
				
				/**
				if(!isActive()){
					handler.post(new Runnable() {
						public void run() {
							mSwitcher.setText("You're not allowed to use now...");
							loginButton.setText("Retry");
							loginButton.setClickable(true);
						}
					});
					return;
				}
				*/
				
				// Create Keyprefix x Sobject createKeyprefixSObject
				anaylizeKeyPreFix();
				
				// call metadata in advnace
				//String mresult = retrieveMetaData();
				
				
				// DescribeSObject
				describeSObject("Event");
				
				// English org record type
				String rds;	
				rds = StaticInformation.MASTER_RECORD_TYPE_ID;
				//rds = "012400000005OcI"; //event
				bind.describeLayout("Event", rds);
				
				describeSObject("Task");
				//rds = "012400000005OcN"; //task
				bind.describeLayout("Task", rds);
				
				// Opportunity
				describeSObject("Opportunity");
				//rds = "012400000005NZa"; 
				bind.describeLayout("Opportunity", rds);
				
				// Case
				describeSObject("Case");
				//rds = "012400000005OnC";
				bind.describeLayout("Case", rds);
				
				// Contact
				describeSObject("Contact");
				//rds = "012400000005OmT";
				bind.describeLayout("Contact", rds);

				// Lead
				describeSObject("Lead");
				//rds = "012400000005On7";
				bind.describeLayout("Lead", rds);
				
				// Account
				describeSObject("Account");
				//rds = "012400000005NXt";
				bind.describeLayout("Account", rds);
				
				describeSObject("User");
					
				
				// query
				
				query("Event");
				query("Task");
				query("Lead");
				query("Opportunity");
				query("Case");
				query("Contact");				
				query("Account");
				
				// user query
				//query("User");
				
				bind.queryUser();
				
				/*
				String id = checkStatus(mresult);
				String zip = checkRetrieveStatus(id);
				writeData(zip);
				unZip("data/data/com.android/files/data5.zip");
				Log.v(TAG, "Unziping zipfile");
				//readFile("data/data/com.android/files/reports_SFA_OpportunityByPhase.report");
				json = readXmlFileAsJson("data/data/com.android/files/dashboards_Folder_OpportunityDashboard.dashboard");
				*/
				
				saveIdAndToken();
				
				ss.close();
				// proccess after calling api

				handler.post(new Runnable() {
					public void run() {
						mSwitcher.setText("Login Success");
						loginButton.setText(" Go ");
						loginButton.setClickable(true);
					}
				});
				Looper.loop();

			};
		});
		t.start();
	}
	
	// describeSobject caller
	private void describeSObject(String sobject) {
		SObject = sobject;

		table = bind.describeSOject(sobject);
		Log.v(TAG, sobject+ " table:" + table.toString());
		handler.post(new Runnable() {
			public void run() {
				mSwitcher.setText("Loading " + SObject + " Layout...");
			}
		});
		ss.create(sa, table.toString(), sobject);

	}

	// metadata retrieve caller
	private String retrieveMetaData() {
		Log.v(TAG, "retrieve Metadata...");
		String ret = bind.retrieveMetaData();
		return ret;
	}
	
	// metadata retrieve caller
	private String checkStatus(String result) {
		Log.v(TAG, "checkStatus of Metadata...");
		String id = bind.checkStatus(result);
		return id;
	}
	
	// metadata retrieve caller
	private String  checkRetrieveStatus(String id) {
		Log.v(TAG, "checkRetrieveStatus of Metadata...");
		String zip = bind.checkRetrieveStatus(id);
		return zip;
	}
	
	// write data
	private void writeData(String data) {
		Log.v(TAG, "Writing data into local file...");
		ss.write(sa, data);
	}
	
	/**
	 * unzip file of zipfile and copy the fils in other directory
	 * @param fileName
	 */
	private void unZip(String fileName) {
		Log.v(TAG, "unzipping file...");
		ss.unZip(fileName);
	}

	/** read file of give file
	 * 
	 * @param fileName
	 */
	private String readXmlFileAsJson(String fileName) {
		Log.v(TAG, "reading xml file as json...");
		String ret = bind.readFileAsStream(fileName);
		return ret;
	}
	/** read file of give file
	 * 
	 * @param fileName
	 */
	private void readFile(String fileName) {
	      BufferedReader br = null;
		try {
			  //String tfn = fileName.replaceAll("/", "_");
		      br = new BufferedReader(new FileReader(fileName));
		      String msg = "";
		      StringBuffer sb = new StringBuffer();
		      while(null != (msg = br.readLine())) {
		    	  Log.v(TAG, msg);
		    	  sb.append(msg.trim());
		      }
		      
		      Pattern pattern = Pattern.compile(".*.report");
		      Matcher matcher = pattern.matcher(fileName);
		      if(matcher.matches())ss.parseReportXML(sb.toString());

		      pattern = Pattern.compile(".*.dashboard");
		      matcher = pattern.matcher(fileName);
		      if(matcher.matches())ss.parseDashboardXML(sb.toString());

		      ss.parseReportXML(sb.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				br.close();
			} catch(IOException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			} 
		}
	}
	
	// save id and token
	private void saveIdAndToken() {
		Log.v(TAG, "saving id and token...");
		String id = UserId.getText().toString();
		String token = UserToken.getText().toString();
		String t = token == null ? "" : token;
		ss.saveIdAndToken(id, t, sa);
	}
	
	// read id and token
	private String readIdAndToken(){
		Log.v(TAG, "reading id and token...");
		return ss.readIdAndToken(sa);
	}
	
	// login caller
	private boolean login(){
		handler.post(new Runnable() {
			public void run() {
				mSwitcher.setText("Authenticating...");
			}
		});

		Log.v(TAG, "Login Result : " + login);

		if(bind.login())return true;
		else return false;
	}
	
	// check is active
	private boolean isActive(){
		boolean isActive = bind.checkIsActive();
		Log.v(TAG, "User Activeness : " + isActive);
		
		if(isActive)return true;
		
		handler.post(new Runnable() {
			public void run() {
				mSwitcher.setText("User is inActive...");
			}
		});
		return false;
	}
	
	// analize key prefix
	private void anaylizeKeyPreFix() {
		Log.v(TAG, "Creating Keyprefix x Sobject Table...");

		ss.createKeyprefixSObject(sa);
	}
	
	// describeLayout caller
	private void describeLayout(String sobject) {
		
		//bind.describeLayout(sobject, rds);
	}
	
	// query caller
	private void query(String sobject){
		SObject = sobject;
		ArrayList<ContentValues> cv = bind.query(sobject);
		handler.post(new Runnable() {
			public void run() {
					mSwitcher.setText("Querying " + SObject + "...");
			}
		});
		
		/** this insert seems to cause an illegalstatement exception */
		/**
		for(ContentValues c : cv) {
			ss.insert(c, sobject);
		}
		*/
	}
	
}