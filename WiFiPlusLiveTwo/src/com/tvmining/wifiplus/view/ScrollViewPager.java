package com.tvmining.wifiplus.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ScrollViewPager extends ViewPager{

	private boolean isCanScroll = true;
	
	public boolean isCanScroll() {
		return isCanScroll;
	}

	public void setCanScroll(boolean isCanScroll) {
		this.isCanScroll = isCanScroll;
	}

	public ScrollViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
		boolean result = false;
		try{

			result = super.onInterceptTouchEvent(ev);

		} catch(IllegalArgumentException ex) {
			result = false;
		}

		return result;

	}
	/*@Override
    public boolean onTouchEvent(final MotionEvent ev) {
		
		try{

			 super.onTouchEvent(ev);

			} catch(IllegalArgumentException ex) {

			}

			return false;


	}*/

	@Override  
    public void scrollTo(int x, int y){  
		if (isCanScroll){  
            super.scrollTo(x, y);  
        }
    } 
}
