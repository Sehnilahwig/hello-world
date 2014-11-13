package com.tvmining.sdk.service;


import android.util.Log;

import com.tvmining.sdk.dao.ICEConnectorDao;
import com.tvmining.sdk.entity.ICELoginEntity;
import com.tvmining.sdk.entity.ICERoleEntity;
import com.tvmining.sdk.helper.UdpInfoRoleEntity;


public class ICEConnectorService {
	private ICEConnectorDao dao;
	
	
	/**
	 * 连接一个中控
	 * @param serverName 中控的服务名
	 */
	public ICEConnectorService(ICELoginEntity serverName) {
		dao = new ICEConnectorDao(serverName);
		connect();
	}

	public void connect(){
		String masterHostname = dao.pickupOneHostnameByServername(UdpInfoRoleEntity.MASTER_UDP);
		String slaveHostname = dao.pickupOneHostnameByServername(UdpInfoRoleEntity.SLAVE_UDP);
		
		if(!dao.connectByHostname(masterHostname)){
			dao.connectByHostname(slaveHostname);
		}
	}
	
	/**
	 * 得到主机名
	 * @return 主机名
	 */
	public String getConnectHostname(){
		return dao.getConnectHostname();
	}
	
	/**
	 * 重新连接
	 */
	public void reConnect(){
		if (isConnected()) {
			disconnect();
		}
		
		connect();
	}
	
	/**
	 * 得到连接名称
	 * @return 连接的名称
	 */
	public ICELoginEntity getLoginICE(){
		return dao.getLoginICE();
	}

	/**
	 * 是否连接
	 * @return
	 */
	public Boolean isConnected(){
		return dao.isConnected();
	}
	
	static public void init(){
		try {
			ICEConnectorDao.init();
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("初始化连接服务出错", e.getMessage());
		}
	}
	
	  public ICERoleEntity getConnectICERole()
      {
          ICERoleEntity connICERole = dao.getConnectICERole();
          return connICERole;
      }
	
	/**
	 * 原始字符串送给  socket
	 * @param sendStr 送出的字符串
	 * @return
	 */
	public Boolean sendSockRawCnt(String sendStr){
		//if (sendStr.lastIndexOf("CMD:echo") == -1) {
			Log.d("送出原始 socket：", ">"+sendStr+"<");
		//}
		
		return dao.setRawStr(sendStr);
	}
	
	/**
	 * 广播一个命令
	 * @param oneCmd 需要广播的命令
	 * @return 
	 */
	public String broadcastOneCmd(String oneCmd){
		return dao.broadcastRawStr(oneCmd);
	}
	
	
	/**
	 * 关闭连接
	 */
	public void disconnect(){
		dao.disconnect();
	}
	
	/**
	 * 得到 socket 的内容
	 * @return
	 * @throws Exception 
	 */
	public String recvSockRawCnt() throws Exception{
		return dao.getSockeString();
	}
	
	/**
	 * 得到所有中控服务列表
	 * @return 中控列表
	 */
	static public ICELoginEntity[] getAllICENameArray(){
		return ICEConnectorDao.getAllICENameArray();
	}
	
	/**
	 * 得到所有连接主机名
	 * @return
	 */
	static public String[] getAllICEServerArray(){
		return ICEConnectorDao.getAllICEServerArray();
	}
}