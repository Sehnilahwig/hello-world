package com.tvmining.wifiplus.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;


public class AnswerImageView extends ImageView {
	
	private Context mContext; 
	private Paint paint;
	public int count = 0;


	public AnswerImageView(Context paramContext) {
		super(paramContext);
		mContext = paramContext;
		init();
	}

	public AnswerImageView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		mContext = paramContext;
		init();
	}

	public AnswerImageView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		mContext = paramContext;
		init();
	}

	private void init(){
		this.paint = new Paint();
		this.paint.setAntiAlias(true); //消除锯齿 
//        this.paint.setStyle(Style.STROKE);  //绘制空心圆或 空心矩形
	}
	
	protected void onDraw(Canvas canvas) {
		int center = getWidth()/2;
		int innerCircle = dip2px(mContext,6); //内圆半径
		int ringWidth = dip2px(mContext, 3);   //圆环宽度
		
		// 第一种方法绘制圆环
		//绘制内圆 
		this.paint.setARGB(255, 255, 255, 255);
//		this.paint.setStrokeWidth(2);
		canvas.drawCircle(center, center, innerCircle, this.paint); 		
		
        //绘制圆环 
//		this.paint.setStrokeWidth(ringWidth);
		canvas.drawCircle(center, center, innerCircle + 1 +ringWidth/2, this.paint); 		
		
        //绘制外圆 
//		this.paint.setStrokeWidth(2);
		canvas.drawCircle(center, center, innerCircle + ringWidth, this.paint);  		
		
		Rect rect = new Rect();
		this.paint.setTextSize(15);
		this.paint.getTextBounds(String.valueOf(count), 0, String.valueOf(count).length(), rect);
		int w = rect.width();
		int h = rect.height();
		this.paint.setColor(Color.BLACK);
		canvas.drawText(String.valueOf(count), center-w/2, center+h/2, this.paint);
		
        super.onDraw(canvas);
	}
	public static int dip2px(Context context, float dpValue) {  
		final float scale = context.getResources().getDisplayMetrics().density;  
		return (int) (dpValue * scale + 0.5f);  
	}
}

