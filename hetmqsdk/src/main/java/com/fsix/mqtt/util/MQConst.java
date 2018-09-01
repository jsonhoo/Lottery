package com.fsix.mqtt.util;

public class MQConst {
    public final static String MQTTSERVICENAME = "android.intent.action.FsixMQTTService";
    //注册client
    public final static int MSG_REGISTER_CLIENT = 1;
    //注销client
    public final static int MSG_UNREGISTER_CLIENT = 2;
    //接受数据
    public final static int MSG_SET_VALUE = 3;
    public static final int MSG_SEND_DATA = 4;


    public static final String MSG_SERVICE_INIT = "init";

    public static final String MSG_DATA = "data";

    public final static class ConnStatus {
        public final static int CONNECTED = 0;
        public final static int DISCONNECT = 1;

    }
}
