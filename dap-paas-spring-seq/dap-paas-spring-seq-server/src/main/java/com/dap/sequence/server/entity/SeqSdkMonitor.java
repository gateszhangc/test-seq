package com.dap.sequence.server.entity;

import com.base.api.entity.BaseEntity;

/**
 * @author: liu
 * @date: 2022/2/24 10:39
 * @description:
 */
public class SeqSdkMonitor extends BaseEntity {

    //服务名称
    private String serviceName;

    //sdk服务ip
    private String hostIp;

    //sdk服务端口
    private String port;

    //sdk状态(0:停止，1：运行，2：故障)
    private String status;

    //服务唯一标识
    private String serviceId;

    //sdk名称
    private String sdkName;

    private String contextPath;
    /**
     * 应用所在虚拟或者容器的HOSTNAME
     */
    private String instanceName;

    /**
     * 序列的workdId
     */
    private Integer workId;

    /**
     * 数据版本，乐观锁
     */
    private Integer version;

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Integer getWorkId() {
        return workId;
    }

    public void setWorkId(Integer workId) {
        this.workId = workId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getSdkName() {
        return sdkName;
    }

    public void setSdkName(String sdkName) {
        this.sdkName = sdkName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHostIp() {
        return hostIp == null? "" : hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getPort() {
        return port == null? "" : port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
