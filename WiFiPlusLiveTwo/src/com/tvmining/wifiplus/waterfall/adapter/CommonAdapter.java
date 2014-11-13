package com.tvmining.wifiplus.waterfall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tvmining.wifiplus.cache.ImageLoader;
import com.tvmining.wifiplus.image.loader.ListImageFetcher;

/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2012-9-13
 * @desc   : TODO
 * @param <T>
 */
public class CommonAdapter<T> extends AbstractAdapter<T> {
	public ImageLoader imageLoader; 
	private ListImageFetcher mImageFetcher;
	
	public CommonAdapter(LayoutInflater inflater, ViewCreator<T> creator) {
		super(inflater, creator);
	}
	
	public CommonAdapter(LayoutInflater inflater, ViewCreator<T> creator,Context context,ListImageFetcher mImageFetcher) {
		super(inflater, creator);
		imageLoader=new ImageLoader(context);
		this.mImageFetcher = mImageFetcher;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		T data = mDataCache.get(pos);
		if(null == convertView){
			convertView = mCreator.createView(mInflater, pos, data,imageLoader,mBusy,mImageFetcher);
		}else{
			mCreator.updateView(convertView, pos, data,imageLoader,mBusy,mImageFetcher);
		}
		
		
		return convertView;
	}

}
