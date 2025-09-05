package com.dap.sequence.api.commons;


/**
 * @className Result
 * @description 结果泛型类
 * @date 2023/11/23 09:58:44
 * @version: V23.06
 */
public class Result<T> {
    /**
     * 响应码
     */
    private Integer code;

    /**
     * 异常信息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 总条数
     */
    private Integer total;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
