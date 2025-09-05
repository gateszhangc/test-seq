package com.dap.sequence.client.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dap.sequence.api.commons.Result;
import com.dap.sequence.api.commons.ResultUtils;
import com.dap.sequence.client.core.SequenceQueueFactory;

@Controller
@RequestMapping("/seq-client")
public class DapSeqApiResourceController {

  @RequestMapping(value = "/getSeq/buffer")
  @ResponseBody
  public Result uiConfiguration() {
	  
	  List<Map<String, Object>> seqBufferMapList = new ArrayList<Map<String,Object>>();
	  SequenceQueueFactory.SEQUENCE_QUEUE_MAP.forEach((k , v) -> {
		  Map<String, Object> seqBufferMap = new HashMap<String, Object>();
		  seqBufferMap.put("seqCode" , k);
		  seqBufferMap.put("lastGetTime" , v.getLastGetTime());
		  List<Map<String, Object>> queueList = new ArrayList<>();
		  BlockingQueue<Object> blockingQueue = v.getBufferQueue();
		  
		  int seqTotal = 0;
		  if(blockingQueue != null) {
			  Map<String, Object> map = getSeqUsed(blockingQueue , 1);
			  seqTotal += (Integer)map.get("size");
			  queueList.add(getSeqUsed(blockingQueue , 1));
		  }
		  BlockingQueue<BlockingQueue<Object>> quenList =  v.getQuenList();
		  if(quenList != null && quenList.size()>0) {
			  for (BlockingQueue<Object> b : quenList) {
				  Map<String, Object> map = getSeqUsed(b , 0);
				  seqTotal += (Integer)map.get("size");
				  queueList.add(getSeqUsed(b , 0));
			  }
		  }
		  seqBufferMap.put("seqTotal" , seqTotal);
		  seqBufferMap.put("queueList" , queueList);
		  
		  seqBufferMapList.add(seqBufferMap);
	  });
    return ResultUtils.success(seqBufferMapList);
  }

  	public Map<String, Object> getSeqUsed(BlockingQueue<Object> blockingQueue , int used){
		  Object[] ogjs = blockingQueue.toArray();
		  Map<String, Object> map = new HashMap<String, Object>();
		  map.put("used", used);
		  if(ogjs != null && ogjs.length > 0) {
			  map.put("startSeq", ogjs[0]);
			  map.put("endSeq", ogjs[ogjs.length - 1]);
			  map.put("size", ogjs.length);
		  }
		  return map;
	  }
}