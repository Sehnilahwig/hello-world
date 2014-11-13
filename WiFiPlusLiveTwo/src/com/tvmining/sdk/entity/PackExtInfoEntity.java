package com.tvmining.sdk.entity;

public class PackExtInfoEntity {
	
	/**
	 * 天脉号
	 */
	public String tvmid;
	
	/**
	 * 搜索的包名称
	 */
	public String packname;
	
	/**
	 * 得到包拓展信息
	 */
	public static String URL_GET_PACK_EXT_INFO = "ice3/search_extpackinfo.php?tvmid=";
}
