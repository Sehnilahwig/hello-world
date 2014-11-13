package com.tvmining.sdk.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;

import com.tvmining.sdk.entity.CommandEntity;
import com.tvmining.sdk.entity.CommandTypeEntity;
import com.tvmining.sdk.entity.StatusEntity;


public class ICECommandParseService {
	private final String RESPONSE_TAIL_STRING = "\r\n\r\n";
    private final String RESPONSE_START_STRING = "CMD:";

    private final String JSON_TAIL_STRING = "}";
    private final String JSON_START_STRING = "{";

    private Lock mutexRawFromServer;
    private Lock mutexCommandArray;
    private Lock mutexStatusArray;

    private Object condCommandArray;
    private Object condStatusArray;
    
    public String rawFromServer;

    public List<CommandEntity> commandArray;
    public List<StatusEntity> statusArray;

    //private ICECommandParseDao dao;

	public ICECommandParseService() {
		// TODO Auto-generated constructor stub
		
		rawFromServer = "";
		commandArray = new ArrayList<CommandEntity>();
		statusArray = new ArrayList<StatusEntity>();
		
		mutexRawFromServer = new ReentrantLock();
		
		mutexCommandArray = new ReentrantLock();
		condCommandArray = new Object();
		condStatusArray = new Object();
		mutexStatusArray = new ReentrantLock();
	}
	
	/**
	 * 得到一个命令
	 * @return 命令
	 * @throws Exception
	 */
	public CommandEntity getOneCmd(Thread periodThread) throws Exception{
		try {
			CommandEntity oneCmdEntity = new CommandEntity();
			oneCmdEntity.CmdTYPE = CommandTypeEntity.ICERESET;
			
			while (!periodThread.isInterrupted()) {
				if (commandArray.size() == 0) {
				}else {
					break;
				}
				
				synchronized(condCommandArray)
				{
					try{
						condCommandArray.wait();
					}catch(InterruptedException ee){
						return oneCmdEntity;
					}
				}
				
				if (commandArray.size() == 0) {
				}else {
					break;
				}
			}
			
			
			if(!periodThread.isInterrupted()){
				mutexCommandArray.lock();
				oneCmdEntity = commandArray.get(0);
				commandArray.remove(0);
				mutexCommandArray.unlock();
			}
			
			return oneCmdEntity;
		} catch (Exception e) {
			// TODO: handle exception
			mutexCommandArray.unlock();
			e.printStackTrace();
			Log.d("在命令解析服务", e.getMessage());
			throw new Exception("得到命令锁出错");
		}
	}
	
	/**
	 * 得到一个状态
	 * @return 状态
	 * @throws Exception 
	 */
	public StatusEntity getOneStatus() throws Exception{
		
		try {
			while (true) {
				if (statusArray.size() == 0) {
				}else {
					break;
				}
				
				synchronized(condStatusArray)
				{
					try{
						condStatusArray.wait();
					}catch(InterruptedException ee){
						
					}
				}
				
				if (statusArray.size() == 0) {
				}else {
					break;
				}
				
			}
			
			mutexStatusArray.lock();
			StatusEntity oneStatusEntity = statusArray.get(statusArray.size()-1);
			statusArray.remove(statusArray.size()-1);
			mutexStatusArray.unlock();
			return oneStatusEntity;
		} catch (Exception e) {
			mutexStatusArray.unlock();
			// TODO: handle exception
			throw new Exception("得到状态出错锁");
		}
	}
	
