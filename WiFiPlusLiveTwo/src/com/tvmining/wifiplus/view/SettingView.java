package com.tvmining.wifiplus.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.tvmining.wifiplus.activity.SetDefaultPasswordActivity;
import com.tvmining.wifiplus.activity.SetImageShareQualityActivity;
import com.tvmining.wifiplus.activity.SetImageUploadQualityActivity;
import com.tvmining.wifiplus.activity.ShareAppActivity;
import com.tvmining.wifiplus.entity.Permission;
import com.tvmining.wifiplus.thread.ShareAppTask;
import com.tvmining.wifiplus.util.AppUtil;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ICEConnect;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.wifiplus.util.ResourceUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifiplus.view.UIButton.ClickListener;
import com.tvmining.wifipluseq.R;

/**
 * @author Like
 *
 */
public class SettingView extends BaseView implements View.OnClickListener,ClickListener{
	private Context mContext;
	
	private ImageView headIcon;
	
	private UITableView qualityView;
	private UITableView userIconname;
	private UIButtonUnClick versionName;
	private UIButton condition;
	private UIButton shareApp;
	private UIButton permissionchangebtn;
	
	private PopupWindow setHeadPopupWindow;
	private PopupWindow setUserNamePopupWindow;
	private View settingView;
	
	private View info;
	private ImageView localpackageqrcodemage;
	private ImageView sharepackagebackground;
	private View qrcodeCreateView;
	private ImageButton shareclose_btn;
	
	public static final String IMAGE_UNSPECIFIED = "image/*";
	public static int IMG_WIDTH = 150;
	public static int IMG_HEIGHT = 150;
	
	private static final int FROM_CAMERA = 5;
	private static final int FROM_GALLERY = 6;
	
