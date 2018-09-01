package com.fsix.mqtt.core.callback;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2016, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * <p>
 * <p>描述：</p>
 * 名称:  <br>
 * 作者: 80010814 4<br>
 * 日期: 2017/7/1<br>
 **/


import android.content.ContextWrapper;
import android.text.TextUtils;

import com.fsix.mqtt.bean.MQBean;
import com.fsix.mqtt.observer.EventManager;
import com.fsix.mqtt.util.Logc;
import com.fsix.mqtt.util.MQConst;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class FSixMqttCallback implements MqttCallback {
    private final String TAG = FSixMqttCallback.class.getSimpleName();
    private ContextWrapper context;
    private String topic;

    public FSixMqttCallback(String topic) {
        // this.context = context;
        this.topic = topic;
    }

    @Override
    public void connectionLost(Throwable cause) {
        //We should reconnect here
        EventManager.getInstance().post(new MQBean(MQConst.ConnStatus.DISCONNECT, cause));
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (message != null) {
            dealMessage(topic, message);
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    /**
     * 处理收到的mqtt消息
     *
     * @param message
     */
    private void dealMessage(String topic, MqttMessage message) {
        if (!TextUtils.isEmpty(topic) && message != null) {
            EventManager.getInstance().post(new MQBean(MQConst.ConnStatus.CONNECTED, topic, message));
        }
        Logc.d(TAG, "NETWORK_TAG.FSixMqttCallback:messageArrived:" + message);
    }
}
