/*     */ package com.tvmining.wifiplus.cache;
/*     */ 
/*     */ import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DBClient extends SQLiteOpenHelper
/*     */ {
/*     */   private String modeName;
/*     */ 
/*     */   public DBClient(Context context, String modeName, String tag)
/*     */   {
/*  20 */     super(context, context.getPackageName() + ".cache", null, 1);
/*  21 */     this.modeName = (modeName + tag);
/*     */   }
/*     */ 
/*     */   public void onCreate(SQLiteDatabase db)
/*     */   {
	Log.d("DBClinet", "--------->"+modeName); 
/*  26 */     db.execSQL("create table " + 
/*  27 */       this.modeName + 
/*  28 */       "  (_id integer primary key autoincrement,cache_url varchar(50), create_time integer, usetimes integer,cache_filename varchar(50),cache_size integer)");
/*     */   }
/*     */ 
/*     */   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
/*     */   {
/*     */   }
/*     */ 
/*     */   synchronized boolean insert(CacheInfo cacheInfo, SQLiteDatabase db)
/*     */   {
/*     */     try
/*     */     {
/*  43 */       db.execSQL(
/*  44 */         "insert into " + 
/*  45 */         this.modeName + 
/*  46 */         "(cache_url,create_time,usetimes,cache_filename,cache_size) values (?,?,?,?,?)", 
/*  47 */         new Object[] { cacheInfo.getUrl().toString(), 
/*  48 */         Long.valueOf(cacheInfo.getCreatAt()), Integer.valueOf(cacheInfo.getUsetimes()), 
/*  49 */         cacheInfo.getFileName(), Long.valueOf(cacheInfo.getFileSize()) });
/*     */     } catch (SQLException e) {
/*  51 */       return false;
/*     */     }
/*  53 */     return true;
/*     */   }
/*     */ 
/*     */   synchronized boolean update(int usetimes, String url, SQLiteDatabase db) {
/*     */     try {
/*  58 */       db.execSQL("update " + this.modeName + 
/*  59 */         " set usetimes=? where cache_url='" + url + "'", 
/*  60 */         new Object[] { Integer.valueOf(usetimes) });
/*     */     } catch (SQLException e) {
/*  62 */       return false;
/*     */     }
/*  64 */     return true;
/*     */   }
/*     */ 
/*     */   synchronized boolean update(long createTime, String url, SQLiteDatabase db) {
/*     */     try {
/*  69 */       db.execSQL("update " + this.modeName + 
/*  70 */         " set create_time=? where cache_url='" + url + "'", 
/*  71 */         new Object[] { Long.valueOf(createTime) });
/*     */     } catch (SQLException e) {
/*  73 */       return false;
/*     */     }
/*  75 */     return true;
/*     */   }
/*     */ 
/*     */   synchronized boolean updateOther(int usetimes, String url, SQLiteDatabase db) {
/*     */     try {
/*  80 */       db.execSQL("update " + this.modeName + 
/*  81 */         " set usetimes=? where cache_url not in('" + url + "')", 
/*  82 */         new Object[] { Integer.valueOf(usetimes) });
/*     */     } catch (SQLException e) {
/*  84 */       return false;
/*     */     }
/*  86 */     return true;
/*     */   }
/*     */ 
/*     */   synchronized CacheInfo select(String url, SQLiteDatabase db)
/*     */   {
/*  98 */     String sql = "select cache_url,create_time,usetimes,cache_filename,cache_size from " + 
/*  99 */       this.modeName + " where cache_url='" + url + "'";
/* 100 */     Cursor cursor = db.rawQuery(sql, null);
/* 101 */     if ((cursor != null) && (cursor.getCount() > 0)) {
/* 102 */       cursor.moveToFirst();
/* 103 */       CacheInfo cacheInfo = new CacheInfo();
/*     */       try {
/* 105 */         cacheInfo.setUrl(new URL(cursor.getString(0)));
/*     */       } catch (MalformedURLException e) {
/* 107 */         return null;
/*     */       }
/* 109 */       cacheInfo.setCreatAt(cursor.getLong(1));
/* 110 */       cacheInfo.setUsetimes(cursor.getInt(2));
/* 111 */       cacheInfo.setFileName(cursor.getString(3));
/* 112 */       cacheInfo.setFileSize(cursor.getLong(4));
/* 113 */       cursor.close();
/* 114 */       return cacheInfo;
/*     */     }
/* 116 */     return null;
/*     */   }
/*     */ 
/*     */   synchronized boolean delete(String url, SQLiteDatabase db)
/*     */   {
/*     */     try
/*     */     {
/* 128 */       db.execSQL("delete from " + this.modeName + " where cache_url='" + url + 
/* 129 */         "'");
/*     */     } catch (SQLException e) {
/* 131 */       return false;
/*     */     }
/* 133 */     return true;
/*     */   }
/*     */ 
/*     */   synchronized List<CacheInfo> selectAll(SQLiteDatabase db)
/*     */   {
/* 142 */     Cursor cursor = db.rawQuery(
/* 143 */       "select cache_url,create_time,usetimes,cache_filename,cache_size from " + 
/* 144 */       this.modeName, null);
/* 145 */     if ((cursor != null) && (cursor.getCount() > 0)) {
/* 146 */       List cacheInfos = new ArrayList();
/* 147 */       cursor.moveToFirst();
/* 148 */       while (cursor.moveToNext()) {
/* 149 */         CacheInfo cacheInfo = new CacheInfo();
/*     */         try {
/* 151 */           cacheInfo.setUrl(new URL(cursor.getString(0)));
/*     */         } catch (MalformedURLException e) {
/* 153 */           return null;
/*     */         }
/* 155 */         cacheInfo.setCreatAt(cursor.getLong(1));
/* 156 */         cacheInfo.setUsetimes(cursor.getInt(2));
/* 157 */         cacheInfo.setFileName(cursor.getString(3));
/* 158 */         cacheInfo.setFileSize(cursor.getLong(4));
/* 159 */         cacheInfos.add(cacheInfo);
/*     */       }
/* 161 */       cursor.close();
/* 162 */       return cacheInfos;
/*     */     }
/* 164 */     return null;
/*     */   }
/*     */ 
/*     */   SQLiteDatabase getSQLiteDatabase() {
/* 168 */     return getWritableDatabase();
/*     */   }
/*     */ }

/* Location:           /home/like/桌面/imagecache.jar
 * Qualified Name:     com.superbearman6.imagecachetatics.DBClient
 * JD-Core Version:    0.6.2
 */