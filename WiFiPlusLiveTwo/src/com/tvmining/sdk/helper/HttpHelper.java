package com.tvmining.sdk.helper;


//import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;  
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;



import android.util.Log;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.uploadFileEntity;



public class HttpHelper {
    /**
     * 正确的提交
     */
    static int HTTP_UPLOAD_OK = 200;

    /**
     * 错误的提交
     */
    static int HTTP_UPLOAD_FAIL = 0;

    /**
     * 当前的上传数量
     */
    public static long HTTP_UPLOADING_BYTES = 0;
    
    /**
     * 是否取消上传
     */
    public static boolean isStopUploading;
	
    public static long getUrlLength(String serverURL){
        URL postUrl;
        long urlLength=0;
        HttpURLConnection postConn = null;
		try {
			postUrl = new URL(serverURL);
			postConn = (HttpURLConnection)postUrl.openConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return urlLength;
		}
        
        try
        {
        	postConn.setRequestMethod("HEAD");
        	if(postConn.getResponseCode() == 200){
        		urlLength = postConn.getContentLength();
        	} 
        	postConn.disconnect();
        }catch(Exception e){
        	Log.d("xx", e.getMessage());
        }
        
        return urlLength;
    } 
    
    /// <summary>
    /// GET 得到一个地址
    /// </summary>
    /// <param name="serverURL"></param>
    /// <returns></returns>
    public static String getURL(String serverURL) {
    	HttpClient httpclient = new DefaultHttpClient();

    	// Prepare a request object
    	HttpGet httpget = new HttpGet(serverURL);
    	String httpResp = "";
    	
    	HttpResponse response;
    	
    	
		try {
			response = httpclient.execute(httpget);
		} catch (Exception e){
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return httpResp;
		}

    	// Examine the response status
    	if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
    		return httpResp;
    	}

    	// Get hold of the response entity
    	HttpEntity entity = response.getEntity();

