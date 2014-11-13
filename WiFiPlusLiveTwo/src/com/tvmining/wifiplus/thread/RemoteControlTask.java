/**
 * project name:(emeeting)
 * create  time:2013-2-27
 * author:liujianjian
 */
package com.tvmining.wifiplus.thread;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.CommandEntity;
import com.tvmining.sdk.entity.NeighbourEntity;
import com.tvmining.sdk.entity.StatusEntity;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.Utility;

public class RemoteControlTask extends AsyncTask<Object, Object, Object> {

	private static final String TAG = "RemoteControlTask";
	private Context context;
	private String cmdType;
	private String body;
	private boolean isVibrator;
	
	public RemoteControlTask(Context context, String cmdType, String body,boolean isVibrator) {
		this.context = context;
		this.cmdType = cmdType;
		this.body = body;
		this.isVibrator = isVibrator;
	}

	@Override
	protected Object doInBackground(Object... params) {
		StringBuffer xx = new StringBuffer();
		//循环像匹配的屏幕发送指令
		if(isVibrator){
			Utility.remoteVibrator(context);
		}
		
		if(Constant.matchScreen!=null){
			for(int i=0;i<Constant.matchScreen.size();i++){
				CommandEntity oneCmd = new CommandEntity();
				oneCmd.CmdTYPE = cmdType;
				oneCmd.body = body;
				oneCmd.OBJ = Constant.matchScreen.get(i).CmdObjToMy;//指定匹配屏幕
				Integer reTry = 1;
				if(params != null && params.length > 0){
					reTry = (Integer) params[0];
				}
				
				try{
		            StatusEntity oneStatus = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),Constant.iceConnectionInfo.getUserInfoEntity()).sendCommmand(oneCmd);
		            if (oneStatus.status.equals(StatusEntity.OK)&&oneStatus.cmd.equals(oneCmd.CmdTYPE)){
		                xx.append(oneCmd.CmdTYPE + "<-" + oneCmd.body + " 发送成功("+String.valueOf(reTry)+")\n");
		            }else{
		            	xx.append(oneCmd.CmdTYPE + "<-" + oneCmd.body + " 发送失败("+String.valueOf(reTry)+")\n");
		            }
		        }catch (Exception ex) {
		        	xx.append(oneCmd.CmdTYPE + "X" + oneCmd.body + " 发送失败"+ex.getMessage()+"\n");
		        	if (reTry <= 0) {
						xx.append("错误重试尽头，不再重试：\n");
						return xx;
		        	}
		        	reTry--;
		        	doInBackground(reTry);//发送指令失败时 执行重发操作
		        }
			}
		}
		Log.i(TAG,"发送指令结果:"+xx.toString());
		return xx;
	}
}
