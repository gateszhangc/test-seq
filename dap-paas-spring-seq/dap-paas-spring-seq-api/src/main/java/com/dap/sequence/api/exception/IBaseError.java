package com.dap.sequence.api.exception;

/**
 * @className IBaseError
 * @description 错误接口
 * @author renle
 * @date 2023/11/24 17:41:49 
 * @version: V23.06
 */
public interface IBaseError {

    /**
     *  获取错误码
     *
     * @return String
     */
    String getResultCode();

    /**
     * 获取错误信息
     *
     * @return String
     */
    String getResultMsg();

}
