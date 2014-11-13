package com.tvmining.sdk.helper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.ICELoginMethodEntity;


public class UdpHelper implements Runnable{
	public static List<UdpInfoEntity> udpNameList = new ArrayList<UdpInfoEntity>();
	public static Thread threadRunner;
	
	public UdpHelper() {
		
	}
	
	public void run(){
		DatagramSocket iceBroad = null;
		try {
			iceBroad = new DatagramSocket(8883);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			try {
				iceBroad.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			e1.printStackTrace();
			return;
		}
		
		try { 
        	byte receiver[]=new byte[128]; 
        	DatagramPacket pack=new DatagramPacket(receiver,receiver.length); 
        	//DatagramSocket iceBroad=new DatagramSocket(8883); 
        	
        	while (!Thread.interrupted()) {
        		iceBroad.receive(pack); 
        		String rawBroadText = new String(pack.getData(), 0, pack.getLength(), "UTF-8");
        		String hostIp = pack.getAddress().getHostAddress();
        		Boolean isChange = false;
        		Boolean isFound = false;
        		
        		for (int i = 0; i < UdpHelper.udpNameList.size(); i++) {
					UdpInfoEntity oneUdpInfo = UdpHelper.udpNameList.get(i);
					if (oneUdpInfo.ip.equals(hostIp)) {
						isFound = true;
						oneUdpInfo.exireTime++;
						break;
					}
				}
        		
        		
        		if (!isFound) {
					Log.d("得到新 UDP", rawBroadText);
					String[] boradcastInfo = rawBroadText.split("_", 4);
					String broadFlag = boradcastInfo[0];
					String broadRole = boradcastInfo[1];
					String broadName = boradcastInfo[2];
					String loginMethod = boradcastInfo[3];
					
					if (!broadFlag.equals("cc3")) {
						continue;
					}
					
					Log.d("received:", rawBroadText);
					
					UdpInfoEntity newUdpInfo = new UdpInfoEntity();
					newUdpInfo.displayName = broadName;
					
					if (broadRole.equals("master")) {
						newUdpInfo.role = UdpInfoRoleEntity.MASTER_UDP;
					}else if (broadRole.equals("slave")) {
						newUdpInfo.role = UdpInfoRoleEntity.SLAVE_UDP;
					}else {
						newUdpInfo.role = UdpInfoRoleEntity.RAW_UDP;
					}
					
					if (loginMethod.toLowerCase().equalsIgnoreCase(ICELoginMethodEntity.PASSWORD))
                    {
                        newUdpInfo.loginMethod = ICELoginMethodEntity.PASSWORD;
                    }
                    else if (loginMethod.toLowerCase().equalsIgnoreCase(ICELoginMethodEntity.REGISTRY))
                    {
                        newUdpInfo.loginMethod = ICELoginMethodEntity.REGISTRY;
                    }
                    else {
                        newUdpInfo.loginMethod = ICELoginMethodEntity.DEFAULT;
                    }

					
					newUdpInfo.exireTime = 10;
					newUdpInfo.ip = hostIp;
					UdpHelper.udpNameList.add(newUdpInfo);
					isChange = true;
				}
        		
        		for (int i = 0; i < UdpHelper.udpNameList.size(); i++) {
        			UdpHelper.udpNameList.get(i).exireTime--;
					
					if (UdpHelper.udpNameList.get(i).exireTime <= 0) {
						UdpHelper.udpNameList.remove(i);
						isChange = true;
					}
				}
        	
        		if(isChange){
        			ICESDK.notifySearchingUpdateWithServiceListEvent(ICESDK.getAllICENameArray());
        		}
        		Thread.sleep(1000);
        	}
        	
        	iceBroad.close();
        	iceBroad.disconnect();
       	}catch(Exception e){
       		e.printStackTrace();
       		Log.d("错误啊错误 UDP RR:", e.getMessage());
        } 
		
		try {

			iceBroad.close();
	    	iceBroad.disconnect();	
		} catch (Exception e) {
			// TODO: handle exception
		}
		
        UdpHelper.threadRunner.interrupt();
	}

}
