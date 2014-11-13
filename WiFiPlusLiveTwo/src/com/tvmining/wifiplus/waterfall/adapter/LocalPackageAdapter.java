package com.tvmining.wifiplus.waterfall.adapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.sdk.helper.HttpHelper;
import com.tvmining.wifiplus.entity.DownloadProgressEntity;
import com.tvmining.wifiplus.entity.ICETable;
import com.tvmining.wifiplus.entity.LocalGroup;
import com.tvmining.wifiplus.entity.PakgeTable;
import com.tvmining.wifiplus.entity.Permission;
import com.tvmining.wifiplus.image.loader.Images;
import com.tvmining.wifiplus.image.loader.ListImageFetcher;
import com.tvmining.wifiplus.thread.GetPackageSizeTask;
import com.tvmining.wifiplus.thread.PakgeDeleteTask;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.MessageUtil;
import com.tvmining.wifiplus.util.ResourceUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifiplus.view.ZoomImageView;
import com.tvmining.wifipluseq.R;

public class LocalPackageAdapter extends BaseAdapter {

	
	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<LocalGroup> TagList;
	private ArrayList<Object> DataList;
	private ListImageFetcher mImageFetcher;
	
	
	public ListImageFetcher getmImageFetcher() {
		return mImageFetcher;
	}

	public void setmImageFetcher(ListImageFetcher mImageFetcher) {
		this.mImageFetcher = mImageFetcher;
	}
	
	public ArrayList<LocalGroup> getGroupList() {
		return TagList;
	}

	public void setGroupList(ArrayList<LocalGroup> groupList) {
		this.TagList = groupList;
	}

	public ArrayList<Object> getChildList() {
		return DataList;
	}

	public void setChildList(ArrayList<Object> childList) {
		this.DataList = childList;
	}
	
	public LocalPackageAdapter(Context mcontext){
		this.mContext = mcontext;
		inflater = LayoutInflater.from(this.mContext);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return DataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return DataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
        if (TagList.contains(getItem(position))) {
        	convertView = getGroupView(position, convertView, parent);
        } else {
        	convertView = getChildView(position, convertView, parent);
        }
        
        return convertView;
	}


	public View getGroupView(int position, View convertView, ViewGroup parent) {
		GroupHolder groupHolder = null;
		groupHolder = new GroupHolder();
		convertView = inflater.inflate(ResourceUtil.getResId(mContext, "local_group", "layout"), null);
		groupHolder.textView = (TextView) convertView
				.findViewById(ResourceUtil.getResId(mContext, "group", "id"));
		groupHolder.imageView = (ImageView) convertView
				.findViewById(ResourceUtil.getResId(mContext, "image", "id"));
		groupHolder.textView.setTextSize(15);
		convertView.setTag(groupHolder);
		
		LocalGroup localGroup = (LocalGroup)getItem(position);
		
		groupHolder.textView.setText(localGroup.getTitle()+"("+localGroup.getIceDate()+")");
		
		groupHolder.imageView.setImageResource(ResourceUtil.getResId(mContext, "collapse", "drawable"));
		return convertView;
	}
	
	
	public View getChildView(final int position, View convertView, ViewGroup parent) {
		
		ChildHolder childHolder = null;
		final PakgeTable pakgeTable = (PakgeTable)getItem(position);
		
		childHolder = new ChildHolder();
		final ChildHolder tempholder = childHolder;
		convertView = inflater.inflate(ResourceUtil.getResId(mContext, "child_local_swip", "layout"), null);
		
		childHolder.textName = (TextView) convertView.findViewById(ResourceUtil.getResId(mContext, "localpackagename", "id"));
		childHolder.localPackageView = (ZoomImageView) convertView.findViewById(ResourceUtil.getResId(mContext, "localpackageview", "id"));
		childHolder.localplayimage = (ImageView) convertView.findViewById(ResourceUtil.getResId(mContext, "localplayimage", "id"));
		childHolder.progressBar = (ProgressBar) convertView.findViewById(ResourceUtil.getResId(mContext, "localprogressbar", "id"));
		
		childHolder.localPackageSize = (TextView) convertView.findViewById(ResourceUtil.getResId(mContext, "localpackagesize", "id"));
		childHolder.localPackageType = (TextView) convertView.findViewById(ResourceUtil.getResId(mContext, "localpackagetype", "id"));
		childHolder.localPackageUpload = (TextView) convertView.findViewById(ResourceUtil.getResId(mContext, "localpackageupload", "id"));
		childHolder.localPackageDelete = (TextView) convertView.findViewById(ResourceUtil.getResId(mContext, "localpackagedelete", "id"));
		childHolder.localPackageShare = (TextView) convertView.findViewById(ResourceUtil.getResId(mContext, "localpackageshare", "id"));
		childHolder.localDownloadPackageDelete = (TextView) convertView.findViewById(ResourceUtil.getResId(mContext, "localdownloadpackagedelete", "id"));
		childHolder.localUploadPackageDelete = (TextView) convertView.findViewById(ResourceUtil.getResId(mContext, "localuploadpackagedelete", "id"));
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.FILL_PARENT);
		params.width = (int)Utility.dpToPixel(Constant.PKG_ICON_WIDTH);
		params.height = (int)Utility.dpToPixel(Constant.PKG_ICON_HEIGHT);
		childHolder.localPackageView.setLayoutParams(params);
		
