package com.dap.sequence.api.constant;

/**
 * 序列设计回拨模式分类
 *
 * @author yzp
 * @date 2024/7/16 20:21
 */
public enum SeqDesignCallbackModeEnum {

    CALLBACK_MODE_GLOBAL("0", SequenceConstant.DAY_BASE,"全局序列"),

    CALLBACK_MODE_DAY("1", "yyyyMMdd","按日回拨"),

    CALLBACK_MODE_MONTH("2", "yyyyMM","按月回拨"),

    CALLBACK_MODE_YEAR("3", "yyyy","按年回拨");

    /**
     * 编码
     */
    private final String code;

    /**
     * 格式化
     */
    private final String format;

    /**
     * 描述
     */
    private final String desc;

    SeqDesignCallbackModeEnum(String code, String format, String desc) {
        this.code = code;
        this.format = format;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getFormat() {
        return format;
    }

    public String getDesc() {
        return desc;
    }
}
