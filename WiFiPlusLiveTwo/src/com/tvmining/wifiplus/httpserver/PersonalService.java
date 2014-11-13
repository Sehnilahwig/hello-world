/*   
 *   Copyright (C) 2012  Alvin Aditya H,
 *   					 Shanti F,
 *   					 Selviana 
 *   
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *       
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *       
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *    MA 02110-1301, USA.
 */

package com.tvmining.wifiplus.httpserver;

import com.tvmining.wifiplus.util.Utility;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PersonalService extends Service {
	private Server server = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		server = new Server(this, "server");
	}
	@Override
	public void onDestroy() {
		server.stopServer();
		super.onDestroy();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(!server.isAlive()){
			server.start();		
		}
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

}
