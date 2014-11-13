package com.tvmining.wifiplus.view;  

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;
import com.tvmining.sdk.entity.SearchFileEntity;
import com.tvmining.sdk.entity.SideThumbMethod;
import com.tvmining.wifiplus.canvas.util.BitmapUtil;
import com.tvmining.wifiplus.canvas.view.SketchPadView;
import com.tvmining.wifiplus.entity.InteractGalleryEntity;
import com.tvmining.wifiplus.image.zoom.PhotoViewAttacher;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.wifiplus.util.ResourceUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifipluseq.R;
public class InteractGallery extends LinearLayout {  
    
	private Vector<InteractGalleryEntity> viewDataVector;
	
	public InteractGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public InteractGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	

	public InteractGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private ViewPagerAdapter viewPagerAdapter;
    private InteractViewPager viewPager;
    private Context mContext;
    private LinearLayout galleryTop;
    
    private final int TIME_WAIT = 5000;
    private boolean isTimeWait;
    private boolean hasNewImage;
    private float scaleWidth=1;    
    private float scaleHeight=1; 
    private float scaleMin = 0.65f;
    private float scaleMax = 0.85f;
    private int currentColor = Color.BLUE;
    private int currentSize = Constant.EDIT_SIZE_SMALL;
    Handler autoScrollHandler = new Handler();
    
