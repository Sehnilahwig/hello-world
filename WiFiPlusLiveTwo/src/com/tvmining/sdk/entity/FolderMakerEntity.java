package com.tvmining.sdk.entity;

import java.util.Hashtable;

public class FolderMakerEntity {
	 /// <summary>
    /// 创建的包名
    /// </summary>
    public String packname;

    /// <summary>
    /// 提示文件夹权限
    /// </summary>
    public String suggestGroup;

    /// <summary>
    /// 请求 Guid
    /// </summary>
    public String requestGuid;

    /// <summary>
    /// 上传数目
    /// </summary>
    public Integer uploadNum;

    /// <summary>
    /// 包类型
    /// </summary>
    public String packType;

    /// <summary>
    /// 转换成 HTTP 的上传
    /// </summary>
    /// <returns></returns>
    public Hashtable<String, String> convertToHttpCond()
    {
    	Hashtable<String, String> postCond = new Hashtable<String, String>();
        
        postCond.put("res_pack", packname);
        postCond.put("suggest_groupid", suggestGroup);
        postCond.put("owner_tvmid", UserInfoEntity.tvmId);
        postCond.put("request_guid", requestGuid);
        postCond.put("upload_file_num", String.valueOf(uploadNum));
        postCond.put("pack_type", packType);

        return postCond;
    }
}
