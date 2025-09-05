package com.dap.sequence.api.dto;


import java.io.Serializable;

public class SeqSdkMonitorDto implements Serializable {

    //应用名称
    private String serviceName;
    //    sdk服务ip
    private String hostIp;
    //    sdk服务端口
    private String port;
    //    sdk服务状态
    private String status;
    //    应用唯一标识
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

    /**
     * 判断是不是K8S容器
     */
    private Boolean isK8sPod;
    
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
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getPort() {
        return port;
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

    public Boolean getK8sPod() {
        return isK8sPod;
    }

    public void setK8sPod(Boolean k8sPod) {
        isK8sPod = k8sPod;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
}
