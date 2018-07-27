package com.wyzk.lottery.utils;

/**
 * 用来进行快速点击处理的工具类
 */
public class FastClickUtils {
    private static final int MIN_CLICK_DELAY_TIME = 1000;//两次点击的时间间隔 暂设1秒钟
    private static long lastClickTime;

    /**
     * 根据时间判断此次点击是否有效
     *
     * @return true  有效 ， false 无效
     */
    public static boolean isClickAvailable() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}
