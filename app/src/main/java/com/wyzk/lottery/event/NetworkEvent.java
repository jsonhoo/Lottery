package com.wyzk.lottery.event;

public class NetworkEvent {
    private boolean available;

    public NetworkEvent(boolean available) {
        this.available = available;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setavailable(boolean available) {
        this.available = available;
    }
}
