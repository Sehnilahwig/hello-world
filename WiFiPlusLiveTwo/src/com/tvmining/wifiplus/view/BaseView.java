package com.tvmining.wifiplus.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;


public class BaseView extends LinearLayout{

	protected Context mContext;
	public BaseView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	public BaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	protected void refreshView(){
		
	}
	
	protected void dealConfigurationChanged(Configuration newConfig){}
}
