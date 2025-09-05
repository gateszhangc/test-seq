package com.dap.sequence.client.balancer;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dap.sequence.client.exception.PaasSequenceException;
import com.dap.sequence.client.pojo.SeqAlarmData;
import com.dap.sequence.client.utils.RsaEncryptionUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.dap.sequence.client.properties.ConfigBeanValue;
import com.dap.sequence.client.utils.NetworkUtil;
import com.dap.sequence.client.utils.OtherUtil;

@Service
public class RestTemplateBalancer {
    private static final Logger logger = LoggerFactory.getLogger(RestTemplateBalancer.class);

    @Autowired
    private ConfigBeanValue cb;

    public static AtomicInteger count = new AtomicInteger(3);

    public static AtomicInteger nextServerCounter = new AtomicInteger(0);

    /**
     *  获取所有的Server 结合列表
     * @param urls
     * @return
     */
    public static List<Server> getServerList(String urls){
        List<Server> serverList = new ArrayList<>();
        if (!"".equals(urls) && urls != null) {
            //兼容yml配置，去掉url中的'或者"
            urls = urls.replaceAll("'","").replaceAll("\"","");
            String[] urlArray = urls.split(",");
            for (int i = 0; i <urlArray.length ; i++) {
                String ipAndPorts = urlArray[i];
                List<String> ipPortList = getIpPort(ipAndPorts);
                serverList.add(new Server(ipPortList.get(0),Integer.parseInt(ipPortList.get(1))));
            }
        }else {
            logger.error("urls参数为空");
        }
        return serverList;
    }

    /**
     * 随机获取一个序列服务 
     * @return
     */
    public Server getServer() {
        String urls = !StringUtils.hasText(OtherUtil.DEFULT_URLS) ? cb.getUrls() : OtherUtil.DEFULT_URLS;
        List<Server> serverList = getServerList(urls);
        if (CollectionUtils.isEmpty(serverList)) {
            logger.error("获取序列服务地址为空！！！");
            return null;
        }

        // 总大小
        int t = serverList.size();
        Set<String> telnetFalse = new HashSet<>();
        while (telnetFalse.size() < t) {
            Server server = serverList.get(nextServerCounter.getAndUpdate((x) -> x == Integer.MAX_VALUE ? 0 : x + 1) % t);
            if (!NetworkUtil.telnet(server.getHost(), server.getPort())) {
                String host = server.getHost() + ":" + server.getPort();
                telnetFalse.add(host);
                processServer(server,3);
                logger.error("无法连接节点:{}", host);
                continue;
            }
            return server;
        }
        logger.error("所有服务端节点连接失败:{}", telnetFalse);
        return null;
    }


    public boolean processServer(Server server,int retry ){
        if(NetworkUtil.telnet(server.getHost(), server.getPort())) {
            return true;
        }else{
            if(retry < count.get()){
                try {
                    retry++;
                    logger.error("The network connection between the client and server is abnormal; "+" sleep 1 s retry: "+retry+" times !");
                    Thread.sleep( 1000);
                } catch (InterruptedException eg) {
                    throw new RuntimeException(eg);
                }
                processServer(server,retry);
            }else{
                logger.error("The network connection between the client and server is bad !!!");
                //网络异常，报告给监控告警平台
                try {
                    this.processAlarm(server);
                } catch (PaasSequenceException ex) {
                    throw new RuntimeException(ex);
                }
                return false;
            }
        }
        return false;
    }

