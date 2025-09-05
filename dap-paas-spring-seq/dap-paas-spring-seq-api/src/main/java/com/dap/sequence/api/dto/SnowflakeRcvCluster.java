package com.dap.sequence.api.dto;

/**
 * 可回收的集群信息
 *
 * @author lyf
 * @date 2024/1/16
 */
public class SnowflakeRcvCluster {

    /**
     * 需要回收的集群信息
     */
    private String sdkName;

    /**
     * 需要回收的集群当前workId数量
     */
    private Integer totalCount;

    /**
     * 回收时，当处于非运行状态且大于此值时，进行删除，回收
     */
    private Integer rcvWorkIdMaxIntervalTime;

    /**
     * workId
     */
    private Integer workId;

    /**
     * 环境变量 instanceName
     */
    private String instanceName;

    /**
     * 端口
     */
    private String port;

    /**
     * IP
     */
    private String hostIp;

    public Integer getWorkId() {
        return workId;
    }

    public void setWorkId(Integer workId) {
        this.workId = workId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getSdkName() {
        return sdkName;
    }

    public void setSdkName(String sdkName) {
        this.sdkName = sdkName;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getRcvWorkIdMaxIntervalTime() {
        return rcvWorkIdMaxIntervalTime;
    }

    public void setRcvWorkIdMaxIntervalTime(Integer rcvWorkIdMaxIntervalTime) {
        this.rcvWorkIdMaxIntervalTime = rcvWorkIdMaxIntervalTime;
    }
}
