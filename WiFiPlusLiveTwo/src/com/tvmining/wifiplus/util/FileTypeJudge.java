package com.tvmining.wifiplus.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FileTypeJudge {    
    
	private static Class<?> mMediaFile, mMediaFileType;    
	private static Method getFileTypeMethod, isAudioFileTypeMethod, isVideoFileTypeMethod, isImageFileTypeMethod;    
	private static String methodName = "getBoolean";    
	private static String getFileType = "getFileType";    
        
    private static String isAudioFileType = "isAudioFileType";    
    private static String isVideoFileType = "isVideoFileType";    
    private static String isImageFileType = "isImageFileType";    
        
    private static Field fileType;    
        
    static {    
        try {    
            mMediaFile = Class.forName("android.media.MediaFile");    
            mMediaFileType = Class.forName("android.media.MediaFile$MediaFileType");    
                
            fileType = mMediaFileType.getField("fileType"); //www.sctarena.com   
                
            getFileTypeMethod = mMediaFile.getMethod(getFileType, String.class);    
                
            isAudioFileTypeMethod = mMediaFile.getMethod(isAudioFileType, int.class);    
            isVideoFileTypeMethod = mMediaFile.getMethod(isVideoFileType, int.class);    
            isImageFileTypeMethod = mMediaFile.getMethod(isImageFileType, int.class);    
                
        } catch (NoSuchMethodException e) {    
            e.printStackTrace();    
        } catch (ClassNotFoundException e) {    
            e.printStackTrace();    
        } catch (NoSuchFieldException e) {    
            e.printStackTrace();    
        }    
    
    }    
        
    public static int getMediaFileType(String path) {    
    
        int type = 0;    
            
        try {    
            Object obj = getFileTypeMethod.invoke(mMediaFile, path);    
            if (obj == null) {    
                type = -1;    
            } else {    
                type = fileType.getInt(obj);    
            }    
        } catch (IllegalArgumentException e) {    
            e.printStackTrace();    
        } catch (IllegalAccessException e) {    
            e.printStackTrace();    
        } catch (InvocationTargetException e) {    
            e.printStackTrace();    
        }    
            
        return type;    
    }    
        
    public static boolean isAudioFile(int fileType) {    
        boolean isAudioFile = false;    
        try {    
            isAudioFile = (Boolean) isAudioFileTypeMethod.invoke(mMediaFile, fileType);    
        } catch (IllegalArgumentException e) {    
            e.printStackTrace();    
        } catch (IllegalAccessException e) {    
            e.printStackTrace();    
        } catch (InvocationTargetException e) {    
            e.printStackTrace();    
        }    
        return isAudioFile;    
    }    
        
    public static boolean isVideoFile(int fileType) {    
        boolean isVideoFile = false;    
        try {    
            isVideoFile = (Boolean) isVideoFileTypeMethod.invoke(mMediaFile, fileType);    
        } catch (IllegalArgumentException e) {    
            e.printStackTrace();    
        } catch (IllegalAccessException e) {    
            e.printStackTrace();    
        } catch (InvocationTargetException e) {    
            e.printStackTrace();    
        }    
        return isVideoFile;    
    }    
        
    public static boolean isImageFile(int fileType) {    
        boolean isImageFile = false;    
        try {    
            isImageFile = (Boolean) isImageFileTypeMethod.invoke(mMediaFile, fileType);    
        } catch (IllegalArgumentException e) {    
            e.printStackTrace();    
        } catch (IllegalAccessException e) {    
            e.printStackTrace();    
        } catch (InvocationTargetException e) {    
            e.printStackTrace();    
        }    
        return isImageFile;    
    }    
        
}    
