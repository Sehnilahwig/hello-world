package com.tvmining.wifiplus.entity;

import android.util.Log;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.GroupTypeEntity;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.Utility;


public class Permission {

	public static String PERMISSION_HIGH = "high";
	public static String PERMISSION_MIDDLE = "middle";
	public static String PERMISSION_LOW = "low";	
	
	private String level;
	
	public String getLevel() {
		return level;
	}

	public void dealLevel() {
		try {
			ICESDK mySdk = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),Constant.iceConnectionInfo.getUserInfoEntity());
			if(mySdk.getUserPower().equalsIgnoreCase(GroupTypeEntity.ANONYMOUS)){
				level = PERMISSION_LOW;
			}
			if (mySdk.getUserPower().equalsIgnoreCase(GroupTypeEntity.PRIVILEGE)){
				level = PERMISSION_MIDDLE;
				Utility.setMiddlePwd(Constant.activity.getApplicationContext());
			}
			if (mySdk.getUserPower().equalsIgnoreCase(GroupTypeEntity.ADMINISTRATOR)){
				level = PERMISSION_HIGH;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
