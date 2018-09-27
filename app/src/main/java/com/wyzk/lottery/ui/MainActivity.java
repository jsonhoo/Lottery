package com.wyzk.lottery.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.otto.Subscribe;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuxia.email.Logc;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.R;
import com.wyzk.lottery.adapter.HomeAdapter;
import com.wyzk.lottery.api.MeshLibraryManager;
import com.wyzk.lottery.api.TimeModel;
import com.wyzk.lottery.constant.IConst;
import com.wyzk.lottery.event.MeshSystemEvent;
import com.wyzk.lottery.model.ExtraBean;
import com.wyzk.lottery.model.LogLevel;
import com.wyzk.lottery.model.Place;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.RoomModel;
import com.wyzk.lottery.model.UserInfoModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.ui.fragment.MangeRechargeTabActivity;
import com.wyzk.lottery.utils.ACache;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.DialogBuilder;
import com.wyzk.lottery.utils.NetUtil;
import com.wyzk.lottery.utils.ToastUtil;
import com.wyzk.lottery.utils.Utils;
import com.wyzk.lottery.video.activity.BjlOwnerActivity;
import com.wyzk.lottery.video.activity.BjlPlayerActivity;
import com.wyzk.lottery.video.activity.HouseOwnerActivity;
import com.wyzk.lottery.video.activity.PlayerActivity;
import com.wyzk.lottery.view.DialogMaterial;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends LotteryBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @Bind(R.id.right)
    CoordinatorLayout right;
    @Bind(R.id.nav_view)
    NavigationView left;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer_layout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    TextView tvName;
    ImageButton status;
    HomeAdapter adapter;
    private boolean isDrawer = false;
    private UserInfoModel userInfoModel;
    private List<RoomModel.RowModel> mDataList = new ArrayList<>();

    private long mPressedTime = 0;

    private int pageIndex = 1;
    private int pageRows = 10;

    private boolean mShuttingDown = false;
    boolean mFirstConnect = true;

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int DEFAULT_HOST_ID = 32768;

    private boolean isChannelClick = false;
    public Handler mhandler = new Handler(Looper.getMainLooper());

    Consumer<ResultReturn<UserInfoModel>> userObserver = new Consumer<ResultReturn<UserInfoModel>>() {

        @Override
        public void accept(ResultReturn<UserInfoModel> result) throws Exception {
            if (result != null && result.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue() && result.getData() != null) {
                userInfoModel = result.getData();
                tvName.setText(userInfoModel.getUsername());
            } else {
                logout();
            }
        }
    };

    Consumer<ResultReturn<RoomModel>> roomObserver = new Consumer<ResultReturn<RoomModel>>() {
        @Override
        public void accept(ResultReturn<RoomModel> result) throws Exception {
            if (result != null && result.getData() != null) {
                List<RoomModel.RowModel> rowModels = result.getData().getRows();
                mDataList.clear();
                if (rowModels != null) {
                    mDataList.addAll(rowModels);
                }
                adapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.setDrawerListener(toggle);
        toggle.syncState();

        BuildManager.setStatusTrans2(this);

        startMqtt();

        left.setNavigationItemSelectedListener(this);
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isDrawer) {
                    return left.dispatchTouchEvent(motionEvent);
                } else {
                    return false;
                }
            }
        });
        drawer_layout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                isDrawer = true;
                //获取屏幕的宽高
                WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                //设置右面的布局位置  根据左面菜单的right作为右面布局的left   左面的right+屏幕的宽度（或者right的宽度这里是相等的）为右面布局的right
                right.layout(left.getRight(), 0, left.getRight() + display.getWidth(), display.getHeight());
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawer = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        LinearLayout rootView = (LinearLayout) left.getHeaderView(0);
        RelativeLayout relativeLayout = (RelativeLayout) rootView.getChildAt(0);
        LinearLayout linearLayout = (LinearLayout) rootView.getChildAt(1);
        tvName = (TextView) linearLayout.getChildAt(1);
        status = (ImageButton) relativeLayout.getChildAt(0);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isChannelClick) {
                    //桥连接没有连接上
                    isChannelClick = true;
                    MeshLibraryManager.getInstance().stopAutoconnect();
                    mhandler.postDelayed(() -> MeshLibraryManager.getInstance().disconnectAllDevices(), 1500);
                    mhandler.postDelayed(() -> {
                        isChannelClick = false;
                        MeshLibraryManager.getInstance().startAutoConnect(1);
                    }, 3000);
                }
            }
        });
        RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (token != null) {
                    pageIndex = 1;
                    getRoomList(token, pageIndex, pageRows);
                    getUserInfo(token);
                }
                refreshlayout.finishRefresh(2000);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (token != null) {
                    getRoomList(token, ++pageIndex, pageRows);
                    getUserInfo(token);
                }
                refreshlayout.finishLoadmore(2000);
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.HORIZONTAL));
        adapter = new HomeAdapter(this, mDataList, R.layout.item_room);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                toVideoActivity(position);
            }
        });

        initData();
    }

    private void initData() {
        MeshLibraryManager.initInstance(getApplicationContext(), MeshLibraryManager.MeshChannel.BLUETOOTH, LogLevel.DEBUG);

        LotteryApplication.bus.register(this);
        checkLocation();

        String token = ACache.get(this).getAsString(IConst.TOKEN);
        if (!TextUtils.isEmpty(token)) {//初始化设备列表
            mDeviceManager.initDevicelist();
        }
    }

    private Place getLastPlaceUsed() {
        return Utils.getLatestPlaceIdUsed();
    }

    public void onAddPressed() {
        Place place = getLastPlaceUsed();
        // Ensure only place owner can add new devices
        if (place != null && !place.getPassphrase().equals("imported")) {
            MeshLibraryManager.MeshChannel channel = MeshLibraryManager.getInstance().getChannel();
            if (channel == MeshLibraryManager.MeshChannel.BLUETOOTH) {
                if (MeshLibraryManager.getInstance().isChannelReady()) {
                    Intent i = new Intent(this, AutoBindDeviceActivity.class);
                    startActivityForResult(i, AutoBindDeviceActivity.NEW_DEVICE_REQUEST);
                } else {
                    showComDialog(getString(R.string.bluetooth_needed_to_add_devices));
                }
            } else {
                showComDialog(getString(R.string.bluetooth_selected_to_add_devices));
                MeshLibraryManager.getInstance().setBluetoothChannelEnabled();
            }
        } else {
            showComDialog(getString(R.string.only_the_place_owner_can_add_new_devices));
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


    private DialogMaterial mDialog;

    private void checkDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new DialogMaterial(this, getString(R.string.check_net), null);
        TextView text = new TextView(this);
        text.setText(R.string.check_neting);
        mDialog.setBodyView(text);
        mDialog.setCancelable(false);
        mDialog.setShowProgress(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetUtil.isNetworkAvailable(MainActivity.this) && NetUtil.ping()) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.hideProgress();
                            MainActivity.this.onAddPressed();
                        }
                    });
                } else {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.hideProgress();
                            MainActivity.this.showComDialog(MainActivity.this.getString(R.string.not_net));
                        }
                    });
                }
            }
        }).start();
    }

    private void hideProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                mDialog = null;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getRoomList(token, pageIndex, pageRows);
        getUserInfo(token);

        // Broadcasting time to the mesh network whenever we resume the app.
        if (MeshLibraryManager.getInstance() != null) {
            MeshLibraryManager.MeshChannel channel = MeshLibraryManager.getInstance().getChannel();
            if (MeshLibraryManager.getInstance().isServiceAvailable() && channel == MeshLibraryManager.MeshChannel.BLUETOOTH) {
                TimeModel.broadcastTime();
            }
        }

        getPermission();
        // Check bluetooth status if not enabled BT & BLE not supported on device display alert
        verifyBluetooth();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_BLUETOOTH_ON) {
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    Logc.e(TAG, "ble enable!!!");
                }
                break;
                case Activity.RESULT_CANCELED: {
                    ToastUtil.showToast(this, getString(R.string.ble_dis));
                }
                break;
                default:
                    break;
            }
        }
    }


    @TargetApi(18)
    public void verifyBluetooth() {
        try {
            if (!MeshLibraryManager.checkAvailability(getApplicationContext())) {
                showBTStatusDialog(true);
            }
        } catch (RuntimeException e) {

            final Dialog bleDialog = DialogBuilder.createSimpleOkErrorDialog(
                    this,
                    getString(R.string.dialog_error_ble_not_supported),
                    getString(R.string.error_message_bluetooth_le_not_supported)
            );
            bleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    MainActivity.this.finish();
                }
            });
            bleDialog.show();
        }
    }


    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions.request(android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.BLUETOOTH)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean grant) throws Exception {
                            if (!grant) {
                                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.permissions_denied), Toast.LENGTH_LONG).show();
                                MainActivity.this.finish();
                            } else {
                                MainActivity.this.startMain();
                            }
                        }
                    });
        } else {
            startMain();
        }
    }

    private void startMain() {

    }

    private void getRoomList(String token, int pageIndex, int pageRows) {
        subscription = Network.getNetworkInstance().getLiveApi()
                .getRoomList(token, pageIndex, pageRows)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(roomObserver);
    }

    private void getUserInfo(String token) {
        subscription2 = Network.getNetworkInstance().getUserApi()
                .getUserInfo(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userObserver);
    }

    public void toVideoActivity(int position) {
        Intent intent = null;

        RoomModel.RowModel rowModel = mDataList.get(position);
        String userId = ACache.get(this).getAsString(IConst.USER_ID);
        if (userId != null && userId.equals(rowModel.getUserId())) {
            //房主
            if (rowModel.getGameId() == 1) {
                intent = new Intent(this, HouseOwnerActivity.class);
            } else if (rowModel.getGameId() == 2) {
                intent = new Intent(this, BjlOwnerActivity.class);
            }
        } else {
            //玩家
            if (rowModel.getGameId() == 1) {
                intent = new Intent(this, PlayerActivity.class);
            }else if(rowModel.getGameId() == 2){
                intent = new Intent(this, BjlPlayerActivity.class);
            }
        }
        intent.putExtra(IConst.ROW_INFO, rowModel);
        startActivity(intent);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.my_integral) {
            String isAdmin = ACache.get(MainActivity.this).getAsString(IConst.IS_ADMIN);
            if ("".equals(isAdmin) || isAdmin == null || "0".equals(isAdmin)) {
                ToastUtil.showToast(this, "权限不够");
            } else {
                toActivity(MangeRechargeTabActivity.class);
            }

        } else if (id == R.id.recharge) {
            toActivity(RechargeWithdrawalActivity.class);
        } else if (id == R.id.personal_information) {
            ExtraBean extraBean = new ExtraBean();
            extraBean.setData(userInfoModel);
            toActivityWithExtra(extraBean, PersonalInformationActivity.class);
        } else if (id == R.id.my_message) {
            toActivity(MyMessageActivity.class);
        } else if (id == R.id.service) {
            //toCall();
            toActivity(BaiJiaLeActivity.class);
        } else if (id == R.id.about) {
            toActivity(AboutActivity.class);
        } else if (id == R.id.deviceList) {
            toActivity(DeviceListActivity.class);
        } else if (id == R.id.add) {
            checkDialog();
        } else if (id == R.id.unbind){
            toActivity(UnbindMeshDeviceActivity.class);
        }
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            long nowTime = System.currentTimeMillis();
            if (nowTime - mPressedTime > 2000) {
                Toast.makeText(this, getString(R.string.press_again), Toast.LENGTH_SHORT).show();
                mPressedTime = nowTime;
            } else {
                mShuttingDown = true;
                MeshLibraryManager.getInstance().shutdown();
                super.onBackPressed();
            }
        }
    }

    public void onExit(View view) {
        ACache.get(this).clear();
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }


    @Override
    public void onPause() {
        super.onPause();
        mFirstConnect = true;
    }


    private void updateConnectionSettings() {
        // If passphrase is not set "" use directly the networkKey (this place was imported and DB does not have passphrase)
        Place mPlace = Utils.getLatestPlaceIdUsed();
        if (mPlace == null) {
            mPlace = new Place();
            mPlace.setName("place1");
            mPlace.setPlaceId(10);
            mPlace.setPassphrase("passphrase"+10);//+10
            if (mPlace.getHostControllerID() == 0) {
                mPlace.setHostControllerID(DEFAULT_HOST_ID);
            }
            Utils.setLatestPlaceIdUsed(mPlace);
        }

        if (mPlace != null) {
            if (mPlace.getPassphrase().equals("imported")) {
                // Use networkKey
                MeshLibraryManager.getInstance().setNetworkKey(mPlace.getNetworkKey());
            } else {
                // Use passhrase 这个很重要是用来控制设备的令牌   不同组网有不同口令  不允许跨平台的控制设备
                if (!TextUtils.isEmpty(mPlace.getPassphrase())) {
                    Logc.d(TAG, "设置当前组网的口令：" + mPlace.getPassphrase());
                    MeshLibraryManager.getInstance().setNetworkPassPhrase(mPlace.getPassphrase());
                } else {
                    MeshLibraryManager.getInstance().setNetworkPassPhrase("passphrase");
                }
            }
            // If in Bluetooth bearer set controller ID.
            if (MeshLibraryManager.getInstance().getChannel() == MeshLibraryManager.MeshChannel.BLUETOOTH) {
                MeshLibraryManager.getInstance().setControllerAddress(mPlace.getHostControllerID());
            }
            // Set tenant & site
//            RestChannel.setTenant(mDBManager.getFirstSetting().getCloudTenantId());
//            RestChannel.setSite(Utils.getLatestPlaceIdUsed().getCloudSiteID());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMqtt();
        LotteryApplication.bus.unregister(this);
    }

    public void checkLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            LocationManager lm = null;
            boolean gps_enabled = false;
            boolean network_enabled = false;

            lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
            // exceptions will be thrown if provider is not permitted.
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                network_enabled = lm
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (gps_enabled == false || network_enabled == false) {
                // Show our settings alert and let the use turn on the GPS/Location
                showBTStatusDialog(false);
            }
        }
    }

    public static final int REQUEST_CODE_BLUETOOTH_ON = 12;
    private Dialog bleDialog = null;

    private void showBTStatusDialog(boolean btDialog) {
        if (btDialog) {
            turnOnBluetooth();
        } else {
            if (bleDialog == null) {
                bleDialog = DialogBuilder.createSimpleDialog(
                        this,
                        getString(R.string.dialog_error_Location_not_enabled),
                        getString(R.string.error_message_enable_Location), btDialog
                );
            }
            if (!bleDialog.isShowing()) {
                bleDialog.show();
            }
        }
    }

    /**
     * 弹出系统弹框提示用户打开 Bluetooth
     */
    private void turnOnBluetooth() {
        // 请求打开 Bluetooth
        Intent requestBluetoothOn = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // 请求开启 Bluetooth
        this.startActivityForResult(requestBluetoothOn,
                REQUEST_CODE_BLUETOOTH_ON);
    }

    private void updateDrawerFragment() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MeshLibraryManager.getInstance() != null) {
                    MeshLibraryManager.MeshChannel channel = MeshLibraryManager.getInstance().getChannel();
                    if (channel == MeshLibraryManager.MeshChannel.BLUETOOTH) {

                        if (MeshLibraryManager.getInstance().isChannelReady()) {
                            status.setImageResource(R.mipmap.ic_bluetooth_24dp);
                            status.setColorFilter(0xffffffff);
                        } else {
                            status.setImageResource(R.mipmap.ic_bluetooth_not_connected_24dp);
                        }
                    }
                }

            }
        });
    }


    @Subscribe
    public void onEvent(MeshSystemEvent event) {
        if (MainActivity.this.isFinishing()) {
            return;
        }
        switch (event.what) {
            case BT_REQUEST: {
                // Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //  startActivityForResult(enableBtIntent, 0);
                break;
            }
            case CHANNEL_READY:
                if (mFirstConnect) {
                    mFirstConnect = false;
                    updateConnectionSettings();
                    // Broadcast time to the mesh network whenever we resume the app.
                    MeshLibraryManager.MeshChannel channel = MeshLibraryManager.getInstance().getChannel();
                    if (channel == MeshLibraryManager.MeshChannel.BLUETOOTH) {
                        TimeModel.broadcastTime();
                    }
                }
                updateDrawerFragment();
                break;
            case CHANNEL_NOT_READY:
                if (!mShuttingDown) {
                    updateDrawerFragment();
                }
                break;
            case SERVICE_SHUTDOWN:
                if (mShuttingDown) {
                    LotteryApplication.bus.unregister(this);
                    MeshLibraryManager.getInstance().onDestroy();
                    System.exit(0);//正常退出App
                }
                break;
            case DEVICE_CHANGED:
                //device change
                break;
            case WEAR_CONNECTED:
            case PLACE_CHANGED: {
                //place change
                updateConnectionSettings();
                break;
            }
        }
    }

}
