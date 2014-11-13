package com.tvmining.sdk.entity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadUserInfoStatusEntity {
	/// 返回成功
    /// </summary>
    public static int SUCC = 0;

    /// <summary>
    /// 内部分析出错
    /// </summary>
    public static int FAILED_INNER_CODE = -10;

    /// <summary>
    /// 返回的代码
    /// </summary>
    public int code;

    /// <summary>
    /// 返回的信息
    /// </summary>
    public JSONArray msg;

    public UploadUserInfoStatusEntity(String rawJson){
    	 	JSONObject seJSON;
    		//List<PackInfoEntity> packInfoList = new ArrayList<PackInfoEntity>(); 
    		if(rawJson.length() == 0){
    			code = UploadUserInfoStatusEntity.FAILED_INNER_CODE;
    			return;
    		}
    		
        	try {
    			seJSON = new JSONObject(rawJson);
    			code = seJSON.getInt("code");
    			msg = seJSON.getJSONArray("msg");
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			code = UploadUserInfoStatusEntity.FAILED_INNER_CODE;
    		}
    }
    
    /// <summary>
    /// 得到 autoplay 的原值
    /// </summary>
    /// <returns></returns>
    public String getAutoPlay()
    {
    	
    	String autoplay = "";
    	
    	/*
    	try {
			if(msg.getString("autoplay") != null){
				autoplay = msg.getString("autoplay");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	*/
        return autoplay;
    }

    /// <summary>
    /// 得到 autoplay 的数组
    /// </summary>
    /// <returns></returns>
    public String[] getAutoPlayArray(){
    	String[] autoplay = new String[0];
    	/*
    	try {
	    	if(msg.getString("autoplay") != null){
				autoplay = msg.getString("autoplay").split(",");
	    	}else{
	    		autoplay = new String[0];
	    	}
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			autoplay = new String[0];
		}
    	*/
        return autoplay;
    }

    
}
