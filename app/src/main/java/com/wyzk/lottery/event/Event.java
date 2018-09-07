package com.wyzk.lottery.event;

import java.util.ArrayList;
import java.util.List;


/**
 * Class that represents an event for ActionModelApi.
 */
public class Event {

    public final static int INVALID_VALUE = -1;

    public final static int TYPE_NONE = 0;
    public final static int TYPE_LIGHT = 1;
    public final static int TYPE_POWER = 2;
    public final static int TYPE_TEMPERATURE = 3;

    public final static int HOUR_MS = 60 * 60 * 1000;
    public final static int DAY_MS = 24 * HOUR_MS;
    public final static int WEEK_MS = 7 * DAY_MS;

    private int mId = INVALID_VALUE;
    private String mName = null;
    private int mType = INVALID_VALUE;
    private double mValue = INVALID_VALUE;
    private boolean mActive = false;
    private int mPlaceId = INVALID_VALUE;

    private List<DeviceEvent> deviceEvents = new ArrayList<>();
    private int repeatType = INVALID_VALUE;

    /**
     * Returns the event Id.
     * @return databaseId
     */
    public int getId() {
        return mId;
    }

    /**
     * Set the place Id that the event belongs to.
     * @param id placeId.
     */
    public void setPlaceId(int id) {
        this.mPlaceId = id;
    }

    /**
     * Get the place Id that the event belongs to.
     * @return placeId.
     */
    public int getPlaceId() {
        return mPlaceId;
    }

    /**
     * Set the event's databaseId.
     * @param id
     */
    public void setId(int id) {
        this.mId = id;
    }

    /**
     * Gets the event's type
     * @return type
     */
    public int getType() {
        return mType;
    }

    /**
     * Set the event's type.
     * @param type
     */
    public void setType(int type) {
        this.mType = type;
    }

    /**
     * Gets the event's name
     * @return name
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the event's name
     * @param name
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Gets the event's value.
     * @return
     */
    public double getValue() {
        return mValue;
    }

    /**
     * Sets the event's value.
     * @param value
     */
    public void setValue(double value) {
        this.mValue = value;
    }

    /**
     * Return whether the event is currently active or not.
     * @return true is the event is active.
     */
    public boolean isActive() { return mActive; }

    /**
     * Set whether the event is currently active or not.
     * @param active
     */
    public void setActive(boolean active) { this.mActive = active; }

    /**
     * Get the list of devices affected by the event.
     * @return list of devices.
     */
    public List<DeviceEvent> getDeviceEvents() {
        return this.deviceEvents;
    }

    /**
     * Set the list of devices affected by the event.
     * @param deviceEvents
     */
    public void setDeviceEvents(List<DeviceEvent> deviceEvents) {
        this.deviceEvents = deviceEvents;
    }

    /**
     * Return the ids of the devices affected by the event.
     * @return devicesListIDs.
     */
    public List<Integer> getDevicesList() {
        List<Integer> listIds = new ArrayList<>();
        for (int i=0; i < deviceEvents.size(); i++) {
            if(!listIds.contains(deviceEvents.get(i).getDeviceId())){
                listIds.add(deviceEvents.get(i).getDeviceId());
            }
        }
        return listIds;
    }

    /**
     * Set the list of ids of the devices affected by the event.
     * @param listIds
     */
    public void setDevicesList(List<Integer> listIds) {
        deviceEvents.clear();

        if (listIds == null) return;

        for (int i=0; i < listIds.size(); i++) {
            DeviceEvent deviceEvent = new DeviceEvent();
            deviceEvent.setDeviceEventId(i + 1);
            deviceEvent.setDeviceId(listIds.get(i));
            deviceEvents.add(deviceEvent);
        }
    }

    /**
     * Return the event's recurring type
     * @return recurring type
     */
    public int getRepeatType() {
        return repeatType;
    }

    /**
     * Set the event's recurring type
     * @param repeatType
     */
    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    /**
     * Get a copy of this event class.
     * @return event
     */
    public Event getCopy() {
        Event event = new Event();
        event.setId(this.getId());
        event.setName(this.getName());
        event.setType(this.getType());
        event.setValue(this.getValue());
        event.setActive(this.isActive());
        event.setPlaceId(this.getPlaceId());
        event.setRepeatType(this.getRepeatType());

        return event;
    }
}
