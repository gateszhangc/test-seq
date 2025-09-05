package com.dap.sequence.server.service.impl;

import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.dto.EnumRuleInfo;
import com.dap.sequence.api.dto.NumberRuleInfo;
import com.dap.sequence.api.dto.Rule;
import com.dap.sequence.api.obj.SeqObj;
import com.dap.sequence.server.core.AsyncTask;
import com.dap.sequence.server.core.ThreadLocalVariables;
import com.dap.sequence.server.entity.SeqCurNum;
import com.dap.sequence.server.entity.SeqRecycleInfo;
import com.dap.sequence.server.rules.NumberRuleHandler;
import com.dap.sequence.server.service.SeqCurNumService;
import com.dap.sequence.server.service.SeqDesignService;
import com.dap.sequence.server.service.SeqRecycleInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 异步生产序列类
 *
 * @author zpj
 * @date 2021/07/01
 */
@Service
public class SeqNumService {
    private static final Logger log = LoggerFactory.getLogger(SeqNumService.class);

    @Autowired
    private SeqCurNumService seqCurNumService;

    @Autowired
    private SeqRecycleInfoService seqRecycleInfoService;

    @Autowired
    private SeqDesignService seqDesignService;

    @Autowired
    private AsyncTask asyncTask;

    /**
     * 更新数字结束值
     * 将号段当前值放入线程变量ThreadLocalVariables中供后续生成序号递增使用<p>
     *
     * @param numberRuleInfo 数字规则对象
     * @param seqDesignPo 序列设计对象
     * @param needSize 序号生成的数量
     * @return 返回号段对象
     */
    public SeqCurNum updateNumEndval(NumberRuleInfo numberRuleInfo, SeqCurNum seqDesignPo, Integer needSize) {
        // 改for update
        SeqCurNum updatePo = seqCurNumService.getObjectById(seqDesignPo.getId());
        String nowVal = updatePo.getCurVal();
        long lastNumVal = Long.parseLong(nowVal);
        // 如果lastNumVal为0，赋初值
        long dataMax;
        if (lastNumVal < numberRuleInfo.getNumberStart()) {
            dataMax = numberRuleInfo.getNumberStart() + ((long) needSize - 1) * numberRuleInfo.getNumberStep();
        } else {
            dataMax = lastNumVal + (long) needSize * numberRuleInfo.getNumberStep();
        }
        log.info("getNumberEnd={}, getNumberCircle={}", numberRuleInfo.getNumberEnd(), numberRuleInfo.getNumberCircle());
        dataMax = NumberRuleHandler.getSeqNumberCurrentData(numberRuleInfo, dataMax);

        updatePo.setCurVal(dataMax + "");
        updatePo.setSeqLock(updatePo.getSeqLock() + 1);
        int u = seqCurNumService.updateObject(updatePo);
        if (u <= 0) {
            return null;
        }
        // 计算完dataMax，还原nowVal
        updatePo.setCurVal(nowVal);
        ThreadLocalVariables.set(numberRuleInfo,nowVal);
        return updatePo;
    }

    /**
     * 更新枚举结束值<p>
     * 将号段当前值放入线程变量ThreadLocalVariables中供后续生成序号递增使用<p>
     *
     * @param enumRuleInfo 枚举序列规则
     * @param seqDesignPo 序列设计对象
     * @param needSize 序号生成的数量
     * @return 返回号段对象
     */
    public SeqCurNum updateEnumEndval(EnumRuleInfo enumRuleInfo, SeqCurNum seqDesignPo, Integer needSize) {
        SeqCurNum updatePo = seqCurNumService.getObjectById(seqDesignPo.getId());

        int beforeIndex = enumRuleInfo.getEnumStore().indexOf(updatePo.getCurVal());
        int index = beforeIndex + needSize * enumRuleInfo.getNumberStep();

        if (index > enumRuleInfo.getEnumStore().size() - 1) {
            index = (index - enumRuleInfo.getEnumStore().size()) % (enumRuleInfo.getEnumStore().size());
        } else if (index == enumRuleInfo.getEnumStore().size() - 1) {
        } else if (index < 0) {
            index = 0;
        } else {
            index = index + enumRuleInfo.getNumberStep();
        }
        updatePo.setCurVal(enumRuleInfo.getEnumStore().get(index));
        updatePo.setSeqLock(updatePo.getSeqLock() + 1);
        int u = seqCurNumService.updateObject(updatePo);
        if (u <= 0) {
            return null;
        }
        ThreadLocalVariables.set(enumRuleInfo,beforeIndex);
        return updatePo;
    }


