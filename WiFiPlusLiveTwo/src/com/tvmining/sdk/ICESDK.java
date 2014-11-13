/**
 * 
 */
package com.tvmining.sdk;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;

import com.tvmining.sdk.entity.CommandEntity;
import com.tvmining.sdk.entity.CommandTypeEntity;
import com.tvmining.sdk.entity.ConnectFailedStatus;
import com.tvmining.sdk.entity.DeleteFolderEntity;
import com.tvmining.sdk.entity.DeleteFolderStatusEntity;
import com.tvmining.sdk.entity.FolderMakerEntity;
import com.tvmining.sdk.entity.FolderMakerStatusEntity;
import com.tvmining.sdk.entity.GroupTypeEntity;
import com.tvmining.sdk.entity.ICELoginEntity;
import com.tvmining.sdk.entity.ICELoginMethodEntity;
import com.tvmining.sdk.entity.ListenEventEntity;
import com.tvmining.sdk.entity.MyNeighboursEntity;
import com.tvmining.sdk.entity.PackInfoEntity;
import com.tvmining.sdk.entity.SearchFileDetailStatusEntity;
import com.tvmining.sdk.entity.SearchFileEntity;
import com.tvmining.sdk.entity.SearchPackExtDetailEntity;
import com.tvmining.sdk.entity.SideThumbMethod;
import com.tvmining.sdk.entity.StatusEntity;
import com.tvmining.sdk.entity.UploadFileDetailStatusEntity;
import com.tvmining.sdk.entity.UploadUserInfoEntity;
import com.tvmining.sdk.entity.UploadUserInfoStatusEntity;
import com.tvmining.sdk.entity.UserInfoEntity;
import com.tvmining.sdk.entity.UserTypeEntity;
import com.tvmining.sdk.entity.fileThumbMethod;
import com.tvmining.sdk.entity.uploadFileEntity;
import com.tvmining.sdk.service.ICECommandParseService;
import com.tvmining.sdk.service.ICEConnectorService;
import com.tvmining.sdk.service.ICEFileService;



/**
 * @author hanshu
 *
 */
public class ICESDK{
	 /**
	  * 
	  */
    public static ICESDK world = null;//new Hashtable<ICELoginEntity, ICESDK>(); 
   
   /**
    * ����ͳ����û�
    */
   // private UserInfoEntity[] lastSendUserArray;

    /**
     * ����ͳ�������
     */
   // private CommandEntity[] cmdDeliveredArray;

   /**
    * �����˵� ICE
    */
    private ICEConnectorService oneICS;

    /**
     * һ�������������
     */
    private ICECommandParseService oneICPS;

   /**
    *  һ���ļ��ϴ�����
    */
    private ICEFileService oneIFS;

    /**
     * ����������߳�
     */
    private Thread threadCmdParser;
    private Boolean cmdParserIsRunning;
    /**
     * �����߳�
     */
    private Thread threadSayHello;
    private Boolean sayHelloIsRunning;
    /**
     * ע���û�����Ϣ
     */
    private UserInfoEntity registerUserInfo;

    /**
     * 登录的中控信息
     */
    //    private ICELoginEntity registerICELogin;
    
    /**
     * 我的邻居的缓存
     */
    private MyNeighboursEntity neibhoursCache; 
    
    
	public static Lock mutexStarOver = new ReentrantLock();
    
	/**
	 * 连接重试失败
	 */
	public static List<ListenEventEntity>  connectionRetryFailed = new ArrayList<ListenEventEntity>();
	
	/**
	 * 重试成功
	 */
	public static List<ListenEventEntity>  connectionRetrySuccess = new ArrayList<ListenEventEntity>();
	
	/**
	 * 搜索更新服务列表
	 */
	public static List<ListenEventEntity>  searchingUpdateWithServiceList = new ArrayList<ListenEventEntity>();
	
	/**
	 * 连接将要重试
	 */
	public static List<ListenEventEntity>  connectionWillRetry = new ArrayList<ListenEventEntity>();
	
	/**
	 * 增加 HTTP 上传
	 */
	public static List<ListenEventEntity>  httpUploadingProgress = new ArrayList<ListenEventEntity>();
	
	/**
	 * 连接失败
	 */
	public static List<ListenEventEntity> connectionFailed = new ArrayList<ListenEventEntity>();
	
