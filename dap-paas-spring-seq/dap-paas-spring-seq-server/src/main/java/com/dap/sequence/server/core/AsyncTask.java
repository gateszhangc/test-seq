package com.dap.sequence.server.core;

import com.dap.paas.spring.common.pojo.AlarmParamData;
import com.dap.paas.spring.common.service.AlarmMonitorService;
import com.dap.sequence.api.constant.SeqDesignCallbackModeEnum;
import com.dap.sequence.api.core.AbstractSeqFlowEngine;
import com.dap.sequence.api.core.SeqProducer;
import com.dap.sequence.api.dto.NumberRuleInfo;
import com.dap.sequence.api.obj.SeqObj;
import com.dap.sequence.server.holder.SeqHolder;
import com.dap.sequence.server.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static com.dap.sequence.api.constant.SeqDesignCallbackModeEnum.*;
import static com.dap.sequence.api.constant.SequenceConstant.DAY_SWITCH_SPLIT;
import static com.dap.sequence.api.constant.SequenceConstant.TenantPostfix;

/**
 * 异步任务
 *
 * @author yzp
 * @date 2024/7/17 10:20
 */
@Service
public class AsyncTask {
    private static final Logger log = LoggerFactory.getLogger(AsyncTask.class);

    @Value("${dap.sequence.retainNum:30}")
    private int retainNum;

    /**
     * 序列缓存倍数
     */
    @Value("${dap.sequence.serverCacheTimes:10}")
    private int serverCacheTimes;

    /**
     * 序列缓存创建倍数
     */
    @Value("${dap.sequence.serverCacheCreateTimes:2}")
    private int serverCacheCreateTimes;

    /**
     * 异步补仓锁
     */
    private final static Object asyncCreateSeqLock = new Object();

    @Value("${dap.sequence.alarmUrl:}")
    private String alarmUrl;

    @Value("${dap.sequence.projectGroup:}")
    private String projectGroup;

    @Value("${dap.sequence.centerMark:}")
    private String centerMark;

    @Value("${dap.sequence.alarmNum:5}")
    private int alarmNum;

    //当前告警次数
    private int currentAlarmNum = 0;

    @Autowired
    private AlarmMonitorService alarmMonitorService;

    /**
     * 清理本地缓存序列存储{@link SeqHolder#getMap()}中
     * 时间类型的回拨模式包含：日切模式{@link SeqDesignCallbackModeEnum#CALLBACK_MODE_DAY}、
     * 月切模式{@link SeqDesignCallbackModeEnum#CALLBACK_MODE_MONTH}、
     * 年切模式{@link SeqDesignCallbackModeEnum#CALLBACK_MODE_YEAR} <p>
     * 及当前时间T前后范围 {@link AsyncTask#retainNum}以外的缓存序列
     *
     *
     * @param seqCodeDay seqCode###回拨模式具体值
     * @param tenantId   租户id
     * @author yzp
     */
    @Async("seqTaskExecutor")
    public void clearSequenceMap(String seqCodeDay, String tenantId) {
        String seqMapKey = seqCodeDay + TenantPostfix + tenantId;
        try {
            if (SeqHolder.getClearFlagSet().add(seqMapKey)) {
                // 获取当前查询的回拨模式时间值
                String[] seqArray = seqCodeDay.split(DAY_SWITCH_SPLIT);
                int dayParam;
                if (seqArray.length == 2) {
                    if (CALLBACK_MODE_GLOBAL.getFormat().equals(seqArray[1])) {
                        log.debug("清理的回拨模式={}，无需清理序列缓存的key={}", seqArray[1], seqCodeDay);
                        return;
                    } else {
                        dayParam = Integer.parseInt(seqArray[1]);
                    }
                } else {
                    log.error("租户号码：{}  序列编号 {} 回拨模式day未找到!!! 无需清理", tenantId, seqCodeDay);
                    SeqHolder.getClearFlagSet().remove(seqMapKey);
                    return;
                }
                // 获取当前查询的序列编码
                String seqCodeParam = extractSeqCode(seqCodeDay);
                AbstractSeqFlowEngine seqFlowEngine = SeqHolder.getEngineMap().get(seqCodeParam + TenantPostfix + tenantId);
                if (seqFlowEngine == null) {
                    log.error("租户号码：{}  序列编号 {} 序列引擎未生成!!! 不清理", tenantId, seqCodeDay);
                    SeqHolder.getClearFlagSet().remove(seqMapKey);
                    return;
                }
                // 开始清理缓存序列
                String callbackMode = seqFlowEngine.getSeqObj().getCallbackMode();
                if ((CALLBACK_MODE_DAY.getCode().equals(callbackMode) ||
                        CALLBACK_MODE_MONTH.getCode().equals(callbackMode) ||
                        CALLBACK_MODE_YEAR.getCode().equals(callbackMode))) {
                    // 获取当前序列设计编号的所有缓存序列key
                    List<String> seqCodeAllKeys = SeqHolder.getMap().keySet().stream()
                            .filter(key -> extractSeqCode(key).equals(seqCodeParam))
                            .collect(Collectors.toList());
                    // 小于当前查询日期保留的缓存序列key
                    List<String> beforeDaySeqCodeKeys = seqCodeAllKeys.stream().filter(key -> extractSeqDay(key) < dayParam)
                            .sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                    List<String> beforeDaySeqCodeRetainKeys = beforeDaySeqCodeKeys
                            .subList(0, Math.min(retainNum, beforeDaySeqCodeKeys.size()));
                    // 大于当前查询日期保留的缓存序列key
                    List<String> afterDaySeqCodeKeys = seqCodeAllKeys.stream().filter(key -> extractSeqDay(key) >= dayParam)
                            .sorted().collect(Collectors.toList());
                    List<String> afterDaySeqCodeRetainKeys = afterDaySeqCodeKeys
                            .subList(0, Math.min(retainNum + 1, afterDaySeqCodeKeys.size()));
                    // 清理不需要保留的缓存序列
                    seqCodeAllKeys.forEach(key -> {
                        if (!beforeDaySeqCodeRetainKeys.contains(key) && !afterDaySeqCodeRetainKeys.contains(key)) {
                            log.info("客户端获取序列{}时触发清理前后范围{}周期以外的对应缓存序列key:{}", seqCodeDay, retainNum, key);
                            SeqHolder.getMap().remove(key);
                            SeqHolder.getClearFlagSet().remove(key);
                        }
                    });
                }
            }
        } catch (Throwable e) {
            log.error("清理时间类型的回拨模式序列缓存异常，移除序列缓存的key={}", seqMapKey, e);
            SeqHolder.getClearFlagSet().remove(seqMapKey);
        }
    }