    //调用监控平台告警
    private void processAlarm(Server server) throws PaasSequenceException {
        String projectGroup = cb.getProjectGroup();
        String pushAlarm = cb.getPushAlarm();
        String centerMark = cb.getCenterMark();
        if(!StringUtils.hasLength(pushAlarm)){
            logger.error("alarm-url is blank,u can add it on application.yml " );
        }else{
            SeqAlarmData alarmData = new SeqAlarmData();
            alarmData.setTime(System.currentTimeMillis());
            alarmData.setTitle("The network connection between the client and server is abnormal ");
            alarmData.setType("sequence");
            alarmData.setAddress(server.getHost()+":"+server.getPort());
            alarmData.setLevel("3");
            alarmData.setProjectGroup(projectGroup);
            alarmData.setBussDomain("");
            alarmData.setContainerCluster("");
            alarmData.setCenterMark(centerMark);
            alarmData.setCategory("network");

            //鉴权
            String token = loginMonitor(cb.getUsername(),cb.getPassword());
            HttpHeaders headers = new HttpHeaders();
            headers.setConnection("close");
            headers.setContentType(MediaType.APPLICATION_JSON);
            List<Charset> charsets = new ArrayList<>();
            charsets.add(Charset.forName("UTF-8"));
            headers.setAcceptCharset(charsets);
            headers.set("token",token);
            List<SeqAlarmData> requestList = Lists.newArrayList(alarmData);
            HttpEntity<String> requestEntity = new HttpEntity<>(JSONArray.toJSONString(requestList), headers);

            try {
                logger.info("客户端与服务端连接异常;;开始调用监控告警平台接口==,请求地址:{},请求参数:{}",pushAlarm, JSONArray.toJSONString(requestList));
                ResponseEntity<String> exchange = getRestTemplate().exchange(pushAlarm, HttpMethod.POST, requestEntity,String.class);
                String result = exchange.getBody();
                if(StringUtils.hasLength(result)){
                    JSONObject jso = JSONObject.parseObject(result);
                    Integer status = (Integer)jso.get("status");
                    if(status.intValue() != 200){
                        throw new PaasSequenceException("调用监控告警平台返回失败！！");
                    }
                }else{
                    throw new PaasSequenceException("调用监控告警平台失败！！");
                }
            } catch (Exception e) {
                logger.error("seq request fail url:{} ,content:{} ,Exception:{} ",pushAlarm ,JSONArray.toJSONString(requestList), e.getMessage());
                throw new PaasSequenceException("调用监控告警平台失败！！");
            }
            logger.info("------------------调用告警平台------------3");
        }
    }

    /**
     * 通过URL截取ipv4、ipv6
     * @param url
     * @return
     */
    private static List<String> getIpPort (String url) {
        List<String> ipPortList = new ArrayList<>();
        // ipv4使用正则表达式截取
        String regEx = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(url);
        if (m.find()) {
            int start = url.lastIndexOf(":");
            ipPortList.add(m.group());
            ipPortList.add(url.substring(start + 1));
        } else if (url.contains("[") || url.contains("]")) {// ipv6截取
            int end = url.lastIndexOf("]:");
            ipPortList.add(url.substring(0, end+1));
            ipPortList.add(url.substring(end + 2));
        } else if (url.contains(":")) {
            String[] array = url.split(":");
            ipPortList.add(array[0]);
            ipPortList.add(array[1]);
        } else {// 只配置域名，默认端口用80
            ipPortList.add(url);
            ipPortList.add("80");
        }
        return ipPortList;
    }

    public static void main(String[] args) {
        String url = "[123.13.11.ad::123]:80";
        String url2 = "192.1.1.1:80";
        String url3 = "domain.test.com.cn:8080";
        String url4 = "domain.test.com.cn";
        List<String>  list = getIpPort(url);
        List<String>  list2 = getIpPort(url2);
        List<String>  list3 = getIpPort(url3);
        List<String>  list4 = getIpPort(url4);

        System.out.println(list3);
    }
    /**
     * 返回调用信息
     * @param content 调用传递内容
     * @param loadPath   调用地址
     * @return  返回调用数据
     */
    public String getRequest(String content, String loadPath){
    	return request(content, loadPath, HttpMethod.GET);
    }
    
    /**
     * 返回调用信息
     * @param content 调用传递内容
     * @param loadPath   调用地址
     * @return  返回调用数据
     */
    public String postRequest(String content, String loadPath){
    	return request(content, loadPath, HttpMethod.POST);
    }

    RestTemplate restTemplate = null;
    
