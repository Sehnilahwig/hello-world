package com.tvmining.sdk.dao;



import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;

import com.tvmining.sdk.entity.ICEConnectorEntity;
import com.tvmining.sdk.entity.ICELoginEntity;
import com.tvmining.sdk.entity.ICERoleEntity;
import com.tvmining.sdk.entity.UserInfoEntity;
import com.tvmining.sdk.helper.SocketHelper;
import com.tvmining.sdk.helper.UdpHelper;
import com.tvmining.sdk.helper.UdpInfoEntity;
import com.tvmining.sdk.helper.UdpInfoRoleEntity;


public class ICEConnectorDao {
	//private static final Object[][] String = null;

	// Socket 连接帮助
    private SocketHelper sh;

    static private UdpHelper uh = null;//new UdpHelper();

    /// <summary>
    /// 注册到的中控信息
    /// </summary>
    private ICERoleEntity connICERole;

    /// <summary>
    /// 注册的用户信息
    /// </summary>
    private UserInfoEntity regUserInfo;

    /// <summary>
    /// 连接的服务名字
    /// </summary>
    private ICELoginEntity connectICE;

    /// <summary>
    /// 连接的主机名
    /// </summary>
    private String connectHostName;

    
    /// 送出锁
    private ReentrantLock sendMutex;
    
    static public void init()
    {
    	if(uh == null){
    		uh.threadRunner = new Thread(new UdpHelper());
    		uh.threadRunner.setName("得到广播的线程");
    		uh.threadRunner.start();
    	}
    }
    
    /**
     * @brief 连接
     * @param servername
     */
    public ICEConnectorDao(ICELoginEntity loginICE)
    {
        connICERole = null;
        regUserInfo = null;
        sh = null;
        sendMutex = new ReentrantLock();
        connectHostName = null;

        connectICE = loginICE;
    }
	
	
  /// <summary>
    /// 得到连接登录中控
    /// </summary>
    /// <returns>连接服务名</returns>
    public ICELoginEntity getLoginICE()
    {
        return connectICE;
    }

    /// <summary>
    /// 得到主机名字
    /// </summary>
    /// <returns>连接主机名</returns>
    public String getConnectHostname()
    {
        return connectHostName;
    }

    /// <summary>
    /// 通过一个 hostName 进行连接
    /// </summary>
    /// <param name="hostname">主机名</param>
    /// <returns>连接成功与否</returns>
    public Boolean connectByHostname(String hostname){
        if (hostname == null ||
           hostname.length() == 0
        ) {
            return false;
        }

        sh = new SocketHelper(hostname, ICEConnectorEntity.SERVER_PORT);
        Boolean isAlive = sh.isConnected();

        if (isAlive) {
            connectHostName = hostname;
        }
        return isAlive;
    }

    /// <summary>
    /// 从 socket 得到字符串
    /// </summary>
    /// <returns></returns>
    public String getSockeString() throws Exception {
        if (!isConnected()) {
            throw new Exception("没有连接，怎么接收 socket");
        }

        String sockStr = sh.recv();
        return sockStr;
    }

    /// <summary>
    /// 关闭连接
    /// </summary>
    public void disconnect() {
        sh.disconnect();
    }
    
    /**
     * 广播原始字符串
     * @param oneStr 发送的字符串
     * @return 返回的字符串
     */
    public String broadcastRawStr(String oneStr){
    	String recvString = sh.sendByShort(connectHostName, ICEConnectorEntity.BROADCAST_PORT, oneStr);
    	return recvString;
    }

    /// <summary>
    /// 送出字符给 socket
    /// </summary>
    /// <param name="oneStr">送出的字符</param>
    public Boolean setRawStr(String oneStr){
		if (!isConnected()) {
			Log.d("送出字符出问题，没连接", "");
			return false;
		}
		
		Boolean isSend = false;
		try {
			sendMutex.lock();
		} catch (Exception e) {
			sendMutex.unlock();
			sendMutex.lock();
		}
		
		isSend = sh.send(oneStr);
		sendMutex.unlock();
		
		return isSend;
    }
    
    /// <summary>
    /// 根据服务名，得到一个主机名
    /// </summary>
    /// <returns>主机名</returns>
    public String pickupOneHostnameByServername(int pickType){
		String hostname = "";
		
		List<UdpInfoEntity> udpList = uh.udpNameList;
		
		for (int i = 0; i < udpList.size() ; i++) {
			
			if (udpList.get(i).displayName.equalsIgnoreCase(connectICE.connectICEName) &&
				udpList.get(i).role == pickType
			) {
				hostname = udpList.get(i).ip;
				break;
			}
		}
		
		return hostname;
	}
    
    /// <summary>
    /// 是否连接
    /// </summary>
    /// <returns></returns>
    public Boolean isConnected() {
      Boolean isConn = false;
      try
      {
          isConn = sh.isConnected();
      }
      catch (Exception e) { 
          Log.d("xxoo", e.getMessage());
      }

      return isConn;
    }
    
    
    /// <summary>
    /// 得到所有中控服务列表
    /// </summary>
    /// <returns>中控列表</returns>
	static public ICELoginEntity[] getAllICENameArray(){
		List<UdpInfoEntity> udpList = uh.udpNameList;
		Hashtable<String, String> allICEName = new Hashtable<String,String>();
		
		List<ICELoginEntity> allICE = new ArrayList<ICELoginEntity>(); //new ICELoginEntity[allICEName.size()];
		Enumeration<String> oneEnume = allICEName.keys();
		for (int i = 0; i < udpList.size(); i++) {
			if (udpList.get(i).role != UdpInfoRoleEntity.RAW_UDP) {
				if (null == allICEName.get(udpList.get(i).displayName)) {
					allICEName.put(udpList.get(i).displayName, udpList.get(i).loginMethod);
					
					ICELoginEntity oneIceLogin = new ICELoginEntity(udpList.get(i).displayName, udpList.get(i).loginMethod);
					allICE.add(oneIceLogin);
				}
			}
		}
		
		ICELoginEntity[] retAllICEArray = new ICELoginEntity[allICE.size()];
		allICE.toArray(retAllICEArray);
		return retAllICEArray;
	}

    
    public ICERoleEntity getConnectICERole()
    {
        return connICERole;
    }

    /// <summary>
    /// 得到所有连接主机名
    /// </summary>
    /// <returns></returns>
    static public String[] getAllICEServerArray(){
		List<UdpInfoEntity> udpList = uh.udpNameList;
		ArrayList<String> serverName = new ArrayList<String>();

		for (int i = 0; i < udpList.size(); i++) {
			serverName.add(udpList.get(i).ip);
		}
		
		String allIceServerArray[]= new String[serverName.size()];
		serverName.toArray(allIceServerArray);
		
		return allIceServerArray;
	}
}
