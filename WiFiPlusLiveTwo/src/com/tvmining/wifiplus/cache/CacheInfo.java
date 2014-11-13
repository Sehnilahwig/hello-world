/*    */package com.tvmining.wifiplus.cache;//com.tvmining.wifiplus.cache包下的代码暂时没有使用
/*    */

/*    */import java.io.Serializable;
import java.net.URL;
import java.util.UUID;

import android.graphics.Bitmap;

/*    */
/*    */
/*    */
/*    */
/*    */public class CacheInfo
/*    */implements Serializable
/*    */{
	/*    */private static final long serialVersionUID = -9063235922001045101L;
	/* 13 */private long creatAt = 0L;
	/*    */
	/* 16 */private URL url = null;
	/*    */
	/* 18 */private String fileName = null;
	/*    */
	/* 20 */private long fileSize = 0L;
	/*    */private Bitmap value;
	/*    */private int usetimes;

	/*    */
	/*    */CacheInfo()
	/*    */{
		/*    */}

	/*    */
	/*    */CacheInfo(URL url, long fileSize, Bitmap value)
	/*    */{
		/* 31 */this.creatAt = System.currentTimeMillis();
		/* 32 */this.usetimes = 0;
		/* 33 */this.fileName = UUID.randomUUID().toString();
		/* 34 */this.url = url;
		/* 35 */this.fileSize = fileSize;
		/* 36 */this.value = value;
		/*    */}

	/*    */
	/*    */public final long getCreatAt()
	/*    */{
		/* 43 */return this.creatAt;
		/*    */}

	/*    */
	/*    */public URL getUrl() {
		/* 47 */return this.url;
		/*    */}

	/*    */
	/*    */public void setUrl(URL url) {
		/* 51 */this.url = url;
		/*    */}

	/*    */
	/*    */public String getFileName() {
		/* 55 */return this.fileName;
		/*    */}

	/*    */
	/*    */public void setFileName(String fileName) {
		/* 59 */this.fileName = fileName;
		/*    */}

	/*    */
	/*    */public void setCreatAt(long creatAt) {
		/* 63 */this.creatAt = creatAt;
		/*    */}

	/*    */
	/*    */public long getFileSize() {
		/* 67 */return this.fileSize;
		/*    */}

	/*    */
	/*    */public void setFileSize(long fileSize) {
		/* 71 */this.fileSize = fileSize;
		/*    */}

	/*    */
	/*    */public Bitmap getValue() {
		/* 75 */return this.value;
		/*    */}

	/*    */
	/*    */public void setValue(Bitmap value) {
		/* 79 */this.value = value;
		/*    */}

	/*    */
	/*    */public int getUsetimes() {
		/* 83 */return this.usetimes;
		/*    */}

	/*    */
	/*    */public void setUsetimes(int usetimes) {
		/* 87 */this.usetimes = usetimes;
		/*    */}
	/*    */
}

/*
 * Location: /home/like/桌面/imagecache.jar Qualified Name:
 * com.superbearman6.imagecachetatics.CacheInfo JD-Core Version: 0.6.2
 */