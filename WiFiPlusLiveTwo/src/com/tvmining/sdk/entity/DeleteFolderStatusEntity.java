package com.tvmining.sdk.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class DeleteFolderStatusEntity {
	 public final static Integer SUCC = 0;
     public final static Integer FAIL = -1;
     
     public Integer code;
     public String msg;

     /**
      * 
      * @param rawJson
      */
     public DeleteFolderStatusEntity(String rawJson){
    	 	JSONObject seJSON;
    		//List<PackInfoEntity> packInfoList = new ArrayList<PackInfoEntity>(); 
    		
        	try {
    			seJSON = new JSONObject(rawJson);
    			code = seJSON.getInt("code");
    			msg = seJSON.getString("msg");
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			code = DeleteFolderStatusEntity.FAIL;
    		}
     }
}