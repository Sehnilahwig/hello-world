/**
 * project name:(emeeting)
 * create  time:2013-2-27
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
import com.tvmining.sdk.entity.CommandEntity;
import com.tvmining.sdk.entity.GroupTypeEntity;
import com.tvmining.sdk.entity.NeighbourEntity;
import com.tvmining.sdk.entity.StatusEntity;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.Utility;

public class RemoteControlTaskAllCanSee extends AsyncTask<Object, Object, Object> {

	private static final String TAG = "RemoteControlTaskAllCanSee";
	private String inst;
	private String body;
	
	public RemoteControlTaskAllCanSee(String inst, String body) {
		this.inst = inst;
		this.body = body;
	}

	@Override
	protected Object doInBackground(Object... params) {
		StringBuffer xx = new StringBuffer();
		//循环像匹配的屏幕发送指令
		CommandEntity oneCmd = new CommandEntity();
		oneCmd.CmdTYPE = inst;
		oneCmd.body = body;
		oneCmd.OBJ = GroupTypeEntity.ALL_CAN_SEE;
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
		
		Log.i(TAG,"发送指令结果:"+xx.toString());
		return xx;
	}
}
