package com.tvmining.wifiplus.thread;


import java.io.ByteArrayOutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.json.JSONException;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.ConnectFailedStatus;
import com.tvmining.sdk.entity.FolderMakerEntity;
import com.tvmining.sdk.entity.FolderMakerStatusEntity;
import com.tvmining.sdk.entity.ICELoginEntity;
import com.tvmining.sdk.entity.ListenEventEntity;
import com.tvmining.sdk.entity.UploadFileDetailStatusEntity;
import com.tvmining.sdk.entity.UserInfoEntity;
import com.tvmining.sdk.entity.uploadFileEntity;
import com.tvmining.sdk.helper.HttpHelper;
import com.tvmining.wifiplus.entity.DownloadProgressEntity;
import com.tvmining.wifiplus.entity.ICETable;
import com.tvmining.wifiplus.entity.ItemTable;
import com.tvmining.wifiplus.entity.PakgeTable;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.IntAreaUtil;

public class UploadThread extends Thread implements ListenEventEntity{
	private Context context;
	private boolean[] freshArray;
//	private long totalSize;
	private DownloadProgressEntity progressEntity;
	PakgeTable pakgeTable = null;
	public String uploadingPackageName;
	
	public UploadThread(Context mContext) {
		context = mContext;
		ICESDK.addHttpUploadingProgressListener(this);
	}

