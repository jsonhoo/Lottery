package com.wyzk.lottery.utils;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-09-06<br>
 * 描述:
 **/

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.database.DBManager;
import com.wyzk.lottery.event.MeshSystemEvent;
import com.wyzk.lottery.model.Place;

import java.util.List;

/**
 *
 */
public class Utils {
    protected static final String TAG = Utils.class.getSimpleName();


    /**
     * Retrieve Drawable from drawableID
     *
     * @param context
     * @param drawableID
     * @return Drawable selected
     */
    static public Drawable getDrawable(Context context, int drawableID) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return ContextCompat.getDrawable(context, drawableID);
            } else {
                return context.getResources().getDrawable(drawableID);
            }
        } catch (Exception e) {
            return null;
        }
    }

    static public void removePlace(Context context) {
//        new Delete().from(Place.class).where("placeId = ?", 100).execute();
        DBManager manager = DBManager.getInstence(LotteryApplication.get(context));
        if (getLatestPlaceIdUsed() != null && getLatestPlaceIdUsed().getPlaceId() != -1) {
            manager.removeAllDevicesByPlaceId(getLatestPlaceIdUsed().getPlaceId());
        }
        new Delete().from(Place.class).execute();
        LotteryApplication.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.PLACE_CHANGED));
    }

    public static Place getLatestPlaceIdUsed() {
        List<Place> lists = new Select()
                .from(Place.class)
                .execute();
        if (lists != null && lists.size() > 0) {
            return lists.get(0);
        }
        return null;
    }

    static public void setLatestPlaceIdUsed(Place place) {
        place.save();
        LotteryApplication.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.PLACE_CHANGED));
    }

    /**
     * Convert celsius value to kelvin.
     *
     * @param celsius Temperature in celsius.
     * @return Temperature in kelvin.
     */
    static public float convertCelsiusToKelvin(float celsius) {
        return (273.15f + celsius);
    }


    /**
     * Convert kelvin value to celsius.
     *
     * @param kelvin Temperature in kelvin.
     * @return Temperature in celsius.
     */
    static public float convertKelvinToCelsius(float kelvin) {
        float result = kelvin;
        result -= 273.15f;
        result *= 10.0f;
        result = Math.round(result);
        result /= 10.0f;
        return result;
    }
}
