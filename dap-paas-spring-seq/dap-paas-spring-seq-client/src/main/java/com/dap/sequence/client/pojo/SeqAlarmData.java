package com.dap.sequence.client.pojo;

public class SeqAlarmData {
    /** 告警时间 */
    private long time;
    /** 告警标题 */
    private String title;
    /** 告警分类
     sequence -序列中心sdk,
     cache -缓存中心sdk,
     config -配置中心sdk,
     kafka -消息中心(kafka)sdk,
     rocketmq-消息中心(rocketmq)sdk
     */
    private String type;
    /** 服务端地址 */
    private String address;
    /** 告警级别
     2: 紧急
     3: 严重
     4：错误
     5：警示
     */
    private String level;
    /** 所属业务系统 */
    private String projectGroup;
    /** 所属业务域 */
    private String bussDomain;
    /** 所属容器集群 */
    private String containerCluster;
    /** 中心标识
     zhdx-珠海主中心(默认)
     zhzh-珠海同城中心
     ssjt-深汕灾备中心
     */
    private String centerMark;
    /** 告警类别
     network-网络告警
     */
    private String category;
    /**
     * 接口鉴权token
     */
    private String token;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    public String getBussDomain() {
        return bussDomain;
    }

    public void setBussDomain(String bussDomain) {
        this.bussDomain = bussDomain;
    }

    public String getContainerCluster() {
        return containerCluster;
    }

    public void setContainerCluster(String containerCluster) {
        this.containerCluster = containerCluster;
    }

    public String getCenterMark() {
        return centerMark;
    }

    public void setCenterMark(String centerMark) {
        this.centerMark = centerMark;
    }

    public String getCategory() {
        return category;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
