package com.dap.sequence.client.api;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dap.sequence.api.core.SeqConsumer;
import com.dap.sequence.api.dto.SeqCallbackDto;
import com.dap.sequence.api.dto.SeqTransferDto;
import com.dap.sequence.api.dto.ServiceParamsDto;
import com.dap.sequence.api.obj.SeqObj;
import com.dap.sequence.client.balancer.RestTemplateBalancer;
import com.dap.sequence.client.config.SequenceConfig;
import com.dap.sequence.client.properties.ConfigBeanValue;
import com.dap.sequence.client.utils.OtherUtil;

@Service
public class SeqConsumerImpl implements SeqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(SeqConsumerImpl.class);

    @Autowired
    SequenceConfig sequenceConfig;

    @Autowired
    ConfigBeanValue cb;
    
    @Autowired
    RestTemplateBalancer restTemplateBalancer;

    
    @Override
    public SeqTransferDto getSeqFormServer(String sequenceCode, String tenantId) {
        logger.info("getSeqFormServer sequenceCode={}",sequenceCode);

        //设置头文件
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型，这里用json所以是MediaType.APPLICATION_JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        //RestTemplate带参传的时候要用HttpEntity<?>对象传递

        ServiceParamsDto serviceParamsDto = new ServiceParamsDto();
//        Map<String, Object> map = new HashMap<String, Object>();
//            map.put("seqCode", sequenceCode);
        serviceParamsDto.setSeqCode(sequenceCode);
        serviceParamsDto.setClientPort(cb.getServerPort());

        String content = JSON.toJSONString(serviceParamsDto);

        String body = restTemplateBalancer.postRequest(content, OtherUtil.SEQ_CONSUMER_URL);

        if(!StringUtils.hasText(body)) {
    		return null;
    	}
//        HttpEntity request = new HttpEntity(serviceParamsDto, headers);
//
//        Server server = RestTemplateConfig.choosedServer("".equals(OtherUtil.DEFULT_URLS )? cb.getUrls(): OtherUtil.DEFULT_URLS);
//        ResponseEntity<Result> entity =  restTemplateConfig.getRestTemplate().postForEntity(
//                OtherUtil.HTTP+server.getHost()+":"+server.getPort()+OtherUtil.SEQ_CONSUMER_URL,
//                request,
//                Result.class);
//        //获取3方接口返回的数据通过entity.getBody();它返回的是一个字符串；
//        Result body = entity.getBody();
//        //然后把str转换成JSON再通过getJSONObject()方法获取到里面的result对象，因为我想要的数据都在result里面
//        //下面的strToJson只是一个str转JSON的一个共用方法；
       // logger.debug("response body{}",body);
        JSONObject object = JSONObject.parseObject(body);
        SeqTransferDto seqTransferDto = JSON.parseObject(object.getString("data"), SeqTransferDto.class);
        return seqTransferDto;

    }

    @Override
    public Boolean clientCacheQueueCallback(SeqCallbackDto SeqCallbackDto) {
        logger.info("clientCacheQueueCallback sequenceCode={} ",SeqCallbackDto.getSequenceCode());

        String content = JSON.toJSONString(SeqCallbackDto);
        System.out.println("content=="+content);
        String body = restTemplateBalancer.postRequest(content, OtherUtil.CacheCallBack_URL);

        logger.debug("response body{}",body);
        JSONObject object = JSONObject.parseObject(body);
        return true;

    }

	@Override
	public BlockingQueue<Object> getSequenceQueue(String sequenceCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigInteger getBigIntSequence(String sequenceCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStringSequence(String sequenceCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigInteger getSerialBigIntSequence(String sequenceCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSerialStringSequence(String sequenceCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SeqObj getSeqDesignObj(String seqCode) {
		
        ServiceParamsDto serviceParamsDto = new ServiceParamsDto();
        serviceParamsDto.setSeqCode(seqCode);
        serviceParamsDto.setClientPort(cb.getServerPort());
	
        String content = JSON.toJSONString(serviceParamsDto);
      
		String body = restTemplateBalancer.postRequest(content , OtherUtil.GET_SEQ_DESIGN_OBJ);
		JSONObject object = JSONObject.parseObject(body);
		
		if(null ==object || object.getIntValue("code") != 200) {
			logger.error("request GET_SEQ_DESIGN_OBJ fail !!!");
			return null;
		}
		SeqObj seqObj = JSON.parseObject(object.getString("data"), SeqObj.class);
        return seqObj;
	}


}
