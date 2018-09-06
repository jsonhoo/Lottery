/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.het.csrmesh.utils;

import com.het.csrmesh.model.Controller;
import com.het.csrmesh.model.Gateway;
import com.het.csrmesh.model.devices.Device;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ApplicationUtils {


    static final int INTEGER_SIZE_BYTES = 4;
    static final int BYTE_SIZE_BITS = 8;


    /**
     * Convert a byte into an int
     *
     * @param source  original byte
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
        } else {
            for (int i = 0; i < source.length; i++) {
                result |= ((source[i] & 0xFF) << shift);
                shift -= BYTE_SIZE_BITS;
            }
        }
        return result;
    }


    /**
     * Helper to sort a List of Devices based on their name String
     *
     * @param list An unsorted list of Devices
     * @return A List of 'by name' sorted Devices
     */
    public static List<Device> sortDevicesListAlphabetically(final List<Device> list) {

        if (list.size() > 0) {
            Collections.sort(list, new Comparator<Device>() {
                @Override
                public int compare(final Device object1, final Device object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
        }
        return list;
    }

    /**
     * Helper to sort a List of Gateways based on their name String
     *
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
     * Helper to sort a List of Controllers based on their name String
     *
     * @param list An unsorted list of Controllers
     * @return A List of 'by name' sorted Controllers
     */
    public static List<Controller> sortControllersListAlphabetically(final List<Controller> list) {

        if (list.size() > 0) {
            Collections.sort(list, new Comparator<Controller>() {
                @Override
                public int compare(final Controller object1, final Controller object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
        }
        return list;
    }

    /**
     * Converts an array of bytes into an array of int
     *
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
     *
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