    /**
     * 提取日切、月切、年切的数值
     *
     * @param key
     * @return
     */
    private int extractSeqDay(String key) {
        return Integer.parseInt(key.split(DAY_SWITCH_SPLIT)[1].split(TenantPostfix)[0]);
    }


    /**
     * 提取序列编码
     *
     * @param key
     * @return
     */
    private String extractSeqCode(String key) {
        return key.split(DAY_SWITCH_SPLIT)[0];
    }

    /**
     * 异步生成序列
     *
     * @param seqProducer 序列生成者
     * @param seqObj 序列设计对象
     * @param day 回拨模式日期具体值
     */
    @Async("seqTaskExecutor")
    public void createSeq(SeqProducer seqProducer, SeqObj seqObj, String day,String mapKey) {
        BlockingQueue<Object> unUseQueue = SeqHolder.getSequenceMapBySequenceCode(mapKey);
        synchronized (asyncCreateSeqLock) {
            while (unUseQueue != null && unUseQueue.size() < serverCacheTimes * seqObj.getSequenceNumber()) {
                seqProducer.createSeq(seqObj, day,serverCacheTimes,serverCacheCreateTimes);
            }
        }
    }

    @Async("seqTaskExecutor")
    public void seqAlarmThreshold(NumberRuleInfo numberRuleInfo, Integer needSize, String curVal,SeqObj seqObj) {
        if(null != seqObj) {
            try {
                int alarmThreshold = Integer.parseInt(seqObj.getCallbackAlarmThreshold());
                //最大值
                long numberEnd = numberRuleInfo.getNumberEnd();
                double curAlarmThreshold = Math.round((Long.parseLong(curVal) + needSize) * 100 / Double.valueOf(numberEnd));
                if (curAlarmThreshold > alarmThreshold) {//当前计算阈值超过设置的阈值，需要告警
                    //告警次数没有超过设置的值
                    if (currentAlarmNum < alarmNum) {
                        AlarmParamData alarmData = new AlarmParamData();
                        alarmData.setTime(System.currentTimeMillis());
                        alarmData.setTitle("序列编号超过设置告警阈值，序列号为：" + seqObj.getSequenceCode() + "当前值；" + curAlarmThreshold + "% 设置阈值：" + alarmThreshold + "%");
                        alarmData.setType("sequenceServer");
                        alarmData.setAddress(NetUtils.ip());
                        alarmData.setLevel("3");
                        alarmData.setProjectGroup(projectGroup);
                        alarmData.setBussDomain("");
                        alarmData.setContainerCluster("");
                        alarmData.setCenterMark(centerMark);
                        alarmData.setCategory("operation");
                        alarmData.setTime(System.currentTimeMillis());
                        alarmMonitorService.processAlarm(alarmData);
                        currentAlarmNum++;
                    }
                }
            } catch (Exception e) {
                log.error("序列调用监控平台异常SequenceCode：{}", seqObj.getSequenceCode());
            }
        }
    }
}