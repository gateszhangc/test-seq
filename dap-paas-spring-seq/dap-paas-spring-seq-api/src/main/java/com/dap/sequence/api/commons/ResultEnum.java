package com.dap.sequence.api.commons;

/**
 * @className ResultEnum
 * @description 结果泛枚举类
 * @date 2023/11/23 09:58:44
 * @version: V23.06
 */
public enum ResultEnum {

    SUCCESS(200, "请求成功"),
    FAILED(201, "请求失败"),
    ADD(202, "添加成功"),
    UPDATE(203, "修改成功"),
    DELETED(204, "删除成功"),
    EXIST(207,"该数据已存在"),
    INSTANCE_RUN(208,"节点在运行，不能删除"),
    INSTANCE_EXIST(209,"集群下存在节点，不能删除"),
    //服务治理
    ZOOKPEER_EXCEPTION(401,"ZK连接异常"),
    CREATED(205,"创建成功"),
    PARAM_NOT_MATH(404,"参数无方法"),
    INSERT_ERROR(405,"新增出错"),
    //注册中新部署
    INVALID_REQUEST(400,"数据请求错误"),
    INTERNAL_SERVER_ERROR(500,"服务器发生错误"),
    TEST_RE(402, "集群链接失败，请核对配置数据"),
    CONFIG_TABLE(209, "配置项key健重复，请核对配置数据"),
    REGISTER_NODE_IS_NULL(211, "集群实例为空，请添加实例后及逆行检测"),
    TEST_SQL(403, "数据库链接失败，请核对配置数据");

    private Integer code;
    private String msg;

     ResultEnum(Integer code, String msg) {
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
