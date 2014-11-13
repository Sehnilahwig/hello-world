package com.tvmining.wifiplus.receiver;

import java.io.File;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.tvmining.wifiplus.util.Constant;

public class CompleteReceiver extends BroadcastReceiver {

	private DownloadManager downloadManager;
	private Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		String action = intent.getAction();
		if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			Toast.makeText(context, "下载完成了....", Toast.LENGTH_LONG).show();

			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			if (Constant.downloaderId == id) {
				installApk(Constant.downloadVersionFile);
			}
		}
	}

	protected void installApk(File file) {

		if (file.toString().endsWith(".apk")) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		}

	}

}
