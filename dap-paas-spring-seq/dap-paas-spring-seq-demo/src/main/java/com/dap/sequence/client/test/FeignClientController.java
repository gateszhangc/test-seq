package com.dap.sequence.client.test;

import com.dap.sequence.api.core.SeqConsumer;
import com.dap.sequence.api.dto.SeqCallbackDto;
import com.dap.sequence.api.exception.ResultResponse;
import com.dap.sequence.api.exception.SequenceException;
import com.dap.sequence.api.util.DateUtils;
import com.dap.sequence.client.core.SequenceQueue;
import com.dap.sequence.client.core.SequenceQueueFactory;
import com.dap.sequence.client.service.SequenceGeneratorClient;
import com.dap.sequence.client.service.impl.SequenceGeneratorClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther zpj
 * @descriptions
 * @Date 2021-09-10 15:15
 */

@RestController
public class FeignClientController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceGeneratorClientImpl.class);

    private final Set<String> set = new LinkedHashSet<>();
    private final List<String> list = new ArrayList<>();
    private final List<String> errList = new LinkedList<>();

    
    
    @Autowired(required = false)
    private SequenceGeneratorClient sequenceGeneratorClient;

    @Autowired
    private SeqConsumer seqConsumer;

    private static final Logger logger = LoggerFactory.getLogger(SequenceGeneratorClientImpl.class);

    /**
     * 雪花算法获取全局唯一id
     *
     * @return String
     */
    @RequestMapping(value = "getSnowFlake", method = RequestMethod.GET)
    public String getSnowFlake() throws InterruptedException {
        return sequenceGeneratorClient.getSnowFlake();
    }
    @RequestMapping(value = "getSnowFlakeQps", method = RequestMethod.GET)
    public Map getSnowFlakeQps(Integer count) throws InterruptedException {
    	ExecutorService executor = new ThreadPoolExecutor(20, 50, 10, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000000));
        
    	int c = 10000;
    	int r = 100;
    	if(count != null ) {
    		c = count / r;
    	}
    	
    	StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        AtomicInteger atomicInteger = new AtomicInteger(0);
        logger.error("雪花算法，开始计算QPS，总数量：{}" ,  c * r);
        Set<String> set = new HashSet<String>();
        final CountDownLatch cdl = new CountDownLatch(c);
    	for (int i = 0; i < c; i++) {
    		executor.submit(() -> {
    			for (int j = 0; j < r; j++) {
    				String sequence = sequenceGeneratorClient.getSnowFlake();
    				
    				if(!set.add(sequence)) {
    					logger.error("雪花算法，获取到的序号：{}，重复了" ,  sequence);
    					
    					atomicInteger.getAndIncrement();
    				}
				}
    			cdl.countDown();
            });
		}
    	cdl.await();
        stopWatch.stop();
        
        long qps =   c * r * 1000/stopWatch.getTotalTimeMillis();
        
        String info = "获取序号 雪花算法, 数量："+(c * r)+",总耗时="+stopWatch.getTotalTimeMillis()+"ms , QPS:"+qps+" , 错误重复数："+atomicInteger.get()+"";
        logger.info(info);
        
        Map<String , String> map = new HashMap<String, String>();
        map.put("code", "200");
        map.put("data", info);
        return map;
    }

    /**
     * 测试序列
     *
     * @param seqCode seqCode
     * @return String
     */
    @RequestMapping(value = "getSequenceCode/{seqCode}", method = RequestMethod.GET)
    public String test(@PathVariable("seqCode") String seqCode) throws Exception {

        String sequence = sequenceGeneratorClient.getStringSequence(seqCode);
        return sequence;
    }

    @RequestMapping(value = "getStringSequence/{seqCode}", method = RequestMethod.GET)
    public Map getStringSequence(@PathVariable("seqCode") String seqCode) throws Exception {

        String sequence = sequenceGeneratorClient.getStringSequence(seqCode);

        Map<String , String> map = new HashMap<String, String>();
        map.put("code", "200");
        map.put("data", sequence);
        return map;
    }
    
    @RequestMapping(value = "getStringSequenceQps/{seqCode}", method = RequestMethod.GET)
    public Map getStringSequenceQps(@PathVariable("seqCode") String seqCode , @PathParam("count") Integer count) throws Exception {

        ExecutorService executor = new ThreadPoolExecutor(20, 50, 10, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000000));
        
    	int c = 10000;
    	int r = 100;
    	if(count != null ) {
    		c = count / r;
    	}
    	
    	StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        AtomicInteger atomicInteger = new AtomicInteger(0);
        logger.error("序号：{}，开始计算QPS，总数量：{}" , seqCode , c * r);
        Set<String> set = new HashSet<String>();
        final CountDownLatch cdl = new CountDownLatch(c);
    	for (int i = 0; i < c; i++) {
    		executor.submit(() -> {
    			for (int j = 0; j < r; j++) {
    				String sequence = sequenceGeneratorClient.getStringSequence(seqCode);
    				if(!set.add(sequence)) {
    					logger.error("序号：{}，获取到的序号：{}，重复了" , seqCode , sequence);
    					
    					atomicInteger.getAndIncrement();
    				}
				}
    			cdl.countDown();
            });
		}
    	cdl.await();
        stopWatch.stop();
        
        long qps =   c * r * 1000/stopWatch.getTotalTimeMillis();
        
        String info = "获取序号 code="+seqCode+", 数量："+(c * r)+",总耗时="+stopWatch.getTotalTimeMillis()+"ms , QPS:"+qps+" , 错误重复数："+atomicInteger.get()+"";
        logger.info(info);
        
        Map<String , String> map = new HashMap<String, String>();
        map.put("code", "200");
        map.put("data", info);
        return map;
    }

    /**
     * 占位符示例：全局序号
     *
     * @param seqCode seqCode
     * @return String
     */
    @RequestMapping(value = "getPlaceholderSequenceCode/{seqCode}/{place}", method = RequestMethod.GET)
    public String getPlaceholderSequenceCode(@PathVariable("seqCode") String seqCode, @PathVariable("place") String place) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String seq = sequenceGeneratorClient.getStringSequence(seqCode, place);
        stopWatch.stop();
        LOGGER.warn("获取序号【{}】时间消耗：{}", seq, stopWatch.getTotalTimeMillis());
        return seq;
    }

    @RequestMapping(value = "getBigIntegerSequence/{seqCode}", method = RequestMethod.GET)
    public Object getBigIntegerSequence(@PathVariable("seqCode") String seqCode) throws Exception {
        //   logger.info("test");
        BigInteger sequence = sequenceGeneratorClient.getBigIntegerSequence(seqCode);
        return sequence;
    }

    @GetMapping(value = "getDaySwitchSequence/{seqCode}")
    public String getDaySwitchSequence(@PathVariable("seqCode") String seqCode) throws Exception {
        long strTime = System.currentTimeMillis();
        LOGGER.info("strTime====>{}", strTime);
        String sequence = sequenceGeneratorClient.getDaySwitchSequence(seqCode, new Date());
        boolean setStatus = set.add(sequence);

        if (!setStatus) {
            errList.add(sequence);
            throw new Exception("重复序列：" + sequence);
        }
        if (null==sequence) {
            return "我是空值";
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("endTime====>{}", endTime);
        return sequence;
    }


    /**
     * 占位符示例：日切序号
     *
     * @param seqCode seqCode
     * @return String
     */
    @GetMapping(value = "getPlaceholderDaySwitchSequence/{seqCode}")
    public String getPlaceholderDaySwitchSequence(@PathVariable("seqCode") String seqCode) throws Exception {
        long strTime = System.currentTimeMillis();
        LOGGER.info("strTime====>{}", strTime);
        String[] place = new String[]{"dddddd"};
        String sequence = sequenceGeneratorClient.getDaySwitchSequence(seqCode, new Date(),place);
        boolean setStatus = set.add(sequence);

        if (!setStatus) {
            errList.add(sequence);
            throw new Exception("重复序列：" + sequence);
        }
        if (null==sequence) {
            return "我是空值";
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("endTime====>{}", endTime);
        return sequence;
    }



    /**
     * 日切接口，不传日期则报错
     *
     * @param seqCode
     * @param date
     * @return
     * @throws Exception
     * @author yzp
     */
    @RequestMapping(value = "getDaySwitchSequence/localDate/{seqCode}", method = RequestMethod.GET)
    public String getDaySwitchSequence(@PathVariable("seqCode") String seqCode,@RequestParam String date) throws Exception {
        Date date1 = null;
        if(StringUtils.hasText(date)){
            date1 = DateUtils.converToDate(date, DateUtils.DATE_FORMAT);
        }
        String sequence = sequenceGeneratorClient.getDaySwitchSequenceRequiredDate(seqCode, date1);
        return sequence;
    }


    /**
     * 占位符示例：日切序号
     *
     * @param seqCode
     * @param date
     * @return
     * @throws Exception
     * @author yzp
     */
    @RequestMapping(value = "getPlaceholderDaySwitchSequence/localDate/{seqCode}", method = RequestMethod.GET)
    public String getPlaceholderDaySwitchSequence(@PathVariable("seqCode") String seqCode,@RequestParam String date) throws Exception {
        Date date1 = null;
        if(StringUtils.hasText(date)){
            date1 = DateUtils.converToDate(date, DateUtils.DATE_FORMAT);
        }
        String[] place = new String[]{"dddddd","yyyyMMdd"};
        String sequence = sequenceGeneratorClient.getDaySwitchSequenceRequiredDate(seqCode, date1,place);
        return sequence;
    }

    @RequestMapping(value = "getDaySwitchSequenceDate/{seqCode}", method = RequestMethod.GET)
    public String getDaySwitchSequenceDate(@PathVariable("seqCode") String seqCode, @RequestParam String date) throws Exception {
        long strTime = System.currentTimeMillis();
        LOGGER.info("strTime====>{}", strTime);
        Date date1 = DateUtils.converToDate(date, DateUtils.DATE_FORMAT);
        String sequence = sequenceGeneratorClient.getDaySwitchSequence(seqCode, date1);
        boolean setStatus = set.add(sequence);
        // boolean listStatus = list.add(sequence);

        //if (!setStatus) {
        //    errList.add(sequence);
        //    throw new Exception("重复序列：" + sequence + "");
        //}
        if (null ==sequence) {
            return "我是空值";
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("endTime====>{}", endTime);
        return sequence;
    }


    @RequestMapping(value = "getDaySwitchSequenceRepeat/{seqCode}", method = RequestMethod.GET)
    public String getDaySwitchSequenceRepeat(@PathVariable("seqCode") String seqCode) throws Exception {
        long startTime = System.currentTimeMillis();
        LOGGER.info("startTime====>{}", startTime);
        String sequence = sequenceGeneratorClient.getDaySwitchSequence(seqCode, new Date());
        boolean setStatus = set.add(sequence);

        if (!setStatus) {
            errList.add(sequence);
            LOGGER.error(seqCode + "重复序列：" + sequence);
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("endTime====>{}", endTime);
        return sequence;
    }

    @RequestMapping(value = "getSequenceTest/{seqCode}", method = RequestMethod.GET)
    public String getSequenceTest(@PathVariable("seqCode") String seqCode) throws Exception {
        return System.currentTimeMillis() + "##" + seqCode;
    }

    @RequestMapping(value = "getError", method = RequestMethod.GET)
    public List<String> test() throws Exception {
        LOGGER.info("list.size:{}", list.size());
        LOGGER.info("set.size:{}", set.size());
        LOGGER.info("erroeSize:{}", this.errList.size());
        return this.errList;
    }


    @RequestMapping(value = "callback", method = RequestMethod.GET)
    public String callback() throws Exception{
        ConcurrentHashMap<String, SequenceQueue> SEQUENCE_QUEUE_MAP = SequenceQueueFactory.SEQUENCE_QUEUE_MAP;
        for (String seqCode : SEQUENCE_QUEUE_MAP.keySet()) {
            SequenceQueue sequenceQueue = SEQUENCE_QUEUE_MAP.get(seqCode);
            if (sequenceQueue != null && sequenceQueue.getCacheBlockingQueue() != null && !sequenceQueue.getCacheBlockingQueue().isEmpty()) {
                SeqCallbackDto seqCallbackDto = new SeqCallbackDto();
                seqCallbackDto.setSequenceCode(seqCode);
                List<Object> callbackNumQueue = new ArrayList<>(sequenceQueue.getCacheBlockingQueue());
                seqCallbackDto.setCallbackNumQueue(callbackNumQueue);
                seqConsumer.clientCacheQueueCallback(seqCallbackDto);
            }
        }
        return "";
    }


    @RequestMapping(value = "getSequenceCodes", method = RequestMethod.GET)
    public String getSequenceCodes() {
        String sequence = sequenceGeneratorClient.getStringSequence("ACCOUNT");
        System.out.println("ACCOUNT:" + sequence);
        sequence = sequenceGeneratorClient.getStringSequence("CoreSeq");
        System.out.println("CoreSeq:" + sequence);
        sequence = sequenceGeneratorClient.getStringSequence("PlatFormSeq");
        System.out.println("PlatFormSeq:" + sequence);
        sequence = sequenceGeneratorClient.getStringSequence("PB_CUSTOMER");
        System.out.println("PB_CUSTOMER:" + sequence);
        sequence = sequenceGeneratorClient.getStringSequence("AC_CUSTOMER");
        System.out.println("AC_CUSTOMER:" + sequence);
        return "执行完成";
    }

}
