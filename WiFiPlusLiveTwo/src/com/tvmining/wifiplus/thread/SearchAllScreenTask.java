/**
 * project name:(eMeeting)
 * create  time:2013-1-9
 * author:liujianjian
 */
package com.tvmining.wifiplus.thread;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.MyNeighboursEntity;
import com.tvmining.sdk.entity.NeighbourEntity;
import com.tvmining.sdk.entity.UserTypeEntity;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.Utility;
/**
 * 解析url返回List数据类型,数据类型可以是任意通用Bean
 * 这个异步类用来获取所有课搜索到的屏幕设备
 */
public class SearchAllScreenTask extends AsyncTask<Object, Void, MyNeighboursEntity> {

	private static final String TAG = "SearchScreenTask2";
	private String type;
	private Context con;
	
	public SearchAllScreenTask(Context con,String type){
		this.con = con;
		this.type = type;
	}

	@Override
	protected MyNeighboursEntity doInBackground(Object... params) {
		Log.i(TAG, "SearchScreenTask2 doInBackground");
		MyNeighboursEntity myNeighbor = null;
		try {
			if(Constant.iceConnectionInfo.getLoginICE() != null && Constant.iceConnectionInfo.getUserInfoEntity() != null){
				ICESDK icesdk = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),Constant.iceConnectionInfo.getUserInfoEntity());
				if(icesdk != null){
					myNeighbor = icesdk.getMyNeighbours();
				}
			}
			
		} catch (Exception e) {
			Log.d(TAG,"得不到我的邻居",e);
			return null;
		}
		return myNeighbor;
	}

	@Override
	protected void onPostExecute(MyNeighboursEntity result) {
		//调用UI线程的方法刷新,Activity根据List去显示UI布局
		
		//分析查找屏幕的结果
  		if(result!=null){
  			NeighbourEntity[] array = result.canSendArray;
  			
  			int deviceCount = 0;//可匹配设备数量
  			for(NeighbourEntity nei:array){
  				if(nei.type.equals(UserTypeEntity.DRIVCE)){
  					deviceCount+=1;
  				}
  			}
  			if(Constant.allScreenList != null){
  				Constant.allScreenList.clear();
  			}
  			
  			Constant.allScreenList = new ArrayList<NeighbourEntity>();
  			if(deviceCount==0){
  			}else{
  				//查询到所有可匹配的屏幕列表赋值给allScreenList
  				for(int i=0;i<result.canSendArray.length;i++){
  					if(result.canSendArray[i].type.equals(UserTypeEntity.DRIVCE)){
  						if(i == 0 && !Constant.isRun){
  							Constant.isRun = true;
  							Constant.matchScreen.add(result.canSendArray[i]);
  						}
  						Constant.allScreenList.add(result.canSendArray[i]);
  					}
  				}
  			}
  			
  			Message msg = new Message();
  			msg.what = Constant.HANDLER_REMOTE_SHOW;
			Constant.activity.getHandler().sendMessage(msg);
  			
  		}
	}
	
}
