/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.main;

import com.android.R;
import com.android.animation.Rotate3dAnimation;
import com.android.salesforce.frame.TabMenuMaker;
import com.android.salesforce.operation.ApexApiCaller;
import com.android.salesforce.util.StaticInformation;

import android.app.Activity;
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
	private static ViewFlipper VFlipper;
	private static TextSwitcher mSwitcher;
	private static EditText UserId;
	private static EditText UserPassword;
	private static boolean login = false;
	private static boolean initialProcess = false;
	private static Button loginButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

		StaticInformation.MainContainer = (ViewGroup) findViewById(R.id.container);

		// progress dialog
		// prog = new ProgressDialog(SalesforceAndroid.this);
		// setting spinner 
		// VFlipper = ((ViewFlipper) this.findViewById(R.id.flipper));
		// VFlipper.startFlipping();
		
		mSwitcher = (TextSwitcher) findViewById(R.id.switcher);
		mSwitcher.setFactory(this);

		Animation in = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in);
		Animation out = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out);
		mSwitcher.setInAnimation(in);
		mSwitcher.setOutAnimation(out);
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
		UserId.setText("demo id/pw is hard coded");
		UserPassword.setText("So Click 'Login'!");

		loginButton = (Button) findViewById(R.id.salesforce_login);

		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// initialProcess = true;
				if (initialProcess) {
					Log.v(TAG, "Already logged in...");
					// applyRotation(-1, 0, 90);

					Intent intent = new Intent();
					intent.setClass(SalesforceAndroid.this, TabMenuMaker.class);
					startActivity(intent);
				} else {

					mSwitcher.setVisibility(0);
					Log.v(TAG, "Not logged in Yet...");

					processLogin();

				}

				/*
				 * Toast.makeText(SalesforceAcessor.this,
				 * R.string.message_while_login, Toast.LENGTH_LONG).show();
				 */

			}
		});

		// setContentView(new ImageView(this));
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
	private void applyRotation(int position, float start, float end) {
		/** Find the center of the container */
		final float centerX = StaticInformation.MainContainer.getWidth() / 2.0f;
		final float centerY = StaticInformation.MainContainer.getHeight() / 2.0f;

		/**
		 * Create a new 3D rotation with the supplied parameter The animation
		 * listener is used to trigger the next animation
		 */
		final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
				centerX, centerY, 310.0f, true);
		rotation.setDuration(700);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		// rotation.setAnimationListener(new SObjectList(position));

		StaticInformation.MainContainer.startAnimation(rotation);

	}

	/** Creating Menu */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// menu.add(0, INSERT_ID, R.string.menu_insert);
		// menu.add(0, DELETE_ID, R.string.menu_delete);
		// menu.add(0, SFDC_LOGIN_ID, R.string.menu_sfdc_login);
		// menu.add(0, SFDC_ACCOUNT_ID, R.string.menu_sfdc_account);
		return true;
	}

	/** Menu on Display */
	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		/*
		 * switch(item.getItemId()) {
		 * 
		 * case INSERT_ID: createNote(); return true; case DELETE_ID:
		 * mDbHelper.deleteNote(getListView().getSelectedItemId()); fillData();
		 * return true;
		 * 
		 * case SFDC_LOGIN_ID: return true;
		 *  }
		 */
		return super.onMenuItemSelected(featureId, item);
	}

	public View makeView() {
		TextView t = new TextView(this);
		// t.setLayoutParams(Alignment.ALIGN_CENTER);
		// t.setAlignment(Alignment.ALIGN_CENTER);
		t.setTextSize(18);
		return t;
	}

	public void processLogin() {

		Thread t = new Thread(new Runnable() {
			public void run() {
				Looper.prepare();

				handler.post(new Runnable() {
					public void run() {
						loginButton.setFocusable(false);
					}
				});

				ApexApiCaller bind = new ApexApiCaller();
				login = bind.login();
				Log.v(TAG, "Login Result : " + login);

				bind.describe("Account");
				handler.post(new Runnable() {
					public void run() {
						mSwitcher.setText("Loading Data...");
					}
				});
				
				bind.query("Account");
				handler.post(new Runnable() {
					public void run() {
						mSwitcher.setText("Login Success");
						loginButton.setText(" Go ");
					}
				});

				initialProcess = true;
				handler.post(new Runnable() {
					public void run() {
						loginButton.setFocusable(true);
					}
				});
				Looper.loop();

			};
		});
		t.start();
	}

}