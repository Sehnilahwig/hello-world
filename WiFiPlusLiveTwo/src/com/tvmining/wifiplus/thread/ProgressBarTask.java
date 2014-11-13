package com.tvmining.wifiplus.thread;

import com.tvmining.wifiplus.application.EmeetingApplication;
import com.tvmining.wifiplus.util.Constant;

import android.os.AsyncTask;
import android.os.Message;


public class ProgressBarTask extends AsyncTask<Void, Void, Object> {

	private boolean run =true;
	
	public ProgressBarTask(){
	}
	
	protected Object doInBackground(Void... args) {
		 while(run){
			 try {
				 if(EmeetingApplication.getDownloadUnInsertedCount() <= 0){
					 Message msg = new Message();
					 msg.what = Constant.HANDLER_ENTER_LOCAL_VIEW;
					 Constant.activity.getHandler().sendMessage(msg);
					 run=false;
					 break;
				 }
				 
				 Thread.sleep(500);
			 } catch (InterruptedException e) {
			  // TODO Auto-generated catch block
				 run=false;
			 }
		 }

		return null;
	}

	protected void onPostExecute(Object result) {
	}
}
