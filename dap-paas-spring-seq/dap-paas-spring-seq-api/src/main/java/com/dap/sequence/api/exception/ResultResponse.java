package com.dap.sequence.api.exception;

/**
 * @className ResultResponse
 * @description 响应结果
 * @author renle
 * @date 2023/11/24 17:48:19 
 * @version: V23.06
 */
public class ResultResponse {

    /**
     * 响应代码
     */
    private String code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应结果
     */
    private Object data;

    public ResultResponse() {
    }

    public ResultResponse(IBaseError error) {
        this.code = error.getResultCode();
        this.msg = error.getResultMsg();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 成功
     *
     * @return ResultResponse
     */
    public static ResultResponse success() {
        return success(null);
    }

    /**
     * 成功
     *
     * @param data data
     * @return ResultResponse
     */
    public static ResultResponse success(Object data) {
        ResultResponse response = new ResultResponse();
        response.setCode(ExceptionEnum.SUCCESS.getResultCode());
        response.setMsg(ExceptionEnum.SUCCESS.getResultMsg());
        response.setData(data);
        return response;
    }

    /**
     * 失败
     *
     * @param error error
     * @return ResultResponse
     */
    public static ResultResponse error(IBaseError error) {
        ResultResponse response = new ResultResponse();
        response.setCode(error.getResultCode());
        response.setMsg(error.getResultMsg());
        response.setData(null);
        return response;
    }

    /**
     * 失败
     *
     * @param code code
     * @param message message
     * @return ResultResponse
     */
    public static ResultResponse error(String code, String message) {
        ResultResponse response = new ResultResponse();
        response.setCode(code);
        response.setMsg(message);
        response.setData(null);
        return response;
    }

    /**
     * 失败
     *
     * @param message message
     * @return ResultResponse
     */
    public static ResultResponse error(String message) {
        ResultResponse response = new ResultResponse();
        response.setCode("-1");
        response.setMsg(message);
        response.setData(null);
        return response;
    }

    @Override
    public String toString() {
        return "ResultResponse{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
