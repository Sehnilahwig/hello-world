package com.tvmining.wifiplus.waterfall.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2012-10-15
 * @desc   : 为ViewPager提供的通用Adapter
 * @param <T>
 */
public class CommonPagerAdapter<T> extends PagerAdapter {

	private List<T> mDataSet ;
	private LayoutInflater mInflater;
	private ViewCreator<T> mCreator;
	private boolean mIsForceUpdateView = false;
	
	public CommonPagerAdapter(LayoutInflater inf,ViewCreator<T> creator){
		mCreator = creator;
		mInflater = inf;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View view = (View) object;
		container.removeView(view);
		int index = Math.max(0, Math.min(position, mDataSet.size() - 1));
		mCreator.releaseView(view, mDataSet.get(index));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = mCreator.createView(mInflater, position, mDataSet.get(position));
		container.addView(view);
		return view;
	}

	@Override
	public int getCount() {
		return mDataSet == null ? 0 : mDataSet.size();
	}
	
	public T getDataItemAt(int position){
		return mDataSet == null ? null : mDataSet.get(position);
	}
	
	public void toggleForceUpdate(boolean isForce){
		mIsForceUpdateView = isForce;
	}
	
	@Override
	public int getItemPosition(Object object) {
		if(mIsForceUpdateView){
			return POSITION_NONE;
		}
		return super.getItemPosition(object);
	}

	public void update(List<T> ds){
		mDataSet = ds;
		notifyDataSetChanged();
	}
	
	public void add(List<T> extraData){
		if(mDataSet == null){
			update(extraData);
			return;
		}
		mDataSet.addAll(extraData);
		notifyDataSetChanged();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

}
