package com.het.csrmesh.utils;

import java.util.ArrayList;
import java.util.List;

public class MathUtils {


    /**
     * Given a number, it returns a list containing the bits positions where the value is 1.
     * @param number
     * @return
     */
    public static List<Integer> bitPositions(int number) {
            List<Integer> positions = new ArrayList<>();
            int position = 1;
            while (number != 0) {
                if ((number & 1) != 0) {
                    positions.add(position -1);
                }
                position++;
                number = number >>> 1;
            }
            return positions;
    }
}
