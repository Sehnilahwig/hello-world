package com.tvmining.wifiplus.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;

import com.tvmining.wifiplus.entity.ICETable;
import com.tvmining.wifiplus.entity.ItemTable;
import com.tvmining.wifiplus.entity.PakgeTable;

public class ImageUtil {

	public static Bitmap downloadToBitmap(String iconUrl) {
		Bitmap bitmap = null;
		InputStream is = null;
		HttpResponse httpResponse = null;
		HttpEntity httpEntity = null;
		HttpGet httpRequest = new HttpGet(iconUrl);
		long start = System.nanoTime();
		// 取得HttpClient 对象
		HttpClient httpclient = new DefaultHttpClient();
		try {
			// 请求httpClient ，取得HttpRestponse
			httpResponse = httpclient.execute(httpRequest);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得相关信息 取得HttpEntiy
				httpEntity = httpResponse.getEntity();
				// 获得一个输入流
				is = httpEntity.getContent();
				bitmap = BitmapFactory.decodeStream(is);
				Log.d("ImageUtil", "--->" + bitmap);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.d("kuLauncher", "download app icon error");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("kuLauncher", "download app icon error");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				httpclient.getConnectionManager().shutdown();
				httpResponse = null;
				httpEntity = null;
				httpclient = null;
			}
		}
		Log.d("kuLauncher-downloadtime", "--->:" + (System.nanoTime() - start));
		return bitmap;
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/*
	 * 旋转图片
	 * 
	 * @param angle
	 * 
	 * @param bitmap
	 * 
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public static void saveBitmapToDisk(File file,Bitmap bitmap,String dirPath){
		if(file != null){
			File dirFile = new File(dirPath);
			if(!dirFile.exists()){
				dirFile.mkdirs();
			}
			FileOutputStream out = null;
			if (file.exists()){
	        	file.delete ();
	        }
	        try {  
	        	out = new FileOutputStream(file); 
	        	if(bitmap != null && !bitmap.isRecycled()){
	        		bitmap.compress(Bitmap.CompressFormat.JPEG,
	   	        		 90, out); 
	        		out.flush(); 
	   	        	bitmap.recycle(); 
	        	}
	        } catch (Exception e) { 
	    		e.printStackTrace(); 
	    	}finally{
	    		if(out != null){
	    			try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
	    	}
		}
		
	}
	
	public static void saveBitmapToDisk(String fileName,Bitmap bitmap,String dirPath,boolean isRecycle){
		File file = new File(dirPath + File.separator + fileName);
		
		if(file != null){
			File dirFile = new File(dirPath);
			if(!dirFile.exists()){
				dirFile.mkdirs();
			}
			FileOutputStream out = null;
			if (file.exists()){
	        	file.delete ();
	        }
	        try {  
	        	out = new FileOutputStream(file); 
	        	if(bitmap != null && !bitmap.isRecycled()){
	        		bitmap.compress(Bitmap.CompressFormat.JPEG,
	   	        		 100, out); 
	        		out.flush(); 
	        		if(isRecycle){
	        			bitmap.recycle(); 
	        		}
	        	}
	        } catch (Exception e) { 
	    		e.printStackTrace(); 
	    	}finally{
	    		if(out != null){
	    			try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
	    	}
		}
		
	}
	
	public static Bitmap bitmapPlus(Bitmap newBitmap, Bitmap bitmap2) {

		newBitmap = newBitmap.copy(Bitmap.Config.ARGB_8888, true);

		Canvas canvas = new Canvas(newBitmap);
		Paint paint = new Paint();

		int w = newBitmap.getWidth();
		int h = newBitmap.getHeight();

		int w_2 = bitmap2.getWidth();
		int h_2 = bitmap2.getHeight();

		paint.setAlpha(0);
		canvas.drawRect(0, 0, newBitmap.getWidth(), newBitmap.getHeight(),
				paint);

		paint = new Paint();
		canvas.drawBitmap(bitmap2, Math.abs(w - w_2) / 2,
				Math.abs(h - h_2) / 2, paint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		// 存储新合成的图片
		canvas.restore();

		return newBitmap;
	}

	public static void startDownload(final String fileUrl, String diskPath,long nPos) {
		BufferedInputStream bis = null;
		RandomAccessFile fos = null;
		InputStream is = null;
		byte[] buf = new byte[1024];
		HttpClient httpClient = new DefaultHttpClient();
		try {
			File diskFile = new File(diskPath);
			if (!diskFile.exists()) {
				diskFile.mkdirs();
			}

			String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1,
					fileUrl.length());

			File file = new File(diskPath + File.separator + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new RandomAccessFile(file, "rw");// 随机存取文件
			// 设置开始写文件的位置
			fos.seek(0);
			is = getFileStream(String.valueOf(fileUrl),nPos,httpClient);
			bis = new BufferedInputStream(is);
			// 开始循环以流的形式读写文件
			int len = 0;
			while ((len = bis.read(buf, 0, 1024)) != -1) {
				fos.write(buf, 0, len);
			}

		} catch (Exception e) {
			Log.d("ImageUtil", "io exception when download file");
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
				if (is != null) {
					is.close();
				}
				if (bis != null) {
					bis.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static InputStream getFileStream(String url,long nPos,HttpClient httpClient) {
		InputStream inStream = null;
		HttpGet httpGet = new HttpGet(url);
		try {
			/*httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 200000);
            // 读取超时
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 200000);*/
			
