package com.tvmining.wifiplus.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvmining.wifiplus.view.UITableView2;
import com.tvmining.wifipluseq.R;

public class SetImageUploadQualityActivity extends Activity implements View.OnClickListener{

	LayoutInflater inflater = null;	
	private Button back;	
	private SharedPreferences preferences;	
	private String uploadQualityValue;//存储的上传质量值	
	private UITableView2 qualityView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setimageuploadquality);
		
		overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
		init();
	}

	@Override
	protected void onResume() {		
		super.onResume();
	}

	public void init(){		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		uploadQualityValue = preferences.getString("uploadquality","中(90%)");
		
		
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		
		qualityView = (UITableView2)findViewById(R.id.uploadtableview);
		
		CustomClickListener listener = new CustomClickListener();
		qualityView.setClickListener(listener);
		
		if(uploadQualityValue.equals(getResources().getString(R.string.highupload))){
			qualityView.addBasicItem(getResources().getString(R.string.highupload),true);
		}else{
			qualityView.addBasicItem(getResources().getString(R.string.highupload),false);
		}
		
		
		if(uploadQualityValue.equals(getResources().getString(R.string.middleupload))){
			qualityView.addBasicItem(getResources().getString(R.string.middleupload),true);
		}else{
			qualityView.addBasicItem(getResources().getString(R.string.middleupload),false);
		}
		qualityView.commit();	
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
			//保存图像上传质量
			Editor editor = preferences.edit();			
			LinearLayout layout = (LinearLayout) qualityView.findViewById(R.id.buttonsContainer);
			
			String value = ((TextView)layout.getChildAt(index).findViewById(R.id.title)).getText().toString();
			editor.putString("uploadquality",value);			

			editor.commit();
			setResult(RESULT_OK);
			finish();
		}    	
    }
}
