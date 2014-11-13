/**
 * project name:(eMeeting)
 * create  time:2012-12-28
 * author:liujianjian
 */
package com.tvmining.wifiplus.view;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.GroupTypeEntity;
import com.tvmining.sdk.entity.ICELoginEntity;
import com.tvmining.sdk.entity.UserInfoEntity;
import com.tvmining.sdk.entity.UserTypeEntity;
import com.tvmining.wifiplus.thread.LoginAsyncTask;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.MessageUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifipluseq.R;

public class LoginView extends FrameLayout implements OnClickListener{

	private Context mContext;
	
	public LoginView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public LoginView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public LoginView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	protected static final String TAG = "SecondLoginActivity";
	private EditText password;//密码输入框
	private Button login,cancel;//确定按钮
	TelephonyManager mTelephony = null;
	InputMethodManager imm = null;
	private View headMaskView;
	
	protected void init(Context mContext) {
		this.mContext = mContext;
		
		LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
		View menuView = (View) mLayoutInflater.inflate(R.layout.loginprompt,
				null, true);
		
		imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		mTelephony = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
		password = (EditText)menuView.findViewById(R.id.password);
		login = (Button)menuView.findViewById(R.id.confirm);
		cancel = (Button)menuView.findViewById(R.id.cancel);
		headMaskView = menuView.findViewById(R.id.headMaskView);
		headMaskView.setOnClickListener(this);
		login.setOnClickListener(this);
		cancel.setOnClickListener(this);

        this.addView(menuView);
	} 

	public void showLoginEditLayout(){
		this.setVisibility(View.VISIBLE);
		password.requestFocus();
		password.requestFocusFromTouch();
		Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
                InputMethodManager inputManager =(InputMethodManager)password.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(password, 0);
            }
            
        },200);
	}
	
	public void hideLoginEditLayout(){
		this.setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.confirm:
				imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
				
				Utility.interruptAll();
				
				if(Constant.iceConnectionInfo.getLoginICE() != null){
					hideLoginEditLayout();
					Constant.activity.showProgressDialog();
					Utility.tryLogin(mContext,password.getText().toString(),null);
				}
				break;
			case R.id.cancel:
				InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
				if(imm.isActive() && password != null){
					InputMethodManager im = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE); 
					im.hideSoftInputFromWindow(password.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
				Message msg = new Message();
				msg.what = Constant.HANDLER_ONLINEL_LOGIN_CANCEL;
				Constant.activity.getHandler().sendMessage(msg);
				break;
			default:
				break;
		}
	}

}