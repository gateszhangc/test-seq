package com.dap.sequence.api.constant;

public class SequenceConstant {

    /**
     * 设计状态为完成
     */
    public static String designStateOn = "2";

    /**
     * 部署类型为JVM类型
     */
    public static String deployTypeJVM = "1";

    /**
     * 部署类型为分布式类型
     */
    public static String deployTypeDistribute = "2";

    /**
     * 发布状态 1 已发布 2 未发布 3更新待发布
     */
    public static String publishStateOn = "1";

    /**
     * 结构类型    1 连续
     */
    public static String seqStructTypeSerial = "1";
    /**
     * 结构类型   2 非连续
     */
    public static String seqStructTypeNoSerial = "2";
    /**
     * 规则类型  1 固定字符串
     */
    public static String STRING_RULE = "1";

    /**
     * 规则类型  2 枚举
     */
    public static String ENUM_RUlE = "2";

    /**
     * 规则类型  3 日期
     */
    public static String DATE_RUlE = "3";

    /**
     * 规则类型  4 数字
     */
    public static String NUMBER_RULE = "4";

    /**
     * 特殊字符串 5
     * SpecialCharRule
     */
    public static String SPECIAL_RULE = "5";

    /**
     * 序列锁  1 锁定
     */
    public static String seqIsLock = "1";
    /**
     * 序列锁 0 未锁
     */
    public static String seqIsNotLock = "0";

    public static final String DAY_SWITCH_SPLIT = "###";

    public static final String DAY_BASE = "base";

    /**
     * 租户后缀标识
     */
    public static String TenantPostfix="@";

    public static final int INT_8 = 8;
}
