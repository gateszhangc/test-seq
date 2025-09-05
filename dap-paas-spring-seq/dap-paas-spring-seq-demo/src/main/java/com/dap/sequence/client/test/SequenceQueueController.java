package com.dap.sequence.client.test;

import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.obj.SeqObj;
import com.dap.sequence.client.core.SequenceQueue;
import com.dap.sequence.client.core.SequenceQueueFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 查看本地缓存
 *
 * @author yzp
 * @date 2024/7/18 11:21
 */
@RestController
@RequestMapping("/queryLocalSequence")
public class SequenceQueueController {

    /**
     * 查询本地缓存序列临时缓存
     *
     */
    @GetMapping(value = "/getSequenceQueueMap/cacheBlockingQueue/{seqCode}/{day}")
    public Object getSequenceQueueMap(@PathVariable("seqCode") String seqCode,@PathVariable("day") String day) {
        ConcurrentHashMap<String, SequenceQueue> SEQUENCE_QUEUE_MAP = SequenceQueueFactory.SEQUENCE_QUEUE_MAP;
        SequenceQueue sequenceQueue = SEQUENCE_QUEUE_MAP.get(seqCode+ SequenceConstant.DAY_SWITCH_SPLIT+day);
        BlockingQueue<Object> cacheBlockingQueue = sequenceQueue.getCacheBlockingQueue();
        return cacheBlockingQueue;
    }

    /**
     * 查询本地缓存序列缓存集合quenList
     * @param seqCode
     * @param day
     * @return
     */
    @GetMapping(value = "/getSequenceQueueMap/quenList/{seqCode}/{day}")
    public Object quenList(@PathVariable("seqCode") String seqCode,@PathVariable("day") String day) {
        ConcurrentHashMap<String, SequenceQueue> SEQUENCE_QUEUE_MAP = SequenceQueueFactory.SEQUENCE_QUEUE_MAP;
        SequenceQueue sequenceQueue = SEQUENCE_QUEUE_MAP.get(seqCode+ SequenceConstant.DAY_SWITCH_SPLIT+day);
        BlockingQueue<BlockingQueue<Object>> quenList = sequenceQueue.getQuenList();
        return quenList;
    }

    /**
     * 查询本地缓存序列缓存集合所有key
     * @return
     */
    @GetMapping(value = "/getSequenceQueueMap/allKey")
    public Object allKey() {
        ConcurrentHashMap<String, SequenceQueue> SEQUENCE_QUEUE_MAP = SequenceQueueFactory.SEQUENCE_QUEUE_MAP;
        Set<String> allKeys = SEQUENCE_QUEUE_MAP.keySet();
        // 将 Set 转换为 List
        List<String> sortedList = new ArrayList<>(allKeys);
        // 对 List 进行排序
        Collections.sort(sortedList);
        return sortedList;
    }

    /**
     * 查询本地缓存序列是否加载第二天序列
     *
     */
    @GetMapping(value = "/getSequenceQueueMap/hasNextDaySeq/{seqCode}/{day}")
    public boolean hasNextDaySeq(@PathVariable("seqCode") String seqCode,@PathVariable("day") String day) {
        ConcurrentHashMap<String, SequenceQueue> SEQUENCE_QUEUE_MAP = SequenceQueueFactory.SEQUENCE_QUEUE_MAP;
        SequenceQueue sequenceQueue = SEQUENCE_QUEUE_MAP.get(seqCode+ SequenceConstant.DAY_SWITCH_SPLIT+day);
        Boolean hasNextDaySeq = sequenceQueue.getHasNextDaySeq();
        return hasNextDaySeq;
    }

    /**
     * 查询本地缓存序列是否已清理本地序列缓存
     *
     */
    @GetMapping(value = "/getSequenceQueueMap/hasClearSequenceQueueMap/{seqCode}/{day}")
    public boolean hasClearSequenceQueueMap(@PathVariable("seqCode") String seqCode,@PathVariable("day") String day) {
        ConcurrentHashMap<String, SequenceQueue> SEQUENCE_QUEUE_MAP = SequenceQueueFactory.SEQUENCE_QUEUE_MAP;
        SequenceQueue sequenceQueue = SEQUENCE_QUEUE_MAP.get(seqCode+ SequenceConstant.DAY_SWITCH_SPLIT+day);
        AtomicBoolean hasClearSequenceQueueMap = sequenceQueue.getHasClearSequenceQueueMap();
        return hasClearSequenceQueueMap.get();
    }

    /**
     * 查询本地缓存序列设计的回拨模式
     *
     * @param seqCode
     * @return
     */
    @GetMapping(value = "/getSequenceDesignMap/callbackMode/{seqCode}")
    public String getSequenceDesignMap(@PathVariable("seqCode") String seqCode){
        ConcurrentHashMap<String, SeqObj> sequenceDesignMap = SequenceQueueFactory.SEQUENCE_DESIGN_MAP;
        SeqObj seqObj = sequenceDesignMap.get(seqCode);
        return seqObj.getCallbackMode();
    }


}
