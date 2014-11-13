package com.tvmining.wifiplus.thread;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.util.Log;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;
import com.tvmining.sdk.entity.SearchFileEntity;
import com.tvmining.sdk.entity.SideThumbMethod;
import com.tvmining.wifiplus.activity.MainActivity;
import com.tvmining.wifiplus.application.EmeetingApplication;
import com.tvmining.wifiplus.entity.DownloadProgressEntity;
import com.tvmining.wifiplus.entity.DownloadTempTable;
import com.tvmining.wifiplus.entity.ICETable;
import com.tvmining.wifiplus.entity.PakgeTable;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ImageUtil;

/**
 * 资源包下载线程
 * @author Administrator
 *
 */
public class PackageDownloadThread extends Thread {
	private Context context;
	private SimpleDateFormat sdf;
	private String downloadingPackageGuid;

	public PackageDownloadThread(Context mContext) {
		context = mContext;
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	public void run() {
		try {
			while (true) {
				if (!Constant.packageDownloadQueue.isEmpty()) {
					PackInfoEntity entity = null;
					synchronized (Constant.packageDownloadQueue) {
						entity = (PackInfoEntity) Constant.packageDownloadQueue
								.take();
					}
					
					ICESDK mySDK = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),
							Constant.iceConnectionInfo.getUserInfoEntity());
					SearchFileDetailStatusEntity[] result = null;
					
					if(Constant.QRCODE_ACTION_SHARE_PACKAGE.equals(entity.action)){
						result = entity.result;
						entity.pkgIndex = insertPakgeInfo(entity, sdf.format(new Date()));
					}else{
						if (!entity.packageUpdate) {
							entity.pkgIndex = insertPakgeInfo(entity, sdf.format(new Date()));
						}
						
						if(entity.packageUpdate){
							result = new SearchFileDetailStatusEntity[entity.compareVector.size()];
							result = entity.compareVector.toArray(result);
						}else{
							SearchFileEntity condition = new SearchFileEntity();
							condition.inPack.add(entity.packname);// 将包名字作为查询条件
							
							result = mySDK.searchFile(condition);
						}
					}
					
					if(!Constant.QRCODE_ACTION_SHARE_PACKAGE.equals(entity.action)){
						for (int i=0;i<result.length;i++) {
							SearchFileDetailStatusEntity searchEntity = result[i];						// 原图
							
							DownloadTempTable tempTable = new DownloadTempTable();
							tempTable.setValues((int)entity.pkgIndex, "0", String.valueOf(entity.packageSize), i, searchEntity.guid, "0", "0", 0);
							Constant.dbConnection.insertDownloadTempTable(tempTable);
						}
					}
					
					
					EmeetingApplication.setDownloadUnInsertedCount(EmeetingApplication.getDownloadUnInsertedCount() - 1);
					
					DownloadProgressEntity progressEntity = new DownloadProgressEntity();
					progressEntity.setIceIndex(entity.iceIndex);
					progressEntity.setPakgeIndex(entity.pkgIndex);
					progressEntity.setPackageGuid(entity.thumb_guid);
					progressEntity.setProgress(0);
					
					Constant.downloadingMap.put(entity.thumb_guid, progressEntity);
					
					synchronized (Constant.queue) {
						Constant.queue.add(entity);
						try {
							Constant.queue.notify();
						} catch (IllegalMonitorStateException e) {
							
						}
					}
					
					Message msg = new Message();
					 msg.what = Constant.HANDLER_ENTER_LOCAL_VIEW;
					 Constant.activity.getHandler().sendMessage(msg);
					
					msg = new Message();
					msg.what = Constant.HANDLER_DOWNLOAD_PROGRESS_FRESH;
					msg.obj = progressEntity;
					Constant.activity.getHandler().sendMessage(msg);

				} else {
					synchronized (Constant.packageDownloadQueue) {
						Constant.packageDownloadQueue.wait();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private long insertPakgeInfo(PackInfoEntity entity, String totleDate) {
		SearchFileEntity condition = new SearchFileEntity();
		condition.inPack.add(entity.packname);// 将包名字作为查询条件
		SearchFileDetailStatusEntity[] result = null;
		long iceIndex = -1;
		long pkgIndex = -1;
		String iceName = entity.iceName;
		if(!Constant.QRCODE_ACTION_SHARE_PACKAGE.equals(entity.action)){
			iceName = Constant.iceConnectionInfo.getLoginICE().connectICEName;
		}
		try {
			ICESDK mySDK = ICESDK.sharedICE(
					Constant.iceConnectionInfo.getLoginICE(),
					Constant.iceConnectionInfo.getUserInfoEntity());
			if(Constant.QRCODE_ACTION_SHARE_PACKAGE.equals(entity.action)){
				result = entity.result;
			}else{
				result = mySDK.searchFile(condition);
			}
			
			Date date = new Date();
			// judge the same package and ice
			iceIndex = Constant.dbConnection.isExistICE(
					iceName,
					totleDate);
			

			// 要下载的包存储在本地的路径
			String dateStr = new String(totleDate);
			dateStr = dateStr.substring(0, dateStr.indexOf(" "));

			String icePath = Constant.savePath + dateStr + File.separator
					+ iceName;

			String pkgPath = icePath + File.separator + entity.tempPackName;
			
			// 包小图
			String smallcover = null;
			if(Constant.QRCODE_ACTION_SHARE_PACKAGE.equals(entity.action)){
				smallcover = entity.packageIconPath;
			}else{
				smallcover = mySDK.getPackThumbByPackInfo(entity, 100, 100);
			}
			

			String pakgeIconName = entity.thumb_guid+".jpg";
			if (entity.drawable != null) {
				BitmapDrawable bd = (BitmapDrawable) entity.drawable;
				Bitmap bm = bd.getBitmap();
				ImageUtil.savePakgeImage(smallcover, pkgPath, bm,pakgeIconName);
			} else {
				ImageUtil.startDownloadPakgeIcon(smallcover, pkgPath,pakgeIconName);
			}

			// 中控
			if (iceIndex == -1) {
				ICETable iceTable = new ICETable();
				iceTable.setValues(
						totleDate,
						icePath,
						iceName,
						"normal");
				iceIndex = Constant.dbConnection.insertICETable(iceTable);
			}

			entity.iceIndex = iceIndex;

			// 包
			pkgIndex = Constant.dbConnection.isExistPkg(iceIndex, totleDate,
					entity.tempPackName);
			if (pkgIndex == -1) {
				PakgeTable pakgeTable = new PakgeTable();
				pakgeTable.setValues(
						totleDate,
						iceIndex,
						entity,
						String.valueOf(result.length),
						pkgPath
								+ File.separator
								+ pakgeIconName, mySDK, "normal");
				pkgIndex = Constant.dbConnection.insertPakgeTable(pakgeTable);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			/*
			 * if(Constant.getDownloadUnInsertedCount() > 0){
			 * Constant.setDownloadUnInsertedCount
			 * (Constant.getDownloadUnInsertedCount()-1); Log.d("insertPackage",
			 * "aaaaaaaaaa:"+Constant.getDownloadUnInsertedCount()); }
			 */
		}

		return pkgIndex;
	}
	
}