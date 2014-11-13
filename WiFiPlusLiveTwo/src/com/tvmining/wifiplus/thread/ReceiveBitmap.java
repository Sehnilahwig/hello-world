package com.tvmining.wifiplus.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;
import com.tvmining.sdk.entity.SearchFileEntity;
import com.tvmining.sdk.entity.SideThumbMethod;
import com.tvmining.wifiplus.entity.InteractGalleryEntity;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ImageUtil;

public class ReceiveBitmap extends AsyncTask<Void, Void, Vector> {

	private String guid;
	private String answerType;
	private Handler handler;

	public ReceiveBitmap(String guid,String answerType,Handler handler) {
		this.guid = guid;
		this.answerType = answerType;
		this.handler = handler;
	}

	protected Vector doInBackground(Void... args) {

		try {
			receiveBitmapByCommand(guid);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	protected void onPostExecute(Vector compareVector) {
	}

	public void receiveBitmapByCommand(String questionguid) {
		SearchFileDetailStatusEntity[] result = null;
		String path = "";

		try {
			if (questionguid != null && !"".equals(questionguid)) {
				Constant.questionGalleryEntity = new InteractGalleryEntity();
				ICESDK mySDK = ICESDK.sharedICE(
						Constant.iceConnectionInfo.getLoginICE(),
						Constant.iceConnectionInfo.getUserInfoEntity());

				List guidList = new ArrayList<String>();
				guidList.add(questionguid);
				// 根据guid查找文件的类型
				SearchFileEntity searchFileCond = new SearchFileEntity();
				searchFileCond.guid = guidList;

				result = mySDK.searchFile(searchFileCond);
				String hostName = "http://" + mySDK.getConnectHostName();

				if (result != null && result.length > 0) {
					if (result[0].height > result[0].width) {
						path = hostName
								+ result[0].getSideThumbURI(
										Constant.MAX_HEIGHT,
										SideThumbMethod.height);
					} else {
						path = hostName
								+ result[0].getSideThumbURI(
										Constant.MAX_HEIGHT,
										SideThumbMethod.width);
					}
					String smallPath = mySDK
							.getThumbURLByFileDetail(
									result[0],
									Constant.screenWidth
											/ Constant.IMAGE_COLUMN,
									(Constant.screenWidth * 3 / Constant.IMAGE_COLUMN) / 4);

					Bitmap smallBitmap = ImageUtil
							.downloadImageNoZoom(smallPath);
					Bitmap largeBitmap = ImageUtil.downloadImage(path);
					
					if ("VIDEO".equals(result[0].file_type)) {
						Constant.questionGalleryEntity.setVideoUrl(hostName
								+ File.separator + result[0].getFileURI());
					}

					Constant.questionGalleryEntity.setImageUrl(path);
					Constant.questionGalleryEntity
							.setFileType(result[0].file_type);
					Constant.questionGalleryEntity.setItemGuid(result[0].guid);
					Constant.questionGalleryEntity.setSmallImageUrl(smallPath);

					
					Constant.questionGalleryEntity
							.setThumbnailBitmap(smallBitmap);
					Constant.questionGalleryEntity.setAnswerType(answerType);
					
					Constant.questionGalleryEntity.setSourceBitmap(largeBitmap);
					
					Message msg = new Message();
					handler.sendMessage(msg);
				}

			}else{
				Constant.questionGalleryEntity = null;
			}

		} catch (Exception ee) {
			Log.d("CommandService", "搜索文件的出错:", ee);
		}
	}
}