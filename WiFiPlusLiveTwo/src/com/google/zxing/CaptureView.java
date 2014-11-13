package com.google.zxing;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.camera.CameraManager;
import com.google.zxing.decoding.CaptureActivityHandler;
import com.google.zxing.decoding.InactivityTimer;
import com.google.zxing.view.ViewfinderView;
import com.tvmining.sdk.entity.NeighbourEntity;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.Utility;
import com.tvmining.wifipluseq.R;

public class CaptureView extends RelativeLayout implements Callback{

	public CaptureView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public CaptureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public CaptureView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private Context mContext;
	private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private TextView mTitle;
    private ImageView mGoHome;
    
    private View qrCodeView;
    
    private Button cancel;
    private TextView resultView;
    public SurfaceView surfaceView;
    private View qrcodeanimtop;
    private View qrcodeanimbottom;
    private View qrcodebg;
    private Button back;
    
    /** Called when the activity is first created. */
    public void init(Context mContext) {
    	this.mContext = mContext;
    	this.removeView(qrCodeView);
    	qrCodeView = null;
    	qrCodeView = LayoutInflater.from(mContext).inflate(R.layout.qr_code_scan,null);
    	back = (Button) qrCodeView.findViewById(R.id.back);
    	this.addView(qrCodeView);
    }

