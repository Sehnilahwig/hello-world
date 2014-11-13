package com.tvmining.sdk.entity;

import java.io.Serializable;
import java.util.Map;
import java.util.Vector;

import android.graphics.drawable.Drawable;

import com.tvmining.sdk.helper.HttpHelper;

public class PackInfoEntity implements Serializable {
	// / <summary>
	// / 拥有者信息
	// / </summary>
	public String owner_tvmid;

	// / <summary>
	// / 不重复的索引号
	// / </summary>
	public int id;

	// / <summary>
	// / 包名
	// / </summary>
	public String packname;

	// / <summary>
	// / 权限组
	// / </summary>
	public String res_groupid;

	// / <summary>
	// / 包生成日期
	// / </summary>
	public String submit_date;

	// / <summary>
	// / 包类型
	// / </summary>
	public String pack_type;

	// / <summary>
	// / 包的缩略图 guid
	// / </summary>
	public String thumb_guid;
	// add by like
	public Drawable drawable;
	public long pkgIndex;
	public long iceIndex;
	public String tempPackName;
	public long packageSize;
	public Vector<SearchFileDetailStatusEntity> compareVector;
	public Map itemMap;
	public SearchFileDetailStatusEntity[] result;
	public boolean packageUpdate;
	public boolean isPause;
	public String iceName;
	public String packageIconPath;
	public int itemCount;
	public String ipport;
	public String action = "ice";// 扫描二维码中的字段

	// end

	public PackInfoEntity() {

	}

	public PackInfoEntity(String packname, String pack_type, String thumb_guid,
			String res_groupid) {
		super();
		this.packname = packname;
		this.pack_type = pack_type;
		this.thumb_guid = thumb_guid;
		this.res_groupid = res_groupid;
	}

	public String getPackThumb() {
		if (thumb_guid.length() == 0) {
			return "";
		}
		String uri = null;
		try {
			uri = String.format("/resource/%s/%s.jpg",
					HttpHelper.UrlEncode(packname),
					HttpHelper.UrlEncode(thumb_guid));
		} catch (Exception ex) {
		}
		return uri;
	}

	public String getPackThumb(int width, int height) {
		if (thumb_guid.length() == 0) {
			return "";
		}
		String uri = null;
		try {
			uri = String.format("/resource/%s/%s.jpg_%d_%d.jpg",
					HttpHelper.UrlEncode(packname),
					HttpHelper.UrlEncode(thumb_guid), width, height);
		} catch (Exception ex) {
		}

		return uri;
	}
}
