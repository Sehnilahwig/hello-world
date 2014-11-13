package com.tvmining.sdk.entity;

import org.json.JSONException;
import org.json.JSONObject;


public class ICETimeStatusEntity {
    /// <summary>
    /// 得到时间的 URI
    /// </summary>
    public final static String URL_TIMER = "ice3/file_time.php";

    /// <summary>
    /// 好的状态
    /// </summary>
    public final static int OK = 0;

    /// <summary>
    /// 失败的状态
    /// </summary>
    public final static int FAIL = -1;

    /// <summary>
    /// 得到的代码
    /// </summary>
    public int code;

    /// <summary>
    /// 得到信息体
    /// </summary>
    //public Hashtable<String, String> msg;
    public JSONObject msg;


    public ICETimeStatusEntity(String rawJson){
    	JSONObject seJSON;
		try {
			seJSON = new JSONObject(rawJson);
			code = seJSON.getInt("code");
			msg = seJSON.getJSONObject("msg");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			code = FAIL;
		}
		
    }
    
    /// <summary>
    /// 得到第一个资源的生成时间
    /// </summary>
    /// <returns></returns>
    public String getOldestResourceTime() { 
        String ret = "";

        try {
		    ret = (String) msg.get("first");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = "";
		}

        return ret;
    }

    /// <summary>
    /// 得到最后一个资源的生成时间
    /// </summary>
    /// <returns></returns>
    public String getNewestResourceTime()
    {
    	String ret = "";

        try {
		    ret = (String) msg.get("tail");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return ret;
     }

    /// <summary>
    /// 得到中控当前时间
    /// </summary>
    /// <returns></returns>
    public String getICECurrentTime()
    {
    	String ret = "";

        try {
		    ret = (String) msg.get("current_time");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return ret;
    }

}
