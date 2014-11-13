package com.tvmining.wifiplus.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tvmining.wifiplus.entity.DownloadTempTable;
import com.tvmining.wifiplus.entity.ICETable;
import com.tvmining.wifiplus.entity.ItemTable;
import com.tvmining.wifiplus.entity.PakgeTable;
import com.tvmining.wifiplus.entity.VersionInfo;

public class DBConnection {
	public SQLiteDatabase db;
	private DatabaseOpener opener;

	public DBConnection(Context context) {
		opener = new DatabaseOpener(context.getApplicationContext());
		db = this.opener.getWritableDatabase();
	}

	public void beginTransaction() {
		if (!db.isOpen())
			db = this.opener.getWritableDatabase();
		db.beginTransaction();
	}

	public void close() {
		this.db.close();
		this.opener.close();
	}

	public void commit() {
		this.db.setTransactionSuccessful();
	}

	public void endTransaction() {
		this.db.endTransaction();
	}

	public Vector getFutureData(int count) {
		Vector vector = null;
		Cursor cursor = null;
		ICETable entity = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			vector = new Vector();
			cursor = db
					.rawQuery(
							"select iceIndex,iceName,iceSmallIconPath,iceMediumIconPath,iceBigIconPath,iceDate "
									+ " from ICETable"
									+ " where ICEDate > '"
									+ sdf.format(new Date())
									+ "' "
									+ " order by ICEDate desc"
									+ " limit 0,"
									+ count + ";", null);
			while (cursor.moveToNext()) {
				Log.d("DBConnection", "future data:" + cursor.getString(0));
				entity = new ICETable();
				entity.setIceIndex(cursor.getInt(0));
				entity.setIceName(cursor.getString(1));
				entity.setIceSmallIconPath(cursor.getString(2));
				entity.setIceMediumIconPath(cursor.getString(3));
				entity.setIceBigIconPath(cursor.getString(4));
				entity.setIceDate(cursor.getString(5));
				vector.add(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return vector;
	}

	public Vector getBeforeData(int count) {
		Vector vector = null;
		Cursor cursor = null;
		ICETable entity = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			vector = new Vector();
			cursor = db
					.rawQuery(
							"select iceIndex,iceName,iceSmallIconPath,iceMediumIconPath,iceBigIconPath,iceDate "
									+ " from ICETable"
									+ " where ICEDate <= '"
									+ sdf.format(new Date())
									+ "' "
									+ " order by ICEDate desc"
									+ " limit 0,"
									+ count + ";", null);
			while (cursor.moveToNext()) {
				Log.d("DBConnection", "future data:" + cursor.getString(0));
				entity = new ICETable();
				entity.setIceIndex(cursor.getInt(0));
				entity.setIceName(cursor.getString(1));
				entity.setIceSmallIconPath(cursor.getString(2));
				entity.setIceMediumIconPath(cursor.getString(3));
				entity.setIceBigIconPath(cursor.getString(4));
				entity.setIceDate(cursor.getString(5));
				vector.add(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return vector;
	}

	public Vector getAllICEData() {
		Vector vector = null;
		Cursor cursor = null;
		ICETable entity = null;
		try {
			cursor = db
					.rawQuery(
							"select iceIndex,iceName,iceSmallIconPath,iceMediumIconPath,iceBigIconPath,iceDate,iceExtentOne "
									+ " from ICETable"
									+ " order by ICEDate desc;", null);
			while (cursor.moveToNext()) {
				if (vector == null) {
					vector = new Vector();
				}
				Log.d("DBConnection", "future data:" + cursor.getString(0));
				entity = new ICETable();
				entity.setIceIndex(cursor.getInt(0));
				entity.setIceName(cursor.getString(1));
				entity.setIceSmallIconPath(cursor.getString(2));
				entity.setIceMediumIconPath(cursor.getString(3));
				entity.setIceBigIconPath(cursor.getString(4));
				entity.setIceDate(cursor.getString(5));
				entity.setIceExtentOne(cursor.getString(6));
				vector.add(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return vector;
	}

	public Vector getPkgsByICEIndex(int iceIndex) {
		Vector vector = null;
		Cursor cursor = null;
		PakgeTable entity = null;
		try {
			cursor = db
					.rawQuery(
							"select pakgeIndex,iceIndex,pakgepath,pakgeName,status,itemCount,"
									+ " pakgeType,pakgeGuid,pakgeOwnerTvmID,pakgeID,pakgeGroupID,"
									+ " pakgeSubmitDate,pakgeDownloadDate,pakgeSmallIconPath,pakgeMediumIconPath,pakgeDataType,pakgeExtentOne,pakgeExtentTwo "
									+ " from PakgeTable"
									+ " where iceIndex = '" + iceIndex + "' "
									// + " and status=2"
									+ " order by pakgeIndex;", null);
			while (cursor.moveToNext()) {
				if (vector == null) {
					vector = new Vector();
				}
				entity = new PakgeTable();
				entity.setPakgeIndex(cursor.getInt(0));
				entity.setIceIndex(cursor.getString(1));
				entity.setPakgepath(cursor.getString(2));
				entity.setPakgeName(cursor.getString(3));
				entity.setStatus(cursor.getInt(4));
				entity.setItemCount(cursor.getString(5));
				entity.setPakgeType(cursor.getString(6));
				entity.setPakgeGuid(cursor.getString(7));
				entity.setPakgeOwnerTvmID(cursor.getString(8));
				entity.setPakgeID(cursor.getString(9));
				entity.setPakgeGroupID(cursor.getString(10));
				entity.setPakgeSubmitDate(cursor.getString(11));
				entity.setPakgeDownloadDate(cursor.getString(12));
				entity.setPakgeSmallIconPath(cursor.getString(13));
				entity.setPakgeMediumIconPath(cursor.getString(14));
				entity.setPakgeDataType(cursor.getString(15));
				entity.setPakgeExtentOne(cursor.getString(16));
				entity.setPakgeExtentTwo(cursor.getString(17));
				if (entity.getStatus() != 2) {
					entity.setProgress(0);
				}

				vector.add(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return vector;
	}

	public Vector getItemsByPakgeIndex(int pakgeIndex, String itemType) {
		Vector vector = null;
		Cursor cursor = null;
		ItemTable entity = null;
		try {
			vector = new Vector();
			cursor = db
					.rawQuery(
							"select itemIndex,pakgeIndex,itemTitle,itemType,itemGuid,itemTag,itemHeight,itemWidth,itemFileName,itemIconPath,itemFilePath,itemDescription,itemOwnerTvmID,itemGroupID "
									+ " from ItemTable"
									+ " where pakgeIndex = '"
									+ pakgeIndex
									+ "'"
									+ "and (itemType='IMAGE' or itemType='VIDEO') "
									+ " order by itemIndex;", null);
			while (cursor.moveToNext()) {
				Log.d("DBConnection", "future data:" + cursor.getString(0));
				entity = new ItemTable();
				entity.setItemIndex(cursor.getInt(0));
				entity.setPakgeIndex(cursor.getString(1));
				entity.setItemTitle(cursor.getString(2));
				entity.setItemType(cursor.getString(3));
				entity.setItemGuid(cursor.getString(4));
				entity.setItemTag(cursor.getString(5));
				entity.setItemHeight(cursor.getString(6));
				entity.setItemWidth(cursor.getString(7));
				entity.setItemFileName(cursor.getString(8));
				entity.setItemIconPath(cursor.getString(9));
				entity.setItemFilePath(cursor.getString(10));
				entity.setItemDescription(cursor.getString(11));
				entity.setItemOwnerTvmID(cursor.getString(12));
				entity.setItemGroupID(cursor.getString(13));
				vector.add(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return vector;
	}

	public void removeDownloadedPakgeTempRecord(long pkgIndex) {
		db.delete("DownloadTempTable", "downloadIndex=" + pkgIndex, null);
	}

	public Vector getItemsByPakgeIndex(int pakgeIndex) {
		Vector vector = null;
		Cursor cursor = null;
		ItemTable entity = null;
		try {
			vector = new Vector();
			cursor = db
					.rawQuery(
							"select itemIndex,pakgeIndex,itemTitle,itemType,itemGuid,itemTag,itemHeight,itemWidth,itemFileName,itemIconPath,itemFilePath,itemDescription,itemOwnerTvmID,itemGroupID "
									+ " from ItemTable"
									+ " where pakgeIndex = '"
									+ pakgeIndex
									+ "'" + " order by itemIndex;", null);
			while (cursor.moveToNext()) {
				Log.d("DBConnection", "future data:" + cursor.getString(0));
				entity = new ItemTable();
				entity.setItemIndex(cursor.getInt(0));
				entity.setPakgeIndex(cursor.getString(1));
				entity.setItemTitle(cursor.getString(2));
				entity.setItemType(cursor.getString(3));
				entity.setItemGuid(cursor.getString(4));
				entity.setItemTag(cursor.getString(5));
				entity.setItemHeight(cursor.getString(6));
				entity.setItemWidth(cursor.getString(7));
				entity.setItemFileName(cursor.getString(8));
				entity.setItemIconPath(cursor.getString(9));
				entity.setItemFilePath(cursor.getString(10));
				entity.setItemDescription(cursor.getString(11));
				entity.setItemOwnerTvmID(cursor.getString(12));
				entity.setItemGroupID(cursor.getString(13));
				vector.add(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return vector;
	}

	public DownloadTempTable queryDownloadTempByStatus(long downloadIndex,
			int status) {
		Cursor cursor = null;
		DownloadTempTable tempTable = null;
		try {
			cursor = db.rawQuery("select *" + " from DownloadTempTable"
					+ " where downloadIndex = " + downloadIndex
					+ " and downloadStatus='" + status + "'; ", null);
			if (cursor.moveToNext()) {
				tempTable = new DownloadTempTable();
				tempTable.setTempIndex(cursor.getInt(cursor
						.getColumnIndex("TempIndex")));
				tempTable.setDownloadedItemSize(cursor.getString(cursor
						.getColumnIndex("DownloadedSize")));
				tempTable.setDownloadedItemWhich(cursor.getInt(cursor
						.getColumnIndex("DownloadedItemWhich")));
				tempTable.setDownloadedSize(cursor.getString(cursor
						.getColumnIndex("DownloadedSize")));
				tempTable.setDownloadIndex(cursor.getInt(cursor
						.getColumnIndex("DownloadIndex")));
				tempTable.setDownloadItemGuid(cursor.getString(cursor
						.getColumnIndex("DownloadItemGuid")));
				tempTable.setDownloadItemTotalSize(cursor.getString(cursor
						.getColumnIndex("DownloadItemTotalSize")));
				tempTable.setDownloadStatus(cursor.getInt(cursor
						.getColumnIndex("DownloadStatus")));
				tempTable.setDownloadTotalSize(cursor.getString(cursor
						.getColumnIndex("DownloadTotalSize")));

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return tempTable;
	}

	public void updateDownloadTemp(int tempIndex, String downloadedSize,
			String downloadedItemSize, String downloadItemTotalSize,
			int downloadStatus, long downloadTotalSize) {
		try {
			ContentValues contentvalues = new ContentValues();
			contentvalues.put("DownloadedSize", downloadedSize);
			contentvalues.put("DownloadTotalSize", downloadTotalSize);
			contentvalues.put("DownloadedItemSize", downloadedItemSize);
			contentvalues.put("DownloadItemTotalSize", downloadItemTotalSize);
			contentvalues.put("DownloadStatus", downloadStatus);

			String[] as = new String[1];
			as[0] = String.valueOf(tempIndex);
			db.update("DownloadTempTable", contentvalues, "TempIndex = ?", as);
		} catch (SQLiteConstraintException e) {
		}
	}

	public void updateDownloadTemp(int DownloadedItemWhich, int tempIndex,
			String downloadedSize, String downloadedItemSize,
			String downloadItemTotalSize, int downloadStatus,
			long downloadTotalSize) {
		try {
			ContentValues contentvalues = new ContentValues();
			contentvalues.put("DownloadedSize", downloadedSize);
			contentvalues.put("DownloadTotalSize", downloadTotalSize);
			contentvalues.put("DownloadedItemSize", downloadedItemSize);
			contentvalues.put("DownloadItemTotalSize", downloadItemTotalSize);
			contentvalues.put("DownloadStatus", downloadStatus);
			contentvalues.put("DownloadedItemWhich", DownloadedItemWhich);

			String[] as = new String[1];
			as[0] = String.valueOf(tempIndex);
			db.update("DownloadTempTable", contentvalues, "TempIndex = ?", as);
		} catch (SQLiteConstraintException e) {
		}
	}

	public DownloadTempTable queryDownloadTempByGuid(long downloadIndex,
			String itemGuid) {
		Cursor cursor = null;
		DownloadTempTable tempTable = null;
		try {
			cursor = db.rawQuery("select *" + " from DownloadTempTable"
					+ " where DownloadIndex = " + downloadIndex
					+ " and DownloadItemGuid='" + itemGuid + "'; ", null);
			if (cursor != null && cursor.moveToNext()) {
				tempTable = new DownloadTempTable();
				tempTable.setTempIndex(cursor.getInt(cursor
						.getColumnIndex("TempIndex")));
				tempTable.setDownloadedItemSize(cursor.getString(cursor
						.getColumnIndex("DownloadedSize")));
				tempTable.setDownloadedItemWhich(cursor.getInt(cursor
						.getColumnIndex("DownloadedItemWhich")));
				tempTable.setDownloadedSize(cursor.getString(cursor
						.getColumnIndex("DownloadedSize")));
				tempTable.setDownloadIndex(cursor.getInt(cursor
						.getColumnIndex("DownloadIndex")));
				tempTable.setDownloadItemGuid(cursor.getString(cursor
						.getColumnIndex("DownloadItemGuid")));
				tempTable.setDownloadItemTotalSize(cursor.getString(cursor
						.getColumnIndex("DownloadItemTotalSize")));
				tempTable.setDownloadStatus(cursor.getInt(cursor
						.getColumnIndex("DownloadStatus")));
				tempTable.setDownloadTotalSize(cursor.getString(cursor
						.getColumnIndex("DownloadTotalSize")));

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return tempTable;
	}

	public Map getItemsMapByPakgeIndex(int pakgeIndex) {
		Cursor cursor = null;
		ItemTable entity = null;
		Map map = new HashMap();
		try {
			cursor = db
					.rawQuery(
							"select itemIndex,pakgeIndex,itemTitle,itemType,itemGuid,itemTag,itemHeight,itemWidth,itemFileName,itemIconPath,itemFilePath,itemDescription,itemOwnerTvmID,itemGroupID "
									+ " from ItemTable"
									+ " where pakgeIndex = '"
									+ pakgeIndex
									+ "'" + " order by itemIndex;", null);
			while (cursor.moveToNext()) {
				Log.d("DBConnection", "future data:" + cursor.getString(0));
				entity = new ItemTable();
				entity.setItemIndex(cursor.getInt(0));
				entity.setPakgeIndex(cursor.getString(1));
				entity.setItemTitle(cursor.getString(2));
				entity.setItemType(cursor.getString(3));
				entity.setItemGuid(cursor.getString(4));
				entity.setItemTag(cursor.getString(5));
				entity.setItemHeight(cursor.getString(6));
				entity.setItemWidth(cursor.getString(7));
				entity.setItemFileName(cursor.getString(8));
				entity.setItemIconPath(cursor.getString(9));
				entity.setItemFilePath(cursor.getString(10));
				entity.setItemDescription(cursor.getString(11));
				entity.setItemOwnerTvmID(cursor.getString(12));
				entity.setItemGroupID(cursor.getString(13));
				map.put(entity.getItemGuid(), entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return map;
	}

	public int isExistICE(String iceName, String iceDate) {
		Cursor cursor = null;
		int iceIndex = -1;
		try {
			iceDate = iceDate.substring(0, iceDate.indexOf(" "));
			String startDate = iceDate + " 00:00:00";
			String endDate = iceDate + " 23:59:59";
			cursor = db.rawQuery("select iceIndex" + " from iceTable"
					+ " where iceName = '" + iceName + "' " + " and iceDate>='"
					+ startDate + "' and iceDate <='" + endDate + "';", null);
			if (cursor != null && cursor.moveToNext()) {
				iceIndex = cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return iceIndex;
	}

	public ICETable queryICE(int iceIndex) {
		Cursor cursor = null;
		ICETable iceTable = null;
		try {
			cursor = db.rawQuery("select *" + " from iceTable"
					+ " where iceIndex = " + iceIndex + "; ", null);
			if (cursor.moveToNext()) {
				iceTable = new ICETable();
				iceTable.setIceIndex(iceIndex);
				iceTable.setIceName(cursor.getString(cursor
						.getColumnIndex("ICEName")));
				iceTable.setIceSmallIconPath(cursor.getString(cursor
						.getColumnIndex("ICESmallIconPath")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return iceTable;
	}

	public PakgeTable queryPkg(int pkgIndex) {
		Cursor cursor = null;
		PakgeTable pkgTable = null;
		try {
			cursor = db.rawQuery("select *" + " from PakgeTable"
					+ " where pakgeIndex = " + pkgIndex + "; ", null);
			if (cursor.moveToNext()) {
				pkgTable = new PakgeTable();
				pkgTable.setPakgeIndex(pkgIndex);
				pkgTable.setPakgepath(cursor.getString(cursor
						.getColumnIndex("Pakgepath")));
				pkgTable.setStatus(cursor.getInt(cursor
						.getColumnIndex("status")));

				pkgTable.setIceIndex(cursor.getString(cursor
						.getColumnIndex("ICEIndex")));
				pkgTable.setItemCount(cursor.getString(cursor
						.getColumnIndex("ItemCount")));
				pkgTable.setPakgeName(cursor.getString(cursor
						.getColumnIndex("PakgeName")));
				pkgTable.setPakgeType(cursor.getString(cursor
						.getColumnIndex("PakgeType")));
				pkgTable.setPakgeGuid(cursor.getString(cursor
						.getColumnIndex("PakgeGuid")));
				pkgTable.setPakgeOwnerTvmID(cursor.getString(cursor
						.getColumnIndex("PakgeOwnerTvmID")));
				pkgTable.setPakgeID(cursor.getString(cursor
						.getColumnIndex("PakgeID")));
				pkgTable.setPakgeGroupID(cursor.getString(cursor
						.getColumnIndex("PakgeGroupID")));
				pkgTable.setPakgeSubmitDate(cursor.getString(cursor
						.getColumnIndex("PakgeSubmitDate")));
				pkgTable.setPakgeSmallIconPath(cursor.getString(cursor
						.getColumnIndex("PakgeSmallIconPath")));
				pkgTable.setPakgeDownloadDate(cursor.getString(cursor
						.getColumnIndex("PakgeDownloadDate")));
				pkgTable.setPakgeMediumIconPath(cursor.getString(cursor
						.getColumnIndex("PakgeMediumIconPath")));

				pkgTable.setPakgeExtentOne(cursor.getString(cursor
						.getColumnIndex("PakgeExtentOne")));
				pkgTable.setPakgeExtentTwo(cursor.getString(cursor
						.getColumnIndex("PakgeExtentTwo")));
				pkgTable.setPakgeExtentThree(cursor.getString(cursor
						.getColumnIndex("PakgeExtentThree")));
				pkgTable.setPakgeExtentFour(cursor.getString(cursor
						.getColumnIndex("PakgeExtentFour")));
				pkgTable.setPakgeExtentFive(cursor.getString(cursor
						.getColumnIndex("PakgeExtentFive")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return pkgTable;
	}

	public PakgeTable queryPkgByGuid(String pakgeGuid) {
		Cursor cursor = null;
		PakgeTable pkgTable = null;
		try {
			cursor = db.rawQuery("select *" + " from PakgeTable"
					+ " where pakgeGuid = '" + pakgeGuid + "'; ", null);
			if (cursor.moveToNext()) {
				pkgTable = new PakgeTable();
				pkgTable.setPakgeIndex(cursor.getInt(cursor
						.getColumnIndex("PakgeIndex")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return pkgTable;
	}

	public VersionInfo queryVersionInfo(String version) {
		Cursor cursor = null;
		VersionInfo versionInfo = null;
		try {
			cursor = db.rawQuery(
					"select addr,describe,isrollback,rule,version,isReminder"
							+ " from VersionInfo" + " where version = '"
							+ version + "'; ", null);
			if (cursor.moveToNext()) {
				versionInfo = new VersionInfo();
				versionInfo.setAddr(cursor.getString(0));
				versionInfo.setDescribe(cursor.getString(1));
				versionInfo.setIsrollback(cursor.getInt(2));
				versionInfo.setRule(cursor.getInt(3));
				versionInfo.setVersion(cursor.getString(4));
				versionInfo.setIsReminder(cursor.getInt(5));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

		}

		return versionInfo;
	}

	public ArrayList<VersionInfo> queryAllVersions() {
		ArrayList<VersionInfo> versionList = null;
		Cursor cursor = null;
		VersionInfo versionInfo = null;
		try {
			cursor = db.rawQuery(
					"select addr,describe,isrollback,rule,version,isReminder"
							+ " from VersionInfo;", null);
			if (cursor.getCount() > 0) {
				versionList = new ArrayList<VersionInfo>();
				while (cursor.moveToNext()) {
					versionInfo = new VersionInfo();
					versionInfo.setAddr(cursor.getString(0));
					versionInfo.setDescribe(cursor.getString(1));
					versionInfo.setIsrollback(cursor.getInt(2));
					versionInfo.setRule(cursor.getInt(3));
					versionInfo.setVersion(cursor.getString(4));
					versionInfo.setIsReminder(cursor.getInt(5));

					versionList.add(versionInfo);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

		}

		return versionList;
	}

	public int isExistPkg(long iceIndex, String iceDate, String pkgName) {
		Cursor cursor = null;
		int pakgeIndex = -1;
		try {
			iceDate = iceDate.substring(0, iceDate.indexOf(" "));
			String startDate = iceDate + " 00:00:00";
			String endDate = iceDate + " 23:59:59";
			cursor = db.rawQuery("select p.pakgeIndex"
					+ " from pakgeTable p,iceTable i" + " where i.iceIndex = "
					+ iceIndex + " and i.iceDate>='" + startDate + "'"
					+ " and i.iceDate<='" + endDate + "'"
					+ " and p.pakgeName='" + pkgName + "'"
					+ " and p.iceIndex=i.iceIndex;", null);
			if (cursor.moveToNext()) {
				pakgeIndex = cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return pakgeIndex;
	}

	public boolean isCreated() {
		return opener.isCreated();
	}

	public void removeIceRecord(PakgeTable pkgTable) {
		db.delete("ICETable", "ICEIndex" + "=" + pkgTable.getIceIndex() + "",
				null);
	}

	public void removePakgeRecord(PakgeTable pkgTable) {
		db.delete("PakgeTable", "PakgeIndex" + "=" + pkgTable.getPakgeIndex()
				+ "", null);
	}

	public void removeUnDownloadedPakgeRecord() {
		db.delete("PakgeTable", "status" + "<>" + 2 + "", null);
	}

	public void removeItemRecords(PakgeTable pkgTable) {
		db.delete("ItemTable", "PakgeIndex" + "=" + pkgTable.getPakgeIndex()
				+ "", null);
	}

	public void setStatus(Long pakgeIndex, int status) {
		try {
			ContentValues contentvalues = new ContentValues();
			contentvalues.put("status", status);

			String[] as = new String[1];
			as[0] = String.valueOf(pakgeIndex);
			db.update("PakgeTable", contentvalues, "PakgeIndex = ?", as);
		} catch (SQLiteConstraintException e) {
		}
	}

	public void setIsReminder(String version, int isReminder) {
		try {
			ContentValues contentvalues = new ContentValues();
			contentvalues.put("isReminder", isReminder);

			String[] as = new String[1];
			as[0] = version;
			db.update("VersionInfo", contentvalues, "version = ?", as);
		} catch (SQLiteConstraintException e) {
		}
	}

	public void setFilePath(String pkgName, String filePath) {
		ContentValues contentvalues = new ContentValues();
		SQLiteDatabase sqlitedatabase;
		String as[];
		if (filePath == null)
			contentvalues.putNull("");
		else
			contentvalues.put("filepath", filePath);
		sqlitedatabase = db;
		as = new String[1];
		as[0] = pkgName;
		sqlitedatabase.update("applications", contentvalues, "package = ?", as);
	}

	public Map queryDownloadTempByTableIndex(long downloadIndex) {
		Cursor cursor = null;
		Map tempTableMap = new HashMap();
		;
		try {
			cursor = db.rawQuery("select *" + " from DownloadTempTable"
					+ " where DownloadIndex = " + downloadIndex + "; ", null);
			while (cursor != null && cursor.moveToNext()) {
				DownloadTempTable tempTable = new DownloadTempTable();
				tempTable.setTempIndex(cursor.getInt(cursor
						.getColumnIndex("TempIndex")));
				tempTable.setDownloadedItemSize(cursor.getString(cursor
						.getColumnIndex("DownloadedSize")));
				tempTable.setDownloadedItemWhich(cursor.getInt(cursor
						.getColumnIndex("DownloadedItemWhich")));
				tempTable.setDownloadedSize(cursor.getString(cursor
						.getColumnIndex("DownloadedSize")));
				tempTable.setDownloadIndex(cursor.getInt(cursor
						.getColumnIndex("DownloadIndex")));
				tempTable.setDownloadItemGuid(cursor.getString(cursor
						.getColumnIndex("DownloadItemGuid")));
				tempTable.setDownloadItemTotalSize(cursor.getString(cursor
						.getColumnIndex("DownloadItemTotalSize")));
				tempTable.setDownloadStatus(cursor.getInt(cursor
						.getColumnIndex("DownloadStatus")));
				tempTable.setDownloadTotalSize(cursor.getString(cursor
						.getColumnIndex("DownloadTotalSize")));

				tempTableMap.put(tempTable.getDownloadItemGuid(), tempTable);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

		}

		return tempTableMap;
	}

	public long insertDownloadTempTable(DownloadTempTable downloadTempTable) {
		ContentValues contentvalues = new ContentValues();

		contentvalues
				.put("DownloadIndex", downloadTempTable.getDownloadIndex());
		contentvalues.put("DownloadedSize",
				downloadTempTable.getDownloadedSize());
		contentvalues.put("DownloadTotalSize",
				downloadTempTable.getDownloadTotalSize());
		contentvalues.put("DownloadedItemWhich",
				downloadTempTable.getDownloadedItemWhich());
		contentvalues.put("DownloadItemGuid",
				downloadTempTable.getDownloadItemGuid());
		contentvalues.put("DownloadedItemSize",
				downloadTempTable.getDownloadedItemSize());
		contentvalues.put("DownloadItemTotalSize",
				downloadTempTable.getDownloadItemTotalSize());
		contentvalues.put("DownloadStatus",
				downloadTempTable.getDownloadStatus());

		return db.insert("DownloadTempTable", null, contentvalues);
	}

	public long insertVersionInfo(VersionInfo versionInfo) {
		ContentValues contentvalues = new ContentValues();

		contentvalues.put("addr", versionInfo.getAddr());
		contentvalues.put("describe", versionInfo.getDescribe());
		contentvalues.put("isrollback", versionInfo.getIsrollback());
		contentvalues.put("rule", versionInfo.getRule());
		contentvalues.put("version", versionInfo.getVersion());
		contentvalues.put("isReminder", versionInfo.getIsReminder());

		return db.insert("VersionInfo", null, contentvalues);
	}

	public long insertICETable(ICETable iceTable) {
		ContentValues contentvalues = new ContentValues();

		contentvalues.put("iceName", iceTable.getIceName());
		contentvalues.put("iceSmallIconPath", iceTable.getIceSmallIconPath());
		contentvalues.put("iceMediumIconPath", iceTable.getIceMediumIconPath());
		contentvalues.put("iceBigIconPath", iceTable.getIceBigIconPath());
		contentvalues.put("iceDate", iceTable.getIceDate());
		contentvalues.put("iceDataType", iceTable.getIceDataType());

		return db.insert("ICETable", null, contentvalues);
	}

	public long insertPakgeTable(PakgeTable pakgeTable) {
		ContentValues contentvalues = new ContentValues();

		contentvalues.put("iceIndex", pakgeTable.getIceIndex());
		contentvalues.put("itemCount", pakgeTable.getItemCount());
		contentvalues.put("pakgeName", pakgeTable.getPakgeName());
		contentvalues.put("pakgeType", pakgeTable.getPakgeType());
		contentvalues.put("pakgeGuid", pakgeTable.getPakgeGuid());
		contentvalues.put("pakgeOwnerTvmID", pakgeTable.getPakgeOwnerTvmID());
		contentvalues.put("pakgeID", pakgeTable.getPakgeID());
		contentvalues.put("pakgeGroupID", pakgeTable.getPakgeGroupID());
		contentvalues.put("pakgeSubmitDate", pakgeTable.getPakgeSubmitDate());
		contentvalues.put("pakgeDownloadDate",
				pakgeTable.getPakgeDownloadDate());
		contentvalues.put("pakgeSmallIconPath",
				pakgeTable.getPakgeSmallIconPath());
		contentvalues.put("pakgeMediumIconPath",
				pakgeTable.getPakgeMediumIconPath());
		contentvalues.put("pakgepath", pakgeTable.getPakgepath());
		contentvalues.put("status", pakgeTable.getStatus());
		contentvalues.put("pakgeExtentTwo", pakgeTable.getPakgeExtentTwo());
		contentvalues.put("pakgeExtentOne", pakgeTable.getPakgeExtentOne());

		return db.insert("PakgeTable", null, contentvalues);
	}

	public long insertItemTable(ItemTable itemTable) {
		ContentValues contentvalues = new ContentValues();

		contentvalues.put("pakgeIndex", itemTable.getPakgeIndex());
		contentvalues.put("itemTitle", itemTable.getItemTitle());
		contentvalues.put("itemType", itemTable.getItemType());
		contentvalues.put("itemGuid", itemTable.getItemGuid());

		contentvalues.put("itemTag", itemTable.getItemTag());
		contentvalues.put("itemHeight", itemTable.getItemHeight());
		contentvalues.put("itemWidth", itemTable.getItemWidth());
		contentvalues.put("itemFileName", itemTable.getItemFileName());
		contentvalues.put("itemIconPath", itemTable.getItemIconPath());
		contentvalues.put("itemFilePath", itemTable.getItemFilePath());
		contentvalues.put("itemDescription", itemTable.getItemDescription());
		contentvalues.put("itemOwnerTvmID", itemTable.getItemOwnerTvmID());
		contentvalues.put("itemGroupID", itemTable.getItemGroupID());
		contentvalues.put("itemDataType", itemTable.getItemDataType());

		return db.insert("ItemTable", null, contentvalues);
	}

	private static class DatabaseOpener extends SQLiteOpenHelper {
		private static final String DB_NAME = "kuLauncher";
		private static final int DB_VERSION = 4;
		private boolean created = false;

		public DatabaseOpener(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		public boolean isCreated() {
			return created;
		}

		public void onCreate(SQLiteDatabase sqlDB) {
			created = true;
			try {// 中控的信息
				sqlDB.execSQL("create table ICETable (ICEIndex integer primary key autoincrement, ICEName text, ICESmallIconPath text, ICEMediumIconPath text, ICEBigIconPath text, ICEDate text, ICEExtentOne text, ICEExtentTwo text, ICEExtentThree text, ICEExtentFour text, ICEExtentFive text,ICEDataType text)");
			} catch (SQLiteException e) {

			}
			try {// 包的信息
				sqlDB.execSQL("create table PakgeTable (PakgeIndex integer primary key autoincrement, ICEIndex integer, ItemCount integer, PakgeName text, PakgeType text, PakgeGuid text, PakgeOwnerTvmID text, PakgeID text, PakgeGroupID text, PakgeSubmitDate date, PakgeDownloadDate text, PakgeSmallIconPath text, PakgeMediumIconPath text, Pakgepath text, PakgeExtentOne text, PakgeExtentTwo text, PakgeExtentThree text, PakgeExtentFour text, PakgeExtentFive text,status int,PakgeDataType text)");
			} catch (SQLiteException e) {

			}
			try {// 包里每个元素的信息
				sqlDB.execSQL("create table ItemTable (ItemIndex integer primary key autoincrement, PakgeIndex interger, ItemTitle text, ItemType text, ItemGuid text, ItemTag integer, ItemHeight integer, ItemWidth integer, ItemFileName text, ItemIconPath text, ItemFilePath text, ItemDescription text, ItemOwnerTvmID text, ItemGroupID text, ItemExtentOne text, ItemExtentTwo text, ItemExtentThree text, ItemExtentFour text, ItemExtentFive text,ItemDataType text)");
			} catch (SQLiteException e) {

			}
			try {// 版本信息
				sqlDB.execSQL("create table VersionInfo (version text primary key, addr text, describe text, isrollback integer, rule integer, isReminder integer)");
			} catch (SQLiteException e) {

			}
			try {// 下载的临时表格,保存失败的文件
				sqlDB.execSQL("create table DownloadTempTable (TempIndex integer primary key autoincrement, DownloadIndex integer, DownloadedSize text, DownloadTotalSize text, DownloadedItemWhich integer, DownloadItemGuid text,DownloadedItemSize text,DownloadItemTotalSize text,DownloadStatus integer)");
			} catch (SQLiteException e) {

			}
		}

		public void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j) {
			switch (i) {
			default:
				/*
				 * sqlitedatabase.execSQL("drop table if exists ICETable");
				 * sqlitedatabase.execSQL("drop table if exists PakgeTable");
				 * sqlitedatabase.execSQL("drop table if exists ItemTable");
				 * sqlitedatabase.execSQL("drop table if exists VersionInfo");
				 * sqlitedatabase
				 * .execSQL("drop table if exists DownloadTempTable");
				 */
				onCreate(sqlitedatabase);
				break;
			}
		}
	}
}
