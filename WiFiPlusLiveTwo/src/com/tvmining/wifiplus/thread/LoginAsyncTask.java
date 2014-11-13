/**
 * project name:(emeeting)
 * create  time:2013-1-14
 * author:liujianjian
 */
package com.tvmining.wifiplus.thread;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.UserInfoEntity;
import com.tvmining.wifiplus.application.EmeetingApplication;
import com.tvmining.wifiplus.entity.Permission;
import com.tvmining.wifiplus.entity.User;
import com.tvmining.wifiplus.util.Constant;

public class LoginAsyncTask extends AsyncTask<Object, Integer, ICESDK> {

	private static final String TAG = "LoginAsyncTask";
	
	private Object obj;
	
	private Context mContext;
	
	public LoginAsyncTask(Object obj,Context mContext){
		this.obj = obj;
		this.mContext = mContext;
	}
	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	protected ICESDK doInBackground(Object... params) {
        UserInfoEntity userInfoEntity = (UserInfoEntity)params[0];
	    ICESDK oneSDK=null;
        try {
        	oneSDK = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(), userInfoEntity);
			Constant.iceConnectionInfo.setUserInfoEntity(userInfoEntity);
		} catch (Exception e) {
		}

        return oneSDK;
	}
	
	@Override
	protected void onPostExecute(ICESDK oneSDK) {
		super.onPostExecute(oneSDK);
		if(oneSDK != null){
			Constant.loginStatus = Constant.LOGIN_SUCCESS;
			Constant.user = new User();
			
			try {
				Permission permission = new Permission();
				permission.dealLevel();
				Constant.user.setPermisssion(permission);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Constant.receiveCmdThread = new ReceiveCmdThread(mContext);
			Constant.receiveCmdThread.start();
			
			Message msg = new Message();
			msg.obj = obj;
			msg.what = Constant.HANDLER_ONLINEL_LOGIN_SUCCESS;
			Constant.activity.getHandler().sendMessage(msg);
		}else{
			Constant.loginStatus = Constant.LOGIN_FAILURE;
			Message msg = new Message();
			msg.what = Constant.HANDLER_ONLINEL_LOGIN_FAILURE;
			Constant.activity.getHandler().sendMessage(msg);
		}
		if(Constant.matchScreen != null){
			Constant.matchScreen.clear();
		}
		new SearchAllScreenTask(mContext,"send").execute();
	}
	
}