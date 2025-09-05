package com.dap.sequence.api.commons;

/**
 * @className ResultUtils
 * @description 结果工具类
 * @date 2023/11/23 09:58:44
 * @version: V23.06
 */
public class ResultUtils {
    public ResultUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Result success() {
        return success((Object)null);
    }

    public static Result success(Object object) {
        return success(ResultEnum.SUCCESS, object);
    }

    public static Result success(ResultEnum re, Object object) {
        Result<Object> result = new Result();
        result.setCode(re.getCode());
        result.setMsg(re.getMsg());
        result.setData(object);
        return result;
    }

    public static Result success(Integer code, String msg,Object object) {
        Result<Object> result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(object);
        return result;
    }

    public static Result success(Integer code, Object object) {
        Result<Object> result = new Result();
        result.setCode(code);
        result.setData(object);
        return result;
    }

    public static Result error(ResultEnum re) {
        return error(re.getCode(), re.getMsg());
    }
    
    public static Result error(String msg) {
        return error(ResultEnum.FAILED.getCode(), msg);
    }

    public static Result error(Integer code, String msg) {
        Result<Object> result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
    public static Result error(Integer code,String msg, Object object) {
        Result<Object> result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(object);
        return result;
    }
}
