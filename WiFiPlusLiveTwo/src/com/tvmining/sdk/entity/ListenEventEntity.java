package com.tvmining.sdk.entity;

public interface ListenEventEntity 
{
	/**
	 * 事件发生
	 * @param log
	 */
	public void raiseHttpUploadingEvent(String filename, long filesize,long uploadingByte, long batchUploadBytes, long totalFileSize);
	
	/**
	 * 重试失败
	 */
	public void raiseConnectionRetryFailEvent();
	
	/**
	 * 将要重试
	 */
	public void raiseConnectionWillRetryEvent();
	
	/**
	 * 成功重试
	 */
	public void raiseConnectionRetrySuccessEvent();
	
	/**
	 * 更新服务列表
	 */
	public void raiseSearchingUpdateWithServiceList(ICELoginEntity[] iceArray);
	
	/**
	 * 连接失败的回调
	 */
	public void raiseConnectionFailedEvent(ICELoginEntity LoginICE, UserInfoEntity userEntity, ConnectFailedStatus failedStatus);
}
