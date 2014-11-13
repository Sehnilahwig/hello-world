package com.tvmining.wifiplus.thread;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;
import com.tvmining.sdk.entity.SearchFileEntity;
import com.tvmining.sdk.entity.SideThumbMethod;
import com.tvmining.wifiplus.cache.ImageCacheManager;
import com.tvmining.wifiplus.entity.InteractGalleryEntity;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.wifiplus.util.Utility;

/**
 * 跟随指令的处理：根据guid来查找图片的大小、路径等信息
 * @author Administrator
 *
 */
public class FollowScreenThread extends Thread {
	private Context context;
	private List guidList = new ArrayList<String>();
	private SearchFileEntity searchFileCond = new SearchFileEntity();
	public static ImageCacheManager imageCacheManager;
	public FollowScreenThread(Context mContext) {
		context = mContext;
	}

	public void run() {
		try {
			while (true) {
				if (!Constant.followScreenQueue.isEmpty()) {
					guidList.clear();
					List objectList = null;
					String body = null;
					Handler handler = null;
					synchronized (Constant.followScreenQueue) {
						objectList = (List) Constant.followScreenQueue
								.take();
					}
					body = (String) objectList.get(0);
					handler = (Handler) objectList.get(1);
					if(body != null && !("".equals(body))){
						handler.sendEmptyMessage(1);
						SearchFileDetailStatusEntity[] result = null;
						String path = "";
						String fileType = "";
						String smallPath = "";
						
				        try{
				        	JSONObject jsonObject = new JSONObject(body);
				    		
				    		guidList.add(jsonObject.getString("guid"));//根据guid搜索
				    		//根据guid查找文件的类型
				    		
				    		searchFileCond.guid = guidList;
				    		ICESDK mySDK = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),Constant.iceConnectionInfo.getUserInfoEntity());
				             result = mySDK.searchFile(searchFileCond);
				             String hostName = "http://" + ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),Constant.iceConnectionInfo.getUserInfoEntity()).getConnectHostName();
				             
				             final InteractGalleryEntity galleryEntity = new InteractGalleryEntity();
				             if(result!=null&&result.length>0){
				 	        	fileType = result[0].file_type;
				 	        	
				 	        	path = hostName + result[0].getSideThumbURI(Constant.MAX_WIGTH, SideThumbMethod.width);//大图路径
				 	        	
//				 	        	path = hostName + File.separator + result[0].getFileURI();
				 	        	
				 	        	smallPath = mySDK
										.getThumbURLByFileDetail(result[0],
												Constant.screenWidth/Constant.IMAGE_COLUMN,
												(Constant.screenWidth * 3/Constant.IMAGE_COLUMN)/4);//小图的路径
				 	        	
				 	        	
				 	        	if("VIDEO".equals(fileType)){
				 	        		galleryEntity.setVideoUrl(hostName + File.separator + result[0].getFileURI());
				 	        	}
				 	        	
				         		galleryEntity.setImageUrl(path);
				         		galleryEntity.setFileType(fileType);
				         		galleryEntity.setItemGuid(result[0].guid);
				         		galleryEntity.setSmallImageUrl(smallPath);
				         		
				         		Bitmap smallBitmap = ImageUtil.downloadImageNoZoom(smallPath);//直接获取小图
			         			galleryEntity.setThumbnailBitmap(smallBitmap);
			         			
			         			Utility.checkRepeatedData(galleryEntity);
			         			Utility.removeExtraData();
			         			
			         			if(Constant.activity.isInInteract() || Constant.activity.isOnlineGalleryShow()){
			         				Message msg = new Message();
			         				msg.obj = galleryEntity;
			         				handler.sendMessage(msg);
			         			}
				 	         }
				             
				        }catch (Exception ee){
				             Log.d("CommandService","搜索文件的出错:",ee);
				        }
					}
					
				} else {
					synchronized (Constant.followScreenQueue) {
						Constant.followScreenQueue.wait();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int dip2px(float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	
}