package com.tvmining.wifiplus.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huewu.pla.lib.MultiColumnListView;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.CommandTypeEntity;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;
import com.tvmining.sdk.entity.SideThumbMethod;
import com.tvmining.wifiplus.entity.ItemTable;
import com.tvmining.wifiplus.entity.Permission;
import com.tvmining.wifiplus.image.zoom.PhotoViewAttacher;
import com.tvmining.wifiplus.image.zoom.PhotoViewAttacher.OnViewTapListener;
import com.tvmining.wifiplus.thread.AutoRemoteControlTask;
import com.tvmining.wifiplus.thread.RemoteControlTask;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.wifiplus.util.MessageUtil;
import com.tvmining.wifiplus.util.ResourceUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifiplus.waterfall.util.Util;
import com.tvmining.wifipluseq.R;

public class GalleryView extends BaseView implements View.OnClickListener{

	public GalleryView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public GalleryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public GalleryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public int position;
	private int previousPos = -1;
	private int onLine;
	
	private TextView packageView;//包名
	private TextView pageInfo;//页码
	
//	private TextView bottomPageInfo;//底部页码
	
	public ScrollViewPager viewPager;
	
	public List viewList;
	
	private Animator mCurrentAnimator;
	
    private View sourceView;
    
    private RelativeLayout galleryRootView;
    
    private boolean animationRun;
    
    private View waterContainer;
    
    private float startScale;
    
    private Rect startBounds;
    private Rect finalBounds;
    
    private ViewPagerAdapter adapter;
    
    private Bitmap sourceBitmap;
    
    private MultiColumnListView mWaterfallView;
    private View topview;
    private View wechar,moment,photos;
    private TextView galleryScreenLockText;
    private View pageLockScreenLayout;
    private View galleryscreenlockview;
    
    private RelativeLayout shareGalleryLayout;
    private RelativeLayout shareFriendLayout;
    private RelativeLayout shareWeiXinLayout;
	private RelativeLayout sharePushLayout;
	private RelativeLayout galleryback;
	private View nextgallerylayout;
	private View previousgallerylayout;
	private boolean isScrolled;
	private View nextthumbnaillayout;
	private ImageView nextthumbimageView;
	private TextView imagedesc;
	private TextView imagetitle;
	
	StringBuffer postdata = new StringBuffer();
	private static final int WEIXIN_SHARE_MAX_SIZE = 32;
	private static final int THUMB_SIZE = 150;
	
	private String LOCAL_GALLYERY_PATH = Environment.getExternalStorageDirectory()+File.separator+"DCIM"+File.separator+"Photo"+File.separator+"myShareImage";
	private String LOCAL_VIDEO_PATH = Environment.getExternalStorageDirectory()+File.separator+"DCIM"+File.separator+"Video"+File.separator+"myShareVideo";
	
	
	private Handler scrollHandler = new Handler();
	
