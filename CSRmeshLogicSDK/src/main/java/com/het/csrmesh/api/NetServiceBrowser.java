/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.het.csrmesh.api;

import com.het.csrmesh.App;
import com.het.csrmesh.events.MeshRequestEvent;

/**
 * Class that contains the API calls for bonjour protocol.
 */
public class NetServiceBrowser {

    /**
     * Start browsing for services using Bonjour.
     * Response event is GATEWAY_DISCOVERED with data:
     * <ul>
     * <li>EXTRA_GATEWAY_HOST</li>
     * <li>EXTRA_GATEWAY_PORT</li>
     * </ul>
     */
    public static void startBrowsing() {
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.START_BROWSING_GATEWAYS));
    }

    public static void stopBrowsing() {
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.STOP_BROWSING_GATEWAYS));
    }

    // Request is handled in MeshLibraryManager as no request databaseId needs to be mapped for these calls.
}
