package com.tvmining.wifiplus.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.tvmining.sdk.entity.CommandTypeEntity;
import com.tvmining.wifiplus.application.EmeetingApplication;
import com.tvmining.wifiplus.image.loader.Images;
import com.tvmining.wifiplus.image.loader.ListImageAdapter;
import com.tvmining.wifiplus.image.loader.ListImageCache.ImageCacheParams;
import com.tvmining.wifiplus.image.loader.ListImageFetcher;
import com.tvmining.wifiplus.swipelistview.BaseSwipeListViewListener;
import com.tvmining.wifiplus.swipelistview.SwipeListView;
import com.tvmining.wifiplus.swipelistview.utils.SettingsManager;
import com.tvmining.wifiplus.thread.RemoteControlTask;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ResourceUtil;
import com.tvmining.wifipluseq.R;

/**
 *在线界面：显示服务器的资源包，以供下载
 *布局文件：online.xml
 */
public class OnlineView extends BaseView {
	
	private TextView iceNameView;
	/**
	 * 扩展的listview，滑动item出现下载/删除 按钮
	 */
	private SwipeListView packageListView;
	private ListImageAdapter mAdapter;
	private ListImageFetcher mImageFetcher;
	private ImageCacheParams cacheParams;
	
	/**
	 * 以瀑布流的形式来显示照片
	 */
	private WaterFallView waterFallView;
	private View onlineTopLayout;
	private View unconnectedlayout;
	private View onlinemaskview;
	
	public OnlineView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public OnlineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public OnlineView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	
	
	public WaterFallView getWaterFallView() {
		return waterFallView;
	}

	public void setMaskShow(){
		unconnectedlayout.setVisibility(View.GONE);
	}
	
