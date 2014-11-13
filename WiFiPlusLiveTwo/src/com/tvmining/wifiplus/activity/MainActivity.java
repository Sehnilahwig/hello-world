package com.tvmining.wifiplus.activity;

import java.io.File;
import java.util.List;
import java.util.Vector;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.google.zxing.CaptureView;
import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.wifiplus.entity.DownloadProgressEntity;
import com.tvmining.wifiplus.entity.InteractGalleryEntity;
import com.tvmining.wifiplus.entity.PakgeTable;
import com.tvmining.wifiplus.entity.VersionInfo;
import com.tvmining.wifiplus.image.loader.Images;
import com.tvmining.wifiplus.service.CommandService;
import com.tvmining.wifiplus.thread.ListDirectorysTask;
import com.tvmining.wifiplus.thread.ReceiveBitmap;
import com.tvmining.wifiplus.thread.SearchAllScreenTask;
import com.tvmining.wifiplus.thread.UpdateVersionInfo;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ICEConnect;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.wifiplus.util.MessageUtil;
import com.tvmining.wifiplus.util.ResourceUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifiplus.view.AboutView;
import com.tvmining.wifiplus.view.GalleryView;
import com.tvmining.wifiplus.view.InteractCameraView;
import com.tvmining.wifiplus.view.InteractView;
import com.tvmining.wifiplus.view.LayoutCalculator;
import com.tvmining.wifiplus.view.LocalView;
import com.tvmining.wifiplus.view.LoginView;
import com.tvmining.wifiplus.view.OnlineView;
import com.tvmining.wifiplus.view.RemoteView;
import com.tvmining.wifiplus.view.SettingView;
import com.tvmining.wifipluseq.R;

/**
 * 该activity中主要存放了4个tab view：互动类InteractView、遥控器类RemoteView、
 * 本地类LocalView、在线类OnlineView
 */
