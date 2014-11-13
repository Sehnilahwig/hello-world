package com.tvmining.sdk.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class uploadFileEntity {
    /// 升级的包的键
    public static final String UPDATE_PACK_KEY = "onto_res_pack";

    /// 提示需要的权限
    public static final String UPDATE_USER_ID_KEY = "owner_tvmid";

    /// 上传过程的唯一号
    public static final String REQUEST_GUID_KEY = "request_guid";
    
    /// 上传过程的唯一号
    public static final String SUGGEST_PACK_KEY = "suggest_groupid";

	/// 上传的源
	public static final String SOURCE_OBJ_KEY = "source_obj_key";
    
    private String title;
    private String desc;
    private String tag;
    private String guid;
    private String filePath;
    private String fileType;
    
    /**
     * 构造类
     * @param t 标题
     * @param d 描述
     * @param ta 标签
     * @param gu guid
     * @param fp 文件路径
     * @param ft 文件类型
     * @throws Exception 
     */
    public uploadFileEntity(String t, String d, String ta,String gu, String fp, String ft) throws Exception
    {
        if (!new File(fp).exists()) {
            throw new Exception("文件 "+fp+" 不存在啊不存在");
        }

        title = t;
        desc = d;
        tag = ta;
        guid = gu;
        filePath = fp;
        fileType = ft;
    }
    
    /**
     * 得到文件名
     * @return
     */
    public String getFileName(){
        String[] filename = filePath.split("/");

        return filename[filename.length - 1];   
    }

  /// <summary>
    /// 得到实体的 mimetype 信息
    /// </summary>
    /// <returns></returns>
    public String getMimeType() {
    	String[] fileExtendsion = filePath.split("\\.");
        if (fileExtendsion.length == 1) {
            return "application/octet-stream";
        }


        String fileExt = fileExtendsion[fileExtendsion.length - 1].toLowerCase();
        if (fileExt.equalsIgnoreCase("jpg")) {
            fileExt = "jpeg";
        }else if(fileExt.equalsIgnoreCase("mov")){
            fileExt = "quicktime";
        }

        //如果是图片
        if (fileExt.equalsIgnoreCase("jpeg") ||
           fileExt.equalsIgnoreCase("png") ||
           fileExt.equalsIgnoreCase("gif") ||
           fileExt.equalsIgnoreCase("bmp")
        ) {
            return "image/"+fileExt;
        }

        //如果是视频
        if (fileExt.equalsIgnoreCase("quicktime") ||
           fileExt.equalsIgnoreCase("mp4")
        ) {
            return "video/"+fileExt;
        }

        return "application/octet-stream";
    }

    /// <summary>
    /// 得到文件类型
    /// </summary>
    /// <returns>文件类型</returns>
    public String getFileType() {
        return fileType;
    }

    /*
    public String getSuggestGroupId() {
        return suggestGroupId;
    }
    */

    /// <summary>
    /// 文件长度
    /// </summary>
    /// <returns>长度</returns>
    public long getFileLength() {
        if (!new File(filePath).exists()) 
        {
            return 0;
        }

        
        long fileLength = new File(filePath).length();
        return fileLength;
    }


    /// <summary>
    /// 文件流
    /// </summary>
    /// <returns>FileStream</returns>
    public FileInputStream getFileStream()
    {
        try {
			return new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }

    /// <summary>
    /// 得到标签
    /// </summary>
    /// <returns></returns>
    public String getTag() {
        return tag;
    }

    /// <summary>
    /// 得到 title
    /// </summary>
    /// <returns>标题</returns>
    public String getTitle() {
        return title;
    }

    /// <summary>
    /// 得到 desc
    /// </summary>
    /// <returns>描述</returns>
    public String getDesc() {
        return desc;
    }

    /// <summary>
    /// 得到 guid
    /// </summary>
    /// <returns>guid</returns>
    public String getGuid() {
        return guid;
    }

}
