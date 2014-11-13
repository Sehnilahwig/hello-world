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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.tvmining.wifiplus.util.Utility;

import android.content.Context;

public class Server extends Thread{
	
	public static int port = 8080;
	public static String host = "127.0.0.1";
	public Context ctx;
	public boolean RUN = false;
	
	public Server(Context context, String threadName){
		this.ctx = context;
		this.setName(threadName);
	}
	
	@Override
	public void run(){
		super.run();
		ServerSocket server = null;
		Socket soket = null;
		try {
			server = new ServerSocket(0);
			server.setReuseAddress(true);
			server.setSoTimeout(5000);
			
			port = server.getLocalPort();
			host = Utility.getLocalIpAddress();
			
			RUN = true;
			while(RUN){
				try{
				soket = server.accept();
				System.out.println("client connected!");
				 
				Thread httpthread = new HttpThread(ctx, soket, "httpthread");
				httpthread.start();
				} catch (InterruptedIOException eiox){					
				} catch (Exception exc){
					System.err.println(exc.getMessage());
					exc.printStackTrace();
				}
				
			}	
			server.close();
			
	    } catch (IOException e) {
	    	System.err.println("Exception in Server.java:socket");
	    	e.printStackTrace();
	    	try{
		    	if(soket != null) soket.close();
		    	server.close();
	    	} catch(Exception ex){
	    		System.err.println("Exception in Server.java:cannot close socket");
	    		ex.printStackTrace();
	    	}
			
		} 
		
	}

	public void stopServer() {
		RUN = false;
	}

}