public class MainActivity extends TabActivity implements
		TabHost.OnTabChangeListener {
	/** Called when the activity is first created. */

	private String TAG = "MainActivity";
	private Context mContext;
	private TabHost tabHost;
	private FrameLayout interactivesecondmenu;
	private FrameLayout remotesecondmenu;
	private FrameLayout onlinesecondmenu;
	private FrameLayout localsecondmenu;
	private FrameLayout interactcolormenu;
	private FrameLayout interactsizemenu;
	private SettingView settingView;
	private InteractCameraView interactCameraView;
	private View interactCameraMaskView;
	private View interactCameraLayout;
	private View interactcamerabtn;
	private View interactgallerybtn;
	private View mainFrame;

	private boolean isSecondMenuShow;
	private boolean isViewShow;
	private int previousTab = Constant.TAB_LOCAL;
	private int currentTab = Constant.TAB_LOCAL;
	
	private Toast toast;
	/**
	 * 互动类
	 */
	private InteractView interactView;
	/**
	 * 遥控器类
	 */
	private RemoteView remoteView;
	/**
	 * 在线类
	 */
	private OnlineView onlineView;
	/**
	 * 本地类
	 */
	private LocalView localView;
	private ProgressDialog pd;
	
	private TextView localSetting,remoteSenior,remotemainhome,remotefeedback;
	
	private static final int FROM_CAMERA = 5;
	private static final int FROM_GALLERY = 6;
	private static final int INTERACT_CAMERA = 7;
	private static final int INTERACT_GALLERY = 8;
	
	private String uploadQualityValue;//存储的上传质量值
	private String shareQualityValue; //存储的分享质量值
	private String passwordValue;//存储的密码
	private LoginView loginView;
	private CaptureView captureView;
	private GalleryView galleryLocalView;
	private GalleryView galleryOnlineView;
	private ImageView interactivesecondmenubackground,remotesecondmenubackground,
					  onlinesecondmenubackground,localsecondmenubackground;
	
	private boolean isonAnima = false;
	
	private SharedPreferences preferences;
	
	private ImageView answertabview;
	private View tabtitle;
	private View tabicon;
	private AboutView aboutView;
	
	public static final String IMAGE_UNSPECIFIED = "image/*"; 
	
	public GalleryView getGalleryLocalView() {
		return galleryLocalView;
	}

	
	
	public RemoteView getRemoteView() {
		return remoteView;
	}



	public LocalView getLocalView() {
		return localView;
	}



	public OnlineView getOnlineView() {
		return onlineView;
	}



	public GalleryView getGalleryOnlineView() {
		return galleryOnlineView;
	}
	
	public void showInteractMaskView(){
		interactView.getInteractfacemaskview().setVisibility(View.VISIBLE);
	}
	
	public void hideInteractMaskView(){
		interactView.getInteractfacemaskview().setVisibility(View.GONE);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case Constant.HANDLER_SETTING:
				showView(mainFrame,settingView,new AnimatorListenerAdapter() {

					@Override
					public void onAnimationCancel(Animator animation) {
						// TODO Auto-generated method stub
						super.onAnimationCancel(animation);
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						// TODO Auto-generated method stub
						mainFrame.setVisibility(View.GONE);
					}
					
				});
				break;
			case Constant.HANDLER_WATERFALL_LOCAL_SHOW:
				final List localShowList = (List) msg.obj;
				showView((View)localShowList.get(0),(View)localShowList.get(1),new AnimatorListenerAdapter() {
			        @Override
			        public void onAnimationEnd(Animator animation) {
			        	((View)localShowList.get(0)).setVisibility(View.GONE);
			        	localView.loadData(
			        			Integer.parseInt(localShowList.get(3).toString()));
			        }

			        @Override
			        public void onAnimationCancel(Animator animation) {
			        }
			    });
				break;
			case Constant.HANDLER_WATERFALL_LOCAL_HIDE:
				final List localHideList = (List) msg.obj;
				hideView((View)localHideList.get(0),(View)localHideList.get(1),new AnimatorListenerAdapter() {
		            @Override
		            public void onAnimationEnd(Animator animation) {
		            	((View)localHideList.get(0)).setVisibility(View.GONE);
		            	localView.clearPreviousWaterFallData();
		            }

		            @Override
		            public void onAnimationCancel(Animator animation) {
		            }
		        });
				break;
			case Constant.HANDLER_ONLINEL_SEARCHICE_SUCCESS:
				ICEConnect.stopTimer();
//				toast.setText("搜索到中控...");
//				toast.show();
				ICEConnect.login(true, mContext,msg.obj);
				break;
			case Constant.HANDLER_ONLINEL_SEARCHICE_FAILURE:
//				toast.setText("没有搜索到中控，重新搜索...");
//				toast.show();
				break;
			case Constant.HANDLER_ONLINEL_LOGIN_SUCCESS:
				Log.d(TAG, "登录成功...");
				ICEConnect.stopTimer();
				MessageUtil.toastInfo(mContext,mContext.getResources().getString(R.string.login_success));
				cancelProgressDialog();
				loginView.hideLoginEditLayout();
				onlineView.setMaskShow();
				onlineView.getWaterFallView().setViewShowOrHide();
				settingView.setPermission();
				tabHost.getTabWidget().setVisibility(View.VISIBLE);
				View currentTabView = tabHost.getTabWidget().getChildAt(Constant.TAB_ONLINE);
				ImageView currentopen = (ImageView)currentTabView.findViewById(
						ResourceUtil.getResId(mContext, "connect", "id"));
				currentopen.setVisibility(View.VISIBLE);
				currentTabView.findViewById(
						ResourceUtil.getResId(mContext, "connectprogressbar", "id")).setVisibility(View.GONE);
				new ListDirectorysTask(mContext,msg.obj).execute();
				Utility.uploadPersonInfo(mContext);
				break;
			case Constant.HANDLER_ONLINEL_LOGIN_FAILURE:
				MessageUtil.toastInfo(mContext,mContext.getResources().getString(R.string.login_failure));
				cancelProgressDialog();
				loginView.showLoginEditLayout();
				tabHost.getTabWidget().setVisibility(View.GONE);
				break;
			case Constant.HANDLER_PERMISSION_CHANGE:
				cancelProgressDialog();
				loginView.showLoginEditLayout();
				tabHost.getTabWidget().setVisibility(View.GONE);
				break;
			case Constant.HANDLER_ONLINEL_LOAD_PACKAGES:
				List list = (List) msg.obj;
				Images.allPackName = (PackInfoEntity[])list.get(0);
				if(list.get(1) != null){
					Handler downloadHandler = (Handler) list.get(1);
					if(downloadHandler != null){
						downloadHandler.sendMessage(new Message());
					}
				}
				onlineView.refreshView();
				cancelProgressDialog();
				break;
			case Constant.HANDLER_WATERFALL_ONLINE_SHOW:
				final List onLineShowList = (List) msg.obj;
				showView((View)onLineShowList.get(0),(View)onLineShowList.get(1),new AnimatorListenerAdapter() {
			        @Override
			        public void onAnimationEnd(Animator animation) {
			        	((View)onLineShowList.get(0)).setVisibility(View.GONE);
			        	onlineView.loadData(Integer.parseInt(onLineShowList.get(2).toString()));
			        }

			        @Override
			        public void onAnimationCancel(Animator animation) {
			        }
			    });
				break;
			case Constant.HANDLER_WATERFALL_ONLINE_HIDE:
				final List onLineHideList = (List) msg.obj;
				hideView((View)onLineHideList.get(0),(View)onLineHideList.get(1),new AnimatorListenerAdapter() {
		            @Override
		            public void onAnimationEnd(Animator animation) {
		            	((View)onLineHideList.get(0)).setVisibility(View.GONE);
		            	onlineView.clearPreviousWaterFallData();
		            }

		            @Override
		            public void onAnimationCancel(Animator animation) {
		            }
		        });
				break;
			case Constant.HANDLER_LOCAL_LOAD_DATA:
				Vector adapterAll = (Vector) msg.obj;
				localView.dealLocalData(adapterAll);
				localView.refreshView();
				cancelProgressDialog();
				break;
			case Constant.HANDLER_WATERFALL_ITEM_CLICK:
				View itemView = (View) msg.obj;
				int position = msg.getData().getInt("position");
				if(currentTab == Constant.TAB_LOCAL){
		    		localView.clickItem(itemView,position);
		    	}else if(currentTab == Constant.TAB_ONLINE){
		    		onlineView.clickItem(itemView,position);
		    	}
				break;
			case Constant.HANDLER_DOWNLOAD_PROGRESS_FRESH:
				DownloadProgressEntity progressEntity = (DownloadProgressEntity) msg.obj;
				localView.freshDownloadProgress(progressEntity);
				break;
			case Constant.HANDLER_ENTER_LOCAL_VIEW:
				cancelProgressDialog();
				localView.loadLocalData(false);
				break;
			case Constant.HANDLER_ONLINEL_LOGIN_CANCEL:
				// show LoginView
				loginView.hideLoginEditLayout();
				tabHost.getTabWidget().setVisibility(View.VISIBLE);
				break;
			case Constant.HANDLER_LOCAL_SHARE_CREATE_QRCODE:
				// show LoginView
				
				PakgeTable pakgeTable = (PakgeTable)msg.obj;
				
				localView.createQrcodeView(pakgeTable,msg.arg1);
				break;
			case Constant.HANDLER_LOCAL_QRCODE_SHOW:
				captureView.setVisibility(View.VISIBLE);
				captureView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						v.setClickable(false);
						closeQrcodeView();
					}
				}); 
				showView(mainFrame,captureView,new AnimatorListenerAdapter() {
			        @Override
			        public void onAnimationEnd(Animator animation) {
			        	
			        	mainFrame.setVisibility(View.GONE);
			        	captureView.setData();
			        	captureView.surfaceView.setVisibility(View.VISIBLE);
			        }

			        @Override
			        public void onAnimationCancel(Animator animation) {
			        }
			    });
				break;
			case Constant.HANDLER_LOCAL_QRCODE_HIDE:
				closeQrcodeView();
				break;
			case Constant.HANDLER_LOCAL_RESPONSE_NOTIFICATION:
				adapterAll = (Vector) msg.obj;
				localView.dealLocalData(adapterAll);
				localView.responseNotification();
				cancelProgressDialog();
				break;
			case Constant.HANDLER_APP_SHARE_DOWNLOAD:
				int rate = (Integer) msg.obj;
				settingView.showAppShareProgressBar(rate);
				break;
			case Constant.HANDLER_APP_SHARE_FILE_CHECK:
				boolean isHave = (Boolean) msg.obj;
				settingView.setCheckResult(isHave);
				break;
			case Constant.HANDLER_APP_SHARE_SHOW_QRCODE:
				Bitmap bitmap = (Bitmap) msg.obj;
				settingView.showQrcodeView(bitmap);
				break;
			case Constant.HANDLER_REMOTE_SHOW:
				remoteView.setMaskShow();
				View ctView = tabHost.getTabWidget().getChildAt(Constant.TAB_REMOTE);
				final ImageView ctopen = (ImageView)ctView.findViewById(
						ResourceUtil.getResId(mContext, "open", "id"));
				showExpandTabFlag(ctopen);
				break;
			case Constant.HANDLER_APPLICAIOTN_UPDATE:
				if(msg.obj != null){
					final VersionInfo versionInfo = (VersionInfo)msg.obj;
					new AlertDialog.Builder(MainActivity.this) 
					.setTitle(getResources().getString(R.string.app_update_confirm))
					.setMessage(getResources().getString(R.string.app_update_content))
					.setPositiveButton(getResources().getString(R.string.app_update_yes), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							new UpdateVersionInfo(versionInfo.getAddr(),mContext).execute();
							dialog.dismiss();
							
						}
					})
					.setNegativeButton(getResources().getString(R.string.app_update_no), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					})
					.show();
				}else{
					new AlertDialog.Builder(Constant.activity)  
		            .setTitle(mContext.getResources().getString(R.string.app_update_title))
		            .setMessage(mContext.getResources().getString(R.string.app_update_no_content))
		            .setPositiveButton(mContext.getResources().getString(R.string.app_update_exit), null)
		            .show();
				}
				break;
			case Constant.HANDLER_DOWNLOAD_AUTO_PAUSE:
				localView.setDownloadViewFlag((PakgeTable)msg.obj,false);
				break;
			}
		}
	};
	
	public Handler getHandler() {
		return handler;
	}
	
	public void setTabVisibility(boolean isShow){
		if(isShow){
			for(int i=0;i<tabHost.getTabWidget().getChildCount();i++){
				tabHost.getTabWidget().getChildAt(i).setVisibility(View.VISIBLE);
			}
			
		}else{
			for(int i=0;i<tabHost.getTabWidget().getChildCount();i++){
				tabHost.getTabWidget().getChildAt(i).setVisibility(View.GONE);
			}
		}
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getApplicationContext();
		Constant.activity = this;
		setContentView(ResourceUtil.getResId(mContext, "main", "layout"));

		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);
		
		galleryLocalView = (GalleryView) findViewById(R.id.gallerylocal);
		galleryOnlineView = (GalleryView) findViewById(R.id.galleryonline);
		captureView = (CaptureView) findViewById(R.id.captureview);
		
		mainFrame = findViewById(ResourceUtil.getResId(mContext, "mainframe", "id"));
		interactivesecondmenu = (FrameLayout) findViewById(
				ResourceUtil.getResId(mContext, "interactivesecondmenu", "id"));
		remotesecondmenu = (FrameLayout) findViewById(
				ResourceUtil.getResId(mContext, "remotesecondmenu", "id"));
		onlinesecondmenu = (FrameLayout) findViewById(
				ResourceUtil.getResId(mContext, "onlinesecondmenu", "id"));
		localsecondmenu = (FrameLayout) findViewById(
				ResourceUtil.getResId(mContext, "localsecondmenu", "id"));
		
		interactcolormenu = (FrameLayout) findViewById(
				ResourceUtil.getResId(mContext, "interactcolormenu", "id"));
		
		interactsizemenu = (FrameLayout) findViewById(
				ResourceUtil.getResId(mContext, "interactsizemenu", "id"));
		
		interactivesecondmenu.getLayoutParams().width = Constant.screenWidth / 4;
		remotesecondmenu.getLayoutParams().width = Constant.screenWidth / 4;
		onlinesecondmenu.getLayoutParams().width = Constant.screenWidth / 4;
		localsecondmenu.getLayoutParams().width = Constant.screenWidth / 4;
		interactivesecondmenubackground = (ImageView) findViewById(ResourceUtil.getResId(mContext, "interactivesecondmenubackground", "id"));
		remotesecondmenubackground = (ImageView) findViewById(ResourceUtil.getResId(mContext, "remotesecondmenubackground", "id"));
		onlinesecondmenubackground = (ImageView) findViewById(ResourceUtil.getResId(mContext, "onlinesecondmenubackground", "id"));
		localsecondmenubackground = (ImageView) findViewById(ResourceUtil.getResId(mContext, "localsecondmenubackground", "id"));
		interactivesecondmenubackground.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideSecondMenu(tabHost.getCurrentTabView(), Constant.TAB_INTERACT);
			}
		});
		remotesecondmenubackground
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						hideSecondMenu(tabHost.getCurrentTabView(), Constant.TAB_REMOTE);
					}
				});
		onlinesecondmenubackground
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						hideSecondMenu(tabHost.getCurrentTabView(), Constant.TAB_ONLINE);
					}
				});
		localsecondmenubackground
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						hideSecondMenu(tabHost.getCurrentTabView(), Constant.TAB_LOCAL);
					}
				});
		
		
		settingView = (SettingView) findViewById(
				ResourceUtil.getResId(mContext, "setting", "id"));
		aboutView = (AboutView) findViewById(
				ResourceUtil.getResId(mContext, "aboutview", "id"));
		
		interactCameraView = (InteractCameraView) findViewById(
				ResourceUtil.getResId(mContext, "interactcameraview", "id"));
		
		interactCameraMaskView = interactCameraView.findViewById(ResourceUtil.getResId(mContext, "interactmaskview", "id"));
		
		interactCameraMaskView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				hideInteractCameraView();
			}
		});
		
		interactCameraLayout = interactCameraView.findViewById(ResourceUtil.getResId(mContext, "interactcameralayout", "id"));
		interactcamerabtn = interactCameraView.findViewById(ResourceUtil.getResId(mContext, "interactcamerabtn", "id"));
		interactgallerybtn = interactCameraView.findViewById(ResourceUtil.getResId(mContext, "interactgallerybtn", "id"));
		
		interactcamerabtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constant.INTERACT_CAMERA_FILE_PATH)));  
				Constant.activity.startActivityForResult(cameraIntent, INTERACT_CAMERA);
			}
		});
		
		interactgallerybtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent galleryintent = new Intent(Intent.ACTION_PICK, null);  
	            galleryintent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_UNSPECIFIED);  
	            Constant.activity.startActivityForResult(galleryintent,INTERACT_GALLERY);
			}
		});
		
		loginView = (LoginView) findViewById(
				ResourceUtil.getResId(mContext, "loginview", "id"));
		
		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		
		Constant.lc = new LayoutCalculator(mContext, preferences.getBoolean("enableStretch",
				true), preferences.getBoolean("tabletLayout", true));
		
		setTabs();
		
		
		showProgressDialog();
		localView.loadLocalData(true);
		
		settingView.findViewById(ResourceUtil.getResId(mContext, "settingback", "id")).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideView(settingView,mainFrame,new AnimatorListenerAdapter() {

					@Override
					public void onAnimationCancel(Animator animation) {
						// TODO Auto-generated method stub
						super.onAnimationCancel(animation);
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						// TODO Auto-generated method stub
						settingView.setVisibility(View.GONE);
						localView.refreshicon();
					}
					
				});
			}
		});
		
		aboutView.findViewById(ResourceUtil.getResId(mContext, "aboutback", "id")).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideView(aboutView,settingView,new AnimatorListenerAdapter() {

					@Override
					public void onAnimationCancel(Animator animation) {
						// TODO Auto-generated method stub
						super.onAnimationCancel(animation);
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						// TODO Auto-generated method stub
						aboutView.setVisibility(View.GONE);
					}
					
				});
			}
		});
		
		secondMenuInit();
		
		toast = Toast.makeText(mContext,"", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP|Gravity.CENTER, 0,0);
		ICESDK.init();
		
		startServices();
		
	}

	private void setTabs() {
//		scanView =  (ScanView) this.findViewById(ResourceUtil.getResId(mContext, "scan", "id"));
		interactView =  (InteractView) this.findViewById(ResourceUtil.getResId(mContext, "interact", "id"));
		remoteView =  (RemoteView) this.findViewById(ResourceUtil.getResId(mContext, "remote", "id"));
		onlineView =  (OnlineView) this.findViewById(ResourceUtil.getResId(mContext, "online", "id"));
		localView =  (LocalView) this.findViewById(ResourceUtil.getResId(mContext, "local", "id"));
		
		interactView.setInteractcolormenu(interactcolormenu);
		interactView.setInteractsizemenu(interactsizemenu);
		
		addTab(ResourceUtil.getResId(mContext, "interact_label", "string"),
				ResourceUtil.getResId(mContext, "interact_btn_bg", "drawable"),
				ResourceUtil.getResId(mContext, "interact", "id"),
				ResourceUtil.getResId(mContext, "interact_id", "string"));

		addTab(ResourceUtil.getResId(mContext, "remote_label", "string"),
				ResourceUtil.getResId(mContext, "remote_btn_bg", "drawable"),
				ResourceUtil.getResId(mContext, "remote", "id"),
				ResourceUtil.getResId(mContext, "remote_id", "string"));

		addTab(ResourceUtil.getResId(mContext, "online_label", "string"),
				ResourceUtil.getResId(mContext, "online_btn_bg", "drawable"),
				ResourceUtil.getResId(mContext, "online", "id"),
				ResourceUtil.getResId(mContext, "online_id", "string"));

		addTab(ResourceUtil.getResId(mContext, "local_label", "string"),
				ResourceUtil.getResId(mContext, "local_btn_bg", "drawable"),
				ResourceUtil.getResId(mContext, "local", "id"),
				ResourceUtil.getResId(mContext, "local_id", "string"));
		
		tabHost.setCurrentTab(Constant.TAB_LOCAL);
		
		if(Constant.loginStatus == Constant.LOGIN_NOTHING || Constant.loginStatus == Constant.LOGIN_FAILURE){
			ICEConnect.searchICE(mContext,null);
		}
	}

	private void addTab(int labelId, int drawableId, int viewId, int tabId) {

		TabHost.TabSpec spec = tabHost.newTabSpec(mContext.getResources()
				.getString(tabId));

		View tabIndicator = LayoutInflater.from(this).inflate(
				ResourceUtil.getResId(mContext, "bottom", "layout"),
				getTabWidget(), false);
		TextView tabTitle = (TextView) tabIndicator.findViewById(ResourceUtil
				.getResId(mContext, "tabtitle", "id"));
		tabTitle.setText(mContext.getResources().getString(labelId));
		ImageView tabIcon = (ImageView) tabIndicator.findViewById(ResourceUtil
				.getResId(mContext, "tabicon", "id"));
		tabIcon.setImageResource(drawableId);
		ImageView open = (ImageView) tabIndicator.findViewById(ResourceUtil
				.getResId(mContext, "open", "id"));
		
		if(viewId == ResourceUtil.getResId(mContext, "online", "id")){
			tabIndicator.findViewById(
					ResourceUtil.getResId(mContext, "connectprogressbar", "id")).setVisibility(View.VISIBLE);
		}
		
		if(viewId == ResourceUtil.getResId(mContext, "interact", "id")){
			answertabview = (ImageView) tabIndicator.findViewById(
					ResourceUtil.getResId(mContext, "answertabview", "id"));
			tabtitle = tabIndicator.findViewById(
					ResourceUtil.getResId(mContext, "tabtitle", "id"));
			tabicon = tabIndicator.findViewById(
					ResourceUtil.getResId(mContext, "tabicon", "id"));
		}
		
		
		spec.setIndicator(tabIndicator);
		spec.setContent(viewId);
		tabHost.addTab(spec);
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		// 每次切换都清除本地数据，重新加载
//		localView.clearAdapter();
		if (tabId.equalsIgnoreCase(getResources().getString(
				ResourceUtil.getResId(mContext, "interact_id", "string")))) {
			setTab(Constant.TAB_INTERACT, true);
		} else if (tabId.equalsIgnoreCase(getResources().getString(
				ResourceUtil.getResId(mContext, "remote_id", "string")))) {
			setTab(Constant.TAB_REMOTE, true);
			new SearchAllScreenTask(mContext,"send").execute();
		} else if (tabId.equalsIgnoreCase(getResources().getString(
				ResourceUtil.getResId(mContext, "online_id", "string")))) {
			setTab(Constant.TAB_ONLINE, true);
		} else if (tabId.equalsIgnoreCase(getResources().getString(
				ResourceUtil.getResId(mContext, "local_id", "string")))) {
			setTab(Constant.TAB_LOCAL, true);
		}
	}

	public void showView(final View waitHideView,final View waitShowview,AnimatorListenerAdapter listener){
		isViewShow = true;
		waitShowview.setVisibility(View.VISIBLE);
		hideSecondMenu(tabHost.getTabWidget().getChildAt(previousTab),previousTab);
		final AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(waitShowview, View.X, Constant.screenWidth,
                        0))
                .with(ObjectAnimator.ofFloat(waitHideView, View.X, 0,-Constant.screenWidth));
        set.setDuration(300);
        set.addListener(listener);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
	}
	
	public void hideView(final View waitHideView,final View waitShowView,AnimatorListenerAdapter listener){
		isViewShow = true;
		waitShowView.setVisibility(View.VISIBLE);
		final AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(waitHideView, View.X, 0,
                		Constant.screenWidth))
                .with(ObjectAnimator.ofFloat(waitShowView, View.X, -Constant.screenWidth, 0));
        set.setDuration(300);
        set.addListener(listener);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
	}
	
	private void showSecondMenu(View v ,int id ) {
		if(isonAnima)
			return ;
		isonAnima = true;
		isSecondMenuShow = true;
		FrameLayout secondMenu ;
		final View background ;
		switch (id) {
		case Constant.TAB_INTERACT:
			secondMenu = interactivesecondmenu;
			background = interactivesecondmenubackground;
			break;
		case Constant.TAB_REMOTE:
			secondMenu = remotesecondmenu;
			background = remotesecondmenubackground;
			break;
		case Constant.TAB_ONLINE:
			secondMenu = onlinesecondmenu;
			background = onlinesecondmenubackground;
			break;
		case Constant.TAB_LOCAL:
			secondMenu = localsecondmenu;
			background = localsecondmenubackground;
			break;

		default:
			secondMenu = localsecondmenu;
			background = localsecondmenubackground;
			break;
		}
		
		
		secondMenu.setVisibility(View.VISIBLE);
		background.setVisibility(View.VISIBLE);
		AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(secondMenu, View.X, v.getX()+(v.getWidth()-secondMenu.getWidth())/2,
                		v.getX()+(v.getWidth()-secondMenu.getWidth())/2))
                .with(ObjectAnimator.ofFloat(secondMenu, View.Y, Constant.screenHeight,
                		Constant.screenHeight - v.getHeight() - secondMenu.getHeight()-Constant.sbar))
                		.with(ObjectAnimator.ofFloat(background, "alpha", 0f,1f));
        set.setDuration(150);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
        		isonAnima = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        set.start();
	}
	
	private void hideSecondMenu(View v , int id) {
		
		if(isSecondMenuShow){
			if(isonAnima)
				return ;
			isonAnima = true;
			final FrameLayout secondMenu ;
			final View background ;
			switch (id) {
			case Constant.TAB_INTERACT:
				secondMenu = interactivesecondmenu;
				background = interactivesecondmenubackground;
				break;
			case Constant.TAB_REMOTE:
				secondMenu = remotesecondmenu;
				background = remotesecondmenubackground;
				break;
			case Constant.TAB_ONLINE:
				secondMenu = onlinesecondmenu;
				background = onlinesecondmenubackground;
				break;
			case Constant.TAB_LOCAL:
				secondMenu = localsecondmenu;
				background = localsecondmenubackground;
				break;

			default:
				secondMenu = localsecondmenu;
				background = localsecondmenubackground;
				break;
			}
			
			isSecondMenuShow = false;
			AnimatorSet set = new AnimatorSet();
	        set
	                .play(ObjectAnimator.ofFloat(secondMenu, View.X, v.getX()+(v.getWidth()-secondMenu.getWidth())/2,
	                		v.getX()+(v.getWidth()-secondMenu.getWidth())/2))
	                .with(ObjectAnimator.ofFloat(secondMenu, View.Y, Constant.screenHeight - v.getHeight() - secondMenu.getHeight()-Constant.sbar,
	                		Constant.screenHeight))
	                .with(ObjectAnimator.ofFloat(background, "alpha", 1f,0f));
	        set.setDuration(150);
	        set.setInterpolator(new DecelerateInterpolator());
	        set.addListener(new AnimatorListenerAdapter() {
	            @Override
	            public void onAnimationEnd(Animator animation) {
	            	secondMenu.setVisibility(View.GONE);
	            	background.setVisibility(View.GONE);
	            	isonAnima = false;
	            }
	            @Override
	            public void onAnimationCancel(Animator animation) {
	            }
	        });
	        set.start();
		}
	}
	
	private void hideSecondMenu(View v) {
		if(isSecondMenuShow){
			isSecondMenuShow = false;
			v.setVisibility(View.GONE);
		}
	}
	
	private void showOrHide(View v , int id ){
		if(!isSecondMenuShow){
			showSecondMenu(v ,id);
		}else{
			hideSecondMenu(v , id);
		}
	}
	
	private void setTab(int id, boolean flag) {
		currentTab = id;
		View currentTabView = tabHost.getTabWidget().getChildAt(id);
		View previousTabView = tabHost.getTabWidget().getChildAt(previousTab);
		hideSecondMenu(previousTabView,previousTab);
		if(null != previousTabView){
			ImageView otheropen = (ImageView)previousTabView.findViewById(
					ResourceUtil.getResId(mContext, "open", "id"));
			otheropen.setVisibility(View.GONE);
		}
		
		final ImageView currentopen = (ImageView)currentTabView.findViewById(
				ResourceUtil.getResId(mContext, "open", "id"));
		
		interactView.getChatView().loseFocus();
		switch (id) {
		
		case Constant.TAB_INTERACT:
			currentTabView
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (currentTab != Constant.TAB_INTERACT) {
								tabHost.setCurrentTab(Constant.TAB_INTERACT);
							}/* else {
								showOrHide(v,Constant.TAB_INTERACT);
							}*/
						}
					});
			previousTab = Constant.TAB_INTERACT;
			currentopen.setVisibility(View.GONE);
			
			if(Constant.forceAnswer && Constant.questionGalleryEntity != null){
				interactView.resetGalleryView(false);
	        	if("0".equals( Constant.questionGalleryEntity.getAnswerType())){
	        		answerSelectQuestion();
	        	}
			}else{
				interactView.resetGalleryView(true);
			}
			
			if(interactView.getInteractGallery().isInEdit()){
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}else{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}
			
			break;
		case Constant.TAB_REMOTE:
			currentTabView
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (currentTab != Constant.TAB_REMOTE) {
								tabHost.setCurrentTab(Constant.TAB_REMOTE);
							} else {
								if(Constant.allScreenList != null && Constant.allScreenList.size() > 0){
									showOrHide(v,Constant.TAB_REMOTE);
								}
							}
						}
					});
			previousTab = Constant.TAB_REMOTE;
			currentopen.setVisibility(View.VISIBLE);
			interactView.getInteractGallery().clearInteractView();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			showExpandTabFlag(currentopen);
			break;
		case Constant.TAB_ONLINE:
			currentTabView
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (currentTab != Constant.TAB_ONLINE) {
								tabHost.setCurrentTab(Constant.TAB_ONLINE);
								
							}/* else {
								showOrHide(v,Constant.TAB_ONLINE);
							}*/
						}
					});
			previousTab = Constant.TAB_ONLINE;
			currentopen.setVisibility(View.GONE);
			interactView.getInteractGallery().clearInteractView();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case Constant.TAB_LOCAL:
			currentTabView
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (currentTab != Constant.TAB_LOCAL) {
								tabHost.setCurrentTab(Constant.TAB_LOCAL);
							} else {
								showOrHide(v,Constant.TAB_LOCAL);
							}
						}
					});
			previousTab = Constant.TAB_LOCAL;
			currentopen.setVisibility(View.VISIBLE);
			interactView.getInteractGallery().clearInteractView();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		}
	}
	
	public void showExpandTabFlag(View currentopen){
		if(this.currentTab == Constant.TAB_REMOTE){
			if(Constant.allScreenList != null && Constant.allScreenList.size() > 0){
				currentopen.setVisibility(View.VISIBLE);
			}else{
				currentopen.setVisibility(View.GONE);
			}
		}else{
			currentopen.setVisibility(View.GONE);
		}
	}
	
	public void showProgressDialog(){
		if(pd == null){
			pd = new ProgressDialog(this);
			pd.setCancelable(false);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setIndeterminate(false);
			pd.show();
		}
	}
	
	public void cancelProgressDialog(){
		if(pd != null){
			pd.cancel();
			pd.dismiss();
			pd = null;
		}
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	if(currentTab == Constant.TAB_LOCAL){
    		localView.dealConfigurationChanged(newConfig);
    	}else if(currentTab == Constant.TAB_ONLINE){
    		onlineView.dealConfigurationChanged(newConfig);
    	}
    	
    	if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
    		  //横向 
//    		  setContentView(R.layout.waterfalllayout); 
    		tabHost.getTabWidget().setVisibility(View.GONE);
    		interactView.setLandscape();
    		}else{
    		  //竖向 
    			tabHost.getTabWidget().setVisibility(View.VISIBLE);
    			interactView.setPortrait();
    		}
	}
	
	public int[] getDownloadChildPosition(DownloadProgressEntity progressEntity){
		return localView.getDownloadChildPosition(progressEntity);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
			Constant.downloadThread.saveToDB(Constant.DOWNLOAD_PAUSE);
			new AlertDialog.Builder(this) 
			.setTitle(getResources().getString(R.string.app_exit_confirm))
			.setMessage(getResources().getString(R.string.app_exit_content))
			.setPositiveButton(getResources().getString(R.string.app_exit_yes), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					MainActivity.this.finish();
					System.exit(0);
				}
			})
			.setNegativeButton(getResources().getString(R.string.app_exit_no), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			})
			.show();
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,final Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == RESULT_OK){
			switch(requestCode){
				case 1:					
					//刷新图像上传质量存储的值
					uploadQualityValue = preferences.getString("uploadquality","中(90%)");
					((SettingView)settingView).changeString(uploadQualityValue, 1);
//					((TextView)layout.getChildAt(0).findViewById(R.id.itemCount)).setText(uploadQualityValue);
					break;
				case 2:					
					//刷新图像分享质量存储的值				
					shareQualityValue = preferences.getString("sharequality","中(1024x1024)");	
					((SettingView)settingView).changeString(shareQualityValue, 2);
//					((TextView)layout.getChildAt(1).findViewById(R.id.itemCount)).setText(shareQualityValue);
					break;
				case 3:					
					//刷新存储的密码
					passwordValue = preferences.getString(Constant.PREFERENCES_NAME,Constant.defaultPwd);
					((SettingView)settingView).changeString(passwordValue, 3);
//					((TextView)layout.getChildAt(2).findViewById(R.id.itemCount)).setText(passwordValue);
					break;
				case FROM_GALLERY:
					if(resultCode == RESULT_OK){
						//处理相册选择文件
						Uri originalUri = data.getData();
						String[] proj = {MediaStore.Images.Media.DATA};
			            //好像是android多媒体数据库的封装接口，具体的看Android文档
			            Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
			            //这个是获得用户选择的图片的索引值
			            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			            //将光标移至开头 ，这个很重要，不小心很容易引起越界
			            cursor.moveToFirst();
			            //最后根据索引值获取图片路径
			            String filepath = cursor.getString(column_index);
			            if(filepath != null && !"".equals(filepath)){
			            	((SettingView)settingView).scaleBitmap(filepath);
			            	((SettingView)settingView).changeHeadImage();
			            }
					}
					break;
				case FROM_CAMERA:
					if(resultCode == RESULT_OK){
						//处理拍照返回文件
						((SettingView)settingView).changeHeadImage();
					}
					break;
				case INTERACT_CAMERA:
					this.hideInteractCameraView();
					final Handler cameraHandler = new Handler() {
						public void handleMessage(Message msg) {
							Bitmap bitmap = (Bitmap) msg.obj;
							if(bitmap != null){
								interactView.showCameraImageView(bitmap);
							}
							
						}
					};
					new Thread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
//							interactView.photoUpload(Constant.INTERACT_CAMERA_FILE_PATH);
							Bitmap bitmap = BitmapFactory.decodeFile(Constant.INTERACT_CAMERA_FILE_PATH);
							int h = (bitmap.getHeight() * Constant.screenWidth)/bitmap.getWidth();
							Bitmap thumbnaiBitmap = ThumbnailUtils.extractThumbnail(bitmap, Constant.screenWidth, h);  
							bitmap.recycle();
							int degree = ImageUtil.readPictureDegree(Constant.INTERACT_CAMERA_FILE_PATH);  
							thumbnaiBitmap = ImageUtil.rotaingImageView(degree, thumbnaiBitmap); 
							Message msg = new Message();
							msg.obj = thumbnaiBitmap;
							cameraHandler.sendMessage(msg);
						}
						
					}).start();
					break;
				case INTERACT_GALLERY:
					this.hideInteractCameraView();
					
					final Handler galleryHandler = new Handler() {
						public void handleMessage(Message msg) {
							Bitmap bitmap = (Bitmap) msg.obj;
							if(bitmap != null){
								interactView.showCameraImageView(bitmap);
							}
							
						}
					};
					
					new Thread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Uri originalUri = data.getData();
							String[] proj = {MediaStore.Images.Media.DATA};
				            //好像是android多媒体数据库的封装接口，具体的看Android文档
				            Cursor cursor = Constant.activity.managedQuery(originalUri, proj, null, null, null); 
				            //按我个人理解 这个是获得用户选择的图片的索引值
				            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				            //将光标移至开头 ，这个很重要，不小心很容易引起越界
				            cursor.moveToFirst();
				            //最后根据索引值获取图片路径
				            String path = cursor.getString(column_index);
				            Log.i(TAG,"从相册选择的文件的绝对路径:"+path);	
