package com.dap.sequence.api.dto;


import com.dap.sequence.api.dto.base.BaseRuleInfo;

public class NumberRuleInfo extends BaseRuleInfo {

    private Integer numberStep;
    private Long numberStart;
    private Long numberEnd;
    private String numberFormat;
    
    /**
     * 达到最大时 0继续增长  1从头开始 2最大值
     */
    private Integer numberCircle;

    public NumberRuleInfo(){

    }
    public NumberRuleInfo(Long numberStart,Long numberEnd,Integer numberStep,Integer numberCircle){
        this.numberStart = numberStart;
        this.numberEnd = numberEnd;
        this.numberStep = numberStep;
        this.numberCircle = numberCircle;
    }
    public Integer getNumberStep() {
        return numberStep;
    }

    public void setNumberStep(Integer numberStep) {
        this.numberStep = numberStep;
    }

    public Long getNumberStart() {
        return numberStart;
    }

    public void setNumberStart(Long numberStart) {
        this.numberStart = numberStart;
    }

    public Long getNumberEnd() {
        return numberEnd;
    }

    public void setNumberEnd(Long numberEnd) {
        this.numberEnd = numberEnd;
    }

    public String getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
    }

    public Integer getNumberCircle() {
        return numberCircle;
    }

    public void setNumberCircle(Integer numberCircle) {
        this.numberCircle = numberCircle;
    }

}
