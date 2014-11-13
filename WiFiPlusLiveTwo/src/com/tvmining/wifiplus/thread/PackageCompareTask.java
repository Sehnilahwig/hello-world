package com.tvmining.wifiplus.thread;

import java.util.Map;
import java.util.Vector;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;
import com.tvmining.sdk.entity.SearchFileEntity;
import com.tvmining.wifiplus.util.Constant;

public class PackageCompareTask extends AsyncTask<Void, Void, Vector> {

	private Map itemMap;
	private PackInfoEntity entity;
	private Handler handler;

	public PackageCompareTask(Map itemMap,PackInfoEntity entity,Handler handler) {
		this.itemMap = itemMap;
		this.entity = entity;
		this.handler = handler;
	}

	protected Vector doInBackground(Void... args) {
		
		SearchFileEntity condition = new SearchFileEntity();
		condition.inPack.add(entity.packname);// 将包名字作为查询条件
		SearchFileDetailStatusEntity[] result = null;
		Vector<SearchFileDetailStatusEntity> compareVector = new Vector<SearchFileDetailStatusEntity>();
		try {
			ICESDK mySDK = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),
					Constant.iceConnectionInfo.getUserInfoEntity());
			result = mySDK.searchFile(condition);
			
			if (result != null && result.length != 0) {
				for (SearchFileDetailStatusEntity searchEntity : result) {
					if(itemMap.get(searchEntity.guid) == null){
						compareVector.add(searchEntity);
					}
				}
			}
			entity.result = result;
			Message msg = new Message();
			msg.what = 0;
			msg.obj = compareVector;
			handler.sendMessage(msg);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return compareVector;
	}

	protected void onPostExecute(Vector compareVector) {
	}
}
