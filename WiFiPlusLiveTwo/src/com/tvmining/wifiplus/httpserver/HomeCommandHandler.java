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
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import android.content.Context;

public class HomeCommandHandler implements HttpRequestHandler {

	private Context context = null;
	private String host = "localhost";
	
	public HomeCommandHandler(Context context) {
		this.context = context;
	}
	
	@Override
	public void handle(HttpRequest req, HttpResponse resp, HttpContext arg2)
			throws HttpException, IOException {
		
		this.host = req.getFirstHeader("Host").getValue();
		System.out.println("Host : "+host);
		HttpEntity entity = new EntityTemplate(new ContentProducer() {
			public void writeTo(final OutputStream outstream) throws IOException {
				OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
				String resp = "<html><head></head>" +
						"<body><center><h1>Welcome to Personal Server<h1></center>" +
						"<p>to browse file click <a href=\"http://"+host+"/dir\">here</a></p>" +
						"</body></html>";

				writer.write(resp);
				writer.flush();
			}
		});
		resp.setHeader("Content-Type", "text/html");
		
		resp.setEntity(entity);

	}

}
