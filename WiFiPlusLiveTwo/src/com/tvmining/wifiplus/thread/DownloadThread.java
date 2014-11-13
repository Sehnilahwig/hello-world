package com.tvmining.wifiplus.thread;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;

import com.tvmining.sdk.ICESDK;
import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;
import com.tvmining.sdk.entity.SearchFileEntity;
import com.tvmining.sdk.entity.SideThumbMethod;
import com.tvmining.wifiplus.activity.MainActivity;
import com.tvmining.wifiplus.application.EmeetingApplication;
import com.tvmining.wifiplus.entity.DownloadProgressEntity;
import com.tvmining.wifiplus.entity.DownloadTempTable;
import com.tvmining.wifiplus.entity.ItemTable;
import com.tvmining.wifiplus.entity.PakgeTable;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.wifiplus.util.IntAreaUtil;
import com.tvmining.wifiplus.util.Utility;

public class DownloadThread extends Thread {
	private Context context;
	private Date date;
	private SimpleDateFormat sdf;
	private long downloadSize;
	private boolean[] freshArray;
	private long allSize;
	private MainActivity activity;
	public String downloadingPackageName;
	private final int REPEAT_COUNT = 10;
	private long singleDownloadSize;
	private DownloadTempTable untampTable;
	private boolean isSuccess;
	private PackInfoEntity entity;
	private long pakgeIndex;
	private String itemGuid;
	private int itemWhich;

	public DownloadThread(Context mContext) {
		context = mContext;
		sdf = new SimpleDateFormat("yyyy-MM-dd");
	}

