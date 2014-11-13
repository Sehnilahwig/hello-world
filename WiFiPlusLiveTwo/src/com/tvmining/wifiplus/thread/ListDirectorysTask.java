/**
 * project name:(eMeeting)
 * create  time:2013-1-9
 * author:liujianjian
 */
package com.tvmining.wifiplus.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.wifiplus.util.Constant;
/**
 * 解析url返回List数据类型,数据类型可以是任意通用Bean
 * 
 */
public class ListDirectorysTask extends AsyncTask<Object, Void, Object> {

	private static final String TAG = "ListDirectorysTask";
	private Context mContext;
	private Object obj;
	
	public ListDirectorysTask(Context mContext,Object obj){
		this.mContext = mContext;
		this.obj = obj;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//在这里打印准备信息
		
	}

	@Override
	protected Object doInBackground(Object... params) {
		//根据url得到服务器返回的json串
		PackInfoEntity[] allPackName = null;
		try {
			if(Constant.iceConnectionInfo.getLoginICE() != null && Constant.iceConnectionInfo.getUserInfoEntity() != null){
				ICESDK mySDK = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),
						Constant.iceConnectionInfo.getUserInfoEntity());
				allPackName = mySDK.getAllPublicPackName();
				
			}
		} catch (Exception e) {
			Log.e(TAG, "获得包出错",e);
		}
        
        if (allPackName==null||allPackName.length == 0){
        	Log.e(TAG, "取得包失败");
        }else {
            for (int i = 0; i < allPackName.length; i++) {
            	for (Object entry: Constant.downloadingMap.entrySet()) {

            		if(entry != null){
            			String key = (String) ((Entry)((Object) entry)).getKey();
            			if(key.equals(allPackName[i].thumb_guid)){
            				allPackName[i].isPause = true;
            				break;
            			}
            		}
            	}
            }
        }
		return allPackName;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		//调用UI线程的方法刷新,Activity根据List去显示UI布局
		Message msg = new Message();
		msg.what = Constant.HANDLER_ONLINEL_LOAD_PACKAGES;
		List list = new ArrayList();
		list.add(result);
		list.add(obj);
		msg.obj = list;
		Constant.activity.getHandler().sendMessage(msg);
	}

	
	@Override
	protected void onCancelled(Object result) {
		super.onCancelled(result);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
	
}
