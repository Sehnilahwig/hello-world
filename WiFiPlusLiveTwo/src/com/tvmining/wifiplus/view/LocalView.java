package com.tvmining.wifiplus.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.tvmining.wifiplus.entity.DownloadProgressEntity;
import com.tvmining.wifiplus.entity.ICETable;
import com.tvmining.wifiplus.entity.LocalGroup;
import com.tvmining.wifiplus.entity.PakgeTable;
import com.tvmining.wifiplus.httpserver.Server;
import com.tvmining.wifiplus.image.loader.ListImageCache.ImageCacheParams;
import com.tvmining.wifiplus.image.loader.ListImageFetcher;
import com.tvmining.wifiplus.swipelistview.BaseSwipeListViewListener;
import com.tvmining.wifiplus.swipelistview.LocalSwipeListView;
import com.tvmining.wifiplus.swipelistview.SwipeListView;
import com.tvmining.wifiplus.swipelistview.utils.SettingsManager;
import com.tvmining.wifiplus.thread.LoadDBDataTask;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.MessageUtil;
import com.tvmining.wifiplus.util.ResourceUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifiplus.waterfall.adapter.LocalPackageAdapter;
import com.tvmining.wifiplus.waterfall.adapter.LocalPackageAdapter.ChildHolder;
import com.tvmining.wifipluseq.R;

/**
 *本地界面：本地下载的资源包
 *布局文件：local.xml
 */
