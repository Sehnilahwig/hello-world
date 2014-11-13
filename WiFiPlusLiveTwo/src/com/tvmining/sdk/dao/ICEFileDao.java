package com.tvmining.sdk.dao;

import java.util.Hashtable;

import android.util.Log;

import com.tvmining.sdk.entity.DeleteFolderEntity;
import com.tvmining.sdk.entity.DeleteFolderStatusEntity;
import com.tvmining.sdk.entity.FolderMakerEntity;
import com.tvmining.sdk.entity.FolderMakerStatusEntity;
import com.tvmining.sdk.entity.ICEFileEntity;
import com.tvmining.sdk.entity.ICEFolderMakerEntity;
import com.tvmining.sdk.entity.ICETimeStatusEntity;
import com.tvmining.sdk.entity.PackExtInfoEntity;
import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.sdk.entity.SearchFileEntity;
import com.tvmining.sdk.entity.SearchFileStatusEntity;
import com.tvmining.sdk.entity.SearchPackExtDetailEntity;
import com.tvmining.sdk.entity.SearchPackExtEntity;
import com.tvmining.sdk.entity.SearchPacknameStatus;
import com.tvmining.sdk.entity.UploadFileStatusEntity;
import com.tvmining.sdk.entity.UploadUserInfoEntity;
import com.tvmining.sdk.entity.UploadUserInfoStatusEntity;
import com.tvmining.sdk.entity.UserInfoEntity;
import com.tvmining.sdk.entity.uploadFileEntity;
import com.tvmining.sdk.helper.HttpHelper;


public class ICEFileDao {

	/// <summary>
    /// 连接的主机名
    /// </summary>
    private String hostname;

    /// <summary>
    /// 析构
    /// </summary>
    /// <param name="serverHostname">主机名</param>
    public ICEFileDao(String serverHostname) 
    {
        hostname = serverHostname;
    }

     /// <summary>
    /// 得到中控当前时间
    /// </summary>
    /// <returns>中控的时间实体</returns>
    public ICETimeStatusEntity getICECurrentTime()
    {
        String url = String.format("http://%s/%s", hostname, ICETimeStatusEntity.URL_TIMER);
        String rawJSON = HttpHelper.getURL(url);

        ICETimeStatusEntity itse = null;
        if (0 != rawJSON.length())
        {
       		itse = new ICETimeStatusEntity(rawJSON);
        }
        else {
            itse = new ICETimeStatusEntity("");
        }

        return itse;
    }

    /// <summary>
    /// 得到拓展包信息
    /// </summary>
    /// <returns>包的名字</returns>
    public SearchPackExtDetailEntity[] getPackExtInfo(String packname)
    {
    	
        String url = String.format("http://%s/%s%s&packname=%s", hostname, PackExtInfoEntity.URL_GET_PACK_EXT_INFO, HttpHelper.UrlEncode(UserInfoEntity.tvmId), HttpHelper.UrlEncode(packname));
        String rawJSON = HttpHelper.getURL(url);

        SearchPackExtEntity spe = null;
        if (0 != rawJSON.length())
        {
            try
            {
            	
            	spe = new SearchPackExtEntity(rawJSON);//new JavaScriptSerializer().Deserialize<SearchPacknameStatus>(rawJSON);
            }
            catch (Exception e) {
                //Console.WriteLine("分析 json 得到包出错('"+rawJSON+"'):"+e.Message);
            }
        }

        SearchPackExtDetailEntity retSpede[] = spe.msg;
        return retSpede;
    }
    
