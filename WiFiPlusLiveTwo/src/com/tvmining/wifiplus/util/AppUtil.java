package com.tvmining.wifiplus.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

import org.textmining.text.extraction.WordExtractor;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.tvmining.wifiplus.application.EmeetingApplication;

public class AppUtil {

	private static final String EM_PACKAGE_NAME = "com.tvmining.wifiplus";

	public static String similarRunningTask(Context mContext) {
		String pakName = "";
		String packageName = mContext.getPackageName();
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(mContext.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskList = am.getRunningTasks(30);
		for (int i = 0; i < taskList.size(); i++) {
			ComponentName cn = taskList.get(i).topActivity;
			if (cn != null) {
				String runPackageName = cn.getPackageName();

				if (runPackageName != null
						&& runPackageName.contains(EM_PACKAGE_NAME)
						&& !runPackageName.equals(packageName)) {
					try {
						PackageInfo packageInfo = mContext.getPackageManager()
								.getPackageInfo(runPackageName, 0);
						if (packageInfo != null) {
							pakName += packageInfo.applicationInfo.loadLabel(
									mContext.getPackageManager()).toString()
									+ ",";
						}
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				}
			}
		}
		if (pakName != null && !"".equals(pakName)) {
			pakName = pakName.substring(0, pakName.length() - 1);
		}
		return pakName;
	}

	/**
	 * 
	 * 读取assets里的指定文件名的文本文件(读取全部内容)
	 * 
	 * @param context
	 * @param fileName
	 *            文本文件名,如:"good.txt"
	 * @return
	 */

	/**
	 * 
	 * 读取assets里的指定文件名的文本文件(读取全部内容)
	 * 
	 * @param context
	 * @param fileName
	 *            文本文件名,如:"good.txt"
	 * @return
	 */

	public static String readAssetTxt(Context context, String fileName) {
		try {
			InputStreamReader inputReader = new InputStreamReader(context
							.getResources().getAssets().open(fileName));

			BufferedReader bufReader = new BufferedReader(inputReader);

			LineNumberReader reader = new LineNumberReader(bufReader);

			String s = reader.readLine();

			String result = "";

			int lines = 0;
			while (s != null) {
				lines++;
				s = reader.readLine();
				result += s;
				// Log.i(TAG, "lines" + lines + ":" + s);

			}

			reader.close();

			bufReader.close();

			return result;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String readDoc(Context mContext,String docName) throws Exception {
		FileInputStream in = new FileInputStream(docName);
		WordExtractor extractor = null;
		String text = null;
		extractor = new WordExtractor();
		text = extractor.extractText(in);
		return text;
	}
	
	public static void copyToData(Context mContext){
		
		File file = new File(Constant.savePath+Constant.CONDITION_NAME);
		try {
			InputStream in = mContext.getAssets().open("condition.doc");
			//从assets目录下复制
            FileOutputStream out = new FileOutputStream(file);
            int length = -1;
            byte[] buf = new byte[1024];
            while ((length = in.read(buf)) != -1)
            {
              out.write(buf, 0, length);
            }
              out.flush();
              in.close();
              out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	public static void delConditionFile(String filePath){
		
		File file = new File(filePath);
		try {
			if(file != null && file.exists()){
				file.delete();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}  
	}
}
