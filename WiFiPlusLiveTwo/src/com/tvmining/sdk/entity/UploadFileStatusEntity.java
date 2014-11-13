package com.tvmining.sdk.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadFileStatusEntity {
	public static int FAILED_INNER_CODE = -1;
    public static int SUCC_CODE = 0;
    
	public int code;
    public UploadFileDetailStatusEntity[] msg;

    public UploadFileStatusEntity(String rawJson){
    	JSONObject seJSON;
		//List<PackInfoEntity> packInfoList = new ArrayList<PackInfoEntity>(); 
		
    	try {
			seJSON = new JSONObject(rawJson);
			code = seJSON.getInt("code");
			JSONArray fileinfoList = seJSON.getJSONArray("msg");
			List<UploadFileDetailStatusEntity> uploadFileArrayList = new ArrayList<UploadFileDetailStatusEntity>();
		
			for(int i=0;i<fileinfoList.length();i++)
			{
				JSONObject oneJsonPackInfo = fileinfoList.getJSONObject(i);
				UploadFileDetailStatusEntity oneFileInfo = new UploadFileDetailStatusEntity();
			
				oneFileInfo.status = oneJsonPackInfo.getString("status");
				oneFileInfo.filename = oneJsonPackInfo.getString("filename");
				oneFileInfo.msg = oneJsonPackInfo.getString("msg");
				oneFileInfo.guid = oneJsonPackInfo.getString("guid");
				oneFileInfo.fileUri = oneJsonPackInfo.getString("fileUri");
				
				uploadFileArrayList.add(oneFileInfo);
			}
			
			msg = new UploadFileDetailStatusEntity[uploadFileArrayList.size()];
			uploadFileArrayList.toArray(msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			code = FAILED_INNER_CODE;
		}
    }
}
