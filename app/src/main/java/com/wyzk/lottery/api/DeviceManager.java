/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.wyzk.lottery.api;

import android.content.Context;

import com.squareup.otto.Subscribe;
import com.uuxia.email.Logc;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.database.DBManager;
import com.wyzk.lottery.event.MeshSystemEvent;
import com.wyzk.lottery.model.Device;
import com.wyzk.lottery.model.Place;
import com.wyzk.lottery.utils.ApplicationUtils;
import com.wyzk.lottery.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/***
 *
 */
public class DeviceManager {

    private static final String TAG = DeviceManager.class.getSimpleName();
    private static DeviceManager mDeviceManager;
    private static Context context;
    private boolean isFirst = true;

    public boolean isFirst() {
        return isFirst;
    }

    public static DeviceManager getInstence(Context context, DBManager dBManager) {
        if (mDeviceManager == null) {
            mDeviceManager = new DeviceManager(context, dBManager);
        }
        return mDeviceManager;
    }

    private HashMap<Integer, Device> mDevices = new HashMap<>();
    private DBManager mDBManager;


    public DeviceManager(Context context, DBManager dBManager) {
        this.context = context;
        isFirst = true;
        mDBManager = dBManager;
        LotteryApplication.bus.register(this);
    }

    /**
     * 1.获取网络的设备列表
     * 2.获取到placeId之后就去拉取游戏分组
     */
    public void initDevicelist() {
        List<Device> deviceList = mDBManager.getAllDevicesList();
        for (int i = 0; i < deviceList.size(); i++) {
            mDevices.put(deviceList.get(i).getDatabaseId(), deviceList.get(i));
        }
        return;
    }


    public Device getDeviceById(int id) {
        return mDevices.get(id);
    }

    public Device getDeviceByUUid(String uuid) {
        if (mDevices != null) {
            Iterator iter = mDevices.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Device device = (Device) entry.getValue();
                if (device != null && device.getUuid().toString().equalsIgnoreCase(uuid)) {
                    return device;
                }
            }
        }
        return null;
    }

    /**
     * 解除组网下的所有设备
     */
    public void reMoveAllDevice() {
        Place place = Utils.getLatestPlaceIdUsed();
        if (place != null) {
            mDBManager.removeAllDevicesByPlaceId(Utils.getLatestPlaceIdUsed().getPlaceId());
            mDevices.clear();
        }
    }


    public List<Device> getAllDevicesList() {
        return ApplicationUtils.sortDevicesListAlphabetically(new ArrayList<>(mDevices.values()));
    }

    public Device createOrUpdateDevice(Device tempDevice, boolean updateDataBase) {
        if (tempDevice.getUuid() == null) return tempDevice;
        if (updateDataBase) {
            Device device = mDBManager.createOrUpdateDevice(tempDevice);
            mDevices.put(device.getDatabaseId(), device);       //http create a device

            return device;
        } else {
            mDevices.put(tempDevice.getDatabaseId(), tempDevice);
            return tempDevice;
        }
    }


    public List<Integer> getAllDevicesIDsList() {
        return mDBManager.getAllDevicesAndControllersIDsList();
    }

    public void removeDevice(int id) {
        Device device = mDBManager.getDevice(id);
        if (device != null) {
            mDBManager.removeDevice(id);
            mDevices.remove(id);
        } else {
            Logc.e(TAG, "设备不存在...");
        }
    }

    public void refreshInfo() {
        List<Device> deviceList = mDBManager.getAllDevicesList();
        mDevices.clear();
        for (int i = 0; i < deviceList.size(); i++) {
            mDevices.put(deviceList.get(i).getDatabaseId(), deviceList.get(i));
        }
    }

    @Subscribe
    public void onEvent(MeshSystemEvent event) {
        switch (event.what) {
            case PLACE_CHANGED:
                refreshInfo();
                break;
        }
    }
}
