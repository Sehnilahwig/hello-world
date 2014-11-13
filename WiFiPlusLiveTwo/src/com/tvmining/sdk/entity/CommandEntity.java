package com.tvmining.sdk.entity;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

import com.tvmining.sdk.helper.HttpHelper;


public class CommandEntity {
	public String CmdTYPE;
	public String MSGID;
	public String PREMSGID;
	public String FROM;
	public String OBJ;
	public String RES;
	public String ClientVaildCode;
	public Dictionary<String,String> CMD_HEAD_DICT;

	public String body;

	public CommandEntity() {
		// TODO Auto-generated constructor stub
		CmdTYPE = CommandTypeEntity.NONE;
		MSGID = "";
		PREMSGID = "";
		FROM = "";
		OBJ = "";
		RES= "";
		ClientVaildCode = "";
		CMD_HEAD_DICT = new Hashtable<String,String>();
		body = "";
	}


	/**
	 * @brief 从一个命令转为原始指令
	 * @return 命令的 socket 字符
	 */
	public String toRaw(int iceid){
		String bodyEncode=null;
		if(CmdTYPE.equalsIgnoreCase(CommandTypeEntity.REGISTER)){
			bodyEncode = body;
		}else{
			try{
				bodyEncode = HttpHelper.UrlEncode(body); 
			}catch(Exception ex){}
		}
		
		String rawStr = String.format("CMD:%s\r\nMSGID:00000000\r\nPREMSGID:00000000\r\nFROM:%s\r\nOBJ:%s\r\nRES:%s\r\nClientVaildcode:%s\r\n\r\n%s\r\n\r\n", 
					CmdTYPE, String.valueOf(iceid), OBJ, RES, UUID.randomUUID().toString(), bodyEncode
				);
		
		return rawStr;
	}
	
	/**
	 * @brief 从一个命令转为原始指令
	 * @return 命令的 socket 字符
	 */
	public String toRaw(){
		String bodyEncode=null;
		if(CmdTYPE.equalsIgnoreCase(CommandTypeEntity.REGISTER)){
			bodyEncode = body;
		}else{
			try{
				bodyEncode = HttpHelper.UrlEncode(body); 
			}catch(Exception ex){}
		}
		
		String fromString ;
		if(FROM.length() == 0){
			fromString = UserInfoEntity.iceId;
		}else{
			fromString = FROM;
		}
		
		String rawStr = String.format("CMD:%s\r\nMSGID:00000000\r\nPREMSGID:00000000\r\nFROM:%s\r\nOBJ:%s\r\nRES:%s\r\nClientVaildcode:%s\r\n\r\n%s\r\n\r\n", 
					CmdTYPE, fromString, OBJ, RES, UUID.randomUUID().toString(), bodyEncode
				);
		
		return rawStr;
	}
}
