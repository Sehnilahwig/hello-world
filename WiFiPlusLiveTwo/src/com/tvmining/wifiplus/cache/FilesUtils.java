/*     */ package com.tvmining.wifiplus.cache;
/*     */ 
/*     */ import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.tvmining.wifiplus.util.Constant;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FilesUtils
/*     */ {
/*     */   private Context mContext;
/*     */ 
/*     */   FilesUtils(Context context)
/*     */   {
/*  21 */     this.mContext = context;
/*     */   }
/*     */ 
/*     */   CacheInfo downloadImage(URL url)
/*     */   {
/*  33 */     CacheInfo cacheInfo = null;
/*  34 */     byte[] imageBuffer = (byte[])null;
/*  35 */     BufferedInputStream is = null;
/*  36 */     ByteArrayOutputStream baos = null;
/*     */     try {
/*  38 */       URLConnection conn = url.openConnection();
/*  39 */       is = new BufferedInputStream(conn.getInputStream());
/*  40 */       baos = new ByteArrayOutputStream();
/*  41 */       long size = 0L;
/*  42 */       int b = -1;
/*  43 */       while ((b = is.read()) != -1) {
/*  44 */         baos.write(b);
/*     */       }
/*  46 */       imageBuffer = baos.toByteArray();
/*  47 */       size = imageBuffer.length;
/*  48 */       Bitmap bm = decodeSampledZoomBitmapFromDescriptor(imageBuffer,Constant.screenWidth);
				if(bm != null){
					cacheInfo = new CacheInfo(url, size, bm);
				}
/*     */     } catch (IOException e) {
/*  52 */       Log.i(this.mContext.getPackageName(), url.toString() + 
/*  53 */         " is unavailable");
/*  54 */       return null;
/*     */     } finally {
/*     */       try {
				  is.close();
/*  57 */         baos.close();
/*     */       } catch (Exception e) {
/*  59 */         return null;
/*     */       }
/*     */     }
/*  62 */     return cacheInfo;
/*     */   }
/*     */ 
		public static Bitmap decodeSampledZoomBitmapFromDescriptor(
				byte[] imageBuffer,int reqWidth) {
			Bitmap bitmap = null;
		    // First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length, options);
		
		    
		    int h = options.outHeight;
			int w = options.outWidth;
			int beWidth = w / reqWidth;
			int be = 1;
			be = beWidth;
			
			if (be <= 0) {
				be = 1;
				options.inSampleSize = be;
			}else{
				options.inSampleSize = be + 1;
			}
			
			options.inJustDecodeBounds = false;
			// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
			bitmap = BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length, options);
			// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
			if(bitmap.getWidth() < reqWidth){
				bitmap = ThumbnailUtils.extractThumbnail(bitmap, reqWidth, (reqWidth * h )/ w ,
						ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
			}
		
		    return bitmap;
		}

/*     */   boolean saveImage(Bitmap bitmap, String fileName)
/*     */   {
/*  73 */     boolean bool = false;
/*  74 */     BufferedOutputStream bos = null;
/*  75 */     BufferedInputStream bis = null;
/*  76 */     ByteArrayOutputStream baos = null;
/*     */     try {
/*  78 */       bos = new BufferedOutputStream(this.mContext.openFileOutput(fileName, 
/*  79 */         0));
/*  80 */       baos = new ByteArrayOutputStream();
/*  81 */       bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
/*  82 */       bis = new BufferedInputStream(new ByteArrayInputStream(
/*  83 */         baos.toByteArray()));
/*  84 */       int b = -1;
/*  85 */       while ((b = bis.read()) != -1) {
/*  86 */         bos.write(b);
/*     */       }
/*  88 */       bool = true;
/*     */     } catch (Exception e) {
/*  90 */       bool = false;
/*  91 */       Log.i(this.mContext.getPackageName(), 
/*  92 */         "the local storage is not available");
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/*  95 */         bos.close();
/*  96 */         bis.close();
/*     */       } catch (IOException e) {
/*  98 */         bool = false;
/*  99 */         Log.i(this.mContext.getPackageName(), 
/* 100 */           "the local storage is not available");
/*     */       }
/*     */     }
/* 103 */     return bool;
/*     */   }
/*     */  
/*     */   Bitmap readImage(String fileName)
/*     */   {
/* 115 */     Bitmap bm = null;
/* 116 */     InputStream is = null;
/*     */     try {
/* 118 */       is = new BufferedInputStream(this.mContext.openFileInput(fileName));
/* 119 */       bm = BitmapFactory.decodeStream(is);
/*     */     } catch (FileNotFoundException e) {
/* 121 */       Log.i(this.mContext.getPackageName(), 
/* 122 */         "image resource is not found int the cache directory");
/*     */     }
/* 124 */     return bm;
/*     */   }
/*     */ 
/*     */   boolean deleteImage(String fileName)
/*     */   {
/* 134 */     return this.mContext.deleteFile(fileName);
/*     */   }
/*     */ }

/* Location:           /home/like/桌面/imagecache.jar
 * Qualified Name:     com.superbearman6.imagecachetatics.FilesUtils
 * JD-Core Version:    0.6.2
 */