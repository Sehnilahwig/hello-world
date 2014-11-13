package com.tvmining.wifiplus.entity;

import java.io.Serializable;
import java.util.Vector;

import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;

public class DownloadProgressEntity implements Serializable{

	private long iceIndex;
	private String iceDate;
	private String pakgeName;
	private long pakgeIndex;
	private int progress;
	private String packageGuid;
	public String getPackageGuid() {
		return packageGuid;
	}
	public void setPackageGuid(String packageGuid) {
		this.packageGuid = packageGuid;
	}
	public long getIceIndex() {
		return iceIndex;
	}
	public void setIceIndex(long iceIndex) {
		this.iceIndex = iceIndex;
	}
	public String getIceDate() {
		return iceDate;
	}
	public void setIceDate(String iceDate) {
		this.iceDate = iceDate;
	}
	public String getPakgeName() {
		return pakgeName;
	}
	public void setPakgeName(String pakgeName) {
		this.pakgeName = pakgeName;
	}
	public long getPakgeIndex() {
		return pakgeIndex;
	}
	public void setPakgeIndex(long pakgeIndex) {
		this.pakgeIndex = pakgeIndex;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
}
