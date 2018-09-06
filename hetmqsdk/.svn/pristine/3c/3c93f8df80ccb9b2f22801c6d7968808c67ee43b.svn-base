package com.fsix.mqtt.observer;

import com.fsix.mqtt.bean.MQBean;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by uuxia-mac on 2017/12/30.
 */

public class EventManager {
    private static Set<INotify> obs = new HashSet<INotify>();
    private INotify dataInterceptor;
    private static EventManager instance;

    public static EventManager getInstance() {
        if (instance == null) {
            synchronized (EventManager.class) {
                if (null == instance) {
                    instance = new EventManager();
                }
            }
        }
        return instance;
    }

    public synchronized void registerObserver(INotify o) {
        if (o != null) {
            if (!obs.contains(o)) {
                obs.add(o);
            }
        }
    }

    public synchronized void unregisterObserver(INotify o) {
        if (obs.contains(o)) {
            obs.remove(o);
        }
    }

    public synchronized void clear() {
        obs.clear();
    }

    public synchronized void post(MQBean obj) {
        if (obj == null)
            return;
        Iterator<INotify> it = obs.iterator();
        while (it.hasNext()) {
            INotify mgr = it.next();
            mgr.onNotify(obj);
        }
    }
}
