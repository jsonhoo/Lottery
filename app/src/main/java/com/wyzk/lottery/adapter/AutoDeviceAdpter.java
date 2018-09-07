package com.wyzk.lottery.adapter;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.het.recyclerview.recycler.HelperRecyclerViewAdapter;
import com.het.recyclerview.recycler.HelperRecyclerViewHolder;
import com.wyzk.lottery.R;
import com.wyzk.lottery.api.ScanDevice;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-09-07<br>
 * 描述:
 **/

public class AutoDeviceAdpter extends HelperRecyclerViewAdapter<ScanDevice> {
    public AutoDeviceAdpter(Context context, int... layoutIds) {
        super(context, layoutIds);
    }

    @Override
    protected void HelperBindData(HelperRecyclerViewHolder helperRecyclerViewHolder, int i, ScanDevice device) {
        TextView uuid = helperRecyclerViewHolder.getView(R.id.mTitle);
        TextView deviceName = helperRecyclerViewHolder.getView(R.id.subtitle);
        TextView distance = helperRecyclerViewHolder.getView(R.id.distance);
        ImageView imageView = helperRecyclerViewHolder.getView(R.id.icon);
        // Update some UI values from the appearance.
        if (device.appearance != null) {
            deviceName.setText(device.appearance.getShortName());
            imageView.setImageDrawable(
                    ResourcesCompat.getDrawable(mContext.getResources(), R.mipmap.ic_launcher, null));
        } else {
            deviceName.setText(mContext.getString(R.string.unknown_device));
            imageView.setImageDrawable(
                    ResourcesCompat.getDrawable(mContext.getResources(), R.mipmap.ic_launcher, null));
        }
        uuid.setText("" + device.uuid);
        if (device.rssi != 0) {
            distance.setText("" + device.rssi + mContext.getString(R.string.decibel_milliwatts));
        }
    }
}
