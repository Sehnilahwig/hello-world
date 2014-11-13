package com.tvmining.wifiplus.entity;

import android.graphics.Bitmap;

public class InteractGalleryEntity {
	
	private String imageUrl;
	private Bitmap thumbnailBitmap;
	private Bitmap sourceBitmap;
	private String fileType;
	private String itemGuid;
	private String smallImageUrl;
	private String videoUrl;
	private String answerType;

	public String getAnswerType() {
		return answerType;
	}

	public void setAnswerType(String answerType) {
		this.answerType = answerType;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getSmallImageUrl() {
		return smallImageUrl;
	}

	public void setSmallImageUrl(String smallImageUrl) {
		this.smallImageUrl = smallImageUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Bitmap getThumbnailBitmap() {
		return thumbnailBitmap;
	}

	public void setThumbnailBitmap(Bitmap thumbnailBitmap) {
		this.thumbnailBitmap = thumbnailBitmap;
	}

	public Bitmap getSourceBitmap() {
		return sourceBitmap;
	}

	public void setSourceBitmap(Bitmap sourceBitmap) {
		this.sourceBitmap = sourceBitmap;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getItemGuid() {
		return itemGuid;
	}

	public void setItemGuid(String itemGuid) {
		this.itemGuid = itemGuid;
	}
	
	

}
