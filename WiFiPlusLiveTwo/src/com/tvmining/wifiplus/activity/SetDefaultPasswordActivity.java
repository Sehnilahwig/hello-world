package com.tvmining.wifiplus.activity;


import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.view.PassWordView;
import com.tvmining.wifiplus.view.PassWordView.ClickListener;
import com.tvmining.wifipluseq.R;
public class SetDefaultPasswordActivity extends Activity{

	private SharedPreferences preferences;
	private String passwordValue;//存储的密码	
	private PassWordView passwordView;
	private EditText pwdedittext;
	private ImageView clearView;
	
	private RelativeLayout pwdlayout;
	
	private Button back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setdefaultpassword);
		
		overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
		init(this);
	}

	public void init(Context context){
		
		final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		passwordValue = preferences.getString(Constant.PREFERENCES_NAME,Constant.defaultPwd);
		
		passwordView = (PassWordView)findViewById(R.id.passwordeditview);
		passwordView.setContent(passwordValue);
		
		passwordView.addClickListener(new ClickListener() {			
			@Override
			public void onClick(View view) {
				imm.showSoftInput(pwdedittext, 0);
			}
		});	
		
		pwdedittext = (EditText) passwordView.findViewById(R.id.title);
		
		pwdedittext.setSelection(passwordValue.length());
		clearView = (ImageView)passwordView.findViewById(R.id.clear);
		
		clearView.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				//清除输入内容
				pwdedittext.setText("");
			}
		});
		
		pwdedittext.setOnKeyListener(new OnKeyListener() {			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER){					
                    if (imm.isActive()){ 
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
					//执行保存密码操作					
					Editor editor = preferences.edit();
					editor.putString(Constant.PREFERENCES_NAME,pwdedittext.getText().toString());
					editor.commit();
					setResult(RESULT_OK);
					finish();		
				}
				return false;
			}
		});
		
		pwdedittext.requestFocus();
		Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
                imm.showSoftInput(pwdedittext, 0);
            }
            
        },200);
		
		pwdlayout = (RelativeLayout)findViewById(R.id.pwdlayout);
		pwdlayout.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (imm.isActive()){ 
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
				//执行保存密码操作					
				Editor editor = preferences.edit();
				editor.putString(Constant.PREFERENCES_NAME,pwdedittext.getText().toString());
				editor.commit();
				setResult(RESULT_OK);
				finish();	
			}
		});
		
		back = (Button)findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				//执行保存密码操作					
				Editor editor = preferences.edit();
				editor.putString(Constant.PREFERENCES_NAME,pwdedittext.getText().toString());
				editor.commit();
				setResult(RESULT_OK);
				finish();
			}
		});
	}
	

	@Override
	protected void onResume() {		
		super.onResume();
	}
}
