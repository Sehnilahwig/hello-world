package com.tvmining.wifiplus.thread;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;

import com.tvmining.wifiplus.entity.DownloadProgressEntity;
import com.tvmining.wifiplus.util.Constant;

public class ProgressFreshThread extends Thread {
	private Context context;
	
	public ProgressFreshThread(Context mContext) {
		context = mContext;
	}

	public void run() {
		try {
			while (true) {
				if (!Constant.freshQueue.isEmpty()) {
					
					DownloadProgressEntity progressEntity = null;
					
					synchronized (Constant.freshQueue) {
						progressEntity = (DownloadProgressEntity) Constant.freshQueue
								.take();
					}
	            	/*String iceName = progressEntity.getIceName();
	            	String iceDate = progressEntity.getIceDate();
	            	String pakgeName = progressEntity.getPakgeName();
	            	long pakgeIndex = progressEntity.getPakgeIndex();
	            	float progress =  progressEntity.getProgress();
	            	long iceIndex = progressEntity.getIceIndex();*/
					 
//	            	progressEntity.setPositions(Constant.activity.getDownloadChildPosition(progressEntity));
					Message msg = new Message();
					msg.what = Constant.HANDLER_DOWNLOAD_PROGRESS_FRESH;
					msg.obj = progressEntity;
					Constant.activity.getHandler().sendMessage(msg);
					
				} else {
					synchronized (Constant.freshQueue) {
						Constant.freshQueue.wait();
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