package com.wyzk.lottery.api.Exceptions;

/**
 * Created by Qualcomm on 29/06/16.
 * indicates that Bluetooth Low Energy is not available on this device
 */
public class BleNotAvailableException extends RuntimeException {
    public BleNotAvailableException(String message) {
        super(message);
    }
}
