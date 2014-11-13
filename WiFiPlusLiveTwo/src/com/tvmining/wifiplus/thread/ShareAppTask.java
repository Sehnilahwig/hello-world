package com.tvmining.wifiplus.thread;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.tvmining.wifiplus.entity.UpdateJsonBean;
import com.tvmining.wifiplus.httpserver.Server;
import com.tvmining.wifiplus.util.Constant;
import com.tvmining.wifiplus.util.DelFile;
import com.tvmining.wifiplus.util.ImageUtil;
import com.tvmining.wifiplus.util.IntAreaUtil;
import com.tvmining.wifiplus.util.Utility;

public class ShareAppTask extends AsyncTask {

	private static final String TAG = "MainActivity";
	String path = "http://update.tvmining.com/SoftUpdateServer/update/update?product=eq&device=Android&version=3.5.0.0_release&format=json";
	String iosPath = "http://update.tvmining.com/SoftUpdateServer/update/update?product=eq&device=iPhone&version=3.5.0.0_Release&format=json";
	private StringBuffer checkUpdateJson = new StringBuffer();
	private StringBuffer iosCheckUpdateJson = new StringBuffer();
	private Context mContext;
	private long downloadSize = 0;
	private long totalSize = 0;
	private boolean[] freshArray;
	private String action;

	public ShareAppTask(Context mContext, String action) {
		this.action = action;
		this.mContext = mContext;
	}

