package com.tvmining.wifiplus.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.huewu.pla.lib.internal.PLA_AbsListView.OnScrollListener;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.CommandTypeEntity;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;
import com.tvmining.sdk.entity.SearchFileEntity;
import com.tvmining.sdk.entity.SearchFileOrderMethod;
import com.tvmining.sdk.entity.SideThumbMethod;
import com.tvmining.wifiplus.cache.ImageCacheManager;
import com.tvmining.wifiplus.entity.ImageWrapper;
import com.tvmining.wifiplus.entity.ItemTable;
import com.tvmining.wifiplus.entity.Permission;
import com.tvmining.wifiplus.image.loader.Images;
import com.tvmining.wifiplus.image.loader.ListImageCache.ImageCacheParams;
import com.tvmining.wifiplus.image.loader.ListImageFetcher;
import com.tvmining.wifiplus.thread.AutoRemoteControlTask;
import com.tvmining.wifiplus.thread.RemoteControlTask;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.wifiplus.util.ImgResource;
import com.tvmining.wifiplus.util.MessageUtil;
import com.tvmining.wifiplus.util.ResourceUtil;
import com.tvmining.wifiplus.util.SimpleViewBuilder;
import com.tvmining.wifiplus.waterfall.adapter.CommonAdapter;
import com.tvmining.wifipluseq.R;

/**
 * 瀑布流图片加载器
 * 布局文件参考waterfall.xml
 * @author Administrator
 *
 */
