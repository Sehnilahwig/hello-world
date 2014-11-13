package com.tvmining.wifiplus.waterfall.adapter;

import android.view.LayoutInflater;
import android.view.View;

import com.tvmining.wifiplus.cache.ImageLoader;
import com.tvmining.wifiplus.image.loader.ListImageFetcher;

/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2012-12-5
 * @desc   : 空实现
 */
public class ViewBuilder<T> implements ViewCreator<T> {

	@Override
	public View createView(LayoutInflater inflater, int position, T data) {
		return null;
	}

	@Override
	public void updateView(View view, int position, T data) { }

	@Override
	public void releaseView(View view, T data) { }


	@Override
	public View createView(LayoutInflater inflater, int position, T data,
			ImageLoader imageLoader, boolean mBusy,
			ListImageFetcher mImageFetcher) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateView(View view, int position, T data,
			ImageLoader imageLoader, boolean mBusy,
			ListImageFetcher mImageFetcher) {
		// TODO Auto-generated method stub
		
	}

}
