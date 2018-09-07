package com.wyzk.lottery.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.uuxia.email.Logc;
import com.wyzk.lottery.utils.ByteUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-09-07<br>
 * 描述:
 **/
public class SendMeshDataThread extends Thread {

    protected boolean runnable;
    protected static BlockingQueue<MeshPacket> inQueue = new LinkedBlockingQueue();
    private static final String TAG = SendMeshDataThread.class.getSimpleName();
    private Handler handler = null;
    private int count = 0;
    /**
     * TimeSend is mesh send data delay time
     */
    private static final int TimeSend = 50;

    public SendMeshDataThread() {
        this.runnable = true;
        initHandler();
    }

    @Override
    public void run() {
        super.run();

        while (this.runnable) {
            try {
                MeshPacket packet = (MeshPacket) inQueue.take();
                if (runnable) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("deviceId", packet.getDeviceId());
                    bundle.putByteArray("data", packet.getData());
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = bundle;
                    count++;
                    if (handler != null) {
                        handler.sendMessageDelayed(msg, count * TimeSend);
                    }
                }
            } catch (InterruptedException var2) {
                var2.printStackTrace();
                Logc.e("mesh", "MeshServiceThread.InterruptedException " + var2.getMessage());
            } catch (Exception var3) {
                var3.printStackTrace();
                Logc.e("mesh", "MeshServiceThread.IOException " + var3.getMessage());
            }
        }
    }


    private void initHandler() {
        this.handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                SendMeshDataThread.this.handleMessage(msg);
            }
        };
    }


    private void handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                Bundle bundle = (Bundle) msg.obj;
                int deviceId = bundle.getInt("deviceId");
                byte[] sendByte = bundle.getByteArray("data");
                count--;
                if (MeshLibraryManager.isBluetoothBridgeReady()) {
                    Logc.e(TAG, "mesh id:" + deviceId + "##send data :" + ByteUtils.toHexString(sendByte));
                    DataModel.sendData(deviceId, sendByte, false);
                } else {
                    Logc.e(TAG, "mesh not BridgeReady .....");
                }
                break;
        }
    }


    public void putData(MeshPacket packet) {
        boolean b = inQueue.offer(packet);
        if (!b) {
            Logc.i("mesh", "MeshServiceThread this packet offer faile:" + packet.toString());
        }
    }

    public void close() {
        this.runnable = false;
        this.count = 0;
        MeshPacket meshPacket = new MeshPacket();
        meshPacket.setDeviceId(-1);
        inQueue.offer(meshPacket);
        handler.removeCallbacksAndMessages((Object) null);
    }

}