public class WaterFallView extends BaseView implements View.OnClickListener,
		PLA_AdapterView.OnItemLongClickListener {

	public WaterFallView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public WaterFallView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public WaterFallView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private static final String TAG = "WaterFallActivity";

	private int onLine = 0;

	private int pos;

	public static ImageCacheManager imageCacheManager;
	private ProgressDialog pd;

	private View push;// 推送
	private View pushdevider;
	private boolean isOwner;

	private ImageView confirm;// 确认推送
	private TextView selectCount;
	private ImageView cancel;

	public static boolean canPush = false;// 设置资源是否是可被推送的状态
	private RelativeLayout bottomLayout;// 底部操作Layout
	private RelativeLayout pushMenuLayout;// 推送操作Layout

	public static int checkedCount = 0;

	StringBuffer postdata = new StringBuffer();

	private GalleryView galleryView;

	public static HashMap<Integer, View> checkedMap = new HashMap<Integer, View>();

	private ListImageFetcher mImageFetcher;
	private int mImageThumbSize;
	private static final String IMAGE_CACHE_DIR = "thumbs";

	private Bitmap videoBitmap;

	// 用来添加包名以及瀑布流内容的Layout

	private TextView packageNameView;
	public MultiColumnListView mWaterfallView;
	private LinearLayout multiColumnLayout;

	private Handler handler;

	private Runnable runnable;

	public FrameLayout viewlayout;

	private View rootView;
	public boolean isRotate;

	private CommonAdapter mAdapter;

	public void resetItems() {
		if (mWaterfallView.imageFileNames != null) {
			mWaterfallView.imageFileNames.clear();
		}

		if (mWaterfallView.sourceImageFileNames != null) {
			mWaterfallView.sourceImageFileNames.clear();
		}

		if (mWaterfallView.itemVector != null) {
			mWaterfallView.itemVector.clear();
		}

		if (mWaterfallView.filterResult != null) {
			mWaterfallView.filterResult.clear();
		}

		if (mAdapter != null) {
			mAdapter.clear();
			mAdapter.notifyDataSetChanged();
		}
	};

	private void sendItemMessage(int position) {
		if (Constant.lockScreen
				&& !Constant.COMMUNICATION_TYPE
						.equalsIgnoreCase(mWaterfallView.packageType)) {
			if (mWaterfallView.filterResult != null
					&& mWaterfallView.filterResult.size() > 0
					&& mWaterfallView.filterResult.get(position) != null) {
				String postdata = "{\"pkg\":\""
						+ mWaterfallView.filterResult.get(position).packname
						+ "\",\"guid\":\""
						+ mWaterfallView.filterResult.get(position).guid
						+ "\",\"tag\":\""
						+ mWaterfallView.filterResult.get(position).tag + "\"}";
				Log.i(TAG, "postdata=" + postdata);
				new RemoteControlTask(mContext, CommandTypeEntity.SYNC,
						postdata, false).execute(3);
			}
		}
	}

	public void setValues(String packageName, String packageType) {
		mWaterfallView.packageName = packageName;
		mWaterfallView.packageType = packageType;
		packageNameView.setText(mWaterfallView.packageName);
	}

	public void setItemVector(Vector vector) {
		mWaterfallView.itemVector = vector;
	}

	private boolean onClickPermit = true;
	
	/**
	 * 选择图片
	 */
	public void runClick(View view, int position) {
		if (onClickPermit) {
			Constant.activity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			if (canPush) {
				// 显示选中状态,准备推送
				if (view.findViewById(R.id.checked).getVisibility() == View.INVISIBLE) {
					view.findViewById(R.id.maskview)
							.setVisibility(View.VISIBLE);
					view.findViewById(R.id.checked).setVisibility(View.VISIBLE);
					checkedCount += 1;
					Log.i(TAG, "选择了第" + position + "张图片");
					checkedMap.put(position, view);
				} else if (view.findViewById(R.id.checked).getVisibility() == View.VISIBLE) {
					view.findViewById(R.id.maskview).setVisibility(
							View.INVISIBLE);
					view.findViewById(R.id.checked).setVisibility(
							View.INVISIBLE);
					checkedCount -= 1;
					checkedMap.put(position, view);
					Log.i(TAG, "取消选择了第" + position + "张图片");
				}

				if (checkedCount > 0) {
					selectCount.setVisibility(View.VISIBLE);
					selectCount.setText(String.valueOf(checkedCount));
				} else {
					selectCount.setVisibility(View.INVISIBLE);
				}
			} else {
				sendItemMessage(position);
				ZoomImageView imasageView = (ZoomImageView) view
						.findViewById(R.id.thumbnail);
				Bitmap sourceBitmap = imasageView.bitmap;
				if (sourceBitmap != null) {
					sourceBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888,
							true);
				} else {
					sourceBitmap = ((BitmapDrawable) mContext.getResources()
							.getDrawable(R.drawable.empty_photo)).getBitmap();
				}

				int animWidth = Constant.screenWidth;
				int animHeight = sourceBitmap.getHeight()
						* Constant.screenWidth / sourceBitmap.getWidth();
				if (onLine == 2) {
					String wstr = ((ItemTable) mWaterfallView.itemVector
							.get(position)).getItemWidth();
					String hstr = ((ItemTable) mWaterfallView.itemVector
							.get(position)).getItemHeight();
					if (wstr != null && !("".equals(wstr))
							&& !("0").equals(wstr)) {
						animWidth = Integer.parseInt(wstr);
					}
					if (hstr != null && !("".equals(hstr))
							&& !("0").equals(hstr)) {
						animHeight = Integer.parseInt(hstr);
					}

				}

				sourceBitmap = ImageUtil.zoomBitmap(sourceBitmap, animWidth,
						animHeight);

				galleryView.loadData(position, onLine, view, sourceBitmap,
						mWaterfallView, true);
				galleryView.setVisibility(View.VISIBLE);
			}
		}
	}

	public boolean isGalleryShow() {
		if (galleryView.getVisibility() == View.VISIBLE) {
			return true;
		} else {
			return false;
		}
	}

	public void followScreen(String itemGuid) {
		galleryView.followScreen(itemGuid);
	}

	/**
	 * 长按删除图片
	 */
	public void runLongClick(int position) {
		final String param = "guids="
				+ mWaterfallView.filterResult.get(position).guid
				+ "&owner_tvmid=" + Constant.tvmId;
		if ("IMAGE".equals(mWaterfallView.packageType)
				|| "VIDEO".equals(mWaterfallView.packageType)) {
			new AlertDialog.Builder(Constant.activity)
					.setTitle(
							mContext.getResources().getString(
									R.string.package_remove_title))
					.setMessage(
							mContext.getResources().getString(
									R.string.package_remove_single_file))
					.setPositiveButton(
							mContext.getResources().getString(
									R.string.package_remove_yes),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

									try {
										sendPost(
												"http://"
														+ ICESDK.sharedICE(
																Constant.iceConnectionInfo
																		.getLoginICE(),
																Constant.iceConnectionInfo
																		.getUserInfoEntity())
																.getConnectHostName()
														+ "/ice3/remove_file.php",
												param);
										initUIAction();
										dialog.dismiss();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							})
					.setNegativeButton(
							mContext.getResources().getString(
									R.string.package_remove_no),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).show();
		} else {
			new AlertDialog.Builder(Constant.activity)
					.setTitle(
							mContext.getResources().getString(
									R.string.single_delete_title))
					.setMessage(
							mContext.getResources().getString(
									R.string.single_delete_content))
					.setPositiveButton(
							mContext.getResources().getString(
									R.string.single_delete_confirm), null)
					.show();
		}
	}

	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);

			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		galleryView.resetViewWH();
		judgeScape(newConfig);
	}

	private void judgeScape(Configuration newConfig) {
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 横向
			// setContentView(R.layout.waterfalllayout);
			// FloatingWindowService.remoteView.setVisibility(View.GONE);
		} else {
			// 竖向
			// setContentView(R.layout.waterfalllayout);
			// FloatingWindowService.remoteView.setVisibility(View.VISIBLE);
		}
	}

	protected void init(Context context) {

		this.mContext = context;

		rootView = LayoutInflater.from(mContext).inflate(
				ResourceUtil.getResId(mContext, "waterfall", "layout"), null);

		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);
		ImageCacheParams cacheParams = new ImageCacheParams(mContext,
				IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.55f);

		mImageFetcher = new ListImageFetcher(mContext, mImageThumbSize);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
		mImageFetcher.addImageCache(mContext, cacheParams);

		multiColumnLayout = (LinearLayout) rootView
				.findViewById(R.id.multicolumnlayout);
		mWaterfallView = (MultiColumnListView) rootView
				.findViewById(R.id.multicolumnview);
		setWaterFallViewPropertity();
		packageNameView = (TextView) rootView
				.findViewById(R.id.multicolumntext);

		viewlayout = (FrameLayout) rootView.findViewById(R.id.viewlayout);

		// galleryView = (GalleryView)
		// rootView.findViewById(R.id.galleryLayout);

		bottomLayout = (RelativeLayout) rootView.findViewById(R.id.bottommenu);
		pushMenuLayout = (RelativeLayout) rootView
				.findViewById(R.id.pushmenulayout);

		push = rootView.findViewById(R.id.push);
		pushdevider = rootView.findViewById(R.id.pushdevider);

		videoBitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
				R.drawable.video_flag)).getBitmap();

		confirm = (ImageView) rootView.findViewById(R.id.confirm);
		selectCount = (TextView) rootView.findViewById(R.id.selectcount);
		cancel = (ImageView) rootView.findViewById(R.id.cancel);

		push.setOnClickListener(this);

		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);

		this.addView(rootView);
	}

	public void initGalleryView(boolean isOnline) {
		if (galleryView == null) {
			if (isOnline) {
				galleryView = Constant.activity.getGalleryOnlineView();
			} else {
				galleryView = Constant.activity.getGalleryLocalView();
			}
		}

	}

	private void setWaterFallViewPropertity() {
		mWaterfallView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(PLA_AbsListView view,
					int scrollState) {
				// TODO Auto-generated method stub
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING
						|| scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					mImageFetcher.setPauseWork(true);
				} else {
					mImageFetcher.setPauseWork(false);
				}
				onClickPermit = false;
				handler.removeCallbacks(runnable);
				handler.postDelayed(runnable, 500);
			}

			@Override
			public void onScroll(PLA_AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		mWaterfallView.setCacheColorHint(Color.parseColor("#00000000"));
		mWaterfallView.setSelector(new ColorDrawable(Color
				.parseColor("#00000000")));

		mWaterfallView.setOnItemLongClickListener(this);
	}

	public void loadData(int position, int onLine) {

		this.pos = position;
		this.onLine = onLine;

		handler = new Handler();
		runnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				onClickPermit = true;
			}
		};

		// 当浏览本地瀑布流时,没有推送和同步操作
		setViewShowOrHide();

		initUIAction();
	}

	public void setViewShowOrHide() {
		if (onLine == 2) {
			push.setVisibility(View.GONE);
			pushdevider.setVisibility(View.GONE);
		} else {
			if (Constant.user.getPermisssion().getLevel()
					.equals(Permission.PERMISSION_HIGH)) {
				if (!Constant.COMMUNICATION_TYPE
						.equals(mWaterfallView.packageType)) {
					if (Constant.allScreenList.size() > 0) {
						push.setVisibility(View.VISIBLE);
						pushdevider.setVisibility(View.VISIBLE);
					} else {
						push.setVisibility(View.GONE);
						pushdevider.setVisibility(View.GONE);
					}
				} else {
					push.setVisibility(View.GONE);
					pushdevider.setVisibility(View.GONE);
				}
			} else {
				push.setVisibility(View.GONE);
				pushdevider.setVisibility(View.GONE);
			}

		}
	}

	/**
	 * 加载数据
	 */
	protected void initUIAction() {
		new GetItemsPathTask(pos, onLine).execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.push:
			canPush = true;// 进入选择状态
			bottomLayout.setVisibility(View.GONE);
			pushMenuLayout.setVisibility(View.VISIBLE);
			if (checkedCount > 0) {
				selectCount.setVisibility(View.VISIBLE);
				selectCount.setText(String.valueOf(checkedCount));
			} else {
				selectCount.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.confirm:
			canPush = false;// 得到所有选中的图片的个数
			bottomLayout.setVisibility(View.GONE);
			pushMenuLayout.setVisibility(View.INVISIBLE);
			Toast.makeText(mContext, "我选中了" + checkedCount + "个图片",
					Toast.LENGTH_SHORT).show();

			if (checkedCount > 0) {// 先判断是否选择了图片
				postdata.append("{\"type\":\"resource\",\"detail\":[");

				for (Entry<Integer, View> entry : checkedMap.entrySet()) {

					if (((View) entry.getValue()).findViewById(R.id.checked)
							.getVisibility() == View.VISIBLE) {
						postdata.append("{\"position\":\"\",\"id\":\""
								+ mWaterfallView.filterResult.get(entry
										.getKey()).guid + "\"},");
					}
				}
				postdata.deleteCharAt(postdata.length() - 1).append("]}");
				Log.i(TAG, "推送资源postdata=" + postdata.toString());
				AutoRemoteControlTask pushSourceTask = new AutoRemoteControlTask(
						mContext, CommandTypeEntity.PUSH, postdata.toString(),
						false);
				pushSourceTask.execute(3);

				MessageUtil.toastInfo(
						mContext,
						mContext.getString(R.string.pushresource) + ":"
								+ checkedCount
								+ mContext.getString(R.string.completed));

				checkedCount = 0;
				selectCount.setVisibility(View.INVISIBLE);
				// 页面取消选中,选中状态map取消
				postdata.setLength(0);// 清空json串
			}

			for (Entry<Integer, View> entry : checkedMap.entrySet()) {
				((View) entry.getValue()).findViewById(R.id.checked)
						.setVisibility(View.INVISIBLE);
				((View) entry.getValue()).findViewById(R.id.maskview)
						.setVisibility(View.INVISIBLE);
			}
			checkedMap.clear();
			break;
		case R.id.cancel:
			canPush = false;// 取消所有图片的选中状态
			checkedCount = 0;
			bottomLayout.setVisibility(View.GONE);
			pushMenuLayout.setVisibility(View.INVISIBLE);
			for (Entry<Integer, View> entry : checkedMap.entrySet()) {
				((View) entry.getValue()).findViewById(R.id.checked)
						.setVisibility(View.INVISIBLE);
				((View) entry.getValue()).findViewById(R.id.maskview)
						.setVisibility(View.INVISIBLE);
			}
			checkedMap.clear();
			break;
		}
	}

	public void onDestroy() {
		if (checkedMap != null) {
			checkedMap.clear();
		}

		imageCacheManager = null;
		System.gc();
	}

	/**
	 * 加载数据的task
	 * @author Administrator
	 *
	 */
	public class GetItemsPathTask extends
			AsyncTask<Void, Void, List<ImageWrapper>> {

		private int onLine;
		private int position;

		public GetItemsPathTask(int position, int onLine) {
			this.position = position;
			this.onLine = onLine;
		}

		protected List<ImageWrapper> doInBackground(Void... args) {

			mWaterfallView.imageFileNames.clear();
			mWaterfallView.sourceImageFileNames.clear();

			if (onLine == 2) {// 本地的数据
				for (int i = 0; i < mWaterfallView.itemVector.size(); i++) {
					ItemTable itemTable = (ItemTable) mWaterfallView.itemVector
							.get(i);
					if ("VIDEO".equals(itemTable.getItemType())) {
						itemTable.setItemWidth(String
								.valueOf(Constant.VIDEO_WIDTH));
						itemTable.setItemHeight(String
								.valueOf(Constant.VIDEO_HEIGHT));
					}
					mWaterfallView.imageFileNames.add(itemTable
							.getItemFilePath());
					int w = Constant.screenWidth / 2;
					mWaterfallView.imageFileNames.add(String.valueOf(w));
					int height = (Integer.parseInt(itemTable.getItemHeight()) * w)
							/ Integer.parseInt(itemTable.getItemWidth());
					mWaterfallView.imageFileNames.add(String.valueOf(height));
					mWaterfallView.imageFileNames.add(itemTable.getItemType());
					mWaterfallView.imageFileNames.add(itemTable.getItemTitle());
					mWaterfallView.sourceImageFileNames.add(itemTable
							.getItemFilePath());//添加本地图片的路径
				}
			} else if (onLine == 1) {// 在线的数据
				getAllPaths();
			}
			ImgResource.onLine = onLine;
			List<ImageWrapper> imageWrapperList = ImgResource.genData(
					videoBitmap, mWaterfallView.packageType,
					mWaterfallView.imageFileNames);
			return imageWrapperList;
		}

		protected void onPostExecute(List<ImageWrapper> imageWrapperList) {
			loadData(imageWrapperList);
		}
	}

	/**
	 * 加载数据函数
	 * @param imageWrapperList
	 */
	private void loadData(List<ImageWrapper> imageWrapperList) {
		mAdapter = new CommonAdapter<ImageWrapper>(
				Constant.activity.getLayoutInflater(), new SimpleViewBuilder(
						mContext), mContext, mImageFetcher);
		mWaterfallView.setAdapter(mAdapter);
		mAdapter.update(imageWrapperList);
	}

	/**
	 * 在线数据加载
	 */
	private void getAllPaths() {
		SearchFileEntity condition = new SearchFileEntity();
		condition.orderColumn = "submit_date";
		if (Images.allPackName[pos].pack_type
				.equals(Constant.COMMUNICATION_TYPE)) {// 如果包的类型是交流,则逆序显示文件,否则顺序显示文件
			condition.orderMethod = SearchFileOrderMethod.DESC;
			/*
			 * if(!isOwner){ condition.tag = Constant.tvmId; }
			 */
		} else {
			condition.orderMethod = SearchFileOrderMethod.ASC;
		}

		condition.inPack.add(Images.allPackName[pos].packname);// 将包名字作为查询条件
		String fileURL = "";// 文件url地址
		try {
			ICESDK mySDK = ICESDK.sharedICE(
					Constant.iceConnectionInfo.getLoginICE(),
					Constant.iceConnectionInfo.getUserInfoEntity());
			SearchFileDetailStatusEntity[] result = mySDK.searchFile(condition);
			if (result != null && result.length != 0) {
				for (SearchFileDetailStatusEntity searchEntity : result) {
					// 原图
					if ("IMAGE".equals(searchEntity.file_type)
							|| "VIDEO".equals(searchEntity.file_type)) {
						mWaterfallView.filterResult.add(searchEntity);

						fileURL = mySDK.getSideThumbURLByFileDetail(
								searchEntity,
								Constant.MAX_WIGTH_ONLINE_WATERFALL,
								SideThumbMethod.width);
						if ("VIDEO".equals(searchEntity.file_type)) {
							searchEntity.width = Constant.VIDEO_WIDTH;
							searchEntity.height = Constant.VIDEO_HEIGHT;
						}
						mWaterfallView.imageFileNames.add(fileURL);
						mWaterfallView.imageFileNames.add(String
								.valueOf(Constant.screenWidth / 2));
						mWaterfallView.imageFileNames.add(String
								.valueOf((searchEntity.height
										* Constant.screenWidth / 2)
										/ searchEntity.width));
						mWaterfallView.imageFileNames
								.add(searchEntity.file_type);
						mWaterfallView.imageFileNames.add(searchEntity.title);

						if (searchEntity.height >= Constant.MAX_HEIGHT
								|| searchEntity.width > Constant.MAX_WIGTH) {
							if (searchEntity.height > searchEntity.width) {
								fileURL = mySDK.getSideThumbURLByFileDetail(
										searchEntity, Constant.MAX_HEIGHT,
										SideThumbMethod.height);
							} else {
								fileURL = mySDK.getSideThumbURLByFileDetail(
										searchEntity, Constant.MAX_WIGTH,
										SideThumbMethod.width);
							}
						} else {
							fileURL = mySDK.getUrlByFileDetail(searchEntity);
						}

						mWaterfallView.sourceImageFileNames.add(fileURL);//添加在线图片的url
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		ScrollViewPager viewPager = (ScrollViewPager) rootView
				.findViewById(R.id.viewPager);
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			/*
			 * if(FloatingWindowService.fullScreen){
			 * FloatingWindowService.closeRemoteView() ; return false; }else
			 * if(waterContainer.getVisibility() == View.VISIBLE){ if(isRotate
			 * && this.viewlayout.getVisibility() == View.VISIBLE){ closeMenu();
			 * }else{ galleryView.backToWaterFall(); } return false; }else{
			 * return super.onKeyDown(keyCode, event); }
			 */
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void dealConfigurationChanged(Configuration newConfig) {
		galleryView.dealConfigurationChanged(newConfig);
	}

	/**
	 * 长按删除图片
	 */
	@Override
	public boolean onItemLongClick(PLA_AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		if (onLine == 1
				&& Constant.user.getPermisssion().getLevel()
						.equals(Permission.PERMISSION_HIGH)) {
			runLongClick(position);
		}
		return true;
	}

}// end of class
