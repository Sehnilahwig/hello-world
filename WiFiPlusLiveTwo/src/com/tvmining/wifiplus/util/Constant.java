package com.tvmining.wifiplus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.ContentResolver;
import android.os.Environment;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tvmining.sdk.entity.NeighbourEntity;
import com.tvmining.wifiplus.activity.MainActivity;
import com.tvmining.wifiplus.cache.ImageCacheManager;
import com.tvmining.wifiplus.db.DBConnection;
import com.tvmining.wifiplus.entity.ConnectionInfo;
import com.tvmining.wifiplus.entity.InteractGalleryEntity;
import com.tvmining.wifiplus.entity.User;
import com.tvmining.wifiplus.thread.CacheFollowScreenThread;
import com.tvmining.wifiplus.thread.DBThread;
import com.tvmining.wifiplus.thread.DownloadThread;
import com.tvmining.wifiplus.thread.FollowScreenThread;
import com.tvmining.wifiplus.thread.PackageDownloadThread;
import com.tvmining.wifiplus.thread.ProgressFreshThread;
import com.tvmining.wifiplus.thread.ReceiveCmdThread;
import com.tvmining.wifiplus.thread.UploadThread;
import com.tvmining.wifiplus.view.LayoutCalculator;

public class Constant {

	public static int screenWidth;
	public static int screenHeight;
	public static int sbar = 50;
	public static LayoutCalculator lc;
	public static final int HANDLER_SETTING = 0;
	public static final int HANDLER_WATERFALL_LOCAL_SHOW = 1;
	public static final int HANDLER_WATERFALL_LOCAL_HIDE = 2;
	public static final int HANDLER_ONLINEL_SEARCHICE_SUCCESS = 3;
	public static final int HANDLER_ONLINEL_SEARCHICE_FAILURE = 4;
	public static final int HANDLER_ONLINEL_LOGIN_SUCCESS = 5;
	public static final int HANDLER_ONLINEL_LOGIN_FAILURE = 6;
	public static final int HANDLER_ONLINEL_LOAD_PACKAGES = 7;
	public static final int HANDLER_WATERFALL_ONLINE_SHOW = 8;
	public static final int HANDLER_WATERFALL_ONLINE_HIDE = 9;
	public static final int HANDLER_LOCAL_LOAD_DATA = 10;
	public static final int HANDLER_WATERFALL_ITEM_CLICK = 11;
	public static final int HANDLER_DOWNLOAD_PROGRESS_FRESH = 12;
	public static final int HANDLER_ENTER_LOCAL_VIEW = 13;
	public static final int HANDLER_ONLINEL_LOGIN_CANCEL = 14;
	public static final int HANDLER_LOCAL_SHARE_CREATE_QRCODE = 15;
	public static final int HANDLER_LOCAL_QRCODE_SHOW = 16;
	public static final int HANDLER_LOCAL_QRCODE_HIDE = 17;
	public static final int HANDLER_LOCAL_RESPONSE_NOTIFICATION = 18;
	public static final int HANDLER_APP_SHARE_DOWNLOAD = 19;
	public static final int HANDLER_APP_SHARE_FILE_CHECK = 20;
	public static final int HANDLER_APP_SHARE_SHOW_QRCODE = 21;
	public static final int HANDLER_PERMISSION_CHANGE = 22;
	public static final int HANDLER_REMOTE_SHOW = 23;
	public static final int HANDLER_APPLICAIOTN_UPDATE = 24;
	public static final int HANDLER_DOWNLOAD_AUTO_PAUSE = 25;

	public static boolean wifiConnected = true;

	public static ConnectionInfo iceConnectionInfo;

	public static final String LOGIN_NOTHING = "nothing";
	public static final String LOGIN_ON = "loginon";
	public static final String LOGIN_SUCCESS = "success";
	public static final String LOGIN_FAILURE = "failure";
	public static String loginStatus = LOGIN_NOTHING;

	public static User user;

	public static ReceiveCmdThread receiveCmdThread;

	public static int ONLINE_WIDTH = 292;
	public static int ONLINE_HEIGHT = 225;

	public static String IMAGE_CACHE = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "Emeeting"
			+ File.separator + "Cache" + File.separator;
	public static String headPath = Environment.getExternalStorageDirectory()
			+ File.separator + "Emeeting" + File.separator + "head.jpg";
	public static String ICE_SMALLE_PATH = "/resource/private_mobile/android_xhdpi_bg_small.jpg";
	public static String ICE_SMALLE_NAME = "android_xhdpi_bg_small.jpg";
	public static String savePath = Environment.getExternalStorageDirectory()
			+ File.separator + "Emeeting" + File.separator;

	public static int MAX_HEIGHT = 2048;
	public static int MAX_WIGTH = 2048;

	public static ContentResolver contentResolver;

	public static String FROM_LOCAL = "local";
	public static String FROM_GRID = "grid";
	public static String FROM_WATER = "water";

	public static String VIDEO_SUFFIX = "jpg_w_720.jpg";
	public static final String IMAGE_CACHE_DIR = "thumbs";

	public static boolean isVibratorOpen = true;

	// 已经匹配的屏幕信息
	public static ArrayList<NeighbourEntity> matchScreen = new ArrayList<NeighbourEntity>();
	// 所有搜索到的屏幕
	public static ArrayList<NeighbourEntity> allScreenList = new ArrayList<NeighbourEntity>();

