package com.tvmining.wifiplus.thread;

import android.content.Context;
import android.util.Log;

import com.tvmining.wifiplus.entity.ICETable;
import com.tvmining.wifiplus.entity.ItemTable;
import com.tvmining.wifiplus.entity.PakgeTable;
import com.tvmining.wifiplus.util.Constant;

public class DBThread extends Thread {
	private Context context;
	private String action;//delete,save,update
	long iceIndex = -1;
	long pakgeIndex = -1;
	
	public DBThread(Context mContext) {
		context = mContext;
	}

	public void run() {
		try {
			while (true) {
				if (!Constant.sqlQueue.isEmpty()) {
					Object obj = null;
					synchronized (Constant.sqlQueue) {
						obj = Constant.sqlQueue.take();
					}
					
					ICETable iceTable = null;
					PakgeTable pakgeTable = null;
					ItemTable itemTable = null;
					
					if(obj instanceof ICETable){
						iceTable = (ICETable) obj;
						iceIndex = Constant.dbConnection.insertICETable(iceTable);
						Log.d("DBThread", "insert iceTable:"+iceTable.getIceName());
					}else if(obj instanceof PakgeTable){
						pakgeTable = (PakgeTable) obj;
//						pakgeTable.setIceIndex(String.valueOf(iceIndex));
						pakgeIndex = Constant.dbConnection.insertPakgeTable(pakgeTable);
						Log.d("DBThread", "insert pakgeTable:"+pakgeTable.getPakgeName());
					}else if(obj instanceof ItemTable){
						itemTable = (ItemTable) obj;
						Constant.dbConnection.insertItemTable(itemTable);
						Log.d("DBThread", "insert itemTable:"+itemTable.getItemTitle());
					}
				} else {
					synchronized (Constant.sqlQueue) {
						Constant.sqlQueue.wait();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}