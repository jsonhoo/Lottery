/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.het.csrmesh.api;

/**
 * APIs that are specific to the Bluetooth bearer.
 */
public class BluetoothChannel {

    public static void disconnect() {
        MeshLibraryManager.getInstance().getMeshService().shutDown();
    }

    public static void setContinuousScanEnabled(boolean enabled) {
        MeshLibraryManager.getInstance().getMeshService().setContinuousLeScanEnabled(enabled);
    }

    public static void killTransaction(int requestId) {
        int meshRequestId = MeshLibraryManager.getInstance().getMeshRequestId(requestId);
        if (meshRequestId != 0) {
            MeshLibraryManager.getInstance().getMeshService().cancelTransaction(meshRequestId);
        } else {
            throw new IllegalArgumentException("Request databaseId is not valid.");
        }
    }

    public static void cancelTransaction(int requestId) {
        if (MeshLibraryManager.getInstance() == null) {
            return;
        }
        int meshRequestId = MeshLibraryManager.getInstance().getMeshRequestId(requestId);
        if (meshRequestId != 0) {

            MeshLibraryManager.getInstance().getMeshService().cancelTransaction(meshRequestId);
        } else {
            throw new IllegalArgumentException("Request databaseId is not valid.");
        }
    }
}
