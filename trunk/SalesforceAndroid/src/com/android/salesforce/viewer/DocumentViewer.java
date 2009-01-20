/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.salesforce.viewer;

import com.android.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

/**
 * This class is responsible for document view.
 * 
 * @author Dai Odahara
 * 
 */
public class DocumentViewer extends Activity implements
		AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.document_viewer);

		mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
		mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
		mSwitcher.setFactory(this);
		mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));

		Gallery g = (Gallery) findViewById(R.id.gallery);
		g.setAdapter(new ImageAdapter(this));
		g.setOnItemSelectedListener(this);
	}

	public void onItemSelected(AdapterView parent, View v, int position, long id) {
		//mSwitcher.setImageResource(mImageIds[position]);
	}

	public void onNothingSelected(AdapterView parent) {
	}

	public View makeView() {
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return i;
	}

	private ImageSwitcher mSwitcher;

	public class ImageAdapter extends BaseAdapter {
		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return 0;
			//return mThumbIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		/**
		 * TODO access to a serve via the internet to get files and dynamically
		 * change them to image files.
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = new ImageView(mContext);

			//iv.setImageResource(mThumbIds[position]);
			iv.setAdjustViewBounds(true);
			iv.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			//iv.setBackgroundResource(R.drawable.picture_frame);
			return iv;
		}

		public float getAlpha(boolean focused, int offset) {
			return Math.max(0.2f, 1.0f - (0.2f * Math.abs(offset)));
		}

		public float getScale(boolean focused, int offset) {
			return Math.max(0, offset == 0 ? 1.0f : 0.6f);
		}

		private Context mContext;

	}

	/** static image files
	 *  TODO gets file via web dynamically using web services.
	 */
	/*
	private Integer[] mThumbIds = { R.drawable.sample_thumb_0,
			R.drawable.sample_thumb_1, R.drawable.sample_thumb_2,
			R.drawable.sample_thumb_3, R.drawable.sample_thumb_4,
			R.drawable.sample_thumb_5, R.drawable.sample_thumb_6,
			R.drawable.sample_thumb_7, R.drawable.sample_thumb_8,
			R.drawable.sample_thumb_9, R.drawable.sample_thumb_10,
			R.drawable.sample_thumb_11 };

	private Integer[] mImageIds = { R.drawable.sample_0, R.drawable.sample_1,
			R.drawable.sample_2, R.drawable.sample_3, R.drawable.sample_4,
			R.drawable.sample_5, R.drawable.sample_6, R.drawable.sample_7,
			R.drawable.sample_8, R.drawable.sample_9, R.drawable.sample_10,
			R.drawable.sample_11 };
*/
}
