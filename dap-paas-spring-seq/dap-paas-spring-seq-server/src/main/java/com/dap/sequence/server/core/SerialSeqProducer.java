package com.dap.sequence.server.core;


import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.BaseRuleHandler;
import com.dap.sequence.api.core.SeqProducer;
import com.dap.sequence.api.dto.Rule;
import com.dap.sequence.api.obj.SeqObj;
import com.dap.sequence.api.util.DateUtils;
import com.dap.sequence.server.entity.SeqCurNum;
import com.dap.sequence.server.holder.SeqHolder;
import com.dap.sequence.server.service.SeqCurNumService;
import com.dap.sequence.server.service.impl.SeqNumService;
import com.dap.sequence.server.utils.SpringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @Description: 连续序列
 * @Author:
 * @Date: update 2023/6/25 14:54
 * @Version: 1.0.0
 */
public class SerialSeqProducer implements SeqProducer {

    // private SeqInstanceRuleService seqInstanceRuleService = SpringUtils.getApplicationContext().getBean(SeqInstanceRuleService.class);
    private SeqCurNumService seqCurNumService = SpringUtils.getApplicationContext().getBean(SeqCurNumService.class);
    private SeqNumService seqNumService = SpringUtils.getApplicationContext().getBean(SeqNumService.class);

    private boolean callbackEnable;

    private static final Logger log = LoggerFactory.getLogger(SerialSeqProducer.class);

    public Boolean before(Rule rule, SeqCurNum seqDesignPo) {
        log.info("生成前处理");
        switch (rule.getRuleType()) {
            case "1": //固定字符串
                break;
            case "2": //枚举
                break;
            case "3": //日期
                break;
            case "4": //数字
                break;
            case "5"://特殊字符
                break;
            default:
                //
                break;
        }
        return true;
    }

