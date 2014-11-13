package com.tvmining.wifiplus.waterfall.resource;

import java.io.File;

import android.os.Environment;

/**
 * <ul>
 * <li><b>name : </b>		SDCard		</li>
 * <li><b>description :</b>	SD卡管理器				</li>
 * <li><b>author : </b>		桥下一粒砂			</li>
 * <li><b>e-mail : </b>		chenyoca@gmail.com	</li>
 * <li><b>weibo : </b>		@桥下一粒砂			</li>
 * <li><b>date : </b>		2012-9-13 下午3:55:02		</li>
 * </ul>
 */
public class SDCard {

	/** SDCard是否可用 **/
	public static boolean IS_MOUNTED = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	
	/** SDCard的根路径 **/
	private static String SDCARD_PATH;
	
	/**
	 * <b>description :</b>		取得SD卡路径，以/结尾
	 * </br><b>time :</b>		2012-9-13 下午3:58:55
	 * @return
	 */
	public static String getSDCardPath(){
		if(!IS_MOUNTED) return null;
		if(null != SDCARD_PATH) return SDCARD_PATH;
		File path = Environment.getExternalStorageDirectory(); 
		String SDCardPath = path.getAbsolutePath();
		SDCardPath += SDCardPath.endsWith(File.separator) ? "" : File.separator;
		SDCARD_PATH = SDCardPath;
		return SDCardPath;
	}
}
