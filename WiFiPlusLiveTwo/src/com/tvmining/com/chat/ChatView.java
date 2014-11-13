package com.tvmining.com.chat;

import java.util.Calendar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvmining.sdk.entity.CommandTypeEntity;
import com.tvmining.wifiplus.entity.InteractGalleryEntity;
import com.tvmining.wifiplus.thread.RemoteControlTaskAllCanSee;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifipluseq.R;

/**
 * 聊天类
 * @author Administrator
 *
 */
public class ChatView extends RelativeLayout implements OnClickListener {

	public ChatView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ChatView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ChatView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private ImageView mBtnMore;

	private EditText mEditTextContent;
	
	private ImageView chatEditView;
	
	private InteractGalleryEntity galleryEntity;
	
	private StringBuffer editContent = new StringBuffer();
	
	private InputMethodManager imm = null;
	
	private FaceRelativeLayout faceLayout;
	
	private Button interactsendbtn;

	
	
	public FaceRelativeLayout getFaceLayout() {
		return faceLayout;
	}

	public InteractGalleryEntity getGalleryEntity() {
		return galleryEntity;
	}

	public void setGalleryEntity(InteractGalleryEntity galleryEntity) {
		this.galleryEntity = galleryEntity;
	}

	public void init(final Context mContext) {
		
		imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				View view = LayoutInflater.from(mContext).inflate(R.layout.layout_chat,null);
				addView(view);
				initView();
			}
		};
		
		new Thread(new Runnable() {
            @Override
            public void run() {
                FaceConversionUtil.getInstace().getFileText(mContext);
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }).start();
	}

	public void loseFocus(){
		if(mEditTextContent != null){
			mEditTextContent.setFocusable(false);
			mEditTextContent.setFocusableInTouchMode(false);
			mEditTextContent.setCursorVisible(false);
			mEditTextContent.clearFocus();
		}
	}
	
	public void initView() {
		mBtnMore = (ImageView) findViewById(R.id.btn_more);
		mBtnMore.setOnClickListener(this);
		faceLayout = (FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout);
		interactsendbtn = (Button) findViewById(R.id.interactsendbtn);
		interactsendbtn.setOnClickListener(this);
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		loseFocus();
		
		mEditTextContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(mEditTextContent.hasFocus()){
					mEditTextContent.setCursorVisible(true);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}else{
					mEditTextContent.setCursorVisible(false);
				}
				
			}
		}); 
		mEditTextContent.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showSoftWare();
			}
		});
		
		mEditTextContent.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					String newEditContent = s.toString();
					String inputStr = newEditContent.substring(start,start + count);
					if(Constant.allExceptionsMap.containsKey(inputStr)){
						editContent.append((String)Constant.allExceptionsMap.get(inputStr));
					}else{
						editContent = new StringBuffer(newEditContent);
					}
					Log.d("ChatView", editContent.toString());
				}
	
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
				}
		});
		
		mEditTextContent.setOnEditorActionListener(new TextView.OnEditorActionListener() { 
			@Override 
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) { 
				if (actionId == EditorInfo.IME_ACTION_SEND) { 
					send();
					return true; 
				} 
				
				return false; 
			}});
		
		chatEditView = (ImageView) findViewById(R.id.chatedit);
		chatEditView.setOnClickListener(this);
	}

	public void showSoftWare(){
		Constant.activity.hideTabs();
		((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView();
		mEditTextContent.setFocusable(true);
		mEditTextContent.setFocusableInTouchMode(true);
		
		mEditTextContent.requestFocus();
		
		Constant.activity.showInteractMaskView();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_more:
			loseFocus();
			faceLayout.resetEditStatus();
			Constant.activity.showInteractCameraView();
			break;
		case R.id.chatedit:
			Constant.activity.beginEdit();//进入绘画模式
			break;
		case R.id.interactsendbtn:
			send();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& ((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout))
						.hideFaceView()) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 发送按钮
	 */
	private void send() {
		if(editContent != null && !"".equals(editContent.toString())){
			String guid = "";
			if(galleryEntity != null){
				guid = galleryEntity.getItemGuid();
			}
			String postdata = "{\"symbol\":\""+editContent.toString()+"\",\"questionguid\":\""+guid+"\"}";
			RemoteControlTaskAllCanSee controlTask = new RemoteControlTaskAllCanSee(CommandTypeEntity.MARCO,postdata);
			controlTask.execute(3);
		}
		
		if (editContent.length() > 0) {
			mEditTextContent.setText("");
			editContent.setLength(0);
		}
	}

	private String getDate() {
		Calendar c = Calendar.getInstance();

		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));

		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
				+ mins);

		return sbBuffer.toString();
	}
}