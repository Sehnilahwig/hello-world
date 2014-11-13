package com.tvmining.wifiplus.thread;

import java.io.File;

import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifipluseq.R;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

public class UpdateVersionInfo extends AsyncTask<Object, Object, Object> {

	private String fileUrl;
	private Context mContext;

	public UpdateVersionInfo(String fileUrl,Context mContext) {
		this.fileUrl = fileUrl;
		this.mContext = mContext;
	}

	protected Object doInBackground(Object... args) {
		try {
			addToSystemDownloader(fileUrl);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

	protected void onPostExecute(Object compareVector) {
	}
	
	private void addToSystemDownloader(String fileUrl){
	    DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
		
		Uri uri = Uri.parse(fileUrl);
		Request request = new Request(uri);

		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

        // 发出通知，既后台下载

		request.setShowRunningNotification(true);

        // 显示下载界面

		request.setVisibleInDownloadsUi(true);

		request.setTitle(mContext.getResources().getString(R.string.title));
        // 设置下载后文件存放的位置
		request.setDestinationInExternalPublicDir("osspad","EQ.apk");

		Constant.downloadVersionFile = new File(Environment.getExternalStoragePublicDirectory("osspad"),"EQ.apk");

		if(Constant.downloadVersionFile.exists()){
			Constant.downloadVersionFile.delete();
		}
		
        // 将下载请求放入队列

		Constant.downloaderId = downloadManager.enqueue(request);


	}
}
