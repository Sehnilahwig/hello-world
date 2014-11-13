package com.tvmining.wifiplus.entity;

public class DownloadTempTable {

	private int tempIndex;
	private int downloadIndex;
	private String downloadedSize;
	private String downloadTotalSize;
	private int downloadedItemWhich;
	private String downloadItemGuid;
	private String downloadedItemSize;
	private String downloadItemTotalSize;
	private int downloadStatus;//0：初始化数据库记录默认值，1：下载失败，2：下载完成

	public void setValues(int downloadIndex, String downloadedSize,
			String downloadTotalSize, int downloadedItemWhich,
			String downloadItemGuid, String downloadedItemSize,
			String downloadItemTotalSize, int downloadStatus) {

		this.downloadIndex = downloadIndex;
		this.downloadedSize = downloadedSize;
		this.downloadTotalSize = downloadTotalSize;
		this.downloadedItemWhich = downloadedItemWhich;
		this.downloadItemGuid = downloadItemGuid;
		this.downloadedItemSize = downloadedItemSize;
		this.downloadItemTotalSize = downloadItemTotalSize;
		this.downloadStatus = downloadStatus;
	}

	public int getTempIndex() {
		return tempIndex;
	}

	public void setTempIndex(int tempIndex) {
		this.tempIndex = tempIndex;
	}

	public int getDownloadIndex() {
		return downloadIndex;
	}

	public void setDownloadIndex(int downloadIndex) {
		this.downloadIndex = downloadIndex;
	}

	public String getDownloadedSize() {
		return downloadedSize;
	}

	public void setDownloadedSize(String downloadedSize) {
		this.downloadedSize = downloadedSize;
	}

	public String getDownloadTotalSize() {
		return downloadTotalSize;
	}

	public void setDownloadTotalSize(String downloadTotalSize) {
		this.downloadTotalSize = downloadTotalSize;
	}

	public int getDownloadedItemWhich() {
		return downloadedItemWhich;
	}

	public void setDownloadedItemWhich(int downloadedItemWhich) {
		this.downloadedItemWhich = downloadedItemWhich;
	}

	public String getDownloadItemGuid() {
		return downloadItemGuid;
	}

	public void setDownloadItemGuid(String downloadItemGuid) {
		this.downloadItemGuid = downloadItemGuid;
	}

	public String getDownloadedItemSize() {
		return downloadedItemSize;
	}

	public void setDownloadedItemSize(String downloadedItemSize) {
		this.downloadedItemSize = downloadedItemSize;
	}

	public String getDownloadItemTotalSize() {
		return downloadItemTotalSize;
	}

	public void setDownloadItemTotalSize(String downloadItemTotalSize) {
		this.downloadItemTotalSize = downloadItemTotalSize;
	}

	public int getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(int downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

}
