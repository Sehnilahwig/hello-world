/**
 * project name:(emeeting)
 * create  time:2013-1-17
 * author:liujianjian
 */
package com.tvmining.wifiplus.application;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tvmining.sdk.entity.CommandTypeEntity;
import com.tvmining.sdk.entity.NeighbourEntity;
import com.tvmining.wifiplus.db.DBConnection;
import com.tvmining.wifiplus.entity.ConnectionInfo;
import com.tvmining.wifiplus.httpserver.PersonalService;
import com.tvmining.wifiplus.thread.CacheFollowScreenThread;
import com.tvmining.wifiplus.thread.DBThread;
import com.tvmining.wifiplus.thread.DownloadThread;
import com.tvmining.wifiplus.thread.FollowScreenThread;
import com.tvmining.wifiplus.thread.ListDirectorysTask;
import com.tvmining.wifiplus.thread.PackageDownloadThread;
import com.tvmining.wifiplus.thread.ProgressFreshThread;
import com.tvmining.wifiplus.thread.UploadThread;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.DelFile;
import com.tvmining.wifiplus.util.MessageUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifipluseq.R;

public class EmeetingApplication extends Application {
	
	

	public static String TAG = "EmeetingApplication";
	
	private static Context mContext;
	
	
private static int downloadUnInsertedCount = 0;
	
	private static int unDownloadedCount = 0;
	
	public static int getDownloadUnInsertedCount() {
		return downloadUnInsertedCount;
	}

	private static String uploadPkgPermission;// 上传包权限 用户登录后将用户权限设置为默认包上传权限
	public static ProgressDialog pd = null;// 搜索中控提示信息

	PkgCommandReceiver packageReceiver;
	IntentFilter intentFilter;
	
	public static String getUploadPkgPermission() {
		return uploadPkgPermission;
	}

	public synchronized static void setDownloadUnInsertedCount(int downloadUnInsertedCount) {
		Log.d("aaaaaa", "------>:"+downloadUnInsertedCount);
		EmeetingApplication.downloadUnInsertedCount = downloadUnInsertedCount;
	}

	public synchronized static int getUnDownloadedCount() {
		return unDownloadedCount;
	}

	public synchronized static void setUnDownloadedCount(int unDownloadedCount) {
		EmeetingApplication.unDownloadedCount = unDownloadedCount;
	}
	
	
	private void regToWx(){
		Constant.api = WXAPIFactory.createWXAPI(this, Constant.APP_ID,true);
		Constant.api.registerApp(Constant.APP_ID);
	}
	
