package com.tvmining.sdk.entity;

import java.io.Serializable;
import java.util.ArrayList;



public class MyNeighboursEntity implements Serializable{
	 public NeighbourEntity[] canSendArray;

     public NeighbourEntity[] canReadArray;

     /// <summary>
     /// 开始分析
     /// </summary>
     /// <param name="rawStr">从中控得到的 xx</param>
     public MyNeighboursEntity(String rawStr) 
     {
         if (rawStr.length() < 5) 
         {
             canSendArray = new NeighbourEntity[0];
             canReadArray = new NeighbourEntity[0];
             
             return;
         }

         ArrayList<NeighbourEntity> sendList ;//= new List<NeighbourEntity>();
         ArrayList<NeighbourEntity> recvList ;//= new List<NeighbourEntity>();

         
         String[] twoPiece = rawStr.split(";", 2);
         if(twoPiece.length != 2){
             return;
         }

         String[] recvPiece = twoPiece[1].split(",");
         String[] sendPiece = twoPiece[0].split(",");

         sendList = parsePiece(sendPiece);
         recvList = parsePiece(recvPiece);

         canSendArray = new NeighbourEntity[sendList.size()];
         canReadArray = new NeighbourEntity[recvList.size()];
         
         sendList.toArray(canSendArray);
         recvList.toArray(canReadArray);

         //"214:green:1;214:green:1"
     }

     

     private ArrayList<NeighbourEntity> parsePiece(String[] piece) {
    	 ArrayList<NeighbourEntity> neigList = new ArrayList<NeighbourEntity>();
        
         if (piece.length == 0) {
             return neigList;
         }

         for (int i = 0; i < piece.length; i++) {
             String[] oneChunk = piece[i].split(":");

             if (oneChunk.length != 3) {
                 continue;
             }

             if (oneChunk[0].equals(UserInfoEntity.iceId)) {
                 continue;
             }

             NeighbourEntity newNeigh = new NeighbourEntity();
             newNeigh.iceId = oneChunk[0];
             newNeigh.tvmId = oneChunk[1];
             
             if (Integer.parseInt(oneChunk[2]) > Integer.parseInt(GroupTypeEntity.ADMINISTRATOR))
             {
                 newNeigh.type = UserTypeEntity.DRIVCE;
             }
             else
             {
                 newNeigh.type = UserTypeEntity.USER;
             }

             if (newNeigh.type.equals(UserTypeEntity.DRIVCE))
             {
                 newNeigh.groupId = GroupTypeEntity.ADMINISTRATOR;
             }
             else {
                 newNeigh.groupId = oneChunk[2];
             }

             newNeigh.CmdObjToMy = "dev-" + newNeigh.iceId;
             neigList.add(newNeigh);
         }

         return neigList;
     } 
}
