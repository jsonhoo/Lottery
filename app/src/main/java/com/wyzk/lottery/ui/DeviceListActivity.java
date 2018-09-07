package com.wyzk.lottery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csr.csrmesh2.DeviceInfo;
import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.MeshService;
import com.het.recyclerview.ProgressStyle;
import com.het.recyclerview.XRecyclerView;
import com.het.recyclerview.swipemenu.SwipeMenuRecyclerView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.squareup.otto.Subscribe;
import com.uuxia.email.Logc;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.R;
import com.wyzk.lottery.adapter.DeviceAdapter;
import com.wyzk.lottery.api.Association;
import com.wyzk.lottery.api.ConfigModel;
import com.wyzk.lottery.api.DataModelManager;
import com.wyzk.lottery.api.MeshLibraryManager;
import com.wyzk.lottery.constant.Constants;
import com.wyzk.lottery.event.MeshResponseEvent;
import com.wyzk.lottery.model.Device;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.ByteUtils;
import com.wyzk.lottery.utils.NetUtil;
import com.wyzk.lottery.utils.ToastUtil;
import com.wyzk.lottery.view.DialogMaterial;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceListActivity extends LotteryBaseActivity implements XRecyclerView.LoadingListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;


    @Bind(R.id.swipe_view)
    SwipeMenuRecyclerView mSwipeMenuRecyclerView;
    @Bind(R.id.rl_device_total)
    LinearLayout deviceTotal;
    @Bind(R.id.device_numb)
    TextView TxDeviceNumb;


    private DeviceAdapter mDeviceAdapter;
    private List<Device> mDeviceList = new ArrayList<>();
    private Device mDevice = null;
    private int mHashExpected = Constants.INVALID_VALUE;
    private final static int WAITING_RESPONSE_MS = 10 * 1000;
    private boolean isNeedRecive = true;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        BuildManager.setStatusTrans(this, 1, title);

        initRecyclerView();
        LotteryApplication.bus.register(this);
    }

    private void initRecyclerView() {
        mSwipeMenuRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.swipe_view);
        mSwipeMenuRecyclerView.setLoadingMoreEnabled(false);
        mSwipeMenuRecyclerView.setLoadingListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(1);
        this.mSwipeMenuRecyclerView.setLayoutManager(layoutManager);
        this.mSwipeMenuRecyclerView.setPullRefreshEnabled(false);
        this.mSwipeMenuRecyclerView.setLoadingMoreEnabled(false);
        this.mSwipeMenuRecyclerView.setLoadingListener(this);
        this.mSwipeMenuRecyclerView.setSwipeDirection(1);
        this.mSwipeMenuRecyclerView.setRefreshProgressStyle(ProgressStyle.BallPulse);
        this.mSwipeMenuRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallPulse);
        this.mSwipeMenuRecyclerView.setArrowImageView(R.mipmap.iconfont_downgrey);

        mDeviceAdapter = new DeviceAdapter(this, R.layout.adapter_mydevice_item);
        mDeviceAdapter.setISwipeMenuClickListener(new DeviceAdapter.ISwipeMenuClickListener() {
            @Override
            public void onNameBtnCilck(View var1, int var2) {
                mDevice = mDeviceList.get(var2);
                showDeleteDevice(mDevice);
            }

            @Override
            public void onCallBtnCilck(View var1, int var2) {
                callDevice(mDeviceList.get(var2));
            }

        });
        mSwipeMenuRecyclerView.setAdapter(mDeviceAdapter);
        mDeviceAdapter.setOnItemClickListener((view, o, i) -> {
            //测试入口  正式版本会关闭
            Device device = mDeviceAdapter.getData(i);
            gotoTestDataActivity(device);
        });

    }


    /**
     * 测试数据  给设备发红色常亮的命令  这个可以设置
     *
     * @param device
     */
    private void callDevice(Device device) {
        if (MeshLibraryManager.getInstance().isBluetoothBridgeReady()) {
            byte[] dataByte = getCmdBytes(4, "0x02", "#ff0000");
            try {
                DataModelManager.getInstance().sendData(dataByte, device.getDeviceId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showComDialog(getString(R.string.bluetooth_needed_to_control_devices));
        }
    }


    /**
     * 封装控制数据报文协议
     *
     * @param lightType 1代表官方活动选中  2代表游戏活动选中   3代表自定义活动
     *                  模式对应的字段是什么 这个要自定义激爽、畅饮、互动、休闲
     * @return
     */
    public static byte[] getCmdBytes(int lightType, String model, String color) {
        ByteBuffer buf = null;
        buf = ByteBuffer.allocate(8);
        putCmdConfig(lightType, buf, model, color);
        buf.flip();
        byte[] configBytes = buf.array();
        return configBytes;
    }


    /**
     * @param lightType 1代表官方活动选中  2代表游戏活动选中  这里就是常亮 0x01表示  3的模式根据设置者自己来设置
     * @param buf
     * @param model     没有模式 给 0x03
     * @param color     没有颜色 给 #ffffff
     */

    private static void putCmdConfig(int lightType, ByteBuffer buf, String model, String color) {
        buf.put((byte) 0x01);//灯控命令 0x01  杯子模式
        byte[] colorBytes = null;
        if (!TextUtils.isEmpty(color)) {
            colorBytes = ByteUtils.hexStringToBytes(color.replace("#", ""));
        } else {
            colorBytes = ByteUtils.hexStringToBytes("ff0000");
        }
        if (TextUtils.isEmpty(model)) {
            model = "0x03";
        }
        byte b = (byte) Integer.parseInt((model).substring(2), 16);
        if (lightType == 1) {
            buf.put(b);
            putColorBuff(buf, colorBytes);
            buf.put((byte) 0xff);//保留
        } else if (lightType == 2) {
            buf.put(b);
            putColorBuff(buf, colorBytes);
            buf.put((byte) 0x0a);//保留
        } else if (lightType == 3) {
            buf.put(b);
            putColorBuff(buf, colorBytes);
            buf.put((byte) 0x00);//保留
        } else {
            buf.put((byte) 0x02);
            putColorBuff(buf, colorBytes);
            buf.put((byte) 0x05);//保留
        }
        buf.put((byte) 0x00);//保留
        buf.put((byte) 0x00);//保留
    }

    private static void putColorBuff(ByteBuffer buf, byte[] colorBytes) {
        if (colorBytes != null && colorBytes.length == 3) {
            buf.put(colorBytes[0]);
            buf.put(colorBytes[1]);
            buf.put(colorBytes[2]);
        } else {
            buf.put((byte) 0x00);
            buf.put((byte) 0x00);
            buf.put((byte) 0x00);
        }
    }


    private void showDeleteDevice(Device device) {
        if (!NetUtil.isNetworkAvailable(this)) {
            ToastUtil.showToast(this, getString(R.string.check_net));
            return;
        }
        MeshLibraryManager.MeshChannel channel = MeshLibraryManager.getInstance().getChannel();
        if (channel == MeshLibraryManager.MeshChannel.BLUETOOTH) {
            if (MeshLibraryManager.getInstance().isChannelReady()) {
                mDevice = device;
                deleteConfirmation(device, false);
            } else {
                showComDialog(getString(R.string.bluetooth_needed_to_delete_devices));
            }
        } else {
            showComDialog(getString(R.string.bluetooth_selected_to_delete_devices));
            MeshLibraryManager.getInstance().setBluetoothChannelEnabled();
        }
    }


    private void showComDialog(String string) {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("提示")
                .setMessage(string)
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .create(mCurrentDialogStyle).show();
    }

    private void gotoTestDataActivity(Device device) {
        if (MeshLibraryManager.isBluetoothBridgeReady()) {
            Intent intent = new Intent(this, BleActivity.class);
            intent.putExtra(BleActivity.EXTRA_DEVICE, device.getDatabaseId());
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.you_can_not_control_your_device_until_bridge_is_connected), Toast.LENGTH_SHORT).show();
        }
    }


    private DialogMaterial dialog;
    private DialogMaterial mDialog;

    private void deleteConfirmation(Device device, final boolean force) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new DialogMaterial(this, getString(R.string.delete_device), force ? getString(R.string.forcing_delete_device) : getString(R.string.delete_device_double_check));
        dialog.addCancelButton(getString(R.string.cancel), view -> {
            dialog.dismiss();
            dialog = null;
        });
        dialog.addAcceptButton(getString(R.string.accept), view -> {
            if (force) {
                deleteDeviceAndFinish();
                dialog.dismiss();
                dialog = null;
            } else {
                dialog.dismiss();
                dialog = null;
                startCheckingScanInfo();
                showProgress();
                mHashExpected = device.getDeviceHash();
                if (device.getUuid() == null) {
                    ConfigModel.getInfo(device.getDeviceId(), DeviceInfo.UUID_LOW);
                } else {
                    resetDevice(device);
                }
            }
        });
        dialog.show();
    }

    private void showProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new DialogMaterial(this, getString(R.string.deleting_device), null);
        TextView text = new TextView(this);
        text.setText(R.string.resetting_device);
        mDialog.setBodyView(text);
        mDialog.setCancelable(false);
        mDialog.setShowProgress(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    private void hideProgress() {
        runOnUiThread(() -> {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            mDialog = null;
        });
    }

    private void resetDevice(Device device) {
        UUID deviceUUID = device.getUuid();
        if (deviceUUID != null) {
            ConfigModel.resetDevice(device.getDeviceId(),
                    MeshService.getDeviceHash64FromUuid(device.getUuid()),
                    ByteUtils.hexStringToBytes(device.getDmKey().replaceAll(" ", "")));
        } else {
            Association.resetDevice(device.getDeviceId(), ByteUtils.hexStringToBytes(device.getDmKey()));
        }
    }

    /**
     * Start checking if the list of devices we are displaying contains a valid info or should be removed from the list.
     */
    private void startCheckingScanInfo() {
        mHandler.postDelayed(removeDeviceTimeOut, WAITING_RESPONSE_MS);
        Association.discoverDevices(true);
    }


    /**
     * Stop checking if the list of devices we are displaying contains a valid info or should be removed from the list.
     */
    private void stopCheckingScanInfo() {
        if (isFinishing()) {
            return;
        }
        Association.discoverDevices(false);
    }


    /**
     * 物理解绑超时   直连解绑
     */
    private Runnable removeDeviceTimeOut = new Runnable() {
        @Override
        public void run() {
            mHashExpected = Constants.INVALID_VALUE;
            Logc.e("物理链路解绑失败...  去直连解绑");
            connectDeleteDevice();
            stopCheckingScanInfo();
        }
    };


    private void connectDeleteDevice() {
        hideProgress();
        deleteConfirmation(mDevice, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LotteryApplication.bus.unregister(this);
        if (mHashExpected != Constants.INVALID_VALUE) {
            stopCheckingScanInfo();
        }
        mHandler.removeCallbacksAndMessages((Object) null);//清除handler
        // dismiss all the dialogs.
        dismissAllDialogs();
    }

    private void dismissAllDialogs() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isNeedRecive = true;
        getDeviceList();
    }

    private void getDeviceList() {
        runOnUiThread(() -> {
            mDeviceList.clear();
            mDeviceList = mDeviceManager.getAllDevicesList();
            mDeviceAdapter.setListAll(mDeviceList);
            if (mDeviceList.size() == 0) {
                deviceTotal.setVisibility(View.GONE);
            } else {
                deviceTotal.setVisibility(View.VISIBLE);
                TxDeviceNumb.setText("已绑定" + mDeviceList.size() + "个啤酒杯");
            }
        });
    }


    @Override
    public void onRefresh() {
        mDeviceManager.initDevicelist();
    }

    @Override
    public void onLoadMore() {

    }

    private void deleteDeviceAndFinish() {
//        Toast.makeText(getActivity(), mDevice.getName() + " removed successfully.", Toast.LENGTH_SHORT).show();
        mDeviceManager.removeDevice(mDevice.getDatabaseId());
        getDeviceList();
        if (mHashExpected != Constants.INVALID_VALUE) {
            stopCheckingScanInfo();
        }
        mHandler.removeCallbacks(removeDeviceTimeOut);
        hideProgress();
    }

    @Subscribe
    public void onEventMainThread(MeshResponseEvent event) {
        if (!isNeedRecive) return;
        if (isFinishing()) {
            return;
        }
        switch (event.what) {
            case DEVICE_UUID: {
                int uuidHash = event.data.getInt(MeshConstants.EXTRA_UUIDHASH_31);
                if (uuidHash == mHashExpected) {
                    deleteDeviceAndFinish();
                }
                break;
            }
            case CONFIG_INFO: {
                int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                DeviceInfo type = DeviceInfo.values()[event.data.getInt(MeshConstants.EXTRA_DEVICE_INFO_TYPE)];
                if (mDevice != null && deviceId == mDevice.getDeviceId()) {
                    if (type == DeviceInfo.UUID_LOW) {
                        long uuidLow = event.data.getLong(MeshConstants.EXTRA_DEVICE_INFORMATION);
                        mDevice.setUuidLow(uuidLow);
                        ConfigModel.getInfo(mDevice.getDeviceId(), DeviceInfo.UUID_HIGH);
                    } else if (type == DeviceInfo.UUID_HIGH) {
                        long uuidHigh = event.data.getLong(MeshConstants.EXTRA_DEVICE_INFORMATION);
                        mDevice.setUuidHigh(uuidHigh);
                        // if we are in Bluetooth run Config.Reset if over rest call Blacklist
                        if (MeshLibraryManager.getInstance().getChannel() == MeshLibraryManager.MeshChannel.BLUETOOTH) {
                            resetDevice(mDevice);
                        }
                    }
                }
                break;
            }
        }
    }

}
