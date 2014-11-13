package com.tvmining.wifiplus.view;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.zxing.CaptureView;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ResourceUtil;
import com.tvmining.wifipluseq.R;

/**
 * @author Like
 *
 */
public class ScanView extends BaseView {
	
	private CaptureView captureView;
	
	public ScanView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public ScanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public ScanView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public void init() {
    	View view = LayoutInflater.from(mContext).inflate(ResourceUtil.getResId(mContext, "scan", "layout"),null);
    	captureView = (CaptureView) view.findViewById(ResourceUtil.getResId(mContext, "scan", "id"));
    	this.addView(view);
    	
    	
    	captureView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setClickable(false);
				captureView.destroy();
//				closeQrcodeView();
			}
		}); 
		showView(captureView,new AnimatorListenerAdapter() {
	        @Override
	        public void onAnimationEnd(Animator animation) {
	        	
	        	captureView.setData();
	        	captureView.surfaceView.setVisibility(View.VISIBLE);
	        }

	        @Override
	        public void onAnimationCancel(Animator animation) {
	        }
	    });
    	
    }
	
	
	public void closeQrcodeView(){
		hideView(captureView,new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            	captureView.destroy();
//            	captureView.surfaceView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
	}
	
	
	public void showView(final View waitShowview,AnimatorListenerAdapter listener){
		waitShowview.setVisibility(View.VISIBLE);
		final AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(waitShowview, View.X, Constant.screenWidth,
                        0));
        set.setDuration(300);
        set.addListener(listener);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
	}
	
	public void hideView(final View waitShowView,AnimatorListenerAdapter listener){
		waitShowView.setVisibility(View.VISIBLE);
		final AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(waitShowView, View.X, -Constant.screenWidth, 0));
        set.setDuration(300);
        set.addListener(listener);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
	}
}