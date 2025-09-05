package com.dap.sequence.server.controller.v1;

import com.alibaba.druid.pool.DruidDataSource;
import com.base.api.exception.ServiceException;
import com.dap.sequence.api.commons.Result;
import com.dap.sequence.api.commons.ResultUtils;
import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.SeqConsumer;
import com.dap.sequence.api.dto.SeqTransferDto;
import com.dap.sequence.server.config.DataSourceContextHolder;
import com.dap.sequence.server.config.DynamicDataSource;
import com.dap.sequence.server.entity.DataSourceInfoEntity;
import com.dap.sequence.server.entity.SeqDesignPo;
import com.dap.sequence.server.entity.SeqServiceNode;
import com.dap.sequence.server.service.SeqDesignService;
import com.dap.sequence.server.service.SeqServiceNodeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: cao
 * Data: 2022/2/28 16:31
 * @Descricption:
 */
@RestController
@RequestMapping("/seq/dynamic")
public class DemoController {

    @Autowired
    private SeqDesignService seqDesignService;

    @Autowired
    private SeqConsumer seqConsumer;

    @Autowired
    private SeqServiceNodeService seqServiceNodeService;

    @GetMapping("/ceshi/{id}")
    public void ceshi(@PathVariable("id") String id) {
        SeqDesignPo designPo = seqDesignService.getObjectById(id);
        System.out.println(designPo.getSequenceName());
    }




    /**
     * 根据集群 切换数据源
     */
    private void setDataSourceInfo(DataSourceInfoEntity sourceInfoEntity) {
        List<DataSourceInfoEntity> list = new ArrayList<>();
        //查询数据库得到
        list.add(sourceInfoEntity);
        //使用自己设置的sourceName作为数据源的key，使用这个进行切换
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
        DataSourceContextHolder.setDBType(sourceInfoEntity.getDatasourceName());
    }

    @GetMapping("/getStringSeq/{seqCode}")
    public Result getStringSeq(@RequestHeader(value = "tenantId",required = true) String tenantId, @PathVariable("seqCode") String seqCode){
        SeqTransferDto seqFormServer;
        if (!checkSeqCode(seqCode)) {
            seqFormServer = seqConsumer.getSeqFormServer(seqCode,tenantId);
        }else {
            return ResultUtils.error(500,"没有找到对应的序列！");
        }
        return ResultUtils.success(seqFormServer);
    }


    @GetMapping("/getStringIps/{seqName}")
    public Result getStringIps(@PathVariable("seqName") String seqName){
        if (Objects.nonNull(seqServiceNodeService.getObjectByClusterName(seqName))) {
            Map map = new HashMap();
            map.put("name", seqName);
            StringBuilder str = new StringBuilder();
            List<SeqServiceNode> listNode = seqServiceNodeService.queryList(map);
            listNode.forEach(node -> {
                str.append(node.getHostIp() + ":" + node.getPort()).append(",");
            });
            return ResultUtils.success(str.deleteCharAt(str.length() - 1));
        } else {
            return ResultUtils.success(seqName + "找不到对应的集群");
        }
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

}
