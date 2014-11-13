package com.tvmining.sdk.service;

import java.util.Hashtable;

import android.util.Log;

import com.tvmining.sdk.dao.ICEFileDao;
import com.tvmining.sdk.entity.DeleteFolderEntity;
import com.tvmining.sdk.entity.DeleteFolderStatusEntity;
import com.tvmining.sdk.entity.FolderMakerEntity;
import com.tvmining.sdk.entity.FolderMakerStatusEntity;
import com.tvmining.sdk.entity.ICETimeStatusEntity;
import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;
import com.tvmining.sdk.entity.SearchFileEntity;
import com.tvmining.sdk.entity.SearchFileStatusEntity;
import com.tvmining.sdk.entity.SearchPackExtDetailEntity;
import com.tvmining.sdk.entity.UploadFileDetailStatusEntity;
import com.tvmining.sdk.entity.UploadFileStatusEntity;
import com.tvmining.sdk.entity.UploadUserInfoEntity;
import com.tvmining.sdk.entity.UploadUserInfoStatusEntity;
import com.tvmining.sdk.entity.uploadFileEntity;

public class ICEFileService {
	private ICEFileDao dao;

    /// <summary>
    /// 连接的主机名
    /// </summary>
    private String connHostname;

    /// <summary>
    /// 析构
    /// </summary>
    /// <param name="hostname">要连接的服务主机名</param>
    public ICEFileService(String hostname) 
    {
        connHostname = hostname;
        dao = new ICEFileDao(hostname);
    }

    /// <summary>
    /// 设置主机名
    /// </summary>
    /// <param name="name">主机名</param>
    public void setHostname(String name){
        connHostname = name;
    }

    /// <summary>
    /// 得到中控当前时间
    /// </summary>
    /// <returns>中控的时间实体</returns>
    public ICETimeStatusEntity getICECurrentTime()
    {
        ICETimeStatusEntity itse = dao.getICECurrentTime();
        return itse;
    }

    /// <summary>
    /// 得到拓展包信息
    /// </summary>
    /// <returns>包的名字</returns>
    public SearchPackExtDetailEntity[] getPackExtInfo(String packname)
    {
    	SearchPackExtDetailEntity[] packExtInfo = dao.getPackExtInfo(packname);
    	return packExtInfo;
    }
    
    /// <summary>
    /// 得到所有包名称
    /// </summary>
    /// <returns>包的名字</returns>
    public PackInfoEntity[] getAllPackName()
    {
        PackInfoEntity[] allPakArray = dao.getAllPackName();
        return allPakArray;
    }

    /// <summary>
    /// 更新用户信息
    /// </summary>
    /// <param name="oneUserInfo">提交的 POST 变量字典</param>
    /// <returns></returns>
    public UploadUserInfoStatusEntity uploadUserInfo(UploadUserInfoEntity oneUserInfo)
    {
        UploadUserInfoStatusEntity uuise = dao.uploadUserInfo(oneUserInfo);


        if (uuise.code != UploadUserInfoStatusEntity.SUCC)
        {
            Log.d("上传用户信息返回逻辑错误在服务层：" , String.valueOf(uuise.code));
        }

        return uuise;
    }

    /// <summary>
    /// 升级本地文件
    /// </summary>
    /// <param name="postDict">POST提交的数据</param>
    /// <param name="uploadFileArray">上传的文件</param>
    /// <returns></returns>
    public UploadFileDetailStatusEntity[] uploadLocalFile(Hashtable<String, String> postDict, uploadFileEntity[] uploadFileArray)
    {
        UploadFileStatusEntity ufs = dao.uploadLocalFile(postDict, uploadFileArray);
        UploadFileDetailStatusEntity[] detailStatus;

        if (ufs.code == UploadFileStatusEntity.SUCC_CODE)
        {
            detailStatus = ufs.msg;
            
        }else{
            Log.d("上传文件返回逻辑错误在服务层：", "");
            detailStatus = new UploadFileDetailStatusEntity[0];
        }

        return detailStatus;
    }

    
//    /// <summary>
//    /// 升级用户信息
//    /// </summary>
//    /// <param name="postDict">POST提交的数据</param>
//    /// <param name="uploadFileArray">上传的文件</param>
//    /// <returns></returns>
//    public UploadFileDetailStatusEntity[] uploadUserInfo(Dictionary<String, String> postDict, uploadFileEntity[] uploadFileArray)
//    {
//        UploadFileStatusEntity ufs = dao.uploadLocalFile(postDict, uploadFileArray);
//        UploadFileDetailStatusEntity[] detailStatus;
//
//        if (ufs.code == UploadFileStatusEntity.SUCC_CODE)
//        {
//            detailStatus = ufs.msg;
//            
//        }else{
//            Console.WriteLine("上传文件返回逻辑错误在服务层："+ufs.msg.ToString());
//            detailStatus = new UploadFileDetailStatusEntity[0];
//        }
//
//        return detailStatus;
//    }
//
    /// <summary>
    /// 删除一个包
    /// </summary>
    /// <param name="delPackName">包名</param>
    /// <returns></returns>
    public DeleteFolderStatusEntity deleteFolder(DeleteFolderEntity deleteFolder)
    {
        DeleteFolderStatusEntity dpse = dao.deleteFolder(deleteFolder);
        return dpse;
    }

    /// <summary>
    /// 创建一个文件夹
    /// </summary>
    /// <param name="folderMakerCond">创建文件的实体</param>
    /// <returns>创建状态</returns>
    public FolderMakerStatusEntity folderMaker(FolderMakerEntity folderMakerCond) {
        FolderMakerStatusEntity fmse = dao.folderMaker(folderMakerCond);

        return fmse;
    }

    /// <summary>
    /// 按条件搜索文件
    /// </summary>
    /// <param name="searchFileCond">搜索条件</param>
    /// <returns>搜索到的具体条件</returns>
    public SearchFileDetailStatusEntity[] searchFile(SearchFileEntity searchFileCond) {
        SearchFileStatusEntity sfse = dao.searchFile(searchFileCond);

        SearchFileDetailStatusEntity[] sfdse = null;

        if (sfse.code == SearchFileStatusEntity.SUCC_CODE)
        {
            sfdse = sfse.msg;
        }
        else {
            Log.d("搜索文件逻辑错误在服务层：", sfse.msg.toString());
            sfdse = new SearchFileDetailStatusEntity[0];
        }

        return sfdse;
    }

}
