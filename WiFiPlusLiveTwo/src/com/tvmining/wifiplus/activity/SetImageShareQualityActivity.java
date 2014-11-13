package com.tvmining.wifiplus.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvmining.wifiplus.view.UITableView2;
import com.tvmining.wifipluseq.R;

public class SetImageShareQualityActivity extends Activity implements View.OnClickListener{

	private Button back;
	private SharedPreferences preferences;
	private String shareQualityValue; //存储的分享质量值
	
	private UITableView2 sharetableView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setimagesharequality);
		
		overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
		init();
	}

	@Override
	protected void onResume() {		
		super.onResume();
	}

	public void init(){
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		shareQualityValue = preferences.getString("sharequality","中(1024x1024)");
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		sharetableView = (UITableView2)findViewById(R.id.sharetableview); 
		CustomClickListener listener = new CustomClickListener();
		sharetableView.setClickListener(listener);
		
		if(shareQualityValue.equals(getResources().getString(R.string.highshare))){
			sharetableView.addBasicItem(getResources().getString(R.string.highshare),true);
		}else{
			sharetableView.addBasicItem(getResources().getString(R.string.highshare),false);
		}
		
		
		if(shareQualityValue.equals(getResources().getString(R.string.middleshare))){
			sharetableView.addBasicItem(getResources().getString(R.string.middleshare),true);
		}else{
			sharetableView.addBasicItem(getResources().getString(R.string.middleshare),false);
		}
		
		if(shareQualityValue.equals(getResources().getString(R.string.lowshare))){
			sharetableView.addBasicItem(getResources().getString(R.string.lowshare),true);
		}else{
			sharetableView.addBasicItem(getResources().getString(R.string.lowshare),false);
		}
		
		sharetableView.commit();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.back:		
				finish();
				break;
		}
	}
	
	 private class CustomClickListener implements UITableView2.ClickListener {		
		@Override
		public void onClick(int index) {
			//保存图像分享质量
			Editor editor = preferences.edit();			
			LinearLayout layout = (LinearLayout) sharetableView.findViewById(R.id.buttonsContainer);
			
			String value = ((TextView)layout.getChildAt(index).findViewById(R.id.title)).getText().toString();
			editor.putString("sharequality",value);			

			editor.commit();
			setResult(RESULT_OK);
			finish();
		}    	
    }
}
