/**
 * project name:(emeeting)
 * create  time:2013-3-22
 * author:liujianjian
 */
package com.tvmining.wifiplus.thread;

import android.os.AsyncTask;
import android.util.Log;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.UploadUserInfoEntity;
import com.tvmining.sdk.entity.UploadUserInfoStatusEntity;
import com.tvmining.wifiplus.util.Constant;

public class UploadPersonInfoTask extends AsyncTask<Object, Void, Object> {

	private static final String TAG = "UploadPersonInfoTask";
	private UploadUserInfoEntity userInfo;
	
	public UploadPersonInfoTask(UploadUserInfoEntity entity){
		userInfo = entity;
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		UploadUserInfoStatusEntity detailStatus;
		try{
			if(Constant.iceConnectionInfo.getLoginICE() != null && Constant.iceConnectionInfo.getUserInfoEntity() != null){
				detailStatus = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),Constant.iceConnectionInfo.getUserInfoEntity()).uploadUserInfo(userInfo);
				if (detailStatus.code != UploadUserInfoStatusEntity.SUCC){
		            Log.d(TAG,"提交失败啊失败啊失败");
		            return null;
		        }
			}
			
		} catch (Exception e) {
			Log.e(TAG,"上传出现异常",e);
		}
		return null;
	}
}