public class LocalView extends BaseView implements
ExpandableListView.OnChildClickListener,
ExpandableListView.OnGroupClickListener,
ExpandableListView.OnGroupExpandListener{
    
	private View localEye;
//	private ExpandableListView expandableListView;
//	private ExpandableListAdapter adapter;
	/**
	 * 扩展的listview:能够滑动item
	 */
	private LocalSwipeListView swipeListView;
	private LocalPackageAdapter adapter;
	private WaterFallView waterFallView;
	private View localContentLayout;
	private ArrayList<LocalGroup> groupList;
	private ArrayList<Object> childList;
	private ListImageFetcher mImageFetcher;
	private ImageCacheParams cacheParams;
	private boolean pause = false;
//	private ImageView qrcodeCreateView;
	private View qrcodeCreateView;
	private ImageView localHead;
	private Bitmap packageMarginBitmap;
	private TextView localusername;
	SharedPreferences preferences;
	private ZoomImageView packageicon;
	private ImageButton shareclose_btn;
	private TextView localpackagename,localpackagesize,localpackagetype,localservername;
	private ImageView localpackageqrcodemage;
	private ImageView sharepackagebackground;

	public Bitmap getPackageMarginBitmap() {
		return packageMarginBitmap;
	}

	public LocalView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public LocalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public LocalView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public void loadLocalData(boolean isReload){
		new LoadDBDataTask(mContext,isReload).execute();
	}
	
	public void dealLocalData(Vector adapterAll) {
		
		groupList = new ArrayList<LocalGroup>();
		childList = new ArrayList<Object>();
		
		if(adapterAll != null){
			for(int i=0;i<adapterAll.size();i++){
				Object[] aobj = (Object[]) adapterAll.get(i);
				ICETable iceTable = (ICETable) aobj[0];
				Vector pkgVector = (Vector) aobj[1];
				
				// deal group
				LocalGroup group = new LocalGroup();
				group.setTitle(iceTable.getIceName());
				group.setIceTable(iceTable);
				group.setIceDate(iceTable.getIceDate().substring(0,iceTable.getIceDate().indexOf(" ")));
				groupList.add(group);
				childList.add(group);
				for(int j = 0 ; j<pkgVector.size() ; j++){
					childList.add(pkgVector.get(j));
				}
				
			}
		}
	}
	
	public void freshDownloadProgress(DownloadProgressEntity progressEntity){
		for(int i=0;i<swipeListView.getChildCount();i++){
			View view = swipeListView.getChildAt(i);
			Object obj = view.getTag();
			if(obj != null && obj instanceof ChildHolder){
				ChildHolder childHolder = (ChildHolder)obj;
				if(progressEntity.getIceIndex() == childHolder.getIceIndex() && progressEntity.getPakgeIndex() == childHolder.getPkgIndex()){
					ProgressBar parogressBar = (ProgressBar) view.findViewById(ResourceUtil.getResId(mContext, "localprogressbar", "id"));
					Log.d("progress-show", ""+progressEntity.getProgress());
					if((!Constant.uploadingMap.containsKey(progressEntity.getPackageGuid()) &&!Constant.downloadingMap.containsKey(progressEntity.getPackageGuid())) || progressEntity.getProgress() == Constant.PROGRESS_DOWNLOAD_ALL_SIZE){
						setViewVisibility(view,true);
					}else{
						setViewVisibility(view,false);
						parogressBar.setProgress((int)progressEntity.getProgress());
					}
					parogressBar.invalidate();
				}
			}
		}
	}
	
	public void setDownloadViewFlag(PakgeTable pakgeTable,boolean isPause){
		for(int i=0;i<swipeListView.getChildCount();i++){
			View view = swipeListView.getChildAt(i);
			Object obj = view.getTag();
			if(obj != null && obj instanceof ChildHolder){
				ChildHolder childHolder = (ChildHolder)obj;
				if(pakgeTable.getPakgeIndex() == childHolder.getPkgIndex()){
					if(isPause){
						Log.d("6666666666666666", "444");
						childHolder.localplayimage.setVisibility(View.VISIBLE);
						childHolder.localplayimage.setImageResource(R.drawable.interact_btn);
					}else{
						Log.d("6666666666666666", "555");
						childHolder.localplayimage.setVisibility(View.VISIBLE);
						childHolder.localplayimage.setImageResource(R.drawable.play);
					}
					
					break;
				}
			}
		}
	}
	
	public void setViewVisibility(View view,boolean isShow){
		if(isShow){
			view.findViewById(ResourceUtil.getResId(mContext, "localprogressbar", "id")).setVisibility(View.GONE);
			view.findViewById(ResourceUtil.getResId(mContext, "localpackagesize", "id")).setVisibility(View.VISIBLE);
			view.findViewById(ResourceUtil.getResId(mContext, "localpackagetype", "id")).setVisibility(View.VISIBLE);
			view.findViewById(ResourceUtil.getResId(mContext, "localpackageupload", "id")).setVisibility(View.VISIBLE);
			view.findViewById(ResourceUtil.getResId(mContext, "localpackagedelete", "id")).setVisibility(View.VISIBLE);
			view.findViewById(ResourceUtil.getResId(mContext, "localpackageshare", "id")).setVisibility(View.VISIBLE);
			view.findViewById(ResourceUtil.getResId(mContext, "localdownloadpackagedelete", "id")).setVisibility(View.GONE);
			view.findViewById(ResourceUtil.getResId(mContext, "localuploadpackagedelete", "id")).setVisibility(View.GONE);
			view.findViewById(ResourceUtil.getResId(mContext, "localplayimage", "id")).setVisibility(View.GONE);
		}else{
			view.findViewById(ResourceUtil.getResId(mContext, "localprogressbar", "id")).setVisibility(View.VISIBLE);
			view.findViewById(ResourceUtil.getResId(mContext, "localpackagesize", "id")).setVisibility(View.GONE);
			view.findViewById(ResourceUtil.getResId(mContext, "localpackagetype", "id")).setVisibility(View.GONE);
			view.findViewById(ResourceUtil.getResId(mContext, "localpackageupload", "id")).setVisibility(View.GONE);
			view.findViewById(ResourceUtil.getResId(mContext, "localpackageshare", "id")).setVisibility(View.GONE);
			view.findViewById(ResourceUtil.getResId(mContext, "localpackagedelete", "id")).setVisibility(View.GONE);
			if(Constant.DO_UPLOAD.equals(Constant.uploadOrDownload)){
				view.findViewById(ResourceUtil.getResId(mContext, "localuploadpackagedelete", "id")).setVisibility(View.VISIBLE);
			}else {
				view.findViewById(ResourceUtil.getResId(mContext, "localplayimage", "id")).setVisibility(View.VISIBLE);
				((ImageView)view.findViewById(ResourceUtil.getResId(mContext, "localplayimage", "id"))).setImageResource(R.drawable.interact_btn);
				view.findViewById(ResourceUtil.getResId(mContext, "localdownloadpackagedelete", "id")).setVisibility(View.VISIBLE);
			}
			
		}
		
	}
	
	public int[] getDownloadChildPosition(DownloadProgressEntity progressEntity) {
		int[] positionArray = null;

		for (int j = 0; j < childList.size(); j++) {
			PakgeTable pkgTable = (PakgeTable) childList.get(j);
			if (progressEntity.getPakgeIndex() == pkgTable.getPakgeIndex()) {
				positionArray = new int[2];
				positionArray[1] = j;
				break;
			}
		}
		return positionArray;
	}
	
	public void clearAdapter(){
		if(groupList != null){
			groupList.clear();
		}
		if(childList != null){
			childList.clear();
		}
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}
	
	public void init() {
		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    	View view = LayoutInflater.from(mContext).inflate(ResourceUtil.getResId(mContext, "local", "layout"),null);
    	sharepackagebackground = (ImageView) view.findViewById(ResourceUtil.getResId(mContext, "sharepackagebackground", "id"));
    	localHead = (ImageView) view.findViewById(ResourceUtil.getResId(mContext, "localhead", "id"));
    	qrcodeCreateView = (View) view.findViewById(ResourceUtil.getResId(mContext, "qrcodecreateview", "id"));
    	packageicon = (ZoomImageView) qrcodeCreateView.findViewById(R.id.localpackageview);
    	localpackagename = (TextView) qrcodeCreateView.findViewById(ResourceUtil.getResId(mContext, "localpackagename", "id"));
    	localpackagesize = (TextView) qrcodeCreateView.findViewById(ResourceUtil.getResId(mContext, "localpackagesize", "id"));
    	localpackagetype = (TextView) qrcodeCreateView.findViewById(ResourceUtil.getResId(mContext, "localpackagetype", "id"));
    	localpackageqrcodemage = (ImageView) qrcodeCreateView.findViewById(ResourceUtil.getResId(mContext, "qrcodeimage", "id"));
    	localservername = (TextView) qrcodeCreateView.findViewById(ResourceUtil.getResId(mContext, "localserverename", "id"));
    	qrcodeCreateView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				closeQRView();
			}
		});
    	shareclose_btn = (ImageButton) qrcodeCreateView.findViewById(ResourceUtil.getResId(mContext, "shareclose", "id"));
    	shareclose_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				closeQRView();
			}
		});
    	localContentLayout = view.findViewById(ResourceUtil.getResId(mContext, "localcontent", "id"));
    	waterFallView = (WaterFallView) view.findViewById(ResourceUtil.getResId(mContext, "waterfalllocal", "id"));
    	localusername = (TextView)view.findViewById(ResourceUtil.getResId(mContext, "localusername", "id"));
    	localusername.setText(preferences.getString("username", getResources().getString(R.string.no_nickname)));
    	Bitmap inputBmp = null;
    	if (new File(Constant.headPath).exists() && new File(Constant.headPath).length() > 0) {
    		inputBmp = BitmapFactory.decodeFile(Constant.headPath);
    	} else {
    		inputBmp = BitmapFactory.decodeResource(mContext.getResources(),
    				R.drawable.head_default);
    	}
    	Bitmap marginBitmap = BitmapFactory.decodeResource(mContext.getResources(),
    			R.drawable.head_border);
    	
    	packageMarginBitmap = BitmapFactory.decodeResource(mContext.getResources(),
    			R.drawable.local_package_border);
    	
    	localHead.setImageBitmap(Utility.loadHeadBitmap(inputBmp, marginBitmap,2,2));
    	
    	/*localHead.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Constant.activity.openSettingView();
			}
		});*/
    	
    	waterFallView.findViewById(ResourceUtil.getResId(mContext, "waterfallback", "id")).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List list = new ArrayList();
				list.add(waterFallView);
				list.add(localContentLayout);
				Message msg = new Message();
				msg.what = Constant.HANDLER_WATERFALL_LOCAL_HIDE;
				msg.obj = list;
				Constant.activity.getHandler().sendMessage(msg);
			}
		});
    	
    	localEye = view.findViewById(ResourceUtil.getResId(mContext, "localeye", "id"));
    	localEye.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*Message msg = new Message();
				msg.what = Constant.HANDLER_SETTING;
				Constant.activity.getHandler().sendMessage(msg);*/
				
				Message msg = new Message();
				msg.what = Constant.HANDLER_LOCAL_QRCODE_SHOW;
				Constant.activity.getHandler().sendMessage(msg);
			}
		});
    	
    	swipeListView = (LocalSwipeListView)view.findViewById(ResourceUtil.getResId(mContext, "locallist", "id"));
    	reload();
