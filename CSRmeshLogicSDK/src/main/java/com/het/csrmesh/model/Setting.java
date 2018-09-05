/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.het.csrmesh.model;


/**
 * This class represents the configuration
 */
public class Setting {

    public static final int MAX_TTL_VALUE = 0xFF;


    private int id = -1;
    private int concurrentConnections;
    private int listeningMode;
    private int retryCount;
    private int retryInterval;

    private String cloudMeshId;
    private String cloudTenantId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConcurrentConnections() {
        return concurrentConnections;
    }

    public void setConcurrentConnections(int concurrentConnections) {
        this.concurrentConnections = concurrentConnections;
    }

    public int getListeningMode() {
        return listeningMode;
    }

    public void setListeningMode(int listeningMode) {
        this.listeningMode = listeningMode;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public String getCloudMeshId() {
        return cloudMeshId;
    }

    public void setCloudMeshId(String cloudMeshId) {
        this.cloudMeshId = cloudMeshId;
    }

    public String getCloudTenantId() {
        return cloudTenantId;
    }

    public void setCloudTenantId(String cloudTenantId) {
        this.cloudTenantId = cloudTenantId;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;

        if (object != null && object instanceof Setting) {
            isEqual = (this.id == ((Setting) object).id);
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}