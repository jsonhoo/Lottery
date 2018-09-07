package com.wyzk.lottery.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.csr.csrmesh2.DeviceInfo;
import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.MeshService;
import com.squareup.otto.Subscribe;
import com.uuxia.email.Logc;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.R;
import com.wyzk.lottery.api.Association;
import com.wyzk.lottery.api.ConfigModel;
import com.wyzk.lottery.api.DeviceManager;
import com.wyzk.lottery.api.GroupModel;
import com.wyzk.lottery.api.ScanDevice;
import com.wyzk.lottery.constant.Constants;
import com.wyzk.lottery.database.DBManager;
import com.wyzk.lottery.database.UnknownDevice;
import com.wyzk.lottery.event.MeshResponseEvent;
import com.wyzk.lottery.event.MeshSystemEvent;
import com.wyzk.lottery.model.AppearanceDevice;
import com.wyzk.lottery.model.Device;
import com.wyzk.lottery.ui.AutoBindDeviceActivity;
import com.wyzk.lottery.utils.ByteUtils;
import com.wyzk.lottery.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-09-07<br>
 * 描述:
 **/
public class AutoScanBindManager {
    private static final String TAG = AutoScanBindManager.class.getSimpleName();
    private static AutoScanBindManager mAutoScanBindManager;
    private Context context;
    private static final int SCANNING_PERIOD_MS = 10 * 1000;
    private static final String GATEWAY_FILTER = "csrmeshgw";

    public static final int FIRST_ID_IN_RANGE = 0x8001;
    private ArrayList<ScanDevice> mDiscoveredDevices = new ArrayList<ScanDevice>();
    private HashMap<Integer, AppearanceDevice> mAppearances = new HashMap<Integer, AppearanceDevice>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ScanLeCallBackThd scanLeCallBackThd;
    private Queue<Integer> mModelsToQueryForGroups = new LinkedList<Integer>();
    private Queue<ScanDevice> mScanDeviceGroups = new LinkedList<ScanDevice>();
    private Device mTempDevice;
    private DeviceInfo mCurrentRequestState = null;
    protected DBManager mDBManager;
    protected DeviceManager mDeviceManager;
    private int mNumPostAssociationSteps;
    // The current step.
    private int mPostAssociationStep;
    // This is the percentage shown on the progress bar for the MASP association part.
    private static final int ASSOCIATION_COMPLETE_PERCENT = 50;
    // This is the percentage shown on the progress bar for querying the appearance and the model support.
    // The remainder of the percentage is used for querying the number of groups per model.
    private static final int APPEARANCE_AND_MODELS_COMPLETE_PERCENT = 60;
    //    ProgressBarDeterminateMaterial mDeterminateProgressBar;
//    DialogMaterial mDialog = null;
//    TextView progressText = null;
    private UpdateDeviceCallBack updateDeviceCallBack;
    private ScanDevice mScanDevice;
    private int scanBindNumb = 0;

    public boolean isCanBack() {
        return isCanBack;
    }

    private boolean isCanBack = true;

    public void setUpdateDeviceCallBack(UpdateDeviceCallBack updateDeviceCallBack) {
        this.updateDeviceCallBack = updateDeviceCallBack;
    }

    public static AutoScanBindManager getInstence(Context context) {
        if (mAutoScanBindManager == null) {
            mAutoScanBindManager = new AutoScanBindManager(context);
        }
        return mAutoScanBindManager;
    }

    public AutoScanBindManager(Context context) {
        this.context = context;
        LotteryApplication.bus.register(this);
        mDBManager = DBManager.getInstence(LotteryApplication.get(context));
        mDeviceManager = DeviceManager.getInstence(LotteryApplication.get(context), mDBManager);
    }

    public void onDestroy() {
        if (scanLeCallBackThd != null) {
            scanLeCallBackThd.removeHandlerMsg();
            scanLeCallBackThd = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages((Object) null);
            mHandler = null;
        }
        setDeviceScanEnabled(false);
        mAutoScanBindManager = null;
        LotteryApplication.bus.unregister(this);
    }

    public void start() {
        LotteryApplication.bus.register(this);
        showInfo("开始扫描设备...");
        startScan();
    }

