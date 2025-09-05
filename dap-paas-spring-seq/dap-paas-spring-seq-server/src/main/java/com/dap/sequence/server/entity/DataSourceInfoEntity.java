package com.dap.sequence.server.entity;

import com.base.api.entity.BaseEntity;

/**
 * @Author: cao
 * Data: 2022/2/28 16:28
 * @Descricption:
 */
public class DataSourceInfoEntity{

    /**
     * 数据库编码
     */
    private String datasourceName;
    /**
     * 数据库驱动
     */
    private String driverClassName;
    /**
     * 数据库地址
     */
    private String datasourceUrl;
    /**
     * 数据库名称
     */
    private String datasourceUsername;
    /**
     * 数据库密码
     */
    private String datasourcePassword;


    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    public void setDatasourceUrl(String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    public String getDatasourceUsername() {
        return datasourceUsername;
    }

    public void setDatasourceUsername(String datasourceUsername) {
        this.datasourceUsername = datasourceUsername;
    }

    public String getDatasourcePassword() {
        return datasourcePassword;
    }

    public void setDatasourcePassword(String datasourcePassword) {
        this.datasourcePassword = datasourcePassword;
    }
}
