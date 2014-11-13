package com.tvmining.wifiplus.view;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvmining.wifipluseq.R;


public class UIButton extends RelativeLayout {

	private LayoutInflater mInflater;
	private RelativeLayout mButtonContainer;
	private ClickListener mClickListener;
	
	public UIButton(Context context, AttributeSet attrs) {
		super(context, attrs);
//		this.setClickable(true);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mButtonContainer = (RelativeLayout) mInflater.inflate(R.layout.list_item_single, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
				
		mButtonContainer.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mClickListener != null)
					mClickListener.onClick(UIButton.this);
			}			
		});		
		addView(mButtonContainer, params);
	}	
	
	public UIButton(Context context) {
		super(context);
//		this.setClickable(true);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mButtonContainer = (RelativeLayout) mInflater.inflate(R.layout.list_item_single, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
				
		mButtonContainer.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mClickListener != null)
					mClickListener.onClick(UIButton.this);
			}			
		});		
		addView(mButtonContainer, params);
	}	
	
	public void setContent(String title,String content,boolean display){
		((TextView) mButtonContainer.findViewById(R.id.title)).setText(title);
		TextPaint tp = ((TextView) mButtonContainer.findViewById(R.id.title)).getPaint(); 
		tp.setFakeBoldText(true);
		((TextView) mButtonContainer.findViewById(R.id.content)).setText(content);
		if(!display){
			mButtonContainer.findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
		}
		
	}	
	
	public interface ClickListener {		
		void onClick(View view);		
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void addClickListener(ClickListener listener) {
		this.mClickListener = listener;
	}
	
	/**
	 * 
	 */
	public void removeClickListener() {
		this.mClickListener = null;
	}

}
