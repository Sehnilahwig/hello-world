package com.tvmining.wifiplus.waterfall.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2012-7-10
 * @desc   : 实现HolderView模式的Adapter。
 * @param <T>
 */
public class HolderAdapter<T> extends AbstractAdapter<T> {

	/**
	 * </br><b>description : </b>	创建对象
	 * @param inflater
	 * @param creator
	 */
	public HolderAdapter(LayoutInflater inflater, ViewCreator<T> creator) {
		super(inflater, creator);
	}

	private static class ViewHolder{
		public View view;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Holder View模式实现
		if (convertView == null) {
			ViewHolder holder = new ViewHolder();
			convertView = mCreator.createView(mInflater, position,getItem(position));
			holder.view = convertView;
			convertView.setTag(holder);
		} else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			//释放当前的View的数据
			mCreator.releaseView(convertView,  getItem(position));
			//将新数据更新到HodlerView中
			mCreator.updateView(holder.view, position, getItem(position));
			Log.d("aaaaaaaaaaa", "111");
		}
		return convertView;
	}
}
