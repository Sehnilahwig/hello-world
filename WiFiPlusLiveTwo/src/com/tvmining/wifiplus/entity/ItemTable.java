package com.tvmining.wifiplus.entity;

import java.io.File;

import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;

public class ItemTable {

	private int itemIndex;
	private String pakgeIndex;
	private String itemTitle;
	private String itemType;
	private String itemGuid;
	private String itemTag;
	private String itemHeight;
	private String itemWidth;
	private String itemFileName;
	private String itemIconPath;
	private String itemFilePath;

	private String itemDescription;
	private String itemOwnerTvmID;
	private String itemGroupID;
	private String itemExtentOne;
	private String itemExtentTwo;
	private String itemExtentThree;
	private String itemExtentFour;
	private String itemExtentFive;
	private String itemDataType;

	public String getItemDataType() {
		return itemDataType;
	}

	public void setItemDataType(String itemDataType) {
		this.itemDataType = itemDataType;
	}

	public int getItemIndex() {
		return itemIndex;
	}

	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}

	public String getPakgeIndex() {
		return pakgeIndex;
	}

	public void setPakgeIndex(String pakgeIndex) {
		this.pakgeIndex = pakgeIndex;
	}

	public String getItemTitle() {
		return itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getItemGuid() {
		return itemGuid;
	}

	public void setItemGuid(String itemGuid) {
		this.itemGuid = itemGuid;
	}

	public String getItemTag() {
		return itemTag;
	}

	public void setItemTag(String itemTag) {
		this.itemTag = itemTag;
	}

	public String getItemHeight() {
		return itemHeight;
	}

	public void setItemHeight(String itemHeight) {
		this.itemHeight = itemHeight;
	}

	public String getItemWidth() {
		return itemWidth;
	}

	public void setItemWidth(String itemWidth) {
		this.itemWidth = itemWidth;
	}

	public String getItemFileName() {
		return itemFileName;
	}

	public void setItemFileName(String itemFileName) {
		this.itemFileName = itemFileName;
	}

	public String getItemIconPath() {
		return itemIconPath;
	}

	public void setItemIconPath(String itemIconPath) {
		this.itemIconPath = itemIconPath;
	}

	public String getItemFilePath() {
		return itemFilePath;
	}

	public void setItemFilePath(String itemFilePath) {
		this.itemFilePath = itemFilePath;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getItemOwnerTvmID() {
		return itemOwnerTvmID;
	}

	public void setItemOwnerTvmID(String itemOwnerTvmID) {
		this.itemOwnerTvmID = itemOwnerTvmID;
	}

	public String getItemGroupID() {
		return itemGroupID;
	}

	public void setItemGroupID(String itemGroupID) {
		this.itemGroupID = itemGroupID;
	}

	public String getItemExtentOne() {
		return itemExtentOne;
	}

	public void setItemExtentOne(String itemExtentOne) {
		this.itemExtentOne = itemExtentOne;
	}

	public String getItemExtentTwo() {
		return itemExtentTwo;
	}

	public void setItemExtentTwo(String itemExtentTwo) {
		this.itemExtentTwo = itemExtentTwo;
	}

	public String getItemExtentThree() {
		return itemExtentThree;
	}

	public void setItemExtentThree(String itemExtentThree) {
		this.itemExtentThree = itemExtentThree;
	}

	public String getItemExtentFour() {
		return itemExtentFour;
	}

	public void setItemExtentFour(String itemExtentFour) {
		this.itemExtentFour = itemExtentFour;
	}

	public String getItemExtentFive() {
		return itemExtentFive;
	}

	public void setItemExtentFive(String itemExtentFive) {
		this.itemExtentFive = itemExtentFive;
	}

	public void setValues(long pakgeIndex,
			SearchFileDetailStatusEntity searchEntity, String pkgPath,
			PackInfoEntity entity, String iconPath,String itemDataType) {
		this.setItemDescription(searchEntity.desc);
		this.setItemFileName(searchEntity.filename);
		this.setItemFilePath(pkgPath +File.separator + searchEntity.filename);
		this.setItemGroupID(entity.res_groupid);
		this.setItemGuid(searchEntity.guid);
		this.setItemHeight(String.valueOf(searchEntity.height));
		this.setItemIconPath(iconPath);
		this.setItemOwnerTvmID(searchEntity.owner_tvmid);
		this.setItemTag(searchEntity.tag);
		this.setItemTitle(searchEntity.title);
		this.setItemType(searchEntity.file_type);
		this.setItemWidth(String.valueOf(searchEntity.width));
		this.setPakgeIndex(String.valueOf(pakgeIndex));
		this.setItemDataType(itemDataType);
	}
	
	public void setInitValues(long pakgeIndex,int height,int width,String itemDataType,String itemFileName,String itemFilePath,String iconPath) {
		this.setItemDescription("desc");
		this.setItemFileName(itemFileName);
		this.setItemFilePath(itemFilePath);
		this.setItemGroupID("res_groupid");
		this.setItemGuid("guid");
		this.setItemHeight(String.valueOf(height));
		this.setItemIconPath(iconPath);
		this.setItemOwnerTvmID("owner_tvmid");
		this.setItemTag("tag");
		this.setItemTitle("itemTitle");
		this.setItemType("IMAGE");
		this.setItemWidth(String.valueOf(width));
		this.setPakgeIndex(String.valueOf(pakgeIndex));
		this.setItemDataType(itemDataType);
	}

}
