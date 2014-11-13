package com.tvmining.wifiplus.view;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.tvmining.wifiplus.thread.CheckUpdateTask;
import com.tvmining.wifiplus.util.AppUtil;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ResourceUtil;
import com.tvmining.wifiplus.view.UITableView;
import com.tvmining.wifipluseq.R;

public class AboutView extends BaseView {

	
	public AboutView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public AboutView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}


	public AboutView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}


	public Button back;
	public UITableView aboutver;
	
	private void init() {
		// TODO Auto-generated method stub
		View rootView = LayoutInflater.from(mContext).inflate(ResourceUtil.getResId(mContext, "appabout", "layout"),null);
		this.addView(rootView);
		findview();
		aboutver.addBasicItem("版本号", "1.1.1");
		aboutver.addBasicItem("检查更新", "");
		aboutver.addBasicItem("条款", "");
		aboutver.commit();
	}

	
	public void findview(){
		back = (Button) findViewById(ResourceUtil.getResId(mContext, "aboutback", "id"));
		aboutver = (UITableView) findViewById(ResourceUtil.getResId(mContext, "aboutver", "id"));
		aboutver.setClickListener(new CustomClickListener());
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private class CustomClickListener implements UITableView.ClickListener{

		@Override
		public void onClick(int index) {
			// TODO Auto-generated method stub
			if(index == 1){
				new CheckUpdateTask(mContext).execute();
			}else if(index == 2){
				AppUtil.copyToData(mContext);
				Intent intent = new Intent("android.intent.action.VIEW"); 
				intent.addCategory("android.intent.category.DEFAULT"); 
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				Uri uri = Uri.fromFile(new File(Constant.savePath+Constant.CONDITION_NAME)); 
				intent.setDataAndType(uri, "application/msword"); 
				mContext.startActivity(intent);
			}
		}
		
	}


	
}