//				            interactView.photoUpload(path);
				            
				            Bitmap bitmap = BitmapFactory.decodeFile(path);
				            int h = (bitmap.getHeight() * Constant.screenWidth)/bitmap.getWidth();
							Bitmap thumbnaiBitmap = ThumbnailUtils.extractThumbnail(bitmap, Constant.screenWidth, h);  
//							bitmap.recycle();
							int degree = ImageUtil.readPictureDegree(path);  
							thumbnaiBitmap = ImageUtil.rotaingImageView(degree, thumbnaiBitmap); 
							Message msg = new Message();
							msg.obj = thumbnaiBitmap;
							galleryHandler.sendMessage(msg);
						}
						
					}).start();
					break;
			}
		}		
	}

	public void createWhiteCanvas(){
		this.hideInteractCameraView();
		final Handler cameraHandler = new Handler() {
			public void handleMessage(Message msg) {
				Bitmap bitmap = (Bitmap) msg.obj;
				if(bitmap != null){
					interactView.showCameraImageView(bitmap);
				}
				
			}
		};
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
//				interactView.photoUpload(Constant.INTERACT_CAMERA_FILE_PATH);
				Bitmap whiteBitmap = Bitmap.createBitmap(Constant.screenWidth,Constant.screenHeight, Config.ARGB_8888);
				Canvas whiteCanvas = new Canvas(whiteBitmap);
				whiteCanvas.drawColor(Color.WHITE);
				Message msg = new Message();
				msg.obj = whiteBitmap;
				cameraHandler.sendMessage(msg);
			}
			
		}).start();
	}
	
	public void answerQuestion(String guid,String answerType){
		answerTabHighShow();
		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				interactView.resetGalleryView(true);
			}
		};
		new ReceiveBitmap(guid,answerType,handler).execute();
	}
	
	public boolean isInInteract(){
		if(currentTab == Constant.TAB_INTERACT){
			return true;
		}else{
			return false;
		}
	}
	
	public Bitmap getPackageBitmapBoraer(){
		return localView.getPackageMarginBitmap();
	}
	
	private void secondMenuInit(){
		localSetting = (TextView) localsecondmenu.findViewById(
				ResourceUtil.getResId(mContext, "localsetting", "id"));
		
		/*onlineLockScreen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setLockScreenStatus();
				
				hideSecondMenu(tabHost.getCurrentTabView(), Constant.TAB_ONLINE);
			}
		});*/
		
		localSetting.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openSettingView();
			}
		});
		
		remoteSenior = (TextView) remotesecondmenu.findViewById(
				ResourceUtil.getResId(mContext, "remote_senior", "id"));
		
		remoteSenior.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				remoteView.showMenuOption();
				hideSecondMenu(tabHost.getTabWidget().getChildAt(Constant.TAB_REMOTE),Constant.TAB_REMOTE);
			}
		});
		
		remotemainhome = (TextView) remotesecondmenu.findViewById(
				ResourceUtil.getResId(mContext, "remote_mainhome", "id"));
		
		remotemainhome.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				remoteView.remoteHomepage();
				hideSecondMenu(tabHost.getTabWidget().getChildAt(Constant.TAB_REMOTE),Constant.TAB_REMOTE);
			}
		});
		
		remotefeedback = (TextView) remotesecondmenu.findViewById(
				ResourceUtil.getResId(mContext, "remote_feedback", "id"));
		
		remotefeedback.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideSecondMenu(tabHost.getTabWidget().getChildAt(Constant.TAB_REMOTE),Constant.TAB_REMOTE);
				
				if(Constant.isVibratorOpen){
					Constant.isVibratorOpen = false;
					remotefeedback.setText(ResourceUtil.getResId(mContext, "second_local_feedback_open", "string"));
				}else {
					Constant.isVibratorOpen = true;
					remotefeedback.setText(ResourceUtil.getResId(mContext, "second_local_feedback_close", "string"));
				}
				Utility.remoteVibrator(mContext);
			}
		});
		
	}
	
	public void openSettingView(){
		hideSecondMenu(tabHost.getTabWidget().getChildAt(Constant.TAB_LOCAL),Constant.TAB_LOCAL);
		Message msg = new Message();
		msg.what = Constant.HANDLER_SETTING;
		handler.sendMessage(msg);
	}
	
	public void openAboutView(){
		hideSecondMenu(tabHost.getTabWidget().getChildAt(Constant.TAB_LOCAL),Constant.TAB_LOCAL);
		showView(settingView,aboutView,new AnimatorListenerAdapter() {

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				super.onAnimationCancel(animation);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				settingView.setVisibility(View.GONE);
			}
			
		});
	}
	
	public void closeQrcodeView(){
		mainFrame.setVisibility(View.VISIBLE);
		hideView(captureView,mainFrame,new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            	captureView.setVisibility(View.GONE);
            	captureView.destroy();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
	}

	
	public boolean isOnlineGalleryShow(){
		return onlineView.getWaterFallView().isGalleryShow();
	}
	
	public void followScreen(String itemGuid){
		onlineView.getWaterFallView().followScreen(itemGuid);
	}
	
	private void startServices() {
		
		Intent commandService = new Intent(this, CommandService.class);
		startService(commandService);
		
		Intent intent = new Intent();
		intent.setAction("com.fix.service.CHECKWIFI");
		startService(intent);
	}
	
	protected void onResume() {
		super.onResume();
		if(currentTab == Constant.TAB_INTERACT){
			interactView.getInteractGallery().clearThumbView();
			interactView.resetGalleryView(true);
		}
	}
	
	/**
	 * 向gallery中添加一个entity
	 * @param galleryEntity
	 */
	public void setInteractView(InteractGalleryEntity galleryEntity){
		interactView.addInteractView(galleryEntity);
	}
	
	
	/**
	 * 显示拍照和 相册按钮
	 */
	public void showInteractCameraView(){
		if(interactCameraView.getVisibility() != View.VISIBLE){
			interactCameraLayout.getLayoutParams().height = this.getTabHost().getTabWidget().getChildAt(Constant.TAB_INTERACT).getHeight();
			interactCameraView.setVisibility(View.VISIBLE);
			interactCameraMaskView.setVisibility(View.VISIBLE);
			AnimatorSet set = new AnimatorSet();
	        set
	                .play(ObjectAnimator.ofFloat(interactCameraLayout, View.Y, Constant.screenHeight,
	                		Constant.screenHeight-interactCameraLayout.getLayoutParams().height-Constant.sbar))
	                		.with(ObjectAnimator.ofFloat(interactCameraMaskView, "alpha", 0f,1f));
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
	
	/**
	 * 隐藏拍照和 相册按钮
	 */
	public void hideInteractCameraView(){
		
		if(interactCameraView.getVisibility() == View.VISIBLE){
			interactCameraLayout.getLayoutParams().height = this.getTabHost().getTabWidget().getChildAt(Constant.TAB_INTERACT).getHeight();
			AnimatorSet set = new AnimatorSet();
	        set
	                .play(ObjectAnimator.ofFloat(interactCameraLayout, View.Y, Constant.screenHeight-interactCameraLayout.getLayoutParams().height-Constant.sbar,
	                		Constant.screenHeight))
	                		.with(ObjectAnimator.ofFloat(interactCameraMaskView, "alpha", 1f,0f));
	        set.setDuration(150);
	        set.setInterpolator(new DecelerateInterpolator());
	        set.addListener(new AnimatorListenerAdapter() {
	            @Override
	            public void onAnimationEnd(Animator animation) {
	            	interactCameraView.setVisibility(View.GONE);
	    			interactCameraMaskView.setVisibility(View.GONE);
	            }

	            @Override
	            public void onAnimationCancel(Animator animation) {
	            }
	        });
	        set.start();
		}
	}
	
	/**
	 * 进入绘画模式
	 */
	public void beginEdit(){
		if(interactView.isImageDownload()){
			interactView.enterEditView();
			interactView.enterEditStatus();
		}
	}
	
	public void hideTabs(){
		tabHost.getTabWidget().setVisibility(View.GONE);
	}
	
	public void showTabs(){
		tabHost.getTabWidget().setVisibility(View.VISIBLE);
	}
	
	public void answerSelectQuestion(){
		if(currentTab == Constant.TAB_INTERACT){
			interactView.enterSelectStatus();//getChatView().showSoftWare();
			interactView.hideCameraImageAnswerSelect();
		}
	}
	
	public void answerTabHighShow(){
		answertabview.setVisibility(View.VISIBLE);
		tabtitle.setVisibility(View.GONE);
		tabicon.setVisibility(View.GONE);
		this.getTabHost().getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.interact_answer_bg);
	}
	
	public void answerTabHighHide(){
		answertabview.setVisibility(View.GONE);
		tabtitle.setVisibility(View.VISIBLE);
		tabicon.setVisibility(View.VISIBLE);
		this.getTabHost().getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.tab_btn_bg);
	}
	
	public void drawAnswerImage(){
		interactView.enterDrawAnswer();
	}
	
	public void endDrawAnswer(){
		interactView.endEdit();
	}
	
	public void endSelectAnswer(){
		interactView.endSelect();
	}
}