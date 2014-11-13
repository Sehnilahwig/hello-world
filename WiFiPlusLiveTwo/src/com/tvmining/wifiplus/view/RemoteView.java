
/**
 * create  time:2013-2-26
 * author:liujianjian
 */
package com.tvmining.wifiplus.view;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.tvmining.sdk.entity.CommandTypeEntity;
import com.tvmining.sdk.entity.NeighbourEntity;
import com.tvmining.wifiplus.application.EmeetingApplication;
import com.tvmining.wifiplus.entity.Permission;
import com.tvmining.wifiplus.thread.RemoteControlTask;
import com.tvmining.wifiplus.thread.RemoteControlTaskToUser;
import com.tvmining.wifiplus.thread.SearchAllScreenTask;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.MessageUtil;
import com.tvmining.wifiplus.util.ResourceUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifiplus.waterfall.adapter.ScreenExpendableAdapter;
import com.tvmining.wifipluseq.R;
/**
 * 遥控器：使用手势判断器来发送上下左右、放大缩小等指令
 * 布局参考remotecontrol_new.xml文件
 */
public class RemoteView extends BaseView implements OnGestureListener,OnDoubleTapListener,OnLongClickListener,OnClickListener {

	public int index = 0;
	
	
	
	public RemoteView(Context context) {
		super(context);
		init(context);
	}
	
	public RemoteView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public RemoteView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	private String TAG="RemoteControlView";
	private TextView match;
	private TextView scancode;
	private TextView remote_menu;//高级
	private TextView remote_cancle;//退出

	private TextView matchname;
	
	private RectF remoteMenuRectF;
	private RectF remoteVibratorRectF;
	private RectF remoteCancelRectF;
	
	
	private TextView remoteHomeLayout;
	private TextView remoteForwardLayout;
	private TextView remoteNextLayout;
	
	private ViewPager viewPager;//用于左右滑动切换遥控指令
	private ArrayList<View> viewList;
	private View view1;//分别代表数字字符指令，表情指令
	private View viewPagerLayout;
	private View showLayout;
	private View controltop;
	private View cmdlayout;
	
	
	private Button ctrlNum;//数字键盘下的ctrl、字符键盘下的ctrl
	
	private RelativeLayout numLayout;//数字指令布局
	private RelativeLayout expLayout;//表情指令布局
	
	
	
	private Button one,two,three,four,five,six,seven,eight,nine,zero,speak;
	private Button charA,charB,charC,charD,charE;
	
	private ImageButton exp1,exp2,exp3,exp4,exp5,exp6,exp7,exp8,exp9,exp10,exp11,exp12,exp13,exp14,exp15,exp16,exp17,exp18,exp19,exp20;
	
	private ImageView currentExp;
	
	private EditText speakContent;
	private Button send;
	private View change;
	
	private View sendLayout;//表情指令布局
	
	
	private Button left,right;
	private RelativeLayout numbercontrol1,numbercontrol2,charcontrol,bottomlayout;
	private RelativeLayout speaklayout;
	private SharedPreferences sp = null;
	private boolean displayAdvance = false;//默认不显示高级下的按钮
	
	private GestureDetector mGestureDetector;
	private MotionEvent touchEvent;
	
	String postdata= "";
	private boolean ctrlSelected = false;//ctrl是否已选中标志
	LayoutInflater inflater = null;
	InputMethodManager imm = null;
	private AsyncTask controlTask = null;
	
	ArrayList<NeighbourEntity> allScreenList = new ArrayList<NeighbourEntity>();//所有搜索到的屏幕信息
	
	private Context mContext;
	
	Toast toast = null;
	Toast toastVibrator;
	ProgressDialog dialog = null;//搜索中控提示信息
	
	private ExpandableListView screenListView;
	private ImageView outsideView;
	
	
	private View controlview;
	private View matchscreen;
	
	public boolean quitOnTouch;
	
	private boolean viewPagerHidden;
	private boolean viewPagerAnim;
	private View remotemasklayout;
	
	final private int VIEW_PAGER_HEIGHT = 350;
	
	final private int charColumn = 5; 
	
	final private int imageColumn = 5; 
	
	final private float scaleH = 0.85f;
	
	private String status = "char";
	
