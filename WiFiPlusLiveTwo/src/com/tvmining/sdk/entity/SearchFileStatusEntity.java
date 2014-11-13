package com.tvmining.sdk.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchFileStatusEntity {
    
	// 失败的代码
    public static final int FAILED_CODE=-1;

    // 成功的代码
    public static final int SUCC_CODE = 0;

    public int code;
    public SearchFileDetailStatusEntity[] msg;

    public SearchFileStatusEntity(String rawJson){
    	JSONObject seJSON;
		//List<PackInfoEntity> packInfoList = new ArrayList<PackInfoEntity>(); 
		
    	try {
			seJSON = new JSONObject(rawJson);
			code = seJSON.getInt("code");
			JSONArray fileinfoList = seJSON.getJSONArray("msg");
			List<SearchFileDetailStatusEntity> packInfoArrayList = new ArrayList<SearchFileDetailStatusEntity>();
			
			for(int i=0;i<fileinfoList.length();i++)
			{
				JSONObject oneJsonPackInfo = fileinfoList.getJSONObject(i);
				SearchFileDetailStatusEntity oneFileInfo = new SearchFileDetailStatusEntity();
			
				oneFileInfo.id = oneJsonPackInfo.getInt("id");
				oneFileInfo.desc = oneJsonPackInfo.getString("desc");
				oneFileInfo.ext = oneJsonPackInfo.getString("ext");
				oneFileInfo.file_type = oneJsonPackInfo.getString("file_type");
				oneFileInfo.filename = oneJsonPackInfo.getString("filename");
				oneFileInfo.guid = oneJsonPackInfo.getString("guid");
				oneFileInfo.packname = oneJsonPackInfo.getString("packname");
				oneFileInfo.submit_date = oneJsonPackInfo.getString("submit_date");
				oneFileInfo.tag = oneJsonPackInfo.getString("tag");
				oneFileInfo.title = oneJsonPackInfo.getString("title");
				oneFileInfo.owner_tvmid = oneJsonPackInfo.getString("owner_tvmid");
				oneFileInfo.weight = oneJsonPackInfo.getInt("weight");
				oneFileInfo.width = oneJsonPackInfo.getInt("width");
				oneFileInfo.height = oneJsonPackInfo.getInt("height");
				
				packInfoArrayList.add(oneFileInfo);
			}
			
			msg = new SearchFileDetailStatusEntity[packInfoArrayList.size()];
			packInfoArrayList.toArray(msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			code = FAILED_CODE;
			
		}
    }
}
