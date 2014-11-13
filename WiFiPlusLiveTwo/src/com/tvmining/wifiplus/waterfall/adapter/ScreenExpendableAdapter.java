package com.tvmining.wifiplus.waterfall.adapter;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tvmining.sdk.entity.NeighbourEntity;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.view.MyGridView;
import com.tvmining.wifipluseq.R;

public class ScreenExpendableAdapter extends BaseExpandableListAdapter {
	
	private List<NeighbourEntity> screenList = null;
	private Context mContext;
	private TextView matchname;
	
	public TextView getMatchname() {
		return matchname;
	}


	public void setMatchname(TextView matchname) {
		this.matchname = matchname;
	}


	public ScreenExpendableAdapter(Context context,List<NeighbourEntity> list){
		this.mContext = context;
		screenList = new ArrayList<NeighbourEntity>();
		for(int i=0;i<list.size();i++){
			screenList.add(list.get(i));
		}
	}
	

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return screenList.get(arg0).applist;
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return arg1;
	}

	@Override
	public View getChildView(int arg0, int arg1, boolean arg2, View convertView,
			ViewGroup arg4) {
		// TODO Auto-generated method stub
		ChildHolder holder ;
		if(convertView == null) {
			holder = new ChildHolder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.gridview_item, null);
			holder.gridview = (MyGridView) convertView.findViewById(R.id.gvtest);
			convertView.setTag(holder);
		}else {
			holder = (ChildHolder) convertView.getTag();
		}
		
		
		
		AppAdapter adapter = new AppAdapter(mContext);
		
		adapter.setList(screenList.get(arg0).applist);
		holder.gridview.setAdapter(adapter);
		return convertView;
	}

	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		if(screenList.get(arg0).applist == null || screenList.get(arg0).applist.size() == 0){
			return 0;
		}else {
			return 1;
		}
		
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return screenList.get(arg0);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return screenList.size();
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getGroupView(int position, boolean arg1, View convertView, ViewGroup arg3) {
		// TODO Auto-generated method stub
		final int index = position;
		StateHolder holder = null;
		if(convertView == null){
			holder = new StateHolder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.screen_item, null);
			convertView.setTag(holder);
		}else{
			holder = (StateHolder) convertView.getTag();
		}
		
		holder.screenName = (TextView) convertView.findViewById(R.id.screenname);
		holder.selected = (CheckBox) convertView.findViewById(R.id.check);
		holder.screenName.setText(screenList.get(position).tvmId);
		if(Constant.matchScreen!=null&&Constant.matchScreen.size()>0){
			boolean flog = false;
			for(NeighbourEntity nei:Constant.matchScreen){
				
				if(screenList.get(position).tvmId.equals(nei.tvmId)){
					Constant.getSelectMap().put(nei.tvmId, screenList.get(position));
					holder.selected.setChecked(true);
					flog = true;
					break;
				}
			}
			if(!flog)
				holder.selected.setChecked(false);
		}else{
			holder.selected.setChecked(false);
		}
		setTitleText();
		final StateHolder temholder = holder;
		
		holder.selected.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkStatusResponse(temholder,index);
			}
		});
		
		convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				checkStatusItemResponse(temholder,index);
			}
		});
		
		return convertView;
	}

	public void checkStatusItemResponse(final StateHolder temholder,int index){
		if(!temholder.selected.isChecked()){
			temholder.selected.setChecked(true);
			temholder.screenName.setTextColor(Color.rgb(0, 187, 224));
			
			boolean flog = false;
			for(NeighbourEntity nei:Constant.matchScreen){
				if(screenList.get(index).tvmId.equals(nei.tvmId)){
					flog = true;
					break;
				}
			}
			if(!flog)
				Constant.matchScreen.add(screenList.get(index));
		}else{
			temholder.selected.setChecked(false);
			temholder.screenName.setTextColor(Color.rgb(120, 120, 120));
			for(NeighbourEntity nei:Constant.matchScreen){
				if(nei.tvmId.equals(screenList.get(index).tvmId)){
					Constant.matchScreen.remove(nei);
					break;
				}
			} 
		}
		setTitleText();
	}
	
	public void checkStatusResponse(final StateHolder temholder,int index){
		if(temholder.selected.isChecked()){
			temholder.selected.setChecked(true);
			temholder.screenName.setTextColor(Color.rgb(0, 187, 224));
			
			boolean flog = false;
			for(NeighbourEntity nei:Constant.matchScreen){
				if(screenList.get(index).tvmId.equals(nei.tvmId)){
					flog = true;
					break;
				}
			}
			if(!flog)
				Constant.matchScreen.add(screenList.get(index));
		}else{
			temholder.selected.setChecked(false);
			temholder.screenName.setTextColor(Color.rgb(120, 120, 120));
			for(NeighbourEntity nei:Constant.matchScreen){
				if(nei.tvmId.equals(screenList.get(index).tvmId)){
					Constant.matchScreen.remove(nei);
					break;
				}
			} 
		}
		setTitleText();
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {  
		        super.registerDataSetObserver(observer);  
		   }
	
	private void setTitleText(){
		String name = "";
		if(Constant.matchScreen != null){
			for(int i=0;i<Constant.matchScreen.size();i++){
				NeighbourEntity nei = Constant.matchScreen.get(i);
				name += nei.tvmId+" ";
			}
			matchname.setText(name);
		}
	}
	
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	
	class StateHolder{
		public TextView screenName;//屏幕名称
		public CheckBox selected;//选中状态
		public int pos;
	}
	
	class ChildHolder{
		public MyGridView gridview;
	}
	
}
