package com.dap.sequence.server.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dap.sequence.server.utils.ErrorResultMsg;
import com.dap.sequence.server.utils.HttpTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * server端容器化部署：springboot启动后，发送请求到管控端
 */
@Component
public class ProcessRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(ProcessRunner.class);

    @Autowired
    private Environment environment;
    @Value("${processor.enable:false}")
    private boolean processorEnable;

    @Override
    public void run(ApplicationArguments args) {
        if(processorEnable){
            process();
        }
    }

    private void process()  {
        String k8sClusterName = environment.getProperty("k8sClusterName");
        String deployMode = environment.getProperty("deployMode");
        log.info(">>>>>>>>>>k8sClusterName="+k8sClusterName+";;deployMode="+deployMode);
        if(StringUtils.isBlank(k8sClusterName) || !"1".equals(deployMode)){
            return ;
        }
        //发送管控端接口请求,管控端接口地址
        ClusterRequestVo requestVo = getRequestVo();
        String sequencePortalIp = environment.getProperty("sequencePortalIp");
        String consoleUrl = String.format("http://%s",sequencePortalIp+"/v1/sequence/openApi/cluster/insert");
        log.info(">>>>>>>>>>容器化部署后调用管控端接口请求地址====consoleUrl:"+consoleUrl);

        HttpTemplateUtil httpTemplateUtil = new HttpTemplateUtil();
        try{
            String result = httpTemplateUtil.postForObject(consoleUrl, JSONObject.toJSONString(requestVo));
            JSONObject returnJson = JSON.parseObject(result);
            if(ErrorResultMsg.COMMON_SUCCESS.getCode().equals(returnJson.get("code"))){
                log.info(">>>>>>>>>>容器化部署调用管控端接口成功!!!<<<<<<<<<");
            }else{
                log.error("容器化部署后调用管控端接口失败！",returnJson.toJSONString());
            }
        }catch(Exception e){
            e.printStackTrace();
            log.error(">>>>>>>>>>容器化部署后调用管控端接口失败!!!!!!",e.getMessage());
        }
    }

    private ClusterRequestVo getRequestVo(){
        //命名空间、容器集群名称、应用列表
        String name = environment.getProperty("name");
        String namespace = environment.getProperty("namespace");
        String k8sClusterName = environment.getProperty("k8sClusterName");
        String applications = environment.getProperty("applications");
        String deployMode = environment.getProperty("deployMode");
        String address = environment.getProperty("address");
        String tenantCode = environment.getProperty("tenantCode");
        log.info(">>>>>k8sArgs>>>>>deployMode:"+deployMode+";namespace:"+namespace+";k8sClusterName:"+k8sClusterName+";applications:"+applications);

        //数据库信息
        String dbDriver = environment.getProperty("spring.datasource.driver-class-name");
        String dbUser = environment.getProperty("spring.datasource.username");
        String dbPassword = environment.getProperty("spring.datasource.password");
        String dbUrl = environment.getProperty("spring.datasource.url");
        String dbName = getDbInfoFromUrl(dbUrl).get("dbName");
        String dbIp = getDbInfoFromUrl(dbUrl).get("dbIp");
        String dbPort = getDbInfoFromUrl(dbUrl).get("dbPort");
        log.info(">>>>>localArgs>>>>>dbDriver:"+dbDriver+";dbUser:"+dbUser+";dbName:"+dbName+";dbIp:"+dbIp+";dbPort:"+dbPort);

        ClusterRequestVo requestVo = new ClusterRequestVo();
        requestVo.setNamespace(namespace);
        requestVo.setK8sClusterName(k8sClusterName);
        requestVo.setName(name);
        requestVo.setApplications(applications);
        requestVo.setDeployMode(deployMode);
        requestVo.setDbDriver(dbDriver);
        requestVo.setDbUser(dbUser);
        requestVo.setDbPassword(dbPassword);
        requestVo.setDbName(dbName);
        requestVo.setDbIp(dbIp);
        requestVo.setDbPort(dbPort);
        requestVo.setDbUrl(dbUrl);
        requestVo.setAddress(address);
        requestVo.setTenantCode(tenantCode);
        return requestVo;
    }

    private Map<String,String> getDbInfoFromUrl(String dbUrl){
        if(dbUrl.contains("jdbc:dm")){
            String dbUrl_ =  dbUrl.substring(dbUrl.indexOf("://")+3);
            String[] dbInfo = dbUrl_.split("\\?");
            String dbIpAndPort = dbInfo[0];
            String dbName = dbInfo[1].substring(dbInfo[1].indexOf("=")+1);
            String dbIp = dbIpAndPort.substring(0,dbIpAndPort.lastIndexOf(":"));
            String dbPort = dbIpAndPort.substring(dbIpAndPort.indexOf(":")+1);
            Map<String,String> dbMap = new HashMap<>();
            dbMap.put("dbName",dbName);
            dbMap.put("dbIp",dbIp);
            dbMap.put("dbPort",dbPort);
            return dbMap;
        }else{
            String dbUrl_ =  dbUrl.substring(dbUrl.indexOf("://")+3,dbUrl.indexOf("?"));
            String[] dbInfo = dbUrl_.split("/");
            String dbIpAndPort = dbInfo[0];
            String dbName = dbInfo[1];
            String dbIp = dbIpAndPort.substring(0,dbIpAndPort.lastIndexOf(":"));
            String dbPort = dbIpAndPort.substring(dbIpAndPort.indexOf(":")+1);
            Map<String,String> dbMap = new HashMap<>();
            dbMap.put("dbName",dbName);
            dbMap.put("dbIp",dbIp);
            dbMap.put("dbPort",dbPort);
            return dbMap;
        }
    }

    private class ClusterRequestVo{
        String name;
        String namespace;
        String k8sClusterName;
        String applications;
        String deployMode;
        String dbDriver;
        String dbUser;
        String dbPassword;
        String dbName;
        String dbIp;
        String dbPort;
        String dbUrl;
        String dbType = "1";//管控端-0 本地数据源，1 外部数据源
        String address;
        String tenantCode;
        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getK8sClusterName() {
            return k8sClusterName;
        }

        public void setK8sClusterName(String k8sClusterName) {
            this.k8sClusterName = k8sClusterName;
        }

        public String getApplications() {
            return applications;
        }

        public void setApplications(String applications) {
            this.applications = applications;
        }

        public String getDeployMode() {
            return deployMode;
        }

        public void setDeployMode(String deployMode) {
            this.deployMode = deployMode;
        }

        public String getDbDriver() {
            return dbDriver;
        }

        public void setDbDriver(String dbDriver) {
            this.dbDriver = dbDriver;
        }

        public String getDbUser() {
            return dbUser;
        }

        public void setDbUser(String dbUser) {
            this.dbUser = dbUser;
        }

        public String getDbPassword() {
            return dbPassword;
        }

        public void setDbPassword(String dbPassword) {
            this.dbPassword = dbPassword;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public String getDbIp() {
            return dbIp;
        }

        public void setDbIp(String dbIp) {
            this.dbIp = dbIp;
        }

        public String getDbPort() {
            return dbPort;
        }

        public void setDbPort(String dbPort) {
            this.dbPort = dbPort;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDbType() {
            return dbType;
        }

        public void setDbType(String dbType) {
            this.dbType = dbType;
        }

        public String getDbUrl() {
            return dbUrl;
        }

        public void setDbUrl(String dbUrl) {
            this.dbUrl = dbUrl;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
        public String getTenantCode() {
            return tenantCode;
        }

        public void setTenantCode(String tenantCode) {
            this.tenantCode = tenantCode;
        }
    }


}
