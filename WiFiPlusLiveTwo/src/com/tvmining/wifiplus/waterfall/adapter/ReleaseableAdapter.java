package com.tvmining.wifiplus.waterfall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2012-12-3
 * @desc   : TODO
 */
public class ReleaseableAdapter<T> extends AbstractAdapter<T> {

	public ReleaseableAdapter(LayoutInflater inflater, ViewCreator<T> creator) {
		super(inflater, creator);
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		T data = mDataCache.get(pos);
		if(null == convertView){
			convertView = mCreator.createView(mInflater, pos, data);
		}else{
			mCreator.releaseView(convertView, data);
			convertView = mCreator.createView(mInflater, pos, data);
		}
		return convertView;
	}
}
