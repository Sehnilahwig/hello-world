package com.tvmining.sdk.helper;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;

public class SocketHelper {
	private Socket sockHandle;
	
	public SocketHelper(String hostname, int port) {
		// TODO Auto-generated constructor stub
		try {
			Log.d(hostname+":", String.valueOf(port));
			
			sockHandle = new Socket();//hostname, port);
			sockHandle.setKeepAlive(true);
			sockHandle.setSoLinger(false, 0);
			sockHandle.setReceiveBufferSize(65536);
			sockHandle.setSendBufferSize(40960);
			sockHandle.setTcpNoDelay(true);
			sockHandle.setReuseAddress(true);
			sockHandle.connect(new InetSocketAddress(hostname, port), 2000);//

		} catch (Exception e) {
			// TODO: handle exception
			Log.d("socketHelper error:", "端socket啊");
			//sockHandle = null;
			sockHandle = null;
		}
	}	

	public boolean isConnected(){
		Boolean isClose = false;
		if(sockHandle == null){
			return isClose;
		}
		
		try{
			
			//isClose = sockHandle.isConnected();//!sockHandle.isClosed();//        .isConnected();
			isClose = sockHandle.isInputShutdown() || sockHandle.isOutputShutdown();// .isConnected();
			isClose = !isClose;
		}catch(Exception e){
			
		}
		
		return isClose;
	}
	
	/**
	 * 通过端连接送
	 * @param hostname 送到的主机
	 * @param port 送到的端口
	 * @param msg 送出的信息
	 * @return 是否送出
	 */
	public String sendByShort(String hostname, int port, String msg){
		String recvString = "";
		try {
			//Log.d(hostname+":", String.valueOf(port));
			sockHandle = new Socket(hostname, port);
			sockHandle.setSoLinger(false, 0);
			sockHandle.setReceiveBufferSize(65536);
			sockHandle.setSendBufferSize(40960);
			sockHandle.setTcpNoDelay(true);
			sockHandle.setReuseAddress(true);
			
			OutputStream os = sockHandle.getOutputStream();
			byte[] outputWrite = msg.getBytes("UTF-8");
			os.write(outputWrite);
			
			byte[] byteRecv = new byte[65536];
			int recvLength;
			
			InputStream is = sockHandle.getInputStream();
			recvLength = is.read(byteRecv);
			if (recvLength > 0) {
				recvString = new String(byteRecv, 0, recvLength);
			}
			
			sockHandle.shutdownInput();
			sockHandle.shutdownOutput();
			sockHandle.close();
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("socketHelper error:", "端socket啊");
			//sockHandle = null;
		}
		
		return recvString;
	}
	
	public Boolean send(String msg){
		try{
			OutputStream os = sockHandle.getOutputStream();
			byte[] outputWrite = msg.getBytes("UTF-8");
			os.write(outputWrite);
			//os.close();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			Log.d("送出 socket 错误", e.getMessage());
			try {
				disconnect();//sockHandle.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}
	}
	
	public String recv(){
		byte[] byteRecv = new byte[65536];
		int recvLength;
		
		String recvString;
		try {
			InputStream is = sockHandle.getInputStream();
			recvLength = is.read(byteRecv);
			if (recvLength == -1) {
				//disconnect();
				return "";
			}
			recvString = new String(byteRecv, 0, recvLength); 
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("接收 socket 原始出错:", e.getMessage());
			recvLength = 0;
			recvString = "";
			
			try {
				disconnect();//sockHandle.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return recvString;
	}

	public void disconnect(){
		if (sockHandle == null) {
			return ;
		}
		
		try {
			sockHandle.shutdownInput();
			sockHandle.shutdownOutput();
			sockHandle.close();
		} catch (Exception e) {
			Log.d("socket 关闭连接错误", e.getMessage());
		}

		sockHandle = null;
	}
}
