package com.tvmining.wifiplus.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateJsonBean implements Serializable{
	private String device;
	private String product;
	private String status;
	private String msg;
	
	private ArrayList<VersionInfo> versionList;

	public UpdateJsonBean(){
		
	}
	
	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public ArrayList<VersionInfo> getVersionList() {
		return versionList;
	}

	public void setVersionList(ArrayList<VersionInfo> versionList) {
		this.versionList = versionList;
	}

	public UpdateJsonBean(String device, String product, String status,
			String msg, ArrayList<VersionInfo> versionList) {
		this.device = device;
		this.product = product;
		this.status = status;
		this.msg = msg;
		this.versionList = versionList;
	}		
}
