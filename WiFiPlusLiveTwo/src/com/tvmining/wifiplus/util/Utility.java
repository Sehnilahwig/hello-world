package com.tvmining.wifiplus.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.http.conn.util.InetAddressUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.MyNeighboursEntity;
import com.tvmining.sdk.entity.NeighbourEntity;
import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;
import com.tvmining.sdk.entity.UploadUserInfoEntity;
import com.tvmining.sdk.entity.UserInfoEntity;
import com.tvmining.sdk.entity.UserTypeEntity;
import com.tvmining.wifiplus.application.EmeetingApplication;
import com.tvmining.wifiplus.entity.ICETable;
import com.tvmining.wifiplus.entity.InteractGalleryEntity;
import com.tvmining.wifiplus.entity.ItemTable;
import com.tvmining.wifiplus.entity.PakgeTable;
import com.tvmining.wifiplus.thread.LoginAsyncTask;
import com.tvmining.wifiplus.thread.PackageCompareTask;
import com.tvmining.wifiplus.thread.UnDownloadedPackageCompareTask;
import com.tvmining.wifiplus.thread.UploadPersonInfoTask;
import com.tvmining.wifipluseq.R;


public class Utility {  
    public static int setListViewHeightBasedOnChildren(ListView listView) {  
        ListAdapter listAdapter = listView.getAdapter();   
        if (listAdapter == null) {  
            // pre-condition  
            return 0;  
        }  
  
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
        listView.setLayoutParams(params);  
        
        return params.height;
    }  
    
