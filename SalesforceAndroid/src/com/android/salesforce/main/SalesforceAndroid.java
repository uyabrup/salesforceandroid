/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.main;

import java.util.ArrayList;

import com.android.R;
import com.android.animation.Rotate3dAnimation;

import com.android.salesforce.database.SObjectSQLite;
import com.android.salesforce.operation.ApexApiCaller;
import com.android.salesforce.sobject.MainMenu;
import com.android.salesforce.util.SObjectDB;
import com.android.salesforce.util.StaticInformation;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
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
	private static SObjectSQLite ss = new SObjectSQLite();
	private static ApexApiCaller bind = new ApexApiCaller();
	//private static ArrayList<ContentValues> cv = new ArrayList<ContentValues>();
	private static ViewFlipper VFlipper;
	private static TextView mSwitcher;
	private static EditText UserId;
	private static EditText UserPassword;
	private static boolean login = false;
	private static boolean initialProcess = false;
	private static Button loginButton;
	private static String SObject;
    private ViewGroup mContainer;
    
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

		/** setting user id/pw */
		UserId = (EditText) findViewById(R.id.salesforce_user_id);
		UserPassword = (EditText) findViewById(R.id.salesforce_user_password);

		UserId.setText("android@vv.com");
		UserPassword.setText("google12345");

		loginButton = (Button) findViewById(R.id.salesforce_login);
		loginButton.setAnimation(in);

		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				 //login = true;
				if (login) {
					//applyRotation(0, 180);
					Log.v(TAG, "Already logged in...");
					// applyRotation(-1, 0, 90);

					Intent intent = new Intent();
					//intent.setClass(SalesforceAndroid.this, TabMenuMaker.class);
					intent.setClass(SalesforceAndroid.this, MainMenu.class);
					startActivity(intent);
				} else {

					StaticInformation.USER_ID = UserId.getText().toString();
					StaticInformation.USER_PW = UserPassword.getText().toString();
					
					mSwitcher.setVisibility(0);
					Log.v(TAG, "Not logged in Yet...");
					loginButton.setClickable(false);
					
					processLogin();
					//loginButton.setFocusable(true);
				}

			}
		});

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
			menu.add(R.string.label_salesforce_refresh);
		// menu.add(0, DELETE_ID, R.string.menu_delete);
		// menu.add(0, SFDC_LOGIN_ID, R.string.menu_sfdc_login);
		// menu.add(0, SFDC_ACCOUNT_ID, R.string.menu_sfdc_account);
		return true;
	}

	/** Menu on Display */
	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		
		initialProcess = false;
		login = false;
		mSwitcher.setVisibility(4);
		mSwitcher.setText("Authenticating...");
		loginButton.setText("Login");
		
		SObjectDB.SOBJECT_DB.clear();
		SObjectDB.SOBJECTS.clear();
		SObjectDB.SYSTEM_DB.clear();
		SObjectDB.WHERE_HOLDER.clear();

		return super.onMenuItemSelected(featureId, item);
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
				// Login Call
				login();
				
				// Create Keyprefix x Sobject createKeyprefixSObject
				anaylizeKeyPreFix();
				
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
				
				ss.close();
				// proccess after calling api
				//initialProcess = true;
				handler.post(new Runnable() {
					public void run() {
						//loginButton.setFocusable(true);
					}
				});

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

	// login caller
	private void login(){
		handler.post(new Runnable() {
			public void run() {
				mSwitcher.setText("Authenticating...");
			}
		});
		login = bind.login();
		Log.v(TAG, "Login Result : " + login);
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