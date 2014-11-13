package com.tvmining.sdk.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class FolderMakerStatusEntity {
	/// <summary>
    /// 成功的代码
    /// </summary>
    public final static int SUCC_CODE = 0;

    /// <summary>
    /// 内部失败
    /// </summary>
    public final static int FAILED_INNER_CODE = -1;

    /// <summary>
    /// 创建的状态号
    /// </summary>
    public int code;

    /// <summary>
    /// 创建的详细信息
    /// </summary>
    public String msg ;

    public FolderMakerStatusEntity(String rawJson){
    	JSONObject seJSON;
		//List<PackInfoEntity> packInfoList = new ArrayList<PackInfoEntity>(); 
		
    	try {
			seJSON = new JSONObject(rawJson);
			code = seJSON.getInt("code");
			msg = seJSON.getString("msg");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			code = FAILED_INNER_CODE;
		}
    }
}