    private Runnable autoScrollRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			hasNewImage = false;
			viewPager.setCurrentItem(viewDataVector.size() - 1, true);
		}
		
	};
	
	private Runnable autoWhenScrollingRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			isTimeWait = false;
			hasNewImage = false;
			viewPager.setOnMove(false);
			resetInteractLargeView();
		}
		
	};
	
	
	private Runnable thumbClickRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			isTimeWait = false;
			if(hasNewImage){
				hasNewImage = false;
				resetInteractLargeView();
			}
		}
		
	};
	
	
	
	public int getCurrentColor() {
		return currentColor;
	}

	public void setCurrentColor(int currentColor) {
		this.currentColor = currentColor;
	}

	public int getCurrentSize() {
		return currentSize;
	}

	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}

	public InteractViewPager getViewPager() {
		return viewPager;
	}

	public LinearLayout getGalleryTop() {
		return galleryTop;
	}

	public boolean isHasNewImage() {
		return hasNewImage;
	}

	public void setHasNewImage(boolean hasNewImage) {
		this.hasNewImage = hasNewImage;
	}

	public InteractGalleryEntity getCurrentGalleryEntity() {
		if(viewDataVector != null && viewDataVector.size() > 0){
			return viewDataVector.get(viewPager.getCurrentItem());
		}else{
			return null;
		}
		
	}

	public boolean isInEdit(){
		return viewPager.isInEdit();
	}
	
		/* 图片放大的method */ 
		private Bitmap big(Bitmap bmp) { 
			int bmpWidth=bmp.getWidth(); 
			int bmpHeight=bmp.getHeight(); 
	
			/* 设置图片放大的比例 */ 
			double scale=1.25; 
			/* 计算这次要放大的比例 */ 
			scaleWidth=(float)(scaleWidth*scale); 
			scaleHeight=(float)(scaleHeight*scale); 
			/* 产生reSize后的Bitmap对象 */ 
			Matrix matrix = new Matrix(); 
			matrix.postScale(scaleWidth, scaleHeight); 
			Bitmap resizeBmp = Bitmap.createBitmap(bmp,0,0,bmpWidth, 
					bmpHeight,matrix,true); 
			
			return resizeBmp;
		}
	
	protected void init(Context mContext) {  
        // TODO Auto-generated method stub  
    	this.mContext = mContext;
    	viewDataVector = new Vector<InteractGalleryEntity>();
    	View view = LayoutInflater.from(mContext).inflate(ResourceUtil.getResId(mContext, "interact_gallery", "layout"),null);
        this.addView(view);
        galleryTop = (LinearLayout) findViewById(R.id.gallerytop);  
        //设置ImageSwitcher控件  
        viewPager = (InteractViewPager) findViewById(R.id.viewpager);
        viewPager.setInteractGallery(this);
        viewPagerAdapter = new ViewPagerAdapter(mContext);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				
				InteractGalleryEntity currentEntity = viewDataVector.get(pos);
				parent:for(int i=0;i<viewPager.getChildCount();i++){
					FrameLayout frameLayout = (FrameLayout) viewPager.getChildAt(i);
					if(frameLayout != null && !frameLayout.getTag().equals(currentEntity)){
						SketchPadView sketchPadView = (SketchPadView) frameLayout.getChildAt(0);
			    		if(sketchPadView != null){
			    			if(sketchPadView.attacher != null && sketchPadView.attacher.getScale() > sketchPadView.attacher.getMinScale()){
		            			sketchPadView.attacher.zoomTo(sketchPadView.attacher.getMinScale(), 0, 0);
							}
			        	}
					}
		    	}
				
				for(int i=0;i<galleryTop.getChildCount();i++){
					ImageView imageView = (ImageView) galleryTop.getChildAt(i);
					if(i != pos){
						imageView.setScaleX(scaleMin);
						imageView.setScaleY(scaleMin);
					}else{
						imageView.setScaleX(scaleMax);
						imageView.setScaleY(scaleMax);
					}
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				if(arg0 == 0){
					// do nothing
					
					
				}else if(arg0 == 1){
					// scrolling
					
				}else if(arg0 == 2){
					// scroll finished
					
				}
			}
		});
    }  
    
	public void delayLoadNewImage(){
		autoScrollHandler.removeCallbacks(autoScrollRunnable);
		autoScrollHandler.removeCallbacks(autoWhenScrollingRunnable);
		autoScrollHandler.postDelayed(autoWhenScrollingRunnable, TIME_WAIT);
	}
	
	public void clickThumbResponse(){
		autoScrollHandler.removeCallbacks(autoScrollRunnable);
		autoScrollHandler.removeCallbacks(autoWhenScrollingRunnable);
		autoScrollHandler.postDelayed(thumbClickRunnable, TIME_WAIT);
	}
	
	public void clearInteractView(){
		if(!viewPager.isInEdit() && !Constant.forceAnswer){
			if(viewDataVector != null && viewDataVector.size() > 0){
				viewDataVector.clear();
				viewPagerAdapter.notifyDataSetChanged();
				galleryTop.removeAllViews();
			}
		}else{
			if(viewDataVector != null && viewDataVector.size() > 0){
				galleryTop.removeAllViews();
			}
		}
	}
	
	public void clearThumbView(){
		galleryTop.removeAllViews();
	}
	
	public void resetNormalInteractView(){
		Utility.removeExtraData();
		if(viewPager.isInEdit()){
			for(int i=0;i<Constant.followGalleryEntityVector.size();i++){
				final InteractGalleryEntity galleryEntity = Constant.followGalleryEntityVector.get(i);
				
				final ImageView imageView = new ImageView(mContext);
		    	imageView.setScaleX(scaleMin);
		    	imageView.setScaleY(scaleMin);
		    	imageView.setTag(galleryEntity);
		    	imageView.setImageBitmap(galleryEntity.getThumbnailBitmap());
		    	imageView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						clickThumbView(galleryEntity);
					}
				});
				galleryTop.addView(imageView);
			}
		}else{
			viewDataVector.clear();
			for(int i=0;i<Constant.followGalleryEntityVector.size();i++){
				final InteractGalleryEntity galleryEntity = Constant.followGalleryEntityVector.get(i);
				viewDataVector.add(galleryEntity);
				
				final ImageView imageView = new ImageView(mContext);
		    	imageView.setScaleX(scaleMin);
		    	imageView.setScaleY(scaleMin);
		    	imageView.setTag(galleryEntity);
		    	imageView.setImageBitmap(galleryEntity.getThumbnailBitmap());
		    	imageView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						clickThumbView(galleryEntity);
					}
				});
				galleryTop.addView(imageView);
			}
			viewPagerAdapter.notifyDataSetChanged();
			viewPager.setCurrentItem(viewDataVector.size() - 1, false);
		}
		
		
		ImageView lastView = (ImageView) galleryTop.getChildAt(galleryTop.getChildCount() - 1);
		if(lastView != null){
			lastView.setScaleX(scaleMax);
			lastView.setScaleY(scaleMax);
		}
		
	}
	
	public void resetAnswerInteractView(){
		galleryTop.removeAllViews();
		Utility.removeExtraData();
		viewDataVector.clear();
		for(int i=0;i<Constant.followGalleryEntityVector.size();i++){
			final InteractGalleryEntity galleryEntity = Constant.followGalleryEntityVector.get(i);
			viewDataVector.add(galleryEntity);
			
			final ImageView imageView = new ImageView(mContext);
	    	imageView.setScaleX(scaleMin);
	    	imageView.setScaleY(scaleMin);
	    	imageView.setTag(galleryEntity);
	    	if(imageView != null && galleryEntity != null && galleryEntity.getThumbnailBitmap() != null){
	    		imageView.setImageBitmap(galleryEntity.getThumbnailBitmap());//设置图片
		    	imageView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						clickThumbView(galleryEntity);
					}
				});
				galleryTop.addView(imageView);//gallery上部添加一个小的imageview
	    	}
	    	
		}
		
		viewPagerAdapter.notifyDataSetChanged();
		viewPager.setCurrentItem(viewDataVector.size() - 1, false);
		
		ImageView lastView = (ImageView) galleryTop.getChildAt(galleryTop.getChildCount() - 1);
		if(lastView != null){
			lastView.setScaleX(scaleMax);
			lastView.setScaleY(scaleMax);
		}
		
	}
	
	public void resetAnswerInteractViewData(){
		galleryTop.removeAllViews();
		Utility.removeExtraData();
		for(int i=0;i<Constant.followGalleryEntityVector.size();i++){
			final InteractGalleryEntity galleryEntity = Constant.followGalleryEntityVector.get(i);
			
			final ImageView imageView = new ImageView(mContext);
	    	imageView.setScaleX(scaleMin);
	    	imageView.setScaleY(scaleMin);
	    	imageView.setTag(galleryEntity);
	    	imageView.setImageBitmap(galleryEntity.getThumbnailBitmap());
	    	imageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					clickThumbView(galleryEntity);
				}
			});
			galleryTop.addView(imageView);
		}
		
		ImageView lastView = (ImageView) galleryTop.getChildAt(galleryTop.getChildCount() - 1);
		if(lastView != null){
			lastView.setScaleX(scaleMax);
			lastView.setScaleY(scaleMax);
		}
		
	}
	
	/**
	 * 上不的小image的点击事件
	 * @param galleryEntity
	 */
	private void clickThumbView(InteractGalleryEntity galleryEntity){
		if(!Constant.forceAnswer && !viewPager.isInEdit()){
			for(int i=0;i<viewDataVector.size();i++){
				if(galleryEntity.equals(viewDataVector.get(i))){
					viewPager.setCurrentItem(i, true);
					isTimeWait = true;
					clickThumbResponse();
					break;
				}
			}
		}
	}
	
	public synchronized void resetInteractLargeView(){
		if(!viewPager.isInEdit()){
			
			Utility.removeExtraData();
			viewDataVector.clear();
			for(int i=0;i<Constant.followGalleryEntityVector.size();i++){
				InteractGalleryEntity galleryEntity = Constant.followGalleryEntityVector.get(i);
				viewDataVector.add(galleryEntity);
			}
			
			viewPagerAdapter.notifyDataSetChanged();
			
			autoScrollHandler.removeCallbacks(autoScrollRunnable);
    		autoScrollHandler.postDelayed(autoScrollRunnable, 0);
		}
	}
	
	/**
	 * 向gallery中添加一个entity
	 * @param galleryEntity
	 */
    public void addImageInfo(final InteractGalleryEntity galleryEntity){
    	hasNewImage = true;
    	if(!viewPager.isInEdit() && !viewPager.isOnMove() && !isTimeWait && !Constant.forceAnswer){//滑动、编辑、TIME_WAIT时间内均不发生变化
//    	if(!viewPager.isInEdit()){
    		InteractGalleryEntity repeatGalleryEntity = null;
    		for(int i=0;i<viewDataVector.size();i++){
    			if(viewDataVector.get(i).getItemGuid().equals(galleryEntity.getItemGuid())){
    				repeatGalleryEntity = viewDataVector.get(i);
    				break;
    			}
    		}
    		
    		if(repeatGalleryEntity != null){
    			viewDataVector.remove(repeatGalleryEntity);
    		}
			viewDataVector.add(galleryEntity);
        	
			Vector indexVector = new Vector();
        	if(viewDataVector.size() > 5){
        		for(int i=0;i<viewDataVector.size()-5;i++){
        			indexVector.add(viewDataVector.get(i));
        		}
        		for(int i=0;i<indexVector.size();i++){
        			viewDataVector.remove(indexVector.get(i));
        		}
        	}
        	viewPagerAdapter.notifyDataSetChanged();
		}
		
		final ImageView imageView = new ImageView(mContext);
    	imageView.setScaleX(scaleMin);
    	imageView.setScaleY(scaleMin);
    	imageView.setTag(galleryEntity);
    	imageView.setImageBitmap(galleryEntity.getThumbnailBitmap());
    	imageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				clickThumbView(galleryEntity);
			}
		});
    	
		View repeatThumbView = null;
    	for(int i=0;i<galleryTop.getChildCount();i++){
    		Object obj = galleryTop.getChildAt(i).getTag();
    		if(obj != null && obj instanceof InteractGalleryEntity){
    			InteractGalleryEntity thumbGalleryEntity = (InteractGalleryEntity) obj;
    			if(thumbGalleryEntity.getItemGuid().equals(galleryEntity.getItemGuid())){
    				repeatThumbView = galleryTop.getChildAt(i);
    				break;
    			}
    		}
    	}
    	
    	if(repeatThumbView != null){
    		galleryTop.removeView(repeatThumbView);
    		galleryTop.requestLayout();
    	}
    	
    	if(galleryTop.getChildCount() < 5){
    		galleryTop.addView(imageView);
		}else{
			galleryTop.removeViewAt(0);
			galleryTop.addView(imageView);
		}
    	
    	if(!viewPager.isInEdit()){
    		if(!viewPager.isOnMove() && !isTimeWait){
        		autoScrollHandler.removeCallbacks(autoScrollRunnable);
        		autoScrollHandler.postDelayed(autoScrollRunnable, 0);
        	}
    	}
    }
    
    public class ViewPagerAdapter extends PagerAdapter {

    	private Context mContext;
    	
    	public ViewPagerAdapter(Context c) {  
            mContext = c;  
        }  
    	@Override
        public int getCount() {
          return viewDataVector.size();
        }

    	@Override    
    	public int getItemPosition(Object object) {         
    		Object tag = ((View)object).getTag();
            for (int i = 0; i < viewDataVector.size(); i++) {
                if (tag.equals(viewDataVector.get(i))) {
                    return i;
                }
            }
            return POSITION_NONE;
   
    	}
    	
        @Override
        public boolean isViewFromObject(View view, Object object) {
          return view == ((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int pos) {
        	
        	final InteractGalleryEntity galleryEntity = viewDataVector.get(pos);
        	
        	
        	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			View sketchPadParentView = inflater.inflate(R.layout.interact_item_gallery, null);
			sketchPadParentView.setTag(galleryEntity);
			SketchPadView sketchPadView = (SketchPadView) sketchPadParentView.findViewById(R.id.sketchpadthumbnail);
			sketchPadView.setStrokeColor(currentColor);
			sketchPadView.setFrom("net");
			sketchPadView.setStrokeSize(currentSize, SketchPadView.STROKE_PEN);
			final ImageView playView = (ImageView) sketchPadParentView.findViewById(R.id.sketchpadplayimage);
			/*int padding = mContext.getResources().getDimensionPixelSize(
		          R.dimen.padding_medium);
			sketchPadView.setPadding(padding, padding, padding, padding);*/
        	if(galleryEntity != null){
        		if("VIDEO".equals(galleryEntity.getFileType())){
    				playView.setVisibility(View.VISIBLE);
    		    	playView.setOnTouchListener(new View.OnTouchListener() {
    					
    					@Override
    					public boolean onTouch(View v, MotionEvent event) {
    						// TODO Auto-generated method stub
    						switch (event.getAction()) {
    				            case MotionEvent.ACTION_DOWN:
    				            	playView.setImageResource(R.drawable.play_touch);
    				                break;
    				            case MotionEvent.ACTION_UP:
    				            case MotionEvent.ACTION_CANCEL:
    				            	playView.setImageResource(R.drawable.play);
    				            	break;
    						}
    						
    						return false;
    					}
    				});
    		    	playView.setOnClickListener(new View.OnClickListener() {
    					
    					@Override
    					public void onClick(View v) {
    						// TODO Auto-generated method stub
    						Uri uri = Uri.parse(galleryEntity.getVideoUrl());
    						Intent intent = new Intent(Intent.ACTION_VIEW);   
    						intent.setDataAndType(uri, "video/mp4");    
    						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    						mContext.startActivity(intent);
    					}
    				});
    			}
    	    	
            	GetImageTask task = new GetImageTask(sketchPadView,galleryEntity);
            	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            	sketchPadView.setImageBitmap(galleryEntity.getThumbnailBitmap());  
//            	sketchPadView.setBkBitmap(galleryEntity.getThumbnailBitmap());
                viewPager.addView(sketchPadParentView);
        	}
			
          return sketchPadParentView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        	((ViewPager)container).removeView((View)object);

        }
      }
    
    /**
     * 进入绘画模式
     */
    public void enterEditStatus(){
    	SketchPadView sketchPadView = null;
    	
    	parent:for(int i=0;i<viewPager.getChildCount();i++){
    		FrameLayout frameLayout = (FrameLayout) viewPager.getChildAt(i);
    		sketchPadView = (SketchPadView) frameLayout.getChildAt(0);
    		PhotoViewAttacher attacher = sketchPadView.attacher;
    		if(sketchPadView != null){
    			viewPager.setInEdit(true);
        		if(attacher != null){
        			attacher.setZoomable(false);
        		}
        		sketchPadView.setDrawStrokeEnable(true);//绘画标识设置为true
        		
        		Bitmap bitmap = sketchPadView.getBkBitmap();
        		if(bitmap != null){
        			int h = (bitmap.getHeight() * Constant.screenWidth)/bitmap.getWidth();
            		sketchPadView.getLayoutParams().height = h;
        		}
        		
        		sketchPadView.setScaleType(ScaleType.FIT_CENTER);
        		sketchPadView.requestLayout();
        	}
    	}
	}
    
    public void endEditStatus(){
    	SketchPadView sketchPadView = null;
    	
    	parent:for(int i=0;i<viewPager.getChildCount();i++){
    		FrameLayout frameLayout = (FrameLayout) viewPager.getChildAt(i);
    		sketchPadView = (SketchPadView) frameLayout.getChildAt(0);
    		PhotoViewAttacher attacher = sketchPadView.attacher;
    		if(sketchPadView != null){
    			viewPager.setInEdit(false);
        		if(attacher != null){
        			attacher.setZoomable(true);
        		}
        		
        		if(attacher != null && attacher.getScale() > attacher.getMinScale()){
        			attacher.zoomTo(attacher.getMinScale(), 0, 0);
				}
        		sketchPadView.setDrawStrokeEnable(false);
        		
        		sketchPadView.getLayoutParams().height = sketchPadView.parentMeasuredHeight;
        		sketchPadView.getLayoutParams().width = Constant.screenWidth;
        		sketchPadView.setScaleType(ScaleType.FIT_CENTER);
        		
        		sketchPadView.requestLayout();
        	}
    	}
	}
    
    public void endEditStatusInForceAnswer(){
    	SketchPadView sketchPadView = null;
    	
    	parent:for(int i=0;i<viewPager.getChildCount();i++){
    		FrameLayout frameLayout = (FrameLayout) viewPager.getChildAt(i);
    		sketchPadView = (SketchPadView) frameLayout.getChildAt(0);
    		PhotoViewAttacher attacher = sketchPadView.attacher;
    		if(sketchPadView != null){
        		if(attacher != null){
        			attacher.setZoomable(true);
        		}
        		
        		if(attacher != null && attacher.getScale() > attacher.getMinScale()){
        			attacher.zoomTo(attacher.getMinScale(), 0, 0);
				}
        		sketchPadView.setDrawStrokeEnable(false);
        		
        		sketchPadView.getLayoutParams().height = sketchPadView.parentMeasuredHeight;
        		sketchPadView.getLayoutParams().width = Constant.screenWidth;
        		sketchPadView.setScaleType(ScaleType.FIT_CENTER);
        		
        		sketchPadView.requestLayout();
        	}
    	}
	}
    
    public void setLandScreenView(){
    	WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		for(int i=0;i<viewPager.getChildCount();i++){
    		FrameLayout frameLayout = (FrameLayout) viewPager.getChildAt(i);
    		SketchPadView sketchPadView = (SketchPadView) frameLayout.getChildAt(0);
    		if(sketchPadView != null){
    			sketchPadView.getLayoutParams().width = wm.getDefaultDisplay().getWidth();
    			sketchPadView.getLayoutParams().height = wm.getDefaultDisplay().getHeight();
    			sketchPadView.setScaleType(ScaleType.FIT_CENTER);
    			sketchPadView.requestLayout();
    		}
    	}
    }
    
    class GetImageTask extends AsyncTask<Void, Void, Bitmap> {

    	private SketchPadView sketchPadView;
    	private InteractGalleryEntity galleryEntity;
    	
    	public GetImageTask(SketchPadView sketchPadView,InteractGalleryEntity galleryEntity){
    		this.sketchPadView = sketchPadView;
    		this.galleryEntity = galleryEntity;
    	}
    	
		protected Bitmap doInBackground(Void... args) {
			
			Bitmap bitmap = null;
			
            if(Constant.cacheMap.containsKey(galleryEntity.getItemGuid())){
           	 Log.d("FollowScreen", "load from cache:"+galleryEntity.getItemGuid());
           	 bitmap = ImageUtil.loadFromCache((String)Constant.cacheMap.get(galleryEntity.getItemGuid()));
           	 if(bitmap == null){
           		 Log.d("FollowScreen", "not found from network:"+galleryEntity.getImageUrl()+",reload from network");
           		int i=0;
           		 do{
               		i++;
               		bitmap = ImageUtil.downloadImageNoZoom(galleryEntity.getImageUrl());
               		if(bitmap == null){
               			try {
    						Thread.sleep(200);
    					} catch (InterruptedException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
               		}
               		
               	 }while(bitmap == null && i < 5);
           	 }
            }else{
           	 Log.d("FollowScreen", "load from network:"+galleryEntity.getImageUrl());
           	 int i=0;
           	 do{
           		i++;
           		bitmap = ImageUtil.downloadImageNoZoom(galleryEntity.getImageUrl());
           		if(bitmap == null){
           			try {
    					Thread.sleep(200);
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
           		}
           		
           	 }while(bitmap == null && i < 5);
           	 
            }
			
			return bitmap;
		}

		protected void onPostExecute(Bitmap bitmap) {
			if(bitmap != null){
				sketchPadView.setImageBitmap(bitmap);  
	        	sketchPadView.setBkBitmap(bitmap);
	        	sketchPadView.setDrawStrokeEnable(false);
	            sketchPadView.parentMeasuredHeight = viewPager.getMeasuredHeight();
	            
	            PhotoViewAttacher attacher = new PhotoViewAttacher(sketchPadView);
	            sketchPadView.attacher = attacher;
	            
	            if(Constant.forceAnswer){
	            	if("1".equals(galleryEntity.getAnswerType())){
	            		Constant.activity.drawAnswerImage();
	            	}else if("0".equals(galleryEntity.getAnswerType())){
	            		viewPager.setUnscrolled(true);
	            		Constant.activity.answerSelectQuestion();
	            	}
        		}
			}
		}
	}
    
    
    public void setDrawColor(int color){
    	currentColor = color;
    	for(int i=0;i<viewPager.getChildCount();i++){
    		FrameLayout frameLayout = (FrameLayout) viewPager.getChildAt(i);
			SketchPadView sketchPadView = (SketchPadView) frameLayout.getChildAt(0);
			sketchPadView.setStrokeColor(color);
    	}
    }
    
    /**
     * 笔迹清除
     */
    public void clearDrawColor(){
    	for(int i=0;i<viewPager.getChildCount();i++){
    		FrameLayout frameLayout = (FrameLayout) viewPager.getChildAt(i);
			SketchPadView sketchPadView = (SketchPadView) frameLayout.getChildAt(0);
			sketchPadView.clearAllStrokes();
    	}
    }
    /**
     * 设置笔迹大小
     */
    public void setDrawSize(int strokeSize,int type){
    	currentSize = strokeSize;
    	for(int i=0;i<viewPager.getChildCount();i++){
    		FrameLayout frameLayout = (FrameLayout) viewPager.getChildAt(i);
			SketchPadView sketchPadView = (SketchPadView) frameLayout.getChildAt(0);
			sketchPadView.setStrokeSize(strokeSize,type);
    	}
    }
    
    public SketchPadView getCurrentSketchPadView(){
    	SketchPadView sketchPadView = null;
    	int pos = viewPager.getCurrentItem();
    	for(int i=0;i<viewPager.getChildCount();i++){
    		FrameLayout frameLayout = (FrameLayout) viewPager.getChildAt(i);
    		if(frameLayout.getTag().equals(viewDataVector.get(pos))){
    			sketchPadView = (SketchPadView) frameLayout.getChildAt(0);
    			break;
    		}
    	}
    	
    	return sketchPadView;
    }
    
    public String saveDrawBitmapToDisk(SketchPadView sketchPadView){
    	String strFilePath = Utility.getStrokeFilePath();
        Bitmap bmp = sketchPadView.getCanvasSnapshot();
        int a = bmp.getWidth();
        int b = bmp.getHeight();
        if (null != bmp)
        {
            BitmapUtil.saveBitmapToSDCard(bmp, strFilePath);
        }
        
        return strFilePath;
    }
    
    class ReceiveBitmap extends AsyncTask<Void, Void, Vector> {

		private String guid;

		public ReceiveBitmap(String guid) {
			this.guid = guid;
		}

		protected Vector doInBackground(Void... args) {
			
			try {
				receiveBitmapByCommand(guid);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			return null;
		}

		protected void onPostExecute(Vector compareVector) {
		}
	}
    
    public void receiveBitmapByCommand(String questionguid){
    	SearchFileDetailStatusEntity[] result = null;
		String path = "";
		
        try{
        	if(questionguid != null && !"".equals(questionguid)){
        		Constant.questionGalleryEntity = new InteractGalleryEntity();
        		ICESDK mySDK = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),Constant.iceConnectionInfo.getUserInfoEntity());
        		
        		List guidList = new ArrayList<String>();
	    		guidList.add(questionguid);
	    		//根据guid查找文件的类型
	    		SearchFileEntity searchFileCond = new SearchFileEntity();
	    		searchFileCond.guid = guidList;
	        	
	             result = mySDK.searchFile(searchFileCond);
	             String hostName = "http://" + mySDK.getConnectHostName();
	             
	             if(result!=null&&result.length>0){
	            	 if(result[0].height > result[0].width){
	            		 path = hostName + result[0].getSideThumbURI(Constant.MAX_HEIGHT, SideThumbMethod.height);
	            	 }else{
	            		 path = hostName + result[0].getSideThumbURI(Constant.MAX_HEIGHT, SideThumbMethod.width);
	            	 }
	            	 String smallPath = mySDK
								.getThumbURLByFileDetail(result[0],
										Constant.screenWidth/Constant.IMAGE_COLUMN,
										(Constant.screenWidth * 3/Constant.IMAGE_COLUMN)/4);
	            	 
	            	 Bitmap bitmap = ImageUtil.downloadImage(path);
		             
		             if("VIDEO".equals(result[0].file_type)){
		            	 Constant.questionGalleryEntity.setVideoUrl(hostName + File.separator + result[0].getFileURI());
		 	         }
		 	         
		             Constant.questionGalleryEntity.setImageUrl(path);
		             Constant.questionGalleryEntity.setFileType(result[0].file_type);
		             Constant.questionGalleryEntity.setItemGuid(result[0].guid);
		             Constant.questionGalleryEntity.setSmallImageUrl(smallPath);
		         		
		         	 Bitmap smallBitmap = ImageUtil.downloadImageNoZoom(smallPath);
		         	 Constant.questionGalleryEntity.setThumbnailBitmap(smallBitmap);
	 	         }
	             
        	}
    		
        }catch (Exception ee){
             Log.d("CommandService","搜索文件的出错:",ee);
        }
	}
}