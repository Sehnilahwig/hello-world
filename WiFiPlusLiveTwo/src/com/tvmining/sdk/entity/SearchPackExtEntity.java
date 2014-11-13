package com.tvmining.sdk.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchPackExtEntity {
	public static int SUCC_CODE = 0;
	
	public static int FAILED_CODE = -1;
	
	/**
	 * 返回的代码
	 */
	public int code;
	
	public SearchPackExtDetailEntity[] msg;
	
	public SearchPackExtEntity(String  rawJson){
		JSONObject seJSON;
		//List<PackInfoEntity> packInfoList = new ArrayList<PackInfoEntity>(); 
		
    	try {
			seJSON = new JSONObject(rawJson);
			code = seJSON.getInt("code");
			JSONObject packExtInfoList = seJSON.getJSONObject("msg");
			List<SearchPackExtDetailEntity> packExtInfoArrayList = new ArrayList<SearchPackExtDetailEntity>();
			
			SearchPackExtDetailEntity onePackExtInfo = new SearchPackExtDetailEntity();
			
			onePackExtInfo.size = packExtInfoList.getInt("size");
			onePackExtInfo.name = packExtInfoList.getString("name");
				
			packExtInfoArrayList.add(onePackExtInfo);
			
			
			msg = new SearchPackExtDetailEntity[packExtInfoArrayList.size()];
			packExtInfoArrayList.toArray(msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			code = FAILED_CODE;
			msg = new SearchPackExtDetailEntity[0];
		}
	}
}