    public RestTemplate getRestTemplate() {
    	if(restTemplate == null) {
    		OkHttp3ClientHttpRequestFactory httpRequestFactory = new OkHttp3ClientHttpRequestFactory(); 
    		httpRequestFactory.setReadTimeout(cb.getReadTimeout());
	   		httpRequestFactory.setConnectTimeout(cb.getConnectTimeout());
	   		restTemplate = new RestTemplate(httpRequestFactory);
    	}
		return restTemplate;
	}
    
    public String request(String content, String loadPath , HttpMethod httpMethod){
    	
//        List<Server> serverList = RestTemplateBalancer.getServerList(!StringUtils.hasText(OtherUtil.DEFULT_URLS) ? cb.getUrls() : OtherUtil.DEFULT_URLS);
//        
//        if(CollectionUtils.isEmpty(serverList)) {
//        	logger.error("seq request fail serverList is null");
//        	return null; 
//        }
//        ILoadBalancer loadBalancer = getLoadBalancer(serverList);
        
        Server server = getServer();
        
        if(server == null) {
        	logger.error("没有可用的序列服务");
            return null;
        }

        // Post请求的url，与get不同的是不需要带参数
        String postUrl = OtherUtil.HTTP + server.getHost() + ":" + server.getPort() + loadPath;
        logger.info("seq request url:{} ,content:{} ,tenantId:{}" , postUrl , content , cb.getTenantId());
          
        HttpHeaders headers = new HttpHeaders();
        headers.setConnection("close");
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<Charset> charsets = new ArrayList<>();
        charsets.add(Charset.forName("UTF-8"));
        headers.setAcceptCharset(charsets);
        headers.add("tenantId", cb.getTenantId());
        HttpEntity<String> requestEntity = new HttpEntity<String>(content, headers);
         
          try {
          	ResponseEntity<String> exchange = getRestTemplate().exchange(postUrl, HttpMethod.POST, requestEntity,String.class);
              return exchange.getBody();
          } catch (Exception e) {
          	logger.error("seq request fail url:{} ,content:{} ,tenantId:{} ,Exception:{} " , postUrl , content , cb.getTenantId() , e.getMessage());
              return null;
          }
    }


    public String queryPublicKey() {
        try {
            ResponseEntity responseEntity = getRestTemplate().getForEntity(cb.getQueryPublicKey(), String.class);
            logger.info("queryPublicKey response return {} ", responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                JSONObject resObject = JSON.parseObject(responseEntity.getBody().toString());
                //true表示成功，false表示失败
                String ret = resObject.get("ret").toString();
                if("true".equals(ret)) {
                    return resObject.get("data").toString();

                } else {
                    String error = String.valueOf(resObject.get("error"));
                    logger.error("调用监控平台服务获取公钥失败：" + error);
                }
            }
        } catch (Exception e){
            logger.error("调用监控平台服务获取公钥失败",e);
            return null;
        }
        return null;
    }

    public String loginMonitor(String username,String password)  {
        try {
            logger.info("loginMonitor request username {} password {} ", username,password);
            String publickkey = queryPublicKey();
            String pwd = RsaEncryptionUtil.getRsaEncryptionPassword(publickkey,password);

            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("username",username);
            paramMap.put("password",pwd);
            ResponseEntity<String> responseEntity = getRestTemplate().postForEntity(cb.getLoginMonitor(), paramMap, String.class);
            logger.info("loginMonitor response return {} ", responseEntity);
            if (responseEntity.getStatusCode() == HttpStatus.OK) { // http请求成功
                JSONObject jsonObject = JSON.parseObject(responseEntity.getBody());
                //true表示成功，false表示失败
                String ret = jsonObject.get("ret").toString();
                if("true".equals(ret)) {
                    JSONObject data = JSON.parseObject(jsonObject.get("data").toString());
                    JSONObject userInfo = JSON.parseObject(data.get("userinfo").toString());
                    return userInfo.get("token").toString();
                } else {
                    String error = String.valueOf(jsonObject.get("error"));
                    logger.error("调用监控平台服务获取公钥失败：" + error);
                }
            }
            return null;
        } catch (Exception e){
            logger.error("调用监控平台服务获取token失败",e);
            return null;
        }
    }

}