    /// <summary>
    /// 得到所有包名称
    /// </summary>
    /// <returns>包的名字</returns>
    public PackInfoEntity[] getAllPackName()
    {
    	
        String url = String.format("http://%s/%s%s", hostname, ICEFileEntity.URL_GET_ALL_PACK, HttpHelper.UrlEncode(UserInfoEntity.tvmId));
        String rawJSON = HttpHelper.getURL(url);

        SearchPacknameStatus cse = null;
        if (0 != rawJSON.length())
        {
            try
            {
            	
                cse = new SearchPacknameStatus(rawJSON);//new JavaScriptSerializer().Deserialize<SearchPacknameStatus>(rawJSON);
            }
            catch (Exception e) {
                //Console.WriteLine("分析 json 得到包出错('"+rawJSON+"'):"+e.Message);
            }
        }

        PackInfoEntity retPIE[] = cse.msg;
        return retPIE;
    }

    /// <summary>
    /// 更新用户信息
    /// </summary>
    /// <param name="postDict">提交的 POST 变量字典</param>
    /// <returns></returns>
//    public UploadUserInfoStatusEntity uploadUserInfo(UploadUserInfoEntity oneUserInfo)
//    {
//        string url = string.Format("http://{0}/{1}", hostname, UploadUserInfoEntity.URL_UPLOAD_USERINFO);
//        Dictionary<String, String> postDict = oneUserInfo.convertToHttpCond();
//        Dictionary<String, String> faceFile = oneUserInfo.getFace();
//
//        string rawJSON;
//        if (faceFile.Count == 0)
//        {
//            rawJSON = HttpHelper.postURL(url, postDict);
//        }
//        else {
//            rawJSON = HttpHelper.UploadFile(url, postDict, faceFile);
//        }
//
//        UploadUserInfoStatusEntity fs = null;
//         
//        if (0 != rawJSON.Length)
//        {
//            try
//            {
//                fs = new JavaScriptSerializer().Deserialize<UploadUserInfoStatusEntity>(rawJSON);
//            }
//            catch (Exception e)
//            {
//                fs = new UploadUserInfoStatusEntity();
//
//                try
//                {
//                    UploadUserInfoEmptyStatusEntity emptyStatus = new JavaScriptSerializer().Deserialize<UploadUserInfoEmptyStatusEntity>(rawJSON);
//                    fs.code = emptyStatus.code;
//                }
//                catch (Exception ee) {
//                    fs.code = fs.code = UploadUserInfoStatusEntity.FAILED_INNER_CODE;
//                }
//             }
//        }
//
//
//        if (fs == null)
//        {
//            fs = new UploadUserInfoStatusEntity();
//            fs.code = UploadUserInfoStatusEntity.FAILED_INNER_CODE; ;
//        }
//
//        return fs;
//    }
//
    /// <summary>
    /// 上传本地文件
    /// </summary>
    /// <param name="postDict">POST提交的变量</param>
    /// <param name="uploadFileArray">本地文件列表</param>
    /// <returns></returns>
    public UploadFileStatusEntity uploadLocalFile(Hashtable<String, String> postDict, uploadFileEntity[] uploadFileArray)
    {
    	String url = String.format("http://%s/%s", hostname, ICEFileEntity.URL_UPLOAD_FILE);
    	String rawJSON = HttpHelper.batchUploadFile(url, postDict, uploadFileArray);

        UploadFileStatusEntity fs = null;

        if (0 != rawJSON.length())
        {
            try
            {
                fs = new UploadFileStatusEntity(rawJSON); // new JavaScriptSerializer().Deserialize<UploadFileStatusEntity>(rawJSON);
            }
            catch (Exception e)
            {
                Log.d("分析 json 上传文件出错了:" ,  rawJSON + "..." + e.getMessage());
                fs = new UploadFileStatusEntity("");
                fs.code = UploadFileStatusEntity.FAILED_INNER_CODE;
            }
        }
        

        if (fs == null) {
            fs = new UploadFileStatusEntity("");
            fs.code = UploadFileStatusEntity.FAILED_INNER_CODE; ;
        }

        return fs;
    }

    
    /// <summary>
    /// 上传本地文件
    /// </summary>
    /// <param name="postDict">POST提交的变量</param>
    /// <param name="uploadFileArray">本地文件列表</param>
    /// <returns></returns>
    public UploadUserInfoStatusEntity uploadUserInfo(UploadUserInfoEntity oneUserInfo)
    {
        String url = String.format("http://%s/%s", hostname, UploadUserInfoEntity.URL_UPLOAD_USERINFO);
        Hashtable<String, String> postDict = oneUserInfo.convertToHttpCond();
        Hashtable<String, String> faceFile = oneUserInfo.getFace();

        String rawJSON;
        if (faceFile.size() == 0)
        {
            rawJSON = HttpHelper.postURL(url, postDict);
        }
        else {
            rawJSON = HttpHelper.UploadFile(url, postDict, faceFile);
        }

        UploadUserInfoStatusEntity fs = null;
        
        try
        {
            fs = new UploadUserInfoStatusEntity(rawJSON);//JavaScriptSerializer().Deserialize<UploadUserInfoStatusEntity>(rawJSON);
        }
        catch (Exception e)
        {
             fs = new UploadUserInfoStatusEntity("");
        }
        

        return fs;
    }

