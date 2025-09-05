package com.dap.sequence.server.controller.v1;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.base.api.entity.BaseEntity;
import com.base.api.exception.DaoException;
import com.dap.sequence.api.dto.SnowflakeRcvCluster;
import com.dap.sequence.api.exception.ExceptionEnum;
import com.dap.sequence.api.exception.ResultResponse;
import com.dap.sequence.server.utils.NetUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.pool.DruidDataSource;
import com.base.api.exception.ServiceException;
import com.dap.sequence.api.commons.Result;
import com.dap.sequence.api.commons.ResultEnum;
import com.dap.sequence.api.commons.ResultUtils;
import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.AbstractSeqFlowEngine;
import com.dap.sequence.api.core.SeqConsumer;
import com.dap.sequence.api.core.SeqFlowEngine;
import com.dap.sequence.api.dto.SeqSdkMonitorDto;
import com.dap.sequence.api.dto.SeqTransferDto;
import com.dap.sequence.api.dto.ServiceParamsDto;
import com.dap.sequence.api.expection.SequenceException;
import com.dap.sequence.server.config.DataSourceContextHolder;
import com.dap.sequence.server.config.DynamicDataSource;
import com.dap.sequence.server.entity.DataSourceInfoEntity;
import com.dap.sequence.server.entity.SeqDesignPo;
import com.dap.sequence.server.entity.SeqSdkMonitor;
import com.dap.sequence.server.entity.SeqServiceNode;
import com.dap.sequence.server.factory.SeqFlowEngineFactory;
import com.dap.sequence.server.holder.SeqHolder;
import com.dap.sequence.server.service.SeqDesignService;
import com.dap.sequence.server.service.SeqSdkMonitorService;
import com.dap.sequence.server.service.SeqServiceNodeService;
import com.dap.sequence.server.service.SeqUseConditionService;
import com.dap.sequence.server.utils.EnvUtils;

/**
 * @Author: liuz
 * Data: 2022/1/19 14:13
 * @Descricption:
 */
@RestController
@RequestMapping("/v1/seq/publish")
public class SeqDesignController {
    private static final Logger logger = LoggerFactory.getLogger(SeqDesignController.class);

    @Autowired
    private SeqConsumer seqConsumer;

    @Autowired
    private SeqFlowEngineFactory seqFlowEngineFactory;

    @Autowired
    private SeqUseConditionService seqUseConditionService;
    @Autowired
    private SeqSdkMonitorService seqSdkMonitorService;

    @Autowired
    private SeqServiceNodeService seqServiceNodeService;

    @Autowired
    private SeqDesignService seqDesignService;

    final private static String NORMAL_STATUS = "1";

    /**
     * 未获取CLUSTER_NAME信息，则给的是"ALL"
     */
    private static final String DEFAULT_CLUSTER_ALL = "ALL";

    private static final String HOST_IP = "hostIp";

    private static final String POST = "port";

    @Value("${server.port}")
    private int port;

    @Value("${dap.sequence.serviceNodeIp:}")
    private String serviceNodeIp;

    @PostMapping("/getStringIps")
    public Result getStringIps(HttpEntity<String> request,HttpServletRequest httpServletRequest) {
        //获取序列服务端ip
        Map ipPortMap = getIpPort(serviceNodeIp);

        SeqServiceNode seqSserviceNode = seqServiceNodeService.getObjectByMap(ipPortMap);
        if (Objects.nonNull(seqSserviceNode)) {
            Map map = new HashMap();
            map.put("clusterId", seqSserviceNode.getClusterId());
            StringBuilder str = new StringBuilder();
            List<SeqServiceNode> listNode = seqServiceNodeService.queryList(map);
            logger.info("=====getStringIps=====服务端ip信息：" + JSON.toJSON(listNode));
            if(listNode != null){
                if (listNode.size() == 0) {
                	return ResultUtils.error("没有启动的序列节点");
                }else {
                    listNode.forEach(node -> {
                        str.append(node.getHostIp() + ":" + node.getPort()).append(",");
                    });
                    return ResultUtils.success(str.deleteCharAt(str.length() - 1));
                }
            }else {
                return ResultUtils.error("没有启动的序列节点");
            }
        } else {
            return ResultUtils.error("集群名称不存在");
        }
    }