    public void startScan() {//开始扫描
        if (updateDeviceCallBack != null) {
            updateDeviceCallBack.onShowState(1);
        }
        scanLeCallBackThd = new ScanLeCallBackThd(SCANNING_PERIOD_MS, context, () -> {
            //开始绑定
            scanLeCallBackThd.removeHandlerMsg();
            startBind();
        });
        scanLeCallBackThd.notifyScanStarted();
    }

    private void startBind() {
        ((AutoBindDeviceActivity) context).runOnUiThread(() -> {
            if (!mScanDeviceGroups.isEmpty()) {
                if (updateDeviceCallBack != null) {
                    updateDeviceCallBack.onShowState(2);
                }
                mScanDevice = mScanDeviceGroups.peek();
                associateDevice(mScanDevice);
            } else {
                setDeviceScanEnabled(false);
                mHandler.postDelayed(() -> {
                    startScan();//重新开始扫描 绑定
                }, 200);
            }
        });
    }

    /**
     * Make the CSRmesh library call to start scanning for devices that can be associated.
     * Also update the UI to show this.
     */
    public void setDeviceScanEnabled(boolean enabled) {
        if (enabled) {
            // We will disable scanning after this timeout.
            Association.discoverDevices(true);
        } else {
            Association.discoverDevices(false);
        }
    }

    private void updateList() {
        ArrayList<ScanDevice> devices = getDiscoveredDevices();
        for (int i = 0; i < devices.size(); i++) {
            Logc.e(TAG, "发现设备 i:" + i + " = " + devices.get(i).toString());
            showInfo("发现设备 i:" + i + " = " + devices.get(i).uuid);
        }
        mScanDeviceGroups.clear();
        mScanDeviceGroups.addAll(devices);
        upScanDevices(devices);
    }

    public void upScanDevices(ArrayList<ScanDevice> devices) {
        if (updateDeviceCallBack != null) {
            updateDeviceCallBack.onUpdataDevices(devices);
        }
    }

    public void showInfo(String msg) {
        if (updateDeviceCallBack != null) {
            updateDeviceCallBack.onShowInfo(msg);
        }
    }

    public ArrayList<ScanDevice> getDiscoveredDevices() {
        ArrayList<ScanDevice> devices = new ArrayList<>();
        Iterator<ScanDevice> iterator = mDiscoveredDevices.iterator();
        // Use iterator rather than for each to prevent null pointer when deleting devices.
        while (iterator.hasNext()) {
            ScanDevice scanDevice = iterator.next();

            // Apply a filter.
            if ((scanDevice.hasAppearance() && scanDevice.getAppearanceDevice().getAppearanceType() != AppearanceDevice.GATEWAY_APPEARANCE
                    && !scanDevice.getName().toLowerCase().contains(GATEWAY_FILTER))
                    && scanDevice.getAppearanceDevice().getAppearanceType() != AppearanceDevice.CONTROLLER_APPEARANCE) {
                devices.add(scanDevice);
            }
        }
        return devices;
    }

    public void associateDevice(final ScanDevice device) {
        Logc.e(TAG, "associateDevice--->");
        showInfo("设置扫描时间：" + SCANNING_PERIOD_MS + "s" + "开始绑定设备 ： " + device.getName());
        startAssociation(device);
    }


    // The association id that we get from the library when associating. It can be used to cancel.
    private int mAssociationRequestId;
    private static final int ASSOCIATION_TIMEOUT_MS = 30 * 1000;

    private void startAssociation(ScanDevice device) {
        if (((AutoBindDeviceActivity) context).isFinishing()) {
            return;
        }
        List<Integer> devicesIdList = mDeviceManager.getAllDevicesIDsList();
        int deviceId = FIRST_ID_IN_RANGE;
        if (devicesIdList.size() > 0) {
            deviceId = devicesIdList.get(devicesIdList.size() - 1) + 1;//分配deviceId给mesh网络的设备Id
        }
        Logc.e(TAG, "startAssociation deviceId = " + deviceId);
        mDeviceAssociated = false;
        mHandler.postDelayed(mAssociationTimeout, ASSOCIATION_TIMEOUT_MS);
        mAssociationRequestId = Association.associateDevice(device.getUuidHash(), device.getAuthCode(), device.getAuthCode() != Constants.INVALID_VALUE, deviceId);
        AutoScanBindManager.this.isCanBack = false;
        updateProgress(0);
    }

