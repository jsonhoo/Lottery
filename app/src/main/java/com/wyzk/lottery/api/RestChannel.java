/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.csr.csrmesh2.MeshService;
import com.wyzk.lottery.R;


/**
 * Class that contains all the API calls related to the Rest channel.
 */
public class RestChannel {

    public enum RestMode {
        GATEWAY,
        CLOUD
    }

/*
    // PRODUCTION server (test)
    private static final String CLOUD_HOST = "csrmesh-test.csrlbs.com"; // test
    private static final String REST_APPLICATION_CODE = "c007b500-cbbd-437d-8594-59b5fec32d81"; // Used for server csrmesh-test.csrlbs.com
*/

    // PRODUCTION server (2.1)
    private static final String CLOUD_HOST = "csrmesh-2-1.csrlbs.com"; //"c007b500-cbbd-437d-8594-59b5fec32d81"  2.1 server
    private static final String REST_APPLICATION_CODE = "app_123"; // Used for server csrmesh-test.csrlbs.com

/*
    // DEV server (test2)
    private static final String CLOUD_HOST = "csrmesh-test2.csrlbs.com";
    private static final String REST_APPLICATION_CODE = "733c2dce-91dd-429e-ba23-ada10c855ddf"; // Used for server csrmesh-test2.csrlbs.com
*/

/*
    // QA server
    private static final String CLOUD_HOST = "qacsrmesh-test.csrlbs.com";
    private static final String REST_APPLICATION_CODE = "app_123"; // Used for server csrmesh-test2.csrlbs.com
*/


    private static final String CLOUD_PORT = "443";
    public static final String GATEWAY_HOST = "192.168.1.1";
    public static final String GATEWAY_PORT = "80";
    public static final String BASE_PATH_AAA = "/csrmesh/aaa"; //"/cgi-bin/csrmesh/aaa";
    public static final String BASE_PATH_CNC = "/csrmesh/cnc"; ///cgi-bin/csrmesh/cnc";
    public static final String BASE_PATH_CONFIG = "/csrmesh/config"; ///cgi-bin/csrmesh/config";
    public static final String BASE_PATH_AUTH = "/csrmesh/security";

    public static final String TENANT_NAME = "tenantid_123";
    public static final String SITE_NAME = "siteid_123";
    public static final String MESH_ID = "mesg456";
    public static final String DEVICE_ID = "12";

    public static final String BASE_PATH_CGI = "/cgi-bin";
    public static final String URI_SCHEMA_HTTP = "http";
    public static final String URI_SCHEMA_HTTPS = "https";
    public static final String BASE_CSRMESH = "/csrmesh";

    /**
     * Method to retrieve the host stored in the cached variables or return the default value
     *
     * @param context
     * @return Host to be used by the app
     */
    public static String getCloudHost(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String storedCloudUrl = sharedPref.getString(context.getString(R.string.pref_cloud_key_url), "");
        if (storedCloudUrl.equals("")) {
            return CLOUD_HOST;
        } else {
            return storedCloudUrl;
        }
    }

    /**
     * Method to retrieve the port stored in the cached variables or return the default value
     *
     * @param context
     * @return Port to be used by the app
     */
    public static String getCloudPort(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String storedCloudPort = sharedPref.getString(context.getString(R.string.pref_cloud_key_port), "");
        if (storedCloudPort.equals("")) {
            return CLOUD_PORT;
        } else {
            return storedCloudPort;
        }
    }


    /**
     * Method to retrieve the Appcode stored in the cached variables or return the default value
     *
     * @param context
     * @return Appcode to be used by the app
     */
    public static String getCloudAppcode(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String storedCloudAppcode = sharedPref.getString(context.getString(R.string.pref_cloud_key_appcode), "");
        if (storedCloudAppcode.equals("")) {
            return REST_APPLICATION_CODE;
        } else {
            return storedCloudAppcode;
        }
    }

    public static void setRestParameters(MeshService.ServerComponent serverComponent, String host, String port, String basePath, String uriScheme) {
        Log.d("edutest", "serverComponent:" + serverComponent.toString() + " - host:" + host + " - port:" + port + " - basePath:" + basePath + " - uriScheme:" + uriScheme);
        if (MeshLibraryManager.getInstance().getMeshService() != null)
            MeshLibraryManager.getInstance().getMeshService().setRestParams(serverComponent, host, port, basePath, uriScheme);
    }

    public static void setTenant(String tenantId) {
        if (MeshLibraryManager.getInstance().getMeshService() != null)
            MeshLibraryManager.getInstance().getMeshService().setTenantId(tenantId);
    }

    public static void setSite(String siteId) {
        if (MeshLibraryManager.getInstance().getMeshService() != null)
            MeshLibraryManager.getInstance().getMeshService().setSiteId(siteId);
    }
}
