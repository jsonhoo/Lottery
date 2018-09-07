package com.wyzk.lottery.api;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.csr.csrmesh2.MeshConstants;
import com.squareup.otto.Subscribe;
import com.uuxia.email.Logc;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.constant.Constants;
import com.wyzk.lottery.event.MeshResponseEvent;
import com.wyzk.lottery.event.TestDataEvent;
import com.wyzk.lottery.utils.RandomUtils;
import com.wyzk.lottery.utils.StringUtil;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.wyzk.lottery.event.MeshResponseEvent.ResponseEvent.DATA_RECEIVE_BLOCK;
import static com.wyzk.lottery.event.MeshResponseEvent.ResponseEvent.DATA_RECEIVE_STREAM;
import static com.wyzk.lottery.event.MeshResponseEvent.ResponseEvent.DATA_RECEIVE_STREAM_END;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-09-07<br>
 * 描述:
 **/
public class DataModelManager {

    private static DataModelManager INSTANCE = null;
    private static final String TAG = DataModelManager.class.getSimpleName();
    private SendMeshDataThread sendMeshDataThread = null;
    private Map<Integer, Set<MeshPacket>> responseMeshPacket = null;//检查这个列表 查看命令发送是失败  实现重发机制
    private Handler handler = new Handler(Looper.getMainLooper());