		convertView.setTag(childHolder);
		
		String sizeStr = "";
		if(pakgeTable.getPakgeExtentOne() != null && !("".equals(pakgeTable.getPakgeExtentOne()))){
			long size = Long.parseLong(pakgeTable.getPakgeExtentOne());
			if(size < 1024){
				sizeStr = size+" B";
			}else if(size < 1024 * 1024){
				sizeStr  = size / 1024 + " KB";
			}else{
				sizeStr = size / (1024 * 1024) + " MB";
			}
		}
		childHolder.localPackageSize.setText(sizeStr);
		childHolder.localPackageType.setText(pakgeTable.getPakgeType());
		
		boolean isInDownload = false;
		if(!Constant.downloadingMap.containsKey(pakgeTable.getPakgeGuid())){
			if(pakgeTable.getStatus() != 2){
				childHolder.progressBar.setProgress((int)pakgeTable.getProgress());
				showDownloadViews(childHolder);
				if(Constant.downloadThread.downloadingPackageName!= null){
					if(Constant.downloadThread.downloadingPackageName.equals(pakgeTable.getPakgeName())){
						Log.d("6666666666666666", "111");
						childHolder.localplayimage.setImageResource(R.drawable.interact_btn);
					}else{
						Log.d("6666666666666666", "222");
						childHolder.localplayimage.setImageResource(R.drawable.play);
					}
				}
			}else{
				showUnDownloadViews(childHolder);
			}
			
		}else{
			DownloadProgressEntity progressEntity = (DownloadProgressEntity) Constant.downloadingMap.get(pakgeTable.getPakgeGuid());
			
			if(progressEntity != null){
				if(progressEntity.getPakgeName() == pakgeTable.getPakgeName()){
					isInDownload = true;
					if(progressEntity.getPakgeIndex() == pakgeTable.getPakgeIndex()){
						childHolder.progressBar.setProgress((int)pakgeTable.getProgress());
					}else{
						childHolder.progressBar.setProgress((int)progressEntity.getProgress());
					}
					showDownloadViews(childHolder);
					if(Constant.downloadThread.downloadingPackageName!= null){
						if(Constant.downloadThread.downloadingPackageName.equals(pakgeTable.getPakgeName())){
							childHolder.localplayimage.setImageResource(R.drawable.interact_btn);
						}else{
							childHolder.localplayimage.setImageResource(R.drawable.play);
						}
					}
				}else{
					showUnDownloadViews(childHolder);
				}
			}
		}
		
		if(!isInDownload){
			if(!Constant.uploadingMap.containsKey(pakgeTable.getPakgeGuid())){
				showUnDownloadViews(childHolder);
			}else{
				DownloadProgressEntity progressEntity = (DownloadProgressEntity) Constant.uploadingMap.get(pakgeTable.getPakgeGuid());
				if(progressEntity != null){
					if(progressEntity.getPakgeIndex() == pakgeTable.getPakgeIndex()){
						childHolder.progressBar.setProgress((int)pakgeTable.getProgress());
					}else{
						childHolder.progressBar.setProgress((int)progressEntity.getProgress());
					}
				}
				showuploadViews(childHolder);
			}
		}
		
