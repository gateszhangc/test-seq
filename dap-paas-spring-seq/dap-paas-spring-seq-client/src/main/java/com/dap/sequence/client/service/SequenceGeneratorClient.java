package com.dap.sequence.client.service;

import com.dap.sequence.client.exception.PaasSequenceException;

import java.math.BigInteger;
import java.util.Date;


public interface SequenceGeneratorClient {

    /**
     * 根据序列编号获取全局唯一ID序
     * 
     * @param seqCode
     * @return  type String
     */
    public abstract String getStringSequence(String seqCode) ;

    /**
     * 根据序列编号获取全局唯一ID序
     *
     * @param seqCode 序列Code编码
     * @param replaceSymbols 占位符规则参数
     * @return 序号
     */
    String getStringSequence(String seqCode, String... replaceSymbols);

    /**
     * 根据序列编号获取（数字类型）全局唯一ID序
     *
     * @param seqCode
     * @return type BigInteger
     */
    public abstract BigInteger getBigIntegerSequence(String seqCode) throws PaasSequenceException;

    /**
     * 获取每日切换序列 
     * @param seqCode
     * @param date
     * @return
     */
    public abstract String getDaySwitchSequence(String seqCode , Date date) ;

    /**
     * 获取每日切换序列
     * @param seqCode
     * @param date
     * @param replaceSymbols 占位符规则参数     *
     * @return
     */
    public String getDaySwitchSequence(String seqCode , Date date, String... replaceSymbols) ;

    /**
     * 指定日期获取序列
     *
     * @param seqCode 序列设计编号
     * @param date 指定获取该日期的序列
     * @return 返回序列字符串
     * @throws PaasSequenceException 日期比传否则抛出异常
     * @author yzp
     */
    String getDaySwitchSequenceRequiredDate(String seqCode, Date date) throws PaasSequenceException;

    /**
     * 指定日期获取序列
     *
     * @param seqCode 序列设计编号
     * @param date 指定获取该日期的序列
     * @param replaceSymbols 占位符规则参数
     * @return 返回序列字符串
     * @throws PaasSequenceException 日期比传否则抛出异常
     * @author yzp
     */
    String getDaySwitchSequenceRequiredDate(String seqCode, Date date, String... replaceSymbols) throws PaasSequenceException;

    /**
     * 获取雪花序列
     * @return  序列号
     */
    public abstract String getSnowFlake();
}