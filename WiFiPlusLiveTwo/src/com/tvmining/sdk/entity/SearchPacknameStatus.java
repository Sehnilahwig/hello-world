package com.tvmining.sdk.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchPacknameStatus {
	public int code;
    public String status;
    //public PackInfoEntity[] msg;
    public PackInfoEntity[] msg;

    public SearchPacknameStatus(String rawJson){
    	JSONObject seJSON;
		//List<PackInfoEntity> packInfoList = new ArrayList<PackInfoEntity>(); 
		
    	try {
			seJSON = new JSONObject(rawJson);
			code = seJSON.getInt("code");
			JSONArray packinfoList = seJSON.getJSONArray("msg");
			List<PackInfoEntity> packInfoArrayList = new ArrayList<PackInfoEntity>(); 
			for(int i=0;i<packinfoList.length();i++)
			{
				JSONObject oneJsonPackInfo = packinfoList.getJSONObject(i);
				PackInfoEntity onePackInfo = new PackInfoEntity();
				
				onePackInfo.id = oneJsonPackInfo.getInt("id");
				onePackInfo.owner_tvmid = oneJsonPackInfo.getString("owner_tvmid");
				onePackInfo.pack_type = oneJsonPackInfo.getString("pack_type");
				onePackInfo.packname = oneJsonPackInfo.getString("packname");
				onePackInfo.res_groupid = oneJsonPackInfo.getString("res_groupid");
				onePackInfo.submit_date = oneJsonPackInfo.getString("submit_date");
				onePackInfo.thumb_guid = oneJsonPackInfo.getString("thumb_guid");
				
				packInfoArrayList.add(onePackInfo);
			}
			
			msg = new PackInfoEntity[packInfoArrayList.size()];
			packInfoArrayList.toArray(msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			code = -1;
			
		}
    }
}