	private Runnable scrollRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			isScrolled = false;
		}
		
	};
    
	public void resetViewWH(){
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		for(int i=0;i<viewList.size();i++){
			View view = (View) viewList.get(i);
			ZoomImageView imageView = (ZoomImageView) view.findViewById(R.id.zoomthumbnail);
			imageView.getLayoutParams().width = wm.getDefaultDisplay().getWidth();
			imageView.getLayoutParams().height = wm.getDefaultDisplay().getHeight();
			if(imageView.attacher != null){
				imageView.attacher.resetMaxScale(imageView);
				imageView.attacher.setScaleType(ScaleType.FIT_CENTER);
				imageView.attacher.update();
			}
		}
	}
	
	
	
	public void backToWaterFall(){
		Constant.lockScreen = false;
		clickLose();
		Constant.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
		alphaAnimation.setDuration(300);
		waterContainer.startAnimation(alphaAnimation);		
		
		if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        AnimatorSet set = new AnimatorSet();
        if(viewPager != null){
        	// 120是tab的大小,
//        	startBounds = new Rect(Constant.screenWidth/2 - 200,Constant.screenHeight/2 - 200-120-100,0,0);
        	set
                    .play(ObjectAnimator.ofFloat(viewPager, View.X, startBounds.left))
                    .with(ObjectAnimator.ofFloat(viewPager, View.Y, startBounds.top))
                    .with(ObjectAnimator
                            .ofFloat(viewPager, View.SCALE_X, startScale))
                    .with(ObjectAnimator
                            .ofFloat(viewPager, View.SCALE_Y, startScale));
            set.setDuration(300);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                	galleryRootView.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                    viewList.clear();
                    viewPager.removeAllViews();
                    adapter.notifyDataSetChanged();
                    waterContainer.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                	galleryRootView.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                    viewList.clear();
                    viewPager.removeAllViews();
                    adapter.notifyDataSetChanged();
                    waterContainer.setVisibility(View.INVISIBLE);
                }
            });
            set.start();
            mCurrentAnimator = set;
        }
	}
	
	private void init(Context context){
		mContext = context;
		galleryRootView = (RelativeLayout) LayoutInflater.from(mContext).inflate(ResourceUtil.getResId(mContext, "gallery", "layout"),null);
		this.addView(galleryRootView);
		
		nextthumbnaillayout = galleryRootView.findViewById(R.id.nextthumbnaillayout);
		nextthumbimageView = (ImageView) galleryRootView.findViewById(R.id.nextthumbnail);
		imagedesc = (TextView) galleryRootView.findViewById(R.id.imagedesc);
		imagetitle = (TextView) galleryRootView.findViewById(R.id.imagetitle);
		shareGalleryLayout = (RelativeLayout) galleryRootView.findViewById(R.id.shareGalleryLayout);
		shareFriendLayout = (RelativeLayout) galleryRootView.findViewById(R.id.shareFriendLayout);
		shareWeiXinLayout = (RelativeLayout) galleryRootView.findViewById(R.id.shareWeiXinLayout);
		sharePushLayout = (RelativeLayout) galleryRootView.findViewById(R.id.gallerypush);
		galleryback = (RelativeLayout) galleryRootView.findViewById(R.id.galleryback);
		nextgallerylayout = galleryRootView.findViewById(R.id.nextgallerylayout);
		previousgallerylayout = galleryRootView.findViewById(R.id.previousgallerylayout);
		
		previousgallerylayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isScrolled = false;
				StringBuffer postdata = new StringBuffer();
				postdata.setLength(0);
				postdata.append("{\"type\":\"resource\",\"detail\":[");
				postdata.append("{\"position\":\""+position+"\",\"id\":\""+ mWaterfallView.filterResult.get(position).guid +"\"},");
				postdata.deleteCharAt(postdata.length()-1).append("]}");
				AutoRemoteControlTask pushSourceTask = new AutoRemoteControlTask(mContext,CommandTypeEntity.RIGHT,postdata.toString(),false);
				pushSourceTask.execute(3);
				
				postdata.setLength(0);
			}
		});
		
		nextgallerylayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isScrolled = false;
				StringBuffer postdata = new StringBuffer();
				postdata.setLength(0);
				postdata.append("{\"type\":\"resource\",\"detail\":[");
				postdata.append("{\"position\":\""+position+"\",\"id\":\""+ mWaterfallView.filterResult.get(position).guid +"\"},");
				postdata.deleteCharAt(postdata.length()-1).append("]}");
				AutoRemoteControlTask pushSourceTask = new AutoRemoteControlTask(mContext,CommandTypeEntity.LEFT,postdata.toString(),false);
				pushSourceTask.execute(3);
				
				postdata.setLength(0);
			}
		});
		
		shareWeiXinLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View view = (View) viewList.get(position);
				ZoomImageView imageView = (ZoomImageView)view.findViewById(R.id.zoomthumbnail);
				if(imageView.bitmap != null){
					shareImageToWeiXin(imageView.bitmap,false);
				}
			}
		});
		
		shareFriendLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View view = (View) viewList.get(position);
				ZoomImageView imageView = (ZoomImageView)view.findViewById(R.id.zoomthumbnail);
				if(imageView.bitmap != null){
					shareImageToWeiXin(imageView.bitmap,true);
				}
			}
		});
		
		// save to Gallery
		shareGalleryLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SaveToDiskTask().execute();
			}
		});
		
		// push
		sharePushLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				postdata.setLength(0);
				postdata.append("{\"type\":\"resource\",\"detail\":[");
				postdata.append("{\"position\":\""+position+"\",\"id\":\""+ mWaterfallView.filterResult.get(position).guid +"\"},");
				postdata.deleteCharAt(postdata.length()-1).append("]}");
				AutoRemoteControlTask pushSourceTask = new AutoRemoteControlTask(mContext,CommandTypeEntity.PUSH,postdata.toString(),false);
				pushSourceTask.execute(3);
				
				MessageUtil.toastInfo(Constant.activity, mContext.getString(R.string.pushresource)+":"+1+mContext.getString(R.string.completed));
				postdata.setLength(0);
			}
		});
		
		galleryback.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				backToWaterFall();
			}
		});
		
		pageLockScreenLayout = galleryRootView.findViewById(R.id.pagelockscreenlayout);
		pageLockScreenLayout.setOnClickListener(this);
		galleryscreenlockview = galleryRootView.findViewById(R.id.galleryscreenlockview);
		galleryscreenlockview.setOnClickListener(this);
		waterContainer = (FrameLayout) galleryRootView.findViewById(R.id.waterContainer);
		topview = galleryRootView.findViewById(ResourceUtil.getResId(mContext, "topview", "id"));
		galleryScreenLockText = (TextView) galleryRootView.findViewById(ResourceUtil.getResId(mContext, "galleryscreenlocktext", "id"));
		galleryScreenLockText.setOnClickListener(this);
		packageView = (TextView)galleryRootView.findViewById(R.id.packagename);
		pageInfo = (TextView)galleryRootView.findViewById(R.id.pageinfo);
