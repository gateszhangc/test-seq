package com.dap.sequence.client.core;


import com.dap.sequence.api.constant.SeqDesignCallbackModeEnum;
import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.SeqConsumer;
import com.dap.sequence.api.dto.SeqTransferDto;
import com.dap.sequence.api.exception.ExceptionEnum;
import com.dap.sequence.api.exception.SequenceException;
import com.dap.sequence.api.obj.SeqObj;
import com.dap.sequence.api.util.DateUtils;
import com.dap.sequence.client.properties.CusSeq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.dap.sequence.api.constant.SeqDesignCallbackModeEnum.*;
import static com.dap.sequence.api.constant.SequenceConstant.DAY_SWITCH_SPLIT;

/**
 * @author zpj
 */
public class SequenceQueueFactory {
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceQueueFactory.class);

    //序列对象缓存
    public static final ConcurrentHashMap<String, SequenceQueue> SEQUENCE_QUEUE_MAP = new ConcurrentHashMap<>();
    
  //序列设计器对象缓存
    public static final ConcurrentHashMap<String, SeqObj> SEQUENCE_DESIGN_MAP = new ConcurrentHashMap<>();

    /**
     *
     */
    private static ExecutorService executor = new ThreadPoolExecutor(10, 100,
            10, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(20), new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 百分比100%
     */
    private static final int PERCENTAGE = 100;


    //序列是否启用缓存
    private boolean useSequenceCache = true;
    // 队列最大容量
    private int sequenceQueueCapacity = 100;
    
    //缓存号段数量 ，默认双号段 压测时可适当调大一点
    private int bufferCount=2;
    
    // 从队列中获取序列的超时时间
    private int timeout;

    /**
     * 重试次数
     */
    private int retryTimes = 2;

    private final Lock lock = new ReentrantLock();

    /**
     * 缓存队列清理范围数量
     */
    private int retainNum;

    /**
     * 客户端缓存补仓阈值
     */
    private int clientCacheThreadHold;


    public Object getBaseSequence(String seqCode, SeqConsumer seqConsumer) {
    	SeqObj seqDesignPo = getOrFetchSeqDesignFromServer(seqCode,seqConsumer);

    	
    	//是否回拨序列： 0 全局序列 1：按日回拨 （到下一日时序号中数字从头开始）  2：按月回拨 3：按年回拨
    	if(Objects.equals(seqDesignPo.getCallbackMode(), CALLBACK_MODE_DAY.getCode())) {
    		seqCode = seqCode + SequenceConstant.DAY_SWITCH_SPLIT +  DateUtils.convertToString(new Date(), CALLBACK_MODE_DAY.getFormat());
    	}else if(Objects.equals(seqDesignPo.getCallbackMode(), CALLBACK_MODE_MONTH.getCode())) {
    		seqCode = seqCode + SequenceConstant.DAY_SWITCH_SPLIT +  DateUtils.convertToString(new Date(), CALLBACK_MODE_MONTH.getFormat());
    	}else if(Objects.equals(seqDesignPo.getCallbackMode(), CALLBACK_MODE_YEAR.getCode())) {
    		seqCode = seqCode + SequenceConstant.DAY_SWITCH_SPLIT +  DateUtils.convertToString(new Date(), CALLBACK_MODE_YEAR.getFormat());
    	}else {
    		seqCode = seqCode + SequenceConstant.DAY_SWITCH_SPLIT + SequenceConstant.DAY_BASE;
    	}

    	return getSequence(seqCode, seqConsumer);
    }

    /**
     * @Author zchuber
     * @Description  先从SEQUENCE_DESIGN_MAP中获取序号配置，如果没有则从服务端加载
     * @Date 4:00 PM 2/22/2025
     * @Param [seqCode, seqConsumer]
     * @return com.dap.sequence.api.obj.SeqObj
     **/
    public SeqObj getOrFetchSeqDesignFromServer(String seqCode, SeqConsumer seqConsumer) {
        SeqObj seqDesignPo=SEQUENCE_DESIGN_MAP.get(seqCode);
        if(seqDesignPo == null) {
            seqDesignPo = seqConsumer.getSeqDesignObj(seqCode);
            if(seqDesignPo == null) {
                LOGGER.error("获取序列：{}规则失败!!!" , seqCode);
                //return null;
                throw new SequenceException(ExceptionEnum.SEQ_NOT_FOUND);
            }
            SEQUENCE_DESIGN_MAP.put(seqCode, seqDesignPo);
        }
        return seqDesignPo;
    }

    /**
     * 获取序列
     *
     * @param seqCode     序列编号
     * @param seqConsumer 序列类型
     * @return
     * @description
     */

    public Object getSequence(String seqCode, SeqConsumer seqConsumer) {
        for (int i = 0; i < retryTimes; i++) {
            try {
                Object seq = getSequenceFromCache(seqCode, seqConsumer);
                if(Objects.isNull(seq)) {
                    continue;
                }
                return seq;
            } catch (Exception e) {
                LOGGER.error("获取序列报错.error:{}", e.getMessage(), e);
            }
        }
        throw new SequenceException("获取序列报错");
    }

    /**
     * 从队列中获取序列
     *
     * @param seqCode     序列编号
     * @param seqConsumer
     * @return
     * @throws Exception
     * @description
     */
    private Object getSequenceFromCache(String seqCode, SeqConsumer seqConsumer) {
        SequenceQueue sequenceQueue = SEQUENCE_QUEUE_MAP.get(seqCode);
        if (isQueueEmpty(sequenceQueue)) {
            LOGGER.warn("sequenceQueue 不存在从服务端获取的,seqCode:{} ", seqCode);
            return getFirstSeqFormServer(seqCode, seqConsumer);
        } else {
            return getSequenceFromLocalCache(seqCode, seqConsumer, sequenceQueue);
        }
    }

    private synchronized boolean isQueueEmpty(SequenceQueue sequenceQueue) {
        return null == sequenceQueue || CollectionUtils.isEmpty(sequenceQueue.getCacheBlockingQueue());
    }

    /**
     * 获取本地缓存中序列
     *
     * @param seqCode
     * @param sequenceQueue
     * @param seqConsumer
     * @throws SequenceException
     */
    public Object getSequenceFromLocalCache(String seqCode, SeqConsumer seqConsumer, SequenceQueue sequenceQueue) {
        if (isQueueEmpty(sequenceQueue)) {
            LOGGER.warn("1.当前缓存队列sequenceQueue(is null {})中没有序列seqCode={}", sequenceQueue == null, seqCode);
            return null;
        }
        CusSeq seq = sequenceQueue.getCacheSeq();
        asyncMakeUpWare(seqCode,seqConsumer,sequenceQueue);
        if (!sequenceQueue.setNextDaySeqIfAbsent()) {
            SeqObj seqDesignPo = SEQUENCE_DESIGN_MAP.get(seqCode.split(SequenceConstant.DAY_SWITCH_SPLIT)[0]);
            if (null != seqDesignPo && Objects.equals(seqDesignPo.getCallbackMode(), CALLBACK_MODE_DAY.getCode())) {
                //按日回拨才预加载下一日序列
                String[] seqArray = seqCode.split(SequenceConstant.DAY_SWITCH_SPLIT);
                if (seqArray.length == 2 && !SequenceConstant.DAY_BASE.equals(seqArray[1])) {
                    String baseCode = seqArray[0];
                    String day = seqArray[1];
                    //获取明天日期
                    String seqCodeNext = baseCode + SequenceConstant.DAY_SWITCH_SPLIT + DateUtils.getNextDate(day);
                    //if(sequenceSet.putIfAbsent(seqCodeNext, 0) == null) {
                    //异步获取明天的序列
                    LOGGER.info("从远程服务器获取下一日序列 seqCodeNext={}", seqCodeNext);
                    makeUpWarehuseNextDay(seqCodeNext, sequenceQueue, seqConsumer);
                }
            }
        }
        // 异步清理序列本地缓存
        clearSequenceQueueMap(sequenceQueue,seqCode);

        return seq.getSeq();
    }

    /**
     * 异步补充序号
     *
     * @param seqCodeDay 序列设计编码###日切模式具体值
     * @param seqConsumer 序列消费者对象
     * @param sequenceQueue 序列缓存对象
     * @author yzp
     */
    private void asyncMakeUpWare(String seqCodeDay,SeqConsumer seqConsumer,SequenceQueue sequenceQueue) {
        BlockingQueue<Object> queue = sequenceQueue.getCacheBlockingQueue();
        int queueSize = Optional.ofNullable(queue).map(Collection::size).orElse(0);
        boolean isMakeUp = sequenceQueue.getPublishCount() * clientCacheThreadHold > queueSize * PERCENTAGE;
        if (sequenceQueue.getQuenList().size() < bufferCount && isMakeUp) {
            LOGGER.info("开始补仓seqCode={} , queueSize={}", seqCodeDay, queueSize);
            makeUpWarehuse(seqCodeDay, sequenceQueue, seqConsumer);
        }
    }

    /**
     * 清理本地缓存序列存储{@link SequenceQueueFactory#SEQUENCE_QUEUE_MAP}中
     * 时间类型的回拨模式包含：日切模式{@link SeqDesignCallbackModeEnum#CALLBACK_MODE_DAY}、
     * 月切模式{@link SeqDesignCallbackModeEnum#CALLBACK_MODE_MONTH}、
     * 年切模式{@link SeqDesignCallbackModeEnum#CALLBACK_MODE_YEAR} <p>
     * 及当前时间T前后范围 {@link SequenceQueueFactory#retainNum}以外的缓存序列
     * @param sequenceQueue 序列缓存对象
     * @param seqCodeDay seqCode###回拨模式具体值
     * @author yzp
     */
    private void clearSequenceQueueMap(SequenceQueue sequenceQueue,String seqCodeDay) {
        try {
            if (!sequenceQueue.setHasClearSequenceQueueMap()) {
                // 获取当前查询的回拨模式时间值
                String[] seqArray = seqCodeDay.split(DAY_SWITCH_SPLIT);
                int dayParam;
                if (seqArray.length == 2) {
                    if (CALLBACK_MODE_GLOBAL.getFormat().equals(seqArray[1])) {
                        LOGGER .debug("清理的回拨模式={}，无需清理序列缓存的key={}", seqArray[1], seqCodeDay);
                        return;
                    } else {
                        dayParam = Integer.parseInt(seqArray[1]);
                    }
                } else {
                    LOGGER.error("序列编号 {} 回拨模式day未找到!!! 无需清理", seqCodeDay);
                    sequenceQueue.getHasClearSequenceQueueMap().compareAndSet(true, false);
                    return;
                }
                // 获取当前查询的序列编码
                String seqCodeParam = extractSeqCode(seqCodeDay);
                // 开始清理缓存序列
                executor.execute(() -> {
                    String callbackMode = SEQUENCE_DESIGN_MAP.get(seqCodeParam).getCallbackMode();
                    if ((CALLBACK_MODE_DAY.getCode().equals(callbackMode) ||
                            CALLBACK_MODE_MONTH.getCode().equals(callbackMode) ||
                            CALLBACK_MODE_YEAR.getCode().equals(callbackMode))) {
                        // 获取当前序列设计编号的所有缓存序列key
                        List<String> seqCodeAllKeys = SEQUENCE_QUEUE_MAP.keySet().stream()
                                .filter(key -> seqCodeParam.equals(extractSeqCode(key)))
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
                                LOGGER.info("客户端获取序列{}时触发清理前后范围{}周期以外的对应缓存序列key:{}", seqCodeDay, retainNum, key);
                                SEQUENCE_QUEUE_MAP.remove(key);
                            }
                        });
                    }
                });
            }
        } catch (Throwable e) {
            LOGGER.error("清理本地序列缓存异常,重置清理本地缓存序列标识", e);
            sequenceQueue.getHasClearSequenceQueueMap().compareAndSet(true, false);
        }
    }

    /**
     * 提取日切、月切、年切的数值
     *
     * @param key
     * @return
     */
    private int extractSeqDay(String key) {
        return Integer.parseInt(key.split(DAY_SWITCH_SPLIT)[1]);
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
     * 首次从远程获取序列数据
     *
     * @param seqCode
     * @param seqConsumer
     * @return
     * @throws InterruptedException
     * @throws SequenceException
     */
    public synchronized Object getFirstSeqFormServer(String seqCode, SeqConsumer seqConsumer)  {
        LOGGER.info("开始远程获取序列号... , seqCode = {}", seqCode);
        SequenceQueue sequenceQueue = SEQUENCE_QUEUE_MAP.get(seqCode);
        if (sequenceQueue != null && !isQueueEmpty(sequenceQueue)) {
            LOGGER.info("其他线程已获取到序列seqCode ={}，从本地缓存中获取...", seqCode);
            return getSequenceFromLocalCache(seqCode, seqConsumer, sequenceQueue);
        }
        sequenceQueue = getSeqArrayFormServer(seqCode, seqConsumer, sequenceQueue);

        //5.检查缓存状态I
        if (this.checkQueueStatus(seqCode)) {
            BlockingQueue<Object> queue = sequenceQueue.getCacheBlockingQueue();
            return queue.poll();
        } else {
            LOGGER.warn("序列未就绪");
        }
        return null;
    }


    /**
     * 获取远程服务端一组序列
     *
     * @param seqCode
     * @param seqConsumer
     * @param sequenceQueue
     * @return
     * @throws SequenceException
     */
    public SequenceQueue getSeqArrayFormServer(String seqCode, SeqConsumer seqConsumer, SequenceQueue sequenceQueue) throws SequenceException {
        if (seqConsumer == null) {
            LOGGER.warn("序列配置有误，序列编号：{}", seqCode);
            throw new SequenceException("序列配置有误，序列编号：{}", seqCode);
        }
        //1.远程获取序列数据
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        SeqTransferDto o = seqConsumer.getSeqFormServer(seqCode , null);
        if (o == null) {
            LOGGER.warn("从服务端获取序列为空，序列编号：{}", seqCode);
            throw new SequenceException(ExceptionEnum.GET_SEQ_QUEUE_ERROR);
        }
       List<Object> list = o.getList();
        stopWatch.stop();
        Object start = Optional.ofNullable(list)
                .filter(objects -> !CollectionUtils.isEmpty(objects))
                .map(objects -> objects.get(0))
                .orElseThrow(() -> new SequenceException(ExceptionEnum.GET_SEQ_QUEUE_ERROR));

        LOGGER.info("从序列服务器获取{}序列耗时：{} , seqSize={} , start : {}", seqCode, stopWatch.getTotalTimeMillis(), list.size(), start);
        if (list == null || list.size() == 0) {
            LOGGER.error("从序列服务器获取{}序列失败", seqCode);
            throw new SequenceException("从序列服务获取[" + seqCode + "]序列失败");
        }
        /*        boolean setStatus = set.add(startSeq);
        
        if (!setStatus) {
            LOGGER.error("setSize = {} , seq start 重复 : {}", list.size(), startSeq);
        }
        LOGGER.info("setSize = {} , seqSize={} , start : {}", set.size(), list.size(), startSeq);*/

        if (sequenceQueue == null) {
            //2.创建序列缓存对象
            sequenceQueue = new SequenceQueue(list, sequenceQueueCapacity, timeout);
            //4.放入缓存中
            SEQUENCE_QUEUE_MAP.put(seqCode, sequenceQueue);
        } else {
            sequenceQueue.getQuenList().add(new ArrayBlockingQueue<Object>(list.size(), true, list));
        }
        //3.保存序列类型
        sequenceQueue.setNum(o.isNum());
        sequenceQueue.setPublishCount(list.size());
        //设置队列的远程填充规则
        // sequenceQueue.setEnabledFeign(enabledFeign);
        //sequenceQueue.setProviderClient(providerClient);
        return sequenceQueue;
    }

    /**
     * 序列补仓
     *
     * @param seqCode
     * @param sequenceQueue
     * @param seqConsumer
     */
    public void makeUpWarehuse(final String seqCode, final SequenceQueue sequenceQueue, final SeqConsumer seqConsumer) {
        executor.execute(() -> {
            try {
                getSeqArrayFormServer(seqCode, seqConsumer, sequenceQueue);
            } catch (Exception e) {
                LOGGER.error("补仓异常", e);
            }
        });
    }


    /**
     * 序列补仓 -补第二天的 如果补仓失败重置补仓状态
     *
     * @param seqCode
     * @param sequenceQueue
     * @param seqConsumer
     */
    public void makeUpWarehuseNextDay(final String seqCode, final SequenceQueue sequenceQueue, final SeqConsumer seqConsumer) {
        LOGGER.info("开始补仓第二天seqCode={}", seqCode);
        executor.execute(() -> {
            try {
                getSeqArrayFormServer(seqCode, seqConsumer, null);
            } catch (Exception e) {
                LOGGER.error("补仓第二天异常", e);
                //重置补仓状态
                sequenceQueue.setHasNextDaySeq(false);
            }
        });
    }

    private Boolean checkQueueStatus(String seqCode) {
        SequenceQueue sequenceQueue = SEQUENCE_QUEUE_MAP.get(seqCode);
        return null != sequenceQueue;
    }

    private Object getSequenceFromQueue(String seqCode, SeqConsumer generator) {
        Object seq = null;
//        try {
//            BlockingQueue<Object> queue = getQueue(seqCode, sequenceType, dataType, map, generator, capacity);
//            seq = queue.poll(timeout, TimeUnit.MILLISECONDS);
//            if (seq == null) {
//                throw new SequenceException(ErrorCodeConstants.SEQ_TIMEOUT, seqCode);
//            }
//        } catch (InterruptedException e) {
//            LOGGER.error("获取序列失败", e);
//        } catch (SequenceException e) {
//            LOGGER.error("获取序列失败", e);
//
//        }
        return seq;
    }

    public void setUseSequenceCache(boolean useSequenceCache) {
        this.useSequenceCache = useSequenceCache;
    }

	public void setBufferCount(int bufferCount) {
		this.bufferCount = bufferCount;
	}
	public void setSequenceQueueCapacity(int sequenceQueueCapacity) {
        this.sequenceQueueCapacity = sequenceQueueCapacity;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setRetainNum(int retainNum) {
        this.retainNum = retainNum;
    }

    public void setClientCacheThreadHold(int clientCacheThreadHold) {
        this.clientCacheThreadHold = clientCacheThreadHold;
    }
}