	/**
	 * 根据命令，得到一个状态
	 * @param cmd
	 * @param timeoutSecond
	 * @param allowNone
	 * @return
	 * @throws Exception 
	 */
	public StatusEntity getOneStatusByCMD(CommandEntity cmd, int timeoutSecond, Boolean allowNone) throws Exception{
		Boolean isForever = (timeoutSecond == 0) ? true : false;
		if(!isForever){
			//timeoutSecond *= 10;
		}
		
		try {
			while (true) {
				if (statusArray.size() == 0) 
				{}else{
					break;
				}
				
				synchronized(condStatusArray)
				{
					try{
						if(timeoutSecond == 0){
							if(isForever){
								condStatusArray.wait();
							}else{
								break;
							}
						}else{
							condStatusArray.wait(timeoutSecond*1000);
						}
					}catch(InterruptedException ee){
						
					}
				}
				
				
				if (statusArray.size() == 0) 
				{
					if (!isForever &&
						timeoutSecond <= 0
					) {
						StatusEntity unparseEntity = new StatusEntity();
						unparseEntity.cmd = CommandTypeEntity.NONE;
						unparseEntity.status = StatusEntity.UNPARSE;
						
						return unparseEntity;
					}
				
				
					//Thread.sleep(1000);
					if (timeoutSecond > 0) {
						timeoutSecond--;
					}
				}else {
					break;
				}
			}
			
			mutexStatusArray.lock();
			StatusEntity retStatusEntity = new StatusEntity();
			retStatusEntity.cmd = CommandTypeEntity.NONE;
			retStatusEntity.status = StatusEntity.NULL;
			
			for (int i = 0; i < statusArray.size(); i++) {
				StatusEntity oneStatusEntity = statusArray.get(i);
				if (oneStatusEntity.cmd == null ||
					oneStatusEntity.msg == null
				){
					//statusArray.remove(i);
					continue;
				}
				
				if (oneStatusEntity.cmd.equalsIgnoreCase(cmd.CmdTYPE)) {
					retStatusEntity = oneStatusEntity;
					
					statusArray.remove(i);
					break;
				}
			}
			
			if (retStatusEntity.status == StatusEntity.NULL &&
				allowNone
			) {
				for (int i = 0; i < statusArray.size(); i++) {
					StatusEntity oneStatusEntity= statusArray.get(i);
					if (oneStatusEntity.cmd.equalsIgnoreCase(CommandTypeEntity.NONE)) {
						retStatusEntity =  oneStatusEntity;
						statusArray.remove(i);
						break;
					}
				}
			}
			
			mutexStatusArray.unlock();
			return retStatusEntity;
		} catch (Exception e) {
			// TODO: handle exception
			mutexStatusArray.unlock();
			e.printStackTrace();
			throw new Exception("得到状态出错锁与关键字");
		}
	}

	
	/**
	 * 
	 * @param onePieceRawStr
	 * @throws Exception 
	 */
	 public void appendRawStr(String onePieceRawStr) throws Exception {
         try
         {
             mutexRawFromServer.lock();
         }
         catch (Exception e) {
             Log.d("线程退出:" , e.getMessage());
             throw new Exception(e.getMessage());
         }

         rawFromServer += onePieceRawStr;

         mutexRawFromServer.unlock();
     }


     /**
      * 从原始的字符里，分析出命令来
      */
	 public void fromRawToArray() {
         if (rawFromServer.length() == 0)
         {
             return;
         }

         List<CommandEntity> commandEntityList = new ArrayList<CommandEntity>();

         int startPos = 0;

         int responseHeadOutPos;
         int responseBodyOutPos;
         
         int jsonOutPos;

         try
         {
             mutexRawFromServer.lock();
         }
         catch (Exception e)
         {
             mutexRawFromServer.unlock();
             return;
         }

         //Log.d("开始的得到命令：", String.valueOf(commandArray.size()));
         //Log.d("开始的得到状态：", String.valueOf(statusArray.size()));
         
         //rawFromServer = "CMD:right\r\nMSGID:m595aaaa\r\nPREMSGID:m594aaaa\r\nFROM:330\r\nOBJ:\r\nRES:\r\nClientVaildcode:f40dd025-6774-4ccf-af3e-8e02153c87c7\r\n\r\n\r\n\r\n{\"cmd\":\"echo\",\"status\":\"0\",\"msg\":[\"OK\"]}";
         while (true)
         {
        	 startPos = 0;
        	 
        	 //if (rawFromServer.length() != "{\"cmd\":\"echo\",\"status\":\"0\",\"msg\":[\"OK\"]}".length() &&
             //        rawFromServer.length() > 0
             //){
             //        Log.d("xx:>", rawFromServer + "<");
             //}

             if (rawFromServer.length() == 0)
             {
                 break;
             }
             
             //startPos = 0;
             int jsonInPos = rawFromServer.indexOf(JSON_START_STRING, startPos);
             int responseInPos = rawFromServer.indexOf(RESPONSE_START_STRING, startPos);

             if (jsonInPos != 0 &&
                 responseInPos != 0
             ){
            	 if(filterRawCmd()){
            		 continue;
            	 }else{
            		 break;
            	 }
            	 
            	 //break;
			 }
             
             if (jsonInPos == -1 ||
                 responseInPos == -1
             ){
                 if(jsonInPos == -1 &&
                    responseInPos == -1    
                 ){
                     break ;
                 }

                 if(responseInPos != -1){
                     if(!parseResponseFromRaw()){
                    	 break;
                     }
                     
                     continue;
                 }

                 if(jsonInPos != -1){
                     if(!parseJsonFromRaw()){
                    	 break;
                     }
                 }

                 continue;
              }

             //如果是行模式，
             if (responseInPos != -1 &&
                 responseInPos < jsonInPos
             ){
                 responseHeadOutPos = rawFromServer.indexOf(RESPONSE_TAIL_STRING, startPos);
                  responseBodyOutPos = rawFromServer.indexOf(RESPONSE_TAIL_STRING, responseHeadOutPos);
             
                 if(responseBodyOutPos == -1){
                     if (responseHeadOutPos != -1) {
                         //oneString = rawFromServer.Substring(responseHeadOutPos);
                         //rawFromServer = oneString;
                         startPos = responseHeadOutPos;
                     }

                     startPos = responseInPos;
                     continue;
                 }

                 
                 if(!parseResponseFromRaw()){
                	 break;
                 }
             }

             //如果是 JSON，
             if (jsonInPos != -1 &&
                 jsonInPos < responseInPos
             ){
                 jsonOutPos = rawFromServer.indexOf(JSON_TAIL_STRING, jsonInPos);
                 if (jsonOutPos == -1) {
                     //oneString = rawFromServer.Replace(JSON_START_STRING, "");
                     startPos = jsonInPos;
                     break;
                 }

                 if(!parseJsonFromRaw()){
                	 break;
                 }
                 startPos = jsonOutPos;
             }
         
         }

         //Log.d("结束的得到命令：", String.valueOf(commandArray.size()));
         //Log.d("结束的得到状态：", String.valueOf(statusArray.size()));
         mutexRawFromServer.unlock();
         return;
     }
	 
