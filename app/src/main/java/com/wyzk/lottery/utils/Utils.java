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
import com.wyzk.lottery.R;
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

    public static int getPokeByIndex(int index) {

        switch (index) {
            case 17:
                return R.mipmap.ht_a;
            case 18:
                return R.mipmap.ht_2;
            case 19:
                return R.mipmap.ht_3;
            case 20:
                return R.mipmap.ht_4;
            case 21:
                return R.mipmap.ht_5;
            case 22:
                return R.mipmap.ht_6;
            case 23:
                return R.mipmap.ht_7;
            case 24:
                return R.mipmap.ht_8;
            case 25:
                return R.mipmap.ht_9;
            case 26:
                return R.mipmap.ht_10;
            case 27:
                return R.mipmap.ht_j;
            case 28:
                return R.mipmap.ht_q;
            case 29:
                return R.mipmap.ht_k;

            case 45:
                return R.mipmap.hx_k;
            case 44:
                return R.mipmap.hx_q;
            case 43:
                return R.mipmap.hx_j;
            case 42:
                return R.mipmap.hx_10;
            case 41:
                return R.mipmap.hx_9;
            case 40:
                return R.mipmap.hx_8;
            case 39:
                return R.mipmap.hx_7;
            case 38:
                return R.mipmap.hx_6;
            case 37:
                return R.mipmap.hx_5;
            case 36:
                return R.mipmap.hx_4;
            case 35:
                return R.mipmap.hx_3;
            case 34:
                return R.mipmap.hx_2;
            case 33:
                return R.mipmap.hx_a;

            case 49:
                return R.mipmap.mh_a;
            case 50:
                return R.mipmap.mh_2;
            case 51:
                return R.mipmap.mh_3;
            case 52:
                return R.mipmap.mh_4;
            case 53:
                return R.mipmap.mh_5;
            case 54:
                return R.mipmap.mh_6;
            case 55:
                return R.mipmap.mh_7;
            case 56:
                return R.mipmap.mh_8;
            case 57:
                return R.mipmap.mh_9;
            case 58:
                return R.mipmap.mh_10;
            case 59:
                return R.mipmap.mh_j;
            case 60:
                return R.mipmap.mh_q;
            case 61:
                return R.mipmap.mh_k;

            case 65:
                return R.mipmap.fk_a;
            case 66:
                return R.mipmap.fk_2;
            case 67:
                return R.mipmap.fk_3;
            case 68:
                return R.mipmap.fk_4;
            case 69:
                return R.mipmap.fk_5;
            case 70:
                return R.mipmap.fk_6;
            case 71:
                return R.mipmap.fk_7;
            case 72:
                return R.mipmap.fk_8;
            case 73:
                return R.mipmap.fk_9;
            case 74:
                return R.mipmap.fk_10;
            case 75:
                return R.mipmap.fk_j;
            case 76:
                return R.mipmap.fk_q;
            case 77:
                return R.mipmap.fk_k;
        }
        return -1;
    }
}
