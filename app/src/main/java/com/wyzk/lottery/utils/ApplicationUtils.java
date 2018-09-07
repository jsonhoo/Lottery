package com.wyzk.lottery.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.wyzk.lottery.model.Device;
import com.wyzk.lottery.model.Gateway;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-09-07<br>
 * 描述:
 **/
public class ApplicationUtils {

    static final int INTEGER_SIZE_BYTES = 4;
    static final int BYTE_SIZE_BITS = 8;

    /**
     * Convert Dp to Pixel
     */
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }


    /**
     * Convert a byte into an int
     * @param source original byte
     * @param reverse boolean to determine if the conversion should be reversed or not
     * @return The original byte converted to int
     */
    public static int convertBytesToInteger(byte[] source, boolean reverse) {
        if (source.length < 0 | source.length > INTEGER_SIZE_BYTES) {
            throw new IndexOutOfBoundsException("Length must be between 0 and " + INTEGER_SIZE_BYTES);
        }
        int result = 0;
        int shift = (source.length - 1) * BYTE_SIZE_BITS;

        if (reverse) {
            for (int i = source.length - 1; i >= 0; i--) {
                result |= ((source[i] & 0xFF) << shift);
                shift -= BYTE_SIZE_BITS;
            }
        }
        else {
            for (int i = 0; i < source.length; i++) {
                result |= ((source[i] & 0xFF) << shift);
                shift -= BYTE_SIZE_BITS;
            }
        }
        return result;
    }


    /**
     * Helper to sort a List of Devices based on their name String
     * @param list An unsorted list of Devices
     * @return A List of 'by name' sorted Devices
     */
    @SuppressLint("UseValueOf")
    public static List<Device> sortDevicesListAlphabetically(final List<Device> list) {

        if (list.size() > 0) {
            Collections.sort(list, new Comparator<Device>() {
                @Override
                public int compare(Device object1, Device object2) {
                    return new Integer(object1.getDeviceId()).compareTo(object2.getDeviceId());
                }
            });
        }
        return list;
    }



    /**
     * Helper to sort a List of Gateways based on their name String
     * @param list An unsorted list of Gateways
     * @return A List of 'by name' sorted Gateways
     */
    public static List<Gateway> sortGatewaysListAlphabetically(final List<Gateway> list) {

        if (list.size() > 0) {
            Collections.sort(list, new Comparator<Gateway>() {
                @Override
                public int compare(final Gateway object1, final Gateway object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
        }
        return list;
    }



    /**
     * Converts an array of bytes into an array of int
     * @param byteArray The original array of bytes
     * @return Array of ints
     */
    public static int[] byteArrayToIntArray(byte byteArray[]) {
        IntBuffer intBuf =
                ByteBuffer.wrap(byteArray)
                        .order(ByteOrder.BIG_ENDIAN)
                        .asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);

        return array;
    }


    /**
     * Converts an array of integers into an array of bytes
     * @param values The original array of ints
     * @return Array of bytes
     */
    public static byte[] intArrayToByteArray(int[] values) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(values.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(values);

        return byteBuffer.array();
    }
}
