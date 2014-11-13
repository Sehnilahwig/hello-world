package com.tvmining.wifiplus.waterfall.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * <ul>
 * <li><b>name : </b>       PhoneUtil      </li>
 * <li><b>description :</b> 电话类</li>
 * <li></b>author : </b>    桥下一粒砂           </li>
 * <li><b>e-mail : </b>     chenyoca@gmail.com  </li>
 * <li><b>weibo : </b>      @桥下一粒砂          </li>
 * <li><b>date : </b>       2012-7-21 上午8:41:34</li>
 * </ul>
 */
public class PhoneUtil {

	/**
	 * 打电话
	 * @param activity
	 * @param phoneNumber
	 */
	public static void call(Activity activity, String phoneNumber){
		Intent dialIntent = new Intent(Intent.ACTION_CALL,Uri.parse(String.format("tel:%s", phoneNumber)));
		try{
		    activity.startActivity(dialIntent);
	    }catch(Exception e){
	        Toast.makeText(activity, 
	                "Cannot call! Security application intercepted this action !", Toast.LENGTH_SHORT).show();
	    }
	}
	 
	/**
	 * 发信息
	 * @param activity
	 * @param recver
	 * @param content
	 *
	 */
	public static void sms(Activity activity, String recver, String content){
		Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms://")); 
		sendIntent.putExtra("address", recver); 
		sendIntent.putExtra("sms_body", content);
		try{
			activity.startActivity(sendIntent);
	    }catch(Exception e){
	        Toast.makeText(activity, 
	                "Cannot send sms! Security application intercepted this action !", Toast.LENGTH_SHORT).show();
	    }
	}
	
	/**
	 * 获取手机号码
	 * @param context
	 * @return
	 */
	public static String getPhoneNum(Context context){
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}
	
}
