package com.tvmining.wifiplus.entity;

import java.io.File;

import com.tvmining.wifiplus.util.Constant;

public class ICETable {

	private int iceIndex;
	private String iceName;
	private String iceSmallIconPath;
	private String iceMediumIconPath;
	private String iceBigIconPath;
	private String iceDate;
	private String iceExtentOne;
	private String iceExtentTwo;
	private String iceExtentThree;
	private String iceExtentFour;
	private String iceExtentFive;
	private String iceDataType;

	public String getIceDataType() {
		return iceDataType;
	}

	public void setIceDataType(String iceDataType) {
		this.iceDataType = iceDataType;
	}

	public int getIceIndex() {
		return iceIndex;
	}

	public void setIceIndex(int iceIndex) {
		this.iceIndex = iceIndex;
	}

	public String getIceName() {
		return iceName;
	}

	public void setIceName(String iceName) {
		this.iceName = iceName;
	}

	public String getIceSmallIconPath() {
		return iceSmallIconPath;
	}

	public void setIceSmallIconPath(String iceSmallIconPath) {
		this.iceSmallIconPath = iceSmallIconPath;
	}

	public String getIceMediumIconPath() {
		return iceMediumIconPath;
	}

	public void setIceMediumIconPath(String iceMediumIconPath) {
		this.iceMediumIconPath = iceMediumIconPath;
	}

	public String getIceBigIconPath() {
		return iceBigIconPath;
	}

	public void setIceBigIconPath(String iceBigIconPath) {
		this.iceBigIconPath = iceBigIconPath;
	}

	public String getIceDate() {
		return iceDate;
	}

	public void setIceDate(String iceDate) {
		this.iceDate = iceDate;
	}

	public String getIceExtentOne() {
		return iceExtentOne;
	}

	public void setIceExtentOne(String iceExtentOne) {
		this.iceExtentOne = iceExtentOne;
	}

	public String getIceExtentTwo() {
		return iceExtentTwo;
	}

	public void setIceExtentTwo(String iceExtentTwo) {
		this.iceExtentTwo = iceExtentTwo;
	}

	public String getIceExtentThree() {
		return iceExtentThree;
	}

	public void setIceExtentThree(String iceExtentThree) {
		this.iceExtentThree = iceExtentThree;
	}

	public String getIceExtentFour() {
		return iceExtentFour;
	}

	public void setIceExtentFour(String iceExtentFour) {
		this.iceExtentFour = iceExtentFour;
	}

	public String getIceExtentFive() {
		return iceExtentFive;
	}

	public void setIceExtentFive(String iceExtentFive) {
		this.iceExtentFive = iceExtentFive;
	}

	public void setValues(String date,String icePath,String iceName,String iceDataType) {
		this.setIceBigIconPath("");
		this.setIceDate(date);
		this.setIceMediumIconPath("");
		this.setIceName(iceName);
		this.setIceDataType(iceDataType);
		this.setIceSmallIconPath(icePath + File.separator + Constant.ICE_SMALLE_NAME);
	}

}
