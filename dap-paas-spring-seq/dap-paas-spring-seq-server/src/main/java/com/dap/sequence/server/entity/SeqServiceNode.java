package com.dap.sequence.server.entity;


import com.base.api.entity.BaseEntity;
import lombok.Data;

/**
 * 序列集群节点
 * @author scalor
 * @date 2022/1/18
 */
@Data
public class SeqServiceNode extends BaseEntity {
    /**
     * 实例节点ip      db_column: ip
     */ 	
	private String hostIp;
	/**
	 * 实例节点端口       db_column: port
	 */
	private Integer port;
	/**
	 * 实例状态 0：停止，1：运行中 ，2：异常      db_column: status
	 */
	private String status;
    /**
     * machineId       db_column: machine_id 
     */ 	
	private String machineId;
    /**
     * 部署路径       db_column: deploy_path 
     */ 	
	private String deployPath;
    /**
     * 服务集群id       db_column: clusterId
     */ 	
	private String clusterId;

	
    /**
     * 配置文件路径       db_column: config_path
     */
    private String configPath;

    /**
     * log文件路径
     */
    private String logfilePath;

    private String name;
}

