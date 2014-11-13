package com.tvmining.wifiplus.cache;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.widget.ImageView;

import com.tvmining.wifiplus.entity.ImageWrapper;
import com.tvmining.wifiplus.util.ImageUtil;

/**
 * Using LazyList via https://github.com/thest1/LazyList/tree/master/src/com/fedorvlasov/lazylist
 * for the example since its super lightweight
 * I barely modified this file
 */
public class ImageLoader {
    
//    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler=new Handler();//handler to display images in UI thread
    private Context mContext;
    
    public ImageLoader(Context context){
    	mContext = context;
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    
//    final int stub_id= android.R.drawable.alert_dark_frame;
    
    public void DisplayImage(ImageWrapper data, ImageView imageView)
    {
        imageViews.put(imageView, data.res);
        Bitmap bitmap=null;//memoryCache.get(url);
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
        {
            queuePhoto(data, imageView);
//            imageView.setImageDrawable(null);
        }
    }
        
    private void queuePhoto(ImageWrapper data, ImageView imageView)
    {
        PhotoToLoad p=new PhotoToLoad(data.res, imageView,data.onLine,data.height,data.width);
        executorService.submit(new PhotosLoader(p));
    }
    
    private Bitmap getBitmap(String url) 
    {
        File f=fileCache.getFile(url);
        
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
        
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex){
           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError){
//        	   memoryCache.clear();
           }
              
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=700;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            if(scale>=2){
            	scale/=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            FileInputStream stream2=new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public int onLine;
        public int height;
        public int width;
        public PhotoToLoad(String u, ImageView i,int o,int h,int w){
            url=u; 
            imageView=i;
            onLine = o;
            height = h;
            width = w;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                Bitmap bmp=null;//getBitmap(photoToLoad.url);
                if(photoToLoad.onLine == 2){
                	bmp = ImageUtil.getImageThumbnail(photoToLoad.url, photoToLoad.width,photoToLoad.height);
//    							.loadZoomFromCache(photoToLoad.url,photoToLoad.width);
    			}else if(photoToLoad.onLine == 1){
    				bmp = getBitmap(photoToLoad.url);//ImageUtil.downloadToBitmap(data.res);
    			}
//                memoryCache.put(photoToLoad.url, bmp);
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null){
            	/*LinearLayout.LayoutParams params = (LayoutParams) photoToLoad.imageView.getLayoutParams();
            	params.height = bitmap.getHeight();*/
            	photoToLoad.imageView.setAdjustViewBounds(false);
            	photoToLoad.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            	photoToLoad.imageView.setLayoutParams(params);
            	final TransitionDrawable td =
                        new TransitionDrawable(new Drawable[] {
                                new ColorDrawable(android.R.color.transparent),
                                photoToLoad.imageView.getBackground()
                        });
            	
            	photoToLoad.imageView.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
            	photoToLoad.imageView.setImageDrawable(td);
                td.startTransition(2000);//ms毫秒
//                photoToLoad.imageView.setImageBitmap(bitmap);
            }else
                photoToLoad.imageView.setImageDrawable(null);
        }
    }

    public void clearCache() {
//        memoryCache.clear();
        fileCache.clear();
    }

}
