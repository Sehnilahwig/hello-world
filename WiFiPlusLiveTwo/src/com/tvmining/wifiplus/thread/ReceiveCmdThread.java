package com.tvmining.wifiplus.thread;

import android.content.Context;
import android.content.Intent;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.CommandEntity;
import com.tvmining.sdk.entity.CommandTypeEntity;
import com.tvmining.wifiplus.util.Constant;

public class ReceiveCmdThread extends Thread{
	
	private Context mContext;
	
	public ReceiveCmdThread(Context mContext){
		this.mContext = mContext;
	}
	
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()){
                try{
                	if(Constant.iceConnectionInfo.getLoginICE() != null && Constant.iceConnectionInfo.getUserInfoEntity() != null){
                		CommandEntity getOneCmd = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(), Constant.iceConnectionInfo.getUserInfoEntity()).recvCommand();
                		
                		if(getOneCmd.CmdTYPE.compareTo(CommandTypeEntity.ICERESET) == 0){ 
                            break;
                        }
                		
                        Intent commandIntent = new Intent();
                        commandIntent.setAction("system_command");
                        commandIntent.putExtra("cmdType", getOneCmd.CmdTYPE);
                        commandIntent.putExtra("body", getOneCmd.body);
                        commandIntent.putExtra("from", getOneCmd.FROM);
                        mContext.sendBroadcast(commandIntent);
                        
                        if(getOneCmd.CmdTYPE.equalsIgnoreCase(CommandTypeEntity.GO)){
                        	String jsonRaw = getOneCmd.body;
                        }
                        
                        if(getOneCmd.CmdTYPE.equalsIgnoreCase(CommandTypeEntity.NEWPACK)){
                        	String packname = getOneCmd.body;
                        }
                	}
                }catch (Exception e) {
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