//		bottomPageInfo = (TextView)galleryRootView.findViewById(R.id.pageinfo2);
		
		viewPager = (ScrollViewPager) galleryRootView.findViewById(R.id.viewPager);
		
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				previousPos = position;
				position = arg0;
				
				if(onLine == 2){
					imagedesc.setText(((ItemTable)mWaterfallView.itemVector.get(position)).getItemDescription());
					imagetitle.setText(((ItemTable)mWaterfallView.itemVector.get(position)).getItemTitle());
			    }else{
			    	imagedesc.setText(((SearchFileDetailStatusEntity)mWaterfallView.filterResult.get(position)).desc);
			    	imagetitle.setText(((SearchFileDetailStatusEntity)mWaterfallView.filterResult.get(position)).title);
			    }
				
				new LoadNextImageViewTask().execute();
			      if(Constant.lockScreen){
			    	  if(position == viewList.size()-1){
			    		  nextthumbnaillayout.setVisibility(View.GONE);
			    	  }else{
			    		  nextthumbnaillayout.setVisibility(View.VISIBLE);
			    	  }
			    	  
			      }else{
			    	  nextthumbnaillayout.setVisibility(View.GONE);
			      }
				
				for(int i=0;i<viewPager.getChildCount();i++){
					if(i != arg0){
						View view = (View) viewList.get(i);
						ZoomImageView imageView = (ZoomImageView) view.findViewById(R.id.zoomthumbnail);
						if(imageView.attacher != null && imageView.attacher.getScale() > imageView.attacher.getMinScale()){
							imageView.attacher.zoomTo(imageView.attacher.getMinScale(), 0, 0);
						}
					}
				}
				
				//刷新页码
				if(isScrolled){
					itemSync();
				}
				
				scrollHandler.removeCallbacks(scrollRunnable);
				scrollHandler.postDelayed(scrollRunnable, 300);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				isScrolled = true;
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				if(arg0 == 1){
					/*handler.removeCallbacks(runnable);
					handler.postDelayed( runnable, 300);*/
					Log.d("aaaaaaaaaaaa", "333");
				}else if(arg0 == 2){
					Log.d("aaaaaaaaaaaa", "444");
				}else if(arg0 == 0){
					Log.d("aaaaaaaaaaaa", "555");
				}
			}
		});
		
		viewList = new ArrayList();
	}
	
	private void itemSync(){
		if(Constant.lockScreen && !Constant.COMMUNICATION_TYPE.equalsIgnoreCase(mWaterfallView.packageType)){
			if(mWaterfallView.filterResult != null && mWaterfallView.filterResult.size() > 0 && mWaterfallView.filterResult.get(position) != null){
				String postdata = "{\"pkg\":\""+ mWaterfallView.filterResult.get(position).packname  +"\",\"guid\":\""+mWaterfallView.filterResult.get(position).guid+"\",\"tag\":\""+mWaterfallView.filterResult.get(position).tag+"\"}";
				new RemoteControlTask(mContext,CommandTypeEntity.SYNC,postdata,false).execute(3);
			}
		}
	}
	
	public synchronized void followScreen(String itemGuid){
		if(!this.isScrolled){
			for(int i=0;i<mWaterfallView.filterResult.size();i++){
				SearchFileDetailStatusEntity searchEntity = mWaterfallView.filterResult.get(i);
				if(searchEntity.guid.equals(itemGuid)){
					previousPos = position;
					position = i;
					viewPager.setCurrentItem(i,false);
					break;
				}
			}
		}
	}
	
	public void loadData(int position,final int onLine,
			View sourceView,
			Bitmap sourceBitmap,final MultiColumnListView mWaterfallView,boolean animationRun) {
		
		this.position = position;
		this.previousPos = position;
		this.onLine = onLine;
		this.sourceView = sourceView;
		this.animationRun = animationRun;
		this.sourceBitmap = sourceBitmap;
		this.mWaterfallView = mWaterfallView;
		
		setViewShow();
		
		enableClick();
		
		packageView.setText(mWaterfallView.packageName);		
		pageInfo.setText(position+"/"+mWaterfallView.sourceImageFileNames.size());
		
		setGalleryLockScreen();
		
		addViews();
		
		adapter = new ViewPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(1); 
		viewPager.setCurrentItem(position,false);
	}
	
	private void setViewShow(){
		if(onLine == 1){
			if(Constant.allScreenList.size() > 0){
				if(Constant.lockScreen){
					sharePushLayout.setVisibility(View.GONE);
					galleryback.setVisibility(View.GONE);
				}else{
					sharePushLayout.setVisibility(View.VISIBLE);
					galleryback.setVisibility(View.VISIBLE);
				}
				pageLockScreenLayout.setVisibility(View.VISIBLE);
				
			}else{
				pageLockScreenLayout.setVisibility(View.GONE);
				sharePushLayout.setVisibility(View.GONE);
				galleryback.setVisibility(View.GONE);
			}
		}else{
			sharePushLayout.setVisibility(View.GONE);
			galleryback.setVisibility(View.VISIBLE);
		}
	}
	
	private void addViews(){
		viewList.clear();
		for(int i=0;i<mWaterfallView.sourceImageFileNames.size();i++){
			final int l = i;
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.item_gallery, null);
			
			ZoomImageView imageView = (ZoomImageView) view.findViewById(R.id.zoomthumbnail);
			imageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, "Onclick", Toast.LENGTH_SHORT).show();
				}
			});
			final ImageView playView = (ImageView) view.findViewById(R.id.playImage);
			int padding = mContext.getResources().getDimensionPixelSize(
		          R.dimen.padding_medium);
		    imageView.setPadding(padding, padding, padding, padding);
		    String type = null;
		    if(onLine == 2){
		    	type = ((ItemTable)mWaterfallView.itemVector.get(l)).getItemType();
		    }else{
		    	type = ((SearchFileDetailStatusEntity)mWaterfallView.filterResult.get(l)).file_type;
		    }
		    
