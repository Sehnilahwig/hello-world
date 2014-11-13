package com.tvmining.wifiplus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

import com.tvmining.wifiplus.util.Constant;

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// �������wifi�Ĵ���رգ���wifi�������޹�
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
			switch (wifiState) {
			case WifiManager.WIFI_STATE_ENABLED:
				Log.e("APActivity", "WIFI_STATE_ENABLED");
				break;
			case WifiManager.WIFI_STATE_ENABLING:
				Log.e("APActivity", "WIFI_STATE_ENABLING");
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				Log.e("APActivity", "WIFI_STATE_DISABLED");
				break;
			case WifiManager.WIFI_STATE_DISABLING:
				Log.e("APActivity", "WIFI_STATE_DISABLING");
				break;
			}
		}
		// �������wifi������״̬���Ƿ�������һ����Ч����·�ɣ����ϱ߹㲥��״̬��WifiManager.WIFI_STATE_DISABLING����WIFI_STATE_DISABLED��ʱ�򣬸���ӵ�����㲥��
		// ���ϱ߹㲥�ӵ��㲥��WifiManager.WIFI_STATE_ENABLED״̬��ͬʱҲ��ӵ�����㲥����Ȼ�մ�wifi�϶���û�����ӵ���Ч������
		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			Parcelable parcelableExtra = intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (null != parcelableExtra) {
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
				switch (networkInfo.getState()) {
				case CONNECTED:
					Log.e("APActivity", "CONNECTED");
					Constant.wifiConnected = true;
					break;
				case CONNECTING:
					Log.e("APActivity", "CONNECTING");
					break;
				case DISCONNECTED:
					Log.e("APActivity", "DISCONNECTED");
					if (Constant.wifiConnected) {
						Constant.wifiConnected = false;
						// goBack();
					}
					break;
				case DISCONNECTING:
					Log.e("APActivity", "DISCONNECTING");
					break;
				case SUSPENDED:
					Log.e("APActivity", "SUSPENDED");
					break;
				case UNKNOWN:
					Log.e("APActivity", "UNKNOWN");
					break;
				default:
					break;
				}
			}
		}
		// ��������������ӵ����ã�����wifi���ƶ���ݵĴ򿪺͹رա�
		// ����õĻ����������wifi���򿪣��رգ��Լ������Ͽ��õ����Ӷ���ӵ������log
		// ����㲥�����׶��Ǳ��ϱ������㲥�ķ�ӦҪ�����ֻ��Ҫ����wifi���Ҿ��û������ϱ�������ϱȽϺ���
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
			NetworkInfo info = intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (info != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("info.getTypeName() : " + info.getTypeName() + "\n");
				sb.append("getSubtypeName() : " + info.getSubtypeName() + "\n");
				sb.append("getState() : " + info.getState() + "\n");
				sb.append("getDetailedState() : "
						+ info.getDetailedState().name() + "\n");
				sb.append("getDetailedState() : " + info.getExtraInfo() + "\n");
				sb.append("getType() : " + info.getType());
				Log.e("APActivity", sb.toString());
			}
		}
	}
}
