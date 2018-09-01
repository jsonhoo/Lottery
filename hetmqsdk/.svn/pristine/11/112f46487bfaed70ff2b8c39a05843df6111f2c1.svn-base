//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.fsix.mqtt;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.fsix.mqtt.bean.MqttConnBean;
import com.fsix.mqtt.core.callback.FSixMqttCallback;
import com.fsix.mqtt.util.Logc;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQ {
    public static final String TAG = "MQ";
    private MqttAndroidClient client = null;
    private boolean isRelease = false;
    private MqttConnectOptions conOpt;
    private MqttConnBean connBean;
    private FSixMqttCallback mqttCallback;
    public volatile boolean isConnectFlag = false;
    private boolean cancleConnectFlag = false;
    private Context mContext;
    private static MQ instances = null;
    private static final String SERVICE_NAME = "org.eclipse.paho.android.service.MqttService";
    private IMqttActionListener mqttActionListener;
    private IMqttActionListener onMqttConnectListener;
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        public void onSuccess(IMqttToken arg0) {
            Logc.d("MQ", "######mqtt========>mq.onSuccess " + arg0.toString());
            if (MQ.this.client != null && MQ.this.client.isConnected() && !MQ.this.isConnectFlag) {
                MQ.this.isConnectFlag = true;
            }

            if (MQ.this.onMqttConnectListener != null) {
                MQ.this.onMqttConnectListener.onSuccess(arg0);
            }

        }

        public void onFailure(IMqttToken arg0, Throwable arg1) {
            Logc.e("MQ", "######mqtt=======>连接失败 " + (arg1 == null ? "" : arg1.getMessage()) + " " + (MQ.this.connBean == null ? "" : MQ.this.connBean.getBrokerUrl()) + " " + (MQ.this.connBean == null ? "" : MQ.this.connBean.getUserName()) + " " + (MQ.this.connBean == null ? "" : MQ.this.connBean.getPassword()));
            if (MQ.this.isConnectFlag) {
                MQ.this.isConnectFlag = false;
            }

            if (MQ.this.onMqttConnectListener != null) {
                MQ.this.onMqttConnectListener.onFailure(arg0, arg1);
            }

            if (!MQ.this.isRelease) {
                (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                    public void run() {
                        MQ.this.reConnect();
                    }
                }, 5000L);
            }

        }
    };

    public MQ() {
    }

    public void subscribe() {
        try {
            if (this.connBean != null) {
                String topic = this.connBean.getTopic();
                Logc.d("MQ", "========>subscribe " + topic);
                int qas = this.connBean.getQos();
                if (!TextUtils.isEmpty(topic)) {
                    this.client.subscribe(topic, qas);
                }
            }
        } catch (MqttException var3) {
            Logc.e("MQ", var3.toString());
        }

    }

    public void subscribe(String topic, int qos) {
        try {
            Logc.d("========>subscribe " + topic);
            if (!TextUtils.isEmpty(topic)) {
                this.client.subscribe(topic, qos);
            }
        } catch (MqttException var4) {
            Logc.e("MQ", var4.toString());
        }

    }

    public void unsubscribe(String topic) {
        try {
            Logc.d("========>subscribe " + topic);
            if (!TextUtils.isEmpty(topic)) {
                this.client.unsubscribe(topic);
            }
        } catch (MqttException var4) {
            Logc.e("MQ", var4.toString());
        }

    }

    public boolean isConnectFlag() {
        return this.client != null && this.client.isConnected();
    }

    public void publish(byte[] data) {
        this.publish(this.connBean.getTopic(), data);
    }

    public void publish(String topic, byte[] data) {
        if (this.isConnectFlag()) {
            MqttMessage mqttMessage = new MqttMessage();

            try {
                mqttMessage.setPayload(data);
                this.client.publish(topic, mqttMessage);
            } catch (MqttException var5) {
                var5.printStackTrace();
            }
        }

    }

    public void publish(byte[] data, IMqttActionListener actionListener) {
        this.publish(this.connBean.getTopic(), data, actionListener);
    }

    public void publish(String topic, byte[] data, IMqttActionListener actionListener) {
        if (this.isConnectFlag()) {
            MqttMessage mqttMessage = new MqttMessage();

            try {
                mqttMessage.setPayload(data);
                this.client.publish(topic, mqttMessage, (Object)null, actionListener);
            } catch (MqttException var6) {
                var6.printStackTrace();
            }
        }

    }

    public void start(Context mContext, MqttConnBean bean) {
        this.connBean = this.processMqtt(bean);
        this.mContext = mContext;
        this.init();
    }

    public void start(Context mContext, MqttConnBean bean, IMqttActionListener listener) {
        this.connBean = this.processMqtt(bean);
        this.mContext = mContext;
        this.onMqttConnectListener = listener;
        this.init();
    }

    public void stop() {
        if (this.client != null) {
            if (this.client.isConnected()) {
                try {
                    this.client.disconnect();
                    this.isRelease = true;
                    Logc.d("MQ", "mqtt server close");
                } catch (MqttException var2) {
                    Logc.d("MQ", var2.toString());
                }

                if (this.isConnectFlag) {
                    this.isConnectFlag = false;
                }
            }

            this.client.unregisterResources();
        }

    }

    private void init() {
        this.isConnectFlag = false;
        this.isRelease = false;
        String uri = this.connBean.getBrokerUrl();
        if (!TextUtils.isEmpty(uri) && this.client == null) {
            this.client = new MqttAndroidClient(this.mContext, uri, this.connBean.getClientId());
            this.mqttCallback = new FSixMqttCallback(this.connBean.getTopic());
            this.client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {
                    MQ.this.mqttCallback.connectionLost(cause);
                    if (!MQ.this.isRelease) {
                        (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                            public void run() {
                                MQ.this.reConnect();
                            }
                        }, 5000L);
                    }

                }

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    MQ.this.mqttCallback.messageArrived(topic, message);
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    MQ.this.mqttCallback.deliveryComplete(token);
                }
            });
            this.conOpt = new MqttConnectOptions();
            this.conOpt.setCleanSession(true);
            this.conOpt.setConnectionTimeout(10);
            int keepalive = this.connBean.getKeepAlive().intValue();
            this.conOpt.setKeepAliveInterval(keepalive);
            this.conOpt.setUserName(this.connBean.getUserName());
            this.conOpt.setPassword(this.connBean.getPassword().toCharArray());
            this.doClientConnection();
        }

    }

    private void doClientConnection() {
        if (this.client != null) {
            Logc.i("MQ", "####mqtt doClientConnection " + this.client.isConnected());
            if (!this.client.isConnected()) {
                try {
                    IMqttToken cc = this.client.connect(this.conOpt, (Object)null, this.iMqttActionListener);
                    System.out.println("");
                } catch (MqttException var2) {
                    Logc.e("MQ", "######mqtt doClientConnection MqttException " + var2.toString());
                }
            }

        }
    }

    public void reConnect() {
        this.doClientConnection();
    }

    private MqttConnBean processMqtt(MqttConnBean mqttConnBean) {
        try {
            MqttConnBean mqttConnBeanData = new MqttConnBean();
            String clientId = mqttConnBean.getClientId();
            mqttConnBeanData.setClientId(clientId);
            mqttConnBeanData.setPassword(mqttConnBean.getPassword());
            mqttConnBeanData.setUserName(mqttConnBean.getUserName());
            mqttConnBeanData.setProtocolVersion(mqttConnBean.getProtocolVersion());
            mqttConnBeanData.setKeepAlive(mqttConnBean.getKeepAlive());
            mqttConnBeanData.setRetain(mqttConnBean.getRetain());
            mqttConnBeanData.setQos(mqttConnBean.getQos());
            mqttConnBeanData.setCleanSession(mqttConnBean.getCleanSession());
            mqttConnBeanData.setTopic(mqttConnBean.getTopic());
            mqttConnBeanData.setBrokerUrl(mqttConnBean.getBrokerUrl());
            return mqttConnBeanData;
        } catch (Exception var4) {
            Logc.d("MQ", var4.toString());
            return null;
        }
    }
}
