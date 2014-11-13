package com.tvmining.wifiplus.entity;

import java.io.Serializable;

public class VersionInfo implements Serializable{
	
	private String addr;
	private String describe;
	private int isrollback;
	private int rule;
	private String version;	
	private int isReminder;
	
	
	
	public int getIsReminder() {
		return isReminder;
	}



	public void setIsReminder(int isReminder) {
		this.isReminder = isReminder;
	}


	public String getAddr() {
		return addr;
	}



	public void setAddr(String addr) {
		this.addr = addr;
	}



	public String getDescribe() {
		return describe;
	}



	public void setDescribe(String describe) {
		this.describe = describe;
	}



	public int getIsrollback() {
		return isrollback;
	}



	public void setIsrollback(int isrollback) {
		this.isrollback = isrollback;
	}



	public int getRule() {
		return rule;
	}



	public void setRule(int rule) {
		this.rule = rule;
	}



	public String getVersion() {
		return version;
	}



	public void setVersion(String version) {
		this.version = version;
	}

	public VersionInfo(){
		
	}


	public VersionInfo(String addr, String describe, int isrollback,int rule, String version) {
		super();
		this.addr = addr;
		this.describe = describe;
		this.isrollback = isrollback;
		this.rule = rule;
		this.version = version;
	}	
}
