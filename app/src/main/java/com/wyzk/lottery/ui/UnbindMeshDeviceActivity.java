package com.wyzk.lottery.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.csr.csrmesh2.MeshConstants;
import com.fsix.mqtt.util.Logc;
import com.heaven7.android.dragflowlayout.ClickToDeleteItemListenerImpl;
import com.heaven7.android.dragflowlayout.DragAdapter;
import com.heaven7.android.dragflowlayout.DragFlowLayout;
import com.heaven7.android.dragflowlayout.IViewObserver;
import com.squareup.otto.Subscribe;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.R;
import com.wyzk.lottery.api.DataModelManager;
import com.wyzk.lottery.api.MeshLibraryManager;
import com.wyzk.lottery.constant.Constants;
import com.wyzk.lottery.event.MeshResponseEvent;
import com.wyzk.lottery.model.MeshBean;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.wyzk.lottery.event.MeshResponseEvent.ResponseEvent.DATA_RECEIVE_BLOCK;
import static com.wyzk.lottery.event.MeshResponseEvent.ResponseEvent.DATA_RECEIVE_STREAM;
import static com.wyzk.lottery.event.MeshResponseEvent.ResponseEvent.DATA_RECEIVE_STREAM_END;

public class UnbindMeshDeviceActivity extends LotteryBaseActivity {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;
    @Bind(R.id.drag_flowLayout)
    DragFlowLayout mDragflowLayout;

    private static final String TAG = "UnbindMeshDeviceActivity";

    private Context mContext;

    private DragFlowLayout.DragItemManager itemManager;
    private List<Integer> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbind_mesh_device);
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

        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LotteryApplication.bus.unregister(this);
    }

    private void initData() {
        LotteryApplication.bus.register(this);

        itemManager = mDragflowLayout.getDragItemManager();
        //用这个处理点击事件
        mDragflowLayout.setOnItemClickListener(new ClickToDeleteItemListenerImpl(R.id.iv_close) {
            @Override
            protected void onDeleteSuccess(DragFlowLayout dfl, View child, Object data) {
                //your code
                Logc.i(TAG, "onDeleteSuccess index = " + ((MeshBean) data).getMeshId());
                unBindMeshDeviceById(((MeshBean) data).getMeshId());
            }

        });
        //DragAdapter 泛型参数就是为了每个Item绑定一个对应的数据。通常很可能是json转化过来的bean对象
        mDragflowLayout.setDragAdapter(new DragAdapter<MeshBean>() {

            @Override  //获取你的item布局Id
            public int getItemLayoutId() {
                return R.layout.item_drag_flow;
            }

            //绑定对应item的数据
            @Override
            public void onBindData(View itemView, int dragState, MeshBean data) {
                itemView.setTag(data);
                TextView tv = (TextView) itemView.findViewById(R.id.tv_text);
                FrameLayout frameBg = (FrameLayout) itemView.findViewById(R.id.frameBg);

                frameBg.setBackgroundResource(data.getBackgtoundId() == -1 ? R.mipmap.pk_bm : data.getBackgtoundId());
                tv.setText(String.valueOf(data.getMeshId()));
                //iv_close是关闭按钮。只有再非拖拽空闲的情况下才显示
                itemView.findViewById(R.id.iv_close).setVisibility(
                        dragState != DragFlowLayout.DRAG_STATE_IDLE
                                && data.isDraggable() ? View.VISIBLE : View.INVISIBLE);
            }

            //根据指定的child获取对应的数据。
            @NonNull
            @Override
            public MeshBean getData(View itemView) {
                return (MeshBean) itemView.getTag();
            }
        });

        //添加view观察者
        mDragflowLayout.addViewObserver(new IViewObserver() {
            @Override
            public void onAddView(View child, int index) {
                Logc.i(TAG, "onAddView index = " + index);
            }

            @Override
            public void onRemoveView(View child, int index) {
                Logc.i(TAG, "onRemoveView index = " + index);
            }
        });
    }

    private void unBindMeshDeviceById(int deviceId) {
        if (MeshLibraryManager.isBluetoothBridgeReady()) {
            //发送解帮命令
            DataModelManager.getInstance().unackUnbindSendData(deviceId);
            //解帮设备
            mDeviceManager.removeDevice(deviceId);
            //去掉重复的
            if (list.contains(deviceId)){
                list.remove(Integer.valueOf(deviceId));
            }
        } else {
            showMyFailDialog(getString(R.string.you_can_not_control_your_device_until_bridge_is_connected), mDragflowLayout);
        }
    }

    private void meshAdd(int meshId, int backgtoundId) {
        final MeshBean bean = new MeshBean();
        bean.setMeshId(meshId);
        bean.setBackgtoundId(backgtoundId);
        if (!list.contains(meshId)){
            list.add(meshId);
            mDragflowLayout.getDragItemManager().addItem(mDragflowLayout.getChildCount(), bean);
        }
    }

    private void showPokeInfo(int deviceId, byte[] result) {
        int backgtoundId = -1;
        if (Utils.getPokeByIndex(result[1]) != -1) {
            backgtoundId = Utils.getPokeByIndex(result[1]);
        } else {
            backgtoundId = R.mipmap.pk_bm;
        }
        meshAdd(deviceId, backgtoundId);
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
                        //showInfo("接收Id:" + deviceId + ":" + StringUtil.byteArrayToHexString(reData));
                        runOnUiThread(() -> {
                            Logc.e(TAG, "设备编号：" + deviceId);
                            if (reData.length == 3) {
                                showPokeInfo(deviceId, reData);
                            }

                        });
                    }
                    break;
                }
            }
        } else {
            com.uuxia.email.Logc.e(TAG, "#####无关数据:");
        }
    }

}