//		    setMenuVisible(type);
		    
		    if(!"VIDEO".equals(type)){
		    	PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
		    	imageView.attacher = attacher;
		    	imageView.attacher.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						if(topview.getVisibility() == View.VISIBLE)
							return false;
						topview.setVisibility(View.VISIBLE);
						AnimatorSet set = new AnimatorSet();
//				        
				        set.play(ObjectAnimator.ofFloat(topview, View.Y, -topview.getHeight(),0));
				        set.setDuration(300);
				        set.setInterpolator(new DecelerateInterpolator());
				        set.addListener(new AnimatorListenerAdapter() {
				            @Override
				            public void onAnimationEnd(Animator animation) {}
				            @Override
				            public void onAnimationCancel(Animator animation) {}
				        });
				        set.start();
						
						return false;
					}
				});
		    	
		    	//gallery中图片点击
		    	imageView.attacher.setOnViewTapListener(new OnViewTapListener() {
					
					@Override
					public void onViewTap(View view, float x, float y) {
						// TODO Auto-generated method stub
						quitGallery();
						topview.setVisibility(View.GONE);
					}
				});
		    }else{
		    	final String t = type;
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
						if(onLine == 2){
							if("VIDEO".equals(t)){
								Uri uri = Uri.parse(mWaterfallView.sourceImageFileNames.get(l));
								Intent intent = new Intent(Intent.ACTION_VIEW);   
								intent.setDataAndType(uri, "video/mp4");    
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								mContext.startActivity(intent);
							}
						}else{
							if("VIDEO".equals(t)){
								Uri uri = Uri.parse(mWaterfallView.sourceImageFileNames.get(l));
								Intent intent = new Intent(Intent.ACTION_VIEW);   
								intent.setDataAndType(uri, "video/mp4");    
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								mContext.startActivity(intent);
							}
						}
					}
				});
		    	
		    	imageView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						quitGallery();
						topview.setVisibility(View.GONE);
					}
				});
		    }
		    
		    viewList.add(view);
		}
	}
	
	
	private void zoomImageFromThumb(final View thumbView,int position) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        startBounds = new Rect();
        finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        galleryRootView.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }
        Log.d("aaaaaaaaaa", viewPager.getX()+"");
        viewPager.setPivotX(0f);
        viewPager.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        
        set
                .play(ObjectAnimator.ofFloat(viewPager, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(viewPager, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(viewPager, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(viewPager, View.SCALE_Y, startScale, 1f));
        set.setDuration(300);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
                animationRun = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
                animationRun = false;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }
	
	private class ViewPagerAdapter extends PagerAdapter {

	    @Override
	    public int getCount() {
	      return viewList.size();
	    }

	    @Override
	    public boolean isViewFromObject(View view, Object object) {
	      return view == ((View) object);
	    }

	    @Override
	    public Object instantiateItem(ViewGroup container, final int pos) {
	      final View view =  (View) viewList.get(pos);
	      
	      
	      if(pos == position && animationRun){
	      		if(viewPager != null){
					if(view.getParent() == null){
						ZoomImageView sourceView = (ZoomImageView) view.findViewById(R.id.zoomthumbnail);
						if(sourceBitmap != null && !sourceBitmap.isRecycled()){
							sourceView.setImageBitmap(sourceBitmap);
						}
			    	  	
						viewPager.addView(view);
					}
				}
	      		if(animationRun){
					//初始化
					waterContainer.setVisibility(View.VISIBLE);
					galleryRootView.setVisibility(View.VISIBLE);
					Animation alphaAnimation = new AlphaAnimation(0f, 1.0f);
					alphaAnimation.setDuration(300);
					waterContainer.startAnimation(alphaAnimation);				
					zoomImageFromThumb(sourceView,position);
				}
	      	}else{
	      		if(viewPager != null){
					if(view.getParent() == null){
						viewPager.addView(view);
					}
				}
	      	}
	      
	      if(onLine == 2){
	    	if(!animationRun){
	    		LoadImageViewTask task = new LoadImageViewTask(view,pos);
  				task.executeOnExecutor(Constant.LIMITED_TASK_EXCUTOR);
	      	}else{
	      		new Handler().postDelayed(new Runnable(){

	    			@Override
	    			public void run() {
	    				// TODO Auto-generated method stub
	    				LoadImageViewTask task = new LoadImageViewTask(view,pos);
		  				task.executeOnExecutor(Constant.LIMITED_TASK_EXCUTOR);
	    			}
	    			
	    		}, 350);
	      	}
	      }else{
	    	  if(!animationRun){
		    		LoadImageViewTask task = new LoadImageViewTask(view,pos);
	  				task.executeOnExecutor(Constant.LIMITED_TASK_EXCUTOR);
		      	}else{
		      		new Handler().postDelayed(new Runnable(){

		    			@Override
		    			public void run() {
		    				// TODO Auto-generated method stub
		    				LoadImageViewTask task = new LoadImageViewTask(view,pos);
			  				task.executeOnExecutor(Constant.LIMITED_TASK_EXCUTOR);
		    			}
		    			
		    		}, 300);
		      	}
	    	  
	      }
	      	
	      return view;
	    }

	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
	    	/*ZoomImageView imageView = (ZoomImageView)((View) object).findViewById(R.id.zoomthumbnail);
		    if(imageView.bitmap != null && !imageView.bitmap.isRecycled()){
		    	imageView.bitmap.recycle();
		    }*/
	    }
	  }
	
	class LoadImageViewTask extends AsyncTask<Object, Object, Bitmap> {

		private ZoomImageView imageView;
		private int currentPageIndex;
		
		public LoadImageViewTask(View view,int currentPageIndex){
			this.currentPageIndex = currentPageIndex;
			imageView = (ZoomImageView) view.findViewById(R.id.zoomthumbnail);
		}
		
		protected Bitmap doInBackground(Object... args) {
			Bitmap bitmap = null;
			if(onLine == 2){
				if("VIDEO".equals(((ItemTable)mWaterfallView.itemVector.get(currentPageIndex)).getItemType())){
					String fileNameNoStuffix = mWaterfallView.sourceImageFileNames.get(currentPageIndex).substring(0,mWaterfallView.sourceImageFileNames.get(currentPageIndex).lastIndexOf("."))+"."+Constant.VIDEO_SUFFIX;
    				bitmap = ImageUtil.loadZoomFromCache(fileNameNoStuffix,false);
					
					if(bitmap == null){
						bitmap = ImageUtil.createVideoThumbnail(mWaterfallView.sourceImageFileNames.get(currentPageIndex),Constant.screenWidth);
					}
					
				}else{
					bitmap = ImageUtil.loadZoomFromCache(mWaterfallView.sourceImageFileNames.get(currentPageIndex),false);
				}
				
			}else if(onLine == 1){
				SearchFileDetailStatusEntity searchEntity = mWaterfallView.filterResult.get(currentPageIndex);
				if("VIDEO".equals(searchEntity.file_type)){
					try {
						String fileURL = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),
								Constant.iceConnectionInfo.getUserInfoEntity())
								.getSideThumbURLByFileDetail(searchEntity,
										Constant.MAX_WIGTH_ONLINE_WATERFALL_VIDEO,
										SideThumbMethod.width);
						bitmap = ImageUtil.downloadImageNoZoom(fileURL);
						if(bitmap != null){
							bitmap = ImageUtil.zoomBitmap(bitmap, Constant.screenWidth, (bitmap.getHeight() * Constant.screenWidth)/bitmap.getWidth());
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else{
					bitmap = ImageUtil.downloadImageNoZoom(mWaterfallView.sourceImageFileNames.get(currentPageIndex));
				}
			}
			
			if (null != bitmap) {
				BitmapDrawable d = new BitmapDrawable(bitmap);
				if (null != d) {
					float bwidth = d.getIntrinsicWidth();
					float bheight = d.getIntrinsicHeight();
					
					if(imageView.attacher != null){
						WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
						if(bheight > bwidth){
							float aw = (bwidth * wm.getDefaultDisplay().getHeight())/bheight;
							float scale = wm.getDefaultDisplay().getWidth()/aw;
							if(scale > imageView.attacher.getMinScale()){
								imageView.attacher.setMaxScale(scale);
							}else{
								imageView.attacher.setMaxScale(1.0f);
							}
							
						}else{
							float ah = (bheight * wm.getDefaultDisplay().getWidth())/bwidth;
							float scale = wm.getDefaultDisplay().getHeight()/ah;
							if(scale > imageView.attacher.getMinScale()){
								imageView.attacher.setMaxScale(scale);
							}else{
								imageView.attacher.setMaxScale(1.0f);
							}
						}
					}
					
				}
			}
			
			imageView.bitmap = bitmap;
			
			return bitmap;
		}

		protected void onPostExecute(Bitmap bitmap) { 
			if(bitmap != null){
//				android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
				
				/*imageView.getLayoutParams().width = EmeetingApplication.screenWidth;
				imageView.getLayoutParams().height = EmeetingApplication.screenHeight;*/
//				imageView.setLayoutParams(params);
				
				WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
				imageView.getLayoutParams().width = wm.getDefaultDisplay().getWidth();
				imageView.getLayoutParams().height = wm.getDefaultDisplay().getHeight();
				imageView.setScaleType(ScaleType.FIT_CENTER);
				imageView.requestLayout();
				
				if(bitmap != null && !bitmap.isRecycled()){
					imageView.setImageBitmap(bitmap);
				}
				
			}
		}
	}

	class LoadNextImageViewTask extends AsyncTask<Object, Object, Bitmap> {

		
		public LoadNextImageViewTask(){
		}
		
		protected Bitmap doInBackground(Object... args) {
			Bitmap bitmap = null;
			if(onLine == 1){
				int next = position + 1;
				if(next < mWaterfallView.filterResult.size()){
					SearchFileDetailStatusEntity searchEntity = mWaterfallView.filterResult.get(next);
					try {
						ICESDK mySDK = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),
								Constant.iceConnectionInfo.getUserInfoEntity());
						if("VIDEO".equals(searchEntity.file_type)){
							String fileURL = mySDK
									.getSideThumbURLByFileDetail(searchEntity,
											Constant.MAX_WIGTH_ONLINE_WATERFALL_VIDEO,
											SideThumbMethod.width);
							bitmap = ImageUtil.downloadImageNoZoom(fileURL);
							if(bitmap != null){
								bitmap = ImageUtil.zoomBitmap(bitmap, Constant.screenWidth, (bitmap.getHeight() * Constant.screenWidth)/bitmap.getWidth());
							}
						}else{
							String fileUrl = mySDK.getSideThumbURLByFileDetail(searchEntity, 200, SideThumbMethod.width);
							bitmap = ImageUtil.downloadImageNoZoom(fileUrl);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
			
			return bitmap;
		}

		protected void onPostExecute(Bitmap bitmap) { 
			if(bitmap != null && !bitmap.isRecycled()){
				nextthumbimageView.setImageBitmap(bitmap);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.pagelockscreenlayout:
		case R.id.galleryscreenlocktext:
		case R.id.galleryscreenlockview:
			setLockScreenStatus();
			break;
		}
	}
	
	public void showNextThumbImage(){
		nextthumbnaillayout.setVisibility(View.VISIBLE);
	}
	
	public void hideNextThumbImage(){
		nextthumbnaillayout.setVisibility(View.GONE);
	}
	
	private void setLockScreenStatus(){
		if(!Constant.lockScreen){
			Constant.lockScreen = true;
			showNextThumbImage();
			galleryScreenLockText.setText(ResourceUtil.getResId(mContext, "online_screen_unlock", "string"));
			showAnimLayout();
			itemSync();
			Constant.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}else{
			Constant.lockScreen = false;
			hideNextThumbImage();
			galleryScreenLockText.setText(ResourceUtil.getResId(mContext, "online_screen_lock", "string"));
			hideAnimLayout();
			Constant.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
	}
	
	private void quitGallery(){
		clickLose();
		if(Constant.lockScreen){
			hideNextThumbImage();
			galleryScreenLockText.setText(ResourceUtil.getResId(mContext, "online_screen_lock", "string"));
			hideAnimLayout();
			Constant.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}else{
			backToWaterFall();
		}
	}
	
	private void clickLose(){
		galleryScreenLockText.setClickable(false);
		pageLockScreenLayout.setClickable(false);
		galleryscreenlockview.setClickable(false);
	}
	
	private void enableClick(){
		galleryScreenLockText.setClickable(true);
		pageLockScreenLayout.setClickable(true);
		galleryscreenlockview.setClickable(true);
	}
	
	private void setGalleryLockScreen(){
		
		if(onLine == 1){
			if(Constant.user.getPermisssion().getLevel().equals(Permission.PERMISSION_HIGH)){
				if(!Constant.COMMUNICATION_TYPE.equals(mWaterfallView.packageType)){
					pageLockScreenLayout.setVisibility(View.VISIBLE);
				}else{
					pageLockScreenLayout.setVisibility(View.GONE);
				}
	        }else{
	        	pageLockScreenLayout.setVisibility(View.GONE);
	        }
			
		}else{
			pageLockScreenLayout.setVisibility(View.GONE);
		}
	}
	
	public void showAnimLayout(){
		if(nextgallerylayout.getVisibility() != View.VISIBLE){
			nextgallerylayout.setVisibility(View.VISIBLE);
			previousgallerylayout.setVisibility(View.VISIBLE);
			sharePushLayout.setVisibility(View.GONE);
			galleryback.setVisibility(View.GONE);
			int viewH = nextgallerylayout.getHeight();
			if(viewH == 0){
				viewH = (int) Utility.dpToPixel(60);
			}
			AnimatorSet set = new AnimatorSet();
	        set
	                .play(ObjectAnimator.ofFloat(previousgallerylayout, View.Y, Constant.screenHeight,
	                		Constant.screenHeight-viewH-Constant.sbar-Utility.dpToPixel(6)))
	                		.with(ObjectAnimator.ofFloat(nextgallerylayout, View.Y, Constant.screenHeight,
	    	                		Constant.screenHeight-viewH-Constant.sbar-Utility.dpToPixel(6)));
	        set.setDuration(200);
	        set.setInterpolator(new DecelerateInterpolator());
	        set.addListener(new AnimatorListenerAdapter() {
	            @Override
	            public void onAnimationEnd(Animator animation) {
	        		
	            }

	            @Override
	            public void onAnimationCancel(Animator animation) {
	            }
	        });
	        set.start();
		}
	}
	
	public void hideAnimLayout(){
		if(nextgallerylayout.getVisibility() == View.VISIBLE){
			int viewH = nextgallerylayout.getHeight();
			if(viewH == 0){
				viewH = (int) Utility.dpToPixel(60);
			}
			AnimatorSet set = new AnimatorSet();
	        set
	                .play(ObjectAnimator.ofFloat(previousgallerylayout, View.Y, Constant.screenHeight-viewH-Constant.sbar-Utility.dpToPixel(6),
	                		Constant.screenHeight
	                		))
	                		.with(ObjectAnimator.ofFloat(nextgallerylayout, View.Y, Constant.screenHeight-viewH-Constant.sbar-Utility.dpToPixel(6),
	                				Constant.screenHeight));
	        set.setDuration(200);
	        set.setInterpolator(new DecelerateInterpolator());
	        set.addListener(new AnimatorListenerAdapter() {
	            @Override
	            public void onAnimationEnd(Animator animation) {
	            	nextgallerylayout.setVisibility(View.GONE);
	    			previousgallerylayout.setVisibility(View.GONE);
	    			sharePushLayout.setVisibility(View.VISIBLE);
	    			galleryback.setVisibility(View.VISIBLE);
	    			backToWaterFall();
	            }

	            @Override
	            public void onAnimationCancel(Animator animation) {
	            }
	        });
	        set.start();
		}
	}
	
	private void judgeScape(Configuration newConfig){
    	if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
  		  //横向 
    		sharePushLayout.setVisibility(View.GONE);
    		galleryback.setVisibility(View.GONE);
    		topview.setVisibility(View.GONE);
  		}else{
  		  //竖向 
  			setViewShow();
  			if(topview.getVisibility() == View.VISIBLE){
  				topview.setVisibility(View.VISIBLE);
  			}else{
  				topview.setVisibility(View.INVISIBLE);
  			}
  		}
    }
	
	@Override
	public void dealConfigurationChanged(Configuration newConfig){
		resetViewWH();
		judgeScape(newConfig);
	}
	
	private void shareImageToWeiXin(Bitmap bmp,boolean allFriends){
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String shareQualityValue = preferences.getString("sharequality","中(1024x1024)");
		shareQualityValue = shareQualityValue.substring(shareQualityValue.indexOf("(")+1, shareQualityValue.indexOf(")"));
		String[] shareQualityValueArray = shareQualityValue.split("x");
		int width = Integer.parseInt(shareQualityValueArray[0]);
		int height = Integer.parseInt(shareQualityValueArray[1]);
		
		Bitmap thumbBmp = null;
		if(bmp.getWidth() > width){
			if(bmp.getWidth() > bmp.getHeight()){
				thumbBmp = Bitmap.createScaledBitmap(bmp, width, (bmp.getHeight() * width) / bmp.getWidth(), true);
			}else{
				thumbBmp = Bitmap.createScaledBitmap(bmp, (bmp.getWidth() * height) / bmp.getHeight(), height, true);
			}
		}else if(bmp.getHeight() > height){
			if(bmp.getHeight() > bmp.getWidth()){
				thumbBmp = Bitmap.createScaledBitmap(bmp, (bmp.getWidth() * height) / bmp.getHeight(), height, true);
			}else{
				thumbBmp = Bitmap.createScaledBitmap(bmp, width, (bmp.getHeight() * width) / bmp.getWidth(), true);
			}
		}else{
			thumbBmp = bmp;
		}
		
		WXImageObject imgObj = new WXImageObject(bmp);
		
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		
		if(thumbBmp.getWidth() > thumbBmp.getHeight()){
			thumbBmp = Bitmap.createScaledBitmap(thumbBmp, THUMB_SIZE, (thumbBmp.getHeight() * THUMB_SIZE) / thumbBmp.getWidth(), true);
		}else{
			thumbBmp = Bitmap.createScaledBitmap(thumbBmp, (thumbBmp.getWidth() * THUMB_SIZE) / thumbBmp.getHeight(), THUMB_SIZE, true);
		}
		
		thumbBmp = compressImage(thumbBmp);
		
		msg.thumbData = Util.bmpToByteArray(thumbBmp, true);  // 设置缩略图

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		if(!allFriends){
			req.scene = SendMessageToWX.Req.WXSceneSession;
		}else{
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
		}
		
		boolean isSend = Constant.api.sendReq(req);
		
		if(!isSend){
			new AlertDialog.Builder(Constant.activity)  
			                .setTitle(getResources().getString(R.string.share_weixin_title))
			                .setMessage(getResources().getString(R.string.share_weixin_content))
			                .setPositiveButton(getResources().getString(R.string.share_weixin_confirm), null)
			                .show();
		}
    }
	
	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while ( baos.toByteArray().length / 1024>WEIXIN_SHARE_MAX_SIZE) {		
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	private void shareGallerySave(Bitmap bitmap){
    	Calendar cal = Calendar.getInstance(); 
		java.util.Date date = cal.getTime(); 

		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS"); 
    	ImageUtil.saveBitmapToDisk(new File(LOCAL_GALLYERY_PATH+File.separator +sdFormat.format(date)+".jpg"), bitmap,LOCAL_GALLYERY_PATH);
    }
    
    private void sendBroadCastGallerySaved(String path){
    	mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ path)));
		MessageUtil.toastInfo(mContext,"保存至系统相册");
    }
	
	class SaveToDiskTask extends AsyncTask<Object, Object, String> {

		protected String doInBackground(Object... args) {
			String path = null;
			String type = null;
			if(onLine == 2){
		    	type = ((ItemTable)mWaterfallView.itemVector.get(position)).getItemType();
		    	if("VIDEO".equals(type)){
		    		Calendar cal = Calendar.getInstance(); 
		    		java.util.Date date = cal.getTime(); 

		    		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS"); 
		    		
		    		String sourceStr = mWaterfallView.sourceImageFileNames.get(position);
		    		String targetStr = LOCAL_VIDEO_PATH+File.separator+sdFormat.format(date) + "."+sourceStr.substring(sourceStr.lastIndexOf(".")+1,sourceStr.length());
		    		
		    		File dirFile = new File(LOCAL_VIDEO_PATH);
					if(!dirFile.exists()){
						dirFile.mkdirs();
					}
		    		
		    		ImageUtil.copyFile(sourceStr, targetStr);
		    		path = LOCAL_VIDEO_PATH;
		    	}else if("IMAGE".equals(type)){
		    		Bitmap bitmap = BitmapFactory.decodeFile(mWaterfallView.sourceImageFileNames.get(position));
					if(bitmap != null){
						shareGallerySave(bitmap);
						path = LOCAL_GALLYERY_PATH;
					}
		    	}
		    }else{
		    	type = ((SearchFileDetailStatusEntity)mWaterfallView.filterResult.get(position)).file_type;
		    	
		    	if("VIDEO".equals(type)){
		    		ImageUtil.startDownload(mWaterfallView.sourceImageFileNames.get(position), LOCAL_VIDEO_PATH);
		    		path = LOCAL_VIDEO_PATH;
		    	}else if("IMAGE".equals(type)){
		    		Bitmap bitmap = ImageUtil.downloadToBitmap(mWaterfallView.sourceImageFileNames.get(position));
					if(bitmap != null){
						shareGallerySave(bitmap);
						path = LOCAL_GALLYERY_PATH;
					}
		    	}
		    }

			return path;
		}

		protected void onPostExecute(String path) {
			sendBroadCastGallerySaved(path);
		}
	}
}