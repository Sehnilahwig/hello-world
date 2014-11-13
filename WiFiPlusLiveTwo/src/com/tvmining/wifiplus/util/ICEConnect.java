package com.tvmining.wifiplus.util;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.ConnectFailedStatus;
import com.tvmining.sdk.entity.ICELoginEntity;
import com.tvmining.sdk.entity.ICELoginMethodEntity;
import com.tvmining.sdk.entity.ListenEventEntity;
import com.tvmining.sdk.entity.UserInfoEntity;
import com.tvmining.sdk.entity.UserTypeEntity;
import com.tvmining.wifiplus.thread.LoginAsyncTask;
import com.tvmining.wifipluseq.R;

public class ICEConnect implements ListenEventEntity{

	private static String TAG = "ICEConnect";
	private static Timer timer;
	private static boolean isUpdateWithServiceList;
	private static Context mContext;
	
	public static void stopTimer() {
		if(timer != null){
			timer.cancel();
			timer = null;
		}
	}

	public synchronized static void searchICE(Context context,final Object obj){
		Log.d(TAG, "���ڵ�½...");
		try{
			mContext = context;
			Constant.loginStatus = Constant.LOGIN_ON; 
			stopTimer();
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					ICELoginEntity[] icename = null;
					if(!isUpdateWithServiceList){
						icename = ICESDK.getAllICENameArray();
					}
					
					if (icename != null && icename.length > 0) {
						if(!isUpdateWithServiceList){
							Constant.iceConnectionInfo.setLoginICE(icename[0]);
						}else{
							Constant.iceConnectionInfo.setLoginICE(icename[icename.length - 1]);
						}
						
						if (Constant.iceConnectionInfo.getLoginICE().loginMethod
								.equalsIgnoreCase(ICELoginMethodEntity.DEFAULT)) {
							// Ĭ�Ϸ�ʽ��¼
							Log.d("", "Ĭ�Ϸ�ʽ��¼\n");
						}

						if (Constant.iceConnectionInfo.getLoginICE().loginMethod
								.equalsIgnoreCase(ICELoginMethodEntity.PASSWORD)) {
							// ���뷽ʽ��¼
							Log.d("", "���뷽ʽ��¼\n");
							Constant.iceConnectionInfo.getLoginICE().password = Constant.defaultPwd;// ���������
						}

						if (Constant.iceConnectionInfo.getLoginICE().loginMethod
								.equalsIgnoreCase(ICELoginMethodEntity.REGISTRY)) {
							// ��շ�ʽ��¼
						}

						if (Constant.iceConnectionInfo.getLoginICE() != null && icename.length > 0) {
							Message msg = new Message();
							msg.obj = obj;
							msg.what = Constant.HANDLER_ONLINEL_SEARCHICE_SUCCESS;
							Constant.activity.getHandler().sendMessage(msg);
						}
						isUpdateWithServiceList = false;
					}else{
						Constant.loginStatus = Constant.LOGIN_FAILURE; 
						Message msg = new Message();
						msg.what = Constant.HANDLER_ONLINEL_SEARCHICE_FAILURE;
						Constant.activity.getHandler().sendMessage(msg);
					}
				}
			}, 2000, 3000);
			
			
		}catch(IllegalStateException e){
			Log.e("MainActivity", "Timer was canceled");
		}
	}
	
	public static void login(boolean defaultLogin,Context mContext,Object obj){
		if(defaultLogin){
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
			String passwordValue = preferences.getString(Constant.PREFERENCES_NAME,Constant.defaultPwd);
			Utility.tryLogin(mContext,passwordValue,obj);
		}else{
			Constant.loginStatus = Constant.LOGIN_FAILURE;
			Message msg = new Message();
			msg.what = Constant.HANDLER_PERMISSION_CHANGE;
			Constant.activity.getHandler().sendMessage(msg);
		}
	}

	@Override
	public void raiseHttpUploadingEvent(String filename, long filesize,
			long uploadingByte, long batchUploadBytes, long totalFileSize) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void raiseConnectionRetryFailEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void raiseConnectionWillRetryEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void raiseConnectionRetrySuccessEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void raiseSearchingUpdateWithServiceList(ICELoginEntity[] iceArray) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void raiseConnectionFailedEvent(ICELoginEntity LoginICE,
			UserInfoEntity userEntity, ConnectFailedStatus failedStatus) {
		// TODO Auto-generated method stub
		
	}
}