	public void run() {
		try {
			while (true) {
				if (!Constant.uploadQueue.isEmpty()) {
					freshArray = new boolean[100];
//					totalSize = 0;
					synchronized (Constant.uploadQueue) {
						pakgeTable = (PakgeTable) Constant.uploadQueue
								.take();
						uploadingPackageName = pakgeTable.getPakgeName();
					}

					upload(pakgeTable);
				} else {
					synchronized (Constant.uploadQueue) {
						Constant.uploadOrDownload = Constant.DO_NOTHING;
						Constant.uploadQueue.wait();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void upload(PakgeTable pakgeTable) {
		UploadFileDetailStatusEntity[] detailStatus = null;
		boolean flag = false;
		String packname = pakgeTable.getTempPakgeName();
		String suggestGroup;

		// suggestGroup = GroupTypeEntity.ANONYMOUS; //上传包是绿色权限
		// suggestGroup = GroupTypeEntity.PRIVILEGE; //上传包是黄色权限
		// suggestGroup = GroupTypeEntity.ADMINISTRATOR; //上传包是红色权限
		suggestGroup = UserInfoEntity.groupId;// 上传文件为用户上传 定义要上传的包的权限
		// 包类型
		String packType = pakgeTable.getPakgeType();// 这个需要和前端对

		String requestGuid = "androidSDK" + UUID.randomUUID().toString();

		FolderMakerEntity folderMaker = new FolderMakerEntity();
		folderMaker.packname = packname;
		folderMaker.suggestGroup = suggestGroup;
		folderMaker.requestGuid = requestGuid;
		if(pakgeTable.getItemCount() == null || "".equals(pakgeTable.getItemCount())){
			folderMaker.uploadNum = 0;
		}else{
			folderMaker.uploadNum = Integer.parseInt(pakgeTable.getItemCount());
		}
		
		folderMaker.packType = packType;
		FolderMakerStatusEntity result = null;
		try {
			result = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),
					Constant.iceConnectionInfo.getUserInfoEntity()).folderMaker(folderMaker);
		} catch (Exception ee) {
			result = new FolderMakerStatusEntity("");
		}

		List<uploadFileEntity> uploadFileList = new ArrayList<uploadFileEntity>();
		Hashtable<String, String> postDict = new Hashtable<String, String>();
		postDict.put(uploadFileEntity.UPDATE_PACK_KEY, packname);
		postDict.put(uploadFileEntity.SOURCE_OBJ_KEY, UploadThread.class.getName());
		String suggestFileGroup = "";
		/*if (Constant.getUploadPkgPermission().equals(Constant.RED_PERMISSION)) {
			suggestFileGroup = GroupTypeEntity.ADMINISTRATOR;
		} else if (EmeetingApplication.getUploadPkgPermission()
				.equals(EmeetingApplication.YELLOW_PERMISSION)) {
			suggestFileGroup = GroupTypeEntity.PRIVILEGE;
		} else if (EmeetingApplication.getUploadPkgPermission()
				.equals(EmeetingApplication.GREEN_PERMISSION)) {
			suggestFileGroup = GroupTypeEntity.ANONYMOUS;
		}*/

		postDict.put(uploadFileEntity.SUGGEST_PACK_KEY, suggestFileGroup);
		postDict.put(uploadFileEntity.REQUEST_GUID_KEY, requestGuid);

		Vector itemVector = Constant.dbConnection.getItemsByPakgeIndex(pakgeTable.getPakgeIndex());
		try {
			for(int i=0;i<itemVector.size();i++){
				ItemTable itemTable = (ItemTable) itemVector.get(i);
				String title = itemTable.getItemTitle();
				String tag = itemTable.getItemTag();
				String guid = itemTable.getItemGuid() + new Date().getTime();
				String filePath = itemTable.getItemFilePath();
				String desc = itemTable.getItemDescription();
				String fileType = itemTable.getItemType();
//				totalSize+=new File(filePath).length();
				// 构造要上传的文件信息
				uploadFileEntity oneUploadFile = new uploadFileEntity(title,
						desc, tag, guid, filePath, fileType);
				uploadFileList.add(oneUploadFile);
			}

			uploadFileEntity[] uploadFileSubmitArray = new uploadFileEntity[uploadFileList
					.size()];
			uploadFileList.toArray(uploadFileSubmitArray);
			
			ICETable iceTable = Constant.dbConnection.queryICE(Integer.parseInt(pakgeTable.getIceIndex()));
			
			
			progressEntity = new DownloadProgressEntity();
			// 上传的时候iceName使用数据库里的name，跟下载不一样，下载的时候iceName使用当前应用连接的iceName
			progressEntity.setPackageGuid(pakgeTable.getPakgeGuid());
			String dateStr = pakgeTable.getPakgeDownloadDate();
			dateStr = dateStr.substring(0, dateStr.indexOf(" "));
			progressEntity.setIceDate(dateStr);
			progressEntity.setPakgeName(pakgeTable.getPakgeName());
			progressEntity.setPakgeIndex((long)pakgeTable.getPakgeIndex());
			progressEntity.setIceIndex(Long.valueOf(pakgeTable.getIceIndex()));
			
			detailStatus = uploadFileRepeat(postDict, uploadFileSubmitArray,progressEntity);
			uploadingPackageName = null;
//			Constant.downLoadingMap.clear();
             for (int i = 0; i < detailStatus.length; i++){
                 Log.d("UploadThread",detailStatus[i].filename + " 上传 " + detailStatus[i].status+" guid:" + detailStatus[i].guid+" fileURI"+detailStatus[i].fileUri);
             }
             if (detailStatus.length == 0){
             	Log.d("UploadThread","没有上传成功神马东西");
             }
			Constant.uploadingMap.remove(progressEntity.getPackageGuid());
			Log.d("UploadThread", "detailStatus:"+detailStatus);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
	
	
	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	
	// 上传失败时尝试五次重传
		private UploadFileDetailStatusEntity[] uploadFileRepeat(
				Hashtable<String, String> dict, uploadFileEntity[] FileArray,DownloadProgressEntity downloadEntity) {
			boolean is = false;
			UploadFileDetailStatusEntity[] detailStatus = null;
			int i = 0;
			while (!is && i < 5) {
				try {
					i++;
					detailStatus = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),
							Constant.iceConnectionInfo.getUserInfoEntity())
							.uploadLocalFile(dict, FileArray);
					if(HttpHelper.isStopUploading){
	            		break;
	            	}
					if (detailStatus.length == 0) {
						is = false;
						this.sleep(200);
					} else {
						is = true;
					}
				} catch (SocketException e) {
					is = false;
				} catch (JSONException e) {
					is = false;
				} catch (Exception e) {
					is = false;
				}
			}
			return detailStatus;
		}

		@Override
		public void raiseConnectionRetryFailEvent() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raiseConnectionWillRetryEvent() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raiseConnectionRetrySuccessEvent() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raiseSearchingUpdateWithServiceList(
				ICELoginEntity[] iceArray) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raiseHttpUploadingEvent(String filename, long filesize,
				long uploadingByte, long batchUploadBytes, long totalFileSize) {
			// TODO Auto-generated method stub
			if(batchUploadBytes <= totalFileSize){
				float rate = ((float)batchUploadBytes/totalFileSize) * 100;
				Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaaarate", "filename:"+filename);
				Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaaarate", "filesize:"+filesize);
				Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaaarate", "uploadingByte:"+uploadingByte);
				Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaaarate", "batchUploadBytes:"+batchUploadBytes);
				Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaaarate", "totalSize:"+totalFileSize);
//				Constant.downLoadingMap.put("pkgIndex", (long)pakgeTable.getPakgeIndex());
//				Constant.downLoadingMap.put("progress", rate);
				if(IntAreaUtil.isFresh(freshArray,rate)){ 
					if(progressEntity != null){
						progressEntity.setProgress((int) rate);
						Constant.uploadingMap.put(progressEntity.getPackageGuid(), progressEntity);
						synchronized (Constant.freshQueue) {
							Constant.freshQueue.add(progressEntity);
							Constant.freshQueue.notify();
						}
					}
				}
				if(batchUploadBytes >= totalFileSize){
					Constant.uploadingMap.remove(progressEntity.getPackageGuid());
				}
			}
		}

		@Override
		public void raiseConnectionFailedEvent(ICELoginEntity LoginICE,
				UserInfoEntity userEntity, ConnectFailedStatus failedStatus) {
			// TODO Auto-generated method stub
			
		}
	
}
