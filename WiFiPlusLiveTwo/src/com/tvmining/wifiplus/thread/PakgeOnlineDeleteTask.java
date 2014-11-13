package com.tvmining.wifiplus.thread;

import java.util.ArrayList;
import java.util.Vector;

import android.os.AsyncTask;

import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.wifiplus.entity.LocalGroup;
import com.tvmining.wifiplus.entity.PakgeTable;
import com.tvmining.wifiplus.image.loader.Images;
import com.tvmining.wifiplus.image.loader.ListImageAdapter;
import com.tvmining.wifiplus.util.Utility;

public class PakgeOnlineDeleteTask extends AsyncTask<Object, Object, Object> {

	private int position;
	private ListImageAdapter adapter;

	public PakgeOnlineDeleteTask(ListImageAdapter adapter,int position) {
		this.position = position;
		this.adapter = adapter;
	}

	@SuppressWarnings("unchecked")
	protected synchronized Object doInBackground(Object... aobj) {
		try {
			
			if(Images.allPackName != null){
				Vector vector = new Vector();
				for(int i=0;i<Images.allPackName.length;i++){
					if(i != position){
						vector.add(Images.allPackName[i]);
					}
				}
				
				Images.allPackName = (PackInfoEntity[]) vector.toArray();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	protected void onPostExecute(Object obj) {
		adapter.notifyDataSetChanged();
		
	}

	protected void onProgressUpdate(Object... aobj) {

	}
}
