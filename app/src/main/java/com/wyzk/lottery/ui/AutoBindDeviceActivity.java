package com.wyzk.lottery.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.het.recyclerview.XRecyclerView;
import com.wyzk.lottery.R;
import com.wyzk.lottery.adapter.AutoDeviceAdpter;
import com.wyzk.lottery.api.ScanDevice;
import com.wyzk.lottery.model.RecyclerViewManager;
import com.wyzk.lottery.network.AutoScanBindManager;
import com.wyzk.lottery.network.UpdateDeviceCallBack;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.ToastUtil;
import com.wyzk.lottery.utils.TtfUtil;
import com.wyzk.lottery.view.CircleWaveView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AutoBindDeviceActivity extends LotteryBaseActivity {

    @Bind(R.id.data_value)
    TextView dataValue;
    @Bind(R.id.textview_scrollview)
    ScrollView mScrollView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;
    @Bind(R.id.circle_view)
    CircleWaveView circleView;
    @Bind(R.id.device_list)
    XRecyclerView mRecyclerView;

    @Bind(R.id.scan_bind_total)
    TextView scanBindTotal;
    @Bind(R.id.scan_bind_pros)
    TextView scanBindPros;
    @Bind(R.id.scan_bind_state)
    TextView scanBindState;
    @Bind(R.id.scan_img)
    ImageView scanImg;


    private AutoScanBindManager autoScanBindManager;
    private AutoDeviceAdpter mAdpter;
    private ArrayList<ScanDevice> mDiscoveredDevices = new ArrayList<ScanDevice>();
    private Animation operatingAnim;
    public static final int NEW_DEVICE_REQUEST = 10;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_bind_device);
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

        mContext = this;
        initRecyclerView();

        initData();
    }

    private void initData() {
        toolbar.setNavigationOnClickListener(v -> {
            if (autoScanBindManager != null && autoScanBindManager.isCanBack()) {
                finish();
            } else {
                ToastUtil.showToast(this, getString(R.string.net_ing_create));
            }
        });

        autoScanBindManager = AutoScanBindManager.getInstence(mContext);
        autoScanBindManager.setUpdateDeviceCallBack(new UpdateDeviceCallBack() {
            @Override
            public void onUpdataDevices(List<ScanDevice> devices) {
                updateList(devices);
            }

            @Override
            public void onDevicesChange(int numb) {
                runOnUiThread(() -> scanBindTotal.setText(getString(R.string.bindeds) + numb + getString(R.string.device_numb)));
            }

            @Override
            public void onShowInfo(String msg) {
                showInfo(msg);
            }

            @Override
            public void onShowPros(int pros) {
                runOnUiThread(() -> {
                    TtfUtil.setDetTtf(mContext, scanBindPros);
                    scanBindPros.setVisibility(View.VISIBLE);
                    scanBindPros.setText(pros + " %");
                });
            }

            @Override
            public void onShowState(int state) {
                if (state == 1) {
                    runOnUiThread(() -> scanBindState.setText(getString(R.string.scan_ing)));
                } else if (state == 2) {
                    runOnUiThread(() -> scanBindState.setText(getString(R.string.net_ing)));
                } else if (state == 3) {
                    runOnUiThread(() -> {
                        TtfUtil.setDetTtf(mContext, scanBindPros);
                        scanBindPros.setVisibility(View.GONE);
                        scanBindPros.setText("");
                    });
                }
            }
        });
        autoScanBindManager.start();
        circleView.start();
        startAnim();
    }

    private void initRecyclerView() {
        mRecyclerView = (XRecyclerView) findViewById(R.id.device_list);
        mRecyclerView = new RecyclerViewManager().getXLinearInstance(this, mRecyclerView, false, false);
        mAdpter = new AutoDeviceAdpter(this, R.layout.adapter_device_discovered);
        mRecyclerView.setAdapter(mAdpter);
        mDiscoveredDevices.clear();
        mAdpter.setListAll(mDiscoveredDevices);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (autoScanBindManager != null && !autoScanBindManager.isCanBack()) {
                ToastUtil.showToast(this, getString(R.string.net_ing_create));
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * Update the list UI with a new set of devices.
     *
     * @param scanDevices The list of devices to show in the list UI.
     */
    public void updateList(final List<ScanDevice> scanDevices) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (scanDevices != null) {
                    mDiscoveredDevices.clear();
                    mDiscoveredDevices.addAll(scanDevices);
                    mAdpter.setListAll(mDiscoveredDevices);
                }
            }
        });
    }



    private void startAnim() {
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null) {
            scanImg.startAnimation(operatingAnim);
        }
    }

    private void stopAnim() {
        scanImg.clearAnimation();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (operatingAnim != null && scanImg != null && operatingAnim.hasStarted()) {
            scanImg.clearAnimation();
            scanImg.startAnimation(operatingAnim);
        }
    }

    private void showInfo(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            runOnUiThread(() -> {
                if (msg != null) {
                    try {
                        DateFormat df = DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.UK);
                        String formattedDate = df.format(new Date());
                        dataValue.append("<" + formattedDate + "> ");
                        dataValue.append(msg);
                        dataValue.append("\n");
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        circleView.stop();
        if (autoScanBindManager != null) {
            autoScanBindManager.onDestroy();
            autoScanBindManager = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
