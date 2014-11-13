package com.tvmining.wifiplus.thread;

import java.util.ArrayList;
import java.util.Vector;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.BaseAdapter;

import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.wifiplus.entity.ICETable;
import com.tvmining.wifiplus.entity.LocalGroup;
import com.tvmining.wifiplus.entity.PakgeTable;
import com.tvmining.wifiplus.image.loader.Images;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.MessageUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifiplus.waterfall.adapter.LocalPackageAdapter;
import com.tvmining.wifipluseq.R;

public class PakgeDeleteTask extends AsyncTask<Object, Object, Object> {

	private PakgeTable entity;
	private LocalPackageAdapter adapter;
	private boolean isDownload;

	public PakgeDeleteTask(PakgeTable entity, LocalPackageAdapter adapter,boolean isDownload) {
		this.entity = entity;
		this.adapter = adapter;
		this.isDownload = isDownload;
	}

	@SuppressWarnings("unchecked")
	protected synchronized Object doInBackground(Object... aobj) {
		try {
			
			if(isDownload){
				ICETable iceTable = Constant.dbConnection.queryICE(Integer.parseInt(entity.getIceIndex()));
				for(int l=0;l<Images.allPackName.length;l++){
					final PackInfoEntity packInfoEntity = Images.allPackName[l];
					
					if(iceTable.getIceName().equals(Constant.iceConnectionInfo.getLoginICE().connectICEName) && entity.getPakgeName().equals(packInfoEntity.packname)){
						packInfoEntity.isPause = false;
						if(Constant.downloadingMap.containsKey(packInfoEntity.thumb_guid)){
							if(!packInfoEntity.isPause){
								
								Constant.downloadThread.savePauseData(packInfoEntity);
							}
						}
						break;
					}
				}
			}
			
			Utility.removePackage(entity);
			
			ArrayList<LocalGroup> groupList = adapter.getGroupList();
			ArrayList<Object> childList = adapter.getChildList();
			
			LocalGroup group = null;
			PakgeTable pakgetable = null;
			for(int i=0;i<childList.size();i++){
				Object child = childList.get(i);
				if(!(child instanceof LocalGroup)){
					if(((PakgeTable) child).getPakgeIndex() == entity.getPakgeIndex()){
						pakgetable = (PakgeTable) child;
						break;
					}
				}
			}
			
			
			for(int i=0;i<groupList.size();i++){
				LocalGroup localGroup = groupList.get(i);
				if(Integer.parseInt(entity.getIceIndex()) == localGroup.getIceTable().getIceIndex()){
					Vector localVector = Constant.dbConnection.getPkgsByICEIndex(localGroup.getIceTable().getIceIndex());
					if(localVector == null){
						group = localGroup;
						break;
					}
				}
			}
			
			if(group != null ){
				groupList.remove(group);
				childList.remove(group);
			}
			if(pakgetable != null){
				childList.remove(pakgetable);
			}
			adapter.setChildList(childList);
			adapter.setGroupList(groupList);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	protected void onPostExecute(Object obj) {
		adapter.notifyDataSetChanged();
		
	}

	protected void onProgressUpdate(Object... aobj) {

	}
}
