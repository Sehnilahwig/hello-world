package com.tvmining.wifiplus.waterfall.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2012-11-1
 * @desc   : 网络工具类
 */
public class NetworkUtil {

	/**
	 * 返回网络是否可用。需要权限：
	 * <p><b> < uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /> </b></p>
	 * @param context
	 * @return
	 */
	public static boolean isAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isAvailable();
	}
	
	/**
	 * 返回Wifi是否启用
	 * @param c
	 * @return
	 */
	public static boolean isWIFIActivate(Context c) {
		return ((WifiManager) c.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
	}
	
	/**
	 * 修改Wifi状态
	 * @param c
	 * @param status
	 */
	public static void changeWIFIStatus(Context c, boolean status) {
		((WifiManager) c.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(status);
	}
}
