package com.tvmining.wifiplus.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvmining.wifiplus.cache.ImageLoader;
import com.tvmining.wifiplus.entity.ImageWrapper;
import com.tvmining.wifiplus.image.loader.ListImageFetcher;
import com.tvmining.wifiplus.view.ZoomImageView;
import com.tvmining.wifiplus.waterfall.adapter.ViewBuilder;

/**
 * @author : 桥下一粒砂
 * @email : chenyoca@gmail.com
 * @date : 2012-12-6
 * @desc : TODO
 */
public class SimpleViewBuilder extends ViewBuilder<ImageWrapper> {

	private Context context;
	
	public SimpleViewBuilder(Context context){
		this.context = context;
	}
	
    @Override
    public View createView(LayoutInflater inflater, int position,
                           ImageWrapper data,ImageLoader imageLoader,boolean mBusy,ListImageFetcher mImageFetcher) {
        View view = inflater.inflate(ResourceUtil.getResId(context, "item_sample", "layout"), null);
        updateView(view, position, data,imageLoader,mBusy,mImageFetcher);
        return view;
    }

    @Override
    public void updateView(View view, final int position, ImageWrapper data,ImageLoader imageLoader,boolean mBusy,ListImageFetcher mImageFetcher) {
    	ZoomImageView image = null;
    	TextView pageInfo = null;
    	ImageView checked;
    	ImageView maskView;
    	TextView videoText = null;
    	ImageView videoImage = null;
    	TextView imgTitle = null;
    	Holder holder = null;
    	if(view.getTag() != null){
    		holder = (Holder) view.getTag();
    		image = holder.image;
    	}else{
    		image = (ZoomImageView) view.findViewById(ResourceUtil.getResId(context, "thumbnail", "id"));
    		
    		pageInfo = (TextView)view.findViewById(ResourceUtil.getResId(context, "text", "id"));
    		checked = (ImageView)view.findViewById(ResourceUtil.getResId(context, "checked", "id"));
    		maskView = (ImageView)view.findViewById(ResourceUtil.getResId(context, "maskview", "id"));
    		videoText = (TextView)view.findViewById(ResourceUtil.getResId(context, "videoText", "id"));
    		videoImage = (ImageView)view.findViewById(ResourceUtil.getResId(context, "videoImage", "id"));
    		imgTitle = (TextView)view.findViewById(ResourceUtil.getResId(context, "imgTitle", "id"));
    		
            holder = new Holder();
            holder.image = image;
            holder.text = pageInfo;
            holder.maskView = maskView;
            holder.checked = checked;
            holder.videoText = videoText;
            holder.videoImage = videoImage;
            holder.imgTitle = imgTitle;
            view.setTag(holder);
    	}
    	holder.image.position = position;
    	holder.text.setText(String.valueOf(position+1));
    	holder.imgTitle.setText(data.imgTitle);
    	ColorDrawable cd = new ColorDrawable(Color.parseColor("#b0000000"));
    	cd.setAlpha(150);
    	holder.imgTitle.setBackgroundDrawable(cd);
    	if("VIDEO".equals(data.type)){
    		holder.videoImage.setVisibility(View.VISIBLE);
    		holder.videoText.setVisibility(View.VISIBLE);
    		holder.videoText.setRotation(45);
    	}else{
    		holder.videoImage.setVisibility(View.GONE);
    		holder.videoText.setVisibility(View.GONE);
    	}
    	
    	if(Constant.COMMUNICATION_TYPE.equals(data.packageType)){
    		holder.imgTitle.setVisibility(View.VISIBLE);
    	}else{
    		holder.imgTitle.setVisibility(View.GONE);
    	}
    	
    	view.setLongClickable(true);
    	view.setClickable(false);
    	view.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = Constant.HANDLER_WATERFALL_ITEM_CLICK;
				msg.obj = v;
				Bundle data = new Bundle();
				data.putInt("position", position);
				msg.setData(data);
				Constant.activity.getHandler().sendMessage(msg);
			}
		});
    	
    	mImageFetcher.loadImage(data, holder.image,Constant.FROM_WATER);
    }
    
    class Holder{
    	public ZoomImageView image;
    	public TextView text;
    	public ImageView checked;
    	public ImageView maskView;
    	public TextView videoText;
    	public ImageView videoImage;
    	public TextView imgTitle;
    }
    
}