    private void updateProgress(final int progress) {
        if (updateDeviceCallBack != null) {
            updateDeviceCallBack.onShowPros(progress);
        }
    }

    private String getString(int resId) {
        return resId != -1 ? context.getString(resId) : "not found this resId";
    }

    private void closeAssociationDialog() {
        if (updateDeviceCallBack != null) {
            updateDeviceCallBack.onShowState(3);
        }
    }


    private void associationComplete(final boolean success) {
        AutoScanBindManager.this.isCanBack = true;
        closeAssociationDialog();
        if (mScanDeviceGroups.size() > 0)
            mScanDeviceGroups.remove();
        if (mScanDevice != null) {
            mDiscoveredDevices.remove(mScanDevice);
        }
        upScanDevices(mDiscoveredDevices);
        mHandler.removeCallbacks(mAssociationTimeout);
        if (success) {
            if (mTempDevice != null && !TextUtils.isEmpty(mTempDevice.getName())) {
                if (updateDeviceCallBack != null)
                    updateDeviceCallBack.onDevicesChange(++scanBindNumb);
                Toast.makeText(context, mTempDevice.getName() + " " + getString(R.string.device_associated), Toast.LENGTH_SHORT).show();
                Logc.e(mTempDevice.getName() + " " + getString(R.string.device_associated));
                showInfo(mTempDevice.getName() + "绑定成功");
            }
        } else {
            if (mTempDevice != null && !TextUtils.isEmpty(mTempDevice.getName())) {
                Toast.makeText(context, getString(R.string.association_failed), Toast.LENGTH_SHORT).show();
                showInfo(mTempDevice.getName() + "失败");
            }
        }
        mHandler.postDelayed(() -> {
            startBind();
        }, 200);
    }

    /**
     * Get percentage of a value.
     *
     * @param percent The percentage (0 - 100).
     * @param value   The value to get the percentage of.
     * @return Value in range 0 to value.
     */
    private int getPercentageOf(int percent, int value) {
        Log.d(TAG, "* Associatio getPercentageOf - progress: " + percent);
        showInfo("* 绑定进度 ： --->" + Math.round((percent / 100.0f) * (float) value));
        return Math.round((percent / 100.0f) * (float) value);
    }

