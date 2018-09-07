package com.wyzk.lottery.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.het.recyclerview.recycler.HelperRecyclerViewAdapter;
import com.het.recyclerview.recycler.HelperRecyclerViewHolder;
import com.het.recyclerview.swipemenu.SwipeMenuLayout;
import com.wyzk.lottery.R;
import com.wyzk.lottery.model.Device;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-09-07<br>
 * 描述:
 **/
public class DeviceAdapter extends HelperRecyclerViewAdapter<Device> {

    private static final int NORMAL = 1;

    public DeviceAdapter(Context context, int itemLayoutId) {
        super(context, itemLayoutId);
    }

    @Override
    protected void HelperBindData(HelperRecyclerViewHolder holder, int position, Device device) {
        SwipeMenuLayout superSwipeMenuLayout = (SwipeMenuLayout) holder.itemView;
        superSwipeMenuLayout.setSwipeEnable(true);
        if (!TextUtils.isEmpty(device.getName())) {
            holder.setText(R.id.device_name, device.getName());
        }
        if (device.getDeviceId() != 0) {
            holder.setText(R.id.device_id, device.getDeviceId() + "");
        }
        if (device.getUuid() != null && !TextUtils.isEmpty(device.getUuid().toString())) {
            holder.setText(R.id.device_Uuid, device.getUuid().toString());
        }
        holder.setOnClickListener(R.id.btRemove, v -> {
            if (mINameBtnClickListener != null)
                DeviceAdapter.this.mINameBtnClickListener.onNameBtnCilck(v, position);
        });

        holder.setOnClickListener(R.id.call_device, v -> {
            if (mINameBtnClickListener != null)
                DeviceAdapter.this.mINameBtnClickListener.onCallBtnCilck(v, position);
        });
    }

    private DeviceAdapter.ISwipeMenuClickListener mINameBtnClickListener;

    public void setISwipeMenuClickListener(DeviceAdapter.ISwipeMenuClickListener mINameBtnClickListener) {
        this.mINameBtnClickListener = mINameBtnClickListener;
    }

    public interface ISwipeMenuClickListener {
        void onNameBtnCilck(View var1, int var2);

        void onCallBtnCilck(View var1, int var2);
    }
}
