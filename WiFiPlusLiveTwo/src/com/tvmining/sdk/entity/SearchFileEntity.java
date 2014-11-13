package com.tvmining.sdk.entity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class SearchFileEntity {
    // 搜索的栏目
    public List<String> guid;
    
    /// 标题包含
    public String titleContain;
    
    /// 描述包含
    public String descContain;

    /// 标签包含
    public String tag;

    /// 在那个资源包中
    public List<String> inPack;
    
    /// 排序方法
    public SearchFileOrderMethod orderMethod;
    
    /// 排序栏
    public String orderColumn;
    
    /// 开始时间
    public String beginTime;
    
    /// 结束时间
    public String finishTime;

    /// <summary>
    /// 天脉号
    /// </summary>
    public String tvmId;

    /// <summary>
    /// 提示权限组
    /// </summary>
    public String suggestGroupId;

	public SearchFileEntity() {
		// TODO Auto-generated constructor stub
		guid = new ArrayList<String>();
		titleContain = "";
		descContain = "";
		tag = "";
		inPack = new ArrayList<String>();
		orderMethod = SearchFileOrderMethod.ASC;
		orderColumn = GroupTypeEntity.ALL_CAN_SEE;
		beginTime = "";
		finishTime = "";
		suggestGroupId="";
	}
	
	public Hashtable<String, String> convertToHttpCond(){
		Hashtable<String, String> postCond = new Hashtable<String,String>();
        String guids = "";
        for (int i = 0; i < guid.size(); i++)
        {
        	
            if (guid.get(i).length() == 0) {
                continue;
            }
            guids += guid.get(i)+",";
        }

        if (guids.length() > 0)
        {
            guids = guids.substring(0, guids.length()-1);
        }
        
      //-------------------------------------
        String inPacks = "";
        for (int i = 0; i < inPack.size(); i++)
        {
            if (inPack.get(i).length() == 0)
            {
                continue;
            }
            inPacks += inPack.get(i) + ",";
        }

        if (inPacks.length() > 0)
        {
            inPacks = inPacks.substring(0, inPacks.length() - 1);
        }
        
        //-------------------------------------
        postCond.put("guid", guids);
        postCond.put("in_res_pack", inPacks);
        postCond.put("title_contain", titleContain);
        postCond.put("desc_contain", descContain);
        postCond.put("tag", tag);
        postCond.put("tvmid", UserInfoEntity.tvmId);
        postCond.put("suggest_groupid", suggestGroupId);
        
        if(orderMethod == SearchFileOrderMethod.ASC){
            postCond.put("is_asc", "1");
        }else{
            postCond.put("is_asc", "0");
        }

        if (orderColumn.equalsIgnoreCase("id") ||
           orderColumn.equalsIgnoreCase("title") ||
           orderColumn.equalsIgnoreCase("submit_date")
        )
        {
            postCond.put("order_column", orderColumn);
        }
        else {
            postCond.put("order_column", "id");
        }
        
        postCond.put("begin_time", beginTime);
        postCond.put("finish_time", finishTime);

        return postCond;

	}

}
