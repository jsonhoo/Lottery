/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.het.csrmesh.api;

import android.os.Bundle;

import com.csr.csrmesh2.MeshConstants;
import com.het.csrmesh.App;
import com.het.csrmesh.events.MeshRequestEvent;

/**
 *  Large Object Transfer Model ability to exchange information between nodes such as to allow a peer-to-peer data transfer to be established after
 *  a negotiation phase.
 */
public class LargeObjectTransferModel {

    public static int announceRequest(final int deviceId, int companyCode, int platformType, int typeEncoding, int imageType, int lotSize,int objectVersion, int targetDestination) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_COMPANY_CODE, companyCode);
        data.putInt(MeshConstants.EXTRA_PLATFORM_TYPE, platformType);
        data.putInt(MeshConstants.EXTRA_TYPE_ENCODING, typeEncoding);
        data.putInt(MeshConstants.EXTRA_IMAGE_TYPE, imageType);
        data.putInt(MeshConstants.EXTRA_LOT_SIZE, lotSize);
        data.putInt(MeshConstants.EXTRA_OBJECT_VERSION, objectVersion);
        data.putInt(MeshConstants.EXTRA_TARGET_DESTINATION, targetDestination);

        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.LOT_ANNOUNCE, data));
        return id;
    }

    public static int interestRequest(final int deviceId, int serviceId) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_INTEREST_ID, serviceId);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.LOT_INTEREST, data));
        return id;
    }


    static void handleRequest(MeshRequestEvent event) {
        int libId = -1;
        int internalId;
        int deviceId, companyCode, platformType, typeEncoding, imageType, lotSize, objectVersion, targetDestination, serviceId;

        switch (event.what) {
            case LOT_INTEREST:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                serviceId = event.data.getInt(MeshConstants.EXTRA_INTEREST_ID);

                // Do API call
                libId = LargeObjectTransferModel.interestRequest(deviceId, serviceId);
                break;

            case LOT_ANNOUNCE:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                companyCode = event.data.getInt(MeshConstants.EXTRA_COMPANY_CODE);
                platformType = event.data.getInt(MeshConstants.EXTRA_PLATFORM_TYPE);
                typeEncoding = event.data.getInt(MeshConstants.EXTRA_TYPE_ENCODING);
                imageType = event.data.getInt(MeshConstants.EXTRA_IMAGE_TYPE);
                lotSize = event.data.getInt(MeshConstants.EXTRA_LOT_SIZE);
                objectVersion=event.data.getInt(MeshConstants.EXTRA_OBJECT_VERSION);
                targetDestination = event.data.getInt(MeshConstants.EXTRA_TARGET_DESTINATION);

                // Do API call
                libId = LargeObjectTransferModel.announceRequest(deviceId, companyCode, platformType, typeEncoding, imageType, lotSize, objectVersion, targetDestination);
                break;

            default:
                break;
        }

        if (libId != -1) {
            internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
            MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
        }
    }
}