    	// If the response does not enclose an entity, there is no need
    	// to worry about connection release
    	if (entity != null) {
    		InputStream instream = null;
    	    try {
    	    	instream = entity.getContent();
    	        BufferedReader reader = new BufferedReader(
    	                new InputStreamReader(instream));
    	        // do something useful with the response
    	        
    	        String oneHttpResp;
    	        while((oneHttpResp = reader.readLine()) != null){
    	        	httpResp += oneHttpResp;
    	        }
    	        
    	        httpget.abort();
    	        return httpResp;
    	    } catch (Exception ex) {

    	        // In case of an IOException the connection will be released
    	        // back to the connection manager automatically
    	        try {
					instream.close();
					httpget.abort();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}

    	        return httpResp;
    	    }
       }
    
    	
		return httpResp;
    }

    
    
    public static String postURL(String serverURL, Hashtable<String, String> postDict) {
    	
      	// Prepare a request object
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpPost httppost = new HttpPost(serverURL);

    	List<NameValuePair> postPairs = new ArrayList<NameValuePair>();
    	
    	Enumeration<String> pdKeys = postDict.keys();
    	while(pdKeys.hasMoreElements()){
    		String k = pdKeys.nextElement();
    		String v = postDict.get(k);
    		
    		BasicNameValuePair oneNVP = new BasicNameValuePair(k, v);
    		postPairs.add(oneNVP);
    	}
    	
    	String httpRes = "";
    	InputStream instream = null;
    	try {
			UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(postPairs, "UTF-8");
			httppost.setEntity(postEntity);
			

			HttpResponse response = httpclient.execute(httppost);

	    	// Examine the response status
	    	if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
	    		return httpRes;
	    	}

	    	// Get hold of the response entity
	    	HttpEntity entity = response.getEntity();

	    	// If the response does not enclose an entity, there is no need
	    	// to worry about connection release
	    	if (entity != null) {
	    	   	instream = entity.getContent();
    	        BufferedReader reader = new BufferedReader(
    	                new InputStreamReader(instream));
    	        // do something useful with the response
    	        
    	        String oneHttpResp;
    	        while((oneHttpResp = reader.readLine()) != null){
    	        	httpRes += oneHttpResp;
    	        }

    	        httppost.abort();
    	        return httpRes;
	    	 }
	    	
			return httpRes;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 try {
					instream.close();
					httppost.abort();
			} catch (IOException ee) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
			}
			 
			e.printStackTrace();
			return httpRes;
		}
    

    }
    
    /// <summary>
    /// ivision2 的文件提交
    /// </summary>
    /// <param>
    /// 服务器 IP
    /// POST 提交的字典
    /// 上传的文件
    /// </param>
    /// <returns>HTTP 状态</returns>
    public static String UploadFile(String ServerURL, Hashtable<String, String> postDict, Hashtable<String, String> uploadFileArray){
    	//HTTP_UPLOADING_BYTES = 0;
    	isStopUploading = false;
        URL postUrl;
        HttpURLConnection postConn = null;
		
		String source = postDict.get(uploadFileEntity.SOURCE_OBJ_KEY);
		//if(source != null){
		//	postDict.remove(uploadFileEntity.SOURCE_OBJ_KEY);
		//}

		try {
			postUrl = new URL(ServerURL);
			postConn = (HttpURLConnection)postUrl.openConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
        
        postConn.setDoInput(true);
        postConn.setDoOutput(true);
        postConn.setUseCaches(false);
        //postConn.setRequestMethod("POST");
        
        
        //构建 http body 的变量定义
        String boundary = "-----------------------------androidsdK" + String.valueOf(new java.util.Date().getTime());
        String crlf = "\r\n";

        long contentLength = 0;
		long totalFileSize = 0;


        //构建 http body 的前缀
        StringBuilder[] httpBodyPrefix = new StringBuilder[uploadFileArray.size()];
        int i=0;
        
        Enumeration<String> keys = uploadFileArray.keys();
        while(keys.hasMoreElements())
        {
        	String oneKey = keys.nextElement();
        	String oneValue = uploadFileArray.get(oneKey);
        	String oneMimeType = getMimeType(oneValue);
        	long oneFileLength = getFileLength(oneValue);
        	
            httpBodyPrefix[i] = new StringBuilder();
            httpBodyPrefix[i].append(boundary);
            httpBodyPrefix[i].append(crlf);
            httpBodyPrefix[i].append("Content-Disposition: form-data; name=\""+oneKey+"\"; filename=\""+oneValue+"\"");

            httpBodyPrefix[i].append(crlf);
            httpBodyPrefix[i].append("Content-Type: "+oneMimeType);
            httpBodyPrefix[i].append(crlf);
            httpBodyPrefix[i].append(crlf);

            try {
				contentLength += httpBodyPrefix[i].toString().getBytes("UTF-8").length;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				contentLength += httpBodyPrefix[i].toString().getBytes().length; 
			}
            contentLength += oneFileLength;
            contentLength += crlf.length();
            
			totalFileSize += oneFileLength;

            i++;
        }

        //构建 http body 的后缀
        StringBuilder httpBodySuffix = new StringBuilder();
        Enumeration<String> postDictKeys = postDict.keys();
        while(postDictKeys.hasMoreElements())
        {
        	String key = postDictKeys.nextElement();
        	String value = postDict.get(key);
        	
            httpBodySuffix.append(boundary);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append("Content-Disposition: form-data; name=\""+key+"\"");
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(value);
            httpBodySuffix.append(crlf);
        }

        httpBodySuffix.append(boundary);
        httpBodySuffix.append("--");
        httpBodySuffix.append(crlf);

        
        byte[] hbp;//= Encoding.UTF8.GetBytes(httpBodyPrefix.ToString());
        byte[] hbs;
		try {
			hbs = httpBodySuffix.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hbs = httpBodyPrefix.toString().getBytes();
		}

        contentLength += hbs.length;

        FileInputStream fs;//= new FileStream(uploadFile, FileMode.Open, FileAccess.Read);

        //FileOutputStream fsReader;

        int bufSize = 8192;
        byte[] cnt = new byte[bufSize];
        //int offset = 0;
        int oneRead;
        
        String httpResp="";
        
        try
        {
        	postConn.setRequestMethod("POST");
        	postConn.setRequestProperty("Connection", "Keep-Alive");
        	postConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary.substring(2));
        	postConn.setRequestProperty("Accept", "image/jpeg, application/x-ms-application, image/gif, application/xaml+xml, image/pjpeg, application/x-ms-xbap, */*");
        	postConn.setRequestProperty("Referer", "");
        	postConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/4.0; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1) ; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Tablet PC 2.0; .NET4.0C; SE 2.X MetaSr 1.0)");
        	postConn.setRequestProperty("Expect", "");
