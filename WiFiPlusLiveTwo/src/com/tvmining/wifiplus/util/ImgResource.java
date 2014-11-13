package com.tvmining.wifiplus.util;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

import com.tvmining.wifiplus.entity.ImageWrapper;



/**
 * @author : 桥下一粒砂
 * @email : chenyoca@gmail.com
 * @date : 2012-12-6
 * @desc : TODO
 */
public class ImgResource {

    public static int onLine;

    public static List<ImageWrapper> genData(Bitmap videoBitmap,String packageType,List<String> imageResource) {
        List<ImageWrapper> data = new ArrayList<ImageWrapper>();
        int count = imageResource.size();
        for (int i = 0; i * 5 < count; i++) {
        	if(i * 5 + 2 < count){
        		ImageWrapper iw = new ImageWrapper();
                iw.width = Integer.parseInt(imageResource.get(i * 5 + 1));
               
                iw.height = Integer.parseInt(imageResource.get(i * 5 + 2));
                
                iw.id = i;
                iw.onLine = onLine;
                iw.res = imageResource.get(i * 5);
                iw.type = imageResource.get(i * 5 + 3);
                iw.videoBitmap = videoBitmap;
                iw.packageType = packageType;
                iw.imgTitle = imageResource.get(i * 5 + 4);
                data.add(iw);
        	}
        }
        return data;
    }
}