    /**
     * 发布rest服务接口
     * @param request
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/getStringSeq")
    public Result getStringSeq(HttpEntity<ServiceParamsDto> request, HttpServletRequest httpServletRequest) {
    	
        String tenantId=httpServletRequest.getHeader("tenantId");
        ServiceParamsDto serviceParamsDto = request.getBody();
        SeqTransferDto seqFormServer;
        	
        String seqCode = serviceParamsDto.getSeqCode();
        
        String sequenceCode = StringUtils.substringBefore(seqCode, SequenceConstant.DAY_SWITCH_SPLIT);
        Map map = new HashMap<Object, Object>();
    	map.put("seqCode", sequenceCode);
    	map.put("tenantId", tenantId);
    	SeqDesignPo seqDesignPo = seqDesignService.getObjectByMap(map);
    	
    	if(seqDesignPo == null) {
    		return ResultUtils.error(500, "没有找到对应的序列！");
    	}
    	
    	StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        seqFormServer = seqConsumer.getSeqFormServer(seqCode ,tenantId);
        ArrayList<Object> list = seqFormServer.getList();
        if (CollectionUtils.isEmpty(list)) {
        	
        	stopWatch.stop();
            return ResultUtils.error(500, "没有找到对应的序列！");
        }
        
        final String clientInfo = EnvUtils.getIpAddress(httpServletRequest)+ ":" + serviceParamsDto.getClientPort();
       
        seqUseConditionService.saveUseCondition(list, seqCode , seqDesignPo , clientInfo);
        stopWatch.stop();
        
        logger.info("序列：{},请求总耗时：{} ms ", sequenceCode , stopWatch.getTotalTimeMillis());
        
        return ResultUtils.success(seqFormServer);
    }

    /**
     * 检查序列是否存在，进行校验
     *
     * @param seqCode
     */
    private boolean checkSeqCode(String seqCode) {
        SeqDesignPo seqDesignPo = new SeqDesignPo();
        String code = StringUtils.substringBefore(seqCode, SequenceConstant.DAY_SWITCH_SPLIT);
        seqDesignPo.setSequenceCode(code);
        List<SeqDesignPo> designPos = new ArrayList<>();
        try {
            designPos = seqDesignService.queryList(seqDesignPo);
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        return CollectionUtils.isEmpty(designPos);
    }

    /**
     * sdk 监控存储
     *
     * @param request
     * @return Result
     */
    @PostMapping("/insert/sdk")
    public ResultResponse saveSdkMonitor(HttpEntity<SeqSdkMonitorDto> request, HttpServletRequest httpServletRequest) {
        SeqSdkMonitorDto dto = request.getBody();
        Optional.ofNullable(dto).orElseThrow(() -> new SequenceException(ExceptionEnum.SEQ_SDK_MONITOR_INFO_EMPTY));
        SeqSdkMonitor body = new SeqSdkMonitor();
        BeanUtils.copyProperties(dto, body);

        //获取序列服务端ip
        Map<String, Object> ipPortMap = getIpPortMap();
        SeqServiceNode seqServiceNode = seqServiceNodeService.getObjectByMap(ipPortMap);
        Optional.ofNullable(seqServiceNode).orElseThrow(() -> new SequenceException(ExceptionEnum.SEQ_NODE_NOT_FOUND));
        body.setTenantId(seqServiceNode.getTenantId());
        body.setServiceId(seqServiceNode.getClusterId());
        body.setStatus(NORMAL_STATUS);
        body.setHostIp(EnvUtils.getIpAddress(httpServletRequest));
        HashMap<String, String> map = new HashMap<>();
        map.put(HOST_IP, body.getHostIp());
        map.put(POST, body.getPort());
        List<SeqSdkMonitor> seqSdkMonitors = seqSdkMonitorService.queryList(map);
        logger.info("=====sdk=====服务端ip信息：{}", JSON.toJSON(seqSdkMonitors));
        Integer integer = Optional.ofNullable(seqSdkMonitors)
                .filter(seqSdk -> !seqSdk.isEmpty())
                .map(seqSdk -> seqSdk.get(0))
                .map(BaseEntity::getId)
                .map(id -> {
                    body.setId(id);
                    return seqSdkMonitorService.updateObject(body);
                })
                .orElseGet(() -> seqSdkMonitorService.saveObject(body));
        logger.info("sdk monitor insert or update num is {}", integer);
        return ResultResponse.success(ResultEnum.SUCCESS);
    }


    /**
     * 雪花算法-workId申请接口
     *
     * @param request 请求信息
     * @return
     */
    @PostMapping("/snowflakewordid")
    public ResultResponse intWorkId(HttpEntity<SeqSdkMonitorDto> request) {
        SeqSdkMonitorDto dto = request.getBody();
        if (dto == null) {
            throw new SequenceException(ExceptionEnum.SEQ_SNOW_INFO_EMPTY);
        }
        //验证instanceName
        if (StringUtils.isBlank(dto.getInstanceName())) {
            throw new SequenceException(ExceptionEnum.SEQ_SNOW_PARAM_ERROR);
        }

        SeqSdkMonitor body = new SeqSdkMonitor();
        body.setServiceName(dto.getServiceName());
        body.setHostIp(dto.getHostIp());
        body.setPort(dto.getPort());
        body.setInstanceName(dto.getInstanceName());
        body.setContextPath(dto.getContextPath());
        body.setSdkName(StringUtils.isBlank(dto.getSdkName()) ? DEFAULT_CLUSTER_ALL : dto.getSdkName());

        //获取序列服务端ip
        Map<String, Object> ipPortMap = getIpPortMap();
        SeqServiceNode seqServiceNode = seqServiceNodeService.getObjectByMap(ipPortMap);
        //获取序列服务信息
        if (seqServiceNode != null) {
            body.setTenantId(seqServiceNode.getTenantId());
            body.setServiceId(seqServiceNode.getClusterId());
        }
        logger.info("starting request workId, client detail info is instanceName: {{}}, sdkName:{{}}", body.getInstanceName(), body.getSdkName());
         SeqSdkMonitor seqSdkMonitor=null;
        try {
            seqSdkMonitor = seqSdkMonitorService.initWorkId(body);
        } catch (DaoException e) {
            e.printStackTrace();
        }
        logger.info("ended request workId, client detail info is instanceName: {{}}, workId:{{}}", body.getInstanceName(), seqSdkMonitor.getWorkId());
        return ResultResponse.success(seqSdkMonitor.getWorkId());
    }


    /**
     * 雪花算法-workId心跳接口接口
     *
     * @param request 请求信息
     * @return
     */
    @PostMapping("/heartbeat")
    public ResultResponse liveWorkId(HttpEntity<SeqSdkMonitorDto> request) {
        //1.拿到持久化的workId，需要更新或者新增
        SeqSdkMonitorDto dto = request.getBody();
        if (dto == null) {
            throw new SequenceException(ExceptionEnum.SEQ_SNOW_INFO_EMPTY);
        }
        //验证instanceName
        if (StringUtils.isBlank(dto.getInstanceName())) {
            throw new SequenceException(ExceptionEnum.SEQ_SNOW_PARAM_ERROR);
        }
        SeqSdkMonitor body = new SeqSdkMonitor();
        body.setSdkName(StringUtils.isBlank(dto.getSdkName()) ? DEFAULT_CLUSTER_ALL : dto.getSdkName());
        body.setInstanceName(dto.getInstanceName());
        body.setWorkId(dto.getWorkId());
        boolean result= false;
        try {
            result = seqSdkMonitorService.liveWorkId(body);
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return ResultResponse.success(result);
    }

    /**
     * 自动回收
     *
     * @param request 请求信息
     * @return
     */
    @PostMapping("/rcvWorkIdAuto")
    public ResultResponse rcvWorkId(HttpEntity<SnowflakeRcvCluster> request) {
        SnowflakeRcvCluster rcvCluster = request.getBody();
        if (rcvCluster == null) {
            throw new SequenceException(ExceptionEnum.SEQ_SNOW_INFO_EMPTY);
        }
        return ResultResponse.success(seqSdkMonitorService.rcvSnowflakeWorkId(rcvCluster.getRcvWorkIdMaxIntervalTime()));
    }

    /**
     * 雪花算法-workId手动回收接口
     *
     * @param request 请求信息
     * @return
     */
    @PostMapping("/snowflake/recycle")
    public ResultResponse rcvWorIdByClient(HttpEntity<SnowflakeRcvCluster> request) {
        SnowflakeRcvCluster rcvCluster = request.getBody();
        if (rcvCluster == null) {
            throw new SequenceException(ExceptionEnum.SEQ_SDK_MONITOR_INFO_EMPTY);
        }

        //验证instanceName
        if (StringUtils.isBlank(rcvCluster.getInstanceName()) && Objects.isNull(rcvCluster.getWorkId())) {
            throw new SequenceException(ExceptionEnum.SEQ_SNOW_PARAM_ERROR);
        }

        SeqSdkMonitor sdkMonitor = new SeqSdkMonitor();
        sdkMonitor.setSdkName(StringUtils.isBlank(rcvCluster.getSdkName()) ? DEFAULT_CLUSTER_ALL : rcvCluster.getSdkName());
        sdkMonitor.setInstanceName(rcvCluster.getInstanceName());
        sdkMonitor.setWorkId(rcvCluster.getWorkId());
        return ResultResponse.success(seqSdkMonitorService.rcvWorIdByClient(sdkMonitor));
    }

    /**
     * 获取序列对象
     * @param request
     * @param tenantId
     * @return
     */
    @PostMapping("/getSeqDesignObj")
    public Result getSeqObj(HttpEntity<ServiceParamsDto> request , @RequestHeader String tenantId) {
    	
    	ServiceParamsDto serviceParamsDto = request.getBody();
    	String seqCodeCom = serviceParamsDto.getSeqCode() + SequenceConstant.TenantPostfix+tenantId;
    	AbstractSeqFlowEngine seqFlowEngine = SeqHolder.getEngineMap().get(seqCodeCom);
    	
    	if(seqFlowEngine == null || seqFlowEngine.getSeqObj() == null) {
    		return ResultUtils.error("序列编号未找到设计器！！");
    	}
    	return ResultUtils.success(seqFlowEngine.getSeqObj()) ;
    }


    /**
     * 下发后动态修改 缓存信息。
     * @param map
     * @return
     */
    @PostMapping("/updateSeqHolder")
    public Result updateSeqHolder(@RequestBody Map<String, String> map) {
        try {
            SeqDesignPo seqDesignPo=  seqFlowEngineFactory.doCreateEngine(map.get("seqId"));
            SeqFlowEngine seqFlowEngine = SeqHolder.getEngineMap().get(map.get("sequenceCode")+SequenceConstant.TenantPostfix+seqDesignPo.getTenantId());
            seqFlowEngine.refresh();
        } catch (SequenceException e) {
            logger.error("updateSeqHolder 更新失败", e);
            return ResultUtils.error(500, e.getMessage());
        }
        return ResultUtils.success(ResultEnum.SUCCESS);
    }

    /**
     * 校验appCode与accessKey是否正确
     * @param request
     * @return
     */
    @PostMapping("/checkAccessKey")
    public Result checkAccessKey(HttpEntity<Map<String,String>> request, HttpServletRequest httpServletRequest) {

        Map queryMap = request.getBody();
        boolean result = seqDesignService.checkAccessKey(queryMap);
        if(!result){
            return ResultUtils.error("accessKey错误，请确认使用正确的accessKey!");
        }
        return ResultUtils.success(ResultEnum.SUCCESS);
    }

    /**
     * 动态切换数据库
     *
     * @param sourceInfoEntity
     * @return
     */
    @PostMapping("/cutDataSource")
    public Result cutDataSource(@RequestBody DataSourceInfoEntity sourceInfoEntity) {
        try {
            if (checkDataSource(sourceInfoEntity)) {
                setDataSourceInfo(sourceInfoEntity);
                return ResultUtils.success(ResultEnum.SUCCESS);
            }
            return ResultUtils.error(500, "数据库信息不能为空");
        } catch (Exception e) {
            logger.error("数据库切换失败", e);
            return ResultUtils.error(ResultEnum.FAILED);
        }
    }

    private boolean checkDataSource(DataSourceInfoEntity sourceInfoEntity) {
        if (Objects.isNull(sourceInfoEntity)) {
            return false;
        }
        try {
            for (Field file : sourceInfoEntity.getClass().getDeclaredFields()) {
                file.setAccessible(true);
                if (file.get(sourceInfoEntity) == null || file.get(sourceInfoEntity).equals("")) {
                    return false;
                }
            }
        } catch (IllegalAccessException e) {
            logger.error("checkDataSource failed", e);
        }
        return true;
    }

    /**
     * 根据集群 切换数据源
     */
    private void setDataSourceInfo(DataSourceInfoEntity sourceInfoEntity) {
        List<DataSourceInfoEntity> list = new ArrayList<>();
        list.add(sourceInfoEntity);
        Map<Object, Object> dataSourceMap = list.stream().collect(Collectors.toMap(x -> x.getDatasourceName(), x -> {
            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setDriverClassName(x.getDriverClassName());
            druidDataSource.setUrl(x.getDatasourceUrl());
            druidDataSource.setUsername(x.getDatasourceUsername());
            druidDataSource.setPassword(x.getDatasourcePassword());
            druidDataSource.setValidationQuery("SELECT 1 FROM DUAL");
            druidDataSource.setKeepAlive(true);
            return druidDataSource;
        }));
        DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
        Map<Object, Object> map = dynamicDataSource.getDataSourceMap();
        map.putAll(dataSourceMap);
        dynamicDataSource.setTargetDataSources(map);
        dynamicDataSource.afterPropertiesSet();
        //使用自己设置的sourceName作为数据源的key，使用这个进行切换
        DataSourceContextHolder.setDBType(sourceInfoEntity.getDatasourceName());
    }

    private Map getIpPort(String serviceNodeIp) {
        Map<String,Object> ipPortMap = new HashMap();
        if(StringUtils.isNotEmpty(serviceNodeIp)) {
            String [] serviceNodeIpArr = serviceNodeIp.split(":");
            ipPortMap.put("hostIp", serviceNodeIpArr[0]);
            ipPortMap.put("port", serviceNodeIpArr[1]);
        } else {
            String ip = NetUtils.ip();
            ipPortMap.put("hostIp", ip);
            ipPortMap.put("port", port);
        }
        return ipPortMap;
    }



    private Map<String, Object> getIpPortMap() {
        return Optional.ofNullable(serviceNodeIp)
                .filter(StringUtils::isNotBlank)
                .map(nodeIp -> {
                    Map<String, Object> map = new HashMap<>();
                    String[] serviceNodeIpArr = serviceNodeIp.split(":");
                    map.put(HOST_IP, serviceNodeIpArr[0]);
                    map.put(POST, serviceNodeIpArr[1]);
                    return map;
                }).orElseGet(() -> {
                    Map<String, Object> map = new HashMap<>();
                    String ip = NetUtils.ip();
                    map.put(HOST_IP, ip);
                    map.put(POST, port);
                    return map;
                });
    }

}
