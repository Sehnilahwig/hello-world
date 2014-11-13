package com.tvmining.sdk.entity;

import org.json.JSONArray;
import org.json.JSONObject;

public class StatusEntity {
	public final static String OK = "0";
    public final static String FAIL = "-11";
    public final static String UNDELIVER = "-22";
    public final static String UNPARSE = "-33";
    public final static String NULL = "-44";

    public String cmd;
    public String status;
    public JSONArray msg;

    public StatusEntity(){
    }
    public StatusEntity(String json){
    	try {
    		JSONObject seJSON = new JSONObject(json);
			
    		cmd = seJSON.getString("cmd");
    		status = seJSON.getString("status");
    		msg = seJSON.getJSONArray("msg");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
    		 
        
    }
}
