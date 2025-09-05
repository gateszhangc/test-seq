package com.dap.sequence.server.config;

import com.alibaba.fastjson.JSONObject;
import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.SeqFlowEngine;
import com.dap.sequence.api.dto.*;
import com.dap.sequence.api.expection.SequenceException;
import com.dap.sequence.api.util.DateUtils;
import com.dap.sequence.server.entity.SeqDesignPo;
import com.dap.sequence.server.entity.SeqInstanceRule;
import com.dap.sequence.server.factory.SeqFlowEngineFactory;
import com.dap.sequence.server.holder.SeqHolder;
import com.dap.sequence.server.service.SeqDesignService;
import com.dap.sequence.server.service.SeqInstanceRuleService;

import com.dap.sequence.server.service.SeqSdkMonitorService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.dap.sequence.api.constant.SequenceConstant.*;

/**
 * @Description: 随着容器启动加载规则
 * @Author: kanguangqiang
 * @Date: 2019/12/12 10:28
 * @Version: 1.0.0
 */
@Component
public class SeqPublishRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(SeqPublishRunner.class);

    @Autowired
    private SeqInstanceRuleService seqInstanceRuleService;

    @Autowired
    private SeqFlowEngineFactory seqFlowEngineFactory;

    @Autowired
    private SeqDesignService seqDesignService;

    /**
     * 超过此时间间隔的且状态不等于运行的回收, 单位为秒
     */
    @Value("${dap.sequence.rcvWorkIdMaxIntervalTime:3600}")
    private int rcvWorkIdMaxIntervalTime;

    @Autowired
    private SeqSdkMonitorService seqSdkMonitorService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        publishServer();

        loadRules();
    }

    /**
     * 加载所有引用规则
     * @throws SequenceException
     */
    @Scheduled(cron = "${dap.sequence.refreshRule:0 0/1 * * * ?}")
    private void publishServer() throws SequenceException {
        loadRules();
        updateSeqHolder();
    }

    private void loadRules() {
        Map map = new HashMap();
        List<SeqInstanceRule> collect = seqInstanceRuleService.queryList(map);
        List<Rule> ruleInfos = new ArrayList<>();
        for (SeqInstanceRule e : collect) {
            Rule rule = new Rule();
            rule.setRuleId(e.getId());
            rule.setRuleCode(e.getSeqDesignId());
            rule.setRuleName(e.getInstanceRuleName());
            rule.setRuleType(e.getInstanceRuleType());
            rule.setTenantId(e.getTenantId());
            switch (e.getInstanceRuleType()) {
                default:
                    //类型不匹配
                    logger.error("序列类型不匹配!");
                    break;
                case "1":
                    //固定字符串
                    StringRuleInfo stringRuleInfo = JSONObject.parseObject(e.getInstanceRuleInfo(), StringRuleInfo.class);
                    rule.setRuleData(stringRuleInfo);
                    ruleInfos.add(rule);
                    break;
                case "2":
                    //枚举
                    JSONObject jsonObject = JSONObject.parseObject(e.getInstanceRuleInfo());
                    EnumRuleInfo enumRuleInfo = new EnumRuleInfo(jsonObject.getInteger("numberStep"), jsonObject.getString("initData"), jsonObject.getString("enumStore"));
                    rule.setRuleData(enumRuleInfo);
                    ruleInfos.add(rule);
                    break;
                case "3":
                    //日期
                    DateRuleInfo dateRuleInfo = JSONObject.parseObject(e.getInstanceRuleInfo(), DateRuleInfo.class);
                    rule.setRuleData(dateRuleInfo);
                    ruleInfos.add(rule);
                    break;
                case "4":
                    //数字
                    NumberRuleInfo numberRuleInfo = JSONObject.parseObject(e.getInstanceRuleInfo(), NumberRuleInfo.class);
                    rule.setRuleData(numberRuleInfo);
                    ruleInfos.add(rule);
                    break;
                case "5":
                    //特殊字符
                    SpecialCharRuleInfo specialCharRuleInfo = JSONObject.parseObject(e.getInstanceRuleInfo(), SpecialCharRuleInfo.class);
                    rule.setRuleData(specialCharRuleInfo);
                    ruleInfos.add(rule);
                    break;
            }
        }
        SeqHolder.getRulesMap().clear();
        ruleInfos.stream().collect(Collectors.groupingBy(Rule::getRuleCode)).forEach((key, value) -> {
            SeqHolder.getRulesMap().put(key, value);
        });
    }

    public void updateSeqHolder() {
        /**
         * 加载所有的规则到序号里面
         */
        Map map = new HashMap();
        map.put("sequenceStatus", SequenceConstant.designStateOn);
        List<SeqDesignPo> SeqDesignList = seqDesignService.queryList(map);
        if (SeqDesignList.isEmpty()) {
            logger.debug("No SeqDesignList found.");
            return;
        }
        SeqDesignList.forEach(seqDesignPo -> {
            try {
                seqFlowEngineFactory.doCreateEngine(seqDesignPo.getId());
            } catch (SequenceException e) {
                logger.error("updateSeqHolder 更新失败", e);
                e.printStackTrace();
            }
        });
    }

    /**
     * 回收workId， 默认半小时执行一次
     */
    @Scheduled(cron = "${dap.sequence.rcvWorkIdCron:0 0/30 * * * ?}")
    private void rcvSnowflakeWorkIdAuto(){
        try {
            logger.info("seq server recycling workId start...");
            final int rcvCount = seqSdkMonitorService.rcvSnowflakeWorkId(rcvWorkIdMaxIntervalTime);
            logger.info("seq server recycling workId end, and recycling total count is {}", rcvCount);
        } catch (Exception e) {
            logger.error("seq server recycling failed, error message: ", e);
        }
    }
}
