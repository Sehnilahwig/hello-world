package com.tvmining.wifiplus.service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class HttpServerService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
	}

	public String getLocalIpAddress() {
		try {
			// 遍历网络接口
			Enumeration<NetworkInterface> infos = NetworkInterface
			.getNetworkInterfaces();
			while (infos.hasMoreElements()) {
				// 获取网络接口
				NetworkInterface niFace = infos.nextElement();
				Enumeration<InetAddress> enumIpAddr = niFace.getInetAddresses();
				while (enumIpAddr.hasMoreElements()) {
					InetAddress mInetAddress = enumIpAddr.nextElement();
					// 所获取的网络地址不是127.0.0.1时返回得得到的IP
					if (!mInetAddress.isLoopbackAddress()
					&& InetAddressUtils.isIPv4Address(mInetAddress
					.getHostAddress())) {
						return mInetAddress.getHostAddress().toString();
					}
				}

			}

		} catch (SocketException e) {

		}

		return null;
	}

}