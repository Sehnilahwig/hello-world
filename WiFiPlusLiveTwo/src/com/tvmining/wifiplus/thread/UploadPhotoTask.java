/**
 * project name:(emeeting)
 * create  time:2013-3-22
 * author:liujianjian
 */
package com.tvmining.wifiplus.thread;

import java.io.File;
import java.io.FileOutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.CommandTypeEntity;
import com.tvmining.sdk.entity.UploadFileDetailStatusEntity;
import com.tvmining.sdk.entity.UserInfoEntity;
import com.tvmining.sdk.entity.uploadFileEntity;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.FileTypeJudge;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.wifipluseq.R;

public class UploadPhotoTask extends AsyncTask<Object, Void, Object> {

	private static final String TAG = "UploadPhotoTask";
	Context context;
	String file_path = "";
	StringBuffer postdata = new StringBuffer();
	private String sendTo;
	private String className;
	
	public UploadPhotoTask(Context context,String path,String sendTo,String className){
		this.context = context;
		file_path = path;
		this.sendTo = sendTo;
		this.className = className;
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		boolean uploadResult = false;
		
        Hashtable<String, String> postDict = new Hashtable<String, String>();
        List<uploadFileEntity> uploadFileArray = new ArrayList<uploadFileEntity>();
        try{
            //有几个，就添加到 uploadFileArray 几个
        	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        	String title = preferences.getString("username",context.getResources().getString(R.string.username));
//            String title = EmeetingApplication.tvmId;//"android_photo";
            String tag = Constant.tvmId;
            String guid = "";//这项要为空，但是必须要有
            String filePath = file_path;
            String desc = "android_desc";
            String requestGuid = "";//无用，但是必须要有
            String fileType = "";//和前端商量使用的属性
            
            if(FileTypeJudge.isImageFile(FileTypeJudge.getMediaFileType(file_path))){
            	fileType = "IMAGE";
            }else if(FileTypeJudge.isVideoFile(FileTypeJudge.getMediaFileType(file_path))){
            	fileType = "VIDEO";
            }else{
            	fileType = "IMAGE";
            }
            
            Bitmap bitmap = BitmapFactory.decodeFile(file_path);
            int degree = ImageUtil.readPictureDegree(file_path);  
            File file = new File(file_path);
            bitmap = ImageUtil.rotaingImageView(degree, bitmap);  
            ImageUtil.saveBitmapToDisk(file, bitmap,Constant.LOCAL_GALLYERY_PATH);
            
            String suggestGroup = UserInfoEntity.groupId;//上传文件为用户上传//提示搜索哪个组
            String suggestGroupId = suggestGroup;

            uploadFileEntity oneUploadFile = new uploadFileEntity(title, desc, tag, guid, filePath, fileType);
            
            //-------------------------------------
            uploadFileArray.add(oneUploadFile); //上传多个文件，就 add 多个实体
            //--以上为文件提交-----------------------------------
            //入到那个包里。如果是空，则入到该用户私有包中。这个包其他用户搜索不到，但是可以访问到
            String packName = "交流";//上传到交流包
            postDict.put(uploadFileEntity.UPDATE_PACK_KEY, packName);

            //提示使用哪个权限，但是如果指定权限高于用户本身权限，则会降到用户全新,不填写，则为用户本身权限
            postDict.put(uploadFileEntity.SUGGEST_PACK_KEY, suggestGroupId);
            //上传过程的 guid。如果有上传过程并且生效，则服务器会忽略包和权限信息
            postDict.put(uploadFileEntity.REQUEST_GUID_KEY, requestGuid);
            postDict.put(uploadFileEntity.SOURCE_OBJ_KEY, className);

            uploadFileEntity[] uploadFileSubmitArray = new uploadFileEntity[uploadFileArray.size()];
            uploadFileArray.toArray(uploadFileSubmitArray);
            
            
//            UploadFileDetailStatusEntity[] detailStatus = ICESDK.sharedICE(EmeetingApplication.loginICE,EmeetingApplication.my).uploadLocalFile(postDict,uploadFileSubmitArray);        
            UploadFileDetailStatusEntity[] detailStatus = uploadFile(postDict,uploadFileSubmitArray);

            for (int i = 0; i < detailStatus.length; i++){
                Log.d(TAG,detailStatus[i].filename + " 上传 " + detailStatus[i].status+ " guid:" + detailStatus[i].guid+" fileURI"+detailStatus[i].fileUri);
                postdata.setLength(0);
    			postdata.append("{\"type\":\"resource\",\"detail\":[");
    			postdata.append("{\"position\":\"\",\"id\":\""+ detailStatus[i].guid +"\"},");
    			postdata.deleteCharAt(postdata.length()-1).append("]}");
    			if("device".equals(sendTo)){
    				RemoteControlTask pushSourceTask = new RemoteControlTask(context,CommandTypeEntity.PUSH,postdata.toString(),false);
        			pushSourceTask.execute(3);
    			}else if("user".equals(sendTo)){
    				RemoteControlTaskToUser pushSourceTask = new RemoteControlTaskToUser(context,CommandTypeEntity.PUSH,postdata.toString(),false,null);
        			pushSourceTask.execute(3);
    			}else if("all".equals(sendTo)){
    				RemoteControlTaskAllCanSee pushSourceTask = new RemoteControlTaskAllCanSee(CommandTypeEntity.PUSH,postdata.toString());
        			pushSourceTask.execute(3);
    			}
    			
    			postdata.setLength(0);
            }

            if (detailStatus.length == 0){
            	Log.d(TAG,"没有上传成功神马东西");
            	uploadResult = false;
            }else if(detailStatus.length>0&&detailStatus[0].status.equals("succ")){
            	uploadResult = true;
            }
        }catch (Exception ee) {
        	uploadResult = false;
        	Log.e(TAG,"上传文件出错啊出错了：", ee);
        }
		return uploadResult;
	}

	//多次上传机制
	private UploadFileDetailStatusEntity[] uploadFile(Hashtable<String, String> dict,uploadFileEntity[] FileArray){
		boolean is = false;
		UploadFileDetailStatusEntity[] detailStatus = null;
		int i = 0;
		while(!is && i < 5){
			Log.i(TAG, "i="+i);
			try{
				i++;        
				detailStatus = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),Constant.iceConnectionInfo.getUserInfoEntity()).uploadLocalFile(dict, FileArray);        
				if(detailStatus.length==0){
					is = false;
				}else{
					is = true;
				}
			}catch(SocketException e){
				is = false;        
			}catch (JSONException e) {
				is = false;
			}catch(Exception e){
				is = false;
			}
		}
		return detailStatus;
	}

	
	@Override
	protected void onPostExecute(Object result) {
		Toast.makeText(context, context.getResources().getString(R.string.interact_send_finish), Toast.LENGTH_SHORT).show();
	}
}