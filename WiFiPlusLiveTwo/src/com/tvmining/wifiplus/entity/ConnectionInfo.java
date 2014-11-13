package com.tvmining.wifiplus.entity;

import com.tvmining.sdk.entity.ICELoginEntity;
import com.tvmining.sdk.entity.UserInfoEntity;

public class ConnectionInfo {

	private UserInfoEntity userInfoEntity ;
	private ICELoginEntity loginICE ;
	public ICELoginEntity getLoginICE() {
		return loginICE;
	}
	public void setLoginICE(ICELoginEntity loginICE) {
		this.loginICE = loginICE;
	}
	public UserInfoEntity getUserInfoEntity() {
		return userInfoEntity;
	}
	public void setUserInfoEntity(UserInfoEntity userInfoEntity) {
		this.userInfoEntity = userInfoEntity;
	} 
}
