package com.tvmining.wifiplus.image.loader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.DeleteFolderEntity;
import com.tvmining.sdk.entity.DeleteFolderStatusEntity;
import com.tvmining.sdk.entity.GroupTypeEntity;
import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.wifiplus.application.EmeetingApplication;
import com.tvmining.wifiplus.entity.PakgeTable;
import com.tvmining.wifiplus.entity.Permission;
import com.tvmining.wifiplus.thread.GetPackageSizeTask;
import com.tvmining.wifiplus.thread.ListDirectorysTask;
import com.tvmining.wifiplus.thread.PackageCompareTask;
import com.tvmining.wifiplus.thread.UnDownloadedPackageCompareTask;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.wifiplus.util.MessageUtil;
import com.tvmining.wifiplus.util.ResourceUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifiplus.view.ZoomImageView;
import com.tvmining.wifipluseq.R;

/**
 * SwipeListView的适配器:加载包的数据、滑动显示删除/下载等按钮
 * @author Administrator
 *
 */
public class ListImageAdapter extends BaseAdapter {
	private static final String TAG = "ListImageAdapter";
	private Context mContext;
	/**
	 * 加载图片
	 */
	private ListImageFetcher mImageFetcher;
	private SimpleDateFormat sdf;
	
	public ListImageAdapter(Context context) {
		mContext = context;
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public void setImageFetcher(ListImageFetcher mImageFetcher) {
		this.mImageFetcher = mImageFetcher;
	}

	@Override
	public int getCount() {
		if (Images.allPackName != null) {
			return Images.allPackName.length;
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		if (Images.allPackName != null) {
			return Images.allPackName[position];
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup container) {
		try {
			final PackInfoEntity entity = Images.allPackName[position];
			StateHolder holder = new StateHolder();
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(ResourceUtil.getResId(mContext,
						"child_online_swip", "layout"), null);

				holder.coverView = (RecyclingImageView) convertView
						.findViewById(ResourceUtil.getResId(mContext, "coverview", "id"));//存放包图片
				holder.packageNameView = (TextView) convertView
						.findViewById(ResourceUtil.getResId(mContext, "packagenametext", "id"));
				holder.packageDownload = (TextView) convertView
						.findViewById(ResourceUtil.getResId(mContext, "packagedownload", "id"));//下载按钮
				holder.packageDelete = (TextView) convertView
						.findViewById(ResourceUtil.getResId(mContext, "packagedelete", "id"));//删除按钮
				holder.packageSizeView = (TextView) convertView
						.findViewById(ResourceUtil.getResId(mContext, "packagesize", "id"));//包size标签
				holder.packageType = (TextView) convertView
						.findViewById(ResourceUtil.getResId(mContext, "packagetype", "id"));//包type标签
				
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.FILL_PARENT);
				params.width = (int)Utility.dpToPixel(Constant.PKG_ICON_WIDTH);
				params.height = (int)Utility.dpToPixel(Constant.PKG_ICON_HEIGHT);
				holder.coverView.setLayoutParams(params);
				
				convertView.setTag(holder);
			} else {
				holder = (StateHolder) convertView.getTag();
			}

			final StateHolder fholder = holder; 
			Handler pkgSizeHandler = new Handler() {
				public void handleMessage(Message msg) {

					String sizeStr = (String) msg.obj;
					fholder.packageSizeView.setText(sizeStr);
					super.handleMessage(msg);
				}
			};
			
			new GetPackageSizeTask(entity,pkgSizeHandler).execute();
			
			holder.packageNameView
					.setText(entity.packname);
			
			if(!"COMMUNICATION".equals(entity.pack_type)){
				holder.packageType.setText(entity.pack_type);
				holder.packageDelete.setText(mContext.getResources().getString(R.string.package_delete));
			 }else{
				 holder.packageType.setText(mContext.getResources().getString(R.string.communication_name));
				 holder.packageDelete.setText(mContext.getResources().getString(R.string.package_clear));
			 }
			
			holder.packageDownload.setOnClickListener(new View.OnClickListener() {//下载该包
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Constant.activity.getOnlineView().closeAllOpendItems();
					if(!"COMMUNICATION".equals(entity.pack_type)){
						if(Permission.PERMISSION_LOW.equals(Constant.user.getPermisssion().getLevel())){
							if(!GroupTypeEntity.ANONYMOUS.equals(entity.res_groupid)){
								new AlertDialog.Builder(Constant.activity)  
					            .setTitle(mContext.getResources().getString(R.string.package_download_title))
					            .setMessage(mContext.getResources().getString(R.string.package_download_content))
					            .setPositiveButton(mContext.getResources().getString(R.string.package_download_exit), null)
					            .show();
							}else{
								beginDownload(entity);
							}
						}else{
							beginDownload(entity);
						}
					}else{
						new AlertDialog.Builder(Constant.activity)  
			            .setTitle(mContext.getResources().getString(R.string.package_download_communication_title))
			            .setMessage(mContext.getResources().getString(R.string.package_download_communication_content))
			            .setPositiveButton(mContext.getResources().getString(R.string.package_download_communication_exit), null)
			            .show();
					}
				}
			});
			holder.packageDelete.setOnClickListener(new View.OnClickListener() {//删除该包
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Constant.activity.getOnlineView().closeAllOpendItems();
					if(Permission.PERMISSION_HIGH.equals(Constant.user.getPermisssion().getLevel())){
						String title = mContext.getResources().getString(R.string.package_remove_title);
						String desc = mContext.getResources().getString(R.string.package_remove_content);
						
						if(!Constant.user.getPermisssion().getLevel().equals(Permission.PERMISSION_HIGH)){
							if(Images.allPackName[position].pack_type.equals("COMMUNICATION")){
								title = mContext.getResources().getString(R.string.package_clear_title);
								desc = mContext.getResources().getString(R.string.package_clear_content);
							}
						}
						
						new AlertDialog.Builder(Constant.activity) 
						.setTitle(title)
						.setMessage(desc)
						.setPositiveButton(mContext.getResources().getString(R.string.package_remove_yes), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(final DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Constant.activity.showProgressDialog();
								new Thread(new Runnable(){
									@Override
									public void run() {
										// TODO Auto-generated method stub
										removePackageFromICE(Images.allPackName[position].packname);
										reLoadDirectory();
										dialog.dismiss();
									}
									
								}).start();
								
							}
						})
						.setNegativeButton(mContext.getResources().getString(R.string.package_remove_no), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						})
						.show();
					}else{
						new AlertDialog.Builder(Constant.activity)  
			            .setTitle(mContext.getResources().getString(R.string.package_delete_title))
			            .setMessage(mContext.getResources().getString(R.string.package_delete_content))
			            .setPositiveButton(mContext.getResources().getString(R.string.package_delete_exit), null)
			            .show();
					}
					
				}
			});

			if (entity.pack_type.equals("COMMUNICATION")) {
				Drawable drawable = mContext.getResources().getDrawable(
						ResourceUtil.getResId(mContext, "exchange", "drawable"));
				BitmapDrawable bd = (BitmapDrawable) drawable;
				Bitmap bitmap = Utility.loadHeadBitmap(bd.getBitmap(), Constant.activity.getPackageBitmapBoraer(),4,4);
				
				holder.coverView.setImageBitmap(bitmap);
			} else {
				ICESDK mySDK = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),
						Constant.iceConnectionInfo.getUserInfoEntity());
				String packThumbURL = mySDK.getPackThumbByPackInfo(
						(PackInfoEntity) Images.allPackName[position],
						Constant.ONLINE_WIDTH, Constant.ONLINE_HEIGHT);
				mImageFetcher.loadImage(packThumbURL, holder.coverView,
						Constant.FROM_GRID);//加载图片
				holder.coverView.setTag(packThumbURL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	/**
	 * 下载包
	 * @param entity
	 */
	private void beginDownload(final PackInfoEntity entity){
		new AlertDialog.Builder(Constant.activity) 
		.setTitle(mContext.getResources().getString(R.string.package_download_title))
		.setMessage(mContext.getResources().getString(R.string.package_download_confirm))
		.setPositiveButton(mContext.getResources().getString(R.string.package_download_yes), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(final DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Utility.startDownload(entity,mContext,sdf.format(new Date()));//下载资源包
				dialog.dismiss();
			}
		})
		.setNegativeButton(mContext.getResources().getString(R.string.package_download_no), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		})
		.show();
	}
	
	/**
	 * 删除包
	 * @param delPakName
	 */
	private void removePackageFromICE(String delPakName){
        if (delPakName.length() == 0) {
            Log.d("", "我要包名");
            return;
        }

        DeleteFolderEntity deleteFolder = new DeleteFolderEntity();
        deleteFolder.packname = delPakName;
        DeleteFolderStatusEntity result = null;
        try
        {
            result = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(), Constant.iceConnectionInfo.getUserInfoEntity()).deleteFolder(deleteFolder);
        }
        catch (Exception ee)
        {
            Log.d("删除文件夹出错:", ee.getMessage());
            result = new DeleteFolderStatusEntity("");
        }

        if (result.code == DeleteFolderStatusEntity.SUCC)
        {
            Log.d(delPakName, "包删除成功");
        }
        else {
            Log.d(delPakName, "包删除失败");
        }
	}
	
	public void reLoadDirectory(){
		//当包数量变动时,在这里执行刷新View操作
		new ListDirectorysTask(mContext,null).execute();
	}
	
	
	class StateHolder {
		RecyclingImageView coverView;
		TextView packageNameView;
		TextView packageDownload;
		TextView packageDelete;
		TextView packageSizeView;
		TextView packageType;

		int position;
	}
}
