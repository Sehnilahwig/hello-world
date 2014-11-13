/**
 * 
 */
package com.tvmining.sdk.entity;

import java.util.Hashtable;

/**
 * @author
 *
 */
public class DeleteFolderEntity {

	/**
	 * 
	 */
	public DeleteFolderEntity() {
		// TODO Auto-generated constructor stub
	}

	 /// <summary>
    /// 清除者的 URL
    /// </summary>
    public final static String URL_REMOVER = "ice3/remove_pack.php";

    /// <summary>
    /// 包名
    /// </summary>
    public String packname;

    /// <summary>
    /// 得到 HTTP 请求
    /// </summary>
    /// <returns></returns>
    public Hashtable<String,String> convertToHttpCond()
    {
        Hashtable<String, String> postCond = new Hashtable<String, String>();

        postCond.put("res_pack", packname);
        postCond.put("user_tvmid", UserInfoEntity.tvmId);

        return postCond;
    }
}