	private MotionEvent singleEvent;
	private MotionEvent doubleEvent;
	private Handler handler = new Handler();
	private Runnable singleRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Constant.matchScreen!=null && viewPagerLayout.getVisibility()==View.GONE && !viewPagerHidden && !viewPagerAnim){
				if(singleEvent != null && singleEvent.getY()>controltop.getHeight() && singleEvent.getY() < Constant.screenHeight - cmdlayout.getHeight()){//当触摸位置的Y坐标大于400才发送确定指令
					toastInfo(mContext.getString(R.string.pauseinfo));
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.TOUCH, "",true);
					controlTask.execute(3);
				}
			}
		}
	};
	
	private Runnable doubleRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Constant.matchScreen!=null && viewPagerLayout.getVisibility()==View.GONE && !viewPagerHidden && !viewPagerAnim){
				if(doubleEvent != null && doubleEvent.getY()>controltop.getHeight() && doubleEvent.getY() < Constant.screenHeight - cmdlayout.getHeight()){//当触摸位置的Y坐标大于400才发送确定指令
					toastInfo(mContext.getString(R.string.okinfo));
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.OK, "",true);
					controlTask.execute(3);
				}
			}
		}
	};
	
	private Runnable topRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			toastInfo(mContext.getString(R.string.updirectioninfo));
			controlTask = new RemoteControlTask(mContext,CommandTypeEntity.UP, "",true);
			controlTask.execute(3);
		}
	};
	
	private Runnable leftRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			toastInfo(mContext.getString(R.string.leftdirectioninfo));
			controlTask = new RemoteControlTask(mContext,CommandTypeEntity.LEFT,"",true);
			controlTask.execute(3);
		}
	};
	
	private Runnable rightRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			toastInfo(mContext.getString(R.string.rightdirectioninfo));
			controlTask = new RemoteControlTask(mContext,CommandTypeEntity.RIGHT,"",true);
			controlTask.execute(3);
		}
	};
	
	private Runnable downRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			toastInfo(mContext.getString(R.string.downdirectioninfo));
			controlTask = new RemoteControlTask(mContext,CommandTypeEntity.DOWN, "",true);
			controlTask.execute(3);
		}
	};
	
	private Runnable forceFollowOffRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			postdata = "{\"power\":\"off\",\"action\":\"follow\",\"guid\":\"\"}";
			controlTask = new RemoteControlTaskToUser(mContext,CommandTypeEntity.FORCE, postdata,false,null);
			controlTask.execute(3);
		}
	};
	
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	
	private int mode = NONE;
	private float oldDist;
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private ScreenExpendableAdapter adapter = null;
	
	public void init(Context context) {
		mContext = context;
		
		inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View remoteControlView = (View) inflater.inflate(R.layout.remotecontrol_new,null, true);
		remoteControlView.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		
		imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		displayAdvance = sp.getBoolean("displayAdvance", false);//初始打开遥控器时不显示高级下的按钮
		Log.i(TAG, "高级按钮是否显示:"+displayAdvance);
		
		remotemasklayout = remoteControlView.findViewById(R.id.unusedlayout);
		remotemasklayout.setOnClickListener(this);
		match = (TextView)remoteControlView.findViewById(R.id.match);
		
		scancode = (TextView)remoteControlView.findViewById(R.id.scancode);
		toast = Toast.makeText(mContext,"", Toast.LENGTH_SHORT);
		toastVibrator = Toast.makeText(mContext,"", Toast.LENGTH_SHORT);
		toastVibrator.setGravity(Gravity.CENTER, 0,0);
		toastVibrator.setDuration(300);

		remote_menu = (TextView)remoteControlView.findViewById(R.id.remote_menu);
		remote_cancle = (TextView)remoteControlView.findViewById(R.id.remote_cancle);
		
		controltop = remoteControlView.findViewById(R.id.controltop);
		cmdlayout = remoteControlView.findViewById(R.id.cmdlayout);
		
		controltop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		matchname = (TextView) remoteControlView.findViewById(R.id.matchname);
		
		viewPager = (ViewPager)remoteControlView.findViewById(R.id.viewpager);
		viewPagerLayout = remoteControlView.findViewById(R.id.viewpagerlayout);
		showLayout = remoteControlView.findViewById(R.id.showlayout);
		
		view1 = inflater.inflate(R.layout.remotecontrol_num,null);
		
		expLayout = (RelativeLayout) view1.findViewById(R.id.explayout);
		sendLayout = view1.findViewById(R.id.sendlayout);
		
		
		
		
		viewList = new ArrayList<View>();
		viewList.add(view1);
		
		PagerAdapter pageAdapter = new PagerAdapter() {		
			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				container.removeView(viewList.get(position));
			}
			@Override
			public int getItemPosition(Object object) {
				return super.getItemPosition(object);
			}
			@Override
			public CharSequence getPageTitle(int position) {
				return super.getPageTitle(position);
			}
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(viewList.get(position));
				return viewList.get(position);
			}
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}		
			@Override
			public int getCount() {
				return viewList.size();
			}
		};
		viewPager.setAdapter(pageAdapter);
		
		numLayout = (RelativeLayout)view1.findViewById(R.id.numlayout);
		
		one = (Button)view1.findViewById(R.id.one);
		two = (Button)view1.findViewById(R.id.two);
		three = (Button)view1.findViewById(R.id.three);
		four = (Button)view1.findViewById(R.id.four);
		five = (Button)view1.findViewById(R.id.five);
		six = (Button)view1.findViewById(R.id.six);
		seven = (Button)view1.findViewById(R.id.seven);
		eight = (Button)view1.findViewById(R.id.eight);
		nine = (Button)view1.findViewById(R.id.nine);
		zero = (Button)view1.findViewById(R.id.zero);
		
		ctrlNum = (Button)view1.findViewById(R.id.ctrl_num);
		
		charA = (Button)view1.findViewById(R.id.chara);
		charB = (Button)view1.findViewById(R.id.charb);
		charC = (Button)view1.findViewById(R.id.charc);
		charD = (Button)view1.findViewById(R.id.chard);
		charE = (Button)view1.findViewById(R.id.chare);
		
		remoteHomeLayout = (TextView)view1.findViewById(R.id.remoteHomeLayout);
		remoteHomeLayout.setOnClickListener(this);
		
		remoteForwardLayout = (TextView)view1.findViewById(R.id.forwardLayout);
		remoteForwardLayout.setOnClickListener(this);
		
		remoteNextLayout = (TextView)view1.findViewById(R.id.nextLayout);
		remoteNextLayout.setOnClickListener(this);
		setAdvanceViewVisibility();
		
		one.getLayoutParams().width = Constant.screenWidth / charColumn;
		two.getLayoutParams().width = Constant.screenWidth / charColumn;
		three.getLayoutParams().width = Constant.screenWidth / charColumn;
		four.getLayoutParams().width = Constant.screenWidth / charColumn;
		five.getLayoutParams().width = Constant.screenWidth / charColumn;
		six.getLayoutParams().width = Constant.screenWidth / charColumn;
		seven.getLayoutParams().width = Constant.screenWidth / charColumn;
		eight.getLayoutParams().width = Constant.screenWidth / charColumn;
		nine.getLayoutParams().width = Constant.screenWidth / charColumn;
		zero.getLayoutParams().width = Constant.screenWidth / charColumn;
		ctrlNum.getLayoutParams().width = Constant.screenWidth / charColumn;
		
		charA.getLayoutParams().width = Constant.screenWidth / charColumn;
		charB.getLayoutParams().width = Constant.screenWidth / charColumn;
		charC.getLayoutParams().width = Constant.screenWidth / charColumn;
		charD.getLayoutParams().width = Constant.screenWidth / charColumn;
		charE.getLayoutParams().width = Constant.screenWidth / charColumn;
		
		remoteHomeLayout.getLayoutParams().width = (Constant.screenWidth * 2) / charColumn;
		remoteForwardLayout.getLayoutParams().width = Constant.screenWidth / charColumn;
		remoteNextLayout.getLayoutParams().width = Constant.screenWidth / charColumn;
		
		one.getLayoutParams().height = (int)(one.getLayoutParams().width * scaleH);
		two.getLayoutParams().height = (int)(two.getLayoutParams().width * scaleH);
		three.getLayoutParams().height = (int)(three.getLayoutParams().width * scaleH);
		four.getLayoutParams().height = (int)(four.getLayoutParams().width * scaleH);
		five.getLayoutParams().height = (int)(five.getLayoutParams().width * scaleH);
		six.getLayoutParams().height = (int)(six.getLayoutParams().width * scaleH);
		seven.getLayoutParams().height = (int)(seven.getLayoutParams().width * scaleH);
		eight.getLayoutParams().height = (int)(eight.getLayoutParams().width * scaleH);
		nine.getLayoutParams().height = (int)(nine.getLayoutParams().width * scaleH);
		zero.getLayoutParams().height = (int)(zero.getLayoutParams().width * scaleH);
		ctrlNum.getLayoutParams().height = (int)(ctrlNum.getLayoutParams().width * scaleH);
		
		charA.getLayoutParams().height = (int)(charA.getLayoutParams().width * scaleH);
		charB.getLayoutParams().height = (int)(charB.getLayoutParams().width * scaleH);
		charC.getLayoutParams().height = (int)(charC.getLayoutParams().width * scaleH);
		charD.getLayoutParams().height = (int)(charD.getLayoutParams().width * scaleH);
		charE.getLayoutParams().height = (int)(charE.getLayoutParams().width * scaleH);
		
		
		remoteHomeLayout.getLayoutParams().height = (int)(remoteHomeLayout.getLayoutParams().width * scaleH);
		remoteForwardLayout.getLayoutParams().height = (int)(remoteForwardLayout.getLayoutParams().width * scaleH);
		remoteNextLayout.getLayoutParams().height = (int)(remoteNextLayout.getLayoutParams().width * scaleH);
		
		currentExp = (ImageView)remoteControlView.findViewById(R.id.currentexp);
		
		exp1 = (ImageButton)view1.findViewById(R.id.exp1);
		exp2 = (ImageButton)view1.findViewById(R.id.exp2);
		exp3 = (ImageButton)view1.findViewById(R.id.exp3);
		exp4 = (ImageButton)view1.findViewById(R.id.exp4);
		exp5 = (ImageButton)view1.findViewById(R.id.exp5);
		exp6 = (ImageButton)view1.findViewById(R.id.exp6);
		exp7 = (ImageButton)view1.findViewById(R.id.exp7);
		exp8 = (ImageButton)view1.findViewById(R.id.exp8);
		exp9 = (ImageButton)view1.findViewById(R.id.exp9);
		exp10 = (ImageButton)view1.findViewById(R.id.exp10);
		exp11 = (ImageButton)view1.findViewById(R.id.exp11);
		exp12 = (ImageButton)view1.findViewById(R.id.exp12);
		exp13 = (ImageButton)view1.findViewById(R.id.exp13);
		exp14 = (ImageButton)view1.findViewById(R.id.exp14);
		exp15 = (ImageButton)view1.findViewById(R.id.exp15);
		exp16 = (ImageButton)view1.findViewById(R.id.exp16);
		exp17 = (ImageButton)view1.findViewById(R.id.exp17);
		exp18 = (ImageButton)view1.findViewById(R.id.exp18);
		exp19 = (ImageButton)view1.findViewById(R.id.exp19);
		exp20 = (ImageButton)view1.findViewById(R.id.exp20);
		
		
		exp1.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp2.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp3.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp4.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp5.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp6.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp7.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp8.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp9.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp10.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp11.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp12.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp13.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp14.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp15.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp16.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp17.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp18.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp19.getLayoutParams().width = Constant.screenWidth / imageColumn;
		exp20.getLayoutParams().width = Constant.screenWidth / imageColumn;
		
		exp1.getLayoutParams().height = (int)(exp1.getLayoutParams().width * scaleH);
		exp2.getLayoutParams().height = (int)(exp2.getLayoutParams().width * scaleH);
		exp3.getLayoutParams().height = (int)(exp3.getLayoutParams().width * scaleH);
		exp4.getLayoutParams().height = (int)(exp4.getLayoutParams().width * scaleH);
		exp5.getLayoutParams().height = (int)(exp5.getLayoutParams().width * scaleH);
		exp6.getLayoutParams().height = (int)(exp6.getLayoutParams().width * scaleH);
		exp7.getLayoutParams().height = (int)(exp7.getLayoutParams().width * scaleH);
		exp8.getLayoutParams().height = (int)(exp8.getLayoutParams().width * scaleH);
		exp9.getLayoutParams().height = (int)(exp9.getLayoutParams().width * scaleH);
		exp10.getLayoutParams().height = (int)(exp10.getLayoutParams().width * scaleH);
		exp11.getLayoutParams().height = (int)(exp11.getLayoutParams().width * scaleH);
		exp12.getLayoutParams().height = (int)(exp12.getLayoutParams().width * scaleH);
		exp13.getLayoutParams().height = (int)(exp13.getLayoutParams().width * scaleH);
		exp14.getLayoutParams().height = (int)(exp14.getLayoutParams().width * scaleH);
		exp15.getLayoutParams().height = (int)(exp15.getLayoutParams().width * scaleH);
		exp16.getLayoutParams().height = (int)(exp16.getLayoutParams().width * scaleH);
		exp17.getLayoutParams().height = (int)(exp17.getLayoutParams().width * scaleH);
		exp18.getLayoutParams().height = (int)(exp18.getLayoutParams().width * scaleH);
		exp19.getLayoutParams().height = (int)(exp19.getLayoutParams().width * scaleH);
		exp20.getLayoutParams().height = (int)(exp20.getLayoutParams().width * scaleH);
		

		
		speakContent = (EditText)view1.findViewById(R.id.content);
		speakContent.setFocusable(false);
		speakContent.setFocusableInTouchMode(false);
		speakContent.setCursorVisible(false);
		speakContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(speakContent.hasFocus()){
					speakContent.setCursorVisible(true);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}else{
					speakContent.setCursorVisible(false);
				}
				
			}
		}); 
		speakContent.setOnClickListener(this);

		send = (Button)view1.findViewById(R.id.send);
		
		change = view1.findViewById(R.id.change);
	
		
		
		
