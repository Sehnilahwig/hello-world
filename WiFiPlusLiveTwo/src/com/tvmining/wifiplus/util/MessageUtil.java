/**
 * project name:(eMeeting)
 * create  time:2013-1-10
 * author:liujianjian
 */
package com.tvmining.wifiplus.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class MessageUtil {

	/**
	 * 
	 */
	public MessageUtil() {
	}
	
	public static void showToast(Context con,String msg){
		Toast.makeText(con, msg,Toast.LENGTH_SHORT).show();
	}
	
	//自定义Toast显示位置的信息
	public static void toastInfo(Context con,String info){
		Toast toast = Toast.makeText(con, info, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP|Gravity.CENTER, 0,0);
		toast.show();
	}
}