    /**
     * 获取数据库中回收池的序列
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class, timeout = 60)
    public List<String> getCallbackSeq(String seqDesignId , String rqDay , String tenantId, Integer needSize , LinkedBlockingQueue<Object> blockingQueue) {

//    	List<String> callbackSeqList = new ArrayList<String>();
        ////1.序列ID  日切  租户ID 查询 needSize
        Map map = new HashMap();
        map.put("seqDesignId", seqDesignId);
        map.put("rqDay", rqDay);
        map.put("tenantId", tenantId);
        List<SeqRecycleInfo> seqRecycleInfoList = new ArrayList<SeqRecycleInfo>();
        List<String> stringList= new ArrayList<>();

        try {
            seqRecycleInfoList = seqRecycleInfoService.getSeqForUpdate(map);
            if (seqRecycleInfoList.size() == 0 ){
                return stringList;
            }
            seqRecycleInfoService.deleteAttributeData(map);
            for (SeqRecycleInfo seqRecycleInfo : seqRecycleInfoList) {
                stringList.add(seqRecycleInfo.getRecycleCode());
                blockingQueue.put(seqRecycleInfo.getRecycleCode());
                //2.删除回收池序列
//                seqRecycleInfoService.delObjectById(seqRecycleInfo.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringList;
    }

    /**
     * 根据序列设计seq_code、tenant_id 为分布式锁<p>
     * 首先根据日期day和ruleId查找号段表是否存在，不存在则添加一条<p>
     * 对应数字规则rule,更新序列号段表起始值和终止值，更新成功将规则起始值放入线程变量ThreadLocalVariables#THREAD_LOCAL中
     * 其中key为规则对象、value为号段的起始值,起始值用于后续程序在内存中生成序号使用<p>
     *
     * @param numRule 设计数字的规则集合对象
     * @param day 日期
     * @param needSize 需要生成的数量
     * @param seqObj 序列设计对下你个
     * @author yzp
     * @return true 成功， false 失败
     */
    @Transactional(rollbackFor = Exception.class, timeout = 60)
    public boolean updateRuleCurVal(List<Rule> numRule, String day, Integer needSize, SeqObj seqObj) {
        Map<String, String> mapSeqDesign = new HashMap<>();
        mapSeqDesign.put("sequenceCode", seqObj.getSequenceCode());
        mapSeqDesign.put("tenantId", seqObj.getTenantId());
        seqDesignService.selectForUpdateBySeqCodeTenantTd(mapSeqDesign);
        String sequenceCode = seqObj.getSequenceCode() + SequenceConstant.DAY_SWITCH_SPLIT +
                day + SequenceConstant.TenantPostfix + seqObj.getTenantId();

        for (Rule seqInstanceRule : numRule) {
            //服务端 规则情况
            Map<String, String> mapCur = new HashMap<>();
            mapCur.put("seqInstanceRuleId", seqInstanceRule.getRuleId());
            mapCur.put("inDay", day);
            SeqCurNum seqCurNum = seqCurNumService.getObjectByCode(mapCur);
            if (Objects.isNull(seqCurNum)) {
                seqCurNum = new SeqCurNum();
                seqCurNum.setInDay(day);
                seqCurNum.setSeqInstanceRuleId(seqInstanceRule.getRuleId());
                seqCurNum.setTenantId(seqInstanceRule.getTenantId());
                seqCurNum.setSeqLock(0);
                seqCurNum.setCurVal("0");
                seqCurNumService.saveObject(seqCurNum);
            }

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            SeqCurNum updatePo;
            if (seqInstanceRule.getRuleType().equals(SequenceConstant.NUMBER_RULE)) {
                updatePo = this.updateNumEndval((NumberRuleInfo) seqInstanceRule.getRuleData(), seqCurNum, needSize);
                //异步调用告警接口
//                asyncTask.seqAlarmThreshold((NumberRuleInfo) seqInstanceRule.getRuleData(),needSize,seqCurNum.getCurVal(),seqObj);
            } else {
                updatePo = this.updateEnumEndval((EnumRuleInfo) seqInstanceRule.getRuleData(), seqCurNum, needSize);
            }
            stopWatch.stop();
            log.info("序列：{} , InstanceRuleId:{} 事务耗时:{}", sequenceCode, seqCurNum.getSeqInstanceRuleId(),
                    stopWatch.getTotalTimeMillis());
            if (updatePo == null) {
                log.warn("序列：{} , InstanceRuleId:{} 事务更新返回空", sequenceCode, seqCurNum.getSeqInstanceRuleId());
                return false;
            }
        }
        return true;
    }
}
