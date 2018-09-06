package com.fsix.mqtt.bean;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Serializable;

public class MQBean<T> implements Serializable {
    private int code;
    private String msg;
    private T data;
    private String topic;
    private MqttMessage message;

    public MQBean() {
    }

    public MQBean(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public MQBean(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public MQBean(int code, String topic, MqttMessage message) {
        this.code = code;
        this.topic = topic;
        this.message = message;
        String msgData = message.toString();
    }

    public MQBean(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public MqttMessage getMessage() {
        return message;
    }

    public void setMessage(MqttMessage message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MQBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", topic='" + topic + '\'' +
                ", message=" + message +
                '}';
    }
}
