package com.tvmining.wifiplus.waterfall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2012-12-3
 * @desc   : 显示多少个，创建多少个。
 */
public class SimpleAdapter<T> extends AbstractAdapter<T> {

	public SimpleAdapter(LayoutInflater inflater, ViewCreator<T> creator) {
		super(inflater, creator);
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		if(null == convertView){
			convertView=  mCreator.createView(mInflater, pos, mDataCache.get(pos));
		}
		return convertView;
	}
}
