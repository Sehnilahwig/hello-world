package com.tvmining.wifiplus.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.tvmining.wifiplus.service.CommandService;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;
import com.tvmining.sdk.entity.SearchFileEntity;
import com.tvmining.sdk.entity.SearchFileOrderMethod;
import com.tvmining.sdk.entity.SideThumbMethod;

public class CacheFollowScreenThread extends Thread {
	
	private int currentItemIndex = -1;
	private Context mContext;

	public CacheFollowScreenThread(Context mContext) {
		this.mContext = mContext;
	}

	public void run() {
		try {
			while (true) {
				if (!Constant.cacheFollowScreenQueue.isEmpty()) {
					CommandService.isCaching = true;
					SearchFileEntity searchFileCond = new SearchFileEntity();
					String body = null;
					synchronized (Constant.cacheFollowScreenQueue) {
						body = (String) Constant.cacheFollowScreenQueue
								.take();
					}
					if (body != null && !("".equals(body))) {
						SearchFileDetailStatusEntity[] result = null;
						String path = "";

						try {
							JSONObject jsonObject = new JSONObject(body);

							searchFileCond.guid.add(jsonObject.getString("guid"));
							ICESDK mySDK = ICESDK.sharedICE(
									Constant.iceConnectionInfo.getLoginICE(),
									Constant.iceConnectionInfo.getUserInfoEntity());
							result = mySDK.searchFile(
									searchFileCond);
							String hostName = "http://"
									+ mySDK
											.getConnectHostName();

							SearchFileEntity condition = new SearchFileEntity();
							condition.orderColumn = "submit_date";
							condition.orderMethod = SearchFileOrderMethod.ASC;
							condition.inPack.add(result[0].packname);// 将包名字作为查询条件
							
							SearchFileDetailStatusEntity[] allItems = mySDK.searchFile(condition);
							
							Vector<SearchFileDetailStatusEntity> cacheVector = new Vector<SearchFileDetailStatusEntity>();
							for(int i=0;i<allItems.length;i++){
								if(result[0].guid.equals(allItems[i].guid)){
									currentItemIndex = i;
									// previous
									int previous = currentItemIndex - 1;
									while(previous >= 0){
										if(!Constant.cacheMap.containsKey(allItems[previous].guid)){
											if(allItems[previous].file_type.equals("IMAGE")){
												cacheVector.add(allItems[currentItemIndex - 1]);
												break;
											}
										}
										previous--;
									}
									
									int next = currentItemIndex + 1;
									while(next < allItems.length){
										if(!Constant.cacheMap.containsKey(allItems[next].guid)){
											if(allItems[next].file_type.equals("IMAGE")){
												cacheVector.add(allItems[next]);
												break;
											}
										}
										next++;
									}
									
									break;
								}
							}
							
							for(int i=0;i<cacheVector.size();i++){
								path = hostName
										+ cacheVector.get(i)
												.getSideThumbURI(
														Constant.screenWidth,
														SideThumbMethod.width);
								Bitmap bitmap = ImageUtil.downloadImageNoZoom(path);
								
								ImageUtil.saveBitmapToDisk(new File(Constant.IMAGE_CACHE+cacheVector.get(i).guid+".jpg"), bitmap, Constant.IMAGE_CACHE);
//								ImageUtil.saveImage(bitmap,EmeetingApplication.IMAGE_CACHE+cacheVector.get(i).guid+".jpg");
								Constant.cacheMap.put(cacheVector.get(i).guid, Constant.IMAGE_CACHE+cacheVector.get(i).guid+".jpg");
								Log.d("FollowScreen", "save image to disk:"+cacheVector.get(i).guid);
								if(bitmap != null && !bitmap.isRecycled()){
									bitmap.recycle();
								}
							}
							cacheVector.clear();
						} catch (Exception ee) {
							Log.d("CommandService", "搜索文件的出错:", ee);
						}finally{
							CommandService.isCaching = false;
						}
					}

				} else {
					synchronized (Constant.cacheFollowScreenQueue) {
						Constant.cacheFollowScreenQueue.wait();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}