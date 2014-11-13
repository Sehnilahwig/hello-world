package com.tvmining.wifiplus.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvmining.wifipluseq.R;

public class PassWordView extends FrameLayout {

	private LayoutInflater mInflater;
	private FrameLayout mButtonContainer;
	private ClickListener mClickListener;
	
	public PassWordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mButtonContainer = (FrameLayout) mInflater.inflate(R.layout.passwordview, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
				
		mButtonContainer.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mClickListener != null)
					mClickListener.onClick(PassWordView.this);
			}			
		});		
		addView(mButtonContainer, params);
	}	
	
	public void setContent(String content){
		((TextView) mButtonContainer.findViewById(R.id.title)).setText(content);
		
		if(content.equals("")){
			mButtonContainer.findViewById(R.id.clear).setVisibility(View.INVISIBLE);
		}else{
			mButtonContainer.findViewById(R.id.clear).setVisibility(View.VISIBLE);
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
