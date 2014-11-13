package com.tvmining.wifiplus.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.CommandTypeEntity;
import com.tvmining.sdk.entity.NeighbourEntity;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;
import com.tvmining.sdk.entity.SearchFileEntity;
import com.tvmining.sdk.entity.SideThumbMethod;
import com.tvmining.wifiplus.entity.InteractGalleryEntity;
import com.tvmining.wifiplus.entity.Permission;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifipluseq.R;

/**
 * 接收服务器发送过来的指令
 * @author Administrator
 *
 */
public class CommandService extends Service{

	public static NeighbourEntity entity;
	public static int itemWidth;//屏幕宽度
	
	public static Handler handler;
	public static boolean forceFollow;
	public static String cacheBody;
	public static boolean isCaching;
	public static String goBody;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		handler = new Handler() {
			public void handleMessage(Message msg) {
				if(msg.obj != null && msg.obj instanceof InteractGalleryEntity){//跟随指令的处理
					if(Constant.activity.isInInteract()){
						Constant.activity.setInteractView((InteractGalleryEntity)msg.obj);
					}
					
					if(Constant.activity.isOnlineGalleryShow()){
						Constant.activity.followScreen(((InteractGalleryEntity)msg.obj).getItemGuid());
					}
				}
			}
		};
		
		//注册接收命令的接收者
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("system_command");
        this.registerReceiver(new CommandReceiver(), intentFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	//命令接收者 用于接收中控发来的指令
	class CommandReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String cmdAction = intent.getAction();
			if(cmdAction.equals("system_command")){
				String cmdType = intent.getStringExtra("cmdType");
				String body = intent.getStringExtra("body");
				String from = intent.getStringExtra("from");
				
				try{
					Log.d("CommandService", "cmd type:"+cmdType);
					Log.d("CommandService", "body:"+body);
	                //如果指令是GO,则去看看guid
					if(cmdType.equalsIgnoreCase(CommandTypeEntity.GO)){//跟随指令
						
						cacheBody = body;
						goBody = body;
						List objectList = new ArrayList();
						objectList.add(body);
						objectList.add(handler);
						
						synchronized (Constant.followScreenQueue) {
							Constant.followScreenQueue.add(objectList);
							Constant.followScreenQueue.notify();
						}
	                }else if(cmdType.equalsIgnoreCase(CommandTypeEntity.FORCE)){//答题指令
	                	if(Constant.user.getPermisssion().getLevel()!=null && !Permission.PERMISSION_HIGH.equals(Constant.user.getPermisssion().getLevel())){
	                		//直接解析得到URL,然后得到bitmap
	                    	JSONObject jsonObject = new JSONObject(body);
	                    	String power = jsonObject.getString("power");
	                    	String action = jsonObject.getString("action");
	                    	
	                    	if("on".equals(power)){
	                    		if("answer".equals(action)){
		                    		if(Permission.PERMISSION_MIDDLE.equals(Constant.user.getPermisssion().getLevel())){
		                    			
		                    			String guid = jsonObject.getString("guid");
		                    			String type = jsonObject.getString("type");
		                    			// type:0选择答题，1圈画答题
		                    			if(guid != null && !"".equals(guid)){
		                    				Constant.forceAnswer = true;
		                    				Constant.activity.answerQuestion(guid,type);
		                    			}else{
		                    				Constant.activity.createWhiteCanvas();
		                    			}
			                		}
		                    	}
	                    	}else if("off".equals(power)){
	                    		if("answer".equals(action)){
	                    			Utility.cancelAnswer();
	                    		}
	                    	}
	                	}
	                }else if(cmdType.equalsIgnoreCase(CommandTypeEntity.CACHE)){//cache指令
	                	synchronized (Constant.cacheFollowScreenQueue) {
	                		if(!isCaching && Constant.cacheFollowScreenQueue.size() == 0){
	                			if(cacheBody != null){
	                				String tranBody = new String(cacheBody);
		                    		Constant.cacheFollowScreenQueue.add(tranBody);
		                    		Constant.cacheFollowScreenQueue.notify();
	                			}
	                    	}
	                	}
	                }else{
	                	Log.d("CommandService", "cmd type:"+cmdType);
	                }
	            }catch (Exception e) {
	            	Log.e("CommandService", "command receive error");
	            }
			}
		}
	}
	
}
