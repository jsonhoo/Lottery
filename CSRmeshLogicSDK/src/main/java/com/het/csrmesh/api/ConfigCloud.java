/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.het.csrmesh.api;

import android.os.Bundle;

import com.csr.csrmesh2.ConfigCloudApi;
import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.data.SiteInfo;
import com.het.csrmesh.App;
import com.het.csrmesh.events.MeshRequestEvent;

import java.util.ArrayList;
import java.util.List;


/**
 * Class that contains the API calls referred to configuring the cloud (related to creating/getting site and tenants).
 */
public class ConfigCloud {

    public static int getTenants(ConfigCloudApi.QueryType queryType, String pattern,
                                 ConfigCloudApi.TenantState state, Integer pageSize, Integer page, Integer maximum) {
        Bundle databundle = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        databundle.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        databundle.putInt(MeshConstants.EXTRA_QUERY_TYPE, queryType.ordinal());
        databundle.putString(MeshConstants.EXTRA_SEARCH_PATTERN, pattern);
        databundle.putInt(MeshConstants.EXTRA_TENANT_STATE, state.ordinal());
        databundle.putInt(MeshConstants.EXTRA_PAGE_SIZE, pageSize);
        databundle.putInt(MeshConstants.EXTRA_PAGE_NO, page);
        databundle.putInt(MeshConstants.EXTRA_MAX_RESULTS, maximum);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CLOUD_GET_TENANTS, databundle));
        return id;
    }

    public int createTenant(String name, ConfigCloudApi.TenantState state) {
        Bundle databundle = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        databundle.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        databundle.putString(MeshConstants.EXTRA_TENANT_NAME, name);
        databundle.putInt(MeshConstants.EXTRA_TENANT_STATE, state.ordinal());
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CLOUD_CREATE_TENANT, databundle));
        return id;
    }

    public static int createTenant(String tenantId, String name, ConfigCloudApi.TenantState state) {
        Bundle databundle = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        databundle.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        databundle.putString(MeshConstants.EXTRA_TENANT_ID, tenantId);
        databundle.putString(MeshConstants.EXTRA_TENANT_NAME, name);
        databundle.putInt(MeshConstants.EXTRA_TENANT_STATE, state.ordinal());
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CLOUD_CREATE_TENANT, databundle));
        return id;
    }

    public static int getTenantInfo(String tenantId) {
        Bundle databundle = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        databundle.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        databundle.putString(MeshConstants.EXTRA_TENANT_ID, tenantId);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CLOUD_GET_TENANT_INFO, databundle));
        return id;
    }

    public static int deleteTenant(String tenantId) {
        Bundle databundle = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        databundle.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        databundle.putString(MeshConstants.EXTRA_TENANT_ID, tenantId);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CLOUD_DELETE_TENANT, databundle));
        return id;
    }

    public static int updateTenant(String tenantId, String name, ConfigCloudApi.TenantState state) {
        Bundle databundle = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        databundle.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        databundle.putString(MeshConstants.EXTRA_TENANT_ID, tenantId);
        databundle.putString(MeshConstants.EXTRA_TENANT_NAME, name);
        databundle.putInt(MeshConstants.EXTRA_TENANT_STATE, state.ordinal());
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CLOUD_UPDATE_TENANT, databundle));
        return id;
    }

    public static int getSites(String tenantId, ConfigCloudApi.QueryType queryType, String pattern,
                               ConfigCloudApi.TenantState state, Integer pageSize, Integer page, Integer maximum) {
        Bundle databundle = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        databundle.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        databundle.putString(MeshConstants.EXTRA_TENANT_ID, tenantId);
        databundle.putInt(MeshConstants.EXTRA_QUERY_TYPE, queryType.ordinal());
        databundle.putString(MeshConstants.EXTRA_SEARCH_PATTERN, pattern);
        databundle.putInt(MeshConstants.EXTRA_TENANT_STATE, state.ordinal());
        databundle.putInt(MeshConstants.EXTRA_PAGE_SIZE, pageSize);
        databundle.putInt(MeshConstants.EXTRA_PAGE_NO, page);
        databundle.putInt(MeshConstants.EXTRA_MAX_RESULTS, maximum);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CLOUD_GET_SITES, databundle));
        return id;
    }


    public int createSite(String tenantId, String name, ConfigCloudApi.SiteState state, List<SiteInfo> meshes) {
        Bundle databundle = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        databundle.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        databundle.putString(MeshConstants.EXTRA_TENANT_ID, tenantId);
        databundle.putString(MeshConstants.EXTRA_SITE_NAME, name);
        databundle.putInt(MeshConstants.EXTRA_SITE_STATE, state.ordinal());
        databundle.putParcelableArrayList(MeshConstants.EXTRA_SITE_LIST, (ArrayList<SiteInfo>) meshes);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CLOUD_CREATE_SITE, databundle));
        return id;
    }

    public static int createSite(String tenantId, String siteId, String name, ConfigCloudApi.SiteState state, List<SiteInfo> meshes) {
        Bundle databundle = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        databundle.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        databundle.putString(MeshConstants.EXTRA_TENANT_ID, tenantId);
        databundle.putString(MeshConstants.EXTRA_SITE_ID, siteId);
        databundle.putString(MeshConstants.EXTRA_SITE_NAME, name);
        databundle.putInt(MeshConstants.EXTRA_SITE_STATE, state.ordinal());
        databundle.putParcelableArrayList(MeshConstants.EXTRA_SITE_LIST, (ArrayList<SiteInfo>) meshes);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CLOUD_CREATE_SITE, databundle));
        return id;
    }

    public static int getSiteInfo(String tenantId, String siteId) {
        Bundle databundle = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        databundle.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        databundle.putString(MeshConstants.EXTRA_TENANT_ID, tenantId);
        databundle.putString(MeshConstants.EXTRA_SITE_ID, siteId);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CLOUD_GET_SITE_INFO, databundle));
        return id;
    }

    public static int deleteSite(String tenantId, String siteId) {
        Bundle databundle = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        databundle.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        databundle.putString(MeshConstants.EXTRA_TENANT_ID, tenantId);
        databundle.putString(MeshConstants.EXTRA_SITE_ID, siteId);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CLOUD_DELETE_SITE, databundle));
        return id;
    }

    public static int updateSite(String tenantId, String siteId, String name, ConfigCloudApi.SiteState state, List<SiteInfo> meshes) {
        Bundle databundle = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        databundle.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        databundle.putString(MeshConstants.EXTRA_TENANT_ID, tenantId);
        databundle.putString(MeshConstants.EXTRA_SITE_ID, siteId);
        databundle.putString(MeshConstants.EXTRA_SITE_NAME, name);
        databundle.putInt(MeshConstants.EXTRA_SITE_STATE, state.ordinal());
        databundle.putParcelableArrayList(MeshConstants.EXTRA_SITE_LIST, (ArrayList<SiteInfo>) meshes);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CLOUD_UPDATE_SITE, databundle));
        return id;
    }

    static void handleRequest(MeshRequestEvent event) {
        int libId = 0;
        switch (event.what) {
            case CLOUD_GET_TENANTS: {
                ConfigCloudApi.QueryType type =
                        ConfigCloudApi.QueryType.values()[event.data.getInt(MeshConstants.EXTRA_QUERY_TYPE)];
                String pattern = event.data.getString(MeshConstants.EXTRA_SEARCH_PATTERN);
                ConfigCloudApi.TenantState state
                        = ConfigCloudApi.TenantState.values()[event.data.getInt(MeshConstants.EXTRA_TENANT_STATE)];
                int pageSize = event.data.getInt(MeshConstants.EXTRA_PAGE_SIZE);
                int pageNo = event.data.getInt(MeshConstants.EXTRA_PAGE_SIZE);
                int max = event.data.getInt(MeshConstants.EXTRA_MAX_RESULTS);
                libId = ConfigCloudApi.getTenants(type, pattern, state, pageSize, pageNo, max);
                break;
            }
            case CLOUD_CREATE_TENANT: {
                String name = event.data.getString(MeshConstants.EXTRA_TENANT_NAME);
                ConfigCloudApi.TenantState state
                        = ConfigCloudApi.TenantState.values()[event.data.getInt(MeshConstants.EXTRA_TENANT_STATE)];
                String tenantId = event.data.getString(MeshConstants.EXTRA_TENANT_ID);
                if (tenantId == null) {
                    libId = ConfigCloudApi.createTenant(name, state);
                }
                else {
                    libId = ConfigCloudApi.createTenant(tenantId, name, state);
                }
                break;
            }
            case CLOUD_GET_TENANT_INFO: {
                String tenantId = event.data.getString(MeshConstants.EXTRA_TENANT_ID);
                libId = ConfigCloudApi.getTenantInfo(tenantId);
                break;
            }
            case CLOUD_DELETE_TENANT: {
                String tenantId = event.data.getString(MeshConstants.EXTRA_TENANT_ID);
                libId = ConfigCloudApi.deleteTenant(tenantId);
                break;
            }
            case CLOUD_UPDATE_TENANT: {
                String tenantId = event.data.getString(MeshConstants.EXTRA_TENANT_ID);
                String name = event.data.getString(MeshConstants.EXTRA_TENANT_NAME);
                ConfigCloudApi.TenantState state
                        = ConfigCloudApi.TenantState.values()[event.data.getInt(MeshConstants.EXTRA_TENANT_STATE)];
                libId = ConfigCloudApi.updateTenant(tenantId, name, state);
                break;
            }
            case CLOUD_GET_SITES: {
                String tenantId = event.data.getString(MeshConstants.EXTRA_TENANT_ID);
                ConfigCloudApi.QueryType type =
                        ConfigCloudApi.QueryType.values()[event.data.getInt(MeshConstants.EXTRA_QUERY_TYPE)];
                String pattern = event.data.getString(MeshConstants.EXTRA_SEARCH_PATTERN);
                ConfigCloudApi.SiteState state
                        = ConfigCloudApi.SiteState.values()[event.data.getInt(MeshConstants.EXTRA_SITE_STATE)];
                int pageSize = event.data.getInt(MeshConstants.EXTRA_PAGE_SIZE);
                int pageNo = event.data.getInt(MeshConstants.EXTRA_PAGE_SIZE);
                int max = event.data.getInt(MeshConstants.EXTRA_MAX_RESULTS);
                libId = ConfigCloudApi.getSites(tenantId, type, pattern, state, pageSize, pageNo, max);
                break;
            }
            case CLOUD_CREATE_SITE: {
                String tenantId = event.data.getString(MeshConstants.EXTRA_TENANT_ID);
                String siteId = event.data.getString(MeshConstants.EXTRA_SITE_ID);
                String name = event.data.getString(MeshConstants.EXTRA_SITE_NAME);
                ConfigCloudApi.SiteState state
                        = ConfigCloudApi.SiteState.values()[event.data.getInt(MeshConstants.EXTRA_SITE_STATE)];
                ArrayList<SiteInfo> meshes = event.data.getParcelableArrayList(MeshConstants.EXTRA_SITE_LIST);
                if (siteId == null) {
                    libId = ConfigCloudApi.createSite(tenantId, name, state, meshes);
                }
                else {
                    libId = ConfigCloudApi.createSite(tenantId, siteId, name, state, meshes);
                }
                break;
            }
            case CLOUD_GET_SITE_INFO: {
                String tenantId = event.data.getString(MeshConstants.EXTRA_TENANT_ID);
                String siteId = event.data.getString(MeshConstants.EXTRA_SITE_ID);
                libId = ConfigCloudApi.getSiteInfo(tenantId, siteId);
                break;
            }
            case CLOUD_DELETE_SITE: {
                String tenantId = event.data.getString(MeshConstants.EXTRA_TENANT_ID);
                String siteId = event.data.getString(MeshConstants.EXTRA_SITE_ID);
                libId = ConfigCloudApi.deleteSite(tenantId, siteId);
                break;
            }
            case CLOUD_UPDATE_SITE: {
                String tenantId = event.data.getString(MeshConstants.EXTRA_TENANT_ID);
                String siteId = event.data.getString(MeshConstants.EXTRA_SITE_ID);
                String name = event.data.getString(MeshConstants.EXTRA_SITE_NAME);
                ConfigCloudApi.SiteState state
                        = ConfigCloudApi.SiteState.values()[event.data.getInt(MeshConstants.EXTRA_SITE_STATE)];
                ArrayList<SiteInfo> meshes = event.data.getParcelableArrayList(MeshConstants.EXTRA_SITE_LIST);
                libId = ConfigCloudApi.updateSite(tenantId, siteId, name, state, meshes);
                break;
            }
        }
        if (libId != 0) {
            int internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
            MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
        }
    }
}
