package com.tvmining.sdk.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class NeighbourEntity implements Serializable{
	/// <summary>
    /// 中控号
    /// </summary>
    public String iceId;

    /// <summary>
    /// 天脉号
    /// </summary>
    public String tvmId;

    /// <summary>
    /// 组号
    /// </summary>
    public String groupId;

    /// <summary>
    /// 设备类型
    /// </summary>
    public String type;

    /// <summary>
    /// 给我发
    /// </summary>
    public String CmdObjToMy;
    public ArrayList<String> applist ;
}
