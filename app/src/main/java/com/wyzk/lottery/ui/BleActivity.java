package com.wyzk.lottery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.csr.csrmesh2.MeshConstants;
import com.squareup.otto.Subscribe;
import com.uuxia.email.Logc;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.R;
import com.wyzk.lottery.api.DataModelManager;
import com.wyzk.lottery.constant.Constants;
import com.wyzk.lottery.event.MeshResponseEvent;
import com.wyzk.lottery.event.TestDataEvent;
import com.wyzk.lottery.model.Device;
import com.wyzk.lottery.utils.StringUtil;
import com.wyzk.lottery.utils.ToastUtil;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.wyzk.lottery.event.MeshResponseEvent.ResponseEvent.DATA_RECEIVE_BLOCK;
import static com.wyzk.lottery.event.MeshResponseEvent.ResponseEvent.DATA_RECEIVE_STREAM;
import static com.wyzk.lottery.event.MeshResponseEvent.ResponseEvent.DATA_RECEIVE_STREAM_END;


public class BleActivity extends LotteryBaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;
    
    @Bind(R.id.data_value)
    TextView dataValue;
    @Bind(R.id.textview_scrollview)
    ScrollView mScrollView;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.bt_send)
    Button btSend;


    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    public static final String TAG = BleActivity.class.getSimpleName();
    Device device = null;
    int id = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goto_test_data);
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
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LotteryApplication.bus.unregister(this);
    }
    
    protected void hideInputMethod() {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @OnClick({R.id.bt_send})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send:
                hideInputMethod();
                sendData();
                break;
        }
    }
    
    private void deviceNotFound() {
        Toast.makeText(this, "没有发现设备", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initData() {
        LotteryApplication.bus.register(this);
        if (getIntent().getExtras().containsKey(EXTRA_DEVICE)) {
            device = mDeviceManager.getDeviceById(getIntent().getIntExtra(EXTRA_DEVICE, Constants.INVALID_VALUE));
            if (device == null) {
                deviceNotFound();
                return;
            }
            id = device.getDeviceId();
        }
        if (!TextUtils.isEmpty(device.getName())) {
            toolbar.setTitle(device.getName());
        }
        
    }

    private void sendDataModlea(final int deviceId, byte[] data, boolean acknowledged) {
        if (deviceId == Constants.INVALID_VALUE) {
            throw new IllegalArgumentException("deviceId can\'t be -1");
        }
        showInfo(getString(R.string.send_data_show) + StringUtil.byteArrayToHexString(data));
        try {
            DataModelManager.getInstance().sendData(data, deviceId);
        } catch (Exception e) {
            e.printStackTrace();
            Logc.e(TAG, "error :" + e.getMessage());
        }
        Logc.e(TAG, "send data =" + StringUtil.byteArrayToHexString(data));
    }

    
    private void sendData() {
        if (etContent.getText() != null) {
            String dataString = etContent.getText().toString().trim().replace(" ", "");
            if (!TextUtils.isEmpty(dataString)) {
                try {
                    byte[] byteArray = StringUtil.hexStringToByteArray(dataString);
                    sendDataModlea(id, byteArray, false);
                } catch (IllegalArgumentException e) {
                    showInfo("e:" + e.getMessage());
                }
            }

        } else {
            ToastUtil.showToast(this, getString(R.string.donot_send_null));
        }
    }


    @Subscribe
    public void onEventMainThread(TestDataEvent event) {
        if (event == null || event.data == null) {
            return;
        }
        int devceId = event.data.getInt("deviceId");
        byte[] dataSt = event.data.getByteArray("data");
        showInfo(devceId + "发送数据eee:" + StringUtil.byteArrayToHexString(dataSt));
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

    @Subscribe
    public void onEventMainThread(MeshResponseEvent event) {
        if (event == null || event.data == null) {
            return;
        }
        if (event.what == DATA_RECEIVE_BLOCK || event.what == DATA_RECEIVE_STREAM || event.what == DATA_RECEIVE_STREAM_END) {
            int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
            if (deviceId == Constants.INVALID_VALUE) {
                return;
            }

            switch (event.what) {
                case DATA_RECEIVE_BLOCK: {//unack rerevice
                    byte[] reData = event.data.getByteArray(MeshConstants.EXTRA_DATA);
                    if (reData != null) {
                        showInfo("接收Id:" + deviceId + ":" + StringUtil.byteArrayToHexString(reData));
                        Logc.e(TAG, "#####接收Id:" + deviceId + ":" + StringUtil.byteArrayToHexString(reData));
                    }
                    break;
                }
                case DATA_RECEIVE_STREAM: {//ack rerevice
                    byte[] reData = event.data.getByteArray(MeshConstants.EXTRA_DATA);
                    int dataSqn = event.data.getInt(MeshConstants.EXTRA_DATA_SQN);
                    if (reData != null) {
                        showInfo("#####接收Id:" + deviceId + "## Sqn:" + dataSqn + "数据:" + StringUtil.byteArrayToHexString(reData) + "##");
                        Logc.e(TAG, "#####接收Id:" + deviceId + "## Sqn:" + dataSqn + "数据:" + StringUtil.byteArrayToHexString(reData) + "##");
                    }

                    break;
                }
                case DATA_RECEIVE_STREAM_END: {//ack rerevice end
                    showInfo("#####接收Id:" + deviceId + ":数据接收完成 end###");
                    Logc.e(TAG, "#####接收Id:" + deviceId + ":数据接收完成 end###");
                    break;
                }
            }
        } else {
            Logc.e(TAG, "#####无关数据:");
        }
    }

    


}