    public static void remoteVibrator(Context mContext){
		if(Constant.isVibratorOpen){
			Vibrator vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);      
			vibrator.vibrate(100);
		}
	}
    
  	
  //获取缺省屏幕设备
  	public static void refreshScreen2(Context context,Object obj) {
  		//分析查找屏幕的结果
  		MyNeighboursEntity neighbour = (MyNeighboursEntity)obj;
  		
  		if(neighbour==null){
  		}else{
  			NeighbourEntity[] array = neighbour.canSendArray;
  			
  			int deviceCount = 0;//可匹配设备数量
  			for(NeighbourEntity nei:array){
  				if(nei.type.equals(UserTypeEntity.DRIVCE)){
  					deviceCount+=1;
  				}
  			}
  			if(deviceCount==0){
  			}else{
  				//查询到所有可匹配的屏幕列表赋值给allScreenList
  				if(Constant.matchScreen==null || (Constant.matchScreen != null && Constant.matchScreen.size() == 0)){
  					Constant.matchScreen.clear();
  					Constant.matchScreen = new ArrayList<NeighbourEntity>();
  					for(int i=0;i<neighbour.canSendArray.length;i++){
  						if(neighbour.canSendArray[i].type.equals(UserTypeEntity.DRIVCE)){
  							Constant.matchScreen.add(neighbour.canSendArray[i]);
  							break;
  						}
  					}
  				}
  			}
  		}
      }
  	
  	public static float dpToPixel(float paramFloat) {
		return paramFloat * Constant.SCALE;
	}

	public static float pixelToDp(float paramInt){
		return paramInt / Constant.SCALE;
	}
	
	public static void removeDownloadData(String packageGuid){
		Constant.downloadingMap.remove(packageGuid);
	}
	
	/**
	 * 删除资源
	 * @param entity
	 * @return
	 */
	public static boolean removePackage(PakgeTable entity){
		boolean isSuc = true;
		try{
			Constant.dbConnection.removePakgeRecord(entity);
			Constant.dbConnection.removeItemRecords(entity);
			
			String filePath = entity.getPakgepath().substring(0,entity.getPakgepath().lastIndexOf("/"));
			
			DelFile.delete(new File(filePath));
			
			Vector vector = Constant.dbConnection.getPkgsByICEIndex(Integer.parseInt(entity.getIceIndex()));
			
			if(vector == null || vector.size() == 0){
				ICETable iceTable = Constant.dbConnection.queryICE(Integer.parseInt(entity.getIceIndex()));
				if(iceTable != null){
					filePath = iceTable.getIceSmallIconPath();
					DelFile.delete(new File(filePath.substring(0,filePath.lastIndexOf("/"))));
				}
				Constant.dbConnection.removeIceRecord(entity);
			}
		}catch(Exception e){
			isSuc = false;
		}
		
		return isSuc;
	}
	
	public static void interruptAll(){
		try {
			Thread.sleep(200);
			if(Constant.receiveCmdThread != null){
				Constant.receiveCmdThread.interrupt();
			}
			if(Constant.iceConnectionInfo.getLoginICE() != null && Constant.iceConnectionInfo.getUserInfoEntity() != null){
				ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(), Constant.iceConnectionInfo.getUserInfoEntity()).isOver();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void tryLogin(Context mContext,String passwordValue,Object obj){
		if(Constant.iceConnectionInfo.getLoginICE() != null){
			UserInfoEntity userInfoEntity = new UserInfoEntity(UserTypeEntity.USER);
			userInfoEntity.id = Constant.tvmId;//���豸ID��ΪΨһ��
			userInfoEntity.name = "WifiPlus_Android";
            Constant.iceConnectionInfo.getLoginICE().password = passwordValue;//����Ǵ�ҳ�����������
            
		    new LoginAsyncTask(obj,mContext).execute(userInfoEntity);
		}
	}
	
	public static String getLocalIpAddress() {
		try {
			// 遍历网络接口
			Enumeration<NetworkInterface> infos = NetworkInterface
			.getNetworkInterfaces();
			while (infos.hasMoreElements()) {
				// 获取网络接口
				NetworkInterface niFace = infos.nextElement();
				Enumeration<InetAddress> enumIpAddr = niFace.getInetAddresses();
				while (enumIpAddr.hasMoreElements()) {
					InetAddress mInetAddress = enumIpAddr.nextElement();
					// 所获取的网络地址不是127.0.0.1时返回得得到的IP
					if (!mInetAddress.isLoopbackAddress()
					&& InetAddressUtils.isIPv4Address(mInetAddress
					.getHostAddress())) {
						return mInetAddress.getHostAddress().toString();
					}
				}

			}

		} catch (SocketException e) {

		}

		return null;
	}
	
	/**
	 * 用字符串生成二维码
	 * @param str
	 * @author zhouzhe@lenovo-cw.com
	 * @return
	 * @throws WriterException
	 */
	public static Bitmap create2DCode(String str) throws WriterException {
		//生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, 300, 300);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		//二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if(matrix.get(x, y)){
					pixels[y * width + x] = 0xff000000;
				}
				
			}
		}
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		//通过像素数组生成bitmap,具体参考api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	
	public static void createJsonFile(PakgeTable pakgeTable){
		JSONObject jsonObjs = new JSONObject();
		try {
			
			Vector itemVector = Constant.dbConnection.getItemsByPakgeIndex(pakgeTable.getPakgeIndex());
			
			String pkgPath = pakgeTable.getPakgepath();
			String filePath = pkgPath.substring(0,pkgPath.lastIndexOf(pakgeTable.getPakgeName())+pakgeTable.getPakgeName().length())+File.separator+"share.json";
			
			JSONObject pakgeJsonObj  = createPakgeJson(pakgeTable);
			JSONObject fromJsonObj  = createFromJson();
			
			JSONArray itemJsonArray  = new JSONArray();
			for(int i=0;i<itemVector.size();i++){
				ItemTable itemTable = (ItemTable) itemVector.get(i);
				JSONObject itemJsonObj  = createItemJson(itemTable);
				itemJsonArray.put(itemJsonObj);
			}
			
			jsonObjs.put("package", pakgeJsonObj);
			jsonObjs.put("from", fromJsonObj);
			jsonObjs.put("itemfile", itemJsonArray);
			
			writeToJsonFile(jsonObjs, filePath);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
	}
	
	public static void writeToJsonFile(JSONObject jsonObjs,String filePath){
		  
		try {
			File file = new File(filePath);
			if(file != null && file.exists()){
				file.delete();
			}
			FileWriter fw = new FileWriter(filePath);
			PrintWriter out = new PrintWriter(fw);
		    out.write(jsonObjs.toString());
		    out.println();
		    fw.close();
		    out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	public static JSONObject createFromJson(){
		JSONObject fromJsonObj  = new JSONObject();
		try {
			fromJsonObj.put("from_username", "like");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
		
		return fromJsonObj;
	}
	
	public static JSONObject createItemJson(ItemTable itemTable){
		JSONObject itemJsonObj  = new JSONObject();
		try {
			itemJsonObj.put("item_guid", itemTable.getItemGuid());
			itemJsonObj.put("item_owner", itemTable.getItemOwnerTvmID());
			itemJsonObj.put("item_height", itemTable.getItemHeight());
			itemJsonObj.put("item_width", itemTable.getItemWidth());
			itemJsonObj.put("item_type", itemTable.getItemType());
			itemJsonObj.put("item_tag", itemTable.getItemTag());
			String itemFilePath = "dir"+itemTable.getItemFilePath();
//			itemFilePath = new String(itemFilePath.getBytes("UTF-8"),"ISO-8859-1");
			itemJsonObj.put("item_filepath", itemFilePath);
			itemJsonObj.put("item_title", itemTable.getItemTitle());
			itemJsonObj.put("item_groupid", itemTable.getItemGroupID());
			
			if("VIDEO".equals(itemTable.getItemType())){
				String fileNameNoStuffix = itemFilePath.substring(0,itemFilePath.lastIndexOf("."))+"."+Constant.VIDEO_SUFFIX;
				itemJsonObj.put("item_iconpath", fileNameNoStuffix);
			}else{
				itemJsonObj.put("item_iconpath", "");
			}
			
			itemJsonObj.put("item_filename", itemTable.getItemFileName());
			itemJsonObj.put("item_description", itemTable.getItemDescription());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		
		return itemJsonObj;
	}
	
	public static JSONObject createPakgeJson(PakgeTable pakgeTable){
		JSONObject pakgeJsonObj  = new JSONObject();
		try {
			pakgeJsonObj.put("package_itemcount", pakgeTable.getItemCount());
			pakgeJsonObj.put("package_guid", pakgeTable.getPakgeGuid());
			pakgeJsonObj.put("package_name", pakgeTable.getPakgeName());
			pakgeJsonObj.put("package_groupid", pakgeTable.getPakgeGroupID());
			pakgeJsonObj.put("package_id", pakgeTable.getPakgeID());
			String pkgIconPath = "dir"+pakgeTable.getPakgepath();
//			pkgIconPath = new String(pkgIconPath.getBytes("UTF-8"),"ISO-8859-1");
			pakgeJsonObj.put("package_iconpath", pkgIconPath);
			pakgeJsonObj.put("package_type", pakgeTable.getPakgeType());
			pakgeJsonObj.put("package_owner", pakgeTable.getPakgeOwnerTvmID());
			pakgeJsonObj.put("package_size", pakgeTable.getPakgeExtentOne());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		
		return pakgeJsonObj;
	}
	
	public static void parseQrCode(String jsonStr){
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			
			final String sharePackagePath = jsonObject.getString("share_package_path");
			final String action = jsonObject.getString("action");
			final String ipport = jsonObject.getString("ipport");
			
			try {
				final String url = ipport + File.separator + URLEncoder.encode(sharePackagePath,"UTF-8") + File.separator + "share.json";
				new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						String jsonInfo = ImageUtil.readNetFile(url);
						Log.d("Utility", jsonInfo);
						
						try {
							JSONObject infoObject = new JSONObject(jsonInfo);
							
							JSONObject pakgeJsonObject = infoObject.getJSONObject("package");
							JSONObject fromJsonObject = infoObject.getJSONObject("from");
							JSONArray itemJsonArray = infoObject.getJSONArray("itemfile");
							
							PackInfoEntity onePackInfo = new PackInfoEntity();
							
							onePackInfo.iceName = fromJsonObject.getString("from_username");
							onePackInfo.id = Integer.parseInt(pakgeJsonObject.getString("package_id"));
							onePackInfo.owner_tvmid = pakgeJsonObject.getString("package_owner");
							onePackInfo.pack_type = pakgeJsonObject.getString("package_type");
							onePackInfo.packname = pakgeJsonObject.getString("package_name");
							onePackInfo.res_groupid = pakgeJsonObject.getString("package_groupid");
							onePackInfo.submit_date = "";
							onePackInfo.thumb_guid = pakgeJsonObject.getString("package_guid");
							onePackInfo.tempPackName = onePackInfo.packname;
							onePackInfo.packageIconPath = ipport + File.separator + pakgeJsonObject.getString("package_iconpath");
							onePackInfo.itemCount = Integer.parseInt(pakgeJsonObject.getString("package_itemcount"));
							onePackInfo.ipport = ipport;
							onePackInfo.packageSize = Long.parseLong(pakgeJsonObject.getString("package_size"));
							onePackInfo.action = action;
							
							SearchFileDetailStatusEntity[] result = new SearchFileDetailStatusEntity[itemJsonArray.length()];
							for(int i=0;i<itemJsonArray.length();i++){
								JSONObject itemJSONObject = (JSONObject) itemJsonArray.get(i);
								
								SearchFileDetailStatusEntity oneFileInfo = new SearchFileDetailStatusEntity();
								
								oneFileInfo.id = 1;
								oneFileInfo.desc = itemJSONObject.getString("item_description");
								oneFileInfo.ext = "";
								oneFileInfo.file_type = itemJSONObject.getString("item_type");
								oneFileInfo.filename = itemJSONObject.getString("item_filename");
								oneFileInfo.guid = itemJSONObject.getString("item_guid");
								oneFileInfo.packname = onePackInfo.packname;
								oneFileInfo.submit_date = "";
								oneFileInfo.tag = itemJSONObject.getString("item_tag");
								oneFileInfo.title = itemJSONObject.getString("item_title");
								oneFileInfo.owner_tvmid = itemJSONObject.getString("item_owner");
								oneFileInfo.weight = 1;
								oneFileInfo.width = Integer.parseInt(itemJSONObject.getString("item_width"));
								oneFileInfo.height = Integer.parseInt(itemJSONObject.getString("item_height"));
								oneFileInfo.videoFilePathUrl = ipport + File.separator + itemJSONObject.getString("item_iconpath");
								oneFileInfo.filePathUrl = ipport + File.separator + itemJSONObject.getString("item_filepath");
								
								result[i] = oneFileInfo;
								
							}
							
							onePackInfo.result = result;
							
							startDownload(onePackInfo,Constant.activity.getApplicationContext(),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				}).start();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 下载资源包：下载的资源通过DB来管理
	 * @param entity
	 * @param mContext
	 * @param dateStr
	 */
	public static void startDownload(final PackInfoEntity entity,final Context mContext,final String dateStr){
		if(!Constant.uploadOrDownload.equals(Constant.DO_UPLOAD)){
			entity.tempPackName = entity.packname;
			String iceName = entity.iceName;
			if(!Constant.QRCODE_ACTION_SHARE_PACKAGE.equals(entity.action)){
				iceName = Constant.iceConnectionInfo.getLoginICE().connectICEName;
			}
			final int iceIndex = Constant.dbConnection.isExistICE(iceName,dateStr);
			// 查询资源是否存在
			final int pkgIndex = Constant.dbConnection.isExistPkg(iceIndex,dateStr,entity.packname);
			
			if(pkgIndex!=-1){//资源存在
				final PakgeTable pkgTable = Constant.dbConnection.queryPkg(pkgIndex);
				Map itemMap = null;
				if(pkgTable.getStatus() == 2){
					itemMap = Constant.dbConnection.getItemsMapByPakgeIndex(pkgIndex);
				}else{
					itemMap = Constant.dbConnection.queryDownloadTempByTableIndex(pkgIndex);
				}
				
				entity.itemMap = itemMap;
				entity.pkgIndex = pkgIndex;
				Handler handler = new Handler(){
					public void handleMessage(Message msg) {
						
						if(pkgTable.getStatus() == 2){//2代表 下载完成 （DOWNLOAD_FINISHED）
							
							Vector compareVector = (Vector) msg.obj;
							entity.compareVector = compareVector;
							
							if(compareVector.size() == 0){
								entity.packageUpdate = false;
								View promptView = LayoutInflater.from(mContext).inflate(ResourceUtil.getResId(mContext, "downloadprompt", "layout"),null);
								final PopupWindow perWindow =  new PopupWindow(promptView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true);
								perWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
								perWindow.showAtLocation(promptView, Gravity.CENTER,0,0);
					        	
								Constant.activity.getOnlineView().showMask();
								Constant.activity.hideTabs();
								
								Button confirm = (Button) promptView.findViewById(R.id.confirm);
					        	Button cancel = (Button) promptView.findViewById(R.id.cancel);
								ImageView clear = (ImageView)promptView.findViewById(R.id.clear);
													
					        	final EditText pkgText = (EditText)promptView.findViewById(R.id.rename);
					        	
					        	Timer timer=new Timer();
					            timer.schedule(new TimerTask() {
					                @Override
					                public void run() {
					                    InputMethodManager inputMethodManager=(InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					                    inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					                }
					            }, 300);
					        	
					        	pkgText.setText(entity.packname);//回填包名
					        	pkgText.setSelection(entity.packname.length());
					        	
					        	clear.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										pkgText.setText("");
									}
								});
					        	
					        	confirm.setOnClickListener(new View.OnClickListener() {								
									@Override
									public void onClick(View v) {
										String rePackage = pkgText.getText().toString();
										if(rePackage != null && !("".equals(rePackage))){
											entity.tempPackName = rePackage;
											int pkgIndex = Constant.dbConnection.isExistPkg(iceIndex,dateStr,entity.tempPackName);
											if(pkgIndex == -1){
												Constant.activity.getOnlineView().hideMask();
												Constant.activity.showTabs();
												perWindow.dismiss();
												entity.tempPackName = rePackage;
												prepareToInsertPackage(entity,false);
											}else{
												Toast.makeText(Constant.activity,"包名已存在，请重新输入!",Toast.LENGTH_LONG).show();
											}
										
										}else{
											Toast.makeText(Constant.activity,"包不能重命名为空!",Toast.LENGTH_LONG).show();
										}									
									}
								});
								cancel.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Constant.activity.getOnlineView().hideMask();
										Constant.activity.showTabs();
										perWindow.dismiss();
									}
								});
							}else{
								prepareToInsertPackage(entity,true);
							}
						}else if(pkgTable.getStatus() == 3 || pkgTable.getStatus() == 0){//下载失败或者等待下载中 的情况
							
							Boolean isChange = (Boolean) msg.obj;
							if(isChange){
								// 应该有重新下载和删除两个选项
								new AlertDialog.Builder(Constant.activity) 
								.setTitle(mContext.getResources().getString(R.string.package_change_title))
								.setMessage(mContext.getResources().getString(R.string.package_change_content))
								.setPositiveButton(mContext.getResources().getString(R.string.package_change_redownload), new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										// 重新下载
										dialog.dismiss();
										boolean isSuc = Utility.removePackage(pkgTable);
										startDownload(entity,mContext,dateStr);
									}
								})
								.setNegativeButton(mContext.getResources().getString(R.string.package_change_remove), new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										// 删除当前下载
										dialog.dismiss();
										boolean isSuc = Utility.removePackage(pkgTable);
										
										String resultStr = mContext.getResources().getString(R.string.package_remove_suc);
										if(!isSuc){
											resultStr = mContext.getResources().getString(R.string.package_remove_fail);
										}
										
										new AlertDialog.Builder(Constant.activity)  
										                .setTitle(mContext.getResources().getString(R.string.delete))
										                .setMessage(resultStr)
										                .setPositiveButton(mContext.getResources().getString(R.string.confirm), null)
										                .show();
									}
								})
								.show();
								
							}else{
								prepareToInsertPackage(entity,false);
							}
							
						}
					}
				};
				//比较下载的资源和服务器中资源
				if(pkgTable.getStatus() == 2){
					new PackageCompareTask(itemMap,entity,handler).execute();
				}else{
					new UnDownloadedPackageCompareTask(itemMap,entity,handler).execute();
				}
			}else{//资源不存在
				prepareToInsertPackage(entity,false);
			}
		}else{
			MessageUtil.toastInfo(mContext,mContext.getResources().getString(R.string.do_upload));
		}
	}
	
	/**
	 * 下载新资源：新资源插入到DB中然后下载
	 * @param entity
	 * @param isPackageUpdate
	 */
	private static void prepareToInsertPackage(PackInfoEntity entity,boolean isPackageUpdate){
		Constant.uploadOrDownload = Constant.DO_DOWNLOAD;
		if(isPackageUpdate){
			entity.packageUpdate = true;
			Constant.dbConnection.setStatus(entity.pkgIndex,1);
		}else{
			entity.packageUpdate = false;
			EmeetingApplication.setDownloadUnInsertedCount(EmeetingApplication.getDownloadUnInsertedCount()+1);
		}
		Constant.uploadOrDownload = Constant.DO_DOWNLOAD;
		EmeetingApplication.setUnDownloadedCount(EmeetingApplication.getUnDownloadedCount() + 1);
		
		synchronized (Constant.packageDownloadQueue) {//通知PackageDownloadThread下载资源
			Constant.packageDownloadQueue.add(entity);
			Constant.packageDownloadQueue.notify();
		}
	}
	
	public static Bitmap loadHeadBitmap(Bitmap inputBmp,Bitmap marginBitmap,int dividerW,int dividerH) {
		inputBmp = ImageUtil.zoomBitmap(inputBmp, marginBitmap.getWidth()-dividerW, marginBitmap.getHeight()-dividerH);
		
		Bitmap output = Bitmap.createBitmap(marginBitmap.getWidth(),
				marginBitmap.getHeight(), Config.ARGB_4444);
		Canvas canvas = new Canvas(output);
	
		final int color = 0xff424242;
		final Paint paint = new Paint();
	
		int startX = (marginBitmap.getWidth() - inputBmp.getWidth());
		int startY = (marginBitmap.getHeight() - inputBmp.getHeight());
		Rect rect = new Rect(startX, startY, inputBmp.getWidth(),
				inputBmp.getHeight());
	
		
		Rect rect1 = new Rect(0,0, marginBitmap.getWidth(),
				marginBitmap.getHeight());
		
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.WHITE);
	
		canvas.drawBitmap(inputBmp, null, rect, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SCREEN));
		
		canvas.drawBitmap(marginBitmap, null, rect1, paint);
		return output;
	}
	
	public static List editIosXml(String shareFilePath,String rootUrl) {
	    List<String> resultList = null;   
	    try {  
	    	
	    	SAXBuilder saxb = new SAXBuilder();
	    	org.jdom.Document doc = saxb.build(new File(Constant.SHARE_APP_PLIST_PATH));  
	    	
	    	resultList = new ArrayList<String>();     
	        // 取的根元素   
	        Element root = doc.getRootElement();   
	  
	        // 得到根元素所有子元素的集合   
	        List node = root.getChildren();   
	        Element et = null;   
	        parent: for (int i = 0; i < node.size(); i++) {   
	            et = (Element) node.get(i);// 循环依次得到子元素   
	            List subNode = et.getChildren(); // 得到内层子节点   
	            Element subEt = null;   
	            for (int j = 0; j < subNode.size(); j++) {   
	                subEt = (Element) subNode.get(j); // 循环依次得到子元素   
	                if(subEt.getName().equals("array")){
	                	List arraySubNode = subEt.getChildren();
	                	for(int l=0;l<arraySubNode.size();l++){
	                		Element arraySubEt = (Element) arraySubNode.get(l);
	                		List arrayDictSubNode = arraySubEt.getChildren();
		                	for(int l1=0;l1<arrayDictSubNode.size();l1++){
		                		Element arrayDictSubEt = (Element) arrayDictSubNode.get(l1);
		                		List valueSubNode = arrayDictSubEt.getChildren();
		                		for(int l2=0;l2<valueSubNode.size();l2++){
			                		Element valueSubEt = (Element) valueSubNode.get(l2);
			                		if("dict".equals(valueSubEt.getName())){
			                			List valueDictSubNode = valueSubEt.getChildren();
			                			for(int l3=0;l3<valueDictSubNode.size();l3++){
					                		Element valueDictSubEt = (Element) valueDictSubNode.get(l3);
					                		String content = valueDictSubEt.getTextTrim();
					                		if(valueDictSubEt.getName().equals("string") && content.contains("http://")){
					                			if(content.indexOf(".ipa") != -1){
					                				String ipaName = content.substring(content.lastIndexOf("/")+1,content.length());
					                				valueDictSubEt.setText(rootUrl + shareFilePath+ipaName);
					                			}else if(content.indexOf(".png") != -1){
					                				String pngName = content.substring(content.lastIndexOf("/")+1,content.length());
					                				valueDictSubEt.setText(rootUrl + shareFilePath+pngName);
					                			}
					                			
					                			resultList.add(valueDictSubEt.getTextTrim());
					                		}
			                			}
			                			
			                		}
			                	}
		                		
		                	}
	                		
	                	}
	                	
	                }
	            }
	        }   
	        if(doc != null){
	        	Format format=Format.getRawFormat();
		        format.setEncoding("UTF-8");
		        //XMLOutputter类提供了将JDOM树输出为字节流的能力
		        XMLOutputter output=new XMLOutputter(format);
		        output.output(doc, new FileOutputStream(Constant.SHARE_APP_PLIST_PATH));
	        }

	    } catch (JDOMException e) {   
	        e.printStackTrace();   
	    } catch (IOException e) {   
	        e.printStackTrace();   
	    }   
	    return resultList;   
	} 
	
	public static void replaceAppInstallHtmlContent(String androidUrl,String iosUrl){
		try{
			// 模板路径
			String filePath = Constant.SHARE_APP_DIR_PATH + Constant.SHARE_APP_HTML_NAME;
			String templateContent = "";
			FileInputStream fileinputstream = new FileInputStream(filePath);// 读取模板文件
			int lenght = fileinputstream.available();
			byte bytes[] = new byte[lenght];
			fileinputstream.read(bytes);
			fileinputstream.close();
			templateContent = new String(bytes);
			templateContent = templateContent.replaceAll("androidlink", androidUrl);
			templateContent = templateContent.replaceAll("ioslink",
					iosUrl);
			// 根据时间得文件名
			Calendar calendar = Calendar.getInstance();
			
			FileOutputStream fileoutputstream = new FileOutputStream(filePath);// 建立文件输出流
			System.out.print(filePath);
			byte tag_bytes[] = templateContent.getBytes();
			fileoutputstream.write(tag_bytes);
			fileoutputstream.close();
		} catch (Exception e) {
			System.out.print(e.toString());
		}
	}
	
	public static void copyToDisk(Context mContext,String iosUrl,String androidUrl) {
		
		AssetManager am = mContext.getResources().getAssets();
		File file = new File(Constant.SHARE_APP_DIR_PATH + Constant.SHARE_APP_HTML_NAME);
		
		if(file != null && file.exists()){
			file.delete();
		}
		
		writeToDisk(mContext,"html",Constant.SHARE_APP_DIR_PATH,am);
		replaceAppInstallHtmlContent(androidUrl,iosUrl);
	}
	
	private static void writeToDisk(Context mContext,String assetsName,String filePath,AssetManager am){
		try {
			String[] stationPicNames = getAllImageNameFromAssetsFile(mContext,assetsName);
			for(int i=0;i<stationPicNames.length;i++){
				if (!(new File(filePath + File.separator + stationPicNames[i])).exists()) {
					InputStream is = am.open(assetsName + File.separator + stationPicNames[i]);
					FileOutputStream fos = new FileOutputStream(filePath+ File.separator + stationPicNames[i]);
					byte[] buffer = new byte[7168];
					int count = 0;
					while ((count = is.read(buffer)) > 0) {
						fos.write(buffer, 0, count);
					}
					fos.close();
					is.close();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String[] getAllImageNameFromAssetsFile(Context mContext,String dirName){
		String[] stationPicNames = null;
		try {
			stationPicNames = mContext.getResources().getAssets().list(dirName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stationPicNames;
	}
	
	public static void removeExtraData(){
		Vector indexVector = new Vector();
		if(Constant.followGalleryEntityVector.size() > Constant.INTERACT_COUNT){
			for(int i=0;i<Constant.followGalleryEntityVector.size()-5;i++){
				indexVector.add(Constant.followGalleryEntityVector.get(i));
			}
			for(int i=0;i<indexVector.size();i++){
				Constant.followGalleryEntityVector.remove(indexVector.get(i));
			}
			
		}
	}
	
	public static void cancelAnswer(){
		Constant.forceAnswer = false;
		Constant.questionGalleryEntity = null;
		Constant.activity.answerTabHighHide();
		Constant.activity.endDrawAnswer();
		Constant.activity.endSelectAnswer();
	}
	
	public static void checkRepeatedData(InteractGalleryEntity galleryEntity){
		Vector repeatVector = new Vector();
			for(int i=0;i<Constant.followGalleryEntityVector.size();i++){
				if(Constant.followGalleryEntityVector.get(i).getItemGuid().equals(galleryEntity.getItemGuid())){
					repeatVector.add(Constant.followGalleryEntityVector.get(i));
				}
			}
			for(int i=0;i<repeatVector.size();i++){
				Constant.followGalleryEntityVector.remove(repeatVector.get(i));
			}
			Constant.followGalleryEntityVector.add(galleryEntity);
	}
	
	public static String getStrokeFilePath()
    {
        File sdcarddir = android.os.Environment.getExternalStorageDirectory();
        String strDir = sdcarddir.getPath() + "/DCIM/sketchpad/";
        String strFileName = getStrokeFileName();
        File file = new File(strDir);
        if (!file.exists())
        {
            file.mkdirs();
        }
        
        String strFilePath = strDir + strFileName;
        
        return strFilePath;
    }
    
    public static String getStrokeFileName()
    {
        
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        int month = rightNow.get(Calendar.MONDAY);
        int date = rightNow.get(Calendar.DATE);
        int hour = rightNow.get(Calendar.HOUR);
        int minute = rightNow.get(Calendar.MINUTE);
        int second = rightNow.get(Calendar.SECOND);
        
        String strFileName = String.format("%02d%02d%02d%02d%02d%02d.png", year, month, date, hour, minute, second);
        return strFileName;
    }
    
    public static void setMiddlePwd(Context mContext){
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String passwordValue = Constant.iceConnectionInfo.getLoginICE().password;
		Editor editor = preferences.edit();
		editor.putString(Constant.PREFERENCES_NAME,passwordValue);
		editor.commit();
    }
    
    public static void setDefaultPassword(Context mContext) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String passwordValue = preferences.getString(Constant.PREFERENCES_NAME, Constant.defaultPwd);

		Editor editor = preferences.edit();
		editor.putString(Constant.PREFERENCES_NAME, passwordValue);
		editor.commit();
	}

	public static void setTvmId(Context mContext){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		Constant.tvmId = preferences.getString("tvmId","");
		
    	if("".equals(Constant.tvmId)){
    		WifiManager wifiMgr = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
    		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
    		if (null != info) {
    			Constant.tvmId = String.valueOf(info.getMacAddress().hashCode());
    			Editor editor = preferences.edit();
        		editor.putString("tvmId",Constant.tvmId);
        		editor.commit();
    		}
    	}
	}
	
	public static void uploadPersonInfo(Context mContext){
		UploadUserInfoEntity userInfo = new UploadUserInfoEntity();
		
		File headFile = new File(Constant.headPath);
		if(headFile.exists()){//如果头像文件存在则上传头像,否则不赋值
			userInfo.face = Constant.headPath;
		}else{
			userInfo.face = "";
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		
		userInfo.nickname = preferences.getString("username", "设置昵称");//取得存储的姓名
		userInfo.company = "天脉聚源(北京)传媒科技有限公司";
		userInfo.email = "test@gmail.com";
		userInfo.mobile = "13888888888";
		new UploadPersonInfoTask(userInfo).execute();
	}
	
	public static String gbEncoding(final String gbString) {
		  char[] utfBytes = gbString.toCharArray();
		  StringBuffer buffer = new StringBuffer();
		  for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
		   String hexB = Integer.toHexString(utfBytes[byteIndex]);
		   if (hexB.length() <= 2) {
		    hexB = "00" + hexB;
		   }
		   buffer.append("" + hexB);
		  }
		  return buffer.substring(0);
		 }

	/**
	  * unicode 转换成 中文
	  * 
	  * @author fanhui 2007-3-15
	  * @param theString
	  * @return
	  */
	 public static String decodeUnicode(String theString) {
	  char aChar;
	  int len = theString.length();
	  StringBuffer outBuffer = new StringBuffer(len);
	  for (int x = 0; x < len;) {
	   aChar = theString.charAt(x++);
	   if (aChar == '\\') {
	    aChar = theString.charAt(x++);
	    if (aChar == 'u') {
	     int value = 0;
	     for (int i = 0; i < 4; i++) {
	      aChar = theString.charAt(x++);
	      switch (aChar) {
	      case '0':
	      case '1':
	      case '2':
	      case '3':
	      case '4':
	      case '5':
	      case '6':
	      case '7':
	      case '8':
	      case '9':
	       value = (value << 4) + aChar - '0';
	       break;
	      case 'a':
	      case 'b':
	      case 'c':
	      case 'd':
	      case 'e':
	      case 'f':
	       value = (value << 4) + 10 + aChar - 'a';
	       break;
	      case 'A':
	      case 'B':
	      case 'C':
	      case 'D':
	      case 'E':
	      case 'F':
	       value = (value << 4) + 10 + aChar - 'A';
	       break;
	      default:
	       throw new IllegalArgumentException(
	         "Malformed      encoding.");
	      }

	     }
	     outBuffer.append((char) value);
	    } else {
	     if (aChar == 't') {
	      aChar = '\t';
	     } else if (aChar == 'r') {
	      aChar = '\r';
	     } else if (aChar == 'n') {
	      aChar = '\n';
	     } else if (aChar == 'f') {
	      aChar = '\f';
	     }
	     outBuffer.append(aChar);
	    }
	   } else {
	    outBuffer.append(aChar);
	   }

	  }
	  return outBuffer.toString();

	 }

	 public static String[] getVersionInfo(Context context){
		    String[] versionInfo = new String[2];
		    try{
		        // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
		    	versionInfo[0] = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
		    	versionInfo[1] = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
		    } catch (NameNotFoundException e){
		        e.printStackTrace();
		    }
		    return versionInfo;
		}
}  