//        	postConn.setFixedLengthStreamingMode((int) contentLength);
        	//postConn.setRequestProperty("Content-Length", String.valueOf(contentLength));
        	postConn.setRequestProperty("Accept-Encoding", "");
        	postConn.setConnectTimeout(9999999);
        	postConn.setReadTimeout(9999999);
        	
        	keys = uploadFileArray.keys();
        	i=0;
        	long filesize;
        	long process;
			HTTP_UPLOADING_BYTES = 0;
            while(keys.hasMoreElements())
            {
            	if(isStopUploading){
            		break;
            	}
            	
            	String oneKey = keys.nextElement();
            	String oneValue = uploadFileArray.get(oneKey);
            	filesize = getFileLength(oneValue);
                fs = getFileStream(oneValue); //uploadFileArray[i].getFileStream();//new FileStream(uploadFile, FileMode.Open, FileAccess.Read);
                //long oneFileLength = getFileLength(oneValue);
                
                //offset = 0;
                hbp = httpBodyPrefix[i].toString().getBytes("UTF-8");//Encoding.UTF8.GetBytes(httpBodyPrefix[i].ToString());
                
                postConn.getOutputStream().write(hbp);
                process=0;
                while ((oneRead = fs.read(cnt, 0, cnt.length)) > 0)
                {
                	if(isStopUploading){
                		break;
                	}
                    //offset += oneRead;
                	//HTTP_UPLOADING_BYTES += oneRead;
                	process+=oneRead;
					HTTP_UPLOADING_BYTES += oneRead;
                	ICESDK.notifyHttpUploadingProgressEvent(oneValue, filesize, process, HTTP_UPLOADING_BYTES, totalFileSize, source);
                    postConn.getOutputStream().write(cnt, 0, oneRead);
                }

                postConn.getOutputStream().write(crlf.getBytes());
                fs.close();
                i++;
            }

            postConn.getOutputStream().write(hbs);
            postConn.getOutputStream().flush();
            postConn.getOutputStream().close();
            
            if (postConn.getResponseCode() != HttpStatus.SC_OK)
            {
                postConn.disconnect();
                return httpResp;
            }

            StringBuilder resp = new StringBuilder();
            InputStreamReader responseReader = new InputStreamReader(postConn.getInputStream(), "UTF-8");
            char[] responseBuff = new char[8192];
            while((oneRead = responseReader.read(responseBuff)) > 0){
            	resp.append(responseBuff, 0, oneRead);
            }
            
            httpResp = resp.toString();
            postConn.getInputStream().close();
            postConn.disconnect();
            
            return httpResp;
        }catch (Exception ex)
        {
            return "xxoo";
        }    	
    }
    
    /// <summary>
    /// 批量上传
    /// </summary>
    /// <param name="ServerIp">服务器 IP</param>
    /// <param name="postDict">上传的字典</param>
    /// <param name="uploadFileArray">上传文件的实体数组</param>
    /// <returns>返回的 JSON</returns>
    public static String batchUploadFile(String ServerURL, Hashtable<String, String> postDict, uploadFileEntity[] uploadFileArray)
    {
    	isStopUploading = false;
    	//HTTP_UPLOADING_BYTES = 0;
        URL postUrl;
        HttpURLConnection postConn = null;
        OutputStream outStream = null;

		String source = postDict.get(uploadFileEntity.SOURCE_OBJ_KEY);
		//if(source != null){
			//postDict.remove(uploadFileEntity.SOURCE_OBJ_KEY);
		//}

		try {
			postUrl = new URL(ServerURL);
			postConn = (HttpURLConnection)postUrl.openConnection();

	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
        
        postConn.setDoInput(true);
        postConn.setDoOutput(true);
        postConn.setUseCaches(false);
        
        //构建 http body 的变量定义
        String boundary = "-----------------------------androidsdk" + String.valueOf(new java.util.Date().getTime());
        String crlf = "\r\n";

        long contentLength = 0;


        //构建 http body 的前缀
        StringBuilder[] httpBodyPrefix = new StringBuilder[uploadFileArray.length];
        int i;
        long totalFileSize = 0; 
        for (i = 0; i < uploadFileArray.length; i++)
        {
            httpBodyPrefix[i] = new StringBuilder();
            httpBodyPrefix[i].append(boundary);
            httpBodyPrefix[i].append(crlf);
            httpBodyPrefix[i].append("Content-Disposition: form-data; name=\"file[]\"; filename=\""+uploadFileArray[i].getFileName()+"\"");

            httpBodyPrefix[i].append(crlf);
            httpBodyPrefix[i].append("Content-Type: "+uploadFileArray[i].getMimeType());
            httpBodyPrefix[i].append(crlf);
            httpBodyPrefix[i].append(crlf);

            try {
				contentLength += httpBodyPrefix[i].toString().getBytes("UTF-8").length;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				contentLength += httpBodyPrefix[i].toString().getBytes().length; 
			}
            contentLength += uploadFileArray[i].getFileLength();
			totalFileSize += uploadFileArray[i].getFileLength();

            contentLength += crlf.length();
        }

	

        //构建 http body 的后缀
        StringBuilder httpBodySuffix = new StringBuilder();
        Enumeration<String> postDictKeys = postDict.keys();

        while(postDictKeys.hasMoreElements())
        {
        	String key = postDictKeys.nextElement();
        	String value = postDict.get(key);
        	
            httpBodySuffix.append(boundary);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append("Content-Disposition: form-data; name=\""+key+"\"");
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(value);
            httpBodySuffix.append(crlf);
            
 
        }

        for (i = 0; i < uploadFileArray.length; i++)
        {
            httpBodySuffix.append(boundary);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append("Content-Disposition: form-data; name=\"title[]\"");
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(uploadFileArray[i].getTitle());
            httpBodySuffix.append(crlf);

            httpBodySuffix.append(boundary);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append("Content-Disposition: form-data; name=\"desc[]\"");
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(uploadFileArray[i].getDesc());
            httpBodySuffix.append(crlf);

            httpBodySuffix.append(boundary);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append("Content-Disposition: form-data; name=\"tag[]\"");
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(uploadFileArray[i].getTag());
            httpBodySuffix.append(crlf);

            httpBodySuffix.append(boundary);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append("Content-Disposition: form-data; name=\"guid[]\"");
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(uploadFileArray[i].getGuid());
            httpBodySuffix.append(crlf);

            httpBodySuffix.append(boundary);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append("Content-Disposition: form-data; name=\"file_type[]\"");
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(uploadFileArray[i].getFileType());
            httpBodySuffix.append(crlf);
            
            httpBodySuffix.append(boundary);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append("Content-Disposition: form-data; name=\"filesize[]\"");
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(crlf);
            httpBodySuffix.append(uploadFileArray[i].getFileLength());
            httpBodySuffix.append(crlf);
        }

        httpBodySuffix.append(boundary);
        httpBodySuffix.append("--");
        httpBodySuffix.append(crlf);

        
        //byte[] hbp;//= Encoding.UTF8.GetBytes(httpBodyPrefix.ToString());
        byte[] hbs;
		try {
			hbs = httpBodySuffix.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hbs = httpBodyPrefix.toString().getBytes();
		}

        contentLength += hbs.length;

        FileInputStream fs;//= new FileStream(uploadFile, FileMode.Open, FileAccess.Read);

        //FileOutputStream fsReader;

        int bufSize = 1024;
        byte[] cnt = new byte[bufSize];
        //int offset = 0;
        int oneRead;
        String httpResp="";
        try
        {
//        	postConn.setRequestMethod("POST");
//        	postConn.setRequestProperty("Connection", "Keep-Alive");
//        	postConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
//        	postConn.setRequestProperty("Accept", "image/jpeg, application/x-ms-application, image/gif, application/xaml+xml, image/pjpeg, application/x-ms-xbap, */*");
//        	postConn.setRequestProperty("Referer", "");
//        	postConn.setRequestProperty("UserAgent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/4.0; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1) ; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Tablet PC 2.0; .NET4.0C; SE 2.X MetaSr 1.0)");
//        	postConn.setRequestProperty("Expect", "");
//        	postConn.setRequestProperty("Content-Length", String.valueOf(contentLength));
//        	

        	postConn.setRequestMethod("POST");
        	postConn.setRequestProperty("Connection", "Keep-Alive");
        	postConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary.substring(2));
        	postConn.setRequestProperty("Accept", "image/jpeg, application/x-ms-application, image/gif, application/xaml+xml, image/pjpeg, application/x-ms-xbap, */*");
        	postConn.setRequestProperty("Referer", "");
        	postConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/4.0; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1) ; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Tablet PC 2.0; .NET4.0C; SE 2.X MetaSr 1.0)");
        	postConn.setRequestProperty("Expect", "");
        	//postConn.setFixedLengthStreamingMode
        	postConn.setFixedLengthStreamingMode((int) contentLength);
        	//postConn.setRequestProperty("Content-Length", String.valueOf(contentLength));
        	postConn.setRequestProperty("Accept-Encoding", "");
        	postConn.setConnectTimeout(9999999);
        	postConn.setReadTimeout(9999999);
        	
        	outStream = postConn.getOutputStream();	
        	//BufferedOutputStream outStream = new BufferedOutputStream(postConn.getOutputStream());
            long filesize;
            long uploadingProgress;
			HTTP_UPLOADING_BYTES = 0;
        	for (i = 0; i < uploadFileArray.length; i++)
            {
            	if(isStopUploading){
            		break;
            	}
            
                fs = uploadFileArray[i].getFileStream();//new FileStream(uploadFile, FileMode.Open, FileAccess.Read);
                //long oneFileLength = uploadFileArray[i].getFileLength();
                
                //offset = 0;
                //hbp = httpBodyPrefix[i].toString().getChars(0, oneRead, cnt, i)  getBytes("UTF-8");//Encoding.UTF8.GetBytes(httpBodyPrefix[i].ToString());
                
                
                //DataOutputStream outStream = new DataOutputStream(postConn.getOutputStream());
                try{ 
	                outStream.write(httpBodyPrefix[i].toString().getBytes("UTF-8"));
	              }catch(Exception ex){
	              	
	              }
                
                //postConnSW.write(httpBodyPrefix[i].toString());
                filesize = uploadFileArray[i].getFileLength();
                uploadingProgress=0;
                while ((oneRead = fs.read(cnt, 0, cnt.length)) >= 0)
                {
                	if(isStopUploading){
                		break;
                	}
                	
                    //offset += oneRead;
                	if(oneRead > 0){
                		//HTTP_UPLOADING_BYTES += oneRead;
                		outStream.write(cnt, 0, oneRead);
                		uploadingProgress += oneRead;
                	    HTTP_UPLOADING_BYTES += oneRead;	

                		ICESDK.notifyHttpUploadingProgressEvent(uploadFileArray[i].getFileName(), filesize, uploadingProgress, HTTP_UPLOADING_BYTES, totalFileSize, source);
                	}
                }
                
                
                outStream.write(crlf.getBytes());
                outStream.flush();
                //postConn.getOutputStream().write(crlf.getBytes());
                fs.close();
            }

            //postConn.getOutputStream().write(hbs);
            //postConn.getOutputStream().flush();
            outStream.write(hbs);
            outStream.flush();
            outStream.close();
            
            int httpStatus = postConn.getResponseCode();
            if (httpStatus != HttpStatus.SC_OK)
            {
                postConn.disconnect();
                return "";
            }

            StringBuilder resp = new StringBuilder();
            InputStreamReader responseReader = new InputStreamReader(postConn.getInputStream(), "UTF-8");
            char[] responseBuff = new char[1024];
            while((oneRead = responseReader.read(responseBuff)) > 0){
            	resp.append(responseBuff, 0, oneRead);
            }
            
            httpResp = resp.toString();
            postConn.disconnect();
            return httpResp;
        }catch (Exception ex)
        {
        	ex.printStackTrace();
            postConn.disconnect();

            return httpResp;
        }
    
    }

    /// <summary>
    /// 得到实体的 mimetype 信息
    /// </summary>
    /// <param name="filePath">文件路径</param>
    /// <returns></returns>
    public static String getMimeType(String filePath)
    {
        String[] fileExtendsion = filePath.split("\\.");
        if (fileExtendsion.length <= 1)
        {
            return "application/octet-stream";
        }

        
        String fileExt = fileExtendsion[fileExtendsion.length - 1].toLowerCase();
        if (fileExt.equalsIgnoreCase("jpg"))
        {
            fileExt = "jpeg";
        }
        else if (fileExt.equalsIgnoreCase("mov"))
        {
            fileExt = "quicktime";
        }

        //如果是图片
        if (fileExt.equalsIgnoreCase("jpeg") ||
           fileExt.equalsIgnoreCase("png") ||
           fileExt.equalsIgnoreCase("gif") ||
           fileExt.equalsIgnoreCase("bmp")
        )
        {
            return "image/" + fileExt;
        }

        //如果是视频
        if (fileExt.equalsIgnoreCase("quicktime") ||
           fileExt.equalsIgnoreCase("mp4")
        )
        {
            return "video/" + fileExt;
        }

        return "application/octet-stream";
    }
    
   
    public static long getFileLength(String filePath) {
        if (!new File(filePath).exists()) 
        {
            return 0;
        }

        
        long fileLength = new File(filePath).length();
        return fileLength;
    }


    /// <summary>
    /// 文件流
    /// </summary>
    /// <returns>FileStream</returns>
    public static FileInputStream getFileStream(String filePath)
    {
        try {
			return new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
 
    
    public static String UrlEncode(String str)
    {
    	
        if (str == null ||
        	str.length() == 0
        ) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        byte[] byStr =null;
        try{
	        byStr = str.getBytes("UTF-8");
	      }catch(Exception ex){}
		
        for (int i = 0; i < byStr.length ; i++)
        {
        	long xx = byStr[i] & 0xff;
        	sb.append("%" + Integer.toHexString((int)xx));
        }

        return (sb.toString());
    }
}
