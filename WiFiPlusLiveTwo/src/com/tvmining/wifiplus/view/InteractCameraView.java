package com.tvmining.wifiplus.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.tvmining.wifiplus.activity.SetDefaultPasswordActivity;
import com.tvmining.wifiplus.activity.SetImageShareQualityActivity;
import com.tvmining.wifiplus.activity.SetImageUploadQualityActivity;
import com.tvmining.wifiplus.activity.ShareAppActivity;
import com.tvmining.wifiplus.thread.ShareAppTask;
import com.tvmining.wifiplus.util.AppUtil;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.wifiplus.util.ResourceUtil;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifiplus.view.UIButton.ClickListener;
import com.tvmining.wifipluseq.R;

/**
 * @author Like
 *
 */
public class InteractCameraView extends BaseView{
	
	
	public InteractCameraView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public InteractCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public InteractCameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}
	
	public void init() {
		View settingView = LayoutInflater.from(mContext).inflate(ResourceUtil.getResId(mContext, "interact_camera", "layout"),null);
		this.addView(settingView);
		
    }
}