package com.tvmining.sdk.entity;

public class UploadUserInfoEmptyStatusEntity {
    /// 返回成功
    public static int SUCC = 0;

    /// 内部分析出错
    public static int FAILED_INNER_CODE = -10;

    /// 返回的代码
    public int code;

    /// 返回的信息
    public Object[] msg;
}
