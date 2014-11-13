package com.tvmining.sdk.entity;

public class CommandTypeEntity {
	 public final static String GETONLINELIST = "getonlinelist";
     public final static String GETMYNEIGHBOUR = "getpermissionlist";
     public final static String REGISTER = "register";
     public final static String RESET = "reset";
     public final static String UP  = "up";
     public final static String DOWN = "down";
     public final static String LEFT = "left";
     public final static String RIGHT = "right";
     public final static String FIRST = "first";
     public final static String LAST = "last";
     public final static String GO = "go";
     public final static String FORWARD = "forward";
     public final static String BACKWARD = "backward";
     public final static String FUNCTION1 = "function1";
     public final static String FUNCTION2 = "function2";
     public final static String NONE = "none";
     public final static String ECHO = "echo";
     public final static String NEWFILE = "newfile";
     public final static String NEWPACK = "newpack";
     public final static String NULL = "null";
     public final static String PUSH = "push";
     public final static String PLAY = "play";
     public final static String SYNC = "sync";
     public final static String CANCEL = "cancel";
     public final static String OK = "ok";
     public final static String ZOOMIN = "zoomin";
     public final static String ZOOMOUT = "zoomout";
     public final static String STICKY = "sticky";
     public final static String MARCO = "marco";
     public final static String EXT = "ext";
     public final static String CLEANALL = "cleanall";
     public final static String VALUECHANGE = "valuechange";
     public final static String STARTOVER = "startover";
     public final static String DELPACK = "delpack";
     public final static String DELFILE = "delfile";
     public final static String TOUCH = "touch";
     public final static String ICERESET = "icereset";
     public final static String TALKTO = "talkto";
     public final static String NEARBY = "nearby";
     public final static String LOCK = "lock";
     public final static String FORCE = "force";
     public final static String CACHE = "cache";
     public final static String MENU = "menu";
     public final static String HOME = "home";
     
     public static String convertFromString(String cmdStr)
     { 
         if (cmdStr.toLowerCase().trim().equals("getonline")) { 
             return CommandTypeEntity.GETONLINELIST;
         }else if (cmdStr.toLowerCase().trim().equals("register")) { 
             return CommandTypeEntity.REGISTER;
         }else if (cmdStr.toLowerCase().trim().equals("reset")) { 
             return CommandTypeEntity.RESET;
         }
         else if (cmdStr.toLowerCase().trim().equals("up"))
         { 
             return CommandTypeEntity.UP;
         }
         else if (cmdStr.toLowerCase().trim().equals("down"))
         { 
             return CommandTypeEntity.DOWN;
         }
         else if (cmdStr.toLowerCase().trim().equals("left"))
         { 
             return CommandTypeEntity.LEFT;
         }
         else if (cmdStr.toLowerCase().trim().equals("right"))
         { 
             return CommandTypeEntity.RIGHT;
         }
         else if (cmdStr.toLowerCase().trim().equals("first"))
         { 
             return CommandTypeEntity.FIRST;
         }
         else if (cmdStr.toLowerCase().trim().equals("last"))
         { 
             return CommandTypeEntity.LAST;
         }
         else if (cmdStr.toLowerCase().trim().equals("go"))
         {
             return CommandTypeEntity.GO;
         }
         else if (cmdStr.toLowerCase().trim().equals("forward"))
         { 
             return CommandTypeEntity.FORWARD;
         }
         else if (cmdStr.toLowerCase().trim().equals("backward"))
         { 
             return CommandTypeEntity.BACKWARD;
         }
         else if (cmdStr.toLowerCase().trim().equals("function1"))
         { 
             return CommandTypeEntity.FUNCTION1;
         }
         else if (cmdStr.toLowerCase().trim().equals("function2"))
         { 
             return CommandTypeEntity.FUNCTION2;
         }
         else if (cmdStr.toLowerCase().trim().equals("echo"))
         { 
             return CommandTypeEntity.ECHO;
         }
         else if (cmdStr.toLowerCase().trim().equals("newfile"))
         {
             return CommandTypeEntity.NEWFILE;
         }
         else if (cmdStr.toLowerCase().trim().equals("newpack"))
         {
             return CommandTypeEntity.NEWPACK;
         }
         
         else if (cmdStr.toLowerCase().trim().equals("push"))
         {
             return CommandTypeEntity.PUSH;
         }
         else if (cmdStr.toLowerCase().trim().equals("play"))
         {
             return CommandTypeEntity.PLAY;
         }
         else if (cmdStr.toLowerCase().trim().equals("sync"))
         {
             return CommandTypeEntity.SYNC;
         }
         else if (cmdStr.toLowerCase().trim().equals("cancel"))
         {
             return CommandTypeEntity.CANCEL;
         }else if (cmdStr.toLowerCase().trim().equals("ok"))
         {
             return CommandTypeEntity.OK;
         }else if (cmdStr.toLowerCase().trim().equals("zoomin"))
         {
             return CommandTypeEntity.ZOOMIN;
         }
         else if (cmdStr.toLowerCase().trim().equals("zoomout"))
         {
             return CommandTypeEntity.ZOOMOUT;
         }
         else if (cmdStr.toLowerCase().trim().equals("sticky"))
         {
             return CommandTypeEntity.STICKY;
         }
         else  if (cmdStr.toLowerCase().trim().equals("marco"))
         {
             return CommandTypeEntity.MARCO;
         }
         else  if (cmdStr.toLowerCase().trim().equals("ext"))
         {
             return CommandTypeEntity.EXT;
         }else   if (cmdStr.toLowerCase().trim().equals("getpermissionlist"))
         {
             return CommandTypeEntity.GETMYNEIGHBOUR;
         }else if (cmdStr.toLowerCase().trim().equals("cleanall"))
         {
             return CommandTypeEntity.CLEANALL;
         }else if (cmdStr.toLowerCase().trim().equals("valuechange"))
         {
             return CommandTypeEntity.VALUECHANGE;
         }else if (cmdStr.toLowerCase().trim().equals("startover"))
         {
             return CommandTypeEntity.STARTOVER;
         }else if (cmdStr.toLowerCase().trim().equals("delpack"))
         {
             return CommandTypeEntity.DELPACK;
         }else if (cmdStr.toLowerCase().trim().equals("delfile"))
         {
             return CommandTypeEntity.DELFILE;
         }else if (cmdStr.toLowerCase().trim().equals("touch"))
         {
             return CommandTypeEntity.TOUCH;
         }else if (cmdStr.toLowerCase().trim().equals("icereset"))
         {
             return CommandTypeEntity.ICERESET;
         }else if (cmdStr.toLowerCase().trim().equals("talkto"))
         {
             return CommandTypeEntity.TALKTO;
         }else if (cmdStr.toLowerCase().trim().equals("nearby"))
         {
             return CommandTypeEntity.NEARBY;
         }else if (cmdStr.toLowerCase().trim().equals("lock"))
         {
             return CommandTypeEntity.LOCK;
         }else if (cmdStr.toLowerCase().trim().equals("force"))
         {
             return CommandTypeEntity.FORCE;
         }else if (cmdStr.toLowerCase().trim().equals("menu"))
         {
             return CommandTypeEntity.MENU;
         }else if (cmdStr.toLowerCase().trim().equals("home"))
         {
             return CommandTypeEntity.HOME;
         }else if (cmdStr.toLowerCase().trim().equals("cache"))
         {
             return CommandTypeEntity.CACHE;
         }else {
             return CommandTypeEntity.NONE;
         }

     }
}
