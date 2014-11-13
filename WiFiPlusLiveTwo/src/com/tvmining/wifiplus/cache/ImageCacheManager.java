/*     */ package com.tvmining.wifiplus.cache;
/*     */ 
/*     */ import java.net.URL;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ImageCacheManager
/*     */ {
/*     */   private DBClient dbClient;
/* 301 */   private static ImageCacheManager instance = null;
/*     */   private Context mContext;
/*     */   private FilesUtils filesUtils;
/* 307 */   private int mode = 1;
/*     */ 
/* 309 */   private int max_Count = 20;
/*     */ 
/* 311 */   private long delay_Millisecond = 259200000L;
/*     */ 
/* 313 */   private long max_Memory = 3145728L;
/*     */   public static final int MODE_LEAST_RECENTLY_USED = 0;
/*     */   public static final int MODE_FIXED_TIMED_USED = 1;
/*     */   public static final int MODE_FIXED_MEMORY_USED = 2;
/*     */   public static final int MODE_NO_CACHE_USED = 3;
/*     */ 
/*     */   private ImageCacheManager(Context context, int mode, String tag)
/*     */   {
/*  22 */     this.mode = mode;
/*  23 */     this.mContext = context;
/*  24 */     this.filesUtils = new FilesUtils(context);
/*  25 */     if (mode == 0)
/*  26 */       this.dbClient = new DBClient(this.mContext, "least_recently_used", tag);
/*  27 */     else if (mode == 1)
/*  28 */       this.dbClient = new DBClient(this.mContext, "fixed_timed_used", tag);
/*  29 */     else if (mode == 2)
/*  30 */       this.dbClient = new DBClient(this.mContext, "fixed_memory_used", tag);
/*     */   }
/*     */ 
/*     */   public static synchronized ImageCacheManager getImageCacheService(Context context, int mode, String tag)
/*     */   {
/*  45 */     if (instance == null) {
/*  46 */       instance = new ImageCacheManager(context, mode, tag);
/*     */     }
/*  48 */     return instance;
/*     */   }
/*     */ 
/*     */   public Bitmap downlaodImage(URL url) {
/*  52 */     Bitmap bitmap = null;
/*  53 */     CacheInfo cacheInfo = null;
/*  54 */     switch (this.mode) {
/*     */     case 0:
/*  56 */       cacheInfo = new LRU(url).execute();
/*  57 */       if (cacheInfo != null) {
/*  58 */         bitmap = cacheInfo.getValue();
/*     */       }
/*  60 */       break;
/*     */     case 1:
/*  62 */       cacheInfo = new FTU(url).execute();
/*  63 */       if (cacheInfo != null) {
/*  64 */         bitmap = cacheInfo.getValue();
/*     */       }
/*  66 */       break;
/*     */     case 2:
/*  68 */       cacheInfo = new FMU(url).execute();
/*  69 */       if (cacheInfo != null) {
/*  70 */         bitmap = cacheInfo.getValue();
/*     */       }
/*  72 */       break;
/*     */     case 3:
/*  74 */       cacheInfo = this.filesUtils.downloadImage(url);
/*  75 */       if (cacheInfo != null) {
/*  76 */         bitmap = cacheInfo.getValue();
/*     */       }
/*     */       break;
/*     */     }
/*  80 */     return bitmap;
/*     */   }
/*     */ 
/*     */   public void setMax_num(int max_num)
/*     */   {
/* 316 */     this.max_Count = max_num;
/*     */   }
/*     */ 
/*     */   public void setDelay_millisecond(long delay_millisecond) {
/* 320 */     this.delay_Millisecond = delay_millisecond;
/*     */   }
/*     */ 
/*     */   public void setMax_Memory(long max_Memory) {
/* 324 */     this.max_Memory = max_Memory;
/*     */   }
/*     */ 
/*     */   private class FMU
/*     */     implements IDownload
/*     */   {
/* 220 */     URL url = null;
/*     */ 
/*     */     FMU(URL url) {
/* 223 */       this.url = url;
/*     */     }
/*     */ 
/*     */     public CacheInfo execute()
/*     */     {
/* 228 */       SQLiteDatabase db = ImageCacheManager.this.dbClient.getSQLiteDatabase();
/* 229 */       db.beginTransaction();
/* 230 */       CacheInfo cacheInfo = null;
/*     */       try {
/* 232 */         cacheInfo = ImageCacheManager.this.dbClient.select(this.url.toString(), db);
/* 233 */         List cacheInfos = ImageCacheManager.this.dbClient.selectAll(db);
/* 234 */         if (cacheInfo == null) {
/* 235 */           cacheInfo = ImageCacheManager.this.filesUtils.downloadImage(this.url);
/* 236 */           if (cacheInfo == null) {
/* 237 */             return null;
/*     */           }
/* 239 */           if (cacheInfo.getFileSize() > ImageCacheManager.this.max_Memory) {
/* 240 */             Log.i(ImageCacheManager.this.mContext.getPackageName(), 
/* 241 */               "the image resource" + 
/* 242 */               cacheInfo.getUrl().toString() + 
/* 243 */               " need more  storage than " + 
/* 244 */               ImageCacheManager.this.max_Memory + "B");
/*     */           } else {
/* 246 */             if ((cacheInfos != null) && (cacheInfos.size() > 0)) {
/* 247 */               long sumSize = 0L;
/* 248 */               while (cacheInfos.size() > 0) {
/* 249 */                 int i = 0; for (int size = cacheInfos.size(); i < size; i++) {
/* 250 */                   CacheInfo tempCache = (CacheInfo)cacheInfos.get(i);
/* 251 */                   sumSize += tempCache.getFileSize();
/*     */                 }
/* 253 */                 if (sumSize + cacheInfo.getFileSize() <= ImageCacheManager.this.max_Memory) {
/*     */                   break;
/*     */                 }
/* 256 */                 CacheInfo deleteCacheInfo = maxSize(cacheInfos);
/* 257 */                 if (ImageCacheManager.this.dbClient.delete(deleteCacheInfo.getUrl()
/* 258 */                   .toString(), db)) {
/* 259 */                   ImageCacheManager.this.filesUtils.deleteImage(deleteCacheInfo
/* 260 */                     .getFileName());
/* 261 */                   cacheInfos.remove(deleteCacheInfo);
/*     */                 }
/*     */               }
/*     */             }
/*     */ 
/* 266 */             if (ImageCacheManager.this.dbClient.insert(cacheInfo, db))
/* 267 */               ImageCacheManager.this.filesUtils.saveImage(cacheInfo.getValue(), 
/* 268 */                 cacheInfo.getFileName());
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 273 */           Bitmap bitmap = ImageCacheManager.this.filesUtils.readImage(cacheInfo
/* 274 */             .getFileName());
/* 275 */           if (bitmap != null)
/* 276 */             cacheInfo.setValue(bitmap);
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 281 */         db.endTransaction(); }
/*     */ 
/* 283 */       return cacheInfo;
/*     */     }
/*     */ 
/*     */     private CacheInfo maxSize(List<CacheInfo> cacheInfos) {
/* 287 */       long max = ((CacheInfo)cacheInfos.get(0)).getFileSize();
/* 288 */       CacheInfo deleteCache = (CacheInfo)cacheInfos.get(0);
/* 289 */       int i = 0; for (int size = cacheInfos.size(); i < size; i++) {
/* 290 */         CacheInfo tempCache = (CacheInfo)cacheInfos.get(i);
/* 291 */         if (tempCache.getFileSize() > max) {
/* 292 */           deleteCache = tempCache;
/*     */         }
/*     */       }
/* 295 */       return deleteCache;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class FTU
/*     */     implements IDownload
/*     */   {
/* 164 */     URL url = null;
/*     */ 
/*     */     FTU(URL url) {
/* 167 */       this.url = url;
/*     */     }
/*     */ 
/*     */     public CacheInfo execute()
/*     */     {
/* 172 */       CacheInfo cacheInfo = null;
/* 173 */       SQLiteDatabase db = ImageCacheManager.this.dbClient.getSQLiteDatabase();
/* 174 */       db.beginTransaction();
/*     */       try {
/* 176 */         cacheInfo = ImageCacheManager.this.dbClient.select(this.url.toString(), db);
/* 177 */         if (cacheInfo == null) {
/* 178 */           cacheInfo = ImageCacheManager.this.filesUtils.downloadImage(this.url);
/* 179 */           if (cacheInfo == null)
/* 180 */             return null;
/* 181 */           if (ImageCacheManager.this.dbClient.insert(cacheInfo, db))
/* 182 */             ImageCacheManager.this.filesUtils.saveImage(cacheInfo.getValue(), 
/* 183 */               cacheInfo.getFileName());
/*     */         }
/*     */         else {
/* 186 */           ImageCacheManager.this.dbClient.update(System.currentTimeMillis(), this.url.toString(), 
/* 187 */             db);
/* 188 */           Bitmap bitmap = ImageCacheManager.this.filesUtils.readImage(cacheInfo
/* 189 */             .getFileName());
/* 190 */           cacheInfo.setValue(bitmap);
/*     */         }
/* 192 */         List cacheInfos = ImageCacheManager.this.dbClient.selectAll(db);
/* 193 */         if (cacheInfos != null) {
/* 194 */           int i = 0; for (int size = cacheInfos.size(); i < size; i++) {
/* 195 */             CacheInfo tempCache = (CacheInfo)cacheInfos.get(i);
/*     */ 
/* 197 */             if ((tempCache.getCreatAt() + ImageCacheManager.this.delay_Millisecond < 
/* 197 */               System.currentTimeMillis()) && 
/* 198 */               (ImageCacheManager.this.dbClient.delete(tempCache.getUrl().toString(), 
/* 199 */               db))) {
/* 200 */               ImageCacheManager.this.filesUtils.deleteImage(tempCache.getFileName());
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 207 */         db.endTransaction(); } db.endTransaction();
/*     */ 
/* 209 */       return cacheInfo;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class LRU
/*     */     implements IDownload
/*     */   {
/*  90 */     URL url = null;
/*     */ 
/*     */     LRU(URL url) {
/*  93 */       this.url = url;
/*     */     }
/*     */ 
/*     */     public CacheInfo execute()
/*     */     {
/*  98 */       SQLiteDatabase db = ImageCacheManager.this.dbClient.getSQLiteDatabase();
/*  99 */       db.beginTransaction();
/* 100 */       CacheInfo cacheInfo = null;
/*     */       try {
/* 102 */         cacheInfo = ImageCacheManager.this.dbClient.select(this.url.toString(), db);
/* 103 */         List cacheInfos = ImageCacheManager.this.dbClient.selectAll(db);
/* 104 */         if (cacheInfo != null) {
/* 105 */           Bitmap bitmap = ImageCacheManager.this.filesUtils.readImage(cacheInfo
/* 106 */             .getFileName());
/* 107 */           cacheInfo.setValue(bitmap);
/* 108 */           int i = 0; for (int size = cacheInfos.size(); i < size; i++) {
/* 109 */             CacheInfo temp = (CacheInfo)cacheInfos.get(i);
/* 110 */             if (this.url.toString().equals(temp.getUrl().toString()))
/* 111 */               ImageCacheManager.this.dbClient.update(0, this.url.toString(), db);
/*     */             else
/* 113 */               ImageCacheManager.this.dbClient.update(temp.getUsetimes() + 1, temp
/* 114 */                 .getUrl().toString(), db);
/*     */           }
/*     */         }
/*     */         else {
/* 118 */           cacheInfo = ImageCacheManager.this.filesUtils.downloadImage(this.url);
/* 119 */           if (cacheInfo == null)
/* 120 */             return null;
/* 121 */           if ((cacheInfos != null) && 
/* 122 */             (cacheInfos.size() >= ImageCacheManager.this.max_Count)) {
/* 123 */             int usetimes = 0;
/* 124 */             CacheInfo deletedCache = (CacheInfo)cacheInfos.get(new Random()
/* 125 */               .nextInt(cacheInfos.size()));
/* 126 */             int i = 0; for (int size = cacheInfos.size(); i < size; i++) {
/* 127 */               CacheInfo tempCache = (CacheInfo)cacheInfos.get(i);
/* 128 */               if (tempCache.getUsetimes() > usetimes) {
/* 129 */                 usetimes = tempCache.getUsetimes();
/* 130 */                 deletedCache = tempCache;
/*     */               }
/*     */             }
/* 133 */             if (ImageCacheManager.this.dbClient.delete(deletedCache.getUrl().toString(), 
/* 134 */               db)) {
/* 135 */               ImageCacheManager.this.filesUtils.deleteImage(deletedCache.getFileName());
/* 136 */               if (ImageCacheManager.this.dbClient.insert(cacheInfo, db)) {
/* 137 */                 ImageCacheManager.this.filesUtils.saveImage(cacheInfo.getValue(), 
/* 138 */                   cacheInfo.getFileName());
/*     */               }
/*     */             }
/*     */ 
/*     */           }
/* 143 */           else if (ImageCacheManager.this.dbClient.insert(cacheInfo, db)) {
/* 144 */             ImageCacheManager.this.filesUtils.saveImage(cacheInfo.getValue(), 
/* 145 */               cacheInfo.getFileName());
/*     */           }
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 151 */         db.endTransaction(); }
/*     */ 
/* 153 */       return cacheInfo;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/like/桌面/imagecache.jar
 * Qualified Name:     com.superbearman6.imagecachetatics.ImageCacheManager
 * JD-Core Version:    0.6.2
 */