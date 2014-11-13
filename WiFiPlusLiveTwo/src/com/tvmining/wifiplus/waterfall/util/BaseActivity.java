package com.tvmining.wifiplus.waterfall.util;

import java.util.Stack;

import android.app.Activity;
import android.content.Context;

/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2012-10-23
 * @desc   : Activity特性辅助工具
 */
public class BaseActivity {

	/** Activity引用  */
	public Activity self;
	
	/** Context引用 */
	public Context context;
	
	/**  双击退出模块 */
	private DClickExit mDClickExit;
	
	/** Activity栈 **/
	private static Stack<Activity> ActivityStack = new Stack<Activity>();
	
	/**
	 * <b>description :</b>		创建
	 * </br><b>time :</b>		2012-8-22 下午11:01:55
	 * @param self
	 */
	public BaseActivity(Activity act){
		self = act;
		context = self.getApplicationContext();
	}
	
	/**
	 * </br><b>description :</b>启用双击返回键退出程序
	 * </br><b>time :</b>		2012-7-18 下午9:24:41
	 */
	public void enabledDClickExit(){
		mDClickExit = new DClickExit(self);
	}
	
	/**
	 * <b>description :</b>		检查按键
	 * </br><b>time :</b>		2012-8-22 下午11:03:57
	 * @param keyCode
	 * @return
	 */
	public boolean checkExist(int keyCode) {
		if( null != mDClickExit ){
			return mDClickExit.doubleClickExit(keyCode);
		}else{
			return false;
		}
	}
	
	/**
	 * <b>description :</b>		检查按键
	 * </br><b>time :</b>		2012-8-22 下午11:03:57
	 * @param keyCode
	 * @return
	 */
	public boolean checkExist(int keyCode,String tip) {
		if( null != mDClickExit ){
			return mDClickExit.dClickExit(keyCode, tip, 2000);
		}else{
			return false;
		}
	}
	
	/**
	 * <b>description :</b>		将Activity压力栈中
	 * </br><b>time :</b>		2012-9-12 下午3:59:13
	 * @param activity
	 */
	public static void pushActivity(Activity activity){
		ActivityStack.add(activity);
	}
	
	/**
	 * <b>description :</b>		将顶端的Activity弹出栈顶
	 * </br><b>time :</b>		2012-9-12 下午3:59:55
	 */
	public static Activity popActivity(){
		return ActivityStack.lastElement();
	}
	
	/**
	 * <b>description :</b>		Activity栈回退到某个Activity
	 * </br><b>time :</b>		2012-9-12 下午4:01:19
	 * @param target
	 */
	public static void rollbackTo(Class<? extends Activity> target){
		for(Activity activity : ActivityStack){
			if(activity.getClass().equals(target)){
				//已经是目标Activity，退出循环
				break;
			}else{
				//不是目标栈，关闭它
				activity.finish();
			}
		}
	}
	
}
