package com.tvmining.wifiplus.entity;

import android.content.Context;
import android.graphics.Bitmap;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.wifiplus.util.Constant;

public class PakgeTable {

	private int pakgeIndex;
	private String iceIndex;
	private String itemCount;
	private String pakgeName;
	private String pakgeType;
	private String pakgeGuid;
	private String pakgeOwnerTvmID;
	private String pakgeID;
	private String pakgeGroupID;
	private String pakgeSubmitDate;
	private String pakgeDownloadDate;

	private String pakgeSmallIconPath;
	private String pakgeMediumIconPath;
	private String pakgepath;
	private String pakgeExtentOne;
	private String pakgeExtentTwo;
	private String pakgeExtentThree;
	private String pakgeExtentFour;
	private String pakgeExtentFive;
	private int status;//0：初始化数据库记录默认值，1：增量更新，2：下载完成，3：下载失败或者暂停
	
	private Bitmap bitmap;
	private Bitmap circleBitmap;
	private String tempPakgeName;
	private float progress;
	private String pakgeDataType;
	
	public int dip2px(Context context,float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	public String getPakgeDataType() {
		return pakgeDataType;
	}

	public void setPakgeDataType(String pakgeDataType) {
		this.pakgeDataType = pakgeDataType;
	}

	public String getTempPakgeName() {
		return tempPakgeName;
	}

	public void setTempPakgeName(String tempPakgeName) {
		this.tempPakgeName = tempPakgeName;
	}

	public Bitmap getCircleBitmap() {
		return circleBitmap;
	}

	public void setCircleBitmap(Bitmap circleBitmap) {
		this.circleBitmap = circleBitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	public int getPakgeIndex() {
		return pakgeIndex;
	}

	public void setPakgeIndex(int pakgeIndex) {
		this.pakgeIndex = pakgeIndex;
	}

	public String getIceIndex() {
		return iceIndex;
	}

	public void setIceIndex(String iceIndex) {
		this.iceIndex = iceIndex;
	}

	public String getItemCount() {
		return itemCount;
	}

	public void setItemCount(String itemCount) {
		this.itemCount = itemCount;
	}

	public String getPakgeName() {
		return pakgeName;
	}

	public void setPakgeName(String pakgeName) {
		this.pakgeName = pakgeName;
	}

	public String getPakgeType() {
		return pakgeType;
	}

	public void setPakgeType(String pakgeType) {
		this.pakgeType = pakgeType;
	}

	public String getPakgeGuid() {
		return pakgeGuid;
	}

	public void setPakgeGuid(String pakgeGuid) {
		this.pakgeGuid = pakgeGuid;
	}

	public String getPakgeOwnerTvmID() {
		return pakgeOwnerTvmID;
	}

	public void setPakgeOwnerTvmID(String pakgeOwnerTvmID) {
		this.pakgeOwnerTvmID = pakgeOwnerTvmID;
	}

	public String getPakgeID() {
		return pakgeID;
	}

	public void setPakgeID(String pakgeID) {
		this.pakgeID = pakgeID;
	}

	public String getPakgeGroupID() {
		return pakgeGroupID;
	}

	public void setPakgeGroupID(String pakgeGroupID) {
		this.pakgeGroupID = pakgeGroupID;
	}

	public String getPakgeSubmitDate() {
		return pakgeSubmitDate;
	}

	public void setPakgeSubmitDate(String pakgeSubmitDate) {
		this.pakgeSubmitDate = pakgeSubmitDate;
	}

	public String getPakgeDownloadDate() {
		return pakgeDownloadDate;
	}

	public void setPakgeDownloadDate(String pakgeDownloadDate) {
		this.pakgeDownloadDate = pakgeDownloadDate;
	}

	public String getPakgeSmallIconPath() {
		return pakgeSmallIconPath;
	}

	public void setPakgeSmallIconPath(String pakgeSmallIconPath) {
		this.pakgeSmallIconPath = pakgeSmallIconPath;
	}

	public String getPakgeMediumIconPath() {
		return pakgeMediumIconPath;
	}

	public void setPakgeMediumIconPath(String pakgeMediumIconPath) {
		this.pakgeMediumIconPath = pakgeMediumIconPath;
	}

	public String getPakgepath() {
		return pakgepath;
	}

	public void setPakgepath(String pakgepath) {
		this.pakgepath = pakgepath;
	}

	public String getPakgeExtentOne() {
		return pakgeExtentOne;
	}

	public void setPakgeExtentOne(String pakgeExtentOne) {
		this.pakgeExtentOne = pakgeExtentOne;
	}

	public String getPakgeExtentTwo() {
		return pakgeExtentTwo;
	}

	public void setPakgeExtentTwo(String pakgeExtentTwo) {
		this.pakgeExtentTwo = pakgeExtentTwo;
	}

	public String getPakgeExtentThree() {
		return pakgeExtentThree;
	}

	public void setPakgeExtentThree(String pakgeExtentThree) {
		this.pakgeExtentThree = pakgeExtentThree;
	}

	public String getPakgeExtentFour() {
		return pakgeExtentFour;
	}

	public void setPakgeExtentFour(String pakgeExtentFour) {
		this.pakgeExtentFour = pakgeExtentFour;
	}

	public String getPakgeExtentFive() {
		return pakgeExtentFive;
	}

	public void setPakgeExtentFive(String pakgeExtentFive) {
		this.pakgeExtentFive = pakgeExtentFive;
	}

	public void setValues(String date, long iceIndex, PackInfoEntity entity,
			String count, String pkgPath, ICESDK mySDK,String pakgeDataType) {
		this.setIceIndex(String.valueOf(iceIndex));
		this.setItemCount(count);
		this.setPakgeDownloadDate(date);
		this.setPakgeGroupID(entity.res_groupid);
		this.setPakgeGuid(entity.thumb_guid);
		this.setPakgeID(String.valueOf(entity.id));
		// middle
		this.setPakgeMediumIconPath("");
		this.setPakgeName(entity.tempPackName);
		this.setPakgeOwnerTvmID(entity.owner_tvmid);
		this.setPakgepath(pkgPath);
		// small
		if(Constant.QRCODE_ACTION_SHARE_PACKAGE.equals(entity.action)){
			this.setPakgeSmallIconPath(entity.packageIconPath);
		}else{
			this.setPakgeSmallIconPath(mySDK.getPackThumbByPackInfo(entity, 330,
					240));
		}
		
		this.setPakgeSubmitDate(entity.submit_date);
		this.setPakgeType(entity.pack_type);
		this.setStatus(0);
		this.setPakgeDataType(pakgeDataType);
		this.setPakgeExtentTwo(entity.action);
		this.setPakgeExtentOne(String.valueOf(entity.packageSize));
	}
	
	public void setInitValues(String date,int count,long iceIndex,String pakgeDataType,String iconPath,String pakgeName,String pkgPath) {
		this.setIceIndex(String.valueOf(iceIndex));
		this.setItemCount(String.valueOf(2));
		this.setPakgeDownloadDate(date);
		this.setPakgeGroupID("groupId");
		this.setPakgeGuid("thumb_guid");
		this.setPakgeID("id");
		// middle
		this.setPakgeMediumIconPath("");
		this.setPakgeName(pakgeName);
		this.setPakgeOwnerTvmID("tvmid");
		this.setPakgepath(iconPath);
		// small
		this.setPakgeSmallIconPath(iconPath);
		this.setPakgeSubmitDate("submit_date");
		this.setPakgeType("false");
		this.setStatus(2);
		this.setCircleBitmap(null);
		this.setProgress(100);
		this.setPakgeDataType(pakgeDataType);
	}
}