//		forcefollow.setOnClickListener(this);
//		remotemenu.setOnClickListener(this);
//		remotevibrator.setOnClickListener(this);
//		remotecancel.setOnClickListener(this);
		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);
		four.setOnClickListener(this);
		five.setOnClickListener(this);
		six.setOnClickListener(this);
		seven.setOnClickListener(this);
		eight.setOnClickListener(this);
		nine.setOnClickListener(this);
		zero.setOnClickListener(this);
		
		ctrlNum.setOnClickListener(this);	
		
		charA.setOnClickListener(this);
		charB.setOnClickListener(this);
		charC.setOnClickListener(this);
		charD.setOnClickListener(this);
		charE.setOnClickListener(this);
		
		exp1.setOnClickListener(this);
		exp2.setOnClickListener(this);
		exp3.setOnClickListener(this);
		exp4.setOnClickListener(this);
		exp5.setOnClickListener(this);
		exp6.setOnClickListener(this);
		exp7.setOnClickListener(this);
		exp8.setOnClickListener(this);
		exp9.setOnClickListener(this);
		
		exp20.setOnClickListener(this);
		send.setOnClickListener(this);
		change.setOnClickListener(this);


		viewPagerLayout.setOnClickListener(this);
		
		remote_cancle.setOnClickListener(this);
		remote_menu.setOnClickListener(this);
		scancode.setOnClickListener(this);
		
		mGestureDetector = new GestureDetector(this);
		
		matchscreen = remoteControlView.findViewById(R.id.matchscreen);
		controlview = remoteControlView.findViewById(R.id.advancelayout);
		//定义匹配popwindow
		screenListView = (ExpandableListView) remoteControlView.findViewById(R.id.screenlistview);	
		screenListView.getBackground().setAlpha(255*40/100);

		matchscreen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideMatchscreen();
			}
		});
		this.addView(remoteControlView);
	}
	
	public void showMenuOption(){
		viewPagerLayout.setVisibility(View.VISIBLE);
		int height = showLayout.getHeight();
		if(height == 0){
			height = Constant.lc.dpToPixel(VIEW_PAGER_HEIGHT);
		}
		TranslateAnimation anim = new TranslateAnimation(0,
				0, height,
				0);
		anim.setFillAfter(true);
		anim.setDuration(250);
		anim.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
			}
		});
		showLayout.startAnimation(anim);
	}
	
	private void closeMenuOption(){
		
		if(!viewPagerAnim){
			viewPagerHidden = false;
			if(viewPagerLayout.getVisibility()==View.VISIBLE){
				
				if(speakContent.hasFocus()){
					imm.hideSoftInputFromWindow(speakContent.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					speakContent.setFocusable(false);
					speakContent.setFocusableInTouchMode(false);
					speakContent.setCursorVisible(false);
				}else{
					viewPagerHidden = true;
					viewPagerAnim = true;
					if(viewPager.getCurrentItem() == 1){
						imm.hideSoftInputFromWindow(speakContent.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
					
					int height = showLayout.getHeight();
					if(height == 0){
//						height = EmeetingApplication.lc.dpToPixel(VIEW_PAGER_HEIGHT);
					}
					
					TranslateAnimation anim = new TranslateAnimation(0,
							0, 0,
							height);
					anim.setFillAfter(true);
					anim.setDuration(250);
					anim.setAnimationListener(new Animation.AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub
							viewPagerAnim = false;
							viewPagerHidden = false;
							viewPagerLayout.setVisibility(View.GONE);
							viewPager.setCurrentItem(0);
						}
					});
					showLayout.startAnimation(anim);
				}
			}	
		}
		
	}
	
	public void setAdvanceViewVisibility(){
		if(Constant.user != null && !Permission.PERMISSION_HIGH.equals(Constant.user.getPermisssion().getLevel())){
			if(ctrlNum != null){
				((Button)ctrlNum).setText("");
				ctrlNum.setClickable(false);
				ctrlNum.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d0d3d9")));
			}
		}else{
			// 暂时不显示airPlay和iceShow
			if(ctrlNum != null){
				((Button)ctrlNum).setText(R.string.ctrl);
				ctrlNum.setClickable(true);
				ctrlNum.setBackgroundResource(R.drawable.cmd_btn);
			}
		}
	}
	

	public void setMaskShow(){
		if(Constant.allScreenList.size() > 0){
			String name = "";
			if(Constant.matchScreen != null){
				for(int i=0;i<Constant.matchScreen.size();i++){
					NeighbourEntity nei = Constant.matchScreen.get(i);
					name += nei.tvmId+" ";
				}
				matchname.setText(name);
			}
			remotemasklayout.setVisibility(View.GONE);
			match.setOnClickListener(this);	
		}else{
			remotemasklayout.setVisibility(View.VISIBLE);
			match.setOnClickListener(null);	
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.match:
				//每次点击重新查询所有可匹配的屏幕,与已经匹配的屏幕对照  已经匹配的屏幕显示选中状态
				if(matchscreen.getVisibility() == View.GONE){
					matchscreen.setVisibility(View.VISIBLE);
					controlview.setVisibility(View.GONE);
					match.setTextColor(Color.rgb(0, 187, 224));
					
					if(Constant.allScreenList.size()>0){
						showMatchscreen();
						adapter = new ScreenExpendableAdapter(mContext,Constant.allScreenList);
						adapter.setMatchname(matchname);
						screenListView.setAdapter(adapter);
						screenListView.setGroupIndicator(null);
						
						screenListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
							
							@Override
							public void onGroupExpand(int groupPosition) {
								// TODO Auto-generated method stub
								if(Constant.allScreenList.get(groupPosition).applist == null 
										|| Constant.allScreenList.get(groupPosition).applist.size() == 0)
									Toast.makeText(mContext, "no child", Toast.LENGTH_SHORT).show();
								for (int i = 0; i < adapter.getGroupCount(); i++) {  
				                    if (groupPosition != i) {  
				                    	screenListView.collapseGroup(i);  
				                    }  
				                }  
							}
						});	
					}
					
					/*Handler searchScreenHandler = new Handler() {
						public void handleMessage(Message msg) {
							if(Constant.allScreenList.size()>0){
								showMatchscreen();
								adapter = new ScreenExpendableAdapter(mContext,Constant.allScreenList);
								adapter.setMatchname(matchname);
								screenListView.setAdapter(adapter);
								screenListView.setGroupIndicator(null);
								
								screenListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
									
									@Override
									public void onGroupExpand(int groupPosition) {
										// TODO Auto-generated method stub
										if(Constant.allScreenList.get(groupPosition).applist == null 
												|| Constant.allScreenList.get(groupPosition).applist.size() == 0)
											Toast.makeText(mContext, "no child", Toast.LENGTH_SHORT).show();
										for (int i = 0; i < adapter.getGroupCount(); i++) {  
						                    if (groupPosition != i) {  
						                    	screenListView.collapseGroup(i);  
						                    }  
						                }  
									}
								});	
							}else{
								MessageUtil.toastInfo(Constant.activity, mContext.getString(R.string.not_search_screen));
							}
						}
					};
					new SearchAllScreenTask(mContext,"send",searchScreenHandler).execute();*/
				}else {
					hideMatchscreen();
				}				
				break;
			case R.id.scancode:
				/*扫描二维码*/
				Message msg = new Message();
				msg.what = Constant.HANDLER_LOCAL_QRCODE_SHOW;
				Constant.activity.getHandler().sendMessage(msg);
				break;
				
			case R.id.remote_menu:
				if(Constant.matchScreen!=null){
					toastInfo(mContext.getString(R.string.remotemenu));
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MENU, "",true);
					controlTask.execute(3);
				}
				break;
			case R.id.remote_cancle:
				if(Constant.matchScreen!=null){
					toastInfo(mContext.getString(R.string.remotecancel));
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.CANCEL,"",true);
					controlTask.execute(3);
				}
				break;
			case R.id.content:
				speakContent.setFocusable(true);
				speakContent.setFocusableInTouchMode(true);
				
				speakContent.requestFocus();
				break;
			case R.id.forwardLayout:
				if(Constant.matchScreen!=null){
					toastInfo(mContext.getString(R.string.leftinfo));
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.BACKWARD, "",true);
					controlTask.execute(3);
				}
				break;
			case R.id.nextLayout:
				if(Constant.matchScreen!=null){
					toastInfo(mContext.getString(R.string.rightinfo));
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.FORWARD, "",true);
					controlTask.execute(3);
				}
				break;
			case R.id.remoteHomeLayout:
				closeMenuOption();
				if(Constant.matchScreen!=null){
					toastInfo(mContext.getString(R.string.home));
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.HOME,"",true);
					controlTask.execute(3);
				}
				break;
			case R.id.viewpagerlayout:
				closeMenuOption();
				break;
				
			case R.id.one:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrloneinfo));
						postdata = "{\"app\":\"1\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.oneinfo));
						postdata = "{\"symbol\":\"1\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.two:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrlsecondinfo));
						postdata = "{\"app\":\"2\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.secondinfo));
						postdata = "{\"symbol\":\"2\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.three:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrlthirdinfo));
						postdata = "{\"app\":\"3\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.thirdinfo));
						postdata = "{\"symbol\":\"3\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.four:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrlfourthinfo));
						postdata = "{\"app\":\"4\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.fourthinfo));
						postdata = "{\"symbol\":\"4\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.five:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrlfifthinfo));
						postdata = "{\"app\":\"5\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.fifthinfo));
						postdata = "{\"symbol\":\"5\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.six:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrlsixthinfo));
						postdata = "{\"app\":\"6\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.sixthinfo));
						postdata = "{\"symbol\":\"6\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.seven:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrlseventhinfo));
						postdata = "{\"app\":\"7\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.seventhinfo));
						postdata = "{\"symbol\":\"7\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.eight:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrleighthinfo));
						postdata = "{\"app\":\"8\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.eighthinfo));
						postdata = "{\"symbol\":\"8\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.nine:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrlninthinfo));
						postdata = "{\"app\":\"9\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.ninthinfo));
						postdata = "{\"symbol\":\"9\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.zero:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrlzeroinfo));
						postdata = "{\"app\":\"0\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.zeroinfo));
						postdata = "{\"symbol\":\"0\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.ctrl_num:
				if(ctrlSelected){//拿当前背景和黑色背景比对
					ctrlSelected = false;
					ctrlNum.setBackgroundResource(R.drawable.cmd_btn);					
					ctrlNum.setTextColor(Color.GRAY);
				}else{
					ctrlSelected = true;
					ctrlNum.setBackgroundResource(R.drawable.ctrl_click);					
					ctrlNum.setTextColor(Color.WHITE);
				}				
				break;
			case R.id.chara:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrlainfo));
						postdata = "{\"app\":\"A\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.ainfo));
						postdata = "{\"symbol\":\"A\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.charb:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrlbinfo));
						postdata = "{\"app\":\"B\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.binfo));
						postdata = "{\"symbol\":\"B\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.charc:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrlcinfo));
						postdata = "{\"app\":\"C\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.cinfo));
						postdata = "{\"symbol\":\"C\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.chard:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrldinfo));
						postdata = "{\"app\":\"D\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.dinfo));
						postdata = "{\"symbol\":\"D\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;				
			case R.id.chare:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					if(ctrlSelected){
						toastInfo(mContext.getString(R.string.ctrleinfo));
						postdata = "{\"app\":\"E\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.STARTOVER,postdata,true);
						ctrlSelected = false;
					}else{
						toastInfo(mContext.getString(R.string.einfo));
						postdata = "{\"symbol\":\"E\"}";
						Log.i(TAG, "postdata="+postdata);
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.MARCO,postdata,true);
					}
					controlTask.execute(3);
				}
				break;
			case R.id.left:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					toastInfo(mContext.getString(R.string.leftinfo));
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.BACKWARD, "",true);
					controlTask.execute(3);
				}
				break;
			case R.id.right:
				recoverCtrlBackGround();//恢复ctrl背景色
				if(Constant.matchScreen!=null){
					toastInfo(mContext.getString(R.string.rightinfo));
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.FORWARD, "",true);
					controlTask.execute(3);
				}
				break;
