/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.main;
  
import android.app.AlertDialog;

import com.android.salesforce.operation.DataHandleFactory;
import com.android.salesforce.sobject.MainMenu;

import com.android.salesforce.util.StaticInformation;
import com.android.salesforce.viewer.DashboardViewer;
import com.android.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
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

	private static EditText UserId;
	private static EditText UserPassword;
	private static CheckBox demoId;
	
	private static boolean login = false;
	private static TextView mSwitcher;
	private static Button loginButton;
	private static TextView helpView;
	private static String json;
    private static final int LOGIN = 0;
    private static final int REFRESH_DATA = 1;
    private static final int RESYNC = 2;
    private static final int HELP_MESSAGE = 0;
    
	private WebView main_view;
	private WebView sub_view;
	
	private DataHandleFactory daf;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.login_menu);
		initComp();
		
		daf = new DataHandleFactory(this, mSwitcher, loginButton, helpView, demoId, main_view, sub_view);
 		
        initTextView();
		initButton();
		initHelpMenu();
	}
	
	/** initi components */
	private void initComp() {
		mSwitcher = (TextView) findViewById(R.id.switcher);		
		loginButton = (Button) findViewById(R.id.salesforce_login);
		helpView = (TextView)findViewById(R.id.salesforce_login_help);
		UserId = (EditText) findViewById(R.id.salesforce_user_id);
		UserPassword = (EditText) findViewById(R.id.salesforce_user_password);
		demoId = (CheckBox)findViewById(R.id.demo_checkbox);
		main_view = (WebView) findViewById(R.id.login_main_web_view);		
		sub_view = (WebView) findViewById(R.id.login_sub_web_view);		
	}
	
	/** init Text View Layout */
	private void initTextView() {
		mSwitcher.setVisibility(View.INVISIBLE);
		
		/** setting user id/pw */
		//UserToken = (EditText) findViewById(R.id.salesforce_user_token);
		//UserId.setText("android@vv.com");
		//UserPassword.setText("google12345");
		
		demoId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					UserId.setText("**********");
					UserPassword.setText("**********");
					
					UserId.setFocusable(false);
					UserPassword.setFocusable(false);
					//UserToken.setFocusable(false);
					 
					UserId.setTextColor(0x66000000);
					UserPassword.setTextColor(0x66000000);
					//UserToken.setTextColor(0x66000000);
					
					demoId.setText(R.string.label_demo_login);
					StaticInformation.isDemo = true;
				} else {
					UserId.setText("");
					UserPassword.setText("");
					
					UserId.setFocusable(true);
					UserPassword.setFocusable(true);
					//UserToken.setFocusable(true);

					UserId.setTextColor(0xFF000000);
					UserPassword.setTextColor(0xFF000000);
					//UserToken.setTextColor(0xFF000000);

					demoId.setText(R.string.label_demo_checkbox);
					StaticInformation.isDemo = false;
				}
			}
		});

		String[] it = daf.readIdAndToken().split(":");
		String t = it[0] == null ? "" : it[0];
		UserId.setText(t);
		if(it.length >= 2){
			t = it[1] == null ? "" : it[1];
			//UserToken.setText(t);
		}
	}
	
	/** init Button Layout */
	private void initButton() {
		Animation in = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in);
		Animation out = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out);
		loginButton.setAnimation(in);
		if(login)loginButton.setText(" Go ");
		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (login) {
					Log.v(TAG, "Already logged in...");

					Intent intent = new Intent();
					intent.putExtra("json", json);
					//intent.setClass(SalesforceAndroid.this, TabMenuMaker.class);
					intent.setClass(SalesforceAndroid.this, MainMenu.class);
					
					startActivity(intent);
				} else if(!StaticInformation.isDemo && 
						(UserId.getText().toString().length() == 0 || UserPassword.getText().toString().length() == 0)) {
					//mSwitcher.setVisibility(0);
					mSwitcher.setText("Enter Id/Pw");
					return;
				} 
				else {

					StaticInformation.USER_ID = UserId.getText().toString();
					StaticInformation.USER_PW = UserPassword.getText().toString();
					
					if(StaticInformation.isDemo){
						StaticInformation.USER_ID = "dai.odahara@gmail.com";
						//StaticInformation.USER_ID = "dai.odahara@google.com";
						StaticInformation.USER_PW = "sfdcj12345";
						//StaticInformation.USER_ID = "android@vv.com";
						//StaticInformation.USER_PW = "google9876";
					}
					
					//String t = UserToken.getText().toString();
					//StaticInformation.USER_TOKEN = t == null ? "" : t;					
					mSwitcher.setVisibility(View.VISIBLE);
					
					Log.v(TAG, "Not logged in Yet...");
					loginButton.setFocusable(false);
					demoId.setFocusable(false);
					loginButton.setClickable(false);
					demoId.setClickable(false);
					
					login = daf.initialProcess();
				}
			}
		});
	}
	
	/** init Help */
	private void initHelpMenu() {
		helpView.setFocusableInTouchMode(true);
		helpView.setFocusable(true);
		helpView.setCursorVisible(true);
		
		helpView.setVerticalScrollBarEnabled(true);
		helpView.setClickable(true);
		helpView.setCursorVisible(true);
				
		helpView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
					Intent intent = new Intent();
					intent.setClass(SalesforceAndroid.this,
							DashboardViewer.class);
					StaticInformation.USER_ID = "dai.odahara@gmail.com";
					StaticInformation.USER_PW = "sfdcj12345";
					//Toast.makeText(AccountList.this, "Dynamic Page Jump is to be implemented", Toast.LENGTH_LONG).show();					
					//startActivity(intent);
					//showDialog(HELP_MESSAGE);
			}
		});
	}
	
	/** help window maker */
	private AlertDialog makeHelpAlert() {
        return new AlertDialog.Builder(SalesforceAndroid.this)
        //.setIcon(R.drawable.alert_dialog_icon)
        .setTitle(R.string.alert_help_title)
        .setMessage(R.string.alert_help_message)
        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        })
        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        })
        .create();
	}	
	
	/** Creating Menu */
	//@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	//	super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, R.string.label_salesforce_refresh);
		menu.add(0, 1, 0, R.string.label_salesforce_resync);
		return true;
	}

	/** Menu on Display */
	//@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		Log.v(TAG, "id:" + featureId + "-item:" + item.getItemId());
		
		switch(item.getItemId()) {
			case LOGIN:
				if(UserId.getText().toString().length() != 0 && UserPassword.getText().toString().length() != 0) {
					mSwitcher.setVisibility(0);
					loginButton.setClickable(false);
					Log.v(TAG, "Not logged in Yet...");
					
					daf.initialProcess();
				}
			case REFRESH_DATA:				
				refresh();
			case RESYNC:
				refresh();
				if(UserId.getText().toString().length() != 0 && UserPassword.getText().toString().length() != 0) {
					mSwitcher.setVisibility(0);
					loginButton.setClickable(false);
					Log.v(TAG, "Not logged in Yet...");
					
					daf.initialProcess();
				}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/** refresh local data */
	private void refresh() {
		mSwitcher.setVisibility(4);
		mSwitcher.setText("Authenticating...");
		loginButton.setText("Login");
		
		daf.dataRefresh();		
	}
	
	/** View Maker for view siwther */
	public View makeView() {
		TextView t = new TextView(this);
		// t.setLayoutParams(Alignment.ALIGN_CENTER);
		// t.setAlignment(Alignment.ALIGN_CENTER);
		t.setTextSize(18);
		return t;
	}

}