	public static Boolean isNotifySearchUpdateWithServiceList = false;
	
	/**
	 * http 进度通知
	 */
	public static void notifyHttpUploadingProgressEvent(String filename, long filesize,long uploadingByte, long batchByte, long totalFileSize, String source){
		if(httpUploadingProgress.size() == 0){
			return;
		}
		
		Iterator<ListenEventEntity> oneEnum = httpUploadingProgress.iterator();
		
		ListenEventEntity oneListenEvent;
		while(oneEnum.hasNext()){
			oneListenEvent = oneEnum.next();
			
			if(isSourceCall(oneListenEvent, source)){
				oneListenEvent.raiseHttpUploadingEvent(filename, filesize, uploadingByte, batchByte, totalFileSize);
			}else{
				
			}
		}
	}
	 
	/**
	 * 是否是源调用
	 * @param oneListenEvent
	 */
	public static Boolean isSourceCall(ListenEventEntity oneListenEvent, String source){
		if(source == null){
			return isSourceCall(oneListenEvent);
		}

		Boolean isSour = false;
		String listenClass = oneListenEvent.getClass().getName();
		String longerClass;
		String shortClass;
		
		String  callClassName = source;//.getClass().getName();
			
		if(listenClass.equals(callClassName)){
			isSour = true;
		}else{
			if(listenClass.length() > callClassName.length()){
				shortClass = callClassName;
				longerClass = listenClass;
			}else{
				shortClass = listenClass;
				longerClass = callClassName;
			}
			
			if(longerClass.contains(shortClass)){
				isSour = true;
			}
		}
		
		return isSour;

	}

	/**
	 * 是否是源调用
	 * @param oneListenEvent
	 */
	public static Boolean isSourceCall(ListenEventEntity oneListenEvent){
		Boolean isSour = false; 
		
		StackTraceElement stack[] = new Throwable().getStackTrace();
		if(stack.length <= 7){
			return isSour;
		}
		
		String listenClass = oneListenEvent.getClass().getName();
		String longerClass;
		String shortClass;
		
		for(int i=6;i<stack.length;i++){
			String  callClassName = stack[i].getClassName();
			
			if(listenClass.equals(callClassName)){
				isSour = true;
				break;
			}else{
				if(listenClass.length() > callClassName.length()){
					shortClass = callClassName;
					longerClass = listenClass;
				}else{
					shortClass = listenClass;
					longerClass = callClassName;
				}
				
				if(longerClass.contains(shortClass)){
					isSour = true;
					break;
				}
			}
		}
		
		return isSour;
	}
	
	/**
	 * 通知连接重试
	 */
	public static void notifyConnectionFailedEvent(ICELoginEntity LoginICE, UserInfoEntity userEntity,  ConnectFailedStatus failedStatus){
		if(connectionFailed.size() == 0){
			return;
		}
		
		Iterator<ListenEventEntity> oneEnum = connectionFailed.iterator();
		
		while(oneEnum.hasNext()){
			oneEnum.next().raiseConnectionFailedEvent(LoginICE, userEntity, failedStatus);
		}
	}
	
	
	/**
	 * 通知连接重试
	 */
	public static void notifyConnectionRetryFailedEvent(){
		if(connectionRetryFailed.size() == 0){
			return;
		}
		
		Iterator<ListenEventEntity> oneEnum = connectionRetryFailed.iterator();
		
		while(oneEnum.hasNext()){
			oneEnum.next().raiseConnectionRetryFailEvent();
		}
	}
	
	/**
	 * 通知连接重试
	 */
	public static void notifyConnectionRetrySuccessEvent(){
		if(connectionRetrySuccess.size() == 0)
		{
			return;	
		}
		
		Iterator<ListenEventEntity> oneEnum = connectionRetrySuccess.iterator();
		
		while(oneEnum.hasNext()){
			oneEnum.next().raiseConnectionRetrySuccessEvent();
		}
	}
	
