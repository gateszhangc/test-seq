package com.dap.sequence.client.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;



@Configuration
public class ConfigBeanValue {

	//动态注册与发现定时时间 如果关闭 "off"
    @Value("${dap.sequence.refreshTime:0 */1 * * * ?}")
    private String refreshTime;

    //客户端定时注册信息到服务端 如果关闭 "off"
    @Value("${dap.sequence.registerTime:0 */5 * * * ?}")
    private String registerTime;
    
    //定时刷新设计器 如果关闭 "off"
    @Value("${dap.sequence.flushDesign:0 */2 * * * ?}")
    private String flushDesign;

    @Value("${dap.sequence.urls:}")
    private String urls;

    @Value("${dap.sequence.seqClusterName:}")
    private String seqClusterName;

    @Value("${dap.sequence.clientName:seq-client}")
    private String clientName;
    
    @Value("${dap.sequence.tenantId:}")
    private  String tenantId;
    
    @Value("${server.port:}")
    private String serverPort;
    
    @Value("${server.context-path:}")
    private String contextPath;
    
    @Value("${spring.application.name:}")
    private String applicationName;
    
    @Value("${dap.sequence.connectTimeout:5000}")
    private int connectTimeout;
    
    @Value("${dap.sequence.readTimeout:15000}")
    private int readTimeout;
    
    @Value("${dap.sequence.workerId:}")
    private Integer workerId;

	@Value("${dap.sequence.projectGroup:}")
	private String projectGroup;

	@Value("${dap.sequence.centerMark:}")
	private String centerMark;

	@Value("${dap.sequence.queryPublicKey:}")
	private String queryPublicKey;
	@Value("${dap.sequence.loginMonitor:}")
	private String loginMonitor;
	@Value("${dap.sequence.pushAlarm:}")
	private String pushAlarm;
	@Value("${dap.sequence.user:}")
	private String username;
	@Value("${dap.sequence.pwd:}")
	private String password;

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(String refreshTime) {
		this.refreshTime = refreshTime;
	}

	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	public String getUrls() {
		return urls;
	}

	public void setUrls(String urls) {
		this.urls = urls;
	}

	public String getSeqClusterName() {
		return seqClusterName;
	}

	public void setSeqClusterName(String seqClusterName) {
		this.seqClusterName = seqClusterName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public Integer getWorkerId() {
		return workerId;
	}

	public void setWorkerId(Integer workerId) {
		this.workerId = workerId;
	}

	public String getProjectGroup() {
		return projectGroup;
	}

	public void setProjectGroup(String projectGroup) {
		this.projectGroup = projectGroup;
	}

	public String getCenterMark() {
		return centerMark;
	}

	public void setCenterMark(String centerMark) {
		this.centerMark = centerMark;
	}

	public String getFlushDesign() {
		return flushDesign;
	}

	public void setFlushDesign(String flushDesign) {
		this.flushDesign = flushDesign;
	}

	public String getQueryPublicKey() {
		return queryPublicKey;
	}

	public void setQueryPublicKey(String queryPublicKey) {
		this.queryPublicKey = queryPublicKey;
	}

	public String getLoginMonitor() {
		return loginMonitor;
	}

	public void setLoginMonitor(String loginMonitor) {
		this.loginMonitor = loginMonitor;
	}

	public String getPushAlarm() {
		return pushAlarm;
	}

	public void setPushAlarm(String pushAlarm) {
		this.pushAlarm = pushAlarm;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
