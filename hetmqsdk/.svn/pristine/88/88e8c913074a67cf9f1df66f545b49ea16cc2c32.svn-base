package com.fsix.mqtt.bean;

import java.io.Serializable;

/**
 * ————————————————————————————————
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * ————————————————————————————————
 * <p>
 * <p>描述：</p>
 * 名称: MqttConnBean <br>
 * 作者: uuxia<br>
 * 版本: 1.0<br>
 * 日期: 2017/5/23 15:25<br>
 **/
public class MqttConnBean implements Serializable {
    //client标识:APP获取配置返回的clientId
    private String clientId;
    //用户名:APP获取配置返回的userName
    private String userName;
    //密码:MD5(userId+appSecret)
    private String password;
    //协议版本号，4标识MQTT3.1.1版本:4
    private Integer protocolVersion = 4;
    //客户端断开不保持要推送的消息:1
    private Integer cleanSession=1;
    //心跳检测时间间隔:必须大于30秒
    private Long keepAlive = 30l;
    //MQTT服务器不保持消息
    private Integer retain;
    //消息QOS Level设置为1，至少一次
    private Integer qos;
    private String broker;
    private String topic;

    public String getBrokerUrl() {
        return broker;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.broker = brokerUrl;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(Integer protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public Integer getCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(Integer cleanSession) {
        this.cleanSession = cleanSession;
    }

    public Long getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Long keepAlive) {
        this.keepAlive = keepAlive;
    }

    public Integer getRetain() {
        return retain;
    }

    public void setRetain(Integer retain) {
        this.retain = retain;
    }

    public Integer getQos() {
        return qos;
    }

    public void setQos(Integer qos) {

        this.qos = qos;
    }

    @Override
    public String toString() {
        return "MqttConnBean{" +
                "clientId='" + clientId + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", protocolVersion=" + protocolVersion +
                ", cleanSession=" + cleanSession +
                ", keepAlive=" + keepAlive +
                ", retain=" + retain +
                ", qos=" + qos +
                ", broker='" + broker + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}
