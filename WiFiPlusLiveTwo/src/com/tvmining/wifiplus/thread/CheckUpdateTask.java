package com.tvmining.wifiplus.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tvmining.wifiplus.activity.MainActivity;
import com.tvmining.wifiplus.entity.UpdateJsonBean;
import com.tvmining.wifiplus.entity.VersionInfo;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.Utility;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

public class CheckUpdateTask extends AsyncTask<Object,Object,UpdateJsonBean>{
	
	private static final String TAG = "MainActivity";
//	String path = "http://update.tvmining.com/SoftUpdateServer/update/update?product=eq&device=Android&version=3.5.0.0_release&format=json";
	String path = "http://update.tvmining.com/SoftUpdateServer/update/update?product=eq37&device=Android&version=";
	String format = "&format=json";
	private StringBuffer checkUpdateJson = new StringBuffer();
	private Context mContext;
	private String currentRelease;
	
	public CheckUpdateTask(Context mContext){
		this.mContext = mContext;
	}
	
	@Override
	protected UpdateJsonBean doInBackground(Object... params) {
		UpdateJsonBean bean = null;
		URL url = null;
		try {
			currentRelease = Utility.getVersionInfo(mContext)[1]+"_release";
			path = path+currentRelease+format;
			url = new URL(path);
			HttpURLConnection urlConnection =  (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(3000);				
			BufferedReader reader= new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			
			String line;
			while((line = reader.readLine())!=null){
				checkUpdateJson.append(line);
			}
			reader.close();
			
			bean = parseJson(checkUpdateJson.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Log.i(TAG, "发生异常了",e);
		}
		return bean;
	}		
	
	@Override
	protected void onPostExecute(UpdateJsonBean bean) {	
		if(bean != null){
			Message msg = new Message();
			msg.what = Constant.HANDLER_APPLICAIOTN_UPDATE;
			msg.obj = bean.getVersionList().get(0);
			Constant.activity.getHandler().sendMessage(msg);
		}else{
			Message msg = new Message();
			msg.what = Constant.HANDLER_APPLICAIOTN_UPDATE;
			msg.obj = null;
			Constant.activity.getHandler().sendMessage(msg);
		}
	}
	
	public UpdateJsonBean parseJson(String str){
		UpdateJsonBean bean = null;
		boolean isReminder = true;
		try {
			JSONObject jsonObject = new JSONObject(str);
			
			String device = jsonObject.getString("device");
			String product = jsonObject.getString("product");
			String status = jsonObject.getString("status");
			String msg = "";
			if("FAILED".equals(status)){
				msg = jsonObject.getString("msg");
			}
			
			JSONArray jsonArray = jsonObject.getJSONArray("versionlist");
			
			Log.i(TAG,"device="+device+";\nproduct="+product+";\nstatus="+status+";\nmsg="+msg+";");
			
			Log.i(TAG, "jsonArray="+jsonArray.toString());
			
			ArrayList<VersionInfo> versionList = new ArrayList<VersionInfo>();
			JSONObject jsonItem;
			if(jsonArray!=null&&jsonArray.length()>0){
				for(int i=0;i<jsonArray.length();i++){
					jsonItem = (JSONObject) jsonArray.get(i);
					
					String addr = jsonItem.getString("addr");
					String describe = jsonItem.getString("describe");
					int isrollback = jsonItem.getInt("isrollback");
					int rule = jsonItem.getInt("rule");
					String version = jsonItem.getString("version");
					Log.i(TAG,"addr="+addr+";describe="+describe+";isrollback="+isrollback+";rule="+rule+";version="+version);
					
					VersionInfo netVersion = new VersionInfo(addr,describe,isrollback,rule,version);
					
					versionList.add(netVersion);
				}
			}
			
			bean = new UpdateJsonBean(device,product,status,msg,versionList);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		return bean;
	}	
	
}
