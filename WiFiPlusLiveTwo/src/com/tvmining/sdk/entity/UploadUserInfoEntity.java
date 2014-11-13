package com.tvmining.sdk.entity;

import java.io.File;
import java.util.Hashtable;

public class UploadUserInfoEntity {
	/// <summary>
    /// 用户更新提交的 URL
    /// </summary>
    public final static String URL_UPLOAD_USERINFO = "ice3/upload_userinfo.php";

    /// <summary>
    /// 天脉号
    /// </summary>
    public String tvmId;

    /// <summary>
    /// 公司名
    /// </summary>
    public String company;

    /// <summary>
    /// 应用名
    /// </summary>
    public String appName;

    /// <summary>
    /// 邮件名
    /// </summary>
    public String email;

    /// <summary>
    /// 手机
    /// </summary>
    public String mobile;

    /// <summary>
    /// 头像
    /// </summary>
    public String face; 

    /// <summary>
    /// 昵称
    /// </summary>
    public String nickname;
    
    /// <summary>
    /// 析构
    /// </summary>
    public UploadUserInfoEntity()
    {
        tvmId = "";
        company = "";
        appName = "";
        email = "";
        mobile = "";
        face = "";
        nickname = "";
    }

    /// <summary>
    /// 转换成 POST 提交的数组
    /// </summary>
    /// <returns></returns>
    public Hashtable<String, String> convertToHttpCond() 
    {
    	Hashtable<String, String> postCond = new Hashtable<String, String>();
        postCond.put("owner_tvmid", tvmId);
        postCond.put("appname", appName);

        if (company != null &&
        	company.length() > 0
        ){
            postCond.put("company", company);
        }

        if (email != null &&
        	email.length() > 0)
        {
            postCond.put("email", email);
        }

        if (mobile != null &&
        	mobile.length() > 0)
        {
            postCond.put("mobile", mobile);
        }
        
        if (nickname != null &&
        	nickname.length() > 0
        ){
                postCond.put("nickname", nickname);
        }

        return postCond;
    }

    /// <summary>
    /// 得到表情文件
    /// </summary>
    /// <returns></returns>
    public Hashtable<String, String> getFace()
    {
    	Hashtable<String, String> faceFile = new Hashtable<String, String>();
        try
        {
        	Boolean isFileExists = new File(face).exists(); 
            if(!isFileExists)
            {
                return faceFile;
            }

            faceFile.put("face", face);
        }
        catch (Exception e) { 
            
        }

        return faceFile;
    }
}