//    	expandableListView.setDividerHeight(0); 
//    	expandableListView.setDivider(null);
//
//		expandableListView.setOnChildClickListener(this);
//		expandableListView.setOnGroupClickListener(this);
//		expandableListView.setOnGroupExpandListener(this);
    	
    	this.addView(view);    
    }
	
	public void createQrcodeView(PakgeTable pakgeTable,int index){
		
		adapter.getmImageFetcher().loadImage(pakgeTable.getPakgepath(), packageicon, Constant.FROM_LOCAL);
		localpackagename.setText(pakgeTable.getPakgeName());
		localpackagesize.setText("3M"/*pakgeTable.getPakgeExtentOne()*/);
		LocalGroup group = null;
		for(int i = adapter.getChildList().size()-1;i>=0 ;i--){
			if(adapter.getChildList().get(i) instanceof LocalGroup){
				group = (LocalGroup) adapter.getChildList().get(i);
				break;
			}
		}
		localservername.setText(group.getTitle());
		localpackagetype.setText(pakgeTable.getPakgeType());
		try {
			Utility.createJsonFile(pakgeTable);
			
			String ipport = "http://"+Server.host+":"+Server.port;
			String pkgPath = pakgeTable.getPakgepath();
//			pkgPath = File.separator+"dir"+pkgPath.substring(pkgPath.indexOf("Emeeting")+9,pkgPath.lastIndexOf(pakgeTable.getPakgeName())+pakgeTable.getPakgeName().length());
			
			pkgPath = "dir"+pkgPath.substring(0,pkgPath.lastIndexOf(pakgeTable.getPakgeName())+pakgeTable.getPakgeName().length());
			
			pkgPath = new String(pkgPath.getBytes("UTF-8"),"ISO-8859-1");
			
			String sharePath = "{\"share_package_path\":\""+pkgPath+"\",\"action\":\""+Constant.QRCODE_ACTION_SHARE_PACKAGE+"\",\"ipport\":\""+ipport+"\"}";
			Bitmap bitmap = Utility.create2DCode(sharePath);
			localpackageqrcodemage.setImageBitmap(bitmap);
			
			
			final String path = ipport + pkgPath+File.separator+"share.json";
			/*new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					ImageUtil.readHtmlFile(path);
				}
				
			}).start();*/
			
			
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		qrcodeCreateView.setVisibility(View.VISIBLE);
		sharepackagebackground.setVisibility(View.VISIBLE);
		AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(qrcodeCreateView, View.X, Constant.screenWidth,0))
                .with(ObjectAnimator.ofFloat(sharepackagebackground, "alpha", 0f,1f));
        set.setDuration(300);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//            	qrcodeCreateView.setBackgroundResource(ResourceUtil.getResId(mContext, "sharebackground", "drawable"));
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        set.start();
		
	}
	
	public void clearPreviousWaterFallData(){
		waterFallView.resetItems();
	}
	
	@Override
	public void refreshView(){
		
		waterFallView.initGalleryView(false);
		
		adapter = new LocalPackageAdapter(mContext);
		adapter.setGroupList(groupList);
		adapter.setChildList(childList);
		
		
		ImageCacheParams cacheParams = new ImageCacheParams(mContext, Constant.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);
		int mImageThumbSize = getResources().getDimensionPixelSize(ResourceUtil.getResId(mContext, "image_thumbnail_size", "dimen"));
    	mImageFetcher = new ListImageFetcher(mContext, mImageThumbSize);
    	mImageFetcher.setLoadingImage(R.drawable.empty_photo);
    	mImageFetcher.addImageCache(mContext, cacheParams);
    	
    	adapter.setmImageFetcher(mImageFetcher);
		
		swipeListView.setAdapter(adapter);
		
//		if(swipeListView.getCount() > 0){
//			swipeListView.expandGroup(0,false);
//		}
	}
	
	public void responseNotification(){
		adapter.setGroupList(groupList);
		adapter.setChildList(childList);
		
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onGroupClick(final ExpandableListView parent, final View v,
			int groupPosition, final long id) {
		
		return false;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		
		PakgeTable pakgeTable = (PakgeTable)adapter.getItem(childPosition);
		if(pakgeTable.getStatus() != Constant.DOWNLOAD_FINISHED && !("share".equals(pakgeTable.getPakgeExtentTwo()))){
			if(Constant.loginStatus == Constant.LOGIN_NOTHING || Constant.loginStatus == Constant.LOGIN_FAILURE){
				MessageUtil.toastInfo(mContext,mContext.getResources().getString(R.string.server_not_connect));
			}else{
				adapter.pauseDownload(pakgeTable);
			}
		}else{
			List list = new ArrayList();
			list.add(localContentLayout);
			list.add(waterFallView);
			list.add(groupPosition);
			list.add(childPosition);
			
			Message msg = new Message();
			msg.what = Constant.HANDLER_WATERFALL_LOCAL_SHOW;
			msg.obj = list;
			Constant.activity.getHandler().sendMessage(msg);
		}
		
		return false;
	}
	
	public void loadData( int position){
		int iceIndex = Integer.parseInt(((PakgeTable)adapter.getItem(position)).getIceIndex());
		
		PakgeTable pakgeTable = (PakgeTable)adapter.getItem(position);
		
		Vector itemVector = Constant.dbConnection.getItemsByPakgeIndex(pakgeTable.getPakgeIndex(),"IMAGE");
		waterFallView.setValues(pakgeTable.getPakgeName(), pakgeTable.getPakgeType());
		waterFallView.setItemVector(itemVector);
		waterFallView.loadData(position, 2);
	}

	@Override
	public void onGroupExpand(int groupPosition) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void dealConfigurationChanged(Configuration newConfig){
		waterFallView.dealConfigurationChanged(newConfig);
	}
	
	public void clickItem(View view,int position){
		waterFallView.runClick(view, position);
	}
	
	
	public void refreshicon(){
		Bitmap inputBmp = null;
    	if (new File(Constant.headPath).exists() && new File(Constant.headPath).length() > 0) {
    		inputBmp = BitmapFactory.decodeFile(Constant.headPath);
    	} else {
    		inputBmp = BitmapFactory.decodeResource(mContext.getResources(),
    				R.drawable.head_default);
    	}
    	Bitmap marginBitmap = BitmapFactory.decodeResource(mContext.getResources(),
    			R.drawable.head_border);
    	
    	packageMarginBitmap = BitmapFactory.decodeResource(mContext.getResources(),
    			R.drawable.local_package_border);
    	
    	localHead.setImageBitmap(Utility.loadHeadBitmap(inputBmp, marginBitmap,2,2));
    	localusername.setText(preferences.getString("username", preferences.getString("username", getResources().getString(R.string.no_nickname))));
    	/*localusername.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Constant.activity.openSettingView();
			}
		});*/
    	
    	Utility.uploadPersonInfo(mContext);
	}
	
	/**
	 * 关闭分享View动画
	 */
	private void closeQRView(){
//		qrcodeCreateView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(qrcodeCreateView, View.X, 0,Constant.screenWidth))
                .with(ObjectAnimator.ofFloat(sharepackagebackground, "alpha", 1f,0f));;
        set.setDuration(300);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            	qrcodeCreateView.setVisibility(View.GONE);
            	sharepackagebackground.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        set.start();
	}
	
	private void reload() {
    	WindowManager manager = ((Activity)mContext).getWindowManager();
        SettingsManager settings = SettingsManager.getInstance();
        settings.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
		settings.setSwipeOffsetLeft(120);
		settings.setSwipeOffsetRight(manager.getDefaultDisplay().getWidth()/2);
		swipeListView.setSwipeMode(settings.getSwipeMode());
		swipeListView.setSwipeActionLeft(settings.getSwipeActionLeft());
		swipeListView.setOffsetLeft(convertDpToPixel(settings.getSwipeOffsetLeft()));
		swipeListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
		swipeListView.setAnimationTime(settings.getSwipeAnimationTime());
		swipeListView.setSwipeOpenOnLongPress(settings.isSwipeOpenOnLongPress());
        
		swipeListView.setSwipeCloseAllItemsWhenMoveList(true);
		swipeListView.setSwipeListViewListener(new MyBaseSwipeListViewListener());

        
    }
	public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

	public void closeAllOpendItems(){
		swipeListView.closeOpenedItems();
	}
	
	class MyBaseSwipeListViewListener extends BaseSwipeListViewListener{

		int opened = -1;
		@Override
		public void onClickFrontView(int position) {
			// TODO Auto-generated method stub
			if(swipeListView.isHaveItemOpened()){
				closeAllOpendItems();
			}else{
				if(opened != -1 && swipeListView.getChildCount()-1>position)
					swipeListView.closeAnimate(opened);
				
				PakgeTable pakgeTable = (PakgeTable)adapter.getItem(position);
				if(pakgeTable.getStatus() != Constant.DOWNLOAD_FINISHED && !("share".equals(pakgeTable.getPakgeExtentTwo()))){
					if(Constant.loginStatus == Constant.LOGIN_NOTHING || Constant.loginStatus == Constant.LOGIN_FAILURE){
						MessageUtil.toastInfo(mContext,mContext.getResources().getString(R.string.server_not_connect));
					}else{
						adapter.pauseDownload(pakgeTable);
					}
				}else{
					List list = new ArrayList();
					list.add(localContentLayout);
					list.add(waterFallView);
					list.add(0);
					list.add(position);
					
					Message msg = new Message();
					msg.what = Constant.HANDLER_WATERFALL_LOCAL_SHOW;
					msg.obj = list;
					Constant.activity.getHandler().sendMessage(msg);
				}
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
			swipeListView.closeAnimate(position);
		}

		@Override
		public void onOpened(int position, boolean toRight) {
			// TODO Auto-generated method stub
			if(toRight){
				swipeListView.closeAnimate(position);
			}else {
				opened = position;
			}
		}

		@Override
		public void onDismiss(int[] reverseSortedPositions) {
			// TODO Auto-generated method stub
			super.onDismiss(reverseSortedPositions);
		}
		
		
	}
	
	
}
