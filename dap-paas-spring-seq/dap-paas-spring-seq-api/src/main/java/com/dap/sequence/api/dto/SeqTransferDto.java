package com.dap.sequence.api.dto;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @Description: 序列传输对象 用于客户端获取服务端序列集合并缓存本地
 * @Author: liuz
 */
public class SeqTransferDto implements Serializable {

    private static final long serialVersionUID = -4630779005875445958L;

    private ArrayList<Object> list;

    private String sequenceCode;

    private boolean isNum;

    public boolean isNum() {
        return isNum;
    }

    public void setNum(boolean num) {
        isNum = num;
    }

    public ArrayList<Object> getList() {
        return list;
    }

    public void setList(ArrayList<Object> list) {
        this.list = list;
    }

    public String getSequenceCode() {
        return sequenceCode;
    }

    public void setSequenceCode(String sequenceCode) {
        this.sequenceCode = sequenceCode;
    }
}
