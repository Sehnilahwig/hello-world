package com.tvmining.sdk.entity;

import java.io.Serializable;

import com.tvmining.sdk.helper.HttpHelper;

public class SearchFileDetailStatusEntity implements Serializable{
	public static final String STORAGE_PREFIX = "resource";

    /// 序列号，每个资源有一个不重复的，无意义
    public int id;

    /// guid
    public String guid;

    /// 用户号
    public String owner_tvmid;

    /// 是否共享
    private int isShare;

    /// 文件名
    public String filename;

    /// 包名信息
    public String packname;

    ///  文件描述
    public String desc;

    /// 文件标题
    public String title;

    /// 标签
    public String tag;

    /// 提交时间
    public String submit_date;

    /// 文件类型
    public String file_type;
	
    /// 文件拓展名
    public String ext;

    //权重
    public Integer weight;
    
    public int height;
    
    public int width; 
    
    public String videoFilePathUrl;
    
    public String filePathUrl;
    
	public SearchFileDetailStatusEntity() {
		// TODO Auto-generated constructor stub
		id = 0;
        guid = "";
        owner_tvmid = "";
        isShare = 0;
        filename = "";
        packname = "";
        desc = "";
        title = "";
        tag = "";
        submit_date = "";
        file_type = "";
        ext = "";
        weight = 0;
        width = 0;
        height = 0;
	}
	
	//增加带参构造
	public SearchFileDetailStatusEntity(String fileName,String fileType,String fileTitle,String fileGuid,String fileTag,String fileOriginalPath,String fileDesc,int height,int width) {
		id = 0;
        guid = fileGuid;
        owner_tvmid = "";
        isShare = 0;
        filename = fileName;
        packname = fileOriginalPath;//其中存放文件本地路径
        desc = fileDesc;
        title = fileTitle;
        tag = fileTag;
        submit_date = "";
        file_type = fileType;
        ext = "";
        weight = 0;
        this.width = width;
        this.height = height;
	}
	
	/**
	 * @brief 得到缩略图
	 * @return String 缩略图 URL
	 */
	public String getFileURI(){
		String uri = null;
		try{
			uri=String.format("%s/%s/%s", SearchFileDetailStatusEntity.STORAGE_PREFIX, HttpHelper.UrlEncode(packname), filename); 
		}catch(Exception ex){}
				
		return uri;
	}

    /**
     * @brief 得到缩略图
     * @param int 宽 a.jgp
     * @param int 高
     * @return String 缩略图
     */
    public String getThumbURI(int width, int height)
    {
        String uri = null;
        try{
        	uri=String.format("/%s/%s/%s.jpg_%d_%d.jpg", 
        							SearchFileDetailStatusEntity.STORAGE_PREFIX, 
        							HttpHelper.UrlEncode(packname), 
        							guid,
        							width, 
        							height 
       	 );
        }catch(Exception ex){}
        return uri;
    }

    /**
     * @brief  得到缩略图
     * @param width
     * @param height
     * @param fileTM
     * @return 缩略图  URL
     */
    public String getThumbURI(int width, int height, fileThumbMethod fileTM) {
        String thumbMethod = "_";
        if (fileTM == fileThumbMethod.resample) {
            thumbMethod = "x";
        }

        String uri = null;
        try{
        	uri=String.format("/%s/%s/%s.jpg%s%d_%d.jpg", 
        							SearchFileDetailStatusEntity.STORAGE_PREFIX, 
        							HttpHelper.UrlEncode(packname), 
        							guid, 
        							thumbMethod,
        							width, 
        							height
        			 );
        }catch(Exception ex){}
        return uri;
    }
    
    /**
     * 单边缩略图
     * @param size 缩放的尺寸
     * @param sideThumb 是按照长，还是按照宽
     * @return
     */
    public String getSideThumbURI(int size, SideThumbMethod sideThumb){
    	String sideThumbMethod = "w";
    	if(sideThumb == SideThumbMethod.height){
    		sideThumbMethod = "h";
    	}
    	 String uri = null;
        try{
        	uri= String.format("/%s/%s/%s.jpg_%s_%d.jpg", 
    									 SearchFileDetailStatusEntity.STORAGE_PREFIX,
    									 HttpHelper.UrlEncode(packname),
    									 guid,
    									 sideThumbMethod,
    									 size
    			);
    	 }catch(Exception ex){}
    	return uri;
    }
}