    /// <summary>
    /// 删除一个包
    /// </summary>
    /// <param name="deleteFolder">删除的包名</param>
    /// <returns>删除的状态</returns>
    public DeleteFolderStatusEntity deleteFolder(DeleteFolderEntity deleteFolder)
    {
        String url = String.format("http://%s/%s", hostname, DeleteFolderEntity.URL_REMOVER);
        String rawJSON = HttpHelper.postURL(url, deleteFolder.convertToHttpCond());

        DeleteFolderStatusEntity dfse = null;
        if (rawJSON.length() != 0) {
            try
            {
                dfse = new DeleteFolderStatusEntity(rawJSON);
            }
            catch (Exception e) {
                Log.d("分析 json 删除文件夹出错了啊("+rawJSON, e.getMessage());
            }
        }

        if (dfse == null) {
            dfse = new DeleteFolderStatusEntity("");
        }

        return dfse;
    }

    /// <summary>
    /// 创建一个文件夹
    /// </summary>
    /// <param name="folderMakderCond">文件夹创建条件</param>
    /// <returns></returns>
    public FolderMakerStatusEntity folderMaker(FolderMakerEntity folderMakderCond) {
        String url = String.format("http://%s/%s", hostname, ICEFolderMakerEntity.URL_MAKER);
        String rawJSON = HttpHelper.postURL(url, folderMakderCond.convertToHttpCond());

        FolderMakerStatusEntity sfse = null;

        if (rawJSON.length() != 0)
        {
            try
            {
                sfse = new FolderMakerStatusEntity(rawJSON);//new JavaScriptSerializer().Deserialize<FolderMakerStatusEntity>(rawJSON);
            }
            catch (Exception e)
            {
                Log.d("分析 json 创建文件夹出错了啊\r\n" ,  rawJSON + ":::::" + e.getMessage());
            }
        }

        if (sfse == null)
        {
            sfse = new FolderMakerStatusEntity("");
        }

        return sfse;
    }

    /// <summary>
    /// 搜索一个文件
    /// </summary>
    /// <param name="searchFileCond">搜索文件条件</param>
    /// <returns>搜索的状态</returns>
    public SearchFileStatusEntity searchFile(SearchFileEntity searchFileCond) {
        String url = String.format("http://%s/%s", hostname, ICEFileEntity.URL_SEARCH_FILE);
        String rawJSON = HttpHelper.postURL(url, searchFileCond.convertToHttpCond());

        SearchFileStatusEntity sfse = null;

        if (rawJSON.length() != 0) {
            try
            {
                sfse = new SearchFileStatusEntity(rawJSON);
            }
            catch (Exception e) {
                Log.d("分析 json 搜索文件出错了啊(", rawJSON+"=="+e.getMessage());
                sfse = new SearchFileStatusEntity("");
            }
        }

        if (sfse == null) { 
            sfse = new SearchFileStatusEntity("");
            sfse.code = -1;
        }

        return sfse;
    }

}