//			case R.id.speak:
//				if(Constant.matchScreen!=null){
//					if(speaklayout.getVisibility()==View.INVISIBLE){
//						charcontrol.setVisibility(View.GONE);
//						bottomlayout.setVisibility(View.GONE);
//						speaklayout.setVisibility(View.VISIBLE);
//						imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//					}else{
//						charcontrol.setVisibility(View.VISIBLE);
//						bottomlayout.setVisibility(View.INVISIBLE);
//						speaklayout.setVisibility(View.INVISIBLE);
//					}
//				}
//				Log.i(TAG, "点击了发言");
//				break;
			case R.id.send:
				if(Constant.matchScreen!=null){
					toastInfo(mContext.getString(R.string.sendinfo)+speakContent.getText().toString());
					postdata = "{\"msg\":\""+ speakContent.getText().toString()  +"\"}";
					Log.i(TAG, "发言内容："+postdata);
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.TALKTO,postdata,true);
					controlTask.execute(3);
				}
				imm.hideSoftInputFromWindow(speakContent.getWindowToken(), 0);
				speakContent.setText("");
				break;
			case R.id.change:
				speakContent.setFocusable(false);
				speakContent.setFocusableInTouchMode(false);
				speakContent.setCursorVisible(false);
				if(imm.isActive(speakContent)){
					imm.hideSoftInputFromWindow(speakContent.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				if("char".equals(status)){
					change.setClickable(false);
					status = "exp";
					change.setBackgroundResource(R.drawable.btn_abc);
					numLayout.setVisibility(View.GONE);
					expLayout.setVisibility(View.VISIBLE);
					change.setClickable(true);
					
					AnimatorSet set = new AnimatorSet();
			        set.play(ObjectAnimator.ofFloat(expLayout, View.Y, numLayout.getHeight(),
			        		numLayout.getTop()));
			        set.setDuration(200);
			        set.addListener(new AnimatorListenerAdapter() {
			            @Override
			            public void onAnimationEnd(Animator animation) {
			            	
			            }

			            @Override
			            public void onAnimationCancel(Animator animation) {
			            	
			            }
			        });
			        set.setInterpolator(new DecelerateInterpolator());
			        set.start();
				}else{
					change.setClickable(false);
					status = "char";
					change.setBackgroundResource(R.drawable.btn_exp);
					numLayout.setVisibility(View.VISIBLE);
					expLayout.setVisibility(View.GONE);
					change.setClickable(true);
					
					AnimatorSet set = new AnimatorSet();
			        set.play(ObjectAnimator.ofFloat(numLayout, View.Y, numLayout.getHeight(),
			        		numLayout.getTop()));
			        set.setDuration(200);
			        set.addListener(new AnimatorListenerAdapter() {
			            @Override
			            public void onAnimationEnd(Animator animation) {
			            	
			            }

			            @Override
			            public void onAnimationCancel(Animator animation) {
			            	
			            }
			        });
			        set.setInterpolator(new DecelerateInterpolator());
			        set.start();
				}
				
				break;
			case R.id.exp1:
				if(Constant.matchScreen!=null){					
					currentExp.setImageResource(R.drawable.exp1);
					doStartAnimation(R.anim.tween_alpha);					
					
					toastInfo(mContext.getString(R.string.sendinfo)+mContext.getString(R.string.exp1));
					postdata = "{\"msg\":\""+ mContext.getString(R.string.exp1)  +"\"}";
					Log.i(TAG, "发言内容："+postdata);
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.TALKTO,postdata,true);
					controlTask.execute(3);
				}
				break;
			case R.id.exp2:
				if(Constant.matchScreen!=null){
					currentExp.setImageResource(R.drawable.exp2);
					doStartAnimation(R.anim.tween_alpha);
					toastInfo(mContext.getString(R.string.sendinfo)+mContext.getString(R.string.exp2));
					postdata = "{\"msg\":\""+ mContext.getString(R.string.exp2)  +"\"}";
					Log.i(TAG, "发言内容："+postdata);
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.TALKTO,postdata,true);
					controlTask.execute(3);
				}
				break;
			case R.id.exp3:
				if(Constant.matchScreen!=null){
					currentExp.setImageResource(R.drawable.exp3);
					doStartAnimation(R.anim.tween_alpha);
					toastInfo(mContext.getString(R.string.sendinfo)+mContext.getString(R.string.exp3));
					postdata = "{\"msg\":\""+ mContext.getString(R.string.exp3)  +"\"}";
					Log.i(TAG, "发言内容："+postdata);
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.TALKTO,postdata,true);
					controlTask.execute(3);
				}
				break;
			case R.id.exp4:
				if(Constant.matchScreen!=null){
					currentExp.setImageResource(R.drawable.exp4);
					doStartAnimation(R.anim.tween_alpha);
					toastInfo(mContext.getString(R.string.sendinfo)+mContext.getString(R.string.exp4));
					postdata = "{\"msg\":\""+ mContext.getString(R.string.exp4)  +"\"}";
					Log.i(TAG, "发言内容："+postdata);
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.TALKTO,postdata,true);
					controlTask.execute(3);
				}
				break;
			case R.id.exp5:
				if(Constant.matchScreen!=null){
					currentExp.setImageResource(R.drawable.exp5);
					doStartAnimation(R.anim.tween_alpha);
					toastInfo(mContext.getString(R.string.sendinfo)+mContext.getString(R.string.exp5));
					postdata = "{\"msg\":\""+ mContext.getString(R.string.exp5)  +"\"}";
					Log.i(TAG, "发言内容："+postdata);
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.TALKTO,postdata,true);
					controlTask.execute(3);
				}
				break;
			case R.id.exp6:
				if(Constant.matchScreen!=null){
					currentExp.setImageResource(R.drawable.exp6);
					doStartAnimation(R.anim.tween_alpha);
					toastInfo(mContext.getString(R.string.sendinfo)+mContext.getString(R.string.exp6));
					postdata = "{\"msg\":\""+ mContext.getString(R.string.exp6)  +"\"}";
					Log.i(TAG, "发言内容："+postdata);
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.TALKTO,postdata,true);
					controlTask.execute(3);
				}
				break;
			case R.id.exp7:
				if(Constant.matchScreen!=null){
					currentExp.setImageResource(R.drawable.exp7);
					doStartAnimation(R.anim.tween_alpha);
					toastInfo(mContext.getString(R.string.sendinfo)+mContext.getString(R.string.exp7));
					postdata = "{\"msg\":\""+ mContext.getString(R.string.exp7)  +"\"}";
					Log.i(TAG, "发言内容："+postdata);
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.TALKTO,postdata,true);
					controlTask.execute(3);
				}
				break;
			case R.id.exp8:
				if(Constant.matchScreen!=null){
					currentExp.setImageResource(R.drawable.exp8);
					doStartAnimation(R.anim.tween_alpha);
					toastInfo(mContext.getString(R.string.sendinfo)+mContext.getString(R.string.exp8));
					postdata = "{\"msg\":\""+ mContext.getString(R.string.exp8)  +"\"}";
					Log.i(TAG, "发言内容："+postdata);
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.TALKTO,postdata,true);
					controlTask.execute(3);
				}
				break;
			case R.id.exp9:
				if(Constant.matchScreen!=null){
					currentExp.setImageResource(R.drawable.exp9);
					doStartAnimation(R.anim.tween_alpha);
					toastInfo(mContext.getString(R.string.sendinfo)+mContext.getString(R.string.exp9));
					postdata = "{\"msg\":\""+ mContext.getString(R.string.exp9)  +"\"}";
					Log.i(TAG, "发言内容："+postdata);
					controlTask = new RemoteControlTask(mContext,CommandTypeEntity.TALKTO,postdata,true);
					controlTask.execute(3);
				}
				break;
			case R.id.exp20:
				break;
			default:
				break;
		}
	}

	//恢复ctrl的选中颜色
	public void recoverCtrlBackGround(){
		/*if(Constant.user!=null && Constant.user.getPermisssion().getLevel().equals(Permission.PERMISSION_HIGH)){
			ctrlNum.setBackgroundResource(R.drawable.cmd_btn);
			ctrlNum.setTextColor(Color.GRAY);
		}*/
		if(ctrlSelected){
			ctrlNum.setBackgroundResource(R.drawable.cmd_btn);
			ctrlNum.setTextColor(Color.GRAY);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		sp.edit().putBoolean("displayAdvance",displayAdvance).commit();
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.i(TAG, "onSingleTapUp");
		if(!isRun){
			singleEvent = e;
			handler.postDelayed(singleRunnable,200);
		}
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
//		Log.i(TAG, "onFling e1.getX="+e1.getX()+";e2.getX="+e2.getX());		
		//区分是发送遥控器指令、还是打开高级界面或者关闭遥控器界面
		Log.i(TAG,"onFling");
		//只有当高级键盘不显示时才响应手势事件
		if(viewPagerLayout.getVisibility()==View.GONE && !viewPagerHidden && !viewPagerAnim){
//			if(e1.getY()>EmeetingApplication.screenHeight*1/2){
			if(false){
				//此处发送的是打开高级选项以及关闭遥控器功能	
				if(e1.getX()>Constant.screenWidth/2){
					//执行关闭遥控器操作
				}
				
			}else if(viewPagerLayout.getVisibility()!=View.VISIBLE && e1.getY()>controltop.getHeight() && e1.getY()<Constant.screenHeight - cmdlayout.getHeight()){
				//此处发送的是遥控器指令
				//根据水平移动距离和垂直移动距离的大小判断是水平操作还是垂直操作
				if(Constant.matchScreen!=null){
					float horizontalDistance = Math.abs(e1.getX()-e2.getX());
					float verticalDistance = Math.abs(e1.getY()-e2.getY());
					if(horizontalDistance>verticalDistance){//当前是水平操作
						if(e1.getX()>e2.getX()){//左划操作
							handler.postDelayed(leftRunnable,200);
						}else if(e2.getX()>e1.getX()){//右滑
							handler.postDelayed(rightRunnable,200);
						}
					}else if(verticalDistance>horizontalDistance){//当前是垂直操作
						if(e1.getY()>e2.getY()){//上滑操作
							/*toastInfo(mContext.getString(R.string.updirectioninfo));
							controlTask = new RemoteControlTask(mContext,CommandTypeEntity.UP, "",EmeetingApplication.matchScreen,true);
							controlTask.execute(3);*/
							
							handler.postDelayed(topRunnable,200);
							
						}else if(e2.getY()>e1.getY()){//下滑操作
							handler.postDelayed(downRunnable,200);
						}
					}
				}
			}
		}			
		return false;
	}
	float baseValue;//基值
	boolean isRun = false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!isRun){
			mGestureDetector.onTouchEvent(event);
		}

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			/*if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else */if (mode == ZOOM) {
				isRun = true;
				handler.removeCallbacks(topRunnable);
				handler.removeCallbacks(downRunnable);
				handler.removeCallbacks(leftRunnable);
				handler.removeCallbacks(rightRunnable);
				
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					if(scale > 1){
						toastInfo(mContext.getString(R.string.zoomininfo));
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.ZOOMIN, "",true);
						controlTask.execute(3);
					}else if(scale < 1){
						toastInfo(mContext.getString(R.string.zoomoutinfo));
						controlTask = new RemoteControlTask(mContext,CommandTypeEntity.ZOOMOUT, "",true);
						controlTask.execute(3);
					}
