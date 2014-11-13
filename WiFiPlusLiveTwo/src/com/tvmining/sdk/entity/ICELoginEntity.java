package com.tvmining.sdk.entity;

import java.io.Serializable;

public class ICELoginEntity implements Serializable{
    /// <summary>
    /// 连接的中控名字
    /// </summary>
    public String connectICEName;
    
    /// <summary>
    /// 连接的方法
    /// </summary>
    public String loginMethod;

    /// <summary>
    /// 连接的密码
    /// </summary>
    public String password;

    public ICELoginEntity(String connIceName, String loginM) {
        connectICEName = connIceName.trim();
        loginMethod = loginM;
        password = "";
    }

    public int hashCode(){
    	return connectICEName.hashCode();
    }
}