	public void init() {
    	View view = LayoutInflater.from(mContext).inflate(ResourceUtil.getResId(mContext, "online", "layout"),null);
    	
    	unconnectedlayout = view.findViewById(ResourceUtil.getResId(mContext, "unconnectedlayout", "id"));
    	onlineTopLayout = view.findViewById(ResourceUtil.getResId(mContext, "onlinetoplayout", "id"));
    	iceNameView = (TextView) view.findViewById(ResourceUtil.getResId(mContext, "icename", "id"));
    	packageListView = (SwipeListView) view.findViewById(ResourceUtil.getResId(mContext, "packagelistview", "id"));
    	waterFallView = (WaterFallView) view.findViewById(ResourceUtil.getResId(mContext, "waterfallonline", "id"));
    	onlinemaskview = view.findViewById(ResourceUtil.getResId(mContext, "onlinemaskview", "id"));
    	
    	packageListView.setDividerHeight(0); 
    	packageListView.setDivider(null);
    	
    	waterFallView.findViewById(ResourceUtil.getResId(mContext, "waterfallback", "id")).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List list = new ArrayList();
				list.add(waterFallView);
				list.add(onlineTopLayout);
				Message msg = new Message();
				msg.what = Constant.HANDLER_WATERFALL_ONLINE_HIDE;
				msg.obj = list;
				Constant.activity.getHandler().sendMessage(msg);
			}
		});
    	reload();
    	this.addView(view);    
    }
	
	
	public void showMask(){
		onlinemaskview.setVisibility(View.VISIBLE);
	}
	
	public void hideMask(){
		onlinemaskview.setVisibility(View.GONE);
	}
	
	private void reload() {
    	WindowManager manager = ((Activity)mContext).getWindowManager();
        SettingsManager settings = SettingsManager.getInstance();
        settings.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
		settings.setSwipeOffsetLeft(200);
		settings.setSwipeOffsetRight(manager.getDefaultDisplay().getWidth()/2);
		packageListView.setSwipeMode(settings.getSwipeMode());
		packageListView.setSwipeActionLeft(settings.getSwipeActionLeft());
		packageListView.setOffsetLeft(convertDpToPixel(settings.getSwipeOffsetLeft()));
		packageListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
		packageListView.setAnimationTime(settings.getSwipeAnimationTime());
		packageListView.setSwipeOpenOnLongPress(settings.isSwipeOpenOnLongPress());
        
		packageListView.setSwipeCloseAllItemsWhenMoveList(true);
		
		
		packageListView.setSwipeListViewListener(new MyBaseSwipeListViewListener());

        
    }
	 public int convertDpToPixel(float dp) {
	        DisplayMetrics metrics = getResources().getDisplayMetrics();
	        float px = dp * (metrics.densityDpi / 160f);
	        return (int) px;
	    }
	
	public void clearPreviousWaterFallData(){
		waterFallView.resetItems();
	}
	
	public void resetICEName(){
		iceNameView.setText(Constant.iceConnectionInfo.getLoginICE().connectICEName);
	}
	
	@Override
	public void refreshView(){
		waterFallView.initGalleryView(true);
		
		resetICEName();
		
		mAdapter = new ListImageAdapter(mContext);
		
		final ImageCacheParams cacheParams = new ImageCacheParams(mContext, Constant.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);
		int mImageThumbSize = getResources().getDimensionPixelSize(ResourceUtil.getResId(mContext, "image_thumbnail_size", "dimen"));
    	mImageFetcher = new ListImageFetcher(mContext, mImageThumbSize);
    	mImageFetcher.setLoadingImage(R.drawable.empty_photo);//设置默认图片
    	mImageFetcher.addImageCache(mContext, cacheParams);
    	
    	mAdapter.setImageFetcher(mImageFetcher);
		
    	packageListView.setAdapter(mAdapter);//设置listview
		packageListView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 瀑布流加载图片数据
	 * @param position
	 */
	public void loadData(int position){
		waterFallView.setValues(Images.allPackName[position].packname, Images.allPackName[position].pack_type);
		waterFallView.loadData(position, 1);
	}
	
	@Override
	public void dealConfigurationChanged(Configuration newConfig){
		waterFallView.dealConfigurationChanged(newConfig);
	}
	
	public void clickItem(View view,int position){
		waterFallView.runClick(view, position);
	}
	
	public void closeAllOpendItems(){
		packageListView.closeOpenedItems();
	}
	
	class MyBaseSwipeListViewListener extends BaseSwipeListViewListener{

		int opened = -1;
		@Override
		public void onClickFrontView(int position) {
			// TODO Auto-generated method stub
			if(packageListView.isHaveItemOpened()){
				packageListView.closeOpenedItems();
			}else{
				if(opened != -1)
					packageListView.closeAnimate(opened);
				waterFallView.setVisibility(View.VISIBLE);
				List list = new ArrayList();
				list.add(onlineTopLayout);
				list.add(waterFallView);
				list.add(position);
				
				if(Constant.lockScreen){
					if(!Constant.COMMUNICATION_TYPE.equalsIgnoreCase(Images.allPackName[position].pack_type)){
						if(Images.allPackName != null && Images.allPackName[position] != null){
							String postdata = "{\"pkg\":\""+ Images.allPackName[position].packname  +"\",\"guid\":\""+Images.allPackName[position].thumb_guid+"\",\"tag\":\"\"}";
							new RemoteControlTask(mContext,CommandTypeEntity.SYNC,postdata,false).execute(3);
						}
					}
				}
				
				Message msg = new Message();
				msg.what = Constant.HANDLER_WATERFALL_ONLINE_SHOW;
				msg.obj = list;
				Constant.activity.getHandler().sendMessage(msg);
			}
		}

		@Override
		public void onClosed(int position, boolean fromRight) {
			// TODO Auto-generated method stub
			opened = -1;
		}

		@Override
		public void onListChanged() {
			// TODO Auto-generated method stub
			super.onListChanged();
			Log.i("DDDD", "onListChanged");
		}

		@Override
		public void onMove(int position, float x) {
			// TODO Auto-generated method stub
			super.onMove(position, x);
			Log.i("DDDD", "onMove");
		}

		@Override
		public void onClickBackView(int position) {
			// TODO Auto-generated method stub
			packageListView.closeAnimate(position);
		}

		@Override
		public void onOpened(int position, boolean toRight) {
			// TODO Auto-generated method stub
			if(toRight){
				packageListView.closeAnimate(position);
			}else {
				opened = position;
			}
		}
	}
}