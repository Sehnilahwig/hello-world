package com.tvmining.wifiplus.thread;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifipluseq.R;
import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.sdk.entity.SearchPackExtDetailEntity;

public class GetPackageSizeTask extends AsyncTask<Object, Object, SearchPackExtDetailEntity[]> {

	private Handler pkgSizeHandler;
	private PackInfoEntity entity;
	
	public GetPackageSizeTask(PackInfoEntity entity,Handler pkgSizeHandler){
		this.entity = entity;
		this.pkgSizeHandler = pkgSizeHandler;
	}
	
	protected SearchPackExtDetailEntity[] doInBackground(Object... aobj) {
		SearchPackExtDetailEntity[] packExtInfo = null;
		try {
			packExtInfo = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(), Constant.iceConnectionInfo.getUserInfoEntity()).getPackExtInfo(entity.packname);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return packExtInfo;
	}

	protected void onPostExecute(SearchPackExtDetailEntity[] packExtInfo) {
		if(packExtInfo != null && packExtInfo.length > 0){
			
			long size = Math.abs(packExtInfo[0].size);
			entity.packageSize = size;
			Message msg = new Message();
			String sizeStr = "";
			if(size < 1024){
				sizeStr = size+" B";
			}else if(size < 1024 * 1024){
				sizeStr  = size / 1024 + " KB";
			}else if(size < 1024 * 1024 * 1024){
				sizeStr = size / (1024 * 1024) + " MB";
			}else if(size < 1024 * 1024 * 1024 * 1024){
				sizeStr = size / (1024 * 1024 * 1024) + " GB";
			}else{
				sizeStr = size / (1024 * 1024) + " MB";
			}
			msg.obj = sizeStr;
			
			pkgSizeHandler.sendMessage(msg);
			
		}
	}
}
