package com.tvmining.wifiplus.thread;

import java.util.Vector;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.tvmining.wifiplus.activity.MainActivity;
import com.tvmining.wifiplus.entity.DownloadProgressEntity;
import com.tvmining.wifiplus.entity.DownloadTempTable;
import com.tvmining.wifiplus.entity.ICETable;
import com.tvmining.wifiplus.entity.PakgeTable;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.DelFile;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.wifipluseq.R;

public class LoadDBDataTask extends AsyncTask {

	private Context mContext;
	private boolean isReload;

	public LoadDBDataTask(Context mContext,boolean isReload) {
		this.isReload = isReload;
		this.mContext = mContext;
	}

	protected Void doInBackground(Object... object) {
		
		Vector iceVector = Constant.dbConnection.getAllICEData();
		Vector adapterAll = new Vector();
		
		if(iceVector != null){
			for (int i = 0; i < iceVector.size(); i++) {
				ICETable iceTable = (ICETable) iceVector.get(i);
				Vector pkgVector = Constant.dbConnection.getPkgsByICEIndex(iceTable
						.getIceIndex());
				if(pkgVector != null){
					for (int j = 0; j < pkgVector.size(); j++) {
						PakgeTable pkgTable = (PakgeTable) pkgVector.get(j);

						DownloadTempTable tampTable = Constant.dbConnection.queryDownloadTempByStatus(pkgTable.getPakgeIndex(), 1);
						if(tampTable != null){
							long totalSize = Long.parseLong(tampTable.getDownloadTotalSize());
							long downloadSize = Long.parseLong(tampTable.getDownloadedSize());
							
							if(totalSize != 0){
								pkgTable.setProgress((downloadSize*100f)/totalSize);
							}
							
							DownloadProgressEntity progressEntity = new DownloadProgressEntity();
							progressEntity.setIceIndex(Long.parseLong(pkgTable.getIceIndex()));
							progressEntity.setPakgeIndex(pkgTable.getPakgeIndex());
							progressEntity.setPackageGuid(pkgTable.getPakgeGuid());
							progressEntity.setProgress((int)pkgTable.getProgress());
							progressEntity.setIceDate(pkgTable.getPakgeDownloadDate());
							progressEntity.setPakgeName(pkgTable.getPakgeName());
							
							Constant.downloadingMap.put(pkgTable.getPakgeGuid(), progressEntity);
						}else if(pkgTable.getStatus() != Constant.DOWNLOAD_FINISHED){
							
							DownloadProgressEntity progressEntity = new DownloadProgressEntity();
							progressEntity.setIceIndex(Long.parseLong(pkgTable.getIceIndex()));
							progressEntity.setPakgeIndex(pkgTable.getPakgeIndex());
							progressEntity.setPackageGuid(pkgTable.getPakgeGuid());
							progressEntity.setProgress(0);
							progressEntity.setIceDate(pkgTable.getPakgeDownloadDate());
							progressEntity.setPakgeName(pkgTable.getPakgeName());
							
							Constant.downloadingMap.put(pkgTable.getPakgeGuid(), progressEntity);
						}
					}
					Object aobj[] = new Object[2];
					aobj[0] = iceTable;
					aobj[1] = pkgVector;
					adapterAll.add(aobj);
				}
			}
		}
		
		Message msg = new Message();
		msg.obj = adapterAll;
		if(isReload){
			msg.what = Constant.HANDLER_LOCAL_LOAD_DATA;
		}else{
			msg.what = Constant.HANDLER_LOCAL_RESPONSE_NOTIFICATION;
		}
		
		Constant.activity.getHandler().sendMessage(msg);
		
		return null;
	}

	protected void onPostExecute(Void void1) {
	}

	protected void onProgressUpdate(Object... aobj) {
	}

}
