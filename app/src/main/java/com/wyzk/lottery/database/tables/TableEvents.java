/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.wyzk.lottery.event.Event;


/**
 * Database table for events.
 */
public class TableEvents implements BaseColumns {

    public static final String TABLE_NAME = "events";

    public static final String COLUMN_EVENT_NAME = "name";
    public static final String COLUMN_EVENT_TYPE = "type";
    public static final String COLUMN_EVENT_VALUE = "value";
    public static final String COLUMN_EVENT_ACTIVE = "isActive";
    public static final String COLUMN_EVENT_PLACE_ID = "placeID";
    public static final String COLUMN_EVENT_REPEAT_TYPE = "repeatType";


    public static String[] getColumnsNames() {

        String[] columns = {
                TableEvents._ID,
                TableEvents.COLUMN_EVENT_NAME,
                TableEvents.COLUMN_EVENT_TYPE,
                TableEvents.COLUMN_EVENT_VALUE,
                TableEvents.COLUMN_EVENT_ACTIVE,
                TableEvents.COLUMN_EVENT_REPEAT_TYPE,
                TableEvents.COLUMN_EVENT_PLACE_ID
        };

        return columns;
    }


    public static ContentValues createContentValues(Event event) {


        ContentValues values = new ContentValues();
        // _ID not included if we are creating a new event as it is Auto-incremental
        if (event.getId() != -1)
            values.put(TableEvents._ID, event.getId());

        values.put(TableEvents.COLUMN_EVENT_NAME, event.getName());
        values.put(TableEvents.COLUMN_EVENT_TYPE, event.getType());
        values.put(TableEvents.COLUMN_EVENT_VALUE, event.getValue());
        values.put(TableEvents.COLUMN_EVENT_ACTIVE, event.isActive());
        values.put(TableEvents.COLUMN_EVENT_PLACE_ID, event.getPlaceId());
        values.put(TableEvents.COLUMN_EVENT_REPEAT_TYPE, event.getRepeatType());

        return values;
    }

    public static Event getEventFromCursor(Cursor cursor) {

        Event event;

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableEvents._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(TableEvents.COLUMN_EVENT_NAME));
        int type = cursor.getInt(cursor.getColumnIndexOrThrow(TableEvents.COLUMN_EVENT_TYPE));
        double value = cursor.getDouble(cursor.getColumnIndexOrThrow(TableEvents.COLUMN_EVENT_VALUE));
        boolean isActive = cursor.getInt(cursor.getColumnIndexOrThrow(TableEvents.COLUMN_EVENT_ACTIVE))==1?true:false;
        int placeId = cursor.getInt(cursor.getColumnIndexOrThrow(TableEvents.COLUMN_EVENT_PLACE_ID));
        int repeatType = cursor.getInt(cursor.getColumnIndexOrThrow(TableEvents.COLUMN_EVENT_REPEAT_TYPE));

        event = new Event();
        event.setId(id);
        event.setName(name);
        event.setType(type);
        event.setValue(value);
        event.setActive(isActive);
        event.setPlaceId(placeId);
        event.setRepeatType(repeatType);

        return event;
    }
}