	@Override
	protected Void doInBackground(Object... params) {
		UpdateJsonBean bean = null;
		URL url = null;
		freshArray = new boolean[1000000];
		try {
			// android
			url = new URL(path);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setConnectTimeout(3000);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				checkUpdateJson.append(line);
			}
			reader.close();

			// ios
			// url = new URL(iosPath);
			// urlConnection = (HttpURLConnection) url.openConnection();
			// urlConnection.setConnectTimeout(3000);
			// reader = new BufferedReader(new InputStreamReader(
			// urlConnection.getInputStream()));
			//
			// while ((line = reader.readLine()) != null) {
			// iosCheckUpdateJson.append(line);
			// }
			// reader.close();
			//
			// String pListUrl = parseIosJson(iosCheckUpdateJson.toString());
			//
			// iosCheckUpdateJson.setLength(0);
			//
			// url = new URL(pListUrl);
			// urlConnection = (HttpURLConnection) url.openConnection();
			// urlConnection.setConnectTimeout(3000);
			// reader = new BufferedReader(new InputStreamReader(
			// urlConnection.getInputStream()));
			//
			// while ((line = reader.readLine()) != null) {
			// iosCheckUpdateJson.append(line);
			// }
			// reader.close();

			File dirFilePath = new File(Constant.SHARE_APP_DIR_PATH);
			if (!dirFilePath.exists()) {
				dirFilePath.mkdirs();
			}

			File plistFile = new File(Constant.SHARE_APP_PLIST_PATH);
			if (!plistFile.exists()) {
				plistFile.createNewFile();
			}

			Document doc = generateXml(iosCheckUpdateJson.toString());
			outputXml(doc, Constant.SHARE_APP_PLIST_PATH);

			List<String> resultList = parseIosXml(iosCheckUpdateJson.toString());
			resultList.add(parseAndroidJson(checkUpdateJson.toString()));
			Log.i(TAG, "");
			// 处理获取到的json结果
			checkOrDownload(resultList);

		} catch (MalformedURLException e) {
			Log.i(TAG, "发生异常了,MalformedURLException:", e);
		} catch (IOException e) {
			Log.i(TAG, "发生异常了,IOException:", e);
		} catch (Exception e) {
			Log.i(TAG, "发生异常了", e);
		}
		return null;
	}

	/**
	 * 解析xml字符串成List<Map>
	 * 
	 * @param String
	 * @return List
	 */
	public List parseIosXml(String xmlDoc) {
		// 创建一个新的字符串
		StringReader xmlString = new StringReader(xmlDoc);
		// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
		InputSource source = new InputSource(xmlString);
		// 创建一个新的SAXBuilder
		SAXBuilder saxb = new SAXBuilder();

		List<String> resultList = null;
		try {
			resultList = new ArrayList<String>();
			// 通过输入源构造一个Document
			org.jdom.Document doc = saxb.build(source);
			// 取的根元素
			Element root = doc.getRootElement();

			// 得到根元素所有子元素的集合
			List node = root.getChildren();
			Element et = null;
			parent: for (int i = 0; i < node.size(); i++) {
				et = (Element) node.get(i);// 循环依次得到子元素
				List subNode = et.getChildren(); // 得到内层子节点
				Element subEt = null;
				for (int j = 0; j < subNode.size(); j++) {
					subEt = (Element) subNode.get(j); // 循环依次得到子元素
					if (subEt.getName().equals("array")) {
						List arraySubNode = subEt.getChildren();
						for (int l = 0; l < arraySubNode.size(); l++) {
							Element arraySubEt = (Element) arraySubNode.get(l);
							List arrayDictSubNode = arraySubEt.getChildren();
							for (int l1 = 0; l1 < arrayDictSubNode.size(); l1++) {
								Element arrayDictSubEt = (Element) arrayDictSubNode
										.get(l1);
								List valueSubNode = arrayDictSubEt
										.getChildren();
								for (int l2 = 0; l2 < valueSubNode.size(); l2++) {
									Element valueSubEt = (Element) valueSubNode
											.get(l2);
									if ("dict".equals(valueSubEt.getName())) {
										List valueDictSubNode = valueSubEt
												.getChildren();
										for (int l3 = 0; l3 < valueDictSubNode
												.size(); l3++) {
											Element valueDictSubEt = (Element) valueDictSubNode
													.get(l3);
											if (valueDictSubEt.getName()
													.equals("string")
													&& valueDictSubEt
															.getTextTrim()
															.contains("http://")) {
												resultList.add(valueDictSubEt
														.getTextTrim());
											}
											Log.d("aaaaaaaaaaaaaaaaaaaaa",
													valueDictSubEt.getName()
															+ ","
															+ valueDictSubEt
																	.getTextTrim());

										}

									}
								}

							}

						}

					}
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultList;
	}

	public Document generateXml(String xmlStr) {
		Document doc = null;
		InputSource is = null;
		StringReader sr = null;
		try {
			sr = new StringReader(xmlStr);
			is = new InputSource(sr);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
			return null;// 如果出现异常，则不再往下执行
		} finally {
			if (sr != null) {
				sr.close();
			}
		}

		return doc;
	}

	private void outputXml(Document doc, String fileName) throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMSource source = new DOMSource(doc);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");// 设置文档的换行与缩进
		PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
		StreamResult result = new StreamResult(pw);
		transformer.transform(source, result);
		System.out.println("生成XML文件成功!");
	}

	public String parseIosJson(String str) {
		String addr = null;
		try {
			JSONObject jsonObject = new JSONObject(str);

			String status = jsonObject.getString("status");
			String msg = "";
			if ("FAILED".equals(status)) {
				msg = jsonObject.getString("msg");
			}

			JSONArray jsonArray = jsonObject.getJSONArray("versionlist");

			Log.i(TAG, "jsonArray=" + jsonArray.toString());

			JSONObject jsonItem;
			if (jsonArray != null && jsonArray.length() > 0) {
				jsonItem = (JSONObject) jsonArray.get(0);

				addr = jsonItem.getString("addr");
				if (addr != null && addr.contains("http://update.tvmining.com")) {
					addr = addr.substring(
							addr.indexOf("http://update.tvmining.com"),
							addr.length());
				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return addr;
	}

	public String parseAndroidJson(String androidJsonStr) {
		String addr = null;
		try {
			JSONObject jsonObject = new JSONObject(androidJsonStr);

			String status = jsonObject.getString("status");
			String msg = "";
			if ("FAILED".equals(status)) {
				msg = jsonObject.getString("msg");
			}

			JSONArray jsonArray = jsonObject.getJSONArray("versionlist");

			Log.i(TAG, "jsonArray=" + jsonArray.toString());

			JSONObject jsonItem;
			if (jsonArray != null && jsonArray.length() > 0) {
				jsonItem = (JSONObject) jsonArray.get(0);
				addr = jsonItem.getString("addr");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return addr;
	}

	private boolean checkAppExist(List<String> resultList) {
		boolean isHave = true;
		for (int i = 0; i < resultList.size(); i++) {
			String addr = (String) resultList.get(i);
			String appName = addr.substring(addr.lastIndexOf("/"),
					addr.length());
			File appFile = new File(Constant.SHARE_APP_DIR_PATH
					+ File.separator + appName);
			if (appFile == null || (appFile != null && !appFile.exists())) {
				isHave = false;
				break;
			}
		}

		if (!isHave) {
			File dirFilePath = new File(Constant.SHARE_APP_DIR_PATH);
			DelFile.deleteExceptSuffix(dirFilePath, "plist");
		}

		return isHave;
	}

	public void checkOrDownload(List<String> resultList) {
		try {
			if (resultList != null) {
				if ("check".equals(action)) {
					Message message = new Message();
					message.what = Constant.HANDLER_APP_SHARE_FILE_CHECK;
					message.obj = checkAppExist(resultList);
					Constant.activity.getHandler().sendMessage(message);
				} else if ("download".equals(action)) {
					if (!checkAppExist(resultList)) {
						for (int i = 0; i < resultList.size(); i++) {
							totalSize = totalSize
									+ ImageUtil.getFileSize(resultList.get(i));
						}

						for (int i = 0; i < resultList.size(); i++) {
							String addr = (String) resultList.get(i);
							downloadApp(addr, Constant.SHARE_APP_DIR_PATH);
						}

					} else {
						String ipport = "http://" + Server.host + ":"
								+ Server.port + File.separator;
						String iosUrl = Constant.SHARE_WEB_ROOT_DIR;
						String androidUrl = Constant.SHARE_WEB_ROOT_DIR;
						File shareAppFile = new File(
								Constant.SHARE_APP_DIR_PATH);
						File[] files = shareAppFile.listFiles();
						if (files != null) {
							for (int i = 0; i < files.length; i++) {
								if (files[i].isFile()
										&& "plist".equals(DelFile
												.getExtensionName(files[i]
														.getName()))) {
									iosUrl = Constant.SHARE_APP_IOS_FORWORD
											+ ipport + iosUrl
											+ files[i].getPath();
								} else if ("apk".equals(DelFile
										.getExtensionName(files[i].getName()))) {
									androidUrl = ipport + androidUrl
											+ files[i].getPath();
								}
							}
						}
						Utility.copyToDisk(mContext, iosUrl, androidUrl);
						Utility.editIosXml(Constant.SHARE_WEB_ROOT_DIR
								+ Constant.SHARE_APP_DIR_PATH, ipport);

						String shareAppHtmlUrl = ipport
								+ Constant.SHARE_WEB_ROOT_DIR
								+ Constant.SHARE_APP_DIR_PATH
								+ Constant.SHARE_APP_HTML_NAME;
						shareAppHtmlUrl = new String(
								shareAppHtmlUrl.getBytes("UTF-8"), "ISO-8859-1");

						Bitmap bitmap = Utility.create2DCode(shareAppHtmlUrl);

						Message message = new Message();
						message.what = Constant.HANDLER_APP_SHARE_SHOW_QRCODE;
						message.obj = bitmap;
						Constant.activity.getHandler().sendMessage(message);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void downloadApp(String appUrl, String dirPath) {
		BufferedInputStream bis = null;
		RandomAccessFile fos = null;
		InputStream is = null;
		byte[] buf = new byte[1024];
		HttpClient httpClient = new DefaultHttpClient();
		try {
			is = ImageUtil.getFileStream(String.valueOf(appUrl), 0, httpClient);
			File file = new File(dirPath);
			if (!file.exists()) {
				file.mkdirs();
			}

			String appName = appUrl.substring(appUrl.lastIndexOf("/"),
					appUrl.length());

			File imageFile = new File(dirPath + File.separator + appName);
			if (!imageFile.exists()) {
				imageFile.createNewFile();
			}

			fos = new RandomAccessFile(imageFile, "rw");// 随机存取文件
			// 设置开始写文件的位置
			fos.seek(0);
			bis = new BufferedInputStream(is);
			// 开始循环以流的形式读写文件
			int len = 0;

			while ((len = bis.read(buf, 0, 1024)) != -1) {
				fos.write(buf, 0, len);

				downloadSize += len;
				Log.d("aaaaaaaaaaaaaaaaaaaaaaaa", "downloadSize:"
						+ downloadSize);
				Log.d("aaaaaaaaaaaaaaaaaaaaaaaa", "totalSize:" + totalSize);
				if (downloadSize <= totalSize) {
					int rate = (int) ((downloadSize * 100.0f) / totalSize);
					Log.d("aaaaaaaaaaaaaaaaaaaaaaaa", "rate:" + rate);
					if (IntAreaUtil.isFresh(freshArray, rate)) {
						Message msg = new Message();
						msg.what = Constant.HANDLER_APP_SHARE_DOWNLOAD;
						msg.obj = rate;
						Constant.activity.getHandler().sendMessage(msg);
					}
				}
			}

			Message msg = new Message();
			msg.what = Constant.HANDLER_APP_SHARE_DOWNLOAD;
			msg.obj = 100;
			Constant.activity.getHandler().sendMessage(msg);

		} catch (Exception e) {
			// 下载出现异常，先重试下载3次，如果仍然下载失败，保存下载失败的数据至数据库，
			Log.e("ShareAppTask", e.getMessage());
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
				Log.e("ShareAppTask", e.getMessage());
			}
		}
	}

}
