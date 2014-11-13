package com.tvmining.wifiplus.waterfall.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tvmining.wifipluseq.R;

public class AppAdapter extends BaseAdapter {

	public ArrayList<String> list = new ArrayList<String>();
	private Context mContext;
	
	public AppAdapter(Context mContext){
		this.mContext = mContext;
	}
	
	
	
	public void setList(ArrayList<String> list) {
		this.list = list;
	}



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
		
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.app_item, null);
			holder.appname = (Button)convertView.findViewById(R.id.app);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.appname.setText(list.get(arg0));
		
		
		
		return convertView;
	}

	
	class ViewHolder {  
        Button appname; 
    } 
}