	/**
	 * 通知连接重试
	 */
	public static void notifyConnectionWillRetryEvent(){
		if(connectionWillRetry.size() == 0){
			return;
		}
		
		Iterator<ListenEventEntity> oneEnum = connectionWillRetry.iterator();
		
		while(oneEnum.hasNext()){
			oneEnum.next().raiseConnectionWillRetryEvent();
		}
	}
	/**
	 * 通知连接重试
	 */
	public static void notifySearchingUpdateWithServiceListEvent(ICELoginEntity[] iceArray){
		if(searchingUpdateWithServiceList.size() == 0 ||
		   !isNotifySearchUpdateWithServiceList
		){
			return;
		}
			
		Iterator<ListenEventEntity> oneEnum = searchingUpdateWithServiceList.iterator();
		
		while(oneEnum.hasNext()){
			oneEnum.next().raiseSearchingUpdateWithServiceList(iceArray);
		}
	}
	
	/**
	 * 增加扥路重试失败监听
	 */
	public static void addConnectionFailedListener(ListenEventEntity oneListener)
	{
		connectionFailed.add(oneListener);
	}
	
	
	/**
	 * 增加连接重试失败监听
	 */
	public static void addConnectionRetryFailedListener(ListenEventEntity oneListener)
	{
		connectionRetryFailed.add(oneListener);
	}
	
	/**
	 * 增加连接成功监听
	 */
	public static void addConnectionRetrySuccessListener(ListenEventEntity oneListener)
	{
		connectionRetrySuccess.add(oneListener);
	}
	
	/**
	 * 增加更新搜索到的中控监听
	 */
	public static void addSearchingUpdateWithServiceListListener(ListenEventEntity oneListener)
	{
		searchingUpdateWithServiceList.add(oneListener);
	}
	
	/**
	 * 增加连接将重试的监听
	 */
	public static void addConnectionWillRetryListener(ListenEventEntity oneListener){
		connectionWillRetry.add(oneListener);
	}
	
	/**
	 * 增加 HTTP 上传进程的监听
	 * @param oneListener
	 */
	public static void addHttpUploadingProgressListener(ListenEventEntity oneListener){
		httpUploadingProgress.add(oneListener);
	}
	
	public ICESDK(ICEConnectorService connICS, ICECommandParseService connICPS){
		oneICS = connICS;
		oneICPS = connICPS;
		
		cmdParserIsRunning = true;
		threadCmdParser = new Thread(new recvRawFromICEToCmdParser());
		threadCmdParser.setName("命令接收线程");
		threadCmdParser.start();

		oneIFS = new ICEFileService(oneICS.getConnectHostname());
	}
	
    /**
     * �������� 
     */
    public void doingPeriod() 
    {
    	sayHelloIsRunning = true;
        threadSayHello = new Thread(new periodSayHello());
        threadSayHello.setName("做周期律");
        threadSayHello.start();

    }

    /**
     * 结束
     */
    public void isOver(){
    	/*
    	if (world.containsKey(oneICS.getLoginICE()))
        {
            world.remove(oneICS.getLoginICE());
        }*/
    	if(world != null){
    		world = null;
    	}
        else { 
            Log.d("", "连接错误");
        }
    	
    	Log.d("断开了断", "哈哈");
    	stayAlone();
    }