    public static DataModelManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DataModelManager.class) {
                if (null == INSTANCE) {
                    INSTANCE = new DataModelManager();
                }
            }
        }

        return INSTANCE;
    }

    public DataModelManager() {
        startThread();
        startHandler();
        registerBus();
        responseMeshPacket = Collections.synchronizedMap(new ConcurrentHashMap<Integer, Set<MeshPacket>>());
    }

    Runnable checkCmd = new Runnable() {
        @Override
        public void run() {
            try {
                Iterator iter = responseMeshPacket.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Set<MeshPacket> packetSet = (Set<MeshPacket>) entry.getValue();
                    Iterator<MeshPacket> it = packetSet.iterator();
                    while (it.hasNext()) {
                        MeshPacket meshPacket = it.next();
                        if (System.currentTimeMillis() - meshPacket.getMinTimes() > 3000L) {//重发，
                            if (MeshLibraryManager.isBluetoothBridgeReady()) {
                                sendMeshPacket(meshPacket);
                            }
                            Logc.e(TAG, "重发并移除这个时间的命令包MinTimes:" + format(meshPacket.getMinTimes(), "yyyy-MM-dd HH:mm:ss") + "meshPacket : " + StringUtil.byteArrayToHexString(meshPacket.getData()));
                            it.remove();
                        }
                    }
                }
            } catch (ConcurrentModificationException e) {
                Logc.e(TAG, "checkCmd e = " + e.getMessage());
                handler.postDelayed(checkCmd, 3000);
            }
            handler.postDelayed(checkCmd, 3000);
        }
    };

    public static String format(long lMillis, String strInFmt) {
        if (TextUtils.isEmpty(strInFmt)) {
            throw new NullPointerException("strInFmt is null");
        } else {
            return (String) DateFormat.format(strInFmt, lMillis);
        }
    }

    private void startThread() {
        sendMeshDataThread = new SendMeshDataThread();
        sendMeshDataThread.start();
    }

    private void startHandler() {
        handler.removeCallbacksAndMessages((Object) null);
        if (handler != null) {
            handler.postDelayed(checkCmd, 15000);
        }
    }

    private void registerBus() {
        LotteryApplication.bus.register(this);
    }

    private void unregisterBus() {
        LotteryApplication.bus.unregister(this);
    }

    public void unBind(Context context) throws Exception {
        if (context == null) {
            throw new Exception("application\'s Context is null");
        } else {
            if (sendMeshDataThread != null) {
                sendMeshDataThread.close();
                sendMeshDataThread = null;
            }
            unregisterBus();
            if (handler != null) {
                handler.removeCallbacks(checkCmd);
                handler = null;
            }
            INSTANCE = null;
        }
    }


    /**
     * 加入到命令列表中
     * 命令下发  存到下发列表里面  延迟200ms再发送
     *
     * @param data
     * @param deviceId
     */
    public synchronized void sendData(byte[] data, int deviceId) throws Exception {
        //判断是否连接上桥
        if (MeshLibraryManager.isBluetoothBridgeReady()) {
            if (data == null) {
                throw new IllegalArgumentException("send data can't  be null");
            }
            if (data.length > 10) {
                throw new IllegalArgumentException("data length must be 0-10");
            }
            if (deviceId == -1) {
                throw new IllegalArgumentException("deviceId  can't  -1");
            }
            byte[] sendByte = new byte[2 + data.length];
            sendByte[0] = (byte) 0x01;
            System.arraycopy(data, 0, sendByte, 1, data.length);
            sendByte[sendByte.length - 1] = RandomUtils.getRandomHex();
            MeshPacket packet = new MeshPacket(deviceId, sendByte);
            packet.setMinTimes(System.currentTimeMillis());
            if (responseMeshPacket.containsKey(deviceId)) {
                responseMeshPacket.get(deviceId).add(packet);
            } else {
                Set<MeshPacket> list = new HashSet<>();
                list.add(packet);
                responseMeshPacket.put(deviceId, list);
            }
            sendMeshPacket(packet);
            showInfo(deviceId, data);
            Logc.e(TAG, "id:" + deviceId + "##DataModel send data =" + StringUtil.byteArrayToHexString(sendByte));
        } else {
            Logc.e(TAG, "mesh not BridgeReady");
        }

    }

    /**
     * 发送mesh数据
     *
     * @param packet 数据包
     */
    private void sendMeshPacket(MeshPacket packet) {
        if (sendMeshDataThread != null) {
            sendMeshDataThread.putData(packet);
        }
    }


    /**
     * 打印 设备数据
     *
     * @param deviceId
     * @param data
     */
    private void showInfo(int deviceId, byte[] data) {
        Bundle bundle = new Bundle();
        bundle.putInt("deviceId", deviceId);
        bundle.putByteArray("data", data);
        TestDataEvent testDataEvent = new TestDataEvent();
        testDataEvent.data = bundle;
        LotteryApplication.bus.post(testDataEvent);
    }


    /**
     * 1.收到的数据第一个字节是0x01  需要回复0x81   0x02是组播(暂时不用， 不做处理)  不需要回复
     * 2.收到的数据第一个字节是0x81  是设备回复(第二个字节是发送数据时生成的随机数)
     *
     * @param event
     */
    @Subscribe
    public void onEvent(MeshResponseEvent event) {
        if (event == null || event.data == null) {
            return;
        }
        if (event.what == DATA_RECEIVE_BLOCK || event.what == DATA_RECEIVE_STREAM || event.what == DATA_RECEIVE_STREAM_END) {
            int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
            if (deviceId == Constants.INVALID_VALUE) {
                Logc.e("deviceId can\'t be -1");
                return;
            }
            switch (event.what) {
                case DATA_RECEIVE_BLOCK: {//unack rerevice
                    byte[] reData = event.data.getByteArray(MeshConstants.EXTRA_DATA);
                    if (reData != null) {
                        if (reData.length < 2) {
                            Logc.e("revice data null");
                            return;
                        }
                        if (reData[0] == (byte) 0x01) {//点播
                            ackBle(deviceId, reData);
                            //读取的数据
                        } else if (reData[0] == (byte) 0x81) {//回应
                            if (reData.length == 3) {
                                receiveAck(deviceId, reData);
                            } else {
                                Logc.e(TAG, " leng回应的数据长度不为3th ：" + reData.length);
                            }
                        }
                        Logc.e(TAG, "#####unack：接收设备数据:" + deviceId + ":" + StringUtil.byteArrayToHexString(reData));
                    }
                    break;
                }
                case DATA_RECEIVE_STREAM: {//ack rerevice
                    byte[] reData = event.data.getByteArray(MeshConstants.EXTRA_DATA);
                    int dataSqn = event.data.getInt(MeshConstants.EXTRA_DATA_SQN);
                    if (reData != null) {
                        Logc.e(TAG, "#####接收设备ack:" + deviceId + "## Sqn:" + dataSqn + "数据:" + StringUtil.byteArrayToHexString(reData) + "##");
                    }
                    break;
                }
                case DATA_RECEIVE_STREAM_END: {//ack rerevice end
                    Logc.e(TAG, "#####接收设备ack:" + deviceId + ":数据接收完成 end###");
                    break;
                }
            }
        }
    }

    /**
     * 发送数据 收到设备发过来的确认回应   移除掉这条命令
     * 注意 ConcurrentModificationException 异常
     *
     * @param deviceId
     * @param reData
     */
    private synchronized void receiveAck(int deviceId, byte[] reData) {
        if (responseMeshPacket.containsKey(deviceId)) {
            try {
                Iterator<MeshPacket> iterator = responseMeshPacket.get(deviceId).iterator();
                while (iterator.hasNext()) {
                    MeshPacket meshPacket = iterator.next();
                    if (reData[1] == meshPacket.getData()[meshPacket.getData().length - 1]) {
                        iterator.remove();
                        Logc.e(TAG, "recive ack data:" + StringUtil.byteArrayToHexString(reData) + "###send packet：" + format(meshPacket.getMinTimes(), "yyyy-MM-dd HH:mm:ss") + " data :" + StringUtil.byteArrayToHexString(meshPacket.getData()));
                        break;
                    }
                }
            } catch (ConcurrentModificationException e) {
                Logc.e(TAG, "receiveAck e = " + e.getMessage());
            }

        }
    }

    private void ackBle(int deviceId, byte[] reData) {
        if (MeshLibraryManager.isBluetoothBridgeReady()) {
            byte[] ackByte = new byte[3];
            ackByte[0] = (byte) 0x81;
            System.arraycopy(reData, reData.length - 1, ackByte, 1, 1);
            ackByte[2] = RandomUtils.getRandomHex();
            Logc.e(TAG, "#####接收Id :" + deviceId + "####" + StringUtil.byteArrayToHexString(reData) + " #ack" + ":" + StringUtil.byteArrayToHexString(ackByte));
            MeshPacket packet = new MeshPacket(deviceId, ackByte);
            packet.setMinTimes(System.currentTimeMillis());
            sendMeshPacket(packet);
        } else {
            Logc.e(TAG, "mesh not BridgeReady");
        }
    }


}
