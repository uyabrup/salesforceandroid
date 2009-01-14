package com.salesforce.android.animation;

import com.salesforce.android.util.StaticInformation;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class SwapViews implements Runnable {
    private final int mPosition;

    public SwapViews(int position) {
        mPosition = position;
    }

    public void run() {
        final float centerX = StaticInformation.MainContainer.getWidth() / 2.0f;
        final float centerY = StaticInformation.MainContainer.getHeight() / 2.0f;
        Rotate3dAnimation rotation;
        
        if (mPosition > -1) {
        	/*
            mPhotosList.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
            mImageView.requestFocus();
        	 */
            rotation = new Rotate3dAnimation(90, 180, centerX, centerY, 310.0f, false);
        } else {
        	/*
            mImageView.setVisibility(View.GONE);
            mPhotosList.setVisibility(View.VISIBLE);
            mPhotosList.requestFocus();
			*/
            rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
        }

        rotation.setDuration(700);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new DecelerateInterpolator());

        StaticInformation.MainContainer.startAnimation(rotation);
    }

}