    /// <summary>
    /// 重来
    /// </summary>
    public void starOver()
    {
    	if(oneICS == null){
    		return;
    	}
        ICELoginEntity registerICELogin = oneICS.getLoginICE();
        Boolean isSucc = false;
        notifyConnectionWillRetryEvent();
        
        for (int i = 0; i < 3; i++) {
			try {
				if (!connectAndRegister(registerICELogin)) {
					Log.d("没找到", "没连接");
				}else{
					Log.d("找到了", "哈哈哈哈");
					notifyConnectionRetrySuccessEvent();
					isSucc = true;
					break;
				}
			} catch (Exception e) {
				try{
					ICESDK.mutexStarOver.unlock();
				}catch(Exception ee){}
				
				
				e.printStackTrace();
				//Log.d("oooxx", "xxaaz");
				Log.d("出了个错", "在 public void starOver() 里");
			}
		
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        if(!isSucc){
        	notifyConnectionRetryFailedEvent();
        }
    }

    /**
     * 重连和注册
     * @param registerICELogin
     * @return
     */
	private Boolean connectAndRegister(ICELoginEntity loginICE){
		try {
			if(!ICESDK.mutexStarOver.tryLock(5, TimeUnit.SECONDS)){
				//ICESDK.mutexStarOver.unlock();
				return false;
			}
		} catch (InterruptedException e2) {
			e2.printStackTrace();
			return false;
		}
		
		//if (!oneICS.isConnected()) {
			oneICS.reConnect();
			
			if (!oneICS.isConnected()) {
				Log.d("没有连接的", "连接没有的");
				ICESDK.mutexStarOver.unlock();
				return false;
			}
			
			try{
				StatusEntity regStatus = registerToICE3(loginICE, registerUserInfo);
				ICESDK.mutexStarOver.unlock();
			
				if (regStatus != null &&
					regStatus.status.equals(StatusEntity.OK)
				){
					return true;
				}else{
					Thread.sleep(500);
					oneICS.disconnect();
					Thread.sleep(500);
					return false;
				}
			}catch(Exception e){
				ICESDK.mutexStarOver.unlock();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return false;
			}
		//}else{
		//	Log.d("已经有了连接？", "练了个接接");
		//}
		
		//  ICESDK.mutexStarOver.unlock();		
		//return true;
	}
	
    
	 /**
	  * �ڲ��̷߳��������� xxoo ����
	  */
    private class recvRawFromICEToCmdParser implements Runnable{
    	public void run(){
    		String rawCntFromSocket;
	        while(cmdParserIsRunning){
	            try
	            {
	                rawCntFromSocket = oneICS.recvSockRawCnt();
	                
	                if (rawCntFromSocket.length() == 0) {
	                    //Log.d("","接收的东西没有空，要退");
	                    Thread.sleep(1000);
	                    continue;
	                }
	
	                //if (rawCntFromSocket.lastIndexOf("{\"cmd\":\"echo\"") == -1) {
	                	Log.d("收到报文", rawCntFromSocket);
					//}
	                
	                oneICPS.appendRawStr(rawCntFromSocket);
	                oneICPS.fromRawToArray();
	            }catch(InterruptedException ee){
	            	Log.d("退出解析线程", "退出");
	            	break;
	            }catch (Exception e)
	            {
	            	e.printStackTrace();
	            	
	                Log.d("报文分析错误","错了歌舞");
	                //starOver();
	                try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                //break;
	            }
	            
	            rawCntFromSocket = "";
	        }
	
	        Log.d("","�˳��������");
	        //threadCmdParser.interrupt();
    	}
    }

    /// <summary>
    /// �����Եĺ��п�����������������
    /// </summary>
    private class periodSayHello implements Runnable{
	    public void run(){
	        CommandEntity echoCmdEntity = new CommandEntity();
	        echoCmdEntity.CmdTYPE = CommandTypeEntity.ECHO;
	        
	        
	        while (sayHelloIsRunning)
	        {
	            try
	            {
	                echoCmdEntity.ClientVaildCode = UUID.randomUUID().toString();
	                if (oneICS.isConnected())
	                {
	                    StatusEntity sendStatus = sendCommmand(echoCmdEntity, 5, true);
	                    if (!sendStatus.status.equalsIgnoreCase(StatusEntity.OK))
	                    {
	                    	sendStatus = sendCommmand(echoCmdEntity, 3, true);
	                    	if (!sendStatus.status.equalsIgnoreCase(StatusEntity.OK)){
	                    		//oneICS.disconnect();
	                    		Log.d("", "心跳重新连接");
	                    		///oneICS.reConnect();
	                    		//Thread.sleep(1000);
	                    		starOver();
	                    		continue;
	                    	}
	                    }
	
	                    
	                    //Log.d("", "心跳好");
	                }
	                else
	                {
	                    Log.d("", "心跳套没有连接，链接啊链接");
	                    /*
	                    oneICS.connect();
	                    oneIFS.setHostname(oneICS.getConnectHostname());
	                	*/
	                    starOver();
	                    continue;
	                }
	
	                Thread.sleep(3000);
	                //Console.WriteLine("����״̬��" + threadSayHello.ThreadState);
	            }catch(InterruptedException ee){
	            	Log.d("退出心跳线程", "退出心跳");
	            	break;
	            }catch(Exception e) {
	                Log.d("周期性的送出心跳出错", "");
	            }
	        }
	        
	        Log.d("心跳", "退出");
	        //threadSayHello.interrupt();
	    }
    }

    /**
     * �ͳ�һ������
     * @param sendCmd �ͳ�������
     * @return ���ص�״̬
     */
    public StatusEntity sendCommmand(CommandEntity sendCmd)
    {
        StatusEntity oneSE = new StatusEntity();
        oneSE.status = StatusEntity.UNDELIVER;
        
        if (!sendCmd.CmdTYPE.equals(CommandTypeEntity.ECHO)) {
        	Log.d("�ͳ����", sendCmd.CmdTYPE);
		}
        
        
        try
        {
            if (cmdParserIsRunning && 
            	oneICS.sendSockRawCnt(sendCmd.toRaw())
            ){
                int i = 0;
                while (i<1)
                {
                	StatusEntity getOneSE = oneICPS.getOneStatusByCMD(sendCmd, 10, true);
                    if (getOneSE != null)
                    {
                        oneSE = getOneSE;
                    }
                    
                    if (oneSE.cmd.equalsIgnoreCase(sendCmd.CmdTYPE) &&
                        !oneSE.status.equalsIgnoreCase(StatusEntity.NULL)
                    ){
                        Log.d("", "");
                        break;
                    }


                    i++;
                    Thread.sleep(500);
                }
            }
        }catch (Exception e) {
        	e.printStackTrace();
            Log.d("送出命令"+sendCmd.CmdTYPE, " 错误:");
            
            //oneICS.reConnect();
        }
        return oneSE;
    }

    /**
     * 广播一个命令
     * @param oneCmd
     * @return 广播状态
     */
    public StatusEntity broadcastOneCmd(CommandEntity oneCmd){
    	String oneRetStr = oneICS.broadcastOneCmd(oneCmd.toRaw());
    	StatusEntity oneSE = oneICPS.parseStatusEntity(oneRetStr);
    	return oneSE;
    }
  
    /**
     * �ͳ�һ������
     * @param sendCmd �ͳ�������
     * @param timeout �ӳٶ�����,0 ����Զ�ȴ�
     * @param allowNone �Ƿ�׼��δ֪��״̬����������
     * @return ���ص�״̬
     */
    public StatusEntity sendCommmand(CommandEntity sendCmd, int timeout, Boolean allowNone)
    {
        StatusEntity oneSE = new StatusEntity();
        oneSE.status = StatusEntity.UNDELIVER;

        if (!sendCmd.CmdTYPE.equals(CommandTypeEntity.ECHO)) {
        	Log.d("改一下吧", sendCmd.CmdTYPE);
		}
        
        int reTryNum = 1;
        try
        {
            if (oneICS.sendSockRawCnt(sendCmd.toRaw()))
            {
                //while (true)
                //{
                    StatusEntity getOneSE = oneICPS.getOneStatusByCMD(sendCmd, timeout, allowNone);
                    if (getOneSE != null)
                    {
                        oneSE = getOneSE;
                    }
                    
                    
                    if (oneSE.cmd != CommandTypeEntity.NONE ||
                       oneSE.status != StatusEntity.NULL
                    ) {
                        //break;
                    }

                    
                    reTryNum--;
                    if (reTryNum <= 0) {
                        //break;
                    }
                //}
            }
        }catch (Exception e)
        {
            Log.d("�ͳ�" + sendCmd.CmdTYPE + "�������:", e.getMessage());
        }
        
        return oneSE;
    }


    /**
     * ����һ������
     * @return
     * @throws Exception 
     */
    public CommandEntity recvCommand() throws Exception
    {
        CommandEntity oneCmdEntity = null;
        while(cmdParserIsRunning){
        	oneCmdEntity = oneICPS.getOneCmd(threadSayHello);
        	if (!oneCmdEntity.FROM.equals(UserInfoEntity.iceId)) {
				break;
			}
        }
        return oneCmdEntity;
    }

    /**
     *  ��ʼ��
     */
    static public void init(){
        ICEConnectorService.init();
    }


    /**
     * ��ʼ��
     */
    public void active()
    {
    }

    /**
     * �õ������пط����б�
     * @return �п��б�
     */
    static public ICELoginEntity[] getAllICENameArray()
    {
    	ICELoginEntity[] allICENameArray = ICEConnectorService.getAllICENameArray();
        
    	if(allICENameArray.length > 0){
    		isNotifySearchUpdateWithServiceList = true;
    	}
    	
        return allICENameArray;
    }

    /**
     * �õ���������������
     * @return
     */
    public static String[] getAllICEServerArray() {
        String[] serverName = ICEConnectorService.getAllICEServerArray();
        

        return serverName;
    }

    /**
     * �ر�����
     */
    public void stayAlone() {
    	try{
    		oneICS.disconnect();
    	}catch(Exception e){}
    	
    	try {
    		threadCmdParser.interrupt();
    		cmdParserIsRunning = false;
    	}catch(Exception e){}
    	
    	try{
    		threadSayHello.interrupt();
    		sayHelloIsRunning = false;
    	}catch(Exception e){}
    	
    	oneICPS = null;
    	oneICS = null;
    	oneIFS = null;
    }

    /**
     * ע�ᵽ ICE
     * @param registerUserEntity
     * @return
     */
    private StatusEntity registerToICE3(ICELoginEntity LoginICE, UserInfoEntity registerUserEntity) {
        //ע��
        CommandEntity regiesterCmd = new CommandEntity();
        regiesterCmd.CmdTYPE = CommandTypeEntity.REGISTER;
        
        if (registerUserEntity.type == UserTypeEntity.USER)
        {
            regiesterCmd.body = LoginICE.loginMethod + "," + registerUserEntity.id + "," + registerUserEntity.type + "," + LoginICE.password;
        }
        else {
            regiesterCmd.body = ICELoginMethodEntity.REGISTRY + "," + registerUserEntity.id + "," + registerUserEntity.type + "," + LoginICE.password;
        }

        StatusEntity regStatus = sendCommmand(regiesterCmd);//, 3, false); //sendCommmand(regiesterCmd);
        if (regStatus.status.equals(StatusEntity.OK)) {
            registerUserInfo = registerUserEntity;
        }else{
        	Log.d("", "");
        }
        
        return regStatus;
    }
    
    /// <summary>
    /// �õ���ǰϵͳʱ��
    /// </summary>
    /// <returns>ϵͳʱ��</returns>
//    public ICETimeStatusEntity getICECurrentTime()
//    {
//        ICETimeStatusEntity itse = oneIFS.getICECurrentTime();
//        return itse;
//    }


    /**
     * �õ����й��������Ϣ
     * @return �õ��İ����Ϣ
     */
    /*
    public String[] getAllPublicPackName() 
    {
        String[] allPubPakInfo = oneIFS.getAllPackName();
        return allPubPakInfo;
    }
	*/
    
    /**
     * 上传本地文件
     * @param postDict �ύ�� POST �����ֵ�
     * @param uploadFiles
     * @return
     * @throws Exception 
     */
    
    public UploadFileDetailStatusEntity[] uploadLocalFile(Hashtable<String, String> postDict, uploadFileEntity[] uploadFiles) throws Exception
    {
        if (uploadFiles.length == 0) {
            throw new Exception("û���ϴ����ļ�");
        }
        
        postDict.put(uploadFileEntity.UPDATE_USER_ID_KEY, registerUserInfo.id);
        UploadFileDetailStatusEntity[] upFEntity = oneIFS.uploadLocalFile(postDict, uploadFiles);

        return upFEntity;
    }
	
	 /// <summary>
        /// �����û���Ϣ
        /// </summary>
        /// <param name="userInfo">�û���Ϣ</param>
        /// <returns></returns>
        public UploadUserInfoStatusEntity uploadUserInfo(UploadUserInfoEntity userInfo)
        {
            userInfo.tvmId = UserInfoEntity.tvmId;
            userInfo.appName = UserInfoEntity.appName;

            UploadUserInfoStatusEntity upFEntity = oneIFS.uploadUserInfo(userInfo);

            return upFEntity;
        }

	
	    /// <summary>
        /// �����ļ�
        /// </summary>
        /// <param name="searchFileCond">��������</param>
        /// <returns>��������ϸ��</returns>
        public SearchFileDetailStatusEntity[] searchFile(SearchFileEntity searchFileCond)
        {
            SearchFileDetailStatusEntity[] detailStatus = oneIFS.searchFile(searchFileCond);
            return detailStatus;
        }

	    /// <summary>
        /// ɾ��һ���ļ���
        /// </summary>
        /// <param name="deleteFolder">删除</param>
        /// <returns>ɾ���״̬</returns>
        public DeleteFolderStatusEntity deleteFolder(DeleteFolderEntity deleteFolder)
        {
            DeleteFolderStatusEntity dfse = oneIFS.deleteFolder(deleteFolder);
            return dfse;
        }

		 /// <summary>
        /// ������
        /// </summary>
        /// <param name="folderMakerCond">�ļ��д���������</param>
        /// <returns></returns>
        public FolderMakerStatusEntity folderMaker(FolderMakerEntity folderMakerCond) {
            FolderMakerStatusEntity makeStatus = oneIFS.folderMaker(folderMakerCond);
            return makeStatus;
        }

        /**
         * 得到连接的服务器 IP
         * @return 连接的服务器 IP
         */
        public String getConnectHostName(){
        	return oneICS.getConnectHostname();
        }
        
        /// <summary>
        /// ���һ���ļ�ϸ�ڣ��õ� URL
        /// </summary>
        /// <param name="oneSFDSE"></param>
        /// <returns></returns>
        public String getUrlByFileDetail(SearchFileDetailStatusEntity oneSFDSE) {
        	String url = String.format("http://%s/%s", oneICS.getConnectHostname() ,oneSFDSE.getFileURI());
            return url;
        }


        public String getThumbURLByFileDetail(SearchFileDetailStatusEntity oneSFDSE, int width, int height) {
        	String url = String.format("http://%s%s", oneICS.getConnectHostname(), oneSFDSE.getThumbURI(width, height));
            return url;
        }

        public String getThumbURLByFileDetail(SearchFileDetailStatusEntity oneSFDSE, int width, int height, fileThumbMethod fileTM) {
        	String url = String.format("http://%s%s", oneICS.getConnectHostname(), oneSFDSE.getThumbURI(width, height, fileTM));
            return url;
        }
        
        /**
         * 单边缩略图
         * @param oneSFDSE 一个搜索实体
         * @param size 缩放的尺寸
         * @param sideThumb 是按照长，还是按照宽
         * @return
         */
        public String getSideThumbURLByFileDetail(SearchFileDetailStatusEntity oneSFDSE, int size, SideThumbMethod sideThumb){
        	String url = String.format("http://%s%s", oneICS.getConnectHostname(), oneSFDSE.getSideThumbURI(size, sideThumb));
        	return url;
        }

        		
        ///<summary>
        ///��һ�� URI �õ�һ�� URL
        ///</summary>
        ///<param name="uri"></param>
        ///<returns>һ�� URL</returns>
        public String getUrlByUri(String uri){
            String url = String.format("http://%s%s", oneICS.getConnectHostname(), uri);
            return url;
        }

		/// <summary>
        /// 得到包原图
        /// </summary>
        /// <param name="pie"></param>
        /// <returns></returns>
        public String getPackThumbByPackInfo(PackInfoEntity pie) {
            String uri = pie.getPackThumb();
            if(uri.length() == 0){
                return "";
            }

            String url = String.format("http://%s%s", oneICS.getConnectHostname(), uri);

            return url;
        }
	
        /**
         * 得到包缩略图，根据宽高
         * @param pie
         * @param width
         * @param heigh
         * @return
         */
        public String getPackThumbByPackInfo(PackInfoEntity pie, int width, int heigh) {
            String uri = pie.getPackThumb(width, heigh);
            if(uri.length() == 0){
                return "";
            }

            String url = String.format("http://%s%s", oneICS.getConnectHostname(), uri);

            return url;
        }
	
//		 ///<summary>
//        ///��һ�� URI �õ�һ�� URL
//        ///</summary>
//        ///<param name="uri"></param>
//        ///<returns>һ�� URL</returns>
//        public string getUrlByUri(string uri){
//            string url = string.Format("http://{0}{1}", oneICS.getConectHostname(), uri);
//            return url;
//        }
//
//	


    /**
     * ���ӵ�һ���п�����ȥ
     * @param connect ���ӵ��п����
     * @param userEntity �û�ʵ��
     * @return
     * @throws Exception 
     */
    public static ICESDK sharedICE(ICELoginEntity LoginICE, UserInfoEntity userEntity) throws Exception
    {
        ICESDK oneSDK=null;
        ICESDK.mutexStarOver.lock();
        
        try{
	        if (ICESDK.world == null) 
	        {
	            ICEConnectorService oneConnService = new ICEConnectorService(LoginICE);
	            ICECommandParseService oneCmdParserService = new ICECommandParseService(); 
	            if (oneConnService.isConnected())
	            {
	                oneSDK = new ICESDK(oneConnService, oneCmdParserService);
	                
	            	
	                //ע��
	                StatusEntity regStatus = oneSDK.registerToICE3(LoginICE, userEntity);
	                if (regStatus != null &&
	                    regStatus.status.equalsIgnoreCase(StatusEntity.OK))
	                {
	                    oneSDK.doingPeriod();
	                    
	                    UserInfoEntity.iceId = regStatus.msg.getString(0);
	                    UserInfoEntity.groupId = regStatus.msg.getString(1);
	                        
	                    UserInfoEntity.tvmId = userEntity.id;
	                    UserInfoEntity.appName = userEntity.name;
	                        
	                    world = oneSDK;//.put(LoginICE, oneSDK);
	                }else{
	                	oneSDK.stayAlone();
	                	//ICESDK.mutexStarOver.unlock();
	                	oneSDK = null;
	                	
	                	if(regStatus.msg != null &&
	                	   regStatus.msg.length() > 0
	                	){
	                		//throw new Exception(regStatus.status);
	                		notifyConnectionFailedEvent(LoginICE, userEntity, ConnectFailedStatus.PASSWORD_REASON);
	                		oneSDK = null;
	                	}
	                }
	            }else{
	            	notifyConnectionFailedEvent(LoginICE, userEntity, ConnectFailedStatus.NETWORK_REASON);
	            	oneSDK = null;
	            }
	
	        }
	        else {
	            oneSDK = ICESDK.world;//ICESDK.world.get(LoginICE);
	        }
        }catch(Exception e){
        	oneSDK = null;
        }
        
        ICESDK.mutexStarOver.unlock();
        return oneSDK;
    }

    /// <summary>
    /// �õ�Ŷ��
    /// </summary>
    /// <returns></returns>
//    public String getConectHostname() {
//        return oneICS.getConectHostname();
//    }
    
    
    /// <summary>
    /// �õ��û�Ȩ��
    /// </summary>
    /// <returns>�û�Ȩ��</returns>
    public String getUserPower(){
        if (registerUserInfo.type.equals(UserTypeEntity.DRIVCE))
        {
            return GroupTypeEntity.ADMINISTRATOR;
        }
        else {
            return UserInfoEntity.groupId;
        }
    }

    /// <summary>
    /// 得到拓展包信息
    /// </summary>
    /// <returns>包的名字</returns>
    public SearchPackExtDetailEntity[] getPackExtInfo(String packname)
    {
    	SearchPackExtDetailEntity packExtInfo[] = oneIFS.getPackExtInfo(packname);
    	return packExtInfo;
    }
    
    /// <summary>
    /// �õ��ҵ��ھ���
    /// </summary>
    /// <returns>�ھ���</returns>
    public MyNeighboursEntity getMyNeighbours() {
        CommandEntity upCommand = new CommandEntity();
        upCommand.CmdTYPE = CommandTypeEntity.GETMYNEIGHBOUR;

        MyNeighboursEntity myNeigh;

        try
        {
            StatusEntity oneStatus = sendCommmand(upCommand, 3, false);
            if (oneStatus.status.equalsIgnoreCase(StatusEntity.OK) &&
               oneStatus.cmd.equalsIgnoreCase(upCommand.CmdTYPE)
            )
            {
                myNeigh = new MyNeighboursEntity((String)oneStatus.msg.getString(0));
                neibhoursCache = myNeigh;
            }
            else
            {
                myNeigh = null;//new MyNeighboursEntity("");
            }
        }
        catch (Exception ex)
        {
            myNeigh = null;//new MyNeighboursEntity("");
            Log.d("分析在线列表出错:", ex.getMessage());
        }

        if(myNeigh == null){
        	if(neibhoursCache != null){
        		myNeigh = neibhoursCache;
        	}else{
        		myNeigh = new MyNeighboursEntity("");
        	}
        }
        return myNeigh;
    }

	public PackInfoEntity[] getAllPublicPackName() {
		PackInfoEntity[] allPubPakInfo = oneIFS.getAllPackName();
        return allPubPakInfo;	
    }

	
}
