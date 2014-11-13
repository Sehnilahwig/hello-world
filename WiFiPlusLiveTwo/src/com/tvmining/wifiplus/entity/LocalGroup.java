package com.tvmining.wifiplus.entity;

public class LocalGroup {

	private String title;
	private String iceDate;
	private ICETable iceTable;

	public String getIceDate() {
		return iceDate;
	}

	public void setIceDate(String iceDate) {
		this.iceDate = iceDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ICETable getIceTable() {
		return iceTable;
	}

	public void setIceTable(ICETable iceTable) {
		this.iceTable = iceTable;
	}

}
