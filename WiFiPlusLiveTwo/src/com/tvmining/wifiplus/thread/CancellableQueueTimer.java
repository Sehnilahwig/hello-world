package com.tvmining.wifiplus.thread;

import android.os.Handler;

public class CancellableQueueTimer implements Runnable {
	private Runnable callback;
	private Handler handler;

	public CancellableQueueTimer(Handler handler, int t, Runnable callback) {
		this.handler = handler;
		handler.postDelayed(this, t);
		this.callback = callback;
	}

	public void cancel() {
		if (this.handler != null) {
			if (this.callback != null)
				this.handler.removeCallbacks(this.callback);
			this.handler = null;
		}
		this.callback = null;
	}

	public void run() {
		this.handler = null;
		if (this.callback != null) {
			this.callback.run();
			this.callback = null;
		}
	}
}
