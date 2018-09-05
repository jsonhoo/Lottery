/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.het.csrmesh.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;

/**
 *
 */
public class ConnectionUtils {

    public static final String INTERNET_AVAILABLE = "internet_available";
    public static int TYPE_OFFLINE = 0;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    private static boolean mInternetAvailable;

    /**
     * Retrieve the current type of connection
     * @param context
     * @return Current type of connection
     */
    public static int getConnectionType(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected()) return TYPE_MOBILE;

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) return TYPE_WIFI;
        }
        return TYPE_OFFLINE;
    }

    /**
     * Ensure wifi is enabled
     * @param context
     * @return true if the current connection type is WIFI, false otherwise
     */
    public static boolean isWifiEnabled(Context context) {
        return getConnectionType(context) == TYPE_WIFI;
    }

    /**
     * Ping CSR server to determine if server is reachable
     * @param handler Handler which will handle the response after the Ping is completed
     */
    public static void isCSRServerAvailable(final Handler handler) {

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                //code to do the HTTP request
                Runtime runtime = Runtime.getRuntime();
                try {
                    String command = "/system/bin/ping -c 1 csrmesh-test.csrlbs.com";
                    Process ipProcess = runtime.exec(command); // Pinging CSR server
                    int     exitValue = ipProcess.waitFor();
                    mInternetAvailable = (exitValue == 0);

                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putBoolean(INTERNET_AVAILABLE, mInternetAvailable);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * Ping largest public DNS server to determine if internet is actually available
     * @param handler Handler which will handle the response after the Ping is completed
     */
    public static void isInternetAvailable(final Handler handler) {

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                //code to do the HTTP request
                Runtime runtime = Runtime.getRuntime();
                try {

                    Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8"); // Pinging Google DNS (the larges public DNS in the world)
                    int     exitValue = ipProcess.waitFor();
                    mInternetAvailable = (exitValue == 0);

                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putBoolean(INTERNET_AVAILABLE, mInternetAvailable);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
