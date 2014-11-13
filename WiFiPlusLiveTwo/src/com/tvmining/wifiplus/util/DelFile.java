package com.tvmining.wifiplus.util;

import java.io.File;

public class DelFile {

	/**
	 * 删除所有文件
	 * 
	 * @param file
	 */
	public static void delete(File file) {
		// TODO Auto-generated method stub
		File[] files = file.listFiles();
		if(files != null){
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					files[i].delete();
				} else if (files[i].isDirectory()) {
					if (!files[i].delete()) {
						delete(files[i]);
					}
				}
			}
		}
		
		deleteDirectory(file);
	}

	public static void deleteExceptSuffix(File file,String suffix) {
		// TODO Auto-generated method stub
		File[] files = file.listFiles();
		if(files != null){
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile() && !suffix.equals(getExtensionName(files[i].getName()))) {
					files[i].delete();
				} else if (files[i].isDirectory()) {
					if (!files[i].delete()) {
						deleteExceptSuffix(files[i],suffix);
					}
				}
			}
		}
	}
	
	/*
	 * Java文件操作 获取文件扩展名
	 *
	 *  Created on: 2011-8-2
	 *      Author: blueeagle
	 */
	    public static String getExtensionName(String filename) { 
	        if ((filename != null) && (filename.length() > 0)) { 
	            int dot = filename.lastIndexOf('.'); 
	            if ((dot >-1) && (dot < (filename.length() - 1))) { 
	                return filename.substring(dot + 1); 
	            } 
	        } 
	        return filename; 
	    } 
	/*
	 * Java文件操作 获取不带扩展名的文件名
	 *
	 *  Created on: 2011-8-2
	 *      Author: blueeagle
	 */
	    public static String getFileNameNoEx(String filename) { 
	        if ((filename != null) && (filename.length() > 0)) { 
	            int dot = filename.lastIndexOf('.'); 
	            if ((dot >-1) && (dot < (filename.length()))) { 
	                return filename.substring(0, dot); 
	            } 
	        } 
	        return filename; 
	    } 
	
	/**
	 * 删除所有文件夹
	 * 
	 * @param file
	 */
	public static void deleteDirectory(File file) {
		// TODO Auto-generated method stub
		File[] filed = file.listFiles();
		if(filed != null){
			for (int i = 0; i < filed.length; i++) {
				deleteDirectory(filed[i]);
				filed[i].delete();
			}
		}
		
	}
}