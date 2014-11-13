/**
 * 
 */
package com.tvmining.sdk.entity;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;


/**
 * @author hanshu
 *
 */
public class UserInfoEntity {
	public String name;
    public String id;
    public String ip;
    public String type;
    public String key;
    public String value;
    
    static public String iceId = "";
    static public String groupId = GroupTypeEntity.ANONYMOUS;
    static public String tvmId = "";
    static public String appName = "AndroidSDK";

	/**
	 * @brief 得到私有包
	 * @return
	 */
	public String getPrivatePack(){
		String privatePack = String.format("private_pack_%d", id);
		return privatePack;
	}
	

	/**
	 * @brief 初始化
	 */
	public UserInfoEntity(String userType){
		name = "";
		id= "";
		ip = UserInfoEntity.getLocalHostIp();
		type = userType;
		UserInfoEntity.groupId = GroupTypeEntity.ANONYMOUS;
	}

	
	/**
	 * @brief 初始化
	 */
	public void UserInfoEntityWithEmpty(){
		name = "";
		id= "";
		ip = UserInfoEntity.getLocalHostIp();
		type = UserTypeEntity.USER;
		UserInfoEntity.groupId = GroupTypeEntity.ANONYMOUS;
	}

	
	/**
	 * @brief 初始化
	 * @param String 用户类型
	 * @param String 组类型
	 * @return
	 */
	public UserInfoEntity(String userType, String groupT){
		name = "";
		id= "";
		ip = UserInfoEntity.getLocalHostIp();
		type = userType;
		UserInfoEntity.groupId = groupT;
	}

	public static String getLocalHostIp()
    {
        String ipaddress = "";
        try
        {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements())
            {
                NetworkInterface nif = en.nextElement(); // 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements())
                {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                                    .getHostAddress()))
                    {
                        ipaddress = ip.getHostAddress();
                    }
                }

            }
        }
        catch (SocketException e)
        {
            //Log.e("feige", "获取本地ip地址失败");
            //e.printStackTrace();
        }
        
        return ipaddress;
    }

}