    @Override
    public void loadSeq(String sequenceCode, List callBackNumList, String tenantId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (callBackNumList != null && callBackNumList.size() > 0) {
                    /**
                     * 获得服务端队列
                     */
                    String key = sequenceCode + SequenceConstant.TenantPostfix + tenantId;
                    LinkedBlockingQueue<Object> blockingQueue = (LinkedBlockingQueue<Object>) SeqHolder.getMap()
                            .get(key);
                    log.info("序列回收开始，编号：{}，回收数量：{}，起始：{}，结束：{}，回收前队列数量：{}", key, callBackNumList.size(),
                            callBackNumList.get(0), callBackNumList.get(callBackNumList.size() - 1),
                            blockingQueue.size());
                    try {
                        for (int i = callBackNumList.size() - 1; i >= 0; i--) {
                            blockingQueue.put(callBackNumList.get(i));
                        }
                        log.info("序列回收结束，编号：{}，回收数量：{}，回收后队列数量：{}", key, callBackNumList.size(), blockingQueue.size());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void createSeq(SeqObj seqObj, String day) {
        try {
            if (seqObj == null) {
                return;
            }
            String sequenceCode = seqObj.getSequenceCode() + SequenceConstant.DAY_SWITCH_SPLIT + day + SequenceConstant.TenantPostfix + seqObj.getTenantId();
            Map<String, String> map = new HashMap<>();
            map.put("seqDesignId", seqObj.getId());
            map.put("instanceRuleType", SequenceConstant.NUMBER_RULE);
            // List<SeqInstanceRule> seqInstanceRules = seqInstanceRuleService.queryList(map);
            // List<Rule> rules = seqObj.getRuleInfos();
            List<Rule> rules = SeqHolder.getRulesMap().get(seqObj.getId());
            if (CollectionUtils.isEmpty(rules)) {
                return;
            }
            Integer needSize = seqObj.getSequenceNumber();

            LinkedBlockingQueue<Object> blockingQueue = (LinkedBlockingQueue<Object>) SeqHolder.getSequenceMapBySequenceCode(sequenceCode);
            if (blockingQueue == null) {
                blockingQueue = new LinkedBlockingQueue<Object>();
                SeqHolder.putSequenceMap(sequenceCode, blockingQueue);
            }

            //获取数据库中回收池的序列
            if (callbackEnable) {
                List<String> callbackSeqList = seqNumService.getCallbackSeq(seqObj.getId(), day, seqObj.getTenantId(), needSize, blockingQueue);

                if (callbackSeqList.size() >= needSize) {
                    return;
                }
            }
            log.info("序列：{},开始组装, queueSize={} , 生成数量：{}", sequenceCode, blockingQueue.size(), needSize);

            List<Rule> numRule = rules.stream().filter(rule -> rule.getRuleType().equals(SequenceConstant.NUMBER_RULE)
                    || rule.getRuleType().equals(SequenceConstant.ENUM_RUlE)).collect(Collectors.toList());
            if (!numRule.isEmpty()) {
                if (!seqNumService.updateRuleCurVal(numRule, day, needSize, seqObj)) {
                    return;
                }
            } else {
                rules.forEach(seqInstanceRule -> {
                    Map<String, String> mapCur = new HashMap<>();
                    mapCur.put("seqInstanceRuleId", seqInstanceRule.getRuleId());
                    mapCur.put("inDay", day);
                    SeqCurNum seqCurNum = seqCurNumService.getObjectByCode(mapCur);
                    boolean before = before(seqInstanceRule, seqCurNum);
                });
            }


            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            //如果当前获取的不是今天的序列
            boolean istoday = StringUtils.isBlank(day) || SequenceConstant.DAY_BASE.equals(day);
            Date date = null;
            if (istoday) {
                date = new Date();
            } else {
                String date_format = DateUtils.DATE_FORMAT;
                if (StringUtils.length(day) == 4) {
                    date_format = "yyyy";
                } else if (StringUtils.length(day) == 6) {
                    date_format = "yyyyMM";
                }
                date = DateUtils.converToDate(day, date_format);
            }

            Date finalDate = date;
            for (int j = 0; j < needSize; j++) {
                StringBuffer seq = new StringBuffer();

                for (int i = 0; i < rules.size(); i++) {
                    Rule rule = rules.get(i);
                    Optional<BaseRuleHandler> optionalRuleHandler= SeqHolder.getRuleHandlerByName(rule.getRuleType());
                    optionalRuleHandler.ifPresent(ruleHandler -> {
                        Object seqVal = ruleHandler.generateRule(seq.toString(),finalDate , seqObj, rule.getRuleData());
                        seq.append(seqVal);
                    });
                }

                try {
                    blockingQueue.put(seq);
                } catch (InterruptedException e) {
                    log.error("序列:{} ,put: {} ,异常{}", sequenceCode, seq, e);
                    return;
                }

            }
            stopWatch.stop();
            log.info("序列：{},组装耗时：{} ms , queueSize={}", sequenceCode, stopWatch.getTotalTimeMillis(), blockingQueue.size());
        } finally {
            ThreadLocalVariables.unset();
        }
    }

    @Override
    public void createSeq(SeqObj seqObj, String day, int serverCacheTimes, int serverCacheCreateTimes) {
        try {
            if (seqObj == null) {
                return;
            }
            String sequenceCode = seqObj.getSequenceCode() + SequenceConstant.DAY_SWITCH_SPLIT + day
                    + SequenceConstant.TenantPostfix + seqObj.getTenantId();
            Map<String, String> map = new HashMap<>();
            map.put("seqDesignId", seqObj.getId());
            map.put("instanceRuleType", SequenceConstant.NUMBER_RULE);
            // List<SeqInstanceRule> seqInstanceRules =
            // seqInstanceRuleService.queryList(map);
            // List<Rule> rules = seqObj.getRuleInfos();
            List<Rule> rules = SeqHolder.getRulesMap().get(seqObj.getId());
            if (CollectionUtils.isEmpty(rules)) {
                return;
            }
            Integer needSize = seqObj.getSequenceNumber() * serverCacheTimes * serverCacheCreateTimes;

            LinkedBlockingQueue<Object> blockingQueue = (LinkedBlockingQueue<Object>) SeqHolder
                    .getSequenceMapBySequenceCode(sequenceCode);
            if (blockingQueue == null) {
                blockingQueue = new LinkedBlockingQueue<Object>();
                SeqHolder.putSequenceMap(sequenceCode, blockingQueue);
            }

            // 获取数据库中回收池的序列
            if (callbackEnable) {
                List<String> callbackSeqList = seqNumService.getCallbackSeq(seqObj.getId(), day, seqObj.getTenantId(),
                        needSize, blockingQueue);

                if (callbackSeqList.size() >= needSize) {
                    return;
                }
            }
            log.info("序列：{},开始组装, queueSize={} , 生成数量：{}", sequenceCode, blockingQueue.size(), needSize);

            List<Rule> numRule = rules.stream().filter(rule -> rule.getRuleType().equals(SequenceConstant.NUMBER_RULE)
                    || rule.getRuleType().equals(SequenceConstant.ENUM_RUlE)).collect(Collectors.toList());
            if (!numRule.isEmpty()) {
                if (!seqNumService.updateRuleCurVal(numRule, day, needSize, seqObj)) {
                    return;
                }
            } else {
                rules.forEach(seqInstanceRule -> {
                    Map<String, String> mapCur = new HashMap<>();
                    mapCur.put("seqInstanceRuleId", seqInstanceRule.getRuleId());
                    mapCur.put("inDay", day);
                    SeqCurNum seqCurNum = seqCurNumService.getObjectByCode(mapCur);
                    boolean before = before(seqInstanceRule, seqCurNum);
                });
            }

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // 如果当前获取的不是今天的序列
            boolean istoday = StringUtils.isBlank(day) || SequenceConstant.DAY_BASE.equals(day);
            Date date = null;
            if (istoday) {
                date = new Date();
            } else {
                String date_format = DateUtils.DATE_FORMAT;
                if (StringUtils.length(day) == 4) {
                    date_format = "yyyy";
                } else if (StringUtils.length(day) == 6) {
                    date_format = "yyyyMM";
                }
                date = DateUtils.converToDate(day, date_format);
            }

            Date finalDate = date;
            for (int j = 0; j < needSize; j++) {
                StringBuffer seq = new StringBuffer();

                for (int i = 0; i < rules.size(); i++) {
                    Rule rule = rules.get(i);
                    Optional<BaseRuleHandler> optionalRuleHandler= SeqHolder.getRuleHandlerByName(rule.getRuleType());
                    optionalRuleHandler.ifPresent(ruleHandler -> {
                        Object seqVal = ruleHandler.generateRule(seq.toString(),finalDate , seqObj, rule.getRuleData());
                        seq.append(seqVal);
                    });
                }

                try {
                    blockingQueue.put(seq);
                } catch (InterruptedException e) {
                    log.error("序列:{} ,put: {} ,异常{}", sequenceCode, seq, e);
                    return;
                }

            }
            stopWatch.stop();
            log.info("序列：{},组装耗时：{} ms , queueSize={}", sequenceCode, stopWatch.getTotalTimeMillis(),
                    blockingQueue.size());
        } finally {
            ThreadLocalVariables.unset();
        }
    }

    public boolean getCallbackEnable() {
        return callbackEnable;
    }

    public void setCallbackEnable(boolean callbackEnable) {
        this.callbackEnable = callbackEnable;
    }
}