	public static boolean matchRun;

	public static MainActivity activity;
	public static String COMMUNICATION_TYPE = "COMMUNICATION";// 包的类型为交流
	public static final Executor LIMITED_TASK_EXCUTOR = (Executor) Executors
			.newFixedThreadPool(2);
	public static final Executor LIMITED_TASK_EXCUTOR_SEARCH_SCREEN = (Executor) Executors
			.newFixedThreadPool(1);
	public static int MAX_WIGTH_ONLINE_WATERFALL_VIDEO = 720;
	public static int MAX_WIGTH_ONLINE_WATERFALL = 308;

	public static String tvmId;

	public static IWXAPI api;

	public static long downloaderId;
	public static File downloadVersionFile;
	public static boolean lockScreen = false;// 点击讲解的时候设置为true

	public static int VIDEO_WIDTH = 512;
	public static int VIDEO_HEIGHT = 384;

	public static DBConnection dbConnection;

	public static String uploadOrDownload = "nothing";
	public static String DO_UPLOAD = "upload";
	public static String DO_DOWNLOAD = "download";
	public static String DO_NOTHING = "nothing";

	public static LinkedBlockingQueue queue;
	public static LinkedBlockingQueue packageDownloadQueue;
	public static LinkedBlockingQueue sqlQueue;
	public static LinkedBlockingQueue freshQueue;
	public static LinkedBlockingQueue uploadQueue;
	public static LinkedBlockingQueue followScreenQueue;
	public static LinkedBlockingQueue cacheFollowScreenQueue;

	public static DownloadThread downloadThread;
	public static PackageDownloadThread packageDownloadThread;

	public static UploadThread uploadThread;
	public static DBThread dbThread;
	public static FollowScreenThread followScreenThread;
	public static ProgressFreshThread progressThread;
	public static CacheFollowScreenThread cacheFollowScreenThread;

	public static float PROGRESS_DOWNLOAD_ALL_SIZE = 100f;

	public static boolean queuePause;

	public static int PKG_ICON_WIDTH = 100;
	public static int PKG_ICON_HEIGHT = 75;

	public static float SCALE;

	public static ConcurrentHashMap downloadingMap;
	public static ConcurrentHashMap uploadingMap;
	public static int unDownloadedCount = 0;
	public static Object unDownloadedCountLock = new Object();

	public static int DOWNLOAD_WAIT = 0;
	public static int DOWNLOAD_READD = 1;
	public static int DOWNLOAD_FINISHED = 2;
	public static int DOWNLOAD_FAIL = 3;
	public static int DOWNLOAD_PAUSE = 4;
	public static int DOWNLOADING = 5;

	public static String CONDITION_NAME = ".condition.doc";

	public static String SHARE_APP_DIR_PATH = Constant.savePath + "install_app"
			+ File.separator;
	public static String SHARE_APP_PLIST_NAME = "EQ.plist";
	public static String SHARE_APP_HTML_NAME = "install.html";
	public static String SHARE_APP_PLIST_PATH = SHARE_APP_DIR_PATH
			+ SHARE_APP_PLIST_NAME;
	public static String SHARE_WEB_ROOT_DIR = "dir";
	public static String SHARE_APP_IOS_FORWORD = "itms-services://?action=download-manifest&url=";

	public static String INTERACT_CACHE_PATH = savePath + "interact"
			+ File.separator + "cache" + File.separator;

	public static String QRCODE_ACTION_REMOTE_SCREEN = "remote_screen";// 通过qrcode控制屏幕
	public static String QRCODE_ACTION_SHARE_PACKAGE = "share_package";// qrcode分享中的下载

	public static String INTERACT_CAMERA_FILE_PATH = savePath
			+ "camera_answer.jpg";

	public static String LOCAL_GALLYERY_PATH = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "DCIM"
			+ File.separator + "Photo" + File.separator + "myShareImage";

	public static Vector<InteractGalleryEntity> followGalleryEntityVector = new Vector<InteractGalleryEntity>();

	public static int INTERACT_COUNT = 5;

	public static Object interactLock = new Object();

	public static boolean isFollowing;

	public static final int IMAGE_COLUMN = 5;

	public static ImageCacheManager imageCacheManager;

	public static Map interactCacheMap = new HashMap();

	public static final int TAB_INTERACT = 0;
	public static final int TAB_REMOTE = 1;
	public static final int TAB_ONLINE = 2;
	public static final int TAB_LOCAL = 3;

	public static final int EDIT_SIZE_SMALL = 2;
	public static final int EDIT_SIZE_MIDDLE = 4;
	public static final int EDIT_SIZE_LARGE = 6;

	public static final Map allExceptionsMap = new HashMap();

	public static String defaultPwd = "xx";

	public static final String APP_ID = "wxcf0b45efa1d0cf29";

	public static final String PREFERENCES_NAME = "password";

	public static boolean isRun = false;

	public static Map cacheMap;

	public static InteractGalleryEntity questionGalleryEntity = null;

	public static boolean forceAnswer;

	// 当前选中的屏幕
	private static Map<String, NeighbourEntity> selectMap = new HashMap<String, NeighbourEntity>();

	public static Map<String, NeighbourEntity> getSelectMap() {
		return selectMap;
	}

	public static void setSelectMap(Map<String, NeighbourEntity> selectMap) {
		Constant.selectMap = selectMap;
	}
}
