package com.tvmining.wifiplus.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.tvmining.wifiplus.image.zoom.PhotoViewAttacher;

public class ZoomImageView extends ImageView{

	public ZoomImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public Bitmap bitmap;
	public PhotoViewAttacher attacher;
	public int position;
	public String from;
	
	
}