//					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}
		
		if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
			isRun = false;
		}
		
		return true;
	}

	//-----------------以下是OnDoubleTapListener的接口方法
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Log.i(TAG, "onDoubleTap");
		/*if(EmeetingApplication.matchScreen!=null && viewPagerLayout.getVisibility()==View.GONE && !viewPagerHidden && !viewPagerAnim){
			if(e.getY()>controltop.getHeight() && e.getY() < EmeetingApplication.screenHeight - cmdlayout.getHeight()){//当触摸位置的Y坐标大于400才发送确定指令
				toastInfo(mContext.getString(R.string.okinfo));
				controlTask = new RemoteControlTask(mContext,CommandTypeEntity.OK, "",EmeetingApplication.matchScreen,true);
				controlTask.execute(3);
			}
		}*/
		if(!isRun){
			doubleEvent = e;
			handler.removeCallbacks(singleRunnable);
			handler.postDelayed(doubleRunnable,200);
		}
		
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}
	//---------------------------------------------------
	
	//自定义Toast显示位置的信息
	public void toastInfo(String info){
		toast.setText(info);
		toast.show();
	}

	
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	};
	//当长按屏幕时,激活上下移动操作
	@Override
	public boolean onLongClick(View v) {
		//此处激活长按操作
		
		return false;
	}
	private void doStartAnimation(int animId) {         
        Animation animation = AnimationUtils.loadAnimation(mContext, animId);         
        currentExp.startAnimation(animation); 
        animation.setAnimationListener(new AnimationListener() {			
			@Override
			public void onAnimationStart(Animation animation) {				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				currentExp.setImageBitmap(null);
			}
		});
    }	
	
	
	public void showMatchscreen(){
		AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(matchscreen, View.Y, -Constant.screenHeight+125,125));
        set.setDuration(500);
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
	
	public void hideMatchscreen(){
		match.setTextColor(Color.WHITE);
		controlview.setVisibility(View.VISIBLE);
		AnimatorSet set = new AnimatorSet();
		set.play(ObjectAnimator.ofFloat(matchscreen, View.Y, 125,-Constant.screenHeight));
        set.setDuration(500);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            	matchscreen.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        set.start();
	}
	
	
	public void remoteHomepage(){
		if(Constant.matchScreen!=null){
			toastInfo(mContext.getString(R.string.home));
			controlTask = new RemoteControlTask(mContext,CommandTypeEntity.HOME,"",true);
			controlTask.execute(3);
		}
	}
}