    private boolean mDeviceAssociated = false;
    private Runnable mAssociationTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mDeviceAssociated) {
                associationComplete(false);
            } else {
                Toast.makeText(context, getString(R.string.associate_config_query_fail)
                        , Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 监听设备桥连接是否断开
     * 不需要退出等待连接成功就好了  连接成功之后继续扫描绑定
     *
     * @param event
     */
    @Subscribe
    public void onEvent(MeshSystemEvent event) {
        switch (event.what) {
            case CHANNEL_NOT_READY: {
                Toast.makeText(context, getString(R.string.le_disconnected)
                        , Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    @Subscribe
    public void onEvent(MeshResponseEvent event) {
        if (event == null || event.data == null) {
            return;
        }
        switch (event.what) {
            case DEVICE_UUID: {
                ParcelUuid uuid = event.data.getParcelable(MeshConstants.EXTRA_UUID);
                Log.d(TAG, "* Association Event DEVICE_UUID - uuid: " + uuid.toString());
                Logc.e(TAG, "获取UUIDHash = " + MeshService.getDeviceHash31FromUuid(uuid.getUuid()));

                int uuidHash = event.data.getInt(MeshConstants.EXTRA_UUIDHASH_31);
                int rssi = event.data.getInt(MeshConstants.EXTRA_RSSI);
                int ttl = event.data.getInt(MeshConstants.EXTRA_TTL);
                boolean existing = false;
                for (ScanDevice info : mDiscoveredDevices) {
                    if (uuid != null && info.uuid.equalsIgnoreCase(uuid.toString())) {
                        info.rssi = rssi;
                        info.ttl = ttl;
                        // check if we already have appearance info according with the uuidHash
                        if (mAppearances.containsKey(uuidHash)) {
                            info.setAppearance(mAppearances.get(uuidHash));
                        }
                        info.updated();
                        updateList();
                        existing = true;
                        break;
                    }
                }
                if (!existing) {
                    ScanDevice info = new ScanDevice(uuid.toString().toUpperCase(), rssi, uuidHash, ttl);
                    // check if we already have appearance info according with the uuidHash
                    if (mAppearances.containsKey(uuidHash)) {
                        info.setAppearance(mAppearances.get(uuidHash));
                    }
                    mDiscoveredDevices.add(info);
                    updateList();
                }
                break;
            }
            case DEVICE_APPEARANCE: {

                byte[] appearance = event.data.getByteArray(MeshConstants.EXTRA_APPEARANCE);
                String shortName = event.data.getString(MeshConstants.EXTRA_SHORTNAME);
                Log.d(TAG, "* Association Event DEVICE_APPEARANCE - shortName: " + shortName);
                int uuidHash = event.data.getInt(MeshConstants.EXTRA_UUIDHASH_31);
                Log.d(TAG, "* Association Event DEVICE_APPEARANCE - uuidHash: " + uuidHash);
                for (ScanDevice info : mDiscoveredDevices) {
                    if (info.uuidHash == uuidHash) {
                        info.setAppearance(new AppearanceDevice(appearance, shortName));
                        info.updated();
                    }
                }
                mAppearances.put(uuidHash, new AppearanceDevice(appearance, shortName));
                Log.d(TAG, "* Association Event DEVICE_APPEARANCE - mAppearances: " + mAppearances.toString());
                updateList();

                break;
            }

            case ASSOCIATION_PROGRESS: {
                int progress = event.data.getInt(MeshConstants.EXTRA_PROGRESS_INFORMATION);
                Log.d(TAG, "* Association Event ASSOCIATION_PROGRESS - progress: " + progress);
                if (!event.data.getBoolean(MeshConstants.EXTRA_CAN_BE_CANCELLED)) {//绑定中 不可以取消了
//                    AutoScanBindManager.this.isCanBack = false;
                }
                updateProgress(getPercentageOf(progress, ASSOCIATION_COMPLETE_PERCENT));
                break;
            }
            case GROUP_NUMBER_OF_MODEL_GROUPIDS: {
                if (!mModelsToQueryForGroups.isEmpty()) {
                    int numIds = event.data.getInt(MeshConstants.EXTRA_NUM_GROUP_IDS);
                    int modelNo = event.data.getInt(MeshConstants.EXTRA_MODEL_NO);
                    int expectedModelNo = mModelsToQueryForGroups.peek();
                    int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);

                    Log.d(TAG, "* Association Event GROUP_NUMBER_OF_MODEL_GROUPIDS - numIds:" + numIds +
                            " - modelNo:" + modelNo +
                            " - expectedModelNo:" + expectedModelNo +
                            " - deviceId:" + deviceId);

                    if (expectedModelNo == modelNo && mTempDevice.getDeviceId() == deviceId) {
                        if (numIds > 0) {
                            int minNumGroups = mTempDevice.getNumGroups() <= 0 ? numIds : Math.min(mTempDevice.getNumGroups(), numIds);
                            mTempDevice.setNumGroups(minNumGroups);
                        }

                        // Update the progress counter.
                        mPostAssociationStep++;
                        updateProgress(APPEARANCE_AND_MODELS_COMPLETE_PERCENT +
                                getPercentageOf((mPostAssociationStep * 100) / mNumPostAssociationSteps,
                                        100 - APPEARANCE_AND_MODELS_COMPLETE_PERCENT));

                        // We know how many groups are supported for this model now so remove it from the queue.
                        mModelsToQueryForGroups.remove();
                        if (mModelsToQueryForGroups.isEmpty()) {
                            mTempDevice = mDeviceManager.createOrUpdateDevice(mTempDevice, true);
                            associationComplete(true);//关联成功
                        } else {
                            // Otherwise ask how many groups the next model supports, by taking the next model number from the queue.
                            GroupModel.getNumberOfModelGroupIds(mTempDevice.getDeviceId(), mModelsToQueryForGroups.peek());
                        }

                    }
                }
                break;
            }
            case DEVICE_ASSOCIATED: {
                /* Once association is completed we are approximately half way through the process.
                   We still need to query lots of info from the device using MCP.
                   The remaining 100 - ASSOCIATION_COMPLETE_PERCENT of the progress is used for that.*/
                updateProgress(ASSOCIATION_COMPLETE_PERCENT);


                int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                int uuidHash = event.data.getInt(MeshConstants.EXTRA_UUIDHASH_31);
                byte[] dhmKey = event.data.getByteArray(MeshConstants.EXTRA_RESET_KEY);

                mTempDevice = new UnknownDevice();
                mTempDevice.setDeviceId(deviceId);
                mTempDevice.setDeviceHash(uuidHash);
                for (ScanDevice info : mDiscoveredDevices) {
                    if (info.uuidHash == uuidHash) {
                        Logc.e(TAG, "uuidHash 相同" + uuidHash + " 设置uuid ：" + info.uuid);
                        mTempDevice.setUuid(info.uuid);
                    }
                }
                mTempDevice.setDmKey(ByteUtils.toHexString(dhmKey));
                mTempDevice.setPlaceID(Utils.getLatestPlaceIdUsed().getPlaceId());
                mTempDevice.setAssociated(true);
                AppearanceDevice appearanceDevice = mAppearances.get(uuidHash);

                Log.d(TAG, "* Association Event DEVICE_ASSOCIATED - appearanceDevice: " + appearanceDevice.toString());
                /* Set to 2 as we always have to query model low and high. We may or may not need appearance and
                   the number of models to query for groups is variable.*/
                mNumPostAssociationSteps = 2;
                mPostAssociationStep = 0;

                if (appearanceDevice != null) {
                    mTempDevice.setName(appearanceDevice.getShortName().trim() + " " + (deviceId - Constants.MIN_DEVICE_ID));
                    mTempDevice.setAppearance(appearanceDevice.getAppearanceType());


                    // Request model low from the device.
                    mCurrentRequestState = DeviceInfo.MODEL_LOW;
                    ConfigModel.getInfo(mTempDevice.getDeviceId(), DeviceInfo.MODEL_LOW);
                } else {
                    // An extra step as we don't have the appearance yet.
                    mNumPostAssociationSteps++;

                    mTempDevice.setName(getString(R.string.unknown) + " " + (deviceId - Constants.MIN_DEVICE_ID));
                    // Request appearance from the device.
                    mCurrentRequestState = DeviceInfo.APPEARANCE;
                    ConfigModel.getInfo(mTempDevice.getDeviceId(), DeviceInfo.APPEARANCE);
                }

                mTempDevice = mDeviceManager.createOrUpdateDevice(mTempDevice, true);
                break;
            }
            case CONFIG_INFO: {

                int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                DeviceInfo type = DeviceInfo.values()[event.data.getInt(MeshConstants.EXTRA_DEVICE_INFO_TYPE)];

                mPostAssociationStep++;

                if (type == DeviceInfo.APPEARANCE) {
                    updateProgress(ASSOCIATION_COMPLETE_PERCENT +
                            getPercentageOf((mPostAssociationStep * 100) / mNumPostAssociationSteps,
                                    APPEARANCE_AND_MODELS_COMPLETE_PERCENT - ASSOCIATION_COMPLETE_PERCENT));
                    // Store appearance into the database
                    int appearance = (int) event.data.getLong(MeshConstants.EXTRA_DEVICE_INFORMATION);
                    AppearanceDevice appearanceDevice = new AppearanceDevice(appearance, null);

                    mTempDevice.setName(appearanceDevice.getShortName() + " " + (deviceId - Constants.MIN_DEVICE_ID));
                    mTempDevice.setAppearance(appearanceDevice.getAppearanceType());
                    mTempDevice = mDeviceManager.createOrUpdateDevice(mTempDevice, true);

                    Log.d(TAG, "* Association Event CONFIG_INFO->APPEARANCE - mTempDevice:" + mTempDevice.toString());

                    // Request model low from the device.
                    mCurrentRequestState = DeviceInfo.MODEL_LOW;
                    ConfigModel.getInfo(deviceId, DeviceInfo.MODEL_LOW);
                } else if (type == DeviceInfo.MODEL_LOW) {
                    updateProgress(ASSOCIATION_COMPLETE_PERCENT +
                            getPercentageOf((mPostAssociationStep * 100) / mNumPostAssociationSteps,
                                    APPEARANCE_AND_MODELS_COMPLETE_PERCENT - ASSOCIATION_COMPLETE_PERCENT));
                    // Store modelLow into the database
                    long modelLow = event.data.getLong(MeshConstants.EXTRA_DEVICE_INFORMATION);
                    mTempDevice.setModelLow(modelLow);
                    mTempDevice = mDeviceManager.createOrUpdateDevice(mTempDevice, true);

                    Log.d(TAG, "* Association Event CONFIG_INFO->MODEL_LOW - mTempDevice:" + mTempDevice.toString());

                    // Request model high from the device.
                    mCurrentRequestState = DeviceInfo.MODEL_HIGH;
                    ConfigModel.getInfo(deviceId, DeviceInfo.MODEL_HIGH);
                } else if (type == DeviceInfo.MODEL_HIGH) {
                    updateProgress(ASSOCIATION_COMPLETE_PERCENT +
                            getPercentageOf((mPostAssociationStep * 100) / mNumPostAssociationSteps,
                                    APPEARANCE_AND_MODELS_COMPLETE_PERCENT - ASSOCIATION_COMPLETE_PERCENT));
                    // Store modelHigh into the database
                    long modelHigh = event.data.getLong(MeshConstants.EXTRA_DEVICE_INFORMATION);
                    mTempDevice.setModelHigh(modelHigh);
                    mTempDevice = mDeviceManager.createOrUpdateDevice(mTempDevice, true);
                    Log.d(TAG, "* Association Event CONFIG_INFO->MODEL_HIGH - mTempDevice:" + mTempDevice.toString());
                    mCurrentRequestState = null;
                    // Set the number of steps for the remaining progress now that we know how many models we need to query.
                    mNumPostAssociationSteps = mTempDevice.getModelsSupported().size();
                    mPostAssociationStep = 0;

                    // Query the number of groups supported for each model.
                    mModelsToQueryForGroups.addAll(mTempDevice.getModelsSupported());
                    if (!mModelsToQueryForGroups.isEmpty()) {
                        GroupModel.getNumberOfModelGroupIds(mTempDevice.getDeviceId(), mModelsToQueryForGroups.peek());
                    }
                } else {
                    associationComplete(false);
                }
                break;
            }
            case TIMEOUT: {
                if (event.data.getInt(MeshConstants.EXTRA_EXPECTED_MESSAGE) == MeshConstants.MESSAGE_DEVICE_ASSOCIATED) {
                    Log.d(TAG, "* Association Event TIMEOUT->MESSAGE_DEVICE_ASSOCIATED - Association Failed!");
                    associationComplete(false);
                } else if (event.data.getInt(MeshConstants.EXTRA_EXPECTED_MESSAGE) == MeshConstants.MESSAGE_CONFIG_DEVICE_INFO) {
                    int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                    if (mCurrentRequestState == DeviceInfo.APPEARANCE) {
                        mTempDevice.setName(getString(R.string.device) + " " + (deviceId - Constants.MIN_DEVICE_ID));
                        mTempDevice = mDeviceManager.createOrUpdateDevice(mTempDevice, true);
                        Log.d(TAG, "* Association Event TIMEOUT->APPEARANCE - Requesting model low...");
                        // Request model low from the device.
                        mCurrentRequestState = DeviceInfo.MODEL_LOW;
                        ConfigModel.getInfo(deviceId, DeviceInfo.MODEL_LOW);
                    } else if (mCurrentRequestState == DeviceInfo.MODEL_LOW) {
                        Log.d(TAG, "* Association Event TIMEOUT->MODEL_LOW - Requesting model high...");
                        // Request model high from the device.
                        mCurrentRequestState = DeviceInfo.MODEL_HIGH;
                        ConfigModel.getInfo(deviceId, DeviceInfo.MODEL_HIGH);
                    } else if (mCurrentRequestState == DeviceInfo.MODEL_HIGH) {
                        Log.d(TAG, "* Association Event TIMEOUT->MODEL_HIGH - Association Completed!");
                        mCurrentRequestState = null;
                        associationComplete(true);
                    }
                } else if (event.data.getInt(MeshConstants.EXTRA_EXPECTED_MESSAGE) == MeshConstants.MESSAGE_GROUP_NUM_GROUPIDS) {
                    Log.d(TAG, "* Association Event TIMEOUT->MESSAGE_GROUP_NUM_GROUPIDS - Association Completed!");
                    associationComplete(true);
                }
                break;
            }


        }
    }
}
