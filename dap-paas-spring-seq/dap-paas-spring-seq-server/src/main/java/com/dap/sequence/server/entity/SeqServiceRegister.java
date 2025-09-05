package com.dap.sequence.server.entity;


import com.base.api.entity.BaseEntity;
import lombok.Data;

/**
 * 序列集群节点
 * @author scalor
 * @date 2022/1/18
 */
@Data
public class SeqServiceRegister extends BaseEntity {
    /**
     * 实例节点ip      db_column: ip
     */ 	
	private String hostIp;
	/**
	 * 实例节点端口       db_column: port
	 */
	private Integer port;
	/**
	 * 应用名
	 */
	private String applicationName;
}