	SharedPreferences preferences;
	public SettingView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		init();
	}

	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		// TODO Auto-generated constructor stub
		init();
	}

	public SettingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		init();
	}
	
	public void init() {
		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		settingView = LayoutInflater.from(mContext).inflate(ResourceUtil.getResId(mContext, "setting", "layout"),null);
//    	this.setGravity(Gravity.CENTER);
    	this.addView(settingView); 
    	
    	userIconname = (UITableView) findViewById(ResourceUtil.getResId(mContext, "user", "id"));
    	userIconname.setClickListener(new CustomClickListener1());
    	qualityView = (UITableView) findViewById(ResourceUtil.getResId(mContext, "ver", "id"));
    	qualityView.setClickListener(new CustomClickListener2());
    	versionName = (UIButtonUnClick) findViewById(ResourceUtil.getResId(mContext, "versionname", "id"));
    	condition = (UIButton) findViewById(ResourceUtil.getResId(mContext, "condition", "id"));
    	condition.addClickListener(this);
    	shareApp = (UIButton) findViewById(ResourceUtil.getResId(mContext, "shareapp", "id"));
    	shareApp.addClickListener(this);
    	
    	permissionchangebtn = (UIButton) findViewById(ResourceUtil.getResId(mContext, "permissionchangebtn", "id"));
    	permissionchangebtn.addClickListener(this);
    	
    	new ShareAppTask(mContext,"check").execute();
    	
    	versionName = (UIButtonUnClick) findViewById(ResourceUtil.getResId(mContext, "versionname", "id"));
    	
    	View setHeadIconView = ((Activity)this.mContext).getLayoutInflater().inflate(R.layout.camera_menu,null);
		setHeadPopupWindow = new PopupWindow(setHeadIconView,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		View setUserNameView = ((Activity)this.mContext).getLayoutInflater().inflate(R.layout.changename_menu,null);
		setUserNamePopupWindow = new PopupWindow(setUserNameView,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
    	
		Button gallery = (Button)setHeadIconView.findViewById(
				ResourceUtil.getResId(mContext, "choosefrom", "id"));
		Button camera = (Button)setHeadIconView.findViewById(
				ResourceUtil.getResId(mContext, "photo", "id"));
		Button cancel = (Button)setHeadIconView.findViewById(
				ResourceUtil.getResId(mContext, "cancel", "id"));
		
		gallery.setOnClickListener(this);
		camera.setOnClickListener(this);
		cancel.setOnClickListener(this);
		
		EditText name = (EditText)setUserNameView.findViewById(ResourceUtil.getResId(mContext, "nickname", "id"));
		Button ok = (Button)setUserNameView.findViewById(ResourceUtil.getResId(mContext, "OK", "id"));
		Button cancel1 = (Button)setUserNameView.findViewById(ResourceUtil.getResId(mContext, "cancel", "id"));
		
		setData();
		
		
		
		info = findViewById(ResourceUtil.getResId(mContext, "info", "id"));
		info.setVisibility(View.INVISIBLE);
		sharepackagebackground = (ImageView) findViewById(ResourceUtil.getResId(mContext, "sharepackagebackground", "id"));
    	qrcodeCreateView = (View) findViewById(ResourceUtil.getResId(mContext, "qrcodecreateview", "id"));
    	localpackageqrcodemage = (ImageView) qrcodeCreateView.findViewById(ResourceUtil.getResId(mContext, "qrcodeimage", "id"));
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
		
		
    }
	
	
	
	
	
	
	
	public void setData(){
		
		
		View view = LayoutInflater.from(this.mContext).inflate(R.layout.list_item_custom,null);
		TextView text2 = (TextView) view.findViewById(R.id.title);
		text2.setText("头像");
		TextPaint tp = text2.getPaint(); 
		tp.setFakeBoldText(true);
		ImageView icon = (ImageView) view.findViewById(R.id.content);
		setHeadBitmap(Constant.headPath, icon);
		headIcon = (ImageView) view.findViewById(R.id.content);
		userIconname.addViewItem(new ViewItem(view));
		userIconname.addBasicItem("名字", preferences.getString("username", "设置昵称"));
		userIconname.commit();
		
		qualityView.addBasicItem(getResources().getString(
				ResourceUtil.getResId(mContext, "imgquality", "string")), preferences.getString("uploadquality", "中(90%)"));
		qualityView.addBasicItem(getResources().getString(
				ResourceUtil.getResId(mContext, "sharequality", "string")), preferences.getString("sharequality", "中(1024x1024)"));
		qualityView.addBasicItem(getResources().getString(
				ResourceUtil.getResId(mContext, "defaultpassword", "string")), preferences.getString(Constant.PREFERENCES_NAME, Constant.defaultPwd));
		
		qualityView.addBasicItem("关于软件", "");
		qualityView.commit();
		
		
		versionName.setContent("版本号", "version", false);
		
		condition.setContent(getResources().getString(
				ResourceUtil.getResId(mContext, "condition", "string")),"",true);
		
		shareApp.setContent(getResources().getString(
				ResourceUtil.getResId(mContext, "shareapp", "string")),"",true);
		
		permissionchangebtn.setContent(getResources().getString(
				ResourceUtil.getResId(mContext, "permission_change", "string")),"",true);
		
//		ok.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				setUserNamePopupWindow.dismiss();
//			}
//		});
//		cancel1.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				setUserNamePopupWindow.dismiss();
//			}
//		});
		
	}
	
	public void setPermission(){
		if(Permission.PERMISSION_HIGH.equals(Constant.user.getPermisssion().getLevel())){
			permissionchangebtn.setContent(getResources().getString(
					ResourceUtil.getResId(mContext, "permission_change", "string")),getResources().getString(ResourceUtil.getResId(mContext, "red_show", "string")),true);
		}else if(Permission.PERMISSION_MIDDLE.equals(Constant.user.getPermisssion().getLevel())){
			permissionchangebtn.setContent(getResources().getString(
					ResourceUtil.getResId(mContext, "permission_change", "string")),getResources().getString(ResourceUtil.getResId(mContext, "yellow_show", "string")),true);
		}else if(Permission.PERMISSION_LOW.equals(Constant.user.getPermisssion().getLevel())){
			permissionchangebtn.setContent(getResources().getString(
					ResourceUtil.getResId(mContext, "permission_change", "string")),getResources().getString(ResourceUtil.getResId(mContext, "green_show", "string")),true);
		}
	}
	
	private class CustomClickListener1 implements UITableView.ClickListener {		
		@Override
		public void onClick(int index) {
			//处理单个View点击
			if(index==0){
				setHeadPopupWindow
				.setAnimationStyle(R.style.popupAnimationChange);
				ColorDrawable cd = new ColorDrawable(Color.parseColor("#b0000000"));
				setHeadPopupWindow.setBackgroundDrawable(cd);
				setHeadPopupWindow.setOutsideTouchable(true);
				setHeadPopupWindow.showAtLocation(
						settingView,
				Gravity.CENTER, 0, 0);
				setHeadPopupWindow.update();

				// 执行动画
//				AnimationUtil.ShowUp(mContainer.getHeight(),
//				setheadLinearLayout);
			}else if(index==1){
				/*setUserNamePopupWindow
				.setAnimationStyle(R.style.popupAnimationChange);
				ColorDrawable cd = new ColorDrawable(Color.parseColor("#b0000000"));
				setUserNamePopupWindow.setBackgroundDrawable(cd);
				setUserNamePopupWindow.setOutsideTouchable(true);
				setUserNamePopupWindow.showAtLocation(
						settingView,
				Gravity.CENTER, 0, 0);
				setUserNamePopupWindow.update();*/
//				((Activity)SettingView.this.mContext).showDialog(0);
				showDialog();
			}
		}    	
    }
	private class CustomClickListener2 implements UITableView.ClickListener {		
		@Override
		public void onClick(int index) {
			//处理单个View点击
			if(index==0){
				((Activity)SettingView.this.mContext).startActivityForResult(
						new Intent(SettingView.this.mContext,SetImageUploadQualityActivity.class),1);
			}else if(index==1){
				((Activity)SettingView.this.mContext).startActivityForResult(
						new Intent(SettingView.this.mContext,SetImageShareQualityActivity.class),2);
			}else if(index==2){
				((Activity)SettingView.this.mContext).startActivityForResult(
						new Intent(SettingView.this.mContext,SetDefaultPasswordActivity.class),3);
			}else if (index==3) {
				Constant.activity.openAboutView();
			}
		}    	
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == ResourceUtil.getResId(mContext, "choosefrom", "id")){
			setHeadPopupWindow.dismiss();
			Intent galleryintent = new Intent(Intent.ACTION_PICK, null);
			galleryintent.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					IMAGE_UNSPECIFIED);
			((Activity)SettingView.this.mContext).startActivityForResult(galleryintent, FROM_GALLERY);
		}else if(v.getId() == ResourceUtil.getResId(mContext, "photo", "id")){
			setHeadPopupWindow.dismiss();
			Intent cameraIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Constant.savePath,
							"head.jpg")));
			cameraIntent.putExtra("camerasensortype", 2);
			cameraIntent.putExtra("autofocus", true); // 自动对焦  
			cameraIntent.putExtra("fullScreen", false); // 全屏  
			cameraIntent.putExtra("showActionIcons", false);
			((Activity)SettingView.this.mContext).startActivityForResult(cameraIntent, FROM_CAMERA);
		}else if (v.getId() == ResourceUtil.getResId(mContext, "cancel", "id")) {
			setHeadPopupWindow.dismiss();
		}else if (v.getId() == ResourceUtil.getResId(mContext, "condition", "id")) {
			AppUtil.copyToData(mContext);
			Intent intent = new Intent("android.intent.action.VIEW"); 
			intent.addCategory("android.intent.category.DEFAULT"); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			Uri uri = Uri.fromFile(new File(Constant.savePath+Constant.CONDITION_NAME)); 
			intent.setDataAndType(uri, "application/msword"); 
			SettingView.this.mContext.startActivity(intent);
		}else if (v.getId() == ResourceUtil.getResId(mContext, "shareapp", "id")) {
			new ShareAppTask(mContext,"download").execute();
		}else if(v.getId() == ResourceUtil.getResId(mContext, "permissionchangebtn", "id")){
			ICEConnect.login(false, mContext,null);
		}
	}

	public void changeString(String text,int index){
		LinearLayout layout = (LinearLayout) qualityView.findViewById(R.id.buttonsContainer);
		switch (index) {
		case 1:
			((TextView)(layout.getChildAt(0).findViewById(R.id.itemCount))).setText(text);
			break;
		case 2:
			((TextView)(layout.getChildAt(1).findViewById(R.id.itemCount))).setText(text);
			break;
		case 3:
			((TextView)(layout.getChildAt(2).findViewById(R.id.itemCount))).setText(text);
			break;

		default:
			break;
		}
	}
	
	public void changeHeadImage(){
		setHeadBitmap(Constant.headPath, headIcon);
	}
	
	/**
	 * 将一个图片缩放成100x100图片
	 * 
	 * @param filePath
	 * @return
	 */
	public Bitmap scaleBitmap(String filePath) {
		Bitmap original = BitmapFactory.decodeFile(filePath);
		int bmpWidth = original.getWidth();// 原图宽度
		int bmpHeight = original.getHeight();// 原图高度
		float widthScale = (float) IMG_WIDTH / bmpWidth;// 宽度缩放比例
		float heightScale = (float) IMG_HEIGHT / bmpHeight;// 高度缩放比例

		Matrix matrix = new Matrix();
		matrix.postScale(widthScale, heightScale);// 矩阵
		Bitmap resizeBitmap = Bitmap.createBitmap(original, 0, 0, bmpWidth,
				bmpHeight, matrix, false);
		File file = new File(Constant.headPath);
		try {
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			}
			FileOutputStream out = new FileOutputStream(file);
			if (resizeBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resizeBitmap;
	}
	
	
	/**
	 * 将一个图片缩放成100x100图片
	 * 
	 * @param filePath
	 * @return
	 */
	public void setHeadBitmap(String filePath,ImageView localview) {
		Bitmap inputBmp = null;
    	if (new File(Constant.headPath).exists() && new File(Constant.headPath).length() > 0) {
    		inputBmp = BitmapFactory.decodeFile(Constant.headPath);
    	} else {
    		inputBmp = BitmapFactory.decodeResource(mContext.getResources(),
    				R.drawable.head_default);
    	}
    	Bitmap marginBitmap = BitmapFactory.decodeResource(mContext.getResources(),
    			R.drawable.head_border);
    	
    	localview.setImageBitmap(Utility.loadHeadBitmap(inputBmp, marginBitmap,2,2));
	}
	
	// 将图片缩放成100x100,再生成圆形图片bitmap
		public Bitmap toCircleImage(String pathName) {
			Bitmap inputBmp = null;
		if (new File(pathName).exists() && new File(pathName).length() > 0) {
			inputBmp = BitmapFactory.decodeFile(pathName);
		} else {
			inputBmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.head_default);
		}
		Bitmap marginBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.head_border);
		
		inputBmp = ImageUtil.zoomBitmap(inputBmp, marginBitmap.getWidth(), marginBitmap.getWidth());
		
		Bitmap output = Bitmap.createBitmap(marginBitmap.getWidth(),
				marginBitmap.getHeight(), Config.ARGB_8888);
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

		canvas.drawCircle(0 + marginBitmap.getWidth() / 2,
				0 + marginBitmap.getHeight() / 2, (marginBitmap.getWidth()-14) / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(inputBmp, null, rect, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SCREEN));
		
		canvas.drawBitmap(marginBitmap, null, rect1, paint);
		return output;
	}
	
	public Bitmap toCircleImage(Bitmap inputBmp) {
		
		
		Bitmap marginBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.head_border);
		
		inputBmp = ImageUtil.zoomBitmap(inputBmp, marginBitmap.getWidth(), marginBitmap.getWidth());
		
		Bitmap output = Bitmap.createBitmap(marginBitmap.getWidth(),
				marginBitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
	
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
	
		canvas.drawCircle(0 + marginBitmap.getWidth() / 2,
				0 + marginBitmap.getHeight() / 2, (marginBitmap.getWidth()-14) / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(inputBmp, null, rect, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SCREEN));
		
		canvas.drawBitmap(marginBitmap, null, rect1, paint);
		return output;
	}
	
	
	public void showDialog(){
		LayoutInflater factory = LayoutInflater.from(this.mContext);
		final LinearLayout layout = (LinearLayout) userIconname.findViewById(R.id.buttonsContainer);
        final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
        final EditText edittext = (EditText) textEntryView.findViewById(R.id.username_edit);
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
                InputMethodManager inputManager =(InputMethodManager)edittext.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(edittext, 0);
            }
            
        },200);
        
        AlertDialog dialog = new AlertDialog.Builder(this.mContext)
        .setTitle("输入名字")
        .setView(textEntryView)
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	String name = edittext.getText().toString();
            	Editor editor = preferences.edit();
        		editor.putString("username",name);
        		editor.commit();
        		((TextView)(layout.getChildAt(1).findViewById(R.id.itemCount))).setText(name);
                
            }
        })
        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* User clicked cancel so do some stuff */
            }
        })
        .create();
        dialog.show();
	}

	
	public void showAppShareProgressBar(int rate){
		if(rate < 100){
			((TextView)shareApp.findViewById(R.id.content)).setVisibility(View.GONE);
			shareApp.findViewById(R.id.shareappprogressbar).setVisibility(View.VISIBLE);
			((ProgressBar)shareApp.findViewById(R.id.shareappprogressbar)).setProgress(rate);
		}else{
			shareApp.findViewById(R.id.shareappprogressbar).setVisibility(View.GONE);
			((TextView)shareApp.findViewById(R.id.content)).setText(R.string.shareapphave);
			((TextView)shareApp.findViewById(R.id.content)).setVisibility(View.VISIBLE);
		}
	}
	
	public void setCheckResult(boolean isHave){
		if(isHave){
			((TextView)shareApp.findViewById(R.id.content)).setText(R.string.shareapphave);
		}else{
			((TextView)shareApp.findViewById(R.id.content)).setText(R.string.shareappno);
		}
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
	
	/**
	 * 关闭分享View动画
	 */
	private void openQRView() {
		qrcodeCreateView.setVisibility(View.VISIBLE);
		sharepackagebackground.setVisibility(View.VISIBLE);
		AnimatorSet set = new AnimatorSet();
		set.play(
				ObjectAnimator.ofFloat(qrcodeCreateView, View.X,
						Constant.screenWidth, 0))
				.with(ObjectAnimator.ofFloat(sharepackagebackground, "alpha",
						0f, 1f));
		set.setDuration(300);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				// qrcodeCreateView.setBackgroundResource(ResourceUtil.getResId(mContext,
				// "sharebackground", "drawable"));
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		set.start();
	}
	
	public void showQrcodeView(Bitmap bitmap){
		localpackageqrcodemage.setImageBitmap(bitmap);
		openQRView();
	}
	
}