     public Boolean filterRawCmd(){
    	 int jsonInPos = rawFromServer.indexOf(JSON_START_STRING, 0);
         int responseInPos = rawFromServer.indexOf(RESPONSE_START_STRING, 0);
         
         if (jsonInPos == -1 &&
             responseInPos == -1
         ) {
        	rawFromServer = "";
		    return false;
		 }
         
         if(jsonInPos == 0 ||
            responseInPos == 0
         ){
        	 return true;
         }
         
         if (jsonInPos == -1) {
			filterRawResponse();
			return true;
		 }
         
         if (responseInPos == -1) {
			filterRawJson();
			return true;
		 }
         
         if (jsonInPos > responseInPos) {
			filterRawResponse();
			return true;
		}
         
        if (responseInPos > jsonInPos) {
			filterRawJson();
			return true;
		}
        
        return true;
     }
	 
     public void filterRawResponse(){
    	 int responseInPos = rawFromServer.indexOf(RESPONSE_START_STRING, 0);
    	 rawFromServer = rawFromServer.substring(responseInPos);
     }
     
     public void filterRawJson(){
    	 int jsonInPos = rawFromServer.indexOf(JSON_START_STRING, 0);
    	 rawFromServer = rawFromServer.substring(jsonInPos);
     }
     
	 /**
	  * 分析 JSON 从原始
	  */
     private Boolean parseJsonFromRaw() {
         int jsonInPos = rawFromServer.indexOf(JSON_START_STRING);
         int jsonOutPos = rawFromServer.indexOf(JSON_TAIL_STRING, jsonInPos);
         
         if(jsonOutPos == -1 ||
            jsonInPos >= jsonOutPos
         ){
        	 return false;
         }
         
         jsonOutPos += JSON_TAIL_STRING.length();

         //try{
        	 String oneString = rawFromServer.substring(jsonInPos, (jsonInPos+jsonOutPos-jsonInPos));
        	 //128  40
         
        	 parseOneJson(oneString);

        	 oneString = "";
        	 if (jsonInPos > 0)
        	 {
        		 oneString = rawFromServer.substring(0, jsonInPos);
        	 }

        	 oneString += rawFromServer.substring(jsonOutPos);

        	 rawFromServer = oneString;
         /*    
     	 }catch(Exception e){
        	 e.printStackTrace();
        	 String ooString = "";
         }
         */
       
         return true;
     }

     /**
      * 分析应答从原始内容
      */
     private Boolean parseResponseFromRaw() {
         int responseInPos = rawFromServer.indexOf(RESPONSE_START_STRING);
         
         int responseHeadOutPos = rawFromServer.indexOf(RESPONSE_TAIL_STRING);
         responseHeadOutPos += RESPONSE_TAIL_STRING.length();

         int responseBodyOutPos = rawFromServer.indexOf(RESPONSE_TAIL_STRING, responseHeadOutPos);
         responseBodyOutPos += RESPONSE_TAIL_STRING.length();
         
         if(responseHeadOutPos == -1 ||
            responseBodyOutPos == 3
         ){
        	//rawFromServer = "";
        	 return false;
         }
         
         String oneString = rawFromServer.substring(responseInPos,responseInPos+responseBodyOutPos);
        
         parseOneResponse(oneString);

         oneString = "";
         if (responseInPos > 0)
         {
             oneString = rawFromServer.substring(0, responseInPos);
         }

         oneString += rawFromServer.substring(responseBodyOutPos);
         rawFromServer = oneString;
         
         return true;
     }

