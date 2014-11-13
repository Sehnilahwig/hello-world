package com.tvmining.wifiplus.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class InteractViewPager extends ViewPager {

	private boolean isInEdit = false;
	private boolean isOnMove = false;
	private InteractGallery interactGallery;
	private boolean unscrolled = false;

	
	public boolean isUnscrolled() {
		return unscrolled;
	}

	public void setUnscrolled(boolean unscrolled) {
		this.unscrolled = unscrolled;
	}

	public InteractGallery getInteractGallery() {
		return interactGallery;
	}

	public void setInteractGallery(InteractGallery interactGallery) {
		this.interactGallery = interactGallery;
	}

	public boolean isOnMove() {
		return isOnMove;
	}

	public void setOnMove(boolean isOnMove) {
		this.isOnMove = isOnMove;
	}

	public boolean isInEdit() {
		return isInEdit;
	}

	public void setInEdit(boolean isInEdit) {
		this.isInEdit = isInEdit;
	}

	public InteractViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public InteractViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(final MotionEvent ev) {

		if (isInEdit || unscrolled) {
			return false;
		} else {
			boolean result = false;
			try {

				result = super.onInterceptTouchEvent(ev);

			} catch (IllegalArgumentException ex) {
				result = false;
			}

			return result;
		}
	}
	
	@Override 
	public boolean onTouchEvent(final MotionEvent ev) {
		super.onTouchEvent(ev);
		final int action = ev.getAction();
		switch (action) {
			case MotionEvent.ACTION_MOVE:
				isOnMove = true;
				if(interactGallery.isHasNewImage()){
					interactGallery.delayLoadNewImage();
				}
				
			break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				
				isOnMove = false;
			break;
		}
		
		return false;
	}

}
