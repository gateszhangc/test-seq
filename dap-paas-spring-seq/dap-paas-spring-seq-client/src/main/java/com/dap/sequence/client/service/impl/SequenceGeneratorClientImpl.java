package com.dap.sequence.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.SeqConsumer;
import com.dap.sequence.api.exception.ExceptionEnum;
import com.dap.sequence.api.exception.SequenceException;
import com.dap.sequence.api.util.DateUtils;
import com.dap.sequence.client.balancer.RestTemplateBalancer;
import com.dap.sequence.client.core.SequenceQueueFactory;
import com.dap.sequence.client.exception.PaasSequenceException;
import com.dap.sequence.client.service.SequenceGeneratorClient;
import com.dap.sequence.client.snow.Snowflake;
import com.dap.sequence.client.utils.OtherUtil;
import com.dap.sequence.client.utils.PlaceholderRuleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列生成
 *
 */
@Service
public class SequenceGeneratorClientImpl implements SequenceGeneratorClient {

    private static final Logger logger = LoggerFactory.getLogger(SequenceGeneratorClientImpl.class);
    @Autowired
    RestTemplateBalancer restTemplateBalancer;
    @Autowired
    private SeqConsumer seqConsumer;
    private static final ConcurrentHashMap<String, String> C0DE_ACCESSKEY_CHECK_MAP = new ConcurrentHashMap<>();
    @Autowired
    private SequenceQueueFactory sequenceQueueFactory;
    @Autowired
    private Snowflake snowflake;

    @Autowired
    private Environment environment;

    @Override
    public String getStringSequence(String seqCode) {
        if (!checkAccessKey()) {
            throw new SequenceException(ExceptionEnum.SEQ_ACCESS_ERROR);
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //String sequenceCode = seqCode + SequenceConstant.DAY_SWITCH_SPLIT + SequenceConstant.DAY_BASE;
        Object o = sequenceQueueFactory.getBaseSequence(seqCode , seqConsumer);
        stopWatch.stop();
        logger.info("get dapSequence success  getSequenceObject={}, time={}",o,stopWatch.getTotalTimeMillis());
        return String.valueOf(o);
    }

    /**
     * 根据序列编号获取全局唯一ID序
     *
     * @param seqCode 序列Code编码
     * @param replaceSymbols 占位符规则参数
     * @return
     */
    @Override
    public String getStringSequence(String seqCode, String... replaceSymbols) {
        Object seq = getStringSequence(seqCode);
        String sequence = String.valueOf(seq);
        return PlaceholderRuleUtil.placeholderHandle(sequence, replaceSymbols);
    }

    @Override
    public BigInteger getBigIntegerSequence(String seqCode) throws PaasSequenceException {
        if (!checkAccessKey()) {
            throw new SequenceException(ExceptionEnum.SEQ_ACCESS_ERROR);
        }
        ;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object o = sequenceQueueFactory.getBaseSequence(seqCode,seqConsumer);
        stopWatch.stop();
        logger.info("get dapSequence1  success   getBigIntegerSequence={}, time={}",o,stopWatch.getTotalTimeMillis());
        return new BigInteger(o.toString());
    }

    @Override
    public String getDaySwitchSequence(String seqCode, Date date) {
        if (!checkAccessKey()) {
            throw new SequenceException(ExceptionEnum.SEQ_ACCESS_ERROR);
        }
        ;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //没有则加载序号设计到缓存中
        sequenceQueueFactory.getOrFetchSeqDesignFromServer(seqCode,seqConsumer);
        String sequenceCode = seqCode + SequenceConstant.DAY_SWITCH_SPLIT + DateUtils.convertToString(date == null ? new Date() : date, DateUtils.DATE_FORMAT);
        Object o = sequenceQueueFactory.getSequence(sequenceCode,seqConsumer);
        stopWatch.stop();
        logger.info("get dapSequence success  getDaySwitchSequence={}, time={}",o,stopWatch.getTotalTimeMillis());
        return String.valueOf(o);
    }

    @Override
    public String getDaySwitchSequenceRequiredDate(String seqCode, Date date) throws PaasSequenceException {
        if (!checkAccessKey()) {
            throw new SequenceException(ExceptionEnum.SEQ_ACCESS_ERROR);
        }
        if (date == null) {
            throw new PaasSequenceException("seqCode: " + seqCode + " 获取指定日期序列，日期不能为null");
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //没有则加载序号设计到缓存中
        sequenceQueueFactory.getOrFetchSeqDesignFromServer(seqCode,seqConsumer);
        String sequenceCode = seqCode + SequenceConstant.DAY_SWITCH_SPLIT + DateUtils.convertToString(date, DateUtils.DATE_FORMAT);
        Object o = sequenceQueueFactory.getSequence(sequenceCode, seqConsumer);
        stopWatch.stop();
        logger.info("get dapSequence success  getDaySwitchSequenceRequiredDate={}, time={}", o, stopWatch.getTotalTimeMillis());
        return String.valueOf(o);
    }


    @Override
    public String getDaySwitchSequence(String seqCode , Date date, String... replaceSymbols) {
        String seq = getDaySwitchSequence(seqCode,date);
        return PlaceholderRuleUtil.placeholderHandle(seq, replaceSymbols);
    }

    @Override
    public String getDaySwitchSequenceRequiredDate(String seqCode , Date date, String... replaceSymbols) throws PaasSequenceException {
        String seq = getDaySwitchSequenceRequiredDate(seqCode,date);
        return PlaceholderRuleUtil.placeholderHandle(seq, replaceSymbols);
    }


    @Override
    public String getSnowFlake() {
        return snowflake.nextIdStr();
    }

    private boolean checkAccessKey() {
        String appCode = environment.getProperty("dap.sequence.appCode");
        String accessKey = environment.getProperty("dap.sequence.accessKey");
        if (C0DE_ACCESSKEY_CHECK_MAP.containsKey(appCode)) {
            logger.info("checkAccessKey in local;;localCacheKey:::{}", C0DE_ACCESSKEY_CHECK_MAP.toString());
            if (accessKey.equals(C0DE_ACCESSKEY_CHECK_MAP.get(appCode))) {
                return true;
            } else {
                logger.error("request checkAccessKey false !!!");
                return false;
            }
        }

        Map queryMap = new HashMap();
        queryMap.put("appCode", appCode);
        queryMap.put("accessKey", accessKey);
        logger.info("checkAccessKey from server;;request args:::{}", JSON.toJSONString(queryMap));
        String body = restTemplateBalancer.postRequest(JSON.toJSONString(queryMap), OtherUtil.CHECK_ACCESSKEY);
        if (!StringUtils.hasText(body)) {
            logger.error("没有可用的序列服务 or 服务端异常！！");
            throw new SequenceException("没有可用的序列服务 or 服务端异常！！");
        }
        JSONObject object = JSONObject.parseObject(body);

        if(object.getIntValue("code") == 200) {
            C0DE_ACCESSKEY_CHECK_MAP.put(appCode,accessKey);
        }else{
            logger.error("request checkAccessKey false !!!");
            return false;
        }
        return true;
    }

}