	public Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "调用Handler的handlemessage方法");
			switch(msg.what){
				case 0:
					reLoadDirectory();
					break;
				default:
					break;
			}
		}
	};
	
	
	@Override
	public void onCreate() {
		super.onCreate();

		mContext = this.getApplicationContext();
		
		Constant.cacheMap = new HashMap();

		Constant.dbConnection = new DBConnection(mContext);
//		Constant.dbConnection.removeUnDownloadedPakgeRecord();
		Constant.queue = new LinkedBlockingQueue();
		Constant.packageDownloadQueue = new LinkedBlockingQueue();
		Constant.sqlQueue = new LinkedBlockingQueue();
		Constant.freshQueue = new LinkedBlockingQueue();
		Constant.uploadQueue = new LinkedBlockingQueue();
		Constant.followScreenQueue = new LinkedBlockingQueue();
		Constant.cacheFollowScreenQueue = new LinkedBlockingQueue();

		Constant.downloadThread = new DownloadThread(mContext);
		Constant.downloadThread.start();
		Constant.packageDownloadThread = new PackageDownloadThread(mContext);
		Constant.packageDownloadThread.start();
		Constant.dbThread = new DBThread(mContext);
		Constant.dbThread.start();
		Constant.progressThread = new ProgressFreshThread(mContext);
		Constant.progressThread.start();
		Constant.uploadThread = new UploadThread(mContext);
		Constant.uploadThread.start();
		Constant.followScreenThread = new FollowScreenThread(mContext);
		Constant.followScreenThread.start();
		Constant.cacheFollowScreenThread = new CacheFollowScreenThread(mContext);
		Constant.cacheFollowScreenThread.start();

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Constant.screenWidth = wm.getDefaultDisplay().getWidth();// 屏幕宽度
		Constant.screenHeight = wm.getDefaultDisplay().getHeight();
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
		    	
				regToWx();
		    	
		    	Utility.setTvmId(mContext);
		    	
		    	Utility.setDefaultPassword(mContext);
				
				DelFile.delete(new File(Constant.INTERACT_CACHE_PATH));
			}
			
		}).start();
		
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			Constant.sbar = getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			Constant.sbar = 50;
		}
		 
		 DisplayMetrics displaymetrics = getResources()
					.getDisplayMetrics();
		Constant.SCALE = displaymetrics.density;
		 
		 Constant.iceConnectionInfo = new ConnectionInfo();
		 Constant.contentResolver = mContext.getContentResolver();
		 
		 Constant.downloadingMap = new ConcurrentHashMap();
		 Constant.uploadingMap = new ConcurrentHashMap();
		 
		 /*Constant.imageCacheManager = ImageCacheManager.getImageCacheService(mContext,
					ImageCacheManager.MODE_NO_CACHE_USED, "nocache");
		 Constant.imageCacheManager.setMax_Memory(1024 * 1024 * 1024);
			
		 Constant. imageCacheManager = ImageCacheManager.getImageCacheService(mContext,
				 	ImageCacheManager.MODE_FIXED_TIMED_USED, "time");
		 Constant.imageCacheManager.setDelay_millisecond(3 * 60 * 1000);*/
			
		 /*Constant.imageCacheManager = ImageCacheManager.getImageCacheService(mContext,
				 	ImageCacheManager.MODE_LEAST_RECENTLY_USED, "num");
		 Constant.imageCacheManager.setMax_num(3000);*/
		 
		 mContext.startService(new Intent(mContext,PersonalService.class));
		 packageReceiver = new PkgCommandReceiver();
		 intentFilter = new IntentFilter();
	      intentFilter.addAction("system_command");
	      this.registerReceiver(packageReceiver, intentFilter);
	}


	// 当前选中的屏幕
	private static Map<String, NeighbourEntity> selectMap = new HashMap<String, NeighbourEntity>();

	public static Map<String, NeighbourEntity> getSelectMap() {
		return selectMap;
	}

	
	//命令接收者 用于接收中控发来的指令
		class PkgCommandReceiver extends BroadcastReceiver{
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if(action.equals("system_command")){
					String cmdType = intent.getStringExtra("cmdType");
					String body = intent.getStringExtra("body");
					Log.i(TAG,"cmdType"+cmdType+";body="+body);
					if(cmdType.equals(CommandTypeEntity.NEWFILE)){
//						MessageUtil.toastInfo(context,getString(R.string.newfile)+body.substring(0,body.indexOf(":")));
					}else if(cmdType.equals(CommandTypeEntity.NEWPACK)){
						MessageUtil.toastInfo(context,getString(R.string.newpkg)+body);
						handler.sendEmptyMessage(0);
					}else if(cmdType.equals(CommandTypeEntity.DELPACK)){
						MessageUtil.toastInfo(context,getString(R.string.delpkg)+body);
						handler.sendEmptyMessage(0);
					}else if(cmdType.equals(CommandTypeEntity.CLEANALL)){
						MessageUtil.toastInfo(context,getString(R.string.delallpkg));
					}else if(cmdType.equals(CommandTypeEntity.GO)){
//						MessageUtil.toastInfo(context,getString(R.string.receivego));
					}else if(cmdType.equals(CommandTypeEntity.DELFILE)){
//						MessageUtil.toastInfo(context,getString(R.string.delfile)+Utility.decodeUnicode(body));
					}
				}
			}
		}
		//在主页面有包增删时主动刷新
		public void reLoadDirectory(){
			//当包数量变动时,在这里执行刷新View操作
//			if(pd == null){
//				pd = new ProgressDialog(this);
//				pd.setCancelable(false);
//				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//				pd.setIndeterminate(false);
//				pd.show();
//			}
			
			ListDirectorysTask task = new ListDirectorysTask(this,null);
	        task.executeOnExecutor(Constant.LIMITED_TASK_EXCUTOR);
		}
		
		
		
}
