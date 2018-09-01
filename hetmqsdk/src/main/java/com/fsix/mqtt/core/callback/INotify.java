package com.fsix.mqtt.core.callback;

import com.fsix.mqtt.bean.MQBean;

public interface INotify {
    void onNotify(MQBean mqBean);
}