	public void run() {
		try {
			while (true) {
				if (!Constant.queue.isEmpty()) {
					if (!Constant.queuePause) {
						synchronized (Constant.queue) {
							entity = (PackInfoEntity) Constant.queue.take();
							pakgeIndex = entity.pkgIndex;
							if (entity.isPause) {
								continue;
							}
							downloadingPackageName = entity.packname;
						}

						parseUrl();
					} else {
						synchronized (Constant.queue) {
							Constant.queue.wait();
						}
					}

				} else {
					synchronized (Constant.queue) {
						Constant.uploadOrDownload = Constant.DO_NOTHING;
						Constant.queue.wait();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addToDBQueue(Object obj) {
		synchronized (Constant.sqlQueue) {
			Constant.sqlQueue.add(obj);
			Constant.sqlQueue.notify();
		}
	}

	private synchronized void parseUrl() {
		if (entity.isPause) {
			return;
		}
		String fileURL = "";// 文件url地址
		SearchFileDetailStatusEntity[] result = null;

		ICESDK mySDK = null;
		try {
			mySDK = ICESDK.sharedICE(Constant.iceConnectionInfo.getLoginICE(),
					Constant.iceConnectionInfo.getUserInfoEntity());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String pkgPath = null;
		date = new Date();
		if (Constant.QRCODE_ACTION_SHARE_PACKAGE.equals(entity.action)) {
			result = entity.result;
			// 包路径
			pkgPath = Constant.savePath + sdf.format(date) + File.separator
					+ entity.iceName + File.separator + entity.tempPackName;
		} else {
			if (entity.packageUpdate) {
				result = new SearchFileDetailStatusEntity[entity.compareVector
						.size()];
				result = entity.compareVector.toArray(result);
			} else {
				SearchFileEntity condition = new SearchFileEntity();
				condition.inPack.add(entity.packname);// 将包名字作为查询条件
				result = mySDK.searchFile(condition);
			}

			// 包路径
			pkgPath = Constant.savePath + sdf.format(date) + File.separator
					+ Constant.iceConnectionInfo.getLoginICE().connectICEName
					+ File.separator + entity.tempPackName;
		}

		if (result != null && result.length != 0) {
			if (entity.isPause) {
				return;
			}
			DownloadProgressEntity progressEntity = new DownloadProgressEntity();

			progressEntity.setIceDate(sdf.format(date));
			progressEntity.setPakgeIndex(entity.pkgIndex);
			progressEntity.setPakgeName(entity.tempPackName);
			progressEntity.setIceIndex(entity.iceIndex);
			progressEntity.setPackageGuid(entity.thumb_guid);

			downloadSize = 0;
			allSize = 0;
			int which = 0;
			// 根据包表的status判断此包是否是上次下载失败的包，如果是，则赋值downloadedSize和downloadedItemSize
			PakgeTable pkgTable = Constant.dbConnection
					.queryPkgByGuid(entity.thumb_guid);
			if (pkgTable != null) {
				untampTable = Constant.dbConnection.queryDownloadTempByStatus(
						entity.pkgIndex, 1);
				if (untampTable != null) {
					which = untampTable.getDownloadedItemWhich();
					Log.d("DownloadThread-which111", "itemGuid:" + itemGuid
							+ "," + "itemWhich:" + itemWhich);
					downloadSize = Long.parseLong(untampTable
							.getDownloadedSize());
				}
			}

			// download item

			freshArray = new boolean[1000000];
			if (Constant.QRCODE_ACTION_SHARE_PACKAGE.equals(entity.action)) {
				allSize = entity.packageSize;
			} else {
				if (!entity.packageUpdate) {
					if (entity.packageSize != -0) {
						allSize = entity.packageSize;
						/*
						 * for (SearchFileDetailStatusEntity searchEntity :
						 * result) { if(entity.isPause){ return; } // 原图 try {
						 * fileURL = mySDK.getUrlByFileDetail( searchEntity);
						 * 
						 * } catch (Exception e) { // TODO Auto-generated catch
						 * block e.printStackTrace(); } long size =
						 * ImageUtil.getFileSize(fileURL); allSize += size; }
						 */
					} else {
						for (SearchFileDetailStatusEntity searchEntity : result) {
							if (entity.isPause) {
								return;
							}
							// 原图
							try {
								fileURL = mySDK
										.getUrlByFileDetail(searchEntity);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							allSize += ImageUtil.getFileSize(fileURL);
						}
					}
				} else {
					for (SearchFileDetailStatusEntity searchEntity : result) {
						if (entity.isPause) {
							return;
						}
						// 原图
						try {
							fileURL = mySDK.getUrlByFileDetail(searchEntity);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						allSize += ImageUtil.getFileSize(fileURL);
					}
				}
			}

			isSuccess = true;
			for (int l = which; l < result.length; l++) {
				if (entity.isPause) {
					return;
				}

				itemWhich = l;
				Log.d("DownloadThread-which22222", "itemGuid:" + itemGuid + ","
						+ "itemWhich:" + itemWhich);
				itemGuid = result[l].guid;
				singleDownloadSize = 0;
				if (untampTable != null) {
					singleDownloadSize = Long.parseLong(untampTable
							.getDownloadedItemSize());
					untampTable = null;
				}

				// 原图
				if (Constant.QRCODE_ACTION_SHARE_PACKAGE.equals(entity.action)) {
					fileURL = result[l].filePathUrl;
					if ("VIDEO".equals(result[l].file_type)) {
						// download thumburl for video
						String videoThumbURL;
						try {
							videoThumbURL = result[l].videoFilePathUrl;
							ImageUtil.startDownload(videoThumbURL, pkgPath, 0);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				} else {
					try {
						fileURL = mySDK.getUrlByFileDetail(result[l]);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					if ("VIDEO".equals(result[l].file_type)) {
						// download thumburl for video
						String videoThumbURL;
						try {
							videoThumbURL = mySDK.getSideThumbURLByFileDetail(
									result[l], Constant.screenWidth,
									SideThumbMethod.width);
							ImageUtil.startDownload(videoThumbURL, pkgPath, 0);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

				// http://10.0.200.2/resource/%e6%95%b4%e5%ae%a3%e4%bc%a0%e7%89%87/692b9644-16ab-4723-a363-091aa1616e2e.jpg_w_720.jpg

				int repeated = 0;

				/*
				 * while(repeated < REPEAT_COUNT){ try{
				 * startDownloadItems(fileURL,
				 * pkgPath,downloadMap,entity.pkgIndex); isSuccess = true;
				 * break; }catch(Exception e){ //应该加一个回调，让用户知道此时连接中断，正在尝试连接
				 * 
				 * repeated++; isSuccess = false; try { sleep(3000); } catch
				 * (InterruptedException e1) { // TODO Auto-generated catch
				 * block e1.printStackTrace(); } continue; } }
				 */

				try {
					startDownloadItems(fileURL, pkgPath, progressEntity,
							entity.pkgIndex);
				} catch (Exception e) {
					// 应该加一个回调，让用户知道此时连接中断，正在尝试连接
					Log.d("touchFinish", "ccc");
					repeated++;
					isSuccess = false;
					Constant.downloadThread.savePauseData(entity);
					/*
					 * try { sleep(3000); } catch (InterruptedException e1) { //
					 * TODO Auto-generated catch block e1.printStackTrace(); }
					 */
				}

				if (entity.isPause) {
					Message msg = new Message();
					msg.obj = pkgTable;
					msg.what = Constant.HANDLER_DOWNLOAD_AUTO_PAUSE;
					Constant.activity.getHandler().sendMessage(msg);
					return;
				} else {
					// 下载成功
					if (!Constant.QRCODE_ACTION_SHARE_PACKAGE
							.equals(entity.action)) {
						DownloadTempTable tampTable = Constant.dbConnection
								.queryDownloadTempByGuid(entity.pkgIndex,
										result[l].guid);
						if (isSuccess) {
							// image
							ItemTable itemTable = new ItemTable();
							itemTable.setValues(entity.pkgIndex, result[l],
									pkgPath, entity, null, "normal");
							addToDBQueue(itemTable);

							if (tampTable != null) {
								Constant.dbConnection.updateDownloadTemp(
										tampTable.getTempIndex(),
										String.valueOf(downloadSize),
										String.valueOf(singleDownloadSize),
										"0", 2, allSize);
							}
						}
					} else {
						if (isSuccess) {
							// image
							ItemTable itemTable = new ItemTable();
							itemTable.setValues(entity.pkgIndex, result[l],
									pkgPath, entity, null, "normal");
							addToDBQueue(itemTable);
						}
					}

				}

				// searchEntity = null;
			}

			if (isSuccess) {
				long pakgeIndex = entity.pkgIndex;
				entity.packageUpdate = false;
				Constant.dbConnection.setStatus(pakgeIndex, 2);
				Constant.dbConnection
						.removeDownloadedPakgeTempRecord(entity.pkgIndex);
				EmeetingApplication.setUnDownloadedCount(EmeetingApplication
						.getUnDownloadedCount() - 1);
				Utility.removeDownloadData(entity.thumb_guid);
				Constant.downloadThread.downloadingPackageName = null;

				Message msg = new Message();
				msg.what = Constant.HANDLER_ENTER_LOCAL_VIEW;
				Constant.activity.getHandler().sendMessage(msg);
			}
		}
	}

	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	private synchronized void startDownloadItems(final String imageUrl,
			String pkgPath, DownloadProgressEntity progressEntity, long pkgIndex)
			throws Exception {
		BufferedInputStream bis = null;
		RandomAccessFile fos = null;
		InputStream is = null;
		byte[] buf = new byte[1024];
		HttpClient httpClient = new DefaultHttpClient();
		try {
			is = ImageUtil.getFileStream(String.valueOf(imageUrl),
					singleDownloadSize, httpClient);
			File file = new File(pkgPath);
			if (!file.exists()) {
				file.mkdirs();
			}

			String imageName = imageUrl.substring(imageUrl.lastIndexOf("/"),
					imageUrl.length());

			File imageFile = new File(pkgPath + File.separator + imageName);
			if (!imageFile.exists()) {
				imageFile.createNewFile();
			}

			fos = new RandomAccessFile(imageFile, "rw");// 随机存取文件
			// 设置开始写文件的位置
			fos.seek(singleDownloadSize);
			bis = new BufferedInputStream(is);
			// 开始循环以流的形式读写文件
			int len = 0;
			while ((len = bis.read(buf, 0, 1024)) != -1) {
				fos.write(buf, 0, len);
				if (entity.isPause) {
					isSuccess = false;
					Log.d("touchFinish", "sss");
					return;
				}
				singleDownloadSize += len;
				downloadSize += len;
				if (downloadSize <= allSize) {
					int rate = (int) ((downloadSize * 100.0f) / allSize);
					if (IntAreaUtil.isFresh(freshArray, rate)) {
						progressEntity.setProgress(rate);
						Constant.downloadingMap
								.put(progressEntity.getPackageGuid(),
										progressEntity);

						synchronized (Constant.freshQueue) {
							Constant.freshQueue.add(progressEntity);
							Constant.freshQueue.notify();
						}
					}

				}
			}

		} catch (Exception e) {
			// 下载出现异常，先重试下载3次，如果仍然下载失败，保存下载失败的数据至数据库，
			throw e;
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
				if (is != null) {
					is.close();
				}
				if (bis != null) {
					bis.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}

	public static Long getLocalFileSize(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			return file.length();
		} else {
			return 0l;
		}

	}

	public void savePauseData(PackInfoEntity clickEntity) {
		if (Constant.uploadOrDownload.equals(Constant.DO_DOWNLOAD)) {
			if (clickEntity.thumb_guid.equals(entity.thumb_guid)) {
				Constant.queuePause = true;
				entity.isPause = true;

				saveToDB(Constant.DOWNLOAD_PAUSE);
				Constant.queuePause = false;
				synchronized (Constant.queue) {
					Constant.queue.notify();
				}

			} else {
				entity.isPause = true;
			}
		}
	}

	public void resumePauseData(PackInfoEntity netPackEntity) {
		boolean isHave = false;
		if (Constant.uploadOrDownload.equals(Constant.DO_DOWNLOAD)) {

			// 集合方式遍历，元素不会被移除
			Iterator iterator = Constant.queue.iterator();
			while (iterator.hasNext()) {
				PackInfoEntity packEntity = (PackInfoEntity) iterator.next();
				if (packEntity.thumb_guid.equals(netPackEntity.thumb_guid)) {
					isHave = true;
					packEntity.isPause = true;
					netPackEntity.isPause = true;
					break;
				}
			}
			if (!isHave) {
				netPackEntity.isPause = false;
				synchronized (Constant.queue) {
					Constant.queue.add(netPackEntity);
					Constant.queue.notify();
				}
			}
		}
	}

	public void saveToDB(int status) {
		if (Constant.uploadOrDownload.equals(Constant.DO_DOWNLOAD)
				&& entity != null) {
			Log.d("DownloadThread-which", "itemGuid:" + itemGuid + ","
					+ "itemWhich:" + itemWhich);
			PakgeTable pkgTable = Constant.dbConnection
					.queryPkg((int) pakgeIndex);
			if (pkgTable != null
					&& Constant.DOWNLOAD_FINISHED != pkgTable.getStatus()) {
				Constant.dbConnection.setStatus(pakgeIndex, status);

				DownloadTempTable tampTable = Constant.dbConnection
						.queryDownloadTempByGuid(pakgeIndex, itemGuid);
				if (tampTable != null) {
					Constant.dbConnection
							.updateDownloadTemp(itemWhich,
									tampTable.getTempIndex(),
									String.valueOf(downloadSize),
									String.valueOf(singleDownloadSize), "0", 1,
									allSize);
				}
			}
		}

	}
}