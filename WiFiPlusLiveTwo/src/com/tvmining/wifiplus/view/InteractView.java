package com.tvmining.wifiplus.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tvmining.com.chat.ChatView;
import com.tvmining.sdk.entity.CommandTypeEntity;
import com.tvmining.sdk.entity.ConnectFailedStatus;
import com.tvmining.sdk.entity.ICELoginEntity;
import com.tvmining.sdk.entity.ListenEventEntity;
import com.tvmining.sdk.entity.NeighbourEntity;
import com.tvmining.sdk.entity.UserInfoEntity;
import com.tvmining.wifiplus.canvas.view.SketchPadView;
import com.tvmining.wifiplus.entity.InteractGalleryEntity;
import com.tvmining.wifiplus.image.zoom.PhotoViewAttacher;
import com.tvmining.wifiplus.thread.RemoteControlTaskAllCanSee;
import com.tvmining.wifiplus.thread.UploadPhotoTask;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.IntAreaUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifipluseq.R;

/**
 * 互动类：类似于pad端的跟随指令
 * 界面内容参考：interact.xml
 */
public class InteractView extends BaseView implements OnLongClickListener,
		OnClickListener, ListenEventEntity {

	public int index = 0;

	public InteractView(Context context) {
		super(context);
		init(context);
	}

	public InteractView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public InteractView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private String TAG = "RemoteControlView";

	private ArrayList<View> viewList;
	private View showLayout;

	private SharedPreferences sp = null;
	private boolean displayAdvance = false;// 默认不显示高级下的按钮

	String postdata = "";
	LayoutInflater inflater = null;
	InputMethodManager imm = null;
	private AsyncTask controlTask = null;

	ArrayList<NeighbourEntity> allScreenList = new ArrayList<NeighbourEntity>();// 所有搜索到的屏幕信息

	private Context mContext;

	Toast toast = null;
	ProgressDialog dialog = null;// 搜索中控提示信息

	public boolean quitOnTouch;

	private boolean[] freshArray;
	/**
	 * 图片显示gallery
	 */
	private InteractGallery interactGallery;
	/**
	 * 聊天view
	 */
	private ChatView chatView;
	private LinearLayout answerselectview;
	private LinearLayout editView;
	private ImageView cancelView;
	private ImageView colorView;
	private ImageView clearView;
	private ImageView sizeView;
	private View editcommitview;
	private View editcommitlayout;
	private View editcommittext;
	private View editMaskView;
	private View interactfacemaskview;
	private FrameLayout interactcolormenu;
	private View whiteView;
	private View blackView;
	private View blueView;
	private View redView;
	private View yellowBiew;
	private View greenView;

	private FrameLayout interactsizemenu;
	private View smallView;
	private View middleView;
	private View largeView;

	private Handler progressHandler;
	private View expressionlayout;
	private SketchPadView cameraimageview;
	private View nointeractlayout;

	private Button answerselectA;
	private Button answerselectB;
	private Button answerselectC;
	private Button answerselectD;
	private Button answerselectE;
	private View answerselectcommitlayout;
	private View answerselectcommitview;
	private View answerselectcommittext;
	private Map selMap = new TreeMap();
	private String[] chars = { "A", "B", "C", "D", "E" };

	public View getInteractfacemaskview() {
		return interactfacemaskview;
	}

	public ChatView getChatView() {
		return chatView;
	}

	public void setInteractcolormenu(FrameLayout interactcolormenu) {
		this.interactcolormenu = interactcolormenu;
		whiteView = interactcolormenu.findViewById(R.id.editcolorwhite);
		blackView = interactcolormenu.findViewById(R.id.editcolorblack);
		blueView = interactcolormenu.findViewById(R.id.editcolorblue);
		redView = interactcolormenu.findViewById(R.id.editcolorred);
		yellowBiew = interactcolormenu.findViewById(R.id.editcoloryellow);
		greenView = interactcolormenu.findViewById(R.id.editcolorgreen);

		whiteView.setOnClickListener(this);
		blackView.setOnClickListener(this);
		blueView.setOnClickListener(this);
		redView.setOnClickListener(this);
		yellowBiew.setOnClickListener(this);
		greenView.setOnClickListener(this);
	}

	public void setInteractsizemenu(FrameLayout interactsizemenu) {
		this.interactsizemenu = interactsizemenu;
		smallView = interactsizemenu.findViewById(R.id.editsizesmall);
		middleView = interactsizemenu.findViewById(R.id.editsizemiddle);
		largeView = interactsizemenu.findViewById(R.id.editsizelarge);

		smallView.setOnClickListener(this);
		middleView.setOnClickListener(this);
		largeView.setOnClickListener(this);
	}

	public InteractGallery getInteractGallery() {
		return interactGallery;
	}

	public void init(Context context) {
		mContext = context;

		inflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View remoteControlView = (View) inflater.inflate(R.layout.interact,
				null, true);
		this.addView(remoteControlView);
		remoteControlView.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		progressHandler = new Handler() {
			public void handleMessage(Message msg) {
				float rateInt = (Float) msg.obj;
				toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
				toast.setText("上传进度:" + rateInt + "%");
				toast.show();
			}
		};

		imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		displayAdvance = sp.getBoolean("displayAdvance", false);// 初始打开遥控器时不显示高级下的按钮
		Log.i(TAG, "高级按钮是否显示:" + displayAdvance);

		toast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);

		nointeractlayout = remoteControlView
				.findViewById(R.id.nointeractlayout);
		expressionlayout = remoteControlView
				.findViewById(R.id.expressionlayout);
		interactGallery = (InteractGallery) remoteControlView
				.findViewById(R.id.interactgallery);
		chatView = (ChatView) remoteControlView.findViewById(R.id.chatview);//聊天控件
		editView = (LinearLayout) remoteControlView.findViewById(R.id.editview);//绘画控件
		editView.getLayoutParams().height = (int) Utility.dpToPixel(40);

		answerselectview = (LinearLayout) remoteControlView
				.findViewById(R.id.answerselectview);
		answerselectview.getLayoutParams().height = (int) Utility.dpToPixel(40);

		cancelView = (ImageView) editView.findViewById(R.id.editcancel);//绘画控件取消按钮
		colorView = (ImageView) editView.findViewById(R.id.editcolor);//颜色控件
		clearView = (ImageView) editView.findViewById(R.id.editclear);//清楚笔迹控件
		sizeView = (ImageView) editView.findViewById(R.id.editsize);//笔迹大小控件
		editcommitview = editView.findViewById(R.id.editcommitview);
		editcommitlayout = editView.findViewById(R.id.editcommitlayout);
		editcommittext = editView.findViewById(R.id.editcommittext);
		editMaskView = findViewById(R.id.editmaskview);
		interactfacemaskview = findViewById(R.id.interactfacemaskview);
		cameraimageview = (SketchPadView) findViewById(R.id.cameraimageview);
		cameraimageview.setFrom("local");
		cameraimageview.setStrokeColor(interactGallery.getCurrentColor());
		cameraimageview.setStrokeSize(interactGallery.getCurrentSize(),
				SketchPadView.STROKE_PEN);
		PhotoViewAttacher attacher = new PhotoViewAttacher(cameraimageview);
		attacher.setZoomable(false);
		cameraimageview.setDrawStrokeEnable(true);
		cameraimageview.attacher = attacher;

		answerselectA = (Button) remoteControlView
				.findViewById(R.id.answerselectA);
		answerselectB = (Button) remoteControlView
				.findViewById(R.id.answerselectB);
		answerselectC = (Button) remoteControlView
				.findViewById(R.id.answerselectC);
		answerselectD = (Button) remoteControlView
				.findViewById(R.id.answerselectD);
		answerselectE = (Button) remoteControlView
				.findViewById(R.id.answerselectE);
		answerselectcommitlayout = remoteControlView
				.findViewById(R.id.answerselectcommitlayout);
		answerselectcommitview = remoteControlView
				.findViewById(R.id.answerselectcommitview);
		answerselectcommittext = remoteControlView
				.findViewById(R.id.answerselectcommittext);

		cancelView.setOnClickListener(this);
		colorView.setOnClickListener(this);
		clearView.setOnClickListener(this);
		sizeView.setOnClickListener(this);
		editcommitview.setOnClickListener(this);
		editcommitlayout.setOnClickListener(this);
		editcommittext.setOnClickListener(this);
		editMaskView.setOnClickListener(this);
		interactfacemaskview.setOnClickListener(this);

		answerselectA.setOnClickListener(this);
		answerselectB.setOnClickListener(this);
		answerselectC.setOnClickListener(this);
		answerselectD.setOnClickListener(this);
		answerselectE.setOnClickListener(this);
		answerselectcommitlayout.setOnClickListener(this);
		answerselectcommitview.setOnClickListener(this);
		answerselectcommittext.setOnClickListener(this);
	}

	public void showCameraImageView(Bitmap bitmap) {
		hideViewPager();
		cameraimageview.clearAllStrokes();
		cameraimageview.setImageBitmap(bitmap);
		cameraimageview.setBkBitmap(bitmap);
		cameraimageview.setVisibility(View.VISIBLE);
		chatView.setVisibility(View.GONE);
		editView.setVisibility(View.VISIBLE);
		int h = (bitmap.getHeight() * Constant.screenWidth) / bitmap.getWidth();
		cameraimageview.getLayoutParams().height = h;
		cameraimageview.setScaleType(ScaleType.FIT_CENTER);

		cameraimageview.requestLayout();
	}

	public void resetGalleryView(boolean isLoad) {
		if (Constant.forceAnswer) {
			Utility.checkRepeatedData(Constant.questionGalleryEntity);
			Utility.removeExtraData();
			if (isLoad) {
				interactGallery.resetAnswerInteractView();
			} else {
				interactGallery.resetAnswerInteractViewData();
			}

		} else {
			interactGallery.resetNormalInteractView();
		}
		if (Constant.followGalleryEntityVector != null
				&& Constant.followGalleryEntityVector.size() > 0
				&& nointeractlayout.getVisibility() == View.VISIBLE) {
			nointeractlayout.setVisibility(View.GONE);
		}
	}

	public void enterDrawAnswer() {
		enterEditView();
		enterEditStatus();
	}

	public void hideCameraImageView() {
		showViewPager();
		cameraimageview.setVisibility(View.GONE);
		chatView.setVisibility(View.VISIBLE);
		editView.setVisibility(View.GONE);
		if (cameraimageview.getBkBitmap() != null
				&& !cameraimageview.getBkBitmap().isRecycled()) {
			cameraimageview.getBkBitmap().recycle();
		}
	}

	public void hideCameraImageAnswerSelect() {
		showViewPager();
		cameraimageview.setVisibility(View.GONE);
		if (cameraimageview.getBkBitmap() != null
				&& !cameraimageview.getBkBitmap().isRecycled()) {
			cameraimageview.getBkBitmap().recycle();
		}
	}

	public void setLandscape() {
		expressionlayout.setVisibility(View.GONE);
		interactGallery.getGalleryTop().setVisibility(View.GONE);
		interactGallery.setLandScreenView();
	}

	public void setPortrait() {
		expressionlayout.setVisibility(View.VISIBLE);
		interactGallery.getGalleryTop().setVisibility(View.VISIBLE);
		interactGallery.setLandScreenView();
	}

	/**
	 * 设置galleryview
	 * @param galleryEntity
	 */
	public void addInteractView(InteractGalleryEntity galleryEntity) {
		if (nointeractlayout.getVisibility() == View.VISIBLE) {
			nointeractlayout.setVisibility(View.GONE);
		}
		interactGallery.addImageInfo(galleryEntity);
	}

	public void photoUpload(String imagePath) {
		// 处理交流包拍照完上传操作
		if (imagePath != null && !imagePath.equals("")) {
			freshArray = new boolean[10000000];
			new UploadPhotoTask(Constant.activity, imagePath, "all",
					InteractView.class.getName()).execute();// 上传图片文件
		}
	}

	public void endEdit() {
		endEditView();
		cancelEditStatus();
	}

	public void changeEditStatus() {
		chatView.setVisibility(View.VISIBLE);
		editView.setVisibility(View.GONE);
		interactGallery.endEditStatusInForceAnswer();
	}

	public void endSelect() {
		interactGallery.resetInteractLargeView();
		cancelEditStatus();
		resetAnswerBtn();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editcancel://绘画取消控件
			if (cameraimageview.getVisibility() == View.VISIBLE) {
				hideCameraImageView();
			} else {
				if (Constant.forceAnswer) {
					// Utility.cancelAnswer();
					changeEditStatus();
				} else {
					endEdit();
				}
			}
			break;
		case R.id.editcolor://笔迹颜色控件
			showInteractColor(interactcolormenu, colorView);
			break;
		case R.id.editclear://笔迹清除控件
			if (cameraimageview.getVisibility() == View.VISIBLE) {
				cameraimageview.clearAllStrokes();
			} else {
				interactGallery.clearDrawColor();
			}
			break;
		case R.id.editsize:
			showInteractColor(interactsizemenu, sizeView);
			break;
		case R.id.editcommitlayout:
		case R.id.editcommitview:
		case R.id.editcommittext:
			commit();
			break;
		case R.id.editmaskview:
			hideInteractColor(interactcolormenu);
			hideInteractColor(interactsizemenu);
			break;
		case R.id.interactfacemaskview:
			interactfacemaskview.setVisibility(View.GONE);
			chatView.loseFocus();
			chatView.getFaceLayout().resetEditStatus();
			break;
		case R.id.editcolorwhite:
			cameraimageview.setStrokeColor(Color.WHITE);
			interactGallery.setDrawColor(Color.WHITE);
			colorView.setImageDrawable(getResources().getDrawable(
					R.drawable.edit_white));
			hideInteractColor(interactcolormenu);
			break;
		case R.id.editcolorblack:
			cameraimageview.setStrokeColor(Color.BLACK);
			interactGallery.setDrawColor(Color.BLACK);
			colorView.setImageDrawable(getResources().getDrawable(
					R.drawable.edit_black));
			hideInteractColor(interactcolormenu);
			break;
		case R.id.editcolorblue:
			cameraimageview.setStrokeColor(Color.BLUE);
			interactGallery.setDrawColor(Color.BLUE);
			colorView.setImageDrawable(getResources().getDrawable(
					R.drawable.edit_blue));
			hideInteractColor(interactcolormenu);
			break;
		case R.id.editcolorred:
			cameraimageview.setStrokeColor(Color.RED);
			interactGallery.setDrawColor(Color.RED);
			colorView.setImageDrawable(getResources().getDrawable(
					R.drawable.edit_red));
			hideInteractColor(interactcolormenu);
			break;
		case R.id.editcoloryellow:
			cameraimageview.setStrokeColor(Color.YELLOW);
			interactGallery.setDrawColor(Color.YELLOW);
			colorView.setImageDrawable(getResources().getDrawable(
					R.drawable.edit_yellow));
			hideInteractColor(interactcolormenu);
			break;
		case R.id.editcolorgreen:
			cameraimageview.setStrokeColor(Color.GREEN);
			interactGallery.setDrawColor(Color.GREEN);
			colorView.setImageDrawable(getResources().getDrawable(
					R.drawable.edit_green));
			hideInteractColor(interactcolormenu);
			break;
		case R.id.editsizesmall:
			cameraimageview.setStrokeSize(Constant.EDIT_SIZE_SMALL,
					SketchPadView.STROKE_PEN);
			interactGallery.setDrawSize(Constant.EDIT_SIZE_SMALL,
					SketchPadView.STROKE_PEN);

			hideInteractColor(interactsizemenu);
			break;
		case R.id.editsizemiddle:
			cameraimageview.setStrokeSize(Constant.EDIT_SIZE_MIDDLE,
					SketchPadView.STROKE_PEN);
			interactGallery.setDrawSize(Constant.EDIT_SIZE_MIDDLE,
					SketchPadView.STROKE_PEN);
			hideInteractColor(interactsizemenu);
			break;
		case R.id.editsizelarge:
			cameraimageview.setStrokeSize(Constant.EDIT_SIZE_LARGE,
					SketchPadView.STROKE_PEN);
			interactGallery.setDrawSize(Constant.EDIT_SIZE_LARGE,
					SketchPadView.STROKE_PEN);
			hideInteractColor(interactsizemenu);
			break;
		case R.id.answerselectA:
			if (selMap.containsKey(chars[0])) {
				selMap.remove(chars[0]);
				answerselectA.setBackgroundResource(R.drawable.answer_btn);
			} else {
				selMap.put(chars[0], answerselectA);
				answerselectA
						.setBackgroundResource(R.drawable.answer_submit_btn);
			}

			break;
		case R.id.answerselectB:
			if (selMap.containsKey(chars[1])) {
				selMap.remove(chars[1]);
				answerselectB.setBackgroundResource(R.drawable.answer_btn);
			} else {
				selMap.put(chars[1], answerselectB);
				answerselectB
						.setBackgroundResource(R.drawable.answer_submit_btn);
			}
			break;
		case R.id.answerselectC:
			if (selMap.containsKey(chars[2])) {
				selMap.remove(chars[2]);
				answerselectC.setBackgroundResource(R.drawable.answer_btn);
			} else {
				selMap.put(chars[2], answerselectC);
				answerselectC
						.setBackgroundResource(R.drawable.answer_submit_btn);
			}
			break;
		case R.id.answerselectD:
			if (selMap.containsKey(chars[3])) {
				selMap.remove(chars[3]);
				answerselectD.setBackgroundResource(R.drawable.answer_btn);
			} else {
				selMap.put(chars[3], answerselectD);
				answerselectD
						.setBackgroundResource(R.drawable.answer_submit_btn);
			}
			break;
		case R.id.answerselectE:
			if (selMap.containsKey(chars[4])) {
				selMap.remove(chars[4]);
				answerselectE.setBackgroundResource(R.drawable.answer_btn);
			} else {
				selMap.put(chars[4], answerselectE);
				answerselectE
						.setBackgroundResource(R.drawable.answer_submit_btn);
			}
			break;
		case R.id.answerselectcommitlayout:
		case R.id.answerselectcommitview:
		case R.id.answerselectcommittext:
			send();
			break;
		default:
			break;
		}
	}

	private void send() {
		InteractGalleryEntity galleryEntity = interactGallery
				.getCurrentGalleryEntity();
		if (galleryEntity != null) {

			String answer = "";
			Set set = selMap.entrySet();
			for (Iterator iter = set.iterator(); iter.hasNext();) {
				Map.Entry entry = (Map.Entry) iter.next();

				String key = (String) entry.getKey();
				// String value = (String)entry.getValue();

				answer = answer + key;
			}
			if (!"".equals(answer)) {
				String guid = "";
				if (galleryEntity != null) {
					guid = galleryEntity.getItemGuid();
				}
				String postdata = "{\"symbol\":\"" + answer
						+ "\",\"questionguid\":\"" + guid + "\"}";
				RemoteControlTaskAllCanSee controlTask = new RemoteControlTaskAllCanSee(
						CommandTypeEntity.MARCO, postdata);
				controlTask.execute(3);
			}
		}

		resetAnswerBtn();

	}

	public void resetAnswerBtn() {
		selMap.clear();

		answerselectA.setBackgroundResource(R.drawable.answer_btn);
		answerselectB.setBackgroundResource(R.drawable.answer_btn);
		answerselectC.setBackgroundResource(R.drawable.answer_btn);
		answerselectD.setBackgroundResource(R.drawable.answer_btn);
		answerselectE.setBackgroundResource(R.drawable.answer_btn);
	}

	/**
	 * 将笔迹提交到服务器
	 */
	private void commit() {
		Toast.makeText(mContext,
				mContext.getResources().getString(R.string.interact_sending),
				Toast.LENGTH_SHORT).show();
		if (cameraimageview.getVisibility() == View.VISIBLE) {
			String strFilePath = interactGallery
					.saveDrawBitmapToDisk(cameraimageview);
			photoUpload(strFilePath);
			this.hideCameraImageView();
		} else {
			String strFilePath = interactGallery
					.saveDrawBitmapToDisk(interactGallery
							.getCurrentSketchPadView());
			photoUpload(strFilePath);
			interactGallery.getCurrentSketchPadView().clearAllStrokes();
			// cancelEditStatus();
			// Utility.cancelAnswer();
			if (!Constant.forceAnswer) {
				cancelEditStatus();
				Constant.activity.endDrawAnswer();//结束绘画
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		sp.edit().putBoolean("displayAdvance", displayAdvance).commit();

		return super.onKeyDown(keyCode, event);
	}

	float baseValue;// 基值
	boolean isRun = false;

	// 自定义Toast显示位置的信息
	public void toastInfo(String info) {
		toast.setText(info);
		toast.show();
	}

	// 当长按屏幕时,激活上下移动操作
	@Override
	public boolean onLongClick(View v) {
		// 此处激活长按操作

		return false;
	}

	public boolean isImageDownload() {
		boolean isDownloaded = false;
		SketchPadView sketchPadView = interactGallery.getCurrentSketchPadView();
		if (sketchPadView != null && sketchPadView.getBkBitmap() != null
				&& !sketchPadView.getBkBitmap().isRecycled()) {
			isDownloaded = true;
		}
		return isDownloaded;
	}

	public void endEditView() {
		interactGallery.endEditStatus();
		interactGallery.resetInteractLargeView();
	}

	/**
	 * 进入绘画模式
	 */
	public void enterEditView() {
		interactGallery.enterEditStatus();
		interactGallery.resetInteractLargeView();
	}

	public void cancelEditStatus() {
		editView.setVisibility(View.GONE);
		answerselectview.setVisibility(View.GONE);
		chatView.setVisibility(View.VISIBLE);
		interactGallery.getViewPager().setUnscrolled(false);
		chatView.setGalleryEntity(interactGallery.getCurrentGalleryEntity());
		Constant.activity
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}

	public void hideViewPager() {
		interactGallery.getViewPager().setVisibility(View.GONE);
	}

	public void showViewPager() {
		interactGallery.getViewPager().setVisibility(View.VISIBLE);
	}

	/**
	 * 显示绘画帮助控件
	 */
	public void enterEditStatus() {
		chatView.setVisibility(View.GONE);
		editView.setVisibility(View.VISIBLE);
		chatView.getFaceLayout().resetEditStatus();
		chatView.loseFocus();
		if (interactGallery != null) {
			chatView.setGalleryEntity(interactGallery.getCurrentGalleryEntity());
		}
		Constant.activity
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public void enterSelectStatus() {
		chatView.setVisibility(View.GONE);
		editView.setVisibility(View.GONE);
		answerselectview.setVisibility(View.VISIBLE);
		Constant.activity.showTabs();
		// chatView.getFaceLayout().resetEditStatus();
		// chatView.loseFocus();
		if (interactGallery != null) {
			chatView.setGalleryEntity(interactGallery.getCurrentGalleryEntity());
		}
		Constant.activity
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		this.chatView.getFaceLayout().KeyBoardCancle();
	}

	/**
	 * 笔迹颜色控件
	 * @param view
	 * @param positionView
	 */
	public void showInteractColor(final FrameLayout view, View positionView) {
		if (editMaskView.getVisibility() != View.VISIBLE) {
			int tabHostHeight = Constant.activity.getTabHost().getTabWidget()
					.getChildAt(Constant.TAB_INTERACT).getHeight();
			view.setX(positionView.getX()
					- (view.getWidth() - positionView.getWidth()) / 2);
			view.setVisibility(View.VISIBLE);
			editMaskView.setVisibility(View.VISIBLE);
			AnimatorSet set = new AnimatorSet();
			set.play(
					ObjectAnimator.ofFloat(
							view,
							View.Y,
							Constant.screenHeight,
							Constant.screenHeight - tabHostHeight
									- view.getHeight() - Constant.sbar
									- editView.getHeight())).with(
					ObjectAnimator.ofFloat(editMaskView, "alpha", 0f, 1f));
			set.setDuration(150);
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

	public void hideInteractColor(final FrameLayout view) {
		if (editMaskView.getVisibility() == View.VISIBLE) {
			int tabHostHeight = Constant.activity.getTabHost().getTabWidget()
					.getChildAt(Constant.TAB_INTERACT).getHeight();
			AnimatorSet set = new AnimatorSet();
			set.play(
					ObjectAnimator.ofFloat(view, View.Y, Constant.screenHeight
							- tabHostHeight - view.getHeight() - Constant.sbar
							- editView.getHeight(), Constant.screenHeight))
					.with(ObjectAnimator.ofFloat(editMaskView, "alpha", 1f, 0f));
			set.setDuration(150);
			set.setInterpolator(new DecelerateInterpolator());
			set.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					view.setVisibility(View.GONE);
					editMaskView.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});
			set.start();
		}
	}

	@Override
	public void raiseHttpUploadingEvent(String filename, long filesize,
			long uploadingByte, long batchUploadBytes, long totalFileSize) {
		// TODO Auto-generated method stub
		float rate = (float) uploadingByte / filesize * 100;
		if (freshArray != null
				&& IntAreaUtil.isFreshLargeProgress(freshArray, rate)) {
			Log.i(TAG, "总大小" + filesize + ",上传了" + uploadingByte + ";rate="
					+ rate);
			Message msg = new Message();
			msg.obj = rate;
			progressHandler.sendMessage(msg);
		}
	}

	@Override
	public void raiseConnectionRetryFailEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void raiseConnectionWillRetryEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void raiseConnectionRetrySuccessEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void raiseSearchingUpdateWithServiceList(ICELoginEntity[] iceArray) {
		// TODO Auto-generated method stub

	}

	@Override
	public void raiseConnectionFailedEvent(ICELoginEntity LoginICE,
			UserInfoEntity userEntity, ConnectFailedStatus failedStatus) {
		// TODO Auto-generated method stub

	}
}
