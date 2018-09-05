/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.het.csrmesh.model.devices;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public  class LargeObjectTransfer implements Parcelable {

    private String companyCode;
    private String typeEncoding;
    private String lotSize;
    private String targetDestination;
    private String deviceId;
    private String serviceId;


    public LargeObjectTransfer(String serviceId , String deviceId, String companyCode, String typeEncoding, String lotSize, String targetDestination) {
        this.deviceId = deviceId;
        this.companyCode = companyCode;
        this.companyCode = typeEncoding;
        this.companyCode = lotSize;
        this.companyCode = targetDestination;
        this.serviceId=serviceId;
    }



    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getTypeEncoding() {
        return typeEncoding;
    }

    public void setTypeEncoding(String typeEncoding) {
        this.typeEncoding = typeEncoding;
    }

    public String getLotSize() {
        return lotSize;
    }

    public void setLotSize(String lotSize) {
        this.lotSize = lotSize;
    }

    public String getTargetDestination() {
        return targetDestination;
    }

    public void setTargetDestination(String targetDestination) {
        this.targetDestination = targetDestination;
    }
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    protected LargeObjectTransfer(Parcel in) {
        deviceId = in.readString();
        companyCode = in.readString();
        typeEncoding = in.readString();
        lotSize = in.readString();
        targetDestination = in.readString();
        serviceId=in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceId);
        dest.writeString(companyCode);
        dest.writeString(typeEncoding);
        dest.writeString(lotSize);
        dest.writeString(targetDestination);
        dest.writeString(serviceId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LargeObjectTransfer> CREATOR = new Parcelable.Creator<LargeObjectTransfer>() {
        @Override
        public LargeObjectTransfer createFromParcel(Parcel in) {
            return new LargeObjectTransfer(in);
        }

        @Override
        public LargeObjectTransfer[] newArray(int size) {
            return new LargeObjectTransfer[size];
        }

    };
}