			httpGet.getParams().setIntParameter(
                    CoreConnectionPNames.SO_TIMEOUT, 600000);
            httpGet.getParams().setIntParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 600000);
            httpGet.addHeader("Range", "bytes=" + nPos + "-");
            
			HttpResponse response = httpClient.execute(httpGet);
			
			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 206 || response.getStatusLine().getStatusCode() == 416) {
				HttpEntity entity = response.getEntity();
				inStream = entity.getContent();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return inStream;
	}
	
	public static Bitmap videoBitmapPlus(Bitmap newBitmap, Bitmap bitmap2) {

		newBitmap = newBitmap.copy(Bitmap.Config.ARGB_8888, true);

		Canvas canvas = new Canvas(newBitmap);
		Paint paint = new Paint();

		int w = newBitmap.getWidth();
		int h = newBitmap.getHeight();

		int w_2 = bitmap2.getWidth();
		int h_2 = bitmap2.getHeight();

		paint.setAlpha(0);
		canvas.drawRect(0, 0, newBitmap.getWidth(), newBitmap.getHeight(),
				paint);

		paint = new Paint();
		canvas.drawBitmap(bitmap2, w - w_2, 0, paint);

		canvas.rotate(45.0f, w - w_2 / 2, 10);
		paint.setColor(Color.RED);
		paint.setTextSize(15);
		canvas.drawText("video", w - w_2 / 2, 10, paint);

		canvas.save(Canvas.ALL_SAVE_FLAG);
		// 存储新合成的图片
		canvas.restore();

		return newBitmap;
	}

	
	public static void startDownloadPakgeIcon(final String fileUrl, String diskPath,String pakgeIconName) {
		BufferedInputStream bis = null;
		RandomAccessFile fos = null;
		InputStream is = null;
		byte[] buf = new byte[1024];
		try {
			File diskFile = new File(diskPath);
			if (!diskFile.exists()) {
				diskFile.mkdirs();
			}

			File file = new File(diskPath + File.separator + pakgeIconName);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new RandomAccessFile(file, "rw");// 随机存取文件
			// 设置开始写文件的位置
			fos.seek(0);
			is = getFileStream(String.valueOf(fileUrl));
			bis = new BufferedInputStream(is);
			// 开始循环以流的形式读写文件
			int len = 0;
			while ((len = bis.read(buf, 0, 1024)) != -1) {
				fos.write(buf, 0, len);
			}

		} catch (Exception e) {
			Log.d("ImageUtil", "io exception when download file");
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (bis != null) {
					bis.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void startDownload(final String fileUrl, String diskPath) {
		BufferedInputStream bis = null;
		RandomAccessFile fos = null;
		InputStream is = null;
		byte[] buf = new byte[1024];
		try {
			File diskFile = new File(diskPath);
			if (!diskFile.exists()) {
				diskFile.mkdirs();
			}

			String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1,
					fileUrl.length());

			File file = new File(diskPath + File.separator + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new RandomAccessFile(file, "rw");// 随机存取文件
			// 设置开始写文件的位置
			fos.seek(0);
			is = getFileStream(String.valueOf(fileUrl));
			bis = new BufferedInputStream(is);
			// 开始循环以流的形式读写文件
			int len = 0;
			while ((len = bis.read(buf, 0, 1024)) != -1) {
				fos.write(buf, 0, len);
			}

		} catch (Exception e) {
			Log.d("ImageUtil", "io exception when download file");
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (bis != null) {
					bis.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */ 
   public static void copyFile(String oldPath, String newPath) { 
       try { 
           int bytesum = 0; 
           int byteread = 0; 
           File oldfile = new File(oldPath); 
           
           if(!oldfile.exists()){
        	   oldfile.createNewFile();
           }
           
           if (oldfile.exists()) { 
               InputStream inStream = new FileInputStream(oldPath); //读入原文件 
               FileOutputStream fs = new FileOutputStream(newPath); 
               byte[] buffer = new byte[1024 * 4]; 
               int length; 
               while ( (byteread = inStream.read(buffer)) != -1) { 
                   bytesum += byteread; //字节数 文件大小 
                   System.out.println(bytesum); 
                   fs.write(buffer, 0, byteread); 
               } 
               inStream.close(); 
           } 
       } 
       catch (Exception e) { 
           System.out.println("复制单个文件操作出错"); 
           e.printStackTrace(); 

       } 

   } 

   /** 
     * 复制整个文件夹内容 
     * @param oldPath String 原文件路径 如：c:/fqf 
     * @param newPath String 复制后路径 如：f:/fqf/ff 
     * @return boolean 
     */ 
   public void copyFolder(String oldPath, String newPath) { 

       try { 
           (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹 
           File a=new File(oldPath); 
           String[] file=a.list(); 
           File temp=null; 
           for (int i = 0; i < file.length; i++) { 
               if(oldPath.endsWith(File.separator)){ 
                   temp=new File(oldPath+file[i]); 
               } 
               else{ 
                   temp=new File(oldPath+File.separator+file[i]); 
               } 

               if(temp.isFile()){ 
                   FileInputStream input = new FileInputStream(temp); 
                   FileOutputStream output = new FileOutputStream(newPath + "/" + 
                           (temp.getName()).toString()); 
                   byte[] b = new byte[1024 * 5]; 
                   int len; 
                   while ( (len = input.read(b)) != -1) { 
                       output.write(b, 0, len); 
                   } 
                   output.flush(); 
                   output.close(); 
                   input.close(); 
               } 
               if(temp.isDirectory()){//如果是子文件夹 
                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]); 
               } 
           } 
       } 
       catch (Exception e) { 
           System.out.println("复制整个文件夹内容操作出错"); 
           e.printStackTrace(); 

       } 

   }
	
    public static void nioTransferCopy(String sourceStr, String targetStr) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(new File(sourceStr));
            
            File targetFile = new File(targetStr);
            if(!targetFile.exists()){
            	targetFile.createNewFile();
            }
            outStream = new FileOutputStream(targetFile);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	try {
        		if(inStream != null){
            		inStream.close();
            	}
            	if(in != null){
            		in.close();
            	}
            	if(outStream != null){
            		outStream.close();
            	}
            	if(out != null){
            		out.close();
            	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
	public static void savePakgeImage(final String imageUrl, String pkgPath,
			Bitmap bm,String pakgeIconName) {
		FileOutputStream fOut = null;
		try {
			File file = new File(pkgPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			File imageFile = new File(pkgPath + File.separator + pakgeIconName);
			if (!imageFile.exists()) {
				imageFile.createNewFile();
			}

			fOut = new FileOutputStream(imageFile);

			Log.d("ImageAsyncLoader",
					"save pkg file start:" + imageFile.getAbsolutePath());

			bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();

			Log.d("ImageAsyncLoader",
					"save pkg file: end:" + imageFile.getAbsolutePath());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fOut != null) {
					fOut.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static InputStream getFileStream(String url) {
		InputStream inStream = null;
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = new DefaultHttpClient().execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				inStream = entity.getContent();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return inStream;
	}

	public static long getFileSize(String url) {
		HttpGet httpGet = new HttpGet(url);
		long size = 0;
		try {
			HttpResponse response = new DefaultHttpClient().execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				size = entity.getContentLength();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return size;
	}

	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	public static List getCacheBitmapNames(String folderPath) {
		List imgNameList = null;
		File file01 = new File(folderPath);

		String[] files01 = file01.list();

		for (int i = 0; i < files01.length; i++) {
			String filePath = folderPath + File.separator + files01[i];
			File file02 = new File(filePath);

			if (!file02.isDirectory()) {
				if (isImageFile(file02.getName())) {
					if (imgNameList == null) {
						imgNameList = new ArrayList();
					}
					imgNameList.add(filePath);
				}
			}
		}
		return imgNameList;

	}

	public static boolean isImageFile(String fileName) {
		String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		if (fileEnd.equalsIgnoreCase("jpg")) {
			return true;
		} else if (fileEnd.equalsIgnoreCase("png")) {
			return true;
		} else if (fileEnd.equalsIgnoreCase("bmp")) {
			return true;
		} else {
			return false;
		}
	}

	public static Bitmap loadZoomFromCache(String filePath, int width) {
		Bitmap scaleBitmap = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(String.valueOf(filePath)));
			if (fis != null) {
				FileDescriptor fd = fis.getFD();
				scaleBitmap = decodeSampledZoomBitmapFromDescriptor(fd, width);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return scaleBitmap;
	}
	
	public static int[] loadBitmapWidthAndHeight(String filePath) {
		int[] wh = new int[2];
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);

			wh[0] = options.outWidth;
			wh[1] = options.outHeight;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return wh;
	}

	public static Bitmap getImageThumbnail(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	public static Bitmap createVideoThumbnail(String filePath, int width) {

		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath,
				Thumbnails.MINI_KIND);
		if (bitmap != null) {
			int height = width * bitmap.getHeight() / bitmap.getWidth();
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}

	static public Bitmap getVideoThumbnail(ContentResolver cr, String path) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		// select condition.
		String whereClause = MediaStore.Video.Media.DATA + " = '" + path + "'";
		// colection of results.
		Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Video.Media._ID }, whereClause, null,
				null);
		if (cursor == null || cursor.getCount() == 0) {
			return ThumbnailUtils.createVideoThumbnail(path,
					MediaStore.Video.Thumbnails.MINI_KIND);
		}
		cursor.moveToFirst();
		// image id in image table.
		String videoId = cursor.getString(cursor
				.getColumnIndex(MediaStore.Video.Media._ID));
		if (videoId == null) {
			return ThumbnailUtils.createVideoThumbnail(path,
					MediaStore.Video.Thumbnails.MINI_KIND);
		}
		cursor.close();
		long videoIdLong = Long.parseLong(videoId);
		// via imageid get the bimap type thumbnail in thumbnail table.
		bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, videoIdLong,
				Images.Thumbnails.MICRO_KIND, options);
		return bitmap;
	}

	public static Bitmap decodeSampledZoomBitmapFromDescriptor(
			FileDescriptor fileDescriptor, int reqWidth) {
		Bitmap bitmap = null;
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

		int h = options.outHeight;
		int w = options.outWidth;
		int be = w / reqWidth;

		if (be <= 0) {
			options.inSampleSize = 1;
		} else {
			options.inSampleSize = be;
		}

		options.inJustDecodeBounds = false;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null,
				options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		/*
		 * if(bitmap.getWidth() < reqWidth){ bitmap =
		 * ThumbnailUtils.extractThumbnail(bitmap, reqWidth, (reqWidth * h )/ w
		 * , ThumbnailUtils.OPTIONS_RECYCLE_INPUT); }
		 */

		return bitmap;
	}

	public static Bitmap downloadImage(String urlStr) {
		Bitmap bm = null;
		byte[] imageBuffer = (byte[]) null;
		BufferedInputStream is = null;
		ByteArrayOutputStream baos = null;
		URL url = null;
		try {
			url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			is = new BufferedInputStream(conn.getInputStream());
			baos = new ByteArrayOutputStream();
			int b = -1;

			byte[] temp = new byte[1024];
			while ((b = is.read(temp)) != -1) {
				baos.write(temp, 0, b);
			}

			/*
			 * while ((b = is.read()) != -1) { baos.write(b); }
			 */
			imageBuffer = baos.toByteArray();
			bm = decodeSampledZoomBitmapFromDescriptor(imageBuffer);
		} catch (IOException e) {
			return null;
		} finally {
			try {
				is.close();
				baos.close();
				imageBuffer = null;
			} catch (Exception e) {
				return null;
			}
		}
		return bm;
	}
	
	
	public static Bitmap downloadImageNoZoom(String urlStr) {
		Bitmap bm = null;
		InputStream is = null;
		URL url = null;
		URLConnection conn = null;
		try {
			url = new URL(urlStr);
			conn = url.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			is = conn.getInputStream();
			bm = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if(is != null){
					is.close();
				}
				conn = null;
				
			} catch (Exception e) {
				return null;
			}
		}
		return bm;
	}

	
	public static Bitmap decodeSampledZoomBitmapFromDescriptor(
			byte[] imageBuffer, int reqWidth) {
		Bitmap bitmap = null;
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length,
				options);

		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / reqWidth;
		int be = 1;
		be = beWidth;

		if (be <= 0) {
			be = 1;
			options.inSampleSize = be;
		} else {
			options.inSampleSize = be + 1;
		}
		options.inSampleSize = 4;
		options.inJustDecodeBounds = false;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeByteArray(imageBuffer, 0,
				imageBuffer.length, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		if (bitmap.getWidth() < reqWidth) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, reqWidth,
					(reqWidth * h) / w, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}

		return bitmap;
	}

	public static Bitmap decodeSampledZoomBitmapFromDescriptor(
			byte[] imageBuffer) {
		Bitmap bitmap = null;
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length,
				options);

		options.inSampleSize = 1;
		if (options.outHeight > Constant.MAX_HEIGHT
				|| options.outWidth > Constant.MAX_WIGTH) {
			options.inSampleSize = 2;
		}

		options.inJustDecodeBounds = false;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeByteArray(imageBuffer, 0,
				imageBuffer.length, options);

		return bitmap;
	}

	public static Bitmap loadZoomFromCache(String filePath, boolean small) {
		Bitmap scaleBitmap = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(String.valueOf(filePath)));
			if (fis != null) {
				FileDescriptor fd = fis.getFD();
				if (small) {
					scaleBitmap = decodeSampledZoomBitmapFromDescriptor(fd,
							Integer.MAX_VALUE, Integer.MAX_VALUE, 8);
				} else {
					scaleBitmap = decodeSampledZoomBitmapFromDescriptor(fd,
							Integer.MAX_VALUE, Integer.MAX_VALUE, 2);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return scaleBitmap;
	}

	public static Bitmap decodeSampledZoomBitmapFromDescriptor(
			FileDescriptor fileDescriptor, int reqWidth, int reqHeight,
			int zoomNum) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

		// Calculate inSampleSize
		options.inSampleSize = 1;// calculateInSampleSize(options, reqWidth,
									// reqHeight);

		if (options.outHeight > Constant.MAX_HEIGHT
				|| options.outWidth > Constant.MAX_WIGTH) {
			options.inSampleSize = 2;
		}
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		Bitmap bit = BitmapFactory.decodeFileDescriptor(fileDescriptor, null,
				options);
		return bit;
	}

	// 传入filePath，获取cache的icon图片
	public static Bitmap loadFromCache(String filePath) {
		byte[] bs = getCacheData(filePath);
		Bitmap bmp = null;
		try {
			bmp = getBitmapFromData(bs);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bmp;
	}

	// 根据cache方法取出的 byte来得到bitmap
	private static Bitmap getBitmapFromData(byte[] bs) {
		Bitmap bitmap = null;
		if (null != bs) {
			BitmapFactory.Options bfOptions = new BitmapFactory.Options();
			bfOptions.inDither = false;
			bfOptions.inPurgeable = true;
			bfOptions.inTempStorage = new byte[48 * 1024];
			try {
				bitmap = BitmapFactory.decodeByteArray(bs, 0, bs.length,
						bfOptions);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} finally {
				bs = null;
			}
		}
		return bitmap;
	}

	// 传入url获取icon
	private static byte[] getCacheData(String filePath) {

		byte[] bmp = null;
		try {
			File file = new File(filePath);
			if (file.exists()) {
				bmp = readFileData(file);
			}
		} catch (Exception e) {
			Log.d("kuLauncher-readFile", "getCacheData error");
		}
		return bmp;
	}

	public static String getMD5(String content) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(content.getBytes());
			return getHashString(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] readFileData(File file) {
		ByteArrayOutputStream out = null;
		FileInputStream fis = null;
		byte[] content = null;
		try {
			fis = new FileInputStream(file);
			out = new ByteArrayOutputStream(1024);
			byte[] temp = new byte[1024];
			int size = 0;
			while ((size = fis.read(temp)) != -1) {
				out.write(temp, 0, size);
			}
			content = out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			fis = null;
			out = null;

		}
		return content;
	}

	private static String getHashString(MessageDigest digest) {
		StringBuilder builder = new StringBuilder();
		for (byte b : digest.digest()) {
			builder.append(Integer.toHexString((b >> 4) & 0xf));
			builder.append(Integer.toHexString(b & 0xf));
		}
		return builder.toString();
	}

	/*
	 * public static void saveImageToGallery(Bitmap finalBitmap) { File
	 * myDir=new File("/sdcard/DCIM/Camera"); myDir.mkdirs(); Random generator =
	 * new Random(); int n = 10000; n = generator.nextInt(n); String fname =
	 * "Image-"+ n +".jpg"; File file = new File (myDir, fname); if (file.exists
	 * ()) file.delete (); try { FileOutputStream out = new
	 * FileOutputStream(file); finalBitmap.compress(Bitmap.CompressFormat.JPEG,
	 * 90, out); out.flush(); out.close(); finalBitmap.recycle(); } catch
	 * (Exception e) { e.printStackTrace(); } }
	 */

	public static String saveImageToGallery(Bitmap bitmap) {
		String picPath = MediaStore.Images.Media.insertImage(
				Constant.contentResolver, bitmap, null, null);
		bitmap.recycle();
		return picPath;
	}

	
	public static void copyToDisk(Context mContext,String guide) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String dateStr = sdf.format(date);
		dateStr = dateStr.substring(0,dateStr.indexOf(" "));
		AssetManager am = mContext.getResources().getAssets();
		
		String iceName = "ice";
		String pkgName = "pkg";
		
		String rootPath = Constant.savePath
				+ dateStr;
		
		String icePath = rootPath + File.separator + iceName;
		String pkgPath = icePath + File.separator + pkgName;
		String itemPath = pkgPath;
		
		File pkgFile = new File(pkgPath);
		// 如果目录不中存在，创建这个目录
		if (!pkgFile.exists()){
			pkgFile.mkdirs();
		}
		
		writeToDisk(mContext,"ice",icePath,am);
		writeToDisk(mContext,"pkg",pkgPath,am);
		writeToDisk(mContext,"item",itemPath,am);
		
		// ice data
		ICETable iceTable = new ICETable();
		iceTable.setValues(sdf.format(date), icePath , guide,"init");
		long iceIndex = Constant.dbConnection.insertICETable(iceTable);
		iceTable.setIceIndex((int)iceIndex);
		
		// pkg data
		String[] pksNames = getAllImageNameFromAssetsFile(mContext,"pkg");
		String[] itemsNames = getAllImageNameFromAssetsFile(mContext,"item");
		PakgeTable pakgeTable = new PakgeTable();
		pakgeTable.setInitValues(
				sdf.format(date),
				itemsNames.length,iceIndex,"init",pkgPath + File.separator + pksNames[0],pkgName,pkgPath);
		long pkgIndex = Constant.dbConnection.insertPakgeTable(pakgeTable);
		
		for(int i=0;i<itemsNames.length;i++){
			ItemTable itemTable = new ItemTable();
			int[] wh = loadBitmapWidthAndHeight(itemPath+File.separator+itemsNames[i]);
			itemTable.setInitValues(pkgIndex, wh[1], wh[0],"init",itemsNames[i],itemPath+File.separator+itemsNames[i],itemPath+File.separator+itemsNames[i]);
			Constant.dbConnection.insertItemTable(itemTable);
		}
	}
	
	
	private static void writeToDisk(Context mContext,String assetsName,String filePath,AssetManager am){
		try {
			String[] stationPicNames = getAllImageNameFromAssetsFile(mContext,assetsName);
			for(int i=0;i<stationPicNames.length;i++){
				if (!(new File(filePath + File.separator + stationPicNames[i])).exists()) {
					InputStream is = am.open(assetsName + File.separator + stationPicNames[i]);
					FileOutputStream fos = new FileOutputStream(filePath+ File.separator + stationPicNames[i]);
					byte[] buffer = new byte[7168];
					int count = 0;
					while ((count = is.read(buffer)) > 0) {
						fos.write(buffer, 0, count);
					}
					fos.close();
					is.close();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String[] getAllImageNameFromAssetsFile(Context mContext,String dirName){
		String[] stationPicNames = null;
		try {
			stationPicNames = mContext.getResources().getAssets().list(dirName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stationPicNames;
	}
	
	
	public static boolean getRemoteFile(String strUrl, String fileName) throws IOException { 
	    URL url = new URL(strUrl); 
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
	    DataInputStream input = new DataInputStream(conn.getInputStream()); 
	    DataOutputStream output = new DataOutputStream(new FileOutputStream(fileName)); 
	    byte[] buffer = new byte[1024 * 8]; 
	    int count = 0; 
	    while ((count = input.read(buffer)) > 0) { 
	      output.write(buffer, 0, count); 
	    } 
	    output.close(); 
	    input.close(); 
	    return true; 
	  }
	
	/**
     * 读取文件
     * @param sourcePath 文件所在的网络路径
     */
    public static String readNetFile(String sourcePath){
        StringBuffer sb = new StringBuffer();
    	String line;
        int lineNum=0;
        BufferedReader reader=null;
        try{
            URL url = new URL(sourcePath);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(3000);
            InputStream stream = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            while ((line = reader.readLine()) != null){
                lineNum++;
                sb.append(line);
            }
        }
        catch (Exception ie){
            ie.printStackTrace();
        }finally{
            try{
                if(reader != null)
                    reader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        
        return sb.toString();
    }
}