    private void initControl() {
        viewfinderView = (ViewfinderView) qrCodeView.findViewById(R.id.viewfinder_view);
        mTitle = (TextView) qrCodeView.findViewById(R.id.details_textview_title);
        mTitle.setText(R.string.scan_tip);
        mTitle.getLayoutParams().width = (Constant.screenWidth * 3) / 4;
        
        mGoHome = (ImageView) qrCodeView.findViewById(R.id.details_imageview_gohome);
        mGoHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
            }
        });
        resultView = (TextView) qrCodeView.findViewById(R.id.qrcoderesult);

        cancel = (Button) qrCodeView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				handleCamera();
			}
		});
        qrcodebg = qrCodeView.findViewById(R.id.qrcodebg);
        qrcodebg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("aaa", "bbb");
			}
		});
        back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeAnim();
				v.setClickable(false);
			}
		});
    }

    public void handleCamera(){
    	hasSurface = false;
		Message msg = new Message();
		msg.what = R.id.restart_preview;
		handler.sendMessage(msg);
    }
    
    public void setData() {
    	CameraManager.init(mContext);
        initControl();

        hasSurface = false;
        inactivityTimer = new InactivityTimer();
        
    	qrcodeanimtop = qrCodeView.findViewById(R.id.qrcodeanimtop);
    	qrcodeanimbottom = qrCodeView.findViewById(R.id.qrcodeanimbottom);
        surfaceView = (SurfaceView) qrCodeView.findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) mContext.getSystemService(mContext.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    protected void pause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    public void destroy() {
    	
//    	closeAnim();
    	
    	hasSurface = false;
    	if(surfaceView != null){
    		SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
            CameraManager.get().stopPreview();
        	CameraManager.get().closeDriver();
    	}
    	if(inactivityTimer != null){
    		inactivityTimer.shutdown();
    		inactivityTimer = null;
    	}
        
        handler = null;
        surfaceView = null;
        CameraManager.destory();
        this.postDelayed(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				init(mContext);
			}
        	
        }, 200);

        
    }
    /**
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        String resultString = result.getText();
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("result", resultString);
        resultIntent.putExtras(bundle);
        resultView.setText(resultString);
        // 回调  edit by like
//        this.setResult(RESULT_OK, resultIntent);
//        resultString = "{\"iceid\":\"dev-123\",\"tvmid\":\"\u5927\u5c4f\"}";
//        resultString = "{\"tvmid\":\"ICE\",\"iceid\":\"658\"}";
        Toast toast = Toast.makeText(mContext,"",Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0,0);
		toast.setDuration(3000);

		try {
			JSONObject jsonObject = new JSONObject(resultString);
			final String action = jsonObject.getString("action");
			
			if(Constant.QRCODE_ACTION_REMOTE_SCREEN.equals(action)){
				try {
					String tvmid = jsonObject.getString("tvmid");
					boolean isHave = false;
					if(tvmid != null){
						String iceid = jsonObject.getString("iceid");
						if(Constant.allScreenList.size()>0){
							for(int i=0;i<Constant.allScreenList.size();i++){
								NeighbourEntity entity = Constant.allScreenList.get(i);
								if(entity != null && entity.tvmId.equals(tvmid)){
									isHave = true;
									Constant.matchScreen.clear();
									Constant.matchScreen.add(entity);
									Constant.activity.getRemoteView().setMaskShow();
									
									toast.setText(mContext.getResources().getString(R.string.qrcode_confirm_text)+tvmid);
									toast.show();
									break;
								}
							}
						}
					}
					if(!isHave){
						toast.setText(mContext.getResources().getString(R.string.qrcode_not_found_screen));
						toast.show();
//						MessageUtil.toastInfo(mContext,getResources().getString(R.string.qrcode_not_found_screen));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					toast.setText(mContext.getResources().getString(R.string.qrcode_screen_error));
					toast.show();
				}
			}else if(Constant.QRCODE_ACTION_SHARE_PACKAGE.equals(action)){
				Utility.parseQrCode(resultString);
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Message msg = new Message();
		msg.what = Constant.HANDLER_LOCAL_QRCODE_HIDE;
		Constant.activity.getHandler().sendMessage(msg);
		
		/*try {
			JSONObject jsonObject = new JSONObject(resultString);
			String tvmid = jsonObject.getString("tvmid");
			boolean isHave = false;
			if(tvmid != null){
//				tvmid = UnicodeByteUtil.loadConvert(tvmid);
				String iceid = jsonObject.getString("iceid");
				if(EmeetingApplication.allScreenList.size()>0){
					for(int i=0;i<EmeetingApplication.allScreenList.size();i++){
						NeighbourEntity entity = EmeetingApplication.allScreenList.get(i);
//						if(entity != null && entity.tvmId.equals(tvmid) && entity.iceId.equals(iceid)){
						if(entity != null && entity.tvmId.equals(tvmid)){
							isHave = true;
							EmeetingApplication.matchScreen.clear();
							EmeetingApplication.matchScreen.add(entity);
							FloatingWindowService.remoteControlView.setSelect();
							
							toast.setText(mContext.getResources().getString(R.string.qrcode_confirm_text)+tvmid);
							toast.show();
							
//							FloatingWindowService.remoteControlView.bglayout.setVisibility(View.VISIBLE);
//							FloatingWindowService.remoteControlView.qrcodeText.setText(mContext.getResources().getString(R.string.qrcode_confirm_text)+tvmid);
						}
					}
				}
			}
			if(!isHave){
				toast.setText(mContext.getResources().getString(R.string.qrcode_not_found_screen));
				toast.show();
//				MessageUtil.toastInfo(mContext,getResources().getString(R.string.qrcode_not_found_screen));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			toast.setText(mContext.getResources().getString(R.string.qrcode_screen_error));
			toast.show();
		}*/
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
//            maskview.setVisibility(View.GONE);
            final AnimatorSet set = new AnimatorSet();
            set
                    .play(ObjectAnimator.ofFloat(qrcodeanimtop, View.Y, 0,
                    		-qrcodeanimtop.getHeight()))
                    .with(ObjectAnimator.ofFloat(qrcodeanimbottom, View.Y, qrcodeanimbottom.getTop(),Constant.screenHeight));
            set.setDuration(500);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                	qrcodeanimtop.setVisibility(View.GONE);
                	qrcodeanimbottom.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }
            });
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	
    	
        hasSurface = false;
        
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    /**
     * 扫描正确后的震动声音,如果感觉apk大了,可以删除
     */
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
        	// edit by like
//            mContext.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = mContext.getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    
    
    
    public void closeAnim(){
    	final AnimatorSet set = new AnimatorSet();
    	qrcodeanimtop.setVisibility(View.VISIBLE);
    	qrcodeanimbottom.setVisibility(View.VISIBLE);
        set
        		.play(ObjectAnimator.ofFloat(qrcodeanimtop, View.Y, -qrcodeanimtop.getHeight(),0))
        		.with(ObjectAnimator.ofFloat(qrcodeanimbottom, View.Y, Constant.screenHeight,qrcodeanimbottom.getTop()));
        
        set.setDuration(500);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            	Message msg = new Message();
				msg.what = Constant.HANDLER_LOCAL_QRCODE_HIDE;
				Constant.activity.getHandler().sendMessage(msg);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }
}
