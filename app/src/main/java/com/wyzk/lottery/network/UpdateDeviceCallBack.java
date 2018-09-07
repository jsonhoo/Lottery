package com.wyzk.lottery.network;


import com.wyzk.lottery.api.ScanDevice;

import java.util.List;

/**
 * Created by Administrator on 2017-05-12.
 */

public interface UpdateDeviceCallBack {
    void onUpdataDevices(List<ScanDevice> devices);

    void onDevicesChange(int numb);

    void onShowInfo(String msg);

    void onShowPros(int pros);

    void onShowState(int state);
}
