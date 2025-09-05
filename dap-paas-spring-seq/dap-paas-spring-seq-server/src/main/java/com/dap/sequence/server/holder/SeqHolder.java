package com.dap.sequence.server.holder;


import com.dap.sequence.api.core.AbstractSeqFlowEngine;
import com.dap.sequence.api.core.BaseRuleHandler;
import com.dap.sequence.api.dto.Rule;
import com.dap.sequence.server.rules.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Description: 序列存储持有对象
 * @Author: liu
 */
public class SeqHolder {

    /**
     * 序列存储map key:SeqDesignCode,不能唯一无法区分租户，所以需要SeqDesignCode+@+tenantId 进行区分
     */
    private static ConcurrentHashMap<String, BlockingQueue<Object>> map = new ConcurrentHashMap<>();

    /**
     *  设计序列 规则存储map key:SeqDesignId->规则Id唯一  ，无需租户区别
     */
    private static  ConcurrentHashMap<String,List<Rule>> rulesMap = new ConcurrentHashMap<>();

        /**
     * 序列引擎存储map  key:SeqDesignCode,不能唯一无法区分租户，所以需要SeqDesignCode+@+tenantId 进行区分
     */
    private static ConcurrentHashMap<String, AbstractSeqFlowEngine> engineMap = new ConcurrentHashMap<>();

    /**
     *序列规则处理器 key: Rule规则类型
     */
    private static ConcurrentHashMap<String, BaseRuleHandler> ruleHandlers = new ConcurrentHashMap<>();

    static {
        //init rule handler
        DateRuleHandler dateRuleHandler= new DateRuleHandler();
        EnumRuleHandler enumRuleHandler= new EnumRuleHandler();
        NumberRuleHandler numberRuleHandler= new NumberRuleHandler();
        StringRuleHandler stringRuleHandler= new StringRuleHandler();
        SpecialCharRuleHandler specialCharRuleHandler = new SpecialCharRuleHandler();
        ruleHandlers.put(dateRuleHandler.getRuleName(),dateRuleHandler);
        ruleHandlers.put(enumRuleHandler.getRuleName(),enumRuleHandler);
        ruleHandlers.put(numberRuleHandler.getRuleName(),numberRuleHandler);
        ruleHandlers.put(stringRuleHandler.getRuleName(),stringRuleHandler);
        ruleHandlers.put(specialCharRuleHandler.getRuleName(), specialCharRuleHandler);
    }

    /**
     * 记录已触发清理序列缓存的key
     * 内容格式：seqCode###回拨模式具体值@tenantId
     */
    private static CopyOnWriteArraySet<String> clearFlagSet = new CopyOnWriteArraySet<>();

    private SeqHolder(){

    }


    public static ConcurrentHashMap<String, BlockingQueue<Object>> getMap() {
        return map;
    }

    public static void setMap(ConcurrentHashMap<String, BlockingQueue<Object>> map) {
        SeqHolder.map = map;
    }

    public static ConcurrentHashMap<String, AbstractSeqFlowEngine> getEngineMap() {
        return engineMap;
    }

    public static void setEngineMap(ConcurrentHashMap<String, AbstractSeqFlowEngine> engineMap) {
        SeqHolder.engineMap = engineMap;
    }

    public static ConcurrentHashMap<String, List<Rule>> getRulesMap() {
        return rulesMap;
    }

    public static void setRulesMap(ConcurrentHashMap<String, List<Rule>> rulesMap) {
        SeqHolder.rulesMap = rulesMap;
    }

    public static ConcurrentHashMap<String, BaseRuleHandler> getRuleHandlers() {
        return ruleHandlers;
    }

    public static void setRuleHandlers(ConcurrentHashMap<String, BaseRuleHandler> ruleHandlers) {
        SeqHolder.ruleHandlers = ruleHandlers;
    }

    public static AbstractSeqFlowEngine getEngineBySequenceCode(String sequenceCode){
        if(engineMap!=null){
          return  engineMap.get(sequenceCode);
        }
        return null;
    }

    public static Optional<BaseRuleHandler> getRuleHandlerByName(String sequenceCode) {
        if (ruleHandlers != null) {
            return Optional.ofNullable(ruleHandlers.get(sequenceCode));
        }
        return Optional.empty();
    }

    public static BlockingQueue<Object> getSequenceMapBySequenceCode(String sequenceCode){
        if(map!=null){
            return  map.get(sequenceCode);
        }
        return null;
    }

    public static ConcurrentHashMap<String, BlockingQueue<Object>> getSequenceMap() {
        return map == null ? new ConcurrentHashMap<>() : map;
    }


    public static BlockingQueue<Object> putSequenceMap(String sequenceCode,BlockingQueue<Object> blockingQueue){
        if(map!=null){
            return  map.put(sequenceCode,blockingQueue);
        }
        return null;
    }

    public static void clear() {
        rulesMap.clear();
    }

    public static CopyOnWriteArraySet<String> getClearFlagSet() {
        return clearFlagSet;
    }

    public static void setClearFlagSet(CopyOnWriteArraySet<String> clearFlagSet) {
        SeqHolder.clearFlagSet = clearFlagSet;
    }
}
