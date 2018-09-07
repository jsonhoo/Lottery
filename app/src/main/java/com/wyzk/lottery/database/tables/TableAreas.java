/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.wyzk.lottery.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.wyzk.lottery.model.Area;


/**
 * Database table for areas.
 */
public class TableAreas implements BaseColumns {

    public static final String TABLE_NAME = "areas";

    public static final String COLUMN_NAME_AREA_ID = "areaID";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_PLACE_ID = "placeID";
    public static final String COLUMN_NAME_IS_FAVOURITE = "isFavourite";


    public static String[] getColumnsNames() {

        String[] columns = {
                TableAreas._ID,
                TableAreas.COLUMN_NAME_AREA_ID,
                TableAreas.COLUMN_NAME_NAME,
                TableAreas.COLUMN_NAME_IS_FAVOURITE,
                TableAreas.COLUMN_NAME_PLACE_ID
        };

        return columns;
    }


    public static ContentValues createContentValues(Area area) {

        // _ID not included since it is Auto-incremental
        ContentValues values = new ContentValues();
        values.put(TableAreas.COLUMN_NAME_AREA_ID, area.getAreaID());
        values.put(TableAreas.COLUMN_NAME_NAME, area.getName());
        values.put(TableAreas.COLUMN_NAME_IS_FAVOURITE, area.isFavorite() ? 1 : 0);
        values.put(TableAreas.COLUMN_NAME_PLACE_ID, area.getPlaceID());

        return values;
    }

    public static Area getAreaFromCursor(Cursor cursor) {

        Area area = new Area();

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableAreas._ID));
        int areaID = cursor.getInt(cursor.getColumnIndexOrThrow(TableAreas.COLUMN_NAME_AREA_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(TableAreas.COLUMN_NAME_NAME));
        boolean isFavorite = (cursor.getInt(cursor.getColumnIndexOrThrow(TableAreas.COLUMN_NAME_IS_FAVOURITE)) == 1);
        int placeID = cursor.getInt(cursor.getColumnIndexOrThrow(TableAreas.COLUMN_NAME_PLACE_ID));

        area.setId(id);
        area.setAreaID(areaID);
        area.setName(name);
        area.setPlaceID(placeID);
        area.setIsFavorite(isFavorite);

        return area;
    }
}