		final View iconvertView = convertView;
		childHolder.localPackageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete
            	Constant.activity.getLocalView().closeAllOpendItems();
            	deleteCell(iconvertView, position,pakgeTable,false);
            }
        });
		
		childHolder.localDownloadPackageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete
            	Constant.activity.getLocalView().closeAllOpendItems();
            	new AlertDialog.Builder(Constant.activity) 
    			.setTitle(mContext.getResources().getString(R.string.download_package_remove_confirm))
    			.setMessage(mContext.getResources().getString(R.string.download_package_remove_content))
    			.setPositiveButton(mContext.getResources().getString(R.string.download_package_remove_yes), new DialogInterface.OnClickListener() {
    				
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					// TODO Auto-generated method stub
    					dialog.dismiss();
    					deleteCell(iconvertView, position,pakgeTable,true);
    				}
    			})
    			.setNegativeButton(mContext.getResources().getString(R.string.download_package_remove_no), new DialogInterface.OnClickListener() {
    				
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					// TODO Auto-generated method stub
    					dialog.dismiss();
    				}
    			})
    			.show();
            }
        });
		
		childHolder.localPackageUpload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Constant.activity.getLocalView().closeAllOpendItems();
               	if(!Constant.uploadOrDownload.equals(Constant.DO_DOWNLOAD)){
            		if(Constant.LOGIN_SUCCESS.equals(Constant.loginStatus)){
                		if(!Constant.uploadQueue.contains(pakgeTable) && 
                				!pakgeTable.getPakgeName().equals(Constant.uploadThread.uploadingPackageName)){
                	        if (Images.allPackName==null||Images.allPackName.length == 0){
                	        	Log.e("PackageAdapter", "取得包失败");
                	        }else {
                	        	
                	        	Constant.activity.hideTabs();
                	        	
                	        	pakgeTable.setTempPakgeName(pakgeTable.getPakgeName());
                	        	//无论是否有重名包,都显示修改包赋予权限界面
                	        	View permissionView = null;
                	        	
                	        	if(Permission.PERMISSION_HIGH.equals(Constant.user.getPermisssion().getLevel())){
                	        		permissionView = Constant.activity.getLayoutInflater().inflate(R.layout.setpermission,null);
                	        		if(permissionView != null){
                	        			final TextView red = (TextView)permissionView.findViewById(R.id.red);
            							final TextView yellow = (TextView)permissionView.findViewById(R.id.yellow);
            							final TextView green = (TextView)permissionView.findViewById(R.id.green);
                        	        	
            							final ImageView line1 = (ImageView)permissionView.findViewById(R.id.selectedline);
            							final ImageView line2 = (ImageView)permissionView.findViewById(R.id.selectedline2);
            							final ImageView line3 = (ImageView)permissionView.findViewById(R.id.selectedline3);
            							
            							red.setOnClickListener(new View.OnClickListener() {								
            								@Override
            								public void onClick(View v) {
            									//
            									Constant.user.getPermisssion().dealLevel();//(Permission.PERMISSION_HIGH);
            									red.setTextColor(Color.parseColor("#157EFB"));
            									yellow.setTextColor(Color.DKGRAY);
            									green.setTextColor(Color.DKGRAY);
            									line1.setVisibility(View.VISIBLE);
            									line2.setVisibility(View.INVISIBLE);
            									line3.setVisibility(View.INVISIBLE);
            								}
            							});
            							yellow.setOnClickListener(new View.OnClickListener() {								
            								@Override
            								public void onClick(View v) {
            									//
            									Constant.user.getPermisssion().dealLevel();//(Permission.PERMISSION_MIDDLE);
            									red.setTextColor(Color.DKGRAY);
            									yellow.setTextColor(Color.parseColor("#157EFB"));
            									green.setTextColor(Color.DKGRAY);
            									line1.setVisibility(View.INVISIBLE);
            									line2.setVisibility(View.VISIBLE);
            									line3.setVisibility(View.INVISIBLE);
            								}
            							});
            							green.setOnClickListener(new View.OnClickListener() {								
            								@Override
            								public void onClick(View v) {
            									//
            									Constant.user.getPermisssion().dealLevel();//(Permission.PERMISSION_LOW);
            									red.setTextColor(Color.DKGRAY);
            									yellow.setTextColor(Color.DKGRAY);
            									green.setTextColor(Color.parseColor("#157EFB"));
            									line1.setVisibility(View.INVISIBLE);
            									line2.setVisibility(View.INVISIBLE);
            									line3.setVisibility(View.VISIBLE);
            								}
            							});
                	        		}
                	        		
                	        	}else if(Permission.PERMISSION_MIDDLE.equals(Constant.user.getPermisssion().getLevel())){
                	        		permissionView = Constant.activity.getLayoutInflater().inflate(R.layout.setpermission2,null);
                	        		if(permissionView != null){
                	        			final TextView yellow = (TextView)permissionView.findViewById(R.id.yellow);
            							final TextView green = (TextView)permissionView.findViewById(R.id.green);              	        	
            							
            							final ImageView line2 = (ImageView)permissionView.findViewById(R.id.selectedline2);
            							final ImageView line3 = (ImageView)permissionView.findViewById(R.id.selectedline3);
            							
            							yellow.setOnClickListener(new View.OnClickListener() {								
            								@Override
            								public void onClick(View v) {
            									//
            									Constant.user.getPermisssion().dealLevel();//(Permission.PERMISSION_MIDDLE);
            									yellow.setTextColor(Color.parseColor("#157EFB"));
            									green.setTextColor(Color.DKGRAY);
            									line2.setVisibility(View.VISIBLE);
            									line3.setVisibility(View.INVISIBLE);
            								}
            							});
            							green.setOnClickListener(new View.OnClickListener() {								
            								@Override
            								public void onClick(View v) {
            									//
            									Constant.user.getPermisssion().dealLevel();//(Permission.PERMISSION_LOW);
            									yellow.setTextColor(Color.DKGRAY);
            									green.setTextColor(Color.parseColor("#157EFB"));
            									line2.setVisibility(View.INVISIBLE);
            									line3.setVisibility(View.VISIBLE);
            								}
            							});
            							
                	        		}
                	        	}
                	        			
    							final PopupWindow perWindow =  new PopupWindow(permissionView,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,true);
    							
    							ColorDrawable cd = new ColorDrawable(Color.parseColor("#b0000000"));
    							perWindow.setBackgroundDrawable(cd);
    							perWindow.setAnimationStyle(R.style.popupAnimationChange);
    							perWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    							perWindow.showAtLocation(permissionView, Gravity.CENTER,0,0);
    							perWindow.update();
    							
    							Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
    							alphaAnimation.setFillAfter(true);
    							alphaAnimation.setDuration(500);
    							permissionView.startAnimation(alphaAnimation);
    							
    							Button confirm = (Button) permissionView.findViewById(R.id.confirm);
                	        	Button cancel = (Button) permissionView.findViewById(R.id.cancel);
    							
    							
                	        	final EditText pkgText = (EditText)permissionView.findViewById(R.id.rename);
                	        	
                	        	pkgText.setText(pakgeTable.getPakgeName());//回填包名
                	        	pkgText.setSelection(pakgeTable.getPakgeName().length());
    							
                	        	Timer timer = new Timer();
                	        	timer.schedule(new TimerTask() {
                	        		@Override
                	        		public void run() {
                	        			InputMethodManager m = (InputMethodManager) pkgText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                	        			m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                	        		}
                	        	}, 200);
                	        	
                	        	ImageView clear = (ImageView)permissionView.findViewById(R.id.clear);
                	        	clear.setOnClickListener(new View.OnClickListener() {
    								@Override
    								public void onClick(View v) {
    									pkgText.setText("");
    								}
    							});
                	        	final PackInfoEntity[] tempAllPackName = Images.allPackName;
                	        	confirm.setOnClickListener(new View.OnClickListener() {								
    								@Override
    								public void onClick(View v) {
    									String rePackage = pkgText.getText().toString();
    									if(rePackage != null && !("".equals(rePackage))){
            	    						pakgeTable.setTempPakgeName(rePackage);
            	    						if(!isExist(tempAllPackName,pakgeTable.getTempPakgeName())){
            	    							Constant.uploadOrDownload = Constant.DO_UPLOAD;
            	    							synchronized (Constant.uploadQueue) {        	    								
            	    								Constant.uploadQueue.add(pakgeTable);
                                					Constant.uploadQueue.notify();
            	    	        				}
            	    							
            	    							showuploadViews(tempholder);
                	    						perWindow.dismiss();
                	    						Constant.activity.showTabs();
            	    						}else{
            	    							Toast.makeText(mContext,"包名已存在，请修改后再试!",Toast.LENGTH_LONG).show();
            	    						}
            	    					}else{
            	    						Toast.makeText(mContext,"上传的包名不能为空!",Toast.LENGTH_LONG).show();
            	    					}									
    								}
    							});
    							cancel.setOnClickListener(new View.OnClickListener() {
    								@Override
    								public void onClick(View v) {
    									perWindow.dismiss();
    									Constant.activity.showTabs();
    								}
    							});
                	        }
                		}else{
                			MessageUtil.toastInfo(mContext,mContext.getResources().getString(R.string.package_uploading));
                		}
                	}else {
                		MessageUtil.toastInfo(mContext,mContext.getResources().getString(R.string.nologin));
					}
	        	}else{
	        		MessageUtil.toastInfo(mContext,mContext.getResources().getString(R.string.do_download));
	        	}
			}
		});
		
		childHolder.localUploadPackageDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				HttpHelper.isStopUploading = true;
				Constant.activity.getLocalView().closeAllOpendItems();
				Constant.uploadingMap.remove(pakgeTable.getPakgeGuid());
				showUnDownloadViews(tempholder);
			}
		});
		
		childHolder.localPackageShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Constant.activity.getLocalView().closeAllOpendItems();
				Message msg = new Message();
				msg.obj = pakgeTable;
				msg.arg1 = position;
				msg.what = Constant.HANDLER_LOCAL_SHARE_CREATE_QRCODE;
				Constant.activity.getHandler().sendMessage(msg);
			}
		});
		
		childHolder.iceIndex = Integer.parseInt(pakgeTable.getIceIndex());
		childHolder.pkgIndex = pakgeTable.getPakgeIndex();
		
		childHolder.textName.setText(pakgeTable.getPakgeName());
		
		mImageFetcher.loadImage(pakgeTable.getPakgepath(), childHolder.localPackageView,
				Constant.FROM_LOCAL);
		
		return convertView;
	}
	
	
	
	
	public synchronized void pauseDownload(PakgeTable pakgeTable){
		if(!Constant.queuePause){
			Constant.uploadOrDownload = Constant.DO_DOWNLOAD;
			ICETable iceTable = Constant.dbConnection.queryICE(Integer.parseInt(pakgeTable.getIceIndex()));
			for(int l=0;l<Images.allPackName.length;l++){
				final PackInfoEntity entity = Images.allPackName[l];
				
				if(iceTable.getIceName().equals(Constant.iceConnectionInfo.getLoginICE().connectICEName) && pakgeTable.getPakgeName().equals(entity.packname)){
					if(Constant.downloadingMap.containsKey(entity.thumb_guid)){
						if(!entity.isPause){
							Constant.activity.getLocalView().setDownloadViewFlag(pakgeTable, true);
							pakgeTable.setStatus(Constant.DOWNLOAD_PAUSE);
							Constant.downloadThread.savePauseData(entity);
						}else{
							pakgeTable.setStatus(Constant.DOWNLOAD_WAIT);
							Constant.activity.getLocalView().setDownloadViewFlag(pakgeTable, false);
							entity.isPause = false;
							entity.packageUpdate = false;
							entity.tempPackName = pakgeTable.getPakgeName();
							entity.pkgIndex = pakgeTable.getPakgeIndex();
							entity.iceIndex = Long.parseLong(pakgeTable.getIceIndex());
							
							Handler pkgSizeHandler = new Handler() {
								public void handleMessage(Message msg) {
									if(entity.packageSize > 0){
										Constant.downloadThread.resumePauseData(entity);
										super.handleMessage(msg);
									}else{
										MessageUtil.toastInfo(mContext,mContext.getResources().getString(R.string.package_size_zero));
									}
								}
							};
							
							new GetPackageSizeTask(entity,pkgSizeHandler).execute();
						}
					}
					break;
				}
			}
		}
	}
	
	private void showUnDownloadViews(ChildHolder childHolder){
		childHolder.progressBar.setVisibility(View.GONE);
		childHolder.localPackageSize.setVisibility(View.VISIBLE);
		childHolder.localPackageType.setVisibility(View.VISIBLE);
		childHolder.localPackageUpload.setVisibility(View.VISIBLE);
		childHolder.localPackageDelete.setVisibility(View.VISIBLE);
		childHolder.localPackageShare.setVisibility(View.VISIBLE);
		childHolder.localDownloadPackageDelete.setVisibility(View.GONE);
		childHolder.localUploadPackageDelete.setVisibility(View.GONE);
	}
	
	private void showDownloadViews(ChildHolder childHolder){
		childHolder.progressBar.setVisibility(View.VISIBLE);
		childHolder.localPackageSize.setVisibility(View.GONE);
		childHolder.localPackageType.setVisibility(View.GONE);
		childHolder.localPackageUpload.setVisibility(View.GONE);
		childHolder.localPackageDelete.setVisibility(View.GONE);
		childHolder.localPackageShare.setVisibility(View.GONE);
		childHolder.localDownloadPackageDelete.setVisibility(View.VISIBLE);
		childHolder.localUploadPackageDelete.setVisibility(View.GONE);
		childHolder.localplayimage.setVisibility(View.VISIBLE);
	}

	private void showuploadViews(ChildHolder childHolder){
		childHolder.progressBar.setVisibility(View.VISIBLE);
		childHolder.localPackageSize.setVisibility(View.GONE);
		childHolder.localPackageType.setVisibility(View.GONE);
		childHolder.localPackageUpload.setVisibility(View.GONE);
		childHolder.localPackageDelete.setVisibility(View.GONE);
		childHolder.localPackageShare.setVisibility(View.GONE);
		childHolder.localDownloadPackageDelete.setVisibility(View.GONE);
		childHolder.localUploadPackageDelete.setVisibility(View.VISIBLE);
		childHolder.localplayimage.setVisibility(View.VISIBLE);
	}
	
	private void deleteCell(final View v, final int index,final PakgeTable item,final boolean isDownload) {
		AnimationListener al = new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				PakgeDeleteTask delTask = new PakgeDeleteTask(item,LocalPackageAdapter.this,isDownload);
                delTask.execute();
			}
			@Override public void onAnimationRepeat(Animation animation) {}
			@Override public void onAnimationStart(Animation animation) {}
		};

		collapse(v, al);
	}

	private void collapse(final View v, AnimationListener al) {
		final int initialHeight = v.getMeasuredHeight();

		Animation anim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				}
				else {
					v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}
		};

		if (al!=null) {
			anim.setAnimationListener(al);
			
		}
		anim.setFillAfter(false);
		anim.setDuration(200);
		v.startAnimation(anim);
	}
	
	
	
	class GroupHolder {
		TextView textView;
		ImageView imageView;
	}
	
	public class ChildHolder {
		public TextView textName;
		public ZoomImageView localPackageView;
		public ImageView localplayimage;
		public ProgressBar progressBar;
		public TextView localPackageSize;
		public TextView localPackageType;
		public TextView localPackageUpload;
		public TextView localPackageDelete;
		public TextView localPackageShare;
		public TextView localDownloadPackageDelete;
		public TextView localUploadPackageDelete;
		
		int iceIndex;
		int pkgIndex;
		
		public int getIceIndex() {
			return iceIndex;
		}
		public int getPkgIndex() {
			return pkgIndex;
		}
	}
	
	private boolean isExist(PackInfoEntity[] allPackName,String pakgeName){
    	boolean isExist = false;
    	for(int i=0;i<allPackName.length;i++){
    		PackInfoEntity entity = allPackName[i];
    		if(entity.packname.equals(pakgeName)){
    			isExist = true;
    			break;
    		}
    	}
    	return isExist;
    }


	public boolean isTouch(int position) {
		// TODO Auto-generated method stub
		if (TagList.contains(getItem(position))) {
        	return false;
        } else {
        	return true;
        }
	}
	
	
}
