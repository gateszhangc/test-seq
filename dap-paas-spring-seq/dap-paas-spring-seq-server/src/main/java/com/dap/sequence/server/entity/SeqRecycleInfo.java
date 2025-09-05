package com.dap.sequence.server.entity;

import com.base.api.entity.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: liu
 * @date: 2022/8/30 19:51
 * @description:
 */
public class SeqRecycleInfo extends BaseEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeqRecycleInfo.class);
    //日切
    private String rqDay;
    //客户端ip
    private String ip;
    //回收编号
    private String recycleCode;
    //序列id
    private String seqDesignId;
    //序列编号
    private String seqCode;
    private Date startTime;

    private Date endTime;

    public String getSeqCode() {
        return seqCode;
    }

    public void setSeqCode(String seqCode) {
        this.seqCode = seqCode;
    }

    public String getRqDay() {
        return rqDay;
    }

    public void setRqDay(String rqDay) {
        this.rqDay = rqDay;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        try {
            this.startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        try {
            this.endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public String getSeqDesignId() {
        return seqDesignId;
    }

    public void setSeqDesignId(String seqDesignId) {
        this.seqDesignId = seqDesignId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRecycleCode() {
        return recycleCode;
    }

    public void setRecycleCode(String recycleCode) {
        this.recycleCode = recycleCode;
    }
}
