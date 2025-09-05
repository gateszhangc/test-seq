package com.dap.sequence.server.utils;

public enum ErrorResultMsg {
    COMMON_SUCCESS(200, "请求成功"),
    COMMON_ERROR(201, "请求失败");

    private Integer code;
    private String msg;

    private ErrorResultMsg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
