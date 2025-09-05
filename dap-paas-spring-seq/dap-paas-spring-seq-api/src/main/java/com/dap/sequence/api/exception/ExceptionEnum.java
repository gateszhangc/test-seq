package com.dap.sequence.api.exception;

/**
 * @className ExceptionEnum
 * @description 异常枚举类
 * @author renle
 * @date 2023/11/24 17:43:54 
 * @version: V23.06
 */
public enum ExceptionEnum implements IBaseError {

    SUCCESS("S2001", "请求成功!"),

    SEQ_RECOVERY_NOT_FOUND("S2002", "没有找到对应的序列！、无法回收对应序号!"),

    SEQ_ENGINE_CACHE_KEY_NOT_FOUND("S2003", "参数引擎缓存key无法找到!"),

    SEQ_NOT_FOUND("S2004", "序列编辑器无法找到!"),

    SEQ_CODE_NOT_FOUND("S2005", "序列参数seqCode为空!"),

    SEQ_FROM_SERVER_EMPTY("S2006", "从服务端获取序列为空!"),

    SEQ_RULE_EMPTY("S2007", "没有定义序列规则!"),

    SEQ_CLUSTER_NOT_FOUND("S2008", "集群服务不存在!"),

    SEQ_NODE_NOT_FOUND("S2009", "没有启动的序列节点!"),

    SEQ_SDK_MONITOR_INFO_EMPTY("S2010", "序列SDK监控信息参数为空!"),

    SEQ_UPDATE_HOLDER_FAILED("S2011", "动态修改Holder缓存失败!"),

    SEQ_ACCESS_KEY_CHECK_FAILED("S2012", "accessKey校验失败!"),

    SEQ_DATA_SOURCE_PARAM_EMPTY("S2013", "数据库参数为空!"),

    SEQ_UPDATE_DB_FILE_EMPTY("S2014", "操作数据库文件为空!"),

    SEQ_SWITCH_DB_FAILED("S2015", "数据库切换失败!"),

    PLACE_DIGITS_LESS_THAN_FIXED("S2016", "参数位数小于固定长度位数!"),

    GET_SEQ_QUEUE_ERROR("S2017", "队列为空,从服务端获取序列为空!"),

    GET_SEQ_OPTIONAL_DB_ERROR("S2018", "自选序列操作数据库失败!"),

    LOCK_KEY_EMPTY("S2019", "加锁key不能为空!"),

    LOCK_NOT_ADD_KEY("S2020", "尚未加锁!"),

    LOCK_NOT_HAVE_THREAD("S2021", "当前线程尚未持有锁!"),

    SEQ_ACCESS_ERROR("S2022", "appCode或accessKey错误！！!"),

    SEQ_GET_WORK_ID_FAIL("S2031", "当前应用未配置workId,且不能从序列服务端获取到workId,雪花算法无法使用！！！!"),

    SEQ_SNOW_INFO_EMPTY("S2032", "序列SDK，请求参数为空!"),

    SEQ_SNOW_PARAM_ERROR("S2033", "序列SDK，请求参数缺失!"),

    SEQ_SNOW_ADD_FAILURE("S2034", "序列服务端已无可用workId,获取失败!"),

    CLOSE_STREAM_ERROR("S2038", "流关闭报错!"),

    BODY_NOT_MATCH("S4400","请求的数据格式不符!"),

    SIGNATURE_NOT_MATCH("S4401","请求的数字签名不匹配!"),

    OPERATE_DB_ERROR("S4402","操作数据库异常!"),

    OPERATE_SERVICE_ERROR("S4403","服务操作异常!"),

    NOT_FOUND("S4404", "未找到该资源!"),

    INTERNAL_SERVER_ERROR("S4500", "服务器内部错误!"),

    SERVER_BUSY("S4503","服务器正忙，请稍后再试!");

    /**
     * 错误码
     */
    private final String resultCode;

    /**
     * 错误描述
     */
    private final String resultMsg;

    ExceptionEnum(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
