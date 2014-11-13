package com.tvmining.wifiplus.canvas.interfaces;

import android.view.MotionEvent;

import com.tvmining.wifiplus.canvas.view.SketchPadView;

public interface ISketchPadCallback
{
    public void onTouchDown(SketchPadView obj, MotionEvent event);
    public void onTouchUp(SketchPadView obj, MotionEvent event);
    public void onDestroy(SketchPadView obj);
}