     /**
      * 分析一个答复
      * @param response 反馈
      */
     private void parseOneResponse(String response) {
         response = response.trim();
         String splitBorder = "\r\n";
         String[] responseArray = response.split(splitBorder);

         String oneLineSplitBorder = ":";

         if (responseArray.length < 3) {
             return;
         }

         Boolean justHead = false;
         if (responseArray[responseArray.length - 2].trim().length() != 0) {
             justHead = true;
         }

         CommandEntity oneCmdEntity = new CommandEntity();

         if (justHead)
         {
             oneCmdEntity.body = "";
         }else {
         	try{
             oneCmdEntity.body = java.net.URLDecoder.decode(responseArray[responseArray.length - 1].trim(),"UTF-8");
           }catch(Exception ex){}
         }

         for(String oneResponseLine : responseArray) {
             if (oneResponseLine.trim().length() == 0) {
                 break;
             }

             String[] oneResponseArray = oneResponseLine.split(oneLineSplitBorder, 2);
             if (oneResponseArray.length != 2)
             {
                 continue;
             }

             String propertyKey = oneResponseArray[0].trim();
             String propertyValue = oneResponseArray[1].trim();

             if (propertyKey.equalsIgnoreCase("CMD")) {
                 oneCmdEntity.CmdTYPE = CommandTypeEntity.convertFromString(propertyValue);
             }
             else if(propertyKey.equalsIgnoreCase("MSGID")) {
                 oneCmdEntity.MSGID = propertyValue;
             }
             else if (propertyKey.equalsIgnoreCase("PREMSGID"))
             {
                 oneCmdEntity.PREMSGID = propertyValue;
             }
             else if (propertyKey.equalsIgnoreCase("FROM"))
             {
                 oneCmdEntity.FROM = propertyValue;
             }
             else if (propertyKey.equalsIgnoreCase("OBJ"))
             {
                 oneCmdEntity.OBJ = propertyValue;
             }
             else if (propertyKey.equalsIgnoreCase("RES"))
             {
                 oneCmdEntity.RES = propertyValue;
             }
             else if (propertyKey.equalsIgnoreCase("CLIENTVAILDCODE"))
             {
                 oneCmdEntity.ClientVaildCode = propertyValue;
             }


             oneCmdEntity.CMD_HEAD_DICT.put(propertyKey, propertyValue);
         }


         Boolean isDuplicated = false;
         mutexCommandArray.lock();
         for(CommandEntity oneCmdEInA : commandArray) {
             if (oneCmdEInA.MSGID.equalsIgnoreCase(oneCmdEntity.MSGID) ||
                 oneCmdEInA.ClientVaildCode.equalsIgnoreCase(oneCmdEntity.ClientVaildCode)
             )
             {
                 isDuplicated = true;
             }
         }

         if (!isDuplicated) {
             commandArray.add(oneCmdEntity);
         }
         
         mutexCommandArray.unlock();
         //mutexCommandArray.unlock();
        synchronized(condCommandArray)
		{
			condCommandArray.notify();
		}
         
     }

     /// <summary>
     /// 分析一个 JSON
     /// </summary>
     /// <param name="json">JSON字符串</param>
     
     /**
      * 分析一个 JSON
      * @param json
      */
     public StatusEntity parseStatusEntity(String json) {
    	 StatusEntity se = null;
    	 
    	 try
         {
        	se = new StatusEntity(json);
         }
         catch (Exception e)
         {
             se = new StatusEntity();
             se.cmd = CommandTypeEntity.NONE;
             se.status = StatusEntity.UNPARSE;
         }
         
    	 return se;
     }
     
     /**
      * 分析一个 JSON
      * @param json
      */
     private void parseOneJson(String json) {
    	 mutexStatusArray.lock();
         try
         {
        	StatusEntity se = new StatusEntity(json);
            statusArray.add(se);
         }
         catch (Exception e)
         {
             StatusEntity se = new StatusEntity();
             se.cmd = CommandTypeEntity.NONE;
             se.status = StatusEntity.UNPARSE;

             statusArray.add(se);
         }
         
         mutexStatusArray.unlock();
         synchronized(condStatusArray)
		 {
				condStatusArray.notify();
		